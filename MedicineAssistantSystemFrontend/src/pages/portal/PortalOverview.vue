<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import {
  getPortalOverview,
  queryMpArticles,
  triggerPortalSummary,
  type MpArticleItem,
  type PortalOverview
} from '@/api/portal'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()
const userId = ref(auth.user?.userId ?? 0)

watch(
  () => auth.user?.userId,
  (id) => {
    if (id != null) userId.value = id
  }
)

const loading = ref(false)
const error = ref<string | null>(null)
const overview = ref<PortalOverview | null>(null)
const mpError = ref<string | null>(null)
const mpLoading = ref(false)
const mpKeyword = ref('')
const mpNickname = ref('')
const mpArticles = ref<MpArticleItem[]>([])
const mpUpdatedAt = ref(0)
const summarizing = ref(false)
const summaryProgress = ref(0)
let summaryRaf = 0
const DEFAULT_MP_NAME = '中药新药'
const MP_PREF_KEY = 'mas_portal_mp_pref'

type MpCache = {
  nickname: string
  items: MpArticleItem[]
  updatedAt: number
}

const projectCount = computed(() => overview.value?.myProjects?.length ?? 0)
const taskCount = computed(() => overview.value?.tasks?.length ?? 0)
const riskCount = computed(() => overview.value?.riskWarnings ?? 0)

const displaySummary = computed(() => {
  const t = overview.value?.summaryText?.trim()
  if (t) return t
  return '暂无总结，点击「手动生成总结」可基于近期智能体对话生成摘要。'
})

const topProjectName = computed(() => {
  const first = overview.value?.myProjects?.[0]
  return first?.projectName?.trim() || '暂无项目焦点'
})

const topTaskName = computed(() => {
  const tasks = [...(overview.value?.tasks ?? [])]
  if (!tasks.length) return '暂无待处理任务'
  tasks.sort((a, b) => {
    const ao = a.overdue ? 0 : 1
    const bo = b.overdue ? 0 : 1
    if (ao !== bo) return ao - bo
    return (a.priority ?? 99) - (b.priority ?? 99)
  })
  const first = tasks[0]
  if (!first) return '暂无待处理任务'
  return `${first.title}${first.projectName ? ` · ${first.projectName}` : ''}`
})

const topTasks = computed(() => {
  const tasks = [...(overview.value?.tasks ?? [])]
  tasks.sort((a, b) => {
    const ao = a.overdue ? 0 : 1
    const bo = b.overdue ? 0 : 1
    if (ao !== bo) return ao - bo
    return (a.priority ?? 99) - (b.priority ?? 99)
  })
  return tasks.slice(0, 5)
})

function priorityText(p?: number | null) {
  if (p === 1) return '高'
  if (p === 2) return '中'
  if (p === 3) return '低'
  return '-'
}

function dateOnly(s?: string | null) {
  if (!s) return '-'
  return String(s).slice(0, 10)
}

const portalNews = computed(() => {
  const rows: string[] = []
  rows.push(`项目焦点：${topProjectName.value}`)
  const tasks = topTasks.value
  if (tasks.length) {
    for (const t of tasks.slice(0, 3)) {
      rows.push(`任务进展：${t.title}${t.projectName ? `（${t.projectName}）` : ''}`)
    }
  } else {
    rows.push('任务进展：暂无待处理任务')
  }
  if (riskCount.value > 0) {
    rows.push(`风险提示：当前有 ${riskCount.value} 个高优先级风险待处理`)
  } else {
    rows.push('风险提示：暂无高优先级风险')
  }
  return rows.slice(0, 5)
})

const statusMap: Record<number, { text: string; type: 'info' | 'warning' | 'success' | 'danger' }> = {
  1: { text: '立项中', type: 'warning' },
  2: { text: '进行中', type: 'success' },
  3: { text: '已完成', type: 'info' },
  4: { text: '已取消', type: 'danger' }
}

const activeMpName = computed(() => mpNickname.value || mpKeyword.value || DEFAULT_MP_NAME)

async function load() {
  if (!userId.value) return
  loading.value = true
  error.value = null
  try {
    overview.value = await getPortalOverview(userId.value)
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    loading.value = false
  }
}

async function runSummary() {
  if (!userId.value || summarizing.value) return
  summarizing.value = true
  summaryProgress.value = 0
  error.value = null
  const start = Date.now()
  const duration = 12000
  let finished = false
  const tick = () => {
    if (finished) return
    const elapsed = Date.now() - start
    if (elapsed < duration) {
      summaryProgress.value = Math.min(99, (elapsed / duration) * 99)
      summaryRaf = requestAnimationFrame(tick)
    } else {
      summaryProgress.value = 99
    }
  }
  summaryRaf = requestAnimationFrame(tick)
  try {
    const text = await triggerPortalSummary(userId.value)
    if (overview.value) {
      overview.value = { ...overview.value, summaryText: text ?? null }
    } else {
      overview.value = {
        tasks: [],
        myProjects: [],
        riskWarnings: 0,
        summaryText: text ?? null
      }
    }
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    finished = true
    cancelAnimationFrame(summaryRaf)
    summaryProgress.value = 100
    summarizing.value = false
    setTimeout(() => {
      summaryProgress.value = 0
    }, 450)
  }
}

function phaseText(phase?: string | null) {
  if (!phase) return '未设置'
  const m: Record<string, string> = {
    EXPLORATION: '探索期',
    VERIFICATION: '验证期',
    OPTIMIZATION: '优化期',
    DECLARATION: '申报期'
  }
  return m[phase] || phase
}

function go(path: string) {
  router.push(path)
}

function restoreMpKeyword() {
  try {
    const v = localStorage.getItem(MP_PREF_KEY)
    mpKeyword.value = v?.trim() || DEFAULT_MP_NAME
  } catch {
    mpKeyword.value = DEFAULT_MP_NAME
  }
}

function saveMpKeyword() {
  try {
    localStorage.setItem(MP_PREF_KEY, mpKeyword.value.trim() || DEFAULT_MP_NAME)
  } catch {}
}

function loadMpCache(name: string) {
  try {
    const raw = localStorage.getItem(`mas_portal_mp_cache:${name}`)
    if (!raw) return false
    const data = JSON.parse(raw) as MpCache
    if (!Array.isArray(data.items) || !data.updatedAt) return false
    mpNickname.value = data.nickname || name
    mpArticles.value = data.items
    mpUpdatedAt.value = data.updatedAt
    return true
  } catch {
    return false
  }
}

function saveMpCache(name: string, nickname: string, items: MpArticleItem[]) {
  try {
    const data: MpCache = {
      nickname,
      items,
      updatedAt: Date.now()
    }
    localStorage.setItem(`mas_portal_mp_cache:${name}`, JSON.stringify(data))
    mpUpdatedAt.value = data.updatedAt
  } catch {}
}

async function loadMpArticles(force = false) {
  const name = mpKeyword.value.trim() || DEFAULT_MP_NAME
  mpKeyword.value = name
  mpError.value = null
  if (!force && loadMpCache(name)) return
  mpLoading.value = true
  try {
    const res = await queryMpArticles({ name, page: 1 })
    const items = Array.isArray(res.articles) ? res.articles : []
    mpNickname.value = (res.mpNickname || name).trim()
    mpArticles.value = items
    saveMpCache(name, mpNickname.value, items)
    saveMpKeyword()
  } catch (e: any) {
    mpError.value = String(e?.message || e)
  } finally {
    mpLoading.value = false
  }
}

function restoreMpDefault() {
  mpKeyword.value = DEFAULT_MP_NAME
  loadMpArticles(true)
}

watch(
  () => userId.value,
  (id) => {
    if (id) load()
  }
)

onMounted(() => {
  load()
  restoreMpKeyword()
  loadMpArticles()
})
</script>

<template>
  <div class="page-wrap">
    <el-alert v-if="error" :title="error" type="error" show-icon class="mb-16" />

    <section class="banner">
      <div class="banner-nav">
        <button type="button" class="nav-pill" @click="go('/portal')">智能门户</button>
        <button type="button" class="nav-pill" @click="go('/knowledge')">中医智研</button>
        <button type="button" class="nav-pill" @click="go('/agents')">人机协同</button>
      </div>
      <h1 class="banner-title">中药新药研发人机协同决策系统</h1>
      <p class="banner-sub">传承本草智慧 · 融合现代证据 · 贯通研发全流程</p>
      <div class="banner-actions">
        <button type="button" class="btn-primary" @click="go('/projects')">进入项目中心</button>
        <button type="button" class="btn-ghost" @click="go('/knowledge')">知识枢库</button>
        <button type="button" class="btn-ghost" @click="go('/agents')">灵智工坊</button>
      </div>
    </section>

    <section class="metric-row">
      <div class="metric-item">
        <div class="metric-label">我的项目</div>
        <div class="metric-value">{{ projectCount }}</div>
        <div class="metric-sub">最近更新前 10 个项目</div>
      </div>
      <div class="metric-item">
        <div class="metric-label">待办任务</div>
        <div class="metric-value">{{ taskCount }}</div>
        <div class="metric-sub">包含实验与审批任务</div>
      </div>
      <div class="metric-item">
        <div class="metric-label">风险预警</div>
        <div class="metric-value">{{ riskCount }}</div>
        <div class="metric-sub">高优先级项目风险提醒</div>
      </div>
    </section>

    <section class="portal-main">
      <aside class="portal-news panel">
        <div class="panel-head">
          <h3>公众号最新文章</h3>
          <span>WeChat</span>
        </div>
        <div class="mp-toolbar">
          <el-input v-model="mpKeyword" size="small" placeholder="输入公众号名称" clearable @keyup.enter="loadMpArticles(true)" />
          <div class="mp-toolbar-actions">
            <el-button size="small" :loading="mpLoading" @click="loadMpArticles(true)">查询</el-button>
            <el-button size="small" :disabled="mpLoading" @click="restoreMpDefault">恢复默认</el-button>
          </div>
        </div>
        <div class="mp-name">{{ activeMpName }}</div>
        <div v-if="mpError" class="mp-error">{{ mpError }}</div>
        <ul v-else class="news-list">
          <li v-for="(item, idx) in mpArticles" :key="`${item.url || idx}`">
            <a class="mp-link" :href="item.url || '#'" target="_blank" rel="noopener noreferrer">{{ item.title || '未命名文章' }}</a>
            <div class="mp-time">{{ item.postTimeStr || '-' }}</div>
          </li>
          <li v-if="!mpArticles.length && !mpLoading">暂无文章</li>
        </ul>
        <div v-if="mpUpdatedAt" class="mp-cache-tip">缓存于 {{ new Date(mpUpdatedAt).toLocaleString() }}</div>
      </aside>

      <article class="portal-summary panel">
        <div class="summary-head">
          <div>
            <div class="section-title">近期用户任务摘要</div>
            <div class="summary-sub">基于近期智能体问答生成，支持手动刷新</div>
          </div>
          <el-button
            type="primary"
            plain
            size="small"
            :loading="summarizing"
            :disabled="summarizing || !userId"
            @click="runSummary"
          >
            重新生成总结
          </el-button>
        </div>
        <el-progress
          v-if="summarizing || summaryProgress > 0"
          class="summary-progress"
          :percentage="Math.round(summaryProgress)"
          :stroke-width="6"
          striped
          striped-flow
        />
        <div class="summary-text">{{ displaySummary }}</div>
      </article>

      <aside class="portal-nav panel">
        <div class="panel-head">
          <h3>研究导航</h3>
          <span>Access</span>
        </div>
        <div class="nav-links">
          <button type="button" class="nav-link" @click="go('/projects')">项目中心</button>
          <button type="button" class="nav-link" @click="go('/projects/draft-evaluate')">立项评估</button>
          <button type="button" class="nav-link" @click="go('/knowledge/expert/chenkeji')">专家问答</button>
          <button type="button" class="nav-link" @click="go('/literatures')">文献库</button>
          <button type="button" class="nav-link" @click="go('/knowledge/patents')">专利库</button>
          <button type="button" class="nav-link" @click="go('/regulations')">法规库</button>
        </div>
      </aside>
    </section>

    <section class="work-shell">
      <div class="work-block section-projects">
        <div class="section-head">
          <div class="section-title">项目进展</div>
          <div class="section-line"></div>
        </div>
        <el-table :data="overview?.myProjects || []" stripe class="table-free">
          <el-table-column prop="projectName" label="项目" min-width="180" />
          <el-table-column label="阶段" width="120">
            <template #default="{ row }">
              <el-tag effect="light">{{ phaseText(row.phase) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="110">
            <template #default="{ row }">
              <el-tag :type="statusMap[row.status]?.type || 'info'" effect="light">
                {{ statusMap[row.status]?.text || `状态${row.status ?? '-'}` }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="优先级" width="90">
            <template #default="{ row }">
              {{ row.priority ?? '-' }}
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="work-block section-tasks">
        <div class="section-head">
          <div class="section-title">今日任务</div>
          <div class="section-line"></div>
        </div>
        <div v-if="!(overview?.tasks?.length)" class="empty-text">暂无任务</div>
        <div v-else class="task-list">
          <div v-for="task in overview?.tasks" :key="task.id" class="task-item">
            <div class="task-main">
              <div class="task-title">{{ task.title }}</div>
              <div class="task-meta">
                <span>{{ task.projectName || '未绑定项目' }}</span>
                <span>优先级 {{ priorityText(task.priority ?? null) }}</span>
                <span>截止 {{ dateOnly(task.deadline || null) }}</span>
              </div>
            </div>
            <el-tag v-if="task.overdue" type="danger" effect="dark">逾期</el-tag>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.mb-16 {
  margin-bottom: 16px;
}

.page-wrap {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-width: 1380px;
  margin: 0 auto;
  padding: 6px 2px 18px;
}

.banner {
  border: 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 0;
  padding: 14px 4px 14px;
  background: transparent;
  position: relative;
  overflow: hidden;
  animation: panel-in 0.5s ease both;
}

.banner::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(110deg, transparent 10%, rgba(115, 209, 180, 0.08) 45%, transparent 70%);
  transform: translateX(-120%);
  animation: shimmer 9s ease-in-out infinite;
  pointer-events: none;
}

.banner-nav {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 10px;
}

.nav-pill {
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.03);
  color: rgba(255, 255, 255, 0.76);
  border-radius: 999px;
  padding: 4px 10px;
  font-size: 11px;
  font-weight: 700;
  cursor: pointer;
}

.nav-pill:hover {
  background: rgba(115, 209, 180, 0.1);
  border-color: rgba(115, 209, 180, 0.22);
}

.banner-title {
  margin: 10px 0 0;
  color: rgba(255, 255, 255, 0.97);
  font-size: 40px;
  line-height: 1.1;
  font-weight: 900;
  letter-spacing: -0.012em;
}

.banner-sub {
  margin: 12px 0 0;
  color: rgba(255, 255, 255, 0.66);
  font-size: 15px;
  line-height: 1.8;
}

.banner-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 16px;
}

.btn-primary,
.btn-ghost {
  padding: 10px 16px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 800;
  cursor: pointer;
  transition: transform 0.18s ease, opacity 0.18s ease, background 0.18s ease;
}

.btn-primary {
  border: 0;
  color: #06241e;
  background: linear-gradient(135deg, rgba(146, 230, 202, 0.96), rgba(115, 209, 180, 0.92));
}

.btn-ghost {
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: rgba(255, 255, 255, 0.82);
  background: rgba(255, 255, 255, 0.03);
}

.btn-primary:hover,
.btn-ghost:hover {
  transform: translateY(-1px);
}

.portal-main {
  display: grid;
  grid-template-columns: minmax(220px, 0.9fr) minmax(0, 1.8fr) minmax(220px, 0.9fr);
  gap: 14px;
  animation: panel-in 0.55s ease both;
}

.portal-main > *:nth-child(1) {
  animation: panel-in 0.45s ease both;
}

.portal-main > *:nth-child(2) {
  animation: panel-in 0.55s ease both;
}

.portal-main > *:nth-child(3) {
  animation: panel-in 0.65s ease both;
}

.panel {
  border: 0;
  border-left: 2px solid rgba(115, 209, 180, 0.18);
  border-radius: 0;
  background: transparent;
  backdrop-filter: none;
  padding: 8px 12px 8px 14px;
}

.portal-news {
  display: flex;
  flex-direction: column;
  min-height: 360px;
  max-height: 360px;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 10px;
  gap: 8px;
}

.panel-head h3 {
  margin: 0;
  font-size: 15px;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.92);
}

.panel-head span {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.52);
}

.metric-row {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
  animation: panel-in 0.5s ease both;
  padding-top: 2px;
}

.metric-item {
  border: 0;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 0;
  background: transparent;
  padding: 10px 2px 0;
}

.metric-label {
  color: rgba(255, 255, 255, 0.62);
  font-size: 13px;
}

.metric-value {
  margin-top: 8px;
  color: rgba(255, 255, 255, 0.96);
  font-size: 28px;
  line-height: 1;
  font-weight: 800;
}

.metric-sub {
  margin-top: 8px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.55);
}

.news-list {
  margin: 0;
  padding: 0;
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 10px;
  overflow-y: auto;
  min-height: 0;
  padding-right: 4px;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.news-list li {
  font-size: 13px;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.8);
  padding-bottom: 7px;
  border-bottom: 1px dashed rgba(255, 255, 255, 0.09);
}

.news-list li:last-child {
  border-bottom: 0;
  padding-bottom: 0;
}

.news-list::-webkit-scrollbar {
  width: 0;
  height: 0;
}

.mp-toolbar {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 10px;
}

.mp-toolbar :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.05);
  box-shadow: none;
  border: 1px solid rgba(255, 255, 255, 0.12);
}

.mp-toolbar :deep(.el-input__inner) {
  color: rgba(255, 255, 255, 0.92);
}

.mp-toolbar :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.42);
}

.mp-toolbar-actions {
  display: flex;
  gap: 8px;
}

.mp-toolbar-actions :deep(.el-button) {
  border-radius: 8px;
  border: 1px solid rgba(115, 209, 180, 0.18);
  background: rgba(115, 209, 180, 0.1);
  color: rgba(219, 245, 236, 0.92);
}

.mp-toolbar-actions :deep(.el-button:hover) {
  border-color: rgba(115, 209, 180, 0.32);
  background: rgba(115, 209, 180, 0.16);
  color: #fff;
}

.mp-name {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.68);
  margin-bottom: 8px;
}

.mp-link {
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
}

.mp-link:hover {
  color: rgba(146, 230, 202, 0.95);
}

.mp-time {
  margin-top: 4px;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.5);
}

.mp-error {
  color: rgba(255, 160, 160, 0.95);
  font-size: 12px;
  margin-bottom: 8px;
}

.mp-cache-tip {
  margin-top: 8px;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.46);
}

.portal-summary {
  display: flex;
  flex-direction: column;
}

.nav-links {
  display: grid;
  grid-template-columns: 1fr;
  gap: 8px;
}

.nav-link {
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.02);
  color: rgba(255, 255, 255, 0.82);
  border-radius: 8px;
  padding: 9px 10px;
  text-align: left;
  cursor: pointer;
  font-size: 13px;
}

.nav-link:hover {
  background: rgba(115, 209, 180, 0.12);
  border-color: rgba(115, 209, 180, 0.26);
}

.summary-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 14px;
}

.section-title {
  color: rgba(255, 255, 255, 0.92);
  font-size: 14px;
  font-weight: 900;
  letter-spacing: 0.4px;
}

.summary-sub {
  margin-top: 4px;
  color: rgba(255, 255, 255, 0.54);
  font-size: 12px;
}

.summary-progress {
  margin: 10px 0 8px;
}

.summary-text {
  color: rgba(255, 255, 255, 0.88);
  font-size: 14px;
  line-height: 1.9;
  white-space: pre-wrap;
}

.work-shell {
  display: grid;
  grid-template-columns: minmax(0, 1.15fr) minmax(320px, 0.85fr);
  gap: 18px;
  align-items: start;
  animation: panel-in 0.7s ease both;
}

.work-block {
  border: 0;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 0;
  background: transparent;
  padding: 12px 0 10px;
  color: rgba(255, 255, 255, 0.92);
}

.section-head {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.section-line {
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, rgba(255, 255, 255, 0.35), rgba(255, 255, 255, 0.02));
}

.table-free {
  overflow: hidden;
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.06);
  background: rgba(255, 255, 255, 0.01);
  backdrop-filter: blur(12px);
}

:deep(.table-free.el-table) {
  --el-table-bg-color: rgba(8, 28, 24, 0.74);
  --el-table-tr-bg-color: rgba(8, 28, 24, 0.2);
  --el-table-row-hover-bg-color: rgba(115, 209, 180, 0.08);
  --el-table-header-bg-color: rgba(255, 255, 255, 0.04);
  --el-table-text-color: rgba(255, 255, 255, 0.88);
  --el-table-header-text-color: rgba(255, 255, 255, 0.68);
  --el-table-border-color: rgba(255, 255, 255, 0.08);
  --el-table-fixed-box-shadow: none;
  color: rgba(255, 255, 255, 0.88);
}

:deep(.table-free .el-table__inner-wrapper),
:deep(.table-free .el-table__header-wrapper),
:deep(.table-free .el-table__body-wrapper),
:deep(.table-free .el-table__header),
:deep(.table-free .el-table__body),
:deep(.table-free .el-table__empty-block),
:deep(.table-free .el-scrollbar__view) {
  background: transparent;
}

:deep(.table-free th.el-table__cell) {
  background: rgba(255, 255, 255, 0.04);
}

:deep(.table-free td.el-table__cell),
:deep(.table-free tr) {
  background: transparent;
}

:deep(.table-free .el-table__row:hover > td.el-table__cell) {
  background: rgba(115, 209, 180, 0.08) !important;
}

:deep(.table-free .el-table__cell) {
  border-bottom-color: rgba(255, 255, 255, 0.08);
}

.empty-text {
  padding: 12px 0;
  color: rgba(255, 255, 255, 0.72);
}

.task-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.task-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  padding: 14px 14px;
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.06);
  background: rgba(255, 255, 255, 0.01);
  backdrop-filter: blur(12px);
}

.task-main {
  min-width: 0;
}

.task-title {
  overflow: hidden;
  color: rgba(255, 255, 255, 0.92);
  font-weight: 600;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.task-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 5px;
  color: rgba(255, 255, 255, 0.68);
  font-size: 12px;
}

@media (max-width: 980px) {
  .banner-title {
    font-size: 34px;
  }

  .portal-main,
  .metric-row,
  .work-shell {
    grid-template-columns: 1fr;
  }

  .summary-head {
    flex-direction: column;
    align-items: flex-start;
  }
}

@media (max-width: 640px) {
  .page-wrap {
    gap: 18px;
    padding-top: 2px;
  }

  .banner-title {
    font-size: 24px;
  }

  .banner-sub,
  .summary-text {
    font-size: 14px;
  }

  .banner-actions {
    width: 100%;
  }

  .btn-primary,
  .btn-ghost {
    width: 100%;
  }
}

@keyframes shimmer {
  0% {
    transform: translateX(-120%);
  }
  50% {
    transform: translateX(120%);
  }
  100% {
    transform: translateX(120%);
  }
}

@keyframes panel-in {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>

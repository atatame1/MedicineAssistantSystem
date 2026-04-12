<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getPortalOverview, triggerPortalSummary, type PortalOverview } from '@/api/portal'
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
const summarizing = ref(false)
const summaryProgress = ref(0)
let summaryRaf = 0

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

const statusMap: Record<number, { text: string; type: 'info' | 'warning' | 'success' | 'danger' }> = {
  1: { text: '立项中', type: 'warning' },
  2: { text: '进行中', type: 'success' },
  3: { text: '已完成', type: 'info' },
  4: { text: '已取消', type: 'danger' }
}

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

watch(
  () => userId.value,
  (id) => {
    if (id) load()
  }
)

onMounted(load)
</script>

<template>
  <div class="page-wrap">
    <el-alert v-if="error" :title="error" type="error" show-icon class="mb-16" />

    <section class="hero">
      <div class="hero-badge">智能门户</div>
      <h1 class="hero-title">中药新药研发的人机协同门户</h1>
      <p class="hero-desc">
        用一个足够克制的首页，把平台是什么、当前在推进什么、以及用户最近在关心什么，直接摆在第一屏。
      </p>
      <div class="hero-tags">
        <span class="hero-tag">知识检索</span>
        <span class="hero-tag">专家问答</span>
        <span class="hero-tag">智能研判</span>
        <span class="hero-tag">项目推进</span>
      </div>
      <div class="hero-actions">
        <button type="button" class="hero-primary" @click="go('/projects')">进入项目中心</button>
        <button type="button" class="hero-link" @click="go('/knowledge')">知识枢库</button>
      </div>
    </section>

    <section class="summary-band">
      <div class="summary-head">
        <div>
          <div class="section-title">近期用户任务摘要</div>
          <div class="summary-sub">基于近期智能体问答生成</div>
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
    </section>

    <section class="stats-row">
      <div class="stat-item">
        <div class="stat-label">我的项目</div>
        <div class="stat-value">{{ projectCount }}</div>
        <div class="stat-tip">最近更新前 10 个项目</div>
      </div>
      <div class="stat-item">
        <div class="stat-label">待办任务</div>
        <div class="stat-value">{{ taskCount }}</div>
        <div class="stat-tip">包含实验与审批任务</div>
      </div>
      <div class="stat-item">
        <div class="stat-label">风险预警</div>
        <div class="stat-value">{{ riskCount }}</div>
        <div class="stat-tip">高优先级项目风险提醒</div>
      </div>
      <div class="stat-item focus">
        <div class="stat-label">当前焦点</div>
        <div class="focus-line">{{ topProjectName }}</div>
        <div class="focus-line soft">{{ topTaskName }}</div>
      </div>
    </section>

    <section class="work-grid">
      <div class="section section-projects">
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

      <div class="section section-tasks">
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
                <span>优先级 {{ task.priority ?? '-' }}</span>
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
  gap: 24px;
  max-width: 1220px;
  margin: 0 auto;
  padding: 8px 2px 18px;
}

.hero {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 18px 0 4px;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  border-radius: 999px;
  border: 1px solid rgba(115, 209, 180, 0.32);
  background: rgba(115, 209, 180, 0.14);
  color: #dff8ee;
  font-size: 12px;
  font-weight: 800;
}

.hero-title {
  max-width: 820px;
  margin: 18px 0 0;
  color: rgba(255, 255, 255, 0.97);
  font-size: 42px;
  line-height: 1.12;
  font-weight: 900;
  letter-spacing: -0.02em;
}

.hero-desc {
  max-width: 760px;
  margin: 16px 0 0;
  color: rgba(255, 255, 255, 0.7);
  font-size: 15px;
  line-height: 1.9;
}

.hero-tags {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 10px;
  margin-top: 20px;
}

.hero-tag {
  padding: 8px 13px;
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.05);
  color: rgba(255, 255, 255, 0.78);
  font-size: 12px;
}

.hero-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 10px;
  margin-top: 24px;
}

.hero-primary,
.hero-link {
  padding: 11px 18px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 800;
  cursor: pointer;
  transition: transform 0.18s ease, opacity 0.18s ease, background 0.18s ease;
}

.hero-primary {
  border: 0;
  color: #06241e;
  background: linear-gradient(135deg, rgba(146, 230, 202, 0.96), rgba(115, 209, 180, 0.92));
}

.hero-link {
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: rgba(255, 255, 255, 0.88);
  background: rgba(255, 255, 255, 0.06);
}

.hero-primary:hover,
.hero-link:hover {
  transform: translateY(-1px);
}

.summary-band {
  padding: 20px 0 18px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
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
  max-width: 980px;
  color: rgba(255, 255, 255, 0.88);
  font-size: 15px;
  line-height: 1.9;
  white-space: pre-wrap;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 18px;
}

.stat-item {
  min-width: 0;
  padding-top: 8px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.stat-label {
  color: rgba(255, 255, 255, 0.68);
  font-size: 13px;
}

.stat-value {
  margin-top: 12px;
  color: rgba(255, 255, 255, 0.95);
  font-size: 42px;
  line-height: 1;
  font-weight: 800;
}

.stat-tip {
  margin-top: 8px;
  color: rgba(255, 255, 255, 0.56);
  font-size: 12px;
}

.stat-item.focus {
  display: flex;
  flex-direction: column;
}

.focus-line {
  margin-top: 12px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  line-height: 1.7;
  font-weight: 700;
}

.focus-line.soft {
  margin-top: 6px;
  color: rgba(255, 255, 255, 0.6);
  font-size: 13px;
  font-weight: 500;
}

.work-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.55fr) minmax(320px, 0.9fr);
  gap: 24px;
  align-items: start;
}

.section {
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
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.035);
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
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.035);
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
  .hero-title {
    font-size: 30px;
  }

  .stats-row,
  .work-grid {
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

  .hero-title {
    font-size: 24px;
  }

  .hero-desc,
  .summary-text {
    font-size: 14px;
  }

  .hero-actions {
    width: 100%;
  }

  .hero-primary,
  .hero-link {
    width: 100%;
  }
}
</style>

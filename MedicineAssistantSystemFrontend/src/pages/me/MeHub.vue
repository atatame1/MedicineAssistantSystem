<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import defaultAvatar from '@/assets/shark_cute2.jpg'
import {
  getMyProfile,
  getUserFavoriteStatistics,
  listMyProjects,
  listMyReports,
  listMyTasks,
  listUserFavorites,
  type DocumentResponse,
  type FavoriteResponse,
  type FavoriteStatisticsResponse,
  type ProjectResponse,
  type TaskResponse,
  type UserProfileResponse
} from '@/api/user'

const router = useRouter()
const auth = useAuthStore()
const uid = computed(() => auth.user?.userId || 0)
const displayName = computed(() => auth.user?.nickname || auth.user?.username || '游客')

const loading = ref(false)
const error = ref<string | null>(null)
const tasks = ref<TaskResponse[]>([])
const projects = ref<ProjectResponse[]>([])
const reports = ref<DocumentResponse[]>([])
const favorites = ref<FavoriteResponse[]>([])
const favStats = ref<FavoriteStatisticsResponse | null>(null)
const profile = ref<UserProfileResponse | null>(null)

const todoTop = computed(() => {
  const rows = tasks.value || []
  const sorted = [...rows].sort((a: any, b: any) => {
    const ao = a?.overdue ? 0 : 1
    const bo = b?.overdue ? 0 : 1
    if (ao !== bo) return ao - bo
    const ad = Number(new Date(a?.deadline || 0))
    const bd = Number(new Date(b?.deadline || 0))
    if (Number.isFinite(ad) && Number.isFinite(bd) && ad !== bd) return ad - bd
    return Number(b?.priority ?? 0) - Number(a?.priority ?? 0)
  })
  return sorted.slice(0, 6)
})

const projectTop = computed(() =>
  [...projects.value].sort((a, b) => {
    const ta = Number(new Date(a.updateTime || a.createTime || 0))
    const tb = Number(new Date(b.updateTime || b.createTime || 0))
    if (Number.isFinite(tb) && Number.isFinite(ta) && tb !== ta) return tb - ta
    return (b.id || 0) - (a.id || 0)
  }).slice(0, 5)
)

const reportTop = computed(() =>
  [...reports.value].sort((a, b) => {
    const ta = Number(new Date(a.uploadTime || 0))
    const tb = Number(new Date(b.uploadTime || 0))
    if (Number.isFinite(tb) && Number.isFinite(ta) && tb !== ta) return tb - ta
    return (b.id || 0) - (a.id || 0)
  }).slice(0, 5)
)

const favTop = computed(() => [...favorites.value].slice(0, 5))

const taskCounts = computed(() => {
  const rows = tasks.value || []
  let completed = 0
  let withdrawn = 0
  let overdue = 0
  let todo = 0
  for (const t of rows as any[]) {
    const s = t?.status
    if (s === 2) {
      completed++
      continue
    }
    if (s === 3) {
      withdrawn++
      continue
    }
    if (t?.overdue) {
      overdue++
      continue
    }
    todo++
  }
  return { completed, withdrawn, overdue, todo }
})

function taskTag(t: any) {
  const s = t?.status
  if (s === 2) return { text: '完成', cls: 'done' }
  if (s === 3) return { text: '撤回', cls: 'withdrawn' }
  if (t?.overdue) return { text: '逾期', cls: 'overdue' }
  return { text: '待办', cls: 'todo' }
}

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

const profileSubline = computed(() => {
  const u = profile.value?.username
  const id = uid.value
  if (u) return `@${u} · ID ${id || '-'}`
  return `ID ${id || '-'}`
})

const avatarSrc = computed(() => profile.value?.avatarUrl || defaultAvatar)

const profileDetailRows = computed(() => {
  const p = profile.value
  if (!p) return [] as { k: string; v: string }[]
  const rows: { k: string; v: string }[] = []
  if (p.email) rows.push({ k: '邮箱', v: p.email })
  if (p.phone) rows.push({ k: '手机', v: p.phone })
  if (p.gender) rows.push({ k: '性别', v: p.gender })
  if (p.status != null && String(p.status).trim() !== '') rows.push({ k: '状态', v: String(p.status) })
  return rows
})

const statExtraBits = computed(() => {
  const s = profile.value?.statistics
  if (!s) return [] as string[]
  const bits: string[] = []
  if (s.pendingTasks != null) bits.push(`待办 ${s.pendingTasks}`)
  if (s.totalDocuments != null) bits.push(`报告 ${s.totalDocuments}`)
  return bits
})

const typeLabelMap: Record<string, string> = {
  TASK: '任务',
  PROJECT: '项目',
  DOCUMENT: '文档',
  LITERATURE: '文献',
  PATENT: '专利',
  FORMULA: '方剂',
  HERB: '药材',
  COMPONENT: '成分',
  TARGET_PATHWAY: '靶点通路'
}

function favTypeLabel(t?: string | null) {
  if (!t) return '-'
  return typeLabelMap[t] || t
}

function statusText(s?: number | null) {
  const m: Record<number, string> = { 1: '立项中', 2: '进行中', 3: '已完成', 4: '已取消' }
  if (s == null) return '-'
  return m[s] || `状态${s}`
}

function go(p: string) {
  router.push(p)
}

function openSettings() {
  router.push(`/user/${uid.value}/settings`)
}

async function load() {
  loading.value = true
  error.value = null
  const id = uid.value
  const errs: string[] = []
  try {
    const settled = await Promise.allSettled([
      listMyTasks(id),
      listMyProjects(id),
      listMyReports(id),
      listUserFavorites(id),
      getUserFavoriteStatistics(id),
      getMyProfile(id)
    ])
    const pick = <T>(i: number, fallback: T) => {
      const r = settled[i]
      if (!r) return fallback
      if (r.status === 'fulfilled') return r.value as T
      const reason = r.reason
      errs.push(String(reason instanceof Error ? reason.message : reason))
      return fallback
    }
    tasks.value = pick(0, [] as TaskResponse[])
    projects.value = pick(1, [] as ProjectResponse[])
    reports.value = pick(2, [] as DocumentResponse[])
    favorites.value = pick(3, [] as FavoriteResponse[])
    favStats.value = pick(4, null as FavoriteStatisticsResponse | null)
    profile.value = pick(5, null as UserProfileResponse | null)
    if (errs.length === settled.length) error.value = errs[0] || '加载失败'
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="wrap">
    <div class="grid">
      <aside class="left">
        <div class="profile">
          <div class="avatar">
            <img class="avimg" :src="avatarSrc" alt="" />
          </div>
          <div class="pn">{{ displayName }}</div>
          <div class="ps">{{ profileSubline }}</div>
          <div v-if="profile?.statistics" class="statline">
            <span>{{ profile.statistics.totalTasks ?? 0 }} 任务</span>
            <span class="sd">·</span>
            <span>{{ profile.statistics.totalProjects ?? 0 }} 项目</span>
            <span class="sd">·</span>
            <span>{{ profile.statistics.totalFavorites ?? 0 }} 收藏</span>
          </div>
          <div v-if="statExtraBits.length" class="statextra">
            <template v-for="(b, i) in statExtraBits" :key="i">
              <span v-if="i > 0" class="sd">·</span>
              <span>{{ b }}</span>
            </template>
          </div>
          <div v-if="profile && profileDetailRows.length" class="pdetail">
            <div v-for="(row, i) in profileDetailRows" :key="i" class="prow">
              <span class="pk">{{ row.k }}</span>
              <span class="pv">{{ row.v }}</span>
            </div>
          </div>
          <div class="pbtns">
            <button class="btn outline" type="button" @click="openSettings">设置</button>
          </div>
        </div>
      </aside>

      <main class="right">
        <header class="head">
          <div>
            <div class="hk">个人中心</div>
            <div class="ht">概览</div>
          </div>
          <div class="hact">
            <div class="badges">
              <div class="badge b-todo">待办 {{ taskCounts.todo }}</div>
              <div class="badge b-overdue">逾期 {{ taskCounts.overdue }}</div>
              <div class="badge b-done">完成 {{ taskCounts.completed }}</div>
              <div class="badge b-withdrawn">撤回 {{ taskCounts.withdrawn }}</div>
            </div>
            <button class="btn ghost" type="button" @click="load">{{ loading ? '加载中…' : '刷新' }}</button>
          </div>
        </header>

        <div v-if="error" class="err">{{ error }}</div>

        <section class="card">
          <div class="cardh">
            <div class="ct">我的任务</div>
            <button class="link" type="button" @click="go('/me/tasks')">查看全部</button>
          </div>
          <div v-if="!todoTop.length && !loading" class="empty">暂无待办</div>
          <div v-else class="tasks">
            <button v-for="t in todoTop" :key="t.id" class="task" type="button" @click="go('/me/tasks')">
              <div class="tl">
                <span class="tt">{{ t.title }}</span>
                <span class="tag" :class="taskTag(t).cls">{{ taskTag(t).text }}</span>
              </div>
              <div class="tm">
                <span class="muted">{{ t.projectName || '未绑定项目' }}</span>
                <span class="muted">优先级 {{ priorityText(t.priority ?? null) }}</span>
                <span class="muted">截止 {{ dateOnly(t.deadline ?? null) }}</span>
              </div>
            </button>
          </div>
        </section>

        <div class="split">
          <section class="card">
            <div class="cardh">
              <div class="ct">我的项目</div>
              <button class="link" type="button" @click="go('/me/projects')">查看全部</button>
            </div>
            <div v-if="!projectTop.length && !loading" class="empty">暂无项目</div>
            <div v-else class="rows">
              <button
                v-for="p in projectTop"
                :key="p.id"
                class="rowline"
                type="button"
                @click="go('/me/projects')"
              >
                <span class="rname">{{ p.projectName || '未命名' }}</span>
                <span class="rmeta">{{ p.phase || '-' }} · {{ statusText(p.status) }}</span>
              </button>
            </div>
          </section>

          <section class="card">
            <div class="cardh">
              <div class="ct">我的文档</div>
              <button class="link" type="button" @click="go('/me/reports')">查看全部</button>
            </div>
            <div v-if="!reportTop.length && !loading" class="empty">暂无报告</div>
            <div v-else class="rows">
              <button
                v-for="d in reportTop"
                :key="d.id"
                class="rowline"
                type="button"
                @click="go('/me/reports')"
              >
                <span class="rname">{{ d.docName || '未命名' }}</span>
                <span class="rmeta">{{ d.projectName || '-' }} · {{ d.uploadTime ?? '-' }}</span>
              </button>
            </div>
          </section>
        </div>

        <section class="card">
          <div class="cardh">
            <div class="ct">我的收藏</div>
            <button class="link" type="button" @click="go(`/user/${uid}/favorites`)">查看全部</button>
          </div>
          <div v-if="favStats?.typeCountMap" class="tags">
            <span v-for="(v, k) in favStats.typeCountMap" :key="k" class="chip">{{ favTypeLabel(k) }} {{ v }}</span>
          </div>
          <div v-if="!favTop.length && !loading" class="empty">暂无收藏</div>
          <div v-else class="rows">
            <button
              v-for="(f, i) in favTop"
              :key="`${f.favoriteId}-${f.favoriteType}-${i}`"
              class="rowline"
              type="button"
              @click="go(`/user/${uid}/favorites`)"
            >
              <span class="rname">{{ f.entityName || '未命名' }}</span>
              <span class="rmeta">{{ favTypeLabel(f.favoriteType) }} · {{ f.collectTime ?? '-' }}</span>
            </button>
          </div>
        </section>
      </main>
    </div>
  </div>
</template>

<style scoped>
.wrap {
  max-width: 1440px;
  margin: 0 auto;
  padding: 10px 10px 28px;
  animation: in 0.45s ease both;
}

@keyframes in {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.grid {
  display: grid;
  grid-template-columns: 360px 1fr;
  gap: 24px;
  align-items: start;
}

.left {
  position: sticky;
  top: 10px;
}

.profile {
  padding: 0 8px 0 0;
}

.avatar {
  width: 148px;
  height: 148px;
  border-radius: 999px;
  display: grid;
  place-items: center;
  overflow: hidden;
  font-weight: 950;
  font-size: 44px;
  color: rgba(10, 34, 31, 0.95);
  background: radial-gradient(circle at 30% 30%, rgba(146, 230, 202, 0.95), rgba(200, 169, 103, 0.55));
  border: 1px solid rgba(255, 255, 255, 0.22);
  box-shadow: 0 8px 28px rgba(0, 0, 0, 0.25);
}

.avimg {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.pn {
  margin-top: 16px;
  font-size: 28px;
  font-weight: 950;
  line-height: 1.2;
  color: rgba(255, 255, 255, 0.94);
}

.ps {
  margin-top: 6px;
  font-size: 15px;
  color: rgba(255, 255, 255, 0.52);
  font-weight: 700;
}

.statline {
  margin-top: 14px;
  font-size: 16px;
  font-weight: 800;
  color: rgba(255, 255, 255, 0.72);
  line-height: 1.5;
  display: flex;
  flex-wrap: wrap;
  gap: 4px 6px;
  align-items: center;
}

.sd {
  color: rgba(255, 255, 255, 0.35);
  user-select: none;
}

.statextra {
  margin-top: 10px;
  font-size: 15px;
  font-weight: 800;
  color: rgba(255, 255, 255, 0.55);
  display: flex;
  flex-wrap: wrap;
  gap: 4px 6px;
  align-items: center;
}

.pdetail {
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  display: grid;
  gap: 8px;
}

.prow {
  display: grid;
  grid-template-columns: 52px 1fr;
  gap: 8px;
  font-size: 15px;
  line-height: 1.4;
}

.pk {
  color: rgba(255, 255, 255, 0.45);
  font-weight: 800;
}

.pv {
  color: rgba(255, 255, 255, 0.78);
  font-weight: 750;
  word-break: break-all;
}

.pbtns {
  display: grid;
  grid-template-columns: 1fr;
  gap: 8px;
  margin-top: 16px;
  max-width: 100%;
}

.btn {
  border: 0;
  cursor: pointer;
  border-radius: 12px;
  padding: 11px 14px;
  font-weight: 900;
  font-size: 16px;
  color: rgba(255, 255, 255, 0.92);
  background: rgba(255, 255, 255, 0.12);
}

.btn.outline {
  width: 100%;
  border: 1px solid rgba(255, 255, 255, 0.22);
  background: rgba(255, 255, 255, 0.04);
}

.btn.ghost {
  background: rgba(255, 255, 255, 0.06);
}

.right {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.head {
  display: flex;
  justify-content: space-between;
  align-items: end;
  gap: 12px;
}

.hk {
  font-size: 13px;
  letter-spacing: 0.22em;
  text-transform: uppercase;
  font-weight: 850;
  color: rgba(255, 255, 255, 0.42);
}

.ht {
  margin-top: 8px;
  font-size: 28px;
  font-weight: 950;
  color: rgba(255, 255, 255, 0.94);
}

.hact {
  display: flex;
  gap: 10px;
  align-items: center;
}

.badges {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
}

.badge {
  border-radius: 999px;
  padding: 8px 10px;
  font-size: 15px;
  font-weight: 950;
  color: rgba(10, 34, 31, 0.95);
  background: rgba(200, 169, 103, 0.92);
}

.badge.b-overdue {
  color: rgba(255, 255, 255, 0.96);
  background: rgba(220, 65, 65, 0.92);
}

.badge.b-done {
  color: rgba(10, 34, 31, 0.95);
  background: rgba(115, 209, 180, 0.92);
}

.badge.b-withdrawn {
  color: rgba(255, 255, 255, 0.86);
  background: rgba(255, 255, 255, 0.16);
}

.badge.b-todo {
  color: rgba(10, 34, 31, 0.95);
  background: rgba(240, 200, 90, 0.92);
}

.card {
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(14px);
  padding: 16px;
}

.cardh {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.ct {
  font-size: 17px;
  font-weight: 950;
  color: rgba(255, 255, 255, 0.9);
}

.link {
  border: 0;
  cursor: pointer;
  background: transparent;
  color: rgba(146, 230, 202, 0.95);
  font-weight: 900;
  font-size: 16px;
}

.empty {
  color: rgba(255, 255, 255, 0.6);
  font-size: 16px;
  padding: 8px 0 2px;
}

.tasks {
  display: grid;
  gap: 10px;
}

.task {
  border: 0;
  cursor: pointer;
  text-align: left;
  border-radius: 14px;
  padding: 14px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.08);
  transition: transform 0.15s ease, background 0.15s ease;
}

.task:hover {
  transform: translateY(-1px);
  background: rgba(255, 255, 255, 0.08);
}

.tl {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.tt {
  font-size: 16px;
  font-weight: 950;
  color: rgba(255, 255, 255, 0.92);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tag {
  border-radius: 999px;
  padding: 6px 8px;
  font-size: 15px;
  font-weight: 950;
  color: rgba(255, 255, 255, 0.86);
  background: rgba(255, 255, 255, 0.1);
}

.tag.overdue {
  color: rgba(255, 255, 255, 0.96);
  background: rgba(220, 65, 65, 0.92);
}

.tag.todo {
  color: rgba(10, 34, 31, 0.95);
  background: rgba(240, 200, 90, 0.92);
}

.tag.done {
  color: rgba(10, 34, 31, 0.95);
  background: rgba(115, 209, 180, 0.92);
}

.tag.withdrawn {
  color: rgba(255, 255, 255, 0.86);
  background: rgba(255, 255, 255, 0.16);
}

.tm {
  margin-top: 8px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px 14px;
}

.muted {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.6);
  font-weight: 800;
}

.split {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  align-items: start;
}

.rows {
  display: grid;
  gap: 8px;
}

.rowline {
  border: 0;
  cursor: pointer;
  text-align: left;
  border-radius: 14px;
  padding: 12px 14px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.08);
  transition: background 0.15s ease;
}

.rowline:hover {
  background: rgba(255, 255, 255, 0.08);
}

.rname {
  display: block;
  font-size: 15px;
  font-weight: 950;
  color: rgba(255, 255, 255, 0.9);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.rmeta {
  display: block;
  margin-top: 6px;
  font-size: 15px;
  color: rgba(255, 255, 255, 0.55);
  font-weight: 800;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 10px;
}

.chip {
  border-radius: 999px;
  padding: 6px 10px;
  font-size: 15px;
  font-weight: 850;
  color: rgba(255, 255, 255, 0.85);
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.err {
  border-radius: 12px;
  padding: 12px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 0, 0, 0.08);
  color: rgba(255, 255, 255, 0.9);
  font-weight: 800;
  font-size: 16px;
}

@media (max-width: 980px) {
  .grid {
    grid-template-columns: 1fr;
  }
  .left {
    position: static;
  }
  .split {
    grid-template-columns: 1fr;
  }
}
</style>

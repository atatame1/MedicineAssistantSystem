<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { getPortalOverview, triggerPortalSummary, type PortalOverview } from '@/api/portal'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
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

    <div class="bento">
      <div class="section section-summary">
        <div class="section-head">
          <div class="section-title">近期用户任务摘要</div>
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
          :stroke-width="8"
          striped
          striped-flow
        />
        <div class="summary-text">{{ displaySummary }}</div>
      </div>

      <div class="kpi kpi-a">
        <div class="kpi-label">我的项目</div>
        <div class="kpi-value">{{ projectCount }}</div>
        <div class="kpi-tip">最近更新前10个项目</div>
      </div>
      <div class="kpi kpi-b">
        <div class="kpi-label">待办任务</div>
        <div class="kpi-value">{{ taskCount }}</div>
        <div class="kpi-tip">包含实验与审批任务</div>
      </div>
      <div class="kpi kpi-c">
        <div class="kpi-label">风险预警</div>
        <div class="kpi-value">{{ riskCount }}</div>
        <div class="kpi-tip">高优先级项目风险提醒</div>
      </div>

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
    </div>
  </div>
</template>

<style scoped>
.mb-16 {
  margin-bottom: 16px;
}

.page-wrap {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.bento {
  display: grid;
  grid-template-columns: repeat(12, minmax(0, 1fr));
  gap: 12px;
}

.section-summary {
  grid-column: span 12;
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 16px;
  padding: 12px 14px 14px;
  background: rgba(255, 255, 255, 0.04);
}

.section-summary .section-head {
  justify-content: space-between;
}

.summary-progress {
  margin: 10px 0 8px;
}

.summary-text {
  font-size: 14px;
  line-height: 1.65;
  color: rgba(255, 255, 255, 0.88);
  white-space: pre-wrap;
}

.kpi {
  grid-column: span 4;
  padding: 10px 2px 14px;
  color: rgba(255, 255, 255, 0.92);
  position: relative;
}

.kpi::before {
  content: '';
  position: absolute;
  inset: -6px -6px -10px -6px;
  border-radius: 28px;
  filter: blur(14px);
  opacity: 0.55;
  pointer-events: none;
}

.kpi-a::before {
  background: radial-gradient(circle at 20% 20%, rgba(115, 209, 180, 0.9), transparent 60%);
}

.kpi-b::before {
  background: radial-gradient(circle at 20% 20%, rgba(146, 230, 202, 0.8), transparent 60%);
}

.kpi-c::before {
  background: radial-gradient(circle at 20% 20%, rgba(200, 169, 103, 0.85), transparent 60%);
}

.kpi-label {
  opacity: 0.9;
}

.kpi-value {
  margin-top: 8px;
  font-size: 34px;
  line-height: 1;
  font-weight: 700;
}

.kpi-tip {
  margin-top: 8px;
  opacity: 0.85;
  font-size: 12px;
}

.section {
  color: rgba(255, 255, 255, 0.92);
}

.section-projects {
  grid-column: span 8;
}

.section-tasks {
  grid-column: span 4;
}

.section-head {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 6px 0 10px;
}

.section-title {
  font-size: 14px;
  font-weight: 900;
  letter-spacing: 0.4px;
}

.section-line {
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, rgba(255, 255, 255, 0.35), rgba(255, 255, 255, 0.02));
}

.table-free {
  border-radius: 16px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.12);
}

.empty-text {
  color: rgba(255, 255, 255, 0.72);
  padding: 12px 0;
}

.task-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.task-item {
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 16px;
  padding: 10px 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(12px);
}

.task-main {
  min-width: 0;
}

.task-title {
  font-weight: 600;
  color: rgba(255, 255, 255, 0.92);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.task-meta {
  margin-top: 5px;
  color: rgba(255, 255, 255, 0.68);
  font-size: 12px;
  display: flex;
  gap: 12px;
}

@media (max-width: 980px) {
  .kpi {
    grid-column: span 12;
  }
  .section-projects {
    grid-column: span 12;
  }
  .section-tasks {
    grid-column: span 12;
  }
}
</style>

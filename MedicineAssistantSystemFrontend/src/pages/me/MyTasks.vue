<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { completeTask, listMyTasks, type TaskResponse } from '@/api/user'
import { useAuthStore } from '@/stores/auth'
import { ElMessage, ElMessageBox } from 'element-plus'

const auth = useAuthStore()
const userId = computed(() => auth.user?.userId || 0)
const loading = ref(false)
const error = ref<string | null>(null)
const rows = ref<TaskResponse[]>([])
const completeOpen = ref(false)
const completing = ref(false)
const completeTaskId = ref<number | null>(null)
const completeTitle = ref('')
const completeFeedback = ref('')
const completeDeadline = ref<string | null>(null)
const completeOverdue = ref(false)
const detailOpen = ref(false)
const detailTitle = ref('')
const detailDesc = ref('')

function descShort(d?: string | null) {
  const t = (d || '').trim()
  if (!t) return '-'
  return t.length > 24 ? `${t.slice(0, 24)}…` : t
}

function priorityText(p?: number | null) {
  if (p === 1) return '高'
  if (p === 2) return '中'
  if (p === 3) return '低'
  return '-'
}

function tagType(row: TaskResponse) {
  if (row.status === 2) return { text: '完成', cls: 'done' }
  if (row.status === 3) return { text: '撤回', cls: 'withdrawn' }
  if (row.overdue) return { text: '逾期', cls: 'overdue' }
  return { text: '待办', cls: 'todo' }
}

function openDesc(row: TaskResponse) {
  detailTitle.value = row.title
  detailDesc.value = row.description || ''
  detailOpen.value = true
}

function statusText(s?: number | null) {
  const m: Record<number, string> = { 0: '已发布', 1: '进行中', 2: '已完成', 3: '已撤回' }
  if (s == null) return '-'
  return m[s] ?? `状态${s}`
}

function parseDeadlineMs(d?: string | null) {
  if (!d) return null
  const t = Date.parse(d)
  if (!Number.isFinite(t)) return null
  return t
}

function openComplete(row: TaskResponse) {
  if (row.status === 3) return
  completeTaskId.value = row.id
  completeTitle.value = row.title
  completeFeedback.value = row.completionFeedback || ''
  completeDeadline.value = row.deadline || null
  const dl = parseDeadlineMs(row.deadline)
  completeOverdue.value = dl != null && Date.now() > dl
  completeOpen.value = true
}

async function submitComplete() {
  if (!completeTaskId.value || !userId.value) return
  if (!completeFeedback.value.trim()) return
  completing.value = true
  try {
    if (completeOverdue.value) {
      await ElMessageBox.alert('该任务已超过截止时间，将以逾期提交记录。', '逾期提交', {
        confirmButtonText: '继续提交'
      })
    }
    await completeTask(userId.value, completeTaskId.value, {
      completionFeedback: completeFeedback.value.trim()
    })
    completeOpen.value = false
    ElMessage.success('已提交')
    await load()
  } finally {
    completing.value = false
  }
}

async function load() {
  loading.value = true
  error.value = null
  try {
    rows.value = await listMyTasks(userId.value)
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="page">
    <header class="head">
      <div>
        <p class="kicker">工作台</p>
        <h1 class="t">我的任务</h1>
        <p class="s">待办与截止</p>
      </div>
      <el-button class="btn" round :loading="loading" @click="load">刷新</el-button>
    </header>

    <el-alert v-if="error" :title="error" type="error" show-icon />

    <div class="panel">
      <el-table v-loading="loading" :data="rows" stripe>
        <el-table-column prop="title" label="标题" min-width="240" />
        <el-table-column label="任务描述" min-width="260">
          <template #default="{ row }">
            <el-button class="desc-link" link type="primary" @click="openDesc(row)">
              {{ descShort(row.description) }}
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="projectName" label="项目" min-width="200" />
        <el-table-column label="优先级" width="90">
          <template #default="{ row }">
            {{ priorityText(row.priority ?? null) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <span class="st" :class="tagType(row).cls">{{ tagType(row).text }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="deadline" label="截止时间" width="180" />
        <el-table-column label="逾期" width="90">
          <template #default="{ row }">
            <el-tag v-if="row.overdue" type="danger">是</el-tag>
            <el-tag v-else effect="light">否</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button
              size="small"
              type="primary"
              plain
              :disabled="row.status === 3"
              @click="openComplete(row)"
            >{{ row.status === 2 ? '修改报告' : '提交报告' }}</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="completeOpen" title="完成报告" width="560px" append-to-body>
      <div style="font-weight: 900; margin-bottom: 10px">{{ completeTitle }}</div>
      <div style="opacity: 0.75; margin-bottom: 10px">
        截止：{{ completeDeadline || '-' }}
        <el-tag v-if="completeOverdue" type="danger" effect="dark" style="margin-left: 8px">逾期提交</el-tag>
      </div>
      <el-input
        v-model="completeFeedback"
        type="textarea"
        :rows="6"
        maxlength="2000"
        show-word-limit
        placeholder="填写完成报告"
      />
      <template #footer>
        <el-button @click="completeOpen = false">取消</el-button>
        <el-button type="primary" :loading="completing" @click="submitComplete">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailOpen" width="720px" append-to-body>
      <div class="dt">详细描述</div>
      <div class="dc">{{ detailDesc || '-' }}</div>
      <template #footer>
        <el-button @click="detailOpen = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-width: 1320px;
  width: 100%;
  margin: 0 auto;
  animation: in 0.5s ease both;
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

.head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 12px;
}

.kicker {
  margin: 0 0 6px;
  font-size: 13px;
  font-weight: 800;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.42);
}

.t {
  margin: 0;
  font-size: 24px;
  font-weight: 950;
  color: rgba(255, 255, 255, 0.94);
}

.s {
  margin-top: 8px;
  font-size: 15px;
  color: rgba(255, 255, 255, 0.58);
}

.btn {
  align-self: center;
}

.panel {
  border-radius: 22px;
  padding: 4px 4px 8px;
  background: linear-gradient(165deg, rgba(255, 255, 255, 0.06), rgba(10, 38, 32, 0.36));
  backdrop-filter: blur(12px);
  border: 1px solid rgba(115, 209, 180, 0.2);
}

.panel :deep(.el-table) {
  --el-table-bg-color: rgba(255, 255, 255, 0.03);
  --el-table-tr-bg-color: rgba(255, 255, 255, 0.03);
  --el-table-header-bg-color: rgba(0, 0, 0, 0.28);
  --el-table-row-hover-bg-color: rgba(255, 255, 255, 0.07);
  --el-table-text-color: rgba(255, 255, 255, 0.9);
  --el-table-header-text-color: rgba(255, 255, 255, 0.76);
  --el-table-border-color: rgba(255, 255, 255, 0.1);
  --el-fill-color-lighter: rgba(255, 255, 255, 0.03);
  background: transparent;
}

.panel :deep(.el-table__inner-wrapper::before) {
  display: none;
}

.st {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 5px 10px;
  border-radius: 999px;
  font-size: 14px;
  font-weight: 950;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.86);
}

.st.overdue {
  color: rgba(255, 255, 255, 0.96);
  background: rgba(220, 65, 65, 0.92);
  border-color: rgba(255, 120, 120, 0.25);
}

.st.todo {
  color: rgba(10, 34, 31, 0.95);
  background: rgba(240, 200, 90, 0.92);
  border-color: rgba(255, 255, 255, 0.12);
}

.st.done {
  color: rgba(10, 34, 31, 0.95);
  background: rgba(115, 209, 180, 0.92);
  border-color: rgba(115, 209, 180, 0.25);
}

.st.withdrawn {
  color: rgba(255, 255, 255, 0.86);
  background: rgba(255, 255, 255, 0.16);
  border-color: rgba(255, 255, 255, 0.12);
}

.desc-link {
  color: rgba(103, 197, 200, 0.95) !important;
  /* font-weight: 850; */
}
.desc-link:hover {
  color: rgba(22, 230, 241, 0.719) !important;
}

.dt {
  font-size: 14px;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  margin-bottom: 8px;
}

.dc {
  border-radius: 10px;
  padding: 10px 12px;
  font-size: 16px;
  color: #303133;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.dc-desc {
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.8;
}

.panel :deep(.el-alert) {
  background: rgba(180, 40, 40, 0.2);
  border: 1px solid rgba(255, 120, 120, 0.38);
}
</style>


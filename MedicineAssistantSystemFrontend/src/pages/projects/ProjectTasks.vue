<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listMyProjects, type MyProjectItem } from '@/api/projects'
import { listUsers, type UserListItem } from '@/api/user'
import { createTask, getTaskDetail, listProjectTasks, withdrawTask } from '@/api/tasks.ts'
import type { TaskResponse } from '@/api/user'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const myUserId = computed(() => auth.user?.userId ?? 0)

const loading = ref(false)
const saving = ref(false)
const error = ref<string | null>(null)

const projects = ref<MyProjectItem[]>([])
const projectId = ref<number | null>(null)

const users = ref<UserListItem[]>([])

const rows = ref<TaskResponse[]>([])

const detailOpen = ref(false)
const detailLoading = ref(false)
const detail = ref<TaskResponse | null>(null)

const createOpen = ref(false)
const form = ref({
  userId: null as number | null,
  title: '',
  description: '',
  priority: 2,
  deadline: '' as string
})

const PRIORITY_OPTIONS = [
  { value: 1, label: '高' },
  { value: 2, label: '中' },
  { value: 3, label: '低' }
] as const

function priorityText(p?: number | null) {
  if (p === 1) return '高'
  if (p === 2) return '中'
  if (p === 3) return '低'
  return '-'
}

function statusText(s?: number | null) {
  const m: Record<number, string> = { 0: '已发布', 1: '进行中', 2: '已完成', 3: '已撤回' }
  if (s == null) return '-'
  return m[s] ?? `状态${s}`
}

function dateOnly(s?: string | null) {
  if (!s) return '-'
  return String(s).slice(0, 10)
}

function descShort(row: TaskResponse) {
  const d = (row.description || '').trim()
  if (!d) return '-'
  return d.length > 18 ? `${d.slice(0, 18)}…` : d
}

function tagType(row: TaskResponse) {
  if (row.status === 2) return { text: '完成', cls: 'done' }
  if (row.status === 3) return { text: '撤回', cls: 'withdrawn' }
  if (row.overdue) return { text: '逾期', cls: 'overdue' }
  return { text: '待办', cls: 'todo' }
}

function canWithdraw(row: TaskResponse) {
  if (!myUserId.value) return false
  if (row.assigneeId !== myUserId.value) return false
  return row.status !== 2 && row.status !== 3
}

async function loadProjects() {
  if (!myUserId.value) return
  projects.value = await listMyProjects(myUserId.value)
  if (!projectId.value && projects.value.length) {
    const first = projects.value.find(p => p.id != null)
    if (first?.id != null) projectId.value = first.id
  }
}

async function loadUsers() {
  users.value = await listUsers()
}

async function loadTasks() {
  if (!projectId.value) {
    rows.value = []
    return
  }
  loading.value = true
  error.value = null
  try {
    rows.value = await listProjectTasks(projectId.value)
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    loading.value = false
  }
}

async function openDetail(row: TaskResponse) {
  detailOpen.value = true
  detailLoading.value = true
  detail.value = null
  try {
    detail.value = await getTaskDetail(row.id)
  } finally {
    detailLoading.value = false
  }
}

async function doWithdraw(row: TaskResponse) {
  if (!canWithdraw(row)) return
  await ElMessageBox.confirm('确认撤回该任务？', '撤回任务', { type: 'warning' })
  await withdrawTask(row.id, myUserId.value)
  ElMessage.success('已撤回')
  await loadTasks()
}

function openCreate() {
  if (!projectId.value) return
  form.value = { userId: null, title: '', description: '', priority: 2, deadline: '' }
  createOpen.value = true
}

async function submitCreate() {
  if (!projectId.value || !myUserId.value) return
  if (!form.value.userId || !form.value.title.trim() || !form.value.deadline) return
  saving.value = true
  try {
    await createTask(myUserId.value, {
      projectId: projectId.value,
      userId: form.value.userId,
      title: form.value.title.trim(),
      description: form.value.description?.trim() || '',
      priority: form.value.priority,
      deadline: form.value.deadline
    })
    createOpen.value = false
    ElMessage.success('已发布')
    await loadTasks()
  } finally {
    saving.value = false
  }
}

watch(projectId, () => loadTasks())

onMounted(async () => {
  await Promise.all([loadProjects(), loadUsers()])
  await loadTasks()
})
</script>

<template>
  <div class="page">
    <header class="head">
      <div>
        <p class="kicker">项目中心</p>
        <h1 class="t">任务管理</h1>
        <p class="s">发布 · 查看 · 撤回 · 详情</p>
      </div>
      <div class="head-tools">
        <el-select v-model="projectId" class="sel" placeholder="选择项目" filterable>
          <el-option
            v-for="p in projects"
            :key="p.id"
            :value="p.id"
            :label="p.projectName"
          />
        </el-select>
        <el-button round :loading="loading" @click="loadTasks">刷新</el-button>
        <el-button type="primary" round @click="openCreate">发布任务</el-button>
      </div>
    </header>

    <el-alert v-if="error" :title="error" type="error" show-icon />

    <div class="panel">
      <el-table v-loading="loading" :data="rows" stripe>
        <el-table-column prop="title" label="标题" min-width="220" />
        <el-table-column label="描述" min-width="220">
          <template #default="{ row }">
            {{ descShort(row) }}
          </template>
        </el-table-column>
        <el-table-column label="执行人" width="140">
          <template #default="{ row }">
            {{ row.userName || (row.userId ? `用户 ${row.userId}` : '-') }}
          </template>
        </el-table-column>
        <el-table-column label="指派人" width="140">
          <template #default="{ row }">
            {{ row.assigneeName || (row.assigneeId ? `用户 ${row.assigneeId}` : '-') }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <span class="st" :class="tagType(row).cls">{{ tagType(row).text }}</span>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="130">
          <template #default="{ row }">
            {{ dateOnly(row.createTime || null) }}
          </template>
        </el-table-column>
        <el-table-column label="截止" width="130">
          <template #default="{ row }">
            {{ dateOnly(row.deadline || null) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDetail(row)">详情</el-button>
            <el-button
              size="small"
              type="danger"
              plain
              :disabled="!canWithdraw(row)"
              @click="doWithdraw(row)"
            >撤回</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog
      v-model="detailOpen"
      title="任务详情"
      width="860px"
      append-to-body
      class="task-detail-dialog"
    >
      <div v-loading="detailLoading" class="detail-shell">
        <el-descriptions v-if="detail" :column="1" border class="detail-desc">
          <el-descriptions-item label="标题">{{ detail.title || '-' }}</el-descriptions-item>
          <el-descriptions-item label="描述">{{ detail.description || '-' }}</el-descriptions-item>
          <el-descriptions-item label="执行人">
            {{ detail.userName || (detail.userId ? `用户 ${detail.userId}` : '-') }}
          </el-descriptions-item>
          <el-descriptions-item label="指派人">
            {{ detail.assigneeName || (detail.assigneeId ? `用户 ${detail.assigneeId}` : '-') }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <span class="st" :class="tagType(detail).cls">{{ tagType(detail).text }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ dateOnly(detail.createTime || null) }}</el-descriptions-item>
          <el-descriptions-item label="截止">{{ dateOnly(detail.deadline || null) }}</el-descriptions-item>
          <el-descriptions-item label="优先级">{{ priorityText(detail.priority ?? null) }}</el-descriptions-item>
          <el-descriptions-item label="完成报告">{{ detail.completionFeedback || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailOpen = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-drawer
      v-model="createOpen"
      title="发布任务"
      direction="rtl"
      size="520px"
      append-to-body
      destroy-on-close
    >
      <div class="form">
        <el-form label-width="92px">
          <el-form-item label="项目">
            <div class="mono">{{ projects.find(p => p.id === projectId)?.projectName || '-' }}</div>
          </el-form-item>
          <el-form-item label="执行人">
            <el-select v-model="form.userId" placeholder="选择执行人" filterable>
              <el-option
                v-for="u in users"
                :key="u.id"
                :value="u.id"
                :label="u.nickname || u.username || `用户 ${u.id}`"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="标题">
            <el-input v-model="form.title" maxlength="80" show-word-limit />
          </el-form-item>
          <el-form-item label="描述">
            <el-input v-model="form.description" type="textarea" :rows="4" maxlength="800" show-word-limit />
          </el-form-item>
          <el-form-item label="优先级">
            <el-select v-model="form.priority" placeholder="选择优先级">
              <el-option
                v-for="o in PRIORITY_OPTIONS"
                :key="o.value"
                :value="o.value"
                :label="o.label"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="截止日期">
            <el-date-picker
              v-model="form.deadline"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="选择日期"
            />
          </el-form-item>
        </el-form>
        <div class="form-actions">
          <el-button @click="createOpen = false">取消</el-button>
          <el-button type="primary" :loading="saving" @click="submitCreate">提交</el-button>
        </div>
      </div>
    </el-drawer>
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

.head-tools {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  align-items: center;
}

.sel {
  width: 240px;
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

.detail-shell {
  max-height: min(66vh, 620px);
  overflow: auto;
  padding-right: 4px;
}

.detail-desc :deep(.el-descriptions__label) {
  width: 210px;
  font-weight: 800;
  color: rgba(0, 0, 0, 0.62) !important;
  background: rgba(245, 247, 250, 1) !important;
}

.detail-desc :deep(.el-descriptions__content) {
  background: rgba(255, 255, 255, 1) !important;
  color: rgba(0, 0, 0, 0.82) !important;
  font-weight: 600;
  white-space: pre-wrap;
  word-break: break-word;
}

.task-detail-dialog :deep(.el-dialog) {
  border-radius: 12px;
}

.task-detail-dialog :deep(.el-dialog__body) {
  padding-top: 8px;
}

.form {
  padding: 0 10px 10px;
}

.mono {
  font-weight: 800;
  color: rgba(255, 255, 255, 0.9);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 10px;
}
</style>


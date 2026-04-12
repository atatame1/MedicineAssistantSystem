<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import {
  listMyProjects,
  updateProjectPhase,
  updateProjectStatus,
  type MyProjectItem
} from '@/api/projects'
import { useAuthStore } from '@/stores/auth'
import ProjectCreateForm from './ProjectCreateForm.vue'
import {
  projectsExtraApi,
  type ProjectBoard,
  type ProjectDocument,
  type ProjectMember
} from '@/api/projectsExtra'
import { projectAiAssessToHtml } from '@/utils/projectAiMarkdown'

const auth = useAuthStore()
const userId = computed(() => auth.user?.userId ?? 0)

const statusFilter = ref<number | ''>('')
const loadingList = ref(false)
const loadingRight = ref(false)
const error = ref<string | null>(null)

const projects = ref<MyProjectItem[]>([])
const activeId = ref<number | null>(null)

const board = ref<ProjectBoard | null>(null)
const documents = ref<ProjectDocument[]>([])
const members = ref<ProjectMember[]>([])

const createDrawerOpen = ref(false)

const statusEditOpen = ref(false)
const phaseEditOpen = ref(false)
const editStatus = ref<number | null>(null)
const editPhase = ref<string | null>(null)
const savingStatus = ref(false)
const savingPhase = ref(false)

const STATUS_EDIT_OPTIONS = [
  { value: 1, label: '立项中' },
  { value: 2, label: '进行中' },
  { value: 3, label: '已完成' },
  { value: 4, label: '已取消' }
] as const

const PHASE_EDIT_OPTIONS = [
  { value: 'EXPLORATION', label: '探索期' },
  { value: 'VERIFICATION', label: '验证期' },
  { value: 'OPTIMIZATION', label: '优化期' },
  { value: 'DECLARATION', label: '申报期' }
] as const

let extraLoadSeq = 0

async function loadList() {
  loadingList.value = true
  error.value = null
  try {
    if (!userId.value) {
      projects.value = []
      return
    }
    projects.value = await listMyProjects(
      userId.value,
      statusFilter.value === '' ? undefined : statusFilter.value
    )
    if (!activeId.value && projects.value.length > 0) {
      const first = projects.value.find(p => p.id != null)
      if (first && first.id != null) {
        activeId.value = first.id
        loadExtra(first.id)
      }
    }
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    loadingList.value = false
  }
}

async function loadExtra(projectId: number) {
  const seq = ++extraLoadSeq
  loadingRight.value = true
  try {
    const [b, docs, mems] = await Promise.all([
      projectsExtraApi.board(projectId),
      projectsExtraApi.documents(projectId),
      projectsExtraApi.members(projectId)
    ])
    if (seq !== extraLoadSeq) return
    board.value = b
    documents.value = docs
    members.value = mems
  } catch (e: any) {
  } finally {
    if (seq === extraLoadSeq) loadingRight.value = false
  }
}

function statusText(status?: number | null) {
  const m: Record<number, string> = { 1: '立项中', 2: '进行中', 3: '已完成', 4: '已取消' }
  if (status == null) return '-'
  return m[status] || `状态${status}`
}

function phaseText(phase?: string | null) {
  if (!phase) return '-'
  const m: Record<string, string> = {
    EXPLORATION: '探索期',
    VERIFICATION: '验证期',
    OPTIMIZATION: '优化期',
    DECLARATION: '申报期'
  }
  return m[phase] ?? phase
}

function priorityText(p?: number | null) {
  if (p == null) return '-'
  if (p === 1) return '高'
  if (p === 2) return '中'
  if (p === 3) return '低'
  return String(p)
}

const activeProject = computed(() =>
  projects.value.find(p => p.id === activeId.value) || null
)

const aiAssessHtml = computed(() => {
  const t = activeProject.value?.aiAssess?.trim()
  if (!t) return ''
  return projectAiAssessToHtml(t)
})

const dashboardProgressPercent = computed(() => {
  const s = activeProject.value?.status
  if (s == null) return 0
  const m: Record<number, number> = { 4: 0, 1: 25, 2: 50, 3: 100 }
  return m[s] ?? 0
})

function myRoleText(role?: string | null) {
  if (role === 'LEADER') return '负责人'
  if (role === 'MEMBER') return '成员'
  return '-'
}

function handleSelect(p: MyProjectItem) {
  if (!p.id || p.id === activeId.value) return
  activeId.value = p.id
  loadExtra(p.id)
}

function goCreate() {
  createDrawerOpen.value = true
}

function onCreateSuccess() {
  createDrawerOpen.value = false
  loadList()
}

function patchActiveProject(partial: Partial<MyProjectItem>) {
  const id = activeId.value
  if (id == null) return
  const i = projects.value.findIndex(p => p.id === id)
  if (i >= 0) {
    projects.value[i] = { ...projects.value[i], ...partial } as MyProjectItem
  }
}

function openStatusEdit() {
  if (!activeProject.value?.id) return
  editStatus.value = activeProject.value.status ?? null
  statusEditOpen.value = true
}

function openPhaseEdit() {
  if (!activeProject.value?.id) return
  editPhase.value = activeProject.value.phase ?? null
  phaseEditOpen.value = true
}

async function saveStatus() {
  const pid = activeId.value
  if (pid == null || editStatus.value == null) return
  savingStatus.value = true
  error.value = null
  try {
    await updateProjectStatus(pid, editStatus.value)
    patchActiveProject({ status: editStatus.value })
    statusEditOpen.value = false
    await loadExtra(pid)
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    savingStatus.value = false
  }
}

async function savePhase() {
  const pid = activeId.value
  if (pid == null || editPhase.value == null || editPhase.value === '') return
  savingPhase.value = true
  error.value = null
  try {
    await updateProjectPhase(pid, editPhase.value)
    patchActiveProject({ phase: editPhase.value })
    phaseEditOpen.value = false
    await loadExtra(pid)
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    savingPhase.value = false
  }
}

function memberDisplayName(m: ProjectMember) {
  const n = m.userNickname?.trim()
  if (n) return n
  return `用户 ${m.userId}`
}

const toolLinks = [
  { to: '/projects/decisions', label: '决策' },
  { to: '/projects/documents', label: '文档' },
  { to: '/projects/members', label: '成员' },
  { to: '/projects/draft-evaluate', label: '立项评估' }
] as const

onMounted(loadList)
</script>

<template>
  <div class="proj-page">
    <div class="proj-shell">

      <header class="proj-header">
        <div class="title-block">
          <div class="title-main">项目自由探索</div>
          <div class="title-sub">一屏纵览 · 右侧剖面 · 动态脉络</div>
        </div>
        <div class="header-tools">
          <el-select
            v-model="statusFilter"
            class="status-select"
            placeholder="全部状态"
            clearable
            popper-class="proj-status-dropdown"
            @change="loadList"
          >
            <el-option :value="1" label="立项中" />
            <el-option :value="2" label="进行中" />
            <el-option :value="3" label="已完成" />
            <el-option :value="4" label="已取消" />
          </el-select>
          <el-button size="small" @click="loadList" :loading="loadingList">刷新</el-button>
          <el-button type="primary" size="small" @click="goCreate">创建项目</el-button>
        </div>
      </header>

      <nav class="proj-tools-bar" aria-label="项目扩展工具">
        <span class="proj-tools-label">快捷工具</span>
        <div class="proj-tools-pills">
          <router-link
            v-for="item in toolLinks"
            :key="item.to"
            :to="item.to"
            class="tool-pill"
          >
            {{ item.label }}
          </router-link>
        </div>
      </nav>

      <el-alert
        v-if="error"
        :title="error"
        type="error"
        show-icon
        class="alert"
      />

      <main class="proj-layout">
        <section class="left-lane">
          <div class="lane-label">项目轨道</div>
          <div
            class="lane-scroll"
            v-loading="loadingList"
            element-loading-background="rgba(6, 28, 24, 0.35)"
            element-loading-custom-class="proj-lane-loading"
          >
            <button
              v-for="p in projects"
              :key="p.id"
              type="button"
              class="proj-pill"
              :class="{ active: p.id === activeId }"
              @click="handleSelect(p)"
            >
              <div class="pill-main-line">
                <span class="pill-id">#{{ p.id }}</span>
                <span class="pill-name">{{ p.projectName }}</span>
              </div>
              <div class="pill-meta">
                <span class="pill-tag phase">{{ phaseText(p.phase) }}</span>
                <span class="pill-tag">{{ statusText(p.status) }}</span>
                <span class="pill-tag">{{ myRoleText(p.currentUserRole) }}</span>
                <span v-if="p.priority != null" class="pill-tag ghost">
                  优先级 {{ priorityText(p.priority) }}
                </span>
              </div>
              <div class="pill-desc">
                <span v-if="p.herbName">药材：{{ p.herbName }}</span>
                <span v-if="p.formulaName">方剂：{{ p.formulaName }}</span>
                <span v-if="p.indication">适应症：{{ p.indication }}</span>
              </div>
            </button>

            <div v-if="!loadingList && projects.length === 0" class="empty-tip">
              <svg
                class="empty-illu"
                viewBox="0 0 88 72"
                xmlns="http://www.w3.org/2000/svg"
                aria-hidden="true"
              >
                <path
                  fill="none"
                  stroke="currentColor"
                  stroke-width="1.6"
                  stroke-linejoin="round"
                  d="M8 14h30l7 7h35v38H8z"
                />
                <path
                  fill="none"
                  stroke="currentColor"
                  stroke-width="1.4"
                  stroke-linecap="round"
                  opacity="0.45"
                  d="M38 14v7h7"
                />
                <path
                  fill="none"
                  stroke="currentColor"
                  stroke-width="1.2"
                  stroke-linecap="round"
                  opacity="0.4"
                  d="M18 40h52M18 48h40M18 56h46"
                />
                <circle cx="66" cy="26" r="5" fill="currentColor" opacity="0.2" />
                <path
                  fill="none"
                  stroke="currentColor"
                  stroke-width="1.4"
                  stroke-linecap="round"
                  d="M64 26h4M66 24v4"
                  opacity="0.7"
                />
              </svg>
              <p class="empty-tip-txt">暂无项目，可点击右上角「创建项目」</p>
            </div>
          </div>
        </section>

        <section class="right-panel">
          <div v-if="!activeProject" class="right-empty">
            <svg
              class="empty-illu empty-illu-lg"
              viewBox="0 0 96 80"
              xmlns="http://www.w3.org/2000/svg"
              aria-hidden="true"
            >
              <rect
                x="8"
                y="16"
                width="26"
                height="48"
                rx="4"
                fill="currentColor"
                opacity="0.08"
                stroke="currentColor"
                stroke-width="1.4"
              />
              <path
                fill="none"
                stroke="currentColor"
                stroke-width="1.2"
                stroke-linecap="round"
                opacity="0.45"
                d="M14 28h14M14 36h14M14 44h10"
              />
              <rect
                x="40"
                y="16"
                width="48"
                height="48"
                rx="4"
                fill="currentColor"
                opacity="0.05"
                stroke="currentColor"
                stroke-width="1.4"
              />
              <path
                fill="none"
                stroke="currentColor"
                stroke-width="1.2"
                stroke-linecap="round"
                opacity="0.4"
                d="M48 30h32M48 40h26M48 50h30"
              />
              <path
                fill="none"
                stroke="currentColor"
                stroke-width="1.6"
                stroke-linecap="round"
                opacity="0.55"
                d="M34 44h4"
              />
            </svg>
            <p class="right-empty-txt">选择左侧任一项目，查看立项脉络与资料。</p>
          </div>

          <div
            v-else
            class="right-content"
            v-loading="loadingRight"
            element-loading-background="rgba(6, 28, 24, 0.28)"
            element-loading-custom-class="proj-right-loading"
          >
            <div class="right-header">
              <div class="right-title">
                <div class="right-title-main">{{ activeProject.projectName }}</div>
                <div class="right-title-sub">
                  <span v-if="activeProject.herbName">药材：{{ activeProject.herbName }}</span>
                  <span v-if="activeProject.formulaName">方剂：{{ activeProject.formulaName }}</span>
                  <span v-if="activeProject.indication">适应症：{{ activeProject.indication }}</span>
                </div>
              </div>
              <div class="right-header-tags">
                <span
                  class="tag-chip tag-chip--click"
                  role="button"
                  tabindex="0"
                  @click="openStatusEdit"
                  @keydown.enter.prevent="openStatusEdit"
                >{{ statusText(activeProject.status) }}</span>
                <span
                  class="tag-chip tag-chip--click soft"
                  role="button"
                  tabindex="0"
                  @click="openPhaseEdit"
                  @keydown.enter.prevent="openPhaseEdit"
                >{{ phaseText(activeProject.phase) }}</span>
                <span v-if="activeProject.budget != null" class="tag-chip ghost">
                  预算 {{ activeProject.budget }} 万
                </span>
              </div>
            </div>

            <div class="right-grid">
              <div class="cell big">
                <div class="cell-label">进展脉冲</div>
                <div class="cell-body progress-body">
                  <div class="progress-ring">
                    <el-progress
                      class="dash-progress"
                      type="dashboard"
                      :percentage="dashboardProgressPercent"
                      :width="144"
                      color="#92e6ca"
                    />
                  </div>
                  <ul class="progress-meta">
                    <li>
                      <span class="meta-label">当前阶段</span>
                      <span class="meta-value">{{ phaseText(board?.currentPhase ?? activeProject.phase) }}</span>
                    </li>
                    <li>
                      <span class="meta-label">状态</span>
                      <span class="meta-value">{{ statusText(board?.status ?? activeProject.status) }}</span>
                    </li>
                    <li>
                      <span class="meta-label">负责人</span>
                      <span class="meta-value">
                        {{ activeProject.projectorName?.trim() || '-' }}
                      </span>
                    </li>
                    <li>
                      <span class="meta-label">我在本项目</span>
                      <span class="meta-value">{{ myRoleText(activeProject.currentUserRole) }}</span>
                    </li>
                  </ul>
                </div>
              </div>

              <div class="cell">
                <div class="cell-label">AI评估结果</div>
                <div class="cell-body ai-body">
                  <div v-if="!activeProject.aiAssess?.trim()" class="ai-text ai-text--plain">
                    尚未生成 AI 评估，可在立项评估中触发。
                  </div>
                  <div v-else class="ai-text ai-md mas-scrollbar" v-html="aiAssessHtml" />
                </div>
              </div>

              <div class="cell">
                <div class="cell-label">成员</div>
                <div class="cell-body small-list">
                  <div v-if="members.length === 0" class="small-empty">暂无成员信息</div>
                  <ul v-else>
                    <li v-for="m in members" :key="m.id" class="small-row">
                      <span class="small-main">{{ memberDisplayName(m) }}</span>
                      <span class="small-sub">{{ m.role || '成员' }}</span>
                    </li>
                  </ul>
                </div>
              </div>

              <div class="cell wide">
                <div class="cell-label">资料轨迹</div>
                <div class="cell-body docs-body">
                  <div v-if="documents.length === 0" class="small-empty">暂无项目文档</div>
                  <ul v-else class="docs-list">
                    <li v-for="d in documents.slice(0, 4)" :key="d.id" class="doc-row">
                      <div class="doc-main">
                        <span class="doc-name">{{ d.docName || d.originalFilename || '未命名文档' }}</span>
                        <span class="doc-type">{{ d.docType }}</span>
                      </div>
                      <div class="doc-sub">
                        <span>{{ d.uploadTime || '-' }}</span>
                        <span v-if="d.tags">#{{ d.tags }}</span>
                      </div>
                    </li>
                  </ul>
                </div>
              </div>
            </div>

            <footer class="right-footer">
              <span>想深入分析？可切换到「立项评估」「AI 报告」等智能体继续协同。</span>
            </footer>
          </div>
        </section>
      </main>

      <el-dialog
        v-model="statusEditOpen"
        title="修改状态"
        width="420px"
        append-to-body
        class="proj-field-dialog"
      >
        <el-select
          v-model="editStatus"
          placeholder="请选择状态"
          class="proj-field-dialog-select"
          popper-class="proj-status-dropdown"
        >
          <el-option
            v-for="o in STATUS_EDIT_OPTIONS"
            :key="o.value"
            :label="o.label"
            :value="o.value"
          />
        </el-select>
        <template #footer>
          <el-button @click="statusEditOpen = false">取消</el-button>
          <el-button type="primary" :loading="savingStatus" @click="saveStatus">确定</el-button>
        </template>
      </el-dialog>

      <el-dialog
        v-model="phaseEditOpen"
        title="修改阶段"
        width="420px"
        append-to-body
        class="proj-field-dialog"
      >
        <el-select
          v-model="editPhase"
          placeholder="请选择阶段"
          class="proj-field-dialog-select"
          popper-class="proj-status-dropdown"
        >
          <el-option
            v-for="o in PHASE_EDIT_OPTIONS"
            :key="o.value"
            :label="o.label"
            :value="o.value"
          />
        </el-select>
        <template #footer>
          <el-button @click="phaseEditOpen = false">取消</el-button>
          <el-button type="primary" :loading="savingPhase" @click="savePhase">确定</el-button>
        </template>
      </el-dialog>

      <el-drawer
        v-model="createDrawerOpen"
        title="新建项目"
        direction="rtl"
        size="720px"
        append-to-body
        destroy-on-close
        class="proj-create-drawer"
        modal-class="proj-create-overlay"
      >
        <ProjectCreateForm @success="onCreateSuccess" />
      </el-drawer>
    </div>
  </div>
</template>

<style scoped>
.proj-page {
  max-width: min(1680px, 98vw);
  margin: 0 auto;
  padding: 6px 8px 22px;
  box-sizing: border-box;
}

.proj-shell {
  position: relative;
  padding: 4px 2px 8px;
}

.proj-header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  margin-bottom: 10px;
}

.title-block {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.title-main {
  font-size: 24px;
  font-weight: 950;
  color: rgba(255, 255, 255, 0.94);
  letter-spacing: 0.02em;
}

.title-sub {
  font-size: 14px;
  font-weight: 750;
  color: rgba(255, 255, 255, 0.86);
}

.header-tools {
  display: flex;
  align-items: center;
  gap: 10px;
}

.status-select {
  width: 168px;
}

:deep(.status-select .el-select__wrapper) {
  background: rgba(255, 255, 255, 0.08) !important;
  box-shadow: none !important;
  border: 1px solid rgba(255, 255, 255, 0.16) !important;
  border-radius: 10px;
  min-height: 32px;
}

:deep(.status-select .el-select__wrapper.is-focused) {
  border-color: rgba(146, 230, 202, 0.45) !important;
}

:deep(.status-select .el-select__placeholder) {
  color: rgba(255, 255, 255, 0.52) !important;
}

:deep(.status-select .el-select__selected-item),
:deep(.status-select .el-select__selection) {
  color: rgba(255, 255, 255, 0.94) !important;
}

:deep(.status-select .el-select__caret) {
  color: rgba(255, 255, 255, 0.62) !important;
}

:deep(.status-select .el-input__wrapper) {
  background: rgba(255, 255, 255, 0.08) !important;
  box-shadow: none !important;
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: 10px !important;
}

:deep(.status-select .el-input__inner) {
  color: rgba(255, 255, 255, 0.94) !important;
}

.proj-tools-bar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px 14px;
  margin: 0 0 14px;
  padding: 12px 16px;
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.18);
}

.proj-tools-label {
  flex-shrink: 0;
  font-size: 11px;
  letter-spacing: 0.22em;
  text-transform: uppercase;
  font-weight: 850;
  color: rgba(255, 255, 255, 0.48);
}

.proj-tools-pills {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  flex: 1;
  min-width: 0;
  justify-content: flex-start;
}

.tool-pill {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 8px 16px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 820;
  color: rgba(255, 255, 255, 0.85);
  text-decoration: none;
  border: 1px solid rgba(255, 255, 255, 0.14);
  background: rgba(0, 0, 0, 0.2);
  transition:
    background 0.15s ease,
    border-color 0.15s ease,
    color 0.15s ease,
    box-shadow 0.15s ease;
}

.tool-pill:hover {
  background: rgba(146, 230, 202, 0.12);
  border-color: rgba(146, 230, 202, 0.4);
  color: rgba(232, 252, 246, 0.98);
}

.tool-pill.router-link-active {
  background: rgba(146, 230, 202, 0.22);
  border-color: rgba(146, 230, 202, 0.55);
  color: rgba(248, 255, 252, 1);
  font-weight: 900;
  box-shadow: 0 0 0 1px rgba(146, 230, 202, 0.15);
}

@media (max-width: 640px) {
  .proj-tools-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .proj-tools-label {
    padding-bottom: 2px;
  }
}

.alert {
  margin-top: 6px;
  margin-bottom: 8px;
}

:deep(.alert.el-alert) {
  background: rgba(255, 80, 80, 0.12);
  border: 1px solid rgba(255, 180, 180, 0.25);
}

.proj-layout {
  display: grid;
  grid-template-columns: minmax(280px, 1.05fr) minmax(0, 2.2fr);
  gap: 16px;
  min-height: 360px;
  margin-top: 4px;
  align-items: start;
}

.left-lane {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.lane-label {
  font-size: 12px;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.85);
  font-weight: 850;
  padding-left: 2px;
}

.lane-scroll {
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.22);
  padding: 12px 10px 10px;
  max-height: 440px;
  overflow: auto;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.proj-pill {
  position: relative;
  border-radius: 14px;
  padding: 12px 12px 10px 14px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.05);
  color: rgba(255, 255, 255, 0.92);
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 6px;
  cursor: pointer;
  text-align: left;
  transition: background 0.18s ease, transform 0.18s ease, border-color 0.18s ease, box-shadow 0.18s ease;
}

.proj-pill:hover {
  transform: translateY(-1px);
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.14);
}

.proj-pill.active {
  background: rgba(146, 230, 202, 0.12);
  border-color: rgba(146, 230, 202, 0.45);
  box-shadow: 0 0 0 1px rgba(146, 230, 202, 0.2), 0 12px 28px rgba(0, 0, 0, 0.35);
}

.pill-main-line {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 950;
  width: 100%;
}

.pill-id {
  font-size: 12px;
  opacity: 0.55;
  font-weight: 850;
}

.pill-name {
  flex: 1;
  min-width: 0;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}

.pill-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  font-size: 12px;
}

.pill-tag {
  padding: 4px 9px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 900;
  color: rgba(255, 255, 255, 0.86);
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.pill-tag.phase {
  color: rgba(10, 34, 31, 0.95);
  background: rgba(200, 169, 103, 0.92);
  border-color: rgba(255, 255, 255, 0.12);
}

.pill-tag.ghost {
  color: rgba(255, 255, 255, 0.78);
  background: rgba(255, 255, 255, 0.06);
}

.pill-desc {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 12px;
  font-size: 13px;
  line-height: 1.45;
  color: rgba(255, 255, 255, 0.58);
  font-weight: 780;
}

.empty-tip {
  padding: 28px 14px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14px;
  text-align: center;
}

.empty-illu {
  width: 100px;
  height: auto;
  color: rgba(146, 230, 202, 0.55);
  flex-shrink: 0;
}

.empty-illu-lg {
  width: 132px;
  color: rgba(146, 230, 202, 0.5);
}

.empty-tip-txt {
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
  color: rgba(255, 255, 255, 0.55);
  font-weight: 780;
}

.right-panel {
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.22);
  padding: 16px 18px 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.right-empty {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 24px 16px;
  min-height: 280px;
  text-align: center;
}

.right-empty-txt {
  margin: 0;
  max-width: 320px;
  font-size: 15px;
  line-height: 1.55;
  font-weight: 780;
  color: rgba(255, 255, 255, 0.58);
}

.right-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-height: 320px;
}

.right-header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
  align-items: flex-start;
}

.right-title {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 0;
}

.right-title-main {
  font-size: 22px;
  font-weight: 950;
  color: rgba(255, 255, 255, 0.94);
  line-height: 1.25;
}

.right-title-sub {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 14px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.62);
  font-weight: 780;
}

.right-header-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: flex-start;
}

.tag-chip {
  padding: 6px 11px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 900;
  color: rgba(255, 255, 255, 0.88);
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.12);
}

.tag-chip.soft {
  color: rgba(10, 34, 31, 0.95);
  background: rgba(200, 169, 103, 0.92);
  border-color: rgba(255, 255, 255, 0.14);
}

.tag-chip.ghost {
  color: rgba(255, 255, 255, 0.78);
  background: rgba(255, 255, 255, 0.06);
}

.tag-chip--click {
  cursor: pointer;
}

.tag-chip--click:focus-visible {
  outline: 2px solid rgba(146, 230, 202, 0.55);
  outline-offset: 2px;
}

:deep(.proj-field-dialog-select) {
  width: 100%;
}

.right-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(0, 1fr);
  grid-auto-rows: minmax(128px, auto);
  gap: 12px;
}

.cell {
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.05);
  padding: 12px 13px 11px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.cell.big {
  grid-row: span 2;
}

.cell.wide {
  grid-column: span 2;
}

.cell-label {
  font-size: 14px;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.74);
  font-weight: 850;
}

.cell-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.progress-body {
  flex-direction: row;
  gap: 16px;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.progress-ring {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.dash-progress :deep(.el-progress__text) {
  color: rgba(255, 255, 255, 0.92) !important;
  font-size: 15px !important;
  font-weight: 900 !important;
}

.progress-meta {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 170px;
}

.meta-label {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.48);
  font-weight: 820;
}

.meta-value {
  display: block;
  margin-top: 2px;
  font-size: 15px;
  font-weight: 950;
  color: rgba(255, 255, 255, 0.92);
}

.ai-body {
  justify-content: space-between;
}

.ai-text {
  font-size: 14px;
  line-height: 1.65;
  color: rgba(255, 255, 255, 0.82);
  max-height: 220px;
  overflow: auto;
  font-weight: 780;
}

.ai-text--plain {
  white-space: pre-wrap;
  word-break: break-word;
}

.ai-md {
  font-weight: 650;
}

.ai-md :deep(h1),
.ai-md :deep(h2),
.ai-md :deep(h3),
.ai-md :deep(h4) {
  margin: 10px 0 6px;
  font-weight: 900;
  line-height: 1.35;
  color: rgba(255, 255, 255, 0.94);
}

.ai-md :deep(h1) {
  font-size: 17px;
}
.ai-md :deep(h2) {
  font-size: 16px;
}
.ai-md :deep(h3) {
  font-size: 15px;
}

.ai-md :deep(p) {
  margin: 0 0 8px;
}

.ai-md :deep(p:last-child) {
  margin-bottom: 0;
}

.ai-md :deep(strong) {
  font-weight: 900;
  color: rgba(255, 255, 255, 0.96);
}

.ai-md :deep(table) {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;
  margin: 8px 0;
}

.ai-md :deep(th),
.ai-md :deep(td) {
  border: 1px solid rgba(255, 255, 255, 0.16);
  padding: 6px 8px;
  text-align: left;
  vertical-align: top;
}

.ai-md :deep(th) {
  background: rgba(0, 0, 0, 0.28);
  font-weight: 850;
}

.ai-md :deep(ul),
.ai-md :deep(ol) {
  margin: 6px 0;
  padding-left: 1.25em;
}

.ai-md :deep(li) {
  margin: 2px 0;
}

.ai-md :deep(hr) {
  border: 0;
  border-top: 1px solid rgba(255, 255, 255, 0.14);
  margin: 10px 0;
}

.ai-md :deep(code) {
  font-size: 12px;
  background: rgba(0, 0, 0, 0.35);
  padding: 1px 5px;
  border-radius: 4px;
}

.ai-md :deep(pre) {
  margin: 8px 0;
  padding: 10px;
  overflow: auto;
  background: rgba(0, 0, 0, 0.32);
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.ai-md :deep(pre code) {
  background: transparent;
  padding: 0;
  font-size: 12px;
}

.ai-md :deep(blockquote) {
  margin: 8px 0;
  padding: 4px 0 4px 12px;
  border-left: 3px solid rgba(115, 209, 180, 0.45);
  color: rgba(255, 255, 255, 0.78);
}

.small-list {
  gap: 4px;
}

.small-empty {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.55);
  font-weight: 780;
}

.small-row {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  gap: 10px;
  font-size: 14px;
  padding: 6px 0;
  border-bottom: 1px dashed rgba(255, 255, 255, 0.14);
  font-weight: 820;
}

.small-row:last-child {
  border-bottom: none;
}

.small-main {
  color: rgba(255, 255, 255, 0.92);
  font-weight: 920;
}

.small-sub {
  color: rgba(255, 255, 255, 0.55);
  font-size: 13px;
  font-weight: 800;
}

.docs-body {
  max-height: 168px;
  overflow: auto;
}

.docs-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.doc-row {
  padding: 6px 0;
  border-bottom: 1px dashed rgba(255, 255, 255, 0.14);
}

.doc-row:last-child {
  border-bottom: none;
}

.doc-main {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.92);
  font-weight: 900;
}

.doc-name {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.doc-type {
  flex-shrink: 0;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.48);
  font-weight: 820;
}

.doc-sub {
  margin-top: 4px;
  display: flex;
  justify-content: space-between;
  gap: 10px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.52);
  font-weight: 780;
}

.right-footer {
  margin-top: 4px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.48);
  font-weight: 780;
}

@media (max-width: 1024px) {
  .proj-shell {
    padding: 12px 10px 14px;
  }

  .proj-layout {
    grid-template-columns: 1fr;
  }

  .right-grid {
    grid-template-columns: 1fr;
  }

  .cell.big,
  .cell.wide {
    grid-column: span 1;
    grid-row: span 1;
  }
}

@media (max-width: 640px) {
  .proj-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .header-tools {
    align-self: stretch;
    justify-content: flex-start;
    flex-wrap: wrap;
  }

  .lane-scroll {
    max-height: 260px;
  }
}
</style>

<style>
.proj-status-dropdown.el-popper {
  background: rgba(12, 42, 36, 0.96) !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  backdrop-filter: blur(12px);
}

.proj-status-dropdown .el-select-dropdown__item {
  color: rgba(255, 255, 255, 0.88);
  font-size: 14px;
}

.proj-status-dropdown .el-select-dropdown__item.is-hovering,
.proj-status-dropdown .el-select-dropdown__item:hover {
  background: rgba(255, 255, 255, 0.08) !important;
}

.proj-status-dropdown .el-select-dropdown__item.is-selected {
  color: #92e6ca;
  font-weight: 700;
}

.proj-lane-loading,
.proj-right-loading {
  border-radius: 16px !important;
}

.proj-create-overlay {
  background-color: rgba(4, 26, 22, 0.38) !important;
  backdrop-filter: blur(2px);
}

.proj-create-drawer.el-drawer {
  --el-drawer-bg-color: rgba(12, 52, 46, 0.9);
  box-shadow: -12px 0 40px rgba(0, 0, 0, 0.18);
}

.proj-create-drawer .el-drawer__header {
  margin-bottom: 0;
  padding: 14px 18px 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.proj-create-drawer .el-drawer__title {
  color: rgba(255, 255, 255, 0.95);
  font-weight: 950;
  font-size: 17px;
}

.proj-create-drawer .el-drawer__body {
  padding: 8px 4px 20px;
  background: transparent;
  max-height: calc(100vh - 56px);
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: rgba(180, 230, 210, 0.35) transparent;
}

.proj-create-drawer .el-drawer__body::-webkit-scrollbar {
  width: 5px;
}

.proj-create-drawer .el-drawer__body::-webkit-scrollbar-track {
  background: transparent;
}

.proj-create-drawer .el-drawer__body::-webkit-scrollbar-thumb {
  background: rgba(180, 230, 210, 0.32);
  border-radius: 5px;
}

.proj-create-drawer .el-drawer__body::-webkit-scrollbar-thumb:hover {
  background: rgba(200, 240, 220, 0.48);
}

.proj-create-drawer .el-drawer__close-btn {
  color: rgba(255, 255, 255, 0.6);
}
</style>
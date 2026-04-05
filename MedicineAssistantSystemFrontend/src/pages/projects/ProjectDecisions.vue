<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { getProjectDetail, listMyProjects, type MyProjectItem, type Project } from '@/api/projects'
import { aiApi } from '@/api/ai'
import { projectsExtraApi, type DecisionCompare, type ProjectDecision } from '@/api/projectsExtra'
import { aiBoldToHtml } from '@/utils/aiBoldHtml'
import { useAuthStore } from '@/stores/auth'

const DECISION_TYPE_LABEL: Record<string, string> = {
  FORMULATION: '配方决策',
  EXPERIMENT: '实验决策',
  RESOURCE: '资源决策',
  RISK: '风险控制'
}

function decisionTypeLabel(t?: string | null) {
  if (!t) return '-'
  return DECISION_TYPE_LABEL[t] ?? t
}

function decText(s?: string | null) {
  const t = s?.trim()
  return t ? t : '—'
}

function aiRichOrDash(s?: string | null) {
  const t = s?.trim()
  return t ? aiBoldToHtml(s) : '—'
}

const auth = useAuthStore()

const projectId = ref<number | null>(null)
const mineProjects = ref<MyProjectItem[]>([])
const projectsLoading = ref(false)

const loading = ref(false)
const error = ref<string | null>(null)
const rows = ref<ProjectDecision[]>([])

const selectedCompareIds = ref<number[]>([])

const createOpen = ref(false)
const saving = ref(false)
const aiSuggestLoading = ref(false)
const aiSuggestProgress = ref(0)
let aiSuggestRaf = 0
const versionText = ref('1.0')
const createForm = ref<Omit<ProjectDecision, 'projectorId' | 'version'>>({
  decisionType: 'FORMULATION',
  title: '',
  content: '',
  aiRecommendation: '',
  expertConclusion: ''
})

const compareOpen = ref(false)
const compareLoading = ref(false)
const compareError = ref<string | null>(null)
const compareResult = ref<DecisionCompare | null>(null)

const detailOpen = ref(false)
const detailRow = ref<ProjectDecision | null>(null)

const compareSlots = computed(() => {
  const r = compareResult.value
  if (!r) return [] as { key: string; side: 'a' | 'b'; badge: string; title: string; d: ProjectDecision }[]
  return [
    { key: 'a', side: 'a', badge: 'A', title: '决策 A', d: r.first },
    { key: 'b', side: 'b', badge: 'B', title: '决策 B', d: r.second }
  ]
})

const detailSlot = computed(() => {
  const d = detailRow.value
  if (!d) return [] as { d: ProjectDecision }[]
  return [{ d }]
})

const projectsForSelect = computed(() =>
  mineProjects.value.filter((p): p is MyProjectItem & { id: number } => p.id != null)
)

const canCompare = computed(
  () =>
    projectId.value != null &&
    selectedCompareIds.value.length === 2 &&
    selectedCompareIds.value[0] !== selectedCompareIds.value[1]
)

function compareRowClass({ row }: { row: ProjectDecision }) {
  const id = row.id
  if (id != null && selectedCompareIds.value.includes(id)) return 'is-compare-pick'
  return ''
}

function toggleComparePick(row: ProjectDecision) {
  const id = row.id
  if (id == null) return
  const i = selectedCompareIds.value.indexOf(id)
  if (i >= 0) {
    selectedCompareIds.value = selectedCompareIds.value.filter((x) => x !== id)
  } else if (selectedCompareIds.value.length < 2) {
    selectedCompareIds.value = [...selectedCompareIds.value, id]
  } else {
    const second = selectedCompareIds.value[1]
    if (second != null) {
      selectedCompareIds.value = [second, id]
    }
  }
}

async function loadProjectOptions() {
  const uid = auth.user?.userId
  if (!uid) {
    mineProjects.value = []
    projectId.value = null
    return
  }
  projectsLoading.value = true
  try {
    mineProjects.value = await listMyProjects(uid)
    if (projectId.value == null && mineProjects.value.length > 0) {
      const first = mineProjects.value[0]?.id
      projectId.value = first != null ? first : null
    }
  } catch {
    mineProjects.value = []
    projectId.value = null
  } finally {
    projectsLoading.value = false
  }
}

async function load() {
  if (projectId.value == null) {
    rows.value = []
    selectedCompareIds.value = []
    return
  }
  loading.value = true
  error.value = null
  try {
    rows.value = await projectsExtraApi.decisions(projectId.value)
    selectedCompareIds.value = selectedCompareIds.value.filter((id) =>
      rows.value.some((r) => r.id === id)
    )
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    loading.value = false
  }
}

function openCreate() {
  versionText.value = '1.0'
  createForm.value = {
    decisionType: 'FORMULATION',
    title: '',
    content: '',
    aiRecommendation: '',
    expertConclusion: ''
  }
  createOpen.value = true
}

async function createOne() {
  if (projectId.value == null) return
  const uid = auth.user?.userId
  if (uid == null) {
    error.value = '未登录'
    return
  }
  const version = String(versionText.value).trim() || '1.0'
  saving.value = true
  error.value = null
  try {
    await projectsExtraApi.createDecision(projectId.value, {
      ...createForm.value,
      projectorId: uid,
      version
    })
    createOpen.value = false
    await load()
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    saving.value = false
  }
}

function buildDecisionAiInput() {
  const f = createForm.value
  return [
    `决策类型：${f.decisionType ?? ''}`,
    `标题：${f.title ?? ''}`,
    `内容：`,
    f.content ?? ''
  ].join('\n')
}

function formatProjectDetailForAi(p: Project) {
  const lines = [
    '【当前项目】',
    `项目ID：${p.id ?? ''}`,
    `项目名称：${p.projectName ?? ''}`,
    `药材：${p.herbName ?? ''}`,
    `方剂：${p.formulaName ?? ''}`,
    `适应症：${p.indication ?? ''}`,
    `阶段：${p.phase ?? ''}`,
    `状态：${p.status != null ? String(p.status) : ''}`,
    `优先级：${p.priority != null ? String(p.priority) : ''}`,
    `预算：${p.budget != null ? String(p.budget) : ''}`,
    '项目描述：',
    p.description ?? '',
    'AI立项评估：',
    p.aiAssess ?? ''
  ]
  const report = p.aiReport?.trim()
  if (report) {
    lines.push('', '立项报告：', report)
  }
  return lines.join('\n')
}

async function fetchAiSuggestion() {
  const t = (createForm.value.title ?? '').trim()
  const c = (createForm.value.content ?? '').trim()
  if (!t && !c) {
    error.value = '请至少填写标题或内容后再获取 AI 建议'
    return
  }
  if (aiSuggestLoading.value) return
  aiSuggestLoading.value = true
  aiSuggestProgress.value = 0
  error.value = null
  const start = Date.now()
  const duration = 12000
  let finished = false
  const tick = () => {
    if (finished) return
    const elapsed = Date.now() - start
    if (elapsed < duration) {
      aiSuggestProgress.value = Math.min(99, (elapsed / duration) * 99)
      aiSuggestRaf = requestAnimationFrame(tick)
    } else {
      aiSuggestProgress.value = 99
    }
  }
  aiSuggestRaf = requestAnimationFrame(tick)
  try {
    if (projectId.value == null) {
      error.value = '请先选择项目'
      return
    }
    const project = await getProjectDetail(projectId.value)
    const input = [formatProjectDetailForAi(project), '', '【本决策】', buildDecisionAiInput()].join('\n')
    const { output } = await aiApi.decisionSupport(input, null)
    createForm.value.aiRecommendation = output
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    finished = true
    cancelAnimationFrame(aiSuggestRaf)
    aiSuggestProgress.value = 100
    aiSuggestLoading.value = false
    setTimeout(() => {
      aiSuggestProgress.value = 0
    }, 450)
  }
}

async function openCompare() {
  if (!canCompare.value || projectId.value == null) return
  compareOpen.value = true
  compareLoading.value = true
  compareError.value = null
  compareResult.value = null
  try {
    const id1 = selectedCompareIds.value[0]
    const id2 = selectedCompareIds.value[1]
    if (id1 == null || id2 == null) return
    compareResult.value = await projectsExtraApi.compareDecisions(projectId.value, id1, id2)
  } catch (e: any) {
    compareError.value = String(e?.message || e)
  } finally {
    compareLoading.value = false
  }
}

function openDetail(row: ProjectDecision) {
  detailRow.value = row
  detailOpen.value = true
}

watch(projectId, () => {
  load()
})

onMounted(async () => {
  await loadProjectOptions()
})
</script>

<template>
  <div class="pt-tool">
    <div class="head">
      <div>
        <div class="t">决策记录</div>
        <div class="s">点选两条决策后对比 · 查看详情 · AI 建议</div>
      </div>
      <div class="act">
        <el-select
          v-model="projectId"
          placeholder="选择项目"
          filterable
          clearable
          style="width: 240px"
          :loading="projectsLoading"
        >
          <el-option
            v-for="p in projectsForSelect"
            :key="p.id"
            :label="p.projectName"
            :value="p.id"
          />
        </el-select>
        <el-button :loading="loading" @click="load">加载</el-button>
        <el-button type="primary" :disabled="projectId == null" @click="openCreate">新增</el-button>
      </div>
    </div>

    <el-alert v-if="error" :title="error" type="error" show-icon />

    <div class="pt-panel">
      <div class="compare">
        <span class="compare-hint">
          已选 {{ selectedCompareIds.length }}/2 条（点击表格行进行选择）
        </span>
        <el-button type="primary" :disabled="!canCompare" @click="openCompare">对比</el-button>
      </div>

      <el-table
        v-loading="loading"
        class="pt-table decision-table"
        :data="rows"
        stripe
        :row-class-name="compareRowClass"
        @row-click="toggleComparePick"
      >
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="decisionType" label="类型" width="140" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="version" label="版本" width="90" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="110" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click.stop="openDetail(row)">查看详细</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="createOpen" class="pt-tool-dialog" title="新增决策记录" width="860px">
      <el-form :model="createForm" label-width="110px">
        <el-form-item label="类型">
          <el-select v-model="createForm.decisionType" style="width: 100%">
            <el-option label="配方决策" value="FORMULATION" />
            <el-option label="实验决策" value="EXPERIMENT" />
            <el-option label="资源决策" value="RESOURCE" />
            <el-option label="风险控制" value="RISK" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="createForm.title" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="createForm.content" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="AI建议">
          <div class="ai-suggest-block">
            <div class="ai-suggest-actions">
              <el-button
                type="primary"
                plain
                size="small"
                :loading="aiSuggestLoading"
                :disabled="aiSuggestLoading"
                @click="fetchAiSuggestion"
              >
                获取 AI 建议
              </el-button>
            </div>
            <el-progress
              v-if="aiSuggestLoading || aiSuggestProgress > 0"
              class="ai-suggest-progress"
              :percentage="Math.round(aiSuggestProgress)"
              :stroke-width="8"
              striped
              striped-flow
            />
            <div class="ai-suggest-rich" v-html="aiBoldToHtml(createForm.aiRecommendation)" />
            <el-input v-model="createForm.aiRecommendation" type="textarea" :rows="4" />
          </div>
        </el-form-item>
        <el-form-item label="专家结论">
          <el-input v-model="createForm.expertConclusion" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="版本号">
          <el-input v-model="versionText" placeholder="如 1.0、2.1" clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createOpen = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="createOne">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="compareOpen"
      class="pt-tool-dialog dec-dialog"
      title="决策对比"
      width="960px"
      top="6vh"
      destroy-on-close
    >
      <el-alert v-if="compareError" :title="compareError" type="error" show-icon class="mb" />
      <div v-loading="compareLoading" class="compare-dialog-body">
        <el-row v-if="compareResult" :gutter="18">
          <el-col v-for="s in compareSlots" :key="s.key" :xs="24" :sm="12">
            <div :class="['dec-card', `dec-card--${s.side}`]">
              <div class="dec-card-head">
                <span :class="['dec-badge', `dec-badge--${s.side}`]">{{ s.badge }}</span>
                <span class="dec-card-title">{{ s.title }}</span>
              </div>
              <div class="dec-card-scroll">
                <div class="dec-detail-inner">
                  <div class="dec-meta-grid">
                    <div class="dec-meta-item">
                      <span class="dec-k">ID</span>
                      <span class="dec-v">{{ s.d.id != null ? s.d.id : '—' }}</span>
                    </div>
                    <div class="dec-meta-item">
                      <span class="dec-k">类型</span>
                      <el-tag size="small" effect="plain" class="dec-tag">{{ decisionTypeLabel(s.d.decisionType) }}</el-tag>
                    </div>
                    <div class="dec-meta-item dec-meta-span2">
                      <span class="dec-k">标题</span>
                      <span class="dec-v dec-v-strong">{{ decText(s.d.title) }}</span>
                    </div>
                    <div class="dec-meta-item">
                      <span class="dec-k">版本</span>
                      <span class="dec-v">{{ decText(s.d.version) }}</span>
                    </div>
                    <div class="dec-meta-item">
                      <span class="dec-k">创建</span>
                      <span class="dec-v dec-v-mono">{{ decText(s.d.createTime) }}</span>
                    </div>
                    <div class="dec-meta-item dec-meta-span2">
                      <span class="dec-k">更新</span>
                      <span class="dec-v dec-v-mono">{{ decText(s.d.updateTime) }}</span>
                    </div>
                  </div>
                  <section class="dec-sec">
                    <div class="dec-sec-h">内容</div>
                    <div class="dec-sec-body">{{ decText(s.d.content) }}</div>
                  </section>
                  <section class="dec-sec">
                    <div class="dec-sec-h">AI 建议</div>
                    <div class="dec-sec-body dec-sec-body--rich" v-html="aiRichOrDash(s.d.aiRecommendation)" />
                  </section>
                  <section class="dec-sec">
                    <div class="dec-sec-h">专家结论</div>
                    <div class="dec-sec-body">{{ decText(s.d.expertConclusion) }}</div>
                  </section>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-dialog>

    <el-dialog
      v-model="detailOpen"
      class="pt-tool-dialog dec-dialog"
      title="决策详情"
      width="640px"
      top="8vh"
      destroy-on-close
    >
      <div v-for="slot in detailSlot" :key="'detail'" class="dec-detail-single">
        <div class="dec-detail-inner">
          <div class="dec-meta-grid">
            <div class="dec-meta-item">
              <span class="dec-k">ID</span>
              <span class="dec-v">{{ slot.d.id != null ? slot.d.id : '—' }}</span>
            </div>
            <div class="dec-meta-item">
              <span class="dec-k">类型</span>
              <el-tag size="small" effect="plain" class="dec-tag">{{ decisionTypeLabel(slot.d.decisionType) }}</el-tag>
            </div>
            <div class="dec-meta-item dec-meta-span2">
              <span class="dec-k">标题</span>
              <span class="dec-v dec-v-strong">{{ decText(slot.d.title) }}</span>
            </div>
            <div class="dec-meta-item">
              <span class="dec-k">版本</span>
              <span class="dec-v">{{ decText(slot.d.version) }}</span>
            </div>
            <div class="dec-meta-item">
              <span class="dec-k">创建</span>
              <span class="dec-v dec-v-mono">{{ decText(slot.d.createTime) }}</span>
            </div>
            <div class="dec-meta-item dec-meta-span2">
              <span class="dec-k">更新</span>
              <span class="dec-v dec-v-mono">{{ decText(slot.d.updateTime) }}</span>
            </div>
          </div>
          <section class="dec-sec">
            <div class="dec-sec-h">内容</div>
            <div class="dec-sec-body">{{ decText(slot.d.content) }}</div>
          </section>
          <section class="dec-sec">
            <div class="dec-sec-h">AI 建议</div>
            <div class="dec-sec-body dec-sec-body--rich" v-html="aiRichOrDash(slot.d.aiRecommendation)" />
          </section>
          <section class="dec-sec">
            <div class="dec-sec-h">专家结论</div>
            <div class="dec-sec-body">{{ decText(slot.d.expertConclusion) }}</div>
          </section>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
.compare {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.compare-hint {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.72);
}

.mb {
  margin-bottom: 12px;
}

.ai-suggest-block {
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
}

.ai-suggest-progress {
  width: 100%;
}

.compare-dialog-body {
  min-height: 120px;
}

.dec-card {
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: linear-gradient(165deg, rgba(255, 255, 255, 0.07) 0%, rgba(255, 255, 255, 0.03) 100%);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.dec-card--a {
  box-shadow: inset 0 0 0 1px rgba(146, 230, 202, 0.12);
}

.dec-card--b {
  box-shadow: inset 0 0 0 1px rgba(147, 197, 253, 0.14);
}

.dec-card-head {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 14px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(0, 0, 0, 0.15);
}

.dec-badge {
  width: 30px;
  height: 30px;
  border-radius: 10px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 800;
  letter-spacing: 0.02em;
  flex-shrink: 0;
}

.dec-badge--a {
  color: rgba(230, 255, 245, 0.98);
  background: linear-gradient(145deg, rgba(146, 230, 202, 0.45), rgba(56, 189, 152, 0.35));
  box-shadow: 0 2px 10px rgba(56, 189, 152, 0.2);
}

.dec-badge--b {
  color: rgba(239, 246, 255, 0.98);
  background: linear-gradient(145deg, rgba(147, 197, 253, 0.45), rgba(96, 165, 250, 0.35));
  box-shadow: 0 2px 10px rgba(96, 165, 250, 0.2);
}

.dec-card-title {
  font-size: 15px;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.94);
}

.dec-card-scroll {
  max-height: min(58vh, 520px);
  overflow: auto;
  padding: 12px 14px 14px;
}

.dec-detail-single {
  max-height: min(72vh, 640px);
  overflow: auto;
  padding: 2px 2px 6px;
}

.dec-detail-inner {
  min-width: 0;
}

.dec-meta-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px 14px;
  margin-bottom: 4px;
}

.dec-meta-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.dec-meta-span2 {
  grid-column: 1 / -1;
}

.dec-k {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.45);
}

.dec-v {
  font-size: 13px;
  line-height: 1.45;
  color: rgba(255, 255, 255, 0.9);
  word-break: break-word;
}

.dec-v-strong {
  font-weight: 650;
  font-size: 14px;
  line-height: 1.4;
}

.dec-v-mono {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.82);
}

.dec-tag {
  align-self: flex-start;
  border-color: rgba(255, 255, 255, 0.22) !important;
  color: rgba(255, 255, 255, 0.88) !important;
  background: rgba(255, 255, 255, 0.06) !important;
}

.dec-sec {
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.dec-sec:first-of-type {
  margin-top: 12px;
  padding-top: 0;
  border-top: 0;
}

.dec-sec-h {
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.04em;
  color: rgba(146, 230, 202, 0.88);
  margin-bottom: 8px;
}

.dec-sec-body {
  margin: 0;
  font-size: 13px;
  line-height: 1.65;
  white-space: pre-wrap;
  word-break: break-word;
  color: rgba(255, 255, 255, 0.86);
  padding: 10px 12px;
  border-radius: 10px;
  background: rgba(0, 0, 0, 0.22);
  border: 1px solid rgba(255, 255, 255, 0.06);
}

.dec-sec-body--rich :deep(strong) {
  font-weight: 750;
  color: rgba(255, 255, 255, 0.96);
}

.ai-suggest-rich {
  margin-bottom: 10px;
  min-height: 40px;
  font-size: 13px;
  line-height: 1.65;
  white-space: pre-wrap;
  word-break: break-word;
  color: rgba(255, 255, 255, 0.86);
  padding: 10px 12px;
  border-radius: 10px;
  background: rgba(0, 0, 0, 0.22);
  border: 1px solid rgba(255, 255, 255, 0.06);
}

.ai-suggest-rich :deep(strong) {
  font-weight: 750;
  color: rgba(255, 255, 255, 0.96);
}
</style>

<style>
.decision-table .el-table__body .el-table__row {
  cursor: pointer;
}
.decision-table .el-table__body tr.is-compare-pick > td.el-table__cell {
  background: rgba(146, 230, 202, 0.26) !important;
  box-shadow: inset 4px 0 0 0 rgba(146, 230, 202, 0.95);
  color: rgba(255, 255, 255, 0.96);
}
.decision-table .el-table__body tr.el-table__row--striped.is-compare-pick > td.el-table__cell {
  background: rgba(146, 230, 202, 0.26) !important;
}
.decision-table .el-table__body tr.is-compare-pick:hover > td.el-table__cell {
  background: rgba(146, 230, 202, 0.34) !important;
}
</style>

<style src="./projectToolPage.css"></style>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { createProject, type Project } from '@/api/projects'
import { aiBoldToHtml } from '@/utils/aiBoldHtml'
import { aiApi } from '@/api/ai'
import { listUsers, type UserListItem } from '@/api/user'
import { projectsExtraApi } from '@/api/projectsExtra'

const emit = defineEmits<{ success: [] }>()

const loading = ref(false)
const error = ref<string | null>(null)
const users = ref<UserListItem[]>([])
const usersLoading = ref(false)
const memberUserIds = ref<number[]>([])

const aiEvalProgress = ref(0)
const aiEvalRunning = ref(false)
let aiEvalRaf = 0

const form = reactive<Project>({
  projectName: '',
  herbName: null,
  formulaName: null,
  indication: null,
  phase: null,
  projectorId: null,
  status: null,
  description: null,
  budget: null,
  priority: null,
  aiAssess: '',
  aiReport: null
})

function userLabel(u: UserListItem) {
  const n = u.nickname?.trim()
  if (n) return n
  if (u.username?.trim()) return u.username
  return `用户 ${u.id}`
}

function buildEvaluationInput() {
  const sb: string[] = []
  sb.push(`项目名称:${form.projectName || ''}`)
  sb.push(`药材:${form.herbName ?? ''}`)
  sb.push(`方剂:${form.formulaName ?? ''}`)
  sb.push(`适应症:${form.indication ?? ''}`)
  sb.push(`描述:${form.description ?? ''}`)
  sb.push(`预算:${form.budget != null ? String(form.budget) : ''}`)
  sb.push(`优先级:${form.priority != null ? String(form.priority) : ''}`)
  return sb.join('\n')
}

async function runAiEvaluate() {
  if (aiEvalRunning.value) return
  error.value = null
  aiEvalRunning.value = true
  aiEvalProgress.value = 0
  const start = Date.now()
  const duration = 12000
  let finished = false

  const tick = () => {
    if (finished) return
    const elapsed = Date.now() - start
    if (elapsed < duration) {
      aiEvalProgress.value = Math.min(99, (elapsed / duration) * 99)
      aiEvalRaf = requestAnimationFrame(tick)
    } else {
      aiEvalProgress.value = 99
    }
  }
  aiEvalRaf = requestAnimationFrame(tick)

  try {
    const { output } = await aiApi.projectEvaluation(buildEvaluationInput(), null)
    form.aiAssess = output
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    finished = true
    cancelAnimationFrame(aiEvalRaf)
    aiEvalProgress.value = 100
    aiEvalRunning.value = false
    setTimeout(() => {
      aiEvalProgress.value = 0
    }, 500)
  }
}

function reset() {
  error.value = null
  form.projectName = ''
  form.herbName = null
  form.formulaName = null
  form.indication = null
  form.phase = null
  form.projectorId = null
  form.status = null
  form.description = null
  form.budget = null
  form.priority = null
  form.aiAssess = ''
  form.aiReport = null
  memberUserIds.value = []
}

async function submit() {
  loading.value = true
  error.value = null
  try {
    if (!form.projectName || !form.projectName.trim()) {
      throw new Error('项目名称必填')
    }
    if (!form.aiAssess || !form.aiAssess.trim()) {
      throw new Error('AI 立项评估必填')
    }
    const created = await createProject(form)
    const pid = created.id
    if (pid != null && memberUserIds.value.length > 0) {
      for (const uid of memberUserIds.value) {
        if (form.projectorId != null && uid === form.projectorId) continue
        try {
          await projectsExtraApi.createMember(pid, { userId: uid, role: 'MEMBER' })
        } catch (e: any) {
          error.value = `项目已创建（ID ${pid}），成员添加失败：${e?.message || e}`
          return
        }
      }
    }
    reset()
    emit('success')
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  usersLoading.value = true
  try {
    users.value = await listUsers()
  } catch {
    users.value = []
  } finally {
    usersLoading.value = false
  }
})

defineExpose({ reset })
</script>

<template>
  <div class="pcf">
    <el-alert v-if="error" type="error" show-icon :title="error" class="pcf-alert" />

    <el-form class="pcf-form" :model="form" label-position="top" @submit.prevent>
      <div class="pcf-grid">
        <el-form-item label="项目名称" class="pcf-span2" required>
          <el-input v-model="form.projectName" placeholder="必填" />
        </el-form-item>
        <el-form-item label="药材名称">
          <el-input v-model="form.herbName" placeholder="选填" />
        </el-form-item>
        <el-form-item label="方剂名称">
          <el-input v-model="form.formulaName" placeholder="选填" />
        </el-form-item>
        <el-form-item label="适应症" class="pcf-span2">
          <el-input v-model="form.indication" placeholder="选填" />
        </el-form-item>
        <el-form-item label="阶段">
          <el-select
            v-model="form.phase"
            placeholder="请选择"
            clearable
            class="pcf-full"
            popper-class="proj-status-dropdown"
          >
            <el-option label="探索期" value="EXPLORATION" />
            <el-option label="验证期" value="VERIFICATION" />
            <el-option label="优化期" value="OPTIMIZATION" />
            <el-option label="申报期" value="DECLARATION" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="form.status"
            placeholder="请选择"
            clearable
            class="pcf-full"
            popper-class="proj-status-dropdown"
          >
            <el-option label="立项中" :value="1" />
            <el-option label="进行中" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="负责人">
          <el-select
            v-model="form.projectorId"
            placeholder="请选择负责人"
            clearable
            filterable
            class="pcf-full"
            popper-class="proj-status-dropdown"
            :loading="usersLoading"
          >
            <el-option
              v-for="u in users"
              :key="u.id"
              :label="userLabel(u)"
              :value="u.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="项目成员">
          <el-select
            v-model="memberUserIds"
            multiple
            collapse-tags
            collapse-tags-tooltip
            placeholder="可选多名成员"
            filterable
            class="pcf-full"
            popper-class="proj-status-dropdown"
            :loading="usersLoading"
          >
            <el-option
              v-for="u in users"
              :key="u.id"
              :label="userLabel(u)"
              :value="u.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="预算（万）">
          <el-input-number v-model="form.budget" :min="0" :step="1" class="pcf-num" controls-position="right" />
        </el-form-item>
        <el-form-item label="优先级" class="pcf-span2">
          <el-input-number v-model="form.priority" :min="0" class="pcf-num" controls-position="right" />
        </el-form-item>
        <el-form-item label="描述" class="pcf-span2">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="选填" />
        </el-form-item>
        <el-form-item label="AI 立项评估" class="pcf-span2" required>
          <div class="pcf-ai-block">
            <div class="pcf-ai-actions">
              <el-button
                type="primary"
                plain
                :loading="aiEvalRunning"
                :disabled="aiEvalRunning"
                @click="runAiEvaluate"
              >
                触发 AI 评估
              </el-button>
            </div>
            <el-progress
              v-if="aiEvalRunning || aiEvalProgress > 0"
              class="pcf-ai-progress"
              :percentage="Math.round(aiEvalProgress)"
              :stroke-width="10"
              striped
              striped-flow
            />
            <div class="pcf-ai-rich" v-html="aiBoldToHtml(form.aiAssess)" />
            <el-input v-model="form.aiAssess" type="textarea" :rows="5" placeholder="必填，可点击上方生成" />
          </div>
        </el-form-item>
      </div>
      <div class="pcf-actions">
        <el-button type="primary" :loading="loading" @click="submit">提交</el-button>
      </div>
    </el-form>
  </div>
</template>

<style scoped>
.pcf {
  width: 100%;
}

.pcf-alert {
  margin-bottom: 14px;
}

:deep(.pcf-alert.el-alert) {
  background: rgba(255, 80, 80, 0.12);
  border: 1px solid rgba(255, 180, 180, 0.25);
}

.pcf-form :deep(.el-form-item__label) {
  color: rgba(255, 255, 255, 0.72);
  font-weight: 780;
  font-size: 15px;
}

.pcf-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0 16px;
  align-items: start;
}

.pcf-span2 {
  grid-column: span 2;
}

.pcf-full {
  width: 100%;
}

.pcf-num {
  width: 100%;
}

.pcf-num :deep(.el-input__wrapper) {
  width: 100%;
}

.pcf-actions {
  margin-top: 8px;
  padding-top: 4px;
}

.pcf-ai-block {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
}

.pcf-ai-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.pcf-ai-progress {
  width: 100%;
}

.pcf-ai-progress :deep(.el-progress-bar__outer) {
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.08);
}

.pcf-ai-progress :deep(.el-progress-bar__inner) {
  border-radius: 8px;
}

.pcf-ai-rich {
  margin-bottom: 10px;
  min-height: 40px;
  padding: 10px 12px;
  border-radius: 10px;
  background: rgba(0, 0, 0, 0.22);
  border: 1px solid rgba(255, 255, 255, 0.08);
  font-size: 15px;
  line-height: 1.65;
  white-space: pre-wrap;
  word-break: break-word;
  color: rgba(255, 255, 255, 0.88);
}

.pcf-ai-rich :deep(strong) {
  font-weight: 750;
  color: rgba(255, 255, 255, 0.96);
}

:deep(.el-input__wrapper),
:deep(.el-textarea__inner) {
  background: rgba(255, 255, 255, 0.07) !important;
  box-shadow: none !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  border-radius: 10px !important;
}

:deep(.el-input__wrapper.is-focus),
:deep(.el-textarea__inner:focus) {
  border-color: rgba(146, 230, 202, 0.45) !important;
}

:deep(.el-input__inner),
:deep(.el-textarea__inner) {
  color: rgba(255, 255, 255, 0.92) !important;
}

:deep(.el-input__inner::placeholder),
:deep(.el-textarea__inner::placeholder) {
  color: rgba(255, 255, 255, 0.35);
}

:deep(.el-select .el-select__wrapper) {
  background: rgba(255, 255, 255, 0.07) !important;
  box-shadow: none !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  border-radius: 10px;
  min-height: 32px;
}

:deep(.el-select .el-select__placeholder) {
  color: rgba(255, 255, 255, 0.45) !important;
}

:deep(.el-select .el-select__selected-item),
:deep(.el-select .el-select__selection) {
  color: rgba(255, 255, 255, 0.9) !important;
}

:deep(.el-input-number .el-input__wrapper) {
  border-radius: 10px !important;
}

@media (max-width: 640px) {
  .pcf-grid {
    grid-template-columns: 1fr;
  }

  .pcf-span2 {
    grid-column: span 1;
  }
}
</style>

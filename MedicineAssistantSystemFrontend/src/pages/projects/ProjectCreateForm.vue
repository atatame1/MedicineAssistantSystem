<script setup lang="ts">
import { reactive, ref } from 'vue'
import { createProject, type Project } from '@/api/projects'

const emit = defineEmits<{ success: [] }>()

const loading = ref(false)
const error = ref<string | null>(null)

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
    await createProject(form)
    reset()
    emit('success')
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    loading.value = false
  }
}

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
        <el-form-item label="负责人用户 ID">
          <el-input-number v-model="form.projectorId" :min="0" class="pcf-num" controls-position="right" />
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
          <el-input v-model="form.aiAssess" type="textarea" :rows="5" placeholder="必填" />
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
  font-size: 13px;
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

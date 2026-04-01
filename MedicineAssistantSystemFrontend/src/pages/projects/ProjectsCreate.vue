<script setup lang="ts">
import { reactive, ref } from 'vue'
import { createProject, type Project } from '@/api/projects'
import { useRouter } from 'vue-router'

const router = useRouter()
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

async function submit() {
  loading.value = true
  error.value = null
  try {
    if (!form.projectName || !form.projectName.trim()) {
      throw new Error('项目名称必填')
    }
    if (!form.aiAssess || !form.aiAssess.trim()) {
      throw new Error('aiAssess必填')
    }
    await createProject(form)
    router.push('/projects')
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="page-wrap">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>新建项目</span>
          <el-button text @click="$router.push('/projects')">返回列表</el-button>
        </div>
      </template>

      <el-alert v-if="error" type="error" show-icon :title="error" class="mb-12" />

      <el-form :model="form" label-width="120px" @submit.prevent>
        <el-form-item label="项目名称">
          <el-input v-model="form.projectName" placeholder="必填" />
        </el-form-item>
        <el-form-item label="药材名称">
          <el-input v-model="form.herbName" />
        </el-form-item>
        <el-form-item label="方剂名称">
          <el-input v-model="form.formulaName" />
        </el-form-item>
        <el-form-item label="适应症">
          <el-input v-model="form.indication" />
        </el-form-item>
        <el-form-item label="阶段">
          <el-select v-model="form.phase" placeholder="请选择阶段" clearable>
            <el-option label="探索期" value="EXPLORATION" />
            <el-option label="验证期" value="VERIFICATION" />
            <el-option label="优化期" value="OPTIMIZATION" />
            <el-option label="申报期" value="DECLARATION" />
          </el-select>
        </el-form-item>
        <el-form-item label="项目负责人用户ID">
          <el-input-number v-model="form.projectorId" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" placeholder="请选择状态" clearable>
            <el-option label="立项中" :value="1" />
            <el-option label="进行中" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" rows="3" />
        </el-form-item>
        <el-form-item label="预算">
          <el-input-number v-model="form.budget" :min="0" :step="1" />
        </el-form-item>
        <el-form-item label="优先级">
          <el-input-number v-model="form.priority" :min="0" />
        </el-form-item>
        <el-form-item label="AI立项评估">
          <el-input v-model="form.aiAssess" type="textarea" rows="6" placeholder="必填" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="submit">提交</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.page-wrap {
  max-width: 920px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.mb-12 {
  margin-bottom: 12px;
}
</style>


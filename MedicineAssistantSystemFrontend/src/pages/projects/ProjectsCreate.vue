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
  <div>
    <div style="display: flex; gap: 12px; margin-bottom: 12px; align-items: center">
      <router-link to="/projects">返回</router-link>
    </div>

    <el-alert v-if="error" type="error" show-icon :title="error" style="margin-bottom: 12px" />

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
        <el-input
          v-model="form.phase"
          placeholder="EXPLORATION / VERIFICATION / OPTIMIZATION / DECLARATION"
        />
      </el-form-item>
      <el-form-item label="项目负责人用户ID">
        <el-input-number v-model="form.projectorId" :min="0" />
      </el-form-item>
      <el-form-item label="状态">
        <el-input-number v-model="form.status" :min="1" />
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
      <el-form-item label="aiAssess">
        <el-input v-model="form.aiAssess" type="textarea" rows="6" placeholder="必填" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" :loading="loading" @click="submit">提交</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped></style>


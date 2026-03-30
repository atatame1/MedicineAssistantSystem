<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { listProjects, type Project } from '@/api/projects'

const status = ref<number | ''>('')
const items = ref<Project[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

async function load() {
  loading.value = true
  error.value = null
  try {
    items.value = await listProjects(status.value === '' ? undefined : status.value)
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div>
    <div style="display: flex; gap: 12px; margin-bottom: 12px; align-items: center">
      <div style="display: flex; gap: 8px; align-items: center">
        <span style="font-size: 14px">状态</span>
        <el-select v-model="status" style="width: 120px" placeholder="全部" clearable>
          <el-option :value="1" label="1" />
          <el-option :value="2" label="2" />
          <el-option :value="3" label="3" />
          <el-option :value="4" label="4" />
        </el-select>
      </div>
      <el-button @click="load" :loading="loading">刷新</el-button>
      <router-link to="/projects/create">创建项目</router-link>
    </div>

    <el-alert v-if="error" :title="error" type="error" show-icon style="margin-bottom: 12px" />

    <el-table v-if="!loading" :data="items" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="projectName" label="项目名称" />
      <el-table-column prop="phase" label="阶段" width="120" />
      <el-table-column prop="status" label="状态" width="90" />
      <el-table-column prop="projectorId" label="负责人" width="100" />
    </el-table>
  </div>
</template>

<style scoped></style>


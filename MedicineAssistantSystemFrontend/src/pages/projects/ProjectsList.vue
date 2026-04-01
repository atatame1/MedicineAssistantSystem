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

function statusText(status?: number | null) {
  const m: Record<number, string> = { 1: '立项中', 2: '进行中', 3: '已完成', 4: '已取消' }
  if (status == null) return '-'
  return m[status] || `状态${status}`
}

onMounted(load)
</script>

<template>
  <div class="page-wrap">
    <el-card>
      <div class="toolbar">
        <div class="left-tools">
          <el-select v-model="status" style="width: 150px" placeholder="全部状态" clearable>
            <el-option :value="1" label="立项中" />
            <el-option :value="2" label="进行中" />
            <el-option :value="3" label="已完成" />
            <el-option :value="4" label="已取消" />
          </el-select>
          <el-button @click="load" :loading="loading">刷新</el-button>
        </div>
        <el-button type="primary" @click="$router.push('/projects/create')">创建项目</el-button>
      </div>

      <el-alert v-if="error" :title="error" type="error" show-icon class="mb-12" />

      <el-table v-loading="loading" :data="items" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="projectName" label="项目名称" min-width="200" />
        <el-table-column prop="phase" label="阶段" width="140" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag effect="light">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="projectorId" label="负责人" width="100" />
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.page-wrap {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  gap: 10px;
  flex-wrap: wrap;
}

.left-tools {
  display: flex;
  align-items: center;
  gap: 10px;
}

.mb-12 {
  margin-bottom: 12px;
}
</style>


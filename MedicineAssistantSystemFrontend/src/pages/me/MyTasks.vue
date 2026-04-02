<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { listMyTasks, type TaskResponse } from '@/api/user'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const userId = computed(() => auth.user?.userId || 0)
const loading = ref(false)
const error = ref<string | null>(null)
const rows = ref<TaskResponse[]>([])

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
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="title" label="标题" min-width="240" />
        <el-table-column prop="projectName" label="项目" min-width="200" />
        <el-table-column prop="priority" label="优先级" width="90" />
        <el-table-column prop="deadline" label="截止时间" width="180" />
        <el-table-column label="逾期" width="90">
          <template #default="{ row }">
            <el-tag v-if="row.overdue" type="danger">是</el-tag>
            <el-tag v-else effect="light">否</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-width: 1100px;
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
  font-size: 11px;
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
  font-size: 13px;
  color: rgba(255, 255, 255, 0.58);
}

.btn {
  align-self: center;
}

.panel {
  border-radius: 22px;
  padding: 4px 4px 8px;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.08);
}
</style>


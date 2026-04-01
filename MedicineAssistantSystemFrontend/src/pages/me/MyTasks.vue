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
    <div class="head">
      <div>
        <div class="t">我的任务</div>
        <div class="s">待办与截止</div>
      </div>
      <div class="act">
        <el-button :loading="loading" @click="load">加载</el-button>
      </div>
    </div>

    <el-alert v-if="error" :title="error" type="error" show-icon />

    <el-card>
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
    </el-card>
  </div>
</template>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}
.t {
  font-size: 18px;
  font-weight: 900;
  color: #123b30;
}
.s {
  margin-top: 6px;
  font-size: 12px;
  color: #5c736b;
}
.act {
  display: flex;
  gap: 10px;
  align-items: center;
}
</style>


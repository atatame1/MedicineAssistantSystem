<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { listMyProjects, type ProjectResponse } from '@/api/user'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const userId = computed(() => auth.user?.userId || 0)
const loading = ref(false)
const error = ref<string | null>(null)
const rows = ref<ProjectResponse[]>([])

async function load() {
  loading.value = true
  error.value = null
  try {
    rows.value = await listMyProjects(userId.value)
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
        <h1 class="t">我的项目</h1>
        <p class="s">参与与创建</p>
      </div>
      <el-button class="btn" round :loading="loading" @click="load">刷新</el-button>
    </header>

    <el-alert v-if="error" :title="error" type="error" show-icon />

    <div class="panel">
      <el-table v-loading="loading" :data="rows" stripe>
        <el-table-column prop="id" label="项目ID" width="110" />
        <el-table-column prop="projectName" label="项目名称" min-width="200" />
        <el-table-column prop="herbName" label="药材" min-width="120" />
        <el-table-column prop="formulaName" label="方剂" min-width="120" />
        <el-table-column prop="indication" label="适应症" min-width="160" />
        <el-table-column prop="phase" label="阶段" width="140" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column prop="plannedEndTime" label="计划完成" width="180" />
      </el-table>
    </div>
  </div>
</template>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-width: 1320px;
  width: 100%;
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
  font-size: 13px;
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
  font-size: 15px;
  color: rgba(255, 255, 255, 0.58);
}

.btn {
  align-self: center;
}

.panel {
  border-radius: 22px;
  padding: 4px 4px 8px;
  background: linear-gradient(165deg, rgba(255, 255, 255, 0.06), rgba(10, 38, 32, 0.36));
  backdrop-filter: blur(12px);
  border: 1px solid rgba(115, 209, 180, 0.2);
}

.panel :deep(.el-table) {
  --el-table-bg-color: rgba(255, 255, 255, 0.03);
  --el-table-tr-bg-color: rgba(255, 255, 255, 0.03);
  --el-table-header-bg-color: rgba(0, 0, 0, 0.28);
  --el-table-row-hover-bg-color: rgba(255, 255, 255, 0.07);
  --el-table-text-color: rgba(255, 255, 255, 0.9);
  --el-table-header-text-color: rgba(255, 255, 255, 0.76);
  --el-table-border-color: rgba(255, 255, 255, 0.1);
  --el-fill-color-lighter: rgba(255, 255, 255, 0.03);
  background: transparent;
}

.panel :deep(.el-table__inner-wrapper::before) {
  display: none;
}

.panel :deep(.el-alert) {
  background: rgba(180, 40, 40, 0.2);
  border: 1px solid rgba(255, 120, 120, 0.38);
}
</style>


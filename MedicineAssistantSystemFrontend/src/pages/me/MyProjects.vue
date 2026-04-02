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
    <div class="head">
      <div>
        <div class="t">我的项目</div>
        <div class="s">参与与创建</div>
      </div>
      <div class="act">
        <el-button :loading="loading" @click="load">加载</el-button>
      </div>
    </div>

    <el-alert v-if="error" :title="error" type="error" show-icon />

    <el-card>
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


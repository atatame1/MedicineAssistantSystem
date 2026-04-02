<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { listMyReports, type DocumentResponse } from '@/api/user'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const userId = computed(() => auth.user?.userId || 0)
const loading = ref(false)
const error = ref<string | null>(null)
const rows = ref<DocumentResponse[]>([])

async function load() {
  loading.value = true
  error.value = null
  try {
    rows.value = await listMyReports(userId.value)
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    loading.value = false
  }
}

function openFile(row: DocumentResponse) {
  if (!row.projectId || !row.id) return
  const url = `/api/projects/${encodeURIComponent(String(row.projectId))}/documents/${encodeURIComponent(String(row.id))}/file`
  window.open(url, '_blank')
}

onMounted(load)
</script>

<template>
  <div class="page">
    <div class="head">
      <div>
        <div class="t">我的报告</div>
        <div class="s">项目产出与文档</div>
      </div>
      <div class="act">
        <el-button :loading="loading" @click="load">加载</el-button>
      </div>
    </div>

    <el-alert v-if="error" :title="error" type="error" show-icon />

    <el-card>
      <el-table v-loading="loading" :data="rows" stripe>
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="docName" label="名称" min-width="220" />
        <el-table-column prop="projectName" label="项目" min-width="160" />
        <el-table-column prop="docType" label="类型" width="140" />
        <el-table-column prop="uploadTime" label="上传时间" width="180" />
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              @click="openFile(row)"
              :disabled="!row.storageKey"
              >下载</el-button
            >
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


<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { listRegulations, type Regulation } from '@/api/regulations'

const keyword = ref('')
const items = ref<Regulation[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

async function load() {
  loading.value = true
  error.value = null
  try {
    items.value = await listRegulations(keyword.value.trim() || undefined)
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
      <el-input v-model="keyword" placeholder="keyword" style="flex: 1" />
      <el-button @click="load" :loading="loading" type="primary">搜索</el-button>
    </div>

    <el-alert v-if="error" type="error" show-icon :title="error" style="margin-bottom: 12px" />

    <el-table v-if="!loading" :data="items" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="regulationNumber" label="文号" width="160" />
      <el-table-column prop="issuingAuthority" label="发布机构" />
    </el-table>

    <div v-if="loading" style="padding: 12px 0">加载中...</div>
  </div>
</template>

<style scoped></style>


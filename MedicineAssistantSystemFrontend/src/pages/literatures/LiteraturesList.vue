<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { listLiteratures, type Literature } from '@/api/literatures'

const keyword = ref('')
const items = ref<Literature[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

async function load() {
  loading.value = true
  error.value = null
  try {
    items.value = await listLiteratures(keyword.value.trim() || undefined)
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
      <router-link to="/literatures/create">新增文献</router-link>
    </div>

    <el-alert v-if="error" type="error" show-icon :title="error" style="margin-bottom: 12px" />

    <el-table v-if="!loading" :data="items" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="researchType" label="研究类型" width="160" />
      <el-table-column label="操作" width="220">
        <template #default="scope">
          <router-link :to="`/literatures/${scope.row.id}/summary`">生成摘要</router-link>
        </template>
      </el-table-column>
    </el-table>

    <div v-if="loading" style="padding: 12px 0">加载中...</div>
  </div>
</template>

<style scoped></style>


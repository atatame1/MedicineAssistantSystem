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
  <div class="page-wrap">
    <el-card>
      <div class="toolbar">
        <el-input v-model="keyword" placeholder="输入关键词检索文献" clearable class="keyword-input" />
        <el-button @click="load" :loading="loading" type="primary">搜索</el-button>
        <el-button @click="$router.push('/literatures/create')">新增文献</el-button>
      </div>

      <el-alert v-if="error" type="error" show-icon :title="error" class="mb-12" />

      <el-table v-loading="loading" :data="items" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="260" />
        <el-table-column prop="researchType" label="研究类型" width="160" />
        <el-table-column label="操作" width="130" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="$router.push(`/literatures/${row.id}/summary`)">
              生成摘要
            </el-button>
          </template>
        </el-table-column>
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
  gap: 10px;
  align-items: center;
  margin-bottom: 12px;
}

.keyword-input {
  flex: 1;
}

.mb-12 {
  margin-bottom: 12px;
}
</style>


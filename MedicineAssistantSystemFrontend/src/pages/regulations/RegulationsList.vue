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
  <div class="page-wrap">
    <el-card>
      <div class="toolbar">
        <el-input v-model="keyword" placeholder="输入关键词检索法规" clearable class="keyword-input" />
        <el-button @click="load" :loading="loading" type="primary">搜索</el-button>
      </div>

      <el-alert v-if="error" type="error" show-icon :title="error" class="mb-12" />

      <el-table v-loading="loading" :data="items" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="名称" min-width="260" />
        <el-table-column prop="regulationNumber" label="文号" width="180" />
        <el-table-column prop="issuingAuthority" label="发布机构" min-width="220" />
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


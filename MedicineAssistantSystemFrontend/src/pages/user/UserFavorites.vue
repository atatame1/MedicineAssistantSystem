<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import {
  getUserFavoriteStatistics,
  listUserFavorites,
  type FavoriteResponse,
  type FavoriteStatisticsResponse
} from '@/api/user'

const route = useRoute()
const userId = computed(() => Number(route.params.userId))

const loading = ref(false)
const error = ref<string | null>(null)
const items = ref<FavoriteResponse[]>([])
const stats = ref<FavoriteStatisticsResponse | null>(null)

async function load() {
  loading.value = true
  error.value = null
  try {
    items.value = await listUserFavorites(userId.value)
    stats.value = await getUserFavoriteStatistics(userId.value)
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
      <router-link :to="`/user/${userId}/settings`">返回设置</router-link>
      <el-button @click="load" :disabled="loading">刷新</el-button>
    </div>

    <div style="margin-bottom: 12px" v-if="stats">
      <div>总数：{{ stats.totalCount }}</div>
      <div v-for="(v, k) in stats.typeCountMap" :key="k">类型 {{ k }}：{{ v }}</div>
    </div>

    <el-alert v-if="error" type="error" show-icon :title="error" style="margin-bottom: 12px" />

    <div v-if="loading">加载中...</div>

    <el-table v-else :data="items" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="favoriteId" label="favoriteId" width="120" />
      <el-table-column prop="favoriteType" label="favoriteType" width="160" />
      <el-table-column prop="entityName" label="entityName" />
      <el-table-column prop="collectTime" label="collectTime" width="180" />
    </el-table>
  </div>
</template>

<style scoped></style>


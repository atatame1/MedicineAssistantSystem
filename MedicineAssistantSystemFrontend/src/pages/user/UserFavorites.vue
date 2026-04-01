<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  getUserFavoriteStatistics,
  listUserFavorites,
  type FavoriteResponse,
  type FavoriteStatisticsResponse
} from '@/api/user'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const userId = computed(() => auth.user?.userId || Number(route.params.userId))

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

onMounted(() => {
  if (auth.user?.userId && Number(route.params.userId) !== auth.user.userId) {
    router.replace(`/user/${auth.user.userId}/favorites`)
    return
  }
  load()
})
</script>

<template>
  <div class="page-wrap">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的收藏</span>
          <div class="header-actions">
            <el-button text @click="$router.push(`/user/${userId}/settings`)">设置</el-button>
            <el-button @click="load" :loading="loading">刷新</el-button>
          </div>
        </div>
      </template>

      <el-alert v-if="error" type="error" show-icon :title="error" class="mb-12" />

      <el-row v-if="stats" :gutter="12" class="mb-12">
        <el-col :xs="24" :sm="8">
          <div class="stat-box">
            <div class="stat-label">收藏总数</div>
            <div class="stat-value">{{ stats.totalCount }}</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="16">
          <div class="stat-box">
            <div class="stat-label">类型分布</div>
            <div class="type-list">
              <el-tag v-for="(v, k) in stats.typeCountMap" :key="k" effect="light">
                {{ k }}：{{ v }}
              </el-tag>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-table v-loading="loading" :data="items" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="favoriteId" label="对象ID" width="120" />
        <el-table-column prop="favoriteType" label="类型" width="160" />
        <el-table-column prop="entityName" label="名称" min-width="260" />
        <el-table-column prop="collectTime" label="收藏时间" width="180" />
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

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.mb-12 {
  margin-bottom: 12px;
}

.stat-box {
  border: 1px solid #e8efeb;
  border-radius: 10px;
  padding: 10px 12px;
}

.stat-label {
  color: #6d7d74;
  font-size: 12px;
}

.stat-value {
  margin-top: 6px;
  font-size: 24px;
  font-weight: 700;
  color: #1f4337;
}

.type-list {
  margin-top: 8px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
</style>


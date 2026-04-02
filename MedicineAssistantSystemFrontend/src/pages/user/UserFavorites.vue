<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { apiGet } from '@/api/http'
import {
  getUserFavoriteStatistics,
  listUserFavorites,
  type FavoriteResponse,
  type FavoriteStatisticsResponse,
  removeFavorite,
  type FavoriteOperationRequest
} from '@/api/user'
import { useAuthStore } from '@/stores/auth'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const userId = computed(() => auth.user?.userId || Number(route.params.userId))

const loading = ref(false)
const error = ref<string | null>(null)
const items = ref<FavoriteResponse[]>([])
const stats = ref<FavoriteStatisticsResponse | null>(null)
const detailOpen = ref(false)
const detailRow = ref<FavoriteResponse | null>(null)
const detailLoading = ref(false)
const detailError = ref<string | null>(null)
const detailData = ref<any>(null)

const typeLabelMap: Record<string, string> = {
  TASK: '任务',
  PROJECT: '项目',
  DOCUMENT: '文档',
  LITERATURE: '文献',
  PATENT: '专利',
  FORMULA: '方剂',
  HERB: '中药材',
  COMPONENT: '成分',
  Disease: '疾病',
  Regulation: '法规'
}

function typeLabel(t?: string | null) {
  if (!t) return '-'
  return typeLabelMap[t] || t
}

function stripIds(value: any): any {
  if (value === null || value === undefined) return value
  if (Array.isArray(value)) return value.map(stripIds)
  if (typeof value !== 'object') return value
  const out: Record<string, any> = {}
  for (const [k, v] of Object.entries(value)) {
    if (k === 'id' || k === 'favoriteId' || k.endsWith('Id')) continue
    out[k] = stripIds(v)
  }
  return out
}

const detailText = computed(() => {
  if (!detailData.value) return ''
  const filtered = stripIds(detailData.value)
  return JSON.stringify(filtered, null, 2)
})

async function openDetail(row: FavoriteResponse) {
  detailRow.value = row
  detailOpen.value = true
  detailLoading.value = true
  detailError.value = null
  detailData.value = null
  try {
    const id = row.favoriteId
    const type = row.favoriteType
    let data: any = null
    if (type === 'TASK') {
      data = await apiGet(`/api/user/${userId.value}/tasks/${encodeURIComponent(String(id))}`)
    } else if (type === 'PROJECT') {
      data = await apiGet(`/api/projects/${encodeURIComponent(String(id))}`)
    } else if (type === 'DOCUMENT') {
      data = await apiGet(`/api/projects/documents/${encodeURIComponent(String(id))}`)
    } else if (type === 'LITERATURE') {
      data = await apiGet(`/api/literatures/${encodeURIComponent(String(id))}`)
    } else if (type === 'PATENT') {
      data = await apiGet(`/api/patents/${encodeURIComponent(String(id))}`)
    } else if (type === 'FORMULA') {
      data = await apiGet(`/api/formulas/${encodeURIComponent(String(id))}`)
    } else if (type === 'HERB') {
      data = await apiGet(`/api/herbs/${encodeURIComponent(String(id))}`)
    } else if (type === 'COMPONENT') {
      data = await apiGet(`/api/components/${encodeURIComponent(String(id))}`)
    } else if (type === 'Disease') {
      data = await apiGet(`/api/diseases/${encodeURIComponent(String(id))}`)
    } else if (type === 'Regulation') {
      data = await apiGet(`/api/regulations/${encodeURIComponent(String(id))}`)
    }
    detailData.value = data
  } catch (e: any) {
    detailError.value = String(e?.message || e)
  } finally {
    detailLoading.value = false
  }
}

async function confirmRemove(row: FavoriteResponse) {
  if (!row.id) return
  const body: FavoriteOperationRequest = { favoriteId: row.id, favoriteType: row.favoriteType }
  try {
    await ElMessageBox.confirm('确定取消收藏吗？', '确认', {
      type: 'warning',
      confirmButtonText: '取消收藏',
      cancelButtonText: '返回'
    })
    await removeFavorite(userId.value, body)
    await load()
  } catch {}
}

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
        <el-table-column prop="favoriteType" label="类型" width="160">
          <template #default="{ row }">
            {{ typeLabel(row.favoriteType) }}
          </template>
        </el-table-column>
        <el-table-column prop="entityName" label="名称" min-width="260" />
        <el-table-column prop="collectTime" label="收藏时间" width="180" />
        <el-table-column label="操作" width="220" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row)">查看详细</el-button>
            <el-button
              link
              type="danger"
              @click="confirmRemove(row)"
            >取消收藏</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>

  <el-dialog v-model="detailOpen" title="收藏详情" width="720px">
    <el-alert v-if="detailError" type="error" show-icon :title="detailError" class="mb-12" />
    <el-descriptions :column="1" border>
      <el-descriptions-item label="类型">{{ detailRow ? typeLabel(detailRow.favoriteType) : '-' }}</el-descriptions-item>
      <el-descriptions-item label="名称">{{ detailRow?.entityName ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="收藏时间">{{ detailRow?.collectTime ?? '-' }}</el-descriptions-item>
    </el-descriptions>

    <div class="detail-box">
      <el-input :model-value="detailText" type="textarea" :rows="12" readonly :disabled="detailLoading" />
    </div>

    <template #footer>
      <el-button type="primary" @click="detailOpen = false">关闭</el-button>
    </template>
  </el-dialog>
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

.detail-box {
  margin-top: 12px;
}
</style>


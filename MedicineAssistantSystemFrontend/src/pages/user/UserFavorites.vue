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
type DetailItem = { key: string; value: string; multiline: boolean }

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
const detailKeyLabelMap: Record<string, string> = {
  name: '名称',
  aliases: '别名',
  source: '出处',
  era: '时代',
  category: '分类',
  composition: '组成',
  function: '功效',
  usage: '用法用量',
  indication: '适应症',
  caution: '注意事项',
  relatedHerbs: '相关药材',
  relatedFormulas: '相关方剂',
  similarFormulas: '相似方剂',
  createTime: '创建时间',
  updateTime: '更新时间'
}
const hiddenDetailKeys = new Set([
  'imageUrl',
  'createBy',
  'updateBy',
  'deleted',
  'delFlag',
  'remark',
  'version'
])

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

function tryParseJsonString(value: string): any {
  const s = value.trim()
  if (!s || (!(s.startsWith('{') && s.endsWith('}')) && !(s.startsWith('[') && s.endsWith(']')))) return value
  try {
    return JSON.parse(s)
  } catch {
    return value
  }
}

function normalizeDetailValue(value: any): any {
  if (value === null || value === undefined) return value
  if (typeof value === 'string') {
    const parsed = tryParseJsonString(value)
    if (parsed !== value) return normalizeDetailValue(parsed)
    return value
  }
  if (Array.isArray(value)) return value.map(normalizeDetailValue)
  if (typeof value !== 'object') return value
  const out: Record<string, any> = {}
  Object.entries(value).forEach(([k, v]) => {
    out[k] = normalizeDetailValue(v)
  })
  return out
}

const detailItems = computed<DetailItem[]>(() => {
  if (!detailData.value) return []
  const filtered = stripIds(normalizeDetailValue(detailData.value))
  if (filtered === null || filtered === undefined) return []
  if (typeof filtered !== 'object' || Array.isArray(filtered)) {
    return [{ key: '内容', value: JSON.stringify(filtered, null, 2), multiline: true }]
  }
  const toLabel = (k: string) => detailKeyLabelMap[k] || k
  const isPrimitive = (x: unknown) => ['string', 'number', 'boolean'].includes(typeof x)
  const formatCompositionItem = (x: any) => {
    if (!x || typeof x !== 'object') return String(x ?? '暂无')
    const name = x.name || '未命名'
    const dose = x.dose ? ` ${x.dose}` : ''
    return `${name}${dose}`.trim()
  }
  return Object.entries(filtered).map(([k, v]) => {
    if (hiddenDetailKeys.has(k)) return null
    if (v === null || v === undefined || v === '') return { key: toLabel(k), value: '暂无', multiline: false }
    if (Array.isArray(v)) {
      if (v.length === 0) return { key: toLabel(k), value: '暂无', multiline: false }
      if (k === 'composition' || k === 'junYao' || k === 'chenYao' || k === 'zuoYao' || k === 'shiYao') {
        const titleMap: Record<string, string> = {
          composition: '组成',
          junYao: '君药',
          chenYao: '臣药',
          zuoYao: '佐药',
          shiYao: '使药'
        }
        const text = v.map(formatCompositionItem).join('；')
        return { key: titleMap[k] || toLabel(k), value: text || '暂无', multiline: text.length > 60 }
      }
      if (v.every(isPrimitive)) {
        const text = (v as Array<string | number | boolean>).map(i => String(i)).join('、')
        return { key: toLabel(k), value: text || '暂无', multiline: text.length > 60 }
      }
      const text = v.map(item => formatCompositionItem(item)).join('；')
      return { key: toLabel(k), value: text || '暂无', multiline: text.length > 60 }
    }
    if (typeof v === 'object') {
      const lines = Object.entries(v)
        .filter(([subK]) => !hiddenDetailKeys.has(subK))
        .map(([subK, subV]) => `${detailKeyLabelMap[subK] || subK}：${subV == null || subV === '' ? '暂无' : String(subV)}`)
      return { key: toLabel(k), value: lines.join('\n') || '暂无', multiline: true }
    }
    const text = String(v)
    return { key: toLabel(k), value: text, multiline: text.length > 80 || text.includes('\n') }
  }).filter(Boolean) as DetailItem[]
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
  if (!row.favoriteId) return
  const body: FavoriteOperationRequest = { favoriteId: row.favoriteId, favoriteType: row.favoriteType }
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
  <div>
    <div class="page-wrap">
      <header class="page-head">
        <div>
          <p class="kicker">星标</p>
          <h1 class="title">我的收藏</h1>
          <p class="sub">跨模块统一查看与管理</p>
        </div>
        <div class="header-actions">
          <el-button round @click="$router.push(`/user/${userId}/settings`)">设置</el-button>
          <el-button round :loading="loading" @click="load">刷新</el-button>
        </div>
      </header>

      <div class="panel">
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
                <el-tag v-for="(v, k) in stats.typeCountMap" :key="k" effect="dark">
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
      </div>
    </div>

    <el-dialog v-model="detailOpen" title="收藏详情" width="760px" class="fav-detail-dialog">
      <el-alert v-if="detailError" type="error" show-icon :title="detailError" class="mb-12" />
      <div class="detail-summary">
        <div class="summary-row">
          <span class="summary-key">类型</span>
          <span class="summary-value">{{ detailRow ? typeLabel(detailRow.favoriteType) : '暂无' }}</span>
        </div>
        <div class="summary-row">
          <span class="summary-key">名称</span>
          <span class="summary-value">{{ detailRow?.entityName || '暂无' }}</span>
        </div>
        <div class="summary-row">
          <span class="summary-key">收藏时间</span>
          <span class="summary-value">{{ detailRow?.collectTime || '暂无' }}</span>
        </div>
      </div>

      <div class="detail-box mas-scrollbar">
        <div v-if="detailLoading" class="detail-empty">加载中…</div>
        <div v-else-if="!detailItems.length" class="detail-empty">暂无详情</div>
        <div v-else class="detail-list">
          <div v-for="(item, idx) in detailItems" :key="`${item.key}-${idx}`" class="detail-row">
            <div class="detail-key">{{ item.key }}</div>
            <pre v-if="item.multiline" class="detail-value pre">{{ item.value }}</pre>
            <div v-else class="detail-value">{{ item.value }}</div>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button type="primary" @click="detailOpen = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-wrap {
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

.page-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 12px;
}

.kicker {
  margin: 0 0 6px;
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.42);
}

.title {
  margin: 0;
  font-size: 24px;
  font-weight: 950;
  color: rgba(255, 255, 255, 0.94);
}

.sub {
  margin: 8px 0 0;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.55);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.panel {
  border-radius: 22px;
  padding: 16px 16px 8px;
  background: linear-gradient(165deg, rgba(255, 255, 255, 0.06), rgba(10, 38, 32, 0.36));
  backdrop-filter: blur(14px);
  border: 1px solid rgba(115, 209, 180, 0.2);
}

.mb-12 {
  margin-bottom: 12px;
}

.stat-box {
  border-radius: 16px;
  padding: 14px 16px;
  background: rgba(14, 42, 36, 0.35);
  border: 1px solid rgba(115, 209, 180, 0.24);
}

.stat-label {
  color: rgba(255, 255, 255, 0.55);
  font-size: 12px;
}

.stat-value {
  margin-top: 8px;
  font-size: 28px;
  font-weight: 800;
  color: rgba(255, 255, 255, 0.95);
}

.type-list {
  margin-top: 8px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.detail-box {
  margin-top: 12px;
  max-height: 340px;
  overflow: auto;
  border: 1px solid rgba(115, 209, 180, 0.18);
  border-radius: 14px;
  background: rgba(14, 42, 36, 0.4);
  padding: 12px 14px;
}

.detail-summary {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
  margin-bottom: 12px;
}

.summary-row {
  border-radius: 12px;
  border: 1px solid rgba(115, 209, 180, 0.18);
  background: rgba(255, 255, 255, 0.04);
  padding: 12px 14px;
}

.summary-key {
  display: block;
  font-size: 12px;
  font-weight: 800;
  color: rgba(255, 255, 255, 0.52);
  margin-bottom: 6px;
}

.summary-value {
  display: block;
  font-size: 15px;
  font-weight: 900;
  color: rgba(255, 255, 255, 0.92);
  line-height: 1.45;
}

.detail-list {
  display: grid;
  gap: 10px;
}

.detail-row {
  display: grid;
  grid-template-columns: 120px 1fr;
  gap: 10px;
  align-items: start;
  padding: 8px 0;
  border-bottom: 1px dashed rgba(255, 255, 255, 0.08);
}

.detail-row:last-child {
  border-bottom: none;
}

.detail-key {
  font-size: 13px;
  font-weight: 900;
  color: rgba(255, 255, 255, 0.7);
  word-break: break-word;
}

.detail-value {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.92);
  line-height: 1.5;
  word-break: break-word;
}

.detail-value.pre {
  margin: 0;
  white-space: pre-wrap;
  font-family: inherit;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 6px;
  padding: 8px;
}

.detail-empty {
  color: rgba(255, 255, 255, 0.6);
  font-size: 13px;
  padding: 8px 2px;
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

.panel :deep(.el-tag.el-tag--dark) {
  background: rgba(14, 42, 36, 0.76);
  border-color: rgba(115, 209, 180, 0.32);
  color: rgba(255, 255, 255, 0.9);
}

:global(.fav-detail-dialog.el-dialog) {
  background: rgba(18, 42, 36, 0.97);
  border: 1px solid rgba(115, 209, 180, 0.28);
  box-shadow: 0 20px 56px rgba(0, 0, 0, 0.48);
}

:global(.fav-detail-dialog .el-dialog__header) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  margin-right: 0;
  padding-bottom: 12px;
}

:global(.fav-detail-dialog .el-dialog__title) {
  color: rgba(255, 255, 255, 0.94);
}

:global(.fav-detail-dialog .el-dialog__body) {
  color: rgba(255, 255, 255, 0.9);
}

@media (max-width: 760px) {
  .detail-summary {
    grid-template-columns: 1fr;
  }

  .detail-row {
    grid-template-columns: 1fr;
    gap: 6px;
  }
}
</style>


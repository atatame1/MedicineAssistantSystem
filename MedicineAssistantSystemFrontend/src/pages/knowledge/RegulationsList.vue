<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { getRegulationDetail, listRegulations, type Regulation } from '@/api/regulations'
import { addFavorite, listUserFavorites, removeFavorite } from '@/api/user'
import { useAuthStore } from '@/stores/auth'

const keyword = ref('')
const items = ref<Regulation[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

const selectedId = ref<number | null>(null)
const detailLoading = ref(false)
const detailError = ref<string | null>(null)
const detail = ref<Regulation | null>(null)
const auth = useAuthStore()
const favoriteIds = ref<Set<number>>(new Set())

const hasDetail = computed(() => !!detail.value)

async function load() {
  loading.value = true
  error.value = null
  try {
    items.value = await listRegulations(keyword.value.trim() || undefined)
    if (auth.user?.userId) {
      const favs = await listUserFavorites(auth.user.userId)
      favoriteIds.value = new Set(
        favs.filter((f) => f.favoriteType === 'Regulation' && f.favoriteId != null).map((f) => Number(f.favoriteId))
      )
    } else {
      favoriteIds.value = new Set()
    }
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    loading.value = false
  }
}

function isFavorited(id?: number | null) {
  return !!id && favoriteIds.value.has(Number(id))
}

async function toggleFavorite(row: Regulation) {
  if (!row.id) return
  if (!auth.user?.userId) {
    try {
      window.dispatchEvent(new CustomEvent('auth:required'))
    } catch {}
    return
  }
  try {
    const id = Number(row.id)
    if (favoriteIds.value.has(id)) {
      await removeFavorite(auth.user.userId, { favoriteId: id, favoriteType: 'Regulation' })
      favoriteIds.value.delete(id)
    } else {
      await addFavorite(auth.user.userId, { favoriteId: id, favoriteType: 'Regulation' })
      favoriteIds.value.add(id)
    }
    favoriteIds.value = new Set(favoriteIds.value)
  } catch (e: any) {
    error.value = String(e?.message || e)
  }
}

async function openDetail(r: Regulation) {
  if (!r.id) return
  selectedId.value = r.id
  detailLoading.value = true
  detailError.value = null
  detail.value = null
  try {
    detail.value = await getRegulationDetail(r.id)
  } catch (e: any) {
    detailError.value = String(e?.message || e)
  } finally {
    detailLoading.value = false
  }
}

const tableMax = ref(400)
function syncTableMax() {
  tableMax.value = Math.max(240, Math.min(520, window.innerHeight - 380))
}

onMounted(() => {
  load()
  syncTableMax()
  window.addEventListener('resize', syncTableMax)
})
onBeforeUnmount(() => window.removeEventListener('resize', syncTableMax))
</script>

<template>
  <div class="page-wrap">
    <div class="head">
      <div class="intro">
        <div class="intro-title">法规库</div>
        <div class="intro-sub">政策法规 · 注册审批 · 合规要点速览</div>
      </div>
      <div class="toolbar">
        <el-input
          v-model="keyword"
          placeholder="按关键词检索法规/通知"
          clearable
          class="keyword-input"
          @keyup.enter="load"
        />
        <el-button class="btn-go" @click="load" :loading="loading" type="primary">检索</el-button>
      </div>
    </div>

    <el-alert v-if="error" type="error" show-icon :title="error" class="mb-12" />

    <div class="layout">
      <div class="list">
        <div class="kb-panel">
          <div class="list-title">法规列表</div>
          <div class="tbl-wrap">
            <el-table
              v-loading="loading"
              :data="items"
              :max-height="tableMax"
              class="kb-table"
              stripe
              border
              highlight-current-row
              @row-click="openDetail"
            >
              <el-table-column prop="name" label="名称" min-width="280" />
              <el-table-column label="操作" width="150" align="center">
                <template #default="{ row }">
                  <el-button link type="primary" :disabled="!row.id" @click.stop="openDetail(row)">
                    查看详细
                  </el-button>
                  <el-button
                    link
                    :type="isFavorited(row.id) ? 'warning' : 'info'"
                    :disabled="!row.id"
                    @click.stop="toggleFavorite(row)"
                  >
                    {{ isFavorited(row.id) ? '★ 收藏中' : '☆ 收藏' }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </div>

      <div class="board">
        <div class="kb-panel board-inner mas-scrollbar">
          <div class="board-title">法规详情</div>

          <el-alert v-if="detailError" type="error" show-icon :title="detailError" class="mb-12" />

          <div v-if="detailLoading" class="skeleton">加载中...</div>

          <div v-else-if="hasDetail" class="desc-wrap">
            <div class="detail-grid">
              <div class="detail-row">
                <div class="detail-k">名称</div>
                <div class="detail-v">{{ detail?.name ?? '暂无' }}</div>
              </div>
              <div class="detail-row">
                <div class="detail-k">文号</div>
                <div class="detail-v">{{ detail?.regulationNumber ?? '暂无' }}</div>
              </div>
              <div class="detail-row">
                <div class="detail-k">发布机构</div>
                <div class="detail-v">{{ detail?.issuingAuthority ?? '暂无' }}</div>
              </div>
              <div class="detail-row">
                <div class="detail-k">发布机构类型</div>
                <div class="detail-v">{{ detail?.issuingAuthorityType ?? '暂无' }}</div>
              </div>
              <div class="detail-row">
                <div class="detail-k">法规类型</div>
                <div class="detail-v">{{ detail?.regulationType ?? '暂无' }}</div>
              </div>
              <div class="detail-row">
                <div class="detail-k">分类</div>
                <div class="detail-v">{{ detail?.category ?? '暂无' }}</div>
              </div>
              <div class="detail-row">
                <div class="detail-k">亚类</div>
                <div class="detail-v">{{ detail?.subCategory ?? '暂无' }}</div>
              </div>
              <div class="detail-row">
                <div class="detail-k">生效日期</div>
                <div class="detail-v">{{ detail?.effectiveDate ?? '暂无' }}</div>
              </div>
              <div class="detail-row detail-row-summary">
                <div class="detail-k">摘要</div>
                <div class="detail-v">{{ detail?.summary ?? '暂无' }}</div>
              </div>
            </div>
          </div>

          <div v-else class="empty">请选择左侧某条法规查看详情</div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-wrap {
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding-bottom: 100px;
  box-sizing: border-box;
}

.head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 14px;
  flex-wrap: wrap;
}

.intro-title {
  font-size: 27px;
  line-height: 1.1;
  font-weight: 900;
  color: rgba(233, 244, 239, 0.96);
}

.intro-sub {
  margin-top: 6px;
  font-size: 12px;
  color: rgba(189, 212, 204, 0.74);
}

.toolbar {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: nowrap;
  padding: 8px;
  border-radius: 14px;
  border: 1px solid rgba(115, 209, 180, 0.2);
  background: rgba(8, 28, 24, 0.42);
}

.keyword-input {
  flex: 0 1 auto;
  width: clamp(260px, 34vw, 420px);
}

.btn-go {
  border: 0;
  background: linear-gradient(145deg, #73d1b4, #3d8f75);
  color: #0a221f;
  font-weight: 800;
}

.mb-12 {
  margin-bottom: 12px;
}

.layout {
  display: grid;
  grid-template-columns: 1fr 1.2fr;
  gap: 14px;
  align-items: start;
}

.kb-panel {
  border-radius: 20px;
  padding: 16px;
  background: linear-gradient(165deg, rgba(14, 42, 36, 0.55), rgba(8, 28, 24, 0.42));
  border: 1px solid rgba(115, 209, 180, 0.22);
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.28);
  backdrop-filter: blur(12px);
}

.board-inner {
  max-height: min(72vh, calc(100vh - 220px));
  overflow: auto;
}

.list-title,
.board-title {
  font-size: 16px;
  font-weight: 900;
  margin-bottom: 12px;
  color: rgba(233, 244, 239, 0.95);
}

.tbl-wrap {
  border-radius: 12px;
  overflow: hidden;
}

:deep(.kb-table) {
  --el-table-border-color: rgba(115, 209, 180, 0.18);
  --el-table-bg-color: rgba(10, 34, 31, 0.35);
  --el-table-tr-bg-color: rgba(10, 34, 31, 0.28);
  --el-table-header-bg-color: rgba(14, 42, 36, 0.85);
  --el-table-header-text-color: rgba(158, 228, 207, 0.95);
  --el-table-row-hover-bg-color: rgba(115, 209, 180, 0.12);
  --el-table-text-color: rgba(233, 244, 239, 0.92);
}

:deep(.keyword-input .el-input__wrapper) {
  background: rgba(8, 28, 24, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.12);
  box-shadow: none;
}

:deep(.keyword-input .el-input__inner) {
  color: #e9f4ef;
}

.desc-wrap {
  margin-top: 4px;
}

.detail-grid {
  border: 1px solid rgba(115, 209, 180, 0.2);
  border-radius: 12px;
  overflow: hidden;
}

.detail-row {
  display: grid;
  grid-template-columns: 160px 1fr;
  border-bottom: 1px solid rgba(115, 209, 180, 0.14);
}

.detail-row:last-child {
  border-bottom: none;
}

.detail-k {
  padding: 10px 12px;
  font-size: 13px;
  font-weight: 900;
  color: rgba(158, 228, 207, 0.92);
  background: rgba(14, 42, 36, 0.72);
}

.detail-v {
  padding: 10px 12px;
  font-size: 13px;
  color: rgba(233, 244, 239, 0.9);
  background: rgba(10, 34, 31, 0.42);
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-word;
}

.detail-row-summary .detail-v {
  min-height: 72px;
}

.skeleton {
  padding: 10px 0;
  color: rgba(255, 255, 255, 0.75);
}

.empty {
  padding: 14px 0;
  color: rgba(255, 255, 255, 0.65);
  font-size: 13px;
}

@media (max-width: 980px) {
  .head {
    align-items: stretch;
  }

  .toolbar {
    width: 100%;
  }

  .keyword-input {
    width: 100%;
  }

  .layout {
    grid-template-columns: 1fr;
  }

  .detail-row {
    grid-template-columns: 1fr;
  }
}
</style>


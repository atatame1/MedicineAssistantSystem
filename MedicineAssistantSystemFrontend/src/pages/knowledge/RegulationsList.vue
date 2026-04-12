<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { getRegulationDetail, listRegulations, type Regulation } from '@/api/regulations'

const keyword = ref('')
const items = ref<Regulation[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

const selectedId = ref<number | null>(null)
const detailLoading = ref(false)
const detailError = ref<string | null>(null)
const detail = ref<Regulation | null>(null)

const hasDetail = computed(() => !!detail.value)

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
    <div class="toolbar">
      <el-input
        v-model="keyword"
        placeholder="输入关键词检索法规"
        clearable
        class="keyword-input"
      />
      <el-button class="btn-go" @click="load" :loading="loading" type="primary">搜索</el-button>
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
            <el-descriptions :column="1" border class="kb-desc">
              <el-descriptions-item label="名称">{{ detail?.name ?? '-' }}</el-descriptions-item>
              <el-descriptions-item label="文号">{{ detail?.regulationNumber ?? '-' }}</el-descriptions-item>
              <el-descriptions-item label="发布机构">{{ detail?.issuingAuthority ?? '-' }}</el-descriptions-item>
              <el-descriptions-item label="发布机构类型">{{ detail?.issuingAuthorityType ?? '-' }}</el-descriptions-item>
              <el-descriptions-item label="法规类型">{{ detail?.regulationType ?? '-' }}</el-descriptions-item>
              <el-descriptions-item label="分类">{{ detail?.category ?? '-' }}</el-descriptions-item>
              <el-descriptions-item label="亚类">{{ detail?.subCategory ?? '-' }}</el-descriptions-item>
              <el-descriptions-item label="生效日期">{{ detail?.effectiveDate ?? '-' }}</el-descriptions-item>
              <el-descriptions-item label="摘要">{{ detail?.summary ?? '-' }}</el-descriptions-item>
            </el-descriptions>
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

.toolbar {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

.keyword-input {
  flex: 1;
  min-width: 200px;
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

:deep(.kb-desc.el-descriptions) {
  --el-descriptions-item-bordered-label-background: rgba(14, 42, 36, 0.75);
  --el-text-color-primary: rgba(233, 244, 239, 0.92);
}

:deep(.kb-desc .el-descriptions__label) {
  color: rgba(158, 228, 207, 0.9) !important;
}

:deep(.kb-desc .el-descriptions__content) {
  color: rgba(233, 244, 239, 0.88) !important;
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
  .layout {
    grid-template-columns: 1fr;
  }
}
</style>


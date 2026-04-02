<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
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

onMounted(load)
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
      <el-button @click="load" :loading="loading" type="primary">搜索</el-button>
    </div>

    <el-alert v-if="error" type="error" show-icon :title="error" class="mb-12" />

    <div class="layout">
      <div class="list">
        <el-card>
          <div class="list-title">法规列表</div>
          <el-table
            v-loading="loading"
            :data="items"
            stripe
            highlight-current-row
            @row-click="openDetail"
            :row-class-name="() => ''"
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
        </el-card>
      </div>

      <div class="board">
        <el-card>
          <div class="board-title">法规详情显示板</div>

          <el-alert v-if="detailError" type="error" show-icon :title="detailError" class="mb-12" />

          <div v-if="detailLoading" class="skeleton">加载中...</div>

          <div v-else-if="hasDetail">
            <el-descriptions :column="1" border>
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
        </el-card>
      </div>
    </div>
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
}

.keyword-input {
  flex: 1;
}

.mb-12 {
  margin-bottom: 12px;
}

.layout {
  display: grid;
  grid-template-columns: 1fr 1.2fr;
  gap: 14px;
}

.list-title {
  font-size: 16px;
  font-weight: 900;
  margin-bottom: 10px;
}

.board-title {
  font-size: 16px;
  font-weight: 900;
  margin-bottom: 12px;
}

.skeleton {
  padding: 10px 0;
  color: rgba(255, 255, 255, 0.75);
}

.empty {
  padding: 14px 0;
  color: rgba(255, 255, 255, 0.75);
}

@media (max-width: 980px) {
  .layout {
    grid-template-columns: 1fr;
  }
}
</style>


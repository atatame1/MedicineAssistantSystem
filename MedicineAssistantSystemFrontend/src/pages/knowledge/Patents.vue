<script setup lang="ts">
import { ref } from 'vue'
import CrudList from './CrudList.vue'
import { knowledgeApi, type Patent, type PatentRisk, type PatentSimilarity } from '@/api/knowledge'
import { patentCreate, patentEdit } from './knowledgeFormSchemas'

const riskOpen = ref(false)
const simOpen = ref(false)
const current = ref<Patent | null>(null)
const risk = ref<PatentRisk | null>(null)
const similar = ref<PatentSimilarity[]>([])
const loadingExtra = ref(false)
const errorExtra = ref<string | null>(null)

function syncPatentRow(row: Record<string, unknown>) {
  current.value = row as Patent
}

async function openRisk(row: Patent | null) {
  if (!row?.id) return
  current.value = row
  riskOpen.value = true
  simOpen.value = false
  loadingExtra.value = true
  errorExtra.value = null
  try {
    risk.value = await knowledgeApi.patents.risk(Number(row.id))
  } catch (e: any) {
    errorExtra.value = String(e?.message || e)
  } finally {
    loadingExtra.value = false
  }
}

async function openSimilar(row: Patent | null) {
  if (!row?.id) return
  current.value = row
  simOpen.value = true
  riskOpen.value = false
  loadingExtra.value = true
  errorExtra.value = null
  try {
    similar.value = await knowledgeApi.patents.similar(Number(row.id))
  } catch (e: any) {
    errorExtra.value = String(e?.message || e)
  } finally {
    loadingExtra.value = false
  }
}
</script>

<template>
  <div class="kb-patents">
  <CrudList
    title="专利库"
    placeholder="按关键词检索专利"
    :columns="[
      { prop: 'id', label: 'ID', width: 80 },
      { prop: 'name', label: '名称', minWidth: 320 },
      { prop: 'patentNumber', label: '专利号', minWidth: 180 },
      { prop: 'applicant', label: '申请人', minWidth: 200 }
    ]"
    :list="knowledgeApi.patents.list"
    favoriteType="PATENT"
    :create="knowledgeApi.patents.create"
    :update="knowledgeApi.patents.update"
    :del="knowledgeApi.patents.delete"
    :form-schema-create="patentCreate"
    :form-schema-edit="patentEdit"
    :on-row-interact="syncPatentRow"
  />

  <div class="extra kb-panel">
    <div class="extra-title">专利分析</div>
    <div class="extra-sub">在卡片上点「详细」或「编辑」可选中专利；再使用下方分析</div>
    <div class="extra-actions">
      <el-button :disabled="!current?.id" @click="openSimilar(current)">相似专利</el-button>
      <el-button type="danger" :disabled="!current?.id" @click="openRisk(current)">侵权风险</el-button>
    </div>

    <div v-if="!current" class="empty">请先点某条卡片的「详细」或「编辑」</div>

    <el-alert v-if="errorExtra" :title="errorExtra" type="error" show-icon class="mb" />

    <el-drawer v-model="simOpen" title="相似专利推荐" size="50%">
      <el-table v-loading="loadingExtra" :data="similar" stripe>
        <el-table-column prop="patentId" label="专利ID" width="100" />
        <el-table-column prop="patentName" label="名称" min-width="240" />
        <el-table-column prop="score" label="得分" width="90" />
        <el-table-column prop="riskLevel" label="风险" width="100" />
      </el-table>
    </el-drawer>

    <el-drawer v-model="riskOpen" title="侵权风险提示" size="50%">
      <div v-loading="loadingExtra">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="风险等级">{{ risk?.riskLevel ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="风险说明">{{ risk?.riskDescription ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="建议">{{ risk?.suggestion ?? '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
  </div>
</template>

<style scoped>
.kb-patents {
  padding-bottom: 8px;
}

.extra {
  margin-top: 14px;
}

.kb-panel {
  border-radius: 20px;
  padding: 16px 18px;
  background: linear-gradient(165deg, rgba(14, 42, 36, 0.55), rgba(8, 28, 24, 0.42));
  border: 1px solid rgba(115, 209, 180, 0.22);
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.28);
  backdrop-filter: blur(12px);
}

.extra-title {
  font-size: 16px;
  font-weight: 900;
  color: rgba(233, 244, 239, 0.95);
}
.extra-sub {
  margin-top: 6px;
  font-size: 14px;
  color: rgba(189, 212, 204, 0.75);
}
.extra-actions {
  margin-top: 10px;
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}
.empty {
  margin-top: 10px;
  color: rgba(255, 255, 255, 0.58);
  font-size: 14px;
}
.mb {
  margin: 10px 0;
}
</style>

<script setup lang="ts">
import { ref } from 'vue'
import CrudList from './CrudList.vue'
import { knowledgeApi, type Patent } from '@/api/knowledge'

const riskOpen = ref(false)
const simOpen = ref(false)
const current = ref<Patent | null>(null)
const risk = ref<any>(null)
const similar = ref<any[]>([])
const loadingExtra = ref(false)
const errorExtra = ref<string | null>(null)

async function openRisk(row: any) {
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

async function openSimilar(row: any) {
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
  <div class="wrap">
    <CrudList
      title="专利库"
      placeholder="按关键词检索专利"
      :columns="[
        { prop: 'id', label: 'ID', width: 80 },
        { prop: 'title', label: '标题', minWidth: 320 },
        { prop: 'patentNumber', label: '专利号', minWidth: 180 },
        { prop: 'applicant', label: '申请人', minWidth: 200 }
      ]"
      :list="knowledgeApi.patents.list"
      :create="knowledgeApi.patents.create"
      :update="knowledgeApi.patents.update"
      :del="knowledgeApi.patents.delete"
      :formSchema="[
        { prop: 'title', label: '标题', type: 'textarea' },
        { prop: 'patentNumber', label: '专利号' },
        { prop: 'applicant', label: '申请人' },
        { prop: 'summary', label: '摘要', type: 'textarea' }
      ]"
    />

    <el-card class="extra">
      <div class="extra-title">专利分析</div>
      <div class="extra-sub">选择一条专利后执行：相似推荐 / 侵权风险</div>
      <div class="extra-actions">
        <el-button :disabled="!current" @click="openSimilar(current)">相似专利</el-button>
        <el-button type="danger" :disabled="!current" @click="openRisk(current)">侵权风险</el-button>
      </div>

      <div v-if="!current" class="empty">提示：在上表点一条“编辑”可快速查看字段；也可先点任意行后再点这里按钮</div>

      <el-alert v-if="errorExtra" :title="errorExtra" type="error" show-icon class="mb" />

      <el-drawer v-model="simOpen" title="相似专利推荐" size="50%">
        <el-table v-loading="loadingExtra" :data="similar" stripe>
          <el-table-column prop="similarPatentId" label="相似ID" width="110" />
          <el-table-column prop="similarPatentTitle" label="标题" min-width="260" />
          <el-table-column prop="similarityScore" label="相似度" width="110" />
        </el-table>
      </el-drawer>

      <el-drawer v-model="riskOpen" title="侵权风险提示" size="50%">
        <div v-loading="loadingExtra">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="风险等级">{{ risk?.riskLevel ?? '-' }}</el-descriptions-item>
            <el-descriptions-item label="风险原因">{{ risk?.riskReason ?? '-' }}</el-descriptions-item>
            <el-descriptions-item label="建议">{{ risk?.suggestion ?? '-' }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </el-drawer>
    </el-card>
  </div>
</template>

<style scoped>
.wrap {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
}
.extra-title {
  font-size: 16px;
  font-weight: 900;
  color: #123b30;
}
.extra-sub {
  margin-top: 6px;
  font-size: 12px;
  color: #5c736b;
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
  color: #6c827a;
  font-size: 12px;
}
.mb {
  margin: 10px 0;
}
</style>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import CrudList from './CrudList.vue'
import type { Literature } from '@/api/literatures'
import { knowledgeApi } from '@/api/knowledge'
import { literatureCreate, literatureEdit } from './knowledgeFormSchemas'

const router = useRouter()

async function createLiterature(body: Record<string, unknown>, file?: File | null) {
  const b = { ...body } as Record<string, unknown>
  const py = b.publishYear
  if (py !== undefined && py !== null && py !== '') {
    const n = typeof py === 'string' ? parseInt(py, 10) : Number(py)
    b.publishYear = Number.isFinite(n) ? n : null
  } else {
    b.publishYear = null
  }
  await knowledgeApi.literatures.create(b as Literature, file)
}

async function downloadPdf(row: Record<string, unknown>) {
  const id = Number(row.id)
  if (!id) return
  if (!row.pdfPath) {
    ElMessage.warning('暂无 PDF')
    return
  }
  try {
    const blob = await knowledgeApi.literatures.downloadPdfBlob(id)
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `literature-${id}.pdf`
    a.click()
    URL.revokeObjectURL(url)
  } catch (e: any) {
    ElMessage.error(String(e?.message || e))
  }
}

const extraTileButtons = [
  {
    label: '下载',
    type: 'info' as const,
    plain: true,
    onClick: (row: Record<string, unknown>) => downloadPdf(row)
  }
]
</script>

<template>
  <div class="wrap kb-literatures">
    <CrudList
      title="文献库"
      placeholder="按关键词检索文献"
      :columns="[
        { prop: 'id', label: 'ID', width: 80 },
        { prop: 'title', label: '标题', minWidth: 260 },
        { prop: 'journalName', label: '期刊', minWidth: 160 },
        { prop: 'abstractContent', label: '文献摘要', minWidth: 200, maxDisplayLen: 120 }
      ]"
      :list="knowledgeApi.literatures.list"
      favoriteType="LITERATURE"
      :create="createLiterature"
      :update="knowledgeApi.literatures.update"
      :del="knowledgeApi.literatures.delete"
      :form-schema-create="literatureCreate"
      :form-schema-edit="literatureEdit"
      multipart-create
      multipart-edit
      :replace-file="knowledgeApi.literatures.replacePdf"
      :extra-tile-buttons="extraTileButtons"
    />
  </div>
</template>

<style scoped>
.wrap {
  display: grid;
  grid-template-columns: 1fr;
  gap: 14px;
}

.kb-literatures {
  padding-bottom: 8px;
}
</style>

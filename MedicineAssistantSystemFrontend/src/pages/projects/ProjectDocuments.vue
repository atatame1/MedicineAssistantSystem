<script setup lang="ts">
import { ref } from 'vue'
import { projectsExtraApi, type ProjectDocument, type ProjectDocumentUpload } from '@/api/projectsExtra'

const projectId = ref<number>(1)
const loading = ref(false)
const error = ref<string | null>(null)
const rows = ref<ProjectDocument[]>([])

const q = ref('')

const uploadOpen = ref(false)
const uploadFile = ref<File | null>(null)
const uploadMeta = ref<ProjectDocumentUpload>({ uploadUserId: 1, docType: 'GENERAL', summary: '' })
const uploading = ref(false)

async function load() {
  loading.value = true
  error.value = null
  try {
    rows.value = await projectsExtraApi.documents(projectId.value)
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    loading.value = false
  }
}

async function search() {
  loading.value = true
  error.value = null
  try {
    const qq = q.value.trim()
    rows.value = qq ? await projectsExtraApi.searchDocuments(projectId.value, qq) : await projectsExtraApi.documents(projectId.value)
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    loading.value = false
  }
}

function onPick(f: any) {
  uploadFile.value = f?.raw ?? null
}

async function doUpload() {
  if (!uploadFile.value) return
  uploading.value = true
  error.value = null
  try {
    const fd = new FormData()
    fd.append('file', uploadFile.value)
    const meta = new Blob([JSON.stringify(uploadMeta.value || {})], { type: 'application/json' })
    fd.append('metadata', meta)
    const res = await fetch(`/api/projects/${encodeURIComponent(String(projectId.value))}/documents/upload`, { method: 'POST', body: fd })
    if (!res.ok) throw new Error('upload failed')
    uploadOpen.value = false
    await load()
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    uploading.value = false
  }
}

function download(row: ProjectDocument) {
  const url = `/api/projects/${encodeURIComponent(String(projectId.value))}/documents/${encodeURIComponent(String(row.id))}/file`
  window.open(url, '_blank')
}

load()
</script>

<template>
  <div class="page">
    <div class="head">
      <div>
        <div class="t">文档管理</div>
        <div class="s">列表 / 检索 / 上传 / 下载</div>
      </div>
      <div class="act">
        <el-input-number v-model="projectId" :min="1" :step="1" style="width: 140px" />
        <el-input v-model="q" placeholder="全文检索（名称/摘要/正文）" clearable style="width: 260px" />
        <el-button :loading="loading" @click="search">查询</el-button>
        <el-button type="primary" @click="uploadOpen = true">上传</el-button>
      </div>
    </div>

    <el-alert v-if="error" :title="error" type="error" show-icon />

    <el-card>
      <el-table v-loading="loading" :data="rows" stripe>
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="docType" label="类型" width="120" />
        <el-table-column prop="docName" label="名称" min-width="240" />
        <el-table-column prop="originalFilename" label="原文件名" min-width="220" />
        <el-table-column prop="uploadTime" label="上传时间" width="180" />
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="download(row)">下载</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="uploadOpen" title="上传项目文档" width="780px">
      <el-form :model="uploadMeta" label-width="110px">
        <el-form-item label="文件">
          <el-upload :auto-upload="false" :limit="1" :on-change="onPick">
            <el-button type="primary">选择文件</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="上传用户ID">
          <el-input-number v-model="uploadMeta.uploadUserId" :min="1" />
        </el-form-item>
        <el-form-item label="docType">
          <el-input v-model="uploadMeta.docType" />
        </el-form-item>
        <el-form-item label="summary">
          <el-input v-model="uploadMeta.summary" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="uploadOpen = false">取消</el-button>
        <el-button type="primary" :loading="uploading" @click="doUpload">上传</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}
.t {
  font-size: 18px;
  font-weight: 900;
  color: #123b30;
}
.s {
  margin-top: 6px;
  font-size: 12px;
  color: #5c736b;
}
.act {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}
</style>

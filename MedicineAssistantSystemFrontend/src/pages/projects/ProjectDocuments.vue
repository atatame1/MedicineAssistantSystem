<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { listMyProjects, type MyProjectItem } from '@/api/projects'
import { projectsExtraApi, type ProjectDocument, type ProjectDocumentUpload } from '@/api/projectsExtra'
import { listUsers } from '@/api/user'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()

const projectId = ref<number | null>(null)
const projectOptions = ref<MyProjectItem[]>([])
const projectsLoading = ref(false)

const uploadProjectId = ref<number | null>(null)
const loading = ref(false)
const error = ref<string | null>(null)
const rows = ref<ProjectDocument[]>([])

const q = ref('')

const uploadOpen = ref(false)
const uploadFile = ref<File | null>(null)
const uploadMeta = ref<ProjectDocumentUpload>({ docType: 'GENERAL', summary: '' })
const uploading = ref(false)

const DOC_TYPES = [
  { value: 'GENERAL', label: '通用资料' },
  { value: 'PROJECT_REPORT', label: '项目报告' },
  { value: 'LITERATURE', label: '文献' },
  { value: 'PATENT', label: '专利' },
  { value: 'REGULATION', label: '法规' }
] as const

const projectsForSelect = computed(() =>
  projectOptions.value.filter((p): p is MyProjectItem & { id: number } => p.id != null)
)

const userNameById = ref<Record<number, string>>({})

function uploaderDisplay(uid: number | null | undefined) {
  if (uid == null) return '-'
  const n = userNameById.value[uid]
  return n != null && n !== '' ? n : `用户 ${uid}`
}

function docTypeLabel(v: string | null | undefined) {
  if (v == null || v === '') return '-'
  const hit = DOC_TYPES.find((d) => d.value === v)
  return hit ? hit.label : v
}

async function loadUserNames() {
  const uid = auth.user?.userId
  if (!uid) return
  try {
    const list = await listUsers()
    const r: Record<number, string> = {}
    for (const u of list) {
      r[u.id] = u.nickname?.trim() || u.username?.trim() || `用户 ${u.id}`
    }
    userNameById.value = r
  } catch {
    userNameById.value = {}
  }
}

async function loadProjectOptions() {
  const uid = auth.user?.userId
  if (!uid) {
    projectOptions.value = []
    projectId.value = null
    return
  }
  projectsLoading.value = true
  try {
    projectOptions.value = await listMyProjects(uid)
    if (projectId.value == null && projectOptions.value.length > 0) {
      const first = projectOptions.value[0]?.id
      projectId.value = first != null ? first : null
    }
  } catch {
    projectOptions.value = []
    projectId.value = null
  } finally {
    projectsLoading.value = false
  }
}

async function load() {
  if (projectId.value == null) {
    rows.value = []
    return
  }
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
  if (projectId.value == null) {
    rows.value = []
    return
  }
  loading.value = true
  error.value = null
  try {
    const qq = q.value.trim()
    rows.value = qq
      ? await projectsExtraApi.searchDocuments(projectId.value, qq)
      : await projectsExtraApi.documents(projectId.value)
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    loading.value = false
  }
}

function onPick(f: any) {
  uploadFile.value = f?.raw ?? null
}

function openUpload() {
  uploadProjectId.value = projectId.value
  uploadFile.value = null
  uploadMeta.value = { docType: 'GENERAL', summary: '' }
  uploadOpen.value = true
}

function uploadHeaders(): Record<string, string> {
  const h: Record<string, string> = {}
  try {
    const t = localStorage.getItem('mas_token')
    if (t) h.Authorization = `Bearer ${t}`
  } catch {}
  return h
}

async function doUpload() {
  if (!uploadFile.value || uploadProjectId.value == null) return
  const uid = auth.user?.userId
  if (!uid) {
    error.value = '未登录'
    return
  }
  uploading.value = true
  error.value = null
  try {
    const fd = new FormData()
    fd.append('file', uploadFile.value)
    const metaPayload: ProjectDocumentUpload = {
      ...uploadMeta.value,
      uploadUserId: uid
    }
    const meta = new Blob([JSON.stringify(metaPayload)], { type: 'application/json' })
    fd.append('metadata', meta)
    const res = await fetch(
      `/api/projects/${encodeURIComponent(String(uploadProjectId.value))}/documents/upload`,
      { method: 'POST', body: fd, headers: uploadHeaders() }
    )
    if (!res.ok) throw new Error('upload failed')
    uploadOpen.value = false
    if (projectId.value === uploadProjectId.value) {
      await load()
    }
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    uploading.value = false
  }
}

function download(row: ProjectDocument) {
  if (projectId.value == null) return
  const url = `/api/projects/${encodeURIComponent(String(projectId.value))}/documents/${encodeURIComponent(String(row.id))}/file`
  window.open(url, '_blank')
}

watch(projectId, () => {
  load()
})

onMounted(async () => {
  await Promise.all([loadProjectOptions(), loadUserNames()])
})
</script>

<template>
  <div class="pt-tool">
    <div class="head">
      <div>
        <div class="t">文档管理</div>
        <div class="s">列表 / 检索 / 上传 / 下载</div>
      </div>
      <div class="act">
        <el-select
          v-model="projectId"
          placeholder="选择项目"
          filterable
          clearable
          style="width: 220px"
          :loading="projectsLoading"
        >
          <el-option
            v-for="p in projectsForSelect"
            :key="p.id"
            :label="p.projectName"
            :value="p.id"
          />
        </el-select>
        <el-input v-model="q" placeholder="全文检索（名称/摘要/正文）" clearable style="width: 260px" />
        <el-button :loading="loading" @click="search">查询</el-button>
        <el-button type="primary" @click="openUpload">上传</el-button>
      </div>
    </div>

    <el-alert v-if="error" :title="error" type="error" show-icon />

    <div class="pt-panel">
      <el-table v-loading="loading" class="pt-table" :data="rows" stripe>
        <el-table-column label="类型" width="120">
          <template #default="{ row }">
            {{ docTypeLabel(row.docType) }}
          </template>
        </el-table-column>
        <el-table-column prop="originalFilename" label="文件名" min-width="220" />
        <el-table-column prop="summary" label="摘要" min-width="240" show-overflow-tooltip />
        <el-table-column label="上传者" width="140" show-overflow-tooltip>
          <template #default="{ row }">
            {{ uploaderDisplay(row.uploadUserId) }}
          </template>
        </el-table-column>
        <el-table-column prop="uploadTime" label="上传时间" width="180" />
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="download(row)">下载</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="uploadOpen" class="pt-tool-dialog" title="上传项目文档" width="780px">
      <el-form :model="uploadMeta" label-width="110px">
        <el-form-item label="项目">
          <el-select
            v-model="uploadProjectId"
            placeholder="选择项目"
            filterable
            style="width: 100%"
            :loading="projectsLoading"
          >
            <el-option
              v-for="p in projectsForSelect"
              :key="p.id"
              :label="p.projectName"
              :value="p.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="文件">
          <el-upload :auto-upload="false" :limit="1" :on-change="onPick">
            <el-button type="primary">选择文件</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="文档类型">
          <el-select v-model="uploadMeta.docType" placeholder="请选择" style="width: 100%">
            <el-option v-for="d in DOC_TYPES" :key="d.value" :label="d.label" :value="d.value" />
          </el-select>
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

<style src="./projectToolPage.css"></style>

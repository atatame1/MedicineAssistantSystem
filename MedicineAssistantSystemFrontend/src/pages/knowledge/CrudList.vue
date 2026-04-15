<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { addFavorite, listUserFavorites, removeFavorite } from '@/api/user'
import EntityFormFields from './EntityFormFields.vue'
import { defaultValue, normalizeFieldValue, type FormField } from './formSchemaTypes'

function formatDetailValue(f: FormField, row: Row | null): string {
  if (!row) return '—'
  const v = row[f.prop]
  if (v === null || v === undefined) return '—'
  if (typeof v === 'string' && v.trim() === '') return '—'
  const t = f.type || 'text'
  if (t === 'select' && f.options?.length) {
    const opt = f.options.find((o) => o.value === v || String(o.value) === String(v))
    if (opt) return String(opt.label)
  }
  if (t === 'switch') return v ? '是' : '否'
  if (typeof v === 'object') {
    try {
      return JSON.stringify(v)
    } catch {
      return '—'
    }
  }
  const s = String(v).trim()
  if (!s) return '—'
  if ((s.startsWith('[') || s.startsWith('{')) && s.length > 1) {
    try {
      return JSON.stringify(JSON.parse(s))
    } catch {
      /* keep raw */
    }
  }
  return s
}

type Row = Record<string, any>

const props = defineProps<{
  title: string
  placeholder: string
  columns: { prop: string; label: string; width?: number; minWidth?: number; maxDisplayLen?: number }[]
  list: (keyword?: string) => Promise<Row[]>
  create: (body: Row, file?: File | null) => Promise<void>
  update: (body: Row) => Promise<void>
  del: (id: number) => Promise<void>
  formSchema?: FormField[]
  formSchemaCreate?: FormField[]
  formSchemaEdit?: FormField[]
  favoriteType?: string
  multipartCreate?: boolean
  multipartEdit?: boolean
  replaceFile?: (id: number, file: File) => Promise<void>
  extraTileButtons?: {
    label: string
    type?: 'primary' | 'success' | 'warning' | 'info' | 'danger'
    plain?: boolean
    onClick: (row: Row) => void
  }[]
  onRowInteract?: (row: Row) => void
  detailInTileHeader?: boolean
}>()

const schemaCreate = computed<FormField[]>(() => props.formSchemaCreate ?? props.formSchema ?? [])
const schemaEdit = computed<FormField[]>(() => props.formSchemaEdit ?? props.formSchema ?? [])

const keyword = ref('')
const loading = ref(false)
const error = ref<string | null>(null)
const rows = ref<Row[]>([])
const auth = useAuthStore()
const favoriteIds = ref<Set<number>>(new Set())

const dialogOpen = ref(false)
const saving = ref(false)
const editing = ref<Row | null>(null)
const form = reactive<Row>({})
const createPdfRaw = ref<File | null>(null)
const createPdfList = ref<{ name?: string; raw?: File }[]>([])
const editPdfRaw = ref<File | null>(null)
const editPdfList = ref<{ name?: string; raw?: File }[]>([])

const detailOpen = ref(false)
const detailRow = ref<Row | null>(null)

const isEdit = computed(() => !!editing.value?.id)

const detailFields = computed(() => schemaEdit.value)

const activeSchema = computed<FormField[]>(() => (isEdit.value ? schemaEdit.value : schemaCreate.value))

const titleProp = computed(() => {
  const prefs = ['name', 'title', 'patentNumber']
  for (const p of prefs) {
    if (props.columns.some((c) => c.prop === p)) return p
  }
  return props.columns[1]?.prop ?? props.columns[0]?.prop ?? 'id'
})

const bodyColumns = computed(() =>
  props.columns.filter((c) => c.prop !== 'id' && c.prop !== titleProp.value)
)

function displayCell(row: Row, c: { prop: string; maxDisplayLen?: number }) {
  const v = row[c.prop]
  if (v == null || v === '') return '—'
  const s = String(v).replace(/\s+/g, ' ').trim()
  const max = c.maxDisplayLen
  if (max != null && max > 0 && s.length > max) {
    return s.slice(0, max) + '…'
  }
  return s
}

function openDetail(row: Row) {
  detailRow.value = row
  props.onRowInteract?.(row)
  detailOpen.value = true
}

async function load() {
  loading.value = true
  error.value = null
  try {
    rows.value = await props.list(keyword.value.trim() || undefined)
    if (auth.user?.userId && props.favoriteType) {
      const favs = await listUserFavorites(auth.user.userId)
      favoriteIds.value = new Set(
        favs
          .filter((f) => f.favoriteType === props.favoriteType && f.favoriteId != null)
          .map((f) => Number(f.favoriteId))
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

function onCreatePdfChange(uploadFile: { raw?: File } | null) {
  createPdfRaw.value = uploadFile?.raw ?? null
  createPdfList.value = uploadFile ? [uploadFile as { name?: string; raw?: File }] : []
}

function clearCreatePdf() {
  createPdfRaw.value = null
  createPdfList.value = []
}

function onEditPdfChange(uploadFile: { raw?: File } | null) {
  editPdfRaw.value = uploadFile?.raw ?? null
  editPdfList.value = uploadFile ? [uploadFile as { name?: string; raw?: File }] : []
}

function clearEditPdf() {
  editPdfRaw.value = null
  editPdfList.value = []
}

function openCreate() {
  editing.value = null
  createPdfRaw.value = null
  createPdfList.value = []
  Object.keys(form).forEach((k) => delete form[k])
  schemaCreate.value.forEach((f) => {
    form[f.prop] = defaultValue(f) as never
  })
  dialogOpen.value = true
}

function openEdit(row: Row) {
  editing.value = row
  clearEditPdf()
  Object.keys(form).forEach((k) => delete form[k])
  schemaEdit.value.forEach((f) => {
    form[f.prop] = normalizeFieldValue(row?.[f.prop], f) as never
  })
  form.id = row.id
  props.onRowInteract?.(row)
  dialogOpen.value = true
}

function buildPayload(isUpdate: boolean): Row {
  const keys = (isUpdate ? schemaEdit.value : schemaCreate.value).map((f) => f.prop)
  const body: Row = {}
  for (const k of keys) {
    const v = form[k]
    body[k] = v
  }
  if (isUpdate) body.id = form.id
  return body
}

async function save() {
  try {
    await ElMessageBox.confirm(
      isEdit.value ? '确定保存当前修改？' : '确定提交新增？',
      '确认',
      { type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消' }
    )
  } catch {
    return
  }
  saving.value = true
  error.value = null
  try {
    const body = buildPayload(isEdit.value)
    if (isEdit.value) {
      await props.update(body)
      if (props.multipartEdit && editPdfRaw.value && props.replaceFile) {
        await props.replaceFile(Number(form.id), editPdfRaw.value)
      }
    } else if (props.multipartCreate) await props.create(body, createPdfRaw.value ?? undefined)
    else await props.create(body)
    dialogOpen.value = false
    await load()
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    saving.value = false
  }
}

async function remove(row: Row) {
  try {
    await ElMessageBox.confirm('确定删除该条记录？删除后不可恢复。', '确认删除', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
  } catch {
    return
  }
  saving.value = true
  error.value = null
  try {
    await props.del(Number(row.id))
    await load()
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    saving.value = false
  }
}

function isFavorited(row: Row) {
  return favoriteIds.value.has(Number(row.id))
}

async function toggleFavorite(row: Row) {
  if (!auth.user?.userId || !props.favoriteType || !row?.id) {
    try {
      window.dispatchEvent(new CustomEvent('auth:required'))
    } catch {}
    return
  }
  const fav = isFavorited(row)
  try {
    await ElMessageBox.confirm(
      fav ? '确定取消收藏该条？' : '确定将该项加入收藏？',
      '确认',
      { type: 'info', confirmButtonText: '确定', cancelButtonText: '取消' }
    )
  } catch {
    return
  }
  saving.value = true
  error.value = null
  try {
    const id = Number(row.id)
    if (favoriteIds.value.has(id)) {
      await removeFavorite(auth.user.userId, { favoriteId: id, favoriteType: props.favoriteType })
      favoriteIds.value.delete(id)
    } else {
      await addFavorite(auth.user.userId, { favoriteId: id, favoriteType: props.favoriteType })
      favoriteIds.value.add(id)
    }
    favoriteIds.value = new Set(favoriteIds.value)
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    saving.value = false
  }
}

load()
</script>

<template>
  <div class="kb-page">
    <div class="kb-root">
      <header class="kb-head">
        <div class="kb-title-block">
          <div class="t">{{ title }}</div>
          <div class="s">卡片一览 · 关键字段直接可见</div>
        </div>
        <div class="kb-toolbar">
          <el-input v-model="keyword" :placeholder="placeholder" clearable class="kb-search" />
          <el-button type="primary" class="btn-go" :loading="loading" @click="load">检索</el-button>
          <el-button class="btn-secondary" @click="openCreate">新增</el-button>
        </div>
      </header>

      <el-alert v-if="error" :title="error" type="error" show-icon class="kb-alert" />

      <div class="kb-grid-wrap mas-scrollbar" v-loading="loading">
        <div v-if="rows.length" class="kb-grid">
          <article v-for="row in rows" :key="row.id" class="kb-tile">
            <div class="kb-tile-accent" aria-hidden="true" />
            <div class="kb-tile-hd">
              <div class="kb-tile-topline">
                <span class="kb-tile-id">#{{ row.id }}</span>
                <button
                  v-if="favoriteType"
                  type="button"
                  class="kb-fav"
                  :class="{ active: isFavorited(row) }"
                  :disabled="saving"
                  :title="isFavorited(row) ? '取消收藏' : '加入收藏'"
                  @click="toggleFavorite(row)"
                >
                  {{ isFavorited(row) ? '★' : '☆' }}
                </button>
              </div>
              <div class="kb-tile-title-row">
                <h3 class="kb-tile-name">{{ row[titleProp] ?? '—' }}</h3>
                <el-button
                  v-if="detailInTileHeader"
                  link
                  type="primary"
                  size="small"
                  class="kb-tile-detail-hd"
                  :disabled="saving"
                  @click.stop="openDetail(row)"
                >
                  详细
                </el-button>
              </div>
            </div>
            <div class="kb-tile-body">
              <div v-for="c in bodyColumns" :key="c.prop" class="kb-kv">
                <span class="kb-k">{{ c.label }}</span>
                <p class="kb-v">{{ displayCell(row, c) }}</p>
              </div>
            </div>
            <div class="kb-tile-ft">
              <el-button size="small" :disabled="saving" @click="openDetail(row)">详细</el-button>
              <el-button type="primary" class="btn-go" size="small" :disabled="saving" @click="openEdit(row)">
                编辑
              </el-button>
              <el-button
                v-for="(b, bi) in extraTileButtons || []"
                :key="bi"
                size="small"
                :type="b.type"
                :plain="b.plain"
                :disabled="saving"
                @click="b.onClick(row)"
              >
                {{ b.label }}
              </el-button>
              <el-button type="danger" plain size="small" :disabled="saving" @click="remove(row)">删除</el-button>
            </div>
          </article>
        </div>
        <div v-else-if="!loading" class="kb-empty">暂无数据，可尝试检索或新增</div>
      </div>
    </div>

    <el-dialog
      v-model="dialogOpen"
      :title="isEdit ? '编辑' : '新增'"
      :width="isEdit ? 'min(960px, 94vw)' : '720px'"
      class="kb-dialog"
    >
      <div class="kb-dialog-body mas-scrollbar">
        <el-form :model="form" label-width="128px">
          <el-form-item v-if="multipartCreate && !isEdit" label="PDF">
            <el-upload
              :auto-upload="false"
              :limit="1"
              :file-list="createPdfList as any"
              :on-change="onCreatePdfChange"
              :on-remove="clearCreatePdf"
              accept="application/pdf,.pdf"
              list-type="text"
            >
              <el-button type="primary">选择 PDF</el-button>
            </el-upload>
          </el-form-item>
          <el-form-item v-if="multipartEdit && isEdit" label="替换PDF">
            <el-upload
              :auto-upload="false"
              :limit="1"
              :file-list="editPdfList as any"
              :on-change="onEditPdfChange"
              :on-remove="clearEditPdf"
              accept="application/pdf,.pdf"
              list-type="text"
            >
              <el-button>选择新 PDF</el-button>
            </el-upload>
            <div class="kb-hint">留空则保留原文件；选择新文件将删除旧 PDF 并上传</div>
          </el-form-item>
          <EntityFormFields :fields="activeSchema" :model="form" />
        </el-form>
      </div>
      <template #footer>
        <el-button @click="dialogOpen = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="detailOpen"
      title="详情"
      width="min(960px, 94vw)"
      class="kb-dialog"
      destroy-on-close
    >
      <div class="kb-dialog-body mas-scrollbar">
        <el-descriptions
          v-if="detailFields.length"
          :column="1"
          border
          class="kb-detail-desc"
        >
          <el-descriptions-item v-if="multipartEdit && detailRow" label="PDF">
            <span class="kb-detail-val">{{ detailRow.pdfPath ? '已上传 PDF' : '无 PDF' }}</span>
          </el-descriptions-item>
          <el-descriptions-item v-for="f in detailFields" :key="f.prop" :label="f.label">
            <span class="kb-detail-val">{{ formatDetailValue(f, detailRow) }}</span>
          </el-descriptions-item>
        </el-descriptions>
        <div v-else class="kb-empty">暂无字段配置</div>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
.kb-dialog-body {
  max-height: min(78vh, 720px);
  overflow: auto;
  padding-right: 4px;
}

.kb-hint {
  margin-top: 6px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.45);
  line-height: 1.4;
}

.kb-page {
  padding-bottom: 100px;
  box-sizing: border-box;
  min-height: 0;
}

.kb-root {
  padding: 0 2px;
  background: transparent;
}

.kb-head {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-bottom: 16px;
  padding-bottom: 14px;
  border-bottom: 1px solid rgba(115, 209, 180, 0.18);
}

.kb-title-block .t {
  font-size: 20px;
  font-weight: 900;
  color: rgba(233, 244, 239, 0.96);
  letter-spacing: 0.02em;
}

.kb-title-block .s {
  margin-top: 6px;
  font-size: 14px;
  color: rgba(189, 212, 204, 0.72);
}

.kb-toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
}

.kb-search {
  flex: 1;
  min-width: 200px;
  max-width: 100%;
}

.btn-go {
  border: 0;
  background: linear-gradient(145deg, #73d1b4, #3d8f75);
  color: #0a221f;
  font-weight: 800;
}

.btn-secondary {
  border: 1px solid rgba(255, 255, 255, 0.22);
  background: rgba(14, 42, 36, 0.35);
  color: #e9f4ef;
  font-weight: 700;
}

.kb-alert {
  margin-bottom: 12px;
}

.kb-grid-wrap {
  max-height: min(74vh, calc(100vh - 220px));
  overflow: auto;
  padding-bottom: 4px;
}

.kb-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 14px;
  align-items: stretch;
}

.kb-tile {
  position: relative;
  display: flex;
  flex-direction: column;
  border-radius: 16px;
  border: 1px solid rgba(115, 209, 180, 0.2);
  background: linear-gradient(160deg, rgba(14, 42, 36, 0.55), rgba(8, 28, 24, 0.42));
  box-shadow: 0 10px 32px rgba(0, 0, 0, 0.2);
  overflow: hidden;
  min-height: 0;
}

.kb-tile-accent {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: linear-gradient(180deg, #c8a967, #73d1b4);
  opacity: 0.9;
}

.kb-tile-hd {
  padding: 14px 14px 10px 18px;
  border-bottom: 1px solid rgba(115, 209, 180, 0.12);
}

.kb-tile-topline {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.kb-tile-id {
  display: inline-block;
  font-size: 13px;
  font-weight: 800;
  color: rgba(200, 169, 103, 0.95);
  letter-spacing: 0.06em;
  margin-bottom: 6px;
}

.kb-fav {
  border: 0;
  background: transparent;
  color: rgba(200, 169, 103, 0.86);
  font-size: 18px;
  line-height: 1;
  cursor: pointer;
  padding: 0;
}

.kb-fav.active {
  color: #f4d27e;
}

.kb-tile-title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 10px;
}

.kb-tile-title-row .kb-tile-name {
  flex: 1;
  min-width: 0;
}

.kb-tile-detail-hd {
  flex-shrink: 0;
  font-weight: 700;
}

.kb-tile-name {
  margin: 0;
  font-size: 17px;
  font-weight: 900;
  line-height: 1.35;
  color: rgba(233, 244, 239, 0.98);
}

.kb-tile-body {
  flex: 1;
  padding: 12px 14px 12px 18px;
  min-height: 0;
}

.kb-kv + .kb-kv {
  margin-top: 10px;
}

.kb-k {
  display: block;
  font-size: 13px;
  font-weight: 800;
  color: rgba(158, 228, 207, 0.78);
  margin-bottom: 4px;
  letter-spacing: 0.03em;
}

.kb-v {
  margin: 0;
  font-size: 15px;
  line-height: 1.55;
  color: rgba(233, 244, 239, 0.88);
  white-space: pre-wrap;
  word-break: break-word;
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.kb-tile-ft {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 10px 14px 12px 18px;
  border-top: 1px solid rgba(115, 209, 180, 0.12);
  background: rgba(0, 0, 0, 0.12);
}

.kb-detail-desc {
  --el-descriptions-item-bordered-label-background: #f5f7fa;
}

.kb-detail-desc :deep(.el-descriptions__label) {
  width: min(32%, 200px);
  background: #f5f7fa !important;
  color: #303133;
  font-weight: 600;
}

.kb-detail-desc :deep(.el-descriptions__content) {
  background: #fff !important;
  color: #303133;
}

.kb-detail-val {
  display: block;
  white-space: pre-wrap;
  word-break: break-word;
  font-size: 16px;
  line-height: 1.55;
}

.kb-empty {
  padding: 40px 16px;
  text-align: center;
  font-size: 16px;
  color: rgba(255, 255, 255, 0.42);
}

:deep(.kb-search .el-input__wrapper) {
  background: rgba(8, 28, 24, 0.45);
  border: 1px solid rgba(115, 209, 180, 0.22);
  box-shadow: none;
}

:deep(.kb-search .el-input__inner) {
  color: #e9f4ef;
}

:deep(.kb-search .el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.35);
}

@media (min-width: 900px) {
  .kb-head {
    flex-direction: row;
    align-items: flex-end;
    justify-content: space-between;
  }

  .kb-toolbar {
    flex: 1;
    justify-content: flex-end;
    max-width: min(720px, 55vw);
  }
}
</style>

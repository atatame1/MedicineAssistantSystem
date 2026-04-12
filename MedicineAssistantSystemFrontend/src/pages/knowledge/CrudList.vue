<script setup lang="ts">
import { computed, reactive, ref } from 'vue'

type Row = Record<string, any>

const props = defineProps<{
  title: string
  placeholder: string
  columns: { prop: string; label: string; width?: number; minWidth?: number }[]
  list: (keyword?: string) => Promise<Row[]>
  create: (body: Row) => Promise<void>
  update: (body: Row) => Promise<void>
  del: (id: number) => Promise<void>
  formSchema: { prop: string; label: string; type?: 'text' | 'textarea' }[]
}>()

const keyword = ref('')
const loading = ref(false)
const error = ref<string | null>(null)
const rows = ref<Row[]>([])

const dialogOpen = ref(false)
const saving = ref(false)
const editing = ref<Row | null>(null)
const form = reactive<Row>({})

const isEdit = computed(() => !!editing.value?.id)

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

async function load() {
  loading.value = true
  error.value = null
  try {
    rows.value = await props.list(keyword.value.trim() || undefined)
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editing.value = null
  Object.keys(form).forEach((k) => delete form[k])
  props.formSchema.forEach((f) => (form[f.prop] = ''))
  dialogOpen.value = true
}

function openEdit(row: Row) {
  editing.value = row
  Object.keys(form).forEach((k) => delete form[k])
  props.formSchema.forEach((f) => (form[f.prop] = row?.[f.prop] ?? ''))
  form.id = row.id
  dialogOpen.value = true
}

async function save() {
  saving.value = true
  error.value = null
  try {
    const body: Row = { ...form }
    if (isEdit.value) await props.update(body)
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
              <span class="kb-tile-id">#{{ row.id }}</span>
              <h3 class="kb-tile-name">{{ row[titleProp] ?? '—' }}</h3>
            </div>
            <div class="kb-tile-body">
              <div v-for="c in bodyColumns" :key="c.prop" class="kb-kv">
                <span class="kb-k">{{ c.label }}</span>
                <p class="kb-v">{{ row[c.prop] ?? '—' }}</p>
              </div>
            </div>
            <div class="kb-tile-ft">
              <el-button type="primary" class="btn-go" size="small" @click="openEdit(row)">编辑</el-button>
              <el-button type="danger" plain size="small" :disabled="saving" @click="remove(row)">删除</el-button>
            </div>
          </article>
        </div>
        <div v-else-if="!loading" class="kb-empty">暂无数据，可尝试检索或新增</div>
      </div>
    </div>

    <el-dialog v-model="dialogOpen" :title="isEdit ? '编辑' : '新增'" width="720px" class="kb-dialog">
      <el-form :model="form" label-width="110px">
        <el-form-item v-for="f in formSchema" :key="f.prop" :label="f.label">
          <el-input v-if="(f.type || 'text') === 'text'" v-model="form[f.prop]" />
          <el-input v-else v-model="form[f.prop]" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogOpen = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
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
  font-size: 12px;
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

.kb-tile-id {
  display: inline-block;
  font-size: 11px;
  font-weight: 800;
  color: rgba(200, 169, 103, 0.95);
  letter-spacing: 0.06em;
  margin-bottom: 6px;
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
  font-size: 11px;
  font-weight: 800;
  color: rgba(158, 228, 207, 0.78);
  margin-bottom: 4px;
  letter-spacing: 0.03em;
}

.kb-v {
  margin: 0;
  font-size: 13px;
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
  gap: 8px;
  padding: 10px 14px 12px 18px;
  border-top: 1px solid rgba(115, 209, 180, 0.12);
  background: rgba(0, 0, 0, 0.12);
}

.kb-empty {
  padding: 40px 16px;
  text-align: center;
  font-size: 14px;
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

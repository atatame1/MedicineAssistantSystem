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
  Object.keys(form).forEach(k => delete form[k])
  props.formSchema.forEach(f => (form[f.prop] = ''))
  dialogOpen.value = true
}

function openEdit(row: Row) {
  editing.value = row
  Object.keys(form).forEach(k => delete form[k])
  props.formSchema.forEach(f => (form[f.prop] = row?.[f.prop] ?? ''))
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
  <div class="page">
    <div class="head">
      <div>
        <div class="t">{{ title }}</div>
        <div class="s">知识中心 · 结构化数据</div>
      </div>
      <div class="act">
        <el-input v-model="keyword" :placeholder="placeholder" clearable class="kw" />
        <el-button type="primary" :loading="loading" @click="load">检索</el-button>
        <el-button @click="openCreate">新增</el-button>
      </div>
    </div>

    <el-alert v-if="error" :title="error" type="error" show-icon class="mb" />

    <el-table v-loading="loading" :data="rows" stripe>
      <el-table-column v-for="c in columns" :key="c.prop" :prop="c.prop" :label="c.label" :width="c.width" :min-width="c.minWidth" />
      <el-table-column label="操作" width="160" align="center">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link type="danger" :disabled="saving" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogOpen" :title="isEdit ? '编辑' : '新增'" width="720px">
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
  align-items: center;
  gap: 10px;
}
.kw {
  width: 320px;
}
.mb {
  margin-bottom: 8px;
}
</style>

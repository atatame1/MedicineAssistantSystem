<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { createLiterature, type Literature } from '@/api/literatures'

const router = useRouter()
const loading = ref(false)
const error = ref<string | null>(null)
const file = ref<File | null>(null)
const fileList = ref<any[]>([])

function handleFileChange(uploadFile: any) {
  file.value = uploadFile?.raw ?? null
  fileList.value = uploadFile ? [uploadFile] : []
}

const form = reactive<Literature>({
  title: '',
  researchType: null,
  methodology: null,
  conclusion: null,
  innovations: null,
  keywords: null,
  abstractContent: null
})

async function submit() {
  loading.value = true
  error.value = null
  try {
    if (!file.value) throw new Error('请选择文件')
    if (!form.title || !form.title.trim()) throw new Error('title必填')
    await createLiterature(form, file.value)
    router.push('/literatures')
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div>
    <div style="display: flex; gap: 12px; margin-bottom: 12px">
      <router-link to="/literatures">返回</router-link>
    </div>

    <el-alert v-if="error" type="error" show-icon :title="error" style="margin-bottom: 12px" />

    <el-form :model="form" label-width="120px" @submit.prevent>
      <el-form-item label="文件">
        <el-upload
          :auto-upload="false"
          :limit="1"
          :file-list="fileList"
          :on-change="handleFileChange"
          accept="application/pdf,.pdf"
          list-type="text"
        >
          <el-button type="primary">选择 PDF</el-button>
        </el-upload>
      </el-form-item>
      <el-form-item label="标题">
        <el-input v-model="form.title" placeholder="必填" />
      </el-form-item>
      <el-form-item label="研究类型">
        <el-input v-model="form.researchType" />
      </el-form-item>
      <el-form-item label="研究方法">
        <el-input v-model="form.methodology" type="textarea" rows="3" />
      </el-form-item>
      <el-form-item label="主要发现">
        <el-input v-model="form.mainFindings" type="textarea" rows="3" />
      </el-form-item>
      <el-form-item label="结论">
        <el-input v-model="form.conclusion" type="textarea" rows="3" />
      </el-form-item>
      <el-form-item label="创新点">
        <el-input v-model="form.innovations" type="textarea" rows="3" />
      </el-form-item>
      <el-form-item label="关键词">
        <el-input v-model="form.keywords" />
      </el-form-item>
      <el-form-item label="摘要">
        <el-input v-model="form.abstractContent" type="textarea" rows="3" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="submit">提交</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped></style>


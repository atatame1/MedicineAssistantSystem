<script setup lang="ts">
import { ref } from 'vue'
import { projectsExtraApi, type DraftEvaluateReq } from '@/api/projectsExtra'
import { aiBoldToHtml } from '@/utils/aiBoldHtml'

const form = ref<DraftEvaluateReq>({
  projectName: '',
  herbName: '',
  formulaName: '',
  indication: '',
  description: '',
  budget: null,
  priority: ''
})

const running = ref(false)
const error = ref<string | null>(null)
const chunks = ref<string[]>([])

let aborter: AbortController | null = null

async function start() {
  stop()
  running.value = true
  error.value = null
  chunks.value = []
  aborter = new AbortController()
  try {
    const res = await projectsExtraApi.postDraftEvaluateStream(form.value)
    const reader = res.body?.getReader()
    if (!reader) throw new Error('no stream')
    const dec = new TextDecoder('utf-8')
    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      const txt = dec.decode(value, { stream: true })
      chunks.value.push(txt)
    }
  } catch (e: any) {
    if (String(e?.name) !== 'AbortError') error.value = String(e?.message || e)
  } finally {
    running.value = false
    aborter = null
  }
}

function stop() {
  try {
    aborter?.abort()
  } catch {}
  aborter = null
  running.value = false
}
</script>

<template>
  <div class="pt-tool">
    <div class="head">
      <div>
        <div class="t">立项评估（流式）</div>
        <div class="s"></div>
      </div>
      <div class="act">
        <el-button type="primary" :disabled="running" @click="start">开始</el-button>
        <el-button :disabled="!running" @click="stop">停止</el-button>
      </div>
    </div>

    <el-alert v-if="error" :title="error" type="error" show-icon />

    <div class="pt-panel">
      <el-form :model="form" label-width="110px">
        <el-form-item label="项目名称"><el-input v-model="form.projectName" /></el-form-item>
        <el-form-item label="药材"><el-input v-model="form.herbName" /></el-form-item>
        <el-form-item label="方剂"><el-input v-model="form.formulaName" /></el-form-item>
        <el-form-item label="适应症"><el-input v-model="form.indication" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="预算"><el-input-number v-model="form.budget" :min="0" /></el-form-item>
        <el-form-item label="优先级"><el-input v-model="form.priority" /></el-form-item>
      </el-form>
    </div>

    <div class="pt-panel">
      <div class="ot">流式输出</div>
      <div class="ai-rich-body ai-stream-out" v-html="aiBoldToHtml(chunks.join(''))" />
    </div>
  </div>
</template>

<style src="./projectToolPage.css"></style>


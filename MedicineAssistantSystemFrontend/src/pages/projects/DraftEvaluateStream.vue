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
  <div class="pt-tool draft-eval">
    <el-alert v-if="error" :title="error" type="error" show-icon />

    <div class="pt-panel de-panel">
      <div class="de-toolbar">
        <div class="t">立项评估（流式）</div>
        <div class="act">
          <el-button type="primary" :disabled="running" @click="start">开始</el-button>
          <el-button :disabled="!running" @click="stop">停止</el-button>
        </div>
      </div>
      <el-form :model="form" label-width="92px" class="de-form">
        <div class="de-grid">
          <el-form-item label="项目名称"><el-input v-model="form.projectName" /></el-form-item>
          <el-form-item label="药材"><el-input v-model="form.herbName" /></el-form-item>
          <el-form-item label="方剂"><el-input v-model="form.formulaName" /></el-form-item>
          <el-form-item label="适应症"><el-input v-model="form.indication" /></el-form-item>
          <el-form-item label="描述" class="de-full">
            <el-input v-model="form.description" type="textarea" :rows="3" />
          </el-form-item>
          <el-form-item label="预算"><el-input-number v-model="form.budget" :min="0" controls-position="right" /></el-form-item>
          <el-form-item label="优先级"><el-input v-model="form.priority" /></el-form-item>
        </div>
      </el-form>
    </div>

    <div class="pt-panel de-out-panel">
      <div class="ot">流式输出</div>
      <div class="ai-rich-body ai-stream-out" v-html="aiBoldToHtml(chunks.join(''))" />
    </div>
  </div>
</template>

<style src="./projectToolPage.css"></style>
<style scoped>
.de-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  margin: -4px 0 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.de-panel {
  padding: 12px 14px;
}

.de-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 4px 18px;
}

.de-grid :deep(.el-form-item) {
  margin-bottom: 8px;
}

.de-grid :deep(.de-full) {
  grid-column: 1 / -1;
}

.de-grid :deep(.el-input-number) {
  width: 100%;
}

.de-out-panel {
  padding: 12px 14px;
}

.de-out-panel .ot {
  margin-bottom: 8px;
}

@media (max-width: 720px) {
  .de-grid {
    grid-template-columns: 1fr;
  }
}
</style>


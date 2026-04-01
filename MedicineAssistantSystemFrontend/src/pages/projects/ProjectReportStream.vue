<script setup lang="ts">
import { ref } from 'vue'
import { projectsExtraApi } from '@/api/projectsExtra'

const projectId = ref<number>(1)
const input = ref<string>('')
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
    const res = await projectsExtraApi.postReportStream(projectId.value, { input: input.value || '' })
    const reader = res.body?.getReader()
    if (!reader) throw new Error('no stream')
    const dec = new TextDecoder('utf-8')
    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      chunks.value.push(dec.decode(value, { stream: true }))
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
  <div class="page">
    <div class="head">
      <div>
        <div class="t">项目报告生成（流式）</div>
        <div class="s">POST `/api/projects/{projectId}/ai/report-stream`</div>
      </div>
      <div class="act">
        <el-input-number v-model="projectId" :min="1" :step="1" style="width: 140px" />
        <el-button type="primary" :disabled="running" @click="start">开始</el-button>
        <el-button :disabled="!running" @click="stop">停止</el-button>
      </div>
    </div>

    <el-alert v-if="error" :title="error" type="error" show-icon />

    <el-card>
      <el-form label-width="110px">
        <el-form-item label="补充说明">
          <el-input v-model="input" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <div class="ot">流式输出</div>
      <el-input :model-value="chunks.join('')" type="textarea" :rows="14" readonly />
    </el-card>
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
.ot {
  font-weight: 900;
  color: #123b30;
  margin-bottom: 8px;
}
</style>


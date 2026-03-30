<script setup lang="ts">
import { computed, onBeforeUnmount, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const literatureId = computed(() => Number(route.params.literatureId))

const API_BASE_URL =
  (import.meta as any).env?.VITE_API_BASE_URL || 'http://localhost:8080'

const chunks = ref<string[]>([])
const finalSummary = ref<any>(null)
const error = ref<string | null>(null)
const running = ref(false)

let es: EventSource | null = null

function stop() {
  try {
    es?.close()
  } catch {}
  es = null
  running.value = false
}

function start() {
  if (!literatureId.value) return
  stop()
  chunks.value = []
  finalSummary.value = null
  error.value = null

  const url =
    API_BASE_URL + '/api/literatures/summary?literatureId=' + encodeURIComponent(String(literatureId.value))

  running.value = true
  es = new EventSource(url)
  es.onmessage = (ev) => {
    const data = String(ev.data ?? '')
    chunks.value.push(data)
    if (data.trim().startsWith('{')) {
      try {
        const obj = JSON.parse(data)
        if (obj && typeof obj === 'object' && obj.id != null) {
          finalSummary.value = obj
          stop()
        }
      } catch {}
    }
  }
  es.onerror = () => {
    error.value = 'SSE连接失败'
    stop()
  }
}

onBeforeUnmount(() => stop())
</script>

<template>
  <div>
    <div style="display: flex; gap: 12px; margin-bottom: 12px; align-items: center">
      <router-link to="/literatures">返回</router-link>
      <el-button type="primary" @click="start" :disabled="running">开始</el-button>
      <el-button @click="stop" :disabled="!running">停止</el-button>
    </div>

    <div style="margin-bottom: 12px">
      文献ID：{{ literatureId }}
    </div>

    <el-alert v-if="error" type="error" show-icon :title="error" style="margin-bottom: 12px" />

    <div v-if="running" style="margin-bottom: 12px">流式输出中...</div>

    <div style="margin-top: 12px">
      <div style="font-weight: 600; margin-bottom: 8px">流式内容</div>
      <el-input
        :model-value="chunks.join('')"
        type="textarea"
        :rows="10"
        readonly
      />
    </div>

    <div v-if="finalSummary" style="margin-top: 16px">
      <div style="font-weight: 600; margin-bottom: 8px">最终结果</div>
      <el-input
        :model-value="JSON.stringify(finalSummary, null, 2)"
        type="textarea"
        :rows="8"
        readonly
      />
    </div>
  </div>
</template>

<style scoped></style>


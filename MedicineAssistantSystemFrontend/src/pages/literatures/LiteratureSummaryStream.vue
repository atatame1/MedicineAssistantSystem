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
  <div class="page-wrap">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>文献摘要流式生成</span>
          <el-button text @click="$router.push('/literatures')">返回列表</el-button>
        </div>
      </template>

      <div class="toolbar">
        <el-tag effect="dark" type="success">文献ID：{{ literatureId }}</el-tag>
        <el-button type="primary" @click="start" :disabled="running">开始</el-button>
        <el-button @click="stop" :disabled="!running">停止</el-button>
        <el-tag v-if="running" type="warning">流式输出中...</el-tag>
      </div>

      <el-alert v-if="error" type="error" show-icon :title="error" class="mb-12" />

      <div class="panel-title">流式内容</div>
      <el-input :model-value="chunks.join('')" type="textarea" :rows="12" readonly />

      <div v-if="finalSummary" class="result-wrap">
        <div class="panel-title">最终结果</div>
        <el-input :model-value="JSON.stringify(finalSummary, null, 2)" type="textarea" :rows="10" readonly />
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.page-wrap {
  max-width: 980px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.toolbar {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.panel-title {
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 8px;
  color: #26493d;
}

.result-wrap {
  margin-top: 16px;
}

.mb-12 {
  margin-bottom: 12px;
}
</style>


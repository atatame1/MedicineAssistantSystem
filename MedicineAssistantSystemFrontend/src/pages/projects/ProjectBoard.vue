<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { projectsExtraApi, type ProjectBoard } from '@/api/projectsExtra'

const projectId = ref<number>(1)
const loading = ref(false)
const error = ref<string | null>(null)
const board = ref<ProjectBoard | null>(null)

function phaseText(v?: string | null) {
  const m: Record<string, string> = {
    EXPLORATION: '探索期',
    VERIFICATION: '验证期',
    OPTIMIZATION: '优化期',
    DECLARATION: '申报期'
  }
  if (!v) return '未设置'
  return m[v] || v
}

function statusText(v?: number | null) {
  const m: Record<number, string> = { 1: '立项中', 2: '进行中', 3: '已完成', 4: '已取消' }
  if (v == null) return '-'
  return m[v] || `状态${v}`
}

async function load() {
  loading.value = true
  error.value = null
  try {
    board.value = await projectsExtraApi.board(projectId.value)
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="pt-tool">
    <div class="head">
      <div>
        <div class="t">项目看板</div>
        <div class="s">按项目ID拉取：阶段 / 状态 / 进度</div>
      </div>
      <div class="act">
        <el-input-number v-model="projectId" :min="1" :step="1" style="width: 140px" />
        <el-button type="primary" :loading="loading" @click="load">加载</el-button>
      </div>
    </div>

    <el-alert v-if="error" :title="error" type="error" show-icon />

    <div v-loading="loading" class="pt-panel">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="项目ID">{{ board?.projectId ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="阶段">{{ phaseText(board?.currentPhase) }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ statusText(board?.status) }}</el-descriptions-item>
        <el-descriptions-item label="进度(%)">{{ board?.progressPercent ?? '-' }}</el-descriptions-item>
      </el-descriptions>
    </div>
  </div>
</template>

<style src="./projectToolPage.css"></style>

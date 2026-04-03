<script setup lang="ts">
import { computed, ref } from 'vue'
import { projectsExtraApi, type ProjectDecision } from '@/api/projectsExtra'

const projectId = ref<number>(1)
const loading = ref(false)
const error = ref<string | null>(null)
const rows = ref<ProjectDecision[]>([])

const createOpen = ref(false)
const saving = ref(false)
const createForm = ref<ProjectDecision>({
  decisionType: 'FORMULATION',
  title: '',
  content: '',
  aiRecommendation: '',
  expertConclusion: '',
  projectorId: 1,
  version: 1
})

const compareId1 = ref<number | null>(null)
const compareId2 = ref<number | null>(null)
const compareOpen = ref(false)
const compareLoading = ref(false)
const compareError = ref<string | null>(null)
const compareResult = ref<any>(null)

const canCompare = computed(() => !!compareId1.value && !!compareId2.value && compareId1.value !== compareId2.value)

async function load() {
  loading.value = true
  error.value = null
  try {
    rows.value = await projectsExtraApi.decisions(projectId.value)
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    loading.value = false
  }
}

async function createOne() {
  saving.value = true
  error.value = null
  try {
    await projectsExtraApi.createDecision(projectId.value, createForm.value)
    createOpen.value = false
    await load()
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    saving.value = false
  }
}

async function openCompare() {
  if (!canCompare.value) return
  compareOpen.value = true
  compareLoading.value = true
  compareError.value = null
  compareResult.value = null
  try {
    compareResult.value = await projectsExtraApi.compareDecisions(
      projectId.value,
      Number(compareId1.value),
      Number(compareId2.value)
    )
  } catch (e: any) {
    compareError.value = String(e?.message || e)
  } finally {
    compareLoading.value = false
  }
}

load()
</script>

<template>
  <div class="pt-tool">
    <div class="head">
      <div>
        <div class="t">决策记录</div>
        <div class="s">列表 / 新增 / 版本对比</div>
      </div>
      <div class="act">
        <el-input-number v-model="projectId" :min="1" :step="1" style="width: 140px" />
        <el-button :loading="loading" @click="load">加载</el-button>
        <el-button type="primary" @click="createOpen = true">新增</el-button>
      </div>
    </div>

    <el-alert v-if="error" :title="error" type="error" show-icon />

    <div class="pt-panel">
      <div class="compare">
        <el-input-number v-model="compareId1" :min="1" :step="1" placeholder="版本ID1" style="width: 160px" />
        <el-input-number v-model="compareId2" :min="1" :step="1" placeholder="版本ID2" style="width: 160px" />
        <el-button :disabled="!canCompare" @click="openCompare">对比</el-button>
      </div>

      <el-table v-loading="loading" class="pt-table" :data="rows" stripe>
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="decisionType" label="类型" width="140" />
        <el-table-column prop="title" label="标题" min-width="220" />
        <el-table-column prop="version" label="版本" width="90" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
      </el-table>
    </div>

    <el-dialog v-model="createOpen" class="pt-tool-dialog" title="新增决策记录" width="860px">
      <el-form :model="createForm" label-width="110px">
        <el-form-item label="类型">
          <el-select v-model="createForm.decisionType">
            <el-option label="配方决策" value="FORMULATION" />
            <el-option label="实验决策" value="EXPERIMENT" />
            <el-option label="资源决策" value="RESOURCE" />
            <el-option label="风险控制" value="RISK" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="createForm.title" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="createForm.content" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="AI建议">
          <el-input v-model="createForm.aiRecommendation" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="专家结论">
          <el-input v-model="createForm.expertConclusion" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="决策人ID">
          <el-input-number v-model="createForm.projectorId" :min="1" />
        </el-form-item>
        <el-form-item label="版本号">
          <el-input-number v-model="createForm.version" :min="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createOpen = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="createOne">提交</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="compareOpen" class="pt-tool-drawer" title="版本对比" size="60%">
      <el-alert v-if="compareError" :title="compareError" type="error" show-icon class="mb" />
      <div v-loading="compareLoading">
        <el-row :gutter="12">
          <el-col :span="12">
            <div class="pt-panel">
              <div class="ct">版本A</div>
              <el-input :model-value="JSON.stringify(compareResult?.first || null, null, 2)" type="textarea" :rows="18" readonly />
            </div>
          </el-col>
          <el-col :span="12">
            <div class="pt-panel">
              <div class="ct">版本B</div>
              <el-input :model-value="JSON.stringify(compareResult?.second || null, null, 2)" type="textarea" :rows="18" readonly />
            </div>
          </el-col>
        </el-row>
      </div>
    </el-drawer>
  </div>
</template>

<style src="./projectToolPage.css"></style>

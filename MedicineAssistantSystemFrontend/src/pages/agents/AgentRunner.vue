<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { aiApi } from '@/api/ai'

const route = useRoute()
const agent = computed(() => String(route.params.agent))

const titleMap: Record<string, string> = {
  'project-evaluation': '立项评估智能体',
  'formula-compatibility': '配伍分析智能体',
  'mechanism-inference': '机制推演智能体',
  'target-prediction': '靶点预测智能体',
  'literature-analysis': '文献分析智能体',
  'patent-analysis': '专利分析智能体',
  'experiment-design': '实验设计智能体',
  'report-generation': '报告生成智能体'
}

const callMap: Record<string, (input: string, conversationId?: number | null) => Promise<{ output: string }>> = {
  'project-evaluation': aiApi.projectEvaluation,
  'formula-compatibility': aiApi.formulaCompatibility,
  'mechanism-inference': aiApi.mechanismInference,
  'target-prediction': aiApi.targetPrediction,
  'literature-analysis': aiApi.literatureAnalysis,
  'patent-analysis': aiApi.patentAnalysis,
  'experiment-design': aiApi.experimentDesign,
  'report-generation': aiApi.reportGeneration
}

const input = ref('')
const loading = ref(false)
const error = ref<string | null>(null)
const output = ref('')
const conversationId = ref<number>(Date.now())

const memoryEnabled = computed(() => {
  return agent.value !== 'project-evaluation' && agent.value !== 'report-generation'
})

watch(agent, () => {
  conversationId.value = Date.now()
  output.value = ''
  error.value = null
})

async function run() {
  loading.value = true
  error.value = null
  output.value = ''
  try {
    const fn = callMap[agent.value]
    if (!fn) throw new Error('unknown agent')
    const res = await fn(input.value || '', memoryEnabled.value ? conversationId.value : null)
    output.value = res?.output ?? ''
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="page">
    <div class="head">
      <div>
        <div class="t">{{ titleMap[agent] || '智能体' }}</div>
        <div class="s">POST `/api/ai/{{ agent }}`</div>
      </div>
      <div class="act">
        <el-button type="primary" :loading="loading" @click="run">运行</el-button>
      </div>
    </div>

    <el-alert v-if="error" :title="error" type="error" show-icon />

    <el-card>
      <el-form label-width="90px">
        <el-form-item label="输入">
          <el-input v-model="input" type="textarea" :rows="6" placeholder="输入需求/上下文/约束" />
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <div class="ot">输出</div>
      <el-input :model-value="output" type="textarea" :rows="14" readonly />
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
}
.ot {
  font-weight: 900;
  color: #123b30;
  margin-bottom: 8px;
}
</style>


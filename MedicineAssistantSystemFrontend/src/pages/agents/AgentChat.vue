<script setup lang="ts">
import { computed, nextTick, ref } from 'vue'
import { useRoute } from 'vue-router'
import { aiApi } from '@/api/ai'

type Msg = { role: 'user' | 'assistant'; content: string; ts: number }

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

const callMap: Record<string, (input: string) => Promise<{ output: string }>> = {
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
const msgs = ref<Msg[]>([
  {
    role: 'assistant',
    content: `你已进入「${titleMap[agent.value] || '智能体'}」。把需求/上下文发给我。`,
    ts: Date.now()
  }
])

const listEl = ref<HTMLElement | null>(null)
const isLanding = computed(() => msgs.value.filter((m) => m.role === 'user').length === 0)
const agentTitle = computed(() => titleMap[agent.value] || '智能体')

async function send() {
  const text = input.value.trim()
  if (!text) return
  input.value = ''
  error.value = null
  msgs.value.push({ role: 'user', content: text, ts: Date.now() })
  loading.value = true

  try {
    await nextTick()
    listEl.value?.scrollTo({ top: listEl.value.scrollHeight })
    const fn = callMap[agent.value]
    if (!fn) throw new Error('unknown agent')
    const res = await fn(text)
    msgs.value.push({ role: 'assistant', content: res?.output ?? '', ts: Date.now() })
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    loading.value = false
    await nextTick()
    listEl.value?.scrollTo({ top: listEl.value.scrollHeight })
  }
}

function clearChat() {
  msgs.value = [
    {
      role: 'assistant',
      content: `你已进入「${titleMap[agent.value] || '智能体'}」。把需求/上下文发给我。`,
      ts: Date.now()
    }
  ]
  error.value = null
}

function pickPrompt(p: string) {
  input.value = p
}
</script>

<template>
  <div class="g">
    <div class="topbar">
      <div class="brand">
        <div class="ttl">{{ agentTitle }}</div>
        <div class="sub">对话</div>
      </div>
      <div class="actions">
        <el-button text @click="clearChat">清空</el-button>
      </div>
    </div>

    <el-alert v-if="error" :title="error" type="error" show-icon />

    <div v-if="isLanding" class="landing">
      <div class="hello">
        <div class="h1">你好</div>
        <div class="h2">从哪里开始？</div>
      </div>

      <div class="center">
        <div class="composer gem">
          <el-input
            v-model="input"
            type="textarea"
            :rows="2"
            resize="none"
            placeholder="问点什么…（Enter 发送，Shift+Enter 换行）"
            @keydown.enter.exact.prevent="send"
            @keydown.enter.shift.exact.stop
          />
          <div class="bar">
            <div class="tools">
              <button class="tool-btn" type="button" @click="pickPrompt('请先问我 3 个关键问题，帮助你更准确地完成任务。')">
                +
              </button>
              <div class="tool-txt">Tools</div>
            </div>
            <el-button class="send" type="primary" :loading="loading" @click="send">发送</el-button>
          </div>
        </div>

        <div class="chips">
          <button class="chip" type="button" @click="pickPrompt('请帮我梳理当前任务目标、输入与输出格式。')">梳理目标</button>
          <button class="chip" type="button" @click="pickPrompt('把我这段内容总结成要点，并给出下一步行动清单。')">总结要点</button>
          <button class="chip" type="button" @click="pickPrompt('给出一个更专业、更清晰的表达版本。')">润色表达</button>
          <button class="chip" type="button" @click="pickPrompt('先给出结论，再给出理由与步骤。')">结论优先</button>
        </div>
      </div>
    </div>

    <div v-else class="chat">
      <div ref="listEl" class="stream">
        <div v-for="m in msgs" :key="m.ts + m.role" class="row" :class="m.role">
          <div class="bubble">
            <div class="who">{{ m.role === 'user' ? '你' : agentTitle }}</div>
            <div class="txt">{{ m.content }}</div>
          </div>
        </div>

        <div v-if="loading" class="row assistant">
          <div class="bubble">
            <div class="who">{{ agentTitle }}</div>
            <div class="txt typing">
              <span></span><span></span><span></span>
            </div>
          </div>
        </div>
      </div>

      <div class="composer dock">
        <el-input
          v-model="input"
          type="textarea"
          :rows="2"
          resize="none"
          placeholder="输入消息…（Enter 发送，Shift+Enter 换行）"
          @keydown.enter.exact.prevent="send"
          @keydown.enter.shift.exact.stop
        />
        <div class="bar">
          <div class="tools">
            <button class="tool-btn" type="button" @click="pickPrompt('请先问我 3 个关键问题，帮助你更准确地完成任务。')">
              +
            </button>
            <div class="tool-txt">Tools</div>
          </div>
          <el-button class="send" type="primary" :loading="loading" @click="send">发送</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.g {
  display: flex;
  flex-direction: column;
  gap: 12px;
  height: min(78vh, 860px);
}

.topbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}
.ttl {
  font-size: 16px;
  font-weight: 950;
  color: rgba(255, 255, 255, 0.92);
}
.sub {
  margin-top: 6px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.62);
}

.landing {
  flex: 1;
  display: grid;
  place-items: center;
  padding: 14px 0;
  min-height: 0;
}
.center {
  width: min(920px, 92vw);
}
.hello {
  width: min(920px, 92vw);
  margin-bottom: 14px;
}
.h1 {
  font-size: 20px;
  font-weight: 900;
  color: rgba(255, 255, 255, 0.78);
}
.h2 {
  margin-top: 8px;
  font-size: clamp(28px, 4vw, 40px);
  font-weight: 950;
  letter-spacing: -0.02em;
  color: rgba(255, 255, 255, 0.92);
}

.chat {
  flex: 1;
  display: grid;
  grid-template-rows: 1fr auto;
  gap: 12px;
  min-height: 0;
}
.stream {
  min-height: 0;
  overflow: auto;
  padding: 10px 2px;
  scrollbar-width: none;
  -ms-overflow-style: none;
}
.stream::-webkit-scrollbar {
  width: 0;
  height: 0;
}
.row {
  display: flex;
  padding: 8px 0;
}
.row.user {
  justify-content: flex-end;
}
.bubble {
  width: min(860px, 92%);
  border-radius: 22px;
  padding: 12px 14px;
  background: rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(12px);
}
.row.user .bubble {
  background: linear-gradient(145deg, rgba(146, 230, 202, 0.55), rgba(103, 181, 150, 0.22));
}
.who {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.68);
  font-weight: 850;
  margin-bottom: 8px;
}
.txt {
  white-space: pre-wrap;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.92);
  font-size: 13px;
}

.composer {
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(12px);
  padding: 10px;
}
.composer.gem {
  border-radius: 28px;
  padding: 12px 12px 10px;
}
.bar {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}
.tools {
  display: flex;
  align-items: center;
  gap: 10px;
  color: rgba(255, 255, 255, 0.66);
}
.tool-btn {
  width: 32px;
  height: 32px;
  border-radius: 999px;
  border: 0;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.9);
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(10px);
}
.tool-txt {
  font-size: 12px;
  font-weight: 800;
  opacity: 0.85;
}

.chips {
  margin-top: 12px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}
.chip {
  border: 0;
  cursor: pointer;
  border-radius: 999px;
  padding: 10px 12px;
  background: rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(12px);
  color: rgba(255, 255, 255, 0.86);
  font-size: 12px;
  font-weight: 800;
}
.chip:hover {
  background: rgba(255, 255, 255, 0.09);
}

.send {
  border-radius: 999px;
  padding-left: 16px;
  padding-right: 16px;
}

:deep(.el-textarea__inner) {
  border: 0 !important;
  box-shadow: none !important;
  background: rgba(255, 255, 255, 0.02) !important;
  color: rgba(255, 255, 255, 0.92) !important;
  border-radius: 18px !important;
}
:deep(.el-textarea__inner::placeholder) {
  color: rgba(255, 255, 255, 0.5) !important;
}
.composer.gem :deep(.el-textarea__inner) {
  border-radius: 22px !important;
  background: rgba(255, 255, 255, 0.03) !important;
}

@media (max-width: 860px) {
  .bubble {
    width: min(860px, 96%);
  }
}

@media (prefers-reduced-motion: reduce) {
  .chip:hover {
    background: rgba(255, 255, 255, 0.06);
  }
}

.typing span {
  display: inline-block;
  width: 6px;
  height: 6px;
  margin-right: 6px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.55);
  animation: dot 1.1s infinite ease-in-out;
}
.typing span:nth-child(2) {
  animation-delay: 0.15s;
}
.typing span:nth-child(3) {
  animation-delay: 0.3s;
}
@keyframes dot {
  0%,
  80%,
  100% {
    transform: translateY(0);
    opacity: 0.35;
  }
  40% {
    transform: translateY(-4px);
    opacity: 1;
  }
}
</style>


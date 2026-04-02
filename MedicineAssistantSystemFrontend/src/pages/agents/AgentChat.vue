<script setup lang="ts">
import { computed, nextTick, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getAiConversationHistory, postAiStreamText, type AiConversationHistoryItem } from '@/api/ai'

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

const pathMap: Record<string, string> = {
  'project-evaluation': '/api/ai/project-evaluation',
  'formula-compatibility': '/api/ai/formula-compatibility',
  'mechanism-inference': '/api/ai/mechanism-inference',
  'target-prediction': '/api/ai/target-prediction',
  'literature-analysis': '/api/ai/literature-analysis',
  'patent-analysis': '/api/ai/patent-analysis',
  'experiment-design': '/api/ai/experiment-design',
  'report-generation': '/api/ai/report-generation'
}

const agentCodeMap: Record<string, string> = {
  'project-evaluation': 'PROJECT_EVALUATION',
  'formula-compatibility': 'FORMULA_COMPATIBILITY',
  'mechanism-inference': 'MECHANISM_INFERENCE',
  'target-prediction': 'TARGET_PREDICTION',
  'literature-analysis': 'LITERATURE_ANALYSIS',
  'patent-analysis': 'PATENT_ANALYSIS',
  'experiment-design': 'EXPERIMENT_DESIGN',
  'report-generation': 'REPORT_GENERATION'
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

const historyLoading = ref(false)
const historyError = ref<string | null>(null)
const history = ref<AiConversationHistoryItem[]>([])

const listEl = ref<HTMLElement | null>(null)
const isLanding = computed(() => msgs.value.filter((m) => m.role === 'user').length === 0)
const agentTitle = computed(() => titleMap[agent.value] || '智能体')

const memoryEnabled = computed(() => {
  return agent.value !== 'project-evaluation' && agent.value !== 'report-generation'
})

const conversationId = ref<number | null>(null)

function bucketLabel(iso: string | null) {
  if (!iso) return '更早'
  const t = new Date(iso).getTime()
  if (!Number.isFinite(t)) return '更早'
  const now = Date.now()
  const days = (now - t) / (1000 * 60 * 60 * 24)
  if (days < 1) return '今天'
  if (days < 2) return '一天前'
  if (days < 8) return '一周前'
  if (days < 32) return '一个月前'
  return '更早'
}

function formatTime(iso: string | null) {
  if (!iso) return '-'
  const d = new Date(iso)
  if (!Number.isFinite(d.getTime())) return '-'
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hh = String(d.getHours()).padStart(2, '0')
  const mm = String(d.getMinutes()).padStart(2, '0')
  return `${y}-${m}-${day} ${hh}:${mm}`
}

function truncate(s: string | null | undefined, n = 60) {
  const v = String(s ?? '')
  if (v.length <= n) return v
  return v.slice(0, n) + '...'
}

const groupedHistory = computed(() => {
  const groups: { label: string; items: AiConversationHistoryItem[] }[] = []
  for (const it of history.value) {
    const label = bucketLabel(it.createTime)
    let g = groups.find(x => x.label === label)
    if (!g) {
      g = { label, items: [] }
      groups.push(g)
    }
    g.items.push(it)
  }
  return groups
})

function loadFromHistory(item: AiConversationHistoryItem) {
  if (memoryEnabled.value) {
    conversationId.value = item.conversationId ?? null
  }
  const greeting = `你已进入「${agentTitle.value || '智能体'}」。把需求/上下文发给我。`
  msgs.value = [
    { role: 'assistant', content: greeting, ts: Date.now() },
    { role: 'user', content: item.inputText ?? '', ts: Date.now() + 1 },
    { role: 'assistant', content: item.outputText ?? '', ts: Date.now() + 2 }
  ]
  error.value = null
  input.value = ''
  nextTick(() => {
    listEl.value?.scrollTo({ top: listEl.value.scrollHeight })
  })
}

async function refreshHistory() {
  if (!memoryEnabled.value) {
    history.value = []
    historyError.value = null
    historyLoading.value = false
    return
  }
  const code = agentCodeMap[agent.value]
  if (!code) return
  historyLoading.value = true
  historyError.value = null
  try {
    history.value = await getAiConversationHistory(code, 200)
  } catch (e: any) {
    historyError.value = String(e?.message || e)
  } finally {
    historyLoading.value = false
  }
}

watch(
  agent,
  () => {
    clearChat()
    refreshHistory()
  },
  { immediate: true }
)

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
    const path = pathMap[agent.value]
    if (!path) throw new Error('unknown agent')

    if (memoryEnabled.value) {
      if (conversationId.value == null) conversationId.value = Date.now()
    } else {
      conversationId.value = null
    }

    const assistantMsg: Msg = { role: 'assistant', content: '', ts: Date.now() }
    msgs.value.push(assistantMsg)

    await postAiStreamText(
      path,
      memoryEnabled.value
        ? { input: text, conversationId: conversationId.value }
        : { input: text },
      (chunk) => {
        assistantMsg.content += chunk
      }
    )

    if (memoryEnabled.value) {
      await refreshHistory()
    }
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    loading.value = false
    await nextTick()
    listEl.value?.scrollTo({ top: listEl.value.scrollHeight })
  }
}

function clearChat() {
  conversationId.value = memoryEnabled.value ? Date.now() : null
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

    <div v-if="isLanding" class="landing-wrap">
      <div v-if="memoryEnabled" class="history-panel">
        <div class="history-head">
          <div class="history-title">历史记录</div>
          <div class="history-sub">{{ agentTitle }}</div>
        </div>

        <div v-if="historyLoading" class="history-loading">加载中...</div>
        <el-alert v-else-if="historyError" type="error" show-icon :title="historyError" />
        <div v-else-if="history.length === 0" class="history-empty">暂无历史</div>
        <div v-else class="history-groups">
          <div v-for="g in groupedHistory" :key="g.label" class="history-group">
            <div class="history-group-label">{{ g.label }}</div>
            <div v-for="it in g.items" :key="String(it.conversationId ?? it.createTime ?? it.title)"
              class="history-item" @click="loadFromHistory(it)">
              <div class="history-item-input">{{ truncate(it.title ?? it.inputText, 40) }}</div>
              <div class="history-item-time">{{ formatTime(it.createTime) }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="landing">
        <div class="hello">
          <div class="h1">你好</div>
          <div class="h2">从哪里开始？</div>
        </div>

        <div class="center">
          <div class="composer gem">
            <el-input v-model="input" type="textarea" :rows="2" resize="none"
              placeholder="问点什么…（Enter 发送，Shift+Enter 换行）" @keydown.enter.exact.prevent="send"
              @keydown.enter.shift.exact.stop />
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
    </div>

    <div v-else class="chat-wrap">
      <div v-if="memoryEnabled" class="history-panel">
        <div class="history-head">
          <div class="history-title">历史记录</div>
          <div class="history-sub">{{ agentTitle }}</div>
        </div>

        <div v-if="historyLoading" class="history-loading">加载中...</div>
        <el-alert v-else-if="historyError" type="error" show-icon :title="historyError" />
        <div v-else-if="history.length === 0" class="history-empty">暂无历史</div>
        <div v-else class="history-groups">
          <div v-for="g in groupedHistory" :key="g.label" class="history-group">
            <div class="history-group-label">{{ g.label }}</div>
            <div v-for="it in g.items" :key="String(it.conversationId ?? it.createTime ?? it.title)"
              class="history-item" @click="loadFromHistory(it)">
              <div class="history-item-input">{{ truncate(it.title ?? it.inputText, 40) }}</div>
              <div class="history-item-time">{{ formatTime(it.createTime) }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="chat">
        <div ref="listEl" class="stream">
          <div v-for="m in msgs" :key="m.ts + m.role" class="row" :class="m.role">
            <div class="bubble">
              <div class="who">{{ m.role === 'user' ? '你' : agentTitle }}</div>
              <div class="txt">{{ m.content }}</div>
            </div>
          </div>

          <div v-if="loading && false" class="row assistant">
            <div class="bubble">
              <div class="who">{{ agentTitle }}</div>
              <div class="txt typing">
                <span></span><span></span><span></span>
              </div>
            </div>
          </div>
        </div>

        <div class="composer dock">
          <el-input v-model="input" type="textarea" :rows="2" resize="none" placeholder="输入消息…（Enter 发送，Shift+Enter 换行）"
            @keydown.enter.exact.prevent="send" @keydown.enter.shift.exact.stop />
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

.landing-wrap {
  flex: 1;
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 12px;
  min-height: 0;
}

.chat-wrap {
  flex: 1;
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 12px;
  min-height: 0;
}

.history-panel {
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(12px);
  padding: 12px;
  overflow: auto;
  min-height: 0;
}

.history-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.history-title {
  font-size: 14px;
  font-weight: 950;
  color: rgba(255, 255, 255, 0.92);
}

.history-sub {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.62);
  font-weight: 800;
}

.history-loading {
  color: rgba(255, 255, 255, 0.75);
  font-size: 12px;
  padding: 12px 0;
}

.history-empty {
  color: rgba(255, 255, 255, 0.72);
  font-size: 12px;
  padding: 12px 0;
}

.history-groups {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.history-group-label {
  font-size: 12px;
  font-weight: 900;
  color: rgba(255, 255, 255, 0.72);
  margin-top: 4px;
}

.history-item {
  cursor: pointer;
  border-radius: 16px;
  padding: 10px 10px 9px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.03);
  margin-top: 8px;
  transition: transform 0.15s ease, background 0.15s ease;
}

.history-item:hover {
  transform: translateY(-1px);
  background: rgba(255, 255, 255, 0.06);
}

.history-item-input {
  color: rgba(255, 255, 255, 0.92);
  font-weight: 800;
  font-size: 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.history-item-time {
  margin-top: 6px;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.62);
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

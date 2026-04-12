<script setup lang="ts">
import { computed, nextTick, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { postExpertChatStream } from '@/api/ai'
import { getExpertByKey } from './experts'
import { resolveExpertMedia } from './expertMedia'

type Msg = { role: 'user' | 'assistant'; content: string }

const route = useRoute()
const router = useRouter()
const expertKey = computed(() => String(route.params.expert || ''))
const expert = computed(() => getExpertByKey(expertKey.value))
const media = computed(() => resolveExpertMedia([expert.value?.key || expertKey.value, expert.value?.slug || '', expert.value?.name || '']))

const talking = ref(false)
const gotReplyChunk = ref(false)
const input = ref('')
const loading = ref(false)
const error = ref<string | null>(null)
const conversationId = ref<number | null>(null)
const msgs = ref<Msg[]>([])
const listEl = ref<HTMLElement | null>(null)

watch(
  () => [expertKey.value, expert.value] as const,
  () => {
    conversationId.value = null
    error.value = null
    const e = expert.value
    if (e) msgs.value = [{ role: 'assistant', content: `你好，我是${e.name}。请向我提问。` }]
    else msgs.value = []
  },
  { immediate: true }
)

watch(
  () => expert.value,
  (e) => {
    if (!e) router.replace('/knowledge')
  },
  { immediate: true }
)

const avatarMode = computed(() => {
  const m = media.value
  if (talking.value && m.talk) return { kind: 'talk' as const, url: m.talk }
  if (m.idle) return { kind: 'idle' as const, url: m.idle }
  return { kind: 'none' as const, url: '' }
})

async function send() {
  const text = input.value.trim()
  if (!text || !expert.value) return
  input.value = ''
  error.value = null
  msgs.value.push({ role: 'user', content: text })
  loading.value = true
  gotReplyChunk.value = false
  talking.value = false
  msgs.value.push({ role: 'assistant', content: '' })
  const assistantIndex = msgs.value.length - 1
  try {
    await nextTick()
    listEl.value?.scrollTo({ top: listEl.value.scrollHeight })
    await postExpertChatStream(
      {
        expert: expert.value.key,
        input: text,
        conversationId: conversationId.value
      },
      (chunk) => {
        if (!gotReplyChunk.value) {
          gotReplyChunk.value = true
          talking.value = true
        }
        const msg = msgs.value[assistantIndex]
        if (msg) msg.content += chunk
        void nextTick(() => {
          listEl.value?.scrollTo({ top: listEl.value.scrollHeight })
        })
      },
      (meta) => {
        if (meta.conversationId != null) conversationId.value = meta.conversationId
      }
    )
  } catch (e: unknown) {
    error.value = String((e as Error)?.message || e)
  } finally {
    loading.value = false
    talking.value = false
    await nextTick()
    listEl.value?.scrollTo({ top: listEl.value.scrollHeight })
  }
}

function back() {
  router.push('/knowledge')
}
</script>

<template>
  <div class="expert-page">
    <header class="top">
      <button type="button" class="back" @click="back">← 返回</button>
      <div class="title-block">
        <h1 class="name">{{ expert?.name }}</h1>
        <p class="tag">{{ expert?.tag }}</p>
      </div>
    </header>

    <p v-if="error" class="err err-banner">{{ error }}</p>

    <div class="body">
      <aside class="avatar-side">
        <div class="avatar-wrap">
          <img v-if="avatarMode.kind === 'talk'" class="avatar-img" :src="avatarMode.url" alt="" />
          <img v-else-if="avatarMode.kind === 'idle'" class="avatar-img" :src="avatarMode.url" alt="" />
          <div v-else class="avatar-fallback">{{ expert?.name?.[0] }}</div>
        </div>
        <div class="side-meta">
          <p class="state-hint">{{ talking ? '正在回复…' : '静候提问' }}</p>
          <button type="button" class="quick" @click="input = '请按中医思路帮我辨证分析，并给出建议'">一键问诊</button>
        </div>
      </aside>

      <section class="chat-side">
        <div class="thread-shell mas-scrollbar">
          <div ref="listEl" class="msg-list">
            <div v-for="(m, i) in msgs" :key="i" class="msg" :class="m.role">
              <div class="bubble">
                <template
                  v-if="m.role === 'assistant' && !m.content && loading && i === msgs.length - 1"
                >
                  <span class="typing" aria-hidden="true">
                    <span class="typing-dot" />
                    <span class="typing-dot" />
                    <span class="typing-dot" />
                  </span>
                  <span class="sr-only">正在回复</span>
                </template>
                <template v-else>{{ m.content }}</template>
              </div>
            </div>
          </div>
        </div>
        <div class="composer-shell">
          <div class="composer">
            <textarea
              v-model="input"
              class="inp"
              rows="1"
              placeholder="请输入关于您的健康问题……"
              @keydown.enter.exact.prevent="send"
            />
            <button type="button" class="send" :disabled="loading || !input.trim()" @click="send">发送</button>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
.expert-page {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
  overflow: hidden;
  box-sizing: border-box;
  width: 100%;
  padding-bottom: calc(100px + env(safe-area-inset-bottom, 0px));
}
.err-banner {
  flex-shrink: 0;
  margin: 0;
  padding: 8px 12px;
  border-radius: 10px;
  background: rgba(120, 30, 30, 0.35);
  border: 1px solid rgba(255, 150, 150, 0.35);
}
.top {
  flex-shrink: 0;
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 2px 0 0;
  z-index: 1;
}
.back {
  border: 1px solid rgba(255, 255, 255, 0.22);
  background: rgba(14, 42, 36, 0.45);
  color: #e9f4ef;
  border-radius: 12px;
  padding: 8px 14px;
  cursor: pointer;
  font-weight: 700;
}
.title-block .name {
  margin: 0;
  font-size: 22px;
  font-weight: 900;
  color: rgba(255, 255, 255, 0.94);
}
.title-block .tag {
  margin: 6px 0 0;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.65);
}
.body {
  display: grid;
  grid-template-columns: 300px minmax(0, 1fr);
  grid-template-rows: minmax(0, 1fr);
  gap: 22px;
  align-items: stretch;
  flex: 1;
  min-height: max(340px, min(66vh, 760px));
  overflow: hidden;
  box-sizing: border-box;
}
.avatar-side {
  position: sticky;
  top: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  width: 100%;
  min-height: 0;
  justify-content: flex-start;
}
.avatar-wrap {
  width: 100%;
  max-width: 300px;
  flex: 0 0 auto;
  max-height: min(56vh, 520px);
  aspect-ratio: 3 / 4;
  border-radius: 18px;
  overflow: hidden;
  border: 1px solid rgba(115, 209, 180, 0.28);
  box-shadow: 0 10px 36px rgba(0, 0, 0, 0.32);
  background: rgba(8, 22, 20, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
}
.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  object-position: center top;
  display: block;
}
.avatar-fallback {
  width: 100%;
  height: 100%;
  display: grid;
  place-items: center;
  font-size: 48px;
  font-weight: 900;
  color: rgba(115, 209, 180, 0.85);
  background: linear-gradient(160deg, rgba(41, 79, 69, 0.9), rgba(16, 47, 42, 0.95));
}
.state-hint {
  margin: 0;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.55);
}
.side-meta {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  padding: 0 6px;
}
.quick {
  border: 1px solid rgba(255, 255, 255, 0.18);
  background: rgba(14, 42, 36, 0.32);
  color: rgba(255, 255, 255, 0.82);
  border-radius: 999px;
  padding: 6px 10px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 800;
}
.chat-side {
  display: grid;
  grid-template-rows: minmax(0, 1fr) auto;
  grid-template-columns: 1fr;
  gap: 12px;
  min-width: 0;
  min-height: 0;
  align-self: stretch;
  align-content: stretch;
}
.thread-shell {
  min-height: 0;
  overflow: auto;
  overscroll-behavior: contain;
  border-radius: 16px;
  background: linear-gradient(165deg, rgba(255, 255, 255, 0.08), rgba(10, 38, 32, 0.42));
  border: 1px solid rgba(115, 209, 180, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.22);
}
.msg-list {
  padding: 18px 16px 22px;
  width: 100%;
  margin: 0;
  box-sizing: border-box;
}
.msg {
  margin-bottom: 16px;
  display: flex;
  width: 100%;
}
.msg:last-child {
  margin-bottom: 0;
}
.msg.user {
  justify-content: flex-end;
}
.msg.assistant {
  justify-content: flex-start;
}
.bubble {
  max-width: min(100%, 38rem);
  padding: 11px 14px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.58;
  white-space: pre-wrap;
  word-break: break-word;
  color: rgba(255, 255, 255, 0.93);
}
.msg.user .bubble {
  background: rgba(115, 209, 180, 0.2);
  border: 1px solid rgba(115, 209, 180, 0.38);
  color: #f0faf6;
}
.msg.assistant .bubble {
  background: rgba(255, 255, 255, 0.07);
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: rgba(255, 255, 255, 0.9);
}
.composer-shell {
  position: relative;
  z-index: 5;
  min-height: 56px;
  align-self: stretch;
}
.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}
.typing {
  display: inline-flex;
  gap: 6px;
  align-items: center;
  min-height: 22px;
  padding: 2px 0;
}
.typing-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: rgba(115, 209, 180, 0.85);
  animation: typing-dot 1.15s ease-in-out infinite;
}
.typing-dot:nth-child(2) {
  animation-delay: 0.18s;
}
.typing-dot:nth-child(3) {
  animation-delay: 0.36s;
}
@keyframes typing-dot {
  0%,
  60%,
  100% {
    transform: translateY(0);
    opacity: 0.35;
  }
  30% {
    transform: translateY(-6px);
    opacity: 1;
  }
}
.err {
  font-size: 12px;
  color: #ffc9c9;
}
.composer {
  display: flex;
  gap: 10px;
  align-items: flex-end;
  padding: 10px 12px;
  border-radius: 16px;
  background: rgba(14, 42, 36, 0.5);
  border: 1px solid rgba(115, 209, 180, 0.28);
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(10px);
}
.inp {
  flex: 1;
  resize: none;
  min-height: 46px;
  max-height: 120px;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.16);
  background: rgba(0, 0, 0, 0.22);
  color: #e9f4ef;
  padding: 10px 14px;
  font-size: 14px;
  line-height: 1.45;
}
.inp::placeholder {
  color: rgba(255, 255, 255, 0.4);
}
.inp:focus {
  outline: none;
  border-color: rgba(115, 209, 180, 0.45);
  box-shadow: 0 0 0 1px rgba(115, 209, 180, 0.25);
}
.send {
  flex-shrink: 0;
  border: 0;
  border-radius: 12px;
  padding: 11px 20px;
  font-weight: 800;
  font-size: 14px;
  cursor: pointer;
  background: linear-gradient(145deg, #73d1b4, #3d8f75);
  color: #0a221f;
}
.send:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}
@media (max-width: 820px) {
  .body {
    grid-template-columns: 1fr;
    grid-template-rows: auto minmax(0, 1fr);
    min-height: 0;
  }
  .avatar-side {
    position: relative;
  }
  .avatar-wrap {
    max-width: 100%;
    max-height: 240px;
    aspect-ratio: 4 / 3;
  }
  .side-meta {
    width: 100%;
  }
}
</style>

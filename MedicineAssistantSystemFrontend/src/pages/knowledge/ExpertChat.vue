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
        <div class="chat-panel">
          <div ref="listEl" class="msg-list">
            <div v-for="(m, i) in msgs" :key="i" class="msg" :class="m.role">
              <div class="bubble">{{ m.content }}</div>
            </div>
          </div>
          <div class="composer">
            <textarea
              v-model="input"
              class="inp"
              rows="2"
              placeholder="请输入关于您的健康问题........."
              @keydown.enter.exact.prevent="send"
            />
            <button type="button" class="send" :disabled="loading || !input.trim()" @click="send">发送</button>
          </div>
        </div>
        <p v-if="error" class="err">{{ error }}</p>
      </section>
    </div>
  </div>
</template>

<style scoped>
.expert-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 0;
  overflow: hidden;
  padding-bottom: 0;
  box-sizing: border-box;
}
.top {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  position: sticky;
  top: 0;
  z-index: 6;
  padding: 10px 0;
  background: linear-gradient(180deg, rgba(10, 34, 31, 0.72), rgba(10, 34, 31, 0));
  backdrop-filter: blur(8px);
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
  grid-template-columns: 320px minmax(0, 1fr);
  gap: 22px;
  align-items: start;
  flex: 1;
  min-height: 0;
  height: calc(100vh - 280px);
  overflow: hidden;
}
.avatar-side {
  position: sticky;
  top: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}
.avatar-wrap {
  width: 320px;
  height: min(64vh, 520px);
  border-radius: 26px;
  overflow: hidden;
  border: 1px solid rgba(115, 209, 180, 0.35);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.35);
  background: rgba(0, 0, 0, 0.2);
}
.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
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
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-width: 0;
  min-height: 0;
  position: relative;
}
.chat-panel {
  flex: 1;
  border-radius: 22px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.07), rgba(8, 28, 24, 0.28));
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 18px 60px rgba(0, 0, 0, 0.35);
  overflow: hidden;
  min-height: 0;
  display: flex;
  flex-direction: column;
}
.msg-list {
  flex: 1;
  overflow: auto;
  padding: 16px 14px;
}
.msg {
  margin-bottom: 12px;
  display: flex;
}
.msg.user {
  justify-content: flex-end;
}
.msg.assistant {
  justify-content: flex-start;
}
.bubble {
  max-width: 92%;
  padding: 10px 14px;
  border-radius: 14px;
  font-size: 14px;
  line-height: 1.55;
  white-space: pre-wrap;
  word-break: break-word;
  color: rgba(255, 255, 255, 0.92);
}
.msg.user .bubble {
  background: rgba(115, 209, 180, 0.22);
  border: 1px solid rgba(115, 209, 180, 0.35);
}
.msg.assistant .bubble {
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.1);
}
.err {
  margin: 0;
  font-size: 12px;
  color: #ff9b9b;
}
.composer {
  display: flex;
  gap: 10px;
  align-items: flex-end;
  padding: 10px 12px;
  border-radius: 0;
  background: rgba(10, 34, 31, 0.72);
  border-top: 1px solid rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(10px);
}
.inp {
  flex: 1;
  resize: none;
  min-height: 44px;
  max-height: 88px;
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.14);
  background: rgba(10, 34, 31, 0.55);
  color: #e9f4ef;
  padding: 10px 12px;
  font-size: 14px;
}
.inp::placeholder {
  color: rgba(255, 255, 255, 0.35);
}
.send {
  border: 0;
  border-radius: 14px;
  padding: 12px 20px;
  font-weight: 800;
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
    height: calc(100vh - 260px);
  }
  .avatar-side {
    position: relative;
  }
  .avatar-wrap {
    width: 100%;
    height: 260px;
  }
  .side-meta {
    width: 100%;
  }
}
</style>

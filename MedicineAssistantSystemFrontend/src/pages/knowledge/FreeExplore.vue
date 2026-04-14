<script setup lang="ts">
import { ref, computed } from 'vue'
import { postAiStreamText } from '@/api/ai'
import { projectAiAssessToHtml } from '@/utils/projectAiMarkdown'

type ExploreRow = { herb: string; dose: string }
const rows = ref<ExploreRow[]>([
  { herb: '', dose: '' },
  { herb: '', dose: '' },
  { herb: '', dose: '' }
])

const running = ref(false)
const error = ref<string | null>(null)
const output = ref('')
const outputHtml = computed(() => projectAiAssessToHtml(output.value))

function addRow() {
  rows.value.push({ herb: '', dose: '' })
}

function removeRow(i: number) {
  if (rows.value.length <= 1) return
  rows.value.splice(i, 1)
}

function buildPrompt() {
  const cleaned = rows.value
    .map((r) => ({ herb: r.herb.trim(), dose: r.dose.trim() }))
    .filter((r) => r.herb || r.dose)
  const lines = cleaned.length
    ? cleaned.map((r) => `- ${r.herb || '（未填药材）'}：${r.dose || '（未填剂量）'}`).join('\n')
    : '- （未填写）'
  return `请对以下“药材+剂量”做可行性与配伍分析（偏中医药新药研发视角）：\n\n${lines}\n\n输出：\n1) 配伍合理性与核心风险点（配伍禁忌/相反相畏/毒性与安全性）\n2) 剂量区间与需要补充的信息（人群、证型、给药途径等）\n3) 可能的优化建议与替代思路\n4) 如需证据，请给出可检索的关键词与检索方向。`
}

async function analyze() {
  if (running.value) return
  running.value = true
  error.value = null
  output.value = ''
  const input = buildPrompt()
  try {
    await postAiStreamText('/api/ai/formula-compatibility/ephemeral', { input }, (chunk) => {
      output.value += chunk
    })
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    running.value = false
  }
}
</script>

<template>
  <div class="page">
    <div class="shell">
      <header class="hd">
        <div class="t">自由探索</div>
        <div class="s">录入药材与剂量 → 交给智能体做可行性/配伍风险分析</div>
      </header>

      <section class="panel">
        <div class="rows">
          <div v-for="(r, i) in rows" :key="i" class="row">
            <input v-model="r.herb" class="in herb" placeholder="药材" />
            <input v-model="r.dose" class="in dose" placeholder="剂量（如 10g）" />
            <button type="button" class="del" title="删除" @click="removeRow(i)">×</button>
          </div>
        </div>
        <div class="acts">
          <button type="button" class="add" @click="addRow">＋ 添加药材</button>
          <button type="button" class="go" :disabled="running" @click="analyze">{{ running ? '分析中…' : '开始分析' }}</button>
        </div>
      </section>

      <section v-if="error || output" class="out">
        <div v-if="error" class="err">{{ error }}</div>
        <div v-else class="txt md" v-html="outputHtml" />
      </section>
    </div>
  </div>
</template>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding-bottom: 100px;
  box-sizing: border-box;
}

.shell {
  width: min(920px, 100%);
  margin: 0 auto;
}

.hd .t {
  font-size: 22px;
  font-weight: 900;
  color: rgba(255, 255, 255, 0.94);
}

.hd .s {
  margin-top: 6px;
  font-size: 12px;
  color: rgba(189, 212, 204, 0.72);
}

.panel {
  border-radius: 16px;
  padding: 14px;
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.rows {
  display: grid;
  gap: 10px;
}

.row {
  display: grid;
  grid-template-columns: 1fr 160px 36px;
  gap: 10px;
  align-items: center;
}

.in {
  width: 100%;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.03);
  color: rgba(233, 244, 239, 0.92);
  border-radius: 12px;
  padding: 10px 12px;
  outline: none;
}

.in::placeholder {
  color: rgba(255, 255, 255, 0.42);
}

.del {
  width: 36px;
  height: 40px;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.02);
  color: rgba(255, 255, 255, 0.72);
  cursor: pointer;
  font-weight: 900;
}

.acts {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 12px;
}

.add {
  width: 100%;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.03);
  color: rgba(255, 255, 255, 0.86);
  padding: 10px 12px;
  cursor: pointer;
  font-weight: 800;
}

.go {
  width: 100%;
  border: 0;
  border-radius: 12px;
  padding: 11px 12px;
  cursor: pointer;
  font-weight: 900;
  color: #06241e;
  background: linear-gradient(135deg, rgba(146, 230, 202, 0.96), rgba(115, 209, 180, 0.92));
}

.go:disabled {
  cursor: not-allowed;
  filter: grayscale(0.15);
  opacity: 0.85;
}

.out {
  margin-top: 14px;
  border-radius: 16px;
  padding: 14px;
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.err {
  color: rgba(255, 165, 165, 0.95);
  font-size: 13px;
  line-height: 1.7;
  white-space: pre-wrap;
}

.txt {
  margin: 0;
  color: rgba(233, 244, 239, 0.9);
  font-size: 13px;
  line-height: 1.8;
  word-break: break-word;
  font-family: inherit;
}

.md :deep(h1),
.md :deep(h2),
.md :deep(h3),
.md :deep(h4) {
  margin: 10px 0 6px;
  font-weight: 900;
  line-height: 1.35;
  color: rgba(255, 255, 255, 0.94);
}

.md :deep(h1) {
  font-size: 17px;
}
.md :deep(h2) {
  font-size: 16px;
}
.md :deep(h3) {
  font-size: 15px;
}

.md :deep(p) {
  margin: 0 0 8px;
}

.md :deep(p:last-child) {
  margin-bottom: 0;
}

.md :deep(strong) {
  font-weight: 900;
  color: rgba(255, 255, 255, 0.96);
}

.md :deep(table) {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;
  margin: 8px 0;
}

.md :deep(th),
.md :deep(td) {
  border: 1px solid rgba(255, 255, 255, 0.16);
  padding: 6px 8px;
  text-align: left;
  vertical-align: top;
}

.md :deep(th) {
  background: rgba(0, 0, 0, 0.28);
  font-weight: 850;
}

.md :deep(ul),
.md :deep(ol) {
  margin: 6px 0;
  padding-left: 1.25em;
}

.md :deep(li) {
  margin: 2px 0;
}

.md :deep(hr) {
  border: 0;
  border-top: 1px solid rgba(255, 255, 255, 0.14);
  margin: 10px 0;
}

.md :deep(code) {
  font-size: 12px;
  background: rgba(0, 0, 0, 0.35);
  padding: 1px 5px;
  border-radius: 4px;
}

.md :deep(pre) {
  margin: 8px 0;
  padding: 10px;
  overflow: auto;
  background: rgba(0, 0, 0, 0.32);
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.md :deep(pre code) {
  background: transparent;
  padding: 0;
  font-size: 12px;
}

.md :deep(blockquote) {
  margin: 8px 0;
  padding: 4px 0 4px 12px;
  border-left: 3px solid rgba(115, 209, 180, 0.45);
  color: rgba(255, 255, 255, 0.78);
}

.md :deep(a) {
  color: rgba(146, 230, 202, 0.95);
}

@media (max-width: 680px) {
  .row {
    grid-template-columns: 1fr;
  }
  .del {
    width: 100%;
  }
}
</style>


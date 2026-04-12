<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { EXPERTS } from './experts'
import { resolveExpertMedia } from './expertMedia'

const router = useRouter()

const expertsWithMedia = computed(() =>
  EXPERTS.filter((e) => resolveExpertMedia([e.key, e.slug || '', e.name]).hasAny)
)
const carouselExperts = computed(() => expertsWithMedia.value.slice(0, 10))
const carouselCount = computed(() => Math.max(1, carouselExperts.value.length))
const carouselAngle = computed(() => 360 / carouselCount.value)

const expertPoster = computed(() => {
  const o: Record<string, string | undefined> = {}
  for (const e of carouselExperts.value) {
    const { idle, talk } = resolveExpertMedia([e.key, e.slug || '', e.name])
    o[e.key] = idle || talk
  }
  return o
})

const kbItems = [
  { name: '中药材库', desc: '来源 / 性味 / 功效', icon: '🌿', path: '/knowledge/herbs' },
  { name: '方剂库', desc: '经典方剂 / 组成分析 / 临床应用', icon: '📜', path: '/knowledge/formulas' },
  { name: '成分库', desc: '结构 / 活性 / 靶点信息', icon: '🧪', path: '/knowledge/components' },
  { name: '疾病库', desc: '分类 / 证候 / 现代关联', icon: '🩺', path: '/knowledge/diseases' },
  { name: '靶点通路库', desc: '靶点 / 通路 / 疾病关联', icon: '🎯', path: '/knowledge/target-pathways' },
  { name: '文献库', desc: '检索 / 上传 / 摘要生成', icon: '📚', path: '/literatures' },
  { name: '专利库', desc: '检索 / 相似推荐 / 风险提示', icon: '🧾', path: '/knowledge/patents' },
  { name: '法规库', desc: '检索 / 指导原则 / 政策更新', icon: '⚖️', path: '/regulations' }
]

function goExpert(key: string) {
  router.push(`/knowledge/expert/${encodeURIComponent(key)}`)
}

function go(p: string) {
  router.push(p)
}
</script>

<template>
  <div class="hub">
    <div class="hero-line">
      <h2 class="hub-title">知识枢库</h2>
      <span class="hub-badge">数字化生</span>
    </div>
    <p class="hub-sub">医学专家万花筒 · 点选进入对话</p>

    <div class="main">
      <section class="carousel">
        <div class="cyl-wrap">
          <div class="cyl" :style="{ '--n': String(carouselCount), '--step': `${carouselAngle}deg` } as any">
            <button
              v-for="(e, i) in carouselExperts"
              :key="e.key"
              type="button"
              class="cell"
              :style="{ '--i': String(i) } as any"
              @click="goExpert(e.key)"
            >
              <div class="cell-fig">
                <img v-if="expertPoster[e.key]" class="cell-img" :src="expertPoster[e.key]" alt="" />
                <div v-else class="cell-ph">{{ e.name[0] }}</div>
              </div>
              <div class="cell-name">{{ e.name }}</div>
              <div class="cell-tag">{{ e.tag }}</div>
            </button>
          </div>
          <div class="cyl-glow"></div>
        </div>
      </section>

      <aside class="kb-side" aria-label="知识库索引">
        <div class="kb-side-hd">
          <h3 class="kb-side-title">知识库索引</h3>
          <span class="kb-side-tip">8 类</span>
        </div>
        <p class="kb-side-sub">悬停可看简介</p>
        <div class="kb-chips">
          <button
            v-for="it in kbItems"
            :key="it.path"
            type="button"
            class="kb-chip"
            :title="it.desc"
            @click="go(it.path)"
          >
            <span class="kb-chip-ico">{{ it.icon }}</span>
            <span class="kb-chip-name">{{ it.name }}</span>
          </button>
        </div>
      </aside>
    </div>
  </div>
</template>

<style scoped>
.hub {
  display: flex;
  flex-direction: column;
  gap: 0;
}
.hero-line {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.hub-title {
  margin: 0;
  font-size: 22px;
  font-weight: 900;
  color: rgba(255, 255, 255, 0.94);
}
.hub-badge {
  font-size: 12px;
  font-weight: 800;
  padding: 4px 10px;
  border-radius: 999px;
  border: 1px solid rgba(115, 209, 180, 0.45);
  color: #9ee4cf;
  background: rgba(115, 209, 180, 0.12);
}
.hub-sub {
  margin: 8px 0 0;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.58);
}

.main {
  margin-top: 16px;
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(260px, 340px);
  gap: 18px;
  align-items: start;
  width: 100%;
}

.carousel {
  min-width: 0;
  width: 100%;
}

.cyl-wrap {
  position: relative;
  height: min(56vh, 520px);
  display: grid;
  place-items: center;
  perspective: 1200px;
}

.cyl {
  position: relative;
  width: min(520px, 62vw);
  height: min(420px, 48vh);
  transform-style: preserve-3d;
  animation: spin 18s linear infinite;
}

.cyl:hover {
  animation-play-state: paused;
}

@keyframes spin {
  0% {
    transform: rotateY(0deg);
  }
  100% {
    transform: rotateY(360deg);
  }
}

.cell {
  position: absolute;
  inset: 0;
  margin: auto;
  width: 320px;
  height: 380px;
  border: 0;
  cursor: pointer;
  border-radius: 26px;
  background: rgba(12, 40, 34, 0.42);
  border: 1px solid rgba(255, 255, 255, 0.12);
  box-shadow: 0 18px 48px rgba(0, 0, 0, 0.35);
  transform-style: preserve-3d;
  transform: rotateY(calc(var(--i) * var(--step))) translateZ(360px);
  display: flex;
  flex-direction: column;
  padding: 14px;
  gap: 10px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.cell:hover {
  box-shadow: 0 18px 58px rgba(115, 209, 180, 0.2);
}

.cell-fig {
  width: 100%;
  height: 270px;
  border-radius: 18px;
  overflow: hidden;
  background: rgba(0, 0, 0, 0.25);
  border: 1px solid rgba(115, 209, 180, 0.22);
}

.cell-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.cell-ph {
  width: 100%;
  height: 100%;
  display: grid;
  place-items: center;
  font-size: 56px;
  font-weight: 900;
  color: rgba(115, 209, 180, 0.75);
  background: linear-gradient(165deg, rgba(41, 79, 69, 0.85), rgba(16, 47, 42, 0.95));
}

.cell-name {
  font-size: 18px;
  font-weight: 900;
  color: rgba(255, 255, 255, 0.92);
}

.cell-tag {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  line-height: 1.35;
}

.cyl-glow {
  position: absolute;
  inset: 0;
  pointer-events: none;
  background: radial-gradient(circle at 50% 50%, rgba(115, 209, 180, 0.18), transparent 60%);
  filter: blur(10px);
}

.kb-side {
  padding: 4px 4px 8px;
  overflow: visible;
}

.kb-side-hd {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 8px;
}

.kb-side-title {
  margin: 0;
  font-size: 17px;
  font-weight: 900;
  color: rgba(255, 255, 255, 0.92);
}

.kb-side-tip {
  font-size: 12px;
  font-weight: 800;
  color: rgba(200, 169, 103, 0.95);
}

.kb-side-sub {
  margin: 0 0 14px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.45);
}

.kb-chips {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px 12px;
}

.kb-chip {
  border: none;
  border-radius: 12px;
  padding: 12px 8px;
  background: transparent;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-height: 96px;
  cursor: pointer;
  color: inherit;
  transition: background 0.2s ease, transform 0.2s ease;
}

.kb-chip:hover {
  background: rgba(115, 209, 180, 0.08);
  transform: translateY(-1px);
}

.kb-chip-ico {
  font-size: 30px;
  line-height: 1;
}

.kb-chip-name {
  font-size: 14px;
  font-weight: 800;
  text-align: center;
  line-height: 1.35;
  color: rgba(255, 255, 255, 0.93);
}

@media (max-width: 1100px) {
  .main {
    grid-template-columns: 1fr;
  }
  .cyl-wrap {
    height: min(58vh, 520px);
  }
}
@media (max-width: 860px) {
  .cell {
    width: 260px;
    height: 330px;
    transform: rotateY(calc(var(--i) * var(--step))) translateZ(300px);
  }
  .cell-fig {
    height: 230px;
  }
}
</style>

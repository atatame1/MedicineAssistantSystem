<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const agents = [
  { key: 'project-evaluation', name: '立项评估', desc: '多维评分：创新性/可行性/风险，输出建议与证据链', icon: '📌' },
  { key: 'formula-compatibility', name: '配伍分析', desc: '解析方剂结构与配伍合理性，输出关系图与优化建议', icon: '🧩' },
  { key: 'mechanism-inference', name: '机制推演', desc: '推演成分-靶点-通路-疾病链路，给出实验验证建议', icon: '🧬' },
  { key: 'target-prediction', name: '靶点预测', desc: '基于成分预测潜在靶点，输出置信度', icon: '🎯' },
  { key: 'literature-analysis', name: '文献分析', desc: '聚类/热点趋势/技术路线提取', icon: '📚' },
  { key: 'patent-analysis', name: '专利分析', desc: '相似识别/创新性评估/风险提示', icon: '🧾' },
  { key: 'experiment-design', name: '实验设计', desc: '推荐实验模型，设计分组方案，输出实验草案', icon: '🧪' },
  { key: 'report-generation', name: '报告生成', desc: '自动生成立项/研究总结/技术资料等结构化报告', icon: '📝' }
]

const particles = [
  { x: '5%', y: '16%', s: 6, d: '0s' },
  { x: '14%', y: '63%', s: 9, d: '-1.3s' },
  { x: '27%', y: '35%', s: 7, d: '-2.2s' },
  { x: '38%', y: '72%', s: 8, d: '-0.8s' },
  { x: '49%', y: '22%', s: 6, d: '-1.7s' },
  { x: '58%', y: '61%', s: 10, d: '-2.8s' },
  { x: '67%', y: '40%', s: 8, d: '-1.1s' },
  { x: '75%', y: '16%', s: 7, d: '-2.4s' },
  { x: '84%', y: '66%', s: 9, d: '-0.4s' },
  { x: '92%', y: '31%', s: 6, d: '-1.9s' }
]

function openAgent(key: string) {
  router.push(`/agents/${key}`)
}
</script>

<template>
  <div class="hub">
    <div class="ambient" aria-hidden="true">
      <span
        v-for="(p, i) in particles"
        :key="i"
        class="dot"
        :style="{ left: p.x, top: p.y, width: `${p.s}px`, height: `${p.s}px`, animationDelay: p.d }"
      />
    </div>

    <header class="hub-intro">
      <div class="intro-badge">
        <span class="pulse" aria-hidden="true" />
        <span>8 个能力就绪</span>
      </div>
      <p class="intro-sub">动态网络编排 · 连结式入口 · 智能体协同</p>
    </header>

    <section class="network">
      <svg class="links" viewBox="0 0 1200 380" preserveAspectRatio="none" aria-hidden="true">
        <path class="link" d="M150 95 C 250 62, 350 62, 450 95 C 550 128, 650 128, 750 95 C 850 62, 950 62, 1050 95" />
        <path class="link delay1" d="M150 285 C 250 252, 350 252, 450 285 C 550 318, 650 318, 750 285 C 850 252, 950 252, 1050 285" />
        <path class="link soft" d="M150 95 C 110 165, 110 215, 150 285" />
        <path class="link soft delay2" d="M450 95 C 410 165, 410 215, 450 285" />
        <path class="link soft delay3" d="M750 95 C 790 165, 790 215, 750 285" />
        <path class="link soft delay1" d="M1050 95 C 1090 165, 1090 215, 1050 285" />
      </svg>
      <button
        v-for="(a, i) in agents"
        :key="a.key"
        type="button"
        class="node"
        :style="{ '--i': i }"
        @click="openAgent(a.key)"
      >
        <span class="halo" aria-hidden="true" />
        <span class="idx">{{ String(i + 1).padStart(2, '0') }}</span>
        <div class="icon" aria-hidden="true">{{ a.icon }}</div>
        <div class="name">{{ a.name }}</div>
        <div class="desc">{{ a.desc }}</div>
      </button>
    </section>
  </div>
</template>

<style scoped>
.hub {
  position: relative;
  max-width: 1220px;
  margin: 0 auto;
  padding: 8px 8px 30px;
  animation: enter 0.7s ease both;
}

.ambient {
  position: absolute;
  inset: -8px -16px 0;
  pointer-events: none;
  overflow: hidden;
  z-index: 0;
}

.ambient::before,
.ambient::after {
  content: '';
  position: absolute;
  border-radius: 50%;
  filter: blur(50px);
  opacity: 0.38;
}

.ambient::before {
  width: 420px;
  height: 420px;
  left: -60px;
  top: -90px;
  background: radial-gradient(circle, rgba(141, 228, 197, 0.34), transparent 70%);
  animation: blobA 16s ease-in-out infinite;
}

.ambient::after {
  width: 360px;
  height: 360px;
  right: -70px;
  bottom: 10px;
  background: radial-gradient(circle, rgba(206, 176, 114, 0.3), transparent 72%);
  animation: blobB 18s ease-in-out infinite;
}

.dot {
  position: absolute;
  border-radius: 50%;
  background: rgba(206, 241, 226, 0.85);
  box-shadow: 0 0 12px rgba(206, 241, 226, 0.5);
  animation: dotFloat 4.8s ease-in-out infinite;
}

.hub-intro {
  position: relative;
  z-index: 2;
  margin-bottom: 12px;
  padding: 0 4px;
}

.intro-badge {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  border-radius: 999px;
  padding: 8px 14px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.14);
  color: rgba(255, 255, 255, 0.88);
  font-size: 14px;
  font-weight: 850;
}

.pulse {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(146, 230, 202, 0.95);
  box-shadow: 0 0 0 0 rgba(146, 230, 202, 0.5);
  animation: pulse 2.2s ease-out infinite;
}

.intro-sub {
  margin: 10px 0 0;
  color: rgba(255, 255, 255, 0.56);
  font-size: 15px;
  font-weight: 700;
}

.network {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  grid-auto-rows: auto;
  gap: 14px;
  padding: 12px 0 6px;
}

.links {
  position: absolute;
  inset: 8px 6px 2px;
  z-index: 0;
  width: calc(100% - 12px);
  height: calc(100% - 10px);
  pointer-events: none;
  opacity: 0.7;
}

.link {
  fill: none;
  stroke: rgba(158, 228, 202, 0.42);
  stroke-width: 1.4;
  stroke-dasharray: 5 10;
  animation: flow 7.5s linear infinite;
}

.link.soft {
  stroke: rgba(212, 184, 125, 0.3);
  stroke-width: 1;
  stroke-dasharray: 4 12;
}

.delay1 {
  animation-delay: -1.2s;
}

.delay2 {
  animation-delay: -2.4s;
}

.delay3 {
  animation-delay: -3.6s;
}

.node {
  --i: 0;
  --offset: 0px;
  position: relative;
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 22px;
  padding: 14px 14px 12px;
  text-align: left;
  cursor: pointer;
  overflow: hidden;
  z-index: 1;
  background: linear-gradient(
    145deg,
    rgba(19, 51, 47, 0.5) 0%,
    rgba(12, 38, 34, 0.38) 48%,
    rgba(22, 56, 50, 0.46) 100%
  );
  box-shadow:
    0 6px 22px rgba(0, 0, 0, 0.18),
    inset 0 1px 0 rgba(255, 255, 255, 0.07);
  transform: translateY(var(--offset));
  transition: transform 0.25s ease, box-shadow 0.25s ease;
  animation:
    nodeIn 0.7s cubic-bezier(0.22, 1, 0.36, 1) backwards,
    drift 6.6s ease-in-out infinite;
  animation-delay: calc(var(--i) * 0.07s), calc(var(--i) * 0.45s);
  min-height: 176px;
}

.node:hover {
  transform: translateY(calc(var(--offset) - 6px)) scale(1.02);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.28);
}

.node:active {
  transform: translateY(calc(var(--offset) - 2px)) scale(1.01);
}

.halo {
  position: absolute;
  inset: -60%;
  background: radial-gradient(circle at 18% 20%, rgba(146, 230, 202, 0.24), transparent 32%);
  animation: halo 9s linear infinite;
  pointer-events: none;
}

.idx {
  position: absolute;
  right: 14px;
  top: 10px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.3);
  font-weight: 800;
  letter-spacing: 0.1em;
}

.icon {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  display: grid;
  place-items: center;
  background: rgba(255, 255, 255, 0.1);
  font-size: 20px;
  animation: iconBob 3.8s ease-in-out infinite;
  animation-delay: calc(var(--i) * 0.18s);
}

.name {
  margin-top: 10px;
  color: rgba(255, 255, 255, 0.95);
  font-size: 28px;
  font-weight: 950;
  line-height: 1;
}

.desc {
  margin-top: 8px;
  color: rgba(255, 255, 255, 0.66);
  font-size: 14px;
  line-height: 1.45;
  font-weight: 700;
}

@keyframes enter {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes nodeIn {
  from {
    opacity: 0;
    transform: translateY(calc(var(--offset) + 18px)) scale(0.97);
  }
  to {
    opacity: 1;
    transform: translateY(var(--offset)) scale(1);
  }
}

@keyframes drift {
  0%,
  100% {
    transform: translateY(var(--offset));
  }
  50% {
    transform: translateY(calc(var(--offset) - 8px));
  }
}

@keyframes iconBob {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-4px);
  }
}

@keyframes flow {
  from {
    stroke-dashoffset: 0;
  }
  to {
    stroke-dashoffset: -240;
  }
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(146, 230, 202, 0.5);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(146, 230, 202, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(146, 230, 202, 0);
  }
}

@keyframes halo {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

@keyframes blobA {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }
  50% {
    transform: translate(8%, 7%) scale(1.08);
  }
}

@keyframes blobB {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }
  50% {
    transform: translate(-10%, -7%) scale(0.93);
  }
}

@keyframes dotFloat {
  0%,
  100% {
    transform: translateY(0);
    opacity: 0.35;
  }
  50% {
    transform: translateY(-12px);
    opacity: 0.9;
  }
}

@media (max-width: 1024px) {
  .network {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .links {
    display: none;
  }

  .node {
    min-height: 148px;
  }
}

@media (max-width: 640px) {
  .network {
    grid-template-columns: 1fr;
  }
}

@media (prefers-reduced-motion: reduce) {
  .ambient::before,
  .ambient::after,
  .dot,
  .pulse,
  .link,
  .node,
  .halo,
  .icon {
    animation: none !important;
  }
}
</style>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()
const items = [
  { name: '中药材库', desc: '来源 / 性味 / 功效', icon: '🌿', path: '/knowledge/herbs' },
  { name: '方剂库', desc: '经典方剂 / 组成分析 / 临床应用', icon: '📜', path: '/knowledge/formulas' },
  { name: '成分库', desc: '结构 / 活性 / 靶点信息', icon: '🧪', path: '/knowledge/components' },
  { name: '疾病库', desc: '分类 / 证候 / 现代关联', icon: '🩺', path: '/knowledge/diseases' },
  { name: '靶点通路库', desc: '靶点 / 通路 / 疾病关联', icon: '🎯', path: '/knowledge/target-pathways' },
  { name: '文献库', desc: '检索 / 上传 / 摘要生成', icon: '📚', path: '/literatures' },
  { name: '专利库', desc: '检索 / 相似推荐 / 风险提示', icon: '🧾', path: '/knowledge/patents' },
  { name: '法规库', desc: '检索 / 指导原则 / 政策更新', icon: '⚖️', path: '/regulations' }
]

function go(p: string) {
  router.push(p)
}
</script>

<template>
  <div class="hub">
    <div class="grid">
      <button v-for="it in items" :key="it.path" class="card" @click="go(it.path)">
        <div class="icon">{{ it.icon }}</div>
        <div class="name">{{ it.name }}</div>
        <div class="desc">{{ it.desc }}</div>
      </button>
    </div>
  </div>
</template>

<style scoped>
.hub {
  display: flex;
  flex-direction: column;
  gap: 0;
}
.grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}
.card {
  border: 0;
  border-radius: 22px;
  background: transparent;
  padding: 14px 8px 12px;
  text-align: left;
  cursor: pointer;
  position: relative;
  transition: transform 0.22s ease;
}
.card:hover {
  transform: translateY(-3px);
}
.card::before {
  content: '';
  position: absolute;
  inset: -10px -10px -12px -10px;
  border-radius: 28px;
  background: radial-gradient(circle at 20% 10%, rgba(115, 209, 180, 0.32), transparent 55%),
    radial-gradient(circle at 80% 40%, rgba(200, 169, 103, 0.22), transparent 60%);
  filter: blur(14px);
  opacity: 0.55;
  pointer-events: none;
}
.card::after {
  content: '';
  position: absolute;
  left: 10px;
  right: 10px;
  bottom: 6px;
  height: 1px;
  background: linear-gradient(90deg, rgba(255, 255, 255, 0.35), rgba(255, 255, 255, 0.02));
  opacity: 0.8;
}
.icon {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  display: grid;
  place-items: center;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(10px);
  font-size: 20px;
}
.name {
  margin-top: 10px;
  font-size: 16px;
  font-weight: 900;
  color: rgba(255, 255, 255, 0.92);
}
.desc {
  margin-top: 8px;
  font-size: 12px;
  line-height: 1.4;
  color: rgba(255, 255, 255, 0.7);
}
@media (max-width: 1100px) {
  .grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}
@media (max-width: 860px) {
  .grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>


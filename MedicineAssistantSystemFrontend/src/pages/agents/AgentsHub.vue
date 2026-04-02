<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const agents = [
  {
    key: 'project-evaluation',
    name: '立项评估',
    desc: '多维评分：创新性/可行性/风险，输出建议与证据链',
    icon: '📌'
  },
  {
    key: 'formula-compatibility',
    name: '配伍分析',
    desc: '解析方剂结构与配伍合理性，输出关系图与优化建议',
    icon: '🧩'
  },
  {
    key: 'mechanism-inference',
    name: '机制推演',
    desc: '推演成分-靶点-通路-疾病链路，给出实验验证建议',
    icon: '🧬'
  },
  {
    key: 'target-prediction',
    name: '靶点预测',
    desc: '基于成分预测潜在靶点，输出置信度',
    icon: '🎯'
  },
  {
    key: 'literature-analysis',
    name: '文献分析',
    desc: '聚类/热点趋势/技术路线提取',
    icon: '📚'
  },
  {
    key: 'patent-analysis',
    name: '专利分析',
    desc: '相似识别/创新性评估/风险提示',
    icon: '🧾'
  },
  {
    key: 'experiment-design',
    name: '实验设计',
    desc: '推荐实验模型，设计分组方案，输出实验草案',
    icon: '🧪'
  },
  {
    key: 'report-generation',
    name: '报告生成',
    desc: '自动生成立项/研究总结/技术资料等结构化报告',
    icon: '📝'
  }
]

function openAgent(key: string) {
  router.push(`/agents/${key}`)
}
</script>

<template>
  <div class="hub">
    <div class="grid">
      <button v-for="a in agents" :key="a.key" class="card" @click="openAgent(a.key)">
        <div class="icon">{{ a.icon }}</div>
        <div class="name">{{ a.name }}</div>
        <div class="desc">{{ a.desc }}</div>
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
  background: radial-gradient(circle at 20% 10%, rgba(146, 230, 202, 0.35), transparent 55%),
    radial-gradient(circle at 80% 40%, rgba(200, 169, 103, 0.25), transparent 60%);
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


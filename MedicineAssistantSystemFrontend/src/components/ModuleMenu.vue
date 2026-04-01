<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

type MenuItem = { label: string; path: string }
type MenuGroup = { key: string; label: string; desc?: string; items: MenuItem[] }

const props = defineProps<{ modelValue: boolean }>()
const emit = defineEmits<{ (e: 'update:modelValue', v: boolean): void }>()

const router = useRouter()
const route = useRoute()

const open = computed({
  get: () => props.modelValue,
  set: (v: boolean) => emit('update:modelValue', v)
})

const activeGroupKey = ref<string>('portal')

const groups: MenuGroup[] = [
  {
    key: 'portal',
    label: '智能门户',
    desc: '总览 / 任务 / 风险 / 快捷入口',
    items: [
      { label: '门户总览', path: '/portal' },
      { label: '我的任务', path: '/me/tasks' },
      { label: '我的资料', path: '/me/profile' }
    ]
  },
  {
    key: 'projects',
    label: '项目中心',
    desc: '立项 / 看板 / 决策 / 文档 / 成员',
    items: [
      { label: '项目列表', path: '/projects' },
      { label: '新建项目', path: '/projects/create' },
      { label: '项目看板', path: '/projects/board' },
      { label: '决策记录', path: '/projects/decisions' },
      { label: '文档管理', path: '/projects/documents' },
      { label: '项目成员', path: '/projects/members' },
      { label: '立项评估流', path: '/projects/draft-evaluate' },
      { label: '报告生成流', path: '/projects/report-stream' }
    ]
  },
  {
    key: 'knowledge',
    label: '知识中心',
    desc: '药材 / 方剂 / 成分 / 疾病 / 靶点通路 / 文献 / 专利 / 法规',
    items: [
      { label: '中药材库', path: '/knowledge/herbs' },
      { label: '方剂库', path: '/knowledge/formulas' },
      { label: '成分库', path: '/knowledge/components' },
      { label: '疾病库', path: '/knowledge/diseases' },
      { label: '靶点通路库', path: '/knowledge/target-pathways' },
      { label: '文献库', path: '/literatures' },
      { label: '专利库', path: '/knowledge/patents' },
      { label: '法规库', path: '/regulations' }
    ]
  },
  {
    key: 'agents',
    label: '智能体中心',
    desc: '研发核心能力入口',
    items: [
      { label: '立项评估', path: '/agents/project-evaluation' },
      { label: '配伍分析', path: '/agents/formula-compatibility' },
      { label: '机制推演', path: '/agents/mechanism-inference' },
      { label: '靶点预测', path: '/agents/target-prediction' },
      { label: '文献分析', path: '/agents/literature-analysis' },
      { label: '专利分析', path: '/agents/patent-analysis' },
      { label: '实验设计', path: '/agents/experiment-design' },
      { label: '报告生成', path: '/agents/report-generation' }
    ]
  },
  {
    key: 'me',
    label: '个人中心',
    desc: '项目 / 报告 / 收藏 / 设置',
    items: [
      { label: '我的项目', path: '/me/projects' },
      { label: '我的报告', path: '/me/reports' },
      { label: '我的收藏', path: '/user/1/favorites' },
      { label: '个人设置', path: '/user/1/settings' }
    ]
  }
]

const activeGroup = computed(() => {
  const g = groups.find(g => g.key === activeGroupKey.value)
  return g || groups[0]!
})

function close() {
  open.value = false
}

function go(path: string) {
  close()
  if (route.path !== path) router.push(path)
}
</script>

<template>
  <teleport to="body">
    <transition name="mm-fade">
      <div v-if="open" class="mm-mask" @click.self="close">
        <div class="mm-panel">
          <div class="mm-top">
            <div class="mm-title">模块导航</div>
            <el-button text @click="close">关闭</el-button>
          </div>
          <div class="mm-body">
            <div class="mm-groups">
              <button
                v-for="g in groups"
                :key="g.key"
                class="mm-group"
                :class="{ active: activeGroupKey === g.key }"
                @click="activeGroupKey = g.key"
              >
                <div class="mm-group-title">{{ g.label }}</div>
                <div class="mm-group-desc">{{ g.desc }}</div>
              </button>
            </div>
            <div class="mm-items">
              <div class="mm-items-head">
                <div class="mm-items-title">{{ activeGroup.label }}</div>
                <div class="mm-items-desc">{{ activeGroup.desc }}</div>
              </div>
              <div class="mm-items-grid">
                <button
                  v-for="it in activeGroup.items"
                  :key="it.path"
                  class="mm-item"
                  @click="go(it.path)"
                >
                  {{ it.label }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </transition>
  </teleport>
</template>

<style scoped>
.mm-mask {
  position: fixed;
  inset: 0;
  z-index: 9999;
  background: rgba(2, 10, 8, 0.55);
  backdrop-filter: blur(8px);
  display: grid;
  place-items: center;
  padding: 18px;
}

.mm-panel {
  width: min(1080px, 100%);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  background: linear-gradient(145deg, rgba(247, 255, 252, 0.92), rgba(232, 246, 242, 0.86));
  box-shadow: 0 26px 70px rgba(0, 0, 0, 0.4);
  overflow: hidden;
}

.mm-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.mm-title {
  font-size: 16px;
  font-weight: 800;
  color: #163b30;
}

.mm-body {
  display: grid;
  grid-template-columns: 320px 1fr;
  min-height: 520px;
}

.mm-groups {
  padding: 14px;
  border-right: 1px solid rgba(0, 0, 0, 0.06);
  background: linear-gradient(180deg, rgba(18, 59, 48, 0.08), rgba(18, 59, 48, 0.02));
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.mm-group {
  border: 1px solid rgba(18, 59, 48, 0.12);
  background: rgba(255, 255, 255, 0.55);
  border-radius: 14px;
  padding: 12px 12px;
  text-align: left;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
}

.mm-group:hover {
  transform: translateY(-2px);
  box-shadow: 0 14px 28px rgba(7, 38, 30, 0.12);
}

.mm-group.active {
  background: linear-gradient(145deg, rgba(146, 230, 202, 0.8), rgba(200, 169, 103, 0.25));
  border-color: rgba(18, 59, 48, 0.18);
}

.mm-group-title {
  font-weight: 800;
  color: #123b30;
}

.mm-group-desc {
  margin-top: 6px;
  font-size: 12px;
  color: #5c736b;
}

.mm-items {
  padding: 16px;
}

.mm-items-head {
  margin-bottom: 12px;
}

.mm-items-title {
  font-size: 18px;
  font-weight: 900;
  color: #123b30;
}

.mm-items-desc {
  margin-top: 6px;
  font-size: 12px;
  color: #5c736b;
}

.mm-items-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.mm-item {
  border: 1px solid rgba(18, 59, 48, 0.12);
  background: rgba(255, 255, 255, 0.62);
  border-radius: 14px;
  padding: 12px 12px;
  cursor: pointer;
  font-weight: 700;
  color: #193e33;
  transition: transform 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
}

.mm-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 14px 28px rgba(7, 38, 30, 0.12);
  background: rgba(255, 255, 255, 0.8);
}

.mm-fade-enter-active,
.mm-fade-leave-active {
  transition: opacity 0.18s ease;
}
.mm-fade-enter-from,
.mm-fade-leave-to {
  opacity: 0;
}

@media (max-width: 980px) {
  .mm-body {
    grid-template-columns: 1fr;
  }
  .mm-groups {
    border-right: 0;
    border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  }
  .mm-items-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>

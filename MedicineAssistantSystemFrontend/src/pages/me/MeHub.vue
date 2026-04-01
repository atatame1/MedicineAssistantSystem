<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()
const uid = computed(() => auth.user?.userId || 0)
const items = computed(() => [
  { name: '我的资料', desc: '资料与统计信息', icon: '👤', path: '/me/profile' },
  { name: '我的任务', desc: '待办与截止', icon: '✅', path: '/me/tasks' },
  { name: '我的项目', desc: '参与与创建', icon: '📌', path: '/me/projects' },
  { name: '我的报告', desc: '项目产出与文档', icon: '📝', path: '/me/reports' },
  { name: '我的收藏', desc: '收藏列表与统计', icon: '⭐', path: `/user/${uid.value}/favorites` },
  { name: '个人设置', desc: '偏好与配置', icon: '⚙️', path: `/user/${uid.value}/settings` }
])

function go(p: string) {
  router.push(p)
}
</script>

<template>
  <div class="hub">
    <div class="head">
      <div>
        <div class="t">个人中心</div>
        <div class="s">常用入口</div>
      </div>
    </div>

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
  gap: 14px;
}
.head {
  display: flex;
  justify-content: space-between;
  align-items: end;
  flex-wrap: wrap;
  gap: 12px;
}
.t {
  font-size: 20px;
  font-weight: 950;
  color: #123b30;
}
.s {
  margin-top: 6px;
  font-size: 12px;
  color: #5c736b;
}
.grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
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
  background: radial-gradient(circle at 20% 10%, rgba(115, 209, 180, 0.28), transparent 55%),
    radial-gradient(circle at 80% 40%, rgba(200, 169, 103, 0.18), transparent 60%);
  filter: blur(14px);
  opacity: 0.5;
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
@media (max-width: 860px) {
  .grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>


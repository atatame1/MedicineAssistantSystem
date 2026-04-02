<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { onBeforeUnmount, onMounted, ref } from 'vue'
import LoginDialog from '@/components/LoginDialog.vue'
import { useAuthStore } from '@/stores/auth'
import defaultAvatar from '@/assets/shark_cute2.jpg'
import { logout } from '@/api/auth'

type NavItem = {
  path: string
  label: string
  icon: string
}

const route = useRoute()
const router = useRouter()
const loginOpen = ref(false)
const auth = useAuthStore()
const userMenuOpen = ref(false)
const displayName = computed(() => auth.user?.nickname || auth.user?.username || '游客')
const pendingTo = ref<string | null>(null)

const navItems: NavItem[] = [
  { path: '/portal', label: '智研门户', icon: '◉' },
  { path: '/knowledge', label: '知识枢库', icon: '◇' },
  { path: '/agents', label: '灵智工坊', icon: '▣' },
  { path: '/projects', label: '自由探索', icon: '△' },
  { path: '/me', label: '我的空间', icon: '★' }
]

const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/projects')) return '/projects'
  if (path.startsWith('/knowledge') || path.startsWith('/literatures') || path.startsWith('/regulations')) return '/knowledge'
  if (path.startsWith('/agents')) return '/agents'
  if (path.startsWith('/me') || path.startsWith('/user/')) return '/me'
  return '/portal'
})

function jump(path: string) {
  if (route.path === path) return
  router.push(path)
}

function requireLogin() {
  loginOpen.value = true
}

function onAuthRequired(ev?: any) {
  pendingTo.value = ev?.detail?.to || pendingTo.value
  requireLogin()
}

async function doLogout() {
  try {
    if (auth.token) await logout({ token: auth.token })
  } catch {}
  auth.clear()
  userMenuOpen.value = false
  if ((route.meta as any)?.requiresAuth) router.push('/portal')
}

onMounted(() => {
  window.addEventListener('auth:required', onAuthRequired as any)
  window.addEventListener('auth:success', () => {
    const to = pendingTo.value
    pendingTo.value = null
    if (to) router.push(to)
  })
})

onBeforeUnmount(() => {
  window.removeEventListener('auth:required', onAuthRequired as any)
})
</script>

<template>
  <div class="app-shell">
    <div class="motion-bg">
      <div class="orb orb-a"></div>
      <div class="orb orb-b"></div>
      <div class="orb orb-c"></div>
      <div class="grid-layer"></div>
    </div>

    <header class="hero">
      <div class="hero-left">
        <div class="brand-badge">药</div>
      </div>

      <div class="hero-center">
        <div class="brand-title">药枢协研——中药新药研发人机协同决策与创新智能体多模态数据交互平台</div>
        <div class="brand-subtitle">记忆超群 · 定时执行 · 变身专家 · 迭代成长</div>
      </div>
      <div class="hero-right">
        <el-popover v-model:visible="userMenuOpen" placement="bottom-end" :width="220" trigger="click">
          <template #reference>
            <button class="user-chip" type="button">
              <img class="user-avatar" :src="defaultAvatar" alt="avatar" />
              <span class="user-name">{{ displayName }}</span>
            </button>
          </template>
          <div class="user-pop">
            <div class="user-pop-name">{{ displayName }}</div>
            <div class="user-pop-sub" v-if="auth.isAuthed"></div>
            <div class="user-pop-sub" v-else>游客模式</div>
            <div class="user-pop-actions">
              <el-button v-if="!auth.isAuthed" type="primary" @click="loginOpen = true; userMenuOpen = false">登录</el-button>
              <el-button v-else type="danger" @click="doLogout">退出登录</el-button>
            </div>
          </div>
        </el-popover>
      </div>
    </header>

    <main class="viewport">
      <section class="content-panel">
        <router-view v-slot="{ Component, route: currentRoute }">
          <transition name="page-swap" mode="out-in">
            <component :is="Component" :key="currentRoute.fullPath" />
          </transition>
        </router-view>
      </section>
    </main>

    <nav class="dock">
      <button
        v-for="item in navItems"
        :key="item.path"
        class="dock-item"
        :class="{ active: activeMenu === item.path }"
        @click="jump(item.path)"
      >
        <span class="dock-icon">{{ item.icon }}</span>
        <span class="dock-label">{{ item.label }}</span>
      </button>
    </nav>

    <LoginDialog v-model="loginOpen" />
  </div>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  padding: 22px 20px 96px;
  background: radial-gradient(circle at 30% 20%, #294f45 0%, #102f2a 45%, #0a221f 100%);
}

.motion-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.orb {
  position: absolute;
  border-radius: 999px;
  filter: blur(8px);
  opacity: 0.4;
}

.orb-a {
  width: 380px;
  height: 380px;
  left: -120px;
  top: -100px;
  background: #73d1b4;
  animation: float-a 12s ease-in-out infinite;
}

.orb-b {
  width: 320px;
  height: 320px;
  right: -80px;
  top: 20%;
  background: #c8a967;
  animation: float-b 10s ease-in-out infinite;
}

.orb-c {
  width: 260px;
  height: 260px;
  left: 30%;
  bottom: -100px;
  background: #4a8f7b;
  animation: float-c 14s ease-in-out infinite;
}

.grid-layer {
  position: absolute;
  inset: 0;
  background-image: linear-gradient(rgba(255, 255, 255, 0.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.04) 1px, transparent 1px);
  background-size: 28px 28px;
  mask-image: radial-gradient(circle at 60% 20%, black 20%, transparent 80%);
}

.hero {
  position: relative;
  z-index: 2;
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.hero-left {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 10px;
  min-width: 0;
}

.hero-center {
  min-width: 0;
  text-align: center;
}

.brand-badge {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  background: linear-gradient(145deg, #d9b66f, #a7864a);
  color: #22332d;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10px 24px rgba(200, 169, 103, 0.26);
}

.brand-title {
  color: #e9f4ef;
  font-size: 19px;
  font-weight: 900;
  line-height: 1.2;
  max-width: min(860px, 74vw);
  display: -webkit-box;
  line-clamp: 2;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.brand-subtitle {
  margin-top: 2px;
  color: #bdd4cc;
  font-size: 14px;
}

.hero-right {
  display: flex;
  align-items: center;
  gap: 12px;
  justify-content: flex-end;
}

.user-chip {
  border: 1px solid rgba(255, 255, 255, 0.22);
  background: rgba(14, 42, 36, 0.45);
  backdrop-filter: blur(10px);
  border-radius: 999px;
  padding: 6px 10px 6px 6px;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #e9f4ef;
  transition: transform 0.2s ease, background 0.2s ease;
}

.user-chip:hover {
  transform: translateY(-1px);
  background: rgba(14, 42, 36, 0.58);
}

.user-avatar {
  width: 28px;
  height: 28px;
  border-radius: 999px;
  object-fit: cover;
  border: 1px solid rgba(255, 255, 255, 0.25);
}

.user-name {
  font-size: 12px;
  font-weight: 800;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-pop-name {
  font-weight: 900;
  color: #123b30;
}

.user-pop-sub {
  margin-top: 6px;
  font-size: 12px;
  color: #5c736b;
}

.user-pop-actions {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}

.viewport {
  position: relative;
  z-index: 2;
}

.content-panel {
  border: 0;
  border-radius: 0;
  background: transparent;
  backdrop-filter: none;
  padding: 0;
  box-shadow: none;
}

.dock {
  position: fixed;
  left: 50%;
  bottom: 18px;
  transform: translateX(-50%);
  z-index: 10;
  background: rgba(14, 42, 36, 0.74);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  backdrop-filter: blur(10px);
  display: flex;
  gap: 6px;
  padding: 8px;
}

.dock-item {
  border: 0;
  border-radius: 12px;
  background: transparent;
  color: #c6ddd5;
  padding: 8px 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 3px;
  min-width: 58px;
  cursor: pointer;
  transition: all 0.25s ease;
}

.dock-icon {
  font-size: 14px;
  line-height: 1;
}

.dock-label {
  font-size: 11px;
}

.dock-item:hover {
  transform: translateY(-2px);
  color: #ffffff;
  background: rgba(255, 255, 255, 0.12);
}

.dock-item.active {
  color: #0f2b24;
  background: linear-gradient(145deg, #92e6ca, #67b596);
  box-shadow: 0 10px 22px rgba(72, 147, 119, 0.5);
}

.page-swap-enter-active,
.page-swap-leave-active {
  transition: all 0.24s ease;
}

.page-swap-enter-from {
  opacity: 0;
  transform: translateY(8px) scale(0.99);
}

.page-swap-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(1.01);
}

@keyframes float-a {
  0%,
  100% {
    transform: translate(0, 0);
  }
  50% {
    transform: translate(24px, 18px);
  }
}

@keyframes float-b {
  0%,
  100% {
    transform: translate(0, 0);
  }
  50% {
    transform: translate(-18px, -16px);
  }
}

@keyframes float-c {
  0%,
  100% {
    transform: translate(0, 0);
  }
  50% {
    transform: translate(10px, -24px);
  }
}

@media (max-width: 900px) {
  .app-shell {
    padding: 14px 10px 96px;
  }

  .content-panel {
    border-radius: 14px;
    padding: 10px;
  }

  .dock {
    width: calc(100vw - 14px);
    justify-content: space-between;
    border-radius: 12px;
    padding: 6px;
  }

  .dock-item {
    min-width: auto;
    flex: 1;
    padding: 7px 4px;
  }

  .hero {
    grid-template-columns: 1fr auto;
    grid-template-areas:
      'left right'
      'center center';
    row-gap: 10px;
  }

  .hero-left {
    grid-area: left;
  }

  .hero-right {
    grid-area: right;
    width: auto;
    justify-content: flex-end;
  }

  .hero-center {
    grid-area: center;
    text-align: left;
  }
}
</style>

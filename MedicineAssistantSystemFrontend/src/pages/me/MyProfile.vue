<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { getMyProfile, type UserProfileResponse } from '@/api/user'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const userId = computed(() => auth.user?.userId || 0)
const loading = ref(false)
const error = ref<string | null>(null)
const profile = ref<UserProfileResponse | null>(null)

async function load() {
  loading.value = true
  error.value = null
  try {
    profile.value = await getMyProfile(userId.value)
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="page">
    <div class="head">
      <div>
        <div class="t">我的资料</div>
        <div class="s">资料与统计信息</div>
      </div>
      <div class="act">
        <el-button :loading="loading" @click="load">加载</el-button>
      </div>
    </div>

    <el-alert v-if="error" :title="error" type="error" show-icon />

    <div class="grid">
      <el-card v-loading="loading" class="profile-card">
        <div class="profile">
          <div class="avatar">
            <span>{{ (profile?.username || 'U').slice(0, 1).toUpperCase() }}</span>
          </div>
          <div class="meta">
            <div class="name">{{ profile?.username ?? '未命名用户' }}</div>
            <div class="sub">
              <el-tag effect="light" type="success">用户ID {{ profile?.userId ?? '-' }}</el-tag>
              <el-tag effect="light">{{ profile?.role ?? '未知角色' }}</el-tag>
            </div>
          </div>
        </div>
        <div class="stats">
          <div class="stat">
            <div class="n">{{ profile?.taskCount ?? '-' }}</div>
            <div class="k">任务</div>
          </div>
          <div class="stat">
            <div class="n">{{ profile?.projectCount ?? '-' }}</div>
            <div class="k">项目</div>
          </div>
          <div class="stat">
            <div class="n">{{ profile?.favoriteCount ?? '-' }}</div>
            <div class="k">收藏</div>
          </div>
        </div>
      </el-card>

      <el-card class="actions-card">
        <div class="at">快捷入口</div>
        <div class="actions">
          <el-button @click="$router.push('/me/tasks')">我的任务</el-button>
          <el-button @click="$router.push('/me/projects')">我的项目</el-button>
          <el-button @click="$router.push('/me/reports')">我的报告</el-button>
          <el-button @click="$router.push(`/user/${userId}/favorites`)">我的收藏</el-button>
          <el-button @click="$router.push(`/user/${userId}/settings`)">个人设置</el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}
.t {
  font-size: 18px;
  font-weight: 900;
  color: #123b30;
}
.s {
  margin-top: 6px;
  font-size: 12px;
  color: #5c736b;
}
.act {
  display: flex;
  gap: 10px;
  align-items: center;
}

.grid {
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  gap: 12px;
}

.profile {
  display: flex;
  gap: 12px;
  align-items: center;
}

.avatar {
  width: 56px;
  height: 56px;
  border-radius: 18px;
  display: grid;
  place-items: center;
  background: linear-gradient(145deg, rgba(146, 230, 202, 0.75), rgba(200, 169, 103, 0.25));
  color: #10382e;
  font-weight: 950;
  font-size: 18px;
}

.name {
  font-size: 18px;
  font-weight: 950;
  color: #123b30;
}

.sub {
  margin-top: 8px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.stats {
  margin-top: 14px;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.stat {
  border: 0;
  border-radius: 16px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(12px);
}

.stat .n {
  font-size: 22px;
  font-weight: 950;
  color: #123b30;
}

.stat .k {
  margin-top: 6px;
  font-size: 12px;
  color: #5c736b;
}

.at {
  font-size: 16px;
  font-weight: 950;
  color: #123b30;
}

.actions {
  margin-top: 12px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

@media (max-width: 980px) {
  .grid {
    grid-template-columns: 1fr;
  }
}
</style>


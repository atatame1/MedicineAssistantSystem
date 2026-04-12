<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getUserSettings, updateUserSettings, type SettingsResponse } from '@/api/user'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const userId = computed(() => auth.user?.userId || Number(route.params.userId))

const loading = ref(false)
const error = ref<string | null>(null)
const form = ref<string>('')

async function load() {
  loading.value = true
  error.value = null
  try {
    const res = (await getUserSettings(userId.value)) as SettingsResponse
    form.value = res?.preferences ?? ''
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    loading.value = false
  }
}

async function submit() {
  loading.value = true
  error.value = null
  try {
    await updateUserSettings(userId.value, { preferences: form.value })
    await load()
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (auth.user?.userId && Number(route.params.userId) !== auth.user.userId) {
    router.replace(`/user/${auth.user.userId}/settings`)
    return
  }
  load()
})
</script>

<template>
  <div class="page-wrap">
    <header class="page-head">
      <div>
        <p class="kicker">偏好</p>
        <h1 class="title">个人设置</h1>
        <p class="sub">同步到账号，随时可改</p>
      </div>
      <el-button round @click="$router.push(`/user/${userId}/favorites`)">我的收藏</el-button>
    </header>

    <div class="panel">
      <el-tag type="success" class="mb-12" effect="dark">用户ID：{{ userId }}</el-tag>
      <el-alert v-if="error" type="error" show-icon :title="error" class="mb-12" />

      <el-form :model="{ preferences: form }" label-width="100px" label-position="top">
        <el-form-item label="偏好配置（JSON 或文本）">
          <el-input v-model="form" type="textarea" :rows="12" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" round :loading="loading" @click="submit">保存</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.page-wrap {
  max-width: 980px;
  width: 100%;
  margin: 0 auto;
  animation: in 0.5s ease both;
}

@keyframes in {
  from {
    opacity: 0;
    transform: translateY(12px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.page-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 16px;
}

.kicker {
  margin: 0 0 6px;
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.42);
}

.title {
  margin: 0;
  font-size: 24px;
  font-weight: 950;
  color: rgba(255, 255, 255, 0.94);
}

.sub {
  margin: 8px 0 0;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.55);
}

.panel {
  border-radius: 22px;
  padding: 20px;
  background: linear-gradient(165deg, rgba(255, 255, 255, 0.06), rgba(10, 38, 32, 0.36));
  backdrop-filter: blur(14px);
  border: 1px solid rgba(115, 209, 180, 0.2);
}

.mb-12 {
  margin-bottom: 12px;
}

.panel :deep(.el-form-item__label) {
  color: rgba(255, 255, 255, 0.78);
}

.panel :deep(.el-input__wrapper),
.panel :deep(.el-textarea__inner) {
  background: rgba(0, 0, 0, 0.22) !important;
  border: 1px solid rgba(255, 255, 255, 0.14);
  box-shadow: none !important;
  color: rgba(255, 255, 255, 0.92);
}

.panel :deep(.el-textarea__inner::placeholder) {
  color: rgba(255, 255, 255, 0.45);
}

.panel :deep(.el-tag.el-tag--success.el-tag--dark) {
  background: rgba(14, 42, 36, 0.76);
  border-color: rgba(115, 209, 180, 0.32);
  color: rgba(255, 255, 255, 0.9);
}
</style>


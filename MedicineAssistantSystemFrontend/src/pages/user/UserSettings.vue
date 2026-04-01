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
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户设置</span>
          <el-button text @click="$router.push(`/user/${userId}/favorites`)">查看收藏</el-button>
        </div>
      </template>

      <el-tag type="success" class="mb-12">用户ID：{{ userId }}</el-tag>
      <el-alert v-if="error" type="error" show-icon :title="error" class="mb-12" />

      <el-form :model="{ preferences: form }" label-width="120px">
        <el-form-item label="preferences">
          <el-input v-model="form" type="textarea" :rows="10" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="submit">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.page-wrap {
  max-width: 900px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.mb-12 {
  margin-bottom: 12px;
}
</style>


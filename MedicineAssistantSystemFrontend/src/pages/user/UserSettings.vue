<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { getUserSettings, updateUserSettings, type SettingsResponse } from '@/api/user'

const route = useRoute()
const userId = computed(() => Number(route.params.userId))

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

onMounted(load)
</script>

<template>
  <div>
    <div style="display: flex; gap: 12px; margin-bottom: 12px">
      <router-link :to="`/user/${userId}/favorites`">返回收藏</router-link>
    </div>

    <div style="margin-bottom: 12px">用户ID：{{ userId }}</div>

    <el-alert v-if="error" type="error" show-icon :title="error" style="margin-bottom: 12px" />

    <el-form :model="{ preferences: form }" label-width="120px">
      <el-form-item label="preferences（前端原样字符串）">
        <el-input v-model="form" type="textarea" :rows="8" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="submit">保存</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped></style>


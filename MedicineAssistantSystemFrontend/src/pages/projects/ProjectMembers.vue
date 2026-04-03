<script setup lang="ts">
import { ref } from 'vue'
import { projectsExtraApi, type MemberCreate, type ProjectMember } from '@/api/projectsExtra'

const projectId = ref<number>(1)
const loading = ref(false)
const error = ref<string | null>(null)
const rows = ref<ProjectMember[]>([])

const createOpen = ref(false)
const saving = ref(false)
const createForm = ref<MemberCreate>({ userId: 2, role: 'MEMBER', joinTime: null })

async function load() {
  loading.value = true
  error.value = null
  try {
    rows.value = await projectsExtraApi.members(projectId.value)
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    loading.value = false
  }
}

async function createOne() {
  saving.value = true
  error.value = null
  try {
    await projectsExtraApi.createMember(projectId.value, createForm.value)
    createOpen.value = false
    await load()
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    saving.value = false
  }
}

load()
</script>

<template>
  <div class="pt-tool">
    <div class="head">
      <div>
        <div class="t">项目成员</div>
        <div class="s">成员列表 / 新增成员</div>
      </div>
      <div class="act">
        <el-input-number v-model="projectId" :min="1" :step="1" style="width: 140px" />
        <el-button :loading="loading" @click="load">加载</el-button>
        <el-button type="primary" @click="createOpen = true">新增成员</el-button>
      </div>
    </div>

    <el-alert v-if="error" :title="error" type="error" show-icon />

    <div class="pt-panel">
      <el-table v-loading="loading" class="pt-table" :data="rows" stripe>
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column label="昵称" min-width="140">
          <template #default="{ row }">
            {{ row.userNickname?.trim() || '—' }}
          </template>
        </el-table-column>
        <el-table-column prop="userId" label="用户ID" width="120" />
        <el-table-column prop="role" label="角色" width="140" />
        <el-table-column prop="joinTime" label="加入时间" width="200" />
      </el-table>
    </div>

    <el-dialog v-model="createOpen" class="pt-tool-dialog" title="新增成员" width="660px">
      <el-form :model="createForm" label-width="110px">
        <el-form-item label="用户ID">
          <el-input-number v-model="createForm.userId" :min="1" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="createForm.role">
            <el-option label="负责人" value="LEAD" />
            <el-option label="普通成员" value="MEMBER" />
            <el-option label="评审员" value="REVIEWER" />
            <el-option label="观察员" value="OBSERVER" />
          </el-select>
        </el-form-item>
        <el-form-item label="加入时间">
          <el-date-picker v-model="createForm.joinTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createOpen = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="createOne">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style src="./projectToolPage.css"></style>

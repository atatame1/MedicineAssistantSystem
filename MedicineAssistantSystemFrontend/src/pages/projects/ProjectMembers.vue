<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { ElMessageBox } from 'element-plus'
import { listMyProjects, type MyProjectItem } from '@/api/projects'
import { projectsExtraApi, type MemberCreate, type ProjectMember } from '@/api/projectsExtra'
import { listUsers, type UserListItem } from '@/api/user'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()

const projectId = ref<number | null>(null)
const mineProjects = ref<MyProjectItem[]>([])
const projectsLoading = ref(false)

const users = ref<UserListItem[]>([])
const usersLoading = ref(false)

const loading = ref(false)
const error = ref<string | null>(null)
const rows = ref<ProjectMember[]>([])

const createOpen = ref(false)
const saving = ref(false)
const deletingId = ref<number | null>(null)
const createForm = ref<Omit<MemberCreate, 'userId'> & { userId: number | null }>({
  userId: null,
  role: 'MEMBER',
  joinTime: null
})

const leaderProjects = computed(() =>
  mineProjects.value.filter(
    (p) => p.currentUserRole === 'LEADER' && p.id != null
  ) as (MyProjectItem & { id: number })[]
)

const MEMBER_ROLES = [
  { value: 'LEAD', label: '负责人' },
  { value: 'MEMBER', label: '普通成员' },
  { value: 'REVIEWER', label: '评审员' },
  { value: 'OBSERVER', label: '观察员' }
] as const

function roleLabel(v: string | null | undefined) {
  if (v == null || v === '') return '—'
  const hit = MEMBER_ROLES.find((r) => r.value === v)
  return hit ? hit.label : v
}

function userLabel(u: UserListItem) {
  const n = u.nickname?.trim()
  if (n) return n
  if (u.username?.trim()) return u.username
  return `用户 ${u.id}`
}

async function loadProjectOptions() {
  const uid = auth.user?.userId
  if (!uid) {
    mineProjects.value = []
    projectId.value = null
    return
  }
  projectsLoading.value = true
  try {
    mineProjects.value = await listMyProjects(uid)
    if (projectId.value == null && leaderProjects.value.length > 0) {
      const first = leaderProjects.value[0]?.id
      projectId.value = first != null ? first : null
    }
  } catch {
    mineProjects.value = []
    projectId.value = null
  } finally {
    projectsLoading.value = false
  }
}

async function loadUsers() {
  usersLoading.value = true
  try {
    users.value = await listUsers()
  } catch {
    users.value = []
  } finally {
    usersLoading.value = false
  }
}

async function load() {
  if (projectId.value == null) {
    rows.value = []
    return
  }
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
  if (projectId.value == null || createForm.value.userId == null) {
    error.value = '请选择项目与用户'
    return
  }
  saving.value = true
  error.value = null
  try {
    const body: MemberCreate = {
      userId: createForm.value.userId,
      role: createForm.value.role,
      joinTime: createForm.value.joinTime
    }
    await projectsExtraApi.createMember(projectId.value, body)
    createOpen.value = false
    createForm.value = { userId: null, role: 'MEMBER', joinTime: null }
    await load()
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    saving.value = false
  }
}

function openCreate() {
  createForm.value = { userId: null, role: 'MEMBER', joinTime: null }
  createOpen.value = true
}

async function removeMember(row: ProjectMember) {
  if (projectId.value == null) return
  try {
    await ElMessageBox.confirm('确定从该项目移除该成员？', '提示', { type: 'warning' })
  } catch {
    return
  }
  deletingId.value = row.id
  error.value = null
  try {
    await projectsExtraApi.deleteMember(projectId.value, row.id)
    await load()
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    deletingId.value = null
  }
}

watch(projectId, () => {
  load()
})

onMounted(async () => {
  await Promise.all([loadProjectOptions(), loadUsers()])
})
</script>

<template>
  <div class="pt-tool">
    <div class="head">
      <div>
        <div class="t">项目成员</div>
        <div class="s">成员列表 / 新增成员（仅本人为负责人的项目）</div>
      </div>
      <div class="act">
        <el-select
          v-model="projectId"
          placeholder="选择项目"
          filterable
          clearable
          style="width: 240px"
          :loading="projectsLoading"
        >
          <el-option
            v-for="p in leaderProjects"
            :key="p.id"
            :label="p.projectName"
            :value="p.id"
          />
        </el-select>
        <el-button :loading="loading" @click="load">加载</el-button>
        <el-button type="primary" :disabled="projectId == null" @click="openCreate">新增成员</el-button>
      </div>
    </div>

    <el-alert v-if="error" :title="error" type="error" show-icon />

    <div class="pt-panel">
      <el-table v-loading="loading" class="pt-table" :data="rows" stripe>
        <el-table-column label="昵称" min-width="140">
          <template #default="{ row }">
            {{ row.userNickname?.trim() || '—' }}
          </template>
        </el-table-column>
        <el-table-column prop="userId" label="用户ID" width="120" />
        <el-table-column label="角色" width="140">
          <template #default="{ row }">
            {{ roleLabel(row.role) }}
          </template>
        </el-table-column>
        <el-table-column prop="joinTime" label="加入时间" width="200" />
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="danger"
              :loading="deletingId === row.id"
              @click="removeMember(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="createOpen" class="pt-tool-dialog" title="新增成员" width="660px">
      <el-form :model="createForm" label-width="110px">
        <el-form-item label="用户">
          <el-select
            v-model="createForm.userId"
            placeholder="请选择用户"
            filterable
            clearable
            style="width: 100%"
            :loading="usersLoading"
          >
            <el-option v-for="u in users" :key="u.id" :label="userLabel(u)" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="createForm.role" style="width: 100%">
            <el-option v-for="r in MEMBER_ROLES" :key="r.value" :label="r.label" :value="r.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="加入时间">
          <el-date-picker v-model="createForm.joinTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" />
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

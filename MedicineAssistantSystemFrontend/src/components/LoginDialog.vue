<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import { login } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'

const props = defineProps<{ modelValue: boolean }>()
const emit = defineEmits<{ (e: 'update:modelValue', v: boolean): void }>()

const auth = useAuthStore()
const open = ref(props.modelValue)
watch(
  () => props.modelValue,
  (v) => (open.value = v)
)
watch(open, (v) => emit('update:modelValue', v))

const loading = ref(false)
const error = ref<string | null>(null)
const form = reactive({ username: '', password: '' })
const remember = ref(true)

async function submit() {
  loading.value = true
  error.value = null
  try {
    const res = await login({ username: form.username.trim(), password: form.password })
    auth.setAuth(res.token, { userId: res.userId, username: res.username, nickname: res.nickname ?? null })
    open.value = false
    form.password = ''
    try {
      window.dispatchEvent(new CustomEvent('auth:success'))
    } catch {}
  } catch (e: any) {
    error.value = String(e?.message || e)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <el-dialog v-model="open" width="820px" class="login-dialog" :show-close="false" align-center>
    <template #header>
      <div class="hd">
        <div class="hd-left">
          <div class="badge">药</div>
          <div>
            <div class="hd-title">欢迎回来</div>
            <div class="hd-sub">登录后解锁收藏、智能体与个人中心</div>
          </div>
        </div>
        <el-button text class="close" @click="open = false">关闭</el-button>
      </div>
    </template>

    <div class="wrap">
      <div class="left">
        <div class="hero">
          <div class="hero-title">中药智研引擎</div>
          <div class="hero-sub">人机协同研发平台 · 安全访问</div>
        </div>

        <div class="benefits">
          <div class="b-item">
            <div class="dot"></div>
            <div>
              <div class="b-title">游客可用</div>
              <div class="b-sub">文献/法规/知识检索与浏览无需登录</div>
            </div>
          </div>
          <div class="b-item">
            <div class="dot"></div>
            <div>
              <div class="b-title">登录后解锁</div>
              <div class="b-sub">收藏、智能体对话、个人任务/报告/设置</div>
            </div>
          </div>
          <div class="b-item">
            <div class="dot"></div>
            <div>
              <div class="b-title">账号说明</div>
              <div class="b-sub">当前项目为固定账号（不支持注册）</div>
            </div>
          </div>
        </div>

        <div class="tips">
          <div class="tips-title">小提示</div>
          <div class="tips-sub">若你只想体验，可以先以游客浏览，再在需要时登录</div>
        </div>
      </div>

      <div class="right">
        <div class="form-title">账号登录</div>
        <div class="form-sub">请输入管理员分配的账号密码</div>

        <el-alert v-if="error" :title="error" type="error" show-icon class="err" />

        <el-form :model="form" label-position="top" class="form">
          <el-form-item label="账号">
            <el-input v-model="form.username" autocomplete="username" placeholder="请输入账号" size="large" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input
              v-model="form.password"
              type="password"
              show-password
              autocomplete="current-password"
              placeholder="请输入密码"
              size="large"
              @keydown.enter.prevent="submit"
            />
          </el-form-item>
          <div class="row">
            <el-checkbox v-model="remember">记住登录</el-checkbox>
            <div class="hint">涉及收藏/智能体将自动触发登录</div>
          </div>
          <el-button type="primary" class="btn" size="large" :loading="loading" @click="submit">
            登录并继续
          </el-button>
          <el-button class="btn-ghost" size="large" @click="open = false">先以游客身份继续</el-button>
          <div class="fine">
            登录代表你同意以项目账号访问系统能力
          </div>
        </el-form>
      </div>
    </div>
    <template #footer>
      <div class="ft">
        <div class="ft-left">遇到登录问题：请联系管理员重置账号密码</div>
        <div class="ft-right">
          <el-button text @click="open = false">关闭</el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped>
.login-dialog :deep(.el-dialog) {
  border-radius: 22px;
  overflow: hidden;
  padding: 0;
}

.login-dialog :deep(.el-dialog__header) {
  margin-right: 0;
  padding: 0;
}

.login-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.login-dialog :deep(.el-dialog__footer) {
  padding: 10px 16px 14px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
  background: rgba(255, 255, 255, 0.65);
  backdrop-filter: blur(10px);
}

.hd {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  background: linear-gradient(90deg, rgba(18, 59, 48, 0.1), rgba(200, 169, 103, 0.08));
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.hd-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.badge {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  background: linear-gradient(145deg, rgba(146, 230, 202, 0.8), rgba(200, 169, 103, 0.3));
  color: #10382e;
  font-weight: 950;
}

.hd-title {
  font-size: 16px;
  font-weight: 950;
  color: #123b30;
}

.hd-sub {
  margin-top: 4px;
  font-size: 14px;
  color: #5c736b;
}

.wrap {
  display: grid;
  grid-template-columns: 1.05fr 1fr;
  min-height: 420px;
}

.left {
  padding: 18px 18px 16px;
  background: radial-gradient(circle at 20% 20%, rgba(115, 209, 180, 0.22), transparent 55%),
    radial-gradient(circle at 70% 40%, rgba(200, 169, 103, 0.18), transparent 60%),
    linear-gradient(180deg, rgba(18, 59, 48, 0.06), rgba(18, 59, 48, 0.02));
  border-right: 1px solid rgba(0, 0, 0, 0.06);
}

.hero-title {
  font-size: 18px;
  font-weight: 950;
  color: #123b30;
}

.hero-sub {
  margin-top: 6px;
  font-size: 14px;
  color: #5c736b;
}

.benefits {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.b-item {
  display: flex;
  gap: 10px;
  align-items: flex-start;
  padding: 12px;
  border-radius: 16px;
  border: 1px solid rgba(18, 59, 48, 0.12);
  background: rgba(255, 255, 255, 0.58);
  backdrop-filter: blur(10px);
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  background: linear-gradient(145deg, #92e6ca, #c8a967);
  margin-top: 6px;
  box-shadow: 0 8px 18px rgba(72, 147, 119, 0.3);
}

.b-title {
  font-weight: 900;
  color: #123b30;
}

.b-sub {
  margin-top: 6px;
  font-size: 14px;
  color: #5c736b;
  line-height: 1.4;
}

.tips {
  margin-top: 16px;
  padding: 12px;
  border-radius: 16px;
  border: 1px dashed rgba(18, 59, 48, 0.18);
  background: rgba(255, 255, 255, 0.5);
}

.tips-title {
  font-weight: 900;
  color: #123b30;
}

.tips-sub {
  margin-top: 6px;
  font-size: 14px;
  color: #5c736b;
  line-height: 1.4;
}

.right {
  padding: 18px 18px 16px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.78), rgba(240, 250, 246, 0.58));
}

.form-title {
  font-size: 18px;
  font-weight: 950;
  color: #123b30;
}

.form-sub {
  margin-top: 6px;
  font-size: 14px;
  color: #5c736b;
}

.err {
  margin-top: 12px;
}

.form {
  margin-top: 12px;
}

.row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  margin: 6px 0 10px;
  flex-wrap: wrap;
}

.hint {
  font-size: 14px;
  color: #6a8078;
}

.btn {
  width: 100%;
}

.btn-ghost {
  width: 100%;
  margin-top: 10px;
  margin-left: 0 !important;
}

.fine {
  margin-top: 10px;
  font-size: 14px;
  color: #6a8078;
  line-height: 1.4;
}

.ft {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.ft-left {
  font-size: 14px;
  color: #6a8078;
}

@media (max-width: 920px) {
  .wrap {
    grid-template-columns: 1fr;
  }
  .left {
    border-right: 0;
    border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  }
}
</style>


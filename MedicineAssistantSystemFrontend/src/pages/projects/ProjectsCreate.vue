<script setup lang="ts">
import { useRouter } from 'vue-router'
import ProjectCreateForm from './ProjectCreateForm.vue'

const router = useRouter()

function onSuccess() {
  router.push('/projects')
}
</script>

<template>
  <div class="pc-page">
    <div class="pc-inner">
      <header class="pc-head">
        <div>
          <div class="pc-t">新建项目</div>
          <div class="pc-s">填写立项信息并提交</div>
        </div>
        <el-button text class="pc-back" @click="router.push('/projects')">返回列表</el-button>
      </header>
      <div class="pc-card">
        <ProjectCreateForm @success="onSuccess" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.pc-page {
  max-width: min(1680px, 98vw);
  margin: 0 auto;
  padding: 10px 8px 28px;
  box-sizing: border-box;
}

.pc-inner {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.pc-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  flex-wrap: wrap;
}

.pc-t {
  font-size: 22px;
  font-weight: 950;
  color: rgba(255, 255, 255, 0.94);
}

.pc-s {
  margin-top: 6px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.52);
  font-weight: 700;
}

.pc-back {
  color: rgba(146, 230, 202, 0.95) !important;
  font-weight: 850;
}

.pc-card {
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.22);
  padding: 18px 20px 22px;
}
</style>

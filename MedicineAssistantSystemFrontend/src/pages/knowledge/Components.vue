<script setup lang="ts">
import CrudList from './CrudList.vue'
import { knowledgeApi } from '@/api/knowledge'
</script>

<template>
  <CrudList
    title="成分库"
    placeholder="按关键词检索成分"
    :columns="[
      { prop: 'id', label: 'ID', width: 80 },
      { prop: 'name', label: '名称', minWidth: 220 },
      { prop: 'structure', label: '结构', minWidth: 220 },
      { prop: 'bioactivity', label: '生物活性', minWidth: 260 }
    ]"
    :list="knowledgeApi.components.list"
    :create="knowledgeApi.components.create"
    :update="knowledgeApi.components.update"
    :del="knowledgeApi.components.delete"
    :formSchema="[
      { prop: 'name', label: '名称' },
      { prop: 'structure', label: '化学结构', type: 'textarea' },
      { prop: 'bioactivity', label: '生物活性', type: 'textarea' },
      { prop: 'targetInfo', label: '靶点信息', type: 'textarea' }
    ]"
  />
</template>

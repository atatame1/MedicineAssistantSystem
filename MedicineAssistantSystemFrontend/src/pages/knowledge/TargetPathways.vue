<script setup lang="ts">
import CrudList from './CrudList.vue'
import { knowledgeApi } from '@/api/knowledge'
</script>

<template>
  <CrudList
    title="靶点通路库"
    placeholder="按关键词检索靶点/通路"
    :columns="[
      { prop: 'id', label: 'ID', width: 80 },
      { prop: 'name', label: '靶点', minWidth: 220 },
      { prop: 'pathway', label: '信号通路', minWidth: 260 },
      { prop: 'diseaseRelation', label: '疾病关联', minWidth: 260 }
    ]"
    :list="knowledgeApi.targetPathways.list"
    :create="knowledgeApi.targetPathways.create"
    :update="knowledgeApi.targetPathways.update"
    :del="knowledgeApi.targetPathways.delete"
    :formSchema="[
      { prop: 'name', label: '靶点/名称' },
      { prop: 'pathway', label: '信号通路', type: 'textarea' },
      { prop: 'diseaseRelation', label: '疾病关联', type: 'textarea' },
      { prop: 'description', label: '说明', type: 'textarea' }
    ]"
  />
</template>

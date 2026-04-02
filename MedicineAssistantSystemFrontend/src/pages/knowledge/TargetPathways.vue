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
      { prop: 'pathwayName', label: '信号通路', minWidth: 260 },
      { prop: 'relatedDiseases', label: '关联疾病', minWidth: 260 }
    ]"
    :list="knowledgeApi.targetPathways.list"
    :create="knowledgeApi.targetPathways.create"
    :update="knowledgeApi.targetPathways.update"
    :del="knowledgeApi.targetPathways.delete"
    :formSchema="[
      { prop: 'name', label: '靶点/名称' },
      { prop: 'pathwayName', label: '信号通路', type: 'textarea' },
      { prop: 'relatedDiseases', label: '关联疾病', type: 'textarea' },
      { prop: 'mechanismDescription', label: '机制说明', type: 'textarea' }
    ]"
  />
</template>

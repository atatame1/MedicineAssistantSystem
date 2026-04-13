<script setup lang="ts">
import CrudList from './CrudList.vue'
import { knowledgeApi } from '@/api/knowledge'
import { componentCreate, componentEdit } from './knowledgeFormSchemas'
</script>

<template>
  <CrudList
    title="成分库"
    placeholder="按关键词检索成分"
    :columns="[
      { prop: 'id', label: 'ID', width: 80 },
      { prop: 'name', label: '名称', minWidth: 220 },
      { prop: 'chemicalStructure', label: '结构', minWidth: 220 },
      { prop: 'bioactivity', label: '生物活性', minWidth: 260 }
    ]"
    :list="knowledgeApi.components.list"
    favoriteType="COMPONENT"
    :create="knowledgeApi.components.create"
    :update="knowledgeApi.components.update"
    :del="knowledgeApi.components.delete"
    :form-schema-create="componentCreate"
    :form-schema-edit="componentEdit"
  />
</template>

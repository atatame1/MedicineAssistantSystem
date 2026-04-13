<script setup lang="ts">
import CrudList from './CrudList.vue'
import { knowledgeApi } from '@/api/knowledge'
import { targetPathwayCreate, targetPathwayEdit } from './knowledgeFormSchemas'
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
    :form-schema-create="targetPathwayCreate"
    :form-schema-edit="targetPathwayEdit"
    favorite-type="TARGET_PATHWAY"
    detail-in-tile-header
  />
</template>

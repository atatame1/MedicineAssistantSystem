<script setup lang="ts">
import CrudList from './CrudList.vue'
import { knowledgeApi } from '@/api/knowledge'
</script>

<template>
  <CrudList
    title="方剂库"
    placeholder="按关键词检索方剂"
    :columns="[
      { prop: 'id', label: 'ID', width: 80 },
      { prop: 'name', label: '名称', minWidth: 220 },
      { prop: 'indication', label: '适应症', minWidth: 260 }
    ]"
    :list="knowledgeApi.formulas.list"
    :create="knowledgeApi.formulas.create"
    :update="knowledgeApi.formulas.update"
    :del="knowledgeApi.formulas.delete"
    :formSchema="[
      { prop: 'name', label: '名称' },
      { prop: 'composition', label: '组成', type: 'textarea' },
      { prop: 'indication', label: '适应症', type: 'textarea' },
      { prop: 'analysis', label: '组成分析', type: 'textarea' }
    ]"
  />
</template>

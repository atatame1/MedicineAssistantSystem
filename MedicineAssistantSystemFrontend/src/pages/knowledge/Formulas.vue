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
      { prop: 'indications', label: '适应症', minWidth: 260 }
    ]"
    :list="knowledgeApi.formulas.list"
    favoriteType="FORMULA"
    :create="knowledgeApi.formulas.create"
    :update="knowledgeApi.formulas.update"
    :del="knowledgeApi.formulas.delete"
    :formSchema="[
      { prop: 'name', label: '名称' },
      { prop: 'composition', label: '组成', type: 'textarea' },
      { prop: 'indications', label: '适应症', type: 'textarea' },
      { prop: 'effects', label: '功效', type: 'textarea' }
    ]"
  />
</template>

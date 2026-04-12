<script setup lang="ts">
import CrudList from './CrudList.vue'
import { knowledgeApi } from '@/api/knowledge'
</script>

<template>
  <CrudList
    title="疾病库"
    placeholder="按关键词检索疾病"
    :columns="[
      { prop: 'id', label: 'ID', width: 80 },
      { prop: 'name', label: '名称', minWidth: 220 },
      { prop: 'category', label: '分类', minWidth: 200 },
      { prop: 'tcmSyndrome', label: '中医证候', minWidth: 240 }
    ]"
    :list="knowledgeApi.diseases.list"
    favoriteType="Disease"
    :create="knowledgeApi.diseases.create"
    :update="knowledgeApi.diseases.update"
    :del="knowledgeApi.diseases.delete"
    :formSchema="[
      { prop: 'name', label: '名称' },
      { prop: 'category', label: '分类' },
      { prop: 'tcmSyndrome', label: '中医证候', type: 'textarea' },
      { prop: 'modernName', label: '现代医学名称', type: 'textarea' }
    ]"
  />
</template>

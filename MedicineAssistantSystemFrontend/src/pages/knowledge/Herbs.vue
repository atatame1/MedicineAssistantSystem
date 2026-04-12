<script setup lang="ts">
import CrudList from './CrudList.vue'
import { knowledgeApi } from '@/api/knowledge'
</script>

<template>
  <CrudList
    title="中药材库"
    placeholder="按关键词检索中药材"
    :columns="[
      { prop: 'id', label: 'ID', width: 80 },
      { prop: 'name', label: '名称', minWidth: 200 },
      { prop: 'source', label: '来源', minWidth: 180 },
      { prop: 'effects', label: '功效', minWidth: 240 }
    ]"
    :list="knowledgeApi.herbs.list"
    favoriteType="HERB"
    :create="knowledgeApi.herbs.create"
    :update="knowledgeApi.herbs.update"
    :del="knowledgeApi.herbs.delete"
    :formSchema="[
      { prop: 'name', label: '名称' },
      { prop: 'source', label: '来源' },
      { prop: 'nature', label: '性' },
      { prop: 'taste', label: '味' },
      { prop: 'meridian', label: '归经' },
      { prop: 'effects', label: '功效', type: 'textarea' }
    ]"
  />
</template>

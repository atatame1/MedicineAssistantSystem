<script setup lang="ts">
import CrudList from './CrudList.vue'
import { createRegulation, deleteRegulation, listRegulations, updateRegulation } from '@/api/regulations'
import { regulationCreate, regulationEdit } from './knowledgeFormSchemas'
</script>

<template>
  <CrudList
    title="法规库"
    placeholder="按关键词检索法规"
    :columns="[
      { prop: 'id', label: 'ID', width: 80 },
      { prop: 'name', label: '名称', minWidth: 260 },
      { prop: 'regulationNumber', label: '文号', minWidth: 160 },
      { prop: 'issuingAuthority', label: '发布机构', minWidth: 200 }
    ]"
    :list="listRegulations"
    favoriteType="Regulation"
    :create="createRegulation"
    :update="updateRegulation"
    :del="deleteRegulation"
    :form-schema-create="regulationCreate"
    :form-schema-edit="regulationEdit"
  />
</template>

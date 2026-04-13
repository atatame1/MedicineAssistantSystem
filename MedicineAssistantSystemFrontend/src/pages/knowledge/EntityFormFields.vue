<script setup lang="ts">
import type { FormField } from './formSchemaTypes'

withDefaults(
  defineProps<{
    fields: FormField[]
    model: Record<string, any>
    readonly?: boolean
  }>(),
  { readonly: false }
)
</script>

<template>
  <el-form-item v-for="f in fields" :key="f.prop" :label="f.label">
    <el-input
      v-if="(f.type || 'text') === 'text'"
      v-model="model[f.prop]"
      :disabled="readonly"
      :readonly="readonly"
    />
    <el-input
      v-else-if="f.type === 'textarea'"
      v-model="model[f.prop]"
      type="textarea"
      :autosize="{ minRows: 2, maxRows: 12 }"
      :disabled="readonly"
      :readonly="readonly"
    />
    <el-input-number
      v-else-if="f.type === 'number'"
      v-model="model[f.prop]"
      class="kb-num"
      :disabled="readonly"
    />
    <el-switch v-else-if="f.type === 'switch'" v-model="model[f.prop]" :disabled="readonly" />
    <el-date-picker
      v-else-if="f.type === 'datetime'"
      v-model="model[f.prop]"
      type="datetime"
      value-format="YYYY-MM-DDTHH:mm:ss"
      format="YYYY-MM-DD HH:mm"
      style="width: 100%"
      :clearable="!readonly"
      :disabled="readonly"
    />
    <el-date-picker
      v-else-if="f.type === 'year'"
      v-model="model[f.prop]"
      type="year"
      value-format="YYYY"
      format="YYYY"
      placeholder="选择发表年"
      style="width: 100%"
      :clearable="!readonly"
      :disabled="readonly"
    />
    <el-select
      v-else-if="f.type === 'select'"
      v-model="model[f.prop]"
      class="kb-sel"
      :disabled="readonly"
      :clearable="!readonly"
      filterable
    >
      <el-option
        v-for="o in f.options || []"
        :key="String(o.value) + '-' + o.label"
        :label="o.label"
        :value="o.value"
      />
    </el-select>
  </el-form-item>
</template>

<style scoped>
.kb-num,
.kb-sel {
  width: 100%;
}
</style>

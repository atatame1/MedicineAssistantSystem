<script setup lang="ts"></script>

<template>
  <div style="padding: 16px; font-family: sans-serif">
    <div style="display: flex; gap: 12px; margin-bottom: 16px">
      <router-link to="/projects">Projects</router-link>
      <router-link to="/literatures">Literatures</router-link>
      <router-link :to="`/user/1/settings`">User Settings</router-link>
      <router-link to="/regulations">Regulations</router-link>
    </div>
    <router-view />
  </div>
</template>

<style scoped></style>

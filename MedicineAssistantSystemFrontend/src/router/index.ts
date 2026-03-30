import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/projects' },
    {
      path: '/projects',
      name: 'projects',
      component: () => import('@/pages/projects/ProjectsList.vue')
    },
    {
      path: '/projects/create',
      name: 'projects-create',
      component: () => import('@/pages/projects/ProjectsCreate.vue')
    },
    {
      path: '/literatures',
      name: 'literatures',
      component: () => import('@/pages/literatures/LiteraturesList.vue')
    },
    {
      path: '/literatures/create',
      name: 'literatures-create',
      component: () => import('@/pages/literatures/LiteraturesCreate.vue')
    },
    {
      path: '/literatures/:literatureId/summary',
      name: 'literatures-summary',
      component: () => import('@/pages/literatures/LiteratureSummaryStream.vue')
    },
    {
      path: '/user/:userId/settings',
      name: 'user-settings',
      component: () => import('@/pages/user/UserSettings.vue')
    },
    {
      path: '/user/:userId/favorites',
      name: 'user-favorites',
      component: () => import('@/pages/user/UserFavorites.vue')
    },
    {
      path: '/regulations',
      name: 'regulations',
      component: () => import('@/pages/regulations/RegulationsList.vue')
    }
  ]
})

export default router

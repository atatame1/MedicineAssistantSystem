import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/portal' },
    {
      path: '/agents',
      name: 'agents',
      component: () => import('@/pages/agents/AgentsHub.vue'),
      meta: { title: '智能体中心', requiresAuth: true }
    },
    {
      path: '/portal',
      name: 'portal',
      component: () => import('@/pages/portal/PortalOverview.vue'),
      meta: { title: '智能门户' }
    },
    {
      path: '/me/tasks',
      name: 'me-tasks',
      component: () => import('@/pages/me/MyTasks.vue'),
      meta: { title: '我的任务', requiresAuth: true }
    },
    { path: '/me/profile', redirect: '/me' },
    {
      path: '/projects',
      name: 'projects',
      component: () => import('@/pages/projects/ProjectsList.vue'),
      meta: { title: '项目中心' }
    },
    {
      path: '/projects/create',
      name: 'projects-create',
      component: () => import('@/pages/projects/ProjectsCreate.vue'),
      meta: { title: '新建项目' }
    },
    {
      path: '/projects/board',
      name: 'projects-board',
      component: () => import('@/pages/projects/ProjectBoard.vue'),
      meta: { title: '项目看板' }
    },
    {
      path: '/projects/decisions',
      name: 'projects-decisions',
      component: () => import('@/pages/projects/ProjectDecisions.vue'),
      meta: { title: '决策记录' }
    },
    {
      path: '/projects/documents',
      name: 'projects-documents',
      component: () => import('@/pages/projects/ProjectDocuments.vue'),
      meta: { title: '文档管理' }
    },
    {
      path: '/projects/members',
      name: 'projects-members',
      component: () => import('@/pages/projects/ProjectMembers.vue'),
      meta: { title: '项目成员' }
    },
    {
      path: '/projects/draft-evaluate',
      name: 'projects-draft-evaluate',
      component: () => import('@/pages/projects/DraftEvaluateStream.vue'),
      meta: { title: '立项评估流' }
    },
    {
      path: '/projects/report-stream',
      name: 'projects-report-stream',
      component: () => import('@/pages/projects/ProjectReportStream.vue'),
      meta: { title: '报告生成流' }
    },
    {
      path: '/literatures',
      name: 'literatures',
      component: () => import('@/pages/knowledge/Literatures.vue'),
      meta: { title: '文献库' }
    },
    {
      path: '/literatures/:literatureId/summary',
      name: 'literatures-summary',
      component: () => import('@/pages/literatures/LiteratureSummaryStream.vue'),
      meta: { title: '文献摘要流' }
    },
    {
      path: '/user/:userId/settings',
      name: 'user-settings',
      component: () => import('@/pages/user/UserSettings.vue'),
      meta: { title: '用户设置', requiresAuth: true }
    },
    {
      path: '/user/:userId/favorites',
      name: 'user-favorites',
      component: () => import('@/pages/user/UserFavorites.vue'),
      meta: { title: '用户收藏', requiresAuth: true }
    },
    {
      path: '/me/projects',
      name: 'me-projects',
      component: () => import('@/pages/me/MyProjects.vue'),
      meta: { title: '我的项目', requiresAuth: true }
    },
    {
      path: '/me/reports',
      name: 'me-reports',
      component: () => import('@/pages/me/MyReports.vue'),
      meta: { title: '我的报告', requiresAuth: true }
    },
    {
      path: '/regulations',
      name: 'regulations',
      component: () => import('@/pages/knowledge/RegulationsList.vue'),
      meta: { title: '法规库' }
    },
    {
      path: '/knowledge/expert/:expert',
      name: 'knowledge-expert',
      component: () => import('@/pages/knowledge/ExpertChat.vue'),
      meta: { title: '专家对话' }
    },
    {
      path: '/knowledge',
      name: 'knowledge',
      component: () => import('@/pages/knowledge/KnowledgeHub.vue'),
      meta: { title: '知识中心' }
    },
    {
      path: '/knowledge/explore',
      name: 'knowledge-explore',
      component: () => import('@/pages/knowledge/FreeExplore.vue'),
      meta: { title: '自由探索' }
    },
    {
      path: '/knowledge/herbs',
      name: 'knowledge-herbs',
      component: () => import('@/pages/knowledge/Herbs.vue'),
      meta: { title: '中药材库' }
    },
    {
      path: '/knowledge/formulas',
      name: 'knowledge-formulas',
      component: () => import('@/pages/knowledge/Formulas.vue'),
      meta: { title: '方剂库' }
    },
    {
      path: '/knowledge/components',
      name: 'knowledge-components',
      component: () => import('@/pages/knowledge/Components.vue'),
      meta: { title: '成分库' }
    },
    {
      path: '/knowledge/diseases',
      name: 'knowledge-diseases',
      component: () => import('@/pages/knowledge/Diseases.vue'),
      meta: { title: '疾病库' }
    },
    {
      path: '/knowledge/target-pathways',
      name: 'knowledge-target-pathways',
      component: () => import('@/pages/knowledge/TargetPathways.vue'),
      meta: { title: '靶点通路库' }
    },
    {
      path: '/knowledge/patents',
      name: 'knowledge-patents',
      component: () => import('@/pages/knowledge/Patents.vue'),
      meta: { title: '专利库' }
    },
    {
      path: '/agents/:agent',
      name: 'agents-runner',
      component: () => import('@/pages/agents/AgentChat.vue'),
      meta: { title: '智能体中心', requiresAuth: true }
    },
    {
      path: '/me',
      name: 'me',
      component: () => import('@/pages/me/MeHub.vue'),
      meta: { title: '个人中心', requiresAuth: true }
    }
  ]
})

export default router

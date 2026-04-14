import { apiGet, apiPostJson } from './http'
import type { Project } from './projects'

export type PortalTask = {
  id: number
  userId: number | null
  userName: string | null
  title: string
  description: string | null
  priority: number | null
  status: number | null
  projectId: number | null
  projectName: string | null
  assigneeId: number | null
  assigneeName: string | null
  deadline: string | null
  overdue: boolean | null
}

export type PortalOverview = {
  tasks: PortalTask[]
  myProjects: Project[]
  riskWarnings: number
  summaryText?: string | null
}

export type MpArticleItem = {
  title: string | null
  url: string | null
  postTimeStr: string | null
  postTime: number | null
  coverUrl: string | null
  original: number | null
}

export type MpArticleQueryResponse = {
  mpNickname: string | null
  nowPage: number | null
  totalPage: number | null
  nowPageArticlesNum: number | null
  totalNum: number | null
  articles: MpArticleItem[]
}

export type MpArticleQueryRequest = {
  biz?: string
  url?: string
  name?: string
  page?: number
}

export async function getPortalOverview(userId: number) {
  return apiGet<PortalOverview>(`/api/portal/overview?userId=${encodeURIComponent(String(userId))}`)
}

export async function triggerPortalSummary(userId: number) {
  return apiPostJson<string | null, Record<string, never>>(
    `/api/portal/summarize?userId=${encodeURIComponent(String(userId))}`,
    {}
  )
}

export async function queryMpArticles(body: MpArticleQueryRequest) {
  return apiPostJson<MpArticleQueryResponse, MpArticleQueryRequest>('/api/portal/mp-articles', body)
}

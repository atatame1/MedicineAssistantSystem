import { apiGet, apiPostJson } from './http'

export type SettingsResponse = {
  userId: number
  preferences: string
}

export type SettingsUpdateRequest = {
  preferences: string
}

export async function getUserSettings(userId: number) {
  return apiGet<SettingsResponse>(`/api/user/${userId}/settings`)
}

export async function updateUserSettings(
  userId: number,
  body: SettingsUpdateRequest
) {
  return apiPostJson<void, SettingsUpdateRequest>(
    `/api/user/${userId}/settings/update`,
    body
  )
}

export type FavoriteResponse = {
  id?: number
  favoriteId: number
  favoriteType: string
  entityName: string | null
  collectTime?: string | null
}

export type FavoriteStatisticsResponse = {
  totalCount: number
  typeCountMap: Record<string, number>
}

export async function listUserFavorites(userId: number) {
  return apiGet<FavoriteResponse[]>(`/api/user/${userId}/favorites`)
}

export async function getUserFavoriteStatistics(userId: number) {
  return apiGet<FavoriteStatisticsResponse>(
    `/api/user/${userId}/favorites/statistics`
  )
}

export type TaskResponse = {
  id: number
  title: string
  description: string | null
  priority: number | null
  status: number | null
  projectId: number | null
  projectName: string | null
  assigneeId: number | null
  deadline: string | null
  completionFeedback: string | null
  createTime: string | null
  overdue: boolean | null
}

export type ProjectResponse = {
  projectId: number
  projectName: string | null
  phase: string | null
  status: number | null
  role: string | null
  joinTime: string | null
}

export type DocumentResponse = {
  id: number
  name: string | null
  type: string | null
  url: string | null
  createTime: string | null
}

export type UserProfileResponse = {
  userId: number
  username: string | null
  role: string | null
  taskCount: number | null
  projectCount: number | null
  favoriteCount: number | null
}

export async function listMyTasks(userId: number) {
  return apiGet<TaskResponse[]>(`/api/user/${userId}/tasks`)
}

export async function listMyProjects(userId: number) {
  return apiGet<ProjectResponse[]>(`/api/user/${userId}/projects`)
}

export async function listMyReports(userId: number) {
  return apiGet<DocumentResponse[]>(`/api/user/${userId}/reports`)
}

export async function getMyProfile(userId: number) {
  return apiGet<UserProfileResponse>(`/api/user/${userId}/profile`)
}


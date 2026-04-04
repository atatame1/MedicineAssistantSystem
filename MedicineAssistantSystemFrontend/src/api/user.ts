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

export type FavoriteOperationRequest = {
  favoriteId: number
  favoriteType: string
}

export async function listUserFavorites(userId: number) {
  return apiGet<FavoriteResponse[]>(`/api/user/${userId}/favorites`)
}

export async function getUserFavoriteStatistics(userId: number) {
  return apiGet<FavoriteStatisticsResponse>(
    `/api/user/${userId}/favorites/statistics`
  )
}

export async function addFavorite(userId: number, body: FavoriteOperationRequest) {
  return apiPostJson<void, FavoriteOperationRequest>(
    `/api/user/${userId}/favorites/add`,
    body
  )
}

export async function removeFavorite(
  userId: number,
  body: FavoriteOperationRequest
) {
  return apiPostJson<void, FavoriteOperationRequest>(
    `/api/user/${userId}/favorites/remove`,
    body
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
  id: number
  projectName: string | null
  herbName: string | null
  formulaName: string | null
  indication: string | null
  phase: string | null
  status: number | null
  priority: number | null
  startTime: string | null
  plannedEndTime: string | null
  description: string | null
  budget: number | null
  createTime: string | null
  updateTime: string | null
}

export type DocumentResponse = {
  id: number
  projectId: number
  projectName: string | null
  docType: string | null
  docName: string | null
  storageKey: string | null
  fileSize: number | null
  fileType: string | null
  uploadTime: string | null
  tags: string | null
  summary: string | null
}

export type UserStatisticsResponse = {
  totalTasks: number | null
  pendingTasks: number | null
  inProgressTasks: number | null
  completedTasks: number | null
  totalProjects: number | null
  activeProjects: number | null
  totalFavorites: number | null
  totalDocuments: number | null
}

export type UserProfileResponse = {
  userId: number
  username: string | null
  nickname: string | null
  email: string | null
  phone: string | null
  gender: string | null
  avatarUrl: string | null
  status: string | null
  statistics: UserStatisticsResponse | null
}

export type UserListItem = {
  id: number
  username: string | null
  nickname: string | null
}

export async function listUsers() {
  return apiGet<UserListItem[]>('/api/user/list')
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


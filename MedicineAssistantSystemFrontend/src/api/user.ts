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


import { apiGet, apiPostJson } from './http'
import type { TaskResponse } from './user'

export type UserTaskCreateRequest = {
  projectId: number
  userId: number
  title: string
  description: string
  priority: number
  deadline: string
}

export async function listProjectTasks(projectId: number) {
  return apiGet<TaskResponse[]>(`/api/tasks/project/${encodeURIComponent(String(projectId))}`)
}

export async function createTask(assigneeId: number, body: UserTaskCreateRequest) {
  return apiPostJson<number, UserTaskCreateRequest>(
    `/api/tasks/create?assigneeId=${encodeURIComponent(String(assigneeId))}`,
    body
  )
}

export async function withdrawTask(taskId: number, assigneeId: number) {
  return apiPostJson<void, Record<string, never>>(
    `/api/tasks/${encodeURIComponent(String(taskId))}/withdraw?assigneeId=${encodeURIComponent(String(assigneeId))}`,
    {}
  )
}

export async function getTaskDetail(taskId: number) {
  return apiGet<TaskResponse>(`/api/tasks/${encodeURIComponent(String(taskId))}`)
}


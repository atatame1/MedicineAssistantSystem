import { apiGet, apiPostMultipart } from './http'

export type Literature = {
  id?: number
  title: string
  pdfPath?: string | null
  fileSize?: number | null
  researchType?: string | null
  methodology?: string | null
  mainFindings?: string | null
  conclusion?: string | null
  innovations?: string | null
  keywords?: string | null
  abstractContent?: string | null
}

export type LiteratureSummaryResponse = {
  id?: number
  title?: string | null
  researchType?: string | null
  methodology?: string | null
  mainFindings?: string | null
  conclusion?: string | null
  innovations?: string | null
  keywords?: string | null
}

export async function listLiteratures(keyword?: string) {
  const qs = keyword ? `?keyword=${encodeURIComponent(keyword)}` : ''
  return apiGet<Literature[]>(`/api/literatures${qs}`)
}

export async function createLiterature(
  metadata: Literature,
  file: File
) {
  const blob = new Blob([JSON.stringify(metadata)], { type: 'application/json' })
  const res = await apiPostMultipart<void, any>(`/api/literatures/create`, {
    file,
    metadata: blob
  })
  return res
}


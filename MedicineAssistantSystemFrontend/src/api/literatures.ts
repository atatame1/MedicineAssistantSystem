import { apiGet, apiGetBlob, apiPostJson, apiPostMultipart } from './http'

export type Literature = {
  id?: number
  title?: string | null
  titleEnglish?: string | null
  authors?: string | null
  publishYear?: number | null
  publishMonth?: number | null
  journalName?: string | null
  issn?: string | null
  volume?: string | null
  issue?: string | null
  pages?: string | null
  doi?: string | null
  pubmedId?: string | null
  wosNumber?: string | null
  abstractContent?: string | null
  abstractEnglish?: string | null
  keywords?: string | null
  keywordsEnglish?: string | null
  literatureType?: string | null
  researchType?: string | null
  subjects?: string | null
  relatedHerbs?: string | null
  relatedFormulas?: string | null
  relatedComponents?: string | null
  relatedTargets?: string | null
  relatedPathways?: string | null
  relatedDiseases?: string | null
  methodology?: string | null
  mainFindings?: string | null
  conclusion?: string | null
  innovations?: string | null
  technicalRoute?: string | null
  citationCount?: number | null
  downloadCount?: number | null
  fullTextUrl?: string | null
  abstractUrl?: string | null
  pdfPath?: string | null
  fileSize?: number | null
  tags?: string | null
  importance?: number | null
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

export async function getLiterature(literatureId: number) {
  return apiGet<Literature>(`/api/literatures/${encodeURIComponent(String(literatureId))}`)
}

export async function createLiterature(metadata: Literature, file?: File | null) {
  const blob = new Blob([JSON.stringify(metadata)], { type: 'application/json' })
  const form: Record<string, Blob | File> = { metadata: blob }
  if (file) form.file = file
  return apiPostMultipart<void, Record<string, Blob | File>>(`/api/literatures/create`, form)
}

export async function updateLiterature(body: Literature) {
  return apiPostJson<void, Literature>('/api/literatures/update', body)
}

export async function deleteLiterature(id: number) {
  return apiPostJson<void, { id: number }>('/api/literatures/delete', { id })
}

export async function replaceLiteraturePdf(literatureId: number, file: File) {
  return apiPostMultipart<void, Record<string, Blob | File>>(
    `/api/literatures/${encodeURIComponent(String(literatureId))}/pdf`,
    { file }
  )
}

export async function downloadLiteraturePdfBlob(literatureId: number) {
  return apiGetBlob(`/api/literatures/${encodeURIComponent(String(literatureId))}/pdf`)
}

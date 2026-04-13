import { apiGet, apiPostJson } from './http'

export type Regulation = {
  id?: number
  name?: string | null
  nameEnglish?: string | null
  regulationNumber?: string | null
  issuingAuthority?: string | null
  issuingAuthorityType?: string | null
  regulationType?: string | null
  category?: string | null
  subCategory?: string | null
  effectiveDate?: string | null
  implementationDate?: string | null
  expirationDate?: string | null
  isValid?: boolean | null
  applicableField?: string | null
  originalUrl?: string | null
  pdfPath?: string | null
  summary?: string | null
  fullText?: string | null
  keyProvisions?: string | null
  relatedHerbs?: string | null
  relatedFormulas?: string | null
  relatedIndications?: string | null
  relatedRegulations?: string | null
  changeDescription?: string | null
  impactAssessment?: string | null
  executionPoints?: string | null
  interpretationUrls?: string | null
  tags?: string | null
  importance?: number | null
}

export async function listRegulations(keyword?: string) {
  const qs = keyword ? `?keyword=${encodeURIComponent(keyword)}` : ''
  return apiGet<Regulation[]>(`/api/regulations${qs}`)
}

export async function getRegulationDetail(regulationId: number) {
  return apiGet<Regulation>(`/api/regulations/${encodeURIComponent(String(regulationId))}`)
}

export async function createRegulation(body: Regulation) {
  return apiPostJson<void, Regulation>('/api/regulations/create', body)
}

export async function updateRegulation(body: Regulation) {
  return apiPostJson<void, Regulation>('/api/regulations/update', body)
}

export async function deleteRegulation(id: number) {
  return apiPostJson<void, { id: number }>('/api/regulations/delete', { id })
}

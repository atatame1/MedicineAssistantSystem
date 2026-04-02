import { apiGet } from './http'

export type Regulation = {
  id?: number
  name: string
  regulationNumber?: string | null
  issuingAuthority?: string | null
  issuingAuthorityType?: string | null
  regulationType?: string | null
  category?: string | null
  subCategory?: string | null
  effectiveDate?: string | null
  summary?: string | null
}

export async function listRegulations(keyword?: string) {
  const qs = keyword ? `?keyword=${encodeURIComponent(keyword)}` : ''
  return apiGet<Regulation[]>(`/api/regulations${qs}`)
}

export async function getRegulationDetail(regulationId: number) {
  return apiGet<Regulation>(`/api/regulations/${encodeURIComponent(String(regulationId))}`)
}


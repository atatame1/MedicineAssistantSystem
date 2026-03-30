import { apiGet } from './http'

export type Regulation = {
  id?: number
  name: string
  regulationNumber?: string | null
  issuingAuthority?: string | null
  summary?: string | null
  effectiveDate?: string | null
}

export async function listRegulations(keyword?: string) {
  const qs = keyword ? `?keyword=${encodeURIComponent(keyword)}` : ''
  return apiGet<Regulation[]>(`/api/regulations${qs}`)
}


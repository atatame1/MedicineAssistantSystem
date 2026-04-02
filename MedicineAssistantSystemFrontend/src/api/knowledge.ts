import { apiGet, apiPostJson } from './http'

export type CrudEntity = { id?: number }

export type Herb = CrudEntity & {
  name?: string | null
  source?: string | null
  nature?: string | null
  taste?: string | null
  meridian?: string | null
  effects?: string | null
}

export type Formula = CrudEntity & {
  name?: string | null
  composition?: string | null
  indications?: string | null
  effects?: string | null
}

export type Component = CrudEntity & {
  name?: string | null
  chemicalStructure?: string | null
  bioactivity?: string | null
  potentialTargets?: string | null
}

export type Disease = CrudEntity & {
  name?: string | null
  category?: string | null
  tcmSyndrome?: string | null
  modernName?: string | null
}

export type TargetPathway = CrudEntity & {
  name?: string | null
  pathwayName?: string | null
  relatedDiseases?: string | null
  mechanismDescription?: string | null
}

export type Patent = CrudEntity & {
  name?: string | null
  patentNumber?: string | null
  applicant?: string | null
  abstractContent?: string | null
}

export type PatentSimilarity = {
  patentId: number
  patentName: string | null
  score: number | null
  reasons: string[] | null
  riskLevel: string | null
}

export type PatentRisk = {
  patentId: number
  patentName: string | null
  riskLevel: string | null
  riskDescription: string | null
  suggestion: string | null
}

export const knowledgeApi = {
  herbs: {
    list: (keyword?: string) =>
      apiGet<Herb[]>(`/api/herbs${keyword ? `?keyword=${encodeURIComponent(keyword)}` : ''}`),
    create: (body: Herb) => apiPostJson<void, Herb>('/api/herbs/create', body),
    update: (body: Herb) => apiPostJson<void, Herb>('/api/herbs/update', body),
    delete: (id: number) => apiPostJson<void, { id: number }>('/api/herbs/delete', { id })
  },
  formulas: {
    list: (keyword?: string) =>
      apiGet<Formula[]>(`/api/formulas${keyword ? `?keyword=${encodeURIComponent(keyword)}` : ''}`),
    create: (body: Formula) => apiPostJson<void, Formula>('/api/formulas/create', body),
    update: (body: Formula) => apiPostJson<void, Formula>('/api/formulas/update', body),
    delete: (id: number) => apiPostJson<void, { id: number }>('/api/formulas/delete', { id })
  },
  components: {
    list: (keyword?: string) =>
      apiGet<Component[]>(`/api/components${keyword ? `?keyword=${encodeURIComponent(keyword)}` : ''}`),
    create: (body: Component) => apiPostJson<void, Component>('/api/components/create', body),
    update: (body: Component) => apiPostJson<void, Component>('/api/components/update', body),
    delete: (id: number) => apiPostJson<void, { id: number }>('/api/components/delete', { id })
  },
  diseases: {
    list: (keyword?: string) =>
      apiGet<Disease[]>(`/api/diseases${keyword ? `?keyword=${encodeURIComponent(keyword)}` : ''}`),
    create: (body: Disease) => apiPostJson<void, Disease>('/api/diseases/create', body),
    update: (body: Disease) => apiPostJson<void, Disease>('/api/diseases/update', body),
    delete: (id: number) => apiPostJson<void, { id: number }>('/api/diseases/delete', { id })
  },
  targetPathways: {
    list: (keyword?: string) =>
      apiGet<TargetPathway[]>(
        `/api/target-pathways${keyword ? `?keyword=${encodeURIComponent(keyword)}` : ''}`
      ),
    create: (body: TargetPathway) => apiPostJson<void, TargetPathway>('/api/target-pathways/create', body),
    update: (body: TargetPathway) => apiPostJson<void, TargetPathway>('/api/target-pathways/update', body),
    delete: (id: number) => apiPostJson<void, { id: number }>('/api/target-pathways/delete', { id })
  },
  patents: {
    list: (keyword?: string) =>
      apiGet<Patent[]>(`/api/patents${keyword ? `?keyword=${encodeURIComponent(keyword)}` : ''}`),
    create: (body: Patent) => apiPostJson<void, Patent>('/api/patents/create', body),
    update: (body: Patent) => apiPostJson<void, Patent>('/api/patents/update', body),
    delete: (id: number) => apiPostJson<void, { id: number }>('/api/patents/delete', { id }),
    similar: (patentId: number) =>
      apiGet<PatentSimilarity[]>(`/api/patents/similar?patentId=${encodeURIComponent(String(patentId))}`),
    risk: (patentId: number) =>
      apiGet<PatentRisk>(`/api/patents/risk?patentId=${encodeURIComponent(String(patentId))}`)
  }
}

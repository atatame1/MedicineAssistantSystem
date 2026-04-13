export type SelectOption = { label: string; value: string | number }

export type FormField = {
  prop: string
  label: string
  type?: 'text' | 'textarea' | 'number' | 'switch' | 'datetime' | 'select' | 'year'
  options?: SelectOption[]
}

function selectUsesNumber(f: FormField): boolean {
  const first = f.options?.[0]
  return typeof first?.value === 'number'
}

export function defaultValue(f: FormField): string | number | boolean | null {
  const t = f.type || 'text'
  if (t === 'number') return null
  if (t === 'switch') return false
  if (t === 'datetime') return ''
  if (t === 'year') return ''
  if (t === 'select') return selectUsesNumber(f) ? null : ''
  return ''
}

export function normalizeFieldValue(v: unknown, f: FormField): string | number | boolean | null {
  const t = f.type || 'text'
  if (t === 'number') {
    if (v === null || v === undefined || v === '') return null
    const n = Number(v)
    return Number.isFinite(n) ? n : null
  }
  if (t === 'switch') return !!v
  if (t === 'datetime') {
    if (v == null || v === '') return ''
    if (Array.isArray(v) && v.length >= 3) {
      const [y, m, d, h = 0, min = 0, s = 0] = v
      const pad = (n: number) => String(n).padStart(2, '0')
      return `${y}-${pad(m)}-${pad(d)}T${pad(h)}:${pad(min)}:${pad(s)}`
    }
    return String(v)
  }
  if (t === 'year') {
    if (v == null || v === '') return ''
    if (typeof v === 'number' && Number.isFinite(v)) return String(v)
    return String(v)
  }
  if (t === 'select') {
    if (v === null || v === undefined || v === '') {
      return selectUsesNumber(f) ? null : ''
    }
    if (selectUsesNumber(f)) {
      const n = Number(v)
      return Number.isFinite(n) ? n : null
    }
    return String(v)
  }
  if (v === null || v === undefined) return ''
  return String(v)
}

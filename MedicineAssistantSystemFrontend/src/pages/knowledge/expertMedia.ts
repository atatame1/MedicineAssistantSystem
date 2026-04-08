const modules = import.meta.glob<string>('@/assets/gif/*.{png,jpg,jpeg,gif,webp}', {
  eager: true,
  query: '?url',
  import: 'default'
})

function fileStem(path: string): string {
  const base = path.split('/').pop() || ''
  return base.replace(/\.(png|jpg|jpeg|gif|webp)$/i, '')
}

function normKey(s: string): string {
  return String(s || '')
    .trim()
    .toLowerCase()
    .replace(/（/g, '(')
    .replace(/）/g, ')')
    .replace(/·/g, '.')
    .replace(/—/g, '-')
}

export function resolveExpertMedia(keysLike: string | string[]): {
  idle: string | undefined
  talk: string | undefined
  hasAny: boolean
} {
  const keys = Object.keys(modules)
  const cand = (Array.isArray(keysLike) ? keysLike : [keysLike]).map(normKey).filter(Boolean)
  let idle: string | undefined
  let talk: string | undefined
  for (const k of keys) {
    const stem = normKey(fileStem(k))
    if (!/\.(png|jpg|jpeg|webp)$/i.test(k)) continue
    for (const c of cand) {
      if (stem === c || stem === `${c}-idle` || stem === `${c}_idle`) idle = modules[k]
    }
    if (idle) break
  }
  for (const k of keys) {
    if (!/\.gif$/i.test(k)) continue
    const stem = normKey(fileStem(k))
    for (const c of cand) {
      if (stem === `${c}-talk` || stem === `${c}_talk` || stem === `${c}-speak`) {
        talk = modules[k]
        break
      }
    }
    if (talk) break
  }
  if (!talk) {
    for (const k of keys) {
      if (!/\.gif$/i.test(k)) continue
      const stem = normKey(fileStem(k))
      for (const c of cand) {
        if (stem === c) {
          talk = modules[k]
          break
        }
      }
      if (talk) break
    }
  }
  return { idle, talk, hasAny: Boolean(idle || talk) }
}

export function resolveExpertMediaSimple(keysLike: string | string[]) {
  const { idle, talk } = resolveExpertMedia(keysLike)
  return { idle, talk }
}

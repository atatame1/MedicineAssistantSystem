const LS_TOKEN = 'mas_token'
const VOICE_CACHE_LIMIT = 24
const voiceBlobCache = new Map<string, Blob>()
const voiceRequestCache = new Map<string, Promise<Blob>>()

function resolveApiUrl(path: string): string {
  const b = (import.meta.env.VITE_API_BASE as string | undefined)?.trim()
  if (b) return `${b.replace(/\/$/, '')}${path}`
  return path
}

export function stripTextForTts(raw: string): string {
  let s = raw
    .replace(/\*\*([^*]+)\*\*/g, '$1')
    .replace(/`{1,3}[^`]*`{1,3}/g, ' ')
    .replace(/#{1,6}\s*/gm, '')
    .replace(/^\s*[-*+]\s+/gm, '')
    .replace(/^\s*\d+\.\s+/gm, '')
    .replace(/\|/g, ' ')
    .replace(/\s+/g, ' ')
    .trim()
  if (s.length > 2500) s = s.slice(0, 2500)
  return s
}

function cacheKey(voiceName: string, plainText: string) {
  return `${voiceName}::${plainText}`
}

function touchBlobCache(key: string, blob: Blob) {
  if (voiceBlobCache.has(key)) voiceBlobCache.delete(key)
  voiceBlobCache.set(key, blob)
  while (voiceBlobCache.size > VOICE_CACHE_LIMIT) {
    const firstKey = voiceBlobCache.keys().next().value as string | undefined
    if (!firstKey) break
    voiceBlobCache.delete(firstKey)
  }
}

export async function postVoiceGenAudio(text: string, voiceName?: string | null): Promise<Blob> {
  const plain = stripTextForTts(text)
  if (!plain) throw new Error('没有可朗读的文本')
  const voice = voiceName || 'chenkeji'
  const key = cacheKey(voice, plain)
  const cached = voiceBlobCache.get(key)
  if (cached) {
    touchBlobCache(key, cached)
    return cached
  }
  const requesting = voiceRequestCache.get(key)
  if (requesting) return requesting
  const headers: Record<string, string> = { 'Content-Type': 'application/json' }
  const t = localStorage.getItem(LS_TOKEN)
  if (t) headers.Authorization = `Bearer ${t}`
  const req = (async () => {
    const res = await fetch(resolveApiUrl('/api/voice/gen/audio'), {
      method: 'POST',
      headers,
      body: JSON.stringify({
        text: plain,
        voiceName: voice
      })
    })
    if (!res.ok) {
      const errText = await res.text().catch(() => '')
      throw new Error(errText || `语音请求失败 (${res.status})`)
    }
    const blob = await res.blob()
    touchBlobCache(key, blob)
    return blob
  })()
  voiceRequestCache.set(key, req)
  try {
    return await req
  } finally {
    voiceRequestCache.delete(key)
  }
}

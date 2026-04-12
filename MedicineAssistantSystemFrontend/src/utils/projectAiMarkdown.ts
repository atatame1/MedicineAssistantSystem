import DOMPurify from 'dompurify'
import { marked } from 'marked'

export function stripLeadingConversationJson(text: string): string {
  const s = text.trimStart()
  if (!s.startsWith('{')) return text
  let depth = 0
  let inStr = false
  let esc = false
  for (let i = 0; i < s.length; i++) {
    const ch = s[i]
    if (inStr) {
      if (esc) {
        esc = false
        continue
      }
      if (ch === '\\') {
        esc = true
        continue
      }
      if (ch === '"') inStr = false
      continue
    }
    if (ch === '"') {
      inStr = true
      continue
    }
    if (ch === '{') depth++
    else if (ch === '}') {
      depth--
      if (depth === 0) {
        try {
          JSON.parse(s.slice(0, i + 1))
          return s.slice(i + 1).trimStart()
        } catch {
          return text
        }
      }
    }
  }
  return text
}

export function projectAiAssessToHtml(raw: string): string {
  const cleaned = stripLeadingConversationJson(raw)
  const html = marked.parse(cleaned, {
    async: false,
    gfm: true,
    breaks: true
  }) as string
  return DOMPurify.sanitize(html, { USE_PROFILES: { html: true } })
}

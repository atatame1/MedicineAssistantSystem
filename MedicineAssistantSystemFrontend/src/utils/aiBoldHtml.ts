function escapeHtml(s: string) {
  return s
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
}

export function aiBoldToHtml(text: string | null | undefined) {
  const escaped = escapeHtml(text ?? '')
  return escaped.replace(/\*\*([\s\S]+?)\*\*/g, '<strong>$1</strong>')
}

import { defineStore } from 'pinia'

export type AuthUser = {
  userId: number
  username: string
  nickname: string | null
}

type State = {
  token: string | null
  user: AuthUser | null
}

const LS_TOKEN = 'mas_token'
const LS_USER = 'mas_user'

export const useAuthStore = defineStore('auth', {
  state: (): State => ({
    token: localStorage.getItem(LS_TOKEN),
    user: (() => {
      const s = localStorage.getItem(LS_USER)
      if (!s) return null
      try {
        return JSON.parse(s)
      } catch {
        return null
      }
    })()
  }),
  getters: {
    isAuthed: (s) => !!s.token
  },
  actions: {
    setAuth(token: string, user: AuthUser) {
      this.token = token
      this.user = user
      localStorage.setItem(LS_TOKEN, token)
      localStorage.setItem(LS_USER, JSON.stringify(user))
    },
    clear() {
      this.token = null
      this.user = null
      localStorage.removeItem(LS_TOKEN)
      localStorage.removeItem(LS_USER)
    }
  }
})


import { createApp } from 'vue'
import { createPinia } from 'pinia'

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './styles/theme.css'

import App from './App.vue'
import router from './router'

const app = createApp(App)

const pinia = createPinia()
app.use(pinia)
app.use(router)
app.use(ElementPlus)

router.beforeEach((to) => {
  if ((to.meta as any)?.requiresAuth && !localStorage.getItem('mas_token')) {
    try {
      window.dispatchEvent(new CustomEvent('auth:required', { detail: { to: to.fullPath } }))
    } catch {}
    return false
  }
})

app.mount('#app')

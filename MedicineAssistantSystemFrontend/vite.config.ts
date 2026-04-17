import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8095',
        changeOrigin: true,
        timeout: 0,
        proxyTimeout: 0,
        configure(proxy) {
          proxy.on('proxyRes', (proxyRes, _req, res) => {
            const ct = proxyRes.headers['content-type']
            if (ct && String(ct).includes('text/event-stream')) {
              res.setHeader('cache-control', 'no-cache, no-transform')
              res.setHeader('x-accel-buffering', 'no')
              res.setHeader('connection', 'keep-alive')
            }
          })
        },
      },
    },
  },
})

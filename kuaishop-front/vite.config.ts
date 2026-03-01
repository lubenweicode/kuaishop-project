import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// 彻底删除 vueDevTools 的导入语句

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    // 彻底删除 vueDevTools() 调用语句
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
})
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './assets/styles/main.css'
import axios from '@/utils/axios'

// 全局引入 Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.use(ElementPlus)  // 使用 Element Plus

// 全局配置 axios
app.config.globalProperties.$axios = axios

app.mount('#app') 
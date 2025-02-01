/// <reference types="vite/client" />
/// <reference types="vue/dist/vue.d.ts" />

declare module '*.vue' {
  import { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

interface ImportMetaEnv {
  readonly VITE_APP_TITLE: string
  readonly BASE_URL: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}

// 添加全局组件类型
declare module '@vue/runtime-core' {
  export interface GlobalComponents {
    BeautyScoreDialog: typeof import('./components/BeautyScoreDialog.vue')['default']
  }
} 
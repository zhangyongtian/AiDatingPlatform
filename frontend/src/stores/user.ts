import { defineStore } from 'pinia'
import axios from '@/utils/axios'

interface UserInfo {
  gender: string
  age: number
  city: string
  income: number
  education: number
  maritalStatus: number
  mbti: string
  coreValues: Array<{ label: string; score: number }>
  lifestyle: Array<{ label: string; score: number }>
  communication: Array<{ label: string; score: number }>
  realConditions: Array<{ label: string; score: number }>
  height: number
}

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: null as UserInfo | null,
    isLoading: false
  }),

  actions: {
    async login(username: string, password: string) {
      try {
        // 开发阶段使用模拟数据
        const mockToken = 'mock_token_' + Date.now()
        this.token = mockToken
        localStorage.setItem('token', mockToken)
        
        // 模拟用户信息
        this.userInfo = {
          gender: 'male',
          age: 25,
          city: '北京',
          income: 3,
          education: 3,
          maritalStatus: 1,
          mbti: 'INTJ',
          coreValues: [
            { label: '家庭观念', score: 80 },
            { label: '事业追求', score: 70 },
            { label: '生活态度', score: 75 },
            { label: '消费理念', score: 65 },
            { label: '教育观念', score: 85 },
            { label: '婚姻观念', score: 90 },
            { label: '道德品质', score: 85 },
            { label: '兴趣爱好', score: 70 }
          ],
          lifestyle: [
            { label: '作息规律', score: 75 },
            { label: '饮食习惯', score: 70 },
            { label: '运动健身', score: 65 },
            { label: '社交活跃度', score: 60 },
            { label: '生活整洁度', score: 80 },
            { label: '休闲方式', score: 70 }
          ],
          communication: [
            { label: '表达能力', score: 75 },
            { label: '倾听能力', score: 80 },
            { label: '情绪管理', score: 70 },
            { label: '矛盾处理', score: 75 }
          ],
          realConditions: [
            { label: '工作稳定性', score: 85 },
            { label: '居住条件', score: 75 },
            { label: '家庭背景', score: 70 },
            { label: '未来规划', score: 80 }
          ],
          height: 175
        }
        
        return true
      } catch (error) {
        console.error('登录失败:', error)
        return false
      }
    },

    async fetchUserInfo() {
      // 开发阶段直接返回已有的用户信息
      return
    },

    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
  }
}) 
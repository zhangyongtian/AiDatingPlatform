<template>
  <div class="dashboard">
    <div class="profile-section">
      <div class="left-section">
        <div class="avatar-container" @click="triggerFileInput">
          <img v-if="userInfo.avatarUrl" :src="userInfo.avatarUrl" class="avatar-image" />
          <div v-else class="avatar-placeholder">
            <i class="el-icon-plus"></i>
            <span>上传头像</span>
          </div>
          <!-- 添加隐藏的文件输入框 -->
          <input
            type="file"
            ref="fileInput"
            style="display: none"
            accept="image/*"
            @change="handleFileUpload"
          />
        </div>
        
        <!-- 颜值评分展示 -->
        <div class="beauty-score-section" v-if="userInfo.beautyScore">
          <!-- 总分展示 -->
          <div class="total-beauty-score">
            <div class="score-circle">
              {{ userInfo.beautyScore }}
              <span class="score-label">分</span>
            </div>
            <div class="score-level">{{ beautyLevel }}</div>
          </div>
          
          <!-- 颜值分析详情 -->
          <div class="beauty-analysis">
            <h3>颜值分析报告</h3>
            <div class="analysis-items">
              <div class="analysis-item" 
                   v-for="item in beautyAnalysis" 
                   :key="item.label"
                   @mouseenter="showDetails = item.details"
                   @mouseleave="showDetails = ''"
              >
                <div class="item-header">
                  <div class="item-label">{{ item.label }}</div>
                  <div class="item-score">{{ item.score }}分</div>
                </div>
                <div class="progress-bar">
                  <div class="progress" :style="{ width: item.score + '%' }"></div>
                </div>
                <div class="details-popup" v-show="showDetails === item.details">
                  {{ item.details }}
                </div>
              </div>
            </div>
          </div>
          
          <!-- 改善建议 -->
          <div class="beauty-suggestions" v-if="parsedBeautySuggestions.length > 0">
            <h3>个性化建议</h3>
            <div class="suggestions-container">
              <div v-for="(suggestion, index) in parsedBeautySuggestions" 
                   :key="index" 
                   class="suggestion-item">
                <div class="suggestion-number">{{ index + 1 }}</div>
                <div class="suggestion-content">{{ suggestion }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <button class="test-beauty-btn" @click="testBeauty" :disabled="isTestingBeauty">
            <i class="el-icon-camera"></i>测试颜值
          </button>
          <button class="match-btn" @click="startMatching" :disabled="isMatching">
            <i class="el-icon-search"></i>匹配对象
          </button>
        </div>
      </div>

      <div class="user-info-card">
        <div class="card-header">
          <div class="header-left">
            <h2>个人信息</h2>
            <span class="username">{{ userInfo.username || '未设置昵称' }}</span>
          </div>
          <button class="edit-btn" @click="startEdit" v-if="!isEditing">
            <span class="edit-icon">✏️</span>
            编辑资料
          </button>
        </div>

        <!-- 查看模式 -->
        <div v-if="!isEditing" class="info-grid">
          <div class="info-item">
            <span class="info-label">性别</span>
            <span class="info-value">{{ userInfo.gender === 'male' ? '男' : '女' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">年龄</span>
            <span class="info-value">{{ userInfo.age }}岁</span>
          </div>
          <div class="info-item">
            <span class="info-label">所在城市</span>
            <span class="info-value">{{ userInfo.city }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">月收入</span>
            <span class="info-value">{{ formatIncome(userInfo.income) }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">学历</span>
            <span class="info-value">{{ formatEducation(userInfo.education) }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">婚姻状况</span>
            <span class="info-value">{{ formatMaritalStatus(userInfo.maritalStatus) }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">MBTI</span>
            <span class="info-value">{{ userInfo.mbti }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">身高</span>
            <span class="info-value">{{ userInfo.height }}cm</span>
          </div>
        </div>

        <!-- 编辑模式 -->
        <div v-else class="edit-form">
          <div class="form-grid">
            <div class="form-item">
              <label>昵称</label>
              <input type="text" v-model="editForm.username" placeholder="请输入昵称">
            </div>
            <div class="form-item">
              <label>性别</label>
              <select v-model="editForm.gender">
                <option value="male">男</option>
                <option value="female">女</option>
              </select>
            </div>

            <div class="form-item">
              <label>年龄</label>
              <input 
                type="number" 
                v-model="editForm.age"
                min="18"
                max="100"
              >
            </div>

            <div class="form-item">
              <label>所在城市</label>
              <input type="text" v-model="editForm.city">
            </div>

            <div class="form-item">
              <label>月收入</label>
              <select v-model="editForm.income">
                <option value="">请选择月收入</option>
                <option value="5k以下">5k以下</option>
                <option value="5k-10k">5k-10k</option>
                <option value="10k-20k">10k-20k</option>
                <option value="20k-50k">20k-50k</option>
                <option value="50k以上">50k以上</option>
              </select>
            </div>

            <div class="form-item">
              <label>学历</label>
              <select v-model="editForm.education">
                <option value="1">高中及以下</option>
                <option value="2">大专</option>
                <option value="3">本科</option>
                <option value="4">硕士</option>
                <option value="5">博士及以上</option>
              </select>
            </div>

            <div class="form-item">
              <label>婚姻状况</label>
              <select v-model="editForm.maritalStatus">
                <option value="1">未婚</option>
                <option value="2">离异</option>
                <option value="3">丧偶</option>
              </select>
            </div>

            <div class="form-item">
              <label>MBTI性格</label>
              <select v-model="editForm.mbti">
                <option v-for="type in mbtiTypes" :key="type" :value="type">
                  {{ type }}
                </option>
              </select>
            </div>

            <div class="form-item">
              <label>身高(cm)</label>
              <input 
                type="number" 
                v-model="editForm.height"
                min="140"
                max="220"
                placeholder="请输入身高"
              >
            </div>
          </div>

          <div class="edit-actions">
            <button class="save-btn" @click="saveChanges">保存</button>
            <button class="cancel-btn" @click="cancelEdit">取消</button>
          </div>
        </div>

        <!-- 个性特征部分 -->
        <div class="vector-info">
          <h3>个性特征</h3>
          
          <!-- 核心价值观 -->
          <div class="vector-section">
            <h4>核心价值观</h4>
            <div class="vector-grid">
              <div v-for="(value, index) in userInfo.coreValues" :key="index" class="vector-item">
                <span class="vector-label">{{ value.label }}</span>
                <div class="progress-container" 
                  @mouseenter="showStandard = value.label" 
                  @mouseleave="showStandard = ''"
                >
                  <div v-if="!isEditing" class="progress-bar">
                    <div class="progress" :style="{ width: value.score + '%' }"></div>
                  </div>
                  <input 
                    v-else
                    type="range"
                    v-model="value.score"
                    min="50"
                    max="100"
                    class="score-slider"
                  >
                  <span class="vector-score">{{ value.score }}</span>
                </div>
                <div class="score-tooltip" v-show="showStandard === value.label">
                  {{ getStandardDescription(value.label) }}
                </div>
              </div>
            </div>
          </div>

          <!-- 生活方式 -->
          <div class="vector-section">
            <h4>生活方式</h4>
            <div class="vector-grid">
              <div v-for="(value, index) in userInfo.lifestyle" :key="index" class="vector-item">
                <span class="vector-label">{{ value.label }}</span>
                <div class="progress-container" 
                  @mouseenter="showStandard = value.label" 
                  @mouseleave="showStandard = ''"
                >
                  <div v-if="!isEditing" class="progress-bar">
                    <div class="progress" :style="{ width: value.score + '%' }"></div>
                  </div>
                  <input 
                    v-else
                    type="range"
                    v-model="value.score"
                    min="50"
                    max="100"
                    class="score-slider"
                  >
                  <span class="vector-score">{{ value.score }}</span>
                </div>
                <div class="score-tooltip" v-show="showStandard === value.label">
                  {{ getStandardDescription(value.label) }}
                </div>
              </div>
            </div>
          </div>

          <!-- 情感沟通 -->
          <div class="vector-section">
            <h4>情感沟通</h4>
            <div class="vector-grid">
              <div v-for="(value, index) in userInfo.communication" :key="index" class="vector-item">
                <span class="vector-label">{{ value.label }}</span>
                <div class="progress-container" 
                  @mouseenter="showStandard = value.label" 
                  @mouseleave="showStandard = ''"
                >
                  <div v-if="!isEditing" class="progress-bar">
                    <div class="progress" :style="{ width: value.score + '%' }"></div>
                  </div>
                  <input 
                    v-else
                    type="range"
                    v-model="value.score"
                    min="50"
                    max="100"
                    class="score-slider"
                  >
                  <span class="vector-score">{{ value.score }}</span>
                </div>
                <div class="score-tooltip" v-show="showStandard === value.label">
                  {{ getStandardDescription(value.label) }}
                </div>
              </div>
            </div>
          </div>

          <!-- 现实条件 -->
          <div class="vector-section">
            <h4>现实条件</h4>
            <div class="vector-grid">
              <div v-for="(value, index) in userInfo.realConditions" :key="index" class="vector-item">
                <span class="vector-label">{{ value.label }}</span>
                <div class="progress-container" 
                  @mouseenter="showStandard = value.label" 
                  @mouseleave="showStandard = ''"
                >
                  <div v-if="!isEditing" class="progress-bar">
                    <div class="progress" :style="{ width: value.score + '%' }"></div>
                  </div>
                  <input 
                    v-else
                    type="range"
                    v-model="value.score"
                    min="50"
                    max="100"
                    class="score-slider"
                  >
                  <span class="vector-score">{{ value.score }}</span>
                </div>
                <div class="score-tooltip" v-show="showStandard === value.label">
                  {{ getStandardDescription(value.label) }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 修改匹配结果列表部分 -->
    <div class="matches-section" v-if="matches.length">
      <h2>匹配结果</h2>
      <div class="matches-grid">
        <div v-for="match in matches" :key="match.id" class="match-card">
          <!-- 基本信息部分 -->
          <div class="match-header">
            <img :src="match.avatarUrl" :alt="match.name" class="match-avatar">
            <div class="match-score-badge">
              {{ match.scores.total.toFixed(2) }}分
            </div>
          </div>

          <div class="match-info">
            <!-- 基本信息 -->
            <div class="match-basic">
              <h3>{{ match.username }}</h3>
              <div class="match-tags">
                <span>{{ match.age }}岁</span>
                <span>{{ match.height }}cm</span>
                <span>{{ match.city }}</span>
                <span>{{ formatEducation(match.education) }}</span>
              </div>
            </div>

            <!-- 匹配分析部分 -->
            <div class="match-analysis">
              <!-- 基础信息匹配 -->
              <div class="match-basic-scores">
                <h4>基础信息匹配</h4>
                <div class="score-item">
                  <span class="label">年纪，身高，地域，收入，颜值</span>
                  <div class="score-bar">
                    <div class="score-progress" :style="{ width: match.scores.basic.age.toFixed(2) + '%' }"></div>
                  </div>
                  <span class="score">{{ match.scores.basic.age.toFixed(2) }}%</span>
                </div>
              </div>

              <!-- MBTI匹配 -->
              <div class="mbti-match">
                <div class="mbti-pair">
                  <span class="your-mbti">{{ userInfo.mbti }}</span>
                  <span class="match-icon">💕</span>
                  <span class="target-mbti">{{ match.mbti }}</span>
                </div>
                <div class="mbti-score">
                  匹配度: {{ match.scores.mbti.toFixed(2) }}%
                </div>
              </div>

              <!-- 核心价值观匹配 -->
              <div class="values-match">
                <h4>核心价值观匹配</h4>
                <div class="score-item">
                  <span class="label">价值观契合度</span>
                  <div class="score-bar">
                    <div class="score-progress" :style="{ width: match.scores.values.toFixed(2) + '%' }"></div>
                  </div>
                  <span class="score">{{ match.scores.values.toFixed(2) }}%</span>
                </div>
              </div>

              <!-- 生活方式匹配 -->
              <div class="lifestyle-match">
                <h4>生活方式匹配</h4>
                <div class="score-item">
                  <span class="label">生活习惯契合度</span>
                  <div class="score-bar">
                    <div class="score-progress" :style="{ width: match.scores.lifestyle.toFixed(2) + '%' }"></div>
                  </div>
                  <span class="score">{{ match.scores.lifestyle.toFixed(2) }}%</span>
                </div>
              </div>

              <!-- 沟通方式匹配 -->
              <div class="communication-match">
                <h4>沟通方式匹配</h4>
                <div class="score-item">
                  <span class="label">沟通契合度</span>
                  <div class="score-bar">
                    <div class="score-progress" :style="{ width: match.scores.communication.toFixed(2) + '%' }"></div>
                  </div>
                  <span class="score">{{ match.scores.communication.toFixed(2) }}%</span>
                </div>
              </div>

              <!-- 现实条件匹配 -->
              <div class="conditions-match">
                <h4>现实条件匹配</h4>
                <div class="score-item">
                  <span class="label">条件契合度</span>
                  <div class="score-bar">
                    <div class="score-progress" :style="{ width: match.scores.conditions.toFixed(2) + '%' }"></div>
                  </div>
                  <span class="score">{{ match.scores.conditions.toFixed(2) }}%</span>
                </div>
              </div>

              <!-- 匹配评价 -->
              <div class="match-comment">
                {{ getMatchComment(match) }}
              </div>
            </div>

            <!-- 操作按钮
            <div class="match-actions">
              <button class="like-btn" @click="handleLike(match.id)">
                <span class="heart-icon">❤️</span>喜欢
              </button>
              <button class="chat-btn" @click="handleChat(match.id)">
                <span class="chat-icon">💌</span>聊天
              </button>
            </div> -->
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, reactive } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

// 定义匹配结果的接口
interface MatchResult {
  id: number
  name: string
  avatar: string
  matchScore: number
  beautyMatchScore: number
  personalityMatchScore: number
  valueMatchScore: number
}

// 用户信息接口
interface UserInfo {
  gender: string
  age: number
  city: string
  income: string
  education: number
  maritalStatus: number
  mbti: string
  coreValues: Array<{ label: string; score: number }>
  lifestyle: Array<{ label: string; score: number }>
  communication: Array<{ label: string; score: number }>
  realConditions: Array<{ label: string; score: number }>
  height: number
}

// 修改编辑表单的类型定义
interface EditForm extends Omit<UserInfo, 'coreValues' | 'lifestyle' | 'communication' | 'realConditions'> {
  username: string;
  coreValues?: Array<{ label: string; score: number }>
  lifestyle?: Array<{ label: string; score: number }>
  communication?: Array<{ label: string; score: number }>
  realConditions?: Array<{ label: string; score: number }>
}

const router = useRouter()
const userStore = useUserStore()
const avatarUrl = ref('')
const beautyScore = ref(0)
const isMatching = ref(false)
const matches = ref<any[]>([])
const fileInput = ref<HTMLInputElement | null>(null)

const userInfo = ref({
  id: '',
  username: '',
  gender: '',
  age: '',
  city: '',
  income: '',
  education: '',
  maritalStatus: '',
  mbti: '',
  height: '',
  avatarUrl: '',
  beautyScore: null,
  beautyScoreExplanation: '',
  beautyAnalysis: '[]',
  beautySuggestions: '[]',
  coreValues: [] as Array<{ label: string; score: number }>,
  lifestyle: [] as Array<{ label: string; score: number }>,
  communication: [] as Array<{ label: string; score: number }>,
  realConditions: [] as Array<{ label: string; score: number }>
})

// 获取用户信息
const getUserInfo = async () => {
  try {
    const storedUser = localStorage.getItem('user')
    if (!storedUser) {
      ElMessage.error('请先登录')
      router.push('/login')
      return
    }

    const user = JSON.parse(storedUser)
    const response = await fetch(`/api/users/${user.id}`)
    
    if (!response.ok) {
      throw new Error('获取用户信息失败')
    }

    const data = await response.json()
    
    // 更新用户信息
    userInfo.value = {
      ...data,
      beautyAnalysis: data.beautyAnalysis || '[]',
      beautySuggestions: data.beautySuggestions || '[]',
      coreValues: parseScores(data.coreValuesScores, [
        '家庭观念', '事业追求', '生活态度', '消费理念',
        '教育观念', '婚姻观念', '道德品质', '兴趣爱好'
      ]),
      lifestyle: parseScores(data.lifestyleScores, [
        '作息规律', '饮食习惯', '运动健身', 
        '社交活跃度', '生活整洁度', '休闲方式'
      ]),
      communication: parseScores(data.communicationScores, [
        '表达能力', '倾听能力', '情绪管理', '矛盾处理'
      ]),
      realConditions: parseScores(data.conditionScores, [
        '工作稳定性', '居住条件', '家庭背景', '未来规划'
      ])
    }

    console.log('用户信息更新:', userInfo.value);
  } catch (error) {
    ElMessage.error('获取用户信息失败，请重试')
    console.error('Error:', error)
  }
}

// 解析评分字符串
const parseScores = (scoresStr: string, labels: string[]) => {
  try {
    if (!scoresStr) return labels.map(label => ({ label, score: 50 }))
    const scores = scoresStr.split('_').map(Number)
    return labels.map((label, index) => ({
      label,
      score: scores[index] || 50
    }))
  } catch {
    return labels.map(label => ({
      label,
      score: 50
    }))
  }
}

onMounted(() => {
  getUserInfo()
})

// 修改收入格式化函数
const formatIncome = (income: string) => {
  return income || '未设置'
}

const formatEducation = (education: number) => {
  const educationMap = {
    1: '高中及以下',
    2: '大专',
    3: '本科',
    4: '硕士',
    5: '博士及以上'
  }
  return educationMap[education as keyof typeof educationMap] || '未设置'
}

const formatMaritalStatus = (status: number) => {
  const statusMap = {
    1: '未婚',
    2: '离异',
    3: '丧偶'
  }
  return statusMap[status as keyof typeof statusMap] || '未设置'
}

// 文件上传相关
const triggerFileInput = () => {
  fileInput.value?.click()
}

const handleFileUpload = async (event: Event) => {
  const input = event.target as HTMLInputElement
  if (!input.files?.length) return
  
  const file = input.files[0]
  const formData = new FormData()
  formData.append('file', file)
  
  try {
    const storedUser = localStorage.getItem('user')
    if (!storedUser) {
      throw new Error('未找到用户信息')
    }
    const user = JSON.parse(storedUser)
    
    const response = await fetch(`/api/users/${user.id}/avatar`, {
      method: 'POST',
      body: formData
    })
    
    if (!response.ok) {
      throw new Error('头像上传失败')
    }
    
    const data = await response.json()
    userInfo.value.avatarUrl = data.avatarUrl
    
    ElMessage({
      message: '头像上传成功',
      type: 'success'
    })

    // 重新获取用户信息
    await getUserInfo()
  } catch (error) {
    ElMessage({
      message: '头像上传失败，请重试',
      type: 'error'
    })
    console.error(error)
  } finally {
    // 清空文件输入框，允许重复上传相同文件
    if (fileInput.value) {
      fileInput.value.value = ''
    }
  }
}

// 修改开始匹配函数
const startMatching = async () => {
  if (isMatching.value) return
  
  isMatching.value = true
  matches.value = [] // 清空之前的匹配结果
  
  try {
    const storedUser = localStorage.getItem('user')
    if (!storedUser) {
      throw new Error('未找到用户信息')
    }
    const user = JSON.parse(storedUser)
    
    // 调用后端匹配接口
    const response = await fetch(`/api/users/${user.id}/match`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      }
    })
    
    if (!response.ok) {
      throw new Error('匹配失败')
    }
    
    const data = await response.json()
    matches.value = data.matches
    
    ElMessage({
      message: '匹配成功',
      type: 'success'
    })
  } catch (error) {
    console.error('匹配失败:', error)
    ElMessage({
      message: '匹配失败，请重试',
      type: 'error'
    })
  } finally {
    isMatching.value = false
  }
}

// 添加处理喜欢和聊天的函数
const handleLike = (id: number) => {
  const match = matches.value.find(m => m.id === id)
  if (match) {
    alert(`已向${match.name}发送喜欢`)
  }
}

const handleChat = (id: number) => {
  const match = matches.value.find(m => m.id === id)
  if (match) {
    router.push(`/chat/${id}`)
  }
}

const isEditing = ref(false)
const editForm = ref({
  username: '',
  gender: '',
  age: '',
  city: '',
  income: '',
  education: '',
  maritalStatus: '',
  mbti: '',
  height: ''
})

// 添加显示标准的状态
const showStandard = ref('')

// 添加显示详情的状态
const showDetails = ref('')

// 修改编辑相关的代码
const startEdit = () => {
  isEditing.value = true
  editForm.value = {
    username: userInfo.value.username || '',
    gender: userInfo.value.gender,
    age: userInfo.value.age,
    city: userInfo.value.city,
    income: userInfo.value.income,
    education: userInfo.value.education,
    maritalStatus: userInfo.value.maritalStatus,
    mbti: userInfo.value.mbti,
    height: userInfo.value.height
  }
}

const cancelEdit = () => {
  isEditing.value = false
}

// 修改保存函数
const saveChanges = async () => {
  try {
    // 构造要保存的数据
    const saveData = {
      ...editForm.value,
      coreValuesScores: userInfo.value.coreValues.map(v => v.score).join('_'),
      lifestyleScores: userInfo.value.lifestyle.map(v => v.score).join('_'),
      communicationScores: userInfo.value.communication.map(v => v.score).join('_'),
      conditionScores: userInfo.value.realConditions.map(v => v.score).join('_')
    }

    const storedUser = localStorage.getItem('user')
    if (!storedUser) {
      throw new Error('未找到用户信息')
    }
    const user = JSON.parse(storedUser)

    await fetch(`/api/users/${user.id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(saveData)
    })

    ElMessage({
      message: '保存成功',
      type: 'success',
      duration: 2000
    })
    isEditing.value = false
    await getUserInfo() // 重新获取用户信息
  } catch (error) {
    ElMessage({
      message: '保存失败，请重试',
      type: 'error',
      duration: 2000
    })
    console.error(error)
  }
}

const mbtiTypes = [
  'INTJ', 'INTP', 'ENTJ', 'ENTP',
  'INFJ', 'INFP', 'ENFJ', 'ENFP',
  'ISTJ', 'ISFJ', 'ESTJ', 'ESFJ',
  'ISTP', 'ISFP', 'ESTP', 'ESFP'
]

// 修改颜值评分相关的计算属性
const beautyLevel = computed(() => {
  const score = userInfo.value.beautyScore
  if (!score) return '未评分'
  if (score >= 90) return '非常优秀'
  if (score >= 80) return '优秀'
  if (score >= 70) return '良好'
  return '一般'
})

const beautyComment = computed(() => {
  return userInfo.value.beautyScoreExplanation || '暂无评语'
})

// 修改颜值分析计算属性
const beautyAnalysis = computed(() => {
  try {
    if (typeof userInfo.value.beautyAnalysis === 'string') {
      const parsed = JSON.parse(userInfo.value.beautyAnalysis);
      return parsed.map((item: any) => ({
        label: item.label,
        score: item.score,
        details: item.details
      }));
    }
    return [];
  } catch (error) {
    console.error('解析颜值分析数据失败:', error);
    return [];
  }
});

// 修改建议解析计算属性
const parsedBeautySuggestions = computed(() => {
  try {
    if (typeof userInfo.value.beautySuggestions === 'string') {
      return JSON.parse(userInfo.value.beautySuggestions);
    }
    return [];
  } catch (error) {
    console.error('解析建议数据失败:', error);
    return [];
  }
});

// 添加评分标准说明函数
const getStandardDescription = (label: string) => {
  const standards: Record<string, string> = {
    // 核心价值观
    '家庭观念': '90-100分：非常重视家庭，愿意为家庭付出；70-89分：关心家庭，注重平衡；50-69分：较为独立，追求个人发展',
    '事业追求': '90-100分：事业心强，目标明确；70-89分：积极上进，规划合理；50-69分：稳定为主，平衡发展',
    '生活态度': '90-100分：积极乐观，充满活力；70-89分：从容淡定，知足常乐；50-69分：随遇而安，平和自然',
    '消费理念': '90-100分：理性规划，注重品质；70-89分：适度消费，量入为出；50-69分：随性消费，享受生活',
    '教育观念': '90-100分：重视教育，愿意投入；70-89分：关注成长，注重实践；50-69分：顺其自然，快乐至上',
    '婚姻观念': '90-100分：慎重认真，注重责任；70-89分：理性务实，追求和谐；50-69分：随缘自然，重视感受',
    '道德品质': '90-100分：品德高尚，正直诚信；70-89分：为人正直，遵纪守法；50-69分：善良本分，不违规矩',
    '兴趣爱好': '90-100分：兴趣广泛，深入发展；70-89分：有所专长，持续投入；50-69分：简单日常，随性而为',
    // 生活方式
    '作息规律': '90-100分：作息规律，早睡早起；70-89分：基本规律，偶有调整；50-69分：随性作息，弹性时间',
    '饮食习惯': '90-100分：营养均衡，定时定量；70-89分：注意饮食，适度放纵；50-69分：随性饮食，享受美食',
    '运动健身': '90-100分：坚持运动，强身健体；70-89分：适度运动，保持健康；50-69分：偶尔运动，随性而为',
    '社交活跃度': '90-100分：广交朋友，积极社交；70-89分：保持联系，适度社交；50-69分：独处为主，选择性社交',
    '生活整洁度': '90-100分：整洁有序，井井有条；70-89分：基本整洁，适度整理；50-69分：随性收纳，不求完美',
    '休闲方式': '90-100分：丰富多彩，充实有趣；70-89分：平衡放松，享受生活；50-69分：简单休闲，随性而为',
    // 沟通能力
    '表达能力': '90-100分：表达清晰，善于沟通；70-89分：表达得当，基本顺畅；50-69分：简单表达，直来直去',
    '倾听能力': '90-100分：专注倾听，善解人意；70-89分：认真聆听，适度回应；50-69分：基本倾听，随性回应',
    '情绪管理': '90-100分：情绪稳定，控制得当；70-89分：基本稳定，偶有波动；50-69分：真实自然，随性表达',
    '矛盾处理': '90-100分：理性处理，积极化解；70-89分：平和处理，寻求共识；50-69分：随性处理，避免冲突',
    // 现实条件
    '工作稳定性': '90-100分：工作稳定，发展良好；70-89分：基本稳定，有所发展；50-69分：工作正常，随遇而安',
    '居住条件': '90-100分：条件优越，环境舒适；70-89分：条件良好，基本舒适；50-69分：条件一般，基本满足',
    '家庭背景': '90-100分：家庭和睦，条件优越；70-89分：家庭和谐，条件良好；50-69分：家庭正常，条件一般',
    '未来规划': '90-100分：规划清晰，目标明确；70-89分：有所规划，循序渐进；50-69分：随遇而安，顺其自然'
  }
  return standards[label] || '暂无评分标准说明'
}

// 添加匹配分析函数
const getBasicMatchScore = (match: any) => {
  const scores = match.scores.basic
  return Math.round((scores.age + scores.height + scores.location + scores.education + scores.income) / 5)
}

// 获取匹配评价
const getMatchComment = (match: any) => {
  const total = match.scores.total
  if (total >= 90) return '你们是天生一对！各方面都非常匹配。'
  if (total >= 85) return '你们非常般配！有很高的匹配度。'
  if (total >= 80) return '你们很合适！可以多了解对方。'
  return '你们有发展潜力，建议进一步了解。'
}

// 获取MBTI匹配评价
const getMbtiMatchComment = (myMbti: string, targetMbti: string) => {
  // 这里可以添加具体的MBTI匹配逻辑
  const matchTypes = {
    'INTJ': ['ENFP', 'ENTP'],
    'ENFP': ['INTJ', 'INFJ'],
    // ... 其他MBTI匹配关系
  }
  
  const isIdealMatch = matchTypes[myMbti as keyof typeof matchTypes]?.includes(targetMbti)
  if (isIdealMatch) {
    return '你们的性格非常互补，能够很好地理解和包容对方。'
  }
  return '你们的性格有一定差异，可以互相学习和成长。'
}

// 修改匹配分数的显示方式
const getValuesScore = (match: any) => {
  return match.scores.values // 直接返回分数值
}

const getLifestyleScore = (match: any) => {
  return match.scores.lifestyle // 直接返回分数值
}

const getCommunicationScore = (match: any) => {
  return match.scores.communication // 直接返回分数值
}

const getConditionsScore = (match: any) => {
  return match.scores.conditions // 直接返回分数值
}

// 添加测试颜值相关的状态
const isTestingBeauty = ref(false)

// 添加测试颜值的函数
const testBeauty = async () => {
  if (!userInfo.value.avatarUrl || isTestingBeauty.value) return
  
  isTestingBeauty.value = true
  try {
    const storedUser = localStorage.getItem('user')
    if (!storedUser) {
      throw new Error('未找到用户信息')
    }
    const user = JSON.parse(storedUser)
    
    // 这里添加调用后端颜值分析接口的代码
    const response = await fetch(`/api/users/${user.id}/beauty-test`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ avatarUrl: userInfo.value.avatarUrl })
    })
    
    if (!response.ok) {
      throw new Error('颜值测试失败')
    }
    
    // 重新获取用户信息以更新颜值分析结果
    await getUserInfo()
    
    ElMessage({
      message: '颜值分析完成',
      type: 'success'
    })
  } catch (error) {
    ElMessage({
      message: '颜值测试失败，请重试',
      type: 'error'
    })
    console.error(error)
  } finally {
    isTestingBeauty.value = false
  }
}
</script>

<style scoped>
:root {
  --primary-color: #409EFF;
  --accent-color: #FF6B6B;
}

/* 基础布局 */
.dashboard {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.profile-section {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 2rem;
  margin-bottom: 3rem;
}

.left-section {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

/* 头像部分 */
.avatar-container {
  width: 200px;
  height: 200px;
  margin: 0 auto;
  position: relative;
  cursor: pointer;
}

.avatar-image {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--primary-color);
  transition: transform 0.3s ease;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  border: 2px dashed #ddd;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.avatar-placeholder:hover {
  border-color: var(--primary-color);
  background: #ecf5ff;
}

/* 颜值评分部分 */
.beauty-score-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  margin: 24px 0;
}

.total-beauty-score {
  text-align: center;
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.score-circle {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409EFF, #36D1DC);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  font-weight: bold;
  margin: 0 auto 16px;
}

.score-label {
  font-size: 16px;
  margin-left: 4px;
}

.score-level {
  color: #409EFF;
  font-size: 18px;
  font-weight: 500;
}

.beauty-analysis {
  margin-bottom: 32px;
}

.beauty-analysis h3 {
  font-size: 20px;
  color: #333;
  margin: 0 0 24px 0;
  font-weight: 600;
}

.analysis-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.analysis-item {
  background: #f8f9fa;
  padding: 16px;
  border-radius: 12px;
  position: relative;
  transition: all 0.3s ease;
  cursor: pointer;
}

.analysis-item:hover {
  background: #ecf5ff;
  transform: translateY(-2px);
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-right: 32px;
}

.item-label {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.item-score {
  color: #409EFF;
  font-weight: bold;
}

.progress-bar {
  height: 6px;
  background: #e9ecef;
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: 8px;
}

.progress {
  height: 100%;
  background: linear-gradient(90deg, #409EFF, #36D1DC);
  border-radius: 3px;
  transition: width 0.3s ease;
}

.details-popup {
  position: absolute;
  left: calc(100% + 12px);
  top: 50%;
  transform: translateY(-50%);
  background: white;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  width: 280px;
  z-index: 1000;
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  pointer-events: none;
  white-space: normal;
  word-break: break-word;
  border: 1px solid #ebeef5;
}

@media (max-width: 768px) {
  .details-popup {
    position: fixed;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    width: calc(100% - 48px);
    max-width: 320px;
  }
}

/* 改善建议部分 */
.beauty-suggestions {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
}

.beauty-suggestions h3 {
  font-size: 18px;
  color: #333;
  margin: 0 0 16px 0;
  font-weight: 600;
}

.suggestions-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.suggestion-item {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  background: white;
  padding: 12px;
  border-radius: 8px;
}

.suggestion-number {
  width: 24px;
  height: 24px;
  background: #409EFF;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  flex-shrink: 0;
}

.suggestion-content {
  flex: 1;
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-top: 1rem;
}

.test-beauty-btn,
.match-btn {
  width: 100%;
  padding: 0.8rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.test-beauty-btn {
  background: var(--primary-color);
  color: white;
}

.test-beauty-btn:hover:not(:disabled) {
  background: #66b1ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.match-btn {
  background: var(--accent-color);
  color: white;
}

.match-btn:hover:not(:disabled) {
  background: #ff8f8f;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.2);
}

.test-beauty-btn:disabled,
.match-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

@media (max-width: 768px) {
  .dashboard {
    padding: 1rem;
  }

  .profile-section {
    grid-template-columns: 1fr;
  }

  .item-details-popup {
    position: fixed;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    width: calc(100% - 48px);
    max-width: 320px;
    background: white;
    z-index: 1000;
  }

  .item-details-popup::before {
    display: none;
  }

  .suggestion-item {
    flex-direction: column;
  }

  .suggestion-number {
    width: 20px;
    height: 20px;
    font-size: 12px;
  }
}

/* 用户信息卡片样式 */
.user-info-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.header-left {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.header-left h2 {
  font-size: 20px;
  color: #333;
  margin: 0;
  font-weight: 600;
}

.username {
  font-size: 16px;
  color: #409EFF;
  font-weight: 500;
}

.edit-btn {
  background: none;
  border: none;
  color: #409EFF;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 16px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.edit-btn:hover {
  background: #ecf5ff;
}

.edit-icon {
  font-size: 16px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.info-item:hover {
  background: #ecf5ff;
  transform: translateY(-2px);
}

.info-label {
  font-size: 14px;
  color: #666;
}

.info-value {
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

/* 编辑表单样式 */
.edit-form {
  padding: 16px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-item label {
  font-size: 14px;
  color: #666;
}

.form-item input,
.form-item select {
  padding: 8px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.form-item input:focus,
.form-item select:focus {
  border-color: #409EFF;
  outline: none;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.edit-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.save-btn,
.cancel-btn {
  padding: 8px 24px;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.save-btn {
  background: #409EFF;
  color: white;
  border: none;
}

.save-btn:hover {
  background: #66b1ff;
}

.cancel-btn {
  background: white;
  color: #666;
  border: 1px solid #dcdfe6;
}

.cancel-btn:hover {
  color: #409EFF;
  border-color: #409EFF;
  background: #ecf5ff;
}

@media (max-width: 768px) {
  .info-grid,
  .form-grid {
    grid-template-columns: 1fr;
  }
  
  .edit-actions {
    flex-direction: column;
  }
  
  .save-btn,
  .cancel-btn {
    width: 100%;
  }
}

/* 匹配结果部分样式 */
.matches-section {
  margin-top: 2rem;
  padding: 2rem;
  background: #f8f9fa;
  border-radius: 16px;
}

.matches-section h2 {
  font-size: 24px;
  color: #333;
  margin-bottom: 1.5rem;
}

.matches-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 2rem;
}

.match-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s ease;
}

.match-card:hover {
  transform: translateY(-5px);
}

.match-header {
  position: relative;
  height: 200px;
}

.match-avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.match-score-badge {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: rgba(64, 158, 255, 0.9);
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-weight: bold;
}

.match-info {
  padding: 1.5rem;
}

.match-basic {
  margin-bottom: 1.5rem;
}

.match-basic h3 {
  font-size: 20px;
  color: #333;
  margin: 0 0 0.5rem 0;
}

.match-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.match-tags span {
  background: #f0f7ff;
  color: #409eff;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 14px;
}

.match-analysis {
  border-top: 1px solid #f0f0f0;
  padding-top: 1.5rem;
}

.score-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 0.75rem;
}

.score-item .label {
  width: 100px;
  font-size: 14px;
  color: #666;
}

.score-bar {
  flex: 1;
  height: 6px;
  background: #f0f0f0;
  border-radius: 3px;
  overflow: hidden;
}

.score-progress {
  height: 100%;
  background: linear-gradient(90deg, #409eff, #36d1dc);
  border-radius: 3px;
  transition: width 0.3s ease;
}

.score-item .score {
  width: 50px;
  font-size: 14px;
  color: #409eff;
  text-align: right;
}

.mbti-match {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 12px;
  margin: 1rem 0;
}

.mbti-pair {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  margin-bottom: 0.5rem;
}

.mbti-pair span {
  font-weight: bold;
  color: #409eff;
}

.match-icon {
  font-size: 20px;
}

.mbti-score {
  text-align: center;
  font-size: 14px;
  color: #666;
}

.mbti-comment {
  margin-top: 0.5rem;
  font-size: 12px;
  color: #999;
}

.match-comment {
  margin-top: 1rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 12px;
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.match-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1.5rem;
}

.like-btn,
.chat-btn {
  flex: 1;
  padding: 0.75rem;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  transition: all 0.3s ease;
}

.like-btn {
  background: #ff6b6b;
  color: white;
}

.like-btn:hover {
  background: #ff8f8f;
  transform: translateY(-2px);
}

.chat-btn {
  background: #409eff;
  color: white;
}

.chat-btn:hover {
  background: #66b1ff;
  transform: translateY(-2px);
}

@media (max-width: 768px) {
  .matches-grid {
    grid-template-columns: 1fr;
  }
  
  .match-card {
    max-width: 100%;
  }
}
</style> 
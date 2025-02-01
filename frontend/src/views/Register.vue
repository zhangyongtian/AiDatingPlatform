<template>
  <div class="register-container">
    <div class="register-card">
      <h2 class="title">{{ currentStep === 1 ? '创建账号' : '完善个人信息' }}</h2>
      
      <!-- 步骤指示器 -->
      <div class="steps">
        <div class="step" :class="{ active: currentStep === 1 }">基本信息</div>
        <div class="step" :class="{ active: currentStep === 2 }">个性测评</div>
      </div>

      <!-- 第一步：基本注册 -->
      <div v-if="currentStep === 1" class="form-section">
        <div class="form-group">
          <label>用户名</label>
          <input 
            v-model="form.username" 
            type="text" 
            placeholder="请输入用户名"
          >
        </div>
        <div class="form-group">
          <label>密码</label>
          <input 
            v-model="form.password" 
            type="password" 
            placeholder="请输入密码"
          >
        </div>
        <div class="form-group">
          <label>确认密码</label>
          <input 
            v-model="form.confirmPassword" 
            type="password" 
            placeholder="请再次输入密码"
          >
        </div>
        <button class="submit-btn" @click="nextStep">下一步</button>
      </div>

      <!-- 第二步：个人信息和个性测评 -->
      <div v-else class="form-section">
        <!-- 基本信息部分 -->
        <div class="basic-info-section">
          <h3>基本信息</h3>
          <div class="form-grid">
            <div class="form-group">
              <label>性别</label>
              <select v-model="form.gender">
                <option value="">请选择</option>
                <option value="male">男</option>
                <option value="female">女</option>
              </select>
            </div>
            
            <div class="form-group">
              <label>年龄</label>
              <input 
                type="number" 
                v-model="form.age" 
                min="18" 
                max="100"
                placeholder="请输入年龄"
              >
            </div>

            <div class="form-item">
              <label>月收入</label>
              <select v-model="form.income">
                <option value="">请选择月收入</option>
                <option value="5k以下">5k以下</option>
                <option value="5k-10k">5k-10k</option>
                <option value="10k-20k">10k-20k</option>
                <option value="20k-50k">20k-50k</option>
                <option value="50k以上">50k以上</option>
              </select>
            </div>

            <div class="form-group">
              <label>学历</label>
              <select v-model="form.education">
                <option value="">请选择</option>
                <option value="1">高中及以下</option>
                <option value="2">大专</option>
                <option value="3">本科</option>
                <option value="4">硕士</option>
                <option value="5">博士及以上</option>
              </select>
            </div>

            <div class="form-group">
              <label>所在城市</label>
              <input 
                type="text" 
                v-model="form.city" 
                placeholder="请输入所在城市"
              >
            </div>

            <div class="form-group">
              <label>婚姻状况</label>
              <select v-model="form.maritalStatus">
                <option value="">请选择</option>
                <option value="1">未婚</option>
                <option value="2">离异</option>
                <option value="3">丧偶</option>
              </select>
            </div>

            <div class="form-group">
              <label>身高(cm)</label>
              <input 
                type="number" 
                v-model="form.height" 
                min="140"
                max="220"
                placeholder="请输入身高"
              >
            </div>
          </div>
        </div>

        <div class="form-group">
          <label>MBTI 性格类型</label>
          <div class="mbti-section">
            <div class="mbti-test-card">
              <div class="mbti-info">
                <h4>什么是MBTI？</h4>
                <p>MBTI是迈尔斯-布里格斯性格分类法，通过测试可以了解你的性格特征，帮助找到更适合的伴侣。</p>
              </div>
              <a 
                href="https://mbti.axiaoxin.com/#google_vignette" 
                target="_blank" 
                class="mbti-test-button"
              >
                <span class="test-icon">🎯</span>
                开始MBTI测试
                <span class="arrow">→</span>
              </a>
            </div>

            <select v-model="form.mbti" class="mbti-select">
              <option value="">请选择你的MBTI类型</option>
              <option v-for="type in mbtiTypes" :key="type" :value="type">
                {{ type }}
              </option>
            </select>
            
            <div class="mbti-tip">
              <span class="tip-icon">💡</span>
              完成测试后请回到本页面选择你的MBTI类型
            </div>
          </div>
        </div>

        <!-- 核心价值观向量 -->
        <div class="vector-section">
          <h3>核心价值观 (拖动滑块进行选择)</h3>
          <div v-for="(value, index) in coreValues" :key="index" class="vector-item">
            <span class="vector-label">{{ value.label }}</span>
            <div class="progress-container" @mouseenter="showStandard = value.label" @mouseleave="showStandard = ''">
              <input 
                type="range"
                v-model="value.score"
                min="50"
                max="100"
                class="score-slider"
              >
              <span class="vector-score">{{ value.score }}</span>
              
              <!-- 添加悬浮提示 -->
              <div class="score-tooltip" v-show="showStandard === value.label">
                {{ getStandardDescription(value.label) }}
              </div>
            </div>
          </div>
        </div>

        <!-- 生活方式向量 -->
        <div class="vector-section">
          <h3>生活方式</h3>
          <div v-for="(value, index) in lifestyle" :key="index" class="vector-item">
            <span class="vector-label">{{ value.label }}</span>
            <div class="progress-container" @mouseenter="showStandard = value.label" @mouseleave="showStandard = ''">
              <input 
                type="range"
                v-model="value.score"
                min="50"
                max="100"
                class="score-slider"
              >
              <span class="vector-score">{{ value.score }}</span>
              
              <!-- 添加悬浮提示 -->
              <div class="score-tooltip" v-show="showStandard === value.label">
                {{ getStandardDescription(value.label) }}
              </div>
            </div>
          </div>
        </div>

        <!-- 情感沟通向量 -->
        <div class="vector-section">
          <h3>情感沟通</h3>
          <div v-for="(value, index) in communication" :key="index" class="vector-item">
            <span class="vector-label">{{ value.label }}</span>
            <div class="progress-container" @mouseenter="showStandard = value.label" @mouseleave="showStandard = ''">
              <input 
                type="range"
                v-model="value.score"
                min="50"
                max="100"
                class="score-slider"
              >
              <span class="vector-score">{{ value.score }}</span>
              
              <!-- 添加悬浮提示 -->
              <div class="score-tooltip" v-show="showStandard === value.label">
                {{ getStandardDescription(value.label) }}
              </div>
            </div>
          </div>
        </div>

        <!-- 现实条件向量 -->
        <div class="vector-section">
          <h3>现实条件</h3>
          <div v-for="(value, index) in realConditions" :key="index" class="vector-item">
            <span class="vector-label">{{ value.label }}</span>
            <div class="progress-container" @mouseenter="showStandard = value.label" @mouseleave="showStandard = ''">
              <input 
                type="range"
                v-model="value.score"
                min="50"
                max="100"
                class="score-slider"
              >
              <span class="vector-score">{{ value.score }}</span>
              
              <!-- 添加悬浮提示 -->
              <div class="score-tooltip" v-show="showStandard === value.label">
                {{ getStandardDescription(value.label) }}
              </div>
            </div>
          </div>
        </div>

        <!-- 第二步表单的提交按钮 -->
        <button 
          class="submit-btn" 
          @click="submitForm" 
          :disabled="loading"
        >
          <span v-if="!loading">完成注册</span>
          <span v-else class="loading-spinner">
            <svg class="loading-icon" viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" width="16" height="16">
              <path fill="currentColor" d="M512 64a32 32 0 0 1 32 32v192a32 32 0 0 1-64 0V96a32 32 0 0 1 32-32zm0 640a32 32 0 0 1 32 32v192a32 32 0 1 1-64 0V736a32 32 0 0 1 32-32zm448-192a32 32 0 0 1-32 32H736a32 32 0 1 1 0-64h192a32 32 0 0 1 32 32zm-640 0a32 32 0 0 1-32 32H96a32 32 0 0 1 0-64h192a32 32 0 0 1 32 32zM195.2 195.2a32 32 0 0 1 45.248 0L376.32 331.008a32 32 0 0 1-45.248 45.248L195.2 240.448a32 32 0 0 1 0-45.248zm452.544 452.544a32 32 0 0 1 45.248 0L828.8 783.552a32 32 0 0 1-45.248 45.248L647.744 692.992a32 32 0 0 1 0-45.248zM828.8 195.264a32 32 0 0 1 0 45.184L692.992 376.32a32 32 0 0 1-45.248-45.248l135.808-135.808a32 32 0 0 1 45.248 0zm-452.544 452.48a32 32 0 0 1 0 45.248L240.448 828.8a32 32 0 0 1-45.248-45.248l135.808-135.808a32 32 0 0 1 45.248 0z"/>
            </svg>
            注册中...
          </span>
        </button>
        <button class="back-btn" @click="currentStep = 1" :disabled="loading">返回</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import 'element-plus/theme-chalk/el-message.css'
import 'element-plus/theme-chalk/el-message-box.css'

const router = useRouter()
const currentStep = ref(1)

const mbtiTypes = [
  'INTJ', 'INTP', 'ENTJ', 'ENTP',
  'INFJ', 'INFP', 'ENFJ', 'ENFP',
  'ISTJ', 'ISFJ', 'ESTJ', 'ESFJ',
  'ISTP', 'ISFP', 'ESTP', 'ESFP'
]

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  mbti: '',
  gender: '',
  age: '',
  income: '',
  education: '',
  city: '',
  maritalStatus: '',
  height: '',
})

const coreValues = reactive([
  { label: '家庭观念', score: 50 },
  { label: '事业追求', score: 50 },
  { label: '生活态度', score: 50 },
  { label: '消费理念', score: 50 },
  { label: '教育观念', score: 50 },
  { label: '婚姻观念', score: 50 },
  { label: '道德品质', score: 50 },
  { label: '兴趣爱好', score: 50 }
])

const lifestyle = reactive([
  { label: '作息规律', score: 50 },
  { label: '饮食习惯', score: 50 },
  { label: '运动健身', score: 50 },
  { label: '社交活跃度', score: 50 },
  { label: '生活整洁度', score: 50 },
  { label: '休闲方式', score: 50 }
])

const communication = reactive([
  { label: '表达能力', score: 50 },
  { label: '倾听能力', score: 50 },
  { label: '情绪管理', score: 50 },
  { label: '矛盾处理', score: 50 }
])

const realConditions = reactive([
  { label: '工作稳定性', score: 50 },
  { label: '居住条件', score: 50 },
  { label: '家庭背景', score: 50 },
  { label: '未来规划', score: 50 }
])

// 添加显示标准的状态
const showStandard = ref('')

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
    // 情感沟通
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

const loading = ref(false)

const nextStep = () => {
  if (!form.username || !form.password || !form.confirmPassword) {
    ElMessage({
      message: '请填写完整信息',
      type: 'warning',
      duration: 2000,
      showClose: true,
      center: true
    })
    return
  }
  if (form.password !== form.confirmPassword) {
    ElMessage({
      message: '两次密码输入不一致',
      type: 'error',
      duration: 2000,
      showClose: true,
      center: true
    })
    return
  }
  currentStep.value = 2
}

const submitForm = async () => {
  if (!form.mbti || !form.gender || !form.age || !form.income || 
      !form.education || !form.city || !form.maritalStatus || !form.height) {
    ElMessage({
      message: '请填写完整信息',
      type: 'warning',
      duration: 2000,
      showClose: true,
      center: true
    })
    return
  }

  try {
    loading.value = true  // 开始加载
    
    // 构造提交的数据
    const userData = {
      username: form.username,
      password: form.password,
      gender: form.gender,
      age: parseInt(form.age),
      city: form.city,
      income: form.income,
      education: parseInt(form.education),
      maritalStatus: parseInt(form.maritalStatus),
      mbti: form.mbti,
      height: parseInt(form.height),
      
      // 将各维度评分转换为下划线分隔的字符串
      coreValuesScores: coreValues.map(v => v.score).join('_'),
      lifestyleScores: lifestyle.map(v => v.score).join('_'),
      communicationScores: communication.map(v => v.score).join('_'),
      conditionScores: realConditions.map(v => v.score).join('_')
    }

    // 调用注册API
    const response = await fetch('/api/users/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(userData)
    })

    if (!response.ok) {
      throw new Error('注册失败')
    }

    await ElMessageBox.alert(
      '您的账号已成功注册！即将跳转到登录页面...',
      '注册成功',
      {
        confirmButtonText: '确定',
        type: 'success',
        center: true,
        showClose: false,
        customClass: 'custom-message-box',
        callback: () => {
          router.push('/login')
        }
      }
    )
  } catch (error) {
    ElMessage({
      message: error.message || '注册失败，请重试',
      type: 'error',
      duration: 3000,
      showClose: true,
      center: true
    })
  } finally {
    loading.value = false  // 结束加载
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 2rem;
  background: var(--background-color);
}

.register-card {
  background: white;
  padding: 2rem;
  border-radius: 20px;
  box-shadow: 0 8px 16px rgba(0,0,0,0.1);
  width: 100%;
  max-width: 600px;
}

.title {
  text-align: center;
  color: var(--primary-color);
  margin-bottom: 2rem;
}

.steps {
  display: flex;
  justify-content: center;
  margin-bottom: 2rem;
  gap: 2rem;
}

.step {
  padding: 0.5rem 1rem;
  border-radius: 20px;
  background: #f0f0f0;
  color: #666;
}

.step.active {
  background: var(--primary-color);
  color: white;
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  color: #666;
  font-size: 0.9rem;
}

input, select {
  padding: 0.8rem;
  border: 1px solid #ddd;
  border-radius: 10px;
  font-size: 1rem;
}

.vector-section {
  margin-top: 1.5rem;
}

.vector-section h3 {
  color: var(--primary-color);
  margin-bottom: 1rem;
  font-size: 1.1rem;
}

.vector-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1rem;
}

.vector-label {
  min-width: 100px;
  color: #666;
}

.progress-container {
  position: relative;
  flex: 1;
  display: flex;
  align-items: center;
  gap: 1rem;
}

.score-slider {
  flex: 1;
  height: 8px;
  border-radius: 4px;
  background: #f0f0f0;
  outline: none;
  -webkit-appearance: none;
}

.score-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: var(--primary-color);
  cursor: pointer;
}

.vector-score {
  min-width: 40px;
  text-align: right;
  color: var(--primary-color);
}

.score-tooltip {
  position: absolute;
  top: -40px;
  left: 50%;
  transform: translateX(-50%);
  background: white;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  font-size: 0.85rem;
  color: #666;
  white-space: nowrap;
  z-index: 10;
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
}

.score-tooltip::after {
  content: '';
  position: absolute;
  bottom: -5px;
  left: 50%;
  transform: translateX(-50%);
  border-width: 5px;
  border-style: solid;
  border-color: white transparent transparent transparent;
}

.progress-container:hover .score-tooltip {
  opacity: 1;
}

.submit-btn, .back-btn {
  padding: 1rem;
  border: none;
  border-radius: 10px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.submit-btn {
  background: var(--primary-color);
  color: white;
  position: relative;
  transition: all 0.3s ease;
}

.submit-btn:disabled {
  background-color: #a0cfff;
  cursor: not-allowed;
  opacity: 0.7;
}

.loading-spinner {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.loading-spinner i {
  animation: rotating 1s linear infinite;
}

@keyframes rotating {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.back-btn {
  background: #f0f0f0;
  color: #666;
  margin-top: 1rem;
  position: relative;
  transition: all 0.3s ease;
}

.back-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.submit-btn:hover, .back-btn:hover {
  transform: translateY(-2px);
}

.basic-info-section {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 15px;
  margin-bottom: 2rem;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1rem;
}

/* 适配移动端 */
@media (max-width: 640px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
  
  .vector-item {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .vector-item .vector-label {
    min-width: 100%;
    margin-bottom: 0.5rem;
  }
}

.mbti-section {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.mbti-test-card {
  background: linear-gradient(135deg, #fff5f5 0%, #fff0f3 100%);
  border: 1px solid #ffe0e0;
  border-radius: 15px;
  padding: 1.5rem;
  margin-bottom: 1rem;
}

.mbti-info {
  margin-bottom: 1rem;
}

.mbti-info h4 {
  color: var(--primary-color);
  margin-bottom: 0.5rem;
  font-size: 1.1rem;
}

.mbti-info p {
  color: #666;
  font-size: 0.9rem;
  line-height: 1.5;
}

.mbti-test-button {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  background: var(--primary-color);
  color: white;
  padding: 0.8rem 1.5rem;
  border-radius: 25px;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;
}

.mbti-test-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.2);
}

.test-icon {
  font-size: 1.2rem;
}

.arrow {
  transition: transform 0.3s ease;
}

.mbti-test-button:hover .arrow {
  transform: translateX(4px);
}

.mbti-select {
  padding: 0.8rem;
  border: 1px solid #ddd;
  border-radius: 10px;
  font-size: 1rem;
  width: 100%;
}

.mbti-tip {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  color: #666;
  padding: 0.8rem;
  background: #f8f9fa;
  border-radius: 8px;
}

.tip-icon {
  font-size: 1.2rem;
}

/* 适配移动端 */
@media (max-width: 640px) {
  .mbti-test-card {
    padding: 1rem;
  }
  
  .mbti-test-button {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .score-tooltip {
    width: 200px;
    white-space: normal;
    text-align: center;
  }
}

/* 添加全局消息提示样式 */
.el-message {
  min-width: 300px;
  padding: 15px 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.el-message--success {
  background: #f0f9eb;
  border-color: #e1f3d8;
}

.el-message--warning {
  background: #fdf6ec;
  border-color: #faecd8;
}

.el-message--error {
  background: #fef0f0;
  border-color: #fde2e2;
}

.el-message__content {
  font-size: 14px;
  line-height: 1.4;
}

/* 自定义 MessageBox 样式 */
.custom-message-box {
  width: 400px;
  border-radius: 12px;
  padding: 20px;
}

.custom-message-box .el-message-box__header {
  padding-bottom: 15px;
}

.custom-message-box .el-message-box__title {
  font-size: 20px;
  color: #67c23a;
}

.custom-message-box .el-message-box__content {
  padding: 20px;
  font-size: 16px;
  color: #606266;
  line-height: 1.6;
}

.custom-message-box .el-message-box__btns {
  padding-top: 15px;
}

.custom-message-box .el-button--primary {
  background-color: #67c23a;
  border-color: #67c23a;
  padding: 12px 24px;
  font-size: 14px;
  border-radius: 6px;
}

.custom-message-box .el-button--primary:hover {
  background-color: #85ce61;
  border-color: #85ce61;
}

/* 移动端适配 */
@media (max-width: 480px) {
  .custom-message-box {
    width: 90%;
    max-width: 350px;
  }
}

.loading-icon {
  animation: rotating 1s linear infinite;
}

@keyframes rotating {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style> 
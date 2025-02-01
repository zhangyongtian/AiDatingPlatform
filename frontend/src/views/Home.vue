<template>
  <div class="home">
    <div class="hero">
      <h1 class="title">寻找你的真爱</h1>
      <p class="subtitle">让我们帮你找到命中注定的那个人</p>
      <button class="start-btn" @click="startJourney">开启寻爱之旅</button>
    </div>
    
    <div class="features">
      <div class="feature-card" v-for="feature in features" :key="feature.title">
        <div class="icon">{{ feature.icon }}</div>
        <h3>{{ feature.title }}</h3>
        <p>{{ feature.description }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

interface Feature {
  icon: string
  title: string
  description: string
}

const router = useRouter()

const features = ref<Feature[]>([
  {
    icon: '💝',
    title: '智能匹配',
    description: '基于自研算法，多维度数据分析，为你匹配最适合的伴侣'
  },
  {
    icon: '🎯',
    title: '价值观契合',
    description: '注重三观匹配，找到真正懂你的人'
  },
  {
    icon: '✨',
    title: '真实可靠',
    description: '严格的实名认证，确保每个用户都是真实可靠的'
  }
])

const startJourney = () => {
  router.push('/login')
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  padding: 2rem;
}

.hero {
  text-align: center;
  padding: 4rem 0;
  background: linear-gradient(135deg, var(--primary-color), var(--accent-color));
  border-radius: 30px;
  color: white;
  margin-bottom: 4rem;
}

.title {
  font-size: 3.5rem;
  margin-bottom: 1rem;
  font-weight: 700;
}

.subtitle {
  font-size: 1.5rem;
  opacity: 0.9;
  margin-bottom: 2rem;
}

.start-btn {
  background: white;
  color: var(--primary-color);
  border: none;
  padding: 1rem 2rem;
  border-radius: 30px;
  font-size: 1.2rem;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.start-btn:hover {
  transform: scale(1.05);
}

.features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  padding: 2rem;
}

.feature-card {
  background: white;
  padding: 2rem;
  border-radius: 20px;
  text-align: center;
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-10px);
}

.icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.feature-card h3 {
  color: var(--primary-color);
  margin-bottom: 1rem;
}

.feature-card p {
  color: #666;
  line-height: 1.6;
}
</style> 
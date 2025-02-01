<template>
  <Transition name="fade">
    <div class="dialog-wrapper" v-if="show">
      <div class="dialog-content">
        <div class="dialog-header">
          <h3>颜值评分结果</h3>
        </div>
        
        <div class="score-section">
          <div class="score-circle">
            {{ score }}
            <span class="score-label">分</span>
          </div>
        </div>

        <div class="analysis-section">
          <h4>评分解析</h4>
          <div class="analysis-items">
            <div v-for="(item, index) in analysisItems" :key="index" class="analysis-item">
              <div class="item-label">{{ item.label }}</div>
              <div class="item-score">
                <div class="progress-bar">
                  <div class="progress" :style="{ width: item.score + '%' }"></div>
                </div>
                <span class="score-value">{{ item.score }}分</span>
              </div>
            </div>
          </div>
        </div>

        <div class="comment-section">
          <h4>评分说明</h4>
          <p class="comment">{{ comment }}</p>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup lang="ts">
import { computed, defineProps, defineEmits } from 'vue'

interface Props {
  show: boolean
  score: number
}

const props = defineProps<Props>()
const emit = defineEmits<{
  (e: 'close'): void
}>()

// 根据总分生成各项分析得分
const analysisItems = computed(() => [
  {
    label: '五官协调度',
    score: Math.min(100, props.score + Math.floor(Math.random() * 10 - 5))
  },
  {
    label: '面部轮廓',
    score: Math.min(100, props.score + Math.floor(Math.random() * 10 - 5))
  },
  {
    label: '肤质状态',
    score: Math.min(100, props.score + Math.floor(Math.random() * 10 - 5))
  },
  {
    label: '气质表现',
    score: Math.min(100, props.score + Math.floor(Math.random() * 10 - 5))
  }
])

// 根据分数生成评语
const comment = computed(() => {
  if (props.score >= 90) {
    return '您的颜值非常出众！五官精致协调，面部轮廓立体，肤质状态良好，整体气质出众。这样的外表会给您带来很大的社交优势。'
  } else if (props.score >= 80) {
    return '您的颜值很不错！五官搭配和谐，面部轮廓清晰，肤质状态不错，给人以阳光清新的印象。这样的外表在社交中很受欢迎。'
  } else if (props.score >= 70) {
    return '您的颜值在平均水平之上！五官比例适中，面部特征明显，整体给人以舒适自然的感觉。这样的外表容易给人留下好印象。'
  } else {
    return '您的颜值比较自然！五官搭配协调，面部轮廓自然，整体给人以亲和的感觉。这样的外表更容易拉近与他人的距离。'
  }
})
</script>

<style scoped>
.dialog-wrapper {
  position: absolute;
  left: calc(100% + 20px);  /* 在头像右侧20px处显示 */
  top: 50%;
  transform: translateY(-50%);
  z-index: 1000;
  pointer-events: none;  /* 防止鼠标移动到弹窗上触发mouseleave */
}

.dialog-content {
  background: white;
  border-radius: 20px;
  padding: 2rem;
  width: 400px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.1);
  position: relative;
  pointer-events: auto;  /* 恢复弹窗内部的鼠标事件 */
}

/* 添加一个小箭头指向头像 */
.dialog-content::before {
  content: '';
  position: absolute;
  left: -10px;
  top: 50%;
  transform: translateY(-50%);
  border-width: 10px;
  border-style: solid;
  border-color: transparent white transparent transparent;
}

/* 添加淡入淡出动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-50%) translateX(-20px);
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.dialog-header h3 {
  margin: 0;
  color: var(--primary-color);
}

.score-section {
  text-align: center;
  margin-bottom: 2rem;
}

.score-circle {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary-color), var(--accent-color));
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 2.5rem;
  font-weight: bold;
  margin: 0 auto;
  position: relative;
}

.score-label {
  font-size: 1rem;
  margin-left: 0.2rem;
}

.analysis-section {
  margin-bottom: 2rem;
}

.analysis-section h4 {
  color: var(--primary-color);
  margin-bottom: 1rem;
}

.analysis-items {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.analysis-item {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.item-label {
  width: 100px;
  color: #666;
}

.item-score {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 1rem;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
}

.progress {
  height: 100%;
  background: var(--primary-color);
  border-radius: 4px;
  transition: width 0.3s ease;
}

.score-value {
  width: 50px;
  color: var(--primary-color);
  font-weight: 500;
}

.comment-section {
  margin-bottom: 2rem;
}

.comment-section h4 {
  color: var(--primary-color);
  margin-bottom: 1rem;
}

.comment {
  color: #666;
  line-height: 1.6;
}

/* 添加移动端适配 */
@media (max-width: 768px) {
  .dialog-wrapper {
    position: fixed;
    left: 0;
    right: 0;
    top: auto;
    bottom: 0;
    transform: none;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: flex-end;
    padding: 1rem;
  }

  .dialog-content {
    width: 100%;
    max-width: 500px;
    border-radius: 20px 20px 0 0;
    padding: 1.5rem;
  }

  /* 移除箭头 */
  .dialog-content::before {
    display: none;
  }

  /* 修改动画 */
  .fade-enter-from,
  .fade-leave-to {
    opacity: 0;
    transform: translateY(100%);
  }

  /* 调整分数圆圈大小 */
  .score-circle {
    width: 100px;
    height: 100px;
    font-size: 2rem;
  }

  /* 调整分析项布局 */
  .item-label {
    width: 80px;
    font-size: 0.9rem;
  }

  .score-value {
    width: 40px;
    font-size: 0.9rem;
  }

  /* 调整评语字体 */
  .comment {
    font-size: 0.9rem;
  }
}
</style> 
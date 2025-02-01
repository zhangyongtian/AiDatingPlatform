<template>
  <div class="profile-card" :style="{ backgroundImage: `url(${profile.coverImage})` }">
    <div class="profile-info">
      <div class="profile-avatar">
        <img :src="profile.avatar" :alt="profile.name">
      </div>
      <h2 class="profile-name">{{ profile.name }}</h2>
      <div class="profile-tags">
        <span class="tag" v-for="tag in profile.tags" :key="tag">{{ tag }}</span>
      </div>
      <p class="profile-bio">{{ profile.bio }}</p>
      <div class="profile-actions">
        <button class="like-btn" @click="handleLike">
          <span class="heart">❤️</span> 喜欢
        </button>
        <button class="message-btn" @click="handleMessage">
          <span class="message">💌</span> 打招呼
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue'

interface Profile {
  id: number
  name: string
  avatar: string
  coverImage: string
  tags: string[]
  bio: string
}

const props = defineProps<{
  profile: Profile
}>()

const emit = defineEmits<{
  (e: 'like', id: number): void
  (e: 'message', id: number): void
}>()

const handleLike = () => {
  emit('like', props.profile.id)
}

const handleMessage = () => {
  emit('message', props.profile.id)
}
</script>

<style scoped>
.profile-card {
  width: 100%;
  max-width: 400px;
  height: 500px;
  border-radius: 20px;
  background-size: cover;
  background-position: center;
  position: relative;
  overflow: hidden;
}

.profile-info {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 2rem;
  background: linear-gradient(to top, rgba(0,0,0,0.8), transparent);
  color: white;
}

.profile-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  border: 4px solid white;
  overflow: hidden;
  margin: 0 auto;
}

.profile-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.profile-name {
  text-align: center;
  margin: 1rem 0;
  font-size: 1.5rem;
}

.profile-tags {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  justify-content: center;
  margin-bottom: 1rem;
}

.tag {
  background: rgba(255,255,255,0.2);
  padding: 0.3rem 0.8rem;
  border-radius: 20px;
  font-size: 0.9rem;
}

.profile-bio {
  text-align: center;
  margin-bottom: 1.5rem;
  line-height: 1.6;
}

.profile-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
}

.like-btn,
.message-btn {
  padding: 0.8rem 1.5rem;
  border-radius: 30px;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  transition: transform 0.3s ease;
}

.like-btn {
  background: var(--primary-color);
  color: white;
}

.message-btn {
  background: white;
  color: var(--primary-color);
}

.like-btn:hover,
.message-btn:hover {
  transform: scale(1.05);
}
</style> 
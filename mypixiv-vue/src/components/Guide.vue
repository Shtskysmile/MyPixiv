<template>
  <section class="guide" :style="bgStyle">
    <div class="overlay">
      <div class="content-wrapper">
        <div class="logo-container">
          <img src="@/assets/images/Pixiv_Icon.svg" alt="MyPixiv Logo" class="guide-logo">
        </div>
        <h1 class="title">欢迎来到 MyPixiv</h1>
        <p class="subtitle">探索精彩插画，发现创作者的世界</p>
        <p class="description">在这里，你可以欣赏来自世界各地创作者的精美作品</p>
        <div class="button-group">
          <button class="btn-enter" @click="enter">
            <span>开始探索</span>
            <svg class="arrow-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M5 12h14M12 5l7 7-7 7"/>
            </svg>
          </button>
        </div>
        <div class="features">
          <div class="feature-item">
            <div class="feature-icon">🎨</div>
            <div class="feature-text">精美插画</div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">👥</div>
            <div class="feature-text">优秀创作者</div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">💡</div>
            <div class="feature-text">创意灵感</div>
          </div>
        </div>
      </div>
    </div>
    <div class="scroll-indicator">
      <div class="mouse">
        <div class="wheel"></div>
      </div>
    </div>
  </section>
</template>

<script>
import bg from '@/assets/images/Lincy_Background.jpg'

export default {
  computed: {
    bgStyle() {
      return {
        backgroundImage: `url(${bg})`,
        backgroundSize: 'cover',
        backgroundPosition: 'center'
      }
    }
  },
  methods: {
    enter() {
      this.$router.push({ path: '/index' })
    }
  }
}
</script>

<style scoped>
.guide {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 50;
  overflow: hidden;
}

.guide::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: inherit;
  filter: blur(0);
  transition: filter 0.5s ease;
}

.overlay {
  position: relative;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    135deg,
    rgba(0, 82, 212, 0.7) 0%,
    rgba(30, 110, 255, 0.6) 50%,
    rgba(0, 150, 255, 0.5) 100%
  );
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
}

.content-wrapper {
  text-align: center;
  color: #ffffff;
  max-width: 800px;
  padding: 40px 20px;
}

/* Logo 动画 */
.logo-container {
  margin-bottom: 32px;
  opacity: 0;
  transform: scale(0.8);
  animation: logoAppear 800ms ease-out 100ms forwards;
}

.guide-logo {
  width: 120px;
  height: 120px;
  filter: drop-shadow(0 8px 24px rgba(0, 0, 0, 0.3));
  animation: float 3s ease-in-out infinite;
}

@keyframes logoAppear {
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

/* 标题样式 */
.title {
  font-size: 56px;
  font-weight: 800;
  margin-bottom: 20px;
  color: #ffffff !important;
  text-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  letter-spacing: -1px;
  opacity: 0;
  transform: translateY(30px);
  animation: fadeUp 800ms ease-out 300ms forwards;
}

.subtitle {
  font-size: 24px;
  font-weight: 500;
  margin-bottom: 16px;
  color: #ffffff !important;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  opacity: 0;
  transform: translateY(30px);
  animation: fadeUp 800ms ease-out 500ms forwards;
}

.description {
  font-size: 18px;
  font-weight: 400;
  margin-bottom: 40px;
  color: rgba(255, 255, 255, 0.9);
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  opacity: 0;
  transform: translateY(30px);
  animation: fadeUp 800ms ease-out 700ms forwards;
}

/* 按钮组 */
.button-group {
  margin-bottom: 48px;
  opacity: 0;
  transform: translateY(30px);
  animation: fadeUp 800ms ease-out 900ms forwards;
}

.btn-enter {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  background: linear-gradient(135deg, #ffffff 0%, #f0f8ff 100%);
  color: #1e6fff;
  border: none;
  border-radius: 50px;
  padding: 18px 48px;
  font-size: 20px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  position: relative;
  overflow: hidden;
}

.btn-enter::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(30, 110, 255, 0.1);
  transform: translate(-50%, -50%);
  transition: width 0.6s, height 0.6s;
}

.btn-enter:hover::before {
  width: 300px;
  height: 300px;
}

.btn-enter:hover {
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.3);
}

.btn-enter:active {
  transform: translateY(-2px) scale(1.02);
}

.arrow-icon {
  transition: transform 0.3s ease;
}

.btn-enter:hover .arrow-icon {
  transform: translateX(5px);
}

/* 特性列表 */
.features {
  display: flex;
  justify-content: center;
  gap: 48px;
  flex-wrap: wrap;
  opacity: 0;
  transform: translateY(30px);
  animation: fadeUp 800ms ease-out 1100ms forwards;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  transition: transform 0.3s ease;
}

.feature-item:hover {
  transform: translateY(-8px);
}

.feature-icon {
  font-size: 48px;
  filter: drop-shadow(0 4px 12px rgba(0, 0, 0, 0.2));
  animation: bounce 2s ease-in-out infinite;
}

.feature-item:nth-child(1) .feature-icon {
  animation-delay: 0s;
}

.feature-item:nth-child(2) .feature-icon {
  animation-delay: 0.2s;
}

.feature-item:nth-child(3) .feature-icon {
  animation-delay: 0.4s;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.feature-text {
  font-size: 16px;
  font-weight: 600;
  color: #ffffff;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

/* 滚动指示器 */
.scroll-indicator {
  position: absolute;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  opacity: 0;
  animation: fadeIn 1s ease-out 1500ms forwards;
}

.mouse {
  width: 26px;
  height: 40px;
  border: 2px solid rgba(255, 255, 255, 0.8);
  border-radius: 13px;
  position: relative;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.wheel {
  width: 4px;
  height: 8px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 2px;
  position: absolute;
  top: 8px;
  left: 50%;
  transform: translateX(-50%);
  animation: scroll 1.5s ease-in-out infinite;
}

@keyframes scroll {
  0%, 100% {
    opacity: 0;
    top: 8px;
  }
  50% {
    opacity: 1;
    top: 16px;
  }
}

@keyframes fadeUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  to {
    opacity: 1;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .title {
    font-size: 40px;
  }
  
  .subtitle {
    font-size: 20px;
  }
  
  .description {
    font-size: 16px;
  }
  
  .guide-logo {
    width: 90px;
    height: 90px;
  }
  
  .btn-enter {
    padding: 16px 36px;
    font-size: 18px;
  }
  
  .features {
    gap: 32px;
  }
  
  .feature-icon {
    font-size: 36px;
  }
  
  .feature-text {
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .title {
    font-size: 32px;
  }
  
  .subtitle {
    font-size: 18px;
  }
  
  .description {
    font-size: 14px;
  }
  
  .guide-logo {
    width: 70px;
    height: 70px;
  }
  
  .btn-enter {
    padding: 14px 28px;
    font-size: 16px;
  }
  
  .features {
    gap: 24px;
  }
}
</style>

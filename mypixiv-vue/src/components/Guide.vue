<template>
  <section class="guide" :style="bgStyle">
    <div class="overlay">
      <div class="content">
        <h1 class="title">欢迎来到 MyPixiv</h1>
        <p class="subtitle">探索精彩插画，发现创作者的世界</p>
        <button class="button is-large is-primary" @click="enter">进入主页</button>
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
}
.overlay {
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.4);
  display: flex;
  align-items: center;
  justify-content: center;
}
  .content {
  text-align: center;
  color: #ffffff;
}
.title {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0;
  transform: translateY(20px);
  animation: fadeUp 700ms ease-out 200ms forwards;
}
.subtitle {
  font-size: 20px;
  margin-bottom: 24px;
  opacity: 0;
  transform: translateY(20px);
  animation: fadeUp 700ms ease-out 400ms forwards;
}

.title, .subtitle {
  color: #ffffff !important;
}
.button.is-large {
  opacity: 0;
  transform: translateY(20px);
  animation: fadeUp 700ms ease-out 600ms forwards;
}

@keyframes fadeUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>

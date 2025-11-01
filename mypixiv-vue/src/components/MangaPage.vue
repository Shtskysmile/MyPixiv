<template>
  <div class="page-root manga-theme">
    <div class="page-bg" :style="bgStyle"></div>
    <div class="page-overlay"></div>
    
    <!-- 漫画主题装饰 -->
    <div class="particles">
      <div class="particle" v-for="i in 15" :key="i" :style="getParticleStyle(i)"></div>
    </div>
    
    <div class="floating-decorations">
      <div class="float-book book-1">📚</div>
      <div class="float-book book-2">📖</div>
      <div class="float-bubble bubble-1">💬</div>
      <div class="float-bubble bubble-2">💭</div>
      <div class="float-star star-1">🌟</div>
      <div class="float-star star-2">✨</div>
    </div>
    
    <div class="container">
      <Navbar></Navbar>

      <div class="columns">
        <div class="column is-one-fifth">
          <Sidebar />
        </div>
        <div class="column">
          <!-- 标题区域 -->
          <div class="header-section anime-box manga-header">
            <div class="title-wrapper">
              <div class="title-icon-wrapper">
                <span class="title-icon">📚</span>
                <div class="icon-glow manga-glow"></div>
              </div>
              <div class="title-content">
                <h1 class="page-title">
                  <span class="title-text">精彩漫画</span>
                  <span class="title-badge manga-badge">Manga</span>
                </h1>
                <p class="page-subtitle">
                  <span class="subtitle-icon">✨</span>
                  沉浸在漫画的奇幻世界
                  <span class="subtitle-icon">✨</span>
                </p>
              </div>
            </div>
            
            <div class="quick-stats">
              <div class="stat-item manga-stat">
                <span class="stat-icon">📚</span>
                <div class="stat-info">
                  <span class="stat-value">{{ images.length }}</span>
                  <span class="stat-label">漫画</span>
                </div>
              </div>
              <div class="stat-item manga-stat">
                <span class="stat-icon">👁️</span>
                <div class="stat-info">
                  <span class="stat-value">{{ totalViews }}</span>
                  <span class="stat-label">浏览</span>
                </div>
              </div>
              <div class="stat-item manga-stat">
                <span class="stat-icon">❤️</span>
                <div class="stat-info">
                  <span class="stat-value">{{ totalLikes }}</span>
                  <span class="stat-label">点赞</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 加载状态 -->
          <div v-if="loading" class="loading-container anime-box">
            <div class="loading-spinner">
              <div class="spinner-ring manga-ring"></div>
              <div class="spinner-ring manga-ring"></div>
              <div class="spinner-ring manga-ring"></div>
              <span class="loading-text">加载漫画中...</span>
            </div>
          </div>

          <!-- 作品网格 -->
          <div v-else class="gallery-section anime-box">
            <div class="grid">
              <div 
                class="cell" 
                v-for="(img, index) in images" 
                :key="img.contributionId"
                :style="{ animationDelay: `${index * 0.05}s` }"
              >
                <ImageBlock :image="img" />
              </div>
            </div>
            
            <div v-if="images.length === 0" class="empty-state">
              <div class="empty-icon-wrapper">
                <span class="empty-icon">📚</span>
                <div class="empty-icon-bg manga-empty-bg"></div>
              </div>
              <p class="empty-title">暂无漫画作品</p>
              <p class="empty-desc">快来上传你的第一部漫画吧！</p>
              <router-link to="/user" class="empty-button manga-button">
                <span class="btn-icon">📤</span>
                <span>上传漫画</span>
              </router-link>
            </div>
          </div>

          <!-- 分页 -->
          <div class="pagination-section anime-box" v-if="totalPage > 1 && !loading">
            <Pagination
              :page="page"
              :totalPage="totalPage"
              @update:page="onPageChange"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import Navbar from "./Navbar.vue";
import ImageBlock from "./ImageBlock.vue";
import Pagination from "./Pagination.vue";
import Sidebar from "./Sidebar.vue";
import bgImg from "@/assets/images/Moonshadow_CyberLeader.jpg";

export default {
  components: {
    ImageBlock,
    Navbar,
    Pagination,
    Sidebar,
  },
  data() {
    return {
      images: [],
      page: 1,
      totalPage: 1,
      loading: false,
    };
  },
  created() {
    this.page = this.getPageFromUrl();
    this.fetchMangas();
  },
  computed: {
    bgStyle() {
      return {
        backgroundImage: `url(${bgImg})`,
        backgroundSize: "cover",
        backgroundPosition: "center center",
        position: "fixed",
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        zIndex: -2,
      };
    },
    totalViews() {
      return this.images.reduce((sum, img) => sum + (img.viewCount || 0), 0);
    },
    totalLikes() {
      return this.images.reduce((sum, img) => sum + (img.likeCount || 0), 0);
    }
  },
  methods: {
    fetchMangas() {
      this.loading = true;
      axios.get('/mangas')
        .then((res) => {
          if (res.data && res.data.code === 200) {
            this.images = res.data.data || [];
            this.totalPage = Math.ceil(this.images.length / 35) || 1;
          } else {
            console.error('获取漫画列表失败:', res.data?.message);
            this.images = [];
          }
        })
        .catch((err) => {
          console.error('请求失败:', err);
          this.images = [];
        })
        .finally(() => {
          this.loading = false;
        });
    },
    getPageFromUrl() {
      const url = new URL(window.location.href);
      const p = url.searchParams.get("page");
      return p ? parseInt(p) : 1;
    },
    onPageChange(newPage) {
      this.page = newPage;
      const url = new URL(window.location.href);
      url.searchParams.set("page", newPage);
      window.history.replaceState(null, "", url.toString());
      this.fetchMangas();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    },
    getParticleStyle(index) {
      const size = Math.random() * 4 + 2;
      const left = Math.random() * 100;
      const animationDuration = Math.random() * 10 + 10;
      const animationDelay = Math.random() * 5;
      
      return {
        width: `${size}px`,
        height: `${size}px`,
        left: `${left}%`,
        animationDuration: `${animationDuration}s`,
        animationDelay: `${animationDelay}s`
      };
    }
  },
};
</script>

<style scoped>
/* 漫画主题色 - 粉红紫色调 */
.manga-theme {
  --primary-color: #f093fb;
  --secondary-color: #f5576c;
  --accent-color: #e94057;
}

.page-root {
  position: relative;
  min-height: 100vh;
  overflow-x: hidden;
}

.page-bg {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-repeat: no-repeat;
  background-size: cover;
  background-position: center center;
  z-index: -2;
  animation: bgPulse 20s ease-in-out infinite;
}

@keyframes bgPulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.page-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    135deg,
    rgba(240, 147, 251, 0.4) 0%,
    rgba(245, 87, 108, 0.5) 50%,
    rgba(0, 0, 0, 0.5) 100%
  );
  z-index: -1;
}

/* 粒子效果 */
.particles {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.particle {
  position: absolute;
  background: radial-gradient(circle, rgba(240, 147, 251, 0.8) 0%, transparent 70%);
  border-radius: 50%;
  animation: particleFloat linear infinite;
  opacity: 0.6;
}

@keyframes particleFloat {
  0% {
    transform: translateY(100vh) rotate(0deg);
    opacity: 0;
  }
  10% {
    opacity: 0.6;
  }
  90% {
    opacity: 0.6;
  }
  100% {
    transform: translateY(-100px) rotate(360deg);
    opacity: 0;
  }
}

/* 浮动装饰 */
.floating-decorations {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 0;
}

.float-book, .float-bubble, .float-star {
  position: absolute;
  font-size: 36px;
  opacity: 0.5;
  filter: drop-shadow(0 0 10px rgba(240, 147, 251, 0.5));
}

.book-1 {
  top: 18%;
  left: 10%;
  animation: floatBook 8s ease-in-out infinite;
}

.book-2 {
  bottom: 28%;
  right: 10%;
  animation: floatBook 9s ease-in-out infinite 1.5s;
}

.bubble-1 {
  top: 45%;
  right: 15%;
  animation: floatBubble 6s ease-in-out infinite;
}

.bubble-2 {
  bottom: 40%;
  left: 12%;
  animation: floatBubble 7s ease-in-out infinite 2s;
}

.star-1 {
  top: 30%;
  left: 18%;
  animation: sparkle 4s ease-in-out infinite;
}

.star-2 {
  top: 68%;
  right: 18%;
  animation: sparkle 5s ease-in-out infinite 1.5s;
}

@keyframes floatBook {
  0%, 100% {
    transform: translateY(0) rotate(-5deg);
  }
  50% {
    transform: translateY(-20px) rotate(5deg);
  }
}

@keyframes floatBubble {
  0%, 100% {
    transform: translateY(0) scale(1);
    opacity: 0.5;
  }
  50% {
    transform: translateY(-15px) scale(1.15);
    opacity: 0.7;
  }
}

@keyframes sparkle {
  0%, 100% {
    opacity: 0.3;
    transform: scale(1);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.3);
  }
}

.container {
  position: relative;
  z-index: 1;
  padding-top: 80px;
}

/* 漫画主题卡片 */
.anime-box {
  background: rgba(255, 255, 255, 0.98) !important;
  border: 3px solid transparent;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(240, 147, 251, 0.2);
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  position: relative;
  overflow: hidden;
  margin-bottom: 24px;
}

.anime-box::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 20px;
  padding: 3px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 50%, #e94057 100%);
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.anime-box:hover::before {
  opacity: 1;
}

.anime-box:hover {
  box-shadow: 0 16px 48px rgba(240, 147, 251, 0.3);
  transform: translateY(-4px);
}

/* 漫画主题标题 */
.manga-header {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.98) 0%, rgba(255, 240, 250, 0.98) 100%) !important;
}

.header-section {
  padding: 32px !important;
}

.title-wrapper {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 24px;
}

.title-icon-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.title-icon {
  font-size: 64px;
  position: relative;
  z-index: 1;
  animation: iconFlip 4s ease-in-out infinite;
}

@keyframes iconFlip {
  0%, 100% { transform: rotateY(0deg); }
  50% { transform: rotateY(180deg); }
}

.manga-glow {
  position: absolute;
  width: 80px;
  height: 80px;
  background: radial-gradient(circle, rgba(240, 147, 251, 0.4) 0%, transparent 70%);
  border-radius: 50%;
  animation: glowPulse 2s ease-in-out infinite;
}

@keyframes glowPulse {
  0%, 100% { transform: scale(1); opacity: 0.5; }
  50% { transform: scale(1.2); opacity: 0.8; }
}

.title-content {
  flex: 1;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;
}

.title-text {
  font-size: 42px;
  font-weight: 800;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -1px;
}

.manga-badge {
  display: inline-block;
  padding: 6px 16px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
  font-size: 14px;
  font-weight: 700;
  border-radius: 20px;
  text-transform: uppercase;
  letter-spacing: 1px;
  box-shadow: 0 4px 12px rgba(240, 147, 251, 0.4);
}

.page-subtitle {
  font-size: 18px;
  color: #f093fb;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.subtitle-icon {
  font-size: 16px;
  animation: sparkle 2s ease-in-out infinite;
}

/* 漫画主题统计 */
.quick-stats {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.manga-stat {
  flex: 1;
  min-width: 150px;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  background: linear-gradient(135deg, rgba(240, 147, 251, 0.1) 0%, rgba(245, 87, 108, 0.1) 100%);
  border-radius: 16px;
  border: 2px solid rgba(240, 147, 251, 0.2);
  transition: all 0.3s ease;
}

.manga-stat:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(240, 147, 251, 0.2);
  border-color: rgba(240, 147, 251, 0.4);
}

.stat-icon {
  font-size: 36px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-value {
  font-size: 24px;
  font-weight: 800;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 12px;
  color: #f093fb;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* 加载状态 */
.loading-container {
  padding: 80px 20px !important;
  display: flex;
  justify-content: center;
  align-items: center;
}

.loading-spinner {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
}

.manga-ring {
  position: absolute;
  width: 80px;
  height: 80px;
  border: 4px solid transparent;
  border-top-color: #f093fb;
  border-radius: 50%;
  animation: spin 1.5s linear infinite;
}

.manga-ring:nth-child(2) {
  width: 60px;
  height: 60px;
  border-top-color: #f5576c;
  animation-duration: 1s;
  animation-direction: reverse;
}

.manga-ring:nth-child(3) {
  width: 40px;
  height: 40px;
  border-top-color: #e94057;
  animation-duration: 0.75s;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-text {
  margin-top: 100px;
  font-size: 18px;
  font-weight: 600;
  color: #f093fb;
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

/* 作品网格 */
.gallery-section {
  padding: 32px !important;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 24px;
  padding: 0;
}

.cell {
  animation: fadeInUp 0.6s ease-out backwards;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 空状态 */
.empty-state {
  padding: 80px 20px;
  text-align: center;
}

.empty-icon-wrapper {
  position: relative;
  display: inline-block;
  margin-bottom: 24px;
}

.empty-icon {
  font-size: 100px;
  position: relative;
  z-index: 1;
  animation: iconFlip 4s ease-in-out infinite;
}

.manga-empty-bg {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 120px;
  height: 120px;
  background: radial-gradient(circle, rgba(240, 147, 251, 0.2) 0%, transparent 70%);
  border-radius: 50%;
  animation: glowPulse 2s ease-in-out infinite;
}

.empty-title {
  font-size: 28px;
  font-weight: 700;
  color: #f093fb;
  margin-bottom: 12px;
}

.empty-desc {
  font-size: 16px;
  color: #f5576c;
  margin-bottom: 32px;
}

.manga-button {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 14px 32px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
  font-size: 16px;
  font-weight: 700;
  border-radius: 50px;
  text-decoration: none;
  box-shadow: 0 8px 20px rgba(240, 147, 251, 0.3);
  transition: all 0.3s ease;
}

.manga-button:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 28px rgba(240, 147, 251, 0.4);
}

.btn-icon {
  font-size: 20px;
}

/* 分页区域 */
.pagination-section {
  padding: 24px 32px !important;
  display: flex;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .title-wrapper {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .quick-stats {
    width: 100%;
  }
  
  .manga-stat {
    min-width: calc(50% - 10px);
  }
}

@media (max-width: 768px) {
  .container {
    padding-top: 60px;
  }
  
  .header-section {
    padding: 24px !important;
  }
  
  .title-icon {
    font-size: 48px;
  }
  
  .title-text {
    font-size: 32px;
  }
  
  .manga-badge {
    font-size: 12px;
    padding: 4px 12px;
  }
  
  .page-subtitle {
    font-size: 16px;
  }
  
  .quick-stats {
    flex-direction: column;
  }
  
  .manga-stat {
    min-width: 100%;
  }
  
  .grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 16px;
  }
  
  .gallery-section {
    padding: 20px !important;
  }
  
  .floating-decorations {
    display: none;
  }
}

@media (max-width: 480px) {
  .title-text {
    font-size: 28px;
  }
  
  .grid {
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
    gap: 12px;
  }
}
</style>


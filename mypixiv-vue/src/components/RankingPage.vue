<template>
  <div class="page-root">
    <div class="page-bg" :style="bgStyle"></div>
    <div class="page-overlay"></div>
    
    <!-- 装饰性粒子效果 -->
    <div class="particles">
      <div class="particle" v-for="i in 20" :key="i" :style="getParticleStyle(i)"></div>
    </div>
    
    <!-- 浮动装饰元素 -->
    <div class="floating-decorations">
      <div class="float-star star-1">🏆</div>
      <div class="float-star star-2">⭐</div>
      <div class="float-star star-3">👑</div>
    </div>
    
    <div class="container">
      <Navbar></Navbar>

      <div class="columns">
        <div class="column is-one-fifth">
          <Sidebar />
        </div>
        <div class="column">
          <!-- 排行榜标题区域 -->
          <div class="header-section anime-box">
            <div class="title-wrapper">
              <div class="title-icon-wrapper">
                <span class="title-icon">🏆</span>
                <div class="icon-glow"></div>
              </div>
              <div class="title-content">
                <h1 class="page-title">
                  <span class="title-text">排行榜</span>
                  <span class="title-badge">Ranking</span>
                </h1>
                <p class="page-subtitle">
                  <span class="subtitle-icon">✨</span>
                  发现最受欢迎的精彩作品
                  <span class="subtitle-icon">✨</span>
                </p>
              </div>
            </div>
            
            <!-- 作品类型选择器 -->
            <div class="filter-section">
              <div class="filter-group">
                <label class="filter-label">🎨 作品类型</label>
                <div class="filter-buttons">
                  <button 
                    class="filter-btn"
                    :class="{ 'active': contentType === -1 }"
                    @click="changeContentType(-1)"
                  >
                    <span class="btn-icon">🌟</span>
                    全部
                  </button>
                  <button 
                    class="filter-btn"
                    :class="{ 'active': contentType === 0 }"
                    @click="changeContentType(0)"
                  >
                    <span class="btn-icon">🖼️</span>
                    插画
                  </button>
                  <button 
                    class="filter-btn"
                    :class="{ 'active': contentType === 1 }"
                    @click="changeContentType(1)"
                  >
                    <span class="btn-icon">📚</span>
                    漫画
                  </button>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 排序方式选择器 -->
          <div class="sort-tabs anime-box">
            <button 
              class="sort-tab"
              :class="{ 'active': sortKey === 0 }"
              @click="changeSortKey(0)"
            >
              <span class="tab-icon">👁️</span>
              浏览量
            </button>
            <button 
              class="sort-tab"
              :class="{ 'active': sortKey === 2 }"
              @click="changeSortKey(2)"
            >
              <span class="tab-icon">❤️</span>
              点赞量
            </button>
            <button 
              class="sort-tab"
              :class="{ 'active': sortKey === 1 }"
              @click="changeSortKey(1)"
            >
              <span class="tab-icon">⭐</span>
              收藏量
            </button>
            <button 
              class="sort-tab"
              :class="{ 'active': sortKey === 3 }"
              @click="changeSortKey(3)"
            >
              <span class="tab-icon">💬</span>
              评论量
            </button>
          </div>
          
          <!-- 加载状态 -->
          <div v-if="loading" class="loading-container anime-box">
            <div class="loading-spinner">
              <div class="spinner-ring"></div>
              <div class="spinner-ring"></div>
              <div class="spinner-ring"></div>
            </div>
            <p class="loading-text">正在加载排行榜...</p>
          </div>
          
          <!-- 全部模式：分别显示插画和漫画排行榜 -->
          <div v-else-if="contentType === -1">
            <!-- 插画排行榜 -->
            <div class="ranking-section">
              <div class="section-header anime-box">
                <span class="section-icon">🖼️</span>
                <h2 class="section-title">插画排行榜</h2>
              </div>
              
              <div v-if="illustrationRankings.length > 0" class="ranking-list">
                <div 
                  v-for="(item, index) in illustrationRankings" 
                  :key="'ill-' + item.contributionId"
                  class="ranking-item anime-box"
                  :class="{ 'top-three': index < 3 }"
                  :style="{ animationDelay: `${index * 0.05}s` }"
                  @click="goToDetail(item.contributionId)"
                >
                  <div class="rank-badge" :class="`rank-${index + 1}`">
                    <span v-if="index === 0" class="rank-icon">🥇</span>
                    <span v-else-if="index === 1" class="rank-icon">🥈</span>
                    <span v-else-if="index === 2" class="rank-icon">🥉</span>
                    <span v-else class="rank-number">{{ index + 1 }}</span>
                  </div>
                  
                  <div class="item-cover">
                    <img 
                      v-if="!loadedImages[item.contributionId]"
                      class="placeholder-img"
                      src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='300' height='300'%3E%3Crect fill='%23f0f0f0' width='300' height='300'/%3E%3C/svg%3E"
                      alt="loading"
                    >
                    <img 
                      :src="getImageUrl(item.image)" 
                      :alt="item.title"
                      :class="{ 'img-loaded': loadedImages[item.contributionId] }"
                      @load="onImageLoad(item.contributionId)"
                      @error="onImageError"
                    />
                  </div>
                  
                  <div class="item-info">
                    <h3 class="item-title">{{ item.title }}</h3>
                    <div class="item-author">
                      <img 
                        :src="getUserAvatarUrl(item.avatar)" 
                        alt="avatar" 
                        class="author-avatar"
                        @error="onAvatarError"
                      />
                      <span class="author-name">{{ item.authorName || formatAuthorId(item.authorId) }}</span>
                    </div>
                    
                    <div class="item-stats">
                      <div class="stat-item" :class="{ 'highlight': sortKey === 0 }">
                        <span class="stat-icon">👁️</span>
                        <span class="stat-value">{{ formatCount(item.viewCount) }}</span>
                      </div>
                      <div class="stat-item" :class="{ 'highlight': sortKey === 2 }">
                        <span class="stat-icon">❤️</span>
                        <span class="stat-value">{{ formatCount(item.likeCount) }}</span>
                      </div>
                      <div class="stat-item" :class="{ 'highlight': sortKey === 1 }">
                        <span class="stat-icon">⭐</span>
                        <span class="stat-value">{{ formatCount(item.favoriteCount) }}</span>
                      </div>
                      <div class="stat-item" :class="{ 'highlight': sortKey === 3 }">
                        <span class="stat-icon">💬</span>
                        <span class="stat-value">{{ formatCount(item.commentCount) }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              <div v-else class="empty-results anime-box">
                <div class="empty-icon">📊</div>
                <h3 class="empty-title">暂无插画排行数据</h3>
              </div>
            </div>
            
            <!-- 漫画排行榜 -->
            <div class="ranking-section">
              <div class="section-header anime-box">
                <span class="section-icon">📚</span>
                <h2 class="section-title">漫画排行榜</h2>
              </div>
              
              <div v-if="mangaRankings.length > 0" class="ranking-list">
                <div 
                  v-for="(item, index) in mangaRankings" 
                  :key="'manga-' + item.contributionId"
                  class="ranking-item anime-box"
                  :class="{ 'top-three': index < 3 }"
                  :style="{ animationDelay: `${index * 0.05}s` }"
                  @click="goToDetail(item.contributionId)"
                >
                  <div class="rank-badge" :class="`rank-${index + 1}`">
                    <span v-if="index === 0" class="rank-icon">🥇</span>
                    <span v-else-if="index === 1" class="rank-icon">🥈</span>
                    <span v-else-if="index === 2" class="rank-icon">🥉</span>
                    <span v-else class="rank-number">{{ index + 1 }}</span>
                  </div>
                  
                  <div class="item-cover">
                    <img 
                      v-if="!loadedImages[item.contributionId]"
                      class="placeholder-img"
                      src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='300' height='300'%3E%3Crect fill='%23f0f0f0' width='300' height='300'/%3E%3C/svg%3E"
                      alt="loading"
                    >
                    <img 
                      :src="getImageUrl(item.image)" 
                      :alt="item.title"
                      :class="{ 'img-loaded': loadedImages[item.contributionId] }"
                      @load="onImageLoad(item.contributionId)"
                      @error="onImageError"
                    />
                  </div>
                  
                  <div class="item-info">
                    <h3 class="item-title">{{ item.title }}</h3>
                    <div class="item-author">
                      <img 
                        :src="getUserAvatarUrl(item.avatar)" 
                        alt="avatar" 
                        class="author-avatar"
                        @error="onAvatarError"
                      />
                      <span class="author-name">{{ item.authorName || formatAuthorId(item.authorId) }}</span>
                    </div>
                    
                    <div class="item-stats">
                      <div class="stat-item" :class="{ 'highlight': sortKey === 0 }">
                        <span class="stat-icon">👁️</span>
                        <span class="stat-value">{{ formatCount(item.viewCount) }}</span>
                      </div>
                      <div class="stat-item" :class="{ 'highlight': sortKey === 2 }">
                        <span class="stat-icon">❤️</span>
                        <span class="stat-value">{{ formatCount(item.likeCount) }}</span>
                      </div>
                      <div class="stat-item" :class="{ 'highlight': sortKey === 1 }">
                        <span class="stat-icon">⭐</span>
                        <span class="stat-value">{{ formatCount(item.favoriteCount) }}</span>
                      </div>
                      <div class="stat-item" :class="{ 'highlight': sortKey === 3 }">
                        <span class="stat-icon">💬</span>
                        <span class="stat-value">{{ formatCount(item.commentCount) }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              <div v-else class="empty-results anime-box">
                <div class="empty-icon">📊</div>
                <h3 class="empty-title">暂无漫画排行数据</h3>
              </div>
            </div>
          </div>
          
          <!-- 单独类型模式：只显示一个排行榜 -->
          <div v-else-if="rankings.length > 0" class="ranking-list">
            <div 
              v-for="(item, index) in rankings" 
              :key="item.contributionId"
              class="ranking-item anime-box"
              :class="{ 'top-three': index < 3 }"
              :style="{ animationDelay: `${index * 0.05}s` }"
              @click="goToDetail(item.contributionId)"
            >
              <div class="rank-badge" :class="`rank-${index + 1}`">
                <span v-if="index === 0" class="rank-icon">🥇</span>
                <span v-else-if="index === 1" class="rank-icon">🥈</span>
                <span v-else-if="index === 2" class="rank-icon">🥉</span>
                <span v-else class="rank-number">{{ index + 1 }}</span>
              </div>
              
              <div class="item-cover">
                <img 
                  v-if="!loadedImages[item.contributionId]"
                  class="placeholder-img"
                  src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='300' height='300'%3E%3Crect fill='%23f0f0f0' width='300' height='300'/%3E%3C/svg%3E"
                  alt="loading"
                >
                <img 
                  :src="getImageUrl(item.image)" 
                  :alt="item.title"
                  :class="{ 'img-loaded': loadedImages[item.contributionId] }"
                  @load="onImageLoad(item.contributionId)"
                  @error="onImageError"
                />
              </div>
              
              <div class="item-info">
                <h3 class="item-title">{{ item.title }}</h3>
                <div class="item-author">
                  <img 
                    :src="getUserAvatarUrl(item.avatar)" 
                    alt="avatar" 
                    class="author-avatar"
                    @error="onAvatarError"
                  />
                  <span class="author-name">{{ item.authorName || formatAuthorId(item.authorId) }}</span>
                </div>
                
                <div class="item-stats">
                  <div class="stat-item" :class="{ 'highlight': sortKey === 0 }">
                    <span class="stat-icon">👁️</span>
                    <span class="stat-value">{{ formatCount(item.viewCount) }}</span>
                  </div>
                  <div class="stat-item" :class="{ 'highlight': sortKey === 2 }">
                    <span class="stat-icon">❤️</span>
                    <span class="stat-value">{{ formatCount(item.likeCount) }}</span>
                  </div>
                  <div class="stat-item" :class="{ 'highlight': sortKey === 1 }">
                    <span class="stat-icon">⭐</span>
                    <span class="stat-value">{{ formatCount(item.favoriteCount) }}</span>
                  </div>
                  <div class="stat-item" :class="{ 'highlight': sortKey === 3 }">
                    <span class="stat-icon">💬</span>
                    <span class="stat-value">{{ formatCount(item.commentCount) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 空状态 -->
          <div v-else class="empty-results anime-box">
            <div class="empty-icon">📊</div>
            <h3 class="empty-title">暂无排行数据</h3>
            <p class="empty-text">请稍后再试</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import Navbar from './Navbar.vue';
import Sidebar from './Sidebar.vue';

export default {
  name: 'RankingPage',
  components: {
    Navbar,
    Sidebar
  },
  data() {
    return {
      contentType: -1, // -1-全部, 0-插画(illustration), 1-漫画(manga)
      sortKey: 0, // 根据Enum: 0-viewCount, 1-favoriteCount, 2-likeCount, 3-commentCount
      illustrationRankings: [], // 插画排行榜
      mangaRankings: [], // 漫画排行榜
      rankings: [], // 单独类型排行榜
      loading: false,
      loadedImages: {},
      bgStyle: {
        backgroundImage: `linear-gradient(135deg, #667eea 0%, #764ba2 100%)`
      }
    };
  },
  created() {
    this.fetchRankings();
  },
  methods: {
    async fetchRankings() {
      this.loading = true;
      
      if (this.contentType === -1) {
        // 全部模式：同时获取插画和漫画排行榜
        await Promise.all([
          this.fetchRankingByType(0), // 插画 (illustration)
          this.fetchRankingByType(1)  // 漫画 (manga)
        ]);
      } else {
        // 单独类型：只获取指定类型的排行榜
        await this.fetchRankingByType(this.contentType);
      }
      
      this.loading = false;
    },
    
    async fetchRankingByType(type) {
      try {
        const params = new URLSearchParams();
        params.append('type', type); // 0-插画, 1-漫画
        params.append('key', this.sortKey); // 0-浏览量, 1-收藏量, 2-点赞量, 3-评论量
        
        const response = await axios.post('/api/contributionsRanking', params);
        
        // 后端成功状态：code === 0 或 code === 200
        if (response.data && (response.data.code === 0 || response.data.code === 200)) {
          const data = response.data.data || [];
          
          if (this.contentType === -1) {
            // 全部模式：分别存储
            if (type === 0) {
              this.illustrationRankings = data;
            } else {
              this.mangaRankings = data;
            }
          } else {
            // 单独类型模式
            this.rankings = data;
          }
        } else {
          console.error('获取排行榜失败:', response.data?.message);
          if (this.contentType === -1) {
            if (type === 0) {
              this.illustrationRankings = [];
            } else {
              this.mangaRankings = [];
            }
          } else {
            this.rankings = [];
          }
        }
      } catch (err) {
        console.error('请求失败:', err);
        if (this.contentType === -1) {
          if (type === 0) {
            this.illustrationRankings = [];
          } else {
            this.mangaRankings = [];
          }
        } else {
          this.rankings = [];
        }
      }
    },
    
    changeContentType(type) {
      this.contentType = type;
      this.loadedImages = {}; // 重置图片加载状态
      this.fetchRankings();
    },
    
    changeSortKey(key) {
      this.sortKey = key;
      this.loadedImages = {}; // 重置图片加载状态
      this.fetchRankings();
    },
    
    goToDetail(id) {
      this.$router.push(`/image/${id}`);
    },
    
    formatCount(count) {
      if (!count) return 0;
      if (count >= 10000) {
        return (count / 10000).toFixed(1) + 'w';
      } else if (count >= 1000) {
        return (count / 1000).toFixed(1) + 'k';
      }
      return count;
    },
    
    formatAuthorId(authorId) {
      if (!authorId) return '匿名';
      return authorId.length > 12 ? authorId.substring(0, 12) + '...' : authorId;
    },
    
    getImageUrl(imagePath) {
      if (!imagePath) {
        return 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="300" height="300"%3E%3Crect fill="%23f0f0f0" width="300" height="300"/%3E%3C/svg%3E';
      }
      
      // 如果是数组，取第一个元素
      if (Array.isArray(imagePath)) {
        imagePath = imagePath[0];
      }
      
      // 如果已经是完整URL，直接返回
      if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
        return imagePath;
      }
      
      // 否则拼接API路径
      const cleanPath = imagePath.startsWith('/') ? imagePath.slice(1) : imagePath;
      return `/api/${cleanPath}`;
    },
    
    getUserAvatarUrl(avatar) {
      if (!avatar) {
        return 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="100" height="100"%3E%3Ccircle cx="50" cy="50" r="50" fill="%23ddd"/%3E%3Ctext x="50" y="50" text-anchor="middle" dy=".3em" font-size="40" fill="%23999"%3E👤%3C/text%3E%3C/svg%3E';
      }
      
      if (avatar.startsWith('http://') || avatar.startsWith('https://')) {
        return avatar;
      }
      
      const cleanPath = avatar.startsWith('/') ? avatar.slice(1) : avatar;
      return `/api/${cleanPath}`;
    },
    
    onImageLoad(id) {
      this.$set(this.loadedImages, id, true);
    },
    
    onImageError(e) {
      e.target.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="300" height="300"%3E%3Crect fill="%23f0f0f0" width="300" height="300"/%3E%3Ctext x="150" y="150" text-anchor="middle" font-size="20" fill="%23999"%3E加载失败%3C/text%3E%3C/svg%3E';
    },
    
    onAvatarError(e) {
      e.target.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="100" height="100"%3E%3Ccircle cx="50" cy="50" r="50" fill="%23ddd"/%3E%3Ctext x="50" y="50" text-anchor="middle" dy=".3em" font-size="40" fill="%23999"%3E👤%3C/text%3E%3C/svg%3E';
    },
    
    getParticleStyle(index) {
      const size = Math.random() * 4 + 2;
      const duration = Math.random() * 20 + 10;
      const delay = Math.random() * 5;
      const left = Math.random() * 100;
      
      return {
        width: `${size}px`,
        height: `${size}px`,
        left: `${left}%`,
        animationDuration: `${duration}s`,
        animationDelay: `${delay}s`
      };
    }
  }
};
</script>

<style scoped>
/* ==================== 页面根容器 ==================== */
.page-root {
  min-height: 100vh;
  position: relative;
  overflow-x: hidden;
}

/* 背景层 */
.page-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -2;
  transition: background-image 1s ease;
}

.page-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.3);
  z-index: -1;
}

/* ==================== 粒子效果 ==================== */
.particles {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.particle {
  position: absolute;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  animation: particle-rise 10s infinite ease-in;
}

@keyframes particle-rise {
  0% {
    transform: translate(0, var(--start-y, 100%)) scale(0);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translate(var(--end-x, 0), -100vh) scale(1);
    opacity: 0;
  }
}

/* ==================== 浮动装饰 ==================== */
.floating-decorations {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.float-star {
  position: absolute;
  font-size: 24px;
  animation: float-bounce 3s infinite ease-in-out;
  opacity: 0.6;
}

.star-1 { top: 15%; left: 10%; animation-delay: 0s; }
.star-2 { top: 25%; right: 15%; animation-delay: 0.5s; }
.star-3 { top: 60%; left: 20%; animation-delay: 1s; }

@keyframes float-bounce {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(10deg); }
}

/* ==================== 容器 ==================== */
.container {
  position: relative;
  z-index: 1;
  padding: 80px 20px 20px 20px;
  max-width: 1400px;
  margin: 0 auto;
}

/* ==================== 动画盒子 ==================== */
.anime-box {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  animation: fadeInUp 0.6s ease;
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

/* ==================== 标题区域 ==================== */
.header-section {
  margin-bottom: 24px;
}

.title-wrapper {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 24px;
}

.title-icon-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.title-icon {
  font-size: 48px;
  filter: drop-shadow(0 4px 12px rgba(0, 0, 0, 0.15));
  animation: bounce 2s ease-in-out infinite;
  position: relative;
  z-index: 1;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.icon-glow {
  position: absolute;
  width: 60px;
  height: 60px;
  background: radial-gradient(circle, rgba(102, 126, 234, 0.4), transparent);
  border-radius: 50%;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.5;
  }
  50% {
    transform: scale(1.5);
    opacity: 0.2;
  }
}

.title-content {
  flex: 1;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 16px;
  margin: 0 0 8px 0;
}

.title-text {
  font-size: 32px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 2px 10px rgba(102, 126, 234, 0.2);
}

.title-badge {
  padding: 4px 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 1px;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.page-subtitle {
  font-size: 16px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
}

.subtitle-icon {
  font-size: 14px;
  animation: sparkle 2s ease-in-out infinite;
}

@keyframes sparkle {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.6;
    transform: scale(1.2);
  }
}

/* ==================== 筛选器区域 ==================== */
.filter-section {
  margin-top: 24px;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.filter-label {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.filter-buttons {
  display: flex;
  gap: 12px;
}

.filter-btn {
  flex: 1;
  padding: 10px 20px;
  background: rgba(102, 126, 234, 0.1);
  border: 2px solid transparent;
  border-radius: 12px;
  color: #667eea;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.filter-btn:hover {
  background: rgba(102, 126, 234, 0.2);
  transform: translateY(-2px);
}

.filter-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: rgba(255, 255, 255, 0.3);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.btn-icon {
  font-size: 16px;
  margin-right: 4px;
}

/* ==================== 排行榜分类标题 ==================== */
.ranking-section {
  margin-bottom: 48px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 24px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  border-radius: 16px;
  margin-bottom: 24px;
  border-left: 4px solid #667eea;
}

.section-icon {
  font-size: 28px;
  animation: float 3s ease-in-out infinite;
}

.section-title {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* ==================== 排序标签页 ==================== */
.sort-tabs {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  margin-bottom: 24px;
}

.sort-tab {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 20px;
  background: rgba(102, 126, 234, 0.1);
  border: 2px solid transparent;
  border-radius: 12px;
  color: #667eea;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.sort-tab:hover {
  background: rgba(102, 126, 234, 0.2);
  transform: translateY(-2px);
}

.sort-tab.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: rgba(255, 255, 255, 0.3);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.tab-icon {
  font-size: 18px;
}

/* ==================== 加载状态 ==================== */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  text-align: center;
}

.loading-spinner {
  position: relative;
  width: 80px;
  height: 80px;
  margin-bottom: 24px;
}

.spinner-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  border: 4px solid transparent;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 1.5s linear infinite;
}

.spinner-ring:nth-child(2) {
  border-top-color: #764ba2;
  animation-delay: 0.5s;
  width: 80%;
  height: 80%;
  top: 10%;
  left: 10%;
}

.spinner-ring:nth-child(3) {
  border-top-color: #f093fb;
  animation-delay: 1s;
  width: 60%;
  height: 60%;
  top: 20%;
  left: 20%;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  font-size: 16px;
  color: #667eea;
  font-weight: 600;
}

/* ==================== 排行榜列表 ==================== */
.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  animation: fadeInUp 0.6s ease;
  animation-fill-mode: both;
}

.ranking-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(102, 126, 234, 0.25);
}

.ranking-item.top-three {
  background: linear-gradient(135deg, rgba(255, 215, 0, 0.08) 0%, rgba(255, 255, 255, 0.98) 100%);
  border: 2px solid rgba(255, 215, 0, 0.2);
  box-shadow: 0 8px 24px rgba(255, 215, 0, 0.15);
}

/* 排名徽章 */
.rank-badge {
  flex-shrink: 0;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-weight: 800;
  font-size: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.rank-badge.rank-1 {
  background: linear-gradient(135deg, #FFD700 0%, #FFA500 100%);
  box-shadow: 0 4px 16px rgba(255, 215, 0, 0.5);
}

.rank-badge.rank-2 {
  background: linear-gradient(135deg, #C0C0C0 0%, #808080 100%);
  box-shadow: 0 4px 16px rgba(192, 192, 192, 0.5);
}

.rank-badge.rank-3 {
  background: linear-gradient(135deg, #CD7F32 0%, #8B4513 100%);
  box-shadow: 0 4px 16px rgba(205, 127, 50, 0.5);
}

.rank-icon {
  font-size: 32px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

.rank-number {
  font-size: 24px;
}

/* 作品封面 */
.item-cover {
  flex-shrink: 0;
  width: 180px;
  height: 135px;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
  background: #f0f0f0;
}

.item-cover .placeholder-img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.3s ease;
  opacity: 0;
}

.item-cover img.img-loaded {
  opacity: 1;
}

.ranking-item:hover .item-cover img {
  transform: scale(1.08);
}

/* 作品信息 */
.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-width: 0;
}

.item-title {
  font-size: 18px;
  font-weight: 700;
  color: #333;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-author {
  display: flex;
  align-items: center;
  gap: 10px;
}

.author-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #667eea;
  flex-shrink: 0;
}

.author-name {
  font-size: 14px;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 统计数据 */
.item-stats {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: rgba(102, 126, 234, 0.08);
  border-radius: 8px;
  transition: all 0.3s ease;
}

.stat-item.highlight {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  transform: scale(1.05);
}

.stat-item.highlight .stat-icon,
.stat-item.highlight .stat-value {
  color: white;
}

.stat-icon {
  font-size: 16px;
}

.stat-value {
  font-size: 13px;
  font-weight: 600;
  color: #333;
}

/* ==================== 空状态 ==================== */
.empty-results {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
  opacity: 0.6;
  animation: bounce 2s ease-in-out infinite;
}

.empty-title {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  margin: 0 0 8px 0;
}

.empty-text {
  font-size: 14px;
  color: #666;
  margin: 0;
}

/* ==================== 响应式设计 ==================== */
@media (max-width: 1200px) {
  .container {
    padding: 80px 16px 20px 16px;
  }
}

@media (max-width: 768px) {
  .title-wrapper {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .title-icon {
    font-size: 36px;
  }
  
  .title-text {
    font-size: 24px;
  }
  
  .page-subtitle {
    font-size: 14px;
  }
  
  .filter-buttons {
    flex-direction: column;
  }
  
  .sort-tabs {
    flex-wrap: wrap;
  }
  
  .sort-tab {
    flex: 1 1 calc(50% - 6px);
    min-width: calc(50% - 6px);
  }
  
  .ranking-item {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .rank-badge {
    width: 50px;
    height: 50px;
    font-size: 20px;
  }
  
  .item-cover {
    width: 100%;
    height: 200px;
  }
  
  .item-stats {
    gap: 12px;
  }
  
  .float-star {
    display: none;
  }
}
</style>


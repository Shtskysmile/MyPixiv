<template>
  <div class="ranking-page">
    <Navbar :no-container="true" />
    
    <div class="ranking-container">
      <Sidebar />
      
      <div class="ranking-content">
        <!-- 背景装饰 -->
        <div class="background-decorations">
          <div class="floating-trophy">🏆</div>
          <div class="floating-star">⭐</div>
          <div class="floating-medal">🥇</div>
          <div class="floating-crown">👑</div>
        </div>
        
        <!-- 页面标题 -->
        <div class="ranking-header anime-box">
          <div class="header-content">
            <div class="title-section">
              <span class="title-icon">🏆</span>
              <h1 class="page-title">
                <span class="title-text">作品排行榜</span>
                <span class="title-badge">Ranking</span>
              </h1>
              <p class="page-subtitle">
                <span class="subtitle-icon">✨</span>
                发现最受欢迎的精彩作品
                <span class="subtitle-icon">✨</span>
              </p>
            </div>
          </div>
        </div>
        
        <!-- 时间范围选择器 -->
        <div class="time-selector anime-box">
          <button 
            class="time-btn"
            :class="{ 'active': timeRange === 'daily' }"
            @click="changeTimeRange('daily')"
          >
            <span class="btn-icon">📅</span>
            <span class="btn-text">每日最佳</span>
          </button>
          <button 
            class="time-btn"
            :class="{ 'active': timeRange === 'weekly' }"
            @click="changeTimeRange('weekly')"
          >
            <span class="btn-icon">📊</span>
            <span class="btn-text">每周最佳</span>
          </button>
          <button 
            class="time-btn"
            :class="{ 'active': timeRange === 'monthly' }"
            @click="changeTimeRange('monthly')"
          >
            <span class="btn-icon">📈</span>
            <span class="btn-text">每月最佳</span>
          </button>
        </div>
        
        <!-- 排序方式选择器 -->
        <div class="sort-selector anime-box">
          <button 
            class="sort-btn"
            :class="{ 'active': sortKey === 0 }"
            @click="changeSortKey(0)"
          >
            <span class="sort-icon">👁️</span>
            <span class="sort-text">浏览量</span>
          </button>
          <button 
            class="sort-btn"
            :class="{ 'active': sortKey === 1 }"
            @click="changeSortKey(1)"
          >
            <span class="sort-icon">⭐</span>
            <span class="sort-text">收藏量</span>
          </button>
          <button 
            class="sort-btn"
            :class="{ 'active': sortKey === 2 }"
            @click="changeSortKey(2)"
          >
            <span class="sort-icon">❤️</span>
            <span class="sort-text">点赞量</span>
          </button>
          <button 
            class="sort-btn"
            :class="{ 'active': sortKey === 3 }"
            @click="changeSortKey(3)"
          >
            <span class="sort-icon">💬</span>
            <span class="sort-text">评论量</span>
          </button>
        </div>
        
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-container">
          <div class="loading-spinner">
            <div class="spinner-ring"></div>
            <div class="spinner-ring"></div>
            <div class="spinner-ring"></div>
          </div>
          <p class="loading-text">正在加载排行榜...</p>
        </div>
        
        <!-- 排行榜列表 -->
        <div v-else-if="rankings.length > 0" class="ranking-list">
          <div 
            v-for="(item, index) in rankings" 
            :key="item.contributionId"
            class="ranking-item anime-box"
            :class="{ 'top-three': index < 3 }"
            @click="goToDetail(item.contributionId)"
          >
            <!-- 排名徽章 -->
            <div class="rank-badge" :class="`rank-${index + 1}`">
              <span v-if="index === 0" class="rank-icon">🥇</span>
              <span v-else-if="index === 1" class="rank-icon">🥈</span>
              <span v-else-if="index === 2" class="rank-icon">🥉</span>
              <span v-else class="rank-number">{{ index + 1 }}</span>
            </div>
            
            <!-- 作品封面 -->
            <div class="item-cover">
              <img :src="item.image" :alt="item.title" />
              <div class="cover-overlay">
                <span class="view-detail">查看详情</span>
              </div>
            </div>
            
            <!-- 作品信息 -->
            <div class="item-info">
              <h3 class="item-title">{{ item.title }}</h3>
              <div class="item-author">
                <img :src="item.avatar" alt="avatar" class="author-avatar" />
                <span class="author-id">{{ formatAuthorId(item.authorId) }}</span>
              </div>
              
              <!-- 统计数据 -->
              <div class="item-stats">
                <div class="stat-item" :class="{ 'highlight': sortKey === 0 }">
                  <span class="stat-icon">👁️</span>
                  <span class="stat-value">{{ formatCount(item.viewCount) }}</span>
                </div>
                <div class="stat-item" :class="{ 'highlight': sortKey === 1 }">
                  <span class="stat-icon">⭐</span>
                  <span class="stat-value">{{ formatCount(item.favoriteCount) }}</span>
                </div>
                <div class="stat-item" :class="{ 'highlight': sortKey === 2 }">
                  <span class="stat-icon">❤️</span>
                  <span class="stat-value">{{ formatCount(item.likeCount) }}</span>
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
        <div v-else class="empty-state anime-box">
          <div class="empty-icon">📊</div>
          <p class="empty-text">暂无排行数据</p>
          <p class="empty-hint">请稍后再试</p>
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
      timeRange: 'daily', // daily, weekly, monthly
      sortKey: 0, // 0-浏览量, 1-收藏量, 2-点赞量, 3-评论量
      rankings: [],
      loading: false
    };
  },
  created() {
    this.fetchRankings();
  },
  methods: {
    async fetchRankings() {
      this.loading = true;
      try {
        const params = new URLSearchParams();
        params.append('type', -1); // -1表示所有类型（插画+漫画）
        params.append('key', this.sortKey);
        
        const response = await axios.post('/api/contributionsRanking', params);
        
        if (response.data && response.data.code === 200) {
          this.rankings = response.data.data || [];
          
          // 根据时间范围模拟筛选（实际应该由后端处理）
          // 这里为了演示，我们只是限制数量
          const limit = this.timeRange === 'daily' ? 20 : 
                       this.timeRange === 'weekly' ? 30 : 50;
          this.rankings = this.rankings.slice(0, limit);
        } else {
          console.error('获取排行榜失败:', response.data?.message);
          this.rankings = [];
        }
      } catch (err) {
        console.error('请求失败:', err);
        this.rankings = [];
      } finally {
        this.loading = false;
      }
    },
    
    changeTimeRange(range) {
      this.timeRange = range;
      this.fetchRankings();
    },
    
    changeSortKey(key) {
      this.sortKey = key;
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
    }
  }
};
</script>

<style scoped>
.ranking-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow-x: hidden;
}

.ranking-container {
  display: flex;
  padding-top: 60px;
  min-height: 100vh;
}

.ranking-content {
  flex: 1;
  padding: 32px;
  margin-left: 250px;
  position: relative;
}

/* 背景装饰 */
.background-decorations {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 0;
}

.floating-trophy,
.floating-star,
.floating-medal,
.floating-crown {
  position: absolute;
  font-size: 48px;
  opacity: 0.1;
  animation: float 6s ease-in-out infinite;
}

.floating-trophy {
  top: 10%;
  left: 15%;
  animation-delay: 0s;
}

.floating-star {
  top: 30%;
  right: 20%;
  animation-delay: 1s;
}

.floating-medal {
  bottom: 20%;
  left: 25%;
  animation-delay: 2s;
}

.floating-crown {
  bottom: 35%;
  right: 15%;
  animation-delay: 3s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(5deg);
  }
}

/* 通用卡片样式 */
.anime-box {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.3);
  position: relative;
  z-index: 1;
  transition: all 0.3s ease;
}

.anime-box:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

/* 页面标题 */
.ranking-header {
  margin-bottom: 24px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.98) 0%, rgba(255, 255, 255, 0.95) 100%);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.title-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.title-icon {
  font-size: 48px;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.1));
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.page-title {
  display: flex;
  align-items: center;
  gap: 16px;
  margin: 0;
}

.title-text {
  font-size: 36px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.title-badge {
  display: inline-block;
  padding: 6px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.page-subtitle {
  font-size: 18px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 8px;
}

.subtitle-icon {
  font-size: 16px;
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

/* 时间范围选择器 */
.time-selector {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  padding: 16px;
}

.time-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 16px 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border: 2px solid transparent;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 16px;
  font-weight: 600;
  color: #666;
}

.time-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.2);
}

.time-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
}

.btn-icon {
  font-size: 24px;
}

.btn-text {
  font-size: 16px;
}

/* 排序方式选择器 */
.sort-selector {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 32px;
  padding: 16px;
}

.sort-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border: 2px solid transparent;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.sort-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.2);
}

.sort-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
}

.sort-btn.active .sort-icon,
.sort-btn.active .sort-text {
  color: white;
}

.sort-icon {
  font-size: 32px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.sort-text {
  font-size: 14px;
  font-weight: 600;
  color: #666;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
}

.loading-spinner {
  position: relative;
  width: 80px;
  height: 80px;
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
}

.spinner-ring:nth-child(3) {
  border-top-color: #f093fb;
  animation-delay: 1s;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  margin-top: 24px;
  font-size: 18px;
  color: white;
  font-weight: 600;
}

/* 排行榜列表 */
.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.ranking-item:hover {
  transform: translateX(8px);
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.3);
}

.ranking-item.top-three {
  background: linear-gradient(135deg, rgba(255, 215, 0, 0.1) 0%, rgba(255, 255, 255, 0.95) 100%);
  border: 2px solid rgba(255, 215, 0, 0.3);
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
  width: 160px;
  height: 120px;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
}

.item-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.ranking-item:hover .item-cover img {
  transform: scale(1.1);
}

.cover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(102, 126, 234, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.ranking-item:hover .cover-overlay {
  opacity: 1;
}

.view-detail {
  color: white;
  font-weight: 600;
  font-size: 14px;
}

/* 作品信息 */
.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.item-title {
  font-size: 20px;
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
  gap: 8px;
}

.author-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #667eea;
}

.author-id {
  font-size: 14px;
  color: #666;
}

/* 统计数据 */
.item-stats {
  display: flex;
  gap: 24px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: rgba(102, 126, 234, 0.1);
  border-radius: 8px;
  transition: all 0.3s ease;
}

.stat-item.highlight {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.stat-item.highlight .stat-icon,
.stat-item.highlight .stat-value {
  color: white;
}

.stat-icon {
  font-size: 18px;
}

.stat-value {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 24px;
  opacity: 0.5;
}

.empty-text {
  font-size: 24px;
  font-weight: 600;
  color: #666;
  margin: 0 0 12px 0;
}

.empty-hint {
  font-size: 16px;
  color: #999;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .ranking-content {
    margin-left: 0;
    padding: 24px 16px;
  }
  
  .sort-selector {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .ranking-item {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .item-cover {
    width: 100%;
    height: 200px;
  }
}

@media (max-width: 768px) {
  .title-text {
    font-size: 28px;
  }
  
  .time-selector {
    flex-direction: column;
  }
  
  .sort-selector {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .item-stats {
    flex-wrap: wrap;
    gap: 12px;
  }
  
  .background-decorations {
    display: none;
  }
}
</style>


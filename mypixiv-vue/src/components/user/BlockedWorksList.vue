<template>
  <div class="anime-blocked-container">
    <div class="blocked-header">
      <h2 class="title anime-gradient-text">
        <span class="icon">🚫</span> 已封禁作品管理
      </h2>
      <p class="subtitle">社区管理员封禁管理平台</p>
    </div>

    <!-- 作品列表 -->
    <div class="works-grid">
      <div v-if="blockedWorks.length === 0" class="empty-state">
        <span class="icon">📭</span>
        <p>暂无已封禁作品</p>
      </div>
      <div v-for="work in blockedWorks" :key="work.contributionId" class="work-card anime-card" @click="viewDetail(work)">
        <div class="work-image">
          <img :src="getImageUrl(work.image[0])" :alt="work.title" @error="onImageError" />
          <div class="work-badge blocked-badge">🚫 已封禁</div>
          <div class="work-overlay">
            <span class="view-btn">查看详情</span>
          </div>
        </div>
        <div class="work-info">
          <h4 class="work-title">{{ work.title }}</h4>
          <div class="work-meta">
            <span class="author">
              <span class="icon">👤</span> {{ work.authorName }}
            </span>
          </div>
          <div class="work-stats">
            <span><span class="icon">👁️</span> {{ work.viewCount }}</span>
            <span><span class="icon">❤️</span> {{ work.likeCount }}</span>
            <span><span class="icon">⭐</span> {{ work.favoriteCount }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request';
import placeholderImg from '@/assets/images/avatar.png';

export default {
  name: 'BlockedWorksList',
  data() {
    return {
      blockedWorks: [],
      placeholderImg
    };
  },
  created() {
    this.fetchBlockedWorks();
  },
  methods: {
    fetchBlockedWorks() {
      // 调用后端接口 GET /api/communityAdmin/blockedContributions
      request.get('/communityAdmin/blockedContributions')
        .then((res) => {
          if (res.data && res.data.code === 0) {
            this.blockedWorks = res.data.data || [];
            console.log('📋 已封禁作品数据:', this.blockedWorks);
          } else {
            console.error('加载已封禁作品失败:', res.data?.message);
          }
        })
        .catch((error) => {
          console.error('加载已封禁作品失败:', error);
          this.blockedWorks = [];
        });
    },
    getImageUrl(imagePath) {
      if (!imagePath) return this.placeholderImg;
      const baseURL = process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080';
      return `${baseURL}${imagePath}`;
    },
    onImageError(e) {
      e.target.src = this.placeholderImg;
    },
    viewDetail(work) {
      // 跳转到作品详情页，添加 blocked=true 查询参数
      this.$router.push({
        path: `/image/${work.contributionId}`,
        query: { blocked: 'true' }
      });
    }
  }
};
</script>

<style scoped>
.anime-blocked-container {
  background: rgba(255, 255, 255, 0.98);
  border: 3px solid rgba(239, 68, 68, 0.2);
  border-radius: 24px;
  padding: 32px;
  box-shadow: 0 12px 48px rgba(239, 68, 68, 0.15);
}

.blocked-header {
  text-align: center;
  margin-bottom: 32px;
}

.title {
  font-size: 2rem;
  font-weight: 900;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.anime-gradient-text {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  color: #6b7280;
  font-weight: 600;
}

.works-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 60px 20px;
  color: #9ca3af;
}

.empty-state .icon {
  font-size: 64px;
  display: block;
  margin-bottom: 16px;
}

.empty-state p {
  font-size: 18px;
  font-weight: 600;
}

.work-card {
  background: white;
  border: 2px solid rgba(239, 68, 68, 0.1);
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
}

.work-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 32px rgba(239, 68, 68, 0.2);
  border-color: #f87171;
}

.work-image {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: #f3f4f6;
}

.work-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.work-card:hover .work-image img {
  transform: scale(1.1);
}

.work-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.work-card:hover .work-overlay {
  opacity: 1;
}

.view-btn {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
  padding: 12px 24px;
  border-radius: 12px;
  font-weight: 700;
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
}

.work-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 700;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.blocked-badge {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
}

.work-info {
  padding: 16px;
}

.work-title {
  font-size: 16px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.work-meta {
  margin-bottom: 8px;
}

.author {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: #6b7280;
  font-size: 14px;
  font-weight: 600;
}

.work-stats {
  display: flex;
  gap: 12px;
  font-size: 13px;
  color: #9ca3af;
  font-weight: 600;
}

.work-stats span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

@media (max-width: 768px) {
  .anime-blocked-container {
    padding: 20px;
  }
  
  .works-grid {
    grid-template-columns: 1fr;
  }
  
  .title {
    font-size: 1.5rem;
  }
}
</style>


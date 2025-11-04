<template>
  <div class="anime-audit-container">
    <div class="audit-header">
      <h2 class="title anime-gradient-text">
        <span class="icon">🛡️</span> 作品审核管理
      </h2>
      <p class="subtitle">社区管理员审核平台</p>
    </div>

    <!-- 标签页切换 -->
    <div class="tabs is-centered is-boxed anime-tabs">
      <ul>
        <li :class="{ 'is-active': activeTab === 'pending' }" @click="activeTab = 'pending'">
          <a>
            <span class="icon">⏳</span>
            <span>待审核 ({{ pendingCount }})</span>
          </a>
        </li>
        <li :class="{ 'is-active': activeTab === 'approved' }" @click="activeTab = 'approved'">
          <a>
            <span class="icon">✅</span>
            <span>已通过 ({{ approvedCount }})</span>
          </a>
        </li>
        <li :class="{ 'is-active': activeTab === 'dismissed' }" @click="activeTab = 'dismissed'">
          <a>
            <span class="icon">❌</span>
            <span>已驳回 ({{ dismissedCount }})</span>
          </a>
        </li>
      </ul>
    </div>

    <!-- 待审核列表 -->
    <div v-if="activeTab === 'pending'" class="works-grid">
      <div v-if="pendingWorks.length === 0" class="empty-state">
        <span class="icon">📭</span>
        <p>暂无待审核作品</p>
      </div>
      <div v-for="work in pendingWorks" :key="work.contributionId" class="work-card anime-card" @click="viewDetail(work)">
        <div class="work-image">
          <img :src="getImageUrl(work.image[0])" :alt="work.title" @error="onImageError" />
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

    <!-- 已通过列表 -->
    <div v-if="activeTab === 'approved'" class="works-grid">
      <div v-if="approvedWorks.length === 0" class="empty-state">
        <span class="icon">📭</span>
        <p>暂无已通过作品</p>
      </div>
      <div v-for="work in approvedWorks" :key="work.contributionId" class="work-card anime-card" @click="viewDetail(work)">
        <div class="work-image">
          <img :src="getImageUrl(work.image[0])" :alt="work.title" @error="onImageError" />
          <div class="work-badge approved-badge">✅ 已通过</div>
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

    <!-- 已驳回列表 -->
    <div v-if="activeTab === 'dismissed'" class="works-grid">
      <div v-if="dismissedWorks.length === 0" class="empty-state">
        <span class="icon">📭</span>
        <p>暂无已驳回作品</p>
      </div>
      <div v-for="work in dismissedWorks" :key="work.contributionId" class="work-card anime-card dismissed-work-card">
        <div class="work-image">
          <img :src="getImageUrl(work.image[0])" :alt="work.title" @error="onImageError" />
          <div class="work-badge dismissed-badge">❌ 已驳回</div>
        </div>
        <div class="work-info">
          <h4 class="work-title">{{ work.title }}</h4>
          <div class="work-meta">
            <span class="author">
              <span class="icon">👤</span> {{ work.authorName }}
            </span>
          </div>
          <div v-if="work.dismissalReason" class="dismissal-reason">
            <span class="icon">📝</span>
            <span>驳回理由: {{ work.dismissalReason }}</span>
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
import placeholderImg from '@/assets/images/avatar.png';

export default {
  name: 'AuditWorksList',
  props: {
    auditData: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      activeTab: 'pending',
      placeholderImg
    };
  },
  computed: {
    pendingWorks() {
      return this.auditData?.pendingContributions || [];
    },
    approvedWorks() {
      return this.auditData?.approvedContributions || [];
    },
    dismissedWorks() {
      return this.auditData?.dismissalContributions || [];
    },
    pendingCount() {
      return this.pendingWorks.length;
    },
    approvedCount() {
      return this.approvedWorks.length;
    },
    dismissedCount() {
      return this.dismissedWorks.length;
    }
  },
  methods: {
    getImageUrl(imagePath) {
      if (!imagePath) return this.placeholderImg;
      const baseURL = process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080';
      return `${baseURL}${imagePath}`;
    },
    onImageError(e) {
      e.target.src = this.placeholderImg;
    },
    viewDetail(work) {
      // 根据当前标签页判断作品状态
      // 待审核和已驳回的作品需要使用 pending=true 调用 /api/pendingContribution 接口
      // 已通过的作品使用普通 /api/contribution 接口
      
      const query = {};
      
      if (this.activeTab === 'pending') {
        // 待审核作品：传递 pending=true
        query.pending = 'true';
      } else if (this.activeTab === 'dismissed') {
        // 已驳回作品：传递 pending=true（查看驳回详情）
        query.pending = 'true';
      }
      // 已通过的作品（activeTab === 'approved'）不需要特殊参数
      
      this.$router.push({
        path: `/image/${work.contributionId}`,
        query: query
      });
    }
  }
};
</script>

<style scoped>
.anime-audit-container {
  background: rgba(255, 255, 255, 0.98);
  border: 3px solid rgba(255, 105, 180, 0.2);
  border-radius: 24px;
  padding: 32px;
  box-shadow: 0 12px 48px rgba(147, 51, 234, 0.15);
}

.audit-header {
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  color: #6b7280;
  font-weight: 600;
}

.anime-tabs {
  margin-bottom: 32px;
}

.anime-tabs ul {
  border-bottom: 3px solid rgba(147, 51, 234, 0.1);
}

.anime-tabs li {
  transition: all 0.3s ease;
}

.anime-tabs li a {
  border: 2px solid transparent;
  border-radius: 12px 12px 0 0;
  font-weight: 600;
  padding: 12px 24px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #6b7280;
}

.anime-tabs li:hover a {
  color: #6366f1;
  background: rgba(147, 51, 234, 0.05);
}

.anime-tabs li.is-active a {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: transparent;
  box-shadow: 0 4px 12px rgba(147, 51, 234, 0.3);
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
  border: 2px solid rgba(147, 51, 234, 0.1);
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
}

.work-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 32px rgba(147, 51, 234, 0.2);
  border-color: #a78bfa;
}

/* 已驳回作品禁用样式 */
.dismissed-work-card {
  opacity: 0.6;
  cursor: not-allowed !important;
  pointer-events: none;
}

.dismissed-work-card:hover {
  transform: none;
  box-shadow: none;
  border-color: rgba(147, 51, 234, 0.1);
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 12px 24px;
  border-radius: 12px;
  font-weight: 700;
  box-shadow: 0 4px 12px rgba(147, 51, 234, 0.3);
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

.approved-badge {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
}

.dismissed-badge {
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

.dismissal-reason {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  margin-bottom: 8px;
  padding: 8px;
  background: #fef2f2;
  border-radius: 8px;
  border-left: 3px solid #ef4444;
  font-size: 13px;
  color: #dc2626;
  line-height: 1.4;
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
  .anime-audit-container {
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


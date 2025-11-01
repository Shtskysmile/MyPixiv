<template>
  <div class="anime-list-container">
    <div class="list-header">
      <h3 class="list-title anime-gradient-text">
        <span class="icon">🎨</span> 我的作品
      </h3>
      <p class="list-subtitle">共 {{ total }} 个作品</p>
    </div>

    <div class="works-grid" v-if="works.length > 0">
      <div class="work-card" v-for="work in works" :key="work.contributionId || work.id">
        <router-link :to="`/image/${work.contributionId || work.id}`" class="card-link">
          <div class="card-image">
            <img :src="work.image || work.url" :alt="work.title" @error="onImageError" />
            <div class="image-overlay">
              <div class="overlay-stats">
                <span class="stat-item">
                  <i>👁️</i> {{ formatCount(work.viewCount) }}
                </span>
                <span class="stat-item">
                  <i>❤️</i> {{ formatCount(work.likeCount) }}
                </span>
                <span class="stat-item">
                  <i>⭐</i> {{ formatCount(work.favoriteCount) }}
                </span>
              </div>
            </div>
            <!-- 审核状态标签 -->
            <div class="audit-badge" v-if="work.auditStatus !== undefined">
              <span 
                class="badge"
                :class="{
                  'badge-pending': work.auditStatus === 0,
                  'badge-passed': work.auditStatus === 1,
                  'badge-rejected': work.auditStatus === 2
                }"
              >
                {{ getAuditStatusText(work.auditStatus) }}
              </span>
            </div>
          </div>
        </router-link>

        <div class="card-content">
          <router-link :to="`/image/${work.contributionId || work.id}`" class="card-title">
            {{ work.title || '无标题' }}
          </router-link>

          <div class="card-stats">
            <span class="stat"><i>👁️</i> {{ formatCount(work.viewCount) }}</span>
            <span class="stat"><i>❤️</i> {{ formatCount(work.likeCount) }}</span>
            <span class="stat"><i>⭐</i> {{ formatCount(work.favoriteCount) }}</span>
            <span class="stat"><i>💬</i> {{ formatCount(work.commentCount) }}</span>
          </div>

          <!-- 驳回理由 -->
          <div class="rejection-reason" v-if="work.dismissalReason">
            <span class="icon">⚠️</span>
            <span>{{ work.dismissalReason }}</span>
          </div>

          <div class="card-actions">
            <button class="anime-button is-small is-info" @click="handleEdit(work)">
              <span class="icon">✏️</span>
              <span>编辑</span>
            </button>
            <button class="anime-button is-small is-danger" @click="handleDelete(work)">
              <span class="icon">🗑️</span>
              <span>删除</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="empty-state" v-else>
      <div class="empty-icon">🎨</div>
      <p class="empty-text">还没有上传作品</p>
      <p class="empty-hint">快去创作并上传你的第一个作品吧！</p>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="totalPages > 1">
      <button 
        class="page-btn prev-btn" 
        :disabled="page <= 1"
        @click="$emit('page-change', page - 1)"
      >
        <span class="icon">◀️</span> 上一页
      </button>
      <span class="page-info">第 {{ page }} / {{ totalPages }} 页</span>
      <button 
        class="page-btn next-btn" 
        :disabled="page >= totalPages"
        @click="$emit('page-change', page + 1)"
      >
        下一页 <span class="icon">▶️</span>
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'UserWorksList',
  props: {
    works: {
      type: Array,
      default: () => []
    },
    page: {
      type: Number,
      default: 1
    },
    pageSize: {
      type: Number,
      default: 12
    },
    total: {
      type: Number,
      default: 0
    }
  },
  computed: {
    totalPages() {
      return Math.max(1, Math.ceil(this.total / this.pageSize));
    }
  },
  methods: {
    formatCount(count) {
      if (!count && count !== 0) return 0;
      if (count >= 10000) return (count / 10000).toFixed(1) + 'w';
      else if (count >= 1000) return (count / 1000).toFixed(1) + 'k';
      return count;
    },
    formatAuthorId(authorId) {
      if (!authorId) return '匿名';
      return authorId.length > 8 ? authorId.substring(0, 8) + '...' : authorId;
    },
    getAuditStatusText(status) {
      const statusMap = {
        0: '⏳ 待审核',
        1: '✅ 已通过',
        2: '❌ 已驳回'
      };
      return statusMap[status] || '未知';
    },
    handleEdit(work) {
      // TODO: 实现编辑功能
      alert('编辑功能开发中...');
    },
    handleDelete(work) {
      const title = work.title || '此作品';
      const confirmed = confirm(`确定删除作品《${title}》吗？此操作不可恢复！`);
      if (confirmed) {
        this.$emit('delete', work);
      }
    },
    onImageError(e) {
      e.target.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="400" height="300"%3E%3Crect fill="%23ddd" width="400" height="300"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999" font-size="20"%3E图片加载失败%3C/text%3E%3C/svg%3E';
    },
    onAvatarError(e) {
      e.target.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="40" height="40"%3E%3Crect fill="%23ddd" width="40" height="40"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999"%3EU%3C/text%3E%3C/svg%3E';
    }
  }
};
</script>

<style scoped>
.anime-list-container {
  background: rgba(255, 255, 255, 0.98);
  border: 2px solid rgba(255, 105, 180, 0.2);
  border-radius: 20px;
  padding: 28px;
  box-shadow: 0 8px 32px rgba(147, 51, 234, 0.12);
}

.list-header {
  margin-bottom: 24px;
  text-align: center;
}

.list-title {
  font-size: 1.75rem;
  font-weight: 900;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 8px;
}

.anime-gradient-text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.list-subtitle {
  color: #6b7280;
  font-weight: 600;
}

.works-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.work-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  border: 2px solid rgba(147, 51, 234, 0.1);
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(147, 51, 234, 0.08);
}

.work-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(147, 51, 234, 0.2);
  border-color: #a78bfa;
}

.card-link {
  display: block;
  text-decoration: none;
}

.card-image {
  position: relative;
  height: 200px;
  overflow: hidden;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.work-card:hover .card-image img {
  transform: scale(1.1);
}

.image-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.7) 0%, transparent 100%);
  padding: 12px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.work-card:hover .image-overlay {
  opacity: 1;
}

.overlay-stats {
  display: flex;
  gap: 12px;
  color: white;
  font-size: 12px;
  font-weight: 600;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.audit-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  z-index: 10;
}

.badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
  border: 2px solid;
}

.badge-pending {
  background: rgba(251, 191, 36, 0.95);
  color: white;
  border-color: #fbbf24;
}

.badge-passed {
  background: rgba(16, 185, 129, 0.95);
  color: white;
  border-color: #10b981;
}

.badge-rejected {
  background: rgba(239, 68, 68, 0.95);
  color: white;
  border-color: #ef4444;
}

.card-content {
  padding: 16px;
}

.card-title {
  display: block;
  color: #6366f1;
  font-weight: 700;
  font-size: 15px;
  margin-bottom: 12px;
  text-decoration: none;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color 0.2s ease;
}

.card-title:hover {
  color: #a855f7;
  text-decoration: underline;
}

.card-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
  margin-bottom: 12px;
  padding: 12px;
  background: rgba(147, 51, 234, 0.03);
  border-radius: 8px;
}

.stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #6b7280;
  font-weight: 600;
}

.rejection-reason {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px;
  background: #fef2f2;
  border-left: 4px solid #ef4444;
  border-radius: 8px;
  margin-bottom: 12px;
  font-size: 13px;
  color: #991b1b;
}

.card-actions {
  display: flex;
  gap: 8px;
}

.anime-button {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px 16px;
  border-radius: 10px;
  font-weight: 700;
  font-size: 14px;
  border: 2px solid transparent;
  transition: all 0.3s ease;
  cursor: pointer;
}

.anime-button.is-info {
  background: linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%);
  color: white;
}

.anime-button.is-info:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.anime-button.is-danger {
  background: linear-gradient(135deg, #f87171 0%, #ef4444 100%);
  color: white;
}

.anime-button.is-danger:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-text {
  font-size: 18px;
  font-weight: 700;
  color: #6b7280;
  margin-bottom: 8px;
}

.empty-hint {
  font-size: 14px;
  color: #9ca3af;
}

.pagination-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 2px solid rgba(147, 51, 234, 0.1);
}

.page-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  border-radius: 12px;
  font-weight: 700;
  background: white;
  border: 2px solid rgba(147, 51, 234, 0.2);
  color: #6366f1;
  cursor: pointer;
  transition: all 0.3s ease;
}

.page-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: transparent;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(147, 51, 234, 0.3);
}

.page-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.page-info {
  font-weight: 700;
  color: #6366f1;
  padding: 0 12px;
}

@media (max-width: 768px) {
  .works-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 16px;
  }
  
  .card-image {
    height: 150px;
  }
  
  .card-stats {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .pagination-wrapper {
    flex-wrap: wrap;
  }
}
</style>


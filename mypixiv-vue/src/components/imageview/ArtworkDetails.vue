<template>
  <div class="artwork-details anime-box">
    <!-- 作品标题和ID -->
    <div class="title-id-section">
      <h2 class="title is-4 anime-title-small">
        <span class="icon">✨</span>
        {{ contribution.title || '无标题' }}
      </h2>
      <div 
        class="work-id clickable-work-id" 
        @click="$emit('copy-id', contribution.contributionId, '作品ID')"
        title="点击复制作品ID"
      >
        <span class="icon">🆔</span>
        <span class="id-text"><strong>作品ID:</strong> {{ contribution.contributionId }}</span>
        <span class="copy-icon-work">📋</span>
      </div>
    </div>
    
    <!-- 作者信息卡片 -->
    <div class="author-card">
      <div class="author-info" @click="$emit('go-to-author')" style="cursor: pointer;">
        <figure class="author-avatar-wrapper">
          <img 
            :src="avatarUrl" 
            alt="author" 
            class="author-avatar anime-avatar"
            @error="onAvatarError"
          />
        </figure>
        <div class="author-details">
          <p class="author-name" v-if="contribution.authorName">
            <strong>{{ contribution.authorName }}</strong>
            <span class="author-badge">创作者</span>
          </p>
          <p 
            class="author-id clickable-author-id" 
            @click.stop="$emit('copy-id', contribution.authorId, '作者ID')"
            title="点击复制作者ID"
          >
            <span class="icon">🆔</span>
            <strong>ID:</strong> {{ formatAuthorId(contribution.authorId) }}
            <span class="copy-icon-author">📋</span>
          </p>
          <p class="publish-time">
            <span class="icon">📅</span>
            {{ formatTime(contribution.publishTime) }}
          </p>
        </div>
      </div>
      
      <!-- 点赞收藏按钮 -->
      <div class="interaction-section">
        <slot name="interaction-buttons"></slot>
      </div>
    </div>

    <!-- 统计信息栏 -->
    <div class="stats-bar">
      <div class="stat-item">
        <span class="stat-icon">👁️</span>
        <div class="stat-content">
          <span class="stat-label">浏览</span>
          <span class="stat-value">{{ formatCount(contribution.viewCount) }}</span>
        </div>
      </div>
      <div class="stat-item">
        <span class="stat-icon">❤️</span>
        <div class="stat-content">
          <span class="stat-label">点赞</span>
          <span class="stat-value">{{ formatCount(contribution.likeCount) }}</span>
        </div>
      </div>
      <div class="stat-item">
        <span class="stat-icon">⭐</span>
        <div class="stat-content">
          <span class="stat-label">收藏</span>
          <span class="stat-value">{{ formatCount(contribution.favoriteCount) }}</span>
        </div>
      </div>
      <div class="stat-item">
        <span class="stat-icon">💬</span>
        <div class="stat-content">
          <span class="stat-label">评论</span>
          <span class="stat-value">{{ formatCount(contribution.commentCount) }}</span>
        </div>
      </div>
    </div>

    <!-- 作品描述 -->
    <div class="description-box" v-if="contribution.description">
      <h4 class="description-title">
        <span class="icon">📖</span> 作品描述
      </h4>
      <p class="description-text">{{ contribution.description }}</p>
    </div>

    <!-- 作品类型和状态标签 -->
    <div class="tags-section">
      <span class="tag type-tag">
        <span class="icon">{{ contribution.type === 0 ? '🖼️' : '📚' }}</span>
        {{ contribution.type === 0 ? '插画' : '漫画' }}
      </span>
      <span 
        class="tag status-tag" 
        :class="{
          'is-success': contribution.auditStatus === 1,
          'is-warning': contribution.auditStatus === 0,
          'is-danger': contribution.auditStatus === 2
        }"
      >
        <span class="icon">{{ getAuditStatusIcon(contribution.auditStatus) }}</span>
        {{ getAuditStatus(contribution.auditStatus) }}
      </span>
      <span v-if="contribution.dismissalReason" class="tag is-danger is-light dismissal-tag">
        <span class="icon">⚠️</span>
        驳回原因: {{ contribution.dismissalReason }}
      </span>
    </div>

    <!-- 作品标签 -->
    <div v-if="contribution.tags && contribution.tags.length > 0" class="artwork-tags-section">
      <h4 class="tags-title">
        <span class="icon">🏷️</span> 标签
      </h4>
      <div class="artwork-tags">
        <span 
          v-for="tag in contribution.tags" 
          :key="tag.id"
          class="artwork-tag"
        >
          <span class="tag-icon">#</span>
          {{ tag.tagName }}
        </span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ArtworkDetails',
  props: {
    contribution: {
      type: Object,
      required: true
    },
    avatarUrl: {
      type: String,
      required: true
    }
  },
  methods: {
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
    
    formatTime(time) {
      if (!time) return '';
      const date = new Date(time);
      if (isNaN(date.getTime())) return time;
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    },
    
    getAuditStatus(status) {
      const statusMap = {
        0: '待审核',
        1: '已通过',
        2: '已驳回'
      };
      return statusMap[status] || '未知';
    },
    
    getAuditStatusIcon(status) {
      const iconMap = {
        0: '⏳',
        1: '✅',
        2: '❌'
      };
      return iconMap[status] || '❓';
    },
    
    onAvatarError(e) {
      e.target.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="64" height="64"%3E%3Crect fill="%23ddd" width="64" height="64"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999" font-size="24"%3EU%3C/text%3E%3C/svg%3E';
    }
  }
};
</script>

<style scoped>
.artwork-details {
  padding: 28px;
  background: rgba(255, 255, 255, 0.98) !important;
  border: 2px solid rgba(147, 51, 234, 0.1);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(147, 51, 234, 0.12);
}

.title-id-section {
  margin-bottom: 24px;
}

.anime-title-small {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-weight: 900;
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  font-size: 1.8rem;
}

.work-id {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #6b7280;
  font-size: 14px;
  margin-top: 8px;
}

.clickable-work-id {
  cursor: pointer;
  padding: 8px 14px;
  border-radius: 10px;
  background: rgba(102, 126, 234, 0.08);
  transition: all 0.3s ease;
  user-select: none;
  display: inline-flex;
  width: fit-content;
}

.clickable-work-id:hover {
  background: rgba(102, 126, 234, 0.18);
  color: #667eea;
  transform: translateX(3px);
}

.clickable-work-id:active {
  transform: scale(0.98);
}

.id-text {
  font-family: 'Courier New', monospace;
  font-weight: 600;
  font-size: 13px;
}

.copy-icon-work {
  opacity: 0;
  transition: opacity 0.3s ease;
  font-size: 12px;
  margin-left: auto;
}

.clickable-work-id:hover .copy-icon-work {
  opacity: 1;
}

/* 作者信息卡片 */
.author-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  padding: 20px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  border-radius: 16px;
  margin-bottom: 24px;
  border: 2px solid rgba(102, 126, 234, 0.1);
  transition: all 0.3s ease;
}

.author-card:hover {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.08) 0%, rgba(118, 75, 162, 0.08) 100%);
  border-color: rgba(102, 126, 234, 0.2);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.15);
}

.author-info {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
}

.author-avatar-wrapper {
  position: relative;
}

.author-avatar-wrapper::after {
  content: '';
  position: absolute;
  inset: -4px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  z-index: -1;
  animation: pulse 2s ease infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 0.6; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.05); }
}

.anime-avatar {
  width: 80px !important;
  height: 80px !important;
  border: 4px solid white !important;
  box-shadow: 0 6px 16px rgba(167, 139, 250, 0.4);
  border-radius: 50% !important;
  object-fit: cover;
  display: block;
  transition: transform 0.3s ease;
}

.anime-avatar:hover {
  transform: scale(1.1);
}

.author-details {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.author-name {
  color: #6366f1;
  font-weight: 700;
  font-size: 1.1rem;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.author-badge {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
}

.author-id {
  color: #6b7280;
  font-weight: 500;
  font-size: 0.9em;
  font-family: 'Courier New', monospace;
}

.clickable-author-id {
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 10px;
  background: rgba(102, 126, 234, 0.08);
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  user-select: none;
  width: fit-content;
}

.clickable-author-id:hover {
  background: rgba(102, 126, 234, 0.18);
  color: #667eea;
  transform: translateX(3px);
}

.copy-icon-author {
  opacity: 0;
  transition: opacity 0.3s ease;
  font-size: 12px;
}

.clickable-author-id:hover .copy-icon-author {
  opacity: 1;
}

.publish-time {
  color: #9ca3af;
  font-size: 0.85rem;
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
}

.interaction-section {
  flex-shrink: 0;
}

/* 统计栏 */
.stats-bar {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  padding: 24px;
  margin-bottom: 24px;
  background: linear-gradient(135deg, #667eea08 0%, #764ba208 100%);
  border-radius: 16px;
  border: 2px solid rgba(147, 51, 234, 0.1);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.15);
}

.stat-icon {
  font-size: 32px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.stat-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-label {
  font-size: 12px;
  color: #6b7280;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.stat-value {
  font-size: 20px;
  font-weight: 900;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 描述框 */
.description-box {
  margin-bottom: 24px;
  padding: 20px;
  background: linear-gradient(135deg, rgba(147, 51, 234, 0.03) 0%, rgba(59, 130, 246, 0.03) 100%);
  border-radius: 16px;
  border-left: 5px solid #9333ea;
  box-shadow: 0 2px 8px rgba(147, 51, 234, 0.08);
}

.description-title {
  font-weight: 800;
  color: #6366f1;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 1.1rem;
}

.description-text {
  color: #374151;
  line-height: 1.8;
  font-size: 0.95rem;
}

/* 标签区 */
.tags-section {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 24px;
}

.tag {
  border-radius: 12px;
  padding: 8px 16px;
  font-weight: 700;
  font-size: 0.9rem;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
  transition: all 0.2s ease;
}

.tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.12);
}

.type-tag {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
}

.status-tag {
  border: 2px solid currentColor;
}

.dismissal-tag {
  max-width: 100%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 作品标签区 */
.artwork-tags-section {
  padding: 20px;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  border-radius: 16px;
  border-left: 5px solid #3b82f6;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.12);
}

.tags-title {
  font-weight: 800;
  color: #1e40af;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 1.1rem;
}

.artwork-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.artwork-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 18px;
  background: linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%);
  color: white;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 700;
  box-shadow: 0 3px 10px rgba(59, 130, 246, 0.3);
  transition: all 0.3s ease;
  cursor: default;
}

.artwork-tag:hover {
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 6px 16px rgba(59, 130, 246, 0.4);
}

.tag-icon {
  font-weight: 900;
  font-size: 1.1em;
}

@media (max-width: 768px) {
  .author-card {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .interaction-section {
    width: 100%;
  }
  
  .stats-bar {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
    padding: 16px;
  }
  
  .stat-item {
    padding: 12px;
  }
  
  .stat-icon {
    font-size: 24px;
  }
  
  .stat-value {
    font-size: 16px;
  }
}
</style>


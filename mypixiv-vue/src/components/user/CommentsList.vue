<template>
  <div class="anime-list-container">
    <div class="list-header">
      <h3 class="list-title anime-gradient-text">
        <span class="icon">💬</span> {{ isOwnProfile ? '我的评论' : 'TA的评论' }}
      </h3>
      <p class="list-subtitle">共 {{ comments.length }} 条评论</p>
    </div>

    <div class="comments-grid" v-if="comments.length > 0">
      <div class="comment-card" v-for="comment in comments" :key="comment.comment.commentId">
        <router-link :to="`/image/${comment.contribution.contributionId}`" class="card-link">
          <div class="card-image">
            <img 
              :src="getContributionImageUrl(comment.contribution)" 
              :alt="comment.contribution.title"
              @error="onImageError" 
            />
            <div class="image-overlay">
              <div class="overlay-stats">
                <span class="stat-item">
                  <i>👁️</i> {{ formatCount(comment.contribution.viewCount) }}
                </span>
                <span class="stat-item">
                  <i>❤️</i> {{ formatCount(comment.contribution.likeCount) }}
                </span>
              </div>
            </div>
          </div>
        </router-link>

        <div class="card-content">
          <router-link :to="`/image/${comment.contribution.contributionId}`" class="card-title">
            {{ comment.contribution.title || '无标题' }}
          </router-link>
          
          <!-- 作品作者信息 -->
          <div class="card-author">
            <img 
              :src="getAvatarUrl(comment.contribution)" 
              class="author-avatar"
              :alt="comment.contribution.authorName || 'User'"
              @error="onAvatarError"
            />
            <div class="author-info">
              <div class="author-name">{{ comment.contribution.authorName || '匿名用户' }}</div>
              <div class="author-id">ID: {{ formatAuthorId(comment.contribution.authorId) }}</div>
            </div>
          </div>

          <!-- 评论者信息 -->
          <div class="commenter-section">
            <div class="commenter-header">
              <img 
                :src="getCommenterAvatarUrl(comment.comment)" 
                class="commenter-avatar"
                :alt="comment.comment.authorName || 'User'"
                @error="onAvatarError"
              />
              <div class="commenter-info">
                <div class="commenter-name">{{ comment.comment.authorName || '匿名用户' }}</div>
                <div class="comment-time">
                  <i>🕐</i> {{ formatTime(comment.comment.time) }}
                </div>
              </div>
            </div>
            
            <div class="comment-text">
              <div class="comment-description">{{ comment.comment.description }}</div>
            </div>
          </div>

          <div class="card-footer">
            <div class="card-stats">
              <span class="stat"><i>❤️</i> {{ formatCount(comment.contribution.likeCount) }}</span>
              <span class="stat"><i>⭐</i> {{ formatCount(comment.contribution.favoriteCount) }}</span>
              <span class="stat"><i>💬</i> {{ formatCount(comment.contribution.commentCount) }}</span>
            </div>

            <button 
              v-if="isOwnProfile" 
              class="delete-button-mini" 
              @click="handleDelete(comment)"
              title="删除评论"
            >
              <span class="icon">🗑️</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="empty-state" v-else>
      <div class="empty-icon">💬</div>
      <p class="empty-text">还没有发表评论</p>
      <p class="empty-hint">快去浏览作品并发表你的看法吧！</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CommentsList',
  props: {
    comments: {
      type: Array,
      default: () => []
    },
    isOwnProfile: {
      type: Boolean,
      default: false
    }
  },
  methods: {
    formatCount(count) {
      if (!count && count !== 0) return 0;
      if (count >= 10000) {
        return (count / 10000).toFixed(1) + 'w';
      } else if (count >= 1000) {
        return (count / 1000).toFixed(1) + 'k';
      }
      return count;
    },
    formatAuthorId(authorId) {
      if (!authorId) return '匿名';
      return authorId.length > 8 ? authorId.substring(0, 8) + '...' : authorId;
    },
    formatCommenterId(commenterId) {
      if (!commenterId) return '匿名';
      return commenterId.length > 8 ? commenterId.substring(0, 8) + '...' : commenterId;
    },
    getCommenterAvatarUrl(comment) {
      // 处理评论者头像路径
      let avatarPath = comment.avatar;
      
      if (!avatarPath) {
        return 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="40" height="40"%3E%3Crect fill="%23ddd" width="40" height="40"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999"%3EU%3C/text%3E%3C/svg%3E';
      }
      
      // 如果是完整URL，直接返回
      if (avatarPath.startsWith('http://') || avatarPath.startsWith('https://')) {
        return avatarPath;
      }
      
      // 拼接基础 URL
      const baseURL = process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080';
      const fullPath = avatarPath.startsWith('/') ? avatarPath : `/${avatarPath}`;
      return `${baseURL}${fullPath}`;
    },
    getAvatarUrl(contribution) {
      // 处理作品作者头像路径（保留此方法以防其他地方使用）
      let avatarPath = contribution.avatar;
      
      if (!avatarPath) {
        return 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="40" height="40"%3E%3Crect fill="%23ddd" width="40" height="40"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999"%3EU%3C/text%3E%3C/svg%3E';
      }
      
      // 如果是完整URL，直接返回
      if (avatarPath.startsWith('http://') || avatarPath.startsWith('https://')) {
        return avatarPath;
      }
      
      // 拼接基础 URL
      const baseURL = process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080';
      const fullPath = avatarPath.startsWith('/') ? avatarPath : `/${avatarPath}`;
      return `${baseURL}${fullPath}`;
    },
    onAvatarError(e) {
      e.target.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="40" height="40"%3E%3Crect fill="%23ddd" width="40" height="40"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999"%3EU%3C/text%3E%3C/svg%3E';
    },
    getContributionImageUrl(contribution) {
      // 参考 LikesList.vue 的图片处理逻辑
      console.group(`🖼️ [评论作品图片URL调试] ${contribution.title || contribution.contributionId}`);
      console.log('📦 完整作品对象:', contribution);
      console.log('🔍 image字段原始值:', contribution.image);
      console.log('🔍 image字段类型:', typeof contribution.image);
      console.log('🔍 是否为数组:', Array.isArray(contribution.image));
      if (Array.isArray(contribution.image)) {
        console.log('🔍 数组长度:', contribution.image.length);
        console.log('🔍 数组内容:', contribution.image);
      }
      
      let imagePath = '';
      
      if (Array.isArray(contribution.image) && contribution.image.length > 0) {
        // 如果是数组，取第一张图
        imagePath = contribution.image[0];
        console.log('✅ 从数组获取图片路径:', imagePath);
      } else if (typeof contribution.image === 'string' && contribution.image) {
        // 如果是字符串（兼容旧数据）
        imagePath = contribution.image;
        console.log('✅ 从字符串获取图片路径:', imagePath);
      }
      
      if (!imagePath) {
        // 返回占位图
        console.warn('❌ 没有找到图片路径，显示占位图');
        console.groupEnd();
        return 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="400" height="300"%3E%3Crect fill="%23ddd" width="400" height="300"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999" font-size="20"%3E暂无图片%3C/text%3E%3C/svg%3E';
      }
      
      // 如果是完整URL，直接返回
      if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
        console.log('✅ 完整URL，直接返回:', imagePath);
        console.groupEnd();
        return imagePath;
      }
      
      // 拼接基础 URL
      const baseURL = process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080';
      const fullPath = imagePath.startsWith('/') ? imagePath : `/${imagePath}`;
      const finalUrl = `${baseURL}${fullPath}`;
      
      console.log('🌐 环境信息:', {
        NODE_ENV: process.env.NODE_ENV,
        VUE_APP_API_BASE_URL: process.env.VUE_APP_API_BASE_URL,
        baseURL: baseURL
      });
      console.log('🔗 最终图片URL:', finalUrl);
      console.groupEnd();
      
      return finalUrl;
    },
    onImageError(e) {
      const failedUrl = e.target.src;
      console.error('❌ 图片加载失败:', failedUrl);
      e.target.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="400" height="300"%3E%3Crect fill="%23ddd" width="400" height="300"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999" font-size="20"%3E图片加载失败%3C/text%3E%3C/svg%3E';
    },
    formatTime(timestamp) {
      if (!timestamp) return '未知时间';
      const date = new Date(timestamp);
      const now = new Date();
      const diff = now - date;
      
      // 一分钟内
      if (diff < 60 * 1000) {
        return '刚刚';
      }
      // 一小时内
      if (diff < 60 * 60 * 1000) {
        return `${Math.floor(diff / (60 * 1000))}分钟前`;
      }
      // 一天内
      if (diff < 24 * 60 * 60 * 1000) {
        return `${Math.floor(diff / (60 * 60 * 1000))}小时前`;
      }
      // 一周内
      if (diff < 7 * 24 * 60 * 60 * 1000) {
        return `${Math.floor(diff / (24 * 60 * 60 * 1000))}天前`;
      }
      
      // 超过一周，显示具体日期
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    },
    handleDelete(comment) {
      const title = comment.contribution.title || '此作品';
      const confirmed = confirm(`确定删除这条评论吗？\n\n作品：《${title}》\n评论内容：${comment.comment.description}`);
      if (confirmed) {
        this.$emit('delete', comment);
      }
    }
  }
};
</script>

<style scoped>
/* 参考 LikesList.vue 的样式设计 */
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

.comments-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-width: 900px;
  margin: 0 auto;
}

.comment-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  border: 2px solid rgba(147, 51, 234, 0.1);
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(147, 51, 234, 0.08);
  display: flex;
  flex-direction: row;
  height: auto;
}

.comment-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(147, 51, 234, 0.2);
  border-color: #a78bfa;
}

.card-link {
  display: block;
  text-decoration: none;
  flex-shrink: 0;
}

.card-image {
  position: relative;
  width: 200px;
  min-height: 100px;
  max-height: 300px;
  overflow: hidden;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-image img {
  width: 100%;
  height: auto;
  object-fit: contain;
  transition: transform 0.4s ease;
  display: block;
}

.comment-card:hover .card-image img {
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

.comment-card:hover .image-overlay {
  opacity: 1;
}

.overlay-stats {
  display: flex;
  gap: 16px;
  color: white;
  font-size: 13px;
  font-weight: 600;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-content {
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-width: 0;
}

.card-title {
  display: block;
  color: #6366f1;
  font-weight: 700;
  font-size: 15px;
  text-decoration: none;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color 0.2s ease;
  margin: 0;
}

.card-title:hover {
  color: #a855f7;
  text-decoration: underline;
}

.card-author {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px;
  background: rgba(99, 102, 241, 0.05);
  border-radius: 8px;
  margin: 0;
}

.author-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e0e7ff;
  flex-shrink: 0;
}

.author-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  flex: 1;
  min-width: 0;
}

.author-name {
  font-size: 13px;
  color: #374151;
  font-weight: 700;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.author-id {
  font-size: 11px;
  color: #9ca3af;
  font-weight: 600;
}

/* 评论者区域 - 放大尺寸 */
.commenter-section {
  margin: 0;
  padding: 16px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(168, 139, 250, 0.05) 100%);
  border-radius: 10px;
  border-left: 4px solid #a78bfa;
}

.commenter-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.commenter-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #c4b5fd;
  flex-shrink: 0;
}

.commenter-info {
  display: flex;
  flex-direction: column;
  gap: 3px;
  flex: 1;
  min-width: 0;
}

.commenter-name {
  font-size: 14px;
  color: #6366f1;
  font-weight: 700;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.comment-time {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #9ca3af;
  font-weight: 600;
}

.comment-time i {
  font-style: normal;
}

.comment-text {
  margin: 0;
}

.comment-description {
  font-size: 15px;
  color: #1f2937;
  line-height: 1.7;
  word-wrap: break-word;
  white-space: pre-wrap;
  font-weight: 500;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 4px;
}

.card-stats {
  display: flex;
  gap: 16px;
  margin: 0;
  padding: 8px 0;
  flex: 1;
  border-top: 1px solid rgba(147, 51, 234, 0.1);
  border-bottom: 1px solid rgba(147, 51, 234, 0.1);
}

.stat {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #6b7280;
  font-weight: 600;
}

.stat i {
  font-style: normal;
}

/* 迷你删除按钮 - 放在左下角 */
.delete-button-mini {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  padding: 0;
  border: none;
  border-radius: 6px;
  background: linear-gradient(135deg, #f87171 0%, #ef4444 100%);
  color: white;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
  opacity: 0.7;
  flex-shrink: 0;
}

.delete-button-mini:hover {
  opacity: 1;
  transform: scale(1.1);
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.4);
}

.delete-button-mini:active {
  transform: scale(0.95);
}

.delete-button-mini .icon {
  font-style: normal;
  line-height: 1;
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

.icon {
  font-style: normal;
}

@media (max-width: 768px) {
  .comments-grid {
    gap: 12px;
  }
  
  .comment-card {
    flex-direction: column;
  }
  
  .card-image {
    width: 100%;
    height: 180px;
  }
  
  .card-content {
    padding: 12px;
    gap: 10px;
  }
  
  .comment-description {
    font-size: 12px;
  }
  
  .card-author {
    padding: 6px;
  }
  
  .author-avatar {
    width: 32px;
    height: 32px;
  }
}
</style>


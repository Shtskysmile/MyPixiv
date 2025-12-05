<template>
  <div class="anime-list-container">
    <div class="list-header">
      <h3 class="list-title anime-gradient-text">
        <span class="icon">❤️</span> {{ isOwnProfile ? '我点赞的作品' : 'TA点赞的作品' }}
      </h3>
      <p class="list-subtitle">共 {{ likes.length }} 个作品</p>
    </div>

    <div class="likes-grid" v-if="likes.length > 0">
      <div class="art-card anime-card" v-for="like in likes" :key="like.contributionId || like.id">
        <div class="art-thumb-link" @click="viewDetail(like)">
          <div class="art-thumb">
            <img :src="getLikeImageUrl(like)" :alt="like.title" @error="onImageError" />
            <div class="hover-overlay">
              <div class="stats-overlay">
                <span class="stat-item" v-if="like.viewCount !== undefined">
                  <i class="icon">👁️</i> {{ formatCount(like.viewCount) }}
                </span>
                <span class="stat-item" v-if="like.likeCount !== undefined">
                  <i class="icon">❤️</i> {{ formatCount(like.likeCount) }}
                </span>
                <span class="stat-item" v-if="like.favoriteCount !== undefined">
                  <i class="icon">⭐</i> {{ formatCount(like.favoriteCount) }}
                </span>
              </div>
            </div>
          </div>
        </div>
        <div class="art-meta">
          <div class="art-title" @click="viewDetail(like)">
            {{ like.title || '无标题' }}
          </div>
          <div class="art-author">
            <img
              v-if="getAvatarUrl(like)"
              class="author-avatar"
              :src="getAvatarUrl(like)"
              :alt="like.authorName || like.authorId || 'author'"
              @error="onAvatarError"
            />
            <div v-else class="author-avatar-placeholder">
              {{ like.authorName ? like.authorName.charAt(0).toUpperCase() : 'U' }}
            </div>
            <div class="author-info">
              <span class="author-name" v-if="like.authorName">{{ like.authorName }}</span>
              <span class="author-id">ID: {{ formatAuthorId(like.authorId) }}</span>
            </div>
          </div>
          <button v-if="isOwnProfile" class="anime-button is-small is-danger" @click.stop="handleUnlike(like)">
            <span class="icon">💔</span>
            <span>取消点赞</span>
          </button>
        </div>
      </div>
    </div>

    <div class="empty-state" v-else>
      <div class="empty-icon">💔</div>
      <p class="empty-text">还没有点赞任何作品</p>
      <p class="empty-hint">快去发现喜欢的作品吧！</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'UserLikesList',
  props: {
    likes: {
      type: Array,
      default: () => []
    },
    isOwnProfile: {
      type: Boolean,
      default: true
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
    getLikeImageUrl(like) {
      // 处理图片路径，参考 WorksList.vue 的逻辑
      console.group(`🖼️ [点赞图片URL调试] ${like.title || like.contributionId}`);
      console.log('📦 完整作品对象:', like);
      console.log('🔍 image字段原始值:', like.image);
      console.log('🔍 image字段类型:', typeof like.image);
      console.log('🔍 是否为数组:', Array.isArray(like.image));
      if (Array.isArray(like.image)) {
        console.log('🔍 数组长度:', like.image.length);
        console.log('🔍 数组内容:', like.image);
      }
      
      let imagePath = '';
      
      if (Array.isArray(like.image) && like.image.length > 0) {
        // 如果是数组，取第一张图
        imagePath = like.image[0];
        console.log('✅ 从数组获取图片路径:', imagePath);
      } else if (typeof like.image === 'string' && like.image) {
        // 如果是字符串（兼容旧数据）
        imagePath = like.image;
        console.log('✅ 从字符串获取图片路径:', imagePath);
      } else if (like.url) {
        // 兼容旧的 url 字段
        imagePath = like.url;
        console.log('✅ 从url字段获取图片路径:', imagePath);
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
      
      // 拼接基础 URL（参考 WorksList.vue）
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
    getAvatarUrl(like) {
      // 处理头像路径
      let avatarPath = like.avatar || (like.author && like.author.avatar) || like.uploaderAvatarPath;
      
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
    viewDetail(like) {
      this.$router.push(`/image/${like.contributionId || like.id}`);
    },
    handleUnlike(like) {
      this.$emit('unlike', like);
    },
    onImageError(e) {
      const failedUrl = e.target.src;
      console.error('❌ 图片加载失败:', failedUrl);
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

.likes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

/* 复用 ImageBlock.vue 的样式 */
.anime-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(147, 51, 234, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 2px solid transparent;
}

.anime-card:hover {
  box-shadow: 0 12px 24px rgba(147, 51, 234, 0.2);
  border-color: rgba(255, 105, 180, 0.3);
  transform: translateY(-4px);
}

.art-card {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.art-thumb-link {
  display: block;
  text-decoration: none;
  cursor: pointer;
}

.art-thumb {
  position: relative;
  overflow: hidden;
  height: 200px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.art-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: transform 0.4s ease;
}

.anime-card:hover .art-thumb img {
  transform: scale(1.1);
}

.hover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.7) 0%, transparent 50%);
  opacity: 0;
  transition: opacity 0.3s ease;
  display: flex;
  align-items: flex-end;
  padding: 12px;
}

.art-thumb:hover .hover-overlay {
  opacity: 1;
}

.stats-overlay {
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
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
}

.stat-item .icon {
  font-size: 14px;
}

.art-meta {
  padding: 12px;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.art-title {
  display: block;
  color: #6366f1;
  font-weight: 700;
  font-size: 14px;
  text-decoration: none;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color 0.2s ease;
  cursor: pointer;
}

.art-title:hover {
  color: #a855f7;
  text-decoration: underline;
}

.art-author {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #6b7280;
  font-size: 12px;
}

.author-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e0e7ff;
  transition: border-color 0.2s ease;
  flex-shrink: 0;
}

.author-avatar-placeholder {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  color: white;
  border: 2px solid #e0e7ff;
  flex-shrink: 0;
}

.anime-card:hover .author-avatar,
.anime-card:hover .author-avatar-placeholder {
  border-color: #a78bfa;
}

.author-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  overflow: hidden;
  min-width: 0;
}

.author-name {
  font-weight: 600;
  color: #6366f1;
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.author-id {
  font-weight: 500;
  font-size: 11px;
  color: #9ca3af;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.anime-button {
  width: 100%;
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

@media (max-width: 768px) {
  .anime-list-container {
    padding: 20px;
  }
  
  .likes-grid {
    grid-template-columns: 1fr;
  }
  
  .list-title {
    font-size: 1.5rem;
  }
  
  .art-thumb {
    height: 160px;
  }
  
  .stats-overlay {
    font-size: 11px;
    gap: 12px;
  }
  
  .art-title {
    font-size: 13px;
  }
}
</style>


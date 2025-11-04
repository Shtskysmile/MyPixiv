<template>
  <div class="art-card anime-card">
    <router-link :to="`/image/${image.contributionId || image.id}`" class="art-thumb-link">
      <div class="art-thumb">
        <!-- 加载状态 -->
        <div v-if="imageLoading" class="image-loading">
          <div class="loading-spinner"></div>
        </div>
        <!-- 实际图片 -->
        <img 
          v-show="!imageLoading" 
          :src="loadedImageUrl || placeholderImage" 
          :alt="image.title" 
          @load="imageLoading = false"
          @error="onImageError"
        />
        <div class="hover-overlay">
          <div class="stats-overlay">
            <span class="stat-item" v-if="image.viewCount !== undefined">
              <i class="icon">👁️</i> {{ formatCount(image.viewCount) }}
            </span>
            <span class="stat-item" v-if="image.likeCount !== undefined">
              <i class="icon">❤️</i> {{ formatCount(image.likeCount) }}
            </span>
            <span class="stat-item" v-if="image.favoriteCount !== undefined">
              <i class="icon">⭐</i> {{ formatCount(image.favoriteCount) }}
            </span>
          </div>
        </div>
      </div>
    </router-link>

    <div class="art-meta">
      <router-link :to="`/image/${image.contributionId || image.id}`" class="art-title">
        {{ image.title || '无标题' }}
      </router-link>
      <div class="art-author">
        <img
          class="author-avatar"
          :src="loadedAvatarUrl || defaultAvatar"
          :alt="image.authorName || image.authorId || 'author'"
          @error="onAvatarError"
        />
        <div class="author-info">
          <span class="author-name" v-if="image.authorName">{{ image.authorName }}</span>
          <span class="author-id">ID: {{ formatAuthorId(image.authorId) }}</span>
        </div>
      </div>
      <div class="art-tags" v-if="image.tags && image.tags.length">
        <span class="mini-tag" v-for="(tag, idx) in image.tags.slice(0, 3)" :key="idx">
          {{ tag }}
        </span>
      </div>
    </div>
  </div>
</template>

<script>
import { loadImage, loadAvatar } from '@/utils/imageLoader';

export default {
  props: {
    image: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      loadedImageUrl: null,
      loadedAvatarUrl: null,
      imageLoading: true,
      placeholderImage: 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="400" height="200"%3E%3Crect fill="%23f5f7fa" width="400" height="200"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23c3cfe2" font-size="24"%3E加载中...%3C/text%3E%3C/svg%3E',
      defaultAvatar: 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="40" height="40"%3E%3Crect fill="%23ddd" width="40" height="40"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999"%3EU%3C/text%3E%3C/svg%3E'
    };
  },
  mounted() {
    this.loadImageData();
    this.loadAvatarData();
  },
  watch: {
    'image.image': function() {
      this.loadImageData();
    },
    'image.url': function() {
      this.loadImageData();
    },
    'image.avatar': function() {
      this.loadAvatarData();
    },
    'image.uploaderAvatarPath': function() {
      this.loadAvatarData();
    }
  },
  methods: {
    async loadImageData() {
      // 处理后端返回的 image 字段：可能是字符串数组或单个字符串
      let imagePath = this.image.url; // 优先使用 url 字段（单个字符串）
      
      if (!imagePath) {
        // 如果没有 url 字段，检查 image 字段
        if (Array.isArray(this.image.image) && this.image.image.length > 0) {
          // image 是数组，取第一张图片作为封面
          imagePath = this.image.image[0];
          console.log('📸 使用数组第一张图片作为封面:', imagePath);
        } else if (typeof this.image.image === 'string') {
          // image 是字符串
          imagePath = this.image.image;
        }
      }
      
      if (!imagePath) {
        console.warn('⚠️ 未找到图片路径:', this.image);
        this.imageLoading = false;
        return;
      }

      console.log('🖼️ 加载图片:', imagePath);
      try {
        const url = await loadImage(imagePath);
        this.loadedImageUrl = url || imagePath;
      } catch (error) {
        console.error('❌ 加载图片失败:', error);
        this.loadedImageUrl = imagePath;
      } finally {
        // imageLoading 会在图片 @load 事件触发时设置为 false
      }
    },
    async loadAvatarData() {
      const avatarPath = this.image.uploaderAvatarPath || this.image.avatar || 
                         (this.image.author && this.image.author.avatar);
      if (!avatarPath) {
        return;
      }

      try {
        const url = await loadAvatar(avatarPath);
        this.loadedAvatarUrl = url || avatarPath;
      } catch (error) {
        console.error('加载头像失败:', error);
        this.loadedAvatarUrl = avatarPath;
      }
    },
    formatCount(count) {
      if (count >= 10000) {
        return (count / 10000).toFixed(1) + 'w';
      } else if (count >= 1000) {
        return (count / 1000).toFixed(1) + 'k';
      }
      return count;
    },
    formatAuthorId(authorId) {
      if (!authorId) return '匿名';
      // 只显示前8位
      return authorId.length > 8 ? authorId.substring(0, 8) + '...' : authorId;
    },
    onImageError(e) {
      // 图片加载失败时使用占位图
      this.imageLoading = false;
      e.target.src = this.placeholderImage;
    },
    onAvatarError(e) {
      // 头像加载失败时使用默认图片
      e.target.src = this.defaultAvatar;
    }
  },
};
</script>

<style scoped>
/* 加载状态样式 */
.image-loading {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  z-index: 1;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(147, 51, 234, 0.1);
  border-top-color: #9333ea;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

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

.anime-card:hover .author-avatar {
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

.art-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.mini-tag {
  display: inline-block;
  padding: 2px 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-size: 10px;
  border-radius: 12px;
  font-weight: 600;
}

@media (max-width: 768px) {
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

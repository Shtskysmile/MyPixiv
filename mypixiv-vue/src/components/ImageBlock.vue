<template>
  <div class="art-card anime-card">
    <router-link :to="`/image/${image.contributionId || image.id}`" class="art-thumb-link">
      <div class="art-thumb">
        <img :src="image.image || image.url" :alt="image.title" />
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
          :src="image.avatar || (image.author && image.author.avatar) || '/static/default-avatar.png'"
          :alt="image.authorId || 'author'"
          @error="onAvatarError"
        />
        <span class="author-name">ID: {{ formatAuthorId(image.authorId) }}</span>
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
export default {
  props: {
    image: {
      type: Object,
      required: true,
    },
  },
  methods: {
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
    onAvatarError(e) {
      // 头像加载失败时使用默认图片
      e.target.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="40" height="40"%3E%3Crect fill="%23ddd" width="40" height="40"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999"%3EU%3C/text%3E%3C/svg%3E';
    }
  },
};
</script>

<style scoped>
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
}

.anime-card:hover .author-avatar {
  border-color: #a78bfa;
}

.author-name {
  font-weight: 500;
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

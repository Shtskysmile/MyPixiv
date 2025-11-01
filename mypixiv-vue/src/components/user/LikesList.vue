<template>
  <div class="anime-list-container">
    <div class="list-header">
      <h3 class="list-title anime-gradient-text">
        <span class="icon">❤️</span> 点赞的作品
      </h3>
      <p class="list-subtitle">共 {{ likes.length }} 个作品</p>
    </div>

    <div class="likes-grid" v-if="likes.length > 0">
      <div class="like-card" v-for="like in likes" :key="like.contributionId || like.id">
        <router-link :to="`/image/${like.contributionId || like.id}`" class="card-link">
          <div class="card-image">
            <img :src="like.image || like.url" :alt="like.title" @error="onImageError" />
            <div class="image-overlay">
              <div class="overlay-stats">
                <span class="stat-item">
                  <i>👁️</i> {{ formatCount(like.viewCount) }}
                </span>
                <span class="stat-item">
                  <i>❤️</i> {{ formatCount(like.likeCount) }}
                </span>
              </div>
            </div>
          </div>
        </router-link>

        <div class="card-content">
          <router-link :to="`/image/${like.contributionId || like.id}`" class="card-title">
            {{ like.title || '无标题' }}
          </router-link>
          
          <div class="card-author">
            <img 
              :src="like.avatar || (like.author && like.author.avatar)" 
              class="author-avatar"
              @error="onAvatarError"
            />
            <span class="author-name">ID: {{ formatAuthorId(like.authorId) }}</span>
          </div>

          <div class="card-stats">
            <span class="stat"><i>⭐</i> {{ formatCount(like.favoriteCount) }}</span>
            <span class="stat"><i>💬</i> {{ formatCount(like.commentCount) }}</span>
          </div>

          <button class="anime-button is-small is-danger" @click="handleUnlike(like)">
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
    handleUnlike(like) {
      const title = like.title || '此作品';
      const confirmed = confirm(`确定取消点赞《${title}》吗？`);
      if (confirmed) {
        this.$emit('unlike', like);
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

.likes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.like-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  border: 2px solid rgba(147, 51, 234, 0.1);
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(147, 51, 234, 0.08);
}

.like-card:hover {
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

.like-card:hover .card-image img {
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

.like-card:hover .image-overlay {
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

.card-author {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.author-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e0e7ff;
}

.author-name {
  font-size: 13px;
  color: #6b7280;
  font-weight: 600;
}

.card-stats {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
  padding: 8px 0;
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
  .likes-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 16px;
  }
  
  .card-image {
    height: 150px;
  }
}
</style>


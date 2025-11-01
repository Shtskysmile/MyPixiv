<template>
  <div class="anime-list-container">
    <div class="list-header">
      <h3 class="list-title anime-gradient-text">
        <span class="icon">👥</span> 我的关注
      </h3>
      <p class="list-subtitle">共 {{ total }} 位用户</p>
    </div>

    <div class="followers-grid" v-if="followers.length > 0">
      <div class="follower-card" v-for="follower in followers" :key="follower.userId || follower.id">
        <div class="card-header">
          <div class="avatar-wrapper">
            <img 
              :src="follower.avatar" 
              alt="avatar" 
              class="follower-avatar"
              @error="onAvatarError"
            />
            <div class="status-dot" :class="{ 'online': follower.status === 0 }"></div>
          </div>
          
          <div class="user-info">
            <div class="user-name">{{ follower.username || follower.name || '用户' }}</div>
            <div class="user-id">ID: {{ formatUserId(follower.userId || follower.id) }}</div>
            <div class="user-badges">
              <span class="mini-badge" :class="getRoleBadgeClass(follower.role)">
                {{ getRoleText(follower.role) }}
              </span>
              <span class="mini-badge" :class="{ 'badge-normal': follower.status === 0, 'badge-banned': follower.status === 1 }">
                {{ follower.status === 0 ? '正常' : '封禁' }}
              </span>
            </div>
          </div>
        </div>

        <div class="card-bio" v-if="follower.bio">
          <p>{{ follower.bio }}</p>
        </div>
        <div class="card-bio empty-bio" v-else>
          <p class="has-text-grey-light">
            <span class="icon">💭</span> 暂无简介
          </p>
        </div>

        <div class="card-actions">
          <button class="anime-button is-small is-primary" @click="handleVisit(follower)">
            <span class="icon">👁️</span>
            <span>查看主页</span>
          </button>
          <button class="anime-button is-small is-danger-outline" @click="handleUnfollow(follower)">
            <span class="icon">💔</span>
            <span>取消关注</span>
          </button>
        </div>
      </div>
    </div>

    <div class="empty-state" v-else>
      <div class="empty-icon">👥</div>
      <p class="empty-text">还没有关注任何用户</p>
      <p class="empty-hint">快去关注有趣的创作者吧！</p>
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
  name: 'UserFollowersList',
  props: {
    followers: {
      type: Array,
      default: () => []
    },
    page: {
      type: Number,
      default: 1
    },
    pageSize: {
      type: Number,
      default: 10
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
    formatUserId(userId) {
      if (!userId) return '未知';
      return userId.length > 12 ? userId.substring(0, 12) + '...' : userId;
    },
    getRoleText(role) {
      const roles = { 0: '用户', 1: '管理', 2: '系统' };
      return roles[role] || '用户';
    },
    getRoleBadgeClass(role) {
      return {
        'badge-user': role === 0,
        'badge-admin': role === 1,
        'badge-system': role === 2
      };
    },
    handleVisit(follower) {
      // TODO: 跳转到用户主页
      alert('查看用户主页功能开发中...');
    },
    handleUnfollow(follower) {
      const name = follower.username || follower.name || '此用户';
      const confirmed = confirm(`确定取消关注 ${name} 吗？`);
      if (confirmed) {
        this.$emit('remove', follower);
      }
    },
    onAvatarError(e) {
      e.target.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="80" height="80"%3E%3Crect fill="%23ddd" width="80" height="80"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999" font-size="24"%3EU%3C/text%3E%3C/svg%3E';
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

.followers-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.follower-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  border: 2px solid rgba(147, 51, 234, 0.1);
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(147, 51, 234, 0.08);
}

.follower-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(147, 51, 234, 0.2);
  border-color: #a78bfa;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.avatar-wrapper {
  position: relative;
  flex-shrink: 0;
}

.follower-avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #a78bfa;
  box-shadow: 0 4px 12px rgba(147, 51, 234, 0.2);
}

.status-dot {
  position: absolute;
  bottom: 4px;
  right: 4px;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #9ca3af;
  border: 3px solid white;
}

.status-dot.online {
  background: #10b981;
  box-shadow: 0 0 8px rgba(16, 185, 129, 0.5);
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 18px;
  font-weight: 800;
  color: #374151;
  margin-bottom: 4px;
}

.user-id {
  font-size: 12px;
  color: #9ca3af;
  font-family: 'Courier New', monospace;
  margin-bottom: 8px;
}

.user-badges {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.mini-badge {
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 700;
  border: 1.5px solid;
}

.badge-user {
  background: #eff6ff;
  color: #3b82f6;
  border-color: #93c5fd;
}

.badge-admin {
  background: #fffbeb;
  color: #f59e0b;
  border-color: #fcd34d;
}

.badge-system {
  background: #fef2f2;
  color: #ef4444;
  border-color: #fca5a5;
}

.badge-normal {
  background: #f0fdf4;
  color: #10b981;
  border-color: #86efac;
}

.badge-banned {
  background: #fef2f2;
  color: #ef4444;
  border-color: #fca5a5;
}

.card-bio {
  padding: 12px;
  background: rgba(147, 51, 234, 0.03);
  border-radius: 10px;
  margin-bottom: 16px;
  font-size: 13px;
  color: #4b5563;
  line-height: 1.6;
}

.empty-bio {
  text-align: center;
  background: rgba(147, 51, 234, 0.02);
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

.anime-button.is-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.anime-button.is-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(147, 51, 234, 0.3);
}

.anime-button.is-danger-outline {
  background: white;
  border-color: #f87171;
  color: #ef4444;
}

.anime-button.is-danger-outline:hover {
  background: linear-gradient(135deg, #f87171 0%, #ef4444 100%);
  color: white;
  border-color: transparent;
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
  .followers-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .pagination-wrapper {
    flex-wrap: wrap;
  }
}
</style>


<template>
  <div class="anime-profile-card">
    <div class="profile-header">
      <div class="avatar-section">
        <div class="avatar-wrapper">
          <!-- 如果有头像URL则显示图片，否则显示占位符 -->
          <img v-if="userAvatar" :src="userAvatar" alt="avatar" class="avatar-img" />
          <div v-else class="avatar-placeholder">
            {{ userData.username ? userData.username.charAt(0).toUpperCase() : 'U' }}
          </div>
          <div class="avatar-ring"></div>
        </div>
      </div>
      
      <div class="info-section">
        <h2 class="user-name anime-gradient-text">
          {{ userData.username || '用户' }}
        </h2>
        <div class="user-badges">
          <span class="badge" :class="getRoleBadgeClass(userData.role)">
            {{ getRoleText(userData.role) }}
          </span>
          <span class="badge" :class="getStatusBadgeClass(userData.status)">
            {{ getStatusText(userData.status) }}
          </span>
          <span class="badge gender-badge">
            {{ userData.sex === 1 ? '♂ 男' : '♀ 女' }}
          </span>
        </div>
        
        <div class="user-id" @click="copyIdToClipboard(userData.userId, '用户ID')" title="点击复制ID">
          <span class="icon">🆔</span>
          <span class="id-text">ID: {{ formatUserId(userData.userId) }}</span>
          <span class="copy-icon">📋</span>
        </div>
      </div>

      <div class="actions-section">
        <button v-if="isOwnProfile" class="anime-button is-primary" @click="$emit('edit')">
          <span class="icon">✏️</span>
          <span>编辑资料</span>
        </button>
        
        
        <!-- 普通用户查看他人主页时显示关注/取消关注按钮（社区管理员和系统管理员不显示） -->
        <button 
          v-if="!isOwnProfile && !isCommunityAdmin && !isSystemAdmin && isConcerned !== undefined" 
          class="anime-button" 
          :class="isConcerned ? 'is-danger' : 'is-success'"
          @click="toggleConcern"
        >
          <span class="icon">{{ isConcerned ? '💔' : '💖' }}</span>
          <span>{{ isConcerned ? '取消关注' : '关注TA' }}</span>
        </button>
        
        <!-- 社区管理员查看他人主页时显示封禁/解封按钮 -->
        <button 
          v-if="!isOwnProfile && isCommunityAdmin" 
          class="anime-button" 
          :class="userData.status === 1 ? 'is-success' : 'is-danger'"
          @click="toggleBlockUser"
        >
          <span class="icon">{{ userData.status === 1 ? '🔓' : '🔒' }}</span>
          <span>{{ userData.status === 1 ? '解封用户' : '封禁用户' }}</span>
        </button>
        
        <button class="anime-button is-light" @click="$emit('back')">
          <span class="icon">🏠</span>
          <span>返回首页</span>
        </button>
      </div>
    </div>

    <!-- 统计卡片（普通用户才显示，社区管理员和系统管理员在自己主页不显示） -->
    <div v-if="!isOwnProfile || (!isCommunityAdmin && !isSystemAdmin)" class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon">👥</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.following }}</div>
          <div class="stat-label">关注</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">💖</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.followers }}</div>
          <div class="stat-label">点赞</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">🎨</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.works }}</div>
          <div class="stat-label">作品</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">⭐</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.favorites }}</div>
          <div class="stat-label">收藏</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import copyIdMixin from '@/mixins/copyId';

export default {
  name: 'UserProfile',
  mixins: [copyIdMixin],
  props: {
    user: {
      type: Object,
      required: true,
    },
    isConcerned: {
      type: Boolean,
      default: undefined
    },
    isOwnProfile: {
      type: Boolean,
      default: true
    },
    isCommunityAdmin: {
      type: Boolean,
      default: false
    },
    isSystemAdmin: {
      type: Boolean,
      default: false
    },
    stats: {
      type: Object,
      default: () => ({
        following: 0,
        followers: 0,
        works: 0,
        favorites: 0
      })
    }
  },
  data() {
    return {};
  },
  computed: {
    // 对齐后端 R_User 结构
    userData() {
      const data = {
        userId: this.user.userId || this.user.id || '',
        username: this.user.username || this.user.name || '用户',
        role: this.user.role !== undefined ? this.user.role : 0,
        status: this.user.status !== undefined ? this.user.status : 0,
        sex: this.user.sex !== undefined ? this.user.sex : 0,
        avatar: this.user.avatar || ''
      };
      console.log('🔍 [Profile调试] userData计算属性:', data);
      return data;
    },
    userAvatar() {
      // 参考 Navbar.vue 的实现：拼接完整的头像 URL
      const avatar = this.userData.avatar;
      const baseURL = process.env.VUE_APP_API_BASE_URL;
      
      console.log('🔍 [Profile调试] 头像路径计算:');
      console.log('  - 原始avatar:', avatar);
      console.log('  - baseURL:', baseURL);
      
      if (avatar) {
        // avatar 格式如: /files/userId/avatar/xxx.jpg
        // 直接拼接基础 URL 即可访问
        const fullUrl = `${baseURL}${avatar}`;
        console.log('  - 完整URL:', fullUrl);
        return fullUrl;
      }
      
      console.log('  - 没有头像，返回空字符串，将显示占位符');
      return '';
    }
  },
  methods: {
    getRoleText(role) {
      const roles = {
        0: '👤 普通用户',
        1: '🛡️ 社区管理',
        2: '👑 系统管理'
      };
      return roles[role] || '👤 用户';
    },
    getRoleBadgeClass(role) {
      return {
        'is-info': role === 0,
        'is-warning': role === 1,
        'is-danger': role === 2
      };
    },
    getStatusText(status) {
      return status === 0 ? '✅ 正常' : '🚫 封禁';
    },
    getStatusBadgeClass(status) {
      return {
        'is-success': status === 0,
        'is-danger': status === 1
      };
    },
    toggleConcern() {
      // 触发父组件事件，传递当前关注状态和用户ID
      this.$emit('toggle-concern', {
        userId: this.userData.userId,
        currentState: this.isConcerned
      });
    },
    toggleBlockUser() {
      // 触发父组件事件，传递用户ID和当前封禁状态
      this.$emit('toggle-block-user', {
        userId: this.userData.userId,
        currentStatus: this.userData.status
      });
    }
  }
};
</script>

<style scoped>
.anime-profile-card {
  background: rgba(255, 255, 255, 0.98);
  border: 3px solid rgba(255, 105, 180, 0.2);
  border-radius: 24px;
  padding: 32px;
  box-shadow: 0 12px 48px rgba(147, 51, 234, 0.15);
  margin-bottom: 24px;
}

.profile-header {
  display: flex;
  gap: 32px;
  align-items: center;
  margin-bottom: 32px;
}

.avatar-section {
  flex-shrink: 0;
}

.avatar-wrapper {
  position: relative;
  width: 140px;
  height: 140px;
}

.avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 5px solid #fff;
  box-shadow: 0 8px 24px rgba(147, 51, 234, 0.3);
  position: relative;
  z-index: 2;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  border: 5px solid #fff;
  box-shadow: 0 8px 24px rgba(147, 51, 234, 0.3);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 3rem;
  font-weight: bold;
  color: white;
  position: relative;
  z-index: 2;
}

.avatar-ring {
  position: absolute;
  top: -8px;
  left: -8px;
  right: -8px;
  bottom: -8px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  z-index: 1;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.5;
  }
  50% {
    transform: scale(1.05);
    opacity: 0.8;
  }
}

.info-section {
  flex: 1;
}

.user-name {
  font-size: 2rem;
  font-weight: 900;
  margin-bottom: 12px;
}

.anime-gradient-text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.user-badges {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 12px;
}

.badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border-radius: 16px;
  font-size: 13px;
  font-weight: 600;
  border: 2px solid;
}

.badge.is-info {
  background: #eff6ff;
  color: #3b82f6;
  border-color: #93c5fd;
}

.badge.is-warning {
  background: #fffbeb;
  color: #f59e0b;
  border-color: #fcd34d;
}

.badge.is-danger {
  background: #fef2f2;
  color: #ef4444;
  border-color: #fca5a5;
}

.badge.is-success {
  background: #f0fdf4;
  color: #10b981;
  border-color: #86efac;
}

.gender-badge {
  background: linear-gradient(135deg, #667eea15 0%, #764ba215 100%);
  color: #6366f1;
  border-color: #a78bfa;
}

.user-id {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #6b7280;
  font-size: 14px;
  margin-bottom: 16px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 8px;
  background: rgba(0, 0, 0, 0.02);
  transition: all 0.3s ease;
  user-select: none;
}

.user-id:hover {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  transform: translateX(2px);
}

.user-id:active {
  transform: scale(0.98);
}

.copy-icon {
  opacity: 0;
  transition: opacity 0.3s ease;
  font-size: 12px;
  margin-left: auto;
}

.user-id:hover .copy-icon {
  opacity: 1;
}

.id-text {
  font-family: 'Courier New', monospace;
  font-weight: 600;
}

.actions-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex-shrink: 0;
}

.anime-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 12px;
  font-weight: 700;
  border: 2px solid transparent;
  transition: all 0.3s ease;
  cursor: pointer;
  white-space: nowrap;
}

.anime-button.is-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.anime-button.is-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(147, 51, 234, 0.3);
}

.anime-button.is-light {
  background: white;
  border-color: rgba(147, 51, 234, 0.3);
  color: #6366f1;
}

.anime-button.is-light:hover {
  background: rgba(147, 51, 234, 0.05);
  border-color: #a78bfa;
}

.anime-button.is-success {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
}

.anime-button.is-success:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(16, 185, 129, 0.3);
}

.anime-button.is-danger {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
}

.anime-button.is-danger:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(239, 68, 68, 0.3);
}

.concern-badge {
  margin-top: 8px;
}

.concern-badge .tag {
  width: 100%;
  justify-content: center;
  padding: 8px 12px;
  border-radius: 12px;
  font-weight: 600;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: linear-gradient(135deg, #667eea10 0%, #764ba210 100%);
  border-radius: 16px;
  border: 2px solid rgba(147, 51, 234, 0.1);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(147, 51, 234, 0.2);
  border-color: #a78bfa;
}

.stat-icon {
  font-size: 36px;
  line-height: 1;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 900;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #6b7280;
  font-weight: 600;
  margin-top: 4px;
}

@media (max-width: 1024px) {
  .profile-header {
    flex-direction: column;
    text-align: center;
  }
  
  .info-section {
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .anime-profile-card {
    padding: 20px;
  }
  
  .avatar-wrapper {
    width: 100px;
    height: 100px;
  }
  
  .user-name {
    font-size: 1.5rem;
  }
  
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .stat-card {
    padding: 16px;
  }
}
</style>


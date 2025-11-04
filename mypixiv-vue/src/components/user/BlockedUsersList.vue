<template>
  <div class="anime-blocked-users-container">
    <div class="blocked-header">
      <h2 class="title anime-gradient-text">
        <span class="icon">🔒</span> 被封禁用户管理
      </h2>
      <p class="subtitle">社区管理员用户管理平台</p>
    </div>

    <!-- 用户列表 -->
    <div class="users-list">
      <div v-if="blockedUsers.length === 0" class="empty-state">
        <span class="icon">📭</span>
        <p>暂无被封禁用户</p>
      </div>
      <div v-for="user in blockedUsers" :key="user.userId" class="user-card anime-card" @click="viewUserProfile(user)">
        <div class="user-avatar">
          <img :src="getAvatarUrl(user.avatar)" :alt="user.username" @error="onAvatarError" />
          <div class="user-badge blocked-badge">🔒 已封禁</div>
        </div>
        <div class="user-info">
          <h4 class="user-name">{{ user.username }}</h4>
          <div class="user-meta">
            <span class="user-id">
              <span class="icon">🆔</span> ID: {{ user.userId }}
            </span>
          </div>
          <div class="user-role">
            <span class="role-badge" :class="getRoleClass(user.role)">
              {{ getRoleText(user.role) }}
            </span>
            <span class="gender-badge">
              {{ getGenderText(user.sex) }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request';
import placeholderAvatar from '@/assets/images/avatar.png';

export default {
  name: 'BlockedUsersList',
  data() {
    return {
      blockedUsers: [],
      placeholderAvatar
    };
  },
  created() {
    this.fetchBlockedUsers();
  },
  methods: {
    fetchBlockedUsers() {
      // 调用后端接口 GET /api/communityAdmin/blockedUsers
      request.get('/communityAdmin/blockedUsers')
        .then((res) => {
          if (res.data && res.data.code === 0) {
            this.blockedUsers = res.data.data || [];
            console.log('📋 被封禁用户数据:', this.blockedUsers);
          } else {
            console.error('加载被封禁用户失败:', res.data?.message);
          }
        })
        .catch((error) => {
          console.error('加载被封禁用户失败:', error);
          this.blockedUsers = [];
        });
    },
    getAvatarUrl(avatarPath) {
      if (!avatarPath) return this.placeholderAvatar;
      const baseURL = process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080';
      const path = avatarPath.startsWith('/') ? avatarPath : `/${avatarPath}`;
      return `${baseURL}${path}`;
    },
    onAvatarError(e) {
      e.target.src = this.placeholderAvatar;
    },
    viewUserProfile(user) {
      // 跳转到用户主页
      this.$router.push({
        path: `/user/${user.userId}`
      });
    },
    getRoleText(role) {
      const roleMap = {
        0: '普通用户',
        1: '社区管理员',
        2: '系统管理员'
      };
      return roleMap[role] || '未知';
    },
    getRoleClass(role) {
      const classMap = {
        0: 'role-normal',
        1: 'role-community',
        2: 'role-system'
      };
      return classMap[role] || 'role-normal';
    },
    getGenderText(sex) {
      const genderMap = {
        0: '未设置',
        1: '♂ 男',
        2: '♀ 女'
      };
      return genderMap[sex] || '未知';
    }
  }
};
</script>

<style scoped>
.anime-blocked-users-container {
  background: rgba(255, 255, 255, 0.98);
  border: 3px solid rgba(239, 68, 68, 0.2);
  border-radius: 24px;
  padding: 32px;
  box-shadow: 0 12px 48px rgba(239, 68, 68, 0.15);
}

.blocked-header {
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
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  color: #6b7280;
  font-weight: 600;
}

.users-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
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

.user-card {
  background: white;
  border: 2px solid rgba(239, 68, 68, 0.1);
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
  display: flex;
  align-items: center;
  padding: 20px;
  gap: 16px;
}

.user-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(239, 68, 68, 0.2);
  border-color: #f87171;
}

.user-avatar {
  position: relative;
  flex-shrink: 0;
}

.user-avatar img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #ef4444;
  transition: transform 0.3s ease;
}

.user-card:hover .user-avatar img {
  transform: scale(1.05);
}

.user-badge {
  position: absolute;
  bottom: -5px;
  right: -5px;
  padding: 4px 8px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 700;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.blocked-badge {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-name {
  font-size: 18px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-meta {
  margin-bottom: 8px;
}

.user-id {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: #6b7280;
  font-size: 13px;
  font-weight: 600;
  font-family: 'Courier New', monospace;
}

.user-role {
  display: flex;
  gap: 8px;
  align-items: center;
}

.role-badge, .gender-badge {
  padding: 4px 10px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 700;
  display: inline-block;
}

.role-badge.role-normal {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
}

.role-badge.role-community {
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
  color: white;
}

.role-badge.role-system {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: white;
}

.gender-badge {
  background: linear-gradient(135deg, #ec4899 0%, #db2777 100%);
  color: white;
}

@media (max-width: 768px) {
  .anime-blocked-users-container {
    padding: 20px;
  }
  
  .users-list {
    grid-template-columns: 1fr;
  }
  
  .title {
    font-size: 1.5rem;
  }
  
  .user-card {
    flex-direction: column;
    text-align: center;
  }
  
  .user-role {
    justify-content: center;
  }
}
</style>


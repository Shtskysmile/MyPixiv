<template>
  <div class="anime-admin-container">
    <div class="admin-header">
      <h2 class="title anime-gradient-text">
        <span class="icon">👥</span> 用户管理
      </h2>
      <p class="subtitle">系统管理员控制面板</p>
    </div>

    <!-- 搜索栏 -->
    <div class="search-section">
      <div class="field has-addons">
        <div class="control is-expanded">
          <input 
            class="input anime-input" 
            type="text" 
            v-model="searchUserId" 
            placeholder="输入用户ID进行搜索..."
            @keyup.enter="searchUser"
          />
        </div>
        <div class="control">
          <button class="button is-info anime-button" @click="searchUser">
            <span class="icon">🔍</span>
            <span>搜索</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 用户信息卡片 -->
    <div v-if="selectedUser" class="user-card anime-card">
      <div class="card-header">
        <p class="card-header-title">
          <span class="icon">👤</span>
          用户信息
        </p>
      </div>
      <div class="card-content">
        <div class="columns">
          <!-- 头像预览 -->
          <div class="column is-one-quarter has-text-centered">
            <figure class="image is-128x128" style="margin: 0 auto;">
              <img :src="avatarPreview" class="is-rounded avatar-preview" />
            </figure>
            <div class="file has-name is-fullwidth" style="margin-top: 1rem;">
              <label class="file-label">
                <input 
                  class="file-input" 
                  type="file" 
                  accept="image/*"
                  @change="onAvatarChange"
                />
                <span class="file-cta anime-file-button">
                  <span class="file-icon">
                    📷
                  </span>
                  <span class="file-label">
                    选择头像
                  </span>
                </span>
                <span class="file-name" v-if="newAvatarFileName">
                  {{ newAvatarFileName }}
                </span>
              </label>
            </div>
          </div>

          <!-- 用户信息表单 -->
          <div class="column">
            <div class="field">
              <label class="label">用户ID</label>
              <div class="control">
                <div 
                  class="input anime-input clickable-user-id" 
                  @click="copyIdToClipboard(selectedUser.userId, '用户ID')"
                  title="点击复制ID"
                >
                  {{ selectedUser.userId }}
                  <span class="copy-icon-inline">📋</span>
                </div>
              </div>
            </div>

            <div class="field">
              <label class="label">用户名</label>
              <div class="control">
                <input 
                  class="input anime-input" 
                  type="text" 
                  v-model="editForm.username" 
                  placeholder="输入新用户名"
                />
              </div>
            </div>

            <div class="field">
              <label class="label">性别</label>
              <div class="control">
                <div class="select is-fullwidth anime-input">
                  <select v-model="editForm.gender">
                    <option :value="0">未知</option>
                    <option :value="1">男</option>
                    <option :value="2">女</option>
                  </select>
                </div>
              </div>
            </div>

            <div class="field">
              <label class="label">角色</label>
              <div class="control">
                <span class="tag is-medium" :class="getRoleClass(selectedUser.role)">
                  {{ getRoleText(selectedUser.role) }}
                </span>
              </div>
            </div>

            <div class="field">
              <label class="label">账号状态</label>
              <div class="control">
                <span class="tag is-medium" :class="getStatusClass(selectedUser.status)">
                  {{ getStatusText(selectedUser.status) }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="buttons is-centered" style="margin-top: 2rem;">
          <button 
            class="button is-primary anime-button"
            @click="updateUserInfo"
            :disabled="loading"
            :class="{ 'is-loading': loading }"
          >
            <span class="icon">💾</span>
            <span>保存修改</span>
          </button>
          <button 
            class="button is-warning anime-button"
            @click="resetPassword"
            :disabled="loading"
            :class="{ 'is-loading': loading }"
          >
            <span class="icon">🔑</span>
            <span>重置密码</span>
          </button>
          <button 
            class="button is-light anime-button"
            @click="clearSelection"
          >
            <span class="icon">❌</span>
            <span>取消</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 空状态提示 -->
    <div v-else class="empty-state">
      <div class="has-text-centered">
        <span class="icon is-large" style="font-size: 4rem;">🔍</span>
        <p class="title is-4" style="margin-top: 1rem;">请输入用户ID进行搜索</p>
        <p class="subtitle is-6">您可以在这里管理用户的基本信息</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import avatar from '@/assets/images/avatar.png';
import copyIdMixin from '@/mixins/copyId';

export default {
  name: 'UserManagement',
  mixins: [copyIdMixin],
  data() {
    return {
      searchUserId: '',
      selectedUser: null,
      editForm: {
        username: '',
        gender: 0
      },
      newAvatar: null,
      newAvatarFileName: '',
      newAvatarPreview: '',
      loading: false,
      avatar
    };
  },
  computed: {
    avatarPreview() {
      // 如果有新上传的头像，显示预览
      if (this.newAvatarPreview) {
        return this.newAvatarPreview;
      }
      // 否则显示用户当前头像
      if (this.selectedUser && this.selectedUser.avatar) {
        if (this.selectedUser.avatar.startsWith('http')) {
          return this.selectedUser.avatar;
        }
        const baseURL = process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080';
        const avatarPath = this.selectedUser.avatar.startsWith('/') 
          ? this.selectedUser.avatar 
          : `/${this.selectedUser.avatar}`;
        return `${baseURL}${avatarPath}`;
      }
      return this.avatar;
    }
  },
  methods: {
    async searchUser() {
      if (!this.searchUserId.trim()) {
        this.$message.warning('请输入用户ID');
        return;
      }

      try {
        const response = await axios.get(`/api/user/${this.searchUserId}`);
        if (response.data.code === 1) {
          this.selectedUser = response.data.data;
          // 初始化编辑表单
          this.editForm.username = this.selectedUser.username;
          this.editForm.gender = this.selectedUser.sex;
          // 重置头像相关
          this.newAvatar = null;
          this.newAvatarFileName = '';
          this.newAvatarPreview = '';
        } else {
          this.$message.error('用户不存在');
        }
      } catch (error) {
        console.error('搜索用户失败:', error);
        this.$message.error('搜索用户失败');
      }
    },

    onAvatarChange(event) {
      const file = event.target.files[0];
      if (file) {
        this.newAvatar = file;
        this.newAvatarFileName = file.name;
        
        // 创建预览
        const reader = new FileReader();
        reader.onload = (e) => {
          this.newAvatarPreview = e.target.result;
        };
        reader.readAsDataURL(file);
      }
    },

    async updateUserInfo() {
      if (!this.selectedUser) {
        this.$message.warning('请先选择用户');
        return;
      }

      if (!this.editForm.username.trim()) {
        this.$message.warning('用户名不能为空');
        return;
      }

      this.loading = true;
      try {
        const formData = new FormData();
        formData.append('userId', this.selectedUser.userId);
        formData.append('newUsername', this.editForm.username);
        formData.append('newGender', this.editForm.gender);
        
        if (this.newAvatar) {
          formData.append('newAvatar', this.newAvatar);
        }

        const response = await axios.post('/api/systemAdmin/updateUserInfo', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });

        if (response.data.code === 1) {
          this.$message.success('更新用户信息成功');
          // 重新搜索用户以获取最新信息
          await this.searchUser();
        } else {
          this.$message.error(response.data.msg || '更新用户信息失败');
        }
      } catch (error) {
        console.error('更新用户信息失败:', error);
        this.$message.error('更新用户信息失败');
      } finally {
        this.loading = false;
      }
    },

    async resetPassword() {
      if (!this.selectedUser) {
        this.$message.warning('请先选择用户');
        return;
      }

      const confirmed = confirm(`确定要重置用户 ${this.selectedUser.username} 的密码吗？`);
      if (!confirmed) return;

      this.loading = true;
      try {
        const response = await axios.post('/api/systemAdmin/resetPassword', null, {
          params: {
            userId: this.selectedUser.userId
          }
        });

        if (response.data.code === 1) {
          this.$message.success('重置密码成功');
        } else {
          this.$message.error(response.data.msg || '重置密码失败');
        }
      } catch (error) {
        console.error('重置密码失败:', error);
        this.$message.error('重置密码失败');
      } finally {
        this.loading = false;
      }
    },

    clearSelection() {
      this.selectedUser = null;
      this.searchUserId = '';
      this.editForm = {
        username: '',
        gender: 0
      };
      this.newAvatar = null;
      this.newAvatarFileName = '';
      this.newAvatarPreview = '';
    },

    getRoleClass(role) {
      const roleClasses = {
        0: 'is-info',      // 普通用户
        1: 'is-warning',   // 社区管理员
        2: 'is-danger'     // 系统管理员
      };
      return roleClasses[role] || 'is-light';
    },

    getRoleText(role) {
      const roleTexts = {
        0: '👤 普通用户',
        1: '🛡️ 社区管理员',
        2: '👑 系统管理员'
      };
      return roleTexts[role] || '未知';
    },

    getStatusClass(status) {
      return status === 0 ? 'is-success' : 'is-danger';
    },

    getStatusText(status) {
      return status === 0 ? '✅ 正常' : '🚫 已封禁';
    }
  }
};
</script>

<style scoped>
.anime-admin-container {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.admin-header {
  text-align: center;
  margin-bottom: 3rem;
  padding: 2rem;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(240, 248, 255, 0.9));
  border-radius: 15px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.anime-gradient-text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: bold;
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

.anime-gradient-text .icon {
  -webkit-text-fill-color: initial;
  background: none;
}

.subtitle {
  color: #666;
  font-size: 1rem;
}

.search-section {
  margin-bottom: 2rem;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.anime-input {
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.clickable-user-id {
  cursor: pointer;
  user-select: none;
  background: rgba(102, 126, 234, 0.05);
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-family: 'Courier New', monospace;
  font-weight: 600;
}

.clickable-user-id:hover {
  background: rgba(102, 126, 234, 0.15);
  color: #667eea;
  border-color: #667eea;
  transform: translateX(2px);
}

.clickable-user-id:active {
  transform: scale(0.99);
}

.copy-icon-inline {
  opacity: 0;
  transition: opacity 0.3s ease;
  font-size: 14px;
}

.clickable-user-id:hover .copy-icon-inline {
  opacity: 1;
}

.anime-input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 0.125em rgba(102, 126, 234, 0.25);
}

.anime-button {
  border-radius: 8px;
  font-weight: 600;
  transition: all 0.3s ease;
  border: none;
}

.anime-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.user-card {
  background: white;
  border-radius: 15px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  animation: slideIn 0.5s ease;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.card-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.card-header-title {
  color: white;
  font-weight: bold;
}

.avatar-preview {
  border: 3px solid #667eea;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.anime-file-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  transition: all 0.3s ease;
}

.anime-file-button:hover {
  transform: scale(1.05);
}

.empty-state {
  padding: 4rem 2rem;
  text-align: center;
  background: white;
  border-radius: 15px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-state .icon {
  color: #ddd;
}

.empty-state .title {
  color: #999;
}

.empty-state .subtitle {
  color: #bbb;
}

.buttons {
  margin-top: 2rem;
}

.field {
  margin-bottom: 1.5rem;
}

.label {
  font-weight: 600;
  color: #333;
  margin-bottom: 0.5rem;
}
</style>


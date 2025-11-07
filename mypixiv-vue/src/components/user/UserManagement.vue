<template>
  <div class="anime-admin-container">
    <!-- 错误提示 -->
    <div v-if="error" class="anime-notification is-danger">
      <button class="delete" @click="error = ''"></button>
      <span class="icon">⚠️</span>
      <span>{{ error }}</span>
    </div>

    <!-- 成功提示 -->
    <div v-if="success" class="anime-notification is-success">
      <button class="delete" @click="success = ''"></button>
      <span class="icon">✅</span>
      <span>{{ success }}</span>
    </div>

    <!-- 用户管理卡片 -->
    <div class="management-card">
      <!-- 头部 -->
      <div class="card-header-section">
        <div class="header-content">
          <h2 class="card-title anime-gradient-text">
            <span class="title-icon">�</span>
            用户管理
          </h2>
          <p class="card-subtitle">系统管理员控制面板</p>
        </div>
      </div>

      <!-- 表单内容 -->
      <div class="card-body">
        <div class="form-layout">
          <!-- 左侧：头像区域 -->
          <div class="avatar-section">
            <div class="avatar-wrapper">
              <img :src="avatarPreview" alt="avatar" class="avatar-img" @error="onAvatarError" />
              <div class="avatar-ring"></div>
            </div>

            <div class="upload-section">
              <label class="upload-button">
                <input
                  type="file"
                  accept="image/*"
                  @change="onAvatarChange"
                  style="display: none;"
                />
                <span class="upload-icon">📷</span>
                <span class="upload-text">选择头像</span>
              </label>
              <p v-if="newAvatarFileName" class="file-name">{{ newAvatarFileName }}</p>
              <p class="upload-hint">支持 JPG/PNG，最大 5MB</p>
            </div>
          </div>

          <!-- 右侧：表单区域 -->
          <div class="form-section">
            <!-- 用户ID -->
            <div class="form-field">
              <label class="field-label">
                <span class="label-icon">🆔</span>
                <span class="label-text">用户ID</span>
                <span class="required-mark">*</span>
              </label>
              <input
                v-model="form.userId"
                type="text"
                class="field-input"
                placeholder="输入要管理的用户ID"
              />
              <p class="field-hint">必填，要修改的目标用户的唯一标识</p>
            </div>

            <!-- 用户名 -->
            <div class="form-field">
              <label class="field-label">
                <span class="label-icon">👤</span>
                <span class="label-text">新用户名</span>
                <span class="required-mark">*</span>
              </label>
              <input
                v-model="form.username"
                type="text"
                class="field-input"
                placeholder="输入新的用户名"
              />
              <p class="field-hint">必填，用户的新名称</p>
            </div>

            <!-- 性别 -->
            <div class="form-field">
              <label class="field-label">
                <span class="label-icon">⚧️</span>
                <span class="label-text">性别</span>
                <span class="required-mark">*</span>
              </label>
              <div class="gender-options">
                <label class="gender-option" :class="{ active: form.gender === 0 }">
                  <input type="radio" v-model="form.gender" :value="0" />
                  <span class="option-content">
                    <span class="option-icon">❓</span>
                    <span class="option-text">未知</span>
                  </span>
                </label>
                <label class="gender-option" :class="{ active: form.gender === 1 }">
                  <input type="radio" v-model="form.gender" :value="1" />
                  <span class="option-content">
                    <span class="option-icon">♂️</span>
                    <span class="option-text">男</span>
                  </span>
                </label>
                <label class="gender-option" :class="{ active: form.gender === 2 }">
                  <input type="radio" v-model="form.gender" :value="2" />
                  <span class="option-content">
                    <span class="option-icon">♀️</span>
                    <span class="option-text">女</span>
                  </span>
                </label>
              </div>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <button
            class="action-btn primary-btn"
            @click="updateUserInfo"
            :disabled="loading"
            :class="{ loading: loading }"
          >
            <span class="btn-icon">💾</span>
            <span class="btn-text">更新用户信息</span>
          </button>
          <button
            class="action-btn warning-btn"
            @click="resetPassword"
            :disabled="loading"
            :class="{ loading: loading }"
          >
            <span class="btn-icon">🔑</span>
            <span class="btn-text">重置密码</span>
          </button>
          <button
            class="action-btn light-btn"
            @click="clearForm"
          >
            <span class="btn-icon">🔄</span>
            <span class="btn-text">清空表单</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request';
import avatar from '@/assets/images/avatar.png';
import copyIdMixin from '@/mixins/copyId';

export default {
  name: 'UserManagement',
  mixins: [copyIdMixin],
  data() {
    return {
      form: {
        userId: '',
        username: '',
        gender: 0
      },
      newAvatar: null,
      newAvatarFileName: '',
      newAvatarPreview: '',
      loading: false,
      error: '',
      success: '',
      defaultAvatar: avatar
    };
  },
  computed: {
    avatarPreview() {
      // 如果有新上传的头像，显示预览
      if (this.newAvatarPreview) {
        return this.newAvatarPreview;
      }
      // 否则显示默认头像
      return this.defaultAvatar;
    }
  },
  methods: {
    onAvatarChange(event) {
      const file = event.target.files[0];
      if (file) {
        // 验证文件类型
        const validTypes = ['image/jpeg', 'image/jpg', 'image/png'];
        if (!validTypes.includes(file.type)) {
          this.error = '只支持 JPG 和 PNG 格式的图片';
          this.showToast('只支持 JPG 和 PNG 格式的图片', 'error');
          return;
        }

        // 验证文件大小（限制为 5MB）
        const maxSize = 5 * 1024 * 1024; // 5MB
        if (file.size > maxSize) {
          this.error = '图片大小不能超过 5MB';
          this.showToast('图片大小不能超过 5MB', 'error');
          return;
        }

        this.newAvatar = file;
        this.newAvatarFileName = file.name;

        // 创建预览
        const reader = new FileReader();
        reader.onload = (e) => {
          this.newAvatarPreview = e.target.result;
        };
        reader.readAsDataURL(file);

        console.log('📷 已选择头像:', file.name, '大小:', (file.size / 1024).toFixed(2) + 'KB');
      }
    },

    onAvatarError(e) {
      e.target.src = this.defaultAvatar;
    },

    async updateUserInfo() {
      // 验证必填字段
      if (!this.form.userId.trim()) {
        this.error = '请输入用户ID';
        this.showToast('请输入用户ID', 'warning');
        return;
      }

      if (!this.form.username.trim()) {
        this.error = '请输入新用户名';
        this.showToast('请输入新用户名', 'warning');
        return;
      }

      this.loading = true;
      this.error = '';
      this.success = '';

      try {
        const formData = new FormData();
        formData.append('userId', this.form.userId.trim());
        formData.append('newUsername', this.form.username.trim());
        formData.append('newGender', this.form.gender);

        // 如果选择了新头像，添加到表单
        if (this.newAvatar) {
          formData.append('newAvatar', this.newAvatar);
        }

        console.log('📤 更新用户信息:', {
          userId: this.form.userId,
          newUsername: this.form.username,
          newGender: this.form.gender,
          hasAvatar: !!this.newAvatar
        });

        const response = await request.post('/systemAdmin/updateUserInfo', formData);

        console.log('📥 更新用户信息响应:', response.data);

        // 后端成功状态：只有 code === 0 才是成功
        if (response.data.code === 0) {
          this.success = '更新用户信息成功！';
          this.showToast('更新用户信息成功', 'success');
          console.log('✅ 更新用户信息成功');
        } else {
          this.error = response.data.message || '更新用户信息失败';
          this.showToast('更新用户信息失败', 'error');
          console.error('❌ 更新用户信息失败:', response.data.message);
        }
      } catch (error) {
        console.error('❌ 更新用户信息失败:', error);
        this.error = error.response?.data?.message || '更新用户信息失败，请稍后重试';
        this.showToast('更新用户信息失败', 'error');
      } finally {
        this.loading = false;
      }
    },

    async resetPassword() {
      // 验证用户ID
      if (!this.form.userId.trim()) {
        this.error = '请输入用户ID';
        this.showToast('请输入用户ID', 'warning');
        return;
      }

      const confirmed = confirm(
        `确定要重置用户 ${this.form.userId} 的密码吗？\n\n` +
        `密码将被重置为系统默认密码。\n` +
        `用户需要使用默认密码重新登录。`
      );
      if (!confirmed) return;

      this.loading = true;
      this.error = '';
      this.success = '';

      try {
        console.log('🔑 重置密码:', this.form.userId);

        const params = new URLSearchParams();
        params.append('userId', this.form.userId.trim());

        const response = await request.post('/systemAdmin/resetPassword', params);
        console.log('📥 重置密码响应:', response.data);

        // 后端成功状态：只有 code === 0 才是成功
        if (response.data.code === 0) {
          this.success = '重置密码成功！密码已重置为系统默认密码。';
          this.showToast('重置密码成功', 'success');
          console.log('✅ 重置密码成功');
        } else {
          this.error = response.data.message || '重置密码失败';
          this.showToast('重置密码失败', 'error');
          console.error('❌ 重置密码失败:', response.data.message);
        }
      } catch (error) {
        console.error('❌ 重置密码失败:', error);
        this.error = error.response?.data?.message || '重置密码失败，请稍后重试';
        this.showToast('重置密码失败', 'error');
      } finally {
        this.loading = false;
      }
    },

    clearForm() {
      this.form = {
        userId: '',
        username: '',
        gender: 0
      };
      this.newAvatar = null;
      this.newAvatarFileName = '';
      this.newAvatarPreview = '';
      this.error = '';
      this.success = '';
      console.log('🔄 表单已清空');
    }
  }
};
</script>

<style scoped>
.anime-admin-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px;
}

/* 通知样式 */
.anime-notification {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  border-radius: 12px;
  margin-bottom: 20px;
  font-weight: 600;
  animation: slideDown 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.anime-notification.is-danger {
  background: #fef2f2;
  color: #ef4444;
  border: 2px solid #fca5a5;
}

.anime-notification.is-success {
  background: #f0fdf4;
  color: #10b981;
  border: 2px solid #86efac;
}

.anime-notification .icon {
  font-size: 20px;
}

.anime-notification .delete {
  margin-left: auto;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 管理卡片 */
.management-card {
  background: rgba(255, 255, 255, 0.98);
  border: 3px solid rgba(255, 105, 180, 0.2);
  border-radius: 24px;
  box-shadow: 0 12px 48px rgba(147, 51, 234, 0.15);
  overflow: hidden;
  animation: fadeInUp 0.5s ease;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 卡片头部 */
.card-header-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 32px;
  text-align: center;
}

.header-content {
  color: white;
}

.card-title {
  font-size: 2rem;
  font-weight: 900;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.anime-gradient-text {
  background: white;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.title-icon {
  font-size: 2rem;
  -webkit-text-fill-color: white;
}

.card-subtitle {
  font-size: 14px;
  opacity: 0.9;
}

/* 卡片主体 */
.card-body {
  padding: 40px;
}

/* 表单布局 */
.form-layout {
  display: flex;
  gap: 40px;
  margin-bottom: 32px;
}

/* 头像区域 */
.avatar-section {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
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

.upload-section {
  text-align: center;
}

.upload-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
}

.upload-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(147, 51, 234, 0.3);
}

.upload-icon {
  font-size: 18px;
}

.file-name {
  margin-top: 8px;
  font-size: 13px;
  color: #6366f1;
  font-weight: 600;
  word-break: break-all;
}

.upload-hint {
  margin-top: 8px;
  font-size: 12px;
  color: #9ca3af;
}

/* 表单区域 */
.form-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.field-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 700;
  color: #374151;
  font-size: 14px;
}

.label-icon {
  font-size: 18px;
}

.required-mark {
  color: #ef4444;
  font-weight: 900;
}

.field-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  font-size: 15px;
  transition: all 0.3s ease;
  background: white;
}

.field-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.field-input::placeholder {
  color: #9ca3af;
}

.field-hint {
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
}

/* 性别选项 */
.gender-options {
  display: flex;
  gap: 12px;
}

.gender-option {
  flex: 1;
  position: relative;
  cursor: pointer;
}

.gender-option input[type="radio"] {
  position: absolute;
  opacity: 0;
}

.option-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px 12px;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  background: white;
  transition: all 0.3s ease;
}

.gender-option:hover .option-content {
  border-color: #a78bfa;
  background: rgba(147, 51, 234, 0.05);
}

.gender-option.active .option-content {
  border-color: #667eea;
  background: linear-gradient(135deg, #667eea10 0%, #764ba210 100%);
  box-shadow: 0 4px 12px rgba(147, 51, 234, 0.2);
}

.option-icon {
  font-size: 28px;
}

.option-text {
  font-size: 14px;
  font-weight: 700;
  color: #374151;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 12px;
  justify-content: center;
  padding-top: 24px;
  border-top: 2px solid rgba(147, 51, 234, 0.1);
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 28px;
  border-radius: 12px;
  font-weight: 700;
  font-size: 15px;
  border: 2px solid transparent;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.action-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.action-btn.loading {
  position: relative;
  color: transparent;
}

.action-btn.loading::after {
  content: '';
  position: absolute;
  width: 20px;
  height: 20px;
  top: 50%;
  left: 50%;
  margin-left: -10px;
  margin-top: -10px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.btn-icon {
  font-size: 18px;
}

.primary-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.primary-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(147, 51, 234, 0.3);
}

.warning-btn {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: white;
}

.warning-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(245, 158, 11, 0.3);
}

.light-btn {
  background: white;
  border-color: rgba(147, 51, 234, 0.3);
  color: #6366f1;
}

.light-btn:hover:not(:disabled) {
  background: rgba(147, 51, 234, 0.05);
  border-color: #a78bfa;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .form-layout {
    flex-direction: column;
    align-items: center;
  }

  .action-buttons {
    flex-direction: column;
  }

  .action-btn {
    width: 100%;
    justify-content: center;
  }

  .gender-options {
    flex-direction: column;
  }
}
</style>



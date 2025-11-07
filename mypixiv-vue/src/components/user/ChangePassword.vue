<template>
  <div class="anime-profile-card">
    <div class="profile-header">
      <h2 class="user-name anime-gradient-text">
        <span class="icon">🔑</span> 修改密码
      </h2>
      <p class="subtitle">为了您的账户安全，请定期更换密码</p>
    </div>

    <!-- 错误提示 -->
    <div v-if="error" class="notification is-danger is-light anime-notification">
      <button class="delete" @click="error = ''"></button>
      <span class="icon">⚠️</span> {{ error }}
    </div>

    <!-- 成功提示 -->
    <div v-if="success" class="notification is-success is-light anime-notification">
      <button class="delete" @click="success = ''"></button>
      <span class="icon">✅</span> {{ success }}
    </div>

    <!-- 修改密码表单 -->
    <form @submit.prevent="handleChangePassword" class="change-password-form">
      <div class="field">
        <label class="label anime-label">
          <span class="icon">🔒</span> 旧密码
        </label>
        <div class="control">
          <input 
            class="input anime-input" 
            type="password" 
            v-model="form.oldPassword" 
            placeholder="请输入当前密码"
            required
          />
        </div>
      </div>

      <div class="field">
        <label class="label anime-label">
          <span class="icon">🔑</span> 新密码
        </label>
        <div class="control">
          <input 
            class="input anime-input" 
            type="password" 
            v-model="form.newPassword" 
            placeholder="请输入新密码（至少6位）"
            required
          />
        </div>
        <p class="help">密码长度至少为6位</p>
      </div>

      <div class="field">
        <label class="label anime-label">
          <span class="icon">✅</span> 确认新密码
        </label>
        <div class="control">
          <input 
            class="input anime-input" 
            type="password" 
            v-model="form.confirmPassword" 
            placeholder="请再次输入新密码"
            required
          />
        </div>
      </div>

      <div class="field is-grouped">
        <div class="control">
          <button 
            class="button anime-button is-primary" 
            type="submit"
            :class="{ 'is-loading': loading }"
            :disabled="loading"
          >
            <span class="icon">💾</span>
            <span>{{ loading ? '修改中...' : '修改密码' }}</span>
          </button>
        </div>
        <div class="control">
          <button 
            class="button anime-button is-light" 
            type="button"
            @click="resetForm"
            :disabled="loading"
          >
            <span class="icon">🔄</span>
            <span>重置</span>
          </button>
        </div>
      </div>
    </form>

    <!-- 提示信息 -->
    <div class="info-box">
      <p class="anime-label">
        <span class="icon">💡</span> 温馨提示
      </p>
      <ul>
        <li>密码长度至少为6位</li>
        <li>建议使用字母、数字和符号的组合</li>
        <li>定期更换密码可以提高账户安全性</li>
      </ul>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request';
import copyIdMixin from '@/mixins/copyId';

export default {
  name: 'ChangePassword',
  mixins: [copyIdMixin],
  data() {
    return {
      form: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      loading: false,
      error: '',
      success: ''
    };
  },
  methods: {
    async handleChangePassword() {
      this.error = '';
      this.success = '';

      // 验证输入
      if (!this.form.oldPassword) {
        this.error = '请输入旧密码';
        return;
      }

      if (!this.form.newPassword) {
        this.error = '请输入新密码';
        return;
      }

      if (this.form.newPassword.length < 6) {
        this.error = '新密码长度至少为6位';
        return;
      }

      if (this.form.newPassword !== this.form.confirmPassword) {
        this.error = '两次输入的密码不一致';
        return;
      }

      if (this.form.oldPassword === this.form.newPassword) {
        this.error = '新密码不能与旧密码相同';
        return;
      }

      this.loading = true;

      try {
        const params = new URLSearchParams();
        params.append('oldPassword', this.form.oldPassword);
        params.append('newPassword', this.form.newPassword);

        console.log('🔑 正在修改密码...');
        const response = await request.post('/changePassword', params);
        console.log('📥 修改密码响应:', response.data);

        if (response.data && response.data.code === 0) {
          this.success = '密码修改成功！';
          this.showToast('密码修改成功', 'success');
          this.resetForm();
          console.log('✅ 密码修改成功');
        } else {
          this.error = response.data?.message || '密码修改失败';
          this.showToast('密码修改失败', 'error');
          console.error('❌ 密码修改失败:', response.data?.message);
        }
      } catch (error) {
        console.error('❌ 修改密码失败:', error);
        this.error = error.response?.data?.message || '密码修改失败，请稍后重试';
        this.showToast('密码修改失败', 'error');
      } finally {
        this.loading = false;
      }
    },

    resetForm() {
      this.form.oldPassword = '';
      this.form.newPassword = '';
      this.form.confirmPassword = '';
      this.error = '';
      this.success = '';
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
  margin-bottom: 32px;
  text-align: center;
}

.user-name {
  font-size: 2rem;
  font-weight: 800;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.anime-gradient-text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  color: #6b7280;
  font-size: 1rem;
}

.change-password-form {
  max-width: 500px;
  margin: 0 auto 32px;
}

.anime-label {
  color: #6366f1;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.anime-input {
  border: 2px solid rgba(147, 51, 234, 0.2);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.anime-input:focus {
  border-color: #a78bfa;
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.1);
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
}

.anime-button.is-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.anime-button.is-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(147, 51, 234, 0.3);
}

.anime-button.is-light {
  background: #f3f4f6;
  color: #6b7280;
}

.anime-button.is-light:hover:not(:disabled) {
  background: #e5e7eb;
}

.anime-notification {
  border-radius: 12px;
  border-left: 4px solid;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 24px;
}

.info-box {
  background: linear-gradient(135deg, #667eea10 0%, #764ba210 100%);
  border: 2px solid rgba(147, 51, 234, 0.1);
  border-radius: 16px;
  padding: 20px;
  max-width: 500px;
  margin: 0 auto;
}

.info-box .anime-label {
  margin-bottom: 12px;
}

.info-box ul {
  list-style: none;
  padding-left: 0;
}

.info-box li {
  padding: 6px 0;
  color: #6b7280;
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-box li:before {
  content: '•';
  color: #a78bfa;
  font-weight: bold;
  font-size: 1.2rem;
}
</style>


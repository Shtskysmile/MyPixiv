<template>
  <div class="anime-profile-card">
    <div class="profile-header">
      <h2 class="user-name anime-gradient-text">
        <span class="icon">🛡️</span> 修改密保问题
      </h2>
      <p class="subtitle">密保问题用于找回密码，请妥善设置</p>
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

    <!-- 修改密保表单 -->
    <form @submit.prevent="handleChangeSecurityIssues" class="security-form">
      <div class="security-section">
        <p class="anime-label">
          <span class="icon">📝</span> 请设置3个密保问题
        </p>

        <div v-for="(issue, index) in securityIssues" :key="index" class="issue-group">
          <div class="field">
            <label class="label is-small">
              <span class="icon">❓</span> 问题 {{ index + 1 }}
            </label>
            <div class="control">
              <input 
                class="input anime-input" 
                type="text" 
                v-model="issue.description" 
                :placeholder="`请输入密保问题 ${index + 1}`"
                required
              />
            </div>
          </div>

          <div class="field">
            <label class="label is-small">
              <span class="icon">💬</span> 答案 {{ index + 1 }}
            </label>
            <div class="control">
              <input 
                class="input anime-input" 
                type="text" 
                v-model="issue.answer" 
                :placeholder="`请输入答案 ${index + 1}`"
                required
              />
            </div>
          </div>
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
            <span>{{ loading ? '保存中...' : '保存密保问题' }}</span>
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
        <li>密保问题用于找回密码，请认真设置</li>
        <li>建议设置只有您自己知道答案的问题</li>
        <li>答案区分大小写，请牢记您的答案</li>
        <li>常见问题示例：您的出生地？您母亲的姓名？您最喜欢的颜色？</li>
      </ul>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request';
import copyIdMixin from '@/mixins/copyId';

export default {
  name: 'ChangeSecurityIssues',
  mixins: [copyIdMixin],
  data() {
    return {
      securityIssues: [
        { description: '', answer: '' },
        { description: '', answer: '' },
        { description: '', answer: '' }
      ],
      loading: false,
      error: '',
      success: ''
    };
  },
  methods: {
    async handleChangeSecurityIssues() {
      this.error = '';
      this.success = '';

      // 验证输入
      for (let i = 0; i < this.securityIssues.length; i++) {
        if (!this.securityIssues[i].description.trim()) {
          this.error = `请输入密保问题 ${i + 1}`;
          return;
        }
        if (!this.securityIssues[i].answer.trim()) {
          this.error = `请输入密保问题 ${i + 1} 的答案`;
          return;
        }
      }

      this.loading = true;

      try {
        // 构造 FormData
        const formData = new FormData();

        // 将密保问题列表转换为 JSON 并作为 Blob 添加
        const securityIssuesJson = this.securityIssues.map(issue => ({
          description: issue.description.trim(),
          answer: issue.answer.trim()
        }));

        formData.append('SecurityIssues', new Blob([JSON.stringify(securityIssuesJson)], {
          type: 'application/json'
        }));

        console.log('🛡️ 正在修改密保问题...');
        console.log('📋 密保问题:', securityIssuesJson);

        const response = await request.post('/changeSecurityIssues', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });

        console.log('📥 修改密保响应:', response.data);

        if (response.data && response.data.code === 0) {
          this.success = '密保问题修改成功！';
          this.showToast('密保问题修改成功', 'success');
          console.log('✅ 密保问题修改成功');
        } else {
          this.error = response.data?.message || '密保问题修改失败';
          this.showToast('密保问题修改失败', 'error');
          console.error('❌ 密保问题修改失败:', response.data?.message);
        }
      } catch (error) {
        console.error('❌ 修改密保问题失败:', error);
        this.error = error.response?.data?.message || '密保问题修改失败，请稍后重试';
        this.showToast('密保问题修改失败', 'error');
      } finally {
        this.loading = false;
      }
    },

    resetForm() {
      this.securityIssues = [
        { description: '', answer: '' },
        { description: '', answer: '' },
        { description: '', answer: '' }
      ];
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

.security-form {
  max-width: 600px;
  margin: 0 auto 32px;
}

.security-section {
  background: linear-gradient(135deg, #667eea10 0%, #764ba210 100%);
  border: 2px solid rgba(147, 51, 234, 0.1);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
}

.security-section > .anime-label {
  font-size: 1.1rem;
  margin-bottom: 20px;
}

.issue-group {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  border: 2px solid rgba(147, 51, 234, 0.1);
}

.issue-group:last-child {
  margin-bottom: 0;
}

.anime-label {
  color: #6366f1;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.label.is-small {
  font-size: 0.9rem;
  color: #6b7280;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 6px;
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
  max-width: 600px;
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
  align-items: flex-start;
  gap: 8px;
}

.info-box li:before {
  content: '•';
  color: #a78bfa;
  font-weight: bold;
  font-size: 1.2rem;
  flex-shrink: 0;
  margin-top: 2px;
}
</style>


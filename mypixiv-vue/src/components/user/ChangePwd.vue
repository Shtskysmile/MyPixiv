<template>
  <section class="change-pwd" :style="bgStyle">
    <div class="overlay">
      <div class="container">
        <div class="columns is-centered">
          <div class="column is-5">
            <div class="box anime-pwd-box">
              <div class="logo-section">
                <h1 class="title has-text-centered anime-title">
                  <span class="icon is-large">🔑</span>
                  <span>找回密码</span>
                </h1>
                <p class="subtitle has-text-centered anime-subtitle">
                  {{ currentStep === 1 ? '请回答密保问题' : '设置新密码' }}
                </p>
              </div>

              <!-- 步骤1：验证密保 -->
              <form v-if="currentStep === 1" @submit.prevent="verifySecurityIssues">
                <div class="field">
                  <label class="label anime-label">
                    <span class="icon">👤</span> 用户名
                  </label>
                  <div class="control has-icons-left">
                    <input 
                      class="input anime-input" 
                      v-model="username" 
                      placeholder="请输入用户名"
                      @blur="fetchSecurityQuestions"
                      required
                    />
                    <span class="icon is-small is-left">
                      <i>👤</i>
                    </span>
                  </div>
                  <p class="help">输入用户名后将自动加载密保问题</p>
                </div>

                <div v-if="questions.length > 0" class="security-section">
                  <p class="anime-label">
                    <span class="icon">🛡️</span> 密保问题
                  </p>
                  <div v-for="(q, idx) in questions" :key="idx" class="field">
                    <label class="label is-small">问题 {{ idx + 1 }}: {{ q }}</label>
                    <div class="control">
                      <input 
                        class="input is-small anime-input" 
                        v-model="answers[idx]" 
                        :placeholder="`请输入答案 ${idx + 1}`"
                        required
                      />
                    </div>
                  </div>
                </div>

                <div class="field" v-if="error">
                  <div class="notification is-danger is-light anime-notification">
                    <span class="icon">⚠️</span> {{ error }}
                  </div>
                </div>

                <div class="field">
                  <div class="control">
                    <button 
                      class="button is-primary is-fullwidth anime-button" 
                      type="submit"
                      :class="{ 'is-loading': loading }"
                      :disabled="loading || questions.length === 0"
                    >
                      <span class="icon">🔍</span>
                      <span>{{ loading ? '验证中...' : '验证密保' }}</span>
                    </button>
                  </div>
                </div>
              </form>

              <!-- 步骤2：修改密码 -->
              <form v-if="currentStep === 2" @submit.prevent="updatePassword">
                <div class="success-info">
                  <p class="anime-label">
                    <span class="icon">✅</span> 密保验证成功！
                  </p>
                  <p class="help">现在可以设置新密码了</p>
                </div>

                <div class="field">
                  <label class="label anime-label">
                    <span class="icon">🔒</span> 新密码
                  </label>
                  <div class="control has-icons-left">
                    <input 
                      class="input anime-input" 
                      type="password" 
                      v-model="newPassword" 
                      placeholder="请输入新密码（至少6位）"
                      required
                    />
                    <span class="icon is-small is-left">
                      <i>🔒</i>
                    </span>
                  </div>
                </div>

                <div class="field">
                  <label class="label anime-label">
                    <span class="icon">✅</span> 确认新密码
                  </label>
                  <div class="control has-icons-left">
                    <input 
                      class="input anime-input" 
                      type="password" 
                      v-model="confirmNewPassword" 
                      placeholder="再次输入新密码"
                      required
                    />
                    <span class="icon is-small is-left">
                      <i>✅</i>
                    </span>
                  </div>
                </div>

                <div class="field" v-if="error">
                  <div class="notification is-danger is-light anime-notification">
                    <span class="icon">⚠️</span> {{ error }}
                  </div>
                </div>

                <div class="field" v-if="success">
                  <div class="notification is-success is-light anime-notification">
                    <span class="icon">✅</span> {{ success }}
                  </div>
                </div>

                <div class="field">
                  <div class="control">
                    <button 
                      class="button is-primary is-fullwidth anime-button" 
                      type="submit"
                      :class="{ 'is-loading': loading }"
                      :disabled="loading"
                    >
                      <span class="icon">🚀</span>
                      <span>{{ loading ? '修改中...' : '修改密码' }}</span>
                    </button>
                  </div>
                </div>
              </form>

              <!-- 链接 -->
              <div class="links-section">
                <p class="has-text-centered">
                  <a class="anime-link" @click.prevent="$router.push('/login')">
                    <span class="icon">🔑</span> 返回登录
                  </a>
                  <span class="separator">|</span>
                  <a class="anime-link" @click.prevent="$router.push('/register')">
                    <span class="icon">📝</span> 注册新账号
                  </a>
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import axios from 'axios';
import bgImg from '@/assets/images/Moonshadow_CyberLeader.jpg';

export default {
  name: 'ChangePwd',
  data() {
    return {
      currentStep: 1, // 1-验证密保, 2-修改密码
      username: '',
      questions: [], // 后端返回的密保问题
      answers: ['', '', ''], // 用户输入的答案
      tempToken: '', // 验证成功后的临时token
      newPassword: '',
      confirmNewPassword: '',
      loading: false,
      error: '',
      success: '',
    };
  },
  computed: {
    bgStyle() {
      return {
        backgroundImage: `url(${bgImg})`,
        backgroundSize: 'cover',
        backgroundPosition: 'center',
      };
    }
  },
  methods: {
    async fetchSecurityQuestions() {
      if (!this.username.trim()) {
        return;
      }

      this.loading = true;
      this.error = '';
      
      try {
        // 对齐后端接口：POST /mySecurityIssues
        // 参数：username
        // 返回：Result<List<String>>
        const params = new URLSearchParams();
        params.append('username', this.username);

        const res = await axios.post('/api/mySecurityIssues', params);
        
        if (res.data && res.data.code === 0) {
          this.questions = res.data.data || [];
          this.answers = new Array(this.questions.length).fill('');
        } else {
          this.error = res.data?.message || '获取密保问题失败';
          this.questions = [];
        }
      } catch (err) {
        console.error('获取密保问题错误:', err);
        this.error = err.response?.data?.message || '获取密保问题失败，请检查用户名';
      } finally {
        this.loading = false;
      }
    },
    
    async verifySecurityIssues() {
      this.error = '';
      
      // 验证输入
      if (!this.username.trim()) {
        this.error = '请输入用户名';
        return;
      }
      
      for (let i = 0; i < this.questions.length; i++) {
        if (!this.answers[i].trim()) {
          this.error = `请填写密保问题 ${i + 1} 的答案`;
          return;
        }
      }

      this.loading = true;
      
      try {
        // 对齐后端接口：POST /verifySecurityIssues
        // 参数：username, SecurityIssues (List<R_SecurityIssue>)
        // 返回：Result<R_VerifySecurityIssuesDTO>
        const formData = new FormData();
        formData.append('username', this.username);
        
        // 构造密保问题列表（对齐 List<R_SecurityIssue>）
        // R_SecurityIssue字段：description（问题）, answer（答案）
        const securityIssuesJson = this.questions.map((q, idx) => ({
          description: q,
          answer: this.answers[idx]
        }));
        
        // 使用Blob发送JSON数组
        formData.append('SecurityIssues', new Blob([JSON.stringify(securityIssuesJson)], {
          type: 'application/json'
        }));

        const res = await axios.post('/api/verifySecurityIssues', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });
        
        if (res.data && res.data.code === 0) {
          const data = res.data.data; // R_VerifySecurityIssuesDTO
          if (data.verified) {
            // 验证成功，保存临时token
            this.tempToken = data.token || '';
            this.currentStep = 2; // 进入下一步：修改密码
            this.error = '';
          } else {
            this.error = '密保答案验证失败，请重新输入';
          }
        } else {
          this.error = res.data?.message || '密保验证失败';
        }
      } catch (err) {
        console.error('验证密保错误:', err);
        this.error = err.response?.data?.message || '验证失败，请稍后重试';
      } finally {
        this.loading = false;
      }
    },
    
    async updatePassword() {
      this.error = '';
      this.success = '';
      
      // 验证输入
      if (!this.newPassword) {
        this.error = '请输入新密码';
        return;
      }
      if (this.newPassword.length < 6) {
        this.error = '密码至少需要6位';
        return;
      }
      if (this.newPassword !== this.confirmNewPassword) {
        this.error = '两次输入的密码不一致';
        return;
      }

      this.loading = true;
      
      try {
        // 对齐后端接口：POST /updatePassword
        // 请求头：Authorization (临时token)
        // 参数：username, newPassword
        // 返回：Result<String>
        const params = new URLSearchParams();
        params.append('username', this.username);
        params.append('newPassword', this.newPassword);

        const res = await axios.post('/api/updatePassword', params, {
          headers: {
            'Authorization': this.tempToken // 使用临时token
          }
        });
        
        if (res.data && res.data.code === 0) {
          // 修改成功
          this.success = '密码修改成功！3秒后跳转到登录页...';
          this.error = '';
          
          // 3秒后跳转到登录页
          setTimeout(() => {
            this.$router.push('/login');
          }, 3000);
        } else {
          this.error = res.data?.message || '密码修改失败';
        }
      } catch (err) {
        console.error('修改密码错误:', err);
        this.error = err.response?.data?.message || '修改密码失败，请稍后重试';
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
.change-pwd {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 50;
  overflow-y: auto;
}

.overlay {
  width: 100%;
  min-height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.anime-pwd-box {
  background: rgba(255, 255, 255, 0.98) !important;
  border: 3px solid rgba(255, 105, 180, 0.3);
  border-radius: 24px;
  padding: 40px;
  box-shadow: 0 12px 48px rgba(147, 51, 234, 0.2);
  opacity: 0;
  transform: translateY(20px);
  animation: panelIn 600ms cubic-bezier(.22,.98,.28,1) 200ms forwards;
}

@keyframes panelIn {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.logo-section {
  margin-bottom: 30px;
  text-align: center;
}

.anime-title {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-weight: 900;
  font-size: 2.2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 8px;
}

.anime-title .icon {
  font-size: 2rem;
  animation: rotate 3s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.anime-subtitle {
  color: #9333ea;
  font-weight: 600;
  font-size: 1.1rem;
}

.anime-label {
  color: #6366f1;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 6px;
}

.anime-input {
  border: 2px solid rgba(147, 51, 234, 0.2);
  border-radius: 12px;
  padding: 12px 16px;
  transition: all 0.3s ease;
  font-size: 15px;
}

.anime-input:focus {
  border-color: #a78bfa;
  box-shadow: 0 0 0 4px rgba(167, 139, 250, 0.1);
}

.security-section {
  padding: 20px;
  background: linear-gradient(135deg, #667eea10 0%, #764ba210 100%);
  border-radius: 16px;
  margin: 20px 0;
  border: 2px solid rgba(147, 51, 234, 0.1);
}

.security-section .anime-label {
  font-size: 16px;
  margin-bottom: 16px;
}

.success-info {
  padding: 20px;
  background: linear-gradient(135deg, #10b98110 0%, #10b98115 100%);
  border-radius: 16px;
  margin-bottom: 20px;
  border: 2px solid rgba(16, 185, 129, 0.2);
}

.success-info .anime-label {
  color: #059669;
  font-size: 16px;
  margin-bottom: 8px;
}

.anime-button {
  border-radius: 12px;
  padding: 14px 28px;
  font-weight: 700;
  font-size: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
  margin-top: 12px;
}

.anime-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(147, 51, 234, 0.3);
}

.anime-button:active:not(:disabled) {
  transform: translateY(0);
}

.anime-notification {
  border-radius: 12px;
  border-left: 4px solid;
  display: flex;
  align-items: center;
  gap: 8px;
}

.links-section {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 2px solid rgba(147, 51, 234, 0.1);
}

.links-section p {
  color: #6b7280;
}

.separator {
  margin: 0 12px;
  color: #d1d5db;
}

.anime-link {
  color: #6366f1;
  font-weight: 600;
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.anime-link:hover {
  color: #a855f7;
  text-decoration: underline;
  transform: translateX(2px);
}

.has-icons-left .icon {
  display: flex;
  align-items: center;
  justify-content: center;
}

@media (max-width: 768px) {
  .anime-pwd-box {
    padding: 28px;
  }
  
  .anime-title {
    font-size: 1.8rem;
  }
  
  .security-section {
    padding: 16px;
  }
}
</style>

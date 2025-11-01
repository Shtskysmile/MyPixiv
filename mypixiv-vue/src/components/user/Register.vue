<template>
  <section class="register-hero" :style="bgStyle">
    <div class="overlay">
      <div class="container">
        <div class="columns is-centered">
          <div class="column is-5">
            <div class="box anime-register-box">
              <div class="logo-section">
                <h1 class="title has-text-centered anime-title">
                  <span class="icon is-large">🎨</span>
                  <span>加入 MyPixiv</span>
                </h1>
                <p class="subtitle has-text-centered anime-subtitle">开始你的创作之旅！</p>
              </div>
              
              <form @submit.prevent="handleRegister">
                <!-- 用户名 -->
                <div class="field">
                  <label class="label anime-label">
                    <span class="icon">👤</span> 用户名
                  </label>
                  <div class="control has-icons-left">
                    <input 
                      class="input anime-input" 
                      type="text" 
                      v-model="username" 
                      placeholder="设置你的用户名" 
                      required 
                    />
                    <span class="icon is-small is-left">
                      <i>👤</i>
                    </span>
                  </div>
                </div>

                <!-- 性别 -->
                <div class="field">
                  <label class="label anime-label">
                    <span class="icon">⚧️</span> 性别
                  </label>
                  <div class="control">
                    <div class="select is-fullwidth anime-select">
                      <select v-model="gender" required>
                        <option value="" disabled>请选择性别</option>
                        <option :value="1">男</option>
                        <option :value="0">女</option>
                      </select>
                    </div>
                  </div>
                </div>

                <!-- 密码 -->
                <div class="field">
                  <label class="label anime-label">
                    <span class="icon">🔒</span> 密码
                  </label>
                  <div class="control has-icons-left">
                    <input 
                      class="input anime-input" 
                      type="password" 
                      v-model="password" 
                      placeholder="设置密码（至少6位）" 
                      required 
                    />
                    <span class="icon is-small is-left">
                      <i>🔒</i>
                    </span>
                  </div>
                </div>

                <!-- 确认密码 -->
                <div class="field">
                  <label class="label anime-label">
                    <span class="icon">✅</span> 确认密码
                  </label>
                  <div class="control has-icons-left">
                    <input 
                      class="input anime-input" 
                      type="password" 
                      v-model="confirmPassword" 
                      placeholder="再次输入密码" 
                      required 
                    />
                    <span class="icon is-small is-left">
                      <i>✅</i>
                    </span>
                  </div>
                </div>

                <!-- 密保问题 -->
                <div class="security-section">
                  <p class="anime-label">
                    <span class="icon">🛡️</span> 设置密保问题（用于找回密码）
                  </p>
                  <div v-for="(q, idx) in securityQuestions" :key="idx" class="field">
                    <label class="label is-small">问题 {{ idx + 1 }}: {{ q.question }}</label>
                    <div class="control">
                      <input 
                        class="input is-small anime-input" 
                        type="text" 
                        v-model="securityAnswers[idx]" 
                        :placeholder="`请输入答案 ${idx + 1}`" 
                        required 
                      />
                    </div>
                  </div>
                </div>

                <!-- 头像（可选） -->
                <div class="field">
                  <label class="label anime-label">
                    <span class="icon">📸</span> 头像（可选）
                  </label>
                  <div class="file has-name is-fullwidth anime-file">
                    <label class="file-label">
                      <input 
                        class="file-input" 
                        type="file" 
                        accept="image/*" 
                        @change="onFileChange"
                      >
                      <span class="file-cta">
                        <span class="file-icon">
                          <i>📁</i>
                        </span>
                        <span class="file-label">选择图片</span>
                      </span>
                      <span class="file-name">
                        {{ avatarFileName || '未选择文件' }}
                      </span>
                    </label>
                  </div>
                  <div v-if="avatarPreview" class="avatar-preview">
                    <img :src="avatarPreview" alt="头像预览" />
                  </div>
                </div>

                <!-- 错误提示 -->
                <div class="field" v-if="error">
                  <div class="notification is-danger is-light anime-notification">
                    <span class="icon">⚠️</span> {{ error }}
                  </div>
                </div>

                <!-- 成功提示 -->
                <div class="field" v-if="success">
                  <div class="notification is-success is-light anime-notification">
                    <span class="icon">✅</span> {{ success }}
                  </div>
                </div>

                <!-- 注册按钮 -->
                <div class="field">
                  <div class="control">
                    <button 
                      class="button is-primary is-fullwidth anime-button" 
                      type="submit"
                      :class="{ 'is-loading': loading }"
                      :disabled="loading"
                    >
                      <span class="icon">🚀</span>
                      <span>{{ loading ? '注册中...' : '立即注册' }}</span>
                    </button>
                  </div>
                </div>

                <!-- 链接 -->
                <div class="links-section">
                  <p class="has-text-centered">
                    <span>已有账号？</span>
                    <a class="anime-link" @click.prevent="$router.push('/login')">
                      <span class="icon">🔑</span> 立即登录
                    </a>
                  </p>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import axios from 'axios';
import bg from '@/assets/images/Lincy_swimsuit_Background.jpg';

export default {
  name: 'Register',
  data() {
    return {
      username: '',
      password: '',
      confirmPassword: '',
      gender: '',
      securityQuestions: [
        { question: '你最喜欢的颜色是什么？' },
        { question: '你的出生地是哪里？' },
        { question: '你母亲的姓名是什么？' }
      ],
      securityAnswers: ['', '', ''],
      avatarFile: null,
      avatarFileName: '',
      avatarPreview: '',
      loading: false,
      error: '',
      success: '',
    };
  },
  computed: {
    bgStyle() {
      return {
        backgroundImage: `url(${bg})`,
        backgroundSize: 'cover',
        backgroundPosition: 'center'
      };
    }
  },
  methods: {
    onFileChange(e) {
      const file = e.target.files[0];
      if (file) {
        this.avatarFile = file;
        this.avatarFileName = file.name;
        
        // 创建预览
        const reader = new FileReader();
        reader.onload = (evt) => {
          this.avatarPreview = evt.target.result;
        };
        reader.readAsDataURL(file);
      }
    },
    
    async handleRegister() {
      this.error = '';
      this.success = '';
      
      // 验证输入
      if (!this.username.trim()) {
        this.error = '请输入用户名';
        return;
      }
      if (!this.password) {
        this.error = '请输入密码';
        return;
      }
      if (this.password.length < 6) {
        this.error = '密码至少需要6位';
        return;
      }
      if (this.password !== this.confirmPassword) {
        this.error = '两次输入的密码不一致';
        return;
      }
      if (this.gender === '') {
        this.error = '请选择性别';
        return;
      }
      
      // 验证密保问题
      for (let i = 0; i < 3; i++) {
        if (!this.securityAnswers[i].trim()) {
          this.error = `请填写密保问题 ${i + 1} 的答案`;
          return;
        }
      }

      this.loading = true;
      
      try {
        // 对齐后端接口：POST /register
        // 参数：username, password, gender, RSecurityIssues, avatar (可选)
        const formData = new FormData();
        formData.append('username', this.username);
        formData.append('password', this.password);
        formData.append('gender', this.gender);
        
        // 构造密保问题列表（对齐 List<R_SecurityIssue>）
        this.securityQuestions.forEach((q, idx) => {
          formData.append(`RSecurityIssues[${idx}].question`, q.question);
          formData.append(`RSecurityIssues[${idx}].answer`, this.securityAnswers[idx]);
        });
        
        // 添加头像（可选）
        if (this.avatarFile) {
          formData.append('avatar', this.avatarFile);
        }

        const res = await axios.post('/register', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });
        
        if (res.data && res.data.code === 0) {
          // 注册成功
          this.success = '注册成功！3秒后跳转到登录页...';
          
          // 清空表单
          this.username = '';
          this.password = '';
          this.confirmPassword = '';
          this.gender = '';
          this.securityAnswers = ['', '', ''];
          this.avatarFile = null;
          this.avatarFileName = '';
          this.avatarPreview = '';
          
          // 3秒后跳转到登录页
          setTimeout(() => {
            this.$router.push('/login');
          }, 3000);
        } else {
          // 注册失败
          this.error = res.data?.message || '注册失败，请稍后重试';
        }
      } catch (err) {
        console.error('注册错误:', err);
        this.error = err.response?.data?.message || '注册失败，请稍后重试';
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
.register-hero {
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
  padding: 20px 0;
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

.anime-register-box {
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
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
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

.anime-input, .anime-select select {
  border: 2px solid rgba(147, 51, 234, 0.2);
  border-radius: 12px;
  padding: 12px 16px;
  transition: all 0.3s ease;
  font-size: 15px;
}

.anime-input:focus, .anime-select select:focus {
  border-color: #a78bfa;
  box-shadow: 0 0 0 4px rgba(167, 139, 250, 0.1);
}

.anime-select {
  border-radius: 12px;
  overflow: hidden;
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

.anime-file .file-cta {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px 0 0 12px;
  color: white;
  font-weight: 600;
}

.anime-file .file-name {
  border: 2px solid rgba(147, 51, 234, 0.2);
  border-left: none;
  border-radius: 0 12px 12px 0;
}

.avatar-preview {
  margin-top: 12px;
  text-align: center;
}

.avatar-preview img {
  width: 128px;
  height: 128px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #a78bfa;
  box-shadow: 0 4px 12px rgba(147, 51, 234, 0.3);
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

.anime-link {
  color: #6366f1;
  font-weight: 600;
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  margin: 0 6px;
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
  .anime-register-box {
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

<template>
  <section class="register-hero" :style="bgStyle">
    <div class="overlay">
      <div class="container">
        <div class="columns is-centered">
          <div class="column is-10-desktop is-12-tablet">
            <div class="box anime-register-box compact-layout">
              <div class="logo-section compact-header">
                <h1 class="title is-4 has-text-centered anime-title">
                  <span class="icon">🎨</span>
                  <span>加入 MyPixiv</span>
                </h1>
              </div>
              
              <form @submit.prevent="handleRegister">
                <!-- 分栏布局：基本信息 + 密保问题 -->
                <div class="columns is-variable is-4">
                  <!-- 左栏：基本信息 -->
                  <div class="column is-half">
                    <h2 class="subtitle is-6 anime-label section-title">
                      <span class="icon">📝</span> 基本信息
                    </h2>
                    
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
                          autocomplete="username"
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
                          <select v-model="gender" required class="gender-select">
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
                          autocomplete="new-password"
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
                          autocomplete="new-password"
                          required 
                        />
                        <span class="icon is-small is-left">
                          <i>✅</i>
                        </span>
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
                  </div>

                  <!-- 右栏：密保问题 -->
                  <div class="column is-half">
                    <h2 class="subtitle is-6 anime-label section-title">
                      <span class="icon">🛡️</span> 密保问题
                    </h2>
                    
                    <div class="security-section">
                      <div v-for="(item, idx) in securityQuestions" :key="idx" class="field security-question-item">
                        <label class="label is-small">密保问题 {{ idx + 1 }}</label>
                        <div class="control">
                          <input 
                            class="input is-small anime-input" 
                            type="text" 
                            v-model="item.question" 
                            :placeholder="`请输入问题 ${idx + 1}（例如：你最喜欢的颜色是什么？）`" 
                            :autocomplete="`security-question-${idx + 1}`"
                            required 
                          />
                        </div>
                        <label class="label is-small" style="margin-top: 8px;">答案 {{ idx + 1 }}</label>
                        <div class="control">
                          <input 
                            class="input is-small anime-input" 
                            type="text" 
                            v-model="item.answer" 
                            :placeholder="`请输入答案 ${idx + 1}`" 
                            :autocomplete="`security-answer-${idx + 1}`"
                            required 
                          />
                        </div>
                      </div>
                    </div>
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
import request from '@/utils/request';
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
        { question: '', answer: '' },
        { question: '', answer: '' },
        { question: '', answer: '' }
      ],
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
        if (!this.securityQuestions[i].question.trim()) {
          this.error = `请填写密保问题 ${i + 1}`;
          return;
        }
        if (!this.securityQuestions[i].answer.trim()) {
          this.error = `请填写密保问题 ${i + 1} 的答案`;
          return;
        }
      }

      this.loading = true;
      
      try {
        // 对齐后端接口：POST /register
        // 参数：username, password, gender, SecurityIssues (List<R_SecurityIssue>), avatar (可选)
        const formData = new FormData();
        formData.append('username', this.username);
        formData.append('password', this.password);
        formData.append('gender', this.gender);
        
        // 构造密保问题列表（对齐 List<R_SecurityIssue>）
        // 后端参数名：SecurityIssues
        // R_SecurityIssue字段：description（问题）, answer（答案）
        const securityIssuesJson = this.securityQuestions.map(q => ({
          description: q.question,
          answer: q.answer
        }));
        
        // 使用Blob发送JSON数组
        formData.append('SecurityIssues', new Blob([JSON.stringify(securityIssuesJson)], {
          type: 'application/json'
        }));
        
        // 添加头像（可选）
        if (this.avatarFile) {
          formData.append('avatar', this.avatarFile);
        }

        // 发送FormData时，不设置任何headers，让axios自动处理（包括boundary）
        const res = await request.post('/register', formData);
        
        if (res.data && res.data.code === 0) {
          // 注册成功，跳转到登录页面让用户重新登录
          this.success = `注册成功！欢迎加入 MyPixiv，${this.username}！2秒后跳转到登录页...`;
          
          // 清空表单
          this.username = '';
          this.password = '';
          this.confirmPassword = '';
          this.gender = '';
          this.securityQuestions = [
            { question: '', answer: '' },
            { question: '', answer: '' },
            { question: '', answer: '' }
          ];
          this.avatarFile = null;
          this.avatarFileName = '';
          this.avatarPreview = '';
          
          // 2秒后跳转到登录页
          setTimeout(() => {
            this.$router.push('/login');
          }, 2000);
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
  border-radius: 20px;
  padding: 24px 32px;
  box-shadow: 0 12px 48px rgba(147, 51, 234, 0.2);
  opacity: 0;
  transform: translateY(20px);
  animation: panelIn 600ms cubic-bezier(.22,.98,.28,1) 200ms forwards;
}

.compact-layout {
  max-width: 100%;
}

@keyframes panelIn {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.logo-section {
  margin-bottom: 16px;
  text-align: center;
}

.compact-header {
  margin-bottom: 12px !important;
}

.anime-title {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-weight: 900;
  font-size: 1.6rem !important;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 0 !important;
}

.anime-title .icon {
  font-size: 1.5rem;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

.section-title {
  font-size: 1rem !important;
  margin-bottom: 12px !important;
  font-weight: 700;
  color: #6366f1;
}

.anime-label {
  color: #6366f1;
  font-weight: 600;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 4px;
}

.field {
  margin-bottom: 12px !important;
}

.anime-input {
  border: 2px solid rgba(147, 51, 234, 0.2);
  border-radius: 10px;
  padding: 8px 12px;
  transition: all 0.3s ease;
  font-size: 14px;
  width: 100%;
  height: 38px;
}

.anime-input:focus {
  border-color: #a78bfa;
  box-shadow: 0 0 0 4px rgba(167, 139, 250, 0.1);
  outline: none;
}

.anime-select {
  border-radius: 12px;
  overflow: visible !important;
  width: 100%;
  position: relative;
  display: block;
}

/* 覆盖Bulma的select样式 */
.field .control .select.anime-select {
  width: 100%;
  display: block;
}

.field .control .select.anime-select::after {
  display: none !important;
}

.anime-select select,
.gender-select {
  width: 100% !important;
  min-width: 100% !important;
  max-width: 100% !important;
  min-height: 38px !important;
  height: 38px !important;
  border: 2px solid rgba(147, 51, 234, 0.2) !important;
  border-radius: 10px !important;
  padding: 8px 40px 8px 12px !important;
  margin: 0 !important;
  background-color: white !important;
  font-size: 14px !important;
  line-height: 1.5 !important;
  appearance: none !important;
  -webkit-appearance: none !important;
  -moz-appearance: none !important;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%236366f1' d='M6 9L1 4h10z'/%3E%3C/svg%3E") !important;
  background-repeat: no-repeat !important;
  background-position: right 12px center !important;
  background-size: 12px 12px !important;
  cursor: pointer;
  transition: all 0.3s ease;
  display: block !important;
  box-sizing: border-box !important;
  white-space: nowrap;
  overflow: visible;
}

.anime-select select option,
.gender-select option {
  padding: 12px 16px !important;
  line-height: 1.5 !important;
  min-height: 44px !important;
  height: auto !important;
  font-size: 15px !important;
  display: block;
  white-space: normal;
  overflow: visible;
}

.anime-select select:focus,
.gender-select:focus {
  border-color: #a78bfa;
  box-shadow: 0 0 0 4px rgba(167, 139, 250, 0.1);
  outline: none;
}

.anime-select select:hover,
.gender-select:hover {
  border-color: #a78bfa;
}

.security-section {
  padding: 0;
  background: transparent;
  border-radius: 0;
  margin: 0;
  border: none;
}

.security-question-item {
  padding: 12px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 10px;
  margin-bottom: 10px;
  border: 1px solid rgba(147, 51, 234, 0.15);
  transition: all 0.3s ease;
}

.security-question-item:hover {
  background: rgba(255, 255, 255, 0.7);
  border-color: rgba(147, 51, 234, 0.25);
  box-shadow: 0 2px 8px rgba(147, 51, 234, 0.1);
}

.security-question-item:last-child {
  margin-bottom: 0;
}

.security-question-item .label.is-small {
  color: #6366f1;
  font-weight: 600;
  font-size: 0.85rem;
  margin-bottom: 4px;
}

.security-question-item .field {
  margin-bottom: 8px !important;
}

.security-question-item .field:last-child {
  margin-bottom: 0 !important;
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
  margin-top: 10px;
  text-align: center;
}

.avatar-preview img {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #a78bfa;
  box-shadow: 0 4px 12px rgba(147, 51, 234, 0.3);
}

.anime-button {
  border-radius: 10px;
  padding: 10px 24px;
  font-weight: 700;
  font-size: 15px;
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
  border-radius: 10px;
  border-left: 4px solid;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  padding: 10px 12px;
}

.links-section {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 2px solid rgba(147, 51, 234, 0.1);
}

.links-section p {
  color: #6b7280;
  font-size: 14px;
}

.anime-link {
  color: #6366f1;
  font-weight: 600;
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  margin: 0 6px;
  font-size: 14px;
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
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .anime-register-box {
    padding: 20px;
  }
  
  .anime-title {
    font-size: 1.4rem !important;
  }
  
  .columns {
    display: block !important;
  }
  
  .column {
    width: 100% !important;
    padding: 0 !important;
  }
  
  .column.is-half:first-child {
    margin-bottom: 16px;
  }
}
</style>

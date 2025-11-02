<template>
  <section class="login-hero" :style="bgStyle">
    <div class="overlay">
      <div class="container">
        <div class="columns is-centered">
          <div class="column is-4">
            <div class="box anime-login-box">
              <div class="logo-section">
                <h1 class="title has-text-centered anime-title">
                  <span class="icon is-large">✨</span>
                  <span>MyPixiv</span>
                </h1>
                <p class="subtitle has-text-centered anime-subtitle">欢迎回来！</p>
              </div>
              
              <form @submit.prevent="handleLogin">
                <div class="field">
                  <label class="label anime-label">
                    <span class="icon">👤</span> 用户名
                  </label>
                  <div class="control has-icons-left">
                    <input
                      class="input anime-input"
                      type="text"
                      v-model="username"
                      placeholder="请输入用户名"
                      required
                    />
                    <span class="icon is-small is-left">
                      <i>👤</i>
                    </span>
                  </div>
                </div>

                <div class="field">
                  <label class="label anime-label">
                    <span class="icon">🔒</span> 密码
                  </label>
                  <div class="control has-icons-left">
                    <input
                      class="input anime-input"
                      type="password"
                      v-model="password"
                      placeholder="请输入密码"
                      required
                    />
                    <span class="icon is-small is-left">
                      <i>🔒</i>
                    </span>
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
                      :disabled="loading"
                    >
                      <span class="icon">🚀</span>
                      <span>{{ loading ? '登录中...' : '登录' }}</span>
                    </button>
                  </div>
                </div>

                <div class="links-section">
                  <p class="has-text-centered">
                    <a class="anime-link" @click.prevent="$router.push('/changepwd')">
                      <span class="icon">🔑</span> 忘记密码？
                    </a>
                  </p>
                  <p class="has-text-centered">
                    <span>还没有账号？</span>
                    <a class="anime-link" @click.prevent="$router.push('/register')">
                      <span class="icon">📝</span> 立即注册
                    </a>
                  </p>
                  <p class="has-text-centered">
                    <a class="anime-link" @click.prevent="$router.push('/index')">
                      <span class="icon">🏠</span> 跳过，查看首页
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
import bg from "@/assets/images/Lincy_swimsuit_Background.jpg";

export default {
  name: 'Login',
  data() {
    return {
      username: '',
      password: '',
      loading: false,
      error: '',
    };
  },
  computed: {
    bgStyle() {
      return {
        backgroundImage: `url(${bg})`,
        backgroundSize: "cover",
        backgroundPosition: "center",
      };
    },
  },
  methods: {
    async handleLogin() {
      this.error = '';
      
      // 验证输入
      if (!this.username.trim()) {
        this.error = '请输入用户名';
        return;
      }
      if (!this.password) {
        this.error = '请输入密码';
        return;
      }

      this.loading = true;
      
      try {
        // 对齐后端接口：POST /login
        // 参数：username, password
        // 返回：Result<R_LoginDTO>
        const params = new URLSearchParams();
        params.append('username', this.username);
        params.append('password', this.password);

        const res = await axios.post('/api/login', params);
        
        if (res.data && res.data.code === 0) {
          // 登录成功
          const data = res.data.data; // R_LoginDTO
          const { user, token } = data;
          
          // 保存用户信息和 token
          localStorage.setItem('token', token);
          localStorage.setItem('userId', user.userId);
          localStorage.setItem('username', user.username);
          localStorage.setItem('userAvatar', user.avatar || '');
          localStorage.setItem('userRole', user.role);
          
          // 显示成功提示
          alert(`欢迎回来，${user.username}！`);
          
          // 跳转到首页
          this.$router.push('/index');
        } else {
          // 登录失败
          this.error = res.data?.message || '登录失败，请检查用户名和密码';
        }
      } catch (err) {
        console.error('登录错误:', err);
        this.error = err.response?.data?.message || '登录失败，请稍后重试';
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<style scoped>
.login-hero {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 50;
}

.overlay {
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
}

.anime-login-box {
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
  font-size: 2.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 8px;
}

.anime-title .icon {
  font-size: 2rem;
  animation: sparkle 2s ease-in-out infinite;
}

@keyframes sparkle {
  0%, 100% { transform: scale(1) rotate(0deg); }
  50% { transform: scale(1.2) rotate(180deg); }
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
  border-left: 4px solid #f14668;
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
  margin: 8px 0;
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
  .anime-login-box {
    padding: 28px;
  }
  
  .anime-title {
    font-size: 2rem;
  }
}
</style>

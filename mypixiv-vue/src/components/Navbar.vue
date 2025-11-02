<template>
  <nav
    class="navbar sticky-navbar"
    role="navigation"
    aria-label="main navigation"
  >
    <!-- when used inside pages that already have a container (e.g. User), set noContainer to true to avoid nested containers -->
    <template v-if="noContainer">
      <div class="navbar-inner">
        <div class="navbar-brand">
          <a class="navbar-item logo-item" href="/">
            <img
              class="site-logo"
              src="@/assets/images/Pixiv_Icon.svg"
              alt="logo"
            />
            <span class="logo-text">MyPixiv</span>
          </a>

          <a
            role="button"
            class="navbar-burger"
            :class="{ 'is-active': isBurgerActive }"
            aria-label="menu"
            :aria-expanded="isBurgerActive"
            data-target="navbarBasicExample"
            @click="toggleBurger"
          >
            <span aria-hidden="true"></span>
            <span aria-hidden="true"></span>
            <span aria-hidden="true"></span>
          </a>
        </div>

        <div id="navbarBasicExample" class="navbar-menu" :class="{ 'is-active': isBurgerActive }">
          <div class="navbar-start"></div>

          <div class="navbar-center">
            <div class="navbar-item nav-search">
              <form class="search-form" @submit.prevent="onSearch">
                <div class="search-input-wrapper">
                  <span class="search-icon">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <circle cx="11" cy="11" r="8"></circle>
                      <path d="m21 21-4.35-4.35"></path>
                    </svg>
                  </span>
                  <input 
                    class="search-input" 
                    type="text" 
                    v-model="search" 
                    placeholder="搜索插画、作者..." 
                    @keyup.enter="onSearch"
                  >
                  <button class="search-button" type="submit">
                    搜索
                  </button>
                </div>
              </form>
            </div>
          </div>

          <div class="navbar-end">
            <div class="navbar-item">
              <!-- 已登录状态 -->
              <div v-if="isLoggedIn" class="user-menu">
                <router-link :to="`/user/${userId}`" class="user-profile-link">
                  <img 
                    v-if="avatarUrl" 
                    :src="avatarUrl" 
                    :alt="username"
                    class="user-avatar"
                  />
                  <span v-else class="user-avatar-placeholder">
                    {{ username ? username.charAt(0).toUpperCase() : 'U' }}
                  </span>
                  <span class="username">{{ username }}</span>
                </router-link>
                <button class="button btn-logout" @click="handleLogout">
                  注销
                </button>
              </div>
              <!-- 未登录状态 -->
              <div v-else class="buttons">
                <router-link class="button btn-register" to="/register">
                  <strong>注册</strong>
                </router-link>
                <router-link class="button btn-login" to="/login">登录</router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>

    <template v-else>
      <div class="container">
        <div class="navbar-brand">
          <a class="navbar-item logo-item" href="/">
            <img
              class="site-logo"
              src="@/assets/images/Pixiv_Icon.svg"
              alt="logo"
            />
            <span class="logo-text">MyPixiv</span>
          </a>

          <a
            role="button"
            class="navbar-burger"
            :class="{ 'is-active': isBurgerActive }"
            aria-label="menu"
            :aria-expanded="isBurgerActive"
            data-target="navbarBasicExample"
            @click="toggleBurger"
          >
            <span aria-hidden="true"></span>
            <span aria-hidden="true"></span>
            <span aria-hidden="true"></span>
          </a>
        </div>

        <div id="navbarBasicExample" class="navbar-menu" :class="{ 'is-active': isBurgerActive }">
          <div class="navbar-start"></div>

          <div class="navbar-center">
            <div class="navbar-item nav-search">
              <form class="search-form" @submit.prevent="onSearch">
                <div class="search-input-wrapper">
                  <span class="search-icon">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <circle cx="11" cy="11" r="8"></circle>
                      <path d="m21 21-4.35-4.35"></path>
                    </svg>
                  </span>
                  <input 
                    class="search-input" 
                    type="text" 
                    v-model="search" 
                    placeholder="搜索插画、作者..." 
                    @keyup.enter="onSearch"
                  >
                  <button class="search-button" type="submit">
                    搜索
                  </button>
                </div>
              </form>
            </div>
          </div>

          <div class="navbar-end">
            <div class="navbar-item">
              <!-- 已登录状态 -->
              <div v-if="isLoggedIn" class="user-menu">
                <router-link :to="`/user/${userId}`" class="user-profile-link">
                  <img 
                    v-if="avatarUrl" 
                    :src="avatarUrl" 
                    :alt="username"
                    class="user-avatar"
                  />
                  <span v-else class="user-avatar-placeholder">
                    {{ username ? username.charAt(0).toUpperCase() : 'U' }}
                  </span>
                  <span class="username">{{ username }}</span>
                </router-link>
                <button class="button btn-logout" @click="handleLogout">
                  注销
                </button>
              </div>
              <!-- 未登录状态 -->
              <div v-else class="buttons">
                <router-link class="button btn-register" to="/register">
                  <strong>注册</strong>
                </router-link>
                <router-link class="button btn-login" to="/login">登录</router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>
  </nav>
</template>

<script>
export default {
  name: 'AppNavbar',
  props: {
    noContainer: { type: Boolean, default: false }
  },
  data() {
    return {
      search: '',
      isBurgerActive: false,
      isLoggedIn: false,
      username: '',
      userId: '',
      userAvatar: '',
      avatarUrl: '' // 存储加载后的头像URL
    };
  },
  mounted() {
    this.checkLoginStatus();
  },
  methods: {
    async checkLoginStatus() {
      // 从 localStorage 获取用户信息
      const token = localStorage.getItem('token');
      const username = localStorage.getItem('username');
      const userId = localStorage.getItem('userId');
      const userAvatar = localStorage.getItem('userAvatar');
      console.log(userAvatar);
      
      if (token && username && userId) {
        this.isLoggedIn = true;
        this.username = username;
        this.userId = userId;
        this.userAvatar = userAvatar || '';
        
        // 直接拼接头像 URL（后端已配置静态资源映射 /files/**）
        if (userAvatar) {
          // userAvatar 格式如: /files/userId/avatar/xxx.jpg
          // 直接拼接基础 URL 即可访问
          const baseURL = process.env.VUE_APP_API_BASE_URL;
          this.avatarUrl = `${baseURL}${userAvatar}`;
          console.log('✅ 头像 URL 已设置:', this.avatarUrl);
        }
      } else {
        this.isLoggedIn = false;
      }
    },
    handleLogout() {
      // 清除本地存储
      localStorage.removeItem('token');
      localStorage.removeItem('username');
      localStorage.removeItem('userId');
      localStorage.removeItem('userAvatar');
      
      // 更新状态
      this.isLoggedIn = false;
      this.username = '';
      this.userId = '';
      this.userAvatar = '';
      
      // 跳转到首页
      this.$router.push('/').catch(err => err);
      
      // 关闭移动端菜单
      this.isBurgerActive = false;
    },
    toggleBurger() {
      this.isBurgerActive = !this.isBurgerActive;
    },
    onSearch() {
      const q = (this.search || '').trim();

      // 构建目标 route 对象（确保 query 值为字符串以便比较）
      const target = q
        ? { path: '/index', query: { search: String(q), page: '1' } }
        : { path: '/index', query: { page: '1' } };

      // 规范化当前 route 的 query 为字符串值并排序键
      const currPath = this.$route.path;
      const currQuery = {};
      Object.keys(this.$route.query || {})
        .sort()
        .forEach((k) => {
          currQuery[k] = String(this.$route.query[k]);
        });

      // 规范化目标 query 并比较是否与当前 route 相同
      const targetQuery = {};
      Object.keys(target.query || {})
        .sort()
        .forEach((k) => {
          targetQuery[k] = String(target.query[k]);
        });

      if (currPath === target.path && JSON.stringify(currQuery) === JSON.stringify(targetQuery)) {
        // 已经在相同位置，避免重复导航
        return;
      }

      // 执行导航并捕获重复导航错误（兼容 Vue Router v3/v4）
      this.$router.push(target).catch((err) => {
        // 某些 Vue Router 版本会抛出 NavigationDuplicated，这里静默处理
        // 其他错误可以选择记录或上报；目前无需中断用户操作
        return err;
      });
      
      // 关闭移动端菜单
      this.isBurgerActive = false;
    }
  }
};
</script>

<style scoped>
@import "../assets/css/sticky-navbar.css";

.sticky-navbar {
  background: linear-gradient(135deg, #0096ff 0%, #1e6fff 50%, #0052d4 100%);
  color: #ffffff;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.sticky-navbar:hover {
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.15);
}

.sticky-navbar .navbar-item,
.sticky-navbar .navbar-link,
.sticky-navbar .navbar-burger span {
  color: #ffffff;
}

/* navbar-inner 用于 noContainer 模式，提供与 container 相同的边距 */
.navbar-inner {
  max-width: 1344px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: stretch;
  min-height: 3.25rem;
  width: 100%;
}

@media screen and (min-width: 1024px) {
  .navbar-inner {
    padding: 0 32px;
  }
}

@media screen and (min-width: 1216px) {
  .navbar-inner {
    max-width: 1152px;
  }
}

@media screen and (min-width: 1408px) {
  .navbar-inner {
    max-width: 1344px;
  }
}

/* Logo 样式 */
.logo-item { 
  padding: 8px 14px; 
  display: flex; 
  align-items: center; 
  gap: 12px;
  transition: transform 0.3s ease;
}

.logo-item:hover {
  transform: scale(1.05);
}

.logo-item .site-logo { 
  height: 48px; 
  width: auto; 
  display: block;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.logo-text {
  font-size: 24px;
  font-weight: 700;
  letter-spacing: -0.5px;
  background: linear-gradient(135deg, #ffffff 0%, #e0f0ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 汉堡菜单 */
.navbar-burger {
  color: #ffffff;
  transition: transform 0.3s ease;
}

.navbar-burger:hover {
  background-color: rgba(255, 255, 255, 0.1);
  transform: rotate(90deg);
}

.navbar-burger span {
  height: 2px;
  width: 20px;
}

/* 居中搜索框布局 */
.sticky-navbar .navbar-menu {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: transparent;
}

.sticky-navbar .navbar-center {
  flex: 1 1 auto;
  display: flex;
  justify-content: center;
}

.nav-search {
  flex: 0 1 720px;
}

/* 搜索框样式 */
.search-form {
  width: 100%;
}

.search-input-wrapper {
  display: flex;
  align-items: center;
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 50px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  max-width: 720px;
  width: 100%;
}

.search-input-wrapper:hover {
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
  background-color: #ffffff;
}

.search-input-wrapper:focus-within {
  box-shadow: 0 8px 24px rgba(30, 110, 255, 0.3);
  background-color: #ffffff;
}

.search-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 16px;
  color: #666;
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  padding: 14px 8px;
  font-size: 16px;
  background: transparent;
  color: #333;
}

.search-input::placeholder {
  color: #999;
}

.search-button {
  background: linear-gradient(135deg, #0096ff 0%, #1e6fff 100%);
  color: #ffffff;
  border: none;
  padding: 14px 28px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.search-button:hover {
  background: linear-gradient(135deg, #0080e6 0%, #1a5ce6 100%);
  transform: translateX(-2px);
}

.search-button:active {
  transform: translateX(-2px) scale(0.98);
}

/* 按钮样式 */
.buttons {
  display: flex;
  gap: 12px;
}

.btn-register {
  background-color: #ffffff !important;
  color: #1e6fff !important;
  border: none !important;
  border-radius: 50px !important;
  padding: 10px 24px !important;
  font-size: 15px !important;
  font-weight: 600 !important;
  transition: all 0.3s ease !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.btn-register:hover {
  background-color: #f0f8ff !important;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.btn-login {
  background-color: transparent !important;
  color: #ffffff !important;
  border: 2px solid rgba(255, 255, 255, 0.8) !important;
  border-radius: 50px !important;
  padding: 10px 24px !important;
  font-size: 15px !important;
  font-weight: 600 !important;
  transition: all 0.3s ease !important;
}

.btn-login:hover {
  background-color: rgba(255, 255, 255, 0.15) !important;
  border-color: #ffffff !important;
  transform: translateY(-2px);
}

/* 给右侧按钮组增加一些右侧留白，避免贴边 */
.sticky-navbar .navbar-end {
  margin-right: 12px;
}

/* 用户菜单样式 */
.user-menu {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-profile-link {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 16px;
  border-radius: 50px;
  background-color: rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
  color: #ffffff !important;
  text-decoration: none;
}

.user-profile-link:hover {
  background-color: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-avatar-placeholder {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 700;
  color: #ffffff;
  border: 2px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.username {
  font-size: 15px;
  font-weight: 600;
  color: #ffffff;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.btn-logout {
  background-color: rgba(255, 255, 255, 0.95) !important;
  color: #ff4757 !important;
  border: none !important;
  border-radius: 50px !important;
  padding: 10px 24px !important;
  font-size: 15px !important;
  font-weight: 600 !important;
  transition: all 0.3s ease !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
}

.btn-logout:hover {
  background-color: #ffffff !important;
  color: #ee2e3d !important;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 71, 87, 0.3);
}

/* 移动端样式 */
@media (max-width: 1024px) {
  .logo-item { padding: 6px 10px; }
  .logo-item .site-logo { height: 40px; }
  .logo-text { font-size: 20px; }
  
  .sticky-navbar .navbar-menu {
    background: linear-gradient(135deg, #0096ff 0%, #1e6fff 50%, #0052d4 100%);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
  
  .sticky-navbar .navbar-center {
    order: 2;
    padding: 12px 16px;
  }
  
  .search-input-wrapper {
    max-width: 100%;
  }
  
  .navbar-end {
    padding: 12px 16px;
  }
  
  .buttons {
    width: 100%;
    justify-content: center;
  }
  
  .btn-register,
  .btn-login {
    flex: 1;
  }
  
  /* 移动端用户菜单 */
  .user-menu {
    width: 100%;
    flex-direction: column;
    gap: 12px;
  }
  
  .user-profile-link {
    width: 100%;
    justify-content: center;
  }
  
  .btn-logout {
    width: 100% !important;
  }
}

@media (max-width: 768px) {
  .logo-item .site-logo { height: 36px; }
  .logo-text { font-size: 18px; }
  
  .search-button {
    padding: 14px 20px;
    font-size: 14px;
  }
}
</style>

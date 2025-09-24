<template>
  <nav
    class="navbar sticky-navbar"
    role="navigation"
    aria-label="main navigation"
  >
    <div class="navbar-brand">
      <a class="navbar-item" href="/">
        <img
          src="@/assets/images/Pixiv_Icon.svg"
          alt="logo"
          style="height: 48px"
        />
      </a>

      <a
        role="button"
        class="navbar-burger"
        aria-label="menu"
        aria-expanded="false"
        data-target="navbarBasicExample"
      >
        <span aria-hidden="true"></span>
        <span aria-hidden="true"></span>
        <span aria-hidden="true"></span>
        <span aria-hidden="true"></span>
      </a>
    </div>

    <div id="navbarBasicExample" class="navbar-menu">
      <div class="navbar-start"></div>

      <div class="navbar-center">
        <div class="navbar-item nav-search">
          <form class="field has-addons" @submit.prevent="onSearch">
            <div class="control is-expanded">
              <input class="input" type="text" v-model="search" placeholder="搜索插画、作者..." @keyup.enter="onSearch">
            </div>
            <div class="control">
              <button class="button is-info" type="submit">
                🔍
              </button>
            </div>
          </form>
        </div>
      </div>

      <div class="navbar-end">
        <div class="navbar-item">
          <div class="buttons">
            <router-link class="button is-primary" to="/register">
              <strong>注册</strong>
            </router-link>
            <router-link class="button is-light" to="/login">登录</router-link>
          </div>
        </div>
      </div>
    </div>
  </nav>
</template>

<script>
export default {
  name: 'AppNavbar',
  data() {
    return {
      search: ''
    };
  },
  methods: {
    onSearch() {
      const q = (this.search || '').trim();
      if (q) {
        this.$router.push({ path: '/index', query: { search: q, page: 1 } });
      } else {
        this.$router.push({ path: '/index', query: { page: 1 } });
      }
    }
  }
};
</script>

<style scoped>
@import "../assets/css/sticky-navbar.css";

.sticky-navbar {
  background-color: #1e6fff;
  color: #ffffff;
}

.sticky-navbar .navbar-item,
.sticky-navbar .navbar-link,
.sticky-navbar .navbar-burger span {
  color: #ffffff;
}

/* 居中搜索框布局 */
.sticky-navbar .navbar-menu {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.sticky-navbar .navbar-center {
  flex: 1 1 auto;
  display: flex;
  justify-content: center;
}

.nav-search {
  flex: 0 1 720px;
}

.nav-search .field.has-addons {
  max-width: 880px;
  width: 720px;
}

.nav-search .input {
  font-size: 16px;
  padding: 10px 12px;
  width: 100%;
  box-sizing: border-box;
}

/* 移动端样式 */
@media (max-width: 768px) {
  .nav-search .field.has-addons {
    max-width: 100%;
  }
  .sticky-navbar .navbar-menu {
    flex-direction: column;
    align-items: stretch;
  }
  .sticky-navbar .navbar-center {
    order: 2;
    padding: 8px 12px;
  }
}

.sticky-navbar .button.is-primary {
  background-color: #ffffff;
  color: #1e6fff;
  border: none;
}

.sticky-navbar .button.is-light {
  background-color: transparent;
  color: #ffffff;
  border: 1px solid rgba(255,255,255,0.2);
}

/* 给右侧按钮组增加一些右侧留白，避免贴边 */
.sticky-navbar .navbar-end {
  margin-right: 12px;
}
</style>

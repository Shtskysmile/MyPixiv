<template>
  <aside class="anime-sidebar">
    <!-- 首页分类 -->
    <div class="menu-section anime-menu-box">
      <p class="menu-label anime-menu-label">
        <span class="icon">🏠</span> 首页
      </p>
      <ul class="menu-list">
        <li>
          <router-link to="/index" class="menu-item" active-class="is-active" exact>
            <span class="icon">🎨</span>
            <span>全部作品</span>
            <span class="badge">HOT</span>
          </router-link>
        </li>
        <li>
          <router-link to="/illustrations" class="menu-item" active-class="is-active">
            <span class="icon">🖼️</span>
            <span>插画</span>
          </router-link>
        </li>
        <li>
          <router-link to="/mangas" class="menu-item" active-class="is-active">
            <span class="icon">📚</span>
            <span>漫画</span>
          </router-link>
        </li>
      </ul>
    </div>

    <!-- 发现分类 -->
    <div class="menu-section anime-menu-box">
      <p class="menu-label anime-menu-label">
        <span class="icon">🔍</span> 发现
      </p>
      <ul class="menu-list">
        <li>
          <router-link to="/ranking" class="menu-item" active-class="is-active">
            <span class="icon">🏆</span>
            <span>排行榜</span>
            <span class="badge">NEW</span>
          </router-link>
        </li>
        <li>
          <a class="menu-item" @click="handleSearchClick">
            <span class="icon">🔎</span>
            <span>搜索</span>
            <span class="badge coming-soon">即将开放</span>
          </a>
        </li>
        <li>
          <a class="menu-item" @click="handleTagsClick">
            <span class="icon">🏷️</span>
            <span>标签</span>
            <span class="badge coming-soon">即将开放</span>
          </a>
        </li>
      </ul>
    </div>

    <!-- 个人分类 -->
    <div class="menu-section anime-menu-box">
      <p class="menu-label anime-menu-label">
        <span class="icon">👤</span> 个人
      </p>
      <ul class="menu-list">
        <li>
          <router-link to="/user" class="menu-item" active-class="is-active">
            <span class="icon">📋</span>
            <span>个人主页</span>
          </router-link>
        </li>
        <li>
          <a class="menu-item" @click="goToUserSection('favorites')">
            <span class="icon">⭐</span>
            <span>我的收藏</span>
          </a>
        </li>
        <li>
          <a class="menu-item" @click="goToUserSection('likes')">
            <span class="icon">❤️</span>
            <span>我的点赞</span>
          </a>
        </li>
        <li>
          <a class="menu-item" @click="goToUserSection('followers')">
            <span class="icon">👥</span>
            <span>我的关注</span>
          </a>
        </li>
      </ul>
    </div>

    <!-- 装饰元素 -->
    <div class="sidebar-decoration">
      <div class="deco-star">✨</div>
      <div class="deco-heart">💖</div>
      <div class="deco-sparkle">⭐</div>
    </div>
  </aside>
</template>

<script>
export default {
  name: 'Sidebar',
  methods: {
    goToUserSection(section) {
      // 跳转到用户页面并打开指定标签
      this.$router.push({ path: '/user', query: { tab: section } });
    },
    handleRankingClick() {
      alert('排行榜功能即将开放，敬请期待！🏆');
    },
    handleSearchClick() {
      alert('搜索功能即将开放，敬请期待！🔎');
    },
    handleTagsClick() {
      alert('标签功能即将开放，敬请期待！🏷️');
    }
  }
};
</script>

<style scoped>
.anime-sidebar {
  position: sticky;
  top: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.menu-section {
  position: relative;
  overflow: visible;
}

.anime-menu-box {
  background: rgba(255, 255, 255, 0.98);
  border: 2px solid rgba(255, 105, 180, 0.2);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 16px rgba(147, 51, 234, 0.1);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.anime-menu-box::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.anime-menu-box:hover {
  box-shadow: 0 8px 24px rgba(147, 51, 234, 0.15);
  transform: translateY(-2px);
}

.anime-menu-box:hover::before {
  opacity: 1;
}

.anime-menu-label {
  color: #6366f1;
  font-weight: 800;
  font-size: 15px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: linear-gradient(135deg, #667eea10 0%, #764ba210 100%);
  border-radius: 10px;
  margin-bottom: 12px;
  border-left: 4px solid #a78bfa;
}

.menu-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.menu-list li {
  margin: 0;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-radius: 10px;
  color: #6b7280;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.3s ease;
  cursor: pointer;
  text-decoration: none;
  margin: 4px 0;
  position: relative;
}

.menu-item:hover {
  background: linear-gradient(135deg, #667eea08 0%, #764ba208 100%);
  color: #6366f1;
  transform: translateX(4px);
  padding-left: 20px;
}

.menu-item.is-active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(147, 51, 234, 0.3);
  font-weight: 800;
}

.menu-item.is-active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 60%;
  background: white;
  border-radius: 0 4px 4px 0;
}

.menu-item .icon {
  font-size: 18px;
  transition: transform 0.3s ease;
}

.menu-item:hover .icon {
  transform: scale(1.2);
}

.menu-item.is-active .icon {
  animation: bounce 0.6s ease;
}

@keyframes bounce {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.2); }
}

.badge {
  margin-left: auto;
  padding: 3px 8px;
  border-radius: 10px;
  font-size: 10px;
  font-weight: 800;
  background: linear-gradient(135deg, #f87171 0%, #ef4444 100%);
  color: white;
  text-transform: uppercase;
  box-shadow: 0 2px 4px rgba(239, 68, 68, 0.3);
}

.badge.coming-soon {
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
  box-shadow: 0 2px 4px rgba(245, 158, 11, 0.3);
}

/* 装饰元素 */
.sidebar-decoration {
  position: fixed;
  pointer-events: none;
  z-index: -1;
}

.deco-star, .deco-heart, .deco-sparkle {
  position: absolute;
  font-size: 24px;
  opacity: 0.2;
  animation: float 4s ease-in-out infinite;
}

.deco-star {
  top: 10%;
  left: -20px;
  animation-delay: 0s;
}

.deco-heart {
  top: 40%;
  right: -20px;
  animation-delay: 1.5s;
}

.deco-sparkle {
  bottom: 20%;
  left: -15px;
  animation-delay: 3s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
    opacity: 0.2;
  }
  50% {
    transform: translateY(-15px) rotate(180deg);
    opacity: 0.4;
  }
}

/* 响应式 */
@media (max-width: 1024px) {
  .anime-sidebar {
    position: relative;
    top: 0;
    margin-bottom: 20px;
  }
  
  .anime-menu-box {
    padding: 16px;
  }
  
  .sidebar-decoration {
    display: none;
  }
}

@media (max-width: 768px) {
  .menu-item {
    padding: 10px 12px;
    font-size: 13px;
  }
  
  .menu-item .icon {
    font-size: 16px;
  }
  
  .anime-menu-label {
    font-size: 14px;
  }
}
</style>

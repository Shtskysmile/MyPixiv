<template>
  <div class="portal-root">
    <div class="portal-bg"></div>
    <div class="portal-overlay"></div>

    <div class="portal-container">
      <Navbar :noContainer="true" />
      
      <div class="portal-content">
        <div class="portal-header">
          <h1 class="portal-title">
            <span class="crown-icon">👑</span>
            管理员控制台
          </h1>
          <p class="portal-subtitle">选择您要进入的管理模块</p>
        </div>

        <div class="admin-cards">
          <div class="admin-card community" @click="goToCommunityAdmin">
            <div class="card-bg"></div>
            <div class="card-content">
              <div class="card-icon">📝</div>
              <h2 class="card-title">社区管理</h2>
              <p class="card-description">
                管理社区内容，包括作品审核、用户封禁、评论管理等功能
              </p>
              <ul class="card-features">
                <li><span class="feature-icon">✅</span> 作品审核与驳回</li>
                <li><span class="feature-icon">🚫</span> 用户与作品封禁</li>
                <li><span class="feature-icon">💬</span> 评论管理</li>
              </ul>
              <div class="card-button">
                <span>进入管理</span>
                <span class="arrow">→</span>
              </div>
            </div>
          </div>

          <div class="admin-card system" @click="goToSystemAdmin">
            <div class="card-bg"></div>
            <div class="card-content">
              <div class="card-icon">⚙️</div>
              <h2 class="card-title">系统管理</h2>
              <p class="card-description">
                管理系统设置，包括用户信息修改、密码重置、系统日志等功能
              </p>
              <ul class="card-features">
                <li><span class="feature-icon">✏️</span> 修改用户信息</li>
                <li><span class="feature-icon">🔑</span> 重置用户密码</li>
                <li><span class="feature-icon">📋</span> 系统日志查看</li>
              </ul>
              <div class="card-button">
                <span>进入管理</span>
                <span class="arrow">→</span>
              </div>
            </div>
          </div>
        </div>

        <div class="quick-stats">
          <div class="stat-item">
            <div class="stat-icon">👥</div>
            <div class="stat-text">
              <div class="stat-label">总用户数</div>
              <div class="stat-value">{{ totalUsers }}</div>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-icon">🎨</div>
            <div class="stat-text">
              <div class="stat-label">总作品数</div>
              <div class="stat-value">{{ totalContributions }}</div>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-icon">⏳</div>
            <div class="stat-text">
              <div class="stat-label">待审核</div>
              <div class="stat-value">{{ pendingAudits }}</div>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-icon">📊</div>
            <div class="stat-text">
              <div class="stat-label">今日访问</div>
              <div class="stat-value">{{ todayVisits }}</div>
            </div>
          </div>
        </div>

        <div class="back-button" @click="goBack">
          <span class="back-icon">←</span>
          <span>返回主页</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Navbar from '../Navbar.vue';

export default {
  name: 'AdminPortal',
  components: {
    Navbar
  },
  data() {
    return {
      totalUsers: 1234,
      totalContributions: 5678,
      pendingAudits: 42,
      todayVisits: 8901
    };
  },
  methods: {
    goToCommunityAdmin() {
      this.$router.push('/admin/community');
    },
    goToSystemAdmin() {
      this.$router.push('/admin/system');
    },
    goBack() {
      this.$router.push('/index');
    }
  }
};
</script>

<style scoped>
.portal-root {
  position: relative;
  min-height: 100vh;
  background: #0f0f23;
  overflow: hidden;
}

.portal-bg {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(ellipse at top, #1a1a3e 0%, #0f0f23 100%);
  z-index: 0;
}

.portal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    repeating-linear-gradient(0deg, rgba(255,255,255,0.03) 0px, transparent 1px, transparent 2px, rgba(255,255,255,0.03) 3px),
    repeating-linear-gradient(90deg, rgba(255,255,255,0.03) 0px, transparent 1px, transparent 2px, rgba(255,255,255,0.03) 3px);
  z-index: 0;
}

.portal-container {
  position: relative;
  z-index: 1;
  padding-top: 60px;
}

.portal-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 60px 20px;
}

/* 头部 */
.portal-header {
  text-align: center;
  margin-bottom: 60px;
}

.portal-title {
  font-size: 56px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
}

.crown-icon {
  font-size: 64px;
  filter: drop-shadow(0 0 20px rgba(102, 126, 234, 0.5));
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-15px) rotate(5deg); }
}

.portal-subtitle {
  font-size: 20px;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
}

/* 管理卡片 */
.admin-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(450px, 1fr));
  gap: 40px;
  margin-bottom: 60px;
}

.admin-card {
  position: relative;
  border-radius: 24px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  height: 480px;
}

.admin-card:hover {
  transform: translateY(-12px) scale(1.02);
}

.card-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  transition: all 0.5s ease;
}

.admin-card.community .card-bg {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.admin-card.system .card-bg {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.admin-card:hover .card-bg {
  transform: scale(1.1);
}

.card-content {
  position: relative;
  z-index: 1;
  padding: 40px;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(10px);
}

.card-icon {
  font-size: 80px;
  margin-bottom: 24px;
  filter: drop-shadow(0 4px 12px rgba(0, 0, 0, 0.3));
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.card-title {
  font-size: 32px;
  font-weight: 800;
  color: #ffffff;
  margin-bottom: 16px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.card-description {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.6;
  margin-bottom: 24px;
}

.card-features {
  list-style: none;
  padding: 0;
  margin: 0 0 auto 0;
}

.card-features li {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  font-size: 15px;
  color: rgba(255, 255, 255, 0.95);
  font-weight: 500;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.card-features li:last-child {
  border-bottom: none;
}

.feature-icon {
  font-size: 20px;
}

.card-button {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(255, 255, 255, 0.2);
  padding: 16px 24px;
  border-radius: 12px;
  font-size: 18px;
  font-weight: 700;
  color: #ffffff;
  margin-top: 24px;
  transition: all 0.3s ease;
}

.admin-card:hover .card-button {
  background: rgba(255, 255, 255, 0.3);
  transform: translateX(8px);
}

.arrow {
  font-size: 24px;
  transition: transform 0.3s ease;
}

.admin-card:hover .arrow {
  transform: translateX(8px);
}

/* 快速统计 */
.quick-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.stat-item {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.stat-item:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.2);
  transform: translateY(-4px);
}

.stat-item .stat-icon {
  font-size: 48px;
  filter: drop-shadow(0 2px 8px rgba(0, 0, 0, 0.3));
}

.stat-text {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  margin-bottom: 4px;
  font-weight: 500;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #ffffff;
}

/* 返回按钮 */
.back-button {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  padding: 14px 32px;
  background: rgba(255, 255, 255, 0.1);
  border: 2px solid rgba(255, 255, 255, 0.2);
  border-radius: 50px;
  color: #ffffff;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  margin: 0 auto;
  display: flex;
  justify-content: center;
}

.back-button:hover {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.4);
  transform: translateX(-8px);
}

.back-icon {
  font-size: 20px;
  transition: transform 0.3s ease;
}

.back-button:hover .back-icon {
  transform: translateX(-4px);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .admin-cards {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .portal-title {
    font-size: 40px;
  }
  
  .crown-icon {
    font-size: 48px;
  }
  
  .portal-subtitle {
    font-size: 16px;
  }
  
  .admin-card {
    height: auto;
    min-height: 400px;
  }
  
  .card-title {
    font-size: 28px;
  }
  
  .quick-stats {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .portal-title {
    font-size: 32px;
    flex-direction: column;
  }
  
  .card-content {
    padding: 24px;
  }
  
  .card-icon {
    font-size: 60px;
  }
}
</style>


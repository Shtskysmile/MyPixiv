<template>
  <div class="admin-root">
    <div class="admin-bg"></div>
    <div class="admin-overlay"></div>

    <div class="admin-container">
      <Navbar :noContainer="true" />
      
      <div class="admin-header">
        <div class="header-content">
          <div class="title-section">
            <h1 class="admin-title">
              <span class="icon-badge">👑</span>
              社区管理中心
            </h1>
            <p class="admin-subtitle">维护社区秩序，守护创作环境</p>
          </div>
          <div class="stats-cards">
            <div class="stat-card pending">
              <div class="stat-icon">⏳</div>
              <div class="stat-info">
                <div class="stat-value">{{ pendingCount }}</div>
                <div class="stat-label">待审核</div>
              </div>
            </div>
            <div class="stat-card blocked">
              <div class="stat-icon">🚫</div>
              <div class="stat-info">
                <div class="stat-value">{{ blockedUsersCount + blockedContributionsCount }}</div>
                <div class="stat-label">已封禁</div>
              </div>
            </div>
            <div class="stat-card approved">
              <div class="stat-icon">✅</div>
              <div class="stat-info">
                <div class="stat-value">{{ approvedCount }}</div>
                <div class="stat-label">已通过</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="admin-tabs">
        <div class="tabs-wrapper">
          <button 
            v-for="tab in tabs" 
            :key="tab.id"
            class="tab-button"
            :class="{ active: currentTab === tab.id }"
            @click="switchTab(tab.id)"
          >
            <span class="tab-icon">{{ tab.icon }}</span>
            <span class="tab-text">{{ tab.name }}</span>
            <span v-if="tab.badge" class="tab-badge">{{ tab.badge }}</span>
          </button>
        </div>
      </div>

      <div class="admin-content">
        <!-- 审核作品 -->
        <div v-if="currentTab === 'audit'" class="content-section">
          <div class="section-header">
            <h2 class="section-title">
              <span class="icon">📝</span>
              作品审核
            </h2>
            <div class="filter-buttons">
              <button 
                v-for="filter in auditFilters" 
                :key="filter.id"
                class="filter-btn"
                :class="{ active: auditFilter === filter.id }"
                @click="auditFilter = filter.id"
              >
                {{ filter.name }} ({{ filter.count }})
              </button>
            </div>
          </div>

          <div v-if="filteredAuditContributions.length === 0" class="empty-state">
            <div class="empty-icon">📭</div>
            <p class="empty-text">暂无{{ auditFilterName }}作品</p>
          </div>

          <div v-else class="contributions-grid">
            <div 
              v-for="contribution in filteredAuditContributions" 
              :key="contribution.contributionId"
              class="contribution-card"
            >
              <div class="card-image">
                <img :src="contribution.image" :alt="contribution.title" />
                <div class="card-overlay">
                  <div class="overlay-stats">
                    <span><span class="stat-icon">👁️</span> {{ contribution.viewCount }}</span>
                    <span><span class="stat-icon">❤️</span> {{ contribution.likeCount }}</span>
                    <span><span class="stat-icon">⭐</span> {{ contribution.favoriteCount }}</span>
                  </div>
                </div>
              </div>
              <div class="card-content">
                <h3 class="card-title">{{ contribution.title }}</h3>
                <div class="card-author">
                  <img :src="contribution.avatar" class="author-avatar" />
                  <span class="author-name">作者ID: {{ contribution.authorId }}</span>
                </div>
                <div v-if="contribution.dismissalReason" class="dismissal-reason">
                  <span class="reason-label">驳回理由：</span>
                  <span class="reason-text">{{ contribution.dismissalReason }}</span>
                </div>
                <div class="card-actions">
                  <button 
                    v-if="auditFilter === 'pending'"
                    class="action-btn approve"
                    @click="approveContribution(contribution.contributionId)"
                  >
                    <span class="btn-icon">✅</span> 通过
                  </button>
                  <button 
                    v-if="auditFilter === 'pending'"
                    class="action-btn dismiss"
                    @click="openDismissModal(contribution)"
                  >
                    <span class="btn-icon">❌</span> 驳回
                  </button>
                  <button 
                    class="action-btn view"
                    @click="viewContribution(contribution.contributionId)"
                  >
                    <span class="btn-icon">👁️</span> 查看
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 封禁管理 -->
        <div v-if="currentTab === 'blocked'" class="content-section">
          <div class="section-header">
            <h2 class="section-title">
              <span class="icon">🚫</span>
              封禁管理
            </h2>
            <div class="filter-buttons">
              <button 
                class="filter-btn"
                :class="{ active: blockedFilter === 'users' }"
                @click="blockedFilter = 'users'"
              >
                用户 ({{ blockedUsersCount }})
              </button>
              <button 
                class="filter-btn"
                :class="{ active: blockedFilter === 'contributions' }"
                @click="blockedFilter = 'contributions'"
              >
                作品 ({{ blockedContributionsCount }})
              </button>
            </div>
          </div>

          <!-- 封禁用户列表 -->
          <div v-if="blockedFilter === 'users'" class="blocked-list">
            <div v-if="blockedUsers.length === 0" class="empty-state">
              <div class="empty-icon">👥</div>
              <p class="empty-text">暂无封禁用户</p>
            </div>
            <div v-else class="users-grid">
              <div 
                v-for="user in blockedUsers" 
                :key="user.userId"
                class="user-card"
              >
                <img :src="user.avatar" class="user-avatar" />
                <div class="user-info">
                  <h3 class="user-name">{{ user.username }}</h3>
                  <p class="user-id">ID: {{ user.userId }}</p>
                  <span class="user-status blocked">已封禁</span>
                </div>
                <div class="user-actions">
                  <button 
                    class="action-btn unblock"
                    @click="unblockUser(user.userId)"
                  >
                    <span class="btn-icon">🔓</span> 解封
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- 封禁作品列表 -->
          <div v-if="blockedFilter === 'contributions'" class="blocked-list">
            <div v-if="blockedContributions.length === 0" class="empty-state">
              <div class="empty-icon">🎨</div>
              <p class="empty-text">暂无封禁作品</p>
            </div>
            <div v-else class="contributions-grid">
              <div 
                v-for="contribution in blockedContributions" 
                :key="contribution.contributionId"
                class="contribution-card"
              >
                <div class="card-image">
                  <img :src="contribution.image" :alt="contribution.title" />
                  <div class="blocked-badge">已封禁</div>
                </div>
                <div class="card-content">
                  <h3 class="card-title">{{ contribution.title }}</h3>
                  <div class="card-author">
                    <img :src="contribution.avatar" class="author-avatar" />
                    <span class="author-name">作者ID: {{ contribution.authorId }}</span>
                  </div>
                  <div class="card-actions">
                    <button 
                      class="action-btn unblock"
                      @click="unblockContribution(contribution.contributionId)"
                    >
                      <span class="btn-icon">🔓</span> 解封
                    </button>
                    <button 
                      class="action-btn view"
                      @click="viewContribution(contribution.contributionId)"
                    >
                      <span class="btn-icon">👁️</span> 查看
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 用户管理 -->
        <div v-if="currentTab === 'users'" class="content-section">
          <div class="section-header">
            <h2 class="section-title">
              <span class="icon">👥</span>
              用户管理
            </h2>
          </div>
          <div class="info-box">
            <p>💡 提示：在此页面可以查看和管理所有用户，包括封禁和解封操作。</p>
          </div>
        </div>

        <!-- 评论管理 -->
        <div v-if="currentTab === 'comments'" class="content-section">
          <div class="section-header">
            <h2 class="section-title">
              <span class="icon">💬</span>
              评论管理
            </h2>
          </div>
          <div class="info-box">
            <p>💡 提示：评论管理功能正在开发中，敬请期待。</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 驳回理由弹窗 -->
    <div class="modal" :class="{ 'is-active': dismissModalVisible }">
      <div class="modal-background" @click="closeDismissModal"></div>
      <div class="modal-card anime-modal">
        <header class="modal-card-head">
          <p class="modal-card-title">
            <span class="icon">❌</span> 驳回作品
          </p>
          <button class="delete" aria-label="close" @click="closeDismissModal"></button>
        </header>
        <section class="modal-card-body">
          <div v-if="selectedContribution" class="dismiss-preview">
            <img :src="selectedContribution.image" class="preview-image" />
            <h3 class="preview-title">{{ selectedContribution.title }}</h3>
          </div>
          <div class="field">
            <label class="label">驳回理由</label>
            <div class="control">
              <textarea 
                class="textarea anime-input" 
                v-model="dismissalReason" 
                placeholder="请输入驳回理由，帮助创作者改进作品..."
                rows="5"
              ></textarea>
            </div>
          </div>
        </section>
        <footer class="modal-card-foot">
          <button class="button is-danger anime-button" @click="submitDismiss">确认驳回</button>
          <button class="button anime-button" @click="closeDismissModal">取消</button>
        </footer>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import Navbar from '../Navbar.vue';

export default {
  name: 'CommunityAdmin',
  components: {
    Navbar
  },
  data() {
    return {
      currentTab: 'audit',
      auditFilter: 'pending',
      blockedFilter: 'users',
      
      // 审核数据
      pendingContributions: [],
      approvedContributions: [],
      dismissalContributions: [],
      
      // 封禁数据
      blockedUsers: [],
      blockedContributions: [],
      
      // 弹窗
      dismissModalVisible: false,
      selectedContribution: null,
      dismissalReason: '',
      
      tabs: [
        { id: 'audit', name: '作品审核', icon: '📝', badge: 0 },
        { id: 'blocked', name: '封禁管理', icon: '🚫', badge: 0 },
        { id: 'users', name: '用户管理', icon: '👥' },
        { id: 'comments', name: '评论管理', icon: '💬' }
      ]
    };
  },
  computed: {
    pendingCount() {
      return this.pendingContributions.length;
    },
    approvedCount() {
      return this.approvedContributions.length;
    },
    blockedUsersCount() {
      return this.blockedUsers.length;
    },
    blockedContributionsCount() {
      return this.blockedContributions.length;
    },
    auditFilters() {
      return [
        { id: 'pending', name: '待审核', count: this.pendingCount },
        { id: 'approved', name: '已通过', count: this.approvedCount },
        { id: 'dismissed', name: '已驳回', count: this.dismissalContributions.length }
      ];
    },
    auditFilterName() {
      const filter = this.auditFilters.find(f => f.id === this.auditFilter);
      return filter ? filter.name : '';
    },
    filteredAuditContributions() {
      if (this.auditFilter === 'pending') {
        return this.pendingContributions;
      } else if (this.auditFilter === 'approved') {
        return this.approvedContributions;
      } else {
        return this.dismissalContributions;
      }
    }
  },
  mounted() {
    this.loadAuditContributions();
    this.loadBlockedUsers();
    this.loadBlockedContributions();
    this.updateTabBadges();
  },
  watch: {
    pendingCount() {
      this.updateTabBadges();
    },
    blockedUsersCount() {
      this.updateTabBadges();
    },
    blockedContributionsCount() {
      this.updateTabBadges();
    }
  },
  methods: {
    switchTab(tabId) {
      this.currentTab = tabId;
    },
    
    updateTabBadges() {
      this.tabs[0].badge = this.pendingCount || null;
      this.tabs[1].badge = (this.blockedUsersCount + this.blockedContributionsCount) || null;
    },
    
    // 加载审核作品
    async loadAuditContributions() {
      try {
        const response = await axios.get('/api/communityAdmin/auditContributions');
        if (response.data.code === 200) {
          const data = response.data.data;
          this.pendingContributions = data.pendingContributions || [];
          this.approvedContributions = data.approvedContributions || [];
          this.dismissalContributions = data.dismissalContributions || [];
        }
      } catch (error) {
        console.error('加载审核作品失败:', error);
        this.$message?.error('加载审核作品失败');
      }
    },
    
    // 加载封禁用户
    async loadBlockedUsers() {
      try {
        const response = await axios.get('/api/communityAdmin/blockedUsers');
        if (response.data.code === 200) {
          this.blockedUsers = response.data.data || [];
        }
      } catch (error) {
        console.error('加载封禁用户失败:', error);
      }
    },
    
    // 加载封禁作品
    async loadBlockedContributions() {
      try {
        const response = await axios.get('/api/communityAdmin/blockedContributions');
        if (response.data.code === 200) {
          this.blockedContributions = response.data.data || [];
        }
      } catch (error) {
        console.error('加载封禁作品失败:', error);
      }
    },
    
    // 通过审核
    async approveContribution(contributionId) {
      try {
        const response = await axios.post('/api/communityAdmin/approveContribution', null, {
          params: { contributionId }
        });
        if (response.data.code === 200) {
          alert('✅ 审核通过！');
          await this.loadAuditContributions();
        } else {
          alert('❌ 操作失败：' + response.data.message);
        }
      } catch (error) {
        console.error('审核通过失败:', error);
        alert('❌ 操作失败');
      }
    },
    
    // 打开驳回弹窗
    openDismissModal(contribution) {
      this.selectedContribution = contribution;
      this.dismissalReason = '';
      this.dismissModalVisible = true;
    },
    
    // 关闭驳回弹窗
    closeDismissModal() {
      this.dismissModalVisible = false;
      this.selectedContribution = null;
      this.dismissalReason = '';
    },
    
    // 提交驳回
    async submitDismiss() {
      if (!this.dismissalReason.trim()) {
        alert('请输入驳回理由');
        return;
      }
      
      try {
        const response = await axios.post('/api/communityAdmin/dismissContribution', null, {
          params: {
            contributionId: this.selectedContribution.contributionId,
            dismissalReason: this.dismissalReason
          }
        });
        if (response.data.code === 200) {
          alert('✅ 已驳回作品');
          this.closeDismissModal();
          await this.loadAuditContributions();
        } else {
          alert('❌ 操作失败：' + response.data.message);
        }
      } catch (error) {
        console.error('驳回作品失败:', error);
        alert('❌ 操作失败');
      }
    },
    
    // 解封用户
    async unblockUser(userId) {
      if (!confirm('确定要解封此用户吗？')) return;
      
      try {
        const response = await axios.post('/api/communityAdmin/unblockUser', null, {
          params: { userId }
        });
        if (response.data.code === 200) {
          alert('✅ 解封成功！');
          await this.loadBlockedUsers();
        } else {
          alert('❌ 操作失败：' + response.data.message);
        }
      } catch (error) {
        console.error('解封用户失败:', error);
        alert('❌ 操作失败');
      }
    },
    
    // 解封作品
    async unblockContribution(contributionId) {
      if (!confirm('确定要解封此作品吗？')) return;
      
      try {
        const response = await axios.post('/api/communityAdmin/unblockContribution', null, {
          params: { contributionId }
        });
        if (response.data.code === 200) {
          alert('✅ 解封成功！');
          await this.loadBlockedContributions();
        } else {
          alert('❌ 操作失败：' + response.data.message);
        }
      } catch (error) {
        console.error('解封作品失败:', error);
        alert('❌ 操作失败');
      }
    },
    
    // 查看作品详情
    viewContribution(contributionId) {
      this.$router.push(`/image/${contributionId}`);
    }
  }
};
</script>

<style scoped>
.admin-root {
  position: relative;
  min-height: 100vh;
  background: #f5f7fa;
}

.admin-bg {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  opacity: 0.05;
  z-index: 0;
}

.admin-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg width="100" height="100" xmlns="http://www.w3.org/2000/svg"><defs><pattern id="grid" width="100" height="100" patternUnits="userSpaceOnUse"><path d="M 100 0 L 0 0 0 100" fill="none" stroke="rgba(102,126,234,0.03)" stroke-width="1"/></pattern></defs><rect width="100" height="100" fill="url(%23grid)"/></svg>');
  z-index: 0;
}

.admin-container {
  position: relative;
  z-index: 1;
  padding-top: 60px;
}

/* 头部区域 */
.admin-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
}

.title-section {
  text-align: center;
  margin-bottom: 32px;
}

.admin-title {
  font-size: 42px;
  font-weight: 800;
  color: #ffffff;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.icon-badge {
  font-size: 48px;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.admin-subtitle {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
}

/* 统计卡片 */
.stats-cards {
  display: flex;
  gap: 24px;
  justify-content: center;
  flex-wrap: wrap;
}

.stat-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 24px 32px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  min-width: 200px;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  font-size: 48px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.stat-card.pending .stat-value { color: #f39c12; }
.stat-card.blocked .stat-value { color: #e74c3c; }
.stat-card.approved .stat-value { color: #27ae60; }

/* 标签页 */
.admin-tabs {
  background: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 60px;
  z-index: 10;
}

.tabs-wrapper {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  gap: 8px;
  padding: 16px 20px;
  overflow-x: auto;
}

.tab-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border: none;
  background: transparent;
  color: #666;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  border-radius: 12px;
  transition: all 0.3s ease;
  position: relative;
  white-space: nowrap;
}

.tab-button:hover {
  background: #f5f7fa;
  color: #667eea;
}

.tab-button.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.tab-icon {
  font-size: 20px;
}

.tab-badge {
  background: #e74c3c;
  color: #ffffff;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: 700;
}

.tab-button.active .tab-badge {
  background: rgba(255, 255, 255, 0.3);
}

/* 内容区域 */
.admin-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 32px 20px;
}

.content-section {
  animation: fadeIn 0.5s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 16px;
}

.section-title {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  display: flex;
  align-items: center;
  gap: 12px;
}

.section-title .icon {
  font-size: 32px;
}

/* 过滤按钮 */
.filter-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.filter-btn {
  padding: 10px 20px;
  border: 2px solid #e0e0e0;
  background: #ffffff;
  color: #666;
  font-size: 15px;
  font-weight: 600;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.filter-btn:hover {
  border-color: #667eea;
  color: #667eea;
}

.filter-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  border-color: transparent;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-text {
  font-size: 18px;
  color: #999;
  font-weight: 500;
}

/* 作品网格 */
.contributions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.contribution-card {
  background: #ffffff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.contribution-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

.card-image {
  position: relative;
  width: 100%;
  height: 240px;
  overflow: hidden;
  background: #f5f7fa;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.contribution-card:hover .card-image img {
  transform: scale(1.1);
}

.card-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, transparent 0%, rgba(0, 0, 0, 0.7) 100%);
  display: flex;
  align-items: flex-end;
  padding: 16px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.contribution-card:hover .card-overlay {
  opacity: 1;
}

.overlay-stats {
  display: flex;
  gap: 16px;
  color: #ffffff;
  font-size: 14px;
  font-weight: 600;
}

.overlay-stats .stat-icon {
  font-size: 16px;
}

.blocked-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: #e74c3c;
  color: #ffffff;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
  box-shadow: 0 2px 8px rgba(231, 76, 60, 0.3);
}

.card-content {
  padding: 20px;
}

.card-title {
  font-size: 18px;
  font-weight: 700;
  color: #333;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-author {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.author-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e0e0e0;
}

.author-name {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.dismissal-reason {
  background: #fff3cd;
  border-left: 4px solid #ffc107;
  padding: 12px;
  margin-bottom: 12px;
  border-radius: 4px;
}

.reason-label {
  font-weight: 700;
  color: #856404;
  font-size: 13px;
}

.reason-text {
  display: block;
  color: #856404;
  font-size: 13px;
  margin-top: 4px;
}

.card-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.action-btn {
  flex: 1;
  min-width: 80px;
  padding: 10px 16px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.action-btn .btn-icon {
  font-size: 16px;
}

.action-btn.approve {
  background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%);
  color: #ffffff;
}

.action-btn.approve:hover {
  box-shadow: 0 4px 12px rgba(39, 174, 96, 0.3);
  transform: translateY(-2px);
}

.action-btn.dismiss {
  background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
  color: #ffffff;
}

.action-btn.dismiss:hover {
  box-shadow: 0 4px 12px rgba(231, 76, 60, 0.3);
  transform: translateY(-2px);
}

.action-btn.view {
  background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
  color: #ffffff;
}

.action-btn.view:hover {
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
  transform: translateY(-2px);
}

.action-btn.unblock {
  background: linear-gradient(135deg, #f39c12 0%, #e67e22 100%);
  color: #ffffff;
}

.action-btn.unblock:hover {
  box-shadow: 0 4px 12px rgba(243, 156, 18, 0.3);
  transform: translateY(-2px);
}

/* 用户网格 */
.users-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.user-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  transition: all 0.3s ease;
}

.user-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.user-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #e0e0e0;
  margin-bottom: 16px;
}

.user-info {
  margin-bottom: 16px;
  width: 100%;
}

.user-name {
  font-size: 18px;
  font-weight: 700;
  color: #333;
  margin-bottom: 4px;
}

.user-id {
  font-size: 13px;
  color: #999;
  margin-bottom: 8px;
}

.user-status {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 700;
}

.user-status.blocked {
  background: #ffe5e5;
  color: #e74c3c;
}

.user-actions {
  width: 100%;
}

/* 信息框 */
.info-box {
  background: linear-gradient(135deg, #e3f2fd 0%, #f3e5f5 100%);
  border-left: 4px solid #667eea;
  padding: 20px;
  border-radius: 8px;
  margin-top: 20px;
}

.info-box p {
  color: #555;
  font-size: 15px;
  margin: 0;
}

/* 弹窗样式 */
.anime-modal {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.2);
  animation: modalSlideIn 0.3s ease;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-50px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-card-head {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  padding: 24px;
}

.modal-card-title {
  color: #ffffff;
  font-size: 20px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 10px;
}

.modal-card-title .icon {
  font-size: 24px;
}

.modal-card-body {
  padding: 24px;
}

.dismiss-preview {
  text-align: center;
  margin-bottom: 20px;
}

.preview-image {
  width: 100%;
  max-height: 200px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 12px;
}

.preview-title {
  font-size: 18px;
  font-weight: 700;
  color: #333;
}

.anime-input {
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.anime-input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.modal-card-foot {
  background: #f5f7fa;
  padding: 20px 24px;
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.anime-button {
  border-radius: 8px;
  font-weight: 600;
  padding: 10px 24px;
  transition: all 0.3s ease;
}

.anime-button:hover {
  transform: translateY(-2px);
}

.button.is-danger {
  background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
  border: none;
  color: #ffffff;
}

.button.is-danger:hover {
  box-shadow: 0 4px 12px rgba(231, 76, 60, 0.3);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .admin-title {
    font-size: 32px;
  }
  
  .stats-cards {
    flex-direction: column;
  }
  
  .stat-card {
    width: 100%;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .contributions-grid {
    grid-template-columns: 1fr;
  }
  
  .users-grid {
    grid-template-columns: 1fr;
  }
  
  .tabs-wrapper {
    overflow-x: auto;
  }
}
</style>


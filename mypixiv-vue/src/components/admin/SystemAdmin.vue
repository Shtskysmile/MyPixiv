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
              <span class="icon-badge">⚙️</span>
              系统管理中心
            </h1>
            <p class="admin-subtitle">系统配置与日志管理</p>
          </div>
          <div class="stats-cards">
            <div class="stat-card logs">
              <div class="stat-icon">📋</div>
              <div class="stat-info">
                <div class="stat-value">{{ logs.length }}</div>
                <div class="stat-label">系统日志</div>
              </div>
            </div>
            <div class="stat-card users">
              <div class="stat-icon">👥</div>
              <div class="stat-info">
                <div class="stat-value">{{ todayLogs }}</div>
                <div class="stat-label">今日操作</div>
              </div>
            </div>
            <div class="stat-card active">
              <div class="stat-icon">⚡</div>
              <div class="stat-info">
                <div class="stat-value">运行中</div>
                <div class="stat-label">系统状态</div>
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
          </button>
        </div>
      </div>

      <div class="admin-content">
        <!-- 系统日志 -->
        <div v-if="currentTab === 'logs'" class="content-section">
          <div class="section-header">
            <h2 class="section-title">
              <span class="icon">📋</span>
              系统日志
            </h2>
            <button class="refresh-btn" @click="loadLogs">
              <span class="btn-icon">🔄</span> 刷新
            </button>
          </div>

          <div v-if="logs.length === 0" class="empty-state">
            <div class="empty-icon">📭</div>
            <p class="empty-text">暂无系统日志</p>
          </div>

          <div v-else class="logs-container">
            <div class="logs-table">
              <div class="table-header">
                <div class="th time">操作时间</div>
                <div class="th operator">操作者</div>
                <div class="th description">操作描述</div>
              </div>
              <div class="table-body">
                <div 
                  v-for="(log, index) in paginatedLogs" 
                  :key="index"
                  class="table-row"
                >
                  <div class="td time">
                    <span class="time-icon">🕐</span>
                    {{ formatTime(log.time) }}
                  </div>
                  <div class="td operator">
                    <span class="operator-badge">{{ log.operatorId }}</span>
                  </div>
                  <div class="td description">{{ log.description }}</div>
                </div>
              </div>
            </div>

            <!-- 分页 -->
            <div class="pagination">
              <button 
                class="page-btn"
                :disabled="currentPage === 1"
                @click="currentPage--"
              >
                ← 上一页
              </button>
              <span class="page-info">
                第 {{ currentPage }} / {{ totalPages }} 页
              </span>
              <button 
                class="page-btn"
                :disabled="currentPage === totalPages"
                @click="currentPage++"
              >
                下一页 →
              </button>
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

          <div class="management-cards">
            <div class="mgmt-card">
              <div class="card-icon">✏️</div>
              <h3 class="card-title">修改用户信息</h3>
              <p class="card-desc">修改用户的基本信息，包括用户名、性别和头像</p>
              <button class="card-btn" @click="openEditUserModal">
                <span class="btn-icon">✏️</span> 修改信息
              </button>
            </div>

            <div class="mgmt-card">
              <div class="card-icon">🔑</div>
              <h3 class="card-title">重置用户密码</h3>
              <p class="card-desc">为用户重置密码，重置后密码将变为默认密码</p>
              <button class="card-btn" @click="openResetPasswordModal">
                <span class="btn-icon">🔑</span> 重置密码
              </button>
            </div>
          </div>
        </div>

        <!-- 系统设置 -->
        <div v-if="currentTab === 'settings'" class="content-section">
          <div class="section-header">
            <h2 class="section-title">
              <span class="icon">⚙️</span>
              系统设置
            </h2>
          </div>

          <div class="settings-panel">
            <div class="setting-item">
              <div class="setting-info">
                <h3 class="setting-title">🌐 系统语言</h3>
                <p class="setting-desc">设置系统默认语言</p>
              </div>
              <select class="setting-select">
                <option value="zh-CN">简体中文</option>
                <option value="en-US">English</option>
                <option value="ja-JP">日本語</option>
              </select>
            </div>

            <div class="setting-item">
              <div class="setting-info">
                <h3 class="setting-title">🎨 主题模式</h3>
                <p class="setting-desc">切换系统主题</p>
              </div>
              <select class="setting-select">
                <option value="light">浅色模式</option>
                <option value="dark">深色模式</option>
                <option value="auto">跟随系统</option>
              </select>
            </div>

            <div class="setting-item">
              <div class="setting-info">
                <h3 class="setting-title">📊 数据统计</h3>
                <p class="setting-desc">启用数据统计功能</p>
              </div>
              <label class="switch">
                <input type="checkbox" checked>
                <span class="slider"></span>
              </label>
            </div>

            <div class="setting-item">
              <div class="setting-info">
                <h3 class="setting-title">🔔 系统通知</h3>
                <p class="setting-desc">接收系统通知</p>
              </div>
              <label class="switch">
                <input type="checkbox" checked>
                <span class="slider"></span>
              </label>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 修改用户信息弹窗 -->
    <div class="modal" :class="{ 'is-active': editUserModalVisible }">
      <div class="modal-background" @click="closeEditUserModal"></div>
      <div class="modal-card anime-modal">
        <header class="modal-card-head">
          <p class="modal-card-title">
            <span class="icon">✏️</span> 修改用户信息
          </p>
          <button class="delete" aria-label="close" @click="closeEditUserModal"></button>
        </header>
        <section class="modal-card-body">
          <div class="field">
            <label class="label">用户ID</label>
            <div class="control">
              <input class="input anime-input" v-model="editUserId" placeholder="请输入用户ID" />
            </div>
          </div>

          <div class="field">
            <label class="label">新用户名</label>
            <div class="control">
              <input class="input anime-input" v-model="editUsername" placeholder="请输入新用户名" />
            </div>
          </div>

          <div class="field">
            <label class="label">性别</label>
            <div class="control">
              <div class="select is-fullwidth">
                <select v-model="editGender" class="anime-input">
                  <option :value="0">保密</option>
                  <option :value="1">男</option>
                  <option :value="2">女</option>
                </select>
              </div>
            </div>
          </div>

          <div class="field">
            <label class="label">新头像（可选）</label>
            <div class="control">
              <input 
                type="file" 
                class="file-input" 
                accept="image/*"
                @change="onAvatarChange"
              />
            </div>
          </div>
        </section>
        <footer class="modal-card-foot">
          <button class="button is-success anime-button" @click="submitEditUser">保存修改</button>
          <button class="button anime-button" @click="closeEditUserModal">取消</button>
        </footer>
      </div>
    </div>

    <!-- 重置密码弹窗 -->
    <div class="modal" :class="{ 'is-active': resetPasswordModalVisible }">
      <div class="modal-background" @click="closeResetPasswordModal"></div>
      <div class="modal-card anime-modal">
        <header class="modal-card-head">
          <p class="modal-card-title">
            <span class="icon">🔑</span> 重置用户密码
          </p>
          <button class="delete" aria-label="close" @click="closeResetPasswordModal"></button>
        </header>
        <section class="modal-card-body">
          <div class="field">
            <label class="label">用户ID</label>
            <div class="control">
              <input class="input anime-input" v-model="resetUserId" placeholder="请输入要重置密码的用户ID" />
            </div>
          </div>
          <div class="warning-box">
            <p>⚠️ 警告：重置密码后，该用户的密码将被重置为系统默认密码。</p>
          </div>
        </section>
        <footer class="modal-card-foot">
          <button class="button is-danger anime-button" @click="submitResetPassword">确认重置</button>
          <button class="button anime-button" @click="closeResetPasswordModal">取消</button>
        </footer>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import Navbar from '../Navbar.vue';

export default {
  name: 'SystemAdmin',
  components: {
    Navbar
  },
  data() {
    return {
      currentTab: 'logs',
      logs: [],
      currentPage: 1,
      pageSize: 20,
      
      // 修改用户信息
      editUserModalVisible: false,
      editUserId: '',
      editUsername: '',
      editGender: 0,
      editAvatarFile: null,
      
      // 重置密码
      resetPasswordModalVisible: false,
      resetUserId: '',
      
      tabs: [
        { id: 'logs', name: '系统日志', icon: '📋' },
        { id: 'users', name: '用户管理', icon: '👥' },
        { id: 'settings', name: '系统设置', icon: '⚙️' }
      ]
    };
  },
  computed: {
    todayLogs() {
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      return this.logs.filter(log => {
        const logDate = new Date(log.time);
        return logDate >= today;
      }).length;
    },
    totalPages() {
      return Math.ceil(this.logs.length / this.pageSize);
    },
    paginatedLogs() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.logs.slice(start, end);
    }
  },
  mounted() {
    this.loadLogs();
  },
  methods: {
    switchTab(tabId) {
      this.currentTab = tabId;
    },
    
    // 加载系统日志
    async loadLogs() {
      try {
        const response = await axios.get('/systemAdmin/logs');
        if (response.data.code === 200) {
          this.logs = response.data.data || [];
          // 按时间倒序排列
          this.logs.sort((a, b) => new Date(b.time) - new Date(a.time));
        }
      } catch (error) {
        console.error('加载系统日志失败:', error);
        alert('❌ 加载系统日志失败');
      }
    },
    
    // 格式化时间
    formatTime(time) {
      if (!time) return '-';
      const date = new Date(time);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');
      const seconds = String(date.getSeconds()).padStart(2, '0');
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    },
    
    // 打开修改用户信息弹窗
    openEditUserModal() {
      this.editUserId = '';
      this.editUsername = '';
      this.editGender = 0;
      this.editAvatarFile = null;
      this.editUserModalVisible = true;
    },
    
    // 关闭修改用户信息弹窗
    closeEditUserModal() {
      this.editUserModalVisible = false;
    },
    
    // 头像文件变化
    onAvatarChange(event) {
      const file = event.target.files[0];
      if (file) {
        this.editAvatarFile = file;
      }
    },
    
    // 提交修改用户信息
    async submitEditUser() {
      if (!this.editUserId.trim()) {
        alert('请输入用户ID');
        return;
      }
      if (!this.editUsername.trim()) {
        alert('请输入新用户名');
        return;
      }
      
      try {
        const formData = new FormData();
        formData.append('userId', this.editUserId);
        formData.append('newUsername', this.editUsername);
        formData.append('newGender', this.editGender);
        if (this.editAvatarFile) {
          formData.append('newAvatar', this.editAvatarFile);
        }
        
        const response = await axios.post('/systemAdmin/updateUserInfo', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });
        
        if (response.data.code === 200) {
          alert('✅ 修改用户信息成功！');
          this.closeEditUserModal();
          await this.loadLogs();
        } else {
          alert('❌ 操作失败：' + response.data.message);
        }
      } catch (error) {
        console.error('修改用户信息失败:', error);
        alert('❌ 操作失败');
      }
    },
    
    // 打开重置密码弹窗
    openResetPasswordModal() {
      this.resetUserId = '';
      this.resetPasswordModalVisible = true;
    },
    
    // 关闭重置密码弹窗
    closeResetPasswordModal() {
      this.resetPasswordModalVisible = false;
    },
    
    // 提交重置密码
    async submitResetPassword() {
      if (!this.resetUserId.trim()) {
        alert('请输入用户ID');
        return;
      }
      
      if (!confirm('确定要重置该用户的密码吗？')) {
        return;
      }
      
      try {
        const response = await axios.post('/systemAdmin/resetPassword', null, {
          params: { userId: this.resetUserId }
        });
        
        if (response.data.code === 200) {
          alert('✅ 重置密码成功！');
          this.closeResetPasswordModal();
          await this.loadLogs();
        } else {
          alert('❌ 操作失败：' + response.data.message);
        }
      } catch (error) {
        console.error('重置密码失败:', error);
        alert('❌ 操作失败');
      }
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
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  opacity: 0.05;
  z-index: 0;
}

.admin-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg width="100" height="100" xmlns="http://www.w3.org/2000/svg"><defs><pattern id="grid" width="100" height="100" patternUnits="userSpaceOnUse"><path d="M 100 0 L 0 0 0 100" fill="none" stroke="rgba(240,147,251,0.03)" stroke-width="1"/></pattern></defs><rect width="100" height="100" fill="url(%23grid)"/></svg>');
  z-index: 0;
}

.admin-container {
  position: relative;
  z-index: 1;
  padding-top: 60px;
}

/* 头部区域 */
.admin-header {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
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
  animation: rotate 4s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
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

.stat-card.logs .stat-value { color: #3498db; }
.stat-card.users .stat-value { color: #9b59b6; }
.stat-card.active .stat-value { color: #27ae60; }

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
  white-space: nowrap;
}

.tab-button:hover {
  background: #f5f7fa;
  color: #f093fb;
}

.tab-button.active {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: #ffffff;
  box-shadow: 0 4px 12px rgba(240, 147, 251, 0.3);
}

.tab-icon {
  font-size: 20px;
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

.refresh-btn {
  padding: 10px 20px;
  border: 2px solid #e0e0e0;
  background: #ffffff;
  color: #666;
  font-size: 15px;
  font-weight: 600;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.refresh-btn:hover {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: #ffffff;
  border-color: transparent;
  box-shadow: 0 4px 12px rgba(240, 147, 251, 0.3);
}

.btn-icon {
  font-size: 16px;
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

/* 日志表格 */
.logs-container {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.logs-table {
  width: 100%;
  overflow-x: auto;
}

.table-header {
  display: flex;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: #ffffff;
  padding: 16px;
  border-radius: 12px 12px 0 0;
  font-weight: 700;
  font-size: 15px;
}

.table-body {
  max-height: 600px;
  overflow-y: auto;
}

.table-row {
  display: flex;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  transition: background 0.2s ease;
}

.table-row:hover {
  background: #f9f9f9;
}

.th, .td {
  padding: 0 12px;
}

.th.time, .td.time {
  flex: 0 0 200px;
}

.th.operator, .td.operator {
  flex: 0 0 150px;
}

.th.description, .td.description {
  flex: 1;
}

.time-icon {
  margin-right: 8px;
}

.operator-badge {
  display: inline-block;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 2px solid #f0f0f0;
}

.page-btn {
  padding: 10px 20px;
  border: 2px solid #e0e0e0;
  background: #ffffff;
  color: #666;
  font-size: 14px;
  font-weight: 600;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.page-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: #ffffff;
  border-color: transparent;
}

.page-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.page-info {
  font-size: 15px;
  color: #666;
  font-weight: 600;
}

/* 管理卡片 */
.management-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
}

.mgmt-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  text-align: center;
  transition: all 0.3s ease;
}

.mgmt-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

.card-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.card-title {
  font-size: 22px;
  font-weight: 700;
  color: #333;
  margin-bottom: 12px;
}

.card-desc {
  font-size: 15px;
  color: #666;
  margin-bottom: 24px;
  line-height: 1.6;
}

.card-btn {
  padding: 12px 32px;
  border: none;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: #ffffff;
  font-size: 16px;
  font-weight: 600;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.card-btn:hover {
  box-shadow: 0 6px 20px rgba(240, 147, 251, 0.4);
  transform: translateY(-2px);
}

/* 设置面板 */
.settings-panel {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.setting-item:last-child {
  border-bottom: none;
}

.setting-info {
  flex: 1;
}

.setting-title {
  font-size: 18px;
  font-weight: 700;
  color: #333;
  margin-bottom: 4px;
}

.setting-desc {
  font-size: 14px;
  color: #999;
}

.setting-select {
  padding: 10px 16px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #666;
  background: #ffffff;
  cursor: pointer;
  transition: all 0.3s ease;
}

.setting-select:focus {
  border-color: #f093fb;
  outline: none;
}

/* 开关按钮 */
.switch {
  position: relative;
  display: inline-block;
  width: 60px;
  height: 34px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: 0.4s;
  border-radius: 34px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 26px;
  width: 26px;
  left: 4px;
  bottom: 4px;
  background-color: white;
  transition: 0.4s;
  border-radius: 50%;
}

input:checked + .slider {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

input:checked + .slider:before {
  transform: translateX(26px);
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
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
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

.anime-input {
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.anime-input:focus {
  border-color: #f093fb;
  box-shadow: 0 0 0 3px rgba(240, 147, 251, 0.1);
}

.file-input {
  width: 100%;
  padding: 10px;
  border: 2px dashed #e0e0e0;
  border-radius: 8px;
  cursor: pointer;
}

.warning-box {
  background: #fff3cd;
  border-left: 4px solid #ffc107;
  padding: 16px;
  border-radius: 4px;
  margin-top: 16px;
}

.warning-box p {
  color: #856404;
  font-size: 14px;
  margin: 0;
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

.button.is-success {
  background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%);
  border: none;
  color: #ffffff;
}

.button.is-success:hover {
  box-shadow: 0 4px 12px rgba(39, 174, 96, 0.3);
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
  
  .table-header, .table-row {
    flex-direction: column;
  }
  
  .th, .td {
    flex: 1 !important;
    padding: 8px 0;
  }
  
  .management-cards {
    grid-template-columns: 1fr;
  }
}
</style>


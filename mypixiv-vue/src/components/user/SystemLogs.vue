<template>
  <div class="anime-logs-container">
    <div class="logs-header">
      <h2 class="title anime-gradient-text">
        <span class="icon">📋</span> 系统日志
      </h2>
      <p class="subtitle">系统操作记录</p>
    </div>

    <!-- 刷新按钮 -->
    <div class="control-bar">
      <button 
        class="button is-info anime-button"
        @click="fetchLogs"
        :disabled="loading"
        :class="{ 'is-loading': loading }"
      >
        <span class="icon">🔄</span>
        <span>刷新日志</span>
      </button>
      <div class="log-count">
        <span class="tag is-medium is-info">
          共 {{ logs.length }} 条记录
        </span>
      </div>
    </div>

    <!-- 日志列表 -->
    <div v-if="logs.length > 0" class="logs-list">
      <div 
        v-for="(log, index) in logs" 
        :key="index" 
        class="log-item anime-card"
      >
        <div class="log-header">
          <div 
            class="log-operator clickable-operator" 
            @click="copyIdToClipboard(log.operatorId, '操作者ID')"
            title="点击复制操作者ID"
          >
            <span class="icon">👤</span>
            <span class="operator-id">{{ log.operatorId }}</span>
            <span class="copy-icon-log">📋</span>
          </div>
          <div class="log-time">
            <span class="icon">🕐</span>
            <span>{{ formatTime(log.time) }}</span>
          </div>
        </div>
        <div class="log-description">
          <span class="icon">📝</span>
          <span>{{ log.description }}</span>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else-if="!loading" class="empty-state">
      <div class="has-text-centered">
        <span class="icon is-large" style="font-size: 4rem;">📭</span>
        <p class="title is-4" style="margin-top: 1rem;">暂无日志记录</p>
        <p class="subtitle is-6">系统还没有任何操作日志</p>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading && logs.length === 0" class="loading-state">
      <div class="has-text-centered">
        <span class="icon is-large" style="font-size: 3rem;">⏳</span>
        <p class="title is-5" style="margin-top: 1rem;">加载中...</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import copyIdMixin from '@/mixins/copyId';

export default {
  name: 'SystemLogs',
  mixins: [copyIdMixin],
  data() {
    return {
      logs: [],
      loading: false
    };
  },
  created() {
    this.fetchLogs();
  },
  methods: {
    async fetchLogs() {
      this.loading = true;
      try {
        const response = await axios.get('/api/systemAdmin/logs');
        if (response.data.code === 1) {
          this.logs = response.data.data || [];
          // 按时间倒序排列（最新的在前面）
          this.logs.sort((a, b) => new Date(b.time) - new Date(a.time));
        } else {
          this.$message.error('获取日志失败');
        }
      } catch (error) {
        console.error('获取日志失败:', error);
        this.$message.error('获取日志失败');
      } finally {
        this.loading = false;
      }
    },

    formatTime(timeString) {
      if (!timeString) return '-';
      
      try {
        const date = new Date(timeString);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        const seconds = String(date.getSeconds()).padStart(2, '0');
        
        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
      } catch (error) {
        console.error('时间格式化失败:', error);
        return timeString;
      }
    }
  }
};
</script>

<style scoped>
.anime-logs-container {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.logs-header {
  text-align: center;
  margin-bottom: 3rem;
  padding: 2rem;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(240, 248, 255, 0.9));
  border-radius: 15px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.anime-gradient-text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: bold;
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

.anime-gradient-text .icon {
  -webkit-text-fill-color: initial;
  background: none;
}

.subtitle {
  color: #666;
  font-size: 1rem;
}

.control-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding: 1rem;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.anime-button {
  border-radius: 8px;
  font-weight: 600;
  transition: all 0.3s ease;
  border: none;
}

.anime-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.log-count {
  display: flex;
  align-items: center;
}

.logs-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.log-item {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  animation: slideIn 0.5s ease;
  border-left: 4px solid #667eea;
}

.log-item:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
  transform: translateX(5px);
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.log-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid #f0f0f0;
}

.log-operator {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 600;
  color: #333;
}

.clickable-operator {
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 8px;
  background: rgba(0, 0, 0, 0.02);
  transition: all 0.3s ease;
  user-select: none;
}

.clickable-operator:hover {
  background: rgba(102, 126, 234, 0.15);
  transform: translateX(2px);
}

.clickable-operator:active {
  transform: scale(0.98);
}

.copy-icon-log {
  opacity: 0;
  transition: opacity 0.3s ease;
  font-size: 12px;
  margin-left: 4px;
}

.clickable-operator:hover .copy-icon-log {
  opacity: 1;
}

.operator-id {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: bold;
  font-family: 'Courier New', monospace;
}

.log-time {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #999;
  font-size: 0.9rem;
}

.log-description {
  display: flex;
  align-items: flex-start;
  gap: 0.5rem;
  color: #555;
  line-height: 1.6;
}

.log-description .icon {
  margin-top: 0.2rem;
}

.empty-state {
  padding: 4rem 2rem;
  text-align: center;
  background: white;
  border-radius: 15px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-state .icon {
  color: #ddd;
}

.empty-state .title {
  color: #999;
}

.empty-state .subtitle {
  color: #bbb;
}

.loading-state {
  padding: 4rem 2rem;
  text-align: center;
  background: white;
  border-radius: 15px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-state .icon {
  color: #667eea;
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}
</style>


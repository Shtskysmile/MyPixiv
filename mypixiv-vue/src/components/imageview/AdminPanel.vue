<template>
  <div>
    <!-- 审核区域（仅社区管理员且作品待审核时显示） -->
    <div v-if="isCommunityAdmin && contribution.auditStatus === 0" class="admin-panel audit-section">
      <div class="panel-header audit-header">
        <h4 class="panel-title audit-title">
          <span class="icon">🛡️</span> 作品审核
        </h4>
        <span class="status-badge audit-status-badge" :class="getAuditStatusClass(contribution.auditStatus)">
          {{ getAuditStatusText(contribution.auditStatus) }}
        </span>
      </div>

      <!-- 驳回理由显示 -->
      <div v-if="contribution.auditStatus === 2 && contribution.dismissalReason" class="dismissal-info">
        <p class="dismissal-label">
          <span class="icon">📝</span> 驳回理由:
        </p>
        <p class="dismissal-text">{{ contribution.dismissalReason }}</p>
      </div>

      <!-- 审核操作按钮（仅待审核状态显示） -->
      <div v-if="contribution.auditStatus === 0" class="panel-actions audit-actions">
        <button 
          class="button is-success anime-button-action approve-btn"
          @click="$emit('approve-work')"
          :disabled="auditLoading"
        >
          <span class="icon">✅</span>
          <span>{{ auditLoading ? '处理中...' : '审核通过' }}</span>
        </button>
        <button 
          class="button is-danger anime-button-action dismiss-btn"
          @click="$emit('show-dismiss-modal')"
          :disabled="auditLoading"
        >
          <span class="icon">❌</span>
          <span>驳回作品</span>
        </button>
      </div>

      <!-- 已审核提示 -->
      <div v-else class="audit-completed-notice">
        <p v-if="contribution.auditStatus === 1" class="approved-notice">
          <span class="icon">✅</span> 该作品已审核通过
        </p>
        <p v-else-if="contribution.auditStatus === 2" class="dismissed-notice">
          <span class="icon">❌</span> 该作品已被驳回
        </p>
      </div>
    </div>

    <!-- 封禁管理区域（仅社区管理员且作品已通过审核时显示） -->
    <div v-if="isCommunityAdmin && contribution.auditStatus === 1" class="admin-panel block-section">
      <div class="panel-header block-header">
        <h4 class="panel-title block-title">
          <span class="icon">🔒</span> 作品封禁管理
        </h4>
        <span class="status-badge block-status-badge" :class="contribution.status === 1 ? 'status-blocked' : 'status-normal'">
          {{ contribution.status === 1 ? '🚫 已封禁' : '✅ 正常' }}
        </span>
      </div>

      <!-- 封禁/解封操作按钮 -->
      <div class="panel-actions block-actions">
        <button 
          v-if="contribution.status === 0"
          class="button is-danger anime-button-action block-btn"
          @click="$emit('block-work')"
          :disabled="blockLoading"
        >
          <span class="icon">🚫</span>
          <span>{{ blockLoading ? '处理中...' : '封禁作品' }}</span>
        </button>
        <button 
          v-else
          class="button is-success anime-button-action unblock-btn"
          @click="$emit('unblock-work')"
          :disabled="blockLoading"
        >
          <span class="icon">🔓</span>
          <span>{{ blockLoading ? '处理中...' : '解封作品' }}</span>
        </button>
      </div>

      <p class="panel-hint block-hint">
        <span class="icon">💡</span>
        {{ contribution.status === 1 ? '作品已被封禁，用户无法在平台上查看' : '封禁后用户将无法查看该作品' }}
      </p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AdminPanel',
  props: {
    contribution: {
      type: Object,
      required: true
    },
    isCommunityAdmin: {
      type: Boolean,
      default: false
    },
    auditLoading: {
      type: Boolean,
      default: false
    },
    blockLoading: {
      type: Boolean,
      default: false
    }
  },
  methods: {
    getAuditStatusText(status) {
      const statusMap = {
        0: '⏳ 待审核',
        1: '✅ 已通过',
        2: '❌ 已驳回'
      };
      return statusMap[status] || '未知';
    },
    
    getAuditStatusClass(status) {
      return {
        'status-pending': status === 0,
        'status-approved': status === 1,
        'status-dismissed': status === 2
      };
    }
  }
};
</script>

<style scoped>
.admin-panel {
  margin-top: 24px;
  padding: 24px;
  border-radius: 20px;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.1);
  animation: slideIn 0.5s ease;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 审核区域 */
.audit-section {
  background: linear-gradient(135deg, #ede9fe 0%, #ddd6fe 100%);
  border: 3px solid rgba(147, 51, 234, 0.3);
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 2px solid rgba(0, 0, 0, 0.1);
}

.panel-title {
  font-size: 1.3rem;
  font-weight: 900;
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0;
}

.audit-title {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.status-badge {
  padding: 8px 18px;
  border-radius: 16px;
  font-size: 14px;
  font-weight: 800;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 3px 12px rgba(0, 0, 0, 0.15);
  animation: pulse 2s ease infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.audit-status-badge.status-pending {
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
  color: white;
}

.audit-status-badge.status-approved {
  background: linear-gradient(135deg, #34d399 0%, #10b981 100%);
  color: white;
}

.audit-status-badge.status-dismissed {
  background: linear-gradient(135deg, #f87171 0%, #ef4444 100%);
  color: white;
}

.dismissal-info {
  margin-bottom: 20px;
  padding: 16px;
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
  border-radius: 16px;
  border-left: 5px solid #ef4444;
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.2);
}

.dismissal-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 800;
  color: #dc2626;
  margin-bottom: 10px;
  font-size: 1rem;
}

.dismissal-text {
  color: #7f1d1d;
  line-height: 1.7;
  margin: 0;
  font-size: 0.95rem;
}

.panel-actions {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.anime-button-action {
  flex: 1;
  min-width: 200px;
  padding: 16px 32px;
  font-weight: 800;
  font-size: 16px;
  border-radius: 16px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.anime-button-action:hover:not(:disabled) {
  transform: translateY(-4px);
  box-shadow: 0 8px 28px rgba(0, 0, 0, 0.25);
}

.anime-button-action:active:not(:disabled) {
  transform: translateY(-2px);
}

.approve-btn {
  background: linear-gradient(135deg, #34d399 0%, #10b981 100%);
  color: white;
}

.dismiss-btn {
  background: linear-gradient(135deg, #f87171 0%, #ef4444 100%);
  color: white;
}

.audit-completed-notice {
  padding: 20px;
  border-radius: 16px;
  text-align: center;
  background: rgba(255, 255, 255, 0.6);
}

.approved-notice,
.dismissed-notice {
  font-weight: 800;
  font-size: 1.1rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin: 0;
}

.approved-notice {
  color: #059669;
}

.dismissed-notice {
  color: #dc2626;
}

/* 封禁管理区域 */
.block-section {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border: 3px solid rgba(251, 191, 36, 0.4);
}

.block-title {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.block-status-badge.status-blocked {
  background: linear-gradient(135deg, #f87171 0%, #ef4444 100%);
  color: white;
}

.block-status-badge.status-normal {
  background: linear-gradient(135deg, #34d399 0%, #10b981 100%);
  color: white;
}

.block-btn {
  background: linear-gradient(135deg, #f87171 0%, #ef4444 100%);
  color: white;
}

.unblock-btn {
  background: linear-gradient(135deg, #34d399 0%, #10b981 100%);
  color: white;
}

.panel-hint {
  color: #92400e;
  font-size: 14px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

@media (max-width: 768px) {
  .admin-panel {
    padding: 20px;
  }
  
  .panel-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .panel-actions {
    flex-direction: column;
  }
  
  .anime-button-action {
    min-width: 100%;
  }
}
</style>


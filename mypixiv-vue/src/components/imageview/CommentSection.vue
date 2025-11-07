<template>
  <div class="comment-section anime-box">
    <h3 class="title is-5 anime-title-small">
      <span class="icon">💬</span>
      评论区
      <span class="comment-count-badge">{{ comments.length }}</span>
    </h3>
    
    <!-- 评论列表 -->
    <div v-if="comments.length" class="comments-list">
      <div v-for="(c, idx) in comments" :key="idx" class="comment-item">
        <div class="comment-header">
          <div class="comment-user-info">
            <img 
              :src="getCommentAvatarUrl(c)" 
              class="comment-avatar"
              @error="onAvatarError"
            />
            <div class="comment-info">
              <strong class="comment-author">{{ c.authorName || c.author || '匿名用户' }}</strong>
              <span class="comment-time">{{ formatTime(c.time) }}</span>
            </div>
          </div>
          <!-- 社区管理员删除按钮 -->
          <button 
            v-if="isCommunityAdmin" 
            class="button is-small is-danger delete-comment-btn anime-button"
            @click="$emit('delete-comment', c)"
            title="删除此评论"
          >
            <span class="icon">🗑️</span>
            <span>删除</span>
          </button>
        </div>
        <div class="comment-content">
          {{ c.description }}
        </div>
      </div>
    </div>
    
    <!-- 无评论状态 -->
    <div v-else class="empty-comments">
      <div class="empty-icon">💭</div>
      <p class="empty-text">还没有评论，快来抢沙发吧！</p>
    </div>

    <!-- 评论输入框 -->
    <div class="comment-input-section">
      <div class="comment-input-header">
        <span class="icon">✍️</span>
        <span class="input-label">发表你的看法</span>
      </div>
      <textarea 
        class="textarea anime-textarea" 
        v-model="localComment"
        :placeholder="(isCommunityAdmin || isSystemAdmin) ? '管理员无法发表评论' : '说点什么吧...'"
        rows="4"
        :disabled="isCommunityAdmin || isSystemAdmin"
      ></textarea>
      <button 
        class="button anime-button is-primary submit-btn" 
        @click="handleSubmit"
        :disabled="!localComment.trim() || isCommunityAdmin || isSystemAdmin"
      >
        <span class="icon">📤</span>
        <span>发送评论</span>
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CommentSection',
  props: {
    comments: {
      type: Array,
      default: () => []
    },
    isCommunityAdmin: {
      type: Boolean,
      default: false
    },
    isSystemAdmin: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      localComment: ''
    };
  },
  methods: {
    handleSubmit() {
      if (this.localComment.trim()) {
        this.$emit('submit-comment', this.localComment);
        this.localComment = '';
      }
    },
    
    formatTime(time) {
      if (!time) return '';
      const date = new Date(time);
      if (isNaN(date.getTime())) return time;
      
      const now = new Date();
      const diff = now - date;
      const seconds = Math.floor(diff / 1000);
      const minutes = Math.floor(seconds / 60);
      const hours = Math.floor(minutes / 60);
      const days = Math.floor(hours / 24);
      
      if (days > 0) {
        return `${days}天前`;
      } else if (hours > 0) {
        return `${hours}小时前`;
      } else if (minutes > 0) {
        return `${minutes}分钟前`;
      } else {
        return '刚刚';
      }
    },
    
    getCommentAvatarUrl(comment) {
      const avatarPath = comment.avatar;
      
      if (!avatarPath) {
        return 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="36" height="36"%3E%3Crect fill="%23ddd" width="36" height="36"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999" font-size="18"%3EU%3C/text%3E%3C/svg%3E';
      }
      
      if (avatarPath.startsWith('http://') || avatarPath.startsWith('https://')) {
        return avatarPath;
      }
      
      const baseURL = process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080';
      const fullPath = avatarPath.startsWith('/') ? avatarPath : `/${avatarPath}`;
      return `${baseURL}${fullPath}`;
    },
    
    onAvatarError(e) {
      e.target.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="36" height="36"%3E%3Crect fill="%23ddd" width="36" height="36"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999" font-size="18"%3EU%3C/text%3E%3C/svg%3E';
    }
  }
};
</script>

<style scoped>
.comment-section {
  padding: 24px;
  background: rgba(255, 255, 255, 0.98) !important;
  border: 2px solid rgba(147, 51, 234, 0.1);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(147, 51, 234, 0.12);
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.anime-title-small {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-weight: 900;
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 0;
}

.comment-count-badge {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 700;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

/* 评论列表 */
.comments-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-height: 600px;
  overflow-y: auto;
  padding-right: 8px;
}

.comments-list::-webkit-scrollbar {
  width: 6px;
}

.comments-list::-webkit-scrollbar-track {
  background: rgba(102, 126, 234, 0.05);
  border-radius: 3px;
}

.comments-list::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 3px;
}

.comment-item {
  padding: 16px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.03) 0%, rgba(118, 75, 162, 0.03) 100%);
  border-radius: 16px;
  border-left: 4px solid #a78bfa;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.comment-item:hover {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.06) 0%, rgba(118, 75, 162, 0.06) 100%);
  transform: translateX(6px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
  border-left-color: #667eea;
}

.comment-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.comment-user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.comment-avatar {
  width: 44px !important;
  height: 44px !important;
  border-radius: 50%;
  border: 3px solid rgba(102, 126, 234, 0.2);
  object-fit: cover;
  display: block;
  flex-shrink: 0;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.comment-avatar:hover {
  transform: scale(1.1);
  border-color: #667eea;
}

.comment-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.comment-author {
  color: #6366f1;
  font-size: 15px;
  font-weight: 700;
}

.comment-time {
  color: #9ca3af;
  font-size: 12px;
  font-weight: 500;
}

.delete-comment-btn {
  flex-shrink: 0;
  border-radius: 10px;
  transition: all 0.2s ease;
  opacity: 0;
}

.comment-item:hover .delete-comment-btn {
  opacity: 1;
}

.delete-comment-btn:hover {
  transform: scale(1.05);
}

.comment-content {
  color: #374151;
  font-size: 14px;
  line-height: 1.7;
  padding-left: 56px;
  word-wrap: break-word;
}

/* 空状态 */
.empty-comments {
  padding: 60px 20px;
  text-align: center;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.03) 0%, rgba(118, 75, 162, 0.03) 100%);
  border-radius: 16px;
  border: 2px dashed rgba(102, 126, 234, 0.2);
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.empty-text {
  color: #6b7280;
  font-size: 16px;
  font-weight: 600;
}

/* 评论输入区 */
.comment-input-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 20px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  border-radius: 16px;
  border: 2px solid rgba(102, 126, 234, 0.1);
}

.comment-input-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 700;
  color: #6366f1;
  font-size: 1rem;
}

.anime-textarea {
  border: 2px solid rgba(147, 51, 234, 0.2);
  border-radius: 12px;
  transition: all 0.3s ease;
  font-size: 14px;
  resize: vertical;
  font-family: inherit;
  padding: 12px;
}

.anime-textarea:focus {
  border-color: #a78bfa;
  box-shadow: 0 0 0 4px rgba(167, 139, 250, 0.1);
  outline: none;
}

.anime-textarea:disabled {
  background-color: #f3f4f6;
  cursor: not-allowed;
}

.submit-btn {
  align-self: flex-end;
  padding: 12px 28px;
  border-radius: 12px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.submit-btn:active:not(:disabled) {
  transform: translateY(0);
}

.submit-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background: linear-gradient(135deg, #ccc 0%, #999 100%);
}

.anime-button {
  display: flex;
  align-items: center;
  gap: 8px;
}

@media (max-width: 768px) {
  .comment-content {
    padding-left: 0;
    margin-top: 8px;
  }
  
  .comment-user-info {
    flex-wrap: wrap;
  }
  
  .comment-input-section {
    padding: 16px;
  }
  
  .submit-btn {
    width: 100%;
    justify-content: center;
  }
}
</style>


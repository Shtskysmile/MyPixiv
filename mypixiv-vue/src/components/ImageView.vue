<template>
  <div class="image-view-root">
    <Navbar />

    <section class="section">
      <div class="container">
        <div class="columns">
          <div class="column is-two-thirds">
            <!-- 主图区 -->
            <div class="box anime-box image-box">
              <img 
                :src="currentImage" 
                alt="artwork" 
                class="main-image" 
                @click="openModal(currentImage)" 
                style="cursor: zoom-in;" 
              />
              
              <!-- 漫画翻页控件 -->
              <div v-if="isManga && totalPages > 1" class="manga-pagination">
                <button 
                  class="page-btn prev-btn anime-button"
                  :disabled="!canPrevPage"
                  @click="prevPage"
                >
                  <span class="icon">◀</span>
                  <span>上一页</span>
                </button>
                
                <div class="page-indicator">
                  <span class="current-page">{{ currentImageIndex + 1 }}</span>
                  <span class="page-separator">/</span>
                  <span class="total-pages">{{ totalPages }}</span>
                </div>
                
                <button 
                  class="page-btn next-btn anime-button"
                  :disabled="!canNextPage"
                  @click="nextPage"
                >
                  <span>下一页</span>
                  <span class="icon">▶</span>
                </button>
              </div>
              
              <!-- 漫画页码缩略图导航 -->
              <div v-if="isManga && totalPages > 1" class="page-thumbnails">
                <div 
                  v-for="(img, idx) in contribution.images" 
                  :key="idx"
                  class="thumbnail-item"
                  :class="{ 'active': idx === currentImageIndex }"
                  @click="goToPage(idx)"
                >
                  <img :src="img" :alt="`Page ${idx + 1}`" />
                  <span class="thumbnail-number">{{ idx + 1 }}</span>
                </div>
              </div>
            </div>

            <!-- 作品信息区 -->
            <div class="box anime-box">
              <h2 class="title is-4 anime-title-small">{{ contribution.title || '无标题' }}</h2>
              
              <!-- 作者信息 -->
              <div class="media">
                <div class="media-left">
                  <figure class="image is-64x64">
                    <img 
                      :src="contribution.uploaderAvatarPath" 
                      alt="author" 
                      class="is-rounded anime-avatar"
                      @error="onAvatarError"
                    />
                  </figure>
                </div>
                <div class="media-content">
                  <p class="subtitle is-6 author-name">
                    <strong>作者 ID:</strong> {{ formatAuthorId(contribution.authorId) }}
                  </p>
                  <p class="is-size-7 publish-time">
                    <span class="icon">📅</span>
                    发布于 {{ formatTime(contribution.publishTime) }}
                  </p>
                </div>
                <div class="media-right">
                  <div class="buttons">
                    <button 
                      class="button anime-button"
                      :class="{ 'is-danger': isLiked, 'is-light': !isLiked }"
                      @click="toggleLike"
                    >
                      <span class="icon">{{ isLiked ? '❤️' : '🤍' }}</span>
                      <span>{{ isLiked ? '已点赞' : '点赞' }}</span>
                    </button>
                    <button 
                      class="button anime-button"
                      :class="{ 'is-warning': isFavorite, 'is-light': !isFavorite }"
                      @click="toggleFavorite"
                    >
                      <span class="icon">{{ isFavorite ? '⭐' : '☆' }}</span>
                      <span>{{ isFavorite ? '已收藏' : '收藏' }}</span>
                    </button>
                    <a 
                      :href="contribution.image" 
                      class="button anime-button is-link is-light" 
                      target="_blank" 
                      download
                    >
                      <span class="icon">📥</span>
                      <span>下载</span>
                    </a>
                  </div>
                </div>
              </div>

              <!-- 统计信息 -->
              <div class="stats-bar">
                <div class="stat-item">
                  <span class="icon">👁️</span>
                  <span class="stat-label">浏览</span>
                  <span class="stat-value">{{ formatCount(contribution.viewCount) }}</span>
                </div>
                <div class="stat-item">
                  <span class="icon">❤️</span>
                  <span class="stat-label">点赞</span>
                  <span class="stat-value">{{ formatCount(contribution.likeCount) }}</span>
                </div>
                <div class="stat-item">
                  <span class="icon">⭐</span>
                  <span class="stat-label">收藏</span>
                  <span class="stat-value">{{ formatCount(contribution.favoriteCount) }}</span>
                </div>
                <div class="stat-item">
                  <span class="icon">💬</span>
                  <span class="stat-label">评论</span>
                  <span class="stat-value">{{ formatCount(contribution.commentCount) }}</span>
                </div>
              </div>

              <!-- 作品描述 -->
              <div class="content description-box" v-if="contribution.description">
                <h4 class="description-title">作品描述</h4>
                <p>{{ contribution.description }}</p>
              </div>

              <!-- 作品类型和状态 -->
              <div class="tags-section">
                <span class="tag is-info is-light">
                  {{ contribution.type === 0 ? '插画' : '漫画' }}
                </span>
                <span 
                  class="tag" 
                  :class="{
                    'is-success': contribution.auditStatus === 1,
                    'is-warning': contribution.auditStatus === 0,
                    'is-danger': contribution.auditStatus === 2
                  }"
                >
                  {{ getAuditStatus(contribution.auditStatus) }}
                </span>
                <span v-if="contribution.dismissalReason" class="tag is-danger is-light">
                  驳回原因: {{ contribution.dismissalReason }}
                </span>
              </div>
            </div>
          </div>

          <!-- 评论区 -->
          <div class="column">
            <div class="box anime-box">
              <h3 class="title is-5 anime-title-small">
                <span class="icon">💬</span>
                评论 ({{ comments.length }})
              </h3>
              
              <!-- 评论列表 -->
              <div v-if="comments.length" class="comments-list">
                <div v-for="(c, idx) in comments" :key="idx" class="comment-item">
                  <div class="comment-header">
                    <img 
                      :src="c.avatar" 
                      class="comment-avatar"
                      @error="onAvatarError"
                    />
                    <div class="comment-info">
                      <strong class="comment-author">{{ c.author }}</strong>
                      <span class="comment-time">{{ formatTime(c.time) }}</span>
                    </div>
                  </div>
                  <div class="comment-content">
                    {{ c.description }}
                  </div>
                </div>
              </div>
              
              <!-- 无评论状态 -->
              <div v-else class="empty-comments">
                <p class="has-text-centered has-text-grey-light">
                  <span class="icon is-large">💭</span>
                </p>
                <p class="has-text-centered has-text-grey">暂无评论，来抢沙发吧！</p>
              </div>

              <!-- 评论输入框 -->
              <div class="comment-input-box">
                <textarea 
                  class="textarea anime-textarea" 
                  v-model="newComment"
                  placeholder="发表你的看法..."
                  rows="3"
                ></textarea>
                <button 
                  class="button anime-button is-primary" 
                  @click="submitComment"
                  :disabled="!newComment.trim()"
                >
                  <span class="icon">📝</span>
                  <span>发送评论</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 图片放大模态框 -->
    <div v-if="showModal" class="image-modal" @click.self="closeModal">
      <button class="close-btn anime-button" @click="closeModal">✕ 关闭</button>
      <div class="modal-content">
        <img :src="modalImageSrc" alt="modal-image" />
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import Navbar from './Navbar.vue';

export default {
  name: 'ImageView',
  components: { Navbar },
  props: {
    id: {
      type: [String, Number],
      default: null,
    },
  },
  data() {
    return {
      // 对齐后端 R_Contribution 结构
      contribution: {
        contributionId: '',
        type: 0,
        title: '',
        image: '', // 插画使用单张图片
        images: [], // 漫画使用多张图片数组
        description: '',
        status: 0,
        auditStatus: 1,
        publishTime: '',
        authorId: '',
        uploaderAvatarPath: '',
        viewCount: 0,
        favoriteCount: 0,
        likeCount: 0,
        commentCount: 0,
        dismissalReason: null,
      },
      comments: [], // R_ContributionComment[]
      isLiked: false,
      isFavorite: false,
      newComment: '',
      showModal: false,
      modalImageSrc: '',
      loading: false,
      currentImageIndex: 0, // 当前显示的图片索引（漫画多图翻页）
    };
  },
  computed: {
    // 当前显示的图片URL
    currentImage() {
      if (this.contribution.type === 1 && this.contribution.images && this.contribution.images.length > 0) {
        // 漫画：返回当前索引的图片
        return this.contribution.images[this.currentImageIndex] || '';
      } else {
        // 插画：返回单张图片
        return this.contribution.image || '';
      }
    },
    // 是否为漫画
    isManga() {
      return this.contribution.type === 1;
    },
    // 漫画总页数
    totalPages() {
      return this.isManga ? (this.contribution.images?.length || 0) : 1;
    },
    // 是否可以上一页
    canPrevPage() {
      return this.isManga && this.currentImageIndex > 0;
    },
    // 是否可以下一页
    canNextPage() {
      return this.isManga && this.currentImageIndex < this.totalPages - 1;
    }
  },
  created() {
    const imgId = this.id || this.$route.params.id || 1;
    this.fetchContribution(imgId);
  },
  methods: {
    fetchContribution(id) {
      this.loading = true;
      const token = localStorage.getItem('token') || 'mock-token-123';
      const params = new URLSearchParams();
      params.append('contributionId', id);

      // 使用后端接口 POST /api/contribution，返回 Result<R_ContributionDTO>
      axios.post('/api/contribution', params, {
        headers: {
          'Authorization': 'Bearer ' + token
        }
      })
        .then((res) => {
          if (res.data && res.data.code === 200) {
            const data = res.data.data;
            // 对齐 R_ContributionDTO 结构
            this.contribution = data.contribution || {};
            this.comments = data.comments || [];
            this.isLiked = data.isLiked || false;
            this.isFavorite = data.isFavorite || false;
            
            // 如果是漫画且有多张图片，初始化当前页
            if (this.contribution.type === 1 && this.contribution.images && this.contribution.images.length > 0) {
              this.currentImageIndex = 0;
            }
          } else {
            console.error('获取作品详情失败:', res.data?.message);
          }
        })
        .catch((err) => {
          console.error('请求失败:', err);
        })
        .finally(() => {
          this.loading = false;
        });
    },
    
    toggleLike() {
      // TODO: 调用后端接口 POST /user/likeContribution 或 /user/unlikeContribution
      this.isLiked = !this.isLiked;
      if (this.isLiked) {
        this.contribution.likeCount++;
      } else {
        this.contribution.likeCount--;
      }
    },
    
    toggleFavorite() {
      // TODO: 调用后端接口 POST /user/favoriteContribution 或 /user/unfavoriteContribution
      this.isFavorite = !this.isFavorite;
      if (this.isFavorite) {
        this.contribution.favoriteCount++;
      } else {
        this.contribution.favoriteCount--;
      }
    },
    
    submitComment() {
      if (!this.newComment.trim()) return;
      
      // TODO: 调用后端接口 POST /user/commentContribution
      const newCommentObj = {
        author: '当前用户',
        description: this.newComment,
        time: new Date().toISOString(),
        avatar: '/static/default-avatar.png'
      };
      
      this.comments.push(newCommentObj);
      this.contribution.commentCount++;
      this.newComment = '';
      
      alert('评论成功！');
    },
    
    openModal(src) {
      this.modalImageSrc = src;
      this.showModal = true;
    },
    
    closeModal() {
      this.showModal = false;
      this.modalImageSrc = '';
    },
    
    // 漫画翻页功能
    prevPage() {
      if (this.canPrevPage) {
        this.currentImageIndex--;
      }
    },
    
    nextPage() {
      if (this.canNextPage) {
        this.currentImageIndex++;
      }
    },
    
    goToPage(index) {
      if (index >= 0 && index < this.totalPages) {
        this.currentImageIndex = index;
      }
    },
    
    formatCount(count) {
      if (!count) return 0;
      if (count >= 10000) {
        return (count / 10000).toFixed(1) + 'w';
      } else if (count >= 1000) {
        return (count / 1000).toFixed(1) + 'k';
      }
      return count;
    },
    
    formatAuthorId(authorId) {
      if (!authorId) return '匿名';
      return authorId.length > 12 ? authorId.substring(0, 12) + '...' : authorId;
    },
    
    formatTime(time) {
      if (!time) return '';
      // 简单格式化时间
      const date = new Date(time);
      if (isNaN(date.getTime())) return time;
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    },
    
    getAuditStatus(status) {
      const statusMap = {
        0: '待审核',
        1: '已通过',
        2: '已驳回'
      };
      return statusMap[status] || '未知';
    },
    
    onAvatarError(e) {
      e.target.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="64" height="64"%3E%3Crect fill="%23ddd" width="64" height="64"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999" font-size="24"%3EU%3C/text%3E%3C/svg%3E';
    }
  },
};
</script>

<style scoped>
.image-view-root {
  background: #f8f9fa;
  min-height: 100vh;
}

.anime-box {
  background: rgba(255, 255, 255, 0.98) !important;
  border: 2px solid rgba(147, 51, 234, 0.1);
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(147, 51, 234, 0.08);
  transition: all 0.3s ease;
}

.anime-box:hover {
  box-shadow: 0 8px 24px rgba(147, 51, 234, 0.15);
}

.image-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%) !important;
  position: relative;
}

.main-image {
  width: 100%;
  max-height: 70vh;
  object-fit: contain;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  transition: transform 0.3s ease;
}

.main-image:hover {
  transform: scale(1.02);
}

.anime-title-small {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-weight: 800;
  display: flex;
  align-items: center;
  gap: 8px;
}

.anime-avatar {
  border: 3px solid #a78bfa !important;
  box-shadow: 0 4px 12px rgba(167, 139, 250, 0.3);
}

.author-name {
  color: #6366f1;
  font-weight: 600;
}

.publish-time {
  color: #6b7280;
  display: flex;
  align-items: center;
  gap: 4px;
}

.anime-button {
  border-radius: 12px;
  font-weight: 600;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.anime-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(147, 51, 234, 0.2);
}

.stats-bar {
  display: flex;
  justify-content: space-around;
  padding: 20px;
  margin: 20px 0;
  background: linear-gradient(135deg, #667eea15 0%, #764ba215 100%);
  border-radius: 12px;
  border: 2px solid rgba(147, 51, 234, 0.1);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat-item .icon {
  font-size: 24px;
}

.stat-label {
  font-size: 12px;
  color: #6b7280;
  font-weight: 500;
}

.stat-value {
  font-size: 18px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.description-box {
  margin-top: 20px;
  padding: 16px;
  background: rgba(147, 51, 234, 0.03);
  border-radius: 12px;
  border-left: 4px solid #9333ea;
}

.description-title {
  font-weight: 700;
  color: #6366f1;
  margin-bottom: 8px;
}

.tags-section {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 16px;
}

/* 评论区样式 */
.comments-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin: 20px 0;
  max-height: 500px;
  overflow-y: auto;
}

.comment-item {
  padding: 12px;
  background: rgba(147, 51, 234, 0.03);
  border-radius: 12px;
  border-left: 3px solid #a78bfa;
  transition: all 0.2s ease;
}

.comment-item:hover {
  background: rgba(147, 51, 234, 0.06);
  transform: translateX(4px);
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.comment-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 2px solid #e0e7ff;
}

.comment-info {
  display: flex;
  flex-direction: column;
}

.comment-author {
  color: #6366f1;
  font-size: 14px;
}

.comment-time {
  color: #9ca3af;
  font-size: 12px;
}

.comment-content {
  color: #374151;
  font-size: 14px;
  line-height: 1.6;
  padding-left: 46px;
}

.empty-comments {
  padding: 40px 20px;
  text-align: center;
}

.empty-comments .icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.comment-input-box {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.anime-textarea {
  border: 2px solid rgba(147, 51, 234, 0.2);
  border-radius: 12px;
  transition: all 0.3s ease;
  font-size: 14px;
}

.anime-textarea:focus {
  border-color: #a78bfa;
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.1);
}

/* 模态框样式 */
.image-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-content {
  max-width: 95%;
  max-height: 95%;
  animation: zoomIn 0.3s ease;
}

@keyframes zoomIn {
  from { transform: scale(0.8); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}

.modal-content img {
  width: auto;
  height: auto;
  max-width: 100%;
  max-height: 90vh;
  border-radius: 8px;
}

.close-btn {
  position: absolute;
  top: 24px;
  right: 24px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.3);
  color: #fff;
  padding: 12px 20px;
  border-radius: 12px;
  cursor: pointer;
  font-weight: 600;
  z-index: 2001;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* 漫画翻页控件样式 */
.manga-pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 24px;
  margin-top: 20px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.page-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.page-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

.page-btn:disabled {
  background: linear-gradient(135deg, #ccc 0%, #999 100%);
  cursor: not-allowed;
  opacity: 0.5;
}

.page-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: #667eea;
}

.current-page {
  font-size: 24px;
  color: #764ba2;
}

.page-separator {
  color: #999;
}

.total-pages {
  color: #667eea;
}

/* 漫画页码缩略图导航 */
.page-thumbnails {
  display: flex;
  gap: 12px;
  margin-top: 16px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  overflow-x: auto;
  max-width: 100%;
}

.page-thumbnails::-webkit-scrollbar {
  height: 6px;
}

.page-thumbnails::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 3px;
}

.page-thumbnails::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 3px;
}

.thumbnail-item {
  position: relative;
  flex-shrink: 0;
  width: 80px;
  height: 100px;
  cursor: pointer;
  border: 3px solid transparent;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
  background: #f0f0f0;
}

.thumbnail-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.3);
}

.thumbnail-item.active {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.5);
}

.thumbnail-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumbnail-number {
  position: absolute;
  bottom: 4px;
  right: 4px;
  background: rgba(102, 126, 234, 0.9);
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

@media (max-width: 768px) {
  .main-image {
    max-height: 50vh;
  }
  
  .stats-bar {
    flex-wrap: wrap;
    gap: 16px;
  }
  
  .stat-item {
    flex: 1 1 40%;
  }
  
  .media-right {
    margin-top: 12px;
  }
  
  .buttons {
    flex-direction: column;
    width: 100%;
  }
}
</style>

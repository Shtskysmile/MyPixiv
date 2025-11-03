<template>
  <div class="anime-list-container">
    <div class="list-header">
      <h3 class="list-title anime-gradient-text">
        <span class="icon">🎨</span> 我的作品
      </h3>
      <p class="list-subtitle">共 {{ total }} 个作品</p>
    </div>

    <!-- 待审核作品 -->
    <div v-if="pendingWorks.length > 0" class="section-container">
      <div class="section-header pending-header">
        <span class="icon">⏳</span>
        <h4 class="section-title">待审核 ({{ pendingWorks.length }})</h4>
      </div>
      <div class="works-grid">
        <div class="work-card" v-for="work in pendingWorks" :key="work.contributionId">
          <div @click="handleViewWork(work)" class="card-link clickable">
            <div class="card-image">
              <img :src="getWorkImageUrl(work)" :alt="work.title" @error="onImageError" />
              <div class="image-overlay">
                <div class="overlay-stats">
                  <span class="stat-item">
                    <i>👁️</i> {{ formatCount(work.viewCount) }}
                  </span>
                  <span class="stat-item">
                    <i>❤️</i> {{ formatCount(work.likeCount) }}
                  </span>
                  <span class="stat-item">
                    <i>⭐</i> {{ formatCount(work.favoriteCount) }}
                  </span>
                </div>
              </div>
              <div class="audit-badge">
                <span class="badge badge-pending">⏳ 待审核</span>
              </div>
            </div>
          </div>

          <div class="card-content">
            <div @click="handleViewWork(work)" class="card-title clickable">
              {{ work.title || '无标题' }}
            </div>

            <div class="card-stats">
              <span class="stat"><i>👁️</i> {{ formatCount(work.viewCount) }}</span>
              <span class="stat"><i>❤️</i> {{ formatCount(work.likeCount) }}</span>
              <span class="stat"><i>⭐</i> {{ formatCount(work.favoriteCount) }}</span>
              <span class="stat"><i>💬</i> {{ formatCount(work.commentCount) }}</span>
            </div>

            <div class="card-actions">
              <button class="anime-button is-small is-info" @click="handleEdit(work)">
                <span class="icon">✏️</span>
                <span>编辑</span>
              </button>
              <button class="anime-button is-small is-danger" @click="handleDelete(work)">
                <span class="icon">🗑️</span>
                <span>删除</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 已通过作品 -->
    <div v-if="approvedWorks.length > 0" class="section-container">
      <div class="section-header approved-header">
        <span class="icon">✅</span>
        <h4 class="section-title">已通过 ({{ approvedWorks.length }})</h4>
      </div>
      <div class="works-grid">
        <div class="work-card" v-for="work in approvedWorks" :key="work.contributionId">
          <router-link :to="`/image/${work.contributionId}`" class="card-link">
            <div class="card-image">
              <img :src="getWorkImageUrl(work)" :alt="work.title" @error="onImageError" />
              <div class="image-overlay">
                <div class="overlay-stats">
                  <span class="stat-item">
                    <i>👁️</i> {{ formatCount(work.viewCount) }}
                  </span>
                  <span class="stat-item">
                    <i>❤️</i> {{ formatCount(work.likeCount) }}
                  </span>
                  <span class="stat-item">
                    <i>⭐</i> {{ formatCount(work.favoriteCount) }}
                  </span>
                </div>
              </div>
              <div class="audit-badge">
                <span class="badge badge-passed">✅ 已通过</span>
              </div>
            </div>
          </router-link>

          <div class="card-content">
            <router-link :to="`/image/${work.contributionId}`" class="card-title">
              {{ work.title || '无标题' }}
            </router-link>

            <div class="card-stats">
              <span class="stat"><i>👁️</i> {{ formatCount(work.viewCount) }}</span>
              <span class="stat"><i>❤️</i> {{ formatCount(work.likeCount) }}</span>
              <span class="stat"><i>⭐</i> {{ formatCount(work.favoriteCount) }}</span>
              <span class="stat"><i>💬</i> {{ formatCount(work.commentCount) }}</span>
            </div>

            <div class="card-actions">
              <button class="anime-button is-small is-info" @click="handleEdit(work)">
                <span class="icon">✏️</span>
                <span>编辑</span>
              </button>
              <button class="anime-button is-small is-danger" @click="handleDelete(work)">
                <span class="icon">🗑️</span>
                <span>删除</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 已驳回作品 -->
    <div v-if="rejectedWorks.length > 0" class="section-container">
      <div class="section-header rejected-header">
        <span class="icon">❌</span>
        <h4 class="section-title">已驳回 ({{ rejectedWorks.length }})</h4>
      </div>
      <div class="works-grid">
        <div class="work-card rejected-card" v-for="work in rejectedWorks" :key="work.contributionId">
          <div class="card-link disabled">
            <div class="card-image">
              <img :src="getWorkImageUrl(work)" :alt="work.title" @error="onImageError" />
              <div class="rejected-overlay">
                <span class="rejected-text">❌ 无法查看</span>
              </div>
              <div class="audit-badge">
                <span class="badge badge-rejected">❌ 已驳回</span>
              </div>
            </div>
          </div>

          <div class="card-content">
            <div class="card-title disabled-title">
              {{ work.title || '无标题' }}
            </div>

            <div class="card-stats">
              <span class="stat"><i>👁️</i> {{ formatCount(work.viewCount) }}</span>
              <span class="stat"><i>❤️</i> {{ formatCount(work.likeCount) }}</span>
              <span class="stat"><i>⭐</i> {{ formatCount(work.favoriteCount) }}</span>
              <span class="stat"><i>💬</i> {{ formatCount(work.commentCount) }}</span>
            </div>

            <!-- 驳回理由 -->
            <div class="rejection-reason" v-if="work.dismissalReason">
              <span class="icon">⚠️</span>
              <span>{{ work.dismissalReason }}</span>
            </div>

            <div class="card-actions">
              <button class="anime-button is-small is-info" @click="handleEdit(work)">
                <span class="icon">✏️</span>
                <span>编辑</span>
              </button>
              <button class="anime-button is-small is-danger" @click="handleDelete(work)">
                <span class="icon">🗑️</span>
                <span>删除</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="empty-state" v-if="works.length === 0">
      <div class="empty-icon">🎨</div>
      <p class="empty-text">还没有上传作品</p>
      <p class="empty-hint">快去创作并上传你的第一个作品吧！</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'UserWorksList',
  props: {
    works: {
      type: Array,
      default: () => []
    },
    page: {
      type: Number,
      default: 1
    },
    pageSize: {
      type: Number,
      default: 12
    },
    total: {
      type: Number,
      default: 0
    }
  },
  computed: {
    totalPages() {
      return Math.max(1, Math.ceil(this.total / this.pageSize));
    },
    // 待审核作品 (auditStatus === 0)
    pendingWorks() {
      return this.works.filter(work => work.auditStatus === 0);
    },
    // 已通过作品 (auditStatus === 1)
    approvedWorks() {
      return this.works.filter(work => work.auditStatus === 1);
    },
    // 已驳回作品 (auditStatus === 2)
    rejectedWorks() {
      return this.works.filter(work => work.auditStatus === 2);
    }
  },
  methods: {
    // 处理待审核作品的查看（使用 pendingContribution 接口）
    handleViewWork(work) {
      // 待审核作品跳转到特殊的待审核详情页（暂时使用普通详情页，但传递 pending 参数）
      this.$router.push({
        path: `/image/${work.contributionId}`,
        query: { pending: 'true' }
      });
    },
    formatCount(count) {
      if (!count && count !== 0) return 0;
      if (count >= 10000) return (count / 10000).toFixed(1) + 'w';
      else if (count >= 1000) return (count / 1000).toFixed(1) + 'k';
      return count;
    },
    formatAuthorId(authorId) {
      if (!authorId) return '匿名';
      return authorId.length > 8 ? authorId.substring(0, 8) + '...' : authorId;
    },
    getAuditStatusText(status) {
      const statusMap = {
        0: '⏳ 待审核',
        1: '✅ 已通过',
        2: '❌ 已驳回'
      };
      return statusMap[status] || '未知';
    },
    getWorkImageUrl(work) {
      // 后端 R_OverviewContribution.image 是 List<String>，每个元素是图片路径
      // 格式如: /files/userId/contributions/xxx.jpg
      
      console.group(`🖼️ [图片URL调试] ${work.title || work.contributionId}`);
      console.log('📦 完整作品对象:', work);
      console.log('🔍 image字段原始值:', work.image);
      console.log('🔍 image字段类型:', typeof work.image);
      console.log('🔍 是否为数组:', Array.isArray(work.image));
      if (Array.isArray(work.image)) {
        console.log('🔍 数组长度:', work.image.length);
        console.log('🔍 数组内容:', work.image);
      }
      
      let imagePath = '';
      
      if (Array.isArray(work.image) && work.image.length > 0) {
        // 如果是数组，取第一张图
        imagePath = work.image[0];
        console.log('✅ 从数组获取图片路径:', imagePath);
      } else if (typeof work.image === 'string' && work.image) {
        // 如果是字符串（兼容旧数据）
        imagePath = work.image;
        console.log('✅ 从字符串获取图片路径:', imagePath);
      } else if (work.url) {
        // 兼容旧的 url 字段
        imagePath = work.url;
        console.log('✅ 从url字段获取图片路径:', imagePath);
      }
      
      if (!imagePath) {
        // 返回占位图
        console.warn('❌ 没有找到图片路径，显示占位图');
        console.groupEnd();
        return 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="400" height="300"%3E%3Crect fill="%23ddd" width="400" height="300"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999" font-size="20"%3E暂无图片%3C/text%3E%3C/svg%3E';
      }
      
      // 如果是完整URL，直接返回
      if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
        console.log('✅ 完整URL，直接返回:', imagePath);
        console.groupEnd();
        return imagePath;
      }
      
      // 拼接基础 URL（参考 Navbar.vue 的头像逻辑）
      const baseURL = process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080';
      const fullPath = imagePath.startsWith('/') ? imagePath : `/${imagePath}`;
      const finalUrl = `${baseURL}${fullPath}`;
      
      console.log('🌐 环境信息:', {
        NODE_ENV: process.env.NODE_ENV,
        VUE_APP_API_BASE_URL: process.env.VUE_APP_API_BASE_URL,
        baseURL: baseURL
      });
      console.log('🔗 最终图片URL:', finalUrl);
      console.groupEnd();
      
      return finalUrl;
    },
    handleEdit(work) {
      // TODO: 实现编辑功能
      alert('编辑功能开发中...');
    },
    handleDelete(work) {
      const title = work.title || '此作品';
      const confirmed = confirm(`确定删除作品《${title}》吗？此操作不可恢复！`);
      if (confirmed) {
        this.$emit('delete', work);
      }
    },
    onImageError(e) {
      const failedUrl = e.target.src;
      console.error('❌ 图片加载失败:', failedUrl);
      
      // 尝试验证URL是否可访问
      console.group('🔍 图片加载失败诊断');
      console.log('失败的URL:', failedUrl);
      console.log('可以尝试在新标签页打开此URL查看详细错误:', failedUrl);
      
      // 测试图片是否真的存在
      fetch(failedUrl, { method: 'HEAD' })
        .then(response => {
          console.log('HEAD请求响应状态:', response.status);
          if (response.ok) {
            console.warn('⚠️ 图片URL可访问，但img标签加载失败。可能是CORS或其他问题。');
          } else {
            console.error('❌ 图片URL不可访问，HTTP状态:', response.status);
          }
        })
        .catch(err => {
          console.error('❌ HEAD请求失败:', err.message);
        })
        .finally(() => {
          console.groupEnd();
        });
      
      e.target.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="400" height="300"%3E%3Crect fill="%23ddd" width="400" height="300"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999" font-size="20"%3E图片加载失败%3C/text%3E%3C/svg%3E';
    },
    onAvatarError(e) {
      e.target.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="40" height="40"%3E%3Crect fill="%23ddd" width="40" height="40"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999"%3EU%3C/text%3E%3C/svg%3E';
    }
  }
};
</script>

<style scoped>
.anime-list-container {
  background: rgba(255, 255, 255, 0.98);
  border: 2px solid rgba(255, 105, 180, 0.2);
  border-radius: 20px;
  padding: 28px;
  box-shadow: 0 8px 32px rgba(147, 51, 234, 0.12);
}

.list-header {
  margin-bottom: 32px;
  text-align: center;
}

/* 分类区域容器 */
.section-container {
  margin-bottom: 40px;
}

.section-container:last-of-type {
  margin-bottom: 0;
}

/* 分类标题 */
.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 24px;
  border-radius: 12px;
  margin-bottom: 20px;
  font-weight: 700;
  border-left: 5px solid;
}

.pending-header {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border-left-color: #fbbf24;
  color: #92400e;
}

.approved-header {
  background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
  border-left-color: #10b981;
  color: #065f46;
}

.rejected-header {
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
  border-left-color: #ef4444;
  color: #991b1b;
}

.section-header .icon {
  font-size: 1.5rem;
}

.section-title {
  font-size: 1.25rem;
  margin: 0;
}

.list-title {
  font-size: 1.75rem;
  font-weight: 900;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 8px;
}

.anime-gradient-text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.list-subtitle {
  color: #6b7280;
  font-weight: 600;
}

.works-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.work-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  border: 2px solid rgba(147, 51, 234, 0.1);
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(147, 51, 234, 0.08);
}

.work-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(147, 51, 234, 0.2);
  border-color: #a78bfa;
}

.card-link {
  display: block;
  text-decoration: none;
}

/* 可点击的待审核卡片 */
.card-link.clickable {
  cursor: pointer;
}

/* 不可点击的驳回卡片 */
.card-link.disabled {
  cursor: not-allowed;
  opacity: 0.7;
}

.card-image {
  position: relative;
  height: 200px;
  overflow: hidden;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

/* 驳回作品的遮罩层 */
.rejected-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 5;
}

.rejected-text {
  color: white;
  font-size: 18px;
  font-weight: 700;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}

/* 驳回卡片特殊样式 */
.rejected-card {
  border-color: rgba(239, 68, 68, 0.3);
  background: rgba(254, 242, 242, 0.3);
}

.rejected-card:hover {
  transform: none;
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.15);
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.work-card:hover .card-image img {
  transform: scale(1.1);
}

.image-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.7) 0%, transparent 100%);
  padding: 12px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.work-card:hover .image-overlay {
  opacity: 1;
}

.overlay-stats {
  display: flex;
  gap: 12px;
  color: white;
  font-size: 12px;
  font-weight: 600;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.audit-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  z-index: 10;
}

.badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
  border: 2px solid;
}

.badge-pending {
  background: rgba(251, 191, 36, 0.95);
  color: white;
  border-color: #fbbf24;
}

.badge-passed {
  background: rgba(16, 185, 129, 0.95);
  color: white;
  border-color: #10b981;
}

.badge-rejected {
  background: rgba(239, 68, 68, 0.95);
  color: white;
  border-color: #ef4444;
}

.card-content {
  padding: 16px;
}

.card-title {
  display: block;
  color: #6366f1;
  font-weight: 700;
  font-size: 15px;
  margin-bottom: 12px;
  text-decoration: none;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color 0.2s ease;
}

.card-title:hover {
  color: #a855f7;
  text-decoration: underline;
}

/* 可点击的待审核标题 */
.card-title.clickable {
  cursor: pointer;
}

/* 驳回作品的标题 */
.disabled-title {
  display: block;
  color: #9ca3af;
  font-weight: 700;
  font-size: 15px;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: not-allowed;
}

.card-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
  margin-bottom: 12px;
  padding: 12px;
  background: rgba(147, 51, 234, 0.03);
  border-radius: 8px;
}

.stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #6b7280;
  font-weight: 600;
}

.rejection-reason {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px;
  background: #fef2f2;
  border-left: 4px solid #ef4444;
  border-radius: 8px;
  margin-bottom: 12px;
  font-size: 13px;
  color: #991b1b;
}

.card-actions {
  display: flex;
  gap: 8px;
}

.anime-button {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px 16px;
  border-radius: 10px;
  font-weight: 700;
  font-size: 14px;
  border: 2px solid transparent;
  transition: all 0.3s ease;
  cursor: pointer;
}

.anime-button.is-info {
  background: linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%);
  color: white;
}

.anime-button.is-info:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.anime-button.is-danger {
  background: linear-gradient(135deg, #f87171 0%, #ef4444 100%);
  color: white;
}

.anime-button.is-danger:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-text {
  font-size: 18px;
  font-weight: 700;
  color: #6b7280;
  margin-bottom: 8px;
}

.empty-hint {
  font-size: 14px;
  color: #9ca3af;
}

.pagination-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 2px solid rgba(147, 51, 234, 0.1);
}

.page-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  border-radius: 12px;
  font-weight: 700;
  background: white;
  border: 2px solid rgba(147, 51, 234, 0.2);
  color: #6366f1;
  cursor: pointer;
  transition: all 0.3s ease;
}

.page-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: transparent;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(147, 51, 234, 0.3);
}

.page-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.page-info {
  font-weight: 700;
  color: #6366f1;
  padding: 0 12px;
}

@media (max-width: 768px) {
  .works-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 16px;
  }
  
  .card-image {
    height: 150px;
  }
  
  .card-stats {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .pagination-wrapper {
    flex-wrap: wrap;
  }
}
</style>


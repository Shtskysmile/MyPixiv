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
                  <img :src="getThumbnailUrl(img)" :alt="`Page ${idx + 1}`" />
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
                      :src="avatarUrl" 
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
                      :disabled="likeLoading"
                    >
                      <span class="icon">{{ isLiked ? '❤️' : '🤍' }}</span>
                      <span>{{ likeLoading ? '处理中...' : (isLiked ? '已点赞' : '点赞') }}</span>
                    </button>
                    <button 
                      class="button anime-button"
                      :class="{ 'is-warning': isFavorite, 'is-light': !isFavorite }"
                      @click="toggleFavorite"
                      :disabled="favoriteLoading"
                    >
                      <span class="icon">{{ isFavorite ? '⭐' : '☆' }}</span>
                      <span>{{ favoriteLoading ? '处理中...' : (isFavorite ? '已收藏' : '收藏') }}</span>
                    </button>
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
                      :src="getCommentAvatarUrl(c)" 
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
import { loadImage, loadImages, loadAvatar } from '@/utils/imageLoader';

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
      likeLoading: false,
      favoriteLoading: false,
      currentImageIndex: 0, // 当前显示的图片索引（漫画多图翻页）
      loadedImages: {}, // 缓存已加载的图片 URL { imagePath: loadedUrl }
      loadedAvatar: null, // 缓存已加载的头像 URL
      currentLoadedImage: null, // 当前显示的已加载图片 URL
      defaultAvatar: 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="64" height="64"%3E%3Crect fill="%23ddd" width="64" height="64"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999" font-size="32"%3EU%3C/text%3E%3C/svg%3E'
    };
  },
  computed: {
    // 作者头像URL
    avatarUrl() {
      // 优先使用已加载的头像 URL
      if (this.loadedAvatar) {
        return this.loadedAvatar;
      }
      
      // 如果有原始路径，使用 getImageUrl 处理
      if (this.contribution.uploaderAvatarPath) {
        return this.getImageUrl(this.contribution.uploaderAvatarPath);
      }
      
      // 最后返回默认头像
      return this.defaultAvatar;
    },
    
    // 当前显示的图片URL
    currentImage() {
      // 优先返回已加载的图片 URL
      if (this.currentLoadedImage) {
        return this.currentLoadedImage;
      }

      // 获取原始图片路径
      let originalPath = '';
      if (this.contribution.images && Array.isArray(this.contribution.images) && this.contribution.images.length > 0) {
        // 多张图片：返回当前索引的图片
        originalPath = this.contribution.images[this.currentImageIndex] || '';
      } else {
        // 单张图片
        let image = this.contribution.image || '';
        // 如果 image 是数组，取第一个元素
        if (Array.isArray(image)) {
          originalPath = image[0] || '';
        } else {
          originalPath = image;
        }
      }

      // 如果在 loadedImages 中找到了处理后的 URL，优先使用
      if (originalPath && this.loadedImages[originalPath]) {
        return this.loadedImages[originalPath];
      }

      // 如果没有找到，使用 getImageUrl 方法处理
      return this.getImageUrl(originalPath);
    },
    // 是否为漫画
    isManga() {
      return this.contribution.type === 1;
    },
    // 总页数（漫画或插画集）
    totalPages() {
      if (this.contribution.images && Array.isArray(this.contribution.images) && this.contribution.images.length > 0) {
        return this.contribution.images.length;
      }
      return 1;
    },
    // 是否可以上一页
    canPrevPage() {
      return this.totalPages > 1 && this.currentImageIndex > 0;
    },
    // 是否可以下一页
    canNextPage() {
      return this.totalPages > 1 && this.currentImageIndex < this.totalPages - 1;
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

      // 检查 URL 参数中是否有 pending 标记
      const isPending = this.$route.query.pending === 'true';
      
      // 根据是否为待审核作品选择不同的接口
      const endpoint = isPending ? '/api/pendingContribution' : '/api/contribution';
      console.log(endpoint);
      console.log(params);

      // 使用后端接口，返回 Result<R_ContributionDTO> 或 Result<R_Contribution>
      axios.post(endpoint, params, {
        headers: {
          'Authorization': 'Bearer ' + token
        }
      })
        .then((res) => {
          if (res.data && res.data.code === 0) {
            const data = res.data.data;
            
            console.log('🔍 [调试] 后端返回完整数据:', JSON.stringify(data, null, 2));
            console.log('🔍 [调试] isPending:', isPending);
            
            if (isPending) {
              // 待审核作品接口返回 R_Contribution（没有评论和点赞收藏状态）
              this.contribution = data || {};
              this.comments = [];
              this.isLiked = false;
              this.isFavorite = false;
              console.log('🔍 [调试] contribution.contributionId:', this.contribution.contributionId);
              console.log('🔍 [调试] contribution.image (原始):', this.contribution.image);
              console.log('🔍 [调试] contribution.images (原始):', this.contribution.images);
              
              // 🔧 修复：后端返回的 image 字段是 List<String>，需要转换
              // 插画：image 是单元素数组，转为 string
              // 漫画：image 是多元素数组，转为 images
              if (Array.isArray(this.contribution.image)) {
                if (this.contribution.image.length === 1) {
                  // 插画：单张图片
                  this.contribution.image = this.contribution.image[0];
                  this.contribution.images = [];
                  console.log('✅ 转换为插画格式 - image:', this.contribution.image);
                } else if (this.contribution.image.length > 1) {
                  // 漫画：多张图片
                  this.contribution.images = this.contribution.image;
                  this.contribution.image = '';
                  console.log('✅ 转换为漫画格式 - images:', this.contribution.images);
                } else {
                  // 空数组，设置默认值
                  this.contribution.image = '';
                  this.contribution.images = [];
                  console.warn('⚠️ image字段是空数组');
                }
              }
            } else {
              // 普通作品接口返回 R_ContributionDTO 结构
              this.contribution = data.contribution || {};
              this.comments = data.comments || [];
              this.isLiked = data.isLiked || false;
              this.isFavorite = data.isFavorite || false;
              
              // 🔧 修复：普通作品也可能有 image 数组格式
              if (this.contribution.image && Array.isArray(this.contribution.image)) {
                if (this.contribution.image.length === 1) {
                  this.contribution.image = this.contribution.image[0];
                  this.contribution.images = [];
                  console.log('✅ 普通作品转换为插画格式');
                } else if (this.contribution.image.length > 1) {
                  this.contribution.images = this.contribution.image;
                  this.contribution.image = '';
                  console.log('✅ 普通作品转换为漫画格式');
                } else {
                  this.contribution.image = '';
                  this.contribution.images = [];
                  console.warn('⚠️ 普通作品image字段是空数组');
                }
              }
            }
            
            // 如果有多张图片（漫画或插画集），初始化当前页
            if (this.contribution.images && Array.isArray(this.contribution.images) && this.contribution.images.length > 0) {
              this.currentImageIndex = 0;
            }

            // 加载图片和头像
            this.loadAllImages();
            this.loadAvatarImage();
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

    // 加载所有图片
    async loadAllImages() {
      console.log('🖼️ [loadAllImages] 开始加载图片...');
      console.log('📦 contribution.images:', this.contribution.images);
      console.log('📦 contribution.image:', this.contribution.image);

      if (this.contribution.images && Array.isArray(this.contribution.images) && this.contribution.images.length > 0) {
        // 多张图片（漫画或插画集）：加载所有图片
        console.log('✅ 检测到多张图片，数量:', this.contribution.images.length);
        const imagePaths = this.contribution.images;
        for (let i = 0; i < imagePaths.length; i++) {
          const path = imagePaths[i];
          console.log(`  - 加载第 ${i + 1}/${imagePaths.length} 张:`, path);
          if (!this.loadedImages[path]) {
            const url = await loadImage(path);
            this.$set(this.loadedImages, path, url || path);
          }
        }
        // 更新当前显示的图片
        this.updateCurrentLoadedImage();
      } else if (this.contribution.image) {
        // 单张图片（插画）
        console.log('✅ 检测到单张图片');
        let path = this.contribution.image;
        
        // 如果 image 是数组，取第一个元素
        if (Array.isArray(path)) {
          console.warn('⚠️ contribution.image 是数组，取第一个元素:', path[0]);
          path = path[0];
        }
        
        console.log('  - 加载图片:', path);
        if (!this.loadedImages[path]) {
          const url = await loadImage(path);
          this.$set(this.loadedImages, path, url || path);
        }
        this.updateCurrentLoadedImage();
      } else {
        console.warn('⚠️ 没有找到图片数据');
      }
    },

    // 更新当前显示的已加载图片
    updateCurrentLoadedImage() {
      let currentPath;
      if (this.contribution.images && Array.isArray(this.contribution.images) && this.contribution.images.length > 0) {
        currentPath = this.contribution.images[this.currentImageIndex];
      } else {
        currentPath = this.contribution.image;
        // 如果 image 是数组，取第一个元素
        if (Array.isArray(currentPath)) {
          currentPath = currentPath[0];
        }
      }

      if (currentPath && this.loadedImages[currentPath]) {
        this.currentLoadedImage = this.loadedImages[currentPath];
      }
    },

    // 加载头像
    async loadAvatarImage() {
      if (this.contribution.uploaderAvatarPath && !this.loadedAvatar) {
        const url = await loadAvatar(this.contribution.uploaderAvatarPath);
        this.loadedAvatar = url || this.contribution.uploaderAvatarPath;
      }
    },
    
    async toggleLike() {
      if (this.likeLoading) return;
      
      this.likeLoading = true;
      const token = localStorage.getItem('token');
      
      if (!token) {
        alert('请先登录');
        this.likeLoading = false;
        return;
      }
      
      try {
        const endpoint = this.isLiked ? '/api/user/unlikeContribution' : '/api/user/likeContribution';
        const params = new URLSearchParams();
        params.append('contributionId', this.contribution.contributionId);
        
        console.log(`📡 正在${this.isLiked ? '取消点赞' : '点赞'}...`);
        
        const response = await axios.post(endpoint, params, {
          headers: {
            'Authorization': 'Bearer ' + token
          }
        });
        
        console.log('✅ 点赞接口响应:', response.data);
        
        // 后端成功状态：code === 0 或 code === 200
        if (response.data?.code === 0 || response.data?.code === 200) {
          console.log('✨', response.data.message || (this.isLiked ? '取消点赞成功' : '点赞成功'));
          
          // 重新请求后端，刷新点赞和收藏数
          await this.refreshContributionData();
        } else {
          alert(response.data?.message || '操作失败');
        }
      } catch (err) {
        console.error('❌ 点赞操作失败:', err);
        alert('操作失败，请稍后重试');
      } finally {
        this.likeLoading = false;
      }
    },
    
    async toggleFavorite() {
      if (this.favoriteLoading) return;
      
      this.favoriteLoading = true;
      const token = localStorage.getItem('token');
      
      if (!token) {
        alert('请先登录');
        this.favoriteLoading = false;
        return;
      }
      
      try {
        const endpoint = this.isFavorite ? '/api/user/unfavoriteContribution' : '/api/user/favoriteContribution';
        const params = new URLSearchParams();
        params.append('contributionId', this.contribution.contributionId);
        
        console.log(`📡 正在${this.isFavorite ? '取消收藏' : '收藏'}...`);
        
        const response = await axios.post(endpoint, params, {
          headers: {
            'Authorization': 'Bearer ' + token
          }
        });
        
        console.log('✅ 收藏接口响应:', response.data);
        
        // 后端成功状态：code === 0 或 code === 200
        if (response.data?.code === 0 || response.data?.code === 200) {
          console.log('✨', response.data.message || (this.isFavorite ? '取消收藏成功' : '收藏成功'));
          
          // 重新请求后端，刷新点赞和收藏数
          await this.refreshContributionData();
        } else {
          alert(response.data?.message || '操作失败');
        }
      } catch (err) {
        console.error('❌ 收藏操作失败:', err);
        alert('操作失败，请稍后重试');
      } finally {
        this.favoriteLoading = false;
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
    
    // 刷新作品数据（用于点赞/收藏后更新统计数据）
    async refreshContributionData() {
      const token = localStorage.getItem('token');
      if (!token) return;
      
      const params = new URLSearchParams();
      params.append('contributionId', this.contribution.contributionId);
      
      // 检查是否为待审核作品
      const isPending = this.$route.query.pending === 'true';
      const endpoint = isPending ? '/api/pendingContribution' : '/api/contribution';
      
      try {
        console.log('🔄 正在刷新作品数据...');
        
        const response = await axios.post(endpoint, params, {
          headers: {
            'Authorization': 'Bearer ' + token
          }
        });
        
        if (response.data && response.data.code === 0) {
          const data = response.data.data;
          
          if (isPending) {
            // 待审核作品：只更新统计数据，不更新图片和评论
            this.contribution.likeCount = data.likeCount || 0;
            this.contribution.favoriteCount = data.favoriteCount || 0;
            this.contribution.viewCount = data.viewCount || 0;
            this.contribution.commentCount = data.commentCount || 0;
          } else {
            // 普通作品：更新统计数据和状态
            this.contribution.likeCount = data.contribution?.likeCount || 0;
            this.contribution.favoriteCount = data.contribution?.favoriteCount || 0;
            this.contribution.viewCount = data.contribution?.viewCount || 0;
            this.contribution.commentCount = data.contribution?.commentCount || 0;
            this.isLiked = data.isLiked || false;
            this.isFavorite = data.isFavorite || false;
            
            // 如果有新评论，也更新评论列表
            if (data.comments) {
              this.comments = data.comments;
            }
          }
          
          console.log('✅ 数据刷新成功');
          console.log('  - 点赞数:', this.contribution.likeCount);
          console.log('  - 收藏数:', this.contribution.favoriteCount);
          console.log('  - 点赞状态:', this.isLiked);
          console.log('  - 收藏状态:', this.isFavorite);
        }
      } catch (err) {
        console.error('❌ 刷新数据失败:', err);
        // 刷新失败不影响用户体验，静默处理
      }
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
        this.updateCurrentLoadedImage();
      }
    },
    
    nextPage() {
      if (this.canNextPage) {
        this.currentImageIndex++;
        this.updateCurrentLoadedImage();
      }
    },
    
    goToPage(index) {
      if (index >= 0 && index < this.totalPages) {
        this.currentImageIndex = index;
        this.updateCurrentLoadedImage();
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
    },
    
    // 获取图片URL（通用方法）
    getImageUrl(imagePath) {
      if (!imagePath) {
        return 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="400" height="300"%3E%3Crect fill="%23ddd" width="400" height="300"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999"%3E暂无图片%3C/text%3E%3C/svg%3E';
      }
      
      // 如果是完整URL，直接返回
      if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
        return imagePath;
      }
      
      // 拼接基础 URL
      const baseURL = process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080';
      const fullPath = imagePath.startsWith('/') ? imagePath : `/${imagePath}`;
      return `${baseURL}${fullPath}`;
    },
    
    // 获取缩略图URL
    getThumbnailUrl(imagePath) {
      // 优先使用已加载的图片URL
      if (this.loadedImages[imagePath]) {
        return this.loadedImages[imagePath];
      }
      
      // 如果还没加载，使用通用方法处理URL
      return this.getImageUrl(imagePath);
    },
    
    // 获取评论头像URL
    getCommentAvatarUrl(comment) {
      const avatarPath = comment.avatar;
      
      if (!avatarPath) {
        return 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="36" height="36"%3E%3Crect fill="%23ddd" width="36" height="36"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999"%3EU%3C/text%3E%3C/svg%3E';
      }
      
      // 使用通用方法处理URL
      return this.getImageUrl(avatarPath);
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

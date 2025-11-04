<template>
  <div class="page-root">
    <div class="page-bg" :style="bgStyle"></div>
    <div class="page-overlay"></div>
    
    <!-- 装饰性粒子效果 -->
    <div class="particles">
      <div class="particle" v-for="i in 20" :key="i" :style="getParticleStyle(i)"></div>
    </div>
    
    <!-- 浮动装饰元素 -->
    <div class="floating-decorations">
      <div class="float-star star-1">🔍</div>
      <div class="float-star star-2">✨</div>
      <div class="float-star star-3">💫</div>
    </div>
    
    <div class="container">
      <Navbar></Navbar>

      <div class="columns">
        <div class="column is-one-fifth">
          <Sidebar />
        </div>
        <div class="column">
          <!-- 搜索标题区域 -->
          <div class="header-section anime-box">
            <div class="title-wrapper">
              <div class="title-icon-wrapper">
                <span class="title-icon">🔍</span>
                <div class="icon-glow"></div>
              </div>
              <div class="title-content">
                <h1 class="page-title">
                  <span class="title-text">搜索</span>
                  <span class="title-badge">Search</span>
                </h1>
                <p class="page-subtitle">
                  <span class="subtitle-icon">✨</span>
                  探索你想要的内容
                  <span class="subtitle-icon">✨</span>
                </p>
              </div>
            </div>
            
            <!-- 搜索框 -->
            <div class="search-box-wrapper">
              <div class="search-input-group">
                <span class="search-icon">🔎</span>
                <input 
                  type="text" 
                  class="search-input"
                  v-model="searchInput"
                  @keyup.enter="performSearch"
                  placeholder="输入关键词搜索..."
                />
                <button class="search-button" @click="performSearch">
                  <span class="button-icon">🔍</span>
                  <span class="button-text">搜索</span>
                </button>
              </div>
            </div>
            
            <!-- 搜索类型切换 -->
            <div class="search-type-tabs">
              <button 
                class="tab-button"
                :class="{ active: searchType === 'id' }"
                @click="changeSearchType('id')"
              >
                <span class="tab-icon">🆔</span>
                ID搜索
              </button>
              <button 
                class="tab-button"
                :class="{ active: searchType === 'name' }"
                @click="changeSearchType('name')"
              >
                <span class="tab-icon">👤</span>
                名称搜索
              </button>
              <button 
                class="tab-button"
                :class="{ active: searchType === 'tag' }"
                @click="changeSearchType('tag')"
              >
                <span class="tab-icon">🏷️</span>
                标签搜索
              </button>
            </div>
            
            <!-- 当前搜索关键词显示 -->
            <div class="current-keyword" v-if="keyword">
              <span class="keyword-label">当前搜索：</span>
              <span class="keyword-value">{{ keyword }}</span>
              <span class="keyword-type">({{ searchTypeLabel }})</span>
            </div>
          </div>

          <!-- 加载状态 -->
          <div v-if="loading" class="loading-container anime-box">
            <div class="loading-spinner">
              <div class="spinner-ring"></div>
              <div class="spinner-ring"></div>
              <div class="spinner-ring"></div>
              <span class="loading-text">搜索中...</span>
            </div>
          </div>

          <!-- 搜索结果 -->
          <div v-else>
            <!-- 结果类型导航栏 -->
            <div v-if="hasResults" class="result-filter-nav anime-box">
              <div class="filter-tabs">
                <button 
                  class="filter-tab"
                  :class="{ active: resultFilter === 'all' }"
                  @click="resultFilter = 'all'"
                >
                  <span class="filter-icon">📋</span>
                  <span class="filter-text">全部</span>
                  <span class="filter-count">{{ totalResultsCount }}</span>
                </button>
                <button 
                  class="filter-tab"
                  :class="{ active: resultFilter === 'illustration' }"
                  @click="resultFilter = 'illustration'"
                >
                  <span class="filter-icon">🖼️</span>
                  <span class="filter-text">插画</span>
                  <span class="filter-count">{{ illustrations.length }}</span>
                </button>
                <button 
                  class="filter-tab"
                  :class="{ active: resultFilter === 'manga' }"
                  @click="resultFilter = 'manga'"
                >
                  <span class="filter-icon">📚</span>
                  <span class="filter-text">漫画</span>
                  <span class="filter-count">{{ mangas.length }}</span>
                </button>
                <button 
                  class="filter-tab"
                  :class="{ active: resultFilter === 'user' }"
                  @click="resultFilter = 'user'"
                >
                  <span class="filter-icon">👥</span>
                  <span class="filter-text">用户</span>
                  <span class="filter-count">{{ searchType === 'tag' ? 0 : users.length }}</span>
                </button>
              </div>
            </div>

            <!-- 用户搜索结果 (仅 ID 和 Name 搜索显示) -->
            <div v-if="shouldShowUsers && users.length > 0" class="results-section anime-box">
              <h2 class="section-title">
                <span class="section-icon">👥</span>
                用户 ({{ users.length }})
              </h2>
              <div class="users-grid">
                <div 
                  class="user-card" 
                  v-for="(user, index) in users" 
                  :key="user.userId"
                  :style="{ animationDelay: `${index * 0.05}s` }"
                  @click="goToUserProfile(user.userId)"
                >
                  <div class="user-avatar-wrapper">
                    <img 
                      :src="getUserAvatarUrl(user)" 
                      class="user-avatar"
                      @error="onAvatarError"
                    />
                  </div>
                  <div class="user-info">
                    <h3 class="user-name">{{ user.username }}</h3>
                    <p 
                      class="user-id clickable-id" 
                      @click.stop="copyIdToClipboard(user.userId, '用户ID')" 
                      title="点击复制ID"
                    >
                      ID: {{ formatUserId(user.userId) }}
                      <span class="copy-icon-small">📋</span>
                    </p>
                    <div class="user-meta">
                      <span class="user-badge" v-if="user.role === 1">
                        <span class="badge-icon">👑</span>
                        管理员
                      </span>
                      <span class="user-badge" v-else-if="user.role === 2">
                        <span class="badge-icon">⭐</span>
                        社区管理
                      </span>
                      <span class="user-badge" v-else>
                        <span class="badge-icon">👤</span>
                        用户
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 用户空状态 (仅在单独查看用户时显示) -->
            <div v-if="shouldShowUsers && users.length === 0 && resultFilter === 'user'" class="empty-results anime-box">
              <div class="empty-icon">👥</div>
              <h3 class="empty-title">未找到相关用户</h3>
              <p class="empty-text">
                {{ searchType === 'tag' ? '标签搜索不支持用户搜索' : '没有搜索到符合条件的用户' }}
              </p>
            </div>

            <!-- 插画搜索结果 -->
            <div v-if="shouldShowIllustrations && illustrations.length > 0" class="results-section anime-box">
              <h2 class="section-title">
                <span class="section-icon">🖼️</span>
                插画 ({{ illustrations.length }})
              </h2>
              <div class="grid">
                <div 
                  class="cell" 
                  v-for="(img, index) in illustrations" 
                  :key="img.contributionId"
                  :style="{ animationDelay: `${index * 0.05}s` }"
                  @click="goToImageDetail(img.contributionId)"
                >
                  <div class="card">
                    <div class="card-image">
                      <figure class="image-wrapper">
                        <img 
                          v-if="!loadedImages[img.contributionId]"
                          class="placeholder-img"
                          src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='300' height='300'%3E%3Crect fill='%23f0f0f0' width='300' height='300'/%3E%3C/svg%3E"
                          alt="loading"
                        >
                        <img 
                          :src="getImageUrl(img.image && img.image[0])" 
                          :alt="img.title"
                          class="artwork-img"
                          :class="{ 'img-loaded': loadedImages[img.contributionId] }"
                          @load="onImageLoad(img.contributionId)"
                          @error="onImageError"
                        >
                        <div class="image-overlay">
                          <div class="overlay-content">
                            <p class="artwork-title">{{ img.title }}</p>
                            <div class="artwork-stats">
                              <span class="stat-item">
                                <span class="stat-icon">👁️</span>
                                {{ img.viewCount || 0 }}
                              </span>
                              <span class="stat-item">
                                <span class="stat-icon">❤️</span>
                                {{ img.likeCount || 0 }}
                              </span>
                              <span class="stat-item">
                                <span class="stat-icon">⭐</span>
                                {{ img.favoriteCount || 0 }}
                              </span>
                            </div>
                          </div>
                        </div>
                      </figure>
                    </div>
                    <div class="card-content">
                      <div class="author-info">
                        <img 
                          :src="getUserAvatarUrl({ avatar: img.avatar })" 
                          class="author-avatar"
                          @error="onAvatarError"
                        />
                        <span class="author-name">{{ img.authorName || img.authorId }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 插画空状态 (仅在单独查看插画时显示) -->
            <div v-if="shouldShowIllustrations && illustrations.length === 0 && resultFilter === 'illustration'" class="empty-results anime-box">
              <div class="empty-icon">🖼️</div>
              <h3 class="empty-title">未找到相关插画</h3>
              <p class="empty-text">
                没有搜索到符合条件的插画作品
              </p>
            </div>

            <!-- 漫画搜索结果 -->
            <div v-if="shouldShowMangas && mangas.length > 0" class="results-section anime-box">
              <h2 class="section-title">
                <span class="section-icon">📚</span>
                漫画 ({{ mangas.length }})
              </h2>
              <div class="grid">
                <div 
                  class="cell" 
                  v-for="(img, index) in mangas" 
                  :key="img.contributionId"
                  :style="{ animationDelay: `${index * 0.05}s` }"
                  @click="goToImageDetail(img.contributionId)"
                >
                  <div class="card">
                    <div class="card-image">
                      <figure class="image-wrapper">
                        <img 
                          v-if="!loadedImages[img.contributionId]"
                          class="placeholder-img"
                          src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='300' height='300'%3E%3Crect fill='%23f0f0f0' width='300' height='300'/%3E%3C/svg%3E"
                          alt="loading"
                        >
                        <img 
                          :src="getImageUrl(img.image && img.image[0])" 
                          :alt="img.title"
                          class="artwork-img"
                          :class="{ 'img-loaded': loadedImages[img.contributionId] }"
                          @load="onImageLoad(img.contributionId)"
                          @error="onImageError"
                        >
                        <div class="image-overlay">
                          <div class="overlay-content">
                            <p class="artwork-title">{{ img.title }}</p>
                            <div class="artwork-stats">
                              <span class="stat-item">
                                <span class="stat-icon">👁️</span>
                                {{ img.viewCount || 0 }}
                              </span>
                              <span class="stat-item">
                                <span class="stat-icon">❤️</span>
                                {{ img.likeCount || 0 }}
                              </span>
                              <span class="stat-item">
                                <span class="stat-icon">⭐</span>
                                {{ img.favoriteCount || 0 }}
                              </span>
                            </div>
                          </div>
                        </div>
                      </figure>
                    </div>
                    <div class="card-content">
                      <div class="author-info">
                        <img 
                          :src="getUserAvatarUrl({ avatar: img.avatar })" 
                          class="author-avatar"
                          @error="onAvatarError"
                        />
                        <span class="author-name">{{ img.authorName || img.authorId }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 漫画空状态 (仅在单独查看漫画时显示) -->
            <div v-if="shouldShowMangas && mangas.length === 0 && resultFilter === 'manga'" class="empty-results anime-box">
              <div class="empty-icon">📚</div>
              <h3 class="empty-title">未找到相关漫画</h3>
              <p class="empty-text">
                没有搜索到符合条件的漫画作品
              </p>
            </div>

            <!-- 无结果状态 -->
            <div v-if="!loading && users.length === 0 && illustrations.length === 0 && mangas.length === 0" class="empty-results anime-box">
              <div class="empty-icon">🔍</div>
              <h3 class="empty-title">未找到相关结果</h3>
              <p class="empty-text">
                尝试使用不同的关键词或切换搜索类型
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Navbar from './Navbar.vue'
import Sidebar from './Sidebar.vue'
import axios from 'axios'
import copyIdMixin from '@/mixins/copyId'

export default {
  name: 'Search',
  mixins: [copyIdMixin],
  components: {
    Navbar,
    Sidebar
  },
  data() {
    return {
      keyword: '',
      searchInput: '', // 搜索框输入
      searchType: 'name', // 'id', 'name', 'tag'
      resultFilter: 'all', // 'all', 'illustration', 'manga', 'user' - 结果类型过滤器
      loading: false,
      users: [],
      illustrations: [],
      mangas: [],
      loadedImages: {},
      bgStyle: {
        backgroundImage: `linear-gradient(135deg, #667eea 0%, #764ba2 100%)`
      }
    }
  },
  computed: {
    // 搜索类型标签
    searchTypeLabel() {
      const labels = {
        'id': 'ID搜索',
        'name': '名称搜索',
        'tag': '标签搜索'
      }
      return labels[this.searchType] || '名称搜索'
    },
    
    // 是否有搜索结果
    hasResults() {
      return this.users.length > 0 || this.illustrations.length > 0 || this.mangas.length > 0
    },
    
    // 总结果数
    totalResultsCount() {
      let count = this.illustrations.length + this.mangas.length
      if (this.searchType !== 'tag') {
        count += this.users.length
      }
      return count
    },
    
    // 是否显示用户结果
    shouldShowUsers() {
      // 当选中用户过滤器时，始终显示（即使是标签搜索，也会显示空状态）
      if (this.resultFilter === 'user') {
        return true
      }
      // 当选中全部时，只在非标签搜索时显示用户
      return this.resultFilter === 'all' && this.searchType !== 'tag'
    },
    
    // 是否显示插画结果
    shouldShowIllustrations() {
      return this.resultFilter === 'all' || this.resultFilter === 'illustration'
    },
    
    // 是否显示漫画结果
    shouldShowMangas() {
      return this.resultFilter === 'all' || this.resultFilter === 'manga'
    }
  },
  mounted() {
    // 监听导航栏的搜索刷新事件
    window.addEventListener('refresh-search', this.handleRefreshSearch)
  },
  beforeDestroy() {
    // 移除事件监听器
    window.removeEventListener('refresh-search', this.handleRefreshSearch)
  },
  watch: {
    '$route.query': {
      handler() {
        this.initSearch()
      },
      immediate: true
    }
  },
  methods: {
    // 处理导航栏触发的刷新搜索事件
    handleRefreshSearch() {
      console.log('🔄 收到刷新搜索事件')
      if (this.keyword) {
        this.executeSearch()
      }
    },

    // 初始化搜索
    initSearch() {
      const query = this.$route.query
      this.keyword = query.keyword || query.search || ''
      this.searchInput = this.keyword // 同步到搜索框
      this.searchType = query.type || 'name'
      
      if (this.keyword) {
        this.executeSearch()
      }
    },

    // 切换搜索类型
    changeSearchType(type) {
      if (this.searchType === type) return
      
      this.searchType = type
      
      // 更新 URL 查询参数
      this.$router.push({
        path: '/search',
        query: {
          keyword: this.keyword,
          type: type
        }
      }).catch(err => err)
    },

    // 从搜索框触发搜索
    performSearch() {
      const keyword = (this.searchInput || '').trim()
      
      if (!keyword) {
        return
      }
      
      // 检查是否与当前搜索相同
      const isSameSearch = this.$route.path === '/search' 
        && this.$route.query.keyword === keyword 
        && this.$route.query.type === this.searchType
      
      if (isSameSearch) {
        // 如果是相同的搜索，直接执行搜索逻辑刷新数据
        this.keyword = keyword
        this.executeSearch()
      } else {
        // 更新 URL，触发搜索
        this.$router.push({
          path: '/search',
          query: {
            keyword: keyword,
            type: this.searchType
          }
        }).catch(err => err)
      }
    },

    // 执行搜索（实际的搜索逻辑）
    async executeSearch() {
      if (!this.keyword.trim()) {
        this.users = []
        this.illustrations = []
        this.mangas = []
        return
      }

      this.loading = true
      this.users = []
      this.illustrations = []
      this.mangas = []
      this.loadedImages = {}

      try {
        let endpoint = ''
        
        switch (this.searchType) {
          case 'id':
            endpoint = '/api/searchById'
            break
          case 'name':
            endpoint = '/api/searchByName'
            break
          case 'tag':
            endpoint = '/api/searchByTag'
            break
          default:
            endpoint = '/api/searchByName'
        }

        console.log(`🔍 正在执行${this.getSearchTypeName()}搜索:`, this.keyword)

        const params = new URLSearchParams()
        params.append('keyword', this.keyword.trim())

        const response = await axios.post(endpoint, params)

        console.log('✅ 搜索响应:', response.data)

        if (response.data?.code === 0 || response.data?.code === 200) {
          const data = response.data.data || {}
          
          this.users = data.users || []
          this.illustrations = data.illustrations || []
          this.mangas = data.mangas || []

          console.log(`✨ 搜索结果: ${this.users.length} 个用户, ${this.illustrations.length} 个插画, ${this.mangas.length} 个漫画`)
        } else {
          console.warn('搜索失败:', response.data?.message)
          alert(response.data?.message || '搜索失败')
        }
      } catch (err) {
        console.error('❌ 搜索出错:', err)
        alert('搜索失败，请稍后重试')
      } finally {
        this.loading = false
      }
    },

    // 获取搜索类型名称
    getSearchTypeName() {
      const names = {
        'id': 'ID',
        'name': '名称',
        'tag': '标签'
      }
      return names[this.searchType] || '名称'
    },

    // 跳转到作品详情
    goToImageDetail(contributionId) {
      this.$router.push({
        name: 'image-detail',
        params: { id: contributionId }
      })
    },

    // 跳转到用户主页
    goToUserProfile(userId) {
      this.$router.push({
        name: 'user-id',
        params: { id: userId }
      })
    },

    // 获取图片URL
    getImageUrl(imagePath) {
      if (!imagePath) {
        return 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="300" height="300"%3E%3Crect fill="%23f0f0f0" width="300" height="300"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999"%3ENo Image%3C/text%3E%3C/svg%3E'
      }
      
      if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
        return imagePath
      }
      
      const baseURL = process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080'
      const fullPath = imagePath.startsWith('/') ? imagePath : `/${imagePath}`
      return `${baseURL}${fullPath}`
    },

    // 获取用户头像URL
    getUserAvatarUrl(user) {
      const avatarPath = user.avatar
      
      if (!avatarPath) {
        return 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="48" height="48"%3E%3Crect fill="%23ddd" width="48" height="48"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999" font-size="24"%3EU%3C/text%3E%3C/svg%3E'
      }
      
      if (avatarPath.startsWith('http://') || avatarPath.startsWith('https://')) {
        return avatarPath
      }
      
      const baseURL = process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080'
      const fullPath = avatarPath.startsWith('/') ? avatarPath : `/${avatarPath}`
      return `${baseURL}${fullPath}`
    },

    // 图片加载完成
    onImageLoad(contributionId) {
      this.$set(this.loadedImages, contributionId, true)
    },

    // 图片加载错误
    onImageError(e) {
      e.target.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="300" height="300"%3E%3Crect fill="%23f0f0f0" width="300" height="300"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999"%3EImage Error%3C/text%3E%3C/svg%3E'
    },

    // 头像加载错误
    onAvatarError(e) {
      e.target.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="48" height="48"%3E%3Crect fill="%23ddd" width="48" height="48"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999" font-size="24"%3EU%3C/text%3E%3C/svg%3E'
    },

    // 获取粒子样式
    getParticleStyle(i) {
      const delay = Math.random() * 5
      const duration = 5 + Math.random() * 10
      const size = 2 + Math.random() * 4
      const startX = Math.random() * 100
      const endX = startX + (Math.random() - 0.5) * 30
      const startY = 100 + Math.random() * 20
      
      return {
        left: `${startX}%`,
        width: `${size}px`,
        height: `${size}px`,
        animationDelay: `${delay}s`,
        animationDuration: `${duration}s`,
        '--end-x': `${endX}%`,
        '--start-y': `${startY}%`
      }
    }
  }
}
</script>

<style scoped>
/* ==================== 页面根容器 ==================== */
.page-root {
  min-height: 100vh;
  position: relative;
  overflow-x: hidden;
}

/* 背景层 */
.page-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -2;
  transition: background-image 1s ease;
}

.page-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.3);
  z-index: -1;
}

/* ==================== 粒子效果 ==================== */
.particles {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.particle {
  position: absolute;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  animation: particle-rise 10s infinite ease-in;
}

@keyframes particle-rise {
  0% {
    transform: translate(0, var(--start-y, 100%)) scale(0);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translate(var(--end-x, 0), -100vh) scale(1);
    opacity: 0;
  }
}

/* ==================== 浮动装饰 ==================== */
.floating-decorations {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.float-star,
.float-heart,
.float-sparkle {
  position: absolute;
  font-size: 24px;
  animation: float-bounce 3s infinite ease-in-out;
  opacity: 0.6;
}

.star-1 { top: 15%; left: 10%; animation-delay: 0s; }
.star-2 { top: 25%; right: 15%; animation-delay: 0.5s; }
.star-3 { top: 60%; left: 20%; animation-delay: 1s; }

@keyframes float-bounce {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(10deg); }
}

/* ==================== 容器 ==================== */
.container {
  position: relative;
  z-index: 1;
  padding: 80px 20px 20px 20px; /* 增加顶部padding避免被导航栏遮挡 */
  max-width: 1400px;
  margin: 0 auto;
}

/* ==================== 动画盒子 ==================== */
.anime-box {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  animation: fadeInUp 0.6s ease;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ==================== 标题区域 ==================== */
.header-section {
  margin-bottom: 32px;
}

.title-wrapper {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 24px;
}

.title-icon-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.title-icon {
  font-size: 48px;
  z-index: 1;
  animation: icon-bounce 2s infinite ease-in-out;
}

@keyframes icon-bounce {
  0%, 100% { transform: translateY(0px) scale(1); }
  50% { transform: translateY(-5px) scale(1.1); }
}

.icon-glow {
  position: absolute;
  width: 60px;
  height: 60px;
  background: radial-gradient(circle, rgba(102, 126, 234, 0.4) 0%, transparent 70%);
  border-radius: 50%;
  animation: glow-pulse 2s infinite ease-in-out;
}

@keyframes glow-pulse {
  0%, 100% { transform: scale(1); opacity: 0.5; }
  50% { transform: scale(1.2); opacity: 0.8; }
}

.title-content {
  flex: 1;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.title-text {
  font-size: 32px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.title-badge {
  padding: 4px 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  text-transform: uppercase;
}

.page-subtitle {
  font-size: 16px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 8px;
}

.subtitle-icon {
  font-size: 14px;
}

/* ==================== 搜索框 ==================== */
.search-box-wrapper {
  margin: 24px 0;
}

.search-input-group {
  display: flex;
  align-items: center;
  gap: 12px;
  background: white;
  border: 2px solid #e0e0e0;
  border-radius: 16px;
  padding: 4px 4px 4px 16px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.search-input-group:focus-within {
  border-color: #667eea;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.2);
}

.search-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 16px;
  padding: 12px 8px;
  color: #333;
  background: transparent;
}

.search-input::placeholder {
  color: #999;
}

.search-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  color: white;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
  flex-shrink: 0;
}

.search-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.search-button:active {
  transform: translateY(0);
}

.button-icon {
  font-size: 18px;
}

.button-text {
  font-weight: 700;
}

/* 当前搜索关键词显示 */
.current-keyword {
  margin-top: 16px;
  padding: 12px 16px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  border-radius: 12px;
  border-left: 4px solid #667eea;
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.keyword-label {
  font-size: 14px;
  color: #666;
  font-weight: 600;
}

.keyword-value {
  font-size: 16px;
  color: #667eea;
  font-weight: 700;
}

.keyword-type {
  font-size: 14px;
  color: #999;
  font-weight: 500;
}

/* ==================== 搜索类型切换 ==================== */
.search-type-tabs {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-top: 20px;
}

.tab-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: white;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  color: #666;
  cursor: pointer;
  transition: all 0.3s ease;
}

.tab-button:hover {
  border-color: #667eea;
  color: #667eea;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.tab-button.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: transparent;
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.tab-icon {
  font-size: 20px;
}

/* ==================== 结果过滤导航栏 ==================== */
.result-filter-nav {
  background: white;
  border-radius: 20px;
  padding: 20px;
  margin-bottom: 30px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
  backdrop-filter: blur(10px);
}

.filter-tabs {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: center;
}

.filter-tab {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 28px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  border: 2px solid transparent;
  border-radius: 16px;
  font-size: 16px;
  font-weight: 600;
  color: #555;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.filter-tab::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  opacity: 0;
  transition: opacity 0.4s ease;
  z-index: 0;
}

.filter-tab > * {
  position: relative;
  z-index: 1;
}

.filter-tab:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.25);
  border-color: #667eea;
}

.filter-tab.active {
  color: white;
  border-color: transparent;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
}

.filter-tab.active::before {
  opacity: 1;
}

.filter-icon {
  font-size: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.3s ease;
}

.filter-tab:hover .filter-icon {
  transform: scale(1.2) rotate(5deg);
}

.filter-tab.active .filter-icon {
  animation: iconPulse 2s ease-in-out infinite;
}

@keyframes iconPulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.15);
  }
}

.filter-text {
  font-size: 16px;
  white-space: nowrap;
}

.filter-count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 28px;
  height: 28px;
  padding: 0 10px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 14px;
  font-size: 14px;
  font-weight: 700;
  backdrop-filter: blur(5px);
}

.filter-tab.active .filter-count {
  background: rgba(255, 255, 255, 0.25);
  color: white;
}

.filter-tab:not(.active) .filter-count {
  background: rgba(102, 126, 234, 0.15);
  color: #667eea;
}

/* ==================== 加载状态 ==================== */
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 80px 20px;
}

.loading-spinner {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.spinner-ring {
  position: absolute;
  width: 60px;
  height: 60px;
  border: 3px solid transparent;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 1.5s linear infinite;
}

.spinner-ring:nth-child(2) {
  width: 80px;
  height: 80px;
  border-top-color: #764ba2;
  animation-duration: 2s;
  animation-direction: reverse;
}

.spinner-ring:nth-child(3) {
  width: 100px;
  height: 100px;
  border-top-color: #667eea;
  animation-duration: 2.5s;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-text {
  margin-top: 120px;
  font-size: 18px;
  font-weight: 600;
  color: #667eea;
}

/* ==================== 结果区域 ==================== */
.results-section {
  margin-bottom: 32px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 3px solid #667eea;
}

.section-icon {
  font-size: 28px;
}

/* ==================== 用户网格 ==================== */
.users-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.user-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
  animation: fadeInScale 0.5s ease;
}

.user-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(102, 126, 234, 0.2);
}

@keyframes fadeInScale {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.user-avatar-wrapper {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  border: 4px solid #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.user-avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-info {
  text-align: center;
  width: 100%;
}

.user-name {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  margin-bottom: 4px;
}

.user-id {
  font-size: 14px;
  color: #999;
  margin-bottom: 12px;
  font-family: 'Courier New', monospace;
}

.clickable-id {
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  background: rgba(0, 0, 0, 0.03);
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  user-select: none;
}

.clickable-id:hover {
  background: rgba(102, 126, 234, 0.15);
  color: #667eea;
  transform: translateX(2px);
}

.clickable-id:active {
  transform: scale(0.98);
}

.copy-icon-small {
  opacity: 0;
  transition: opacity 0.3s ease;
  font-size: 11px;
}

.clickable-id:hover .copy-icon-small {
  opacity: 1;
}

.user-meta {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.user-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 600;
}

.badge-icon {
  font-size: 14px;
}

/* ==================== 作品网格 ==================== */
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.cell {
  animation: fadeInScale 0.5s ease;
}

.card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(102, 126, 234, 0.2);
}

.card-image {
  position: relative;
  overflow: hidden;
  background: #f0f0f0;
  aspect-ratio: 1;
}

.image-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  margin: 0;
}

.placeholder-img,
.artwork-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.3s ease;
}

.placeholder-img {
  position: absolute;
  top: 0;
  left: 0;
  filter: blur(10px);
}

.artwork-img {
  position: relative;
  opacity: 0;
  transform: scale(1.1);
}

.artwork-img.img-loaded {
  opacity: 1;
  transform: scale(1);
}

.card:hover .artwork-img {
  transform: scale(1.05);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to bottom, transparent 0%, rgba(0, 0, 0, 0.8) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
  display: flex;
  align-items: flex-end;
  padding: 16px;
}

.card:hover .image-overlay {
  opacity: 1;
}

.overlay-content {
  width: 100%;
  color: white;
}

.artwork-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.artwork-stats {
  display: flex;
  gap: 12px;
  font-size: 14px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-icon {
  font-size: 16px;
}

.card-content {
  padding: 12px;
  flex: 1;
  display: flex;
  align-items: center;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.author-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #667eea;
}

.author-name {
  font-size: 14px;
  font-weight: 600;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}

/* ==================== 空状态 ==================== */
.empty-results {
  text-align: center;
  padding: 80px 20px;
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 20px;
  animation: icon-bounce 2s infinite ease-in-out;
}

.empty-title {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 12px;
}

.empty-text {
  font-size: 16px;
  color: #999;
}

/* ==================== 响应式设计 ==================== */
@media (max-width: 768px) {
  .container {
    padding: 100px 16px 16px 16px; /* 移动端增加更多顶部padding */
  }
  
  .title-wrapper {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .title-text {
    font-size: 24px;
  }
  
  .search-input-group {
    flex-direction: column;
    gap: 8px;
    padding: 12px;
  }
  
  .search-input {
    width: 100%;
    padding: 12px;
  }
  
  .search-button {
    width: 100%;
    justify-content: center;
  }
  
  .current-keyword {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
  
  .search-type-tabs {
    width: 100%;
  }
  
  .tab-button {
    flex: 1;
    justify-content: center;
    min-width: 0;
  }
  
  .result-filter-nav {
    padding: 16px;
    margin-bottom: 20px;
  }
  
  .filter-tabs {
    gap: 8px;
  }
  
  .filter-tab {
    flex: 1;
    min-width: 0;
    padding: 12px 16px;
    font-size: 14px;
    justify-content: center;
  }
  
  .filter-text {
    font-size: 14px;
  }
  
  .filter-count {
    min-width: 24px;
    height: 24px;
    font-size: 12px;
    padding: 0 8px;
  }
  
  .filter-icon {
    font-size: 18px;
  }
  
  .users-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  }
  
  .grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 12px;
  }
}
</style>


<template>
  <div class="image-display anime-box image-box">
    <!-- 主图 -->
    <img 
      :src="currentImage" 
      alt="artwork" 
      class="main-image" 
      @click="$emit('open-modal', currentImage)" 
      style="cursor: zoom-in;" 
    />
    
    <!-- 漫画翻页控件 -->
    <div v-if="isManga && totalPages > 1" class="manga-pagination">
      <button 
        class="page-btn prev-btn anime-button"
        :disabled="!canPrevPage"
        @click="$emit('prev-page')"
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
        @click="$emit('next-page')"
      >
        <span>下一页</span>
        <span class="icon">▶</span>
      </button>
    </div>
    
    <!-- 漫画页码缩略图导航 -->
    <div v-if="isManga && totalPages > 1" class="page-thumbnails">
      <div 
        v-for="(img, idx) in images" 
        :key="idx"
        class="thumbnail-item"
        :class="{ 'active': idx === currentImageIndex }"
        @click="$emit('go-to-page', idx)"
      >
        <img :src="getThumbnailUrl(img)" :alt="`Page ${idx + 1}`" />
        <span class="thumbnail-number">{{ idx + 1 }}</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ImageDisplay',
  props: {
    currentImage: {
      type: String,
      required: true
    },
    images: {
      type: Array,
      default: () => []
    },
    currentImageIndex: {
      type: Number,
      default: 0
    },
    loadedImages: {
      type: Object,
      default: () => ({})
    }
  },
  computed: {
    isManga() {
      return this.totalPages > 1;
    },
    totalPages() {
      return this.images.length || 1;
    },
    canPrevPage() {
      return this.totalPages > 1 && this.currentImageIndex > 0;
    },
    canNextPage() {
      return this.totalPages > 1 && this.currentImageIndex < this.totalPages - 1;
    }
  },
  methods: {
    getThumbnailUrl(imagePath) {
      if (this.loadedImages[imagePath]) {
        return this.loadedImages[imagePath];
      }
      return this.getImageUrl(imagePath);
    },
    getImageUrl(imagePath) {
      if (!imagePath) {
        return 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="400" height="300"%3E%3Crect fill="%23ddd" width="400" height="300"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999"%3E暂无图片%3C/text%3E%3C/svg%3E';
      }
      if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
        return imagePath;
      }
      const baseURL = process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080';
      const fullPath = imagePath.startsWith('/') ? imagePath : `/${imagePath}`;
      return `${baseURL}${fullPath}`;
    }
  }
};
</script>

<style scoped>
.image-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%) !important;
  position: relative;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.15);
}

.main-image {
  width: 100%;
  max-height: 70vh;
  object-fit: contain;
  border-radius: 16px;
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.2);
  transition: transform 0.3s ease;
}

.main-image:hover {
  transform: scale(1.02);
}

/* 漫画翻页控件 */
.manga-pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 24px;
  margin-top: 24px;
  padding: 16px 24px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.page-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.page-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
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
  font-size: 28px;
  font-weight: 800;
  color: #764ba2;
  text-shadow: 0 2px 4px rgba(118, 75, 162, 0.2);
}

.page-separator {
  color: #999;
}

.total-pages {
  color: #667eea;
}

/* 缩略图导航 */
.page-thumbnails {
  display: flex;
  gap: 12px;
  margin-top: 20px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  overflow-x: auto;
  max-width: 100%;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.page-thumbnails::-webkit-scrollbar {
  height: 8px;
}

.page-thumbnails::-webkit-scrollbar-track {
  background: rgba(102, 126, 234, 0.05);
  border-radius: 4px;
}

.page-thumbnails::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 4px;
}

.thumbnail-item {
  position: relative;
  flex-shrink: 0;
  width: 90px;
  height: 120px;
  cursor: pointer;
  border: 3px solid transparent;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  background: #f0f0f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.thumbnail-item:hover {
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
}

.thumbnail-item.active {
  border-color: #667eea;
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.5);
  transform: translateY(-2px);
}

.thumbnail-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumbnail-number {
  position: absolute;
  bottom: 6px;
  right: 6px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 700;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
}

@media (max-width: 768px) {
  .main-image {
    max-height: 50vh;
  }
  
  .manga-pagination {
    gap: 16px;
    padding: 12px 16px;
  }
  
  .page-btn {
    padding: 10px 16px;
    font-size: 14px;
  }
  
  .thumbnail-item {
    width: 70px;
    height: 90px;
  }
}
</style>


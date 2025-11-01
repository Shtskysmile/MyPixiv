
<template>
  <nav class="anime-pagination" role="navigation" aria-label="pagination">
    <button
      class="page-btn prev-btn"
      :disabled="page === 1"
      @click="changePage(page - 1)"
    >
      <span class="btn-icon">←</span>
      <span class="btn-text">上一页</span>
    </button>
    
    <div class="page-numbers">
      <button 
        v-if="page > 3"
        class="page-number"
        @click="changePage(1)"
      >
        1
      </button>
      
      <span v-if="page > 4" class="page-ellipsis">
        <span class="dot">•</span>
        <span class="dot">•</span>
        <span class="dot">•</span>
      </span>
      
      <button 
        v-for="p in pagesToShow" 
        :key="p"
        class="page-number"
        :class="{ 'is-current': p === page }"
        @click="changePage(p)"
      >
        {{ p }}
      </button>
      
      <span v-if="page < totalPage - 3" class="page-ellipsis">
        <span class="dot">•</span>
        <span class="dot">•</span>
        <span class="dot">•</span>
      </span>
      
      <button 
        v-if="page < totalPage - 2"
        class="page-number"
        @click="changePage(totalPage)"
      >
        {{ totalPage }}
      </button>
    </div>
    
    <button
      class="page-btn next-btn"
      :disabled="page === totalPage"
      @click="changePage(page + 1)"
    >
      <span class="btn-text">下一页</span>
      <span class="btn-icon">→</span>
    </button>
  </nav>
</template>

<script>
export default {
  props: {
    page: {
      type: Number,
      required: true
    },
    totalPage: {
      type: Number,
      required: true
    }
  },
  computed: {
    pagesToShow() {
      const pages = [];
      const start = Math.max(1, this.page - 2);
      const end = Math.min(this.totalPage, this.page + 2);
      for (let i = start; i <= end; i++) {
        pages.push(i);
      }
      return pages;
    }
  },
  methods: {
    changePage(newPage) {
      if (newPage < 1 || newPage > this.totalPage || newPage === this.page) return;
      this.$emit('update:page', newPage);
    }
  }
};
</script>

<style scoped>
.anime-pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
}

.page-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 50px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.page-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.page-btn:active:not(:disabled) {
  transform: translateY(0);
}

.page-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
  transform: none;
}

.btn-icon {
  font-size: 18px;
  transition: transform 0.3s ease;
}

.prev-btn:hover:not(:disabled) .btn-icon {
  transform: translateX(-4px);
}

.next-btn:hover:not(:disabled) .btn-icon {
  transform: translateX(4px);
}

.page-numbers {
  display: flex;
  align-items: center;
  gap: 8px;
}

.page-number {
  min-width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
  color: #667eea;
  border: 2px solid #e0e7ff;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
}

.page-number:hover {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  border-color: #667eea;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.page-number.is-current {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: transparent;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.4);
  transform: scale(1.1);
  position: relative;
}

.page-number.is-current::before {
  content: '';
  position: absolute;
  top: -4px;
  left: -4px;
  right: -4px;
  bottom: -4px;
  border: 2px solid rgba(102, 126, 234, 0.3);
  border-radius: 14px;
  animation: pulse-border 2s ease-in-out infinite;
}

@keyframes pulse-border {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.5;
    transform: scale(1.1);
  }
}

.page-ellipsis {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 0 8px;
  color: #9333ea;
  font-size: 20px;
}

.dot {
  animation: dotBounce 1.5s ease-in-out infinite;
}

.dot:nth-child(2) {
  animation-delay: 0.2s;
}

.dot:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes dotBounce {
  0%, 100% {
    transform: translateY(0);
    opacity: 0.5;
  }
  50% {
    transform: translateY(-4px);
    opacity: 1;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .anime-pagination {
    gap: 8px;
  }
  
  .page-btn {
    padding: 10px 20px;
    font-size: 14px;
  }
  
  .btn-text {
    display: none;
  }
  
  .btn-icon {
    font-size: 20px;
  }
  
  .page-number {
    min-width: 40px;
    height: 40px;
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .page-btn {
    padding: 8px 16px;
  }
  
  .page-number {
    min-width: 36px;
    height: 36px;
    font-size: 13px;
  }
  
  .page-ellipsis {
    padding: 0 4px;
    font-size: 16px;
  }
}
</style>

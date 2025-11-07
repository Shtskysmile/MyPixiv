<template>
  <div class="interaction-buttons">
    <button 
      class="button anime-button like-button"
      :class="{ 'is-danger': isLiked, 'is-light': !isLiked }"
      @click="$emit('toggle-like')"
      :disabled="likeLoading || disabled"
    >
      <span class="icon">{{ isLiked ? '❤️' : '🤍' }}</span>
      <span>{{ likeLoading ? '处理中...' : (isLiked ? '已点赞' : '点赞') }}</span>
    </button>
    <button 
      class="button anime-button favorite-button"
      :class="{ 'is-warning': isFavorite, 'is-light': !isFavorite }"
      @click="$emit('toggle-favorite')"
      :disabled="favoriteLoading || disabled"
    >
      <span class="icon">{{ isFavorite ? '⭐' : '☆' }}</span>
      <span>{{ favoriteLoading ? '处理中...' : (isFavorite ? '已收藏' : '收藏') }}</span>
    </button>
  </div>
</template>

<script>
export default {
  name: 'InteractionButtons',
  props: {
    isLiked: {
      type: Boolean,
      default: false
    },
    isFavorite: {
      type: Boolean,
      default: false
    },
    likeLoading: {
      type: Boolean,
      default: false
    },
    favoriteLoading: {
      type: Boolean,
      default: false
    },
    disabled: {
      type: Boolean,
      default: false
    }
  }
};
</script>

<style scoped>
.interaction-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.anime-button {
  border-radius: 12px;
  font-weight: 700;
  font-size: 15px;
  padding: 12px 20px;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.anime-button:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(147, 51, 234, 0.25);
}

.anime-button:active:not(:disabled) {
  transform: translateY(-1px);
}

.like-button.is-danger {
  background: linear-gradient(135deg, #f43f5e 0%, #e11d48 100%);
  color: white;
  animation: heartbeat 1.5s ease infinite;
}

@keyframes heartbeat {
  0%, 100% { transform: scale(1); }
  25% { transform: scale(1.05); }
}

.favorite-button.is-warning {
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
  color: white;
  animation: sparkle 2s ease infinite;
}

@keyframes sparkle {
  0%, 100% { filter: brightness(1); }
  50% { filter: brightness(1.2); }
}

.anime-button .icon {
  font-size: 18px;
}

@media (max-width: 768px) {
  .interaction-buttons {
    width: 100%;
  }
  
  .anime-button {
    flex: 1;
    justify-content: center;
  }
}
</style>


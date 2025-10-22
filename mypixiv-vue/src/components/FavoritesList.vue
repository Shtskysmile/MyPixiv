<template>
  <div class="box">
    <h3 class="title is-5">收藏</h3>
    <div class="favorites-list">
      <div class="favorite-item" v-for="fav in favorites" :key="fav.id">
        <img class="fav-thumb" :src="fav.url" :alt="fav.title" />
        <div class="fav-meta">
          <div class="fav-title">{{ fav.title }}</div>
          <div class="fav-author" v-if="fav.author">
            <img class="author-avatar" :src="fav.author.avatar" alt="author" />
            <span class="author-name">{{ fav.author.name }}</span>
          </div>
          <div class="fav-stats">
            <span>👍 {{ fav.likes || 0 }}</span>
            <span style="margin-left:12px">❤ {{ fav.favorites || 0 }}</span>
          </div>
        </div>
        <div class="fav-actions">
          <button class="button is-small is-danger" @click="$emit('unfavorite', fav)">取消收藏</button>
        </div>
      </div>
    </div>
    <div class="pager" style="margin-top:12px; display:flex; gap:8px; align-items:center; justify-content:center">
      <button class="button is-small" :disabled="page <= 1" @click="$emit('page-change', page - 1)">上一页</button>
      <span>第 {{ page }} 页 / {{ totalPages }}</span>
      <button class="button is-small" :disabled="page >= totalPages" @click="$emit('page-change', page + 1)">下一页</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'FavoritesList',
  props: {
    favorites: {
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
    }
  }
};
</script>

<style scoped>
/* 复用父组件样式：如果需要可覆盖 */
.favorites-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.favorite-item {
  display: flex;
  align-items: center;
  padding: 10px;
  background: rgba(255,255,255,0.02);
  border-radius: 6px;
  border: 1px solid rgba(0,0,0,0.04);
}
.fav-thumb {
  width: 120px;
  height: 90px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 12px;
}
.fav-meta {
  display: flex;
  flex-direction: column;
}
.fav-title {
  font-weight: 600;
  color: #222;
}
.fav-author {
  display: flex;
  align-items: center;
  margin-top: 6px;
  font-size: 12px;
  color: #666;
}
.author-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 8px;
}
.fav-stats {
  margin-top: 8px;
  color: #666;
}
.fav-actions { margin-left: auto; }
</style>
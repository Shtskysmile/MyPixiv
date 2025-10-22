<template>
  <div class="box">
    <h3 class="title is-5">点赞</h3>
    <div class="likes-list">
      <div class="like-item" v-for="like in likes" :key="like.id">
        <img class="like-thumb" :src="like.url" :alt="like.title" />
        <div class="like-meta">
          <div class="like-title">{{ like.title }}</div>
          <div class="like-author" v-if="like.author">
            <img class="author-avatar" :src="like.author.avatar" alt="author" />
            <span class="author-name">{{ like.author.name }}</span>
          </div>
          <div class="like-stats">
            <span>👍 {{ like.likes || 0 }}</span>
            <span style="margin-left:12px">❤ {{ like.favorites || 0 }}</span>
          </div>
        </div>
        <div class="like-actions">
          <button class="button is-small" @click="$emit('unlike', like)">取消点赞</button>
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
  name: 'LikesList',
  props: {
    likes: { type: Array, default: () => [] },
    page: { type: Number, default: 1 },
    pageSize: { type: Number, default: 12 },
    total: { type: Number, default: 0 }
  },
  computed: {
    totalPages() { return Math.max(1, Math.ceil(this.total / this.pageSize)); }
  }
};
</script>

<style scoped>
.likes-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.like-item {
  display: flex;
  align-items: center;
  padding: 10px;
  background: rgba(255,255,255,0.02);
  border-radius: 6px;
  border: 1px solid rgba(0,0,0,0.04);
}
.like-thumb {
  width: 120px;
  height: 90px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 12px;
}
.like-meta {
  display: flex;
  flex-direction: column;
}
.like-title {
  font-weight: 600;
  color: #222;
}
.like-author {
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
.like-stats {
  margin-top: 8px;
  color: #666;
}
.like-actions { margin-left: auto; }
</style>
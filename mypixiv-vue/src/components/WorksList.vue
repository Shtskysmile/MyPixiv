<template>
  <div class="box">
    <h3 class="title is-5">我的画作</h3>
    <div class="works-list">
      <div class="work-item" v-for="w in works" :key="w.id">
        <img class="work-thumb" :src="w.url" :alt="w.title" />
        <div class="work-meta">
          <div class="work-title">{{ w.title }}</div>
          <div class="work-author" v-if="w.author">
            <img class="author-avatar" :src="w.author.avatar" alt="author" />
            <span class="author-name">{{ w.author.name }}</span>
          </div>
          <div class="work-stats">
            <span>👍 {{ w.likes || 0 }}</span>
            <span style="margin-left:12px">❤ {{ w.favorites || 0 }}</span>
          </div>
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
  name: 'WorksList',
  props: {
    works: { type: Array, default: () => [] },
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
.works-list { display:flex; flex-direction:column; gap:12px; }
.work-item { display:flex; align-items:center; padding:10px; background: rgba(255,255,255,0.02); border-radius:6px; border:1px solid rgba(0,0,0,0.04); }
.work-thumb { width:120px; height:90px; object-fit:cover; border-radius:4px; margin-right:12px; }
.work-meta { display:flex; flex-direction:column; }
.work-title { font-weight:600; color:#222; }
.work-author { display:flex; align-items:center; margin-top:6px; font-size:12px; color:#666; }
.author-avatar { width:28px; height:28px; border-radius:50%; object-fit:cover; margin-right:8px; }
.work-stats { margin-top:8px; color:#666; }
</style>

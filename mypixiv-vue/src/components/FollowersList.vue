<template>
  <div class="box">
    <h3 class="title is-5">粉丝</h3>
    <div class="followers-list">
      <div class="follower-item" v-for="f in followers" :key="f.id">
        <img class="follower-avatar" :src="f.avatar" alt="avatar" />
        <div class="follower-meta">
          <div class="follower-name">{{ f.name }}</div>
          <div class="follower-bio" v-if="f.bio">{{ f.bio }}</div>
        </div>
        <div class="follower-actions">
          <button class="button is-small" @click="$emit('remove', f)">取消关注</button>
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
  name: 'FollowersList',
  props: {
    followers: { type: Array, default: () => [] },
    page: { type: Number, default: 1 },
    pageSize: { type: Number, default: 12 },
    total: { type: Number, default: 0 }
  },
  computed: {
    totalPages() { return Math.max(1, Math.ceil(this.total / this.pageSize)); }
  }
}
</script>

<style scoped>
.followers-list { display:flex; flex-direction:column; gap:12px }
.follower-item { display:flex; align-items:center; padding:10px; background: rgba(255,255,255,0.02); border-radius:6px; border:1px solid rgba(0,0,0,0.04) }
.follower-avatar { width:56px; height:56px; border-radius:50%; object-fit:cover; margin-right:12px }
.follower-meta { display:flex; flex-direction:column }
.follower-name { font-weight:600 }
.follower-bio { font-size:12px; color:#666 }
.follower-actions { margin-left:auto }
</style>
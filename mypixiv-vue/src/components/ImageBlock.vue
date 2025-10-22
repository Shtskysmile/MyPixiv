<template>
  <div class="art-card">
    <router-link :to="`/image/${image.id}`" class="art-thumb-link">
      <div class="art-thumb image-fill">
        <img :src="image.url || image.img" :alt="image.title" />
      </div>
    </router-link>

    <div class="art-meta">
      <a :href="`/image/${image.id}`" class="art-title">{{ image.title }}</a>
      <div class="art-author">
        <img
          class="author-avatar"
          :src="(image.author && image.author.avatar) || image.author_avatar"
          :alt="(image.author && image.author.name) || image.author_name"
        />
        <span class="author-name">{{ (image.author && image.author.name) || image.author_name || '匿名' }}</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    image: {
      type: Object,
      required: true,
    },
  },
};
</script>

<style scoped>
.art-card { background: transparent; display: flex; flex-direction: column; gap: 8px; }
.art-thumb { position: relative; overflow: hidden; border-radius: 12px; height: 160px; }
.art-thumb img { width: 100%; height: 100%; object-fit: cover; display:block; }
.art-meta { padding: 0 6px; }
.art-title { display: block; color: #1e6fff; font-weight: 600; margin-bottom: 6px; text-decoration: none; }
.art-title:hover { text-decoration: underline; }
.art-author { display:flex; align-items:center; gap:8px; color:#6b7280; font-size: 13px; }
.author-avatar { width:20px; height:20px; border-radius:50%; object-fit:cover; }
</style>

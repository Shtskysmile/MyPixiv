<template>
  <div>
    <div>
        <router-link :to="`/image/${image.id}`">
          <figure class="image is-4by3">
            <img :src="image.url || image.img" :alt="image.title" />
          </figure>
        </router-link>
      </div>
      <div>
        <div>
          <router-link :to="`/image/${image.id}`">{{ image.title }}</router-link>
        </div>

      <div
        style="margin-top: 4px; display: flex; align-items: center; gap: 6px"
      >
        <img
          :src="(image.author && image.author.avatar) || image.author_avatar"
          :alt="(image.author && image.author.name) || image.author_name"
          style="width: 18px; height: 18px; border-radius: 50%"
        />
        <span>{{ (image.author && image.author.name) || image.author_name }}</span>
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

<style></style>

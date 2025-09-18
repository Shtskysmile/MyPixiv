<template>
  <div>
    <div>
      <figure class="image is-4by3">
        <img :src="image.img" :alt="image.title" />
      </figure>
    </div>
    <div>
      <div>{{ image.title }}</div>

      <div
        style="margin-top: 4px; display: flex; align-items: center; gap: 6px"
      >
        <img
          :src="image.author_avatar"
          :alt="image.author_name"
          style="width: 18px; height: 18px; border-radius: 50%"
        />
        <span>{{ image.author_name }}</span>
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

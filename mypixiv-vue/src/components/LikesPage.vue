<template>
  <div>
    <LikesList :likes="likes" @unlike="handleUnlike" />
  </div>
</template>

<script>
import LikesList from './LikesList.vue';

export default {
  name: 'LikesPage',
  components: { LikesList },
  data() {
    return {
      likes: []
    }
  },
  created() {
    // placeholder: in real app you'd fetch user's liked items
    // here we mock by leaving likes empty or you can populate for testing
  },
  methods: {
    handleUnlike(item) {
      // emit or call API to unlike; for now just remove locally
      this.likes = this.likes.filter(l => l.id !== item.id);
      this.$emit('unliked', item);
    }
  }
}
</script>

<style scoped>
/* small page-level styles if needed */
</style>

<template>
  <nav class="pagination is-centered" role="navigation" aria-label="pagination">
    <a
      href="#"
      class="pagination-previous"
      :disabled="page === 1"
      @click.prevent="changePage(page - 1)"
    >上一页</a>
    <a
      href="#"
      class="pagination-next"
      :disabled="page === totalPage"
      @click.prevent="changePage(page + 1)"
    >下一页</a>
    <ul class="pagination-list">
      <li v-if="page > 3">
        <a href="#" class="pagination-link" @click.prevent="changePage(1)">1</a>
      </li>
      <li v-if="page > 4"><span class="pagination-ellipsis">&hellip;</span></li>
      <li v-for="p in pagesToShow" :key="p">
        <a
          href="#"
          class="pagination-link"
          :class="{ 'is-current': p === page }"
          @click.prevent="changePage(p)"
        >{{ p }}</a>
      </li>
      <li v-if="page < totalPage - 3"><span class="pagination-ellipsis">&hellip;</span></li>
      <li v-if="page < totalPage - 2">
        <a href="#" class="pagination-link" @click.prevent="changePage(totalPage)">{{ totalPage }}</a>
      </li>
    </ul>
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

<style>
</style>

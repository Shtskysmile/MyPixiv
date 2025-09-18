<template>
  <div class="container">
    <Navbar></Navbar>
    <div class="box">
      <h1 class="title is-1">插画</h1>
    </div>

    <div class="box">
      <div class="grid">
        <div class="cell" v-for="img in images" :key="img.id">
          <ImageBlock :image="img" />
        </div>
      </div>
    </div>

    <div class="box">
      <Pagination :page="page" :totalPage="totalPage" @update:page="onPageChange" />
    </div>
  </div>
</template>

<script>
import axios from "axios";
import Navbar from "./Navbar.vue";
import ImageBlock from "./ImageBlock.vue";
import Pagination from "./Pagination.vue";

export default {
  components: {
    ImageBlock,
    Navbar,
    Pagination,
  },
  data() {
    return {
      images: [],
      page: 1,
      totalPage: 100 // 可根据实际API返回设置
    };
  },
  created() {
    this.page = this.getPageFromUrl();
    axios.get(`/api/images?page=${this.page}`).then((res) => {
      this.images = res.data.list;
      // this.totalPage = res.data.totalPage; // 如果API有返回总页数可用
    });
  },
  methods: {
    getPageFromUrl() {
      const url = new URL(window.location.href);
      const p = url.searchParams.get('page');
      return p ? parseInt(p) : 1;
    },
    onPageChange(newPage) {
      this.page = newPage;
      // 更新url
      const url = new URL(window.location.href);
      url.searchParams.set('page', newPage);
      window.history.replaceState(null, '', url.toString());
      // 重新请求数据
      axios.get(`/api/images?page=${newPage}`).then((res) => {
        this.images = res.data.list;
        // this.totalPage = res.data.totalPage;
      });
    }
  },
};
</script>

<style></style>

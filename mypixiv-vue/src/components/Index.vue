<template>
  <div class="page-root">
    <div class="page-bg" :style="bgStyle"></div>
    <div class="page-overlay"></div>
    <div class="container">
      <Navbar></Navbar>

      <div class="columns">
        <div class="column is-one-fifth">
          <Sidebar />
        </div>
        <div class="column">
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
            <Pagination
              :page="page"
              :totalPage="totalPage"
              @update:page="onPageChange"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import Navbar from "./Navbar.vue";
import ImageBlock from "./ImageBlock.vue";
import Pagination from "./Pagination.vue";
import Sidebar from "./Sidebar.vue";
import bgImg from "@/assets/images/Myth_Crystalcastle.jpg";

export default {
  components: {
    ImageBlock,
    Navbar,
    Pagination,
    Sidebar,
  },
  data() {
    return {
      images: [],
      page: 1,
      totalPage: 100, // 可根据实际API返回设置
    };
  },
  created() {
    this.page = this.getPageFromUrl();
    axios.get(`/api/images?page=${this.page}`).then((res) => {
      this.images = res.data.list;
      // this.totalPage = res.data.totalPage; // 如果API有返回总页数可用
    });
  },
  computed: {
    bgStyle() {
      return {
        backgroundImage: `url(${bgImg})`,
        backgroundSize: "cover",
        backgroundPosition: "center center",
        position: "fixed",
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        zIndex: -2,
      };
    },
  },
  methods: {
    getPageFromUrl() {
      const url = new URL(window.location.href);
      const p = url.searchParams.get("page");
      return p ? parseInt(p) : 1;
    },
    onPageChange(newPage) {
      this.page = newPage;
      // 更新url
      const url = new URL(window.location.href);
      url.searchParams.set("page", newPage);
      window.history.replaceState(null, "", url.toString());
      // 重新请求数据
      axios.get(`/api/images?page=${newPage}`).then((res) => {
        this.images = res.data.list;
        // this.totalPage = res.data.totalPage;
      });
    },
  },
};
</script>

<style scoped>
.page-root {
  position: relative;
  min-height: 100vh;
}
.page-bg {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-repeat: no-repeat;
  background-size: cover;
  background-position: center center;
  z-index: -2;
}
.page-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.45);
  z-index: -1;
}

/* 确保容器内容在前景可读 */
.container {
  position: relative;
  z-index: 1;
}
</style>

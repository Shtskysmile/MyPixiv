<template>
  <div class="image-view-root">
    <Navbar />

    <section class="section">
      <div class="container">
        <div class="columns">
          <div class="column is-two-thirds">
            <div class="box image-box">
              <img :src="image.url" alt="image" class="main-image" @click="openModal(image.url)" style="cursor: zoom-in;" />
            </div>
            <div class="box">
              <h2 class="title is-4">{{ image.title }}</h2>
              <div class="media">
                <div class="media-left">
                  <figure class="image is-48x48">
                    <img :src="image.author.avatar" alt="author" class="is-rounded" />
                  </figure>
                </div>
                <div class="media-content">
                  <p class="subtitle is-6"><strong>{{ image.author.name }}</strong></p>
                  <p class="is-size-7">发布于 {{ image.date }}</p>
                </div>
                <div class="media-right">
                  <div class="buttons">
                    <button class="button is-warning" @click="toggleLike">{{ liked ? '已收藏' : '收藏' }}</button>
                    <a :href="image.url" class="button is-link" target="_blank" download>下载</a>
                  </div>
                </div>
              </div>

              <div class="tags" style="margin-top: 12px">
                <span class="tag" v-for="tag in image.tags" :key="tag">{{ tag }}</span>
              </div>

              <div class="content" style="margin-top: 12px">
                <p>{{ image.description }}</p>
              </div>
            </div>
          </div>

          <div class="column">
            <div class="box">
              <h3 class="title is-5">评论</h3>
              <div v-if="comments.length">
                <div v-for="c in comments" :key="c.id" style="margin-bottom:12px">
                  <p class="is-size-7"><strong>{{ c.author }}</strong> · <span class="is-size-7">{{ c.date }}</span></p>
                  <p>{{ c.content }}</p>
                </div>
              </div>
              <div v-else>
                <p class="is-size-7">暂无评论</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
    <div v-if="showModal" class="image-modal" @click.self="closeModal">
      <button class="close-btn" @click="closeModal">关闭</button>
      <div class="modal-content">
        <img :src="modalImageSrc" alt="modal-image" />
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import Navbar from './Navbar.vue';

export default {
  name: 'ImageView',
  components: { Navbar },
  props: {
    id: {
      type: [String, Number],
      default: null,
    },
  },
  data() {
    return {
      image: {
        id: null,
        url: '',
        title: '',
        description: '',
        date: '',
        tags: [],
        author: { name: '', avatar: '' },
      },
      
      liked: false,
    comments: [],
      showModal: false,
      modalImageSrc: '',
    };
  },
  created() {
    const imgId = this.id || this.$route.params.id || 1;
    this.fetchImage(imgId);
    this.fetchComments(imgId);
  },
  methods: {
    openModal(src) {
      this.modalImageSrc = src;
      this.showModal = true;
    },
    fetchComments(id) {
      axios
        .get(`/api/image/${id}/comments`)
        .then((res) => {
          if (res && res.data && res.data.comments) {
            this.comments = res.data.comments;
          }
        })
        .catch(() => {
          this.comments = [];
        });
    },
    closeModal() {
      this.showModal = false;
      this.modalImageSrc = '';
    },
    fetchImage(id) {
      // 尝试从 mock 接口获取，若失败则使用示例数据
      axios
        .get(`/api/image/${id}`)
        .then((res) => {
          if (res && res.data) {
            this.image = res.data;
          }
        })
        .catch(() => {
          // 回退示例数据
          this.image = {
            id,
            url: '/static/sample-illu.jpg',
            title: '示例插画标题',
            description: '这是一个示例描述，用于展示 ImageView 布局。',
            date: '2025-09-22',
            tags: ['原创', '二次元', '清新'],
            author: { name: '示例作者', avatar: '/static/avatar.png' },
          };
        });
    },
    
    toggleLike() {
      this.liked = !this.liked;
    },
  },
};
</script>

<style scoped>
.image-view-root {
  background: transparent;
}
.image-box {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px;
}
.image-box .main-image {
  width: 100%;
  max-height: 80vh;
  object-fit: contain;
}

@media (max-width: 768px) {
  .image-box .main-image {
    max-height: 60vh;
  }
}

/* Modal styles */
.image-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.75);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}
.image-modal .modal-content {
  max-width: 95%;
  max-height: 95%;
}
.image-modal img {
  width: auto;
  height: auto;
  max-width: 100%;
  max-height: 100%;
}
.image-modal .close-btn {
  position: absolute;
  top: 18px;
  right: 18px;
  background: rgba(255,255,255,0.1);
  border: none;
  color: #fff;
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
}

</style>

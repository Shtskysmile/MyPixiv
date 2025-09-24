<template>
  <div class="user-root">
    <div class="user-bg" :style="bgStyle"></div>
    <div class="user-overlay"></div>

    <div class="user-container">
      <div class="card user-card">
        <div class="card-content">
          <div class="media">
            <div class="media-left">
              <figure class="image is-96x96">
                <img :src="avatarSrc" alt="avatar" class="is-rounded" />
              </figure>
            </div>
            <div class="media-content">
              <p class="title is-4">{{ name }}</p>
              <p class="subtitle is-6">{{ role }}</p>
            </div>
          </div>

          <div class="content">
            <p>{{ bio }}</p>
            <div class="buttons">
              <button class="button is-primary" @click="onEdit">编辑资料</button>
              <button class="button is-light" @click="$router.push('/index')">返回首页</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import avatar from '@/assets/images/avatar.png';
import bgImg from '@/assets/images/Alice_Damage.jpg';

export default {
  name: 'UserPage',
  data() {
    return {
      name: 'Alice',
      role: '艺术家',
      bio: '这里是个人简介，写一些关于自己的信息。',
      avatarSrc: avatar,
      bgImg,
    };
  },
  computed: {
    bgStyle() {
      return {
        backgroundImage: `url(${this.bgImg})`,
        backgroundSize: 'cover',
        backgroundPosition: 'center center',
      };
    },
  },
  methods: {
    onEdit() {
      // 这里可以打开编辑模态或导航到编辑页面
      this.$router.push('/profile/edit');
    },
  },
};
</script>

<style scoped>
.user-root {
  position: relative;
  min-height: 100vh;
}
.user-bg {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: -2;
  background-repeat: no-repeat;
  background-size: cover;
  background-position: center center;
}
.user-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  z-index: -1;
}
.user-container {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 24px;
}
.user-card {
  width: 720px;
  max-width: 95%;
  background: rgba(255,255,255,0.06);
  backdrop-filter: blur(6px);
}
.user-card .title,
.user-card .subtitle,
.user-card .content {
  color: #ffffff;
}
.user-card .image img {
  object-fit: cover;
}

@media (max-width: 768px) {
  .user-card {
    width: 100%;
  }
}
</style>

<template>
  <div class="user-root">
    <div class="user-bg" :style="bgStyle"></div>
    <div class="user-overlay"></div>

    <div class="user-container">
      <Navbar />
      <div class="columns is-gapless" style="width:100%">
        <div class="column is-one-quarter">
          <aside class="menu user-menu">
            <p class="menu-label">个人</p>
            <ul class="menu-list">
              <li><a :class="{ 'is-active': view === 'info' }" @click.prevent="view = 'info'">个人信息</a></li>
              <li><a :class="{ 'is-active': view === 'favorites' }" @click.prevent="openFavorites">收藏的画作</a></li>
              <li><a :class="{ 'is-active': view === 'submit' }" @click.prevent="view = 'submit'">提交作品</a></li>
            </ul>
          </aside>
        </div>
        <div class="column">
          <div v-if="view === 'info'">
            <div class="card user-card">
              <div class="card-content">
                <div class="media">
                  <div class="media-left">
                    <figure class="image is-96x96">
                      <img :src="avatarSrc" alt="avatar" class="is-rounded" />
                    </figure>
                  </div>
                  <div class="media-content">
                    <p class="title is-4">{{ user.name }}</p>
                    <p class="subtitle is-6">{{ user.role }}</p>
                  </div>
                </div>

                <div class="content">
                    <p>{{ user.bio }}</p>
                    <div class="buttons">
                      <button class="button is-primary" @click="openEdit">编辑资料</button>
                    <button class="button is-light" @click="$router.push('/index')">返回首页</button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div v-if="view === 'favorites'">
            <div class="box">
              <h3 class="title is-5">收藏</h3>
              <div class="columns is-multiline">
                <div class="column is-one-quarter" v-for="fav in favorites" :key="fav.id">
                  <router-link :to="`/image/${fav.id}`">
                    <figure class="image is-4by3">
                      <img :src="fav.url" :alt="fav.title" />
                    </figure>
                    <p class="is-size-7">{{ fav.title }}</p>
                  </router-link>
                </div>
              </div>
            </div>
          </div>

          <div v-if="view === 'submit'">
            <div class="box">
              <h3 class="title is-5">提交作品</h3>
              <p class="is-size-7">提交表单占位（可以扩展为上传图片及填写信息）</p>
              <button class="button is-primary" @click="$router.push('/upload')">进入提交界面</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  <!-- Edit Modal -->
  <div class="modal" :class="{ 'is-active': editModalVisible }">
    <div class="modal-background" @click="closeEdit"></div>
    <div class="modal-card">
      <header class="modal-card-head">
        <p class="modal-card-title">编辑资料</p>
        <button class="delete" aria-label="close" @click="closeEdit"></button>
      </header>
      <section class="modal-card-body">
        <div class="field">
          <label class="label">姓名</label>
          <div class="control">
            <input class="input" v-model="editName" placeholder="姓名" />
          </div>
        </div>

        <div class="field">
          <label class="label">简介</label>
          <div class="control">
            <textarea class="textarea" v-model="editBio" placeholder="个人简介"></textarea>
          </div>
        </div>
      </section>
      <footer class="modal-card-foot">
        <button class="button is-success" @click="submitEdit">保存</button>
        <button class="button" @click="closeEdit">取消</button>
      </footer>
    </div>
  </div>
  </div>
</template>

<script>
import axios from 'axios';
import Navbar from './Navbar.vue';
import avatar from '@/assets/images/avatar.png';
import bgImg from '@/assets/images/Alice_Damage.jpg';

export default {
  name: 'UserPage',
  components: { Navbar },
  data() {
    return {
      user: {
        id: 1,
        name: 'Alice',
        role: '艺术家',
        bio: '这里是个人简介，写一些关于自己的信息。',
        avatar: avatar,
      },
      favorites: [],
      view: 'info', // info | favorites | submit
      editModalVisible: false,
      editName: '',
      editBio: '',
      bgImg,
    };
  },
  computed: {
    avatarSrc() {
      return this.user.avatar || avatar;
    },
    bgStyle() {
      return {
        backgroundImage: `url(${this.bgImg})`,
        backgroundSize: 'cover',
        backgroundPosition: 'center center',
      };
    },
  },
  created() {
    this.fetchUser();
  },
  methods: {
    onEdit() {
      this.$router.push('/profile/edit');
    },
    openEdit() {
      this.editName = this.user.name;
      this.editBio = this.user.bio;
      this.editModalVisible = true;
    },
    closeEdit() {
      this.editModalVisible = false;
    },
    submitEdit() {
      const payload = { name: this.editName, bio: this.editBio };
      axios.post(`/api/user/${this.user.id}`, payload).then((res) => {
        if (res && res.data) {
          this.user = res.data;
        }
        this.closeEdit();
      }).catch(() => {
        // ignore
        this.closeEdit();
      });
    },
    fetchUser() {
      axios.get('/api/user/1').then((res) => {
        if (res && res.data) {
          this.user = res.data;
        }
      }).catch(() => {});
    },
    openFavorites() {
      this.view = 'favorites';
      if (!this.favorites.length) {
        axios.get(`/api/user/${this.user.id}/favorites`).then((res) => {
          if (res && res.data && res.data.list) {
            this.favorites = res.data.list;
          }
        }).catch(() => {
          this.favorites = [];
        });
      }
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

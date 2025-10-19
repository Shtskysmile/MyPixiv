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
            <Profile :user="user" @edit="openEdit" @back="$router.push('/index')" />
          </div>

          <div v-if="view === 'favorites'">
            <FavoritesList :favorites="favorites" @unfavorite="unfavorite" />
          </div>

          <div v-if="view === 'submit'">
            <div class="box submit-grid">
              <div class="preview-card">
                <div class="preview-inner">
                  <div v-if="imagePreviews && imagePreviews.length" class="preview-paged">
                    <div class="preview-single">
                      <img :src="imagePreviews[imageIndex]" alt="preview" />
                    </div>
                    <div class="preview-controls">
                      <button class="button is-small" @click="prevImage">上一张</button>
                      <span style="margin:0 8px">{{ imageIndex + 1 }} / {{ imagePreviews.length }}</span>
                      <button class="button is-small" @click="nextImage">下一张</button>
                      <button class="button is-small is-danger" style="margin-left:12px" @click="deleteCurrentImage">删除图片</button>
                    </div>
                  </div>
                  <div class="preview-empty" v-else>
                    <div class="placeholder">预览区</div>
                  </div>
                </div>
              
                <div class="preview-actions">
                  <input type="file" accept="image/*" multiple @change="onImageChange" />
                </div>
              </div>

              <div class="form-column">
                <h3 class="title is-5">提交作品</h3>
                <div class="field">
                  <label class="label">画作类型</label>
                  <div class="control">
                    <div class="select">
                      <select v-model="artType">
                        <option value="illustration">插画</option>
                        <option value="manga">漫画</option>
                        <option value="concept">概念</option>
                      </select>
                    </div>
                  </div>
                </div>

                <div class="field">
                  <label class="label">画作标题</label>
                  <div class="control">
                    <input class="input" v-model="artTitle" placeholder=" 输入画作标题" />
                  </div>
                </div>

                <div class="field">
                  <label class="label">画作描述</label>
                  <div class="control">
                    <textarea class="textarea" v-model="artDesc" placeholder="撰写画作描述"></textarea>
                  </div>
                </div>

                <div class="field">
                  <div class="control">
                    <button class="button is-primary" :class="{ 'is-loading': submitting }" @click="submitArtwork" :disabled="submitting">提交画作</button>
                  </div>
                </div>

                <p class="help is-danger" v-if="submitError">{{ submitError }}</p>
                <p class="help is-success" v-if="submitSuccess">{{ submitSuccess }}</p>
              </div>
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
import SubmitArtwork from './SubmitArtwork.vue';
import FavoritesList from './FavoritesList.vue';
import Profile from './Profile.vue';
import avatar from '@/assets/images/avatar.png';
import bgImg from '@/assets/images/Alice_Damage.jpg';

export default {
  name: 'UserPage',
  components: { Navbar, SubmitArtwork, FavoritesList, Profile },
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
      // submit fields
  artType: 'illustration',
  artTitle: '',
  imageFiles: [],
  imagePreviews: [],
  imageIndex: 0,
      artDesc: '',
      submitError: '',
      submitting: false,
      submitSuccess: '',
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
      // 请求用户收藏（使用 mock 接口 /api/user/:id/favorite）
      axios.get(`/api/user/${this.user.id}/favorite`).then((res) => {
        // 兼容不同后端结构：直接数组或 { data: [...] }
        let list = [];
        if (!res) {
          list = [];
        } else if (Array.isArray(res.data)) {
          list = res.data;
        } else if (Array.isArray(res)) {
          list = res;
        } else if (res.data && Array.isArray(res.data.items)) {
          list = res.data.items;
        } else if (res.data && res.data.favorites) {
          list = res.data.favorites;
        } else if (res.data && res.data.list) {
          list = res.data.list;
        }
        // 最后兜底：如果还是空，保持空数组
        this.favorites = list || [];
      }).catch(() => {
        this.favorites = [];
      });
    },
    unfavorite(fav) {
      const ok = window.confirm(`确定取消收藏《${fav.title}》吗？`);
      if (!ok) return;
      // 这里由于是 mock，直接从本地列表移除；真实场景应调用后端接口
      this.favorites = this.favorites.filter((f) => f.id !== fav.id);
      window.alert('已取消收藏');
    },
    onImageChange(e) {
      const files = e.target.files ? Array.from(e.target.files) : [];
      if (!files.length) return;
      this.imageFiles = files;
      this.imagePreviews = [];
      files.forEach((file) => {
        const reader = new FileReader();
        reader.onload = (ev) => {
          this.imagePreviews.push(ev.target.result);
        };
        reader.readAsDataURL(file);
      });
      this.imageIndex = 0;
    },
    async submitArtwork() {
      this.submitError = '';
      this.submitSuccess = '';
      if (!this.artTitle.trim()) {
        this.submitError = '请输入画作标题';
        return;
      }
      if (!this.imageFiles || !this.imageFiles.length) {
        this.submitError = '请上传至少一张画作图片';
        return;
      }
      this.submitting = true;
      try {
        const fd = new FormData();
        fd.append('type', this.artType);
        fd.append('title', this.artTitle.trim());
        fd.append('description', this.artDesc.trim());
  // 多图：每个文件追加为 images[]
  this.imageFiles.forEach((f) => fd.append('images[]', f));

        const res = await axios.post(`/api/user/${this.user.id}/submit`, fd, {
          headers: { 'Content-Type': 'multipart/form-data' }
        });
        if (res && res.data && res.data.success) {
          this.submitSuccess = '提交成功（mock）';
          // 重置表单
          this.artTitle = '';
          this.imageFiles = [];
          this.imagePreviews = [];
          this.imageIndex = 0;
          this.artDesc = '';
        } else {
          this.submitError = (res && res.data && res.data.message) || '提交失败';
        }
      } catch (e) {
        this.submitError = e.response && e.response.data && e.response.data.message ? e.response.data.message : String(e);
      } finally {
        this.submitting = false;
      }
    },
    prevImage() {
      if (!this.imagePreviews || !this.imagePreviews.length) return;
      this.imageIndex = (this.imageIndex - 1 + this.imagePreviews.length) % this.imagePreviews.length;
    },
    nextImage() {
      if (!this.imagePreviews || !this.imagePreviews.length) return;
      this.imageIndex = (this.imageIndex + 1) % this.imagePreviews.length;
    },
    deleteCurrentImage() {
      const idx = this.imageIndex;
      if (idx < 0 || idx >= this.imagePreviews.length) return;
      const ok = window.confirm('确定删除当前已选图片吗？');
      if (!ok) return;
      // 移除 previews 和 files
      this.imagePreviews.splice(idx, 1);
      if (this.imageFiles && this.imageFiles.length > idx) {
        this.imageFiles.splice(idx, 1);
      }
      // 调整索引
      if (this.imagePreviews.length === 0) {
        this.imageIndex = 0;
      } else if (this.imageIndex >= this.imagePreviews.length) {
        this.imageIndex = this.imagePreviews.length - 1;
      }
    },
    onArtworkSubmitted(artwork) {
      // 简单处理：提醒并可将返回的作品追加到用户收藏或个人作品列表
      window.alert('作品提交成功（mock）: ' + (artwork && artwork.title ? artwork.title : '已提交'));
      // 如果需要，可以将 artwork 添加到本地列表，例如 favorites 或 user.works
      // this.favorites.unshift(artwork);
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
.favorites-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.favorite-item {
  display: flex;
  align-items: center;
  padding: 10px;
  background: rgba(255,255,255,0.02); /* 轻微对比，适配整体主题 */
  border-radius: 6px;
  border: 1px solid rgba(0,0,0,0.04);
}
.fav-thumb {
  width: 120px;
  height: 90px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 12px;
}
.fav-meta {
  display: flex;
  flex-direction: column;
}
.fav-title {
  font-weight: 600;
  color: #222; /* 深色文字，保证在白色盒子中可读 */
}
.fav-author {
  display: flex;
  align-items: center;
  margin-top: 6px;
  font-size: 12px;
  color: #666;
}
.author-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 8px;
}
.fav-stats {
  margin-top: 8px;
  color: #666;
}
.fav-actions {
  margin-left: auto;
}

/* Submit form improvements */
.submit-grid {
  display: grid;
  grid-template-columns: 360px 1fr;
  gap: 20px;
  align-items: start;
}
.preview-card {
  background: rgba(255,255,255,0.03);
  border-radius: 8px;
  padding: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.preview-inner {
  width: 100%;
  height: 220px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0,0,0,0.04);
  border-radius: 6px;
}
.preview-img img {
  max-width: 100%;
  max-height: 100%;
  border-radius: 6px;
}
.preview-empty .placeholder {
  color: rgba(255,255,255,0.5);
}
.preview-actions { margin-top: 12px; }

@media (max-width: 900px) {
  .submit-grid { grid-template-columns: 1fr; }
  .preview-inner { height: 180px; }
}

/* 多图预览样式 */
.preview-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
  width: 100%;
  padding: 8px;
}
.preview-grid .preview-cell img {
  width: 100%;
  height: 100px;
  object-fit: cover;
  border-radius: 6px;
}

.preview-paged {
  width: 100%;
}
.preview-single img {
  width: 100%;
  max-height: 380px;
  object-fit: contain;
  border-radius: 6px;
}
.preview-controls {
  margin-top: 8px;
  display: flex;
  align-items: center;
}

@media (max-width: 768px) {
  .user-card {
    width: 100%;
  }
}
</style>

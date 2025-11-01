<template>
  <div class="user-root">
    <div class="user-bg" :style="bgStyle"></div>
    <div class="user-overlay"></div>

    <div class="user-container">
      <Navbar :noContainer="true" />
      <div class="columns is-gapless" style="width:100%">
        <div class="column is-one-quarter">
          <aside class="menu user-menu anime-menu">
            <p class="menu-label anime-label">
              <span class="icon">👤</span> 个人中心
            </p>
            <ul class="menu-list">
              <li><a :class="{ 'is-active': view === 'info' }" @click.prevent="view = 'info'">
                <span class="icon">📋</span> 个人信息
              </a></li>
              <li><a :class="{ 'is-active': view === 'favorites' }" @click.prevent="openFavorites">
                <span class="icon">⭐</span> 收藏的画作
              </a></li>
              <li><a :class="{ 'is-active': view === 'likes' }" @click.prevent="openLikes">
                <span class="icon">❤️</span> 点赞的画作
              </a></li>
              <li><a :class="{ 'is-active': view === 'works' }" @click.prevent="openWorks">
                <span class="icon">🎨</span> 我的画作
              </a></li>
              <li><a :class="{ 'is-active': view === 'followers' }" @click.prevent="openFollowers">
                <span class="icon">👥</span> 我的关注
              </a></li>
              <li><a :class="{ 'is-active': view === 'submit' }" @click.prevent="view = 'submit'">
                <span class="icon">📤</span> 提交作品
              </a></li>
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

          <div v-if="view === 'likes'">
            <LikesList :likes="likes" @unlike="unlike" />
          </div>

          <div v-if="view === 'works'">
            <WorksList :works="userWorks" :page="worksPage" :pageSize="12" :total="worksTotal" @page-change="openWorks" />
          </div>

          <div v-if="view === 'followers'">
            <FollowersList :followers="followers" :page="followersPage" :pageSize="10" :total="followersTotal" @page-change="openFollowers" @remove="unfollow" />
          </div>

          <div v-if="view === 'submit'">
            <SubmitArtwork :user="user" @submitted="onArtworkSubmitted" />
          </div>
        </div>
      </div>
    </div>
    
    <!-- Edit Modal -->
    <div class="modal" :class="{ 'is-active': editModalVisible }">
      <div class="modal-background" @click="closeEdit"></div>
      <div class="modal-card anime-modal">
        <header class="modal-card-head">
          <p class="modal-card-title">✏️ 编辑资料</p>
          <button class="delete" aria-label="close" @click="closeEdit"></button>
        </header>
        <section class="modal-card-body">
          <div class="field">
            <label class="label">姓名</label>
            <div class="control">
              <input class="input anime-input" v-model="editName" placeholder="姓名" />
            </div>
          </div>

          <div class="field">
            <label class="label">简介</label>
            <div class="control">
              <textarea class="textarea anime-input" v-model="editBio" placeholder="个人简介" rows="4"></textarea>
            </div>
          </div>
        </section>
        <footer class="modal-card-foot">
          <button class="button is-success anime-button" @click="submitEdit">保存</button>
          <button class="button anime-button" @click="closeEdit">取消</button>
        </footer>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import Navbar from '../Navbar.vue';
import SubmitArtwork from './SubmitArtwork.vue';
import FavoritesList from './FavoritesList.vue';
import LikesList from './LikesList.vue';
import WorksList from './WorksList.vue';
import Profile from './Profile.vue';
import FollowersList from './FollowersList.vue';
import avatar from '@/assets/images/avatar.png';
import bgImg from '@/assets/images/Alice_Damage.jpg';

export default {
  name: 'UserPage',
  components: { Navbar, SubmitArtwork, FavoritesList, LikesList, FollowersList, Profile, WorksList },
  data() {
    return {
      user: {
        userId: '1',
        username: 'Alice',
        role: 0,
        sex: 0,
        bio: '这里是个人简介，写一些关于自己的信息。',
        avatar: avatar,
      },
      favorites: [],
      likes: [],
      followers: [],
      followersPage: 1,
      followersTotal: 0,
      userWorks: [],
      worksPage: 1,
      worksTotal: 0,
      view: 'info',
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
    fetchUser() {
      const token = localStorage.getItem('token') || 'mock-token-123';
      const params = new URLSearchParams();
      params.append('userId', this.user.userId);

      // 使用后端接口 POST /userInfo，返回 Result<R_UserInfoDTO>
      axios.post('/userInfo', params, {
        headers: {
          'Authorization': 'Bearer ' + token
        }
      })
        .then((res) => {
          if (res.data && res.data.code === 0) {
            // R_UserInfoDTO { user: R_User, isConcerned: boolean }
            const data = res.data.data;
            if (data && data.user) {
              this.user = {
                userId: data.user.userId,
                username: data.user.username,
                role: data.user.role,
                sex: data.user.sex,
                avatar: data.user.avatar,
                bio: data.user.bio || '这里是个人简介'
              };
            }
          }
        })
        .catch(() => {});
    },
    
    openEdit() {
      this.editName = this.user.username;
      this.editBio = this.user.bio;
      this.editModalVisible = true;
    },
    
    closeEdit() {
      this.editModalVisible = false;
    },
    
    submitEdit() {
      // TODO: 使用后端接口 POST /user/updateUserInfo
      const token = localStorage.getItem('token') || 'mock-token-123';
      const formData = new FormData();
      formData.append('newUsername', this.editName);
      formData.append('newGender', this.user.sex);

      axios.post('/user/updateUserInfo', formData, {
        headers: {
          'Authorization': 'Bearer ' + token
        }
      })
        .then((res) => {
          if (res.data && res.data.code === 0) {
            this.user.username = this.editName;
            this.user.bio = this.editBio;
            alert('更新成功！');
          } else {
            alert('更新失败: ' + (res.data?.message || '未知错误'));
          }
          this.closeEdit();
        })
        .catch(() => {
          alert('更新失败');
          this.closeEdit();
        });
    },
    
    openFavorites() {
      this.view = 'favorites';
      const params = new URLSearchParams();
      params.append('userId', this.user.userId);

      // 使用后端接口 POST /favouriteList，返回 Result<List<R_OverviewContribution>>
      axios.post('/favouriteList', params)
        .then((res) => {
          if (res.data && res.data.code === 0) {
            this.favorites = res.data.data || [];
          }
        })
        .catch(() => {
          this.favorites = [];
        });
    },
    
    openLikes() {
      this.view = 'likes';
      const params = new URLSearchParams();
      params.append('userId', this.user.userId);

      // 使用后端接口 POST /likedList，返回 Result<List<R_OverviewContribution>>
      axios.post('/likedList', params)
        .then((res) => {
          if (res.data && res.data.code === 0) {
            this.likes = res.data.data || [];
          }
        })
        .catch(() => {
          this.likes = [];
        });
    },
    
    openWorks(page = 1) {
      this.view = 'works';
      const p = typeof page === 'number' ? page : (page && page.detail) || 1;
      const params = new URLSearchParams();
      params.append('userId', this.user.userId);

      // 使用后端接口 POST /contributionList，返回 Result<List<R_OverviewContribution>>
      axios.post('/contributionList', params)
        .then((res) => {
          if (res.data && res.data.code === 0) {
            const list = res.data.data || [];
            // 前端分页
            this.worksPage = p;
            this.worksTotal = list.length;
            const pageSize = 12;
            const start = (p - 1) * pageSize;
            const end = start + pageSize;
            this.userWorks = list.slice(start, end);
          }
        })
        .catch(() => {
          this.userWorks = [];
          this.worksPage = 1;
          this.worksTotal = 0;
        });
    },
    
    unlike(item) {
      const ok = window.confirm(`确定取消点赞《${item.title}》吗？`);
      if (!ok) return;
      
      // TODO: 调用后端接口 POST /user/unlikeContribution
      const token = localStorage.getItem('token') || 'mock-token-123';
      const params = new URLSearchParams();
      params.append('contributionId', item.contributionId || item.id);

      axios.post('/user/unlikeContribution', params, {
        headers: {
          'Authorization': 'Bearer ' + token
        }
      })
        .then((res) => {
          if (res.data && res.data.code === 0) {
            this.likes = this.likes.filter((l) => (l.contributionId || l.id) !== (item.contributionId || item.id));
            alert('已取消点赞');
          }
        })
        .catch(() => {
          alert('取消点赞失败');
        });
    },
    
    unfavorite(fav) {
      const ok = window.confirm(`确定取消收藏《${fav.title}》吗？`);
      if (!ok) return;
      
      // TODO: 调用后端接口 POST /user/unfavoriteContribution
      const token = localStorage.getItem('token') || 'mock-token-123';
      const params = new URLSearchParams();
      params.append('contributionId', fav.contributionId || fav.id);

      axios.post('/user/unfavoriteContribution', params, {
        headers: {
          'Authorization': 'Bearer ' + token
        }
      })
        .then((res) => {
          if (res.data && res.data.code === 0) {
            this.favorites = this.favorites.filter((f) => (f.contributionId || f.id) !== (fav.contributionId || fav.id));
            alert('已取消收藏');
          }
        })
        .catch(() => {
          alert('取消收藏失败');
        });
    },
    
    openFollowers(page = 1) {
      this.view = 'followers';
      const p = typeof page === 'number' ? page : (page && page.detail) || 1;
      const params = new URLSearchParams();
      params.append('userId', this.user.userId);

      // 使用后端接口 POST /concernedList，返回 Result<List<R_User>>
      axios.post('/concernedList', params)
        .then((res) => {
          if (res.data && res.data.code === 0) {
            const list = res.data.data || [];
            // 前端分页
            this.followersPage = p;
            this.followersTotal = list.length;
            const pageSize = 10;
            const start = (p - 1) * pageSize;
            const end = start + pageSize;
            this.followers = list.slice(start, end);
          }
        })
        .catch(() => {
          this.followers = [];
          this.followersPage = 1;
          this.followersTotal = 0;
        });
    },
    
    unfollow(f) {
      const ok = window.confirm(`确定取消关注 ${f.username || f.name} 吗？`);
      if (!ok) return;
      
      // TODO: 调用后端接口 POST /user/unconcernUser
      const token = localStorage.getItem('token') || 'mock-token-123';
      const params = new URLSearchParams();
      params.append('concernedUserId', f.userId || f.id);

      axios.post('/user/unconcernUser', params, {
        headers: {
          'Authorization': 'Bearer ' + token
        }
      })
        .then((res) => {
          if (res.data && res.data.code === 0) {
            this.followers = this.followers.filter(x => (x.userId || x.id) !== (f.userId || f.id));
            alert('已取消关注');
          }
        })
        .catch(() => {
          alert('取消关注失败');
        });
    },
    
    onArtworkSubmitted(artwork) {
      alert('作品提交成功！');
      // 刷新作品列表
      if (this.view === 'works') {
        this.openWorks(1);
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
  background: rgba(0, 0, 0, 0.6);
  z-index: -1;
}

.user-container {
  position: relative;
  z-index: 1;
  min-height: 100vh;
  padding: 24px;
}

/* 二次元风格菜单 */
.anime-menu {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 8px 32px rgba(147, 51, 234, 0.15);
  border: 2px solid rgba(255, 105, 180, 0.2);
}

.anime-label {
  color: #6366f1;
  font-weight: 800;
  font-size: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: linear-gradient(135deg, #667eea15 0%, #764ba215 100%);
  border-radius: 8px;
  margin-bottom: 16px;
}

.menu-list a {
  border-radius: 12px;
  transition: all 0.3s ease;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  color: #6b7280;
}

.menu-list a:hover {
  background: linear-gradient(135deg, #667eea15 0%, #764ba215 100%);
  color: #6366f1;
  transform: translateX(4px);
}

.menu-list a.is-active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(147, 51, 234, 0.3);
}

/* 二次元风格模态框 */
.anime-modal .modal-card-head {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.anime-modal .modal-card-title {
  color: white;
  font-weight: 800;
}

.anime-modal .delete {
  background: rgba(255, 255, 255, 0.3);
}

.anime-modal .delete:hover {
  background: rgba(255, 255, 255, 0.5);
}

.anime-input {
  border: 2px solid rgba(147, 51, 234, 0.2);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.anime-input:focus {
  border-color: #a78bfa;
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.1);
}

.anime-button {
  border-radius: 12px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.anime-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(147, 51, 234, 0.2);
}

@media (max-width: 768px) {
  .anime-menu {
    margin-bottom: 20px;
  }
}
</style>

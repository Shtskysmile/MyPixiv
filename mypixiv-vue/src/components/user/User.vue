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
              <span class="icon">👤</span> {{ isOwnProfile ? '个人中心' : '用户主页' }}
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
                <span class="icon">🎨</span> {{ isOwnProfile ? '我的' : 'TA的' }}画作
              </a></li>
              <li><a :class="{ 'is-active': view === 'followers' }" @click.prevent="openFollowers">
                <span class="icon">👥</span> {{ isOwnProfile ? '我的' : 'TA的' }}关注
              </a></li>
              <li v-if="isOwnProfile"><a :class="{ 'is-active': view === 'submit' }" @click.prevent="view = 'submit'">
                <span class="icon">📤</span> 提交作品
              </a></li>
            </ul>
          </aside>
        </div>
        
        <div class="column">
          <div v-if="view === 'info'">
            <Profile :user="user" :isOwnProfile="isOwnProfile" @edit="openEdit" @back="$router.push('/index')" />
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
            <label class="label">性别</label>
            <div class="control">
              <div class="select is-fullwidth anime-input">
                <select v-model="editGender">
                  <option :value="0">未设置</option>
                  <option :value="1">男</option>
                  <option :value="2">女</option>
                </select>
              </div>
            </div>
          </div>

          <div class="field">
            <label class="label">头像（可选）</label>
            <div class="control">
              <input class="input anime-input" type="file" @change="onAvatarChange" accept="image/*" />
            </div>
            <p class="help">不上传则保持原头像不变</p>
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
import request from '@/utils/request';
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
  props: {
    id: {
      type: String,
      default: null
    }
  },
  data() {
    return {
      user: {
        userId: '',
        username: '加载中...',
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
      editGender: 0,
      editAvatar: null,
      bgImg,
    };
  },
  computed: {
    avatarSrc() {
      if (!this.user.avatar) {
        return avatar; // 默认头像
      }
      // 如果是完整URL，直接返回
      if (this.user.avatar.startsWith('http')) {
        return this.user.avatar;
      }
      // 如果已经包含 /files/ 前缀，直接返回
      if (this.user.avatar.startsWith('/files/')) {
        return this.user.avatar;
      }
      // 否则拼接 /files/ 前缀
      return `/files/${this.user.avatar}`;
    },
    bgStyle() {
      return {
        backgroundImage: `url(${this.bgImg})`,
        backgroundSize: 'cover',
        backgroundPosition: 'center center',
      };
    },
    // 当前查看的用户ID：优先使用路由参数，否则使用当前登录用户ID
    currentUserId() {
      return this.id || localStorage.getItem('userId');
    },
    // 是否是当前登录用户自己的主页
    isOwnProfile() {
      const loggedInUserId = localStorage.getItem('userId');
      return this.currentUserId === loggedInUserId;
    }
  },
  created() {
    this.fetchUser();
  },
  watch: {
    // 监听路由参数变化，重新加载用户信息
    id(newId) {
      this.fetchUser();
    }
  },
  methods: {
    fetchUser() {
      const userId = this.currentUserId;
      
      if (!userId) {
        alert('未登录，请先登录');
        this.$router.push('/login');
        return;
      }
      
      const params = new URLSearchParams();
      params.append('userId', userId);

      // 使用后端接口 POST /userInfo，返回 Result<R_UserInfoDTO>
      request.post('/userInfo', params)
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
      this.editGender = this.user.sex || 0;
      this.editAvatar = null;
      this.editModalVisible = true;
    },
    
    onAvatarChange(event) {
      const file = event.target.files[0];
      this.editAvatar = file || null;
    },
    
    closeEdit() {
      this.editModalVisible = false;
    },
    
    submitEdit() {
      // 验证输入
      if (!this.editName || this.editName.trim() === '') {
        alert('用户名不能为空');
        return;
      }

      // 准备表单数据
      const formData = new FormData();
      formData.append('newUsername', this.editName.trim());
      formData.append('newGender', this.editGender);
      
      // 只有选择了新头像才添加到表单
      if (this.editAvatar) {
        formData.append('newAvatar', this.editAvatar);
      }

      request.post('/user/updateUserInfo', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
        .then((res) => {
          if (res.data && res.data.code === 0) {
            // 更新本地数据
            this.user.username = this.editName;
            this.user.sex = this.editGender;
            
            // 如果上传了新头像，刷新页面以显示新头像
            if (this.editAvatar) {
              alert('更新成功！页面将刷新以显示新头像。');
              location.reload();
            } else {
              alert('更新成功！');
              this.closeEdit();
            }
          } else {
            alert('更新失败: ' + (res.data?.message || '未知错误'));
          }
        })
        .catch((error) => {
          console.error('更新失败:', error);
          alert('更新失败，请稍后重试');
        });
    },
    
    openFavorites() {
      this.view = 'favorites';
      const params = new URLSearchParams();
      params.append('userId', this.currentUserId);

      // 使用后端接口 POST /favouriteList，返回 Result<List<R_OverviewContribution>>
      request.post('/favouriteList', params)
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
      params.append('userId', this.currentUserId);

      // 使用后端接口 POST /likedList，返回 Result<List<R_OverviewContribution>>
      request.post('/likedList', params)
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
      params.append('userId', this.currentUserId);

      // 使用后端接口 POST /contributionList，返回 Result<List<R_OverviewContribution>>
      request.post('/contributionList', params)
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
      const params = new URLSearchParams();
      params.append('contributionId', item.contributionId || item.id);

      request.post('/user/unlikeContribution', params)
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
      const params = new URLSearchParams();
      params.append('contributionId', fav.contributionId || fav.id);

      request.post('/user/unfavoriteContribution', params)
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
      params.append('userId', this.currentUserId);

      // 使用后端接口 POST /concernedList，返回 Result<List<R_User>>
      request.post('/concernedList', params)
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
      const params = new URLSearchParams();
      params.append('concernedUserId', f.userId || f.id);

      request.post('/user/unconcernUser', params)
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
  padding-top: 84px; /* 60px navbar高度 + 24px间距 */
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

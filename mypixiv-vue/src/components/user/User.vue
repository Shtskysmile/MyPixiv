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
            <Profile 
              :user="user" 
              :isOwnProfile="isOwnProfile" 
              :isConcerned="isConcerned"
              :stats="userStats"
              @edit="openEdit" 
              @back="$router.push('/index')" 
              @toggle-concern="handleToggleConcern"
            />
          </div>

          <div v-if="view === 'favorites'">
            <FavoritesList :favorites="favorites" :isOwnProfile="isOwnProfile" @unfavorite="unfavorite" />
          </div>

          <div v-if="view === 'likes'">
            <LikesList :likes="likes" :isOwnProfile="isOwnProfile" @unlike="unlike" />
          </div>

          <div v-if="view === 'works'">
            <WorksList :works="userWorks" :page="worksPage" :pageSize="12" :total="worksTotal" :isOwnProfile="isOwnProfile" @page-change="openWorks" @edit="handleEditWork" @delete="handleDeleteWork" />
          </div>

          <div v-if="view === 'followers'">
            <FollowersList :followers="followers" :page="followersPage" :pageSize="10" :total="followersTotal" :isOwnProfile="isOwnProfile" @page-change="openFollowers" @remove="unfollow" />
          </div>

          <div v-if="view === 'submit'">
            <SubmitArtwork :user="user" :editWork="editingWork" @submitted="onArtworkSubmitted" @cancel-edit="cancelEditWork" />
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
            <label class="label anime-label">
              <span class="icon">📸</span> 头像（可选）
            </label>
            <div class="file has-name is-fullwidth anime-file">
              <label class="file-label">
                <input 
                  class="file-input" 
                  type="file" 
                  accept="image/*" 
                  @change="onAvatarChange"
                >
                <span class="file-cta">
                  <span class="file-icon">
                    <i>📁</i>
                  </span>
                  <span class="file-label">选择图片</span>
                </span>
                <span class="file-name">
                  {{ editAvatarFileName || '未选择文件' }}
                </span>
              </label>
            </div>
            <div v-if="editAvatarPreview" class="avatar-preview">
              <img :src="editAvatarPreview" alt="头像预览" />
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
        status: 0,
        avatar: avatar,
      },
      isConcerned: false, // 是否已关注该用户
      userStats: {
        following: 0,
        followers: 0,
        works: 0,
        favorites: 0
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
      editingWork: null, // 正在编辑的作品
      editModalVisible: false,
      editName: '',
      editGender: 0,
      editAvatar: null,
      editAvatarFileName: '',
      editAvatarPreview: '',
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
      // 后端返回的路径格式: /files/userId/avatar/xxx.jpg
      // 需要拼接基础 URL
      const baseURL = process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080';
      const avatarPath = this.user.avatar.startsWith('/') ? this.user.avatar : `/${this.user.avatar}`;
      return `${baseURL}${avatarPath}`;
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
    id(newId, oldId) {
      // 重置所有状态
      this.resetPageData();
      // 重新加载用户信息
      this.fetchUser();
    },
    // 监听整个路由变化（包括从 /user 到 /user/:id）
    '$route'(to, from) {
      // 如果是在用户页面之间切换
      if (to.name === 'user-id' || to.name === 'user') {
        const oldUserId = from.params.id || localStorage.getItem('userId');
        const newUserId = to.params.id || localStorage.getItem('userId');
        
        // 只有当用户ID真正改变时才重置和刷新
        if (oldUserId !== newUserId) {
          this.resetPageData();
          this.fetchUser();
        }
      }
    }
  },
  methods: {
    resetPageData() {
      // 重置用户信息
      this.user = {
        userId: '',
        username: '加载中...',
        role: 0,
        sex: 0,
        status: 0,
        avatar: avatar,
      };
      // 重置关注状态
      this.isConcerned = false;
      // 重置统计数据
      this.userStats = {
        following: 0,
        followers: 0,
        works: 0,
        favorites: 0
      };
      // 重置各个列表
      this.favorites = [];
      this.likes = [];
      this.followers = [];
      this.followersPage = 1;
      this.followersTotal = 0;
      this.userWorks = [];
      this.worksPage = 1;
      this.worksTotal = 0;
      // 重置视图为个人信息
      this.view = 'info';
    },
    
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
      // 使用相对路径，由 Vue devServer 代理转发到后端
      request.post('/userInfo', params)
        .then((res) => {
          if (res.data && res.data.code === 0) {
            // R_UserInfoDTO { user: R_User, isConcerned: boolean }
            const data = res.data.data;
            if (data && data.user) {
              // 后端 R_User 结构: userId, username, role, status, sex, avatar
              this.user = {
                userId: data.user.userId,
                username: data.user.username,
                role: data.user.role,
                status: data.user.status,
                sex: data.user.sex,
                avatar: data.user.avatar
              };
              // 设置是否已关注
              this.isConcerned = data.isConcerned || false;
            }
          }
        })
        .catch(() => {});
      
      // 获取统计数据
      this.fetchUserStats(userId);
    },
    
    fetchUserStats(userId) {
      // 重置统计数据
      this.userStats = {
        following: 0,
        followers: 0,
        works: 0,
        favorites: 0
      };
      
      const params = new URLSearchParams();
      params.append('userId', userId);

      // 获取关注数（关注的人数）
      request.post('/concernedList', params)
        .then((res) => {
          if (res.data && res.data.code === 0) {
            this.userStats.following = (res.data.data || []).length;
          }
        })
        .catch(() => {});
      
      // 获取作品数
      if (this.isOwnProfile) {
        // 查看自己的作品数：使用 /user/myContributions 接口
        request.post('/user/myContributions')
          .then((res) => {
            if (res.data && res.data.code === 0) {
              const data = res.data.data;
              const total = (data.pendingContributions || []).length + 
                           (data.approvedContributions || []).length + 
                           (data.dismissalContributions || []).length;
              this.userStats.works = total;
            }
          })
          .catch(() => {});
      } else {
        // 查看他人的作品数：使用 /contributionList 接口
        request.post('/contributionList', params)
          .then((res) => {
            if (res.data && res.data.code === 0) {
              this.userStats.works = (res.data.data || []).length;
            }
          })
          .catch(() => {});
      }
      
      // 获取收藏数
      request.post('/favouriteList', params)
        .then((res) => {
          if (res.data && res.data.code === 0) {
            this.userStats.favorites = (res.data.data || []).length;
          }
        })
        .catch(() => {});
      
      // 注意：后端没有提供"粉丝数"（有多少人关注我）的接口
      // 如果需要，需要后端添加新接口
    },
    
    openEdit() {
      this.editName = this.user.username;
      this.editGender = this.user.sex || 0;
      this.editAvatar = null;
      this.editAvatarFileName = '';
      this.editAvatarPreview = '';
      this.editModalVisible = true;
    },
    
    onAvatarChange(event) {
      const file = event.target.files[0];
      if (file) {
        this.editAvatar = file;
        this.editAvatarFileName = file.name;
        
        // 创建预览
        const reader = new FileReader();
        reader.onload = (evt) => {
          this.editAvatarPreview = evt.target.result;
        };
        reader.readAsDataURL(file);
      } else {
        this.editAvatar = null;
        this.editAvatarFileName = '';
        this.editAvatarPreview = '';
      }
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
            
            // 更新 localStorage 中的用户信息
            localStorage.setItem('username', this.editName);
            
            // 如果后端返回了新的头像路径，更新它
            if (res.data.data && res.data.data.avatar) {
              this.user.avatar = res.data.data.avatar;
              localStorage.setItem('userAvatar', res.data.data.avatar);
            }
            
            // 触发自定义事件通知其他组件更新（例如 Navbar）
            window.dispatchEvent(new Event('userInfoUpdated'));
            
            // 如果上传了新头像，需要重新获取用户信息以确保头像路径正确
            if (this.editAvatar) {
              alert('更新成功！');
              this.closeEdit();
              // 重新获取用户信息以更新头像
              this.fetchUser();
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

      console.log('🔍 [调试] 开始加载作品列表...');
      console.log('🔍 [调试] isOwnProfile:', this.isOwnProfile);
      console.log('🔍 [调试] currentUserId:', this.currentUserId);
      
      if (this.isOwnProfile) {
        // 查看自己的作品：使用 /user/myContributions（包含待审核、已通过、已驳回）
        request.post('/user/myContributions')
          .then((res) => {
            console.log('🔍 [调试] 后端返回完整响应:', res.data);
            
            if (res.data && res.data.code === 0) {
              const data = res.data.data;
              
              console.log('🔍 [调试] 后端返回data对象:', data);
              console.log('🔍 [调试] pendingContributions:', data.pendingContributions);
              console.log('🔍 [调试] approvedContributions:', data.approvedContributions);
              console.log('🔍 [调试] dismissalContributions:', data.dismissalContributions);
              
              // 为每个作品添加 auditStatus 字段
              const pendingWorks = (data.pendingContributions || []).map(work => ({
                ...work,
                auditStatus: 0  // 待审核
              }));
              const approvedWorks = (data.approvedContributions || []).map(work => ({
                ...work,
                auditStatus: 1  // 已通过
              }));
              const dismissalWorks = (data.dismissalContributions || []).map(work => ({
                ...work,
                auditStatus: 2  // 已驳回
              }));
              
              // 打印示例作品的详细信息
              if (pendingWorks.length > 0) {
                console.group('🔍 [调试] 待审核作品示例');
                console.log('完整对象:', pendingWorks[0]);
                console.log('image字段:', pendingWorks[0].image);
                console.log('image字段类型:', typeof pendingWorks[0].image);
                console.log('是否为数组:', Array.isArray(pendingWorks[0].image));
                console.groupEnd();
              }
              
              if (approvedWorks.length > 0) {
                console.group('🔍 [调试] 已通过作品示例');
                console.log('完整对象:', approvedWorks[0]);
                console.log('image字段:', approvedWorks[0].image);
                console.log('image字段类型:', typeof approvedWorks[0].image);
                console.log('是否为数组:', Array.isArray(approvedWorks[0].image));
                console.groupEnd();
              }
              
              // 合并三个列表：待审核、已通过、已驳回
              const allWorks = [
                ...pendingWorks,
                ...approvedWorks,
                ...dismissalWorks
              ];
              
              // 前端分页
              this.worksPage = p;
              this.worksTotal = allWorks.length;
              const pageSize = 12;
              const start = (p - 1) * pageSize;
              const end = start + pageSize;
              this.userWorks = allWorks.slice(start, end);
              
              console.log('✅ 作品数据加载成功:', {
                待审核: pendingWorks.length,
                已通过: approvedWorks.length,
                已驳回: dismissalWorks.length,
                总计: allWorks.length,
                当前页: p,
                显示作品数: this.userWorks.length
              });
            } else {
              console.warn('⚠️ 后端返回code不为0:', res.data);
            }
          })
          .catch((error) => {
            console.error('❌ 加载作品失败:', error);
            console.error('❌ 错误详情:', error.response || error.message);
            this.userWorks = [];
            this.worksPage = 1;
            this.worksTotal = 0;
          });
      } else {
        // 查看他人的作品：使用 /contributionList（只显示已通过的作品）
        const params = new URLSearchParams();
        params.append('userId', this.currentUserId);
        
        request.post('/contributionList', params)
          .then((res) => {
            console.log('🔍 [调试] 他人作品后端返回:', res.data);
            
            if (res.data && res.data.code === 0) {
              const works = res.data.data || [];
              
              // 所有作品都是已通过的，添加 auditStatus 标记
              const allWorks = works.map(work => ({
                ...work,
                auditStatus: 1  // 已通过
              }));
              
              // 前端分页
              this.worksPage = p;
              this.worksTotal = allWorks.length;
              const pageSize = 12;
              const start = (p - 1) * pageSize;
              const end = start + pageSize;
              this.userWorks = allWorks.slice(start, end);
              
              console.log('✅ 他人作品加载成功:', {
                总计: allWorks.length,
                当前页: p,
                显示作品数: this.userWorks.length
              });
            } else {
              console.warn('⚠️ 后端返回code不为0:', res.data);
              this.userWorks = [];
              this.worksPage = 1;
              this.worksTotal = 0;
            }
          })
          .catch((error) => {
            console.error('❌ 加载他人作品失败:', error);
            console.error('❌ 错误详情:', error.response || error.message);
            this.userWorks = [];
            this.worksPage = 1;
            this.worksTotal = 0;
          });
      }
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
      // 清除编辑状态
      this.editingWork = null;
      // 刷新作品列表
      this.openWorks(1);
      // 切换回作品列表视图
      this.view = 'works';
    },
    
    handleEditWork(work) {
      console.log('📝 编辑作品:', work);
      // 设置正在编辑的作品
      this.editingWork = work;
      // 切换到提交作品视图
      this.view = 'submit';
    },
    
    cancelEditWork() {
      console.log('❌ 取消编辑');
      // 清除编辑状态
      this.editingWork = null;
      // 切换回作品列表视图
      this.view = 'works';
    },
    
    handleDeleteWork(work) {
      console.log('🗑️ 删除作品:', work);
      // TODO: 调用后端删除接口
      const params = new URLSearchParams();
      params.append('contributionId', work.contributionId);
      
      request.post('/user/deleteContribution', params)
        .then((res) => {
          if (res.data && res.data.code === 0) {
            alert('删除成功！');
            // 刷新作品列表
            this.openWorks(this.worksPage);
          } else {
            alert('删除失败: ' + (res.data?.message || '未知错误'));
          }
        })
        .catch((error) => {
          console.error('删除失败:', error);
          alert('删除失败，请稍后重试');
        });
    },
    
    handleToggleConcern({ userId, currentState }) {
      if (currentState) {
        // 当前已关注，执行取消关注
        const ok = window.confirm('确定要取消关注吗？');
        if (!ok) return;
        
        const params = new URLSearchParams();
        params.append('concernedUserId', userId);
        
        request.post('/user/unconcernUser', params)
          .then((res) => {
            if (res.data && res.data.code === 0) {
              this.isConcerned = false;
              alert('已取消关注');
              // 刷新统计数据
              this.fetchUserStats(this.currentUserId);
            } else {
              alert('取消关注失败: ' + (res.data?.message || '未知错误'));
            }
          })
          .catch((error) => {
            console.error('取消关注失败:', error);
            alert('取消关注失败，请稍后重试');
          });
      } else {
        // 当前未关注，执行关注
        const params = new URLSearchParams();
        params.append('concernedUserId', userId);
        
        request.post('/user/concernUser', params)
          .then((res) => {
            if (res.data && res.data.code === 0) {
              this.isConcerned = true;
              alert('关注成功！');
              // 刷新统计数据
              this.fetchUserStats(this.currentUserId);
            } else {
              alert('关注失败: ' + (res.data?.message || '未知错误'));
            }
          })
          .catch((error) => {
            console.error('关注失败:', error);
            alert('关注失败，请稍后重试');
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

/* 文件上传样式 */
.anime-file .file-cta {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px 0 0 12px;
  color: white;
  font-weight: 600;
  transition: all 0.3s ease;
}

.anime-file .file-cta:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(147, 51, 234, 0.3);
}

.anime-file .file-name {
  border: 2px solid rgba(147, 51, 234, 0.2);
  border-left: none;
  border-radius: 0 12px 12px 0;
  color: #6b7280;
  font-weight: 500;
}

.avatar-preview {
  margin-top: 16px;
  text-align: center;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.avatar-preview img {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #a78bfa;
  box-shadow: 0 8px 24px rgba(147, 51, 234, 0.3);
  transition: all 0.3s ease;
}

.avatar-preview img:hover {
  transform: scale(1.05);
  box-shadow: 0 12px 32px rgba(147, 51, 234, 0.4);
}

@media (max-width: 768px) {
  .anime-menu {
    margin-bottom: 20px;
  }
}
</style>

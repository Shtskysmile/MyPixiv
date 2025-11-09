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
              <!-- 查看他人页面时：如果被查看的用户不是管理员，则显示这些选项 -->
              <!-- 查看自己页面时：如果自己不是管理员，则显示这些选项 -->
              <li v-if="!isCommunityAdmin && !isSystemAdmin"><a :class="{ 'is-active': view === 'favorites' }" @click.prevent="openFavorites">
                <span class="icon">⭐</span> 收藏的画作
              </a></li>
              <li v-if="!isCommunityAdmin && !isSystemAdmin"><a :class="{ 'is-active': view === 'likes' }" @click.prevent="openLikes">
                <span class="icon">❤️</span> 点赞的画作
              </a></li>
              <li v-if="!isCommunityAdmin && !isSystemAdmin"><a :class="{ 'is-active': view === 'works' }" @click.prevent="openWorks">
                <span class="icon">🎨</span> {{ isOwnProfile ? '我的' : 'TA的' }}画作
              </a></li>
              <li v-if="!isCommunityAdmin && !isSystemAdmin"><a :class="{ 'is-active': view === 'followers' }" @click.prevent="openFollowers">
                <span class="icon">👥</span> {{ isOwnProfile ? '我的' : 'TA的' }}关注
              </a></li>
              <li v-if="!isCommunityAdmin && !isSystemAdmin"><a :class="{ 'is-active': view === 'comments' }" @click.prevent="openComments">
                <span class="icon">💬</span> {{ isOwnProfile ? '我的' : 'TA的' }}评论
              </a></li>
              <li v-if="isOwnProfile && !isCommunityAdmin && !isSystemAdmin"><a :class="{ 'is-active': view === 'submit' }" @click.prevent="view = 'submit'">
                <span class="icon">📤</span> 提交作品
              </a></li>
              <!-- 账户安全设置（所有用户都可见，但只有查看自己时才显示） -->
              <li v-if="isOwnProfile"><a :class="{ 'is-active': view === 'changePassword' }" @click.prevent="view = 'changePassword'">
                <span class="icon">🔑</span> 修改密码
              </a></li>
              <li v-if="isOwnProfile"><a :class="{ 'is-active': view === 'changeSecurityIssues' }" @click.prevent="view = 'changeSecurityIssues'">
                <span class="icon">🛡️</span> 修改密保
              </a></li>
              <!-- 社区管理员菜单（当前登录用户是社区管理员时始终显示） -->
              <li v-if="isLoggedInUserCommunityAdmin"><a :class="{ 'is-active': view === 'audit' }" @click.prevent="openAuditWorks">
                <span class="icon">🔨</span> 审核作品
              </a></li>
              <li v-if="isLoggedInUserCommunityAdmin"><a :class="{ 'is-active': view === 'blockedWorks' }" @click.prevent="view = 'blockedWorks'">
                <span class="icon">🚫</span> 已封禁作品
              </a></li>
              <li v-if="isLoggedInUserCommunityAdmin"><a :class="{ 'is-active': view === 'blockedUsers' }" @click.prevent="view = 'blockedUsers'">
                <span class="icon">🔒</span> 被封禁用户
              </a></li>
              <!-- 系统管理员菜单（当前登录用户是系统管理员时始终显示） -->
              <li v-if="isLoggedInUserSystemAdmin"><a :class="{ 'is-active': view === 'userManagement' }" @click.prevent="view = 'userManagement'">
                <span class="icon">👥</span> 用户管理
              </a></li>
              <li v-if="isLoggedInUserSystemAdmin"><a :class="{ 'is-active': view === 'systemLogs' }" @click.prevent="view = 'systemLogs'">
                <span class="icon">📋</span> 系统日志
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
              :isCommunityAdmin="isCurrentUserCommunityAdmin"
              :isSystemAdmin="isCurrentUserSystemAdmin"
              :stats="userStats"
              @edit="openEdit" 
              @back="$router.push('/index')" 
              @toggle-concern="handleToggleConcern"
              @toggle-block-user="handleToggleBlockUser"
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

          <div v-if="view === 'comments'">
            <CommentsList :comments="comments" :isOwnProfile="isOwnProfile" @delete="handleDeleteComment" />
          </div>

          <div v-if="view === 'submit'">
            <SubmitArtwork :user="user" :editWork="editingWork" @submitted="onArtworkSubmitted" @cancel-edit="cancelEditWork" />
          </div>

          <div v-if="view === 'changePassword'">
            <ChangePassword />
          </div>

          <div v-if="view === 'changeSecurityIssues'">
            <ChangeSecurityIssues />
          </div>

          <div v-if="view === 'audit'">
            <AuditWorksList :auditData="auditData" />
          </div>

          <div v-if="view === 'blockedWorks'">
            <BlockedWorksList />
          </div>

          <div v-if="view === 'blockedUsers'">
            <BlockedUsersList />
          </div>

          <div v-if="view === 'userManagement'">
            <UserManagement />
          </div>

          <div v-if="view === 'systemLogs'">
            <SystemLogs />
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
import CommentsList from './CommentsList.vue';
import ChangePassword from './ChangePassword.vue';
import ChangeSecurityIssues from './ChangeSecurityIssues.vue';
import AuditWorksList from './AuditWorksList.vue';
import BlockedWorksList from './BlockedWorksList.vue';
import BlockedUsersList from './BlockedUsersList.vue';
import UserManagement from './UserManagement.vue';
import SystemLogs from './SystemLogs.vue';

export default {
  name: 'UserPage',
  components: {
    Navbar,
    SubmitArtwork,
    FavoritesList,
    LikesList,
    FollowersList,
    CommentsList,
    Profile,
    WorksList,
    ChangePassword,
    ChangeSecurityIssues,
    AuditWorksList,
    BlockedWorksList,
    BlockedUsersList,
    UserManagement,
    SystemLogs
  },
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
        avatar: '',
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
      comments: [],
      userWorks: [],
      worksPage: 1,
      worksTotal: 0,
      view: 'info',
      editingWork: null, // 正在编辑的作品
      auditData: null, // 审核数据
      editModalVisible: false,
      editName: '',
      editGender: 0,
      editAvatar: null,
      editAvatarFileName: '',
      editAvatarPreview: '',
    };
  },
  computed: {
    avatarSrc() {
      if (!this.user.avatar) {
        return ''; // 没有头像时返回空字符串
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
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      };
    },
    // 当前查看的用户ID：优先使用路由参数，否则使用当前登录用户ID
    currentUserId() {
      return this.id || localStorage.getItem('userId');
    },
    // 是否是当前登录用户自己的主页
    isOwnProfile() {
      const loggedInUserId = localStorage.getItem('userId');
      const result = this.currentUserId === loggedInUserId;
      console.log('🔍 [DEBUG] isOwnProfile:', {
        currentUserId: this.currentUserId,
        loggedInUserId,
        result
      });
      return result;
    },
    // 是否是社区管理员（role为1）- 被查看用户的角色
    isCommunityAdmin() {
      const result = this.user.role === 1;
      console.log('🔍 [DEBUG] isCommunityAdmin:', {
        userRole: this.user.role,
        userId: this.user.userId,
        result
      });
      return result;
    },
    // 是否是系统管理员（role为2）- 被查看用户的角色
    isSystemAdmin() {
      const result = this.user.role === 2;
      console.log('🔍 [DEBUG] isSystemAdmin:', {
        userRole: this.user.role,
        userId: this.user.userId,
        result
      });
      return result;
    },
    // 当前登录用户是否是社区管理员
    isLoggedInUserCommunityAdmin() {
      const currentUserRole = parseInt(localStorage.getItem('userRole') || '0');
      const result = currentUserRole === 1;
      console.log('🔍 [DEBUG] isLoggedInUserCommunityAdmin:', {
        currentUserRole,
        result,
        localStorage_userRole: localStorage.getItem('userRole')
      });
      return result;
    },
    // 当前登录用户是否是系统管理员
    isLoggedInUserSystemAdmin() {
      const currentUserRole = parseInt(localStorage.getItem('userRole') || '0');
      const result = currentUserRole === 2;
      console.log('🔍 [DEBUG] isLoggedInUserSystemAdmin:', {
        currentUserRole,
        result,
        localStorage_userRole: localStorage.getItem('userRole')
      });
      return result;
    },
    // 当前登录用户是否是社区管理员
    isCurrentUserCommunityAdmin() {
      const currentUserRole = parseInt(localStorage.getItem('userRole') || '0');
      // 返回当前登录用户是否是社区管理员（role=1）
      // 系统管理员（role=2）不算社区管理员
      return currentUserRole === 1;
    },
    // 当前登录用户是否是系统管理员
    isCurrentUserSystemAdmin() {
      const currentUserRole = parseInt(localStorage.getItem('userRole') || '0');
      return currentUserRole === 2;
    }
  },
  created() {
    console.log('🎬 [DEBUG] User component created:', {
      id: this.id,
      routeName: this.$route.name,
      routeParams: this.$route.params
    });
    this.fetchUser();
  },
  watch: {
    // 监听路由参数变化，重新加载用户信息
    id(newId, oldId) {
      console.log('🔄 [DEBUG] id prop changed:', { oldId, newId });
      // 只有当 id 真正改变时才重新加载（避免初始化时重复加载）
      if (newId !== oldId && oldId !== undefined) {
        console.log('✅ [DEBUG] id 已改变，重新加载数据');
        this.resetPageData();
        this.fetchUser();
      }
    },
    // 监听整个路由变化（主要用于从 /user 切换到 /user/:id 的情况）
    '$route'(to, from) {
      console.log('🔄 [DEBUG] $route changed:', {
        from: { name: from.name, params: from.params, path: from.path },
        to: { name: to.name, params: to.params, path: to.path }
      });
      
      // 特殊处理：从 /user（无参数）切换到 /user/:id 的情况
      // 这种情况下 id prop 从 null 变为某个值，会被 id watcher 处理
      // 这里主要处理从 /user/:id 切换到 /user 的情况
      if (from.name === 'user-id' && to.name === 'user') {
        console.log('✅ [DEBUG] 从他人主页返回自己主页，重新加载数据');
        this.resetPageData();
        this.fetchUser();
      }
    }
  },
  methods: {
    resetPageData() {
      console.log('🔄 [DEBUG] resetPageData called');
      // 重置用户信息
      this.user = {
        userId: '',
        username: '加载中...',
        role: 0,
        sex: 0,
        status: 0,
        avatar: '',
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
      this.comments = [];
      this.userWorks = [];
      this.worksPage = 1;
      this.worksTotal = 0;
      // 重置视图为个人信息
      this.view = 'info';
    },
    
    fetchUser() {
      const userId = this.currentUserId;
      
      console.log('🔍 [DEBUG] fetchUser called:', {
        userId,
        routeId: this.id,
        localStorageUserId: localStorage.getItem('userId'),
        localStorageUserRole: localStorage.getItem('userRole')
      });
      
      if (!userId) {
        alert('未登录，请先登录');
        this.$router.push('/login');
        return;
      }
      
      const params = new URLSearchParams();
      params.append('userId', userId);

      // 获取token
      const token = localStorage.getItem('token');
      console.log('📥 [User.fetchUser] token:', token);
      const headers = token ? { 'Authorization': `Bearer ${token}` } : {};

      // 使用后端接口 POST /userInfo，返回 Result<R_UserInfoDTO>
      // 使用相对路径，由 Vue devServer 代理转发到后端
      request.post('/userInfo', params, { headers })
        .then((res) => {
          console.log('📥 [User.fetchUser] 获取用户信息响应:', res.data);
          
          if (res.data && res.data.code === 0) {
            // R_UserInfoDTO { user: R_User, isConcerned: boolean }
            const data = res.data.data;
            if (data && data.user) {
              // 后端 R_User 结构: userId, username, role, status, sex, avatar
              console.log('🔍 [DEBUG] 更新 this.user 为:', {
                userId: data.user.userId,
                username: data.user.username,
                role: data.user.role,
                status: data.user.status
              });
              this.user = {
                userId: data.user.userId,
                username: data.user.username,
                role: data.user.role,
                status: data.user.status,
                sex: data.user.sex,
                avatar: data.user.avatar
              };
              
              console.log('✅ [User.fetchUser] 用户信息已更新:', this.user);
              console.log('🖼️ [User.fetchUser] 头像路径详情:');
              console.log('  - 从后端获取的avatar字段:', data.user.avatar);
              console.log('  - 赋值到this.user.avatar:', this.user.avatar);
              
              // 如果是当前登录用户自己的主页，更新 localStorage
              if (this.isOwnProfile) {
                console.log('📝 [User.fetchUser] 这是当前用户自己的主页，更新本地存储...');
                
                // 更新用户名
                if (data.user.username) {
                  localStorage.setItem('username', data.user.username);
                  console.log('✅ [User.fetchUser] 已更新用户名到本地存储:', data.user.username);
                }
                
                // 更新头像路径
                if (data.user.avatar) {
                  localStorage.setItem('userAvatar', data.user.avatar);
                  console.log('✅ [User.fetchUser] 已更新头像路径到本地存储:', data.user.avatar);
                }
                
                // 再次触发 userInfoUpdated 事件，确保 Navbar 能获取到最新数据
                console.log('📢 [User.fetchUser] 触发 userInfoUpdated 事件');
                window.dispatchEvent(new Event('userInfoUpdated'));
              }
              
              // 设置是否已关注
              this.isConcerned = data.isConcerned || false;
              
              // 获取统计数据（必须在用户信息更新后调用）
              this.fetchUserStats(userId);
            }
          }
        })
        .catch((err) => {
          console.error('❌ [User.fetchUser] 获取用户信息失败:', err);
        });
    },
    
    fetchUserStats(userId) {
      // 重置统计数据
      this.userStats = {
        following: 0,
        followers: 0,
        works: 0,
        favorites: 0
      };
      
      // 如果被查看用户不是管理员，则加载统计数据
      // 管理员账户不显示作品、收藏等数据
      if (this.user.role === 0) {
        console.log('📊 [DEBUG] 开始加载统计数据，用户ID:', userId);
        this.fetchFollowingCount(userId);
        this.fetchFollowersCount(userId);
        this.fetchWorksCount(userId);
        this.fetchFavoritesCount(userId);
      } else {
        console.log('⚠️ [DEBUG] 管理员账户，跳过统计数据加载');
      }
    },
    
    // 获取关注数统计
    fetchFollowingCount(userId) {
      const params = new URLSearchParams();
      params.append('userId', userId);
      
      request.post('/concernedList', params)
        .then((res) => {
          if (res.data && res.data.code === 0) {
            this.userStats.following = (res.data.data || []).length;
            console.log('✅ [DEBUG] 关注数:', this.userStats.following);
          }
        })
        .catch((error) => {
          console.error('❌ [DEBUG] 获取关注数失败:', error);
        });
    },
    
    // 获取粉丝数统计（使用同样的接口，因为暂时没有专门的粉丝接口）
    fetchFollowersCount(userId) {
      const params = new URLSearchParams();
      params.append('userId', userId);
      
      request.post('/concernedList', params)
        .then((res) => {
          if (res.data && res.data.code === 0) {
            this.userStats.followers = (res.data.data || []).length;
            console.log('✅ [DEBUG] 粉丝数:', this.userStats.followers);
          }
        })
        .catch((error) => {
          console.error('❌ [DEBUG] 获取粉丝数失败:', error);
        });
    },
    
    // 获取作品数统计
    fetchWorksCount(userId) {
      const params = new URLSearchParams();
      params.append('userId', userId);
      
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
              console.log('✅ [DEBUG] 作品数（自己）:', this.userStats.works);
            }
          })
          .catch((error) => {
            console.error('❌ [DEBUG] 获取作品数失败:', error);
          });
      } else {
        // 查看他人的作品数：使用 /contributionList 接口
        request.post('/contributionList', params)
          .then((res) => {
            if (res.data && res.data.code === 0) {
              this.userStats.works = (res.data.data || []).length;
              console.log('✅ [DEBUG] 作品数（他人）:', this.userStats.works);
            }
          })
          .catch((error) => {
            console.error('❌ [DEBUG] 获取作品数失败:', error);
          });
      }
    },
    
    // 获取收藏数统计
    fetchFavoritesCount(userId) {
      const params = new URLSearchParams();
      params.append('userId', userId);
      
      request.post('/favouriteList', params)
        .then((res) => {
          if (res.data && res.data.code === 0) {
            this.userStats.favorites = (res.data.data || []).length;
            console.log('✅ [DEBUG] 收藏数:', this.userStats.favorites);
          }
        })
        .catch((error) => {
          console.error('❌ [DEBUG] 获取收藏数失败:', error);
        });
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
          console.log('📥 [User] 后端返回数据:', res.data);
          
          if (res.data && res.data.code === 0) {
            // 更新本地数据
            this.user.username = this.editName;
            this.user.sex = this.editGender;
            
            // 更新 localStorage 中的用户名
            localStorage.setItem('username', this.editName);
            console.log('✅ [User] 已更新用户名到本地存储:', this.editName);
            
            // 如果上传了新头像，后端会返回新的头像路径
            if (this.editAvatar) {
              console.log('🖼️ [User] 已上传新头像，检查后端返回的头像路径...');
              
              // 后端返回数据结构可能是：
              // { code: 0, message: "success", data: { avatar: "/files/xxx/avatar/xxx.jpg" } }
              // 或者直接在 data 字段中返回头像路径
              if (res.data.data) {
                let newAvatarPath = null;
                
                // 尝试从不同的可能位置获取头像路径
                if (res.data.data.avatar) {
                  newAvatarPath = res.data.data.avatar;
                } else if (res.data.data.avatarPath) {
                  newAvatarPath = res.data.data.avatarPath;
                } else if (typeof res.data.data === 'string') {
                  // 有些后端可能直接返回字符串
                  newAvatarPath = res.data.data;
                }
                
                if (newAvatarPath) {
                  console.log('✅ [User] 后端返回新头像路径:', newAvatarPath);
                  this.user.avatar = newAvatarPath;
                  localStorage.setItem('userAvatar', newAvatarPath);
                  console.log('✅ [User] 已保存新头像路径到本地存储');
                } else {
                  console.warn('⚠️ [User] 后端未返回新头像路径，将重新获取用户信息');
                }
              }
            }
            
            // 触发自定义事件通知其他组件更新（例如 Navbar）
            console.log('📢 [User] 触发 userInfoUpdated 事件');
            window.dispatchEvent(new Event('userInfoUpdated'));
            
            // 如果上传了新头像，需要重新获取用户信息以确保头像路径正确
            if (this.editAvatar) {
              alert('更新成功！头像已更新');
              this.closeEdit();
              // 重新获取用户信息以确保所有数据同步
              console.log('🔄 [User] 重新获取用户信息...');
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
          console.error('❌ [User] 更新失败:', error);
          alert('更新失败，请稍后重试');
        });
    },
    
    openFavorites() {
      // 被查看的用户是管理员时不能查看收藏
      if (this.isCommunityAdmin || this.isSystemAdmin) {
        console.warn('⚠️ 管理员账户无法查看收藏');
        return;
      }
      
      this.view = 'favorites';
      
      // 如果数据已加载过，直接返回
      if (this.favorites.length > 0) return;
      
      const params = new URLSearchParams();
      params.append('userId', this.currentUserId);

      // 使用后端接口 POST /favouriteList，返回 Result<List<R_OverviewContribution>>
      request.post('/favouriteList', params)
        .then((res) => {
          if (res.data && res.data.code === 0) {
            this.favorites = res.data.data || [];
            // 更新统计数据
            this.userStats.favorites = this.favorites.length;
          }
        })
        .catch(() => {
          this.favorites = [];
        });
    },
    
    openLikes() {
      // 被查看的用户是管理员时不能查看点赞
      if (this.isCommunityAdmin || this.isSystemAdmin) {
        console.warn('⚠️ 管理员账户无法查看点赞');
        return;
      }
      
      this.view = 'likes';
      
      // 如果数据已加载过，直接返回
      if (this.likes.length > 0) return;
      
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
      // 被查看的用户是管理员时不能查看作品
      if (this.isCommunityAdmin || this.isSystemAdmin) {
        console.warn('⚠️ 管理员账户无法查看作品');
        return;
      }
      
      this.view = 'works';
      const p = typeof page === 'number' ? page : (page && page.detail) || 1;

      // 如果是分页请求（page > 1），或者数据已加载过，直接处理分页
      if (p > 1 || (this.worksTotal > 0 && this.userWorks.length > 0)) {
        // 处理分页逻辑（这里需要保存所有作品数据才能分页）
        // 暂时保留原逻辑，继续加载
      }

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
              
              // 更新统计数据
              this.userStats.works = allWorks.length;
              
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
              
              // 更新统计数据
              this.userStats.works = allWorks.length;
              
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
      // 被查看的用户是管理员时不能取消点赞
      if (this.isCommunityAdmin || this.isSystemAdmin) {
        console.warn('⚠️ 管理员账户无法取消点赞');
        alert('管理员账户无法取消点赞');
        return;
      }
      
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
      // 被查看的用户是管理员时不能取消收藏
      if (this.isCommunityAdmin || this.isSystemAdmin) {
        console.warn('⚠️ 管理员账户无法取消收藏');
        alert('管理员账户无法取消收藏');
        return;
      }
      
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
      // 被查看的用户是管理员时不能查看关注列表
      if (this.isCommunityAdmin || this.isSystemAdmin) {
        console.warn('⚠️ 管理员账户无法查看关注列表');
        return;
      }
      
      this.view = 'followers';
      const p = typeof page === 'number' ? page : (page && page.detail) || 1;
      
      // 如果是分页请求或数据已加载，跳过重复请求
      if (p > 1 || this.followersTotal > 0) {
        // 分页逻辑会在下面处理
        // 暂时保留，因为需要完整数据进行分页
      }
      
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
            
            // 更新统计数据
            this.userStats.following = list.length;
          }
        })
        .catch(() => {
          this.followers = [];
          this.followersPage = 1;
          this.followersTotal = 0;
        });
    },
    
    openComments() {
      // 被查看的用户是管理员时不能查看评论列表
      if (this.isCommunityAdmin || this.isSystemAdmin) {
        console.warn('⚠️ 管理员账户无法查看评论列表');
        return;
      }
      
      this.view = 'comments';
      
      // 如果已经加载过评论，不重复请求
      if (this.comments.length > 0) {
        return;
      }
      
      console.log('📋 加载用户评论列表...');
      const params = new URLSearchParams();
      params.append('userId', this.currentUserId);
      
      // 调用后端接口 POST /userCommentList，返回 Result<List<R_UserComment>>
      request.post('/userCommentList', params)
        .then((res) => {
          console.log('📋 评论列表响应:', res.data);
          if (res.data && res.data.code === 0) {
            this.comments = res.data.data || [];
            console.log('✅ 成功加载评论列表，共', this.comments.length, '条');
          } else {
            console.error('❌ 加载评论列表失败:', res.data?.message);
            this.comments = [];
          }
        })
        .catch((error) => {
          console.error('❌ 加载评论列表异常:', error);
          this.comments = [];
        });
    },
    
    handleDeleteComment(comment) {
      // 被查看的用户是管理员时不能删除评论
      if (this.isCommunityAdmin || this.isSystemAdmin) {
        console.warn('⚠️ 管理员账户无法删除评论');
        alert('管理员账户无法删除评论');
        return;
      }
      
      // 只能删除自己的评论
      if (!this.isOwnProfile) {
        alert('您只能删除自己的评论');
        return;
      }
      
      console.log('🗑️ 删除评论:', comment);
      
      const params = new URLSearchParams();
      params.append('commentId', comment.comment.commentId);
      
      // 调用后端接口 POST /user/deleteComment
      // 需要 Authorization 头
      const token = localStorage.getItem('token');
      if (!token) {
        alert('未登录，请先登录');
        this.$router.push('/login');
        return;
      }
      
      request.post('/user/deleteComment', params, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      })
        .then((res) => {
          if (res.data && res.data.code === 0) {
            alert('删除成功！');
            // 从列表中移除该评论
            this.comments = this.comments.filter(c => c.comment.commentId !== comment.comment.commentId);
          } else {
            alert('删除失败: ' + (res.data?.message || '未知错误'));
          }
        })
        .catch((error) => {
          console.error('删除评论失败:', error);
          alert('删除失败，请稍后重试');
        });
    },
    
    unfollow(f) {
      // 被查看的用户是管理员时不能取消关注
      if (this.isCommunityAdmin || this.isSystemAdmin) {
        console.warn('⚠️ 管理员账户无法取消关注');
        alert('管理员账户无法取消关注');
        return;
      }
      
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
      // 被查看的用户是管理员时不能提交作品
      if (this.isCommunityAdmin || this.isSystemAdmin) {
        console.warn('⚠️ 管理员账户无法提交作品');
        alert('管理员账户无法提交作品');
        return;
      }
      
      alert('作品提交成功！');
      // 清除编辑状态
      this.editingWork = null;
      // 刷新作品列表
      this.openWorks(1);
      // 切换回作品列表视图
      this.view = 'works';
    },
    
    handleEditWork(work) {
      // 被查看的用户是管理员时不能编辑作品
      if (this.isCommunityAdmin || this.isSystemAdmin) {
        console.warn('⚠️ 管理员账户无法编辑作品');
        alert('管理员账户无法编辑作品');
        return;
      }
      
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
      // 被查看的用户是管理员时不能删除作品
      if (this.isCommunityAdmin || this.isSystemAdmin) {
        console.warn('⚠️ 管理员账户无法删除作品');
        alert('管理员账户无法删除作品');
        return;
      }
      
      console.log('🗑️ 删除作品:', work);
      
      const params = new URLSearchParams();
      params.append('contributionId', work.contributionId);
      
      // 根据作品的审核状态选择不同的删除接口
      let deleteEndpoint = '';
      let statusText = '';
      
      switch (work.auditStatus) {
        case 0:
          // 待审核作品
          deleteEndpoint = '/user/deletePendingContribution';
          statusText = '待审核';
          break;
        case 1:
          // 已通过作品
          deleteEndpoint = '/user/deleteContribution';
          statusText = '已通过';
          break;
        case 2:
          // 已驳回作品
          deleteEndpoint = '/user/deleteDismissalContribution';
          statusText = '已驳回';
          break;
        default:
          console.error('❌ 未知的审核状态:', work.auditStatus);
          alert('删除失败：未知的作品状态');
          return;
      }
      
      console.log(`🔧 删除${statusText}作品，调用接口: ${deleteEndpoint}`);
      
      request.post(deleteEndpoint, params)
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
      // 当前登录用户是管理员时不能关注或取消关注
      if (this.isLoggedInUserCommunityAdmin || this.isLoggedInUserSystemAdmin) {
        console.warn('⚠️ 管理员账户无法进行关注操作');
        alert('管理员账户无法进行关注操作');
        return;
      }
      
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
    
    // 打开审核作品列表
    openAuditWorks() {
      this.view = 'audit';
      
      // 调用后端接口 GET /communityAdmin/auditContributions
      // 返回 Result<R_Audit_My_ContributionsDTO>
      request.get('/communityAdmin/auditContributions')
        .then((res) => {
          if (res.data && res.data.code === 0) {
            this.auditData = res.data.data;
            console.log('📋 审核数据:', this.auditData);
          } else {
            alert('加载审核数据失败: ' + (res.data?.message || '未知错误'));
          }
        })
        .catch((error) => {
          console.error('加载审核数据失败:', error);
          alert('加载审核数据失败，请稍后重试');
        });
    },
    
    // 处理封禁/解封用户
    handleToggleBlockUser({ userId, currentStatus }) {
      const isBlocked = currentStatus === 1;
      const action = isBlocked ? '解封' : '封禁';
      const confirmMsg = `确定要${action}该用户吗？`;
      
      const ok = window.confirm(confirmMsg);
      if (!ok) return;
      
      const params = new URLSearchParams();
      params.append('userId', userId);
      
      // 根据当前状态选择封禁或解封接口
      const endpoint = isBlocked ? '/communityAdmin/unblockUser' : '/communityAdmin/blockUser';
      
      // 使用 request 工具调用后端接口（不需要 /api 前缀）
      // POST /communityAdmin/blockUser 或 POST /communityAdmin/unblockUser
      request.post(endpoint, params)
        .then((res) => {
          if (res.data && res.data.code === 0) {
            alert(`${action}成功！`);
            // 更新本地用户状态
            this.user.status = isBlocked ? 0 : 1;
          } else {
            alert(`${action}失败: ` + (res.data?.message || '未知错误'));
          }
        })
        .catch((error) => {
          console.error(`${action}失败:`, error);
          alert(`${action}失败，请稍后重试`);
        });
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

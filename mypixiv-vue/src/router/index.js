import Vue from 'vue'
import Router from 'vue-router'
import Index from '@/components/Index.vue'
import Guide from '@/components/Guide.vue'
import Login from '@/components/user/Login.vue'
import Register from '@/components/user/Register.vue'
import User from '@/components/user/User.vue'
import ImageView from '@/components/ImageView.vue'
import ChangePwd from '@/components/user/ChangePwd.vue'
import AdminPortal from '@/components/admin/AdminPortal.vue'
import CommunityAdmin from '@/components/admin/CommunityAdmin.vue'
import SystemAdmin from '@/components/admin/SystemAdmin.vue'
import IllustrationPage from '@/components/IllustrationPage.vue'
import MangaPage from '@/components/MangaPage.vue'
import RankingPage from '@/components/RankingPage.vue'
import Search from '@/components/Search.vue'

Vue.use(Router)

export default new Router({
  mode: 'history',
  routes: [
    { path: '/', name: 'guide', component: Guide },
    { path: '/index', name: 'index', component: Index },
    { path: '/search', name: 'search', component: Search },
    { path: '/illustrations', name: 'illustrations', component: IllustrationPage },
    { path: '/mangas', name: 'mangas', component: MangaPage },
    { path: '/ranking', name: 'ranking', component: RankingPage },
    { path: '/user', name: 'user', component: User },
    { path: '/user/:id', name: 'user-id', component: User, props: true },
    { path: '/login', name: 'login', component: Login },
    { path: '/register', name: 'register', component: Register },
    { path: '/guide', name: 'guide-explicit', component: Guide },
    { path: '/changepwd', name: 'changepwd', component: ChangePwd },
    { path: '/image/:id', name: 'image-detail', component: ImageView, props: true },
    { path: '/admin', name: 'admin-portal', component: AdminPortal },
    { path: '/admin/community', name: 'community-admin', component: CommunityAdmin },
    { path: '/admin/system', name: 'system-admin', component: SystemAdmin }
  ]
})

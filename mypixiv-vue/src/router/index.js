import Vue from 'vue'
import Router from 'vue-router'
import Index from '@/components/Index.vue'
import Guide from '@/components/Guide.vue'
import Login from '@/components/Login.vue'
import Register from '@/components/Register.vue'
import User from '@/components/User.vue'
import ImageView from '@/components/ImageView.vue'

Vue.use(Router)

export default new Router({
  mode: 'history',
  routes: [
    { path: '/', name: 'guide', component: Guide },
    { path: '/index', name: 'index', component: Index },
    { path: '/user', name: 'user', component: User },
    { path: '/user/:id', name: 'user-id', component: User, props: true },
    { path: '/login', name: 'login', component: Login },
    { path: '/register', name: 'register', component: Register },
    { path: '/guide', name: 'guide-explicit', component: Guide },
    { path: '/image/:id', name: 'image-detail', component: ImageView, props: true   }
  ]
})

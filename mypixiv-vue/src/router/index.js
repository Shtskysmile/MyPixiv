import Vue from 'vue'
import Router from 'vue-router'
import Index from '@/components/Index.vue'
import Guide from '@/components/Guide.vue'

Vue.use(Router)

export default new Router({
  mode: 'history',
  routes: [
    { path: '/', name: 'guide', component: Guide },
    { path: '/index', name: 'index', component: Index },
    { path: '/guide', name: 'guide-explicit', component: Guide },
  ]
})

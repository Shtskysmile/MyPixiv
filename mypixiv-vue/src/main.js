import Vue from 'vue'
import App from './App.vue'
import router from './router'
import 'bulma/css/bulma.css'
import mockConfig from './config/mock.config'

// 根据配置决定是否启用Mock
if (mockConfig.enabled) {
  console.log('🎭 Mock模式已启用')
  console.log(`📡 Mock延迟: ${mockConfig.delay}ms`)
  require('./mock')
} else {
  console.log('🔌 Mock模式已关闭，使用真实后端API')
  console.log(`📡 API地址: ${mockConfig.apiBaseUrl}`)
}

Vue.config.productionTip = false

new Vue({
  router,
  render: h => h(App),
}).$mount('#app')

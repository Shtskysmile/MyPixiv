if (window.location.pathname === '/' && !window.location.search.includes('page=')) {
  window.location.replace('/?page=1');
}

import './mock'
import Vue from 'vue'
import App from './App.vue'
import 'bulma/css/bulma.css';

Vue.config.productionTip = false

new Vue({
  render: h => h(App),
}).$mount('#app')

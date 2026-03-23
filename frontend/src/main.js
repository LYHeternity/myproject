import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import './assets/style.css'
import webSocketService from './utils/websocket'

// 配置Element UI默认样式
Vue.use(ElementUI, {
  size: 'medium',
  zIndex: 3000
})

Vue.config.productionTip = false

/* 滚动渐入指令：元素进入视口 1/3 时添加 .reveal-in */
Vue.directive('reveal', {
  inserted(el) {
    el.classList.add('reveal-item')
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) entry.target.classList.add('reveal-in')
        })
      },
      { threshold: 0.2, rootMargin: '0px 0px -30px 0px' }
    )
    observer.observe(el)
  }
})

new Vue({
  router,
  store,
  render: h => h(App),
  mounted() {
    // 连接WebSocket
    webSocketService.connect()
    
    // 组件销毁时断开连接
    this.$once('hook:beforeDestroy', () => {
      webSocketService.disconnect()
    })
  }
}).$mount('#app')

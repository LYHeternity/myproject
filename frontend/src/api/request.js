import axios from 'axios'
import store from '@/store'

// 创建axios实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    return res
  },
  error => {
    console.error('Error:', error)
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // 未授权，跳转到登录页
          localStorage.removeItem('token')
          localStorage.removeItem('user')
          store.commit('user/SET_TOKEN', null)
          store.commit('user/SET_USER', null)
          window.location.href = '/login'
          break
        case 403:
          // 无权限
          alert('无权限操作')
          break
        case 404:
          // 资源不存在
          alert('请求的资源不存在')
          break
        case 500:
          // 服务器错误
          alert('服务器错误，请稍后重试')
          break
        default:
          alert('请求失败，请稍后重试')
      }
    } else {
      alert('网络错误，请检查网络连接')
    }
    return Promise.reject(error)
  }
)

export default request
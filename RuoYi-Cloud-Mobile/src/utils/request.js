import axios from 'axios'
import { showToast, showLoadingToast, closeToast } from 'vant'
import { useAuthStore } from '@/stores/auth'

// 创建 axios 实例
const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API || '/dev-api',
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const authStore = useAuthStore()
    
    // 如果存在 token 且请求头需要 token（默认需要）
    if (authStore.token && config.headers.needToken !== false) {
      config.headers['Authorization'] = `Bearer ${authStore.token}`
    }
    
    // 显示全局 loading（可配置关闭）
    if (config.showLoading !== false) {
      showLoadingToast({
        message: '加载中...',
        forbidClick: true,
        duration: 0
      })
    }
    
    return config
  },
  error => {
    closeToast()
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    closeToast()
    const res = response.data
    
    // 若依后端响应格式：{ code, msg, data }
    const code = res.code || 200
    const msg = res.msg || '未知错误'
    
    if (code === 401) {
      showToast('登录状态已过期，请重新登录')
      const authStore = useAuthStore()
      authStore.logout()
      setTimeout(() => {
        window.location.hash = '#/login'
      }, 1500)
      return Promise.reject(new Error('无效的会话'))
    } else if (code !== 200) {
      showToast(msg)
      return Promise.reject(new Error(msg))
    } else {
      return res
    }
  },
  error => {
    closeToast()
    let message = error.message
    if (message === 'Network Error') {
      message = '网络连接异常'
    } else if (message.includes('timeout')) {
      message = '请求超时'
    }
    showToast(message)
    return Promise.reject(error)
  }
)

export default service
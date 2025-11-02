// Axios请求配置
import axios from 'axios'
import mockConfig from '@/config/mock.config'

// 创建axios实例
const service = axios.create({
  // Mock模式：空baseURL（使用mock拦截）
  // 非Mock模式：
  //   - 开发环境：空baseURL（使用Vue代理）
  //   - 生产环境：完整API地址
  baseURL: mockConfig.enabled 
    ? '' 
    : (process.env.NODE_ENV === 'production' ? mockConfig.apiBaseUrl : ''),
  timeout: 15000,
  headers: {
    'Content-Type': 'application/x-www-form-urlencoded'
  }
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    
    // 如果是FormData，删除默认的Content-Type，让浏览器自动设置（包括boundary）
    if (config.data instanceof FormData) {
      delete config.headers['Content-Type']
    }
    
    // 打印请求信息（开发模式）
    if (process.env.NODE_ENV === 'development' && mockConfig.logging) {
      console.log(`📤 [${config.method.toUpperCase()}] ${config.url}`, config.data || config.params)
    }
    
    return config
  },
  error => {
    console.error('❌ 请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 打印响应信息（开发模式）
    if (process.env.NODE_ENV === 'development' && mockConfig.logging) {
      console.log(`📥 [${response.config.method.toUpperCase()}] ${response.config.url}`, response.data)
    }
    
    const res = response.data
    
    // 根据后端返回的code判断
    if (res.code === 200 || res.code === 0) {
      return response
    } else {
      console.error('❌ 业务错误:', res.message || '未知错误')
      return Promise.reject(new Error(res.message || '未知错误'))
    }
  },
  error => {
    console.error('❌ 响应错误:', error.message)
    
    // 处理不同的HTTP状态码
    if (error.response) {
      switch (error.response.status) {
        case 401:
          console.error('❌ 未授权，请重新登录')
          // 可以在这里跳转到登录页
          // router.push('/login')
          break
        case 403:
          console.error('❌ 拒绝访问')
          break
        case 404:
          console.error('❌ 请求的资源不存在')
          break
        case 500:
          console.error('❌ 服务器内部错误')
          break
        default:
          console.error(`❌ 错误代码: ${error.response.status}`)
      }
    } else if (error.request) {
      console.error('❌ 网络错误，请检查网络连接')
    }
    
    return Promise.reject(error)
  }
)

export default service


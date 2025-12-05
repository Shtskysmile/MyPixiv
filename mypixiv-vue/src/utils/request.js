// Axios请求配置
import axios from 'axios'
import mockConfig from '@/config/mock.config'
import router from '@/router'

// 创建axios实例
const service = axios.create({
  // Mock模式：空baseURL（使用mock拦截）
  // 非Mock模式：
  //   - 开发环境：使用 /api 前缀（通过Vue代理转发到后端，避免CORS和路由冲突）
  //   - 生产环境：完整API地址
  baseURL: mockConfig.enabled 
    ? '' 
    : (process.env.NODE_ENV === 'production' ? mockConfig.apiBaseUrl : '/api'),
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
    
    // 不需要token的接口列表（根据WebConfig.java的excludePathPatterns配置）
    const noAuthUrls = [
      '/login',
      '/register',
      '/mySecurityIssues',
      '/verifySecurityIssue',
      '/updatePassword',
      '/illustrations',
      '/mangas',
      '/search',
      '/allContributions'
    ]
    
    // 检查当前请求是否需要认证
    const needsAuth = !noAuthUrls.some(url => {
      // 移除 /api 前缀进行匹配
      const urlWithoutApi = config.url.replace(/^\/api/, '')
      return urlWithoutApi === url || urlWithoutApi.startsWith(url)
    })
    
    if (token && needsAuth) {
      config.headers['Authorization'] = `Bearer ${token}`
      config.__withAuth = true
    } else {
      // 标记本次请求未携带认证，便于区分401是“未登录”还是“登录过期”
      config.__withAuth = false
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

    // 根据后端返回的code判断：只有 code === 0 才是成功
    if (res.code === 0) {
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
      const status = error.response.status
      const currentPath = router.currentRoute.path
      
      switch (status) {
        case 401:
          {
            const sentWithAuth = error.config && error.config.__withAuth === true
            const hasToken = !!localStorage.getItem('token')
            if (sentWithAuth && hasToken) {
              // 携带了过期/无效的令牌 —— 登录过期
              console.error('❌ 未授权：登录已过期')
              alert('您的登录已过期，请重新登录')
              localStorage.removeItem('token')
              localStorage.removeItem('userId')
              localStorage.removeItem('userRole')
            } else {
              // 未携带认证信息 —— 未登录
              console.error('❌ 未授权：未登录')
              alert('您还未登录，请先登录')
            }
            if (currentPath !== '/login') {
              router.push({
                path: '/login',
                query: { redirect: currentPath }
              })
            }
            break
          }
        case 403:
          console.error('❌ 拒绝访问：权限不足')
          alert('您没有权限访问该资源')
          // 403通常是权限问题，也跳转到登录页
          if (currentPath !== '/login') {
            router.push('/login')
          }
          break
        case 404:
          console.error('❌ 请求的资源不存在')
          // 404不需要跳转登录页
          break
        case 500:
          console.error('❌ 服务器内部错误')
          alert('服务器内部错误，请稍后重试')
          break
        default:
          console.error(`❌ 错误代码: ${status}`)
      }
    } else if (error.request) {
      console.error('❌ 网络错误，请检查网络连接')
      alert('网络连接失败，请检查网络设置')
    }
    
    return Promise.reject(error)
  }
)

export default service


// Mock配置文件
// 用于控制是否启用Mock数据

const mockConfig = {
  // 是否启用Mock - 通过环境变量控制
  enabled: process.env.VUE_APP_USE_MOCK === 'true',
  
  // Mock延迟时间（毫秒）- 模拟网络延迟
  delay: 300,
  
  // 是否打印Mock日志
  logging: true,
  
  // API基础路径
  apiBaseUrl: process.env.VUE_APP_API_BASE_URL || 'http://frp-bus.com:20771'
}

export default mockConfig


const { defineConfig } = require('@vue/cli-service')

// 判断是否启用Mock模式
const useMock = process.env.VUE_APP_USE_MOCK === 'true'

module.exports = defineConfig({
  transpileDependencies: true,
  
  devServer: {
    port: 8080, // 前端端口
    host: 'localhost', // 强制使用 localhost，避免 WebSocket 连接到局域网 IP
    client: {
      webSocketURL: 'ws://localhost:8080/ws', // HMR WebSocket 连接到前端开发服务器
    },
    
    // 只在非Mock模式下启用代理
    proxy: useMock ? {} : {
      // 代理所有 /api 开头的请求到后端
      '/api': {
        target: process.env.VUE_APP_API_BASE_URL,
        changeOrigin: true,
        ws: false, // 禁用 WebSocket 代理
        pathRewrite: {
          '^/api': '' // 移除 /api 前缀，转发到后端时变为原始路径
        },
        onProxyReq: (proxyReq, req, res) => {
          console.log(`🔄 代理请求: ${req.method} ${req.url} -> ${process.env.VUE_APP_API_BASE_URL}${req.url.replace('/api', '')}`)
        },
        onProxyRes: (proxyRes, req, res) => {
          console.log(`✅ 代理响应: ${proxyRes.statusCode} ${req.url}`)
        },
        onError: (err, req, res) => {
          console.error('❌ 代理错误:', err.message)
          console.error('   请求:', req.url)
          console.error('   目标:', process.env.VUE_APP_API_BASE_URL)
          console.error('💡 提示: 请确保后端服务已启动')
        }
      }
    },
    
    // 在启动时显示Mock状态
    onListening: function(devServer) {
      const port = devServer.server.address().port
      console.log('\n========================================')
      if (useMock) {
        console.log('🎭 Mock模式已启用')
        console.log('📡 所有API请求将被Mock拦截')
      } else {
        console.log('🔌 Mock模式已关闭')
        console.log(`📡 API请求将代理到: ${process.env.VUE_APP_API_BASE_URL}`)
        console.log('💡 请确保后端服务已启动')
      }
      console.log(`🌐 前端服务运行在: http://localhost:${port}`)
      console.log('========================================\n')
    }
  }
})

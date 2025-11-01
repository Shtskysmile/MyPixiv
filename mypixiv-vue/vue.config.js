const { defineConfig } = require('@vue/cli-service')

// 判断是否启用Mock模式
const useMock = process.env.VUE_APP_USE_MOCK === 'true'

module.exports = defineConfig({
  transpileDependencies: true,
  
  devServer: {
    port: 8080, // 前端端口
    
    // 只在非Mock模式下启用代理
    proxy: useMock ? {} : {
      '/api': {
        // 后端接口代理配置
        target: process.env.VUE_APP_API_BASE_URL || 'http://frp-bus.com:20771',
        changeOrigin: true,
        pathRewrite: {
          // 保持 /api 前缀
          '^/api': '/api'
        },
        logLevel: 'debug',
        onProxyReq: (proxyReq, req, res) => {
          console.log(`🔄 代理请求: ${req.method} ${req.url} -> ${proxyReq.path}`)
        },
        onProxyRes: (proxyRes, req, res) => {
          console.log(`✅ 代理响应: ${proxyRes.statusCode} ${req.url}`)
        },
        onError: (err, req, res) => {
          console.error('❌ 代理错误:', err.message)
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
        console.log(`📡 API请求将代理到: ${process.env.VUE_APP_API_BASE_URL || 'http://frp-bus.com:20771'}`)
        console.log('💡 请确保后端服务已启动')
      }
      console.log(`🌐 前端服务运行在: http://localhost:${port}`)
      console.log('========================================\n')
    }
  }
})

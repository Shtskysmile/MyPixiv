const { defineConfig } = require('@vue/cli-service')

// 判断是否启用Mock模式
const useMock = process.env.VUE_APP_USE_MOCK === 'true'

module.exports = defineConfig({
  transpileDependencies: true,
  
  devServer: {
    port: 8080, // 前端端口
    
    // 只在非Mock模式下启用代理
    proxy: useMock ? {} : {
      // 代理 /api 开头的请求
      '/api': {
        target: process.env.VUE_APP_API_BASE_URL || 'http://localhost:8081',
        changeOrigin: true,
        pathRewrite: {
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
      },
      // 代理用户认证相关的请求
      '^/(login|register|mySecurityIssues|verifySecurityIssue|updatePassword)': {
        target: process.env.VUE_APP_API_BASE_URL || 'http://localhost:8081',
        changeOrigin: true,
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
      },
      // 代理其他不带 /api 前缀的后端接口
      '^/(illustrations|mangas|contribution|search|userInfo|contributionList|favouriteList|likedList|concernedList|userCommentList)': {
        target: process.env.VUE_APP_API_BASE_URL || 'http://localhost:8081',
        changeOrigin: true,
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
      },
      // 代理 /user/ 开头的 API 请求（如 updateUserInfo, concernUser 等）
      // 注意：不包括 /user/:id 这样的路由路径
      '^/user/(updateUserInfo|concernUser|unconcernUser)': {
        target: process.env.VUE_APP_API_BASE_URL || 'http://localhost:8081',
        changeOrigin: true,
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
      },
      // 代理静态文件（图片、头像等）
      '/files': {
        target: process.env.VUE_APP_API_BASE_URL || 'http://localhost:8081',
        changeOrigin: true,
        logLevel: 'debug'
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
        console.log(`📡 API请求将代理到: ${process.env.VUE_APP_API_BASE_URL || 'http://localhost:8081'}`)
        console.log('💡 请确保后端服务已启动')
      }
      console.log(`🌐 前端服务运行在: http://localhost:${port}`)
      console.log('========================================\n')
    }
  }
})

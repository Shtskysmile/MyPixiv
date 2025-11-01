const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  
  devServer: {
    port: 8080, // 前端端口
    proxy: {
      '/api': {
        // 后端接口代理配置
        // 当后端 Spring Boot 启动后，将请求代理到后端端口
        target: 'http://localhost:8081', // Spring Boot 默认端口通常是 8080 或 8081
        changeOrigin: true,
        pathRewrite: {
          // 如果后端有统一前缀，可以在这里配置
          // '^/api': '/api' // 保持 /api 前缀
        },
        // 在开发环境下，如果后端未启动，Mock 会自动拦截
        // 所以这个配置只在后端真正运行时生效
        logLevel: 'debug'
      }
    }
  }
})

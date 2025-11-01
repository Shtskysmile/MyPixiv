# 后端API配置说明

## 📡 后端地址

**内网穿透地址**: `http://frp-bus.com:20771`

## 🔧 配置位置

### 1. 开发环境代理配置
**文件**: `vue.config.js`

```javascript
proxy: {
  '/api': {
    target: 'http://frp-bus.com:20771',  // 内网穿透地址
    changeOrigin: true,
    pathRewrite: {
      '^/api': '/api'
    }
  }
}
```

### 2. Mock配置
**文件**: `src/config/mock.config.js`

```javascript
const mockConfig = {
  apiBaseUrl: 'http://frp-bus.com:20771'
}
```

## 🚀 使用方式

### 方式1: 使用环境变量（推荐）

创建 `.env.local` 文件（如果不存在）:
```bash
# .env.local
VUE_APP_API_BASE_URL=http://frp-bus.com:20771
VUE_APP_USE_MOCK=false
```

### 方式2: 直接使用默认配置

配置文件已默认设置为 `http://frp-bus.com:20771`，直接运行即可：

```bash
# 真实后端模式（已配置内网穿透地址）
npm run serve:dev

# Mock模式（不使用后端）
npm run serve:mock
```

## 📊 请求流程

### 真实后端模式
```
前端请求: /api/login
    ↓
webpack-dev-server 代理
    ↓
转发到: http://frp-bus.com:20771/api/login
    ↓
内网穿透服务器
    ↓
后端服务器
    ↓
返回响应
```

### Mock模式
```
前端请求: /api/login
    ↓
Mock.js 拦截
    ↓
返回模拟数据
```

## 🔍 验证连接

### 1. 检查代理配置
启动开发服务器时，查看控制台输出：
```
🔌 Mock模式已关闭
📡 API请求将代理到: http://frp-bus.com:20771
```

### 2. 测试API连接
在浏览器控制台的Network面板中，查看请求是否被正确代理。

### 3. 常见问题

**Q: 代理失败，请求404？**
- 检查后端服务是否正常运行
- 确认内网穿透地址是否正确
- 检查后端是否有 `/api` 前缀

**Q: CORS错误？**
- 后端需要配置CORS允许前端域名
- 或者使用代理（已配置）

**Q: 连接超时？**
- 检查网络连接
- 确认内网穿透服务是否正常
- 检查防火墙设置

## 📝 修改后端地址

### 临时修改（当前会话）
```bash
# Windows
set VUE_APP_API_BASE_URL=http://your-backend-url
npm run serve:dev

# Linux/Mac
export VUE_APP_API_BASE_URL=http://your-backend-url
npm run serve:dev
```

### 永久修改
1. 修改 `vue.config.js` 中的 `target` 默认值
2. 修改 `src/config/mock.config.js` 中的 `apiBaseUrl` 默认值
3. 或创建 `.env.local` 文件设置环境变量

## 🎯 注意事项

1. **Mock模式**: Mock模式下不会使用后端地址，所有请求被拦截
2. **真实后端模式**: 确保后端服务正常运行时使用
3. **内网穿透**: 如果后端地址变更，需要更新配置
4. **HTTPS**: 如果需要HTTPS，修改为 `https://frp-bus.com:20771`

---

**当前后端地址**: `http://frp-bus.com:20771` ✅


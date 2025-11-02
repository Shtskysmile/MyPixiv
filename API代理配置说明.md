# API 代理配置说明

## 📋 概述

为了解决前端路由与后端 API 路径冲突的问题，本项目采用了**统一 API 前缀**的方案：

- **前端**：所有后端 API 请求统一使用 `/api` 前缀
- **代理**：Vue DevServer 将 `/api/*` 请求代理到后端服务器
- **后端**：保持原有路径不变（不需要修改）

## 🔧 配置详情

### 1. Vue 代理配置 (`vue.config.js`)

```javascript
proxy: {
  '/api': {
    target: process.env.VUE_APP_API_BASE_URL,  // 后端地址：http://10.17.73.80:8080
    changeOrigin: true,
    ws: false,
    pathRewrite: {
      '^/api': ''  // 移除 /api 前缀后转发到后端
    }
  }
}
```

**工作流程：**
```
前端请求: /api/login
    ↓
代理处理: pathRewrite 移除 /api
    ↓
转发到后端: http://10.17.73.80:8080/login
```

### 2. Axios 请求工具配置 (`src/utils/request.js`)

```javascript
const service = axios.create({
  baseURL: mockConfig.enabled 
    ? '' 
    : (process.env.NODE_ENV === 'production' ? mockConfig.apiBaseUrl : '/api'),
  timeout: 15000
})
```

**环境说明：**
- **Mock 模式**：`baseURL = ''`（空，使用 Mock 拦截器）
- **开发环境**：`baseURL = '/api'`（通过代理转发）
- **生产环境**：`baseURL = 'http://10.17.73.80:8080'`（直接访问后端）

### 3. 前端 API 调用方式

#### 方式一：使用 `request.js`（推荐）

```javascript
import request from '@/utils/request'

// 自动添加 /api 前缀
request.post('/login', params)        // 实际请求：/api/login
request.post('/userInfo', params)     // 实际请求：/api/userInfo
```

#### 方式二：直接使用 axios

```javascript
import axios from 'axios'

// 需要手动添加 /api 前缀
axios.post('/api/login', params)
axios.get('/api/illustrations')
```

## 📝 已修改的文件

### 配置文件
- ✅ `mypixiv-vue/vue.config.js` - 代理配置
- ✅ `mypixiv-vue/src/utils/request.js` - Axios 实例配置

### 组件文件（已添加 `/api` 前缀）
- ✅ `src/components/user/Login.vue`
- ✅ `src/components/user/ChangePwd.vue`
- ✅ `src/components/user/SubmitArtwork.vue`
- ✅ `src/components/Index.vue`
- ✅ `src/components/IllustrationPage.vue`
- ✅ `src/components/MangaPage.vue`
- ✅ `src/components/RankingPage.vue`
- ✅ `src/components/ImageView.vue`
- ✅ `src/components/admin/SystemAdmin.vue`
- ✅ `src/components/admin/CommunityAdmin.vue`

### 使用 request.js 的组件（无需修改）
- ✅ `src/components/user/User.vue`
- ✅ `src/components/user/Register.vue`

## 🚀 启动方式

### 开发环境（连接真实后端）

```bash
run_vue_without_mock.bat
```

该脚本会：
1. 设置环境变量 `VUE_APP_USE_MOCK=false`
2. 设置后端地址 `VUE_APP_API_BASE_URL=http://10.17.73.80:8080`
3. 启动 Vue 开发服务器（带 `/api` 代理）

### Mock 模式（使用 Mock 数据）

```bash
npm run serve
```

## 🔍 路由与 API 区分

### 前端路由（Vue Router 处理）
- `/` - 首页
- `/login` - 登录页
- `/user` - 用户主页
- `/user/:id` - 用户详情页
- `/image/:id` - 图片详情页
- `/illustrations` - 插画页
- `/ranking` - 排行榜

### 后端 API（代理转发）
- `/api/login` → 后端 `/login`
- `/api/user/uploadContribution` → 后端 `/user/uploadContribution`
- `/api/illustrations` → 后端 `/illustrations`
- `/api/userInfo` → 后端 `/userInfo`

## ✨ 优势

1. **路径清晰**：前端路由和后端 API 路径完全分离，不会冲突
2. **易于维护**：所有 API 请求都有统一的 `/api` 标识
3. **符合规范**：业界标准做法，易于理解和扩展
4. **代理简单**：只需一条代理规则，无需复杂的 bypass 逻辑
5. **环境隔离**：开发/生产环境自动切换，无需手动修改

## 🐛 排查问题

### 如果 API 请求 404

1. **检查请求路径**：确保所有 API 请求都包含 `/api` 前缀
   ```bash
   # 在浏览器 Network 面板查看
   正确：/api/login
   错误：/login
   ```

2. **检查后端服务**：确保后端已启动在 `http://10.17.73.80:8080`
   ```bash
   # 测试后端连通性
   curl http://10.17.73.80:8080/login
   ```

3. **检查环境变量**：确保 `VUE_APP_USE_MOCK=false`
   ```bash
   echo %VUE_APP_USE_MOCK%  # 应该输出 false
   ```

4. **查看代理日志**：启动服务器后查看控制台日志
   ```
   🔄 代理请求: POST /api/login -> http://10.17.73.80:8080/login
   ✅ 代理响应: 200 /api/login
   ```

## 📚 参考资料

- [Vue CLI DevServer 代理配置](https://cli.vuejs.org/config/#devserver-proxy)
- [Axios 配置文档](https://axios-http.com/docs/config_defaults)
- [常见前后端分离方案](https://juejin.cn/post/6844904008051810311)


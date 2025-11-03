# API 地址配置总结

## 🎯 核心原则

**所有 API 代理地址都由环境变量 `VUE_APP_API_BASE_URL` 控制**

当前后端地址：`http://10.61.133.80:8080`

---

## 📋 已修复的问题

### 1. **package.json 硬编码地址**
❌ **修复前**：
```json
"serve:dev": "cross-env VUE_APP_USE_MOCK=false VUE_APP_API_BASE_URL=http://www.pcoi.top vue-cli-service serve"
```

✅ **修复后**：
```json
"serve:dev": "vue-cli-service serve"
```

**原因**：npm 脚本中硬编码的环境变量会覆盖 `.bat` 文件设置的变量

---

### 2. **启动脚本统一**

所有启动脚本都设置了正确的环境变量：

#### Windows 脚本
- `run_vue_without_mock.bat` ✅
- `mypixiv-vue/scripts/mock-off.bat` ✅

#### Linux/Mac 脚本
- `mypixiv-vue/scripts/mock-off.sh` ✅

**统一设置**：
```batch
set VUE_APP_API_BASE_URL=http://10.61.133.80:8080
```

---

### 3. **配置文件添加默认值**

`mypixiv-vue/src/config/mock.config.js`：
```javascript
apiBaseUrl: process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080'
```

避免环境变量未设置时出现 `undefined`

---

### 4. **Vue 代理端口调整**

`mypixiv-vue/vue.config.js`：
```javascript
devServer: {
  port: 8081,  // 前端端口（避免和后端 8080 冲突）
  client: {
    webSocketURL: 'ws://localhost:8081/ws'  // HMR WebSocket
  }
}
```

---

## 🔧 完整配置链

```
1. 启动脚本设置环境变量
   run_vue_without_mock.bat
   ├─ VUE_APP_USE_MOCK=false
   └─ VUE_APP_API_BASE_URL=http://10.61.133.80:8080

2. npm 脚本启动服务（不覆盖环境变量）
   npm run serve:dev
   └─ vue-cli-service serve

3. Mock 配置读取环境变量
   mock.config.js
   └─ apiBaseUrl: process.env.VUE_APP_API_BASE_URL

4. Vue 代理配置
   vue.config.js
   └─ proxy: { '/api': { target: apiBaseUrl } }

5. Axios 请求
   request.js
   └─ baseURL: '/api' (开发模式)

6. 最终请求流程
   axios.get('/user/123')
   → http://localhost:8081/api/user/123
   → Vue 代理转发
   → http://10.61.133.80:8080/user/123
```

---

## 🚀 使用方法

### 启动开发服务器（真实后端）

**Windows**：
```batch
run_vue_without_mock.bat
```

**Linux/Mac**：
```bash
cd mypixiv-vue
./scripts/mock-off.sh
```

### 访问前端
```
http://localhost:8081
```

### 验证配置
启动后查看控制台输出：
```
🔌 Mock模式已关闭，使用真实后端API
📡 API地址: http://10.61.133.80:8080
🌐 前端服务运行在: http://localhost:8081
```

---

## 📝 修改后端地址

**只需修改一个地方**：

编辑 `run_vue_without_mock.bat` 第 9 行：
```batch
set VUE_APP_API_BASE_URL=新的后端地址
```

同时更新其他启动脚本中的相同变量即可。

---

## ✅ 检查清单

- [x] `package.json` 中删除硬编码的 API 地址
- [x] `run_vue_without_mock.bat` 设置正确的环境变量
- [x] `mock-off.bat` 设置正确的环境变量
- [x] `mock-off.sh` 设置正确的环境变量
- [x] `mock.config.js` 添加默认值
- [x] `vue.config.js` 调整前端端口为 8081
- [x] `vue.config.js` WebSocket 端口同步为 8081

---

## 🐛 故障排查

### 如果 API 请求还是指向旧地址

1. **完全关闭开发服务器**（Ctrl+C）
2. **清除 npm 缓存**：
   ```batch
   cd mypixiv-vue
   npm cache clean --force
   ```
3. **重新启动**：
   ```batch
   cd ..
   run_vue_without_mock.bat
   ```

### 如果出现 503 错误

- 检查后端服务是否已启动
- 检查后端地址是否正确：`http://10.61.133.80:8080`
- 检查网络连接

---

**最后更新**：2025-11-03


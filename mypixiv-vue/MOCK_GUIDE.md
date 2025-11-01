# Mock模式切换指南

## 📋 概述

本项目提供了灵活的Mock模式切换机制，方便在开发过程中快速切换Mock数据和真实后端API。

## 🎯 使用场景

### Mock模式 (推荐用于)
- ✅ 前端独立开发
- ✅ 后端接口未完成
- ✅ 演示和测试UI
- ✅ 离线开发

### 真实后端模式 (推荐用于)
- ✅ 前后端联调
- ✅ 接口测试
- ✅ 集成测试
- ✅ 生产环境

---

## 🚀 快速开始

### 方法1: 使用npm脚本 (推荐)

#### 启用Mock模式
```bash
npm run serve:mock
```

#### 关闭Mock模式 (使用真实后端)
```bash
npm run serve:dev
```

#### 默认模式
```bash
npm run serve
# 默认行为取决于环境变量配置
```

---

### 方法2: 使用便捷脚本

#### Windows系统

**启用Mock模式:**
```cmd
cd mypixiv-vue
scripts\mock-on.bat
```

**关闭Mock模式:**
```cmd
cd mypixiv-vue
scripts\mock-off.bat
```

#### Linux/Mac系统

**启用Mock模式:**
```bash
cd mypixiv-vue
chmod +x scripts/mock-on.sh
./scripts/mock-on.sh
```

**关闭Mock模式:**
```bash
cd mypixiv-vue
chmod +x scripts/mock-off.sh
./scripts/mock-off.sh
```

---

### 方法3: 手动设置环境变量

#### Windows (CMD)
```cmd
# 启用Mock
set VUE_APP_USE_MOCK=true
npm run serve

# 关闭Mock
set VUE_APP_USE_MOCK=false
npm run serve
```

#### Windows (PowerShell)
```powershell
# 启用Mock
$env:VUE_APP_USE_MOCK="true"
npm run serve

# 关闭Mock
$env:VUE_APP_USE_MOCK="false"
npm run serve
```

#### Linux/Mac
```bash
# 启用Mock
export VUE_APP_USE_MOCK=true
npm run serve

# 关闭Mock
export VUE_APP_USE_MOCK=false
npm run serve
```

---

## 📁 配置文件说明

### 1. `src/config/mock.config.js`
Mock配置中心，控制Mock行为。

```javascript
const mockConfig = {
  // 是否启用Mock
  enabled: process.env.VUE_APP_USE_MOCK === 'true',
  
  // Mock延迟时间（毫秒）
  delay: 300,
  
  // 是否打印Mock日志
  logging: true,
  
  // API基础路径
  apiBaseUrl: process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080'
}
```

### 2. `vue.config.js`
开发服务器配置，根据Mock模式动态配置代理。

**Mock模式**: 不启用代理，所有请求被Mock拦截  
**真实后端模式**: 启用代理，请求转发到后端服务器

### 3. `package.json`
NPM脚本配置。

```json
{
  "scripts": {
    "serve": "vue-cli-service serve",
    "serve:mock": "cross-env VUE_APP_USE_MOCK=true vue-cli-service serve",
    "serve:dev": "cross-env VUE_APP_USE_MOCK=false vue-cli-service serve",
    "build": "vue-cli-service build",
    "build:mock": "cross-env VUE_APP_USE_MOCK=true vue-cli-service build"
  }
}
```

---

## 🔧 环境变量说明

### `VUE_APP_USE_MOCK`
- **类型**: String (`"true"` / `"false"`)
- **默认值**: 根据启动命令决定
- **作用**: 控制是否启用Mock数据

### `VUE_APP_API_BASE_URL`
- **类型**: String (URL)
- **默认值**: `http://frp-bus.com:20771` (内网穿透地址)
- **作用**: 真实后端API的基础路径

### `VUE_APP_TITLE`
- **类型**: String
- **默认值**: `MyPixiv`
- **作用**: 应用标题

---

## 📊 工作原理

### Mock模式流程
```
前端发起请求
    ↓
axios拦截器
    ↓
检查mockConfig.enabled = true
    ↓
Mock.js拦截请求
    ↓
返回模拟数据
    ↓
前端接收响应
```

### 真实后端模式流程
```
前端发起请求
    ↓
axios拦截器
    ↓
检查mockConfig.enabled = false
    ↓
webpack-dev-server代理
    ↓
转发到后端服务器 (http://localhost:8081)
    ↓
后端处理请求
    ↓
返回真实数据
    ↓
前端接收响应
```

---

## 🎨 控制台输出

### Mock模式启动时
```
========================================
🎭 Mock模式已启用
📡 所有API请求将被Mock拦截
🌐 前端服务运行在: http://localhost:8080
========================================

✅ Mock.js 已加载 - 对齐后端 SpringBoot 接口
```

### 真实后端模式启动时
```
========================================
🔌 Mock模式已关闭
📡 API请求将代理到: http://localhost:8081
💡 请确保后端服务已启动
🌐 前端服务运行在: http://localhost:8080
========================================
```

### 请求日志 (开发模式)
```
📤 [POST] /api/login {username: "admin", password: "123456"}
📥 [POST] /api/login {code: 200, message: "登录成功", data: {...}}
```

---

## 🔍 调试技巧

### 1. 检查当前模式
打开浏览器控制台，查看启动日志：
- 看到 `🎭 Mock模式已启用` → Mock模式
- 看到 `🔌 Mock模式已关闭` → 真实后端模式

### 2. 查看请求日志
在控制台中可以看到：
- `📤` 发送的请求
- `📥` 接收的响应
- `🔄` 代理转发的请求
- `❌` 错误信息

### 3. 网络面板
- **Mock模式**: 请求状态为 `200 OK`，Type显示为 `xhr`
- **真实后端模式**: 可以看到实际的网络请求和响应

---

## ⚙️ 高级配置

### 修改Mock延迟时间
编辑 `src/config/mock.config.js`:
```javascript
const mockConfig = {
  delay: 500  // 修改为500ms延迟
}
```

### 修改后端API地址
编辑 `vue.config.js`:
```javascript
proxy: {
  '/api': {
    target: 'http://frp-bus.com:20771',  // 当前内网穿透地址
    // ...
  }
}
```

或设置环境变量:
```bash
export VUE_APP_API_BASE_URL=http://frp-bus.com:20771
```

> **注意**: 当前默认后端地址为内网穿透地址 `http://frp-bus.com:20771`，如需修改请参考 `BACKEND_CONFIG.md`

### 关闭请求日志
编辑 `src/config/mock.config.js`:
```javascript
const mockConfig = {
  logging: false  // 关闭日志
}
```

---

## 🚨 常见问题

### Q1: 切换模式后没有生效？
**A**: 需要重启开发服务器。按 `Ctrl+C` 停止，然后重新运行启动命令。

### Q2: 真实后端模式下报错 "代理错误"？
**A**: 请确保：
1. 后端服务已启动
2. 内网穿透服务正常（当前地址: `http://frp-bus.com:20771`）
3. 检查 `vue.config.js` 中的 `target` 配置

### Q3: Mock模式下看不到数据？
**A**: 检查：
1. 控制台是否有 `✅ Mock.js 已加载` 提示
2. 浏览器控制台是否有错误信息
3. 检查 `src/mock/index.js` 中的mock配置

### Q4: 如何知道当前是哪种模式？
**A**: 查看：
1. 启动时的控制台输出
2. 浏览器控制台的日志
3. Network面板中的请求响应

### Q5: 生产环境会使用Mock吗？
**A**: 不会。生产构建时Mock代码不会被包含，所有请求都会发送到真实API。

---

## 📦 依赖说明

### 必需依赖
- `mockjs`: Mock数据生成
- `axios`: HTTP请求库
- `vue-cli-service`: Vue CLI服务

### 可选依赖
- `cross-env`: 跨平台环境变量设置（推荐安装）

### 安装cross-env (可选但推荐)
```bash
cd mypixiv-vue
npm install --save-dev cross-env
```

如果不安装 `cross-env`，Windows用户需要使用批处理脚本，Linux/Mac用户可以直接使用npm脚本。

---

## 🎯 最佳实践

### 1. 开发阶段
- 前端独立开发时使用 **Mock模式**
- 定期切换到 **真实后端模式** 验证接口对接

### 2. 联调阶段
- 使用 **真实后端模式**
- 遇到后端问题时临时切换到Mock模式继续开发

### 3. 测试阶段
- 主要使用 **真实后端模式**
- 使用Mock模式测试边界情况

### 4. 部署阶段
- 确保使用 **真实后端模式** 构建
- 检查环境变量配置正确

---

## 📝 文件清单

### 配置文件
- ✅ `src/config/mock.config.js` - Mock配置中心
- ✅ `src/main.js` - 条件加载Mock
- ✅ `vue.config.js` - 开发服务器配置
- ✅ `package.json` - NPM脚本

### 脚本文件
- ✅ `scripts/mock-on.bat` - Windows启用Mock
- ✅ `scripts/mock-off.bat` - Windows关闭Mock
- ✅ `scripts/mock-on.sh` - Linux/Mac启用Mock
- ✅ `scripts/mock-off.sh` - Linux/Mac关闭Mock

### Mock数据
- ✅ `src/mock/index.js` - Mock数据定义

### 工具文件
- ✅ `src/utils/request.js` - Axios封装（可选）

---

## 🎉 总结

通过本指南，你可以：
- ✅ 快速切换Mock和真实后端模式
- ✅ 理解Mock机制的工作原理
- ✅ 根据需求灵活配置
- ✅ 高效进行前后端联调

**推荐使用方式**: `npm run serve:mock` 和 `npm run serve:dev`

祝开发顺利！🚀


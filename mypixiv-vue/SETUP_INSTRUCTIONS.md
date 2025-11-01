# 项目安装和启动说明

## 📦 安装依赖

### 1. 安装Node.js依赖
```bash
cd mypixiv-vue
npm install
```

### 2. 安装cross-env (推荐)
```bash
npm install --save-dev cross-env
```

> **注意**: `cross-env` 用于跨平台设置环境变量。如果不安装，Windows用户需要使用批处理脚本。

---

## 🚀 启动项目

### 方式1: 使用NPM脚本 (推荐)

#### Mock模式 (前端独立开发)
```bash
npm run serve:mock
```

#### 真实后端模式 (前后端联调)
```bash
npm run serve:dev
```

#### 默认模式
```bash
npm run serve
```

---

### 方式2: 使用便捷脚本

#### Windows
```cmd
# Mock模式
scripts\mock-on.bat

# 真实后端模式
scripts\mock-off.bat
```

#### Linux/Mac
```bash
# 添加执行权限（首次使用）
chmod +x scripts/*.sh

# Mock模式
./scripts/mock-on.sh

# 真实后端模式
./scripts/mock-off.sh
```

---

## 🔧 配置后端地址

### 默认配置
- 前端端口: `8080`
- 后端端口: `8081`
- 后端地址: `http://localhost:8081`

### 修改后端地址

编辑 `vue.config.js`:
```javascript
proxy: {
  '/api': {
    target: 'http://your-backend-url:port',  // 修改这里
    // ...
  }
}
```

---

## 📊 验证安装

### 1. 启动成功标志

**Mock模式:**
```
========================================
🎭 Mock模式已启用
📡 所有API请求将被Mock拦截
🌐 前端服务运行在: http://localhost:8080
========================================
```

**真实后端模式:**
```
========================================
🔌 Mock模式已关闭
📡 API请求将代理到: http://localhost:8081
💡 请确保后端服务已启动
🌐 前端服务运行在: http://localhost:8080
========================================
```

### 2. 访问应用
打开浏览器访问: `http://localhost:8080`

### 3. 检查控制台
浏览器F12打开控制台，应该看到：
- Mock模式: `✅ Mock.js 已加载`
- 真实后端模式: `🔌 Mock模式已关闭`

---

## 🚨 常见问题

### Q1: npm install 失败
**解决方案:**
```bash
# 清理缓存
npm cache clean --force

# 删除node_modules
rm -rf node_modules

# 重新安装
npm install
```

### Q2: 端口8080被占用
**解决方案:**
修改 `vue.config.js`:
```javascript
devServer: {
  port: 8888,  // 改为其他端口
  // ...
}
```

### Q3: cross-env不是内部命令
**解决方案:**
```bash
# 全局安装
npm install -g cross-env

# 或使用批处理脚本
scripts\mock-on.bat  # Windows
./scripts/mock-on.sh  # Linux/Mac
```

### Q4: 真实后端模式连接失败
**检查清单:**
- [ ] 后端服务是否已启动
- [ ] 后端端口是否正确（默认8081）
- [ ] 防火墙是否阻止连接
- [ ] vue.config.js中的target配置是否正确

---

## 📝 项目结构

```
mypixiv-vue/
├── src/
│   ├── assets/          # 静态资源
│   ├── components/      # Vue组件
│   ├── config/          # 配置文件
│   │   └── mock.config.js  # Mock配置
│   ├── mock/            # Mock数据
│   │   └── index.js     # Mock定义
│   ├── router/          # 路由配置
│   ├── utils/           # 工具函数
│   ├── App.vue          # 根组件
│   └── main.js          # 入口文件
├── scripts/             # 便捷脚本
│   ├── mock-on.bat      # Windows启用Mock
│   ├── mock-off.bat     # Windows关闭Mock
│   ├── mock-on.sh       # Linux/Mac启用Mock
│   └── mock-off.sh      # Linux/Mac关闭Mock
├── vue.config.js        # Vue CLI配置
├── package.json         # 项目依赖
├── MOCK_GUIDE.md        # Mock详细文档
└── MOCK_QUICK_REFERENCE.md  # Mock快速参考
```

---

## 🎯 下一步

1. ✅ 安装依赖
2. ✅ 选择模式启动
3. ✅ 访问 http://localhost:8080
4. ✅ 开始开发！

---

## 📚 相关文档

- [Mock模式详细指南](./MOCK_GUIDE.md)
- [Mock快速参考](./MOCK_QUICK_REFERENCE.md)

---

**祝开发顺利！** 🚀


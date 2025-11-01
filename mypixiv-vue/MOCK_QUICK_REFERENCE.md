# Mock模式快速参考

## 🚀 快速命令

### 启用Mock模式
```bash
npm run serve:mock
```

### 关闭Mock模式
```bash
npm run serve:dev
```

---

## 📊 模式对比

| 特性 | Mock模式 | 真实后端模式 |
|------|---------|------------|
| 命令 | `npm run serve:mock` | `npm run serve:dev` |
| 数据来源 | Mock.js模拟数据 | 后端API真实数据 |
| 后端依赖 | ❌ 不需要 | ✅ 需要启动后端 |
| 网络请求 | 被Mock拦截 | 通过代理转发 |
| 开发速度 | ⚡ 快速 | 🐢 依赖后端 |
| 数据真实性 | 📝 模拟数据 | ✅ 真实数据 |
| 适用场景 | 前端独立开发 | 前后端联调 |

---

## 🎯 使用场景

### 使用Mock模式
- ✅ 后端接口未完成
- ✅ 前端独立开发
- ✅ UI演示
- ✅ 离线开发

### 使用真实后端模式
- ✅ 前后端联调
- ✅ 接口测试
- ✅ 数据验证
- ✅ 集成测试

---

## 🔍 如何判断当前模式

### 控制台输出
```
Mock模式:
🎭 Mock模式已启用
📡 所有API请求将被Mock拦截

真实后端模式:
🔌 Mock模式已关闭
📡 API请求将代理到: http://localhost:8081
```

### 浏览器控制台
```
Mock模式:
✅ Mock.js 已加载

真实后端模式:
🔌 Mock模式已关闭，使用真实后端API
```

---

## ⚙️ 配置文件

| 文件 | 作用 |
|------|------|
| `src/config/mock.config.js` | Mock配置中心 |
| `vue.config.js` | 代理配置 |
| `package.json` | NPM脚本 |
| `src/main.js` | 条件加载Mock |

---

## 🚨 常见问题

**Q: 切换模式后没生效？**  
A: 重启开发服务器 (Ctrl+C 后重新运行)

**Q: 真实后端模式报错？**  
A: 确保后端服务已启动在 8081 端口

**Q: 如何修改后端地址？**  
A: 修改 `vue.config.js` 中的 `target` 配置

---

## 📞 快速支持

详细文档: `MOCK_GUIDE.md`  
Mock数据: `src/mock/index.js`  
配置中心: `src/config/mock.config.js`

---

**记住**: 开发用Mock，联调用真实！🎯


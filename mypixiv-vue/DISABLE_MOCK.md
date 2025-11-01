# 如何确保Mock模式已关闭

## 🚀 快速方法

### 方法1: 使用更新后的启动脚本（推荐）
```cmd
# 在项目根目录运行
run_vue.bat
```

这个脚本会自动：
- ✅ 设置 `VUE_APP_USE_MOCK=false`
- ✅ 使用 `npm run serve:dev` 启动
- ✅ 明确关闭Mock模式

### 方法2: 使用npm脚本
```bash
cd mypixiv-vue
npm run serve:dev
```

### 方法3: 使用便捷脚本
```cmd
# Windows
cd mypixiv-vue
scripts\mock-off.bat
```

---

## 🔍 验证Mock是否已关闭

### 1. 查看控制台输出
启动后，应该在控制台看到：

```
========================================
🔌 Mock模式已关闭
📡 API请求将代理到: http://frp-bus.com:20771
💡 请确保后端服务已启动
🌐 前端服务运行在: http://localhost:8080
========================================
```

**如果看到以下输出，说明Mock仍在启用：**
```
🎭 Mock模式已启用
📡 所有API请求将被Mock拦截
```

### 2. 查看浏览器控制台
打开浏览器控制台（F12），应该看到：
```
🔌 Mock模式已关闭，使用真实后端API
📡 API地址: http://frp-bus.com:20771
```

**不应该看到：**
```
✅ Mock.js 已加载 - 对齐后端 SpringBoot 接口
```

### 3. 检查网络请求
在浏览器Network面板中：
- **Mock模式**: 请求会被拦截，Response显示Mock数据
- **真实模式**: 可以看到实际的HTTP请求发送到后端服务器

---

## 🛠️ 手动关闭Mock

### Windows (CMD)
```cmd
set VUE_APP_USE_MOCK=false
cd mypixiv-vue
npm run serve
```

### Windows (PowerShell)
```powershell
$env:VUE_APP_USE_MOCK="false"
cd mypixiv-vue
npm run serve
```

### Linux/Mac
```bash
export VUE_APP_USE_MOCK=false
cd mypixiv-vue
npm run serve
```

---

## 🚨 常见问题

### Q1: 为什么还是被Mock拦截？
**可能的原因：**
1. 环境变量设置了 `VUE_APP_USE_MOCK=true`
2. 使用了 `npm run serve:mock` 命令
3. 有 `.env.local` 文件设置了Mock模式
4. 需要重启开发服务器

**解决方法：**
```cmd
# 1. 确保环境变量正确
set VUE_APP_USE_MOCK=false

# 2. 使用正确的启动命令
npm run serve:dev

# 3. 或者使用脚本
run_vue.bat
```

### Q2: 如何彻底清除Mock？
**步骤：**
1. 停止当前开发服务器（Ctrl+C）
2. 清除环境变量
3. 重新启动

```cmd
# Windows
set VUE_APP_USE_MOCK=
npm run serve:dev

# 或直接使用
run_vue.bat
```

### Q3: 如何临时测试后端连接？
1. 确保Mock已关闭（使用 `npm run serve:dev`）
2. 检查后端服务是否运行
3. 查看Network面板确认请求发送到 `http://frp-bus.com:20771`

---

## 📋 检查清单

在启动前确认：
- [ ] 使用 `npm run serve:dev` 或 `run_vue.bat`
- [ ] 没有设置 `VUE_APP_USE_MOCK=true`
- [ ] 控制台显示 "🔌 Mock模式已关闭"
- [ ] 浏览器控制台显示 "🔌 Mock模式已关闭"
- [ ] Network面板显示实际HTTP请求

---

## 🎯 当前配置状态

**后端地址**: `http://frp-bus.com:20771` ✅  
**Mock模式**: 关闭 ✅ (使用 `run_vue.bat` 或 `npm run serve:dev`)

---

**提示**: 如果遇到问题，确保完全停止开发服务器后重新启动！


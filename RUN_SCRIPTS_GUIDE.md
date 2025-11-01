# 启动脚本使用指南

## 📋 脚本说明

项目提供了三个启动脚本，方便在不同模式下启动开发服务器。

### 1. `run_vue.bat` - 默认脚本（使用真实后端）
- **模式**: 关闭Mock，使用真实后端API
- **后端地址**: `http://frp-bus.com:20771`
- **适用场景**: 前后端联调、测试真实接口

### 2. `run_vue_without_mock.bat` - 无Mock模式
- **模式**: 关闭Mock，使用真实后端API
- **功能**: 与 `run_vue.bat` 相同，提供更明确的命名

### 3. `run_vue_with_mock.bat` - Mock模式
- **模式**: 启用Mock，使用模拟数据
- **适用场景**: 前端独立开发、后端未完成时

---

## 🚀 使用方法

### Windows系统

#### 使用真实后端（推荐用于联调）
```cmd
# 方式1: 使用默认脚本
run_vue.bat

# 方式2: 使用明确命名的脚本
run_vue_without_mock.bat
```

#### 使用Mock数据（推荐用于前端开发）
```cmd
run_vue_with_mock.bat
```

---

## 🔧 脚本特性

### 1. 自动编码修复
- 使用 `chcp 65001` 切换到UTF-8编码
- 避免中文乱码问题
- 所有输出使用英文，确保兼容性

### 2. 自动依赖安装
- 检测 `cross-env` 是否存在
- 如果不存在，自动安装
- 安装后自动重试启动

### 3. 清晰的输出信息
- 显示当前模式（Mock/Real Backend）
- 显示后端地址
- 显示错误信息和解决方案

---

## 📊 脚本对比

| 脚本 | Mock模式 | 后端地址 | 适用场景 |
|------|---------|---------|---------|
| `run_vue.bat` | ❌ 关闭 | `http://frp-bus.com:20771` | 前后端联调 |
| `run_vue_without_mock.bat` | ❌ 关闭 | `http://frp-bus.com:20771` | 前后端联调 |
| `run_vue_with_mock.bat` | ✅ 启用 | - | 前端独立开发 |

---

## 🔍 验证启动状态

### Mock模式启动成功
控制台应该显示：
```
========================================
  Starting Vue Dev Server (Mock Mode)
========================================

Mock mode: ENABLED
Starting development server with Mock data...
All API requests will be intercepted by Mock.js
```

### 真实后端模式启动成功
控制台应该显示：
```
========================================
  Starting Vue Dev Server (Real Backend)
========================================

Mock mode: DISABLED
Starting development server with Real Backend API...
Backend URL: http://frp-bus.com:20771
```

然后应该看到：
```
========================================
🔌 Mock模式已关闭
📡 API请求将代理到: http://frp-bus.com:20771
🌐 前端服务运行在: http://localhost:8080
========================================
```

---

## 🚨 常见问题

### Q1: 提示 "cross-env 不是内部或外部命令"
**原因**: 缺少 `cross-env` 依赖

**解决方案**: 
脚本会自动安装，如果仍然失败，手动安装：
```cmd
cd mypixiv-vue
npm install --save-dev cross-env
```

### Q2: 中文乱码问题
**原因**: Windows CMD默认编码不是UTF-8

**解决方案**: 
脚本已自动处理，使用 `chcp 65001` 切换到UTF-8

### Q3: 启动失败
**检查清单**:
- [ ] Node.js 是否已安装
- [ ] npm 是否可用
- [ ] 是否在项目根目录
- [ ] 依赖是否已安装（`npm install`）

---

## 📝 脚本内容说明

### `run_vue.bat` / `run_vue_without_mock.bat`
```batch
@echo off
chcp 65001 >nul                    # 切换到UTF-8编码
set VUE_APP_USE_MOCK=false         # 关闭Mock
call npm run serve:dev             # 启动开发服务器
if errorlevel 1 (                  # 如果失败
    call npm install --save-dev cross-env  # 安装cross-env
    call npm run serve:dev          # 重试
)
```

### `run_vue_with_mock.bat`
```batch
@echo off
chcp 65001 >nul                    # 切换到UTF-8编码
set VUE_APP_USE_MOCK=true          # 启用Mock
call npm run serve:mock             # 启动开发服务器（Mock模式）
if errorlevel 1 (                  # 如果失败
    call npm install --save-dev cross-env  # 安装cross-env
    call npm run serve:mock         # 重试
)
```

---

## 🎯 最佳实践

### 开发阶段
- **前端开发**: 使用 `run_vue_with_mock.bat`
- **前后端联调**: 使用 `run_vue_without_mock.bat`

### 测试阶段
- **功能测试**: 使用 `run_vue_without_mock.bat`（真实后端）
- **UI测试**: 使用 `run_vue_with_mock.bat`（稳定数据）

---

## 🔄 快速切换

### 从Mock切换到真实后端
1. 停止当前服务器（Ctrl+C）
2. 运行 `run_vue_without_mock.bat`

### 从真实后端切换到Mock
1. 停止当前服务器（Ctrl+C）
2. 运行 `run_vue_with_mock.bat`

---

## 📚 相关文档

- **`BACKEND_CONFIG.md`** - 后端配置说明
- **`MOCK_GUIDE.md`** - Mock模式详细指南
- **`DISABLE_MOCK.md`** - 如何关闭Mock

---

**提示**: 如果遇到问题，确保完全停止开发服务器后重新运行脚本！


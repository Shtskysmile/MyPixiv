# CORS 问题完全解决方案

## 问题分析

### 症状
- ✅ 登录成功（使用 `application/x-www-form-urlencoded`）
- ❌ 注册失败（使用 `multipart/form-data`）
- ❌ 错误信息：`405 Method Not Allowed` + `No 'Access-Control-Allow-Origin' header`

### 根本原因
1. **登录请求**是简单请求（simple request），不触发 CORS 预检
2. **注册请求**使用 `FormData` + `multipart/form-data`，触发 CORS 预检（OPTIONS 请求）
3. **远程服务器** `www.pcoi.top` 没有正确处理 CORS，拒绝了 OPTIONS 预检请求

---

## 已完成的修改

### 1. 后端：添加 CORS 配置
**文件**：`mypixiv-springboot/src/main/java/org/example/PCOI/Config/WebConfig.java`

添加了 `addCorsMappings` 方法：
```java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins("http://localhost:8080", "http://127.0.0.1:8080")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
}
```

**注意**：这个修改需要重新部署到 `www.pcoi.top` 服务器才能生效！

### 2. 前端：使用代理绕过 CORS（临时方案）
**文件**：`mypixiv-vue/src/utils/request.js`

修改了 `baseURL` 配置：
```javascript
baseURL: mockConfig.enabled 
  ? '' 
  : (process.env.NODE_ENV === 'production' ? mockConfig.apiBaseUrl : ''),
```

**作用**：
- 开发环境：使用空 baseURL → 请求变成相对路径 → 走 Vue 代理
- 生产环境：使用完整 API 地址

---

## 测试步骤

### ⚠️ 重要：需要重启 Vue 开发服务器

1. **停止当前的 Vue 服务器**
   - 在运行 Vue 的终端按 `Ctrl + C`

2. **重新启动 Vue 服务器**
   ```bash
   npm run serve
   ```
   或者使用批处理文件：
   ```bash
   run_vue_without_mock.bat
   ```

3. **刷新浏览器**
   - 按 `F5` 或 `Ctrl + F5` 强制刷新

4. **测试注册功能**
   - 打开 `http://localhost:8080/register`
   - 填写注册信息
   - 提交

### 预期结果
- ✅ 请求应该通过 Vue 代理转发到 `www.pcoi.top`
- ✅ 浏览器控制台显示：`🔄 代理请求: POST /register`
- ✅ 注册成功

---

## 工作原理

### 修改前（直接请求，触发 CORS）
```
浏览器 (localhost:8080)
    ↓ POST /register + FormData
    ↓ (触发 CORS 预检)
    ↓ OPTIONS /register
    ↓
远程服务器 (www.pcoi.top)
    ↓ 405 Method Not Allowed
    ✗ 没有 CORS 配置
```

### 修改后（通过代理，绕过 CORS）
```
浏览器 (localhost:8080)
    ↓ POST /register + FormData
    ↓ (相对路径，不触发 CORS)
    ↓
Vue 开发服务器 (localhost:8080)
    ↓ (作为代理)
    ↓ POST /register
    ↓
远程服务器 (www.pcoi.top)
    ↓ 200 OK
    ✓ 成功
```

---

## 长期解决方案

### 将后端代码部署到 `www.pcoi.top`

1. **重新编译后端**
   ```bash
   cd mypixiv-springboot
   mvn clean package
   ```

2. **上传 JAR 到服务器**
   ```bash
   scp target/Laboratory-1.0-SNAPSHOT.jar user@www.pcoi.top:/path/to/app/
   ```

3. **重启后端服务**
   ```bash
   # SSH 到服务器
   ssh user@www.pcoi.top
   
   # 停止旧服务
   kill $(cat app.pid)
   
   # 启动新服务
   nohup java -jar Laboratory-1.0-SNAPSHOT.jar > app.log 2>&1 & echo $! > app.pid
   ```

4. **验证 CORS 配置**
   ```bash
   curl -X OPTIONS http://www.pcoi.top/register \
     -H "Origin: http://localhost:8080" \
     -H "Access-Control-Request-Method: POST" \
     -v
   ```
   
   应该看到响应头：
   ```
   Access-Control-Allow-Origin: http://localhost:8080
   Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
   ```

---

## 常见问题

### Q1: 为什么登录可以，注册不行？
A: 登录使用 `application/x-www-form-urlencoded`（简单请求），注册使用 `multipart/form-data`（复杂请求需要预检）。

### Q2: 为什么要重启 Vue 服务器？
A: `baseURL` 在服务启动时确定，需要重启才能应用新的配置。

### Q3: 生产环境怎么办？
A: 生产环境必须在服务器上配置 CORS，或者使用 Nginx 反向代理。

### Q4: 代理和生产环境的区别？
- **开发**：`baseURL = ''` → 请求 `/register` → Vue 代理转发
- **生产**：`baseURL = 'http://www.pcoi.top'` → 请求 `http://www.pcoi.top/register` → 直连

---

## 验证检查清单

- [ ] 已修改 `WebConfig.java` 添加 CORS 配置
- [ ] 已修改 `request.js` 使用空 baseURL（开发环境）
- [ ] 已重启 Vue 开发服务器
- [ ] 浏览器控制台显示代理日志
- [ ] 注册功能测试成功
- [ ] 登录功能仍然正常

---

## 相关文件

1. `mypixiv-springboot/src/main/java/org/example/PCOI/Config/WebConfig.java` - CORS 配置
2. `mypixiv-vue/src/utils/request.js` - Axios baseURL 配置
3. `mypixiv-vue/vue.config.js` - Vue 代理配置

完成时间：2025-11-01


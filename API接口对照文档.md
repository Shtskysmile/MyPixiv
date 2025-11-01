# MyPixiv 前后端接口对照文档

## 后端地址配置

**后端 API 基础地址**: `http://www.pcoi.top`

## 接口对照表

### 1. 用户认证模块 (UserController)

| 功能         | 前端调用                  | 后端接口                  | 请求方式 | 需要Token | 状态        |
| ------------ | ------------------------- | ------------------------- | -------- | --------- | ----------- |
| 用户注册     | `/api/register`         | `/register`             | POST     | ❌        | ⚠️ 不匹配 |
| 用户登录     | `/login`                | `/login`                | POST     | ❌        | ✅ 匹配     |
| 获取用户信息 | `/userInfo`             | `/userInfo`             | POST     | ✅        | ✅ 匹配     |
| 更新用户信息 | `/user/updateUserInfo`  | `/user/updateUserInfo`  | POST     | ✅        | ✅ 匹配     |
| 获取密保问题 | `/mySecurityIssues`     | `/mySecurityIssues`     | POST     | ❌        | ✅ 匹配     |
| 验证密保问题 | `/verifySecurityIssues` | `/verifySecurityIssues` | POST     | ❌        | ✅ 匹配     |
| 修改密码     | `/updatePassword`       | `/updatePassword`       | POST     | ✅(临时)  | ✅ 匹配     |

### 2. 作品相关模块 (ContributionController)

| 功能           | 前端调用                      | 后端接口                     | 请求方式 | 需要Token | 状态          |
| -------------- | ----------------------------- | ---------------------------- | -------- | --------- | ------------- |
| 获取插画列表   | `/api/illustrations`        | `/illustrations`           | GET      | ❌        | ⚠️ 不匹配   |
| 获取漫画列表   | `/api/mangas`               | `/mangas`                  | GET      | ❌        | ⚠️ 不匹配   |
| 获取作品详情   | `/api/contribution`         | `/contribution`            | POST     | ✅        | ⚠️ 不匹配   |
| 获取待审核作品 | -                             | `/pendingContribution`     | POST     | ✅        | ❌ 前端未使用 |
| 获取作品排行   | `/api/contributionsRanking` | `/contributionsRanking`    | POST     | ❌        | ⚠️ 不匹配   |
| 上传作品       | `/user/uploadContribution`  | `/user/uploadContribution` | POST     | ✅        | ✅ 匹配       |

### 3. 用户作品管理模块 (UserController - Contribution)

| 功能             | 前端调用              | 后端接口                     | 请求方式 | 需要Token | 状态          |
| ---------------- | --------------------- | ---------------------------- | -------- | --------- | ------------- |
| 获取用户作品列表 | `/contributionList` | `/contributionList`        | POST     | ❌        | ✅ 匹配       |
| 获取我的作品     | -                     | `/user/myContributions`    | POST     | ✅        | ❌ 前端未使用 |
| 删除作品         | -                     | `/user/deleteContribution` | POST     | ✅        | ❌ 前端未使用 |

### 4. 互动功能模块 (ContributionController - Interaction)

| 功能     | 前端调用                         | 后端接口                         | 请求方式 | 需要Token | 状态          |
| -------- | -------------------------------- | -------------------------------- | -------- | --------- | ------------- |
| 点赞作品 | -                                | `/user/likeContribution`       | POST     | ✅        | ❌ 前端未使用 |
| 取消点赞 | `/user/unlikeContribution`     | `/user/unlikeContribution`     | POST     | ✅        | ✅ 匹配       |
| 收藏作品 | -                                | `/user/favoriteContribution`   | POST     | ✅        | ❌ 前端未使用 |
| 取消收藏 | `/user/unfavoriteContribution` | `/user/unfavoriteContribution` | POST     | ✅        | ✅ 匹配       |
| 评论作品 | -                                | `/user/commentContribution`    | POST     | ✅        | ❌ 前端未使用 |
| 删除评论 | -                                | `/user/deleteComment`          | POST     | ✅        | ❌ 前端未使用 |

### 5. 用户列表模块 (UserController - Lists)

| 功能         | 前端调用                | 后端接口                | 请求方式 | 需要Token | 状态          |
| ------------ | ----------------------- | ----------------------- | -------- | --------- | ------------- |
| 获取关注列表 | `/concernedList`      | `/concernedList`      | POST     | ❌        | ✅ 匹配       |
| 获取点赞列表 | `/likedList`          | `/likedList`          | POST     | ❌        | ✅ 匹配       |
| 获取收藏列表 | `/favouriteList`      | `/favouriteList`      | POST     | ❌        | ✅ 匹配       |
| 获取评论列表 | -                       | `/userCommentList`    | POST     | ❌        | ❌ 前端未使用 |
| 关注用户     | -                       | `/user/concernUser`   | POST     | ✅        | ❌ 前端未使用 |
| 取消关注     | `/user/unconcernUser` | `/user/unconcernUser` | POST     | ✅        | ✅ 匹配       |

### 6. 搜索模块 (SearchController)

| 功能       | 前端调用 | 后端接口    | 请求方式 | 需要Token | 状态          |
| ---------- | -------- | ----------- | -------- | --------- | ------------- |
| 关键词搜索 | -        | `/search` | POST     | ❌        | ❌ 前端未使用 |
| 图片搜索   | -        | `/image`  | POST     | ❌        | ❌ 前端未使用 |

### 7. 社区管理员模块 (CommunityAdminController)

| 功能             | 前端调用                                     | 后端接口                                 | 请求方式 | 需要Token | 状态          |
| ---------------- | -------------------------------------------- | ---------------------------------------- | -------- | --------- | ------------- |
| 封禁用户         | -                                            | `/communityAdmin/blockUser`            | POST     | ❌        | ❌ 前端未使用 |
| 解封用户         | `/api/communityAdmin/unblockUser`          | `/communityAdmin/unblockUser`          | POST     | ❌        | ⚠️ 不匹配   |
| 封禁作品         | -                                            | `/communityAdmin/blockContribution`    | POST     | ❌        | ❌ 前端未使用 |
| 解封作品         | `/api/communityAdmin/unblockContribution`  | `/communityAdmin/unblockContribution`  | POST     | ❌        | ⚠️ 不匹配   |
| 获取待审核作品   | `/api/communityAdmin/auditContributions`   | `/communityAdmin/auditContributions`   | GET      | ❌        | ⚠️ 不匹配   |
| 驳回作品         | `/api/communityAdmin/dismissContribution`  | `/communityAdmin/dismissContribution`  | POST     | ❌        | ⚠️ 不匹配   |
| 通过审核         | `/api/communityAdmin/approveContribution`  | `/communityAdmin/approveContribution`  | POST     | ❌        | ⚠️ 不匹配   |
| 获取封禁用户列表 | `/api/communityAdmin/blockedUsers`         | `/communityAdmin/blockedUsers`         | GET      | ❌        | ⚠️ 不匹配   |
| 获取封禁作品列表 | `/api/communityAdmin/blockedContributions` | `/communityAdmin/blockedContributions` | GET      | ❌        | ⚠️ 不匹配   |
| 删除评论         | -                                            | `/communityAdmin/deleteComment`        | POST     | ❌        | ❌ 前端未使用 |

### 8. 系统管理员模块 (SystemAdminController)

| 功能         | 前端调用                            | 后端接口                        | 请求方式 | 需要Token | 状态        |
| ------------ | ----------------------------------- | ------------------------------- | -------- | --------- | ----------- |
| 更新用户信息 | `/api/systemAdmin/updateUserInfo` | `/systemAdmin/updateUserInfo` | POST     | ❌        | ⚠️ 不匹配 |
| 重置密码     | `/api/systemAdmin/resetPassword`  | `/systemAdmin/resetPassword`  | POST     | ❌        | ⚠️ 不匹配 |
| 获取系统日志 | `/api/systemAdmin/logs`           | `/systemAdmin/logs`           | GET      | ❌        | ⚠️ 不匹配 |

## 问题汇总

### 🔴 路径不匹配问题

前端在某些接口调用中添加了 `/api` 前缀，而后端没有这个前缀：

**受影响的接口：**

- `/api/register` → 应该是 `/register`
- `/api/illustrations` → 应该是 `/illustrations`
- `/api/mangas` → 应该是 `/mangas`
- `/api/contribution` → 应该是 `/contribution`
- `/api/contributionsRanking` → 应该是 `/contributionsRanking`
- 所有 `/api/communityAdmin/*` → 应该是 `/communityAdmin/*`
- 所有 `/api/systemAdmin/*` → 应该是 `/systemAdmin/*`

### 🟡 功能缺失问题

**后端已实现但前端未使用的功能：**

1. 点赞作品 (`/user/likeContribution`)
2. 收藏作品 (`/user/favoriteContribution`)
3. 评论作品 (`/user/commentContribution`)
4. 删除评论 (`/user/deleteComment`)
5. 删除作品 (`/user/deleteContribution`)
6. 获取我的作品 (`/user/myContributions`)
7. 获取评论列表 (`/userCommentList`)
8. 关注用户 (`/user/concernUser`)
9. 关键词搜索 (`/search`)
10. 图片搜索 (`/image`)
11. 获取待审核作品详情 (`/pendingContribution`)
12. 封禁用户 (`/communityAdmin/blockUser`)
13. 封禁作品 (`/communityAdmin/blockContribution`)
14. 社区管理员删除评论 (`/communityAdmin/deleteComment`)

## 解决方案

### 方案一：修改前端（推荐）

删除前端调用中的 `/api` 前缀，使其与后端接口路径完全一致。

**需要修改的文件：**

- `mypixiv-vue/src/components/user/Register.vue`
- `mypixiv-vue/src/components/Index.vue`
- `mypixiv-vue/src/components/IllustrationPage.vue`
- `mypixiv-vue/src/components/MangaPage.vue`
- `mypixiv-vue/src/components/ImageView.vue`
- `mypixiv-vue/src/components/RankingPage.vue`
- `mypixiv-vue/src/components/admin/CommunityAdmin.vue`
- `mypixiv-vue/src/components/admin/SystemAdmin.vue`

### 方案二：修改后端

在 Spring Boot 配置中添加全局 `/api` 前缀。

**修改方式：**
在 `application.properties` 或 `application.yml` 中添加：

```properties
server.servlet.context-path=/api
```

### 方案三：配置代理

在前端 `vue.config.js` 中配置代理，将 `/api/*` 请求转发到后端。

## 响应码说明

### 后端响应码

- `code: 0` - 成功
- `code: 200` - 成功（部分接口）
- 其他值 - 失败

### 前端期望响应码

- 大部分接口期望 `code === 0`
- 部分接口期望 `code === 200`（如 ImageView.vue）

**建议**: 统一后端响应码为 `code: 200` 表示成功，以保持一致性。

## 更新日期

2025-11-01

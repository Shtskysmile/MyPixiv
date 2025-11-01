// src/mock/index.js - 对齐后端接口的 Mock 数据
import Mock from 'mockjs'

// ==================== 全局状态 ====================
// 存储作品ID和类型的映射关系，确保列表和详情页类型一致
const contributionTypeMap = new Map()

// ==================== 辅助函数 ====================
// 统一的 Result 返回格式（对齐后端 Result.java）
const successResult = (data) => ({
  code: 200,
  message: '操作成功',
  data: data
})

const errorResult = (message) => ({
  code: 500,
  message: message,
  data: null
})

// 生成随机用户数据（对齐 R_User.java）
const generateUser = (userId = null) => ({
  userId: userId || Mock.Random.guid(),
  role: Mock.Random.integer(0, 2), // 0-普通用户 1-社区管理员 2-系统管理员
  status: Mock.Random.integer(0, 1), // 0-正常 1-封禁
  username: Mock.Random.cname(),
  sex: Mock.Random.integer(0, 1), // 0-女 1-男
  avatar: Mock.Random.image('96x96', Mock.Random.hex(), '#FFF', 'U')
})

// 生成作品概览数据（对齐 R_OverviewContribution.java）
const generateOverviewContribution = (type = null) => {
  const contributionId = Mock.Random.guid()
  const finalType = type !== null ? type : Mock.Random.integer(0, 1) // 0-插画 1-漫画
  
  // 保存类型映射，确保详情页能获取到正确的类型
  contributionTypeMap.set(contributionId, finalType)
  
  return {
    contributionId: contributionId,
    type: finalType,
    title: Mock.Random.ctitle(5, 15),
    authorId: Mock.Random.guid(),
    image: Mock.Random.image('800x600', Mock.Random.hex(), '#FFF', 'Art'),
    viewCount: Mock.Random.integer(0, 10000),
    favoriteCount: Mock.Random.integer(0, 999),
    likeCount: Mock.Random.integer(0, 999),
    commentCount: Mock.Random.integer(0, 100),
    dismissalReason: null,
    avatar: Mock.Random.image('64x64', Mock.Random.hex(), '#FFF', 'A')
  }
}

// 生成作品详情数据（对齐 R_Contribution.java）
const generateContribution = (contributionId, forceType = null) => {
  const type = forceType !== null ? forceType : Mock.Random.integer(0, 1) // 0-插画 1-漫画
  const contribution = {
    contributionId: contributionId || Mock.Random.guid(),
    type: type,
    title: Mock.Random.ctitle(6, 12),
    description: Mock.Random.cparagraph(2, 5),
    status: 0, // 0-正常 1-封禁
    auditStatus: 1, // 0-待审核 1-已通过 2-已驳回
    publishTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
    authorId: Mock.Random.guid(),
    uploaderAvatarPath: Mock.Random.image('96x96', Mock.Random.hex(), '#FFF', 'U'),
    viewCount: Mock.Random.integer(0, 10000),
    favoriteCount: Mock.Random.integer(0, 999),
    likeCount: Mock.Random.integer(0, 999),
    commentCount: Mock.Random.integer(5, 20),
    dismissalReason: null
  }
  
  // 插画：单张图片
  if (type === 0) {
    contribution.image = Mock.Random.image('1200x900', Mock.Random.hex(), '#FFF', 'Illustration')
    contribution.images = [] // 插画不使用images数组
  } 
  // 漫画：多张图片
  else {
    contribution.image = '' // 漫画不使用单张image
    const pageCount = Mock.Random.integer(3, 8) // 漫画3-8页
    contribution.images = Array.from({ length: pageCount }, (_, i) => 
      Mock.Random.image('1200x1600', Mock.Random.hex(), '#FFF', `P${i + 1}`)
    )
  }
  
  return contribution
}

// 生成评论数据（对齐 R_ContributionComment.java）
const generateComment = () => ({
  author: Mock.Random.cname(),
  description: Mock.Random.cparagraph(1, 2),
  time: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
  avatar: Mock.Random.image('64x64', Mock.Random.hex(), '#FFF', 'U')
})

// ==================== UserController 接口 ====================

// POST /register - 用户注册
Mock.mock(/\/register$/, 'post', (options) => {
  try {
    const params = new URLSearchParams(options.body)
    const username = params.get('username')
    if (username === 'existinguser') {
      return errorResult('注册失败，用户名已存在')
    }
    return successResult('注册成功')
  } catch (e) {
    return errorResult('注册过程中出现错误')
  }
})

// POST /login - 用户登录（对齐 R_LoginDTO.java）
Mock.mock(/\/login$/, 'post', (options) => {
  try {
    const params = new URLSearchParams(options.body)
    const username = params.get('username')
    const password = params.get('password')
    
    if (!username || !password) {
      return errorResult('用户名或密码不能为空')
    }
    
    const user = generateUser('user-123456')
    user.username = username
    
    return successResult({
      user: user,
      token: 'mock-jwt-token-' + Mock.Random.guid()
    })
  } catch (e) {
    return errorResult('登录过程中出现错误')
  }
})

// POST /contributionList - 获取用户作品列表
Mock.mock(/\/contributionList$/, 'post', (options) => {
  try {
    const list = Array.from({ length: Mock.Random.integer(5, 15) }, () => 
      generateOverviewContribution()
    )
    return successResult(list)
  } catch (e) {
    return errorResult('获取作品列表出错')
  }
})

// POST /user/myContributions - 获取我的作品（需要token）
Mock.mock(/\/user\/myContributions$/, 'post', (options) => {
  try {
    const data = {
      pending: Array.from({ length: Mock.Random.integer(0, 3) }, () => 
        generateOverviewContribution()
      ),
      passed: Array.from({ length: Mock.Random.integer(5, 12) }, () => 
        generateOverviewContribution()
      ),
      rejected: Array.from({ length: Mock.Random.integer(0, 2) }, () => {
        const c = generateOverviewContribution()
        c.dismissalReason = '内容不符合社区规范'
        return c
      })
    }
    return successResult(data)
  } catch (e) {
    return errorResult('获取我的作品出错')
  }
})

// POST /concernedList - 获取关注列表
Mock.mock(/\/concernedList$/, 'post', (options) => {
  try {
    const list = Array.from({ length: Mock.Random.integer(5, 20) }, () => 
      generateUser()
    )
    return successResult(list)
  } catch (e) {
    return errorResult('获取关注列表出错')
  }
})

// POST /likedList - 获取点赞列表
Mock.mock(/\/likedList$/, 'post', (options) => {
  try {
    const list = Array.from({ length: Mock.Random.integer(5, 15) }, () => 
      generateOverviewContribution()
    )
    return successResult(list)
  } catch (e) {
    return errorResult('获取点赞列表出错')
  }
})

// POST /favouriteList - 获取收藏列表
Mock.mock(/\/favouriteList$/, 'post', (options) => {
  try {
    const list = Array.from({ length: Mock.Random.integer(5, 15) }, () => 
      generateOverviewContribution()
    )
    return successResult(list)
  } catch (e) {
    return errorResult('获取收藏列表出错')
  }
})

// POST /userCommentList - 获取用户评论列表
Mock.mock(/\/userCommentList$/, 'post', (options) => {
  try {
    const list = Array.from({ length: Mock.Random.integer(3, 10) }, () => ({
      comment: generateComment(),
      contribution: generateOverviewContribution()
    }))
    return successResult(list)
  } catch (e) {
    return errorResult('获取评论列表出错')
  }
})

// POST /user/deleteComment - 删除评论
Mock.mock(/\/user\/deleteComment$/, 'post', (options) => {
  return successResult('删除评论成功')
})

// POST /user/deleteContribution - 删除作品
Mock.mock(/\/user\/deleteContribution$/, 'post', (options) => {
  return successResult('删除作品成功')
})

// POST /userInfo - 获取用户信息（对齐 R_UserInfoDTO.java）
Mock.mock(/\/userInfo$/, 'post', (options) => {
  try {
    const params = new URLSearchParams(options.body)
    const userId = params.get('userId')
    
    const data = {
      user: generateUser(userId),
      isConcerned: Mock.Random.boolean()
    }
    return successResult(data)
  } catch (e) {
    return errorResult('获取用户信息出错')
  }
})

// POST /mySecurityIssues - 获取密保问题
Mock.mock(/\/mySecurityIssues$/, 'post', (options) => {
  try {
    const issues = [
      '您的出生地是？',
      '您母亲的姓名是？',
      '您最喜欢的颜色是？'
    ]
    return successResult(issues)
  } catch (e) {
    return errorResult('获取密保问题出错')
  }
})

// POST /verifySecurityIssues - 验证密保问题
Mock.mock(/\/verifySecurityIssues$/, 'post', (options) => {
  try {
    const data = {
      verified: true,
      token: 'temp-token-' + Mock.Random.guid()
    }
    return successResult(data)
  } catch (e) {
    return errorResult('校验密保出错')
  }
})

// POST /updatePassword - 修改密码
Mock.mock(/\/updatePassword$/, 'post', (options) => {
  return successResult('密码修改成功')
})

// POST /user/updateUserInfo - 更新用户信息
Mock.mock(/\/user\/updateUserInfo$/, 'post', (options) => {
  return successResult('更新成功')
})

// POST /user/concernUser - 关注用户
Mock.mock(/\/user\/concernUser$/, 'post', (options) => {
  return successResult('关注成功')
})

// POST /user/unconcernUser - 取消关注
Mock.mock(/\/user\/unconcernUser$/, 'post', (options) => {
  return successResult('已取消关注')
})

// ==================== ContributionController 接口 ====================

// GET /illustrations - 获取插画列表
Mock.mock(/\/api\/illustrations$/, 'get', (options) => {
  try {
    const list = Array.from({ length: 35 }, () => 
      generateOverviewContribution(0) // 0-插画
    )
    return successResult(list)
  } catch (e) {
    return errorResult('获取插画列表出错')
  }
})

// GET /mangas - 获取漫画列表
Mock.mock(/\/api\/mangas$/, 'get', (options) => {
  try {
    const list = Array.from({ length: 30 }, () => 
      generateOverviewContribution(1) // 1-漫画
    )
    return successResult(list)
  } catch (e) {
    return errorResult('获取漫画列表出错')
  }
})

// POST /api/contribution - 获取作品详情（对齐 R_ContributionDTO.java）
Mock.mock(/\/api\/contribution$/, 'post', (options) => {
  try {
    const params = new URLSearchParams(options.body)
    const contributionId = params.get('contributionId')
    
    // 从映射表中获取类型，如果不存在则使用哈希值生成（兼容直接访问详情页的情况）
    let type = contributionTypeMap.get(contributionId)
    
    if (type === undefined) {
      // 如果映射表中没有，使用哈希值生成一个稳定的类型
      let hash = 0
      for (let i = 0; i < contributionId.length; i++) {
        hash = ((hash << 5) - hash) + contributionId.charCodeAt(i)
        hash = hash & hash
      }
      type = Math.abs(hash) % 2 // 0-插画 1-漫画
      contributionTypeMap.set(contributionId, type) // 保存到映射表
    }
    
    const contribution = generateContribution(contributionId, type)
    const comments = Array.from({ length: Mock.Random.integer(5, 15) }, () => 
      generateComment()
    )
    
    const data = {
      contribution: contribution,
      comments: comments,
      isLiked: Mock.Random.boolean(),
      isFavorite: Mock.Random.boolean()
    }
    return successResult(data)
  } catch (e) {
    return errorResult('获取作品详情出错')
  }
})

// POST /pendingContribution - 获取待审核作品
Mock.mock(/\/pendingContribution$/, 'post', (options) => {
  try {
    const params = new URLSearchParams(options.body)
    const contributionId = params.get('contributionId')
    const contribution = generateContribution(contributionId)
    contribution.auditStatus = 0 // 待审核
    return successResult(contribution)
  } catch (e) {
    return errorResult('获取待审核作品出错')
  }
})

// POST /api/contributionsRanking - 获取作品排行
Mock.mock(/\/api\/contributionsRanking$/, 'post', (options) => {
  try {
    const params = new URLSearchParams(options.body)
    const type = parseInt(params.get('type')) // -1-全部, 0-插画, 1-漫画
    const key = parseInt(params.get('key')) // 0-浏览量, 1-收藏量, 2-点赞量, 3-评论量
    
    // 生成50个作品用于排行
    const list = Array.from({ length: 50 }, (_, i) => {
      // 根据type生成对应类型的作品
      let contributionType = null
      if (type === 0) {
        contributionType = 0 // 插画
      } else if (type === 1) {
        contributionType = 1 // 漫画
      } else {
        contributionType = i % 2 // -1表示全部，随机生成
      }
      
      const c = generateOverviewContribution(contributionType)
      
      // 根据key设置不同的排序数据
      const baseValue = 10000 - i * 150
      switch (key) {
        case 0: // 浏览量
          c.viewCount = baseValue + Mock.Random.integer(0, 500)
          c.favoriteCount = Mock.Random.integer(100, 1000)
          c.likeCount = Mock.Random.integer(100, 1000)
          c.commentCount = Mock.Random.integer(10, 200)
          break
        case 1: // 收藏量
          c.favoriteCount = baseValue + Mock.Random.integer(0, 300)
          c.viewCount = Mock.Random.integer(1000, 10000)
          c.likeCount = Mock.Random.integer(100, 1000)
          c.commentCount = Mock.Random.integer(10, 200)
          break
        case 2: // 点赞量
          c.likeCount = baseValue + Mock.Random.integer(0, 400)
          c.viewCount = Mock.Random.integer(1000, 10000)
          c.favoriteCount = Mock.Random.integer(100, 1000)
          c.commentCount = Mock.Random.integer(10, 200)
          break
        case 3: // 评论量
          c.commentCount = Math.floor(baseValue / 50) + Mock.Random.integer(0, 50)
          c.viewCount = Mock.Random.integer(1000, 10000)
          c.favoriteCount = Mock.Random.integer(100, 1000)
          c.likeCount = Mock.Random.integer(100, 1000)
          break
        default:
          c.viewCount = baseValue
          c.favoriteCount = Math.floor(baseValue * 0.5)
          c.likeCount = Math.floor(baseValue * 0.5)
          c.commentCount = Math.floor(baseValue * 0.02)
      }
      
      return c
    })
    
    // 根据key进行排序
    list.sort((a, b) => {
      switch (key) {
        case 0: return b.viewCount - a.viewCount
        case 1: return b.favoriteCount - a.favoriteCount
        case 2: return b.likeCount - a.likeCount
        case 3: return b.commentCount - a.commentCount
        default: return b.viewCount - a.viewCount
      }
    })
    
    return successResult(list)
  } catch (e) {
    return errorResult('获取作品排行出错')
  }
})

// POST /user/likeContribution - 点赞作品
Mock.mock(/\/user\/likeContribution$/, 'post', (options) => {
  return successResult('点赞成功')
})

// POST /user/unlikeContribution - 取消点赞
Mock.mock(/\/user\/unlikeContribution$/, 'post', (options) => {
  return successResult('取消点赞成功')
})

// POST /user/favoriteContribution - 收藏作品
Mock.mock(/\/user\/favoriteContribution$/, 'post', (options) => {
  return successResult('收藏成功')
})

// POST /user/unfavoriteContribution - 取消收藏
Mock.mock(/\/user\/unfavoriteContribution$/, 'post', (options) => {
  return successResult('取消收藏成功')
})

// POST /user/commentContribution - 评论作品
Mock.mock(/\/user\/commentContribution$/, 'post', (options) => {
  return successResult('评论成功')
})

// POST /user/uploadContribution - 上传作品
Mock.mock(/\/user\/uploadContribution$/, 'post', (options) => {
  return successResult('上传成功')
})

// ==================== SearchController 接口 ====================

// POST /search - 搜索（对齐 R_SearchDTO.java）
Mock.mock(/\/search$/, 'post', (options) => {
  try {
    const params = new URLSearchParams(options.body)
    const keyword = params.get('keyword')
    const isTag = params.get('isTag') === 'true'
    
    const data = {
      users: Array.from({ length: Mock.Random.integer(2, 8) }, () => 
        generateUser()
      ),
      illustrations: Array.from({ length: Mock.Random.integer(5, 15) }, () => {
        const c = generateOverviewContribution()
        c.type = 0
        return c
      }),
      mangas: Array.from({ length: Mock.Random.integer(3, 10) }, () => {
        const c = generateOverviewContribution()
        c.type = 1
        return c
      })
    }
    return successResult(data)
  } catch (e) {
    return errorResult('搜索出错')
  }
})

// POST /image - 以图搜图
Mock.mock(/\/image$/, 'post', (options) => {
  try {
    const files = [] // Mock 无法真实返回文件，返回空数组
    return successResult(files)
  } catch (e) {
    return errorResult('查找图片出错')
  }
})

// ==================== 兼容旧接口（如果前端还在使用）====================

// 兼容旧的图片列表接口 GET /api/images?page=xxx
Mock.mock(/\/api\/images\?page=\d+/, 'get', (options) => {
  const list = Array.from({ length: 35 }, () => generateOverviewContribution())
  return successResult({
    list: list,
    totalPage: 100,
    page: 1
  })
})

// 兼容旧的图片详情接口 GET /api/image/:id
Mock.mock(/\/api\/image\/\d+$/, 'get', (options) => {
  const parts = options.url.split('/')
  const id = parts[parts.length - 1]
  const contribution = generateContribution(id)
  return successResult(contribution)
})

// 兼容旧的评论接口 GET /api/image/:id/comments
Mock.mock(/\/api\/image\/\d+\/comments/, 'get', (options) => {
  const comments = Array.from({ length: Mock.Random.integer(5, 12) }, () => 
    generateComment()
  )
  return successResult({
    id: options.url.split('/')[3],
    comments: comments
  })
})

// 兼容旧的用户信息接口 GET /api/user/:id
Mock.mock(/\/api\/user\/\d+$/, 'get', (options) => {
  const parts = options.url.split('/')
  const id = parts[parts.length - 1]
  const user = generateUser(id)
  return successResult(user)
})

// 兼容旧的用户收藏接口 GET /api/user/:id/favorite
Mock.mock(/\/api\/user\/\d+\/favorite$/, 'get', (options) => {
  const list = Array.from({ length: Mock.Random.integer(6, 14) }, () => 
    generateOverviewContribution()
  )
  return successResult({
    id: options.url.split('/')[3],
    list: list
  })
})

// 兼容旧的用户点赞接口 GET /api/user/:id/likes
Mock.mock(/\/api\/user\/\d+\/likes$/, 'get', (options) => {
  const list = Array.from({ length: Mock.Random.integer(4, 12) }, () => 
    generateOverviewContribution()
  )
  return successResult({
    id: options.url.split('/')[3],
    list: list
  })
})

// 兼容旧的用户作品接口 GET /api/user/:id/works
Mock.mock(/\/api\/user\/\d+\/works/, 'get', (options) => {
  const url = new URL('http://dummy' + options.url)
  const page = parseInt(url.searchParams.get('page')) || 1
  const pageSize = parseInt(url.searchParams.get('pageSize')) || 12
  
  const list = Array.from({ length: pageSize }, () => 
    generateOverviewContribution()
  )
  
  return successResult({
    id: options.url.split('/')[3],
    page: page,
    pageSize: pageSize,
    total: Mock.Random.integer(20, 120),
    list: list
  })
})

// 兼容旧的用户粉丝接口 GET /api/user/:id/followers
Mock.mock(/\/api\/user\/\d+\/followers/, 'get', (options) => {
  const url = new URL('http://dummy' + options.url)
  const page = parseInt(url.searchParams.get('page')) || 1
  const pageSize = parseInt(url.searchParams.get('pageSize')) || 10
  
  const list = Array.from({ length: pageSize }, () => generateUser())
  
  return successResult({
    id: options.url.split('/')[3],
    page: page,
    pageSize: pageSize,
    total: Mock.Random.integer(20, 120),
    list: list
  })
})

// 兼容旧的修改密码接口 POST /api/changepwd
Mock.mock(/\/api\/changepwd$/, 'post', (options) => {
  return successResult({
    success: true,
    message: '密码修改请求已发送'
  })
})

// 兼容旧的提交作品接口 POST /api/user/:id/submit
Mock.mock(/\/api\/user\/\d+\/submit$/, 'post', (options) => {
  return successResult({
    success: true,
    message: '提交已接收 (mock)',
    artwork: generateOverviewContribution()
  })
})

console.log('✅ Mock.js 已加载 - 对齐后端 SpringBoot 接口')

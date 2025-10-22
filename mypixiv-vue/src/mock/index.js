// src/mock/index.js
import Mock from 'mockjs'

Mock.mock(/\/api\/images\?page=\d+/, 'get', (options) => {
  const url = new URL('http://dummy' + options.url);
  const page = parseInt(url.searchParams.get('page')) || 1;
  const totalPage = 100;
  return Mock.mock({
    list: Array.from({ length: 35 }, (_, i) => ({
      id: Mock.Random.integer(1, 1000000),
      url: Mock.Random.image('800x600', Mock.Random.hex(), '#FFF', 'Illu'),
      title: Mock.Random.ctitle(5, 8),
      description: Mock.Random.cparagraph(1, 3),
      likes: Mock.Random.integer(0, 999),
      favorites: Mock.Random.integer(0, 999),
      author: {
        avatar: Mock.Random.image('64x64', Mock.Random.hex(), '#FFF', 'A'),
        name: Mock.Random.cname(),
      },
      tags: [Mock.Random.word(), Mock.Random.word(), Mock.Random.word()],
    })),
    totalPage,
    page
  });
});

// 详细图片信息（精确匹配，避免匹配 /comments）
Mock.mock(/\/api\/image\/\d+$/ , 'get', (options) => {
  const parts = options.url.split('/');
  const id = parts[parts.length - 1];
  return Mock.mock({
    id: parseInt(id),
    url: Mock.Random.image('1200x900', Mock.Random.hex(), '#FFF', 'Illu'),
    title: Mock.Random.ctitle(6, 12),
    description: Mock.Random.cparagraph(2, 5),
    date: Mock.Random.date('yyyy-MM-dd'),
    tags: [Mock.Random.word(), Mock.Random.word(), Mock.Random.word()],
    author: {
      name: Mock.Random.cname(),
      avatar: Mock.Random.image('96x96', Mock.Random.hex(), '#FFF', 'U')
    }
  });
});

// 图片评论列表
Mock.mock(/\/api\/image\/\d+\/comments/, 'get', (options) => {
  const count = Mock.Random.integer(5, 12);
  const comments = Array.from({ length: count }).map((_, i) => ({
    id: Mock.Random.integer(1000, 999999),
    author: Mock.Random.cname(),
    content: Mock.Random.cparagraph(1, 2),
    date: Mock.Random.date('yyyy-MM-dd'),
  }));
  return {
    id: options.url.split('/')[3],
    comments,
  };
});

// 用户信息（精确匹配）
Mock.mock(/\/api\/user\/\d+$/, 'get', (options) => {
  const parts = options.url.split('/');
  const id = parts[parts.length - 1];
  return Mock.mock({
    id: parseInt(id),
    name: Mock.Random.cname(),
    role: '艺术家',
    bio: Mock.Random.cparagraph(1, 2),
    avatar: Mock.Random.image('96x96', Mock.Random.hex(), '#FFF', 'U')
  });
});

// 用户收藏
Mock.mock(/\/api\/user\/\d+\/favorites/, 'get', (options) => {
  const list = Array.from({ length: 12 }, (_, i) => ({
    id: Mock.Random.integer(1, 1000000),
    url: Mock.Random.image('400x300', Mock.Random.hex(), '#FFF', 'Illu'),
    title: Mock.Random.ctitle(4, 8),
    likes: Mock.Random.integer(0, 9999),
    favorites: Mock.Random.integer(0, 9999),
    author: {
      name: Mock.Random.cname(),
      avatar: Mock.Random.image('48x48', Mock.Random.hex(), '#FFF', 'A')
    }
  }));
  return {
    id: options.url.split('/')[3],
    list,
  };
});

// 通用假收藏接口：/api/user/:uid/favorite
Mock.mock(/\/api\/user\/\d+\/favorite$/, 'get', (options) => {
  // 从 url 提取 uid
  const parts = options.url.split('/');
  // url 例子: /api/user/123456/favorite
  const uid = parts[parts.length - 2] || '0';
  const list = Array.from({ length: Mock.Random.integer(6, 14) }, (_, i) => ({
    id: Mock.Random.integer(1, 1000000),
    url: Mock.Random.image('400x300', Mock.Random.hex(), '#FFF', 'Illu'),
    title: Mock.Random.ctitle(4, 10),
    author: {
      name: Mock.Random.cname(),
      avatar: Mock.Random.image('64x64', Mock.Random.hex(), '#FFF', 'A')
    }
  }));
  return {
    id: uid,
    list,
  };
});

// 用户点赞（mock）
Mock.mock(/\/api\/user\/\d+\/likes$/, 'get', (options) => {
  const parts = options.url.split('/');
  const uid = parts[parts.length - 2] || '0';
  const list = Array.from({ length: Mock.Random.integer(4, 12) }, (_, i) => ({
    id: Mock.Random.integer(1, 1000000),
    url: Mock.Random.image('400x300', Mock.Random.hex(), '#FFF', 'Illu'),
    title: Mock.Random.ctitle(4, 10),
    likes: Mock.Random.integer(0, 9999),
    favorites: Mock.Random.integer(0, 9999),
    author: {
      name: Mock.Random.cname(),
      avatar: Mock.Random.image('64x64', Mock.Random.hex(), '#FFF', 'A')
    }
  }));
  return {
    id: uid,
    list,
  };
});

// 用户作品（mock，支持分页）
Mock.mock(/\/api\/user\/\d+\/works/, 'get', (options) => {
  const url = new URL('http://dummy' + options.url);
  const page = parseInt(url.searchParams.get('page')) || 1;
  const pageSize = parseInt(url.searchParams.get('pageSize')) || 12;
  const total = Mock.Random.integer(10, 120);
  const list = Array.from({ length: pageSize }, (_, i) => ({
    id: Mock.Random.integer(1, 1000000),
    url: Mock.Random.image('800x600', Mock.Random.hex(), '#FFF', 'Illu'),
    title: Mock.Random.ctitle(4, 12),
    likes: Mock.Random.integer(0, 9999),
    favorites: Mock.Random.integer(0, 9999),
    author: {
      name: Mock.Random.cname(),
      avatar: Mock.Random.image('64x64', Mock.Random.hex(), '#FFF', 'A')
    }
  }));
  return {
    id: options.url.split('/')[3],
    page,
    pageSize,
    total,
    list
  };
});

// 用户粉丝（mock），支持分页参数 ?page=&pageSize=
Mock.mock(/\/api\/user\/\d+\/followers/, 'get', (options) => {
  const url = new URL('http://dummy' + options.url);
  const page = parseInt(url.searchParams.get('page')) || 1;
  const pageSize = parseInt(url.searchParams.get('pageSize')) || 10;
  const total = Mock.Random.integer(20, 120);
  const list = Array.from({ length: pageSize }, (_, i) => ({
    id: Mock.Random.integer(1, 1000000),
    name: Mock.Random.cname(),
    avatar: Mock.Random.image('96x96', Mock.Random.hex(), '#FFF', 'U'),
    bio: Mock.Random.cparagraph(1, 2)
  }));
  return {
    id: options.url.split('/')[3],
    page,
    pageSize,
    total,
    list
  };
});

// 更新用户信息（模拟 POST/PUT）
Mock.mock(/\/api\/user\/\d+$/, 'post', (options) => {
  try {
    const body = JSON.parse(options.body || '{}');
    const parts = options.url.split('/');
    const id = parseInt(parts[parts.length - 1]);
    return Mock.mock({
      id,
      name: body.name || Mock.Random.cname(),
      role: body.role || '艺术家',
      bio: body.bio || Mock.Random.cparagraph(1,2),
      avatar: body.avatar || Mock.Random.image('96x96', Mock.Random.hex(), '#FFF', 'U')
    });
  } catch (e) {
    return { error: 'invalid_body' };
  }
});

// 模拟提交画作接口：接收 multipart/form-data 或 JSON，返回 success
Mock.mock(/\/api\/user\/\d+\/submit$/, 'post', (options) => {
  // options.body 在 multipart 的情况下可能不是可解析的 JSON；我们只返回成功响应
  return Mock.mock({
    success: true,
    message: '提交已接收 (mock)',
    artwork: {
      id: Mock.Random.integer(100000, 999999),
      url: Mock.Random.image('800x600', Mock.Random.hex(), '#FFF', 'Illu'),
      title: Mock.Random.ctitle(4, 8),
      likes: 0,
      favorites: 0,
      author: { name: Mock.Random.cname(), avatar: Mock.Random.image('48x48', Mock.Random.hex(), '#FFF', 'A') }
    }
  });
});
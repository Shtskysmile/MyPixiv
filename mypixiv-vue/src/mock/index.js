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
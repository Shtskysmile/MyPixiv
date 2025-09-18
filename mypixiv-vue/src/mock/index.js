// src/mock/index.js
import Mock from 'mockjs'

Mock.mock(/\/api\/images\?page=\d+/, 'get', (options) => {
  const url = new URL('http://dummy' + options.url);
  const page = parseInt(url.searchParams.get('page')) || 1;
  const totalPage = 100;
  return Mock.mock({
    list: Array.from({ length: 35 }, (_, i) => ({
      id: (page - 1) * 35 + i + 1,
      img: Mock.Random.image('128x128', '#50B347', '#FFF', 'Mock'),
      title: Mock.Random.ctitle(5, 8),
      desc: Mock.Random.cparagraph(1, 3),
      likes: Mock.Random.integer(0, 999),
      favorites: Mock.Random.integer(0, 999),
      author_avatar: Mock.Random.image('32x32', '#FF6600', '#FFF', 'A'),
      author_name: Mock.Random.cname(),
    })),
    totalPage,
    page
  });
});
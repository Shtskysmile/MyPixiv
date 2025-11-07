/**
 * 图片加载工具
 * 用于从后端获取图片数据并转换为可用的 URL
 */

import request from './request';

/**
 * 从后端获取图片数据
 * @param {string} imagePath - 后端返回的图片路径（如 /files/userId/avatar/xxx.jpg）
 * @returns {Promise<string|null>} - 返回图片的 URL
 */
export async function loadImage(imagePath) {
  if (!imagePath) {
    return null;
  }

  // 🔍 调试：检查 imagePath 的类型
  console.log('🔍 [imageLoader] imagePath:', imagePath, 'type:', typeof imagePath);

  // 如果 imagePath 是数组，取第一个元素
  if (Array.isArray(imagePath)) {
    console.warn('⚠️ imagePath 是数组，取第一个元素:', imagePath[0]);
    imagePath = imagePath[0];
  }

  // 确保 imagePath 是字符串
  if (typeof imagePath !== 'string') {
    console.error('❌ imagePath 不是字符串类型:', imagePath, typeof imagePath);
    return null;
  }

  // 🔧 修复：将Windows路径分隔符 \ 替换为URL标准的 /
  imagePath = imagePath.replace(/\\/g, '/');

  // 如果已经是完整的 URL 或 data URI，直接返回
  if (imagePath.startsWith('data:') || imagePath.startsWith('blob:') ||
      imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
    return imagePath;
  }

  try {
    // 后端已配置静态资源映射 /files/** -> uploads/
    // imagePath 格式如: /files/userId/avatar/xxx.jpg 或 /files/userId/illustration/workId/xxx.jpg
    // 直接拼接基础 URL 即可访问
    const baseURL = process.env.VUE_APP_API_BASE_URL;
    const imageUrl = imagePath.startsWith('/') ? `${baseURL}${imagePath}` : `${baseURL}/${imagePath}`;

    console.log('✅ 图片 URL 已生成:', imageUrl);
    return imageUrl;

  } catch (error) {
    console.error('❌ 生成图片 URL 失败:', imagePath, error);

    // 发生错误时，尝试直接返回路径
    return imagePath.startsWith('/') ? imagePath : `/${imagePath}`;
  }
}

/**
 * 批量加载图片
 * @param {string[]} imagePaths - 图片路径数组
 * @returns {Promise<string[]>} - 返回图片 URL 数组
 */
export async function loadImages(imagePaths) {
  if (!imagePaths || !Array.isArray(imagePaths)) {
    return [];
  }

  const promises = imagePaths.map(path => loadImage(path));
  const results = await Promise.all(promises);
  return results.filter(url => url !== null);
}

/**
 * 从后端获取头像图片
 * 专门用于头像加载，提供更好的错误处理和默认值
 * @param {string} avatarPath - 后端返回的头像路径
 * @returns {Promise<string|null>} - 返回头像 URL 或 null
 */
export async function loadAvatar(avatarPath) {
  if (!avatarPath) {
    return null;
  }

  try {
    const url = await loadImage(avatarPath);
    if (url) {
      console.log('✅ 头像加载成功:', avatarPath);
      return url;
    }
    return null;
  } catch (error) {
    console.error('❌ 头像加载失败:', avatarPath, error);
    return null;
  }
}

/**
 * 清理 Blob URL
 * 当不再需要图片时调用，释放内存
 * @param {string} blobUrl - Blob URL
 */
export function revokeBlobUrl(blobUrl) {
  if (blobUrl && blobUrl.startsWith('blob:')) {
    URL.revokeObjectURL(blobUrl);
  }
}

export default {
  loadImage,
  loadImages,
  loadAvatar,
  revokeBlobUrl
};


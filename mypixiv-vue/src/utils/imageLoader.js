/**
 * 图片加载工具
 * 用于从后端获取图片数据并转换为可用的 URL
 */

import request from './request';

/**
 * 从后端获取图片数据
 * @param {string} imagePath - 后端返回的图片路径（如 /files/1/avatar/）
 * @returns {Promise<string|null>} - 返回图片的 blob URL 或 null
 */
export async function loadImage(imagePath) {
  if (!imagePath) {
    return null;
  }

  // 如果已经是完整的 URL 或 data URI，直接返回
  if (imagePath.startsWith('data:') || imagePath.startsWith('blob:') || 
      imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
    return imagePath;
  }

  try {
    // 方法1: 尝试使用 /image 接口获取二进制图片数据
    const params = new URLSearchParams();
    params.append('imagePath', imagePath);

    const res = await request.post('/image', params, {
      responseType: 'blob' // 直接请求二进制数据
    });

    // 如果返回的是 Blob 对象
    if (res.data instanceof Blob && res.data.size > 0) {
      const blobUrl = URL.createObjectURL(res.data);
      console.log('✅ 图片加载成功 (blob):', imagePath);
      return blobUrl;
    }

    console.warn('⚠️  /image 接口返回空数据，尝试备用方法...');

    // 方法2: 如果方法1失败，尝试使用 JSON 响应格式
    const res2 = await request.post('/image', params, {
      responseType: 'json'
    });

    if (res2.data && res2.data.code === 0 && res2.data.data && res2.data.data.length > 0) {
      const imageFile = res2.data.data[0];
      
      // 如果后端返回的是 base64 编码的图片数据
      if (imageFile.base64) {
        console.log('✅ 图片加载成功 (base64):', imagePath);
        return `data:${imageFile.contentType || 'image/jpeg'};base64,${imageFile.base64}`;
      }
      
      // 如果后端返回的是二进制数据数组
      if (imageFile.bytes && Array.isArray(imageFile.bytes)) {
        const blob = new Blob([new Uint8Array(imageFile.bytes)], { 
          type: imageFile.contentType || 'image/jpeg' 
        });
        console.log('✅ 图片加载成功 (bytes array):', imagePath);
        return URL.createObjectURL(blob);
      }

      // 如果返回的是文件名，构造直接访问 URL
      if (imageFile.name || imageFile.originalFilename) {
        // 假设可以直接通过路径访问图片
        const directUrl = imagePath.startsWith('/') ? imagePath : `/${imagePath}`;
        console.log('✅ 使用直接路径访问图片:', directUrl);
        return directUrl;
      }
    }

    // 方法3: 最后尝试直接使用路径作为 URL
    console.warn('⚠️  尝试直接使用路径作为 URL:', imagePath);
    return imagePath.startsWith('/') ? imagePath : `/${imagePath}`;

  } catch (error) {
    console.error('❌ 加载图片失败:', imagePath, error);
    
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


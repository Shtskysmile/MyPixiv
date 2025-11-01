<template>
  <div class="anime-submit-container">
    <div class="submit-header">
      <h3 class="submit-title anime-gradient-text">
        <span class="icon">📤</span> 提交作品
      </h3>
      <p class="submit-subtitle">分享你的创作，让更多人看到！</p>
    </div>

    <form @submit.prevent="handleSubmit" class="submit-form">
      <div class="form-grid">
        <!-- 左侧：图片上传和预览 -->
        <div class="upload-section">
          <div class="upload-area" @click="triggerFileInput" v-if="imagePreviews.length === 0">
            <div class="upload-placeholder">
              <div class="upload-icon">📸</div>
              <p class="upload-text">点击上传图片</p>
              <p class="upload-hint">支持 JPG、PNG、GIF 格式</p>
            </div>
          </div>

          <!-- 图片预览 -->
          <div class="preview-area" v-else>
            <div class="preview-main">
              <img :src="currentPreview" alt="preview" class="preview-image" />
              <button type="button" class="delete-btn" @click="deleteCurrentImage">
                <span class="icon">🗑️</span>
              </button>
            </div>

            <!-- 缩略图列表 -->
            <div class="thumbnails" v-if="imagePreviews.length > 1">
              <div 
                v-for="(preview, idx) in imagePreviews" 
                :key="idx"
                class="thumbnail"
                :class="{ 'active': idx === imageIndex }"
                @click="imageIndex = idx"
              >
                <img :src="preview" alt="thumb" />
              </div>
            </div>

            <div class="preview-controls">
              <button type="button" class="control-btn" @click="prevImage" :disabled="imagePreviews.length <= 1">
                <span class="icon">◀️</span>
              </button>
              <span class="image-counter">{{ imageIndex + 1 }} / {{ imagePreviews.length }}</span>
              <button type="button" class="control-btn" @click="nextImage" :disabled="imagePreviews.length <= 1">
                <span class="icon">▶️</span>
              </button>
              <button type="button" class="control-btn add-more" @click="triggerFileInput">
                <span class="icon">➕</span> 添加更多
              </button>
            </div>
          </div>

          <input 
            type="file" 
            ref="fileInput"
            @change="onFileChange" 
            accept="image/*" 
            multiple
            style="display: none;"
          />
        </div>

        <!-- 右侧：表单信息 -->
        <div class="info-section">
          <!-- 作品标题 -->
          <div class="field">
            <label class="label anime-label">
              <span class="icon">✏️</span> 作品标题
            </label>
            <div class="control">
              <input 
                class="input anime-input" 
                type="text" 
                v-model="title" 
                placeholder="给你的作品起个好听的名字"
                required
              />
            </div>
          </div>

          <!-- 作品类型 -->
          <div class="field">
            <label class="label anime-label">
              <span class="icon">🎨</span> 作品类型
            </label>
            <div class="control">
              <div class="type-selector">
                <label class="type-option" :class="{ 'active': artType === 0 }">
                  <input type="radio" v-model="artType" :value="0" />
                  <span class="type-label">
                    <span class="icon">🖼️</span>
                    <span>插画</span>
                  </span>
                </label>
                <label class="type-option" :class="{ 'active': artType === 1 }">
                  <input type="radio" v-model="artType" :value="1" />
                  <span class="type-label">
                    <span class="icon">📚</span>
                    <span>漫画</span>
                  </span>
                </label>
              </div>
            </div>
          </div>

          <!-- 作品描述 -->
          <div class="field">
            <label class="label anime-label">
              <span class="icon">📝</span> 作品描述
            </label>
            <div class="control">
              <textarea 
                class="textarea anime-textarea" 
                v-model="description" 
                placeholder="介绍一下你的作品吧..."
                rows="5"
              ></textarea>
            </div>
            <p class="help">{{ description.length }} / 500 字</p>
          </div>

          <!-- 提示信息 -->
          <div class="info-box">
            <p class="info-title">📌 提交须知</p>
            <ul class="info-list">
              <li>作品将进入审核队列，通过后才会公开显示</li>
              <li>请确保作品内容符合社区规范</li>
              <li>支持上传多张图片（漫画模式）</li>
            </ul>
          </div>

          <!-- 错误提示 -->
          <div class="notification is-danger is-light anime-notification" v-if="error">
            <span class="icon">⚠️</span> {{ error }}
          </div>

          <!-- 成功提示 -->
          <div class="notification is-success is-light anime-notification" v-if="success">
            <span class="icon">✅</span> {{ success }}
          </div>

          <!-- 提交按钮 -->
          <div class="field">
            <div class="control">
              <button 
                type="submit" 
                class="button is-primary is-fullwidth anime-button"
                :class="{ 'is-loading': loading }"
                :disabled="loading || imagePreviews.length === 0"
              >
                <span class="icon">🚀</span>
                <span>{{ loading ? '上传中...' : '提交作品' }}</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </form>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'SubmitArtwork',
  props: {
    user: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      title: '',
      artType: 0, // 0-插画 1-漫画
      description: '',
      imageFiles: [],
      imagePreviews: [],
      imageIndex: 0,
      loading: false,
      error: '',
      success: ''
    };
  },
  computed: {
    currentPreview() {
      return this.imagePreviews[this.imageIndex] || '';
    }
  },
  methods: {
    triggerFileInput() {
      this.$refs.fileInput.click();
    },
    
    onFileChange(e) {
      const files = Array.from(e.target.files || []);
      if (!files.length) return;

      files.forEach(file => {
        // 验证文件类型
        if (!file.type.startsWith('image/')) {
          alert(`文件 ${file.name} 不是图片格式`);
          return;
        }

        this.imageFiles.push(file);
        
        // 创建预览
        const reader = new FileReader();
        reader.onload = (evt) => {
          this.imagePreviews.push(evt.target.result);
        };
        reader.readAsDataURL(file);
      });

      // 重置 input
      e.target.value = '';
    },
    
    prevImage() {
      if (this.imagePreviews.length <= 1) return;
      this.imageIndex = (this.imageIndex - 1 + this.imagePreviews.length) % this.imagePreviews.length;
    },
    
    nextImage() {
      if (this.imagePreviews.length <= 1) return;
      this.imageIndex = (this.imageIndex + 1) % this.imagePreviews.length;
    },
    
    deleteCurrentImage() {
      const idx = this.imageIndex;
      if (idx < 0 || idx >= this.imagePreviews.length) return;
      
      const confirmed = confirm('确定删除当前图片吗？');
      if (!confirmed) return;
      
      this.imagePreviews.splice(idx, 1);
      this.imageFiles.splice(idx, 1);
      
      if (this.imagePreviews.length === 0) {
        this.imageIndex = 0;
      } else if (this.imageIndex >= this.imagePreviews.length) {
        this.imageIndex = this.imagePreviews.length - 1;
      }
    },
    
    async handleSubmit() {
      this.error = '';
      this.success = '';
      
      // 验证
      if (!this.title.trim()) {
        this.error = '请输入作品标题';
        return;
      }
      if (this.imageFiles.length === 0) {
        this.error = '请至少上传一张图片';
        return;
      }
      if (this.description.length > 500) {
        this.error = '作品描述不能超过500字';
        return;
      }

      this.loading = true;

      try {
        // 对齐后端接口：POST /user/uploadContribution
        // 参数：title, type, description, images (List<MultipartFile>)
        const token = localStorage.getItem('token') || 'mock-token-123';
        const formData = new FormData();
        
        formData.append('title', this.title.trim());
        formData.append('type', this.artType);
        formData.append('description', this.description.trim());
        
        // 添加多个图片（对齐后端 List<MultipartFile>）
        this.imageFiles.forEach(file => {
          formData.append('images', file);
        });

        const res = await axios.post('/user/uploadContribution', formData, {
          headers: {
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'multipart/form-data'
          }
        });
        
        if (res.data && res.data.code === 0) {
          // 上传成功
          this.success = '作品提交成功！等待审核中...';
          this.$emit('submitted', null);
          
          // 重置表单
          this.title = '';
          this.artType = 0;
          this.description = '';
          this.imageFiles = [];
          this.imagePreviews = [];
          this.imageIndex = 0;
          
          // 3秒后清空成功提示
          setTimeout(() => {
            this.success = '';
          }, 3000);
        } else {
          this.error = res.data?.message || '提交失败，请稍后重试';
        }
      } catch (err) {
        console.error('提交作品错误:', err);
        this.error = err.response?.data?.message || '提交失败，请稍后重试';
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
.anime-submit-container {
  background: rgba(255, 255, 255, 0.98);
  border: 2px solid rgba(255, 105, 180, 0.2);
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 8px 32px rgba(147, 51, 234, 0.12);
}

.submit-header {
  margin-bottom: 32px;
  text-align: center;
}

.submit-title {
  font-size: 1.75rem;
  font-weight: 900;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 8px;
}

.anime-gradient-text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.submit-subtitle {
  color: #6b7280;
  font-weight: 600;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 32px;
}

.upload-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.upload-area {
  height: 400px;
  border: 3px dashed rgba(147, 51, 234, 0.3);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #667eea05 0%, #764ba205 100%);
}

.upload-area:hover {
  border-color: #a78bfa;
  background: linear-gradient(135deg, #667eea10 0%, #764ba210 100%);
  transform: scale(1.02);
}

.upload-placeholder {
  text-align: center;
}

.upload-icon {
  font-size: 64px;
  margin-bottom: 16px;
  opacity: 0.6;
}

.upload-text {
  font-size: 18px;
  font-weight: 700;
  color: #6366f1;
  margin-bottom: 8px;
}

.upload-hint {
  font-size: 14px;
  color: #9ca3af;
}

.preview-area {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.preview-main {
  position: relative;
  height: 400px;
  border-radius: 16px;
  overflow: hidden;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  box-shadow: 0 4px 12px rgba(147, 51, 234, 0.1);
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.delete-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(239, 68, 68, 0.9);
  border: 2px solid white;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.delete-btn:hover {
  transform: scale(1.1);
  background: #dc2626;
}

.thumbnails {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding: 8px 0;
}

.thumbnail {
  flex-shrink: 0;
  width: 80px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 3px solid transparent;
  transition: all 0.3s ease;
  opacity: 0.6;
}

.thumbnail:hover {
  opacity: 1;
  transform: scale(1.05);
}

.thumbnail.active {
  border-color: #a78bfa;
  opacity: 1;
  box-shadow: 0 0 0 2px rgba(167, 139, 250, 0.3);
}

.thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-controls {
  display: flex;
  gap: 8px;
  align-items: center;
  justify-content: center;
}

.control-btn {
  padding: 8px 16px;
  border-radius: 10px;
  border: 2px solid rgba(147, 51, 234, 0.2);
  background: white;
  color: #6366f1;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.control-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: transparent;
}

.control-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.control-btn.add-more {
  margin-left: auto;
}

.image-counter {
  font-weight: 700;
  color: #6366f1;
  padding: 0 12px;
}

.info-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.anime-label {
  color: #6366f1;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 6px;
}

.anime-input, .anime-textarea {
  border: 2px solid rgba(147, 51, 234, 0.2);
  border-radius: 12px;
  padding: 12px 16px;
  transition: all 0.3s ease;
  font-size: 15px;
}

.anime-input:focus, .anime-textarea:focus {
  border-color: #a78bfa;
  box-shadow: 0 0 0 4px rgba(167, 139, 250, 0.1);
}

.type-selector {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.type-option {
  position: relative;
  cursor: pointer;
}

.type-option input[type="radio"] {
  position: absolute;
  opacity: 0;
}

.type-label {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px;
  border: 2px solid rgba(147, 51, 234, 0.2);
  border-radius: 12px;
  background: white;
  transition: all 0.3s ease;
  font-weight: 700;
  color: #6b7280;
}

.type-label .icon {
  font-size: 32px;
}

.type-option.active .type-label {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: transparent;
  box-shadow: 0 4px 12px rgba(147, 51, 234, 0.3);
}

.info-box {
  padding: 16px;
  background: linear-gradient(135deg, #667eea08 0%, #764ba208 100%);
  border-radius: 12px;
  border-left: 4px solid #a78bfa;
}

.info-title {
  font-weight: 700;
  color: #6366f1;
  margin-bottom: 12px;
}

.info-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.info-list li {
  padding: 6px 0;
  color: #4b5563;
  font-size: 14px;
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.info-list li::before {
  content: '•';
  color: #a78bfa;
  font-weight: 900;
  font-size: 18px;
}

.anime-notification {
  border-radius: 12px;
  border-left: 4px solid;
  display: flex;
  align-items: center;
  gap: 8px;
}

.anime-button {
  border-radius: 12px;
  padding: 14px 28px;
  font-weight: 700;
  font-size: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
}

.anime-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(147, 51, 234, 0.3);
}

.anime-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@media (max-width: 1024px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
  
  .upload-area, .preview-main {
    height: 300px;
  }
}

@media (max-width: 768px) {
  .anime-submit-container {
    padding: 20px;
  }
  
  .submit-title {
    font-size: 1.5rem;
  }
  
  .upload-area, .preview-main {
    height: 250px;
  }
}
</style>


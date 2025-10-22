<template>
  <div class="box">
    <div class="columns is-gapless">
  <div class="column is-one-third" v-if="!hasRenderedImages && !hasSubmitted">
        <div class="preview-card">
          <div class="preview-inner">
            <div v-if="imagePreviews && imagePreviews.length" class="preview-paged">
              <div class="preview-single">
                <img :src="imagePreviews[imageIndex]" alt="preview" />
              </div>
              <div class="preview-controls" style="display:flex; gap:8px; align-items:center; flex-wrap:wrap">
                <div style="display:flex; gap:6px; align-items:center">
                  <button class="button is-small" @click="prevImage">上一张</button>
                  <span>{{ imageIndex + 1 }} / {{ imagePreviews.length }}</span>
                  <button class="button is-small" @click="nextImage">下一张</button>
                </div>
                <div style="display:flex; gap:6px; align-items:center">
                  <button class="button is-small is-danger" @click="deleteCurrentImage">删除图片</button>
                </div>
              </div>
              
            </div>
            <div v-else class="preview-empty">
              <div class="placeholder">预览区</div>
            </div>
          </div>

          <div class="preview-actions" style="width:100%; display:flex; gap:8px; align-items:center; justify-content:flex-start; flex-wrap:wrap">
            <label class="button is-light" style="margin-right:8px">
              选择文件
              <input ref="fileInput" type="file" accept="image/*" multiple @change="onImageChange" style="display:none" />
            </label>
            <span v-if="imageFiles && imageFiles.length" style="font-size:13px; color:#444; min-width:0; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; max-width:160px">已选 {{ imageFiles.length }} 张</span>
            <button v-if="imageFiles && imageFiles.length" class="button is-small is-danger" @click="clearAllImages">删除所有图片</button>
          </div>
        </div>
      </div>

      <div class="column">
        <h3 class="title is-5">提交作品</h3>
        <div v-if="hasRenderedImages" class="rendered-images" style="margin-bottom:12px">
          <label class="label">已渲染图片</label>
          <div style="display:flex; gap:8px; flex-wrap:wrap">
            <div v-for="(r, i) in renderedImages" :key="i" style="width:120px; height:80px; overflow:hidden; border-radius:6px; box-shadow:0 1px 0 rgba(0,0,0,0.05) inset">
              <img :src="r" style="width:100%; height:100%; object-fit:cover; display:block" />
            </div>
          </div>
        </div>
        <div class="field">
          <label class="label">画作类型</label>
          <div class="control">
            <div class="select">
              <select v-model="artType">
                <option value="illustration">插画</option>
                <option value="manga">漫画</option>
                <option value="concept">概念</option>
              </select>
            </div>
          </div>
        </div>

        <div class="field">
          <label class="label">画作标题</label>
          <div class="control">
            <input class="input" v-model="artTitle" placeholder=" 输入画作标题" />
          </div>
        </div>

        <div class="field">
          <label class="label">画作描述</label>
          <div class="control">
            <textarea class="textarea" v-model="artDesc" placeholder="撰写画作描述"></textarea>
          </div>
        </div>

        <div class="field">
          <div class="control">
            <button class="button is-primary" :class="{ 'is-loading': submitting }" @click="submitArtwork" :disabled="submitting">提交画作</button>
          </div>
        </div>

        <p class="help is-danger" v-if="submitError">{{ submitError }}</p>
        <p class="help is-success" v-if="submitSuccess">{{ submitSuccess }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'SubmitArtwork',
  props: {
    user: {
      type: Object,
      required: true,
    },
    renderedImages: {
      type: Array,
      required: false,
      default: () => []
    }
  },
  data() {
    return {
      artType: 'illustration',
      artTitle: '',
      artDesc: '',
    imageFiles: [],
  imagePreviews: [],
  imageIndex: 0,
  thumbsPerPage: 6,
  thumbPage: 0,
    hasSubmitted: false,
      submitError: '',
      submitSuccess: '',
      submitting: false,
    };
  },
  computed: {
    totalThumbPages() {
      const len = (this.imagePreviews || []).length;
      return Math.max(1, Math.ceil(len / this.thumbsPerPage));
    },
    hasRenderedImages() {
      return Array.isArray(this.renderedImages) && this.renderedImages.length > 0;
    }
  },
  methods: {
    onImageChange(e) {
      const files = e.target.files ? Array.from(e.target.files) : [];
      if (!files.length) return;
      // debug
      console.log('onImageChange files:', files.length);
      // append new files
      const startIndex = this.imagePreviews.length;
      this.imageFiles = this.imageFiles.concat(files);
      // set imageIndex to first of newly added so UI shows a preview immediately
      this.imageIndex = startIndex;
      this.ensureThumbPageForIndex();
      files.forEach((file) => {
        const reader = new FileReader();
        reader.onload = (ev) => {
          this.imagePreviews.push(ev.target.result);
        };
        reader.readAsDataURL(file);
      });
      // clear native input value so same file can be reselected
      if (this.$refs.fileInput) this.$refs.fileInput.value = null;
      // user is editing again, clear submitted flag
      this.hasSubmitted = false;
    },
    clearAllImages() {
      this.imageFiles = [];
      this.imagePreviews = [];
      this.imageIndex = 0;
      this.thumbPage = 0;
      this.hasSubmitted = false;
    },
    prevImage() {
      const len = (this.imagePreviews || []).length;
      if (!len) return;
      this.imageIndex = (this.imageIndex - 1 + len) % len;
      this.ensureThumbPageForIndex();
    },
    nextImage() {
      const len = (this.imagePreviews || []).length;
      if (!len) return;
      this.imageIndex = (this.imageIndex + 1) % len;
      this.ensureThumbPageForIndex();
    },
    jumpTo(idx) {
      const len = (this.imagePreviews || []).length;
      if (idx < 0 || idx >= len) return;
      this.imageIndex = idx;
    },
    deleteCurrentImage() {
      const idx = this.imageIndex;
      const len = (this.imagePreviews || []).length;
      if (idx < 0 || idx >= len) return;
      const ok = window.confirm('确定删除当前已选图片吗？');
      if (!ok) return;
      this.imagePreviews.splice(idx, 1);
      if (this.imageFiles && this.imageFiles.length > idx) {
        this.imageFiles.splice(idx, 1);
      }
      const newLen = (this.imagePreviews || []).length;
      if (newLen === 0) {
        this.imageIndex = 0;
      } else if (this.imageIndex >= newLen) {
        this.imageIndex = newLen - 1;
      }
      this.ensureThumbPageForIndex();
    },
    ensureThumbPageForIndex() {
      this.thumbPage = Math.floor(this.imageIndex / this.thumbsPerPage);
    },
    prevThumbPage() {
      if (this.thumbPage > 0) this.thumbPage--;
    },
    nextThumbPage() {
      if (this.thumbPage + 1 < this.totalThumbPages) this.thumbPage++;
    },
    
    async submitArtwork() {
      this.submitError = '';
      this.submitSuccess = '';
      if (!this.artTitle.trim()) {
        this.submitError = '请输入画作标题';
        return;
      }
      if (!this.imageFiles || !this.imageFiles.length) {
        this.submitError = '请上传至少一张画作图片';
        return;
      }
      this.submitting = true;
      try {
    const fd = new FormData();
    fd.append('type', this.artType);
    fd.append('title', this.artTitle.trim());
    fd.append('description', this.artDesc.trim());
    // multiple images
    this.imageFiles.forEach((f) => fd.append('images[]', f));

        const res = await axios.post(`/api/user/${this.user.id}/submit`, fd, {
          headers: { 'Content-Type': 'multipart/form-data' }
        });
        if (res && res.data && res.data.success) {
          this.submitSuccess = '提交成功（mock）';
          const artwork = res.data.artwork || null;
          // emit event to parent
          this.$emit('submitted', artwork);
          // reset form
          this.artTitle = '';
          this.imageFiles = [];
          this.imagePreviews = [];
          this.imageIndex = 0;
          this.thumbPage = 0;
          this.artDesc = '';
          // mark as submitted so preview-inner will be hidden (avoid colored background)
          this.hasSubmitted = true;
        } else {
          this.submitError = (res && res.data && res.data.message) || '提交失败';
        }
      } catch (e) {
        this.submitError = e.response && e.response.data && e.response.data.message ? e.response.data.message : String(e);
      } finally {
        this.submitting = false;
      }
    }
  }
};
</script>

<style scoped>
.preview-card { background: rgba(255,255,255,0.03); border-radius:8px; padding:12px; display:flex; flex-direction:column; align-items:center }
.preview-inner { width:100%; height:240px; display:flex; align-items:center; justify-content:center; background: rgba(0,0,0,0.04); border-radius:6px; overflow:hidden }
.preview-single img { width:100%; max-height:100%; height:auto; object-fit:contain; border-radius:6px; display:block }
.preview-empty .placeholder { color: rgba(255,255,255,0.5) }

/* thumbnail grid */
.preview-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
  width: 100%;
  padding: 8px 0;
}
.preview-cell {
  width: 100%;
  height: 96px;
  overflow: hidden;
  border-radius: 6px;
  box-shadow: 0 1px 0 rgba(0,0,0,0.05) inset;
}
.preview-cell img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
.preview-cell.is-active {
  outline: 2px solid #ff8da1;
}

@media (max-width: 600px) {
  .preview-grid { grid-template-columns: 1fr; }
  .preview-single img { max-height: 260px; }
}
/* removed multi-file list styles; using single-file UI */
</style>

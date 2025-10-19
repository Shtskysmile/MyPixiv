<template>
  <div class="box submit-grid">
    <div class="preview-card">
      <div
        class="preview-inner"
        :class="{ 'no-placeholder': submitSuccess }"
        @dragover.prevent
        @drop="onDropFiles"
      >
        <div v-if="visibleItems && visibleItems.length" class="preview-paged">
          <div class="preview-single">
            <img :src="visibleItems[displayPos].preview" alt="preview" />
          </div>
          <div class="preview-controls">
            <button class="button is-small" @click="prevImage">上一张</button>
            <span style="margin: 0 8px"
              >{{ displayPos + 1 }} / {{ visibleItems.length }}</span
            >
            <button class="button is-small" @click="nextImage">下一张</button>
            <button
              class="button is-small is-danger"
              style="margin-left: 12px"
              @click="deleteCurrentImage"
            >
              删除图片
            </button>
          </div>
        </div>
        <div class="preview-empty" v-else-if="!submitSuccess">
          <div class="placeholder">预览区</div>
        </div>
      </div>
      <div class="preview-actions">
        <input
          ref="fileInput"
          type="file"
          accept="image/*"
          multiple
          @change="onImageChange"
        />
      </div>

      <div class="thumb-list" v-if="visibleItems && visibleItems.length">
        <div
          class="thumb-item"
          v-for="(it, idx) in visibleItems"
          :key="it.id"
          draggable="true"
          @dragstart.prevent="onThumbDragStart($event, idx)"
          @dragover.prevent="onThumbDragOver($event, idx)"
          @drop.prevent="onThumbDrop($event, idx)"
        >
          <img :src="it.preview" alt="thumb" />
          <div class="thumb-index">{{ idx + 1 }}</div>
        </div>
      </div>
    </div>

    <div class="form-column">
      <h3 class="title is-5">提交作品</h3>
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
          <textarea
            class="textarea"
            v-model="artDesc"
            placeholder="撰写画作描述"
          ></textarea>
        </div>
      </div>

      <div class="field">
        <div class="control">
          <button
            class="button is-primary"
            :class="{ 'is-loading': submitting }"
            @click="submitArtwork"
            :disabled="submitting"
          >
            提交画作
          </button>
        </div>
      </div>

      <p class="help is-danger" v-if="submitError">{{ submitError }}</p>
      <p class="help is-success" v-if="submitSuccess">{{ submitSuccess }}</p>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "SubmitArtwork",
  props: {
    userId: {
      type: [String, Number],
      required: true,
    },
  },
  data() {
    return {
      artType: "illustration",
      artTitle: "",
      // 每个图片项包含 { id, file, preview }
  imageItems: [],
  // position inside visibleItems (items that already have preview)
  displayPos: 0,
      artDesc: "",
      submitError: "",
      submitting: false,
      submitSuccess: "",
      // 拖拽临时索引
      dragSrcIndex: -1,
    };
  },
  created() {
    // ensure console.print exists for debugging; map to console.log if absent
    if (!console.print) {
      console.print = console.log.bind(console);
    }
  },
  methods: {
    created() {},
    readFileAsDataURL(file) {
      return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = (e) => resolve(e.target.result);
        reader.onerror = (e) => reject(e);
        reader.readAsDataURL(file);
      });
    },

    async addFiles(files) {
      if (!files || !files.length) return;
      const startIndex = this.imageItems.length;
      console.print &&
        console.print("addFiles start", { count: files.length, startIndex });
      for (let i = 0; i < files.length; i++) {
        const file = files[i];
        const id = Date.now() + Math.random();
        // push placeholder item first to keep order
        const item = { id, file, preview: null };
        this.imageItems.push(item);
        console.print &&
          console.print("added placeholder item", { id, name: file.name });
        try {
          const preview = await this.readFileAsDataURL(file);
          // find the item (could have moved) and set preview
          const idx = this.imageItems.findIndex((it) => it.id === id);
          if (idx !== -1) this.$set(this.imageItems[idx], "preview", preview);
          console.print && console.print("set preview for item", { id, idx });
        } catch (err) {
          // ignore preview error
          console.print && console.print("readFileAsDataURL error", err);
        }
      }
      console.print &&
        console.print("addFiles done", { total: this.imageItems.length });
      // show first of newly added files that have preview
      const firstPreviewGlobal = this.imageItems.findIndex((it, idx) => idx >= startIndex && it.preview);
      if (firstPreviewGlobal !== -1) {
        const vid = this.visibleItems.findIndex((v) => v.id === this.imageItems[firstPreviewGlobal].id);
        this.displayPos = vid >= 0 ? vid : 0;
      }
    },
    async onImageChange(e) {
      const input = this.$refs.fileInput;
      const newFiles = input && input.files ? Array.from(input.files) : [];
      console.print && console.print("onImageChange", { count: newFiles.length });
      if (!newFiles.length) return;
      await this.addFiles(newFiles);
      // 清空 input，允许用户重复选择同一文件
      try {
        if (input) input.value = null;
      } catch (err) {}
    },
    prevImage() {
      if (!this.visibleItems.length) return;
      this.displayPos = (this.displayPos - 1 + this.visibleItems.length) % this.visibleItems.length;
    },
    nextImage() {
      if (!this.visibleItems.length) return;
      this.displayPos = (this.displayPos + 1) % this.visibleItems.length;
    },
    deleteCurrentImage() {
      // delete currently visible image
      if (!this.visibleItems.length) return;
      const currentVisible = this.visibleItems[this.displayPos];
      if (!currentVisible) return;
      const idx = this.imageItems.findIndex((it) => it.id === currentVisible.id);
      if (idx < 0) return;
      const ok = window.confirm("确定删除当前已选图片吗？");
      if (!ok) return;
      this.imageItems.splice(idx, 1);
      if (!this.visibleItems.length) {
        this.displayPos = 0;
      } else if (this.displayPos >= this.visibleItems.length) {
        this.displayPos = this.visibleItems.length - 1;
      }
    },
    // 拖拽：把文件从外部 drop 进来（支持从系统文件管理器拖入）
    async onDropFiles(e) {
      const items =
        e.dataTransfer && e.dataTransfer.files
          ? Array.from(e.dataTransfer.files)
          : [];
      console.print && console.print("onDropFiles", { count: items.length });
      if (!items.length) return;
      await this.addFiles(items);
    },

    // 缩略图拖拽排序相关
    onThumbDragStart(e, idx) {
      // idx is visible index — map to global
      const visible = this.visibleItems[idx];
      const global = this.imageItems.findIndex((it) => it.id === visible.id);
      this.dragSrcIndex = global;
      try {
        e.dataTransfer.effectAllowed = "move";
      } catch (err) {}
    },
    onThumbDragOver(e, idx) {
      // 可用于显示占位效果
      e.preventDefault();
    },
    onThumbDrop(e, idx) {
      e.preventDefault();
      const srcGlobal = this.dragSrcIndex;
      const destVisible = idx;
      const destVisibleItem = this.visibleItems[destVisible];
      if (!destVisibleItem) return;
      const destGlobal = this.imageItems.findIndex((it) => it.id === destVisibleItem.id);
      console.print && console.print("onThumbDrop", { srcGlobal, destGlobal });
      if (srcGlobal < 0 || srcGlobal === destGlobal) return;
      const item = this.imageItems.splice(srcGlobal, 1)[0];
      const insertAt = destGlobal;
      this.imageItems.splice(insertAt, 0, item);
      // 更新 displayPos 到移动后该项在 visible list 的位置
      const newVisibleIndex = this.visibleItems.findIndex((v) => v.id === item.id);
      this.displayPos = newVisibleIndex >= 0 ? newVisibleIndex : 0;
      this.dragSrcIndex = -1;
    },
    async submitArtwork() {
      console.print &&
        console.print("submitArtwork start", {
          title: this.artTitle,
          count: (this.imageItems || []).length,
        });
      this.submitError = "";
      this.submitSuccess = "";
      if (!this.artTitle.trim()) {
        this.submitError = "请输入画作标题";
        return;
      }
      if (!this.imageItems || !this.imageItems.length) {
        this.submitError = "请上传至少一张画作图片";
        return;
      }
      this.submitting = true;
      try {
        const fd = new FormData();
        fd.append("type", this.artType);
        fd.append("title", this.artTitle.trim());
        fd.append("description", this.artDesc.trim());
        // 按当前 order 上传每个 file
  this.imageItems.forEach((it) => fd.append("images", it.file));

        const res = await axios.post(`/api/user/${this.userId}/submit`, fd, {
          headers: { "Content-Type": "multipart/form-data" },
        });
        if (res && res.data && res.data.success) {
          console.print && console.print("submitArtwork response", res.data);
          this.submitSuccess = "提交成功（mock）";
          const artwork = res.data.artwork || null;
          // 重置表单
          this.artTitle = "";
          this.imageItems = [];
          this.displayPos = 0;
          this.artDesc = "";
          // 通知父组件（如果需要）
          this.$emit("submitted", artwork);
        } else {
          this.submitError =
            (res && res.data && res.data.message) || "提交失败";
        }
      } catch (e) {
        this.submitError =
          e.response && e.response.data && e.response.data.message
            ? e.response.data.message
            : String(e);
      } finally {
        this.submitting = false;
      }
    },
  },
  computed: {
    // only items that have preview ready should be visible in preview/thumbnail lists
    visibleItems() {
      return (this.imageItems || []).filter((it) => it && it.preview);
    },
  },
};
</script>

<style scoped>
.submit-grid {
  display: grid;
  grid-template-columns: 360px 1fr;
  gap: 20px;
  align-items: start;
}
.preview-card {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 8px;
  padding: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.preview-inner {
  width: 100%;
  height: 220px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.04);
  border-radius: 6px;
}
.preview-inner.no-placeholder {
  background: transparent !important;
}
.preview-paged {
  width: 100%;
}
.preview-single img {
  width: 100%;
  max-height: 380px;
  object-fit: contain;
  border-radius: 6px;
}
.preview-controls {
  margin-top: 8px;
  display: flex;
  align-items: center;
}
.preview-actions {
  margin-top: 12px;
}
/* 缩略图列表样式 */
.thumb-list {
  display: flex;
  gap: 8px;
  margin-top: 12px;
  width: 100%;
  overflow-x: auto;
  padding-bottom: 6px;
}
.thumb-item {
  width: 80px;
  height: 60px;
  border-radius: 6px;
  overflow: hidden;
  position: relative;
  cursor: grab;
  border: 1px solid rgba(255, 255, 255, 0.04);
  background: rgba(0, 0, 0, 0.02);
  display: flex;
  align-items: center;
  justify-content: center;
}
.thumb-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.thumb-index {
  position: absolute;
  right: 4px;
  bottom: 4px;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 12px;
}
.thumb-item:active {
  cursor: grabbing;
  opacity: 0.9;
}
/* form-column 保持默认样式 */
@media (max-width: 900px) {
  .submit-grid {
    grid-template-columns: 1fr;
  }
  .preview-inner {
    height: 180px;
  }
}
</style>

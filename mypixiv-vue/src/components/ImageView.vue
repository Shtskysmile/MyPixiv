<template>
  <div class="image-view-root">
    <Navbar />

    <section class="section main-content">
      <div class="container">
        <!-- 返回按钮 -->
        <button class="back-button anime-button" @click="goBack">
          <span class="icon">◀️</span>
          <span>返回</span>
        </button>

        <div class="columns is-variable is-6">
          <!-- 左侧：图片展示 + 详情 -->
          <div class="column is-two-thirds">
            <!-- 图片展示组件 -->
            <ImageDisplay
              :current-image="currentImage"
              :images="contribution.images || []"
              :current-image-index="currentImageIndex"
              :loaded-images="loadedImages"
              @open-modal="openModal"
              @prev-page="prevPage"
              @next-page="nextPage"
              @go-to-page="goToPage"
            />

            <!-- 作品详情组件 -->
            <ArtworkDetails
              :contribution="contribution"
              :avatar-url="avatarUrl"
              @go-to-author="goToAuthorProfile"
              @copy-id="copyIdToClipboard"
            >
              <template #interaction-buttons>
                <InteractionButtons
                  :is-liked="isLiked"
                  :is-favorite="isFavorite"
                  :like-loading="likeLoading"
                  :favorite-loading="favoriteLoading"
                  :disabled="isCommunityAdmin || isSystemAdmin || isPendingWork"
                  @toggle-like="toggleLike"
                  @toggle-favorite="toggleFavorite"
                />
              </template>
            </ArtworkDetails>

            <!-- 管理员面板组件 -->
            <AdminPanel
              v-if="isCommunityAdmin"
              :contribution="contribution"
              :is-community-admin="isCommunityAdmin"
              :audit-loading="auditLoading"
              :block-loading="blockLoading"
              @approve-work="approveWork"
              @show-dismiss-modal="showDismissModal"
              @block-work="blockWork"
              @unblock-work="unblockWork"
            />
          </div>

          <!-- 右侧：评论区 -->
          <div class="column">
            <CommentSection
              :comments="comments"
              :is-community-admin="isCommunityAdmin"
              :is-system-admin="isSystemAdmin"
              :is-pending="isPendingWork"
              @submit-comment="submitComment"
              @delete-comment="deleteComment"
            />
          </div>
        </div>
      </div>
    </section>

    <!-- 图片放大模态框 -->
    <div
      v-if="showModal"
      class="image-modal"
      @click.self="closeModal"
      @wheel="handleWheel"
    >
      <button class="close-btn anime-button" @click="closeModal">✕ 关闭</button>
      <button class="reset-btn anime-button" @click="resetZoom">🔄 重置</button>
      <div class="zoom-indicator">{{ Math.round(zoomLevel * 100) }}%</div>
      <div
        class="modal-content"
        @mousedown="startDrag"
        @mousemove="onDrag"
        @mouseup="endDrag"
        @mouseleave="endDrag"
        :style="{
          cursor: isDragging ? 'grabbing' : zoomLevel > 1 ? 'grab' : 'default',
        }"
      >
        <img
          :src="modalImageSrc"
          alt="modal-image"
          :style="imageTransformStyle"
          @dragstart.prevent
        />
      </div>
    </div>

    <!-- 驳回理由模态框 -->
    <div class="modal" :class="{ 'is-active': showDismissReasonModal }">
      <div class="modal-background" @click="closeDismissModal"></div>
      <div class="modal-card anime-modal">
        <header class="modal-card-head">
          <p class="modal-card-title"><span class="icon">❌</span> 驳回作品</p>
          <button
            class="delete"
            aria-label="close"
            @click="closeDismissModal"
          ></button>
        </header>
        <section class="modal-card-body">
          <div class="field">
            <label class="label">
              <span class="icon">📝</span> 驳回理由
              <span class="has-text-danger">*</span>
            </label>
            <div class="control">
              <textarea
                class="textarea anime-input"
                v-model="dismissalReason"
                placeholder="请详细说明驳回的原因，帮助作者改进..."
                rows="5"
                maxlength="500"
              ></textarea>
            </div>
            <p class="help">{{ dismissalReason.length }} / 500 字符</p>
          </div>
        </section>
        <footer class="modal-card-foot">
          <button
            class="button is-danger anime-button"
            @click="dismissWork"
            :disabled="!dismissalReason.trim() || auditLoading"
          >
            <span class="icon">❌</span>
            <span>{{ auditLoading ? "提交中..." : "确认驳回" }}</span>
          </button>
          <button class="button anime-button" @click="closeDismissModal">
            取消
          </button>
        </footer>
      </div>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";
import Navbar from "./Navbar.vue";
import ImageDisplay from "./imageview/ImageDisplay.vue";
import ArtworkDetails from "./imageview/ArtworkDetails.vue";
import InteractionButtons from "./imageview/InteractionButtons.vue";
import CommentSection from "./imageview/CommentSection.vue";
import AdminPanel from "./imageview/AdminPanel.vue";
import { loadImage, loadAvatar } from "@/utils/imageLoader";
import copyIdMixin from "@/mixins/copyId";

export default {
  name: "ImageView",
  mixins: [copyIdMixin],
  components: {
    Navbar,
    ImageDisplay,
    ArtworkDetails,
    InteractionButtons,
    CommentSection,
    AdminPanel,
  },
  props: {
    id: {
      type: [String, Number],
      default: null,
    },
  },
  data() {
    return {
      contribution: {
        contributionId: "",
        type: 0,
        title: "",
        image: "",
        images: [],
        description: "",
        status: 0,
        auditStatus: 1,
        publishTime: "",
        authorId: "",
        authorName: "",
        uploaderAvatarPath: "",
        viewCount: 0,
        favoriteCount: 0,
        likeCount: 0,
        commentCount: 0,
        dismissalReason: null,
        tags: [],
      },
      comments: [],
      isLiked: false,
      isFavorite: false,
      showModal: false,
      modalImageSrc: "",
      loading: false,
      likeLoading: false,
      favoriteLoading: false,
      currentImageIndex: 0,
      loadedImages: {},
      loadedAvatar: null,
      currentLoadedImage: null,
      defaultAvatar:
        'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="64" height="64"%3E%3Crect fill="%23ddd" width="64" height="64"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999" font-size="32"%3EU%3C/text%3E%3C/svg%3E',
      showDismissReasonModal: false,
      dismissalReason: "",
      auditLoading: false,
      blockLoading: false,
      zoomLevel: 1,
      translateX: 0,
      translateY: 0,
      isDragging: false,
      dragStartX: 0,
      dragStartY: 0,
      dragStartTranslateX: 0,
      dragStartTranslateY: 0,
    };
  },
  computed: {
    avatarUrl() {
      if (this.loadedAvatar) {
        return this.loadedAvatar;
      }
      if (this.contribution.uploaderAvatarPath) {
        return this.getImageUrl(this.contribution.uploaderAvatarPath);
      }
      return this.defaultAvatar;
    },

    currentImage() {
      if (this.currentLoadedImage) {
        return this.currentLoadedImage;
      }

      let originalPath = "";
      if (
        this.contribution.images &&
        Array.isArray(this.contribution.images) &&
        this.contribution.images.length > 0
      ) {
        originalPath = this.contribution.images[this.currentImageIndex] || "";
      } else {
        let image = this.contribution.image || "";
        if (Array.isArray(image)) {
          originalPath = image[0] || "";
        } else {
          originalPath = image;
        }
      }

      if (originalPath && this.loadedImages[originalPath]) {
        return this.loadedImages[originalPath];
      }

      return this.getImageUrl(originalPath);
    },

    isCommunityAdmin() {
      const role = localStorage.getItem("userRole");
      return role === "1";
    },

    isSystemAdmin() {
      const role = localStorage.getItem("userRole");
      return role === "2";
    },

    isPendingWork() {
      // 判断是否为待审核作品（auditStatus === 0 或 URL中有 pending=true）
      return (
        this.contribution.auditStatus === 0 ||
        this.$route.query.pending === "true"
      );
    },

    imageTransformStyle() {
      return {
        transform: `translate(${this.translateX}px, ${this.translateY}px) scale(${this.zoomLevel})`,
        transition: this.isDragging ? "none" : "transform 0.2s ease",
      };
    },
  },
  created() {
    const imgId = this.id || this.$route.params.id || 1;
    this.fetchContribution(imgId);
  },
  methods: {
    goBack() {
      this.$router.go(-1);
    },

    fetchContribution(id) {
      this.loading = true;
      const token = localStorage.getItem("token") || "mock-token-123";
      const params = new URLSearchParams();
      params.append("contributionId", id);

      const isPending = this.$route.query.pending === "true";
      const isBlocked = this.$route.query.blocked === "true";

      let endpoint = "/contribution";
      if (isPending) {
        endpoint = "/pendingContribution";
      } else if (isBlocked && this.isCommunityAdmin) {
        endpoint = "/communityAdmin/bannedContribution";
      }

      request
        .post(endpoint, params)
        .then((res) => {
          if (res.data && res.data.code === 0) {
            const data = res.data.data;

            if (isPending || isBlocked) {
              this.contribution = data || {};
              this.comments = [];
              this.isLiked = false;
              this.isFavorite = false;

              if (Array.isArray(this.contribution.image)) {
                if (this.contribution.image.length === 1) {
                  this.contribution.image = this.contribution.image[0];
                  this.contribution.images = [];
                } else if (this.contribution.image.length > 1) {
                  this.contribution.images = this.contribution.image;
                  this.contribution.image = "";
                } else {
                  this.contribution.image = "";
                  this.contribution.images = [];
                }
              }
            } else {
              this.contribution = data.contribution || {};
              this.comments = data.comments || [];
              this.isLiked = data.isLiked || false;
              this.isFavorite = data.isFavorite || false;

              if (
                this.contribution.image &&
                Array.isArray(this.contribution.image)
              ) {
                if (this.contribution.image.length === 1) {
                  this.contribution.image = this.contribution.image[0];
                  this.contribution.images = [];
                } else if (this.contribution.image.length > 1) {
                  this.contribution.images = this.contribution.image;
                  this.contribution.image = "";
                } else {
                  this.contribution.image = "";
                  this.contribution.images = [];
                }
              }

              if (this.contribution.status === 1) {
                if (this.isCommunityAdmin) {
                  this.$router.replace({
                    path: `/image/${id}`,
                    query: { blocked: "true" },
                  });
                  return;
                } else {
                  alert("该作品已被封禁，无法访问");
                  this.$router.push("/");
                  return;
                }
              }
            }

            if (
              this.contribution.images &&
              Array.isArray(this.contribution.images) &&
              this.contribution.images.length > 0
            ) {
              this.currentImageIndex = 0;
            }

            this.loadAllImages();
            this.loadAvatarImage();
          } else {
            console.error("获取作品详情失败:", res.data?.message);
          }
        })
        .catch((err) => {
          console.error("请求失败:", err);
          if (
            err.response &&
            (err.response.status === 404 || err.response.status === 403)
          ) {
            alert("作品不存在或已被封禁");
            this.$router.push("/");
          }
        })
        .finally(() => {
          this.loading = false;
        });
    },

    async loadAllImages() {
      if (
        this.contribution.images &&
        Array.isArray(this.contribution.images) &&
        this.contribution.images.length > 0
      ) {
        const imagePaths = this.contribution.images;
        for (let i = 0; i < imagePaths.length; i++) {
          const path = imagePaths[i];
          if (!this.loadedImages[path]) {
            const url = await loadImage(path);
            this.$set(this.loadedImages, path, url || path);
          }
        }
        this.updateCurrentLoadedImage();
      } else if (this.contribution.image) {
        let path = this.contribution.image;

        if (Array.isArray(path)) {
          path = path[0];
        }

        if (!this.loadedImages[path]) {
          const url = await loadImage(path);
          this.$set(this.loadedImages, path, url || path);
        }
        this.updateCurrentLoadedImage();
      }
    },

    updateCurrentLoadedImage() {
      let currentPath;
      if (
        this.contribution.images &&
        Array.isArray(this.contribution.images) &&
        this.contribution.images.length > 0
      ) {
        currentPath = this.contribution.images[this.currentImageIndex];
      } else {
        currentPath = this.contribution.image;
        if (Array.isArray(currentPath)) {
          currentPath = currentPath[0];
        }
      }

      if (currentPath && this.loadedImages[currentPath]) {
        this.currentLoadedImage = this.loadedImages[currentPath];
      }
    },

    async loadAvatarImage() {
      if (this.contribution.uploaderAvatarPath && !this.loadedAvatar) {
        const url = await loadAvatar(this.contribution.uploaderAvatarPath);
        this.loadedAvatar = url || this.contribution.uploaderAvatarPath;
      }
    },

    async toggleLike() {
      if (this.likeLoading || this.isCommunityAdmin || this.isSystemAdmin) {
        if (this.isCommunityAdmin || this.isSystemAdmin) {
          alert("管理员无法点赞或取消点赞");
        }
        return;
      }

      this.likeLoading = true;
      const token = localStorage.getItem("token");

      if (!token) {
        alert("请先登录");
        this.likeLoading = false;
        return;
      }

      try {
        const endpoint = this.isLiked
          ? "/user/unlikeContribution"
          : "/user/likeContribution";
        const params = new URLSearchParams();
        params.append("contributionId", this.contribution.contributionId);

        const response = await request.post(endpoint, params);

        if (response.data?.code === 0 || response.data?.code === 200) {
          await this.refreshContributionData();
        } else {
          alert(response.data?.message || "操作失败");
        }
      } catch (err) {
        console.error("❌ 点赞操作失败:", err);
        alert("操作失败，请稍后重试");
      } finally {
        this.likeLoading = false;
      }
    },

    async toggleFavorite() {
      if (this.favoriteLoading || this.isCommunityAdmin || this.isSystemAdmin) {
        if (this.isCommunityAdmin || this.isSystemAdmin) {
          alert("管理员无法收藏或取消收藏");
        }
        return;
      }

      this.favoriteLoading = true;
      const token = localStorage.getItem("token");

      if (!token) {
        alert("请先登录");
        this.favoriteLoading = false;
        return;
      }

      try {
        const endpoint = this.isFavorite
          ? "/user/unfavoriteContribution"
          : "/user/favoriteContribution";
        const params = new URLSearchParams();
        params.append("contributionId", this.contribution.contributionId);

        const response = await request.post(endpoint, params);

        if (response.data?.code === 0 || response.data?.code === 200) {
          await this.refreshContributionData();
        } else {
          alert(response.data?.message || "操作失败");
        }
      } catch (err) {
        console.error("❌ 收藏操作失败:", err);
        alert("操作失败，请稍后重试");
      } finally {
        this.favoriteLoading = false;
      }
    },

    async submitComment(commentText) {
      if (!commentText.trim() || this.isCommunityAdmin || this.isSystemAdmin) {
        if (this.isCommunityAdmin || this.isSystemAdmin) {
          alert("管理员无法发表评论");
        }
        return;
      }

      const token = localStorage.getItem("token");

      if (!token) {
        alert("请先登录");
        return;
      }

      try {
        const params = new URLSearchParams();
        params.append("contributionId", this.contribution.contributionId);
        params.append("comment", commentText.trim());

        const response = await request.post(
          "/user/commentContribution",
          params
        );

        if (response.data?.code === 0 || response.data?.code === 200) {
          await this.refreshContributionData();
          alert("评论成功！");
        } else {
          alert(response.data?.message || "评论失败");
        }
      } catch (err) {
        console.error("❌ 评论失败:", err);
        alert("评论失败，请稍后重试");
      }
    },

    async deleteComment(comment) {
      if (!this.isCommunityAdmin) {
        alert("只有社区管理员可以删除评论");
        return;
      }

      const confirmed = window.confirm(
        `确定要删除这条评论吗？此操作不可恢复！\n\n` +
          `评论者: ${comment.authorName || comment.author || "匿名用户"}\n` +
          `评论内容: ${comment.description || "无内容"}`
      );

      if (!confirmed) {
        return;
      }

      const token = localStorage.getItem("token");
      if (!token) {
        alert("未登录，请先登录");
        this.$router.push("/login");
        return;
      }

      try {
        const params = new URLSearchParams();
        params.append("commentId", comment.commentId);

        const response = await request.post(
          "/communityAdmin/deleteComment",
          params
        );

        if (response.data?.code === 0 || response.data?.code === 200) {
          alert("删除评论成功！");
          this.comments = this.comments.filter(
            (c) => c.commentId !== comment.commentId
          );
          if (this.contribution.commentCount > 0) {
            this.contribution.commentCount--;
          }
        } else {
          alert(response.data?.message || "删除评论失败");
        }
      } catch (err) {
        console.error("❌ 删除评论失败:", err);
        alert("删除评论失败，请稍后重试");
      }
    },

    async refreshContributionData() {
      const token = localStorage.getItem("token");
      if (!token) return;

      const params = new URLSearchParams();
      params.append("contributionId", this.contribution.contributionId);

      const isPending = this.$route.query.pending === "true";
      const endpoint = isPending ? "/pendingContribution" : "/contribution";

      try {
        const response = await request.post(endpoint, params);

        if (response.data && response.data.code === 0) {
          const data = response.data.data;

          if (isPending) {
            this.contribution.likeCount = data.likeCount || 0;
            this.contribution.favoriteCount = data.favoriteCount || 0;
            this.contribution.viewCount = data.viewCount || 0;
            this.contribution.commentCount = data.commentCount || 0;
          } else {
            this.contribution.likeCount = data.contribution?.likeCount || 0;
            this.contribution.favoriteCount =
              data.contribution?.favoriteCount || 0;
            this.contribution.viewCount = data.contribution?.viewCount || 0;
            this.contribution.commentCount =
              data.contribution?.commentCount || 0;
            this.isLiked = data.isLiked || false;
            this.isFavorite = data.isFavorite || false;

            if (data.comments) {
              this.comments = data.comments;
            }
          }
        }
      } catch (err) {
        console.error("❌ 刷新数据失败:", err);
      }
    },

    openModal(src) {
      this.modalImageSrc = src;
      this.showModal = true;
      this.resetZoom();
    },

    closeModal() {
      this.showModal = false;
      this.modalImageSrc = "";
      this.resetZoom();
    },

    handleWheel(e) {
      e.preventDefault();

      const delta = e.deltaY > 0 ? -0.1 : 0.1;
      let newZoom = this.zoomLevel + delta;

      newZoom = Math.max(0.5, Math.min(5, newZoom));

      this.zoomLevel = newZoom;

      if (this.zoomLevel <= 1) {
        this.translateX = 0;
        this.translateY = 0;
      }
    },

    resetZoom() {
      this.zoomLevel = 1;
      this.translateX = 0;
      this.translateY = 0;
      this.isDragging = false;
    },

    startDrag(e) {
      if (this.zoomLevel <= 1) return;

      this.isDragging = true;
      this.dragStartX = e.clientX;
      this.dragStartY = e.clientY;
      this.dragStartTranslateX = this.translateX;
      this.dragStartTranslateY = this.translateY;
    },

    onDrag(e) {
      if (!this.isDragging) return;

      const deltaX = e.clientX - this.dragStartX;
      const deltaY = e.clientY - this.dragStartY;

      this.translateX = this.dragStartTranslateX + deltaX;
      this.translateY = this.dragStartTranslateY + deltaY;
    },

    endDrag() {
      this.isDragging = false;
    },

    prevPage() {
      if (this.currentImageIndex > 0) {
        this.currentImageIndex--;
        this.updateCurrentLoadedImage();
      }
    },

    nextPage() {
      if (this.currentImageIndex < this.contribution.images.length - 1) {
        this.currentImageIndex++;
        this.updateCurrentLoadedImage();
      }
    },

    goToPage(index) {
      if (index >= 0 && index < this.contribution.images.length) {
        this.currentImageIndex = index;
        this.updateCurrentLoadedImage();
      }
    },

    showDismissModal() {
      this.dismissalReason = "";
      this.showDismissReasonModal = true;
    },

    closeDismissModal() {
      this.showDismissReasonModal = false;
      this.dismissalReason = "";
    },

    async approveWork() {
      const ok = window.confirm("确定要审核通过这个作品吗？");
      if (!ok) return;

      this.auditLoading = true;

      try {
        const params = new URLSearchParams();
        params.append("contributionId", this.contribution.contributionId);

        const res = await request.post(
          "/communityAdmin/approveContribution",
          params
        );

        if (res.data && res.data.code === 0) {
          alert("审核通过成功！");
          this.contribution.auditStatus = 1;
          this.$router.push("/user");
        } else {
          alert("审核通过失败: " + (res.data?.message || "未知错误"));
        }
      } catch (error) {
        console.error("审核通过失败:", error);
        alert("审核通过失败，请稍后重试");
      } finally {
        this.auditLoading = false;
      }
    },

    async dismissWork() {
      if (!this.dismissalReason.trim()) {
        alert("请填写驳回理由");
        return;
      }

      this.auditLoading = true;

      try {
        const params = new URLSearchParams();
        params.append("contributionId", this.contribution.contributionId);
        params.append("dismissalReason", this.dismissalReason.trim());

        const res = await request.post(
          "/communityAdmin/dismissContribution",
          params
        );

        if (res.data && res.data.code === 0) {
          alert("作品已驳回");
          this.contribution.auditStatus = 2;
          this.contribution.dismissalReason = this.dismissalReason.trim();
          this.closeDismissModal();
          this.$router.push("/user");
        } else {
          alert("驳回失败: " + (res.data?.message || "未知错误"));
        }
      } catch (error) {
        console.error("驳回失败:", error);
        alert("驳回失败，请稍后重试");
      } finally {
        this.auditLoading = false;
      }
    },

    async blockWork() {
      const ok = window.confirm(
        "确定要封禁这个作品吗？封禁后用户将无法查看该作品。"
      );
      if (!ok) return;

      this.blockLoading = true;

      try {
        const params = new URLSearchParams();
        params.append("contributionId", this.contribution.contributionId);

        const res = await request.post(
          "/communityAdmin/blockContribution",
          params
        );

        if (res.data && res.data.code === 0) {
          alert("作品已封禁");
          this.contribution.status = 1;
        } else {
          alert("封禁失败: " + (res.data?.message || "未知错误"));
        }
      } catch (error) {
        console.error("封禁失败:", error);
        alert("封禁失败，请稍后重试");
      } finally {
        this.blockLoading = false;
      }
    },

    async unblockWork() {
      const ok = window.confirm("确定要解封这个作品吗？");
      if (!ok) return;

      this.blockLoading = true;

      try {
        const params = new URLSearchParams();
        params.append("contributionId", this.contribution.contributionId);

        const res = await request.post(
          "/communityAdmin/unblockContribution",
          params
        );

        if (res.data && res.data.code === 0) {
          alert("作品已解封");
          this.contribution.status = 0;
        } else {
          alert("解封失败: " + (res.data?.message || "未知错误"));
        }
      } catch (error) {
        console.error("解封失败:", error);
        alert("解封失败，请稍后重试");
      } finally {
        this.blockLoading = false;
      }
    },

    getImageUrl(imagePath) {
      if (!imagePath) {
        return 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="400" height="300"%3E%3Crect fill="%23ddd" width="400" height="300"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999"%3E暂无图片%3C/text%3E%3C/svg%3E';
      }

      if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
        return imagePath;
      }

      const baseURL =
        process.env.VUE_APP_API_BASE_URL || "http://localhost:8080";
      const fullPath = imagePath.startsWith("/") ? imagePath : `/${imagePath}`;
      return `${baseURL}${fullPath}`;
    },

    goToAuthorProfile() {
      if (this.contribution.authorId) {
        this.$router.push({
          name: "user-id",
          params: { id: this.contribution.authorId },
        });
      } else {
        console.warn("作者ID不存在，无法跳转");
      }
    },
  },
};
</script>

<style scoped>
.image-view-root {
  background: linear-gradient(135deg, #f5f7fa 0%, #e3e9f0 100%);
  min-height: 100vh;
  position: relative;
}

.main-content {
  padding-top: 2rem;
  padding-bottom: 3rem;
}

.back-button {
  margin-bottom: 1.5rem;
  padding: 10px 20px;
  border-radius: 12px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.back-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.anime-box {
  background: rgba(255, 255, 255, 0.98) !important;
  border: 2px solid rgba(147, 51, 234, 0.1);
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(147, 51, 234, 0.08);
  transition: all 0.3s ease;
  margin-bottom: 1.5rem;
}

.anime-box:hover {
  box-shadow: 0 8px 24px rgba(147, 51, 234, 0.15);
}

/* 模态框样式 */
.image-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.95);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.modal-content {
  max-width: 95%;
  max-height: 95%;
  animation: zoomIn 0.3s ease;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

@keyframes zoomIn {
  from {
    transform: scale(0.8);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

.modal-content img {
  width: auto;
  height: auto;
  max-width: 100%;
  max-height: 90vh;
  border-radius: 8px;
  user-select: none;
}

.close-btn {
  position: absolute;
  top: 24px;
  right: 24px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.3);
  color: #fff;
  padding: 12px 20px;
  border-radius: 12px;
  cursor: pointer;
  font-weight: 600;
  z-index: 2001;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.reset-btn {
  position: absolute;
  top: 24px;
  right: 140px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.3);
  color: #fff;
  padding: 12px 20px;
  border-radius: 12px;
  cursor: pointer;
  font-weight: 600;
  z-index: 2001;
}

.reset-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.zoom-indicator {
  position: absolute;
  top: 24px;
  left: 24px;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.3);
  color: #fff;
  padding: 12px 20px;
  border-radius: 12px;
  font-weight: 700;
  font-size: 18px;
  z-index: 2001;
  user-select: none;
}

/* 驳回模态框 */
.anime-modal .modal-card-head {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.anime-modal .modal-card-title {
  color: white;
  font-weight: 800;
  display: flex;
  align-items: center;
  gap: 8px;
}

.anime-modal .delete {
  background: rgba(255, 255, 255, 0.3);
}

.anime-modal .delete:hover {
  background: rgba(255, 255, 255, 0.5);
}

.anime-input {
  border: 2px solid rgba(147, 51, 234, 0.2);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.anime-input:focus {
  border-color: #a78bfa;
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.1);
}

.anime-button {
  border-radius: 12px;
  font-weight: 600;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

@media (max-width: 768px) {
  .columns {
    display: flex;
    flex-direction: column;
  }

  .column {
    width: 100%;
  }
}
</style>

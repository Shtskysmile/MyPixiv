<template>
  <div class="profile-card card horizontal-card">
    <div class="profile-inner">
      <div class="left-col">
        <figure class="image is-128x128 avatar-figure">
          <img :src="user.avatar || defaultAvatar" alt="avatar" class="is-rounded avatar-img" />
        </figure>
        <div class="profile-actions">
          <button class="button is-primary" @click="$emit('edit')">编辑资料</button>
          <button class="button is-light" @click="$emit('back')">返回首页</button>
        </div>
      </div>

      <div class="center-col">
        <div class="user-name">{{ user.name }}</div>
        <div class="user-role">{{ user.role }}</div>
        <div class="bio">
          <p v-if="user.bio">{{ user.bio }}</p>
          <p v-else class="has-text-grey">还没有简介，快去填写吧～</p>
        </div>
      </div>

      <div class="right-col">
        <div class="profile-stats">
          <div class="stat"><div class="stat-num">128</div><div class="stat-label">关注</div></div>
          <div class="stat"><div class="stat-num">54</div><div class="stat-label">粉丝</div></div>
          <div class="stat"><div class="stat-num">23</div><div class="stat-label">作品</div></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import defaultAvatar from '@/assets/images/avatar.png';

export default {
  name: 'Profile',
  props: {
    user: {
      type: Object,
      required: true,
    }
  },
  computed: {
    defaultAvatar() { return defaultAvatar; },
    // 目前不在组件中使用单独背景，由父组件 User.vue 提供背景
  }
}
</script>

<style scoped>
.profile-card {
  max-width: 1000px;
  margin: 0 auto 18px;
  background: rgba(255,255,255,0.06);
  border-radius: 12px;
  overflow: hidden;
  color: #fff;
  border: 1px solid rgba(255,255,255,0.06);
}
.horizontal-card { padding: 18px; }
.profile-inner { display: flex; align-items: center; gap: 20px; }
.left-col { width: 220px; display:flex; flex-direction:column; align-items:flex-start; gap:12px }
.avatar-figure { margin: 0; }
.avatar-img { width: 128px; height: 128px; object-fit: cover; border: 4px solid rgba(255,255,255,0.06); }
.profile-actions { display:flex; gap:8px }
.center-col { flex: 1; text-align: center; }
.user-name { font-size: 28px; font-weight: 700; color: #fff; }
.user-role { color: rgba(255,255,255,0.75); margin-top: 6px }
.bio { margin-top: 12px; color: rgba(255,255,255,0.92); max-width: 720px; margin-left:auto; margin-right:auto }
.right-col { width: 260px; display:flex; justify-content:center }
.profile-stats { display:flex; gap:36px; align-items:center }
.stat { text-align:center }
.stat-num { font-weight: 700; font-size: 20px; }
.stat-label { font-size: 12px; color: rgba(255,255,255,0.7); margin-top:6px }

@media (max-width: 768px) {
  .cover-figure { height: 110px; }
  .avatar-figure { margin-top: -42px; }
}
</style>
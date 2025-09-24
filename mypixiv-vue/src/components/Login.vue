<template>
  <section class="login-hero" :style="bgStyle">
    <div class="overlay">
      <div class="container">
        <div class="columns is-centered">
          <div class="column is-4">
            <div class="box login-box">
              <h1 class="title has-text-centered">登录 MyPixiv</h1>
              <form @submit.prevent="submit">
                <div class="field">
                  <label class="label">邮箱</label>
                  <div class="control">
                    <input
                      class="input"
                      type="email"
                      v-model="email"
                      placeholder="example@domain.com"
                      required
                    />
                  </div>
                </div>

                <div class="field">
                  <label class="label">密码</label>
                  <div class="control">
                    <input
                      class="input"
                      type="password"
                      v-model="password"
                      placeholder="你的密码"
                      required
                    />
                  </div>
                </div>

                <div class="field">
                  <div class="control">
                    <button
                      class="button is-primary is-fullwidth"
                      type="submit"
                    >
                      登录
                    </button>
                  </div>
                </div>

                <p class="has-text-centered">
                  <a @click.prevent="$router.push('/index')">跳过，查看首页</a>
                </p>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import bg from "@/assets/images/Lincy_swimsuit_Background.jpg";

export default {
  data() {
    return {
      email: "",
      password: "",
    };
  },
  computed: {
    bgStyle() {
      return {
        backgroundImage: `url(${bg})`,
        backgroundSize: "cover",
        backgroundPosition: "center",
      };
    },
  },
  methods: {
    submit() {
      // 这里放登录逻辑，当前用 mock 跳转到首页
      this.$router.push("/index");
    },
  },
};
</script>

<style scoped>
.login-hero {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 50;
}
.overlay {
  width: 100%;
  height: 100%;
  /* 深色遮罩（无模糊效果） */
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-box {
  background: rgba(255, 255, 255, 0.95);
  opacity: 0;
  transform: translateY(20px);
  animation: panelIn 600ms cubic-bezier(.22,.98,.28,1) 200ms forwards;
}

@keyframes panelIn {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>

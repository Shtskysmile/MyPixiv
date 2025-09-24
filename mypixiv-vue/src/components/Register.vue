<template>
  <section class="register-hero" :style="bgStyle">
    <div class="overlay">
      <div class="container">
        <div class="columns is-centered">
          <div class="column is-4">
            <div class="box register-box">
              <h1 class="title has-text-centered">注册 MyPixiv</h1>
              <form @submit.prevent="submit">
                <div class="field">
                  <label class="label">用户名</label>
                  <div class="control">
                    <input class="input" type="text" v-model="username" placeholder="你的用户名" required />
                  </div>
                </div>

                <div class="field">
                  <label class="label">邮箱</label>
                  <div class="control">
                    <input class="input" type="email" v-model="email" placeholder="example@domain.com" required />
                  </div>
                </div>

                <div class="field">
                  <label class="label">密码</label>
                  <div class="control">
                    <input class="input" type="password" v-model="password" placeholder="你的密码" required />
                  </div>
                </div>

                <div class="field">
                  <label class="label">确认密码</label>
                  <div class="control">
                    <input class="input" type="password" v-model="confirmPassword" placeholder="重复输入密码" required />
                  </div>
                </div>

                <div class="field">
                  <div class="control">
                    <button class="button is-primary is-fullwidth" type="submit">注册</button>
                  </div>
                </div>

                <p class="has-text-centered">
                  <a @click.prevent="$router.push('/login')">已有账号？去登录</a>
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
import bg from '@/assets/images/Lincy_swimsuit_Background.jpg'

export default {
  data() {
    return {
      username: '',
      email: '',
      password: '',
      confirmPassword: ''
    }
  },
  computed: {
    bgStyle() {
      return {
        backgroundImage: `url(${bg})`,
        backgroundSize: 'cover',
        backgroundPosition: 'center'
      }
    }
  },
  methods: {
    submit() {
      if (this.password !== this.confirmPassword) {
        alert('两次输入的密码不一致')
        return
      }
      // mock 注册后跳转到登录页
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.register-hero {
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
  background: rgba(0,0,0,0.45);
  display: flex;
  align-items: center;
  justify-content: center;
}
.register-box {
  background: rgba(255,255,255,0.95);
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
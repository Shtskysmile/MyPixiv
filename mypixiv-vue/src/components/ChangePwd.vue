<template>
  <section class="change-pwd" :style="bgStyle">
    <div class="overlay">
      <div class="container">
        <div class="box">
          <h1 class="title is-3">修改密码</h1>

        <div class="field">
          <label class="label">账号</label>
          <div class="control">
            <input class="input" v-model="account" placeholder="请输入账号" />
          </div>
        </div>

        <div v-for="(q, idx) in questions" :key="idx" class="field">
          <label class="label">密保问题 {{ idx + 1 }}</label>
          <div class="control">
            <input class="input" v-model="answers[idx]" :placeholder="q" />
          </div>
        </div>

        <div class="field">
          <label class="label">新密码</label>
          <div class="control">
            <input class="input" type="password" v-model="newPwd" placeholder="新密码" />
          </div>
        </div>

        <div class="field">
          <label class="label">确认新密码</label>
          <div class="control">
            <input class="input" type="password" v-model="confirmPwd" placeholder="再次输入新密码" />
          </div>
        </div>

        <div class="field">
          <div class="control">
            <button class="button is-primary" @click="submit">提交修改</button>
          </div>
        </div>

        <p class="help is-danger" v-if="error">{{ error }}</p>
        <p class="help is-success" v-if="success">{{ success }}</p>
      </div>
    </div>
  </div>
</section>
</template>

<script>
import axios from 'axios';
import bgImg from '@/assets/images/Moonshadow_CyberLeader.jpg'

// 浏览器端计算 sha256，返回 hex 字符串
async function sha256Hex(message) {
  const enc = new TextEncoder();
  const data = enc.encode(message);
  const hashBuffer = await crypto.subtle.digest('SHA-256', data);
  const hashArray = Array.from(new Uint8Array(hashBuffer));
  return hashArray.map((b) => b.toString(16).padStart(2, '0')).join('');
}

export default {
  name: 'ChangePwd',
  data() {
    return {
      account: '',
      questions: [
        '你最喜欢的宠物名字是什么？',
        '你的母亲的娘家姓氏？',
        '你出生的城市？'
      ],
      answers: ['', '', ''],
      newPwd: '',
      confirmPwd: '',
      error: '',
      success: ''
    };
  },
  computed: {
    bgStyle() {
      return {
        backgroundImage: `url(${bgImg})`,
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        position: 'fixed',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        zIndex: 50
      }
    }
  },
  methods: {
    async submit() {
      this.error = '';
      this.success = '';

      if (!this.account.trim()) {
        this.error = '账号不能为空';
        return;
      }
      for (let i = 0; i < 3; i++) {
        if (!this.answers[i] || !this.answers[i].trim()) {
          this.error = `请填写第 ${i + 1} 个密保答案`;
          return;
        }
      }
      if (!this.newPwd) {
        this.error = '请输入新密码';
        return;
      }
      if (this.newPwd !== this.confirmPwd) {
        this.error = '两次输入的密码不一致';
        return;
      }

      try {
        // 计算新密码的 SHA-256 哈希（hex）
        const pwdHash = await sha256Hex(this.newPwd);

        // 发送 account、answers（明文）和 pwdHash 到后端
        const payload = {
          account: this.account.trim(),
          answers: this.answers.map((a) => a.trim()),
          pwdHash
        };

        const res = await axios.post('/api/changepwd', payload);
        if (res && res.data && res.data.success) {
          this.success = '密码修改请求已发送（请查看邮箱或等待服务端确认）';
          this.newPwd = '';
          this.confirmPwd = '';
        } else {
          this.error = (res && res.data && res.data.message) || '修改密码失败';
        }
      } catch (e) {
        this.error = e.response && e.response.data && e.response.data.message ? e.response.data.message : String(e);
      }
    }
  }
};
</script>

<style scoped>
.change-pwd {
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
.container { z-index: 51; }
</style>

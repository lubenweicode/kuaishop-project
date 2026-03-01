<template>
  <div class="login-page">
    <div class="login-card">
      <h2>登录</h2>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label>用户名</label>
          <input v-model="form.username" type="text" placeholder="请输入用户名" required />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input v-model="form.password" type="password" placeholder="请输入密码" required />
        </div>
        <div class="form-group">
          <label>
            <input v-model="form.rememberMe" type="checkbox" />
            记住密码
          </label>
        </div>
        <button type="submit" :disabled="loading" class="login-btn">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>
      <p class="signup-link">
        没有账号？<router-link to="/register">立即注册</router-link>
      </p>
      <div v-if="errorMsg" class="error-msg">{{ errorMsg }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const errorMsg = ref('')

const form = reactive({
  username: '',
  password: '',
  rememberMe: false
})

const handleLogin = async () => {
  if (!form.username || !form.password) {
    errorMsg.value = '请填写用户名和密码'
    return
  }

  loading.value = true
  try {
    await userStore.login({
      username: form.username,
      password: form.password,
      rememberMe: form.rememberMe
    })
    errorMsg.value = ''
    router.push('/')
  } catch (error: any) {
    errorMsg.value = error.message || '登录失败，请重试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
}

.login-card {
  background: #fff;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.login-card h2 {
  text-align: center;
  margin-bottom: 30px;
  font-size: 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
}

.form-group input[type='text'],
.form-group input[type='password'] {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-group input[type='text']:focus,
.form-group input[type='password']:focus {
  outline: none;
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
}

.form-group input[type='checkbox'] {
  margin-right: 8px;
}

.login-btn {
  width: 100%;
  padding: 12px;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.3s;
}

.login-btn:hover:not(:disabled) {
  background: #66b1ff;
}

.login-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.signup-link {
  text-align: center;
  margin-top: 20px;
  color: #666;
}

.signup-link a {
  color: #409eff;
  text-decoration: none;
}

.signup-link a:hover {
  text-decoration: underline;
}

.error-msg {
  margin-top: 15px;
  padding: 10px;
  background: #fde7e7;
  border: 1px solid #f5222d;
  color: #f5222d;
  border-radius: 4px;
  font-size: 14px;
}
</style>

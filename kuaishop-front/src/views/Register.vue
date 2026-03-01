<template>
  <div class="register-page">
    <div class="register-card">
      <h2>注册</h2>
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label>用户名</label>
          <input v-model="form.username" type="text" placeholder="请输入用户名（3-20字符）" required />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input v-model="form.password" type="password" placeholder="请输入密码（6-20字符）" required />
        </div>
        <div class="form-group">
          <label>确认密码</label>
          <input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" required />
        </div>
        <div class="form-group">
          <label>手机号（可选）</label>
          <input v-model="form.phone" type="tel" placeholder="请输入手机号" />
        </div>
        <div class="form-group">
          <label>邮箱（可选）</label>
          <input v-model="form.email" type="email" placeholder="请输入邮箱" />
        </div>
        <button type="submit" :disabled="loading" class="register-btn">
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </form>
      <p class="login-link">
        已有账号？<router-link to="/login">立即登录</router-link>
      </p>
      <div v-if="errorMsg" class="error-msg">{{ errorMsg }}</div>
      <div v-if="successMsg" class="success-msg">{{ successMsg }}</div>
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
const successMsg = ref('')

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  phone: '',
  email: ''
})

const handleRegister = async () => {
  if (!form.username || !form.password || !form.confirmPassword) {
    errorMsg.value = '请填写必填字段'
    return
  }

  if (form.password !== form.confirmPassword) {
    errorMsg.value = '两次密码输入不一致'
    return
  }

  if (form.password.length < 6 || form.password.length > 20) {
    errorMsg.value = '密码长度为6-20字符'
    return
  }

  loading.value = true
  try {
    await userStore.register({
      username: form.username,
      password: form.password,
      phone: form.phone || undefined,
      email: form.email || undefined
    })
    successMsg.value = '注册成功，请登录'
    errorMsg.value = ''
    setTimeout(() => {
      router.push('/login')
    }, 1500)
  } catch (error: any) {
    errorMsg.value = error.message || '注册失败，请重试'
    successMsg.value = ''
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
  padding: 20px;
}

.register-card {
  background: #fff;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.register-card h2 {
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

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
}

.register-btn {
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

.register-btn:hover:not(:disabled) {
  background: #66b1ff;
}

.register-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  color: #666;
}

.login-link a {
  color: #409eff;
  text-decoration: none;
}

.login-link a:hover {
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

.success-msg {
  margin-top: 15px;
  padding: 10px;
  background: #f6ffed;
  border: 1px solid #52c41a;
  color: #52c41a;
  border-radius: 4px;
  font-size: 14px;
}
</style>

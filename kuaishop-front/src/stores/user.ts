import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as authApi from '@/api/auth'

interface UserInfo {
  id: number
  username: string
  phone: string
  email: string
  avatar: string
  createTime: string
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)
  const loading = ref(false)

  // 核心修复1：暴露userId计算属性（兼容userInfo为空的情况）
  const userId = computed(() => {
    // 优先取userInfo.id，为空则返回0或null（根据后端需求）
    return userInfo.value?.id || 0
  })

  // 核心修复2：完善登录状态判断（token存在 + userInfo不为空）
  const isLoggedIn = computed(() => {
    return !!token.value && !!userInfo.value
  })

  // 注册
  const register = async (data: {
    username: string
    password: string
    phone?: string
    email?: string
  }) => {
    loading.value = true
    try {
      const result = await authApi.register(data)
      return result
    } finally {
      loading.value = false
    }
  }

  // 登录（优化：登录成功后自动加载完整用户信息）
  const login = async (data: {
    username: string
    password: string
    rememberMe?: boolean
  }) => {
    loading.value = true
    try {
      const result = await authApi.login(data)
      // 1. 保存token
      token.value = result.token
      localStorage.setItem('token', result.token)
      // 2. 自动加载完整用户信息（确保userInfo有值）
      if (result.token) {
        await fetchUserInfo()
      } else {
        userInfo.value = result.userInfo || null
      }
      return result
    } finally {
      loading.value = false
    }
  }

  // 获取用户信息（增加错误处理，token失效时自动登出）
  const fetchUserInfo = async () => {
    // 无token时直接返回
    if (!token.value) {
      return null
    }
    loading.value = true
    try {
      const result = await authApi.getMe()
      userInfo.value = result
      return result
    } catch (error) {
      console.error('获取用户信息失败:', error)
      // token失效时自动登出
      logout()
      return null
    } finally {
      loading.value = false
    }
  }

  // 核心修复3：初始化用户信息（页面刷新后自动恢复）
  const initUser = async () => {
    // 有token但无userInfo时，自动加载用户信息
    if (token.value && !userInfo.value) {
      await fetchUserInfo()
    }
  }

  // 登出（优化：清空所有状态）
  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    // 忽略登出接口的错误（避免影响用户体验）
    authApi.logout().catch((err) => console.warn('登出接口调用失败:', err))
  }

  return {
    token,
    userInfo,
    loading,
    userId, // 核心修复：导出userId
    isLoggedIn,
    register,
    login,
    fetchUserInfo,
    initUser, // 导出初始化方法
    logout
  }
})
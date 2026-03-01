import service from '@/utils/request'

// 用户注册
export const register = (data: {
  username: string
  password: string
  phone?: string
  email?: string
}) => {
  return service.post('/auth/register', data)
}

// 用户登录
export const login = (data: {
  username: string
  password: string
  rememberMe?: boolean
}) => {
  return service.post('/auth/login', data)
}

// 获取当前用户信息
export const getMe = () => {
  return service.get('/auth/me')
}

// 用户登出
export const logout = () => {
  return service.post('/auth/logout')
}

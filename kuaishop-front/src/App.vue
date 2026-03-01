<script setup lang="ts">
import { onMounted } from 'vue'
import { RouterView } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'

const userStore = useUserStore()
const cartStore = useCartStore()

// 核心修复：给onMounted的回调函数添加async关键字
onMounted(async () => { // 这里加async
  // 有token时才加载用户信息和购物车
  if (userStore.token) {
    try {
      // 先加载用户信息（await 现在合法了）
      await userStore.fetchUserInfo()
      // 用户信息加载完成后，再加载购物车
      if (userStore.userId) {
        await cartStore.fetchCartList()
      }
    } catch (error) {
      console.error('初始化用户数据失败:', error)
      // 加载失败时清空token（比如token过期）
      userStore.logout()
    }
  }
})
</script>

<template>
  <div class="layout">
    <!-- 导航栏 -->
    <header class="navbar">
      <div class="container">
        <div class="logo">
          <router-link to="/">🛒 QuickShop</router-link>
        </div>
        <nav class="nav">
          <router-link to="/">首页</router-link>
          <router-link to="/products">商品</router-link>
          <router-link to="/cart">购物车 ({{ cartStore.totalItems }})</router-link>
          <router-link to="/seckill">秒杀</router-link>
          <router-link to="/pay">支付</router-link>
          <router-link to="/order-status-ws">订单状态</router-link>
          <router-link v-if="userStore.isLoggedIn" to="/orders">订单</router-link>
          <div v-if="userStore.isLoggedIn" class="user-menu">
            <span>{{ userStore.userInfo?.username }}</span>
            <button @click="userStore.logout" class="logout-btn">退出</button>
          </div>
          <div v-else class="auth-links">
            <router-link to="/login">登录</router-link>
            <router-link to="/register">注册</router-link>
          </div>
        </nav>
      </div>
    </header>

    <!-- 主内容 -->
    <main class="main-content">
      <RouterView />
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <p>&copy; 2024 QuickShop. All rights reserved.</p>
    </footer>
  </div>
</template>


<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--color-white);
}

.navbar {
  background-color: var(--color-white);
  border-bottom: var(--border-light);
  position: sticky;
  top: 0;
  z-index: 100;
  padding: var(--spacing-lg) 0;
  box-shadow: var(--shadow-sm);
}

.navbar .container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--spacing-xl);
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: auto;
  gap: var(--spacing-xl);
}

/* Logo - 品牌形象 */
.logo a {
  font-size: var(--font-size-2xl);
  font-weight: 600;
  text-decoration: none;
  color: var(--color-black);
  letter-spacing: -0.5px;
  transition: all var(--transition-fast);
}

.logo a:hover {
  color: var(--color-beige);
  transform: scale(0.98);
}

/* 导航 */
.nav {
  display: flex;
  gap: var(--spacing-2xl);
  align-items: center;
  flex: 1;
  justify-content: flex-end;
}

.nav a {
  text-decoration: none;
  color: var(--color-black);
  font-size: var(--font-size-base);
  transition: all var(--transition-fast);
  position: relative;
  padding: var(--spacing-sm) 0;
  font-weight: 500;
}

.nav a::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 1px;
  background-color: var(--color-gold);
  transition: width var(--transition-fast);
}

.nav a:hover::after,
.nav a.router-link-active::after {
  width: 100%;
}

/* 用户菜单和认证链接 */
.user-menu,
.auth-links {
  display: flex;
  gap: var(--spacing-lg);
  align-items: center;
  padding-left: var(--spacing-lg);
  border-left: var(--border-light);
}

.user-menu span {
  color: var(--color-gray-dark);
  font-size: var(--font-size-sm);
  font-weight: 500;
}

.logout-btn {
  padding: 6px var(--spacing-md);
  background-color: transparent;
  border: 1px solid var(--color-black);
  color: var(--color-black);
  border-radius: var(--radius-capsule);
  cursor: pointer;
  font-size: var(--font-size-sm);
  transaction: all var(--transition-fast);
  font-weight: 500;
}

.logout-btn:hover {
  background-color: var(--color-black);
  color: var(--color-white);
  transform: translateY(-1px);
}

.logout-btn:active {
  transform: translateY(0);
}

/* 主内容区域 - 极简留白 */
.main-content {
  flex: 1;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
  padding: var(--spacing-3xl) var(--spacing-xl);
}

/* 页脚 */
.footer {
  background-color: var(--color-gray-light);
  text-align: center;
  padding: var(--spacing-2xl);
  color: var(--color-gray-dark);
  margin-top: var(--spacing-3xl);
  border-top: var(--border-light);
  font-size: var(--font-size-sm);
}

.footer p {
  margin: 0;
  letter-spacing: 0.5px;
  font-weight: 400;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .navbar .container {
    flex-direction: column;
    align-items: stretch;
    padding: var(--spacing-lg);
    gap: var(--spacing-lg);
  }

  .logo {
    text-align: center;
  }

  .nav {
    flex-direction: column;
    gap: var(--spacing-md);
    width: 100%;
    justify-content: flex-start;
  }

  .nav a {
    width: 100%;
    padding: var(--spacing-md);
    text-align: center;
    border-radius: var(--radius-capsule);
    background-color: var(--color-gray-light);
    transition: all var(--transition-fast);
  }

  .nav a:hover,
  .nav a.router-link-active {
    background-color: var(--color-black);
    color: var(--color-white);
  }

  .nav a::after {
    display: none;
  }

  .user-menu,
  .auth-links {
    border-left: none;
    padding-left: 0;
    width: 100%;
  }

  .main-content {
    padding: var(--spacing-2xl) var(--spacing-lg);
  }
}
</style>

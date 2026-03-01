import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import Products from '@/views/Products.vue'
import ProductDetail from '@/views/ProductDetail.vue'
import Cart from '@/views/Cart.vue'
import Orders from '@/views/Orders.vue'
import Pay from '@/views/Pay.vue'
import Seckill from '@/views/Seckill.vue'
import OrderStatusWS from '@/views/OrderStatusWS.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: Home,
    },
    {
      path: '/login',
      name: 'Login',
      component: Login,
    },
    {
      path: '/register',
      name: 'Register',
      component: Register,
    },
    {
      path: '/products',
      name: 'Products',
      component: Products,
    },
    {
      path: '/products/:id',
      name: 'ProductDetail',
      component: ProductDetail,
    },
    {
      path: '/cart',
      name: 'Cart',
      component: Cart,
    },
    {
      path: '/orders',
      name: 'Orders',
      component: Orders,
    },
    {
      path: '/pay',
      name: 'Pay',
      component: Pay,
    },
    {
      path: '/seckill',
      name: 'Seckill',
      component: Seckill,
    },
    {
      path: '/order-status-ws',
      name: 'OrderStatusWS',
      component: OrderStatusWS,
    },
  ],
})

export default router

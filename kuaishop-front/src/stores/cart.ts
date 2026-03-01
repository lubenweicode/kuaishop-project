import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as cartApi from '@/api/cart'

export interface CartItem {
  id: number
  productId: number
  productName: string
  productImage: string
  price: number
  quantity: number
  stock: number
  selected: boolean
  totalPrice: number
}

export const useCartStore = defineStore('cart', () => {
  const items = ref<CartItem[]>([])
  const loading = ref(false)

  // 计算属性
  const totalItems = computed(() => {
    return items.value.reduce((sum, item) => sum + item.quantity, 0)
  })

  const totalPrice = computed(() => {
    return items.value.reduce((sum, item) => {
      return sum + (item.selected ? item.totalPrice : 0)
    }, 0)
  })

  const selectedItems = computed(() => {
    return items.value.filter(item => item.selected)
  })

  // 获取购物车列表
  const fetchCartList = async () => {
    loading.value = true
    try {
      const result = await cartApi.getCartList()
      items.value = result
      return result
    } finally {
      loading.value = false
    }
  }

  // 添加到购物车
  const addToCart = async (productId: number, quantity: number) => {
    loading.value = true
    try {
      await cartApi.addToCart({ productId, quantity })
      return await fetchCartList()
    } finally {
      loading.value = false
    }
  }

  // 更新数量
  const updateQuantity = async (productId: number, quantity: number) => {
    loading.value = true
    try {
      await cartApi.updateCartItem(productId, { quantity })
      return await fetchCartList()
    } finally {
      loading.value = false
    }
  }

  // 删除商品
  const removeItem = async (productId: number) => {
    loading.value = true
    try {
      await cartApi.removeCartItem(productId)
      return await fetchCartList()
    } finally {
      loading.value = false
    }
  }

  // 切换选择状态
  const toggleSelect = (productId: number) => {
    const item = items.value.find(i => i.productId === productId)
    if (item) {
      item.selected = !item.selected
    }
  }

  // 全选
  const selectAll = () => {
    items.value.forEach(item => {
      item.selected = true
    })
  }

  // 取消全选
  const unselectAll = () => {
    items.value.forEach(item => {
      item.selected = false
    })
  }

  // 清空购物车
  const clearCart = async () => {
    loading.value = true
    try {
      await cartApi.clearCart()
      items.value = []
    } finally {
      loading.value = false
    }
  }

  return {
    items,
    loading,
    totalItems,
    totalPrice,
    selectedItems,
    fetchCartList,
    addToCart,
    updateQuantity,
    removeItem,
    toggleSelect,
    selectAll,
    unselectAll,
    clearCart
  }
})

import service from '@/utils/request'

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

// 添加商品到购物车
export const addToCart = (data: {
  productId: number
  quantity: number
}) => {
  return service.post('/cart/items', data)
}

// 获取购物车列表
export const getCartList = () => {
  return service.get<CartItem[]>('/cart/items')
}

// 更新购物车商品数量
export const updateCartItem = (productId: number, data: {
  quantity: number
}) => {
  return service.put(`/cart/items/${productId}`, data)
}

// 删除购物车商品
export const removeCartItem = (productId: number) => {
  return service.delete(`/cart/items/${productId}`)
}

// 清空购物车
export const clearCart = () => {
  return service.delete('/cart/items')
}

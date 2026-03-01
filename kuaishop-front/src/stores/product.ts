import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as productApi from '@/api/product'

export interface Product {
  id: number
  name: string
  description: string
  price: number
  originalPrice: number
  stock: number
  sales: number
  imageUrl: string
  categoryId: number
  categoryName: string
  status: number
  createTime: string
}

export interface ProductDetail extends Product {
  detail: string
  images: string[]
  specifications: Array<{
    name: string
    value: string
  }>
}

export const useProductStore = defineStore('product', () => {
  const products = ref<Product[]>([])
  const productDetail = ref<ProductDetail | null>(null)
  const categories = ref<any[]>([])
  const loading = ref(false)
  const total = ref(0)
  const pages = ref(0)
  const current = ref(1)

  // 获取商品列表
  const fetchProductList = async (params: {
    page?: number
    size?: number
    keyword?: string
    categoryId?: number
    sortBy?: string
    order?: string
  }) => {
    loading.value = true
    try {
      const result = await productApi.getProductList(params)
      products.value = result.records
      total.value = result.total
      pages.value = result.pages
      current.value = result.current
      return result
    } finally {
      loading.value = false
    }
  }

  // 获取商品详情
  const fetchProductDetail = async (id: number) => {
    loading.value = true
    try {
      const result = await productApi.getProductDetail(id)
      productDetail.value = result
      return result
    } finally {
      loading.value = false
    }
  }

  // 获取分类
  const fetchCategories = async () => {
    loading.value = true
    try {
      const result = await productApi.getCategories()
      categories.value = result
      return result
    } finally {
      loading.value = false
    }
  }

  // 搜索商品
  const searchProducts = (keyword: string) => {
    return fetchProductList({
      keyword,
      page: 1
    })
  }

  return {
    products,
    productDetail,
    categories,
    loading,
    total,
    pages,
    current,
    fetchProductList,
    fetchProductDetail,
    fetchCategories,
    searchProducts
  }
})

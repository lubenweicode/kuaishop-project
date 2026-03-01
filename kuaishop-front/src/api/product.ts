import service from '@/utils/request'

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

export interface ProductListResponse {
  total: number
  pages: number
  current: number
  size: number
  records: Product[]
}

// 获取商品列表
export const getProductList = (params: {
  page?: number
  size?: number
  keyword?: string
  categoryId?: number
  sortBy?: string
  order?: string
}) => {
  return service.get<ProductListResponse>('/products', { params })
}

// 获取商品详情
export const getProductDetail = (id: number) => {
  return service.get<ProductDetail>(`/products/${id}`)
}

// 获取商品分类
export const getCategories = () => {
  return service.get('/categories')
}

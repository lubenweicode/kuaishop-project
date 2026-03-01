import service from '@/utils/request'

/**
 * 订单商品项类型（适配后端返回格式）
 */
export interface OrderItem {
  productId: number | string  // 兼容后端返回的字符串/数字类型
  productName: string
  productImage: string | null // 允许为空
  price: number
  quantity: number
  totalPrice: number | null   // 后端返回为空，前端计算
}

/**
 * 订单主类型（精准匹配后端返回字段）
 */
export interface Order {
  orderNo: string             // 订单号（核心标识）
  userId: number | string     // 用户ID
  username: string | null     // 用户名（允许为空）
  totalAmount: number | null  // 总金额（允许为空）
  payAmount: number           // 支付金额（核心金额字段）
  status: number              // 订单状态：0-待付款 1-待发货 2-待收货 3-已完成 4-已取消
  statusText: string | null   // 状态文本（允许为空）
  info: string                // 商品信息JSON字符串
  remark: string | null       // 备注
  receiverName: string | null // 收件人姓名
  receiverPhone: string | null// 收件人电话
  receiverAddress: string | null// 收件人地址
  createTime: string          // 创建时间（格式：2026-02-15 10:11:13）
  payUrl: string | null       // 支付链接
  paymentTime: string | null  // 支付时间
  items: OrderItem[]          // 订单商品列表
  logistics: any[] | null     // 物流信息
}

/**
 * 订单列表响应类型（精准匹配后端分页结构）
 */
export interface OrderListResponse {
  total: string | number      // 总条数（兼容字符串/数字）
  pages: string | number      // 总页数
  current: number             // 当前页
  size: number                // 页大小
  records: Order[]            // 订单列表
}

/**
 * 后端统一响应格式
 */
export interface ApiResponse<T = any> {
  code: number                // 状态码：200成功
  msg: string                 // 提示信息
  data: T                     // 响应数据
  timestamp: string           // 时间戳
}

/**
 * 创建订单
 * @param data 创建订单参数
 * @returns 订单创建结果
 */
export const createOrder = (data: {
  cartItemIds?: number[]
  items?: Array<{
    productId: number
    quantity: number
  }>
  addressId: number
  remark?: string
  couponId?: number
}) => {
  return service.post<ApiResponse<Order>>('/orders', data)
}

/**
 * 获取订单列表
 * @param params 查询参数
 * @returns 订单列表分页数据
 */
export const getOrderList = (params: {
  userId: number | string     // 用户ID（必传）
  pageNum?: number            // 页码，默认1
  pageSize?: number           // 页大小，默认10
  status?: number             // 订单状态：0-待付款 1-待发货 2-待收货 3-已完成 4-已取消 5-全部
}) => {
  // 设置默认参数
  const queryParams = {
    pageNum: 1,
    pageSize: 10,
    status: 5,
    ...params
  }
  return service.get<ApiResponse<OrderListResponse>>('/orders', { params: queryParams })
}

/**
 * 获取订单详情
 * @param orderNo 订单号
 * @returns 订单详情
 */
export const getOrderDetail = (orderNo: string) => {
  return service.get<ApiResponse<Order>>(`/orders/${orderNo}`)
}

/**
 * 取消订单
 * @param orderNo 订单号
 * @param data 取消原因等参数
 * @returns 取消结果
 */
export const cancelOrder = (orderNo: string, data?: {
  reason?: string
}) => {
  return service.put<ApiResponse<string>>(`/orders/${orderNo}/cancel`, data)
}

/**
 * 删除订单
 * @param orderNo 订单号
 * @returns 删除结果
 */
export const deleteOrder = (orderNo: string) => {
  return service.delete<ApiResponse<string>>(`/orders/${orderNo}`)
}
import service from '@/utils/request'

export interface Address {
  id: number
  receiverName: string
  receiverPhone: string
  province: string
  city: string
  district: string
  detail: string
  isDefault: boolean
  fullAddress: string
}

// 添加收货地址
export const addAddress = (data: {
  receiverName: string
  receiverPhone: string
  province: string
  city: string
  district: string
  detail: string
  isDefault?: boolean
}) => {
  return service.post('/addresses', data)
}

// 获取地址列表
export const getAddressList = () => {
  return service.get<Address[]>('/addresses')
}

// 更新地址
export const updateAddress = (id: number, data: Partial<Omit<Address, 'id'>>) => {
  return service.put(`/addresses/${id}`, data)
}

// 删除地址
export const deleteAddress = (id: number) => {
  return service.delete(`/addresses/${id}`)
}

// 设置默认地址
export const setDefaultAddress = (id: number) => {
  return service.put(`/addresses/${id}/default`)
}

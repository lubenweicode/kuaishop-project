import request from '../utils/request';

// 支付相关API
export const payOrder = (data: { orderNo: string; payType: number }) => {
  return request.post('/payment/pay', data);
};

export const getPayStatus = (orderNo: string) => {
  return request.get('/payment/status', { params: { orderNo } });
};

// 支付回调（一般由后端处理，前端无需调用）

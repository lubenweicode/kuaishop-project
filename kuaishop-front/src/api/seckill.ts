import request from '../utils/request';

// 秒杀活动列表
export const getSeckillActivities = (params?: { status?: number }) => {
  return request.get('/seckill/activities', { params });
};

// 秒杀下单
export const seckillOrder = (data: { activityId: number; productId: number; quantity?: number }) => {
  return request.post('/seckill/order', data);
};

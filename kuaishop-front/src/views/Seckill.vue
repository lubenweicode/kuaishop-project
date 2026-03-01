<template>
  <div class="seckill-container">
    <h2>秒杀活动</h2>
    <div v-if="activities.length">
      <div v-for="activity in activities" :key="activity.activityId" class="activity">
        <h3>{{ activity.activityName }} <span>({{ activity.statusText }})</span></h3>
        <div>时间：
          {{ activity.startTime ? activity.startTime : '待定' }} ~ {{ activity.endTime ? activity.endTime : '待定' }}
        </div>
        <div v-for="product in activity.products" :key="product.id" class="product">
          <div>{{ product.productName }}</div>
          <div>秒杀价：￥{{ product.seckillPrice }} 原价：￥{{ product.originalPrice }}</div>
          <div>库存：{{ product.totalStock }} 已售：{{ product.sold }} 限购：{{ product.limitPerUser }}件</div>
          <button @click="seckill(activity.activityId, product.productId)">立即秒杀</button>
        </div>
      </div>
    </div>
    <div v-else>
      <p>暂无秒杀活动</p>
    </div>
    <div v-if="orderResult">
      <h4>秒杀结果</h4>
      <div>订单号：{{ orderResult.orderNo }}</div>
      <div>商品：{{ orderResult.productName }}</div>
      <div>支付金额：￥{{ orderResult.payAmount }}</div>
      <a :href="orderResult.payUrl" target="_blank">去支付</a>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getSeckillActivities, seckillOrder } from '../api/seckill';

const activities = ref<any[]>([]);
const orderResult = ref<any>(null);

onMounted(async () => {
  const res = await getSeckillActivities({ page: 1 });
  activities.value = res || [];
});

const seckill = async (activityId: number, productId: number) => {
  try {
    const res = await seckillOrder({ activityId, productId, quantity: 1 });
    // 响应拦截器已返回 data 字段，直接赋值
    orderResult.value = res;
  } catch (err: any) {
    alert(err.message || '秒杀失败');
  }
};
</script>

<style scoped>
.seckill-container {
  max-width: 600px;
  margin: 40px auto;
  padding: 24px;
  border: 1px solid #eee;
  border-radius: 8px;
  background: #fff;
}
.activity {
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 1px dashed #ccc;
}
.product {
  margin: 12px 0;
  padding: 8px;
  background: #f9f9f9;
  border-radius: 4px;
}
button {
  margin-top: 8px;
}
</style>

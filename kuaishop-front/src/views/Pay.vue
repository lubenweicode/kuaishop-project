<template>
  <div class="pay-container">
    <h2>订单支付</h2>
    <div v-if="orderNo">
      <div>订单号：{{ orderNo }}</div>
      <div>支付金额：{{ payAmount }}</div>
      <div>
        <button @click="pay(1)">微信支付</button>
        <button @click="pay(2)">支付宝支付</button>
      </div>
      <div v-if="payUrl">
        <p>请扫码支付：</p>
        <img :src="qrCode" alt="支付二维码" v-if="qrCode" style="width:200px;height:200px;" />
        <a :href="payUrl" target="_blank">支付链接</a>
      </div>
      <div v-if="payStatus !== null">
        <p>支付状态：{{ payStatusText }}</p>
      </div>
    </div>
    <div v-else>
      <p>无订单信息</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { payOrder, getPayStatus } from '../api/payment';

const orderNo = ref('');
const payAmount = ref(0);
const payUrl = ref('');
const qrCode = ref('');
const payStatus = ref<number|null>(null);
const payStatusText = ref('');

// 假设通过路由参数传递订单号和金额
import { useRoute } from 'vue-router';
const route = useRoute();
orderNo.value = route.query.orderNo as string || '';
payAmount.value = Number(route.query.payAmount) || 0;

const pay = async (payType: number) => {
  const res = await payOrder({ orderNo: orderNo.value, payType });
  if (res.data.code === 200) {
    payUrl.value = res.data.data.payUrl;
    qrCode.value = res.data.data.qrCode || '';
    // 可轮询支付状态
    pollPayStatus();
  }
};

const pollPayStatus = () => {
  const timer = setInterval(async () => {
    const res = await getPayStatus(orderNo.value);
    if (res.data.code === 200) {
      payStatus.value = res.data.data.payStatus;
      payStatusText.value =
        payStatus.value === 1 ? '支付成功' : payStatus.value === 0 ? '未支付' : '支付失败';
      if (payStatus.value === 1 || payStatus.value === 2) {
        clearInterval(timer);
      }
    }
  }, 2000);
};
</script>

<style scoped>
.pay-container {
  max-width: 400px;
  margin: 40px auto;
  padding: 24px;
  border: 1px solid #eee;
  border-radius: 8px;
  background: #fff;
}
button {
  margin-right: 12px;
}
</style>

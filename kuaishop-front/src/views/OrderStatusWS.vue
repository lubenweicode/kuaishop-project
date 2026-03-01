<template>
  <div class="ws-container">
    <h2>实时订单状态</h2>
    <div v-if="connected">
      <p>WebSocket已连接</p>
      <button @click="sendPing">发送心跳</button>
    </div>
    <div v-else>
      <p>未连接</p>
    </div>
    <div v-if="orderUpdate">
      <h4>订单状态更新</h4>
      <div>订单号：{{ orderUpdate.orderNo }}</div>
      <div>状态：{{ orderUpdate.statusText }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { QuickShopWebSocket } from '../api/websocket';

const token = localStorage.getItem('token') || '';
const ws = ref<QuickShopWebSocket|null>(null);
const connected = ref(false);
const orderUpdate = ref<any>(null);

onMounted(() => {
  ws.value = new QuickShopWebSocket(token);
  ws.value.connect();
  connected.value = true;
  ws.value.onMessage((msg) => {
    if (msg.type === 'ORDER_UPDATE') {
      orderUpdate.value = msg.data;
    }
  });
});

onUnmounted(() => {
  ws.value?.close();
  connected.value = false;
});

const sendPing = () => {
  ws.value?.sendPing();
};
</script>

<style scoped>
.ws-container {
  max-width: 400px;
  margin: 40px auto;
  padding: 24px;
  border: 1px solid #eee;
  border-radius: 8px;
  background: #fff;
}
button {
  margin-top: 12px;
}
</style>

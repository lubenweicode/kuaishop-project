<template>
  <div class="orders-container">
    <h2>我的订单</h2>
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="orders.length === 0" class="empty-orders">
      <p>暂无订单</p>
      <router-link to="/products" class="back-btn">继续购物</router-link>
    </div>
    <div v-else class="orders-list">
      <!-- 使用orderNo作为唯一标识 -->
      <div v-for="order in orders" :key="order.orderNo" class="order-card">
        <div class="order-header">
          <div class="order-info">
            <h3>订单 #{{ order.orderNo || '未知订单号' }}</h3>
            <p class="order-date">{{ formatDate(order.createTime) }}</p>
          </div>
          <!-- 适配后端status字段，增加空值判断 -->
          <div class="order-status" :class="getStatusClass(order.status || 0)">
            {{ getStatusText(order.status || 0) }}
          </div>
        </div>
        <table class="order-items">
          <thead>
            <tr>
              <th>商品</th>
              <th>价格</th>
              <th>数量</th>
              <th>小计</th>
            </tr>
          </thead>
          <tbody>
            <!-- 增加items空值判断，避免遍历undefined -->
            <tr v-for="(item, index) in (order.items || [])" :key="index">
              <td>{{ item.productName || '未知商品' }}</td>
              <!-- 使用安全的格式化函数 -->
              <td>¥{{ formatPrice(item.price) }}</td>
              <td>{{ item.quantity || 0 }}</td>
              <!-- 小计：先校验再计算，避免NaN -->
              <td>¥{{ formatPrice(calcItemTotal(item.price, item.quantity)) }}</td>
            </tr>
          </tbody>
        </table>
        <div class="order-footer">
          <!-- 总金额：使用安全格式化 -->
          <span class="order-total">总金额: ¥{{ formatPrice(order.payAmount) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getOrderList } from '@/api/order'

const userStore = useUserStore()
const orders = ref<any[]>([])
const loading = ref<boolean>(false)

/**
 * 安全格式化价格（核心修复：处理null/undefined/字符串）
 * @param price 价格（可能是null/undefined/字符串/数字）
 * @returns 格式化后的价格字符串（保留2位小数）
 */
const formatPrice = (price: number | string | null | undefined): string => {
  // 空值直接返回0.00
  if (price === null || price === undefined || price === '') {
    return '0.00'
  }
  // 转换为数字
  const num = Number(price)
  // 非数字返回0.00
  if (isNaN(num)) {
    return '0.00'
  }
  // 保留2位小数
  return num.toFixed(2)
}

/**
 * 计算商品小计（价格*数量）
 * @param price 单价
 * @param quantity 数量
 * @returns 小计金额（数字）
 */
const calcItemTotal = (price: number | string | null | undefined, quantity: number | string | null | undefined): number => {
  const priceNum = Number(price) || 0
  const qtyNum = Number(quantity) || 0
  return priceNum * qtyNum
}

/**
 * 格式化日期（兼容后端返回的时间格式）
 */
const formatDate = (dateStr: string | null | undefined): string => {
  if (!dateStr) return '未知时间'
  // 处理后端返回的 "2026-02-15 10:11:13" 格式
  try {
    const date = new Date(dateStr.replace(' ', 'T') + 'Z')
    if (isNaN(date.getTime())) {
      return '未知时间'
    }
    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch (e) {
    return '未知时间'
  }
}

/**
 * 适配后端数字状态值转为中文描述
 * 后端状态：0-待付款 1-待发货 2-待收货 3-已完成 4-已取消
 */
const getStatusText = (status: number): string => {
  const statusMap: Record<number, string> = {
    0: '待付款',
    1: '待发货',
    2: '待收货',
    3: '已完成',
    4: '已取消',
  }
  return statusMap[status] || '未知状态'
}

/**
 * 适配状态对应的样式类
 */
const getStatusClass = (status: number): string => {
  const statusClassMap: Record<number, string> = {
    0: 'status-pending',    // 待付款
    1: 'status-shipped',    // 待发货
    2: 'status-shipped',    // 待收货（复用已发货样式）
    3: 'status-completed',  // 已完成
    4: 'status-cancelled',  // 已取消
  }
  return statusClassMap[status] || 'status-pending'
}

/**
 * 获取订单列表
 */
onMounted(async () => {
  // 验证用户是否登录
  if (!userStore.userId) {
    console.warn('用户未登录，无法获取订单列表')
    return
  }

  loading.value = true
  try {
    const userId = userStore.userId
    const response = await getOrderList({
      userId,
      pageNum: 1,
      pageSize: 10,
      status: 5 // 5表示查询所有状态（根据后端定义）
    })
    
    // 安全处理响应数据
    if (response && response.records) {
      orders.value = response.records || []
    } else {
      orders.value = []
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
    orders.value = []
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
/* 原有样式保持不变 */
.orders-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #666;
}

.empty-orders {
  text-align: center;
  padding: 40px;
  color: #999;
}

.back-btn {
  display: inline-block;
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  text-decoration: none;
  border-radius: 4px;
}

.back-btn:hover {
  background-color: #0056b3;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-card {
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 15px;
  background-color: #fafafa;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.order-info h3 {
  margin: 0 0 5px 0;
  color: #333;
}

.order-date {
  color: #999;
  font-size: 12px;
  margin: 0;
}

.order-status {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.status-pending {
  background-color: #fff3cd;
  color: #856404;
}

.status-shipped {
  background-color: #cfe2ff;
  color: #084298;
}

.status-completed {
  background-color: #d1e7dd;
  color: #0f5132;
}

.status-cancelled {
  background-color: #f8d7da;
  color: #842029;
}

.order-items {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 10px;
  font-size: 14px;
}

.order-items th,
.order-items td {
  padding: 8px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.order-items th {
  background-color: #f5f5f5;
  font-weight: bold;
}

.order-footer {
  text-align: right;
  margin-top: 10px;
}

.order-total {
  font-weight: bold;
  color: #ff6b6b;
  font-size: 16px;
}
</style>
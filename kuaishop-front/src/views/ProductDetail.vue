<template>
  <div class="product-detail-page">
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="!productStore.productDetail" class="empty">商品不存在</div>
    <div v-else class="product-detail">
      <div class="detail-container">
        <div class="images">
          <div class="main-image">
            <img :src="currentImage" :alt="productStore.productDetail.name" />
          </div>
          <div class="thumbnails">
            <div
              v-for="(img, idx) in productStore.productDetail.images"
              :key="idx"
              class="thumbnail"
              :class="{ active: currentImage === img }"
              @click="currentImage = img"
            >
              <img :src="img" :alt="`${productStore.productDetail.name}-${idx}`" />
            </div>
          </div>
        </div>

        <div class="info">
          <h1>{{ productStore.productDetail.name }}</h1>
          <p class="category">分类: {{ productStore.productDetail.categoryName }}</p>
          <div class="price-section">
            <span class="price">¥{{ productStore.productDetail.price }}</span>
            <span
              v-if="productStore.productDetail.originalPrice"
              class="original-price"
            >
              ¥{{ productStore.productDetail.originalPrice }}
            </span>
          </div>
          <p class="description">{{ productStore.productDetail.description }}</p>

          <div v-if="productStore.productDetail.specifications.length > 0" class="specifications">
            <h3>商品规格</h3>
            <div v-for="spec in productStore.productDetail.specifications" :key="spec.name" class="spec-item">
              <span class="spec-name">{{ spec.name }}:</span>
              <span class="spec-value">{{ spec.value }}</span>
            </div>
          </div>

          <div class="stock-info">
            <p>库存: <strong :class="{ 'out-of-stock': productStore.productDetail.stock === 0 }">
              {{ productStore.productDetail.stock }} 件
            </strong></p>
            <p>销量: {{ productStore.productDetail.sales }}</p>
          </div>

          <div class="purchase-section">
            <div class="quantity">
              <label>购买数量:</label>
              <div class="quantity-control">
                <button @click="quantity = Math.max(1, quantity - 1)" :disabled="quantity <= 1">-</button>
                <input v-model.number="quantity" type="number" min="1" />
                <button @click="quantity = quantity + 1" :disabled="quantity >= productStore.productDetail.stock">+</button>
              </div>
            </div>
            <button
              @click="handleAddToCart"
              :disabled="productStore.productDetail.stock === 0 || addingToCart"
              class="add-to-cart-btn"
            >
              {{ addingToCart ? '加入中...' : '加入购物车' }}
            </button>
            <button @click="handleBuyNow" :disabled="productStore.productDetail.stock === 0" class="buy-now-btn">
              立即购买
            </button>
          </div>
        </div>
      </div>

      <div class="detail-content">
        <h2>商品详情</h2>
        <p>{{ productStore.productDetail.detail }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useProductStore } from '@/stores/product'
import { useCartStore } from '@/stores/cart'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const productStore = useProductStore()
const cartStore = useCartStore()

const loading = ref(false)
const addingToCart = ref(false)
const quantity = ref(1)
const currentImage = ref('')

onMounted(async () => {
  const productId = parseInt(route.params.id as string)
  loading.value = true
  try {
    await productStore.fetchProductDetail(productId)
    if (productStore.productDetail) {
      currentImage.value = productStore.productDetail.imageUrl
    }
  } catch (error: any) {
    ElMessage.error(error.message || '加载商品信息失败')
  } finally {
    loading.value = false
  }
})

const handleAddToCart = async () => {
  if (!cartStore.userStore?.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  addingToCart.value = true
  try {
    await cartStore.addToCart(parseInt(route.params.id as string), quantity.value)
    ElMessage.success('已添加到购物车')
    quantity.value = 1
  } catch (error: any) {
    ElMessage.error(error.message || '添加失败')
  } finally {
    addingToCart.value = false
  }
}

const handleBuyNow = async () => {
  await handleAddToCart()
  if (!addingToCart.value) {
    router.push('/cart')
  }
}
</script>

<style scoped>
.product-detail-page {
  width: 100%;
}

.loading,
.empty {
  text-align: center;
  padding: 40px;
  color: #999;
  font-size: 16px;
}

.detail-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  margin-bottom: 40px;
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}

.images {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.main-image {
  width: 100%;
  height: 400px;
  background: #f5f5f5;
  border-radius: 8px;
  overflow: hidden;
}

.main-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumbnails {
  display: flex;
  gap: 10px;
}

.thumbnail {
  width: 60px;
  height: 60px;
  border: 2px solid #e0e0e0;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  transition: border-color 0.3s;
}

.thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumbnail.active {
  border-color: #409eff;
}

.info h1 {
  font-size: 28px;
  margin-bottom: 10px;
}

.category {
  color: #999;
  margin-bottom: 15px;
}

.price-section {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
}

.price {
  font-size: 28px;
  font-weight: bold;
  color: #f5222d;
}

.original-price {
  font-size: 16px;
  color: #999;
  text-decoration: line-through;
}

.description {
  color: #666;
  line-height: 1.6;
  margin-bottom: 20px;
}

.specifications {
  margin-bottom: 20px;
}

.specifications h3 {
  font-size: 14px;
  margin-bottom: 10px;
}

.spec-item {
  display: flex;
  gap: 10px;
  margin-bottom: 8px;
}

.spec-name {
  color: #666;
}

.spec-value {
  font-weight: 500;
}

.stock-info {
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f5f5;
  border-radius: 4px;
}

.stock-info p {
  margin: 5px 0;
}

.out-of-stock {
  color: #f5222d;
}

.purchase-section {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.quantity {
  display: flex;
  align-items: center;
  gap: 10px;
}

.quantity-control {
  display: flex;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.quantity-control button {
  width: 40px;
  height: 40px;
  border: none;
  background: #f5f5f5;
  cursor: pointer;
  transition: background 0.3s;
}

.quantity-control button:hover:not(:disabled) {
  background: #e0e0e0;
}

.quantity-control button:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.quantity-control input {
  width: 60px;
  border: none;
  text-align: center;
  font-size: 14px;
}

.quantity-control input:focus {
  outline: none;
}

.add-to-cart-btn,
.buy-now-btn {
  padding: 12px;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.add-to-cart-btn {
  background: #f5f5f5;
  color: #333;
  border: 2px solid #409eff;
}

.add-to-cart-btn:hover:not(:disabled) {
  background: #e0e0e0;
}

.buy-now-btn {
  background: #409eff;
  color: #fff;
}

.buy-now-btn:hover:not(:disabled) {
  background: #66b1ff;
}

.add-to-cart-btn:disabled,
.buy-now-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.detail-content {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}

.detail-content h2 {
  margin-bottom: 20px;
}

.detail-content p {
  color: #666;
  line-height: 1.8;
}

@media (max-width: 768px) {
  .detail-container {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .main-image {
    height: 300px;
  }

  .info h1 {
    font-size: 20px;
  }

  .price {
    font-size: 20px;
  }
}
</style>

<template>
  <div class="cart-page">
    <div class="cart-header">
      <h1>轻奢收纳盒</h1>
      <p class="header-subtitle">您为自己精选的品味</p>
    </div>

    <div v-if="cartStore.items.length === 0" class="empty-cart-state">
      <div class="empty-icon">🎁</div>
      <h2>收纳盒为空</h2>
      <p>为生活添加一些质感</p>
      <router-link to="/" class="btn-capsule">继续发现</router-link>
    </div>

    <div v-else class="cart-content">
      <!-- 收纳盒容器 -->
      <div class="cart-storage-box">
        <div class="storage-title">
          <span>已选商品 ({{ cartStore.items.length }})</span>
          <div class="divider-gold"></div>
        </div>

        <!-- 商品胶囊列表 -->
        <div class="items-container">
          <div
            v-for="item in cartStore.items"
            :key="item.id"
            class="item-capsule animation-soft-push"
          >
            <!-- 商品视觉部分 -->
            <div class="item-image">
              <img
                :src="item.mainImage || 'https://via.placeholder.com/80x80?text=Item'"
                :alt="item.name"
              />
            </div>

            <!-- 商品信息部分 -->
            <div class="item-info">
              <h3 class="item-name">{{ item.name }}</h3>
              <p class="item-price">¥{{ item.price }}</p>
            </div>

            <!-- 数量调整 -->
            <div class="quantity-control">
              <button
                class="qty-btn"
                @click="decreaseQuantity(item)"
                :disabled="item.quantity <= 1"
              >
                −
              </button>
              <input
                v-model.number="item.quantity"
                type="number"
                min="1"
                class="qty-input"
                @change="updateQuantity(item)"
              />
              <button class="qty-btn" @click="increaseQuantity(item)">
                +
              </button>
            </div>

            <!-- 小计 -->
            <div class="item-subtotal">
              <span class="subtotal-label">小计</span>
              <span class="subtotal-price">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
            </div>

            <!-- 删除按钮 -->
            <button
              class="btn-remove"
              @click="removeItem(item.productId)"
              title="从收纳盒中取出"
            >
              ✕
            </button>
          </div>
        </div>
      </div>

      <!-- 结算区域 -->
      <div class="checkout-section">
        <div class="checkout-box capsule-card-elevated">
          <div class="checkout-summary">
            <div class="summary-item">
              <span>小计</span>
              <span>¥{{ subtotal.toFixed(2) }}</span>
            </div>
            <div class="divider-line"></div>
            <div class="summary-item total">
              <span>总计</span>
              <span class="total-amount">¥{{ (cartStore.totalPrice || 0).toFixed(2) }}</span>
            </div>
          </div>
          <button @click="checkout" class="btn-capsule btn-checkout">
            确认收纳 · 去结算
          </button>
          <router-link to="/products" class="btn-continue-shopping">
            继续寻找
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useCartStore } from '@/stores/cart'
import { computed } from 'vue'

const cartStore = useCartStore()

const subtotal = computed(() => {
  return cartStore.items.reduce((sum, item) => sum + (item.price * item.quantity), 0)
})

const updateQuantity = async (item: any) => {
  await cartStore.updateQuantity(item.productId, item.quantity)
}

const decreaseQuantity = async (item: any) => {
  if (item.quantity > 1) {
    item.quantity--
    await updateQuantity(item)
  }
}

const increaseQuantity = async (item: any) => {
  item.quantity++
  await updateQuantity(item)
}

const removeItem = async (productId: any) => {
  await cartStore.removeItem(productId)
}

const checkout = () => {
  alert('功能开发中...')
}
</script>

<style scoped>
.cart-page {
  max-width: 1000px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-3xl);
}

/* === 页面header === */
.cart-header {
  text-align: center;
  padding: var(--spacing-2xl) 0;
}

.cart-header h1 {
  font-size: var(--font-size-3xl);
  color: var(--color-black);
  margin-bottom: var(--spacing-sm);
  font-weight: 600;
}

.header-subtitle {
  font-size: var(--font-size-sm);
  color: var(--color-gray-dark);
  letter-spacing: 1px;
  margin: 0;
}

/* === 空状态 === */
.empty-cart-state {
  text-align: center;
  padding: var(--spacing-3xl) var(--spacing-xl);
  background-color: var(--color-gray-light);
  border-radius: var(--radius-capsule);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-lg);
}

.empty-icon {
  font-size: 64px;
  animation: softPush var(--transition-slow) ease-out;
}

.empty-cart-state h2 {
  font-size: var(--font-size-2xl);
  color: var(--color-black);
  margin: 0;
}

.empty-cart-state p {
  color: var(--color-gray-dark);
  margin: 0;
  font-size: var(--font-size-sm);
}

/* === 购物车内容 === */
.cart-content {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: var(--spacing-2xl);
  align-items: start;
}

/* === 收纳盒容器 === */
.cart-storage-box {
  background-color: var(--color-white);
  border: var(--border-light);
  border-radius: var(--radius-capsule);
  padding: var(--spacing-xl);
  box-shadow: var(--shadow-sm);
}

.storage-title {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-xl);
  font-weight: 600;
  color: var(--color-black);
}

.storage-title span {
  font-size: var(--font-size-lg);
}

.divider-gold {
  flex: 1;
  height: 1px;
  background: var(--color-gold);
}

/* === 商品列表容器 === */
.items-container {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

/* === 商品胶囊 === */
.item-capsule {
  display: grid;
  grid-template-columns: 80px 1fr auto auto auto;
  gap: var(--spacing-lg);
  align-items: center;
  padding: var(--spacing-lg);
  background-color: var(--color-gray-light);
  border-radius: var(--radius-capsule);
  border: 1px solid var(--color-gray-medium);
  transition: all var(--transition-normal);
  position: relative;
}

.item-capsule:hover {
  border-color: var(--color-gold);
  background-color: var(--color-white);
  box-shadow: var(--shadow-md);
}

/* === 商品图片 === */
.item-image {
  width: 80px;
  height: 80px;
  border-radius: var(--radius-md);
  overflow: hidden;
  background-color: var(--color-white);
  border: var(--border-light);
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* === 商品信息 === */
.item-info {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.item-name {
  font-size: var(--font-size-base);
  font-weight: 600;
  color: var(--color-black);
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-price {
  font-size: var(--font-size-sm);
  color: var(--color-gray-dark);
  margin: 0;
}

/* === 数量控制 === */
.quantity-control {
  display: flex;
  align-items: center;
  gap: 4px;
  background-color: var(--color-white);
  border-radius: var(--radius-md);
  border: var(--border-light);
  padding: 2px;
}

.qty-btn {
  width: 28px;
  height: 28px;
  border: none;
  background-color: transparent;
  color: var(--color-black);
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
  transition: all var(--transition-fast);
  border-radius: var(--radius-sm);
}

.qty-btn:hover:not(:disabled) {
  background-color: var(--color-beige);
  color: var(--color-black);
}

.qty-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.qty-input {
  width: 40px;
  height: 28px;
  border: none;
  background-color: transparent;
  text-align: center;
  font-size: var(--font-size-sm);
  font-weight: 600;
  color: var(--color-black);
  outline: none;
  caret-color: var(--color-gold);
}

.qty-input::-webkit-outer-spin-button,
.qty-input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

/* === 小计 === */
.item-subtotal {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
  min-width: 90px;
}

.subtotal-label {
  font-size: var(--font-size-xs);
  color: var(--color-gray-dark);
  text-transform: uppercase;
  letter-spacing: 1px;
}

.subtotal-price {
  font-size: 16px;
  font-weight: 700;
  color: var(--color-black);
}

/* === 删除按钮 === */
.btn-remove {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: 1px solid var(--color-gray-medium);
  background-color: transparent;
  color: var(--color-black);
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
  transition: all var(--transition-fast);
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-remove:hover {
  border-color: var(--color-error);
  background-color: var(--color-error);
  color: var(--color-white);
  transform: scale(1.1);
}

/* === 结算区域 === */
.checkout-section {
  position: sticky;
  top: var(--spacing-2xl);
}

.checkout-box {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
  padding: var(--spacing-xl);
}

.checkout-summary {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  padding: var(--spacing-lg) 0;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: var(--font-size-sm);
  color: var(--color-gray-dark);
}

.summary-item span:last-child {
  font-weight: 600;
}

.summary-item.total {
  font-size: var(--font-size-base);
  color: var(--color-black);
  padding-top: var(--spacing-md);
}

.divider-line {
  height: 1px;
  background-color: var(--color-gray-light);
}

.total-amount {
  font-size: 20px;
  font-weight: 700;
  color: var(--color-black);
}

.btn-checkout {
  width: 100%;
  font-weight: 700;
  letter-spacing: 1px;
  padding: var(--spacing-md) var(--spacing-lg);
}

.btn-continue-shopping {
  display: block;
  text-align: center;
  padding: var(--spacing-md);
  color: var(--color-black);
  text-decoration: none;
  border: 1px solid var(--color-black);
  border-radius: var(--radius-capsule);
  font-size: var(--font-size-sm);
  font-weight: 500;
  transition: all var(--transition-fast);
  text-transform: uppercase;
  letter-spacing: 1px;
}

.btn-continue-shopping:hover {
  background-color: var(--color-black);
  color: var(--color-white);
}

/* === 响应式设计 === */
@media (max-width: 768px) {
  .cart-content {
    grid-template-columns: 1fr;
  }

  .item-capsule {
    grid-template-columns: 70px 1fr;
    gap: var(--spacing-md);
    grid-template-areas:
      "image name"
      "image price"
      "qty-control qty-control"
      "subtotal btn-remove";
  }

  .item-image {
    grid-area: image;
  }

  .item-info {
    grid-area: name;
  }

  .quantity-control {
    grid-area: qty-control;
    justify-self: start;
  }

  .item-subtotal {
    grid-area: subtotal;
  }

  .btn-remove {
    grid-area: btn-remove;
    justify-self: end;
  }

  .checkout-section {
    position: relative;
    top: 0;
  }
}
</style>
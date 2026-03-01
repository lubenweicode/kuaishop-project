<template>
  <div class="home-page">
    <!-- 品牌banner - 极简风格 -->
    <section class="banner-section">
      <div class="banner">
        <div class="banner-content">
          <h1 class="banner-title">轻奢陶冶</h1>
          <p class="banner-subtitle">择一物，悦一心</p>
          <div class="brand-description">
            <span>精选设计师品牌  ·  少而精的呈现</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 搜索与筛选 - 胶囊镜像 -->
    <section class="search-section">
      <div class="search-bar">
        <input
          v-model="searchQuery"
          type="text"
          placeholder="寻找心动之物..."
          @keyup.enter="search"
          class="capsule-input search-input"
        />
        <button @click="search" class="btn-capsule search-btn">搜索</button>
      </div>
      
      <div class="sort-options">
        <select v-model="sortBy" @change="loadProducts" class="capsule-input sort-select">
          <option value="price">按价格</option>
          <option value="sales">按销量</option>
          <option value="createTime">按最新</option>
        </select>
        <select v-model="sortOrder" @change="loadProducts" class="capsule-input sort-select">
          <option value="asc">从低到高</option>
          <option value="desc">从高到低</option>
        </select>
      </div>
    </section>

    <div class="gold-divider"></div>

    <!-- 核心商品展示 - 3-5款"少而精" -->
    <section class="products-section">
      <div class="section-header">
        <h2>精选推荐</h2>
        <p class="section-subtitle">每一件都值得拥有</p>
      </div>
      
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>
      
      <div v-else-if="displayProducts.length === 0" class="empty-state">
        <p>暂无商品</p>
        <router-link to="/products" class="btn-capsule">浏览全部</router-link>
      </div>
      
      <div v-else class="products-showcase">
        <div
          v-for="product in displayProducts"
          :key="product.id"
          class="product-capsule metallic-brush"
          @click="navigateToDetail(product.id)"
        >
          <div class="product-visual">
            <img 
              :src="product.imageUrl || 'https://via.placeholder.com/300x400?text=Collection'" 
              :alt="product.name"
              class="product-image"
            />
            <div class="product-overlay">
              <button class="btn-capsule btn-detail">查看详情</button>
            </div>
          </div>
          
          <div class="product-details">
            <h3 class="product-name">{{ product.name }}</h3>
            <p class="product-category">{{ product.categoryName }}</p>
            <div class="price-section">
              <span class="price-current">¥{{ product.price }}</span>
              <span v-if="product.originalPrice" class="price-original">¥{{ product.originalPrice }}</span>
            </div>
            <button @click.stop="addToCart(product.id)" class="btn-capsule btn-add-cart">加入购物车</button>
          </div>
        </div>
      </div>
      
      <div class="view-all">
        <router-link to="/products" class="btn-capsule-outline">浏览全部商品</router-link>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useProductStore } from '@/stores/product'
import { useCartStore } from '@/stores/cart'
import { ElMessage } from 'element-plus'

const router = useRouter()
const productStore = useProductStore()
const cartStore = useCartStore()
const loading = ref(false)
const searchQuery = ref('')
const sortBy = ref('price')
const sortOrder = ref('asc')

onMounted(() => {
  loadProducts()
})

const loadProducts = async () => {
  loading.value = true
  try {
    await productStore.fetchProductList({ 
      page: 1, 
      size: 12,
      sortBy: sortBy.value,
      order: sortOrder.value
    })
  } catch (error: any) {
    ElMessage.error(error.message || '加载商品失败')
  } finally {
    loading.value = false
  }
}

const search = async () => {
  if (!searchQuery.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  loading.value = true
  try {
    await productStore.fetchProductList({
      page: 1,
      size: 12,
      keyword: searchQuery.value,
      sortBy: sortBy.value,
      order: sortOrder.value
    })
  } catch (error: any) {
    ElMessage.error(error.message || '搜索失败')
  } finally {
    loading.value = false
  }
}

const goToPage = (page: number) => {
  productStore.fetchProductList({ 
    page, 
    size: 12,
    keyword: searchQuery.value || undefined,
    sortBy: sortBy.value,
    order: sortOrder.value
  })
}

// 只展示首3-5款核心商品
const displayProducts = computed(() => {
  const products = productStore.products
  return products.slice(0, Math.min(5, products.length))
})

const navigateToDetail = (productId: number) => {
  router.push(`/products/${productId}`)
}

const addToCart = async (productId: number) => {
  try {
    await cartStore.addToCart(productId, 1)
    ElMessage.success('已添加到购物车')
  } catch (error: any) {
    ElMessage.error(error.message || '添加失败，请先登录')
  }
}
</script>

<style scoped>
.home-page {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-3xl);
}

/* === Banner区域 === */
.banner-section {
  margin: calc(-1 * var(--spacing-3xl)) calc(-1 * var(--spacing-xl)) 0;
  padding: var(--spacing-3xl) var(--spacing-xl);
  background: linear-gradient(
    135deg,
    var(--color-beige) 0%,
    rgba(243, 233, 224, 0.8) 50%,
    var(--color-white) 100%
  );
  border-bottom: var(--border-thin);
  min-height: 280px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.banner {
  text-align: center;
  max-width: 600px;
}

.banner-content {
  animation: softPush var(--transition-slow) ease-out;
}

.banner-title {
  font-size: var(--font-size-3xl);
  font-weight: 600;
  color: var(--color-black);
  margin-bottom: var(--spacing-md);
  letter-spacing: -1px;
}

.banner-subtitle {
  font-size: var(--font-size-lg);
  color: var(--color-gray-dark);
  font-weight: 400;
  margin-bottom: var(--spacing-xl);
  letter-spacing: 1px;
}

.brand-description {
  font-size: var(--font-size-sm);
  color: var(--color-gray-dark);
  letter-spacing: 2px;
  text-transform: uppercase;
  font-weight: 500;
}

/* === 搜索与筛选区域 === */
.search-section {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
  margin: var(--spacing-xl) 0;
}

.search-bar {
  display: flex;
  gap: var(--spacing-md);
  align-items: center;
  max-width: 500px;
}

.search-input {
  flex: 1;
  caret-color: var(--color-gold);
}

.search-input::placeholder {
  color: var(--color-gray-medium);
}

.search-btn {
  font-weight: 500;
  letter-spacing: 1px;
}

.sort-options {
  display: flex;
  gap: var(--spacing-md);
  align-items: center;
  flex-wrap: wrap;
}

.sort-select {
  min-width: 150px;
}

/* === 分割线 === */
.gold-divider {
  opacity: 0.6;
}

/* === 产品展示区域 === */
.products-section {
  padding: var(--spacing-2xl) 0;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-2xl);
}

.section-header {
  text-align: center;
  margin-bottom: var(--spacing-xl);
}

.section-header h2 {
  font-size: var(--font-size-3xl);
  color: var(--color-black);
  margin-bottom: var(--spacing-sm);
  font-weight: 600;
}

.section-subtitle {
  font-size: var(--font-size-sm);
  color: var(--color-gray-dark);
  font-weight: 400;
  letter-spacing: 1px;
  margin: 0;
}

/* === 加载和空状态 === */
.loading-state,
.empty-state {
  text-align: center;
  padding: var(--spacing-3xl) var(--spacing-xl);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-lg);
  color: var(--color-gray-dark);
  background-color: var(--color-gray-light);
  border-radius: var(--radius-capsule);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 2px solid var(--color-gray-medium);
  border-top: 2px solid var(--color-gold);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* === 产品展示网格 === */
.products-showcase {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--spacing-2xl);
  margin: var(--spacing-xl) 0;
}

.product-capsule {
  background-color: var(--color-white);
  border-radius: var(--radius-capsule);
  overflow: hidden;
  cursor: pointer;
  transition: all var(--transition-normal);
  border: var(--border-light);
  display: flex;
  flex-direction: column;
  animation: softPush var(--transition-normal) ease-out;
}

.product-capsule:hover {
  border-color: var(--color-gold);
  box-shadow: var(--shadow-lg);
  transform: translateY(-4px);
}

.product-visual {
  position: relative;
  overflow: hidden;
  height: 320px;
  background-color: var(--color-gray-light);
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-normal);
}

.product-capsule:hover .product-image {
  transform: scale(1.05);
}

.product-overlay {
  position: absolute;
  inset: 0;
  background: rgba(42, 42, 42, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity var(--transition-normal);
}

.product-capsule:hover .product-overlay {
  opacity: 1;
}

.btn-detail {
  background-color: var(--color-white);
  color: var(--color-black);
  font-weight: 600;
}

.btn-detail:hover {
  background-color: var(--color-beige);
  transform: scale(1.02);
}

/* === 产品详情 === */
.product-details {
  padding: var(--spacing-lg);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  flex: 1;
}

.product-name {
  font-size: var(--font-size-lg);
  font-weight: 600;
  color: var(--color-black);
  margin: 0;
  line-height: 1.4;
}

.product-category {
  font-size: var(--font-size-sm);
  color: var(--color-gray-dark);
  margin: 0;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.price-section {
  display: flex;
  align-items: baseline;
  gap: var(--spacing-md);
  padding: var(--spacing-md) 0;
  border-top: var(--border-light);
  border-bottom: var(--border-light);
}

.price-current {
  font-size: 20px;
  font-weight: 700;
  color: var(--color-black);
}

.price-original {
  font-size: var(--font-size-sm);
  color: var(--color-gray-medium);
  text-decoration: line-through;
}

.btn-add-cart {
  width: 100%;
  font-weight: 600;
  letter-spacing: 1px;
  margin-top: auto;
}

/* === 浏览全部 === */
.view-all {
  text-align: center;
  padding: var(--spacing-2xl) 0;
  border-top: var(--border-light);
}

/* === 响应式设计 === */
@media (max-width: 768px) {
  .home-page {
    gap: var(--spacing-2xl);
  }

  .banner-section {
    min-height: 240px;
    margin-left: calc(-1 * var(--spacing-lg));
    margin-right: calc(-1 * var(--spacing-lg));
    padding: var(--spacing-2xl) var(--spacing-lg);
  }

  .banner-title {
    font-size: var(--font-size-2xl);
  }

  .banner-subtitle {
    font-size: var(--font-size-base);
  }

  .search-bar {
    flex-direction: column;
  }

  .search-btn {
    width: 100%;
  }

  .products-showcase {
    grid-template-columns: 1fr;
  }

  .section-header h2 {
    font-size: var(--font-size-2xl);
  }
}
</style>


<template>
  <div class="products-container">
    <div class="filters">
      <h3>筛选与搜索</h3>
      <div class="filter-row">
        <input
          v-model="searchQuery"
          type="text"
          placeholder="搜索商品名称..."
          class="search-input"
          @keyup.enter="handleSearch"
        />
        <button @click="handleSearch" class="search-btn">搜索</button>
      </div>
      
      <div class="filter-row">
        <label>排序方式:</label>
        <select v-model="sortBy" class="filter-select" @change="handleSearch">
          <option value="price">按价格</option>
          <option value="sales">按销量</option>
          <option value="createTime">按最新</option>
        </select>
        
        <select v-model="sortOrder" class="filter-select" @change="handleSearch">
          <option value="asc">从低到高</option>
          <option value="desc">从高到低</option>
        </select>
      </div>

      <button @click="resetFilters" class="reset-btn">重置筛选</button>
    </div>

    <div class="products-grid">
      <div
        v-if="filteredProducts.length === 0"
        class="empty-message"
      >
        <p>暂无符合条件的商品</p>
      </div>
      <div
        v-for="product in filteredProducts"
        v-else
        :key="product.id"
        class="product-card"
        @click="navigateTo(`/products/${product.id}`)"
      >
        <img 
          :src="product.mainImage || 'https://via.placeholder.com/200x150?text=No+Image'" 
          :alt="product.name" 
          class="product-image" 
        />
        <h3>{{ product.name }}</h3>
        <p class="description">{{ product.description || '暂无描述' }}</p>
        <div class="price-section">
          <span class="price">¥{{ product.price }}</span>
          <button @click.stop="addToCart(product)" class="add-btn">加入购物车</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { getProductList } from '@/api/product'

const router = useRouter()
const route = useRoute()
const cartStore = useCartStore()
const products = ref<any[]>([])
const searchQuery = ref('')
const sortBy = ref('price')
const sortOrder = ref('asc')
const loading = ref(false)

const filteredProducts = computed(() => {
  let result = products.value
  
  // 按搜索关键词过滤
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter((product) =>
      product.name.toLowerCase().includes(query),
    )
  }
  
  return result
})

onMounted(async () => {
  await fetchProducts()
})

const fetchProducts = async () => {
  loading.value = true
  try {
    const categoryId = route.query.categoryId ? Number(route.query.categoryId) : undefined
    const keyword = searchQuery.value.trim() || undefined
    
    const response = await getProductList({
      categoryId,
      keyword,
      sortBy: sortBy.value,
      order: sortOrder.value,
    })
    products.value = response.records || []
  } catch (error) {
    console.error('获取商品列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = async () => {
  await fetchProducts()
}

const resetFilters = async () => {
  searchQuery.value = ''
  sortBy.value = 'price'
  sortOrder.value = 'asc'
  await fetchProducts()
}

const navigateTo = (path: string) => {
  router.push(path)
}

const addToCart = (product: any) => {
  cartStore.addToCart(product)
  alert('已添加到购物车')
}
</script>

<style scoped>
.products-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.filters {
  margin-bottom: 30px;
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 4px;
}

.filters h3 {
  margin-top: 0;
  margin-bottom: 15px;
}

.filter-row {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
  align-items: center;
  flex-wrap: wrap;
}

.filter-row label {
  font-weight: bold;
  white-space: nowrap;
}

.search-input {
  flex: 1;
  min-width: 200px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.search-btn {
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  white-space: nowrap;
}

.search-btn:hover {
  background-color: #0056b3;
}

.filter-select {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  background-color: white;
}

.reset-btn {
  padding: 10px 20px;
  background-color: #6c757d;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.reset-btn:hover {
  background-color: #5a6268;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.empty-message {
  text-align: center;
  padding: 40px;
  color: #999;
  grid-column: 1 / -1;
}

.product-card {
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  padding: 15px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.product-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 4px;
  margin-bottom: 10px;
}

.product-card h3 {
  font-size: 16px;
  margin: 10px 0;
  height: 40px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.description {
  font-size: 12px;
  color: #666;
  margin-bottom: 10px;
  height: 36px;
  overflow: hidden;
}

.price-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.price {
  font-size: 18px;
  font-weight: bold;
  color: #ff6b6b;
}

.add-btn {
  padding: 6px 12px;
  background-color: #ff6b6b;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.add-btn:hover {
  background-color: #ff5252;
}
</style>
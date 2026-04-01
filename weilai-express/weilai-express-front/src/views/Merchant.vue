<template>
  <div class="merchant-container">
    <el-header class="header">
      <el-button @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
      <h1>{{ merchant.name }}</h1>
    </el-header>

    <el-main class="main">
      <el-row :gutter="20">
        <el-col :span="16">
          <div class="category-list">
            <el-tabs v-model="activeCategory">
              <el-tab-pane
                  v-for="category in categories"
                  :key="category.id"
                  :label="category.name"
                  :name="category.id"
              />
            </el-tabs>

            <div class="dish-list">
              <el-card
                  v-for="dish in filteredDishes"
                  :key="dish.id"
                  class="dish-card"
              >
                <div class="dish-image-placeholder">🍲</div>
                <div class="dish-info">
                  <h3>{{ dish.name }}</h3>
                  <p class="dish-desc">{{ dish.description || '' }}</p>
                  <div class="dish-price">
                    <span class="current-price">¥{{ dish.price }}</span>
                    <span class="original-price" v-if="dish.originalPrice">¥{{ dish.originalPrice }}</span>
                  </div>
                  <div class="dish-sales">
                    <span>月售 {{ dish.salesCount || 0 }}</span>
                    <el-rate v-model="dish.rating" disabled />
                  </div>
                </div>
                <div class="dish-action">
                  <el-button-group>
                    <el-button
                        size="small"
                        @click="decreaseQuantity(dish)"
                        :disabled="(dish.quantity || 0) <= 0"
                    >
                      -
                    </el-button>
                    <span class="quantity">{{ dish.quantity || 0 }}</span>
                    <el-button
                        size="small"
                        type="primary"
                        @click="increaseQuantity(dish)"
                    >
                      +
                    </el-button>
                  </el-button-group>
                  <el-button type="primary" size="small" @click="addToCart(dish)">
                    加入购物车
                  </el-button>
                </div>
              </el-card>
            </div>
          </div>
        </el-col>

        <el-col :span="8">
          <el-card class="shop-cart">
            <template #header>
              <h3>购物车</h3>
            </template>
            <div class="cart-items">
              <div v-for="item in cartItems" :key="item.id" class="cart-item">
                <span>{{ item.name }}</span>
                <span>x{{ item.quantity }}</span>
                <span>¥{{ (item.price * item.quantity).toFixed(2) }}</span>
              </div>
            </div>
            <div class="cart-total">
              <span>合计：</span>
              <span class="total-price">¥{{ totalPrice.toFixed(2) }}</span>
            </div>
            <el-button type="primary" class="checkout-btn" @click="goCheckout" block>
              去结算
            </el-button>
          </el-card>
        </el-col>
      </el-row>
    </el-main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getDishList, addToCart as apiAddToCart } from '@/api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const merchant = ref({})
const categories = ref([])
const dishes = ref([])
const activeCategory = ref(null)
const cartItems = ref([])

onMounted(async () => {
  try {
    const merchantId = parseInt(route.params.id)

    // 加载商家信息（暂时用模拟数据，等后续完善商家详情 API）
    merchant.value = {
      id: merchantId,
      name: '美味餐厅',
      description: '正宗川菜，新鲜食材，现点现做',
      deliveryFee: 5.00,
      minOrderAmount: 20.00,
      rating: 4.8,
      salesCount: 9999,
      address: '北京市朝阳区建国路 88 号',
      phone: '010-88888888'
    }

    // 从后端加载菜品列表
    console.log('加载菜品列表，商家 ID:', merchantId)
    const dishesRes = await getDishList({ merchantId })
    console.log('菜品数据:', dishesRes)
    dishes.value = dishesRes || []

    // 提取分类
    const categoryMap = new Map()
    dishes.value.forEach(dish => {
      if (!categoryMap.has(dish.categoryId)) {
        categoryMap.set(dish.categoryId, { id: dish.categoryId, name: dish.categoryName || '未分类' })
      }
    })
    categories.value = Array.from(categoryMap.values())

    if (categories.value.length > 0) {
      activeCategory.value = categories.value[0].id
    }
  } catch (error) {
    console.error('加载商家详情失败:', error)
    ElMessage.error('加载失败')
  }
})

const filteredDishes = computed(() => {
  if (!activeCategory.value) return []
  return dishes.value.filter(dish => dish.categoryId === activeCategory.value)
})

const totalPrice = computed(() => {
  if (!Array.isArray(cartItems.value) || cartItems.value.length === 0) {
    return 0
  }
  return cartItems.value.reduce((sum, item) => sum + (item.price || 0) * (item.quantity || 1), 0)
})

const increaseQuantity = (dish) => {
  dish.quantity = (dish.quantity || 0) + 1
}

const decreaseQuantity = (dish) => {
  if (dish.quantity > 0) {
    dish.quantity--
  }
}

const addToCart = async (dish) => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'))
    if (!userInfo) {
      ElMessage.warning('请先登录')
      router.push('/login')
      return
    }

    await apiAddToCart({
      userId: userInfo.id,
      dishId: dish.id,
      quantity: 1
    })

    // 更新本地购物车显示
    const existItem = cartItems.value.find(item => item.id === dish.id)
    if (existItem) {
      existItem.quantity = (existItem.quantity || 0) + 1
    } else {
      cartItems.value.push({
        id: dish.id,
        name: dish.name,
        price: dish.price,
        quantity: 1
      })
    }

    ElMessage.success('已加入购物车')
  } catch (error) {
    console.error(error)
    ElMessage.error('添加失败')
  }
}

const goBack = () => {
  router.back()
}

const goCheckout = () => {
  router.push('/cart')
}
</script>

<style scoped lang="scss">
.merchant-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: #fff;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 0 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  h1 {
    margin: 0;
    font-size: 20px;
  }
}

.main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;

  .category-list {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
  }

  .dish-list {
    margin-top: 20px;

    .dish-card {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      padding: 16px;

      .dish-image-placeholder {
        width: 120px;
        height: 120px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 48px;
        margin-right: 20px;
        flex-shrink: 0;
      }

      .dish-info {
        flex: 1;

        h3 {
          margin: 0 0 8px;
          font-size: 16px;
        }

        .dish-desc {
          color: #666;
          font-size: 14px;
          margin-bottom: 8px;
        }

        .dish-price {
          margin-bottom: 8px;

          .current-price {
            color: #f56c6c;
            font-size: 18px;
            font-weight: bold;
            margin-right: 8px;
          }

          .original-price {
            color: #999;
            text-decoration: line-through;
            font-size: 14px;
          }
        }

        .dish-sales {
          display: flex;
          gap: 8px;
          font-size: 13px;
          color: #999;
        }
      }

      .dish-action {
        display: flex;
        flex-direction: column;
        gap: 8px;
        align-items: center;

        .quantity {
          padding: 4px 12px;
          font-size: 14px;
        }
      }
    }
  }

  .shop-cart {
    position: sticky;
    top: 20px;

    .cart-items {
      max-height: 400px;
      overflow-y: auto;

      .cart-item {
        display: flex;
        justify-content: space-between;
        padding: 12px 0;
        border-bottom: 1px solid #eee;
      }
    }

    .cart-total {
      display: flex;
      justify-content: space-between;
      padding: 16px 0;
      font-size: 16px;
      font-weight: bold;

      .total-price {
        color: #f56c6c;
        font-size: 20px;
      }
    }

    .checkout-btn {
      margin-top: 16px;
    }
  }
}
</style>
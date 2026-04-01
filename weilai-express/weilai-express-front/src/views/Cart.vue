<template>
  <div class="cart-container">
    <el-card class="cart-card">
      <template #header>
        <div class="card-header">
          <h2>购物车</h2>
          <el-button @click="goHome">继续购物</el-button>
        </div>
      </template>

      <div v-if="cartItems.length === 0" class="empty-cart">
        <el-empty description="购物车空空如也～" />
        <el-button type="primary" @click="goHome">去逛逛</el-button>
      </div>

      <el-table v-else :data="cartItems" border @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column label="菜品信息">
          <template #default="{ row }">
            <div class="dish-info">
              <div class="dish-image-placeholder">🍲</div>
              <div class="dish-name">{{ row.dishName || '未知菜品' }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="单价" width="100">
          <template #default="{ row }">¥{{ (parseFloat(row.price) || 0).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="数量" width="150">
          <template #default="{ row }">
            <el-input-number
                v-model="row.quantity"
                :min="1"
                :max="99"
                size="small"
                @change="updateQuantity(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="小计" width="100">
          <template #default="{ row }">
            <span class="subtotal">¥{{ ((parseFloat(row.price) || 0) * (parseInt(row.quantity) || 1)).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="danger" size="small" @click="deleteItem(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="cartItems.length > 0" class="cart-footer">
        <div class="cart-total">
          <span>已选 {{ selectedCount }} 件商品</span>
          <span class="total-price">合计：¥{{ totalPrice.toFixed(2) }}</span>
        </div>

        <!-- 支付方式选择 -->
        <el-select
          v-model="paymentMethod"
          placeholder="选择支付方式"
          style="width: 150px; margin-right: 10px;"
        >
          <el-option label="微信支付" :value="1">
            <span>💚 微信支付</span>
          </el-option>
          <el-option label="支付宝" :value="2">
            <span>💙 支付宝</span>
          </el-option>
          <el-option label="银行卡" :value="3">
            <span>🏦 银行卡</span>
          </el-option>
        </el-select>

        <el-button
          type="primary"
          size="large"
          @click="handleCreateOrder"
          :disabled="selectedCount === 0 || totalPrice <= 0"
        >
          去结算
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCart, updateCartItem, deleteCartItem, createOrder as apiCreateOrder } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

console.log('=== Cart.vue 组件已加载 ===')

const router = useRouter()
const cartItems = ref([])
const selectedItems = ref([])
const paymentMethod = ref(1) // 默认微信支付

onMounted(async () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo'))
  if (!userInfo) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  await loadCart()
})

const loadCart = async () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'))

    console.log('=== 开始加载购物车 ===')
    console.log('用户 ID:', userInfo.id)

    // 直接调用 API，不使用拦截器
    const axios = await import('axios')
    const response = await axios.default.get('http://localhost:8080/api/cart', {
      params: { userId: userInfo.id }
    })

    console.log('=== 后端响应 ===')
    console.log('完整响应:', response)
    console.log('响应数据:', response.data)

    if (response.data && response.data.code === 200) {
      cartItems.value = response.data.data || []
      console.log('购物车商品数量:', cartItems.value.length)
    } else {
      console.warn('响应格式不对:', response.data)
      cartItems.value = []
    }
  } catch (error) {
    console.error('=== 加载购物车失败 ===')
    console.error('错误对象:', error)
    console.error('错误响应:', error.response)
    console.error('错误数据:', error.response?.data)

    ElMessage.error('加载购物车失败：' + (error.response?.data?.message || error.message))
    cartItems.value = []
  }
}

const selectedCount = computed(() => selectedItems.value.length)

// 先定义 totalPrice，避免循环引用
const totalPrice = computed(() => {
  if (!Array.isArray(selectedItems.value) || selectedItems.value.length === 0) {
    return 0
  }

  const total = selectedItems.value.reduce((sum, item) => {
    const price = parseFloat(item.price) || 0
    const quantity = parseInt(item.quantity) || 1
    return sum + (price * quantity)
  }, 0)

  return total
})

const handleSelectionChange = (selection) => {
  console.log('=== 选择的商品（触发事件）===')
  console.log('选中列表:', selection)
  console.log('选中数量:', selection.length)

  selectedItems.value = selection

  // 打印总价
  console.log('当前总价:', totalPrice.value)
}

const updateQuantity = async (item) => {
  try {
    await updateCartItem(item.id, { quantity: item.quantity })
    ElMessage.success('更新成功')
  } catch (error) {
    console.error(error)
    ElMessage.error('更新失败')
  }
}

const deleteItem = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该商品吗？', '删除提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteCartItem(id)
    await loadCart()
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error('删除失败')
    }
  }
}

const handleCreateOrder = async () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'))
    if (!userInfo) {
      ElMessage.warning('请先登录')
      return
    }

    if (selectedItems.value.length === 0) {
      ElMessage.warning('请选择商品')
      return
    }

    if (!paymentMethod.value) {
      ElMessage.warning('请选择支付方式')
      return
    }

    console.log('=== 开始创建订单 ===')
    console.log('选中商品:', selectedItems.value)
    console.log('总价:', totalPrice.value)
    console.log('支付方式:', paymentMethod.value)

    // 获取第一个商品的商家 ID
    const firstItem = selectedItems.value[0]
    const merchantId = firstItem.merchantId

    if (!merchantId) {
      ElMessage.error('商品信息不完整，缺少商家 ID')
      return
    }

    const orderData = {
      userId: userInfo.id,
      merchantId: merchantId,
      items: selectedItems.value.map(item => ({
        dishId: item.dishId,
        quantity: parseInt(item.quantity) || 1,
        price: parseFloat(item.price) || 0
      })),
      deliveryAddress: '北京市朝阳区建国路 88 号',
      receiverName: userInfo.nickname || userInfo.username,
      receiverPhone: userInfo.phone || '13800138001',
      paymentMethod: paymentMethod.value
    }

    console.log('订单数据:', orderData)

    const res = await apiCreateOrder(orderData)

    console.log('订单创建成功:', res)
    ElMessage.success('订单创建成功！')

    // 清空购物车
    cartItems.value = cartItems.value.filter(item =>
      !selectedItems.value.find(selected => selected.id === item.id)
    )
    selectedItems.value = []

    // 跳转到支付页面
    if (res && res.id) {
      router.push(`/pay/${res.id}?method=${paymentMethod.value}`)
    } else {
      router.push('/')
    }
  } catch (error) {
    console.error('=== 创建订单失败 ===')
    console.error('错误详情:', error)
    console.error('响应数据:', error.response?.data)

    const errorMsg = error.response?.data?.message || error.message || '创建订单失败'
    ElMessage.error(errorMsg)
  }
}

const goHome = () => {
  router.push('/')
}

const goCheckout = () => {
  router.push('/cart')
}
</script>

<style scoped lang="scss">
.cart-container {
  max-width: 900px;
  margin: 20px auto;
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  h2 {
    margin: 0;
  }
}

.empty-cart {
  text-align: center;
  padding: 40px 0;
}

.dish-info {
  display: flex;
  align-items: center;
  gap: 12px;

  .dish-image {
    width: 60px;
    height: 60px;
    border-radius: 4px;
    object-fit: cover;
  }

  .dish-name {
    font-size: 14px;
  }
}

.cart-footer {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .cart-total {
    display: flex;
    gap: 20px;
    font-size: 16px;

    .total-price {
      color: #f56c6c;
      font-weight: bold;
      font-size: 18px;
    }
  }
}
</style>

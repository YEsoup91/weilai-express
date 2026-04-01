<template>
  <div class="order-detail-container">
    <el-card class="order-card">
      <template #header>
        <div class="card-header">
          <h2>订单详情</h2>
          <el-button @click="goBack">返回</el-button>
        </div>
      </template>

      <el-descriptions title="订单信息" :column="2" border>
        <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(order.status)">
            {{ getStatusText(order.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ order.createTime }}</el-descriptions-item>
        <el-descriptions-item label="支付方式">{{ getPaymentText(order.paymentMethod) }}</el-descriptions-item>
        <el-descriptions-item label="收货人">{{ order.receiverName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ order.receiverPhone }}</el-descriptions-item>
        <el-descriptions-item label="配送地址" :span="2">{{ order.deliveryAddress }}</el-descriptions-item>
      </el-descriptions>

      <el-divider>商品清单</el-divider>

      <el-table :data="orderItems" border>
        <el-table-column prop="dishName" label="菜品名称" />
        <el-table-column prop="price" label="单价" width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="100" />
        <el-table-column label="小计" width="100">
          <template #default="{ row }">¥{{ (row.price * row.quantity).toFixed(2) }}</template>
        </el-table-column>
      </el-table>

      <div class="order-total">
        <div class="total-item">
          <span>商品总额：</span>
          <span>¥{{ order.totalAmount?.toFixed(2) }}</span>
        </div>
        <div class="total-item">
          <span>配送费：</span>
          <span>¥{{ order.deliveryFee?.toFixed(2) }}</span>
        </div>
        <div class="total-item">
          <span>优惠金额：</span>
          <span>-¥{{ order.discountAmount?.toFixed(2) }}</span>
        </div>
        <div class="total-item total-amount">
          <span>实付金额：</span>
          <span class="price">¥{{ order.payAmount?.toFixed(2) }}</span>
        </div>
      </div>

      <div class="order-actions" v-if="order.status === 0">
        <el-button type="primary" @click="handlePayOrder">立即支付</el-button>
        <el-button type="danger" @click="handleCancelOrder">取消订单</el-button>
      </div>

      <div class="order-actions" v-else-if="order.status === 4">
        <el-button type="success" @click="showReviewDialog">评价订单</el-button>
      </div>
    </el-card>

    <!-- 评价对话框 -->
    <el-dialog v-model="reviewDialogVisible" title="评价订单" width="500px">
      <el-form :model="reviewForm" label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="reviewForm.rating" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input
              v-model="reviewForm.content"
              type="textarea"
              :rows="4"
              placeholder="请输入评价内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReview">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getOrderDetail, payOrder, cancelOrder, addReview } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const order = ref({})
const orderItems = ref([])
const reviewDialogVisible = ref(false)
const reviewForm = ref({
  rating: 5,
  content: ''
})

onMounted(async () => {
  try {
    const res = await getOrderDetail(route.params.id)
    order.value = res
    orderItems.value = res.items || []
  } catch (error) {
    console.error(error)
  }
})

const getStatusType = (status) => {
  const types = ['info', 'warning', 'success', 'primary', 'success', 'danger']
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = ['待支付', '待制作', '待配送', '配送中', '已完成', '已取消']
  return texts[status] || '未知状态'
}

const getPaymentText = (method) => {
  const texts = ['未支付', '微信支付', '支付宝', '银行卡']
  return texts[method] || '未知'
}

const handlePayOrder = async () => {
  try {
    await ElMessageBox.confirm('确认支付该订单吗？', '支付提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await payOrder(route.params.id, { paymentMethod: 1 })
    ElMessage.success('支付成功')
    location.reload()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleCancelOrder = async () => {
  try {
    await ElMessageBox.confirm('确认取消该订单吗？', '取消提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelOrder(route.params.id)
    ElMessage.success('订单已取消')
    location.reload()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const showReviewDialog = () => {
  reviewDialogVisible.value = true
}

const submitReview = async () => {
  try {
    await addReview({
      orderId: route.params.id,
      rating: reviewForm.value.rating,
      content: reviewForm.value.content
    })
    ElMessage.success('评价成功')
    reviewDialogVisible.value = false
    router.push('/')
  } catch (error) {
    console.error(error)
  }
}

const goBack = () => {
  router.back()
}
</script>

<style scoped lang="scss">
.order-detail-container {
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

.order-total {
  margin-top: 20px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 4px;

  .total-item {
    display: flex;
    justify-content: space-between;
    margin-bottom: 8px;

    &.total-amount {
      font-weight: bold;
      font-size: 16px;
      margin-top: 12px;
      padding-top: 12px;
      border-top: 1px solid #ddd;

      .price {
        color: #f56c6c;
        font-size: 20px;
      }
    }
  }
}

.order-actions {
  margin-top: 24px;
  text-align: center;

  button {
    margin: 0 8px;
  }
}
</style>

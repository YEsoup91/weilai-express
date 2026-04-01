<template>
  <div class="pay-container">
    <el-card class="pay-card">
      <template #header>
        <h2>订单支付</h2>
      </template>

      <div v-if="loading" class="loading">
        <el-skeleton :rows="5" animated />
      </div>

      <div v-else-if="!order.id" class="empty">
        <el-empty description="订单不存在" />
        <el-button type="primary" @click="goHome">返回首页</el-button>
      </div>

      <div v-else>
        <!-- 订单信息 -->
        <div class="order-info">
          <h3>订单信息</h3>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="订单金额">¥{{ order.payAmount }}</el-descriptions-item>
            <el-descriptions-item label="配送费">¥{{ order.deliveryFee }}</el-descriptions-item>
            <el-descriptions-item label="实付金额">
              <span class="pay-amount">¥{{ order.payAmount }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="收货地址">{{ order.deliveryAddress }}</el-descriptions-item>
            <el-descriptions-item label="收货人">{{ order.receiverName }} {{ order.receiverPhone }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 支付方式 -->
        <div class="payment-section">
          <h3>选择支付方式</h3>

          <el-radio-group v-model="selectedPaymentMethod" class="payment-methods">
            <el-radio :label="1" class="payment-option">
              <div class="payment-icon wechat">💚</div>
              <div class="payment-name">微信支付</div>
            </el-radio>
            <el-radio :label="2" class="payment-option">
              <div class="payment-icon alipay">💙</div>
              <div class="payment-name">支付宝</div>
            </el-radio>
            <el-radio :label="3" class="payment-option">
              <div class="payment-icon bank">🏦</div>
              <div class="payment-name">银行卡</div>
            </el-radio>
          </el-radio-group>
        </div>

        <!-- 支付按钮 -->
        <div class="pay-action">
          <el-button
              type="success"
              size="large"
              @click="handlePay"
              :loading="paying"
              style="width: 100%;"
          >
            {{ getPayButtonText() }}
          </el-button>
        </div>

        <!-- 模拟支付提示 -->
        <el-alert
            title="💡 开发环境提示"
            type="info"
            :closable="false"
            show-icon
            class="dev-tip"
        >
          <p>当前为开发环境，点击支付按钮将：</p>
          <ol>
            <li>模拟调用微信支付接口</li>
            <li>显示支付成功/失败结果</li>
            <li>实际项目中会跳转到真实支付页面</li>
          </ol>
        </el-alert>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getOrderDetail, payOrder } from '@/api'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const order = ref({})
const loading = ref(true)
const paying = ref(false)
const selectedPaymentMethod = ref(parseInt(route.query.method) || 1)

onMounted(async () => {
  try {
    const orderId = route.params.id
    console.log('加载订单详情，ID:', orderId)

    const res = await getOrderDetail(orderId)
    console.log('订单详情:', res)
    order.value = res
  } catch (error) {
    console.error('加载订单失败:', error)
    ElMessage.error('订单不存在')
  } finally {
    loading.value = false
  }
})

const getPayButtonText = () => {
  switch (selectedPaymentMethod.value) {
    case 1: return '💚 微信支付'
    case 2: return '💙 支付宝支付'
    case 3: return '🏦 银行卡支付'
    default: return '立即支付'
  }
}

const handlePay = async () => {
  try {
    paying.value = true

    const orderId = route.params.id

    console.log('=== 开始支付 ===')
    console.log('订单 ID:', orderId)
    console.log('支付方式:', selectedPaymentMethod.value)

    // 调用后端支付接口
    await payOrder(orderId, {
      paymentMethod: selectedPaymentMethod.value
    })

    // 模拟跳转第三方支付
    if (selectedPaymentMethod.value === 1) {
      // 微信支付
      console.log('跳转到微信支付...')

      // TODO: 实际项目中，这里应该调用微信支付 API
      // 例如：window.location.href = wechatPayUrl

      // 开发环境：模拟延迟后显示成功
      await new Promise(resolve => setTimeout(resolve, 2000))

      ElMessage.success('支付成功！')
      router.push(`/order/${orderId}?status=paid`)

    } else if (selectedPaymentMethod.value === 2) {
      // 支付宝
      console.log('跳转到支付宝...')

      // TODO: 实际项目中，调用支付宝 API

      await new Promise(resolve => setTimeout(resolve, 2000))
      ElMessage.success('支付成功！')
      router.push(`/order/${orderId}?status=paid`)

    } else {
      // 银行卡
      ElMessage.success('支付成功！')
      router.push(`/order/${orderId}?status=paid`)
    }

  } catch (error) {
    console.error('支付失败:', error)
    ElMessage.error(error.response?.data?.message || '支付失败')
  } finally {
    paying.value = false
  }
}

const goHome = () => {
  router.push('/')
}
</script>

<style scoped lang="scss">
.pay-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
}

.pay-card {
  h2, h3 {
    margin-top: 0;
  }
}

.loading, .empty {
  padding: 40px 0;
}

.order-info {
  margin-bottom: 30px;

  .pay-amount {
    color: #f56c6c;
    font-size: 20px;
    font-weight: bold;
  }
}

.payment-section {
  margin-bottom: 30px;

  .payment-methods {
    display: flex;
    flex-direction: column;
    gap: 16px;

    .payment-option {
      border: 2px solid #e4e7ed;
      border-radius: 8px;
      padding: 16px;
      transition: all 0.3s;

      &:hover {
        border-color: #67c23a;
      }

      &.is-checked {
        border-color: #67c23a;
        background: #f0f9eb;
      }

      .payment-icon {
        display: inline-block;
        font-size: 24px;
        margin-right: 8px;

        &.wechat { color: #07c160; }
        &.alipay { color: #1677ff; }
        &.bank { color: #f56c6c; }
      }

      .payment-name {
        display: inline-block;
        font-size: 16px;
        font-weight: 500;
      }
    }
  }
}

.pay-action {
  margin-bottom: 20px;
}

.dev-tip {
  ol {
    margin: 8px 0;
    padding-left: 20px;

    li {
      margin: 4px 0;
    }
  }
}
</style>


<template>
  <div class="merchant-dashboard">
    <el-header class="header">
      <h1>商家后台 - 订单管理</h1>
      <el-button @click="logout">退出登录</el-button>
    </el-header>

    <el-main>
      <!-- 订单状态筛选 -->
      <div class="filter-section">
        <el-radio-group v-model="statusFilter" @change="loadOrders">
          <el-radio-button :label="null">全部</el-radio-button>
          <el-radio-button :label="1">待制作</el-radio-button>
          <el-radio-button :label="2">待配送</el-radio-button>
          <el-radio-button :label="3">配送中</el-radio-button>
          <el-radio-button :label="4">已完成</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 订单列表 -->
      <el-table :data="orders" border style="margin-top: 20px;">
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column label="用户信息" width="150">
          <template #default="{ row }">
            <div>{{ row.userName || '未知用户' }}</div>
            <div style="font-size: 12px; color: #999;">{{ row.receiverPhone }}</div>
          </template>
        </el-table-column>
        <el-table-column label="订单金额" width="100">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold;">¥{{ row.payAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="配送地址" min-width="200">
          <template #default="{ row }">
            {{ row.deliveryAddress }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="骑手" width="100">
          <template #default="{ row }">
            {{ row.riderName || '未分配' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button
                v-if="row.status === 1"
                type="success"
                size="small"
                @click="confirmOrder(row.id)"
            >
              接单制作
            </el-button>

            <el-button
                v-if="row.status === 2"
                type="primary"
                size="small"
                @click="showAssignRiderDialog(row)"
            >
              分配骑手
            </el-button>

            <el-button
                v-if="row.status === 3"
                type="info"
                size="small"
                @click="contactRider(row)"
            >
              联系骑手
            </el-button>

            <el-button
                type="primary"
                size="small"
                @click="viewOrderDetail(row.id)"
            >
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分配骑手对话框 -->
      <el-dialog
          v-model="assignDialogVisible"
          title="分配骑手"
          width="500px"
      >
        <el-form :model="assignForm">
          <el-form-item label="当前订单">
            <span>{{ currentOrder.orderNo }}</span>
          </el-form-item>
          <el-form-item label="选择骑手">
            <el-select v-model="assignForm.riderId" placeholder="请选择骑手" style="width: 100%;">
              <el-option
                  v-for="rider in riders"
                  :key="rider.id"
                  :label="rider.nickname"
                  :value="rider.id"
              >
                <span>{{ rider.nickname }}</span>
                <span style="float: right; color: #8492a6; font-size: 13px;">{{ rider.phone }}</span>
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="assignDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmAssignRider">确定</el-button>
        </template>
      </el-dialog>
    </el-main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMerchantOrders, confirmMerchantOrder, assignRiderToOrder } from '@/api/index.js'

const router = useRouter()
const orders = ref([])
const statusFilter = ref(null)
const assignDialogVisible = ref(false)
const currentOrder = ref({})
const assignForm = ref({
  orderId: null,
  riderId: null
})

// 骑手列表 - 从后端加载
const riders = ref([])

onMounted(() => {
  loadOrders()
  loadRiders()
})

// 加载骑手列表
const loadRiders = async () => {
  try {
    // TODO: 调用后端 API 获取骑手列表
    // const res = await getRiderList()
    // riders.value = res

    // 临时方案：使用固定的骑手数据
    riders.value = [
      { id: 3, nickname: '快递小哥', phone: '13800138003' },
      { id: 4, nickname: '闪电骑手', phone: '13800138004' },
      { id: 5, nickname: '跑腿小哥', phone: '13800138005' }
    ]
  } catch (error) {
    console.error('加载骑手列表失败:', error)
  }
}

const loadOrders = async () => {
  try {
    const userInfoStr = localStorage.getItem('userInfo')

    if (!userInfoStr) {
      console.error('=== ❌ 未登录，userInfo 为空')
      ElMessage.error('请先登录')
      router.push('/login')
      return
    }

    const userInfo = JSON.parse(userInfoStr)

    if (!userInfo || !userInfo.id) {
      console.error('=== ❌ userInfo 格式不正确:', userInfo)
      ElMessage.error('用户信息不完整，请重新登录')
      router.push('/login')
      return
    }

    console.log('========================================')
    console.log('=== 开始加载商家订单')
    console.log('=== 用户 ID:', userInfo.id)

    const userId = userInfo.id

    // 直接测试 API 调用
    console.log('=== 准备调用 API: /api/orders/merchant/by-user/' + userId)

    const res = await getMerchantOrders(userId, statusFilter.value)

    console.log('=== 商家订单响应 ===')
    console.log('响应数据:', res)
    console.log('订单数量:', res ? res.length : 0)

    orders.value = res || []
    console.log('========================================')

  } catch (error) {
    console.error('=== ❌ 加载订单失败:')
    console.error('错误对象:', error)
    console.error('错误响应:', error.response)
    console.error('错误状态:', error.response?.status)
    console.error('错误数据:', error.response?.data)

    const errorMsg = error.response?.data?.message || error.message || '加载失败'
    ElMessage.error(errorMsg)
    orders.value = []
  }
}

const confirmOrder = async (orderId) => {
  try {
    await ElMessageBox.confirm('确认接单吗？', '接单确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await confirmMerchantOrder(orderId)

    ElMessage.success('接单成功')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error(error.response?.data?.message || '接单失败')
    }
  }
}

const showAssignRiderDialog = (order) => {
  currentOrder.value = order
  assignForm.value.orderId = order.id
  assignForm.value.riderId = null
  assignDialogVisible.value = true
}

const confirmAssignRider = async () => {
  try {
    if (!assignForm.value.riderId) {
      ElMessage.warning('请选择骑手')
      return
    }

    console.log('=== 分配骑手 ===')
    console.log('订单 ID:', assignForm.value.orderId)
    console.log('骑手 ID:', assignForm.value.riderId)

    await assignRiderToOrder(assignForm.value.orderId, assignForm.value.riderId)

    ElMessage.success('分配成功')
    assignDialogVisible.value = false
    loadOrders()
  } catch (error) {
    console.error('分配失败:', error)
    ElMessage.error(error.response?.data?.message || '分配失败')
  }
}

const contactRider = (order) => {
  ElMessage.info(`联系骑手：${order.riderName}`)
}

const viewOrderDetail = (orderId) => {
  router.push(`/order/${orderId}`)
}

const getStatusType = (status) => {
  const types = {
    0: 'info',
    1: 'warning',
    2: 'primary',
    3: 'success',
    4: 'success',
    5: 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    0: '待支付',
    1: '待制作',
    2: '待配送',
    3: '配送中',
    4: '已完成',
    5: '已取消'
  }
  return texts[status] || '未知'
}

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  router.push('/login')
}
</script>

<style scoped lang="scss">.merchant-dashboard {
  min-height: 100vh;
  background: #f5f5f5;

  .header {
    background: #fff;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    h1 {
      margin: 0;
      font-size: 20px;
    }
  }

  .filter-section {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
  }
}
</style>
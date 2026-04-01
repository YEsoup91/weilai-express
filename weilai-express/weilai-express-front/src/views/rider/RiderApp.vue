<template>
  <div class="rider-app">
    <el-header class="header">
      <h1>骑手端 - 配送任务</h1>
      <el-dropdown>
        <span class="user-info">
          {{ userInfo?.nickname || userInfo?.username }}
          <el-icon><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </el-header>

    <el-main class="main">
      <!-- 订单状态筛选 -->
      <el-card class="filter-card">
        <template #header>
          <div class="card-header">
            <span>订单筛选</span>
          </div>
        </template>
        <el-radio-group v-model="taskFilter" @change="loadTasks">
          <el-radio-button :label="null">全部</el-radio-button>
          <el-radio-button :label="2">可抢单</el-radio-button>
          <el-radio-button :label="3">配送中</el-radio-button>
          <el-radio-button :label="4">已完成</el-radio-button>
        </el-radio-group>
      </el-card>

      <!-- 可抢单列表 -->
      <div v-if="taskFilter === 2 || taskFilter === null" class="order-list">
        <el-card class="list-header">
          <h3>📦 可抢订单（{{ availableOrders.length }}）</h3>
        </el-card>

        <el-table :data="availableOrders" border style="margin-top: 20px;" v-loading="loading">
          <el-table-column prop="orderNo" label="订单号" width="200" />
          <el-table-column label="配送费" width="100">
            <template #default="{ row }">
              <span style="color: #f56c6c; font-weight: bold;">¥{{ row.deliveryFee }}</span>
            </template>
          </el-table-column>
          <el-table-column label="订单金额" width="100">
            <template #default="{ row }">
              <span style="color: #67c23a;">¥{{ row.payAmount }}</span>
            </template>
          </el-table-column>
          <el-table-column label="取餐地址" min-width="200">
            <template #default="{ row }">
              {{ row.merchantAddress }}
            </template>
          </el-table-column>
          <el-table-column label="送餐地址" min-width="200">
            <template #default="{ row }">
              {{ row.deliveryAddress }}
            </template>
          </el-table-column>
          <el-table-column label="顾客电话" width="150">
            <template #default="{ row }">
              {{ row.receiverPhone }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button type="success" size="small" @click="grabOrder(row.id)">
                立即抢单
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 配送中订单 -->
      <div v-if="taskFilter === 3 || taskFilter === null" class="order-list">
        <el-card class="list-header">
          <h3>🚴 配送中订单（{{ deliveringOrders.length }}）</h3>
        </el-card>

        <el-table :data="deliveringOrders" border style="margin-top: 20px;">
          <el-table-column prop="orderNo" label="订单号" width="200" />
          <el-table-column label="订单金额" width="100">
            <template #default="{ row }">
              <span style="color: #67c23a;">¥{{ row.payAmount }}</span>
            </template>
          </el-table-column>
          <el-table-column label="取餐地址" min-width="200">
            <template #default="{ row }">
              {{ row.merchantAddress }}
            </template>
          </el-table-column>
          <el-table-column label="送餐地址" min-width="200">
            <template #default="{ row }">
              {{ row.deliveryAddress }}
            </template>
          </el-table-column>
          <el-table-column label="顾客电话" width="150">
            <template #default="{ row }">
              {{ row.receiverPhone }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="callCustomer(row)">
                联系顾客
              </el-button>
              <el-button type="success" size="small" @click="completeDelivery(row.id)">
                确认送达
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 已完成订单 -->
      <div v-if="taskFilter === 4 || taskFilter === null" class="order-list">
        <el-card class="list-header">
          <h3>✅ 已完成订单（{{ completedOrders.length }}）</h3>
        </el-card>

        <el-table :data="completedOrders" border style="margin-top: 20px;">
          <el-table-column prop="orderNo" label="订单号" width="200" />
          <el-table-column label="配送费" width="100">
            <template #default="{ row }">
              <span style="color: #f56c6c; font-weight: bold;">¥{{ row.deliveryFee }}</span>
            </template>
          </el-table-column>
          <el-table-column label="订单金额" width="100">
            <template #default="{ row }">
              <span style="color: #67c23a;">¥{{ row.payAmount }}</span>
            </template>
          </el-table-column>
          <el-table-column label="送餐地址" min-width="200">
            <template #default="{ row }">
              {{ row.deliveryAddress }}
            </template>
          </el-table-column>
          <el-table-column label="完成时间" width="180">
            <template #default="{ row }">
              {{ formatTime(row.finishTime) }}
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag type="success">已完成</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 空状态提示 -->
      <el-empty v-if="!loading && availableOrders.length === 0 && deliveringOrders.length === 0 && completedOrders.length === 0" description="暂无订单" />
    </el-main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRiderAvailableOrders, getRiderDeliveringOrders, getRiderCompletedOrders, grabOrder as apiGrabOrder, completeDelivery as apiCompleteDelivery } from '@/api/index.js'

const router = useRouter()
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
const taskFilter = ref(null)
const availableOrders = ref([])
const deliveringOrders = ref([])
const completedOrders = ref([])
const loading = ref(false)

onMounted(() => {
  loadTasks()
})

const loadTasks = async () => {
  try {
    const riderId = userInfo.value.id

    console.log('========================================')
    console.log('=== 开始加载骑手任务')
    console.log('=== 骑手 ID:', riderId)
    console.log('=== 筛选条件:', taskFilter.value)

    loading.value = true

    // 加载可抢单列表
    if (taskFilter.value === 2 || taskFilter.value === null) {
      console.log('=== 正在加载可抢订单...')
      const availableRes = await getRiderAvailableOrders(taskFilter.value === 2 ? 2 : null)
      availableOrders.value = Array.isArray(availableRes) ? availableRes : []
      console.log('=== 可抢订单数量:', availableOrders.value.length)
    }

    // 加载配送中订单
    if (taskFilter.value === 3 || taskFilter.value === null) {
      console.log('=== 正在加载配送中订单...')
      const deliveringRes = await getRiderDeliveringOrders(riderId)
      deliveringOrders.value = Array.isArray(deliveringRes) ? deliveringRes : []
      console.log('=== 配送中订单数量:', deliveringOrders.value.length)
    }

    // 加载已完成订单
    if (taskFilter.value === 4 || taskFilter.value === null) {
      console.log('=== 正在加载已完成订单...')
      const completedRes = await getRiderCompletedOrders(riderId)
      completedOrders.value = Array.isArray(completedRes) ? completedRes : []
      console.log('=== 已完成订单数量:', completedOrders.value.length)
    }

    console.log('========================================')

  } catch (error) {
    console.error('=== ❌ 加载任务失败:', error)
    ElMessage.error(error.response?.data?.message || '加载任务失败')
    availableOrders.value = []
    deliveringOrders.value = []
    completedOrders.value = []
  } finally {
    loading.value = false
  }
}

const grabOrder = async (orderId) => {
  try {
    await ElMessageBox.confirm('确认抢这个订单吗？', '抢单确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const riderId = userInfo.value.id
    await apiGrabOrder(orderId, riderId)

    ElMessage.success('抢单成功')
    loadTasks()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error(error.response?.data?.message || '抢单失败')
    }
  }
}

const callCustomer = (order) => {
  ElMessage.info(`拨打顾客电话：${order.receiverPhone}`)
}

const completeDelivery = async (orderId) => {
  try {
    await ElMessageBox.confirm('确认已送达吗？', '送达确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await apiCompleteDelivery(orderId)

    ElMessage.success('送达成功')
    loadTasks()
  } catch (error) {
    console.error(error)
    ElMessage.error(error.response?.data?.message || '操作失败')
  }
}

const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  router.push('/login')
}
</script>

<style scoped lang="scss">
.rider-app {
  min-height: 100vh;
  background: #f5f7fa;
}

.header {
  background: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0 20px;

  h1 {
    margin: 0;
    font-size: 20px;
    color: #333;
  }

  .user-info {
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 4px;
    color: #666;
  }
}

.main {
  padding: 20px;
}

.filter-card {
  margin-bottom: 20px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}

.order-list {
  .list-header {
    margin-bottom: 20px;

    h3 {
      margin: 0;
      color: #333;
      font-size: 16px;
    }
  }
}
</style>

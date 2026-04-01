<template>
  <div class="admin-container">
    <el-container>
      <el-aside width="200px" class="sidebar">
        <h2 class="logo">味来速递</h2>
        <el-menu
            default-active="1"
            background-color="#304156"
            text-color="#bfcbd9"
            active-text-color="#409EFF"
        >
          <el-menu-item index="1">
            <el-icon><HomeFilled /></el-icon>
            <span>控制台</span>
          </el-menu-item>
          <el-menu-item index="2">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="3">
            <el-icon><Shop /></el-icon>
            <span>商家管理</span>
          </el-menu-item>
          <el-menu-item index="4">
            <el-icon><Food /></el-icon>
            <span>菜品管理</span>
          </el-menu-item>
          <el-menu-item index="5">
            <el-icon><List /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item index="6">
            <el-icon><Setting /></el-icon>
            <span>权限管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header class="header">
          <h3>管理后台</h3>
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
          <el-card>
            <template #header>
              <h4>数据概览</h4>
            </template>
            <el-row :gutter="20">
              <el-col :span="6">
                <el-statistic title="用户总数" :value="stats.userCount" />
              </el-col>
              <el-col :span="6">
                <el-statistic title="商家总数" :value="stats.merchantCount" />
              </el-col>
              <el-col :span="6">
                <el-statistic title="订单总数" :value="stats.orderCount" />
              </el-col>
              <el-col :span="6">
                <el-statistic title="今日营收" :value="stats.todayRevenue" precision="2" />
              </el-col>
            </el-row>
          </el-card>

          <el-card style="margin-top: 20px;">
            <template #header>
              <h4>快捷操作</h4>
            </template>
            <el-row :gutter="20">
              <el-col :span="8">
                <el-button type="primary" block @click="showAssignRoleDialog">分配权限</el-button>
              </el-col>
              <el-col :span="8">
                <el-button type="success" block @click="viewOrders">查看订单</el-button>
              </el-col>
              <el-col :span="8">
                <el-button type="warning" block @click="manageDishes">管理菜品</el-button>
              </el-col>
            </el-row>
          </el-card>

          <el-card style="margin-top: 20px;">
            <template #header>
              <h4>最近订单</h4>
            </template>
            <el-table :data="recentOrders" border stripe>
              <el-table-column prop="orderNo" label="订单号" />
              <el-table-column prop="userName" label="用户" />
              <el-table-column prop="amount" label="金额">
                <template #default="{ row }">¥{{ row.amount }}</template>
              </el-table-column>
              <el-table-column prop="status" label="状态">
                <template #default="{ row }">
                  <el-tag :type="getStatusType(row.status)">
                    {{ getStatusText(row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="下单时间" />
            </el-table>
          </el-card>
        </el-main>
      </el-container>
    </el-container>

    <!-- 权限分配对话框 -->
    <el-dialog v-model="assignRoleDialogVisible" title="分配权限" width="500px">
      <el-form :model="assignRoleForm" label-width="100px">
        <el-form-item label="选择用户">
          <el-select v-model="assignRoleForm.userId" placeholder="请选择用户" style="width: 100%">
            <el-option
                v-for="user in userList"
                :key="user.id"
                :label="user.username"
                :value="user.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="分配角色">
          <el-select v-model="assignRoleForm.roleId" placeholder="请选择角色" style="width: 100%">
            <el-option
                v-for="role in roleList"
                :key="role.id"
                :label="role.roleName"
                :value="role.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignRoleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssignRole">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserList, assignRole as apiAssignRole } from '@/api/index.js'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

const stats = ref({
  userCount: 1024,
  merchantCount: 56,
  orderCount: 8976,
  todayRevenue: 12345.67
})

const recentOrders = ref([
  { orderNo: 'ORD20260327001', userName: '张三', amount: 45.50, status: 3, createTime: '2026-03-27 10:30:00' },
  { orderNo: 'ORD20260327002', userName: '李四', amount: 78.00, status: 1, createTime: '2026-03-27 11:15:00' },
  { orderNo: 'ORD20260327003', userName: '王五', amount: 32.80, status: 4, createTime: '2026-03-27 12:00:00' },
  { orderNo: 'ORD20260327004', userName: '赵六', amount: 156.00, status: 2, createTime: '2026-03-27 13:20:00' }
])

const assignRoleDialogVisible = ref(false)
const assignRoleForm = ref({
  userId: null,
  roleId: null
})

const userList = ref([])
const roleList = ref([
  { id: 1, roleName: '普通用户' },
  { id: 2, roleName: '商家' },
  { id: 3, roleName: '骑手' },
  { id: 4, roleName: '管理员' }
])

onMounted(async () => {
  try {
    const res = await getUserList()
    userList.value = res
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
  return texts[status] || '未知'
}

const showAssignRoleDialog = () => {
  assignRoleDialogVisible.value = true
}

const handleAssignRole = async () => {
  try {
    await apiAssignRole(assignRoleForm.value)
    ElMessage.success('权限分配成功')
    assignRoleDialogVisible.value = false
  } catch (error) {
    console.error(error)
  }
}

const viewOrders = () => {
  router.push('/admin/orders')
}

const manageDishes = () => {
  router.push('/admin/dishes')
}

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  router.push('/login')
}
</script>

<style scoped lang="scss">
.admin-container {
  height: 100vh;
}

.el-container {
  height: 100%;
}

.sidebar {
  background-color: #304156;

  .logo {
    color: #fff;
    text-align: center;
    padding: 20px 0;
    margin: 0;
    background-color: #2b3a4b;
  }
}

.header {
  background: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0 20px;

  h3 {
    margin: 0;
  }

  .user-info {
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 4px;
  }
}

.main {
  background: #f5f7fa;
  padding: 20px;
}
</style>

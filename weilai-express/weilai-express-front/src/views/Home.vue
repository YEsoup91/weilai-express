<template>
  <div class="home-container">
    <el-header class="header">
      <h1>味来速递</h1>
      <div class="header-right">
        <el-button @click="goCart">
          <el-icon><ShoppingCart /></el-icon>
          购物车
        </el-button>
        <el-dropdown>
          <span class="user-info">
            {{ userInfo?.nickname || userInfo?.username }}
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>我的订单</el-dropdown-item>
              <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <el-main class="main">
      <el-input
          v-model="searchText"
          placeholder="搜索商家"
          prefix-icon="Search"
          size="large"
          class="search-input"
      />

      <div class="merchant-list">
        <h2>推荐商家</h2>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="merchant in merchants" :key="merchant.id">
            <el-card class="merchant-card" shadow="hover" @click="goMerchant(merchant.id)">
              <div class="merchant-logo-placeholder">🏪</div>
              <h3>{{ merchant.name }}</h3>
              <p class="merchant-desc">{{ merchant.description || '' }}</p>
              <div class="merchant-info">
                <span>配送费 ¥{{ merchant.deliveryFee }}</span>
                <span>起送价 ¥{{ merchant.minOrderAmount }}</span>
              </div>
              <div class="merchant-sales">
                <el-rate v-model="merchant.rating" disabled />
                <span>月售 {{ merchant.salesCount }}单</span>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMerchantList } from '@/api'

const router = useRouter()
const searchText = ref('')
const merchants = ref([])

// 修复：安全地获取 userInfo
const getUserInfo = () => {
  try {
    const info = localStorage.getItem('userInfo')
    if (!info) {
      console.warn('localStorage 中没有 userInfo，跳转到登录页')
      router.push('/login')
      return null
    }
    return JSON.parse(info)
  } catch (e) {
    console.error('解析 userInfo 失败:', e)
    // 如果解析失败，清除并跳转登录
    localStorage.removeItem('userInfo')
    localStorage.removeItem('token')
    router.push('/login')
    return null
  }
}

const userInfo = ref(getUserInfo())

onMounted(async () => {
  // 如果没有 userInfo，不加载数据
  if (!userInfo.value) {
    return
  }

  try {
    console.log('开始加载商家列表...')
    const res = await getMerchantList()
    console.log('商家列表数据:', res)
    merchants.value = res || []
  } catch (error) {
    console.error('加载商家列表失败:', error)
  }
})

const goCart = () => {
  router.push('/cart')
}

const goMerchant = (id) => {
  router.push(`/merchant/${id}`)
}

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  router.push('/login')
}
</script>

<style scoped lang="scss">
.home-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  h1 {
    color: #667eea;
    margin: 0;
  }

  .header-right {
    display: flex;
    gap: 16px;
    align-items: center;

    .user-info {
      cursor: pointer;
      display: flex;
      align-items: center;
      gap: 4px;
    }
  }
}

.main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;

  .search-input {
    margin-bottom: 20px;
  }

  .merchant-list {
    h2 {
      margin-bottom: 20px;
      color: #333;
    }

    .merchant-card {
      margin-bottom: 20px;
      cursor: pointer;
      transition: transform 0.3s;

      &:hover {
        transform: translateY(-4px);
      }

      .merchant-logo {
        width: 80px;
        height: 80px;
        border-radius: 8px;
        object-fit: cover;
      }

      .merchant-logo-placeholder {
        width: 80px;
        height: 80px;
        border-radius: 8px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 36px;
        margin-bottom: 12px;
      }

      h3 {
        margin: 12px 0 8px;
        font-size: 16px;
      }

      .merchant-desc {
        color: #666;
        font-size: 14px;
        margin-bottom: 12px;
      }

      .merchant-info {
        display: flex;
        justify-content: space-between;
        color: #999;
        font-size: 13px;
        margin-bottom: 8px;
      }

      .merchant-sales {
        display: flex;
        justify-content: space-between;
        align-items: center;
        font-size: 13px;
        color: #666;
      }
    }
  }
}
</style>
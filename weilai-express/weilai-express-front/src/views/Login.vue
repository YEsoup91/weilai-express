<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2>味来速递 - 用户登录</h2>
      </template>

      <el-form :model="loginForm" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              prefix-icon="User"
              size="large"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              size="large"
              show-password
              @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <el-button
              type="primary"
              size="large"
              :loading="loading"
              @click="handleLogin"
              style="width: 100%"
          >
            登录
          </el-button>
        </el-form-item>

        <div class="login-footer">
          <span>还没有账号？</span>
          <router-link to="/register">立即注册</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于 6 位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      console.log('发送登录请求:', loginForm)
      const res = await login(loginForm)

      console.log('登录响应:', res)
      console.log('userInfo:', res.userInfo)
      console.log('token:', res.token)

      if (!res.userInfo) {
        ElMessage.error('登录失败：未获取到用户信息')
        console.error('后端返回的 userInfo 为空:', res)
        return
      }

      localStorage.setItem('token', res.token || '')
      localStorage.setItem('userInfo', JSON.stringify(res.userInfo))

      console.log('=== 登录成功 ===')
      console.log('已保存 userInfo:', localStorage.getItem('userInfo'))
      console.log('解析后的 userInfo:', res.userInfo)
      console.log('roleName 的值:', res.userInfo.roleName)
      console.log('roleCode 的值:', res.userInfo.roleCode)

      ElMessage.success('登录成功')

      // 根据角色跳转不同页面（优先使用 roleCode）
      const roleName = res.userInfo.roleName
      const roleCode = res.userInfo.roleCode

      console.log('准备根据角色跳转')
      console.log('roleName:', roleName)
      console.log('roleCode:', roleCode)

      // 使用 roleCode 判断（如果有的话）
      if (roleCode === 'ADMIN') {
        console.log('跳转到管理员后台')
        router.push('/admin')
      } else if (roleCode === 'MERCHANT') {
        console.log('跳转到商家后台')
        router.push('/merchant/dashboard')
      } else if (roleCode === 'RIDER') {
        console.log('跳转到骑手端')
        router.push('/rider/app')
      } else {
        console.log('跳转到普通用户首页')
        router.push('/')
      }
    } catch (error) {
      console.error('登录异常:', error)
      console.error('错误详情:', error.response?.data || error.message)
      ElMessage.error(error.response?.data?.message || error.message || '登录失败')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped lang="scss">
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;

  h2 {
    text-align: center;
    margin: 0;
    color: #333;
  }
}

.login-footer {
  text-align: center;
  margin-top: 16px;

  a {
    color: #667eea;
    text-decoration: none;
    margin-left: 8px;
  }
}
</style>

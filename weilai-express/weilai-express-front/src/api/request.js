import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
    baseURL: '/api',
    timeout: 10000
})

request.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    error => {
        return Promise.reject(error)
    }
)

request.interceptors.response.use(
    response => {
        const res = response.data
        
        // 添加调试日志
        console.log('响应数据:', res)
        console.log('响应状态码:', res.code)
        
        if (res.code !== 200) {
            ElMessage.error(res.message || '请求失败')
            if (res.code === 401) {
                router.push('/login')
            }
            return Promise.reject(new Error(res.message || '请求失败'))
        }
        return res.data
    },
    error => {
        // 显示更详细的错误信息
        console.error('请求错误详情:', error)
        console.error('错误响应:', error.response)
        console.error('错误数据:', error.response?.data)
        
        const errorMsg = error.response?.data?.message || error.message || '网络错误'
        ElMessage.error('错误：' + errorMsg)
        return Promise.reject(error)
    }
)

export default request

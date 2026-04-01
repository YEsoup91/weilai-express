import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/Login.vue'),
        meta: { title: '登录' }
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/Register.vue'),
        meta: { title: '注册' }
    },
    {
        path: '/',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页' }
    },
    {
        path: '/order/:id',
        name: 'OrderDetail',
        component: () => import('@/views/OrderDetail.vue'),
        meta: { title: '订单详情' }
    },
    {
        path: '/pay/:id',
        name: 'Pay',
        component: () => import('@/views/Pay.vue'),
        meta: { title: '订单支付' }
    },
    {
        path: '/cart',
        name: 'Cart',
        component: () => import('@/views/Cart.vue'),
        meta: { title: '购物车' }
    },
    {
        path: '/merchant/:id',
        name: 'Merchant',
        component: () => import('@/views/Merchant.vue'),
        meta: { title: '商家详情' }
    },
    {
        path: '/admin',
        name: 'Admin',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '管理后台' }
    },
    {
        path: '/merchant/dashboard',
        name: 'MerchantDashboard',
        component: () => import('@/views/admin/MerchantDashboard.vue'),
        meta: { title: '商家后台' }
    },
    {
        path: '/rider/app',
        name: 'RiderApp',
        component: () => import('@/views/rider/RiderApp.vue'),
        meta: { title: '骑手端' }
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    if (to.meta.title) {
        document.title = to.meta.title + ' - 味来速递'
    }
    next()
})

export default router

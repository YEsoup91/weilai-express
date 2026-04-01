import request from './request'

// 用户相关
export const login = (data) => request.post('/auth/login', data)
export const register = (data) => request.post('/auth/register', data)
export const getUserInfo = () => request.get('/user/info')

// 商家相关
export const getMerchantList = (params) => request.get('/merchants', { params })
export const getMerchantDetail = (id) => request.get(`/merchants/${id}`)

// 菜品相关
export const getDishList = (params) => request.get('/dishes', { params })
export const getDishDetail = (id) => request.get(`/dishes/${id}`)

// 购物车相关
export const getCart = () => request.get('/cart')
export const addToCart = (data) => request.post('/cart', data)
export const updateCartItem = (id, data) => request.put(`/cart/${id}`, data)
export const deleteCartItem = (id) => request.delete(`/cart/${id}`)

// 订单相关
export const createOrder = (data) => request.post('/orders', data)
export const getOrderList = (params) => request.get('/orders', { params })
export const getOrderDetail = (id) => request.get(`/orders/${id}`)
export const payOrder = (id, data) => request.post(`/orders/${id}/pay`, data)
export const cancelOrder = (id) => request.post(`/orders/${id}/cancel`)
export const getMerchantOrders = (userId, status) => request.get(`/orders/merchant/by-user/${userId}`, { params: { status } })
export const confirmMerchantOrder = (id) => request.post(`/orders/${id}/confirm`)
export const assignRiderToOrder = (id, riderId) => request.post(`/orders/${id}/deliver`, { riderId })

export const getRiderAvailableOrders = (status) => request.get('/orders/rider/available', { params: { status } })
export const getRiderDeliveringOrders = (riderId) => request.get(`/orders/rider/${riderId}/delivering`)
export const getRiderCompletedOrders = (riderId) => request.get(`/orders/rider/${riderId}/completed`)
export const grabOrder = (id, riderId) => request.post(`/orders/${id}/grab`, { riderId })
export const completeDelivery = (id) => request.post(`/orders/${id}/complete`)

// 评价相关
export const addReview = (data) => request.post('/reviews', data)
export const getReviewList = (orderId) => request.get(`/reviews/order/${orderId}`)

// 管理员相关
export const assignRole = (data) => request.post('/admin/role', data)
export const getUserList = (params) => request.get('/admin/users', { params })

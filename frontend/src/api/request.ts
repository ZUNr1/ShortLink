import axios, {type AxiosInstance, type AxiosResponse, type InternalAxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import type { Result } from '../types'

const request: AxiosInstance = axios.create({
    baseURL: '/',  // 始终使用相对路径
    timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        const token = localStorage.getItem('accessToken')
        if (token && config.headers) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    (response: AxiosResponse<Result<any>>) => {
        const res = response.data
        if (res.code === 200) {
            return res.data
        } else if (res.code === 401) {
            ElMessage.error(res.message || '登录已过期，请重新登录')
            localStorage.clear()
            window.location.href = '/login'
            return Promise.reject(new Error(res.message))
        } else {
            ElMessage.error(res.message || '请求失败')
            return Promise.reject(new Error(res.message))
        }
    },
// 响应拦截器 - 在 catch 部分添加
    (error) => {
        console.error('Request Error Details:', {
            url: error.config?.url,
            method: error.config?.method,
            status: error.response?.status,
            statusText: error.response?.statusText,
            data: error.response?.data
        })

        if (error.response?.status === 401) {
            ElMessage.error('请先登录')
            localStorage.clear()
            window.location.href = '/login'
        } else if (error.code === 'ERR_NETWORK') {
            ElMessage.error('网络错误，请检查后端服务是否启动（http://localhost:8080）')
        } else {
            ElMessage.error(error.response?.data?.message || error.message || '请求失败')
        }
        return Promise.reject(error)
    }
)

export default request
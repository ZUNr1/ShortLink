import type {LoginRequest, LoginResponse, RegisterRequest} from '../types'
import request from "./request.ts";

export const authApi = {
    // 注册
    register(data: RegisterRequest): Promise<LoginResponse> {
        return request.post('/auth/register', data)
    },

    // 登录
    login(data: LoginRequest): Promise<LoginResponse> {
        return request.post('/auth/login', data)
    },

    // 刷新token
    refresh(refreshToken: string): Promise<LoginResponse> {
        return request.post('/auth/refresh', null, {
            params: { refreshToken }
        })
    }
}
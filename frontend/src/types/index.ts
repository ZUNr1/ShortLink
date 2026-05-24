// 通用响应
export interface Result<T> {
    code: number
    message: string
    data: T
}

// 登录请求
export interface LoginRequest {
    email: string
    password: string
}

// 注册请求
export interface RegisterRequest {
    name: string
    email: string
    password: string
}

// 登录响应
export interface LoginResponse {
    userId: number
    name: string
    email: string
    accessToken: string
    refreshToken: string
    expiresIn: number
}

// 短链响应
export interface LinkResponse {
    id: number
    name: string
    longUrl: string
    shortCode: string
    clickCount: number
    expireAt: string | null
    createdAt: string
    updatedAt: string
    message?: string
    existing?: boolean
}

// 创建短链请求
export interface SwitchUrlRequest {
    url: string
    name: string
    expire?: string
}
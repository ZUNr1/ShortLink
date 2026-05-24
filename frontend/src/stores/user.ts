import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { LoginResponse } from '../types'

export const useUserStore = defineStore('user', () => {
    const userId = ref<number | null>(localStorage.getItem('userId') ? Number(localStorage.getItem('userId')) : null)
    const name = ref<string>(localStorage.getItem('name') || '')
    const email = ref<string>(localStorage.getItem('email') || '')
    const accessToken = ref<string>(localStorage.getItem('accessToken') || '')
    const refreshToken = ref<string>(localStorage.getItem('refreshToken') || '')
    const expiresIn = ref<number>(Number(localStorage.getItem('expiresIn')) || 0)

    const isLoggedIn = computed(() => !!accessToken.value && !!userId.value)

    const setUserInfo = (data: LoginResponse) => {
        userId.value = data.userId
        name.value = data.name
        email.value = data.email
        accessToken.value = data.accessToken
        refreshToken.value = data.refreshToken
        expiresIn.value = data.expiresIn

        localStorage.setItem('userId', String(data.userId))
        localStorage.setItem('name', data.name)
        localStorage.setItem('email', data.email)
        localStorage.setItem('accessToken', data.accessToken)
        localStorage.setItem('refreshToken', data.refreshToken)
        localStorage.setItem('expiresIn', String(data.expiresIn))
    }

    const logout = () => {
        userId.value = null
        name.value = ''
        email.value = ''
        accessToken.value = ''
        refreshToken.value = ''
        expiresIn.value = 0

        localStorage.clear()
    }

    const updateAccessToken = (token: string) => {
        accessToken.value = token
        localStorage.setItem('accessToken', token)
    }

    return {
        userId,
        name,
        email,
        accessToken,
        refreshToken,
        expiresIn,
        isLoggedIn,
        setUserInfo,
        logout,
        updateAccessToken
    }
})
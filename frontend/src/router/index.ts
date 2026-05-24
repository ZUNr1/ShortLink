import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/Login.vue'),
        meta: { requiresAuth: false }
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('../views/Register.vue'),
        meta: { requiresAuth: false }
    },
    {
        path: '/',
        name: 'Layout',
        component: () => import('../layouts/MainLayout.vue'),
        meta: { requiresAuth: true },
        children: [
            {
                path: '',
                name: 'LinkManage',
                component: () => import('../views/LinkManager.vue')
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫 - 不使用 next 回调
router.beforeEach((to, _from) => {
    const token = localStorage.getItem('accessToken')
    const requiresAuth = to.meta.requiresAuth !== false

    if (requiresAuth && !token) {
        return '/login'
    } else if ((to.path === '/login' || to.path === '/register') && token) {
        return '/'
    }
    return true
})

export default router
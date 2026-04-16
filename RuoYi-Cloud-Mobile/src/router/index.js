import { createRouter, createWebHashHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    redirect: '/workbench'
  },
  {
    path: '/workbench',
    name: 'Workbench',
    component: () => import('../views/Workbench.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/customers',
    name: 'Customers',
    component: () => import('../views/Customers.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/log',
    name: 'Log',
    component: () => import('../views/Log.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/Profile.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 全局导航守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    // 需要登录但未登录，跳转到登录页
    next({
      path: '/login',
      query: { redirect: to.fullPath }
    })
  } else if (to.path === '/login' && authStore.isLoggedIn) {
    // 已登录用户访问登录页，重定向到工作台
    next('/workbench')
  } else {
    next()
  }
})

export default router
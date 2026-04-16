// src/stores/auth.js
import { defineStore } from 'pinia'
import { login as loginApi, getUserInfo, logout as logoutApi } from '@/api/auth'
import { showToast } from 'vant'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('access_token') || '',
    userInfo: JSON.parse(localStorage.getItem('user_info') || 'null'),
    roles: JSON.parse(localStorage.getItem('user_roles') || '[]'),
    permissions: JSON.parse(localStorage.getItem('user_permissions') || '[]')
  }),
  
  getters: {
    isLoggedIn: (state) => !!state.token
  },
  
  actions: {
    /**
     * 登录（支持验证码）
     * @param {Object} credentials - 包含 username, password, code, uuid
     */
    async login(credentials) {
      try {
        const res = await loginApi(
          credentials.username,
          credentials.password,
          credentials.code || '',
          credentials.uuid || ''
        )
        
        this.token = res.data.access_token
        localStorage.setItem('access_token', this.token)
        
        await this.fetchUserInfo()
        
        showToast({
          message: '登录成功',
          type: 'success',
          duration: 1500
        })
        
        return res
      } catch (error) {
        this.clearUserData()
        throw error
      }
    },
    
    /**
     * 获取用户信息
     * 实际后端返回结构: { code, msg, user, roles, permissions, ... }
     */
    async fetchUserInfo() {
      try {
        const res = await getUserInfo()
        
        console.log('===== 获取用户信息原始响应 =====')
        console.log('完整响应:', res)
        console.log('=============================')
        
        if (!res) {
          throw new Error('用户信息响应为空')
        }
        
        this.userInfo = res.user || {}
        this.roles = res.roles || []
        this.permissions = res.permissions || []
        
        this.persistUserData()
        
        console.log('解析后的用户信息:', this.userInfo)
        console.log('角色列表:', this.roles)
        console.log('权限列表:', this.permissions)
        
        return res
      } catch (error) {
        console.error('获取用户信息失败:', error)
        throw error
      }
    },
    
    /**
     * 持久化用户数据到本地存储
     */
    persistUserData() {
      localStorage.setItem('user_info', JSON.stringify(this.userInfo))
      localStorage.setItem('user_roles', JSON.stringify(this.roles))
      localStorage.setItem('user_permissions', JSON.stringify(this.permissions))
    },
    
    /**
     * 退出登录
     */
    async logout() {
      try {
        await logoutApi()
      } catch (error) {
        console.warn('退出接口调用失败:', error)
      } finally {
        this.clearUserData()
        showToast('已退出登录')
        window.location.hash = '#/login'
      }
    },
    
    /**
     * 清除本地数据
     */
    clearUserData() {
      this.token = ''
      this.userInfo = null
      this.roles = []
      this.permissions = []
      localStorage.removeItem('access_token')
      localStorage.removeItem('user_info')
      localStorage.removeItem('user_roles')
      localStorage.removeItem('user_permissions')
    }
  }
})
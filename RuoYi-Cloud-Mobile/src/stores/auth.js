// src/stores/auth.js
import { defineStore } from 'pinia'
import { login as loginApi, getUserInfo, logout as logoutApi } from '@/api/auth'
import { showToast } from 'vant'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('access_token') || '',
    userInfo: null,
    roles: [],
    permissions: []
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
     */
    async fetchUserInfo() {
      try {
        const res = await getUserInfo()
        this.userInfo = res.user
        this.roles = res.roles || []
        this.permissions = res.permissions || []
        return res
      } catch (error) {
        throw error
      }
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
    }
  }
})
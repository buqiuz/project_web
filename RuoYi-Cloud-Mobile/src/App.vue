<template>
  <div id="app-container">
    <!-- 未登录：只显示登录页 -->
    <template v-if="!authStore.isLoggedIn">
      <router-view />
    </template>
    
    <!-- 已登录：显示主框架（包含底部导航） -->
    <template v-else>
      <router-view v-slot="{ Component }">
        <keep-alive include="Workbench,Customers,Log,Profile">
          <component :is="Component" />
        </keep-alive>
      </router-view>
      
      <!-- 底部导航栏 -->
      <van-tabbar v-model="active" route>
        <van-tabbar-item icon="home-o" to="/workbench">工作台</van-tabbar-item>
        <van-tabbar-item icon="friends-o" to="/customers">客户</van-tabbar-item>
        <van-tabbar-item icon="notes-o" to="/log">日志</van-tabbar-item>
        <van-tabbar-item icon="user-o" to="/profile">我的</van-tabbar-item>
      </van-tabbar>
    </template>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from './stores/auth'

const route = useRoute()
const authStore = useAuthStore()
const active = ref(0)

watch(() => route.path, (path) => {
  const map = {
    '/workbench': 0,
    '/customers': 1,
    '/log': 2,
    '/profile': 3
  }
  active.value = map[path] ?? 0
}, { immediate: true })
</script>

<style scoped>
#app-container {
  padding-bottom: 50px; /* 给 tabbar 留空间 */
  min-height: 100vh;
  background-color: #f5f7fa;
}

/* 登录页不需要内边距，由页面自己控制 */
#app-container:has(.login-container) {
  padding-bottom: 0;
}
</style>
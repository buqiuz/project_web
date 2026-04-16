<template>
  <div class="page-container">
    <van-row align="center" class="profile-header">
      <van-image round width="60" height="60" :src="store.currentUser.avatar" />
      <div class="user-info">
        <h3>{{ store.currentUser.name }}</h3>
        <van-tag type="primary">{{ store.currentUser.role }} · {{ store.currentUser.department }}</van-tag>
      </div>
    </van-row>

    <van-cell-group inset title="📈 我的业绩">
      <van-cell title="累计客户数" :value="store.performance.totalCustomers" />
      <van-cell title="签约金额" :value="formatMoney(store.performance.signedAmount) + ' 万元'" />
      <van-cell title="放款金额" :value="formatMoney(store.performance.loanAmount) + ' 万元'" />
      <van-cell title="部门排名" :value="'第 ' + store.performance.rankInDept + ' 名'" />
    </van-cell-group>

    <van-cell-group inset style="margin-top:16px;" title="💰 预估提成">
      <van-cell title="本月提成(预估)" value="¥ 8,450.00" label="按放款金额0.5%计算" />
    </van-cell-group>

    <van-cell-group inset style="margin-top:16px;">
      <van-cell title="🏅 业绩排名" is-link @click="showRank" />
      <van-cell title="📋 我的合同" is-link @click="showMyContracts" />
    </van-cell-group>

    <div class="logout-btn">
      <van-button block round type="default" @click="logout">退出登录</van-button>
    </div>
  </div>
</template>

<script setup>
import { useSalesStore } from '../stores/sales'
import { useAuthStore } from '../stores/auth'
import { showDialog, showToast } from 'vant'

const store = useSalesStore()
const authStore = useAuthStore()

const formatMoney = (value) => {
  return (value / 10000).toFixed(2)
}

const showRank = () => {
  showDialog({
    title: '部门业绩排名',
    message: '1. 王丽  2. 李强  3. 张明(您)'
  })
}

const showMyContracts = () => {
  showToast('合同列表: 2份待审核, 1份已放款')
}

const logout = async () => {
  try {
    await showDialog({
      title: '提示',
      message: '确定要退出登录吗？'
    })
    await authStore.logout()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('退出失败:', error)
    }
  }
}
</script>

<style scoped>
.page-container {
  padding: 16px 12px;
}
.profile-header {
  margin-bottom: 20px;
}
.user-info {
  margin-left: 16px;
}
.user-info h3 {
  margin: 0 0 6px 0;
}
.logout-btn {
  margin: 30px 16px;
}
</style>
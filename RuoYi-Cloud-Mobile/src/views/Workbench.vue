<template>
  <div class="page-container">
    <van-row justify="space-between" align="center">
      <van-col>
        <h2 class="greeting">你好，{{ store.currentUser.name }}</h2>
        <span class="dept">{{ store.currentUser.department }}</span>
      </van-col>
      <van-col>
        <van-image round width="44" height="44" :src="store.currentUser.avatar" />
      </van-col>
    </van-row>

    <!-- 今日指标卡片 -->
    <div class="stat-grid">
      <div class="stat-card">
        <div class="stat-label">📞 电话量</div>
        <div class="stat-value">{{ store.todayLog.callCount }}<span class="stat-unit">通</span></div>
        <div class="stat-sub">有效 {{ store.todayLog.validCallCount }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">🎯 意向客户</div>
        <div class="stat-value">{{ store.todayLog.intentCount }}<span class="stat-unit">人</span></div>
      </div>
      <div class="stat-card">
        <div class="stat-label">🤝 面谈</div>
        <div class="stat-value">{{ store.todayLog.meetingCount }}<span class="stat-unit">次</span></div>
      </div>
      <div class="stat-card highlight">
        <div class="stat-label">🏆 部门排名</div>
        <div class="stat-value">{{ store.performance.rankInDept }}<span class="stat-unit">/12</span></div>
      </div>
    </div>

    <!-- 快捷填写日志 -->
    <van-cell-group inset class="quick-log">
      <van-cell title="📝 快速填写工作日志" is-link @click="showLogPopup = true" />
    </van-cell-group>

    <!-- 业绩速览 -->
    <div class="section-title">
      <span>📊 业绩概览</span>
      <van-tag type="primary" size="medium">本月</van-tag>
    </div>
    <van-grid :column-num="3" :border="false" :gutter="8">
      <van-grid-item text="客户数" :value="store.performance.totalCustomers" />
      <van-grid-item text="签约(万)" :value="(store.performance.signedAmount/10000).toFixed(1)" />
      <van-grid-item text="放款(万)" :value="(store.performance.loanAmount/10000).toFixed(1)" />
    </van-grid>

    <!-- 待办提醒 -->
    <div class="section-title">
      <span>⏰ 待跟进客户</span>
      <van-icon name="arrow" />
    </div>
    <van-cell v-for="cust in pendingCustomers" :key="cust.id" :title="cust.name" :label="`意向: ${cust.intent} · ${cust.lastContact}`">
      <template #right-icon>
        <van-button size="small" type="primary" plain @click="store.signContract(cust.id)" v-if="!cust.hasContract">签约</van-button>
        <van-tag v-else type="success">已签约</van-tag>
      </template>
    </van-cell>

    <!-- 公海提醒 -->
    <van-notice-bar left-icon="volume-o" scrollable text="公海现有2位客户可领取，超过7天未跟进将自动释放" background="#fef8e7" color="#d48806" />

    <!-- 日志填写弹窗 -->
    <van-popup v-model:show="showLogPopup" position="bottom" round :style="{ height: '50%' }">
      <div class="popup-content">
        <h3>填写工作日志 ({{ store.todayLog.date }})</h3>
        <van-form @submit="onLogSubmit">
          <van-field v-model="logForm.callCount" type="digit" label="电话数" placeholder="请输入" />
          <van-field v-model="logForm.validCallCount" type="digit" label="有效电话" />
          <van-field v-model="logForm.intentCount" type="digit" label="意向客户" />
          <van-field v-model="logForm.meetingCount" type="digit" label="面谈客户" />
          <div class="popup-btn">
            <van-button round block type="primary" native-type="submit">保存</van-button>
          </div>
        </van-form>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { useSalesStore } from '../stores/sales'

const store = useSalesStore()
const showLogPopup = ref(false)

const logForm = reactive({
  callCount: store.todayLog.callCount,
  validCallCount: store.todayLog.validCallCount,
  intentCount: store.todayLog.intentCount,
  meetingCount: store.todayLog.meetingCount
})

const pendingCustomers = computed(() => {
  return store.myCustomers.filter(c => c.status !== '已放款').slice(0, 3)
})

watch(() => store.todayLog, (newVal) => {
  logForm.callCount = newVal.callCount
  logForm.validCallCount = newVal.validCallCount
  logForm.intentCount = newVal.intentCount
  logForm.meetingCount = newVal.meetingCount
}, { deep: true })

const onLogSubmit = () => {
  store.updateTodayLog({
    callCount: Number(logForm.callCount),
    validCallCount: Number(logForm.validCallCount),
    intentCount: Number(logForm.intentCount),
    meetingCount: Number(logForm.meetingCount)
  })
  showLogPopup.value = false
}
</script>

<style scoped>
.page-container {
  padding: 16px 12px;
}
.greeting {
  font-weight: 600;
  font-size: 20px;
  margin: 0;
}
.dept {
  color: #86909c;
  font-size: 14px;
}
.stat-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin: 16px 0;
}
.stat-card {
  background: #f8fafc;
  border-radius: 16px;
  padding: 16px 12px;
  border: 1px solid #eef2f6;
}
.stat-card.highlight {
  background: #e6f7ff;
  border-color: #bae0ff;
}
.stat-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 6px;
}
.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1f2f3e;
}
.stat-unit {
  font-size: 12px;
  font-weight: normal;
  color: #909399;
  margin-left: 4px;
}
.stat-sub {
  font-size: 12px;
  color: #00a870;
  margin-top: 4px;
}
.quick-log {
  margin: 8px 0 20px;
}
.section-title {
  font-size: 16px;
  font-weight: 600;
  margin: 20px 0 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.popup-content {
  padding: 20px 16px;
}
.popup-btn {
  margin: 24px 16px;
}
</style>
<template>
  <div class="page-container">
    <!-- 下拉刷新 -->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh" success-text="刷新成功">
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
          <div class="stat-label">
            <van-icon name="phone-o" class="stat-icon" />
            电话量
          </div>
          <div class="stat-value">{{ store.todayLog.callCount }}<span class="stat-unit">通</span></div>
          <div class="stat-sub">有效 {{ store.todayLog.validCallCount }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-label">
            <van-icon name="star-o" class="stat-icon" />
            意向客户
          </div>
          <div class="stat-value">{{ store.todayLog.intentCount }}<span class="stat-unit">人</span></div>
        </div>
        <div class="stat-card">
          <div class="stat-label">
            <van-icon name="friends-o" class="stat-icon" />
            面谈
          </div>
          <div class="stat-value">{{ store.todayLog.meetingCount }}<span class="stat-unit">次</span></div>
        </div>
        <div class="stat-card highlight">
          <div class="stat-label">
            <van-icon name="edit" class="stat-icon" />
            今日签约
          </div>
          <div class="stat-value">{{ store.todayLog.signedCount || 0 }}<span class="stat-unit">单</span></div>
        </div>
      </div>

      <!-- 快捷填写日志 -->
      <van-cell-group inset class="quick-log">
        <van-cell title="快速填写工作日志" is-link @click="openLogPopup">
          <template #icon>
            <van-icon name="edit" class="cell-icon" />
          </template>
        </van-cell>
      </van-cell-group>

      <!-- 业绩速览 -->
      <div class="section-title">
        <span>
          <van-icon name="bar-chart-o" class="section-icon" />
          本月业绩
        </span>
        <van-tag type="primary" size="medium">{{ currentMonth }}</van-tag>
      </div>

      <!-- 核心指标卡片 -->
      <div class="stat-grid">
        <div class="stat-card">
          <div class="stat-label">
            <van-icon name="balance-list-o" class="stat-icon" />
            签约金额
          </div>
          <div class="stat-value">¥{{ formatAmount(store.performance.signedAmount) }}<span class="stat-unit" v-if="shouldShowWan(store.performance.signedAmount)">万</span></div>
          <div class="stat-sub">{{ store.performance.contractCount }} 单合同</div>
        </div>

        <div class="stat-card">
          <div class="stat-label">
            <van-icon name="checked" class="stat-icon" />
            放款金额
          </div>
          <div class="stat-value">¥{{ formatAmount(store.performance.loanAmount) }}<span class="stat-unit" v-if="shouldShowWan(store.performance.loanAmount)">万</span></div>
          <div class="stat-sub">{{ store.performance.loanedCount }} 单已放款</div>
        </div>

        <div class="stat-card">
          <div class="stat-label">
            <van-icon name="gold-coin-o" class="stat-icon" />
            服务费
          </div>
          <div class="stat-value">¥{{ formatAmount(store.performance.feeIncome) }}<span class="stat-unit" v-if="shouldShowWan(store.performance.feeIncome)">万</span></div>
          <div class="stat-sub">前置+后置</div>
        </div>

        <div class="stat-card highlight">
          <div class="stat-label">
            <van-icon name="cash-back-record" class="stat-icon" />
            预计提成
          </div>
          <div class="stat-value">¥{{ formatAmount(store.performance.commissionAmount) }}<span class="stat-unit" v-if="shouldShowWan(store.performance.commissionAmount)">万</span></div>
          <div class="stat-sub">本月预估</div>
        </div>
      </div>

      <!-- 待办提醒 -->
      <div class="section-title">
        <span>
          <van-icon name="clock-o" class="section-icon" />
          待跟进客户
        </span>
        <van-icon name="arrow" />
      </div>
      <van-cell v-for="cust in pendingCustomers" :key="cust.id" :title="cust.name" :label="`意向: ${cust.intent} · ${cust.lastContact}`">
        <template #right-icon>
          <van-button size="small" type="primary" plain @click="goToContract(cust)" v-if="!cust.hasContract">签约</van-button>
          <van-tag v-else type="success">已签约</van-tag>
        </template>
      </van-cell>

      <!-- 公海提醒 -->
      <van-notice-bar left-icon="volume-o" scrollable :text="seaNoticeText" background="#fef8e7" color="#d48806" />
    </van-pull-refresh>

    <!-- 日志填写弹窗 -->
    <van-popup v-model:show="showLogPopup" position="bottom" round :style="{ height: '50%' }">
      <div class="popup-content">
        <h3>填写工作日志 ({{ store.todayLog.date }})</h3>
        <van-form @submit="onLogSubmit">
          <van-field
            v-model="logForm.callCount"
            type="digit"
            label="电话数"
            placeholder="请输入"
            :rules="[{ required: true, message: '请填写电话数' }]"
          >
            <template #left-icon>
              <van-icon name="phone-o" />
            </template>
          </van-field>
          <van-field
            v-model="logForm.validCallCount"
            type="digit"
            label="有效电话"
            :rules="[{ required: true, message: '请填写有效电话数' }]"
          >
            <template #left-icon>
              <van-icon name="checked" />
            </template>
          </van-field>
          <van-field
            v-model="logForm.intentCount"
            type="digit"
            label="意向客户"
            :rules="[{ required: true, message: '请填写意向客户数' }]"
          >
            <template #left-icon>
              <van-icon name="star-o" />
            </template>
          </van-field>
          <van-field
            v-model="logForm.meetingCount"
            type="digit"
            label="面谈客户"
            :rules="[{ required: true, message: '请填写面谈客户数' }]"
          >
            <template #left-icon>
              <van-icon name="friends-o" />
            </template>
          </van-field>
          <van-field
            v-model="logForm.remark"
            rows="2"
            autosize
            type="textarea"
            label="备注"
            placeholder="可选填"
          >
            <template #left-icon>
              <van-icon name="notes-o" />
            </template>
          </van-field>
          <div class="popup-btn">
            <van-button round block type="primary" native-type="submit" :loading="submitting">保存</van-button>
          </div>
        </van-form>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useSalesStore } from '../stores/sales'
import { showToast } from 'vant'
import dayjs from 'dayjs'

const router = useRouter()
const store = useSalesStore()
const showLogPopup = ref(false)
const submitting = ref(false)
const refreshing = ref(false)

const logForm = reactive({
  callCount: 0,
  validCallCount: 0,
  intentCount: 0,
  meetingCount: 0,
  remark: ''
})

// 当前月份显示
const currentMonth = computed(() => {
  return dayjs().format('YYYY年MM月')
})

// 格式化金额（转换为万元或保留元）
const formatAmount = (amount) => {
  if (!amount && amount !== 0) return '0'
  const num = parseFloat(amount)
  if (num >= 10000) {
    return (num / 10000).toFixed(2)
  }
  return num.toFixed(2)
}

// 判断是否显示"万"单位
const shouldShowWan = (amount) => {
  if (!amount && amount !== 0) return false
  return parseFloat(amount) >= 10000
}

const pendingCustomers = computed(() => {
  return store.myCustomers.filter(c => c.status !== '已放款').slice(0, 3)
})

const seaNoticeText = computed(() => {
  const count = store.seaCustomers.length
  if (count > 0) {
    return `公海现有${count}位客户可领取，超过7天未跟进将自动释放`
  }
  return '公海暂无客户可领取'
})

// 监听 store 中的 todayLog 变化，同步到表单（静默同步，不打印日志）
watch(() => store.todayLog, (newVal) => {
  logForm.callCount = newVal.callCount || 0
  logForm.validCallCount = newVal.validCallCount || 0
  logForm.intentCount = newVal.intentCount || 0
  logForm.meetingCount = newVal.meetingCount || 0
}, { deep: true })

// 打开日志弹窗
const openLogPopup = () => {
  // 同步最新数据到表单
  logForm.callCount = store.todayLog.callCount || 0
  logForm.validCallCount = store.todayLog.validCallCount || 0
  logForm.intentCount = store.todayLog.intentCount || 0
  logForm.meetingCount = store.todayLog.meetingCount || 0
  logForm.remark = ''
  showLogPopup.value = true
}

// 提交日志
const onLogSubmit = async () => {
  submitting.value = true

  const success = await store.saveWorkLog({
    callCount: logForm.callCount,
    validCallCount: logForm.validCallCount,
    intentCount: logForm.intentCount,
    meetingCount: logForm.meetingCount,
    remark: logForm.remark
  })

  submitting.value = false

  if (success) {
    showLogPopup.value = false
  }
}

// 跳转到签署合同页面
const goToContract = (cust) => {
  router.push({
    path: '/customers',
    query: { action: 'contract', customerId: cust.id }
  })
}

// 下拉刷新
const onRefresh = async () => {
  try {
    // 强制刷新所有数据
    await Promise.all([
      store.loadTodayWorkLog(),
      store.loadMyCustomers(),
      store.loadSeaCustomers(),
      store.refreshPerformance()
    ])

    showToast('刷新成功')
  } catch (error) {
    console.error('刷新失败:', error)
    showToast('刷新失败')
  } finally {
    refreshing.value = false
  }
}

// 页面加载时获取真实数据
onMounted(async () => {
  // 强制刷新所有数据，不使用缓存

  // 1. 加载今日工作日志（每次都发送请求）
  await store.loadTodayWorkLog()

  // 2. 并行加载客户数据（每次都发送请求）
  await Promise.all([
    store.loadMyCustomers(),
    store.loadSeaCustomers()
  ])

  // 3. 刷新业绩数据（每次都发送请求）
  await store.refreshPerformance()

  // 4. 所有数据加载完成后，统一打印一次
  console.log('===== Workbench 页面数据加载完成 =====')
  console.log('📊 今日工作日志:', JSON.stringify(store.todayLog, null, 2))
  console.log('👥 我的客户数:', store.myCustomers.length)
  console.log('🌊 公海客户数:', store.seaCustomers.length)
  console.log('💼 业绩数据:', JSON.stringify(store.performance, null, 2))
  console.log('====================================')
})
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
  display: flex;
  align-items: center;
  gap: 6px;
}
.stat-icon {
  color: #1989fa;
  font-size: 16px;
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
.cell-icon {
  margin-right: 8px;
  color: #1989fa;
  font-size: 18px;
}
.section-title {
  font-size: 16px;
  font-weight: 600;
  margin: 20px 0 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.section-icon {
  margin-right: 6px;
  color: #1989fa;
  font-size: 18px;
}
.popup-content {
  padding: 20px 16px;
}
.popup-btn {
  margin: 24px 16px;
}
</style>
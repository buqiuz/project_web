<template>
  <div class="page-container">
    <van-nav-bar title="工作日志" />
    
    <van-calendar 
      :show-confirm="false" 
      :min-date="minDate" 
      :max-date="maxDate" 
      :default-date="today" 
      @select="onDateSelect" 
      class="calendar"
    />
    
    <van-cell-group inset>
      <van-cell title="日期" :value="selectedDate" />
    </van-cell-group>

    <div class="log-form">
      <van-form @submit="saveLog">
        <van-field v-model="logData.callCount" type="digit" label="📞 电话数" />
        <van-field v-model="logData.validCallCount" type="digit" label="✅ 有效电话" />
        <van-field v-model="logData.intentCount" type="digit" label="🎯 意向客户" />
        <van-field v-model="logData.meetingCount" type="digit" label="🤝 面谈数" />
        <div class="form-btn">
          <van-button block round type="primary" native-type="submit">保存日志</van-button>
        </div>
      </van-form>
    </div>

    <van-divider>历史日志 (最近3天)</van-divider>
    <van-cell v-for="item in historyLogs" :key="item.date" :title="item.date">
      <template #label>
        <span>📞{{ item.callCount }}  ✅{{ item.validCallCount }}  🎯{{ item.intentCount }}  🤝{{ item.meetingCount }}</span>
      </template>
    </van-cell>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useSalesStore } from '../stores/sales'
import dayjs from 'dayjs'
import { showToast } from 'vant'

const store = useSalesStore()
const today = dayjs().format('YYYY-MM-DD')
const selectedDate = ref(today)
const minDate = dayjs().subtract(30, 'day').toDate()
const maxDate = dayjs().toDate()

const logData = reactive({
  callCount: 0,
  validCallCount: 0,
  intentCount: 0,
  meetingCount: 0
})

const historyLogs = ref([
  { date: dayjs().subtract(1, 'day').format('YYYY-MM-DD'), callCount: 32, validCallCount: 18, intentCount: 7, meetingCount: 3 },
  { date: dayjs().subtract(2, 'day').format('YYYY-MM-DD'), callCount: 28, validCallCount: 14, intentCount: 5, meetingCount: 1 }
])

const loadLogForDate = (date) => {
  if (date === today) {
    Object.assign(logData, store.todayLog)
  } else {
    const found = historyLogs.value.find(h => h.date === date)
    if (found) {
      Object.assign(logData, found)
    } else {
      Object.assign(logData, { callCount: 0, validCallCount: 0, intentCount: 0, meetingCount: 0 })
    }
  }
}

const onDateSelect = (value) => {
  selectedDate.value = dayjs(value).format('YYYY-MM-DD')
  loadLogForDate(selectedDate.value)
}

const saveLog = () => {
  if (selectedDate.value === today) {
    store.updateTodayLog({ ...logData })
  } else {
    const idx = historyLogs.value.findIndex(h => h.date === selectedDate.value)
    const entry = { date: selectedDate.value, ...logData }
    if (idx >= 0) {
      historyLogs.value[idx] = entry
    } else {
      historyLogs.value.unshift(entry)
    }
    showToast('日志已更新')
  }
}

onMounted(() => {
  loadLogForDate(today)
})
</script>

<style scoped>
.page-container {
  padding: 12px;
}
.calendar {
  margin-bottom: 12px;
}
.log-form {
  margin: 20px 0;
}
.form-btn {
  margin: 20px 16px;
}
</style>
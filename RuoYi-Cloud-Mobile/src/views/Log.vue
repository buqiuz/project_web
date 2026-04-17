<template>
  <div class="page-container">
    <van-nav-bar title="工作日志" />
    
    <van-calendar 
      :show-confirm="false" 
      :min-date="minDate" 
      :max-date="maxDate" 
      :default-date="defaultDate"
      @select="onDateSelect" 
      class="calendar"
    />
    
    <van-cell-group inset>
      <van-cell title="日期" :value="selectedDate">
        <template #icon>
          <van-icon name="calendar-o" class="cell-icon" />
        </template>
      </van-cell>
    </van-cell-group>

    <div class="log-form">
      <van-form @submit="saveLog">
        <van-field v-model="logData.callCount" type="digit" label="电话数">
          <template #left-icon>
            <van-icon name="phone-o" class="field-icon" />
          </template>
        </van-field>

        <van-field v-model="logData.validCallCount" type="digit" label="有效电话">
          <template #left-icon>
            <van-icon name="checked" class="field-icon" />
          </template>
        </van-field>

        <van-field v-model="logData.intentCount" type="digit" label="意向客户">
          <template #left-icon>
            <van-icon name="star-o" class="field-icon" />
          </template>
        </van-field>

        <van-field v-model="logData.meetingCount" type="digit" label="面谈数">
          <template #left-icon>
            <van-icon name="friends-o" class="field-icon" />
          </template>
        </van-field>

        <div class="form-btn">
          <van-button block round type="primary" native-type="submit">
            <van-icon name="success" style="margin-right: 4px;" />
            保存日志
          </van-button>
        </div>
      </van-form>
    </div>

    <van-divider>历史日志 (最近3天)</van-divider>
    <van-cell v-for="item in historyLogs" :key="item.date" :title="item.date">
      <template #label>
        <div class="log-stats">
          <span class="stat-item">
            <van-icon name="phone-o" class="stat-icon" />
            {{ item.callCount }}
          </span>
          <span class="stat-item">
            <van-icon name="checked" class="stat-icon" />
            {{ item.validCallCount }}
          </span>
          <span class="stat-item">
            <van-icon name="star-o" class="stat-icon" />
            {{ item.intentCount }}
          </span>
          <span class="stat-item">
            <van-icon name="friends-o" class="stat-icon" />
            {{ item.meetingCount }}
          </span>
        </div>
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
const defaultDate = dayjs().toDate()

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

const saveLog = async () => {
  if (selectedDate.value === today) {
    const success = await store.saveWorkLog({
      callCount: logData.callCount,
      validCallCount: logData.validCallCount,
      intentCount: logData.intentCount,
      meetingCount: logData.meetingCount,
      remark: ''
    })

    if (!success) {
      showToast('保存失败')
    }
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
.cell-icon {
  margin-right: 8px;
  color: #1989fa;
  font-size: 18px;
}
.log-form {
  margin: 20px 0;
}
.field-icon {
  margin-right: 6px;
  color: #1989fa;
  font-size: 16px;
}
.form-btn {
  margin: 20px 16px;
}
.log-stats {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
.stat-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #646566;
}
.stat-icon {
  color: #1989fa;
  font-size: 14px;
}
</style>
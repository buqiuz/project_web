<template>
  <div class="page-container">
    <van-nav-bar title="我的合同" left-arrow @click-left="goBack" fixed placeholder />

    <!-- 下拉刷新 -->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh" success-text="刷新成功">
      <!-- 搜索栏 -->
      <van-search
        v-model="searchKeyword"
        placeholder="搜索合同编号或客户姓名"
        @search="handleSearch"
      />

      <!-- 状态筛选 -->
      <van-tabs v-model:active="activeStatus" @change="onStatusChange">
        <van-tab title="全部" name="ALL" />
        <van-tab title="已签约" name="SIGNED" />
        <van-tab title="审核中" name="FINANCE_REVIEW" />
        <van-tab title="银行审核" name="BANK_REVIEW" />
        <van-tab title="已放款" name="LOANED" />
        <van-tab title="已拒绝" name="REJECTED" />
      </van-tabs>

      <!-- 合同列表 -->
      <div class="contract-list">
        <van-cell-group
          v-for="contract in filteredContracts"
          :key="contract.contractId"
          class="contract-card"
        >
          <van-cell :title="contract.contractNo" :label="`客户: ${contract.customerName}`">
            <template #value>
              <van-tag :type="getStatusType(contract.status)" size="medium">
                {{ getStatusText(contract.status) }}
              </van-tag>
            </template>
          </van-cell>

          <van-cell title="金融产品" :value="contract.productName" />
          <van-cell title="合同金额" :value="formatAmount(contract.contractAmount)" />
          <van-cell title="发放金额" :value="formatAmount(contract.disburseAmount)" />
          <van-cell title="前置服务费" :value="`${formatAmount(contract.firstFeePaid || 0)} / ${formatAmount(contract.firstFeeAmount)}`" />
          <van-cell title="后置服务费" :value="`${formatAmount(contract.secondFeePaid || 0)} / ${formatAmount(contract.secondFeeAmount)}`" />
          <van-cell title="签约时间" :value="formatTime(contract.signTime)" />

          <div class="card-actions">
            <van-button
              v-if="contract.status === 'SIGNED'"
              size="small"
              type="primary"
              @click="submitReview(contract)"
            >
              提交审核
            </van-button>
            <van-button
              size="small"
              plain
              type="primary"
              @click="viewDetail(contract)"
            >
              查看详情
            </van-button>
          </div>
        </van-cell-group>

        <!-- 空状态 -->
        <van-empty
          v-if="!loading && filteredContracts.length === 0"
          description="暂无合同数据"
        />

        <!-- 加载中 -->
        <van-loading v-if="loading" class="loading-tip">加载中...</van-loading>
      </div>
    </van-pull-refresh>

    <!-- 合同详情弹窗 -->
    <van-popup v-model:show="showDetailPopup" position="bottom" round :style="{ height: '80%' }">
      <div class="popup-content" v-if="selectedContract">
        <h3>合同详情 - {{ selectedContract.contractNo }}</h3>
        <van-cell-group inset>
          <van-cell title="合同编号" :value="selectedContract.contractNo" />
          <van-cell title="客户姓名" :value="selectedContract.customerName" />
          <van-cell title="金融产品" :value="selectedContract.productName" />
          <van-cell title="合同金额" :value="formatAmount(selectedContract.contractAmount)" />
          <van-cell title="发放金额" :value="formatAmount(selectedContract.disburseAmount)" />
          <van-cell title="前置服务费(应收)" :value="formatAmount(selectedContract.firstFeeAmount)" />
          <van-cell title="前置服务费(实收)" :value="formatAmount(selectedContract.firstFeePaid || 0)" />
          <van-cell title="后置服务费(应收)" :value="formatAmount(selectedContract.secondFeeAmount)" />
          <van-cell title="后置服务费(实收)" :value="formatAmount(selectedContract.secondFeePaid || 0)" />
          <van-cell title="合同状态" :value="getStatusText(selectedContract.status)" />
          <van-cell title="签约时间" :value="formatTime(selectedContract.signTime)" />
          <van-cell title="提交审核时间" :value="formatTime(selectedContract.submitTime)" />
          <van-cell title="金融审核时间" :value="formatTime(selectedContract.financeAuditTime)" />
          <van-cell title="银行放款时间" :value="formatTime(selectedContract.bankLoanTime)" />
          <van-cell title="银行反馈" :value="selectedContract.bankResult || '-'" label-class="remark-label" />
          <van-cell title="备注" :value="selectedContract.remark || '-'" label-class="remark-label" />
        </van-cell-group>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import dayjs from 'dayjs'
import { listContract, submitFinanceReview } from '@/api/contract'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const refreshing = ref(false)
const loading = ref(false)
const searchKeyword = ref('')
const activeStatus = ref('ALL')
const contractList = ref([])
const showDetailPopup = ref(false)
const selectedContract = ref(null)

// 返回上一页
const goBack = () => {
  router.back()
}

// 获取合同列表
const fetchContracts = async () => {
  try {
    loading.value = true
    const userId = authStore.userInfo?.userId

    if (!userId) {
      showToast('未获取到用户信息')
      return
    }

    const params = {
      userId: userId,
      pageNum: 1,
      pageSize: 100
    }

    // 如果选择了特定状态，添加到查询参数
    if (activeStatus.value !== 'ALL') {
      params.status = activeStatus.value
    }

    const res = await listContract(params)

    if (res.code === 200) {
      contractList.value = res.rows || []
    } else {
      showToast(res.msg || '加载失败')
    }
  } catch (error) {
    console.error('加载合同列表失败:', error)
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

// 过滤后的合同列表（支持搜索）
const filteredContracts = computed(() => {
  if (!searchKeyword.value) {
    return contractList.value
  }

  const keyword = searchKeyword.value.toLowerCase()
  return contractList.value.filter(contract =>
    contract.contractNo?.toLowerCase().includes(keyword) ||
    contract.customerName?.toLowerCase().includes(keyword)
  )
})

// 状态变更
const onStatusChange = () => {
  fetchContracts()
}

// 搜索
const handleSearch = () => {
  // 搜索已在 computed 中处理
}

// 下拉刷新
const onRefresh = async () => {
  await fetchContracts()
  refreshing.value = false
  showToast('刷新成功')
}

// 格式化金额
const formatAmount = (amount) => {
  if (!amount && amount !== 0) return '¥0.00'
  const num = parseFloat(amount)
  if (num >= 10000) {
    return '¥' + (num / 10000).toFixed(2) + '万'
  }
  return '¥' + num.toFixed(2)
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '-'
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    'SIGNED': 'primary',
    'FINANCE_REVIEW': 'warning',
    'BANK_REVIEW': 'warning',
    'LOANED': 'success',
    'REJECTED': 'danger'
  }
  return typeMap[status] || 'default'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    'SIGNED': '已签约',
    'FINANCE_REVIEW': '金融审核中',
    'BANK_REVIEW': '银行审核中',
    'LOANED': '已放款',
    'REJECTED': '已拒绝'
  }
  return textMap[status] || status
}

// 查看详情
const viewDetail = (contract) => {
  selectedContract.value = contract
  showDetailPopup.value = true
}

// 提交审核
const submitReview = async (contract) => {
  try {
    await showConfirmDialog({
      title: '确认提交',
      message: `确定将合同"${contract.contractNo}"提交金融审核吗？`
    })

    const res = await submitFinanceReview(contract.contractId)

    if (res.code === 200) {
      showToast({ message: '已提交金融审核', type: 'success' })
      await fetchContracts()
    } else {
      showToast({ message: res.msg || '提交失败', type: 'fail' })
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('提交审核失败:', error)
      showToast('提交失败')
    }
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchContracts()
})
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  background: #f7f8fa;
}

.contract-list {
  padding: 12px;
}

.contract-card {
  margin-bottom: 12px;
  border-radius: 8px;
  overflow: hidden;
}

.card-actions {
  padding: 8px 16px 12px;
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.loading-tip {
  text-align: center;
  padding: 20px 0;
}

.popup-content {
  padding: 20px 16px;
}

.popup-content h3 {
  text-align: center;
  margin-bottom: 20px;
  font-size: 18px;
  font-weight: 600;
}

.remark-label {
  white-space: normal;
  word-break: break-all;
}
</style>
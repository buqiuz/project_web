<template>
  <div class="app-container dashboard" v-loading="loading">
    <section class="hero">
      <div class="hero-content">
        <h2>大富翁金融服务公司 · 业务驾驶舱</h2>
        <p>聚焦客户推进、合同审核与放款节奏，帮助销售与金融团队高效协同。</p>
        <div class="hero-tags">
          <el-tag>客户开发</el-tag>
          <el-tag type="success">合同流转</el-tag>
          <el-tag type="warning">公海管理</el-tag>
          <el-tag type="info">服务费收取</el-tag>
        </div>
      </div>
      <div class="hero-time">
        <div class="label">数据更新时间</div>
        <div class="value">{{ updateTime }}</div>
      </div>
    </section>

    <el-row :gutter="16" class="summary-row">
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="summary-card">
          <div class="card-head">客户总量</div>
          <div class="card-value">{{ stats.customerTotal }}</div>
          <div class="card-foot">全部客户池</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="summary-card">
          <div class="card-head">公海客户</div>
          <div class="card-value">{{ stats.seaCustomer }}</div>
          <div class="card-foot">可领取资源</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="summary-card">
          <div class="card-head">待审核合同</div>
          <div class="card-value">{{ stats.pendingReview }}</div>
          <div class="card-foot">金融/银行审核阶段</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="summary-card">
          <div class="card-head">已放款合同</div>
          <div class="card-value">{{ stats.loanedContract }}</div>
          <div class="card-foot">已完成放款</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="12">
        <el-card class="panel" shadow="never">
          <template #header>
            <div class="panel-title">快捷入口</div>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="go('/loan/customer')">客户管理</el-button>
            <el-button type="success" @click="go('/loan/contract')">合同管理</el-button>
            <el-button type="warning" @click="refreshStats">刷新看板</el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card class="panel" shadow="never">
          <template #header>
            <div class="panel-title">今日重点</div>
          </template>
          <ul class="todo-list">
            <li>优先跟进高意向客户，避免超期转入公海。</li>
            <li>及时提交已签约合同到金融审核流程。</li>
            <li>放款后核对服务费收款节点与记录完整性。</li>
            <li>部门经理每日检查客户转移与领取记录。</li>
          </ul>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { parseTime } from '@/utils/ruoyi'
import { listLoanCustomer } from '@/api/loan/customer'
import { listLoanContract } from '@/api/loan/contract'

const router = useRouter()
const loading = ref(false)
const lastUpdate = ref(new Date())

const stats = reactive({
  customerTotal: 0,
  seaCustomer: 0,
  pendingReview: 0,
  loanedContract: 0
})

const updateTime = computed(() => parseTime(lastUpdate.value, '{y}-{m}-{d} {h}:{i}:{s}'))

function go(path) {
  router.push(path)
}

function countCustomer(query = {}) {
  return listLoanCustomer({ ...query, pageNum: 1, pageSize: 1 }).then(res => Number(res.total || 0))
}

function countContract(query = {}) {
  return listLoanContract({ ...query, pageNum: 1, pageSize: 1 }).then(res => Number(res.total || 0))
}

async function refreshStats() {
  loading.value = true
  try {
    const [customerTotal, seaCustomer, financeReview, bankReview, loanedContract] = await Promise.all([
      countCustomer(),
      countCustomer({ status: 'SEA' }),
      countContract({ status: 'FINANCE_REVIEW' }),
      countContract({ status: 'BANK_REVIEW' }),
      countContract({ status: 'LOANED' })
    ])

    stats.customerTotal = customerTotal
    stats.seaCustomer = seaCustomer
    stats.pendingReview = financeReview + bankReview
    stats.loanedContract = loanedContract
    lastUpdate.value = new Date()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  refreshStats()
})
</script>

<style lang="scss" scoped>
.dashboard {
  .hero {
    display: flex;
    justify-content: space-between;
    gap: 16px;
    margin-bottom: 16px;
    padding: 20px;
    border-radius: 12px;
    background: linear-gradient(120deg, #0f3d91 0%, #1e58c7 55%, #5b9dff 100%);
    color: #fff;

    .hero-content {
      h2 {
        margin: 0 0 8px;
        font-size: 24px;
      }

      p {
        margin: 0 0 12px;
        opacity: 0.95;
      }

      .hero-tags :deep(.el-tag) {
        margin-right: 8px;
        margin-bottom: 6px;
        border: none;
      }
    }

    .hero-time {
      min-width: 200px;
      text-align: right;

      .label {
        font-size: 12px;
        opacity: 0.85;
      }

      .value {
        margin-top: 6px;
        font-size: 16px;
        font-weight: 700;
      }
    }
  }

  .summary-row {
    margin-bottom: 16px;
  }

  .summary-card {
    .card-head {
      color: #6b7280;
      font-size: 13px;
      margin-bottom: 8px;
    }

    .card-value {
      font-size: 30px;
      font-weight: 700;
      color: #111827;
      line-height: 1.2;
    }

    .card-foot {
      margin-top: 6px;
      color: #9ca3af;
      font-size: 12px;
    }
  }

  .panel {
    margin-bottom: 16px;

    .panel-title {
      font-weight: 600;
      color: #1f2937;
    }
  }

  .quick-actions {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
  }

  .todo-list {
    margin: 0;
    padding-left: 18px;
    color: #374151;
    line-height: 1.9;
  }
}

@media (max-width: 768px) {
  .dashboard {
    .hero {
      flex-direction: column;

      .hero-time {
        text-align: left;
        min-width: 0;
      }
    }
  }
}
</style>

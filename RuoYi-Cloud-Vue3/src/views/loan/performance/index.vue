<template>
  <div class="app-container perf-page" v-loading="loading">
    <el-card class="filter-card" shadow="never">
      <el-form :model="queryParams" :inline="true">
        <el-form-item label="统计区间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 260px"
          />
        </el-form-item>
        <el-form-item label="部门ID">
          <el-input-number v-model="queryParams.deptId" :min="1" controls-position="right" style="width: 130px" />
        </el-form-item>
        <el-form-item label="TOP">
          <el-input-number v-model="queryParams.topN" :min="5" :max="50" controls-position="right" style="width: 120px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="loadAll">刷新分析</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <section class="hero">
      <div class="hero-main">
        <h2>销售与金融业绩可视化分析</h2>
        <p>覆盖个人业绩、部门与战区统计、金融排名和提成测算，支持管理层与业务团队统一查看。</p>
      </div>
      <div class="hero-time">{{ updateTime }}</div>
    </section>

    <el-row :gutter="14" class="summary-row">
      <el-col :xs="12" :sm="8" :lg="4">
        <el-card class="summary-card">
          <div class="label">活跃销售</div>
          <div class="value">{{ overview.salesUserCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :lg="4">
        <el-card class="summary-card">
          <div class="label">合同总数</div>
          <div class="value">{{ overview.contractCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :lg="4">
        <el-card class="summary-card">
          <div class="label">合同金额</div>
          <div class="value">{{ amountText(overview.contractAmount) }}</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :lg="4">
        <el-card class="summary-card">
          <div class="label">放款金额</div>
          <div class="value">{{ amountText(overview.loanedAmount) }}</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :lg="4">
        <el-card class="summary-card">
          <div class="label">服务费实收</div>
          <div class="value">{{ amountText(overview.feeIncome) }}</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :lg="4">
        <el-card class="summary-card emphasis">
          <div class="label">提成合计</div>
          <div class="value">{{ amountText(overview.commissionTotal) }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="14" class="mb14">
      <el-col :xs="24" :lg="16">
        <el-card class="panel-card">
          <template #header>
            <div class="panel-title">销售代表个人业绩</div>
          </template>
          <el-table :data="personalList" height="350">
            <el-table-column type="index" label="#" width="55" align="center" />
            <el-table-column label="销售" prop="userName" width="120" />
            <el-table-column label="部门" prop="deptName" min-width="120" />
            <el-table-column label="电话/有效" width="120" align="center">
              <template #default="scope">
                <span>{{ scope.row.callCount || 0 }}/{{ scope.row.validCallCount || 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column label="意向/面谈" width="120" align="center">
              <template #default="scope">
                <span>{{ scope.row.intentCustomerCount || 0 }}/{{ scope.row.visitCount || 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column label="合同金额" prop="contractAmount" width="120" align="right">
              <template #default="scope">{{ amountText(scope.row.contractAmount) }}</template>
            </el-table-column>
            <el-table-column label="放款金额" prop="loanedAmount" width="120" align="right">
              <template #default="scope">{{ amountText(scope.row.loanedAmount) }}</template>
            </el-table-column>
            <el-table-column label="转化率" width="180">
              <template #default="scope">
                <el-progress :percentage="Number(scope.row.conversionRate || 0)" :stroke-width="12" :text-inside="true" />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="8">
        <el-card class="panel-card">
          <template #header>
            <div class="panel-title">销售排名（放款金额）</div>
          </template>
          <el-table :data="rankList" height="350">
            <el-table-column type="index" label="名次" width="70" align="center" />
            <el-table-column label="销售" prop="userName" min-width="100" />
            <el-table-column label="放款金额" align="right" min-width="120">
              <template #default="scope">{{ amountText(scope.row.loanedAmount) }}</template>
            </el-table-column>
            <el-table-column label="提成" align="right" min-width="120">
              <template #default="scope">{{ amountText(scope.row.commissionAmount) }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="14" class="mb14">
      <el-col :xs="24" :lg="12">
        <el-card class="panel-card">
          <template #header>
            <div class="panel-title">部门经理统计部门业绩</div>
          </template>
          <el-table :data="deptList" height="320">
            <el-table-column type="index" width="55" align="center" />
            <el-table-column label="部门" prop="deptName" min-width="150" />
            <el-table-column label="销售人数" prop="salesUserCount" width="95" align="center" />
            <el-table-column label="合同金额" min-width="120" align="right">
              <template #default="scope">{{ amountText(scope.row.contractAmount) }}</template>
            </el-table-column>
            <el-table-column label="放款金额" min-width="120" align="right">
              <template #default="scope">{{ amountText(scope.row.loanedAmount) }}</template>
            </el-table-column>
            <el-table-column label="转化率" width="95" align="center">
              <template #default="scope">{{ percentText(scope.row.conversionRate) }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card class="panel-card">
          <template #header>
            <div class="panel-title">销售总监统计战区业绩</div>
          </template>
          <div class="zone-list">
            <div v-for="item in zoneList" :key="item.zoneName" class="zone-item">
              <div class="zone-top">
                <span class="name">{{ item.zoneName }}</span>
                <span class="money">{{ amountText(item.loanedAmount) }}</span>
              </div>
              <el-progress
                :percentage="zonePercent(item.loanedAmount)"
                :stroke-width="14"
                :text-inside="true"
                status="success"
              />
              <div class="zone-meta">
                销售人数 {{ item.salesUserCount || 0 }}
                · 合同数 {{ item.contractCount || 0 }}
                · 转化率 {{ percentText(item.conversionRate) }}
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="14">
      <el-col :xs="24" :lg="24">
        <el-card class="panel-card">
          <template #header>
            <div class="panel-title">金融部发布业绩排名与提成测算</div>
          </template>
          <el-table :data="commissionList" height="360">
            <el-table-column type="index" label="名次" width="70" align="center" />
            <el-table-column label="销售" prop="userName" width="120" />
            <el-table-column label="部门" prop="deptName" min-width="140" />
            <el-table-column label="放款金额" min-width="130" align="right">
              <template #default="scope">{{ amountText(scope.row.loanedAmount) }}</template>
            </el-table-column>
            <el-table-column label="服务费实收" min-width="130" align="right">
              <template #default="scope">{{ amountText(scope.row.feeIncome) }}</template>
            </el-table-column>
            <el-table-column label="提成比例" width="100" align="center">
              <template #default="scope">{{ percentText(scope.row.commissionRate) }}</template>
            </el-table-column>
            <el-table-column label="提成金额" min-width="130" align="right">
              <template #default="scope">{{ amountText(scope.row.commissionAmount) }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="LoanPerformance">
import { parseTime } from '@/utils/ruoyi'
import {
  getLoanPerformanceOverview,
  listPersonalPerformance,
  listDeptPerformance,
  listZonePerformance,
  listPerformanceRank,
  listPerformanceCommission
} from '@/api/loan/performance'
// 【新增这一行引入】
import { checkPermi } from '@/utils/permission'
const loading = ref(false)
const updateTime = ref(new Date())

const dateRange = ref([])
const queryParams = reactive({
  beginDate: undefined,
  endDate: undefined,
  deptId: undefined,
  topN: 10
})

const overview = ref({})
const personalList = ref([])
const deptList = ref([])
const zoneList = ref([])
const rankList = ref([])
const commissionList = ref([])

function defaultDateRange() {
  const now = new Date()
  const first = new Date(now.getFullYear(), now.getMonth(), 1)
  return [parseTime(first, '{y}-{m}-{d}'), parseTime(now, '{y}-{m}-{d}')]
}

function normalizeQuery() {
  const q = { ...queryParams }
  if (dateRange.value && dateRange.value.length === 2) {
    q.beginDate = dateRange.value[0]
    q.endDate = dateRange.value[1]
  }
  return q
}

function amountText(v) {
  const n = Number(v || 0)
  if (n >= 100000000) {
    return (n / 100000000).toFixed(2) + '亿'
  }
  if (n >= 10000) {
    return (n / 10000).toFixed(2) + '万'
  }
  return n.toFixed(2)
}

function percentText(v) {
  return Number(v || 0).toFixed(2) + '%'
}

function zonePercent(value) {
  const max = Math.max(...zoneList.value.map(x => Number(x.loanedAmount || 0)), 1)
  return Number(((Number(value || 0) * 100) / max).toFixed(2))
}

async function loadAll() {
  loading.value = true
  const q = normalizeQuery()
  try {
    const promises = []

    // 1. 个人业绩与总览
    if (checkPermi(['loan:performance:view'])) {
      promises.push(getLoanPerformanceOverview(q).then(res => { overview.value = res.data || {} }).catch(() => {}))
      promises.push(listPersonalPerformance(q).then(res => { personalList.value = res.data || [] }).catch(() => {}))
    }

    // 2. 部门统计业绩
    if (checkPermi(['loan:performance:dept'])) {
      promises.push(listDeptPerformance(q).then(res => { deptList.value = res.data || [] }).catch(() => {}))
    }

    // 3. 战区统计业绩
    if (checkPermi(['loan:performance:zone'])) {
      promises.push(listZonePerformance(q).then(res => { zoneList.value = res.data || [] }).catch(() => {}))
    }

    // 4. 销售排名
    if (checkPermi(['loan:performance:rank'])) {
      promises.push(listPerformanceRank(q).then(res => { rankList.value = res.data || [] }).catch(() => {}))
    }

    // 5. 提成测算
    if (checkPermi(['loan:performance:commission'])) {
      promises.push(listPerformanceCommission(q).then(res => { commissionList.value = res.data || [] }).catch(() => {}))
    }

    // 等待所有拥有权限的请求处理完毕
    await Promise.all(promises)
    updateTime.value = new Date()
  } finally {
    loading.value = false
  }
}

dateRange.value = defaultDateRange()
loadAll()
</script>

<style lang="scss" scoped>
.perf-page {
  .filter-card {
    margin-bottom: 14px;
  }

  .hero {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 14px;
    border-radius: 12px;
    padding: 16px 18px;
    background: linear-gradient(120deg, #0f766e 0%, #0ea5a4 55%, #67e8f9 100%);
    color: #ffffff;

    h2 {
      margin: 0 0 8px;
      font-size: 22px;
    }

    p {
      margin: 0;
      opacity: 0.95;
    }

    .hero-time {
      font-size: 13px;
      opacity: 0.92;
      white-space: nowrap;
    }
  }

  .summary-row {
    margin-bottom: 14px;
  }

  .summary-card {
    .label {
      color: #6b7280;
      font-size: 12px;
      margin-bottom: 8px;
    }

    .value {
      color: #111827;
      font-size: 26px;
      font-weight: 700;
      line-height: 1.2;
    }

    &.emphasis .value {
      color: #b45309;
    }
  }

  .mb14 {
    margin-bottom: 14px;
  }

  .panel-card {
    .panel-title {
      font-size: 15px;
      font-weight: 700;
      color: #0f172a;
    }
  }

  .zone-list {
    display: flex;
    flex-direction: column;
    gap: 16px;

    .zone-item {
      background: #f8fafc;
      border-radius: 10px;
      padding: 12px;

      .zone-top {
        display: flex;
        justify-content: space-between;
        margin-bottom: 8px;

        .name {
          font-weight: 600;
          color: #1f2937;
        }

        .money {
          font-weight: 700;
          color: #0f766e;
        }
      }

      .zone-meta {
        margin-top: 8px;
        color: #6b7280;
        font-size: 12px;
      }
    }
  }
}

@media (max-width: 768px) {
  .perf-page {
    .hero {
      flex-direction: column;
      align-items: flex-start;
      gap: 8px;
    }
  }
}
</style>

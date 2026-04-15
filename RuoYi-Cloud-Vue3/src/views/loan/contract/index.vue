<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="合同编号" prop="contractNo">
        <el-input v-model="queryParams.contractNo" placeholder="请输入合同编号" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="客户姓名" prop="customerName">
        <el-input v-model="queryParams.customerName" placeholder="请输入客户姓名" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="合同状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 200px">
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['loan:contract:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['loan:contract:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['loan:contract:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="contractList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="合同ID" align="center" prop="contractId" width="90" />
      <el-table-column label="合同编号" align="center" prop="contractNo" width="150" />
      <el-table-column label="客户姓名" align="center" prop="customerName" width="120" />
      <el-table-column label="合同金额" align="center" prop="contractAmount" width="120" />
      <el-table-column label="发放金额" align="center" prop="disburseAmount" width="120" />
      <el-table-column label="前置服务费" align="center" width="160">
        <template #default="scope">
          <span>{{ scope.row.firstFeePaid || 0 }} / {{ scope.row.firstFeeAmount || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="放款后服务费" align="center" width="170">
        <template #default="scope">
          <span>{{ scope.row.secondFeePaid || 0 }} / {{ scope.row.secondFeeAmount || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="120">
        <template #default="scope">
          <el-tag :type="statusTagType(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="签约时间" align="center" prop="signTime" width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.signTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" min-width="360" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Promotion" @click="handleSubmitReview(scope.row)" v-hasPermi="['loan:contract:submit']">提交审核</el-button>
          <el-button link type="primary" icon="Checked" @click="handleOpenAudit(scope.row)" v-hasPermi="['loan:contract:audit']">金融审核</el-button>
          <el-button link type="primary" icon="Coin" @click="handleOpenFee(scope.row)" v-hasPermi="['loan:contract:fee']">服务费</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['loan:contract:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['loan:contract:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="title" v-model="open" width="860px" append-to-body>
      <el-form ref="loanContractRef" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="合同编号" prop="contractNo">
              <el-input v-model="form.contractNo" placeholder="请输入合同编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户ID" prop="customerId">
              <el-input-number v-model="form.customerId" :min="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="销售ID" prop="userId">
              <el-input-number v-model="form.userId" :min="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门ID" prop="deptId">
              <el-input-number v-model="form.deptId" :min="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="金融产品" prop="productName">
              <el-input v-model="form.productName" placeholder="请输入产品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="附件地址" prop="attachmentUrls">
              <el-input v-model="form.attachmentUrls" placeholder="多个附件请用逗号分隔" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同金额" prop="contractAmount">
              <el-input-number v-model="form.contractAmount" :min="0" :precision="2" :step="1000" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发放金额" prop="disburseAmount">
              <el-input-number v-model="form.disburseAmount" :min="0" :precision="2" :step="1000" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="前置服务费" prop="firstFeeAmount">
              <el-input-number v-model="form.firstFeeAmount" :min="0" :precision="2" :step="100" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="放款后服务费" prop="secondFeeAmount">
              <el-input-number v-model="form.secondFeeAmount" :min="0" :precision="2" :step="100" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态">
                <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="签约时间" prop="signTime">
              <el-date-picker v-model="form.signTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="3" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog title="金融审核" v-model="auditOpen" width="560px" append-to-body>
      <el-form ref="auditRef" :model="auditForm" :rules="auditRules" label-width="110px">
        <el-form-item label="合同ID" prop="contractId">
          <el-input v-model="auditForm.contractId" disabled />
        </el-form-item>
        <el-form-item label="审核结果" prop="status">
          <el-select v-model="auditForm.status" placeholder="请选择审核结果" style="width: 100%">
            <el-option label="进入银行审核" value="BANK_REVIEW" />
            <el-option label="放款成功" value="LOANED" />
            <el-option label="拒绝" value="REJECTED" />
          </el-select>
        </el-form-item>
        <el-form-item label="发放金额" prop="disburseAmount">
          <el-input-number v-model="auditForm.disburseAmount" :min="0" :precision="2" :step="1000" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="放款时间" prop="bankLoanTime">
          <el-date-picker v-model="auditForm.bankLoanTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="银行反馈" prop="bankResult">
          <el-input v-model="auditForm.bankResult" type="textarea" :rows="3" placeholder="请输入银行反馈" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitAudit">确 定</el-button>
          <el-button @click="auditOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog :title="feeTitle" v-model="feeOpen" width="900px" append-to-body>
      <el-form ref="feeRef" :model="feeForm" :rules="feeRules" :inline="true" class="mb8">
        <el-form-item label="阶段" prop="stage">
          <el-select v-model="feeForm.stage" style="width: 130px">
            <el-option label="前置" value="PRE" />
            <el-option label="放款后" value="POST" />
          </el-select>
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input-number v-model="feeForm.amount" :min="0" :precision="2" :step="100" controls-position="right" style="width: 140px" />
        </el-form-item>
        <el-form-item label="付款人" prop="payerName">
          <el-input v-model="feeForm.payerName" style="width: 130px" />
        </el-form-item>
        <el-form-item label="支付方式" prop="payMethod">
          <el-select v-model="feeForm.payMethod" style="width: 140px">
            <el-option label="现金" value="CASH" />
            <el-option label="转账" value="TRANSFER" />
            <el-option label="线上支付" value="ONLINE" />
          </el-select>
        </el-form-item>
        <el-form-item label="收款时间" prop="payTime">
          <el-date-picker v-model="feeForm.payTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Plus" @click="submitFee" v-hasPermi="['loan:contract:fee']">新增收款</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="feeList" v-loading="feeLoading">
        <el-table-column label="记录ID" prop="feeId" width="90" align="center" />
        <el-table-column label="阶段" prop="stage" width="90" align="center" />
        <el-table-column label="金额" prop="amount" width="120" align="center" />
        <el-table-column label="付款人" prop="payerName" width="120" align="center" />
        <el-table-column label="支付方式" prop="payMethod" width="120" align="center" />
        <el-table-column label="状态" prop="status" width="90" align="center" />
        <el-table-column label="收款人" prop="collectorUserName" width="100" align="center" />
        <el-table-column label="收款时间" prop="payTime" width="170" align="center">
          <template #default="scope">
            <span>{{ parseTime(scope.row.payTime) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup name="LoanContract">
import {
  listLoanContract,
  getLoanContract,
  addLoanContract,
  updateLoanContract,
  delLoanContract,
  submitFinanceReview,
  financeAudit,
  listContractFee,
  collectContractFee
} from '@/api/loan/contract'

const { proxy } = getCurrentInstance()

const loading = ref(true)
const showSearch = ref(true)
const contractList = ref([])
const total = ref(0)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const open = ref(false)
const title = ref('')

const auditOpen = ref(false)
const feeOpen = ref(false)
const feeTitle = ref('')
const feeLoading = ref(false)
const feeList = ref([])

const statusOptions = [
  { label: '已签约', value: 'SIGNED' },
  { label: '金融审核中', value: 'FINANCE_REVIEW' },
  { label: '银行审核中', value: 'BANK_REVIEW' },
  { label: '已放款', value: 'LOANED' },
  { label: '已拒绝', value: 'REJECTED' }
]

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    contractNo: undefined,
    customerName: undefined,
    status: undefined
  },
  form: {},
  auditForm: {},
  feeForm: {},
  rules: {
    contractNo: [{ required: true, message: '合同编号不能为空', trigger: 'blur' }],
    customerId: [{ required: true, message: '客户ID不能为空', trigger: 'blur' }]
  },
  auditRules: {
    status: [{ required: true, message: '审核结果不能为空', trigger: 'change' }]
  },
  feeRules: {
    stage: [{ required: true, message: '阶段不能为空', trigger: 'change' }],
    amount: [{ required: true, message: '金额不能为空', trigger: 'blur' }]
  }
})

const { queryParams, form, auditForm, feeForm, rules, auditRules, feeRules } = toRefs(data)

function getList() {
  loading.value = true
  listLoanContract(queryParams.value).then(response => {
    contractList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

function statusLabel(status) {
  const item = statusOptions.find(x => x.value === status)
  return item ? item.label : status
}

function statusTagType(status) {
  if (status === 'LOANED') {
    return 'success'
  }
  if (status === 'REJECTED') {
    return 'danger'
  }
  if (status === 'FINANCE_REVIEW' || status === 'BANK_REVIEW') {
    return 'warning'
  }
  return 'info'
}

function reset() {
  form.value = {
    contractId: undefined,
    contractNo: undefined,
    customerId: undefined,
    userId: undefined,
    deptId: undefined,
    productName: undefined,
    attachmentUrls: undefined,
    contractAmount: 0,
    disburseAmount: 0,
    firstFeeAmount: 0,
    secondFeeAmount: 0,
    status: 'SIGNED',
    signTime: undefined,
    remark: undefined
  }
  proxy.resetForm('loanContractRef')
}

function resetAudit() {
  auditForm.value = {
    contractId: undefined,
    status: 'BANK_REVIEW',
    disburseAmount: 0,
    bankLoanTime: undefined,
    bankResult: undefined
  }
  proxy.resetForm('auditRef')
}

function resetFee(contractId) {
  feeForm.value = {
    contractId,
    stage: 'PRE',
    amount: 0,
    payMethod: 'TRANSFER',
    payerName: undefined,
    payTime: undefined,
    remark: undefined
  }
  proxy.resetForm('feeRef')
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm('queryRef')
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.contractId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = '新增合同'
}

function handleUpdate(row) {
  const contractId = row.contractId || ids.value[0]
  reset()
  getLoanContract(contractId).then(response => {
    form.value = response.data
    open.value = true
    title.value = '修改合同'
  })
}

function submitForm() {
  proxy.$refs.loanContractRef.validate(valid => {
    if (!valid) return
    if (form.value.contractId) {
      updateLoanContract(form.value).then(() => {
        proxy.$modal.msgSuccess('修改成功')
        open.value = false
        getList()
      })
    } else {
      addLoanContract(form.value).then(() => {
        proxy.$modal.msgSuccess('新增成功')
        open.value = false
        getList()
      })
    }
  })
}

function cancel() {
  open.value = false
  reset()
}

function handleDelete(row) {
  const contractIds = row.contractId || ids.value
  proxy.$modal.confirm('是否确认删除合同编号为"' + contractIds + '"的数据项？').then(() => {
    return delLoanContract(contractIds)
  }).then(() => {
    proxy.$modal.msgSuccess('删除成功')
    getList()
  }).catch(() => {})
}

function handleSubmitReview(row) {
  proxy.$modal.confirm('确认提交合同"' + row.contractNo + '"到金融部审核？').then(() => {
    return submitFinanceReview(row.contractId)
  }).then(() => {
    proxy.$modal.msgSuccess('已提交金融审核')
    getList()
  }).catch(() => {})
}

function handleOpenAudit(row) {
  resetAudit()
  auditForm.value.contractId = row.contractId
  auditForm.value.disburseAmount = row.disburseAmount || 0
  auditOpen.value = true
}

function submitAudit() {
  proxy.$refs.auditRef.validate(valid => {
    if (!valid) return
    financeAudit(auditForm.value).then(() => {
      proxy.$modal.msgSuccess('审核结果已提交')
      auditOpen.value = false
      getList()
    })
  })
}

function handleOpenFee(row) {
  feeTitle.value = '服务费收款 - ' + row.contractNo
  feeOpen.value = true
  resetFee(row.contractId)
  getFeeList(row.contractId)
}

function getFeeList(contractId) {
  feeLoading.value = true
  listContractFee({ contractId, pageNum: 1, pageSize: 100 }).then(response => {
    feeList.value = response.rows || []
    feeLoading.value = false
  })
}

function submitFee() {
  proxy.$refs.feeRef.validate(valid => {
    if (!valid) return
    collectContractFee(feeForm.value).then(() => {
      proxy.$modal.msgSuccess('收款成功')
      getFeeList(feeForm.value.contractId)
      getList()
    })
  })
}

getList()
</script>

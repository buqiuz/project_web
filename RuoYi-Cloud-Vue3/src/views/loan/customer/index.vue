<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="客户姓名" prop="customerName">
        <el-input v-model="queryParams.customerName" placeholder="请输入客户姓名" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="联系电话" prop="phone">
        <el-input v-model="queryParams.phone" placeholder="请输入联系电话" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="客户状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 200px">
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="意向等级" prop="intentLevel">
        <el-select v-model="queryParams.intentLevel" placeholder="请选择等级" clearable style="width: 200px">
          <el-option v-for="item in intentOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['loan:customer:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['loan:customer:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['loan:customer:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="RefreshLeft" @click="handleReleaseTimeout" v-hasPermi="['loan:customer:release']">释放超期(7天)</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="customerList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="客户ID" align="center" prop="customerId" width="90" />
      <el-table-column label="客户姓名" align="center" prop="customerName" min-width="130" />
      <el-table-column label="类型" align="center" prop="customerType" width="80">
        <template #default="scope">
          <span>{{ customerTypeLabel(scope.row.customerType) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="电话" align="center" prop="phone" width="130" />
      <el-table-column label="意向等级" align="center" prop="intentLevel" width="90" />
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template #default="scope">
          <el-tag :type="statusTagType(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="归属销售" align="center" prop="userName" width="100" />
      <el-table-column label="归属部门" align="center" prop="deptName" min-width="120" />
      <el-table-column label="最近跟进" align="center" prop="lastFollowTime" width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.lastFollowTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="下次跟进" align="center" prop="nextFollowTime" width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.nextFollowTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" min-width="340" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="ChatRound" @click="handleOpenFollow(scope.row)" v-hasPermi="['loan:customer:follow:add']">跟进</el-button>
          <el-button link type="primary" icon="Switch" @click="handleOpenTransfer(scope.row)" v-hasPermi="['loan:customer:transfer']">转移</el-button>
          <el-button link type="primary" icon="Upload" @click="handleToSea(scope.row)" v-hasPermi="['loan:customer:sea']">转公海</el-button>
          <el-button link type="primary" icon="Download" @click="handleClaim(scope.row)" v-hasPermi="['loan:customer:claim']">领取</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['loan:customer:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['loan:customer:remove']">删除</el-button>
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

    <el-dialog :title="title" v-model="open" width="780px" append-to-body>
      <el-form ref="loanCustomerRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="客户姓名" prop="customerName">
              <el-input v-model="form.customerName" placeholder="请输入客户姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户类型" prop="customerType">
              <el-select v-model="form.customerType" placeholder="请选择类型">
                <el-option v-for="item in customerTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCardNo">
              <el-input v-model="form.idCardNo" placeholder="个人客户填写" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公司名称" prop="companyName">
              <el-input v-model="form.companyName" placeholder="企业客户填写" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="营业执照号" prop="businessLicenseNo">
              <el-input v-model="form.businessLicenseNo" placeholder="企业客户填写" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户来源" prop="sourceType">
              <el-select v-model="form.sourceType" placeholder="请选择来源">
                <el-option v-for="item in sourceTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="意向等级" prop="intentLevel">
              <el-select v-model="form.intentLevel" placeholder="请选择等级">
                <el-option v-for="item in intentOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="意向金额" prop="intentAmount">
              <el-input-number v-model="form.intentAmount" :min="0" :precision="2" :step="1000" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态">
                <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
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

    <el-dialog title="客户转移" v-model="transferOpen" width="520px" append-to-body>
      <el-form ref="transferRef" :model="transferForm" :rules="transferRules" label-width="110px">
        <el-form-item label="客户ID" prop="customerId">
          <el-input v-model="transferForm.customerId" disabled />
        </el-form-item>
        <el-form-item label="目标销售ID" prop="userId">
          <el-input-number v-model="transferForm.userId" :min="1" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="目标部门ID" prop="deptId">
          <el-input-number v-model="transferForm.deptId" :min="1" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="转移说明" prop="transferRemark">
          <el-input v-model="transferForm.transferRemark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitTransfer">确 定</el-button>
          <el-button @click="transferOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog :title="followTitle" v-model="followOpen" width="920px" append-to-body>
      <el-form ref="followRef" :model="followForm" :rules="followRules" :inline="true" class="mb8">
        <el-form-item label="跟进方式" prop="followType">
          <el-select v-model="followForm.followType" style="width: 160px">
            <el-option label="电话" value="PHONE" />
            <el-option label="面谈" value="MEET" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="意向等级" prop="intentLevel">
          <el-select v-model="followForm.intentLevel" style="width: 120px">
            <el-option v-for="item in intentOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="下次跟进" prop="nextFollowTime">
          <el-date-picker v-model="followForm.nextFollowTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 190px" />
        </el-form-item>
        <el-form-item label="有效跟进" prop="validFlag">
          <el-select v-model="followForm.validFlag" style="width: 120px">
            <el-option label="是" value="1" />
            <el-option label="否" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Plus" @click="submitFollow" v-hasPermi="['loan:customer:follow:add']">新增跟进</el-button>
        </el-form-item>
        <el-form-item label="跟进内容" prop="followContent" style="display: block; width: 100%;">
          <el-input v-model="followForm.followContent" type="textarea" :rows="3" placeholder="请输入跟进内容" />
        </el-form-item>
      </el-form>

      <el-table :data="followList" v-loading="followLoading">
        <el-table-column label="跟进ID" prop="followId" width="90" align="center" />
        <el-table-column label="方式" prop="followType" width="90" align="center" />
        <el-table-column label="内容" prop="followContent" min-width="280" show-overflow-tooltip />
        <el-table-column label="意向" prop="intentLevel" width="80" align="center" />
        <el-table-column label="有效" prop="validFlag" width="80" align="center">
          <template #default="scope">
            <span>{{ scope.row.validFlag === '1' ? '是' : '否' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="下次跟进" prop="nextFollowTime" width="160" align="center">
          <template #default="scope">
            <span>{{ parseTime(scope.row.nextFollowTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="跟进人" prop="userName" width="90" align="center" />
        <el-table-column label="跟进时间" prop="createTime" width="160" align="center">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="primary" icon="Delete" @click="handleDeleteFollow(scope.row)" v-hasPermi="['loan:customer:follow:remove']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup name="LoanCustomer">
import {
  listLoanCustomer,
  getLoanCustomer,
  addLoanCustomer,
  updateLoanCustomer,
  delLoanCustomer,
  transferLoanCustomer,
  toSeaLoanCustomer,
  claimLoanCustomer,
  releaseTimeoutCustomer,
  listLoanFollow,
  addLoanFollow,
  delLoanFollow
} from '@/api/loan/customer'

const { proxy } = getCurrentInstance()

const loading = ref(true)
const showSearch = ref(true)
const customerList = ref([])
const total = ref(0)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const open = ref(false)
const title = ref('')

const transferOpen = ref(false)
const followOpen = ref(false)
const followTitle = ref('')
const followLoading = ref(false)
const currentCustomerId = ref(undefined)
const followList = ref([])

const customerTypeOptions = [
  { label: '个人', value: 'P' },
  { label: '企业', value: 'C' }
]

const statusOptions = [
  { label: '私有', value: 'PRIVATE' },
  { label: '公海', value: 'SEA' },
  { label: '已签约', value: 'SIGNED' }
]

const sourceTypeOptions = [
  { label: '电话开发', value: 'PHONE' },
  { label: '转介绍', value: 'REFERRAL' },
  { label: '活动渠道', value: 'ACTIVITY' },
  { label: '其他', value: 'OTHER' }
]

const intentOptions = [
  { label: 'A', value: 'A' },
  { label: 'B', value: 'B' },
  { label: 'C', value: 'C' },
  { label: 'D', value: 'D' }
]

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    customerName: undefined,
    phone: undefined,
    status: undefined,
    intentLevel: undefined
  },
  form: {},
  transferForm: {},
  followForm: {},
  rules: {
    customerName: [{ required: true, message: '客户姓名不能为空', trigger: 'blur' }],
    customerType: [{ required: true, message: '客户类型不能为空', trigger: 'change' }],
    phone: [{ required: true, message: '联系电话不能为空', trigger: 'blur' }]
  },
  transferRules: {
    userId: [{ required: true, message: '目标销售ID不能为空', trigger: 'blur' }],
    deptId: [{ required: true, message: '目标部门ID不能为空', trigger: 'blur' }]
  },
  followRules: {
    followType: [{ required: true, message: '请选择跟进方式', trigger: 'change' }],
    followContent: [{ required: true, message: '请输入跟进内容', trigger: 'blur' }]
  }
})

const { queryParams, form, transferForm, followForm, rules, transferRules, followRules } = toRefs(data)

function getList() {
  loading.value = true
  listLoanCustomer(queryParams.value).then(response => {
    customerList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

function statusLabel(status) {
  const item = statusOptions.find(x => x.value === status)
  return item ? item.label : status
}

function customerTypeLabel(type) {
  const item = customerTypeOptions.find(x => x.value === type)
  return item ? item.label : type
}

function statusTagType(status) {
  if (status === 'SIGNED') {
    return 'success'
  }
  if (status === 'SEA') {
    return 'warning'
  }
  return 'info'
}

function reset() {
  form.value = {
    customerId: undefined,
    customerName: undefined,
    customerType: 'P',
    phone: undefined,
    idCardNo: undefined,
    companyName: undefined,
    businessLicenseNo: undefined,
    sourceType: 'PHONE',
    intentLevel: 'C',
    intentAmount: 0,
    status: 'PRIVATE',
    remark: undefined
  }
  proxy.resetForm('loanCustomerRef')
}

function resetFollow() {
  followForm.value = {
    customerId: currentCustomerId.value,
    followType: 'PHONE',
    followContent: undefined,
    validFlag: '1',
    intentLevel: 'C',
    nextFollowTime: undefined
  }
  proxy.resetForm('followRef')
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
  ids.value = selection.map(item => item.customerId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = '新增客户'
}

function handleUpdate(row) {
  const customerId = row.customerId || ids.value[0]
  reset()
  getLoanCustomer(customerId).then(response => {
    form.value = response.data
    open.value = true
    title.value = '修改客户'
  })
}

function submitForm() {
  proxy.$refs.loanCustomerRef.validate(valid => {
    if (!valid) return
    if (form.value.customerId) {
      updateLoanCustomer(form.value).then(() => {
        proxy.$modal.msgSuccess('修改成功')
        open.value = false
        getList()
      })
    } else {
      addLoanCustomer(form.value).then(() => {
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
  const customerIds = row.customerId || ids.value
  proxy.$modal.confirm('是否确认删除客户编号为"' + customerIds + '"的数据项？').then(() => {
    return delLoanCustomer(customerIds)
  }).then(() => {
    proxy.$modal.msgSuccess('删除成功')
    getList()
  }).catch(() => {})
}

function handleOpenTransfer(row) {
  transferForm.value = {
    customerId: row.customerId,
    userId: undefined,
    deptId: undefined,
    transferRemark: undefined
  }
  proxy.resetForm('transferRef')
  transferOpen.value = true
}

function submitTransfer() {
  proxy.$refs.transferRef.validate(valid => {
    if (!valid) return
    transferLoanCustomer(transferForm.value).then(() => {
      proxy.$modal.msgSuccess('转移成功')
      transferOpen.value = false
      getList()
    })
  })
}

function handleToSea(row) {
  proxy.$modal.confirm('确认将客户"' + row.customerName + '"转入公海？').then(() => {
    return toSeaLoanCustomer(row.customerId)
  }).then(() => {
    proxy.$modal.msgSuccess('已转入公海')
    getList()
  }).catch(() => {})
}

function handleClaim(row) {
  proxy.$modal.confirm('确认领取客户"' + row.customerName + '"？').then(() => {
    return claimLoanCustomer(row.customerId)
  }).then(() => {
    proxy.$modal.msgSuccess('领取成功')
    getList()
  }).catch(() => {})
}

function handleReleaseTimeout() {
  proxy.$modal.confirm('确认按7天规则释放超期客户到公海？').then(() => {
    return releaseTimeoutCustomer(7)
  }).then(response => {
    proxy.$modal.msgSuccess(response.msg || '执行成功')
    getList()
  }).catch(() => {})
}

function handleOpenFollow(row) {
  currentCustomerId.value = row.customerId
  followTitle.value = '客户跟进 - ' + row.customerName
  followOpen.value = true
  resetFollow()
  getFollowList()
}

function getFollowList() {
  followLoading.value = true
  listLoanFollow({ customerId: currentCustomerId.value, pageNum: 1, pageSize: 100 }).then(response => {
    followList.value = response.rows || []
    followLoading.value = false
  })
}

function submitFollow() {
  followForm.value.customerId = currentCustomerId.value
  proxy.$refs.followRef.validate(valid => {
    if (!valid) return
    addLoanFollow(followForm.value).then(() => {
      proxy.$modal.msgSuccess('跟进记录已保存')
      resetFollow()
      getFollowList()
      getList()
    })
  })
}

function handleDeleteFollow(row) {
  proxy.$modal.confirm('确认删除该跟进记录？').then(() => {
    return delLoanFollow(row.followId)
  }).then(() => {
    proxy.$modal.msgSuccess('删除成功')
    getFollowList()
  }).catch(() => {})
}

getList()
</script>

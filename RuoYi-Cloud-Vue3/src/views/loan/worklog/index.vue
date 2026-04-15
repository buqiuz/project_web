<template>
  <div class="app-container worklog-page">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="销售账号" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入销售账号" clearable style="width: 220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="工作日期">
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
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['loan:worklog:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Calendar" @click="handleToday" v-hasPermi="['loan:worklog:add']">今日填报</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['loan:worklog:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['loan:worklog:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="workLogList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="日期" align="center" prop="workDate" width="120">
        <template #default="scope">
          <span>{{ parseTime(scope.row.workDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="销售" align="center" prop="userName" width="120" />
      <el-table-column label="部门" align="center" prop="deptName" min-width="140" />
      <el-table-column label="电话数" align="center" prop="callCount" width="100" />
      <el-table-column label="有效电话" align="center" prop="validCallCount" width="100" />
      <el-table-column label="意向客户" align="center" prop="intentCustomerCount" width="100" />
      <el-table-column label="面谈客户" align="center" prop="visitCount" width="100" />
      <el-table-column label="签约数" align="center" prop="signedCount" width="90" />
      <el-table-column label="有效率" align="center" width="170">
        <template #default="scope">
          <el-progress :stroke-width="12" :percentage="validRate(scope.row)" :text-inside="true" :color="progressColor(validRate(scope.row))" />
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" min-width="180" show-overflow-tooltip />
      <el-table-column label="操作" align="center" width="180" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['loan:worklog:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['loan:worklog:remove']">删除</el-button>
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

    <el-dialog :title="title" v-model="open" width="760px" append-to-body>
      <el-form ref="workLogRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="工作日期" prop="workDate">
              <el-date-picker v-model="form.workDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电话数" prop="callCount">
              <el-input-number v-model="form.callCount" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="有效电话" prop="validCallCount">
              <el-input-number v-model="form.validCallCount" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="意向客户" prop="intentCustomerCount">
              <el-input-number v-model="form.intentCustomerCount" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="面谈客户" prop="visitCount">
              <el-input-number v-model="form.visitCount" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="签约数" prop="signedCount">
              <el-input-number v-model="form.signedCount" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入当天工作总结" />
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
  </div>
</template>

<script setup name="LoanWorkLog">
import { parseTime } from '@/utils/ruoyi'
import {
  listLoanWorkLog,
  getLoanWorkLog,
  getMyTodayWorkLog,
  addLoanWorkLog,
  updateLoanWorkLog,
  delLoanWorkLog
} from '@/api/loan/worklog'

const { proxy } = getCurrentInstance()

const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const workLogList = ref([])
const dateRange = ref([])
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const open = ref(false)
const title = ref('')

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    userName: undefined,
    params: {}
  },
  form: {},
  rules: {
    workDate: [{ required: true, message: '工作日期不能为空', trigger: 'change' }],
    callCount: [{ required: true, message: '电话数不能为空', trigger: 'blur' }],
    validCallCount: [{ required: true, message: '有效电话不能为空', trigger: 'blur' }],
    intentCustomerCount: [{ required: true, message: '意向客户数不能为空', trigger: 'blur' }],
    visitCount: [{ required: true, message: '面谈客户数不能为空', trigger: 'blur' }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function validRate(row) {
  const all = Number(row.callCount || 0)
  const valid = Number(row.validCallCount || 0)
  if (!all) {
    return 0
  }
  const value = (valid * 100) / all
  return Number(value.toFixed(2))
}

function progressColor(rate) {
  if (rate >= 65) {
    return '#22c55e'
  }
  if (rate >= 40) {
    return '#f59e0b'
  }
  return '#ef4444'
}

function getList() {
  loading.value = true
  const params = { ...queryParams.value, params: { ...queryParams.value.params } }
  if (dateRange.value && dateRange.value.length === 2) {
    params.params.beginTime = dateRange.value[0]
    params.params.endTime = dateRange.value[1]
  }
  listLoanWorkLog(params).then(res => {
    workLogList.value = res.rows
    total.value = res.total
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

function reset() {
  form.value = {
    logId: undefined,
    workDate: parseTime(new Date(), '{y}-{m}-{d}'),
    callCount: 0,
    validCallCount: 0,
    intentCustomerCount: 0,
    visitCount: 0,
    signedCount: 0,
    remark: undefined
  }
  proxy.resetForm('workLogRef')
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  dateRange.value = []
  queryParams.value.params = {}
  proxy.resetForm('queryRef')
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.logId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = '新增工作日志'
}

function handleToday() {
  reset()
  getMyTodayWorkLog().then(res => {
    if (res.data) {
      form.value = { ...res.data }
      title.value = '今日工作日志（已填报，继续修改）'
    } else {
      title.value = '今日工作日志填报'
    }
    open.value = true
  })
}

function handleUpdate(row) {
  reset()
  const logId = row.logId || ids.value[0]
  getLoanWorkLog(logId).then(res => {
    form.value = res.data
    open.value = true
    title.value = '修改工作日志'
  })
}

function submitForm() {
  proxy.$refs.workLogRef.validate(valid => {
    if (!valid) {
      return
    }
    if (form.value.logId) {
      updateLoanWorkLog(form.value).then(() => {
        proxy.$modal.msgSuccess('修改成功')
        open.value = false
        getList()
      })
      return
    }
    addLoanWorkLog(form.value).then(() => {
      proxy.$modal.msgSuccess('新增成功')
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const logIds = row.logId || ids.value
  proxy.$modal.confirm('确认删除工作日志编号为"' + logIds + '"的数据项？').then(() => {
    return delLoanWorkLog(logIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  })
}

function cancel() {
  open.value = false
  reset()
}

getList()
</script>

<style lang="scss" scoped>
.worklog-page {
  .el-progress {
    margin-top: 4px;
  }
}
</style>

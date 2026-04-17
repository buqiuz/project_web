<template>
  <div>
    <van-nav-bar title="客户管理" fixed placeholder />
    <van-tabs v-model:active="activeTab" sticky offset-top="46" @change="onTabChange">
      <!-- 我的客户 -->
      <van-tab title="我的客户">
        <div class="tab-content">
          <van-button type="primary" block round icon="plus" @click="showAddPopup = true" class="add-btn">新建客户</van-button>

          <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
            <van-cell-group inset v-for="cust in store.myCustomers" :key="cust.id" class="customer-card">
              <van-cell :title="cust.name" :label="buildCustomerLabel(cust)" is-link @click="viewDetail(cust)">
                <template #value>
                  <van-tag :type="getStatusType(cust.status)" size="small">{{ cust.status }}</van-tag>
                </template>
              </van-cell>
              <div class="card-actions">
                <van-button size="small" plain type="success" @click="showContractDialog(cust)" v-if="!cust.hasContract && cust.status !== '已放款'">
                  <van-icon name="description" style="margin-right: 4px;" />
                  签署合同
                </van-button>
                <van-button size="small" plain type="primary" @click="showFollowDialog(cust)">
                  <van-icon name="edit" style="margin-right: 4px;" />
                  跟进
                </van-button>
                <van-button size="small" plain @click="showEditDialog(cust)">
                  <van-icon name="replay" style="margin-right: 4px;" />
                  编辑
                </van-button>
                <van-button size="small" plain type="warning" @click="moveToSea(cust)">
                  <van-icon name="exchange" style="margin-right: 4px;" />
                  转公海
                </van-button>
              </div>
            </van-cell-group>
          </van-pull-refresh>

          <van-loading v-if="store.loading" class="loading-tip">加载中...</van-loading>
          <div v-else-if="store.myCustomers.length===0 && !hasLoadedMy" class="empty-tip">下拉刷新或切换标签页加载数据</div>
          <div v-else-if="store.myCustomers.length===0" class="empty-tip">暂无未签约客户</div>
        </div>
      </van-tab>

      <!-- 公海客户 -->
      <van-tab title="公海客户">
        <div class="tab-content">
          <van-notice-bar left-icon="info-o" text="公海客户超过7天未签约自动释放,可领取跟进" />
          <van-pull-refresh v-model="seaRefreshing" @refresh="onSeaRefresh">
            <van-cell-group inset v-for="cust in store.seaCustomers" :key="cust.id" class="customer-card">
              <van-cell :title="cust.name" :label="buildSeaCustomerLabel(cust)" is-link @click="viewSeaDetail(cust)">
                <template #value>
                  <van-tag type="danger" size="small">释放{{ cust.releaseDays }}天</van-tag>
                </template>
              </van-cell>
              <div class="card-actions">
                <van-button size="small" type="primary" @click="claim(cust)">领取</van-button>
              </div>
            </van-cell-group>
          </van-pull-refresh>
          <van-loading v-if="store.loading" class="loading-tip">加载中...</van-loading>
          <div v-else-if="store.seaCustomers.length===0 && !hasLoadedSea" class="empty-tip">下拉刷新或切换标签页加载数据</div>
          <div v-else-if="store.seaCustomers.length===0" class="empty-tip">公海暂无客户</div>
        </div>
      </van-tab>
    </van-tabs>

    <!-- 添加/编辑客户弹窗 -->
    <van-popup v-model:show="showAddPopup" position="bottom" round :style="{ height: '85%' }">
      <div class="popup-content">
        <h3>{{ isEditMode ? '编辑客户' : '新建客户' }}</h3>
        <van-form ref="addCustomerFormRef" @submit="onAddCustomer" @failed="onAddCustomerFailed">
          <van-field
            v-model="newCustomer.customerName"
            name="customerName"
            label="客户姓名"
            placeholder="请输入客户姓名"
            :rules="[{ required: true, message: '请填写客户姓名' }]"
          >
            <template #left-icon>
              <van-icon name="user-o" />
            </template>
          </van-field>

          <van-field name="customerType" label="客户类型" :rules="[{ required: true, message: '请选择客户类型' }]">
            <template #left-icon>
              <van-icon name="friends-o" />
            </template>
            <template #input>
              <van-radio-group v-model="newCustomer.customerType" direction="horizontal">
                <van-radio name="P">个人</van-radio>
                <van-radio name="C">企业</van-radio>
              </van-radio-group>
            </template>
          </van-field>

          <van-field
            v-model="newCustomer.phone"
            name="phone"
            label="联系电话"
            type="tel"
            placeholder="请输入联系电话"
            :rules="[
              { required: true, message: '请填写联系电话' },
              { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }
            ]"
          >
            <template #left-icon>
              <van-icon name="phone-o" />
            </template>
          </van-field>

          <van-field
            v-if="newCustomer.customerType === 'P'"
            v-model="newCustomer.idCardNo"
            label="身份证号"
            placeholder="个人客户填写"
          >
            <template #left-icon>
              <van-icon name="idcard" />
            </template>
          </van-field>

          <van-field
            v-if="newCustomer.customerType === 'C'"
            v-model="newCustomer.companyName"
            label="公司名称"
            placeholder="企业客户填写"
            :rules="[{ required: true, message: '请填写公司名称' }]"
          >
            <template #left-icon>
              <van-icon name="shop-o" />
            </template>
          </van-field>

          <van-field
            v-if="newCustomer.customerType === 'C'"
            v-model="newCustomer.businessLicenseNo"
            label="营业执照号"
            placeholder="企业客户填写"
          >
            <template #left-icon>
              <van-icon name="certificate" />
            </template>
          </van-field>

          <van-field name="sourceType" label="客户来源" :rules="[{ required: true, message: '请选择客户来源' }]">
            <template #left-icon>
              <van-icon name="guide-o" />
            </template>
            <template #input>
              <van-radio-group v-model="newCustomer.sourceType">
                <van-cell-group inset>
                  <van-cell
                    v-for="item in sourceTypeOptions"
                    :key="item.value"
                    clickable
                    @click="newCustomer.sourceType = item.value"
                  >
                    <template #title>
                      {{ item.text }}
                    </template>
                    <template #right-icon>
                      <van-radio :name="item.value" />
                    </template>
                  </van-cell>
                </van-cell-group>
              </van-radio-group>
            </template>
          </van-field>

          <van-field name="intentLevel" label="意向等级" :rules="[{ required: true, message: '请选择意向等级' }]">
            <template #left-icon>
              <van-icon name="star-o" />
            </template>
            <template #input>
              <van-radio-group v-model="newCustomer.intentLevel" direction="horizontal">
                <van-radio name="A">A级</van-radio>
                <van-radio name="B">B级</van-radio>
                <van-radio name="C">C级</van-radio>
                <van-radio name="D">D级</van-radio>
              </van-radio-group>
            </template>
          </van-field>

          <van-field
            v-model="newCustomer.intentAmount"
            type="number"
            label="意向金额(元)"
            placeholder="请输入意向金额"
          >
            <template #left-icon>
              <van-icon name="gold-coin-o" />
            </template>
          </van-field>

          <van-field name="status" label="客户状态" :rules="[{ required: true, message: '请选择客户状态' }]">
            <template #left-icon>
              <van-icon name="flag-o" />
            </template>
            <template #input>
              <van-radio-group v-model="newCustomer.status" direction="horizontal">
                <van-radio name="PRIVATE">私有</van-radio>
                <van-radio name="SEA">公海</van-radio>
              </van-radio-group>
            </template>
          </van-field>

          <van-field
            v-model="newCustomer.remark"
            rows="2"
            autosize
            type="textarea"
            label="备注"
            placeholder="请输入备注信息"
          >
            <template #left-icon>
              <van-icon name="notes-o" />
            </template>
          </van-field>

          <div class="popup-btn">
            <van-button round block type="primary" native-type="submit" :loading="submitLoading">确认{{ isEditMode ? '修改' : '添加' }}</van-button>
          </div>
        </van-form>
      </div>
    </van-popup>

    <!-- 签署合同弹窗 -->
    <van-popup v-model:show="showContractPopup" position="bottom" round :style="{ height: '75%' }">
      <div class="popup-content">
        <h3>签署合同 - {{ selectedCust?.name }}</h3>
        <van-form @submit="onSubmitContract">
          <van-field
            v-model="contractForm.productName"
            label="金融产品"
            placeholder="请输入金融产品名称"
            :rules="[{ required: true, message: '请填写金融产品名称' }]"
          >
            <template #left-icon>
              <van-icon name="coupon-o" />
            </template>
          </van-field>

          <van-field
            v-model="contractForm.contractAmount"
            type="number"
            label="合同金额"
            placeholder="请输入合同金额"
            :rules="[{ required: true, message: '请填写合同金额' }]"
          >
            <template #left-icon>
              <van-icon name="balance-list-o" />
            </template>
          </van-field>

          <van-field
            v-model="contractForm.disburseAmount"
            type="number"
            label="发放金额"
            placeholder="银行实际放款金额（可选）"
          >
            <template #left-icon>
              <van-icon name="cash-back-record" />
            </template>
          </van-field>

          <van-field
            v-model="contractForm.firstFeeAmount"
            type="number"
            label="前置服务费"
            placeholder="签约前应收服务费"
          >
            <template #left-icon>
              <van-icon name="point-gift-o" />
            </template>
          </van-field>

          <van-field
            v-model="contractForm.secondFeeAmount"
            type="number"
            label="放款后服务费"
            placeholder="放款后应收服务费"
          >
            <template #left-icon>
              <van-icon name="gift-o" />
            </template>
          </van-field>

          <van-field
            v-model="contractForm.signTime"
            label="签约时间"
            placeholder="点击选择签约时间"
            readonly
            @click="selectSignTime"
          >
            <template #left-icon>
              <van-icon name="calendar-o" />
            </template>
          </van-field>

          <van-field
            v-model="contractForm.remark"
            rows="2"
            autosize
            type="textarea"
            label="备注"
            placeholder="请输入备注信息"
          >
            <template #left-icon>
              <van-icon name="notes-o" />
            </template>
          </van-field>

          <div class="popup-btn">
            <van-button round block type="primary" native-type="submit" :loading="submitLoading">确认签署</van-button>
          </div>
        </van-form>
      </div>
    </van-popup>

    <!-- 签约时间选择器 -->
    <van-popup v-model:show="showSignTimePicker" position="bottom" round :style="{ height: '70%' }">
      <van-calendar
        v-model:show="showSignTimePicker"
        type="datetime"
        :min-date="minDate"
        :max-date="maxDate"
        @confirm="onSignTimeConfirm"
      >
        <template #footer>
          <div style="padding: 16px;">
            <van-button block round type="primary" @click="confirmSignTime">确定</van-button>
          </div>
        </template>
      </van-calendar>
    </van-popup>

    <!-- 添加跟进记录弹窗 -->
    <van-popup v-model:show="showFollowPopup" position="bottom" round :style="{ height: '75%' }">
      <div class="popup-content">
        <h3>客户跟进 - {{ selectedCust?.name }}</h3>
        <van-form @submit="onSubmitFollow">
          <van-field name="followType" label="跟进方式" :rules="[{ required: true, message: '请选择跟进方式' }]">
            <template #left-icon>
              <van-icon name="chat-o" />
            </template>
            <template #input>
              <van-radio-group v-model="followForm.followType" direction="horizontal">
                <van-radio name="PHONE">电话</van-radio>
                <van-radio name="MEET">面谈</van-radio>
                <van-radio name="OTHER">其他</van-radio>
              </van-radio-group>
            </template>
          </van-field>

          <van-field
            v-model="followForm.followContent"
            rows="3"
            autosize
            type="textarea"
            label="跟进内容"
            placeholder="请输入跟进详情"
            :rules="[{ required: true, message: '请填写跟进内容' }]"
          >
            <template #left-icon>
              <van-icon name="comment-o" />
            </template>
          </van-field>

          <van-field name="result" label="跟进结果">
            <template #left-icon>
              <van-icon name="todo-list-o" />
            </template>
            <template #input>
              <van-radio-group v-model="followForm.result" direction="horizontal">
                <van-radio name="有意向">有意向</van-radio>
                <van-radio name="无意向">无意向</van-radio>
                <van-radio name="待跟进">待跟进</van-radio>
              </van-radio-group>
            </template>
          </van-field>

          <van-field name="validFlag" label="有效跟进">
            <template #left-icon>
              <van-icon name="passed" />
            </template>
            <template #input>
              <van-switch v-model="followForm.validFlag" active-value="1" inactive-value="0" />
            </template>
          </van-field>

          <van-field name="intentLevel" label="更新意向等级">
            <template #left-icon>
              <van-icon name="star-o" />
            </template>
            <template #input>
              <van-radio-group v-model="followForm.intentLevel" direction="horizontal">
                <van-radio name="A">A</van-radio>
                <van-radio name="B">B</van-radio>
                <van-radio name="C">C</van-radio>
                <van-radio name="D">D</van-radio>
              </van-radio-group>
            </template>
          </van-field>

          <van-field
            v-model="followForm.nextFollowTime"
            label="下次跟进"
            placeholder="点击选择日期时间"
            readonly
            @click="selectNextFollowTime"
          >
            <template #left-icon>
              <van-icon name="clock-o" />
            </template>
          </van-field>

          <div class="popup-btn">
            <van-button round block type="primary" native-type="submit">保存记录</van-button>
          </div>
        </van-form>
      </div>
    </van-popup>

    <!-- 日期时间选择器 -->
    <van-popup v-model:show="showDateTimePicker" position="bottom" round :style="{ height: '70%' }">
      <van-calendar
        v-model:show="showDateTimePicker"
        type="datetime"
        :min-date="minDate"
        :max-date="maxDate"
        @confirm="onDateTimeConfirm"
      >
        <template #footer>
          <div style="padding: 16px;">
            <van-button block round type="primary" @click="confirmDateTime">确定</van-button>
          </div>
        </template>
      </van-calendar>
    </van-popup>

    <!-- 客户详情/操作面板 -->
    <van-action-sheet v-model:show="showDetailSheet" :title="selectedCust?.name" :actions="detailActions" @select="onDetailSelect" />

    <!-- 客户详细信息弹窗 -->
    <van-popup v-model:show="showDetailPopup" position="bottom" round :style="{ height: '70%' }">
      <div class="popup-content" v-if="selectedCust">
        <h3>客户档案 - {{ selectedCust.name }}</h3>
        <van-cell-group inset>
          <van-cell title="客户类型" :value="getCustomerTypeText(selectedCust.rawData?.customerType)">
            <template #icon>
              <van-icon name="friends-o" class="cell-icon" />
            </template>
          </van-cell>
          <van-cell title="联系电话" :value="selectedCust.phone">
            <template #icon>
              <van-icon name="phone-o" class="cell-icon" />
            </template>
          </van-cell>
          <van-cell title="身份证号" :value="selectedCust.rawData?.idCardNo || '-'" v-if="selectedCust.rawData?.customerType === 'P'">
            <template #icon>
              <van-icon name="idcard" class="cell-icon" />
            </template>
          </van-cell>
          <van-cell title="公司名称" :value="selectedCust.rawData?.companyName || '-'" v-if="selectedCust.rawData?.customerType === 'C'">
            <template #icon>
              <van-icon name="shop-o" class="cell-icon" />
            </template>
          </van-cell>
          <van-cell title="营业执照号" :value="selectedCust.rawData?.businessLicenseNo || '-'" v-if="selectedCust.rawData?.customerType === 'C'">
            <template #icon>
              <van-icon name="certificate" class="cell-icon" />
            </template>
          </van-cell>
          <van-cell title="客户来源" :value="getSourceTypeText(selectedCust.rawData?.sourceType)">
            <template #icon>
              <van-icon name="guide-o" class="cell-icon" />
            </template>
          </van-cell>
          <van-cell title="意向等级" :value="selectedCust.rawData?.intentLevel">
            <template #icon>
              <van-icon name="star-o" class="cell-icon" />
            </template>
          </van-cell>
          <van-cell title="意向金额" :value="formatAmount(selectedCust.rawData?.intentAmount)">
            <template #icon>
              <van-icon name="gold-coin-o" class="cell-icon" />
            </template>
          </van-cell>
          <van-cell title="客户状态" :value="selectedCust.status">
            <template #icon>
              <van-icon name="flag-o" class="cell-icon" />
            </template>
          </van-cell>
          <van-cell title="归属销售" :value="selectedCust.rawData?.userName || '-'">
            <template #icon>
              <van-icon name="user-circle-o" class="cell-icon" />
            </template>
          </van-cell>
          <van-cell title="归属部门" :value="selectedCust.rawData?.deptName || '-'">
            <template #icon>
              <van-icon name="friends-o" class="cell-icon" />
            </template>
          </van-cell>
          <van-cell title="最近跟进" :value="formatTime(selectedCust.rawData?.lastFollowTime)">
            <template #icon>
              <van-icon name="clock-o" class="cell-icon" />
            </template>
          </van-cell>
          <van-cell title="下次跟进" :value="formatTime(selectedCust.rawData?.nextFollowTime)">
            <template #icon>
              <van-icon name="pending-deliver" class="cell-icon" />
            </template>
          </van-cell>
          <van-cell title="跟进次数" :value="String(selectedCust.followCount || 0)">
            <template #icon>
              <van-icon name="chart-trending-o" class="cell-icon" />
            </template>
          </van-cell>
          <van-cell title="备注" :value="selectedCust.rawData?.remark || '-'" label-class="remark-label">
            <template #icon>
              <van-icon name="notes-o" class="cell-icon" />
            </template>
          </van-cell>
        </van-cell-group>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useSalesStore } from '../stores/sales'
import { useAuthStore } from '../stores/auth'
import { showDialog, showToast, showConfirmDialog } from 'vant'
import dayjs from 'dayjs'
import { getCustomer, updateCustomer } from '@/api/customer'
import { addContract } from '@/api/contract'

const store = useSalesStore()
const authStore = useAuthStore()
const activeTab = ref(0)
const showAddPopup = ref(false)
const showDetailSheet = ref(false)
const showFollowPopup = ref(false)
const showDateTimePicker = ref(false)
const showDetailPopup = ref(false)
const showContractPopup = ref(false)
const showSignTimePicker = ref(false)
const submitLoading = ref(false)
const refreshing = ref(false)
const seaRefreshing = ref(false)
const isEditMode = ref(false)
const selectedCust = ref(null)
const addCustomerFormRef = ref(null)
const hasLoadedMy = ref(false)
const hasLoadedSea = ref(false)

const filteredMyCustomers = computed(() => {
  return store.myCustomers
})

// 新增/编辑客户表单（与后端字段对应）
const newCustomer = reactive({
  customerId: undefined,
  customerName: '',
  customerType: 'P',
  phone: '',
  idCardNo: '',
  companyName: '',
  businessLicenseNo: '',
  sourceType: 'PHONE',
  intentLevel: 'C',
  intentAmount: 0,
  status: 'PRIVATE',
  remark: ''
})

// 客户来源选项
const sourceTypeOptions = [
  { text: '电话开发', value: 'PHONE' },
  { text: '转介绍', value: 'REFERRAL' },
  { text: '活动渠道', value: 'ACTIVITY' },
  { text: '其他', value: 'OTHER' }
]

const followForm = reactive({
  followType: 'PHONE',
  followContent: '',
  result: '待跟进',
  validFlag: '1',
  intentLevel: 'C',
  nextFollowTime: ''
})

// 合同表单
const contractForm = reactive({
  customerId: undefined,
  customerName: '',
  contractNo: '',
  productName: '',
  contractAmount: 0,
  disburseAmount: 0,
  firstFeeAmount: 0,
  secondFeeAmount: 0,
  signTime: dayjs().format('YYYY-MM-DD HH:mm:ss'),
  remark: ''
})

const minDate = new Date()
const maxDate = new Date(minDate.getFullYear() + 1, minDate.getMonth(), minDate.getDate())
const selectedDateTime = ref(new Date())

const detailActions = [
  { name: '查看完整档案' },
  { name: '编辑客户信息' }
]

const getStatusType = (status) => {
  if (status === '已放款') return 'success'
  if (status.includes('签约')) return 'primary'
  return 'warning'
}

const buildCustomerLabel = (cust) => {
  const parts = []
  if (cust.phone) {
    parts.push(`电话 ${cust.phone}`)
  }
  if (cust.rawData?.intentLevel) {
    parts.push(`意向${cust.rawData.intentLevel}`)
  }
  if (cust.rawData?.intentAmount) {
    parts.push(`¥${formatAmount(cust.rawData.intentAmount)}`)
  }
  return parts.join(' · ')
}

const buildSeaCustomerLabel = (cust) => {
  const parts = []
  if (cust.phone) {
    parts.push(`电话 ${cust.phone}`)
  }
  if (cust.rawData?.intentLevel) {
    parts.push(`意向${cust.rawData.intentLevel}`)
  }
  if (cust.rawData?.intentAmount) {
    parts.push(`¥${formatAmount(cust.rawData.intentAmount)}`)
  }
  return parts.join(' · ')
}

const getCustomerTypeText = (type) => {
  return type === 'P' ? '个人' : type === 'C' ? '企业' : type
}

const getSourceTypeText = (sourceType) => {
  const option = sourceTypeOptions.find(item => item.value === sourceType)
  return option ? option.text : sourceType
}

const formatAmount = (amount) => {
  if (!amount) return '0'
  return Number(amount).toLocaleString('zh-CN')
}

const formatTime = (time) => {
  if (!time) return '-'
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

// 表单验证失败回调
const onAddCustomerFailed = (errorInfo) => {
  console.log('表单验证失败:', errorInfo)
  showToast(errorInfo.errors[0]?.message || '请完善表单信息')
  submitLoading.value = false
}

const onAddCustomer = async () => {
  submitLoading.value = true

  try {
    let success
    if (isEditMode.value) {
      // 编辑模式
      const res = await updateCustomer({
        customerId: newCustomer.customerId,
        customerName: newCustomer.customerName,
        customerType: newCustomer.customerType,
        phone: newCustomer.phone,
        idCardNo: newCustomer.idCardNo,
        companyName: newCustomer.companyName,
        businessLicenseNo: newCustomer.businessLicenseNo,
        sourceType: newCustomer.sourceType,
        intentLevel: newCustomer.intentLevel,
        intentAmount: Number(newCustomer.intentAmount) || 0,
        status: newCustomer.status,
        remark: newCustomer.remark
      })

      if (res.code === 200) {
        await store.loadMyCustomers()
        showToast({ message: '修改成功', type: 'success' })
        success = true
      } else {
        showToast({ message: res.msg || '修改失败', type: 'fail' })
        success = false
      }
    } else {
      // 新增模式
      success = await store.addCustomer({
        customerName: newCustomer.customerName,
        customerType: newCustomer.customerType,
        phone: newCustomer.phone,
        idCardNo: newCustomer.idCardNo,
        companyName: newCustomer.companyName,
        businessLicenseNo: newCustomer.businessLicenseNo,
        sourceType: newCustomer.sourceType,
        intentLevel: newCustomer.intentLevel,
        intentAmount: Number(newCustomer.intentAmount) || 0,
        status: newCustomer.status,
        remark: newCustomer.remark
      })
    }

    if (success) {
      // 重置表单
      resetCustomerForm()
      showAddPopup.value = false
      isEditMode.value = false
    }
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    submitLoading.value = false
  }
}

const resetCustomerForm = () => {
  Object.assign(newCustomer, {
    customerId: undefined,
    customerName: '',
    customerType: 'P',
    phone: '',
    idCardNo: '',
    companyName: '',
    businessLicenseNo: '',
    sourceType: 'PHONE',
    intentLevel: 'C',
    intentAmount: 0,
    status: 'PRIVATE',
    remark: ''
  })
}

// 重置合同表单
const resetContractForm = () => {
  Object.assign(contractForm, {
    customerId: undefined,
    customerName: '',
    contractNo: generateContractNo(), // 自动生成合同编号
    productName: '',
    contractAmount: 0,
    disburseAmount: 0,
    firstFeeAmount: 0,
    secondFeeAmount: 0,
    signTime: dayjs().format('YYYY-MM-DD HH:mm:ss'),
    remark: ''
  })
}

// 生成合同编号：HT + 年月日 + 4位随机数
const generateContractNo = () => {
  const dateStr = dayjs().format('YYYYMMDD')
  const randomNum = Math.floor(Math.random() * 10000).toString().padStart(4, '0')
  return `HT${dateStr}${randomNum}`
}

const claim = (cust) => store.claimSeaCustomer(cust)

const moveToSea = async (cust) => {
  try {
    await showDialog({
      title: '确认操作',
      message: `确定将 ${cust.name} 转入公海吗？`
    })

    const success = await store.moveToSea(cust.id)
    if (success) {
      showDetailSheet.value = false
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
    }
  }
}

// 打开签署合同弹窗
const showContractDialog = (cust) => {
  selectedCust.value = cust
  resetContractForm()
  contractForm.customerId = cust.id
  contractForm.customerName = cust.name
  // 如果有意向金额，自动填充为合同金额
  if (cust.rawData?.intentAmount) {
    contractForm.contractAmount = cust.rawData.intentAmount
  }
  showContractPopup.value = true
}

// 提交合同
const onSubmitContract = async () => {
  if (!contractForm.productName) {
    showToast('请填写金融产品名称')
    return
  }
  if (!contractForm.contractAmount || contractForm.contractAmount <= 0) {
    showToast('请填写正确的合同金额')
    return
  }

  submitLoading.value = true

  try {
    const res = await addContract({
      contractNo: contractForm.contractNo,
      customerId: contractForm.customerId,
      productName: contractForm.productName,
      contractAmount: Number(contractForm.contractAmount),
      disburseAmount: Number(contractForm.disburseAmount) || 0,
      firstFeeAmount: Number(contractForm.firstFeeAmount) || 0,
      secondFeeAmount: Number(contractForm.secondFeeAmount) || 0,
      signTime: contractForm.signTime,
      remark: contractForm.remark,
      status: 'FINANCE_REVIEW'  // 修改为金融审核中
    })

    if (res.code === 200) {
      showToast({ message: '合同已提交金融审核', type: 'success' })
      showContractPopup.value = false

      // 刷新客户列表
      await store.loadMyCustomers()

      // 更新当天工作日志的签约数
      await updateTodaySignedCount()

      // 更新客户状态为已签约
      const cust = store.myCustomers.find(c => c.id === contractForm.customerId)
      if (cust) {
        cust.hasContract = true
        cust.status = '待签约'
      }
    } else {
      showToast({ message: res.msg || '签署失败', type: 'fail' })
    }
  } catch (error) {
    console.error('签署合同失败:', error)
    showToast({ message: '签署失败', type: 'fail' })
  } finally {
    submitLoading.value = false
  }
}

// 更新当天工作日志的签约数
const updateTodaySignedCount = async () => {
  try {
    const authStore = useAuthStore()
    const userId = authStore.userInfo?.userId
    const deptId = authStore.userInfo?.deptId

    if (!userId) {
      console.warn('未获取到用户ID')
      return
    }

    // 先获取今日日志
    await store.loadTodayWorkLog()

    // 计算新的签约数（当前签约数 + 1）
    const newSignedCount = (store.todayLog.signedCount || 0) + 1

    // 调用保存接口更新签约数
    const { updateWorkLog, addWorkLog } = await import('@/api/worklog')

    const payload = {
      logId: store.todayLog.logId,
      userId: userId,
      deptId: deptId,
      workDate: dayjs().format('YYYY-MM-DD'),
      callCount: store.todayLog.callCount || 0,
      validCallCount: store.todayLog.validCallCount || 0,
      intentCustomerCount: store.todayLog.intentCount || 0,
      visitCount: store.todayLog.meetingCount || 0,
      signedCount: newSignedCount,
      remark: store.todayLog.remark || ''
    }

    let res
    if (store.todayLog.logId) {
      res = await updateWorkLog(payload)
    } else {
      res = await addWorkLog(payload)
    }

    if (res.code === 200) {
      console.log('[工作日志] ✅ 签约数已更新:', newSignedCount)
      // 重新加载日志以同步最新数据
      await store.loadTodayWorkLog()
    } else {
      console.error('[工作日志] ❌ 更新失败:', res.msg)
    }
  } catch (error) {
    console.error('[工作日志] 更新签约数异常:', error)
  }
}

const viewDetail = async (cust) => {
  selectedCust.value = cust

  // 如果 rawData 不存在，获取详细信息
  if (!cust.rawData) {
    try {
      const res = await getCustomer(cust.id)
      if (res.code === 200) {
        selectedCust.value = {
          ...cust,
          rawData: res.data
        }
      }
    } catch (error) {
      console.error('获取客户详情失败:', error)
    }
  }

  showDetailPopup.value = true
}

const viewSeaDetail = async (cust) => {
  selectedCust.value = cust

  // 如果 rawData 不存在，获取详细信息
  if (!cust.rawData) {
    try {
      const res = await getCustomer(cust.id)
      if (res.code === 200) {
        selectedCust.value = {
          ...cust,
          rawData: res.data
        }
      }
    } catch (error) {
      console.error('获取客户详情失败:', error)
    }
  }

  showDetailPopup.value = true
}

const showEditDialog = async (cust) => {
  selectedCust.value = cust
  isEditMode.value = true

  // 获取完整客户信息
  try {
    const res = await getCustomer(cust.id)
    if (res.code === 200) {
      const data = res.data
      Object.assign(newCustomer, {
        customerId: data.customerId,
        customerName: data.customerName,
        customerType: data.customerType || 'P',
        phone: data.phone,
        idCardNo: data.idCardNo || '',
        companyName: data.companyName || '',
        businessLicenseNo: data.businessLicenseNo || '',
        sourceType: data.sourceType || 'PHONE',
        intentLevel: data.intentLevel || 'C',
        intentAmount: data.intentAmount || 0,
        status: data.status || 'PRIVATE',
        remark: data.remark || ''
      })
      showAddPopup.value = true
    }
  } catch (error) {
    console.error('获取客户信息失败:', error)
    showToast('获取客户信息失败')
  }
}

const showFollowDialog = (cust) => {
  selectedCust.value = cust
  followForm.followType = 'PHONE'
  followForm.followContent = ''
  followForm.result = '待跟进'
  followForm.validFlag = '1'
  followForm.intentLevel = cust.rawData?.intentLevel || 'C'
  followForm.nextFollowTime = ''
  showFollowPopup.value = true
}

const selectNextFollowTime = () => {
  selectedDateTime.value = new Date()
  showDateTimePicker.value = true
}

const confirmDateTime = () => {
  if (!selectedDateTime.value) {
    showToast('请选择日期时间')
    return
  }
  followForm.nextFollowTime = dayjs(selectedDateTime.value).format('YYYY-MM-DD HH:mm:ss')
  showDateTimePicker.value = false
}

const onDateTimeConfirm = (value) => {
  selectedDateTime.value = value
}

// 选择签约时间
const selectSignTime = () => {
  selectedDateTime.value = new Date()
  showSignTimePicker.value = true
}

const confirmSignTime = () => {
  if (!selectedDateTime.value) {
    showToast('请选择签约时间')
    return
  }
  contractForm.signTime = dayjs(selectedDateTime.value).format('YYYY-MM-DD HH:mm:ss')
  showSignTimePicker.value = false
}

const onSignTimeConfirm = (value) => {
  selectedDateTime.value = value
}

const onSubmitFollow = async () => {
  if (!followForm.followContent) {
    showToast('请输入跟进内容')
    return
  }

  const success = await store.addFollowRecord(selectedCust.value.id, {
    followType: followForm.followType,
    content: followForm.followContent,
    result: followForm.result,
    validFlag: followForm.validFlag,
    intentLevel: followForm.intentLevel,
    nextFollowTime: followForm.nextFollowTime || null
  })

  if (success) {
    showFollowPopup.value = false
  }
}

const onDetailSelect = (item) => {
  if (item.name === '查看完整档案') {
    viewDetail(selectedCust.value)
  } else if (item.name === '编辑客户信息') {
    showEditDialog(selectedCust.value)
  }
  showDetailSheet.value = false
}

const onSeaRefresh = async () => {
  await store.loadSeaCustomers()
  hasLoadedSea.value = true
  seaRefreshing.value = false
  showToast('刷新成功')
}

const onRefresh = async () => {
  refreshing.value = true
  await Promise.all([
    store.loadMyCustomers(),
    store.loadSeaCustomers()
  ])
  hasLoadedMy.value = true
  hasLoadedSea.value = true
  refreshing.value = false
  showToast('刷新成功')
}

const onTabChange = async (name) => {
  if (name === 0 && !hasLoadedMy.value) {
    await store.loadMyCustomers()
    hasLoadedMy.value = true
  } else if (name === 1 && !hasLoadedSea.value) {
    await store.loadSeaCustomers()
    hasLoadedSea.value = true
  }
}

onMounted(async () => {
  // 默认加载第一个Tab的数据
  await store.loadMyCustomers()
  hasLoadedMy.value = true
})
</script>

<style scoped>
.tab-content {
  padding: 12px;
}
.add-btn {
  margin-bottom: 16px;
}
.customer-card {
  margin-bottom: 8px;
}
.card-actions {
  padding: 0 16px 8px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.empty-tip {
  text-align: center;
  color: #969799;
  padding: 40px 0;
  font-size: 14px;
}
.loading-tip {
  text-align: center;
  padding: 20px 0;
}
.popup-content {
  padding: 20px 16px;
}
.popup-btn {
  margin: 24px 16px;
}
.remark-label {
  white-space: normal;
  word-break: break-all;
}
.cell-icon {
  margin-right: 8px;
  color: #1989fa;
  font-size: 18px;
}
</style>
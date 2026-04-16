<template>
  <div>
    <van-nav-bar title="客户管理" fixed placeholder />
    <van-tabs v-model:active="activeTab" sticky offset-top="46">
      <!-- 我的客户 -->
      <van-tab title="我的客户">
        <div class="tab-content">
          <van-button type="primary" block round icon="plus" @click="showAddPopup = true" class="add-btn">新建客户</van-button>
          <van-cell-group inset v-for="cust in store.myCustomers" :key="cust.id" class="customer-card">
            <van-cell :title="cust.name" :label="`📱 ${cust.phone} · ${cust.intent}`" is-link @click="viewDetail(cust)">
              <template #value>
                <van-tag :type="getStatusType(cust.status)" size="small">{{ cust.status }}</van-tag>
              </template>
            </van-cell>
            <div class="card-actions">
              <van-button size="small" plain @click="store.signContract(cust.id)" v-if="!cust.hasContract">📄 签署合同</van-button>
              <van-button size="small" plain type="default" @click="addFollowRecord(cust)">📝 洽谈记录</van-button>
            </div>
          </van-cell-group>
          <div v-if="store.myCustomers.length===0" class="empty-tip">暂无客户，去公海看看吧</div>
        </div>
      </van-tab>

      <!-- 公海客户 -->
      <van-tab title="公海客户">
        <div class="tab-content">
          <van-notice-bar left-icon="info-o" text="公海客户超过7天未签约自动释放，可领取跟进" />
          <van-cell-group inset style="margin-top:12px;">
            <van-cell v-for="sea in store.seaCustomers" :key="sea.id" :title="sea.name" :label="`📱 ${sea.phone} · 意向${sea.intent}`">
              <template #value>
                <van-tag type="danger" size="small">释放{{ sea.releaseDays }}天</van-tag>
              </template>
              <template #right-icon>
                <van-button size="small" type="primary" @click="claim(sea)">领取</van-button>
              </template>
            </van-cell>
          </van-cell-group>
          <div v-if="store.seaCustomers.length===0" class="empty-tip">公海暂无客户</div>
        </div>
      </van-tab>
    </van-tabs>

    <!-- 添加客户弹窗 -->
    <van-popup v-model:show="showAddPopup" position="bottom" round :style="{ height: '65%' }">
      <div class="popup-content">
        <h3>新建客户</h3>
        <van-form @submit="onAddCustomer">
          <van-field v-model="newCustomer.name" label="姓名" placeholder="客户姓名" required />
          <van-field v-model="newCustomer.phone" label="手机号" placeholder="自动去重校验" required />
          <van-field v-model="newCustomer.idCard" label="身份证" placeholder="选填" />
          <van-field v-model="newCustomer.intent" label="贷款意向" placeholder="如: 经营贷" />
          <van-field v-model="newCustomer.amount" type="digit" label="意向金额(万)" />
          <div class="popup-btn">
            <van-button round block type="primary" native-type="submit">确认添加</van-button>
          </div>
        </van-form>
      </div>
    </van-popup>

    <!-- 客户详情/操作面板 -->
    <van-action-sheet v-model:show="showDetailSheet" :title="selectedCust?.name" :actions="detailActions" @select="onDetailSelect" />
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useSalesStore } from '../stores/sales'
import { showDialog, showToast } from 'vant'

const store = useSalesStore()
const activeTab = ref(0)
const showAddPopup = ref(false)
const showDetailSheet = ref(false)
const selectedCust = ref(null)

const newCustomer = reactive({
  name: '',
  phone: '',
  idCard: '',
  intent: '',
  amount: ''
})

const detailActions = [
  { name: '添加洽谈记录' },
  { name: '查看完整档案' }
]

const getStatusType = (status) => {
  if (status === '已放款') return 'success'
  if (status.includes('签约')) return 'primary'
  return 'warning'
}

const onAddCustomer = () => {
  if (!newCustomer.name || !newCustomer.phone) {
    showToast('请填写姓名和手机号')
    return
  }
  store.addCustomer({
    name: newCustomer.name,
    phone: newCustomer.phone,
    idCard: newCustomer.idCard,
    intent: newCustomer.intent || '未填写',
    amount: Number(newCustomer.amount) || 0
  })
  Object.assign(newCustomer, { name: '', phone: '', idCard: '', intent: '', amount: '' })
  showAddPopup.value = false
}

const claim = (cust) => store.claimSeaCustomer(cust)

const viewDetail = (cust) => {
  selectedCust.value = cust
  showDetailSheet.value = true
}

const addFollowRecord = (cust) => {
  showDialog({
    title: '添加洽谈记录',
    message: `与 ${cust.name} 的跟进内容`
  }).then(() => {
    showToast('记录已保存 (演示)')
  })
}

const onDetailSelect = (item) => {
  showToast(item.name + ' (演示交互)')
  showDetailSheet.value = false
}
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
}
.empty-tip {
  text-align: center;
  color: #969799;
  padding: 40px 0;
  font-size: 14px;
}
.popup-content {
  padding: 20px 16px;
}
.popup-btn {
  margin: 24px 16px;
}
</style>
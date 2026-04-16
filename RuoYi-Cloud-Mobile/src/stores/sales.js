import { defineStore } from 'pinia'
import { useAuthStore } from './auth'
import { showToast } from 'vant'
import dayjs from 'dayjs'
import { 
  listCustomer, 
  addCustomer as addCustomerApi, 
  claimSeaCustomer as claimApi,
  transferToSea,
  addFollow as addFollowApi
} from '@/api/customer'

export const useSalesStore = defineStore('sales', {
  state: () => {
    return {
      myCustomers: [],
      seaCustomers: [],
      todayLog: {
        callCount: 0,
        validCallCount: 0,
        intentCount: 0,
        meetingCount: 0,
        date: dayjs().format('YYYY-MM-DD')
      },
      performance: {
        totalCustomers: 0,
        signedAmount: 0,
        loanAmount: 0,
        rankInDept: 0
      },
      loading: false
    }
  },

  getters: {
    currentUser: () => {
      const authStore = useAuthStore()
      
      if (authStore.userInfo && Object.keys(authStore.userInfo).length > 0) {
        return {
          id: authStore.userInfo.userId || authStore.userInfo.userName || 'unknown',
          name: authStore.userInfo.nickName || authStore.userInfo.userName || '销售代表',
          role: authStore.roles?.[0] || '销售代表',
          department: authStore.userInfo.dept?.deptName || '销售部门',
          avatar: authStore.userInfo.avatar ? 
            (authStore.userInfo.avatar.startsWith('http') ? 
              authStore.userInfo.avatar : 
              `https://your-domain.com${authStore.userInfo.avatar}`) : 
            'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'
        }
      }
      
      return {
        id: 'S10086',
        name: '张明',
        role: '销售代表',
        department: '华东战区一部',
        avatar: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'
      }
    },

    pendingCustomers: (state) => {
      return state.myCustomers.filter(c => c.status !== '已放款')
    },

    logProgress: (state) => {
      const target = { call: 50, valid: 25, intent: 8, meeting: 4 }
      return {
        call: Math.min((state.todayLog.callCount / target.call) * 100, 100),
        valid: Math.min((state.todayLog.validCallCount / target.valid) * 100, 100),
        intent: Math.min((state.todayLog.intentCount / target.intent) * 100, 100),
        meeting: Math.min((state.todayLog.meetingCount / target.meeting) * 100, 100)
      }
    }
  },

  actions: {
    /**
     * 加载我的客户列表
     */
    async loadMyCustomers() {
      try {
        this.loading = true
        const authStore = useAuthStore()
        const userId = authStore.userInfo?.userId
        
        if (!userId) {
          console.warn('未获取到用户ID')
          return
        }

        const res = await listCustomer({
          userId: userId,
          pageNum: 1,
          pageSize: 100
        })

        this.myCustomers = (res.rows || []).map(item => ({
          id: item.customerId,
          name: item.customerName,
          phone: item.phone,
          idCard: item.idCard || '',
          intent: item.intentLevel || '未填写',
          amount: item.loanAmount ? item.loanAmount / 10000 : 0,
          status: this.mapStatus(item.status),
          lastContact: item.lastFollowTime || item.createTime,
          followCount: item.followCount || 0,
          hasContract: item.hasContract === '1' || item.status === 'CONTRACT_SIGNED',
          rawData: item
        }))

        this.performance.totalCustomers = this.myCustomers.length
      } catch (error) {
        console.error('加载客户列表失败:', error)
        showToast('加载客户列表失败')
      } finally {
        this.loading = false
      }
    },

    /**
     * 加载公海客户列表
     */
    async loadSeaCustomers() {
      try {
        const res = await listCustomer({
          pageNum: 1,
          pageSize: 50
        })

        this.seaCustomers = (res.rows || [])
          .filter(item => item.status === 'PUBLIC_SEA')
          .map(item => ({
            id: item.customerId,
            name: item.customerName,
            phone: item.phone,
            company: item.companyName || '',
            intent: item.intentLevel || '未填写',
            releaseDays: this.calculateReleaseDays(item.createTime),
            amount: item.loanAmount ? item.loanAmount / 10000 : 0,
            rawData: item
          }))
      } catch (error) {
        console.error('加载公海客户失败:', error)
      }
    },

    /**
     * 添加新客户
     */
    async addCustomer(customer) {
      try {
        const authStore = useAuthStore()
        const userId = authStore.userInfo?.userId

        const res = await addCustomerApi({
          customerName: customer.customerName,
          customerType: customer.customerType || 'P',
          phone: customer.phone,
          idCardNo: customer.idCardNo,
          companyName: customer.companyName,
          businessLicenseNo: customer.businessLicenseNo,
          sourceType: customer.sourceType || 'PHONE',
          intentLevel: customer.intentLevel || 'C',
          intentAmount: customer.intentAmount || 0,
          status: customer.status || 'PRIVATE',
          remark: customer.remark,
          userId: userId
        })

        if (res.code === 200) {
          await this.loadMyCustomers()
          showToast({ message: '客户添加成功', type: 'success' })
          return true
        } else {
          showToast({ message: res.msg || '添加失败', type: 'fail' })
          return false
        }
      } catch (error) {
        console.error('添加客户失败:', error)
        showToast({ message: error.message || '添加失败', type: 'fail' })
        return false
      }
    },

    /**
     * 领取公海客户
     */
    async claimSeaCustomer(cust) {
      try {
        const res = await claimApi(cust.id)
        
        if (res.code === 200) {
          await Promise.all([
            this.loadMyCustomers(),
            this.loadSeaCustomers()
          ])
          showToast({
            message: `已领取 ${cust.name}，请及时跟进`,
            type: 'success'
          })
        } else {
          showToast({ message: res.msg || '领取失败', type: 'fail' })
        }
      } catch (error) {
        console.error('领取客户失败:', error)
        showToast({ message: '领取失败', type: 'fail' })
      }
    },

    /**
     * 将客户转入公海
     */
    async moveToSea(customerId) {
      try {
        const res = await transferToSea(customerId)
        
        if (res.code === 200) {
          await Promise.all([
            this.loadMyCustomers(),
            this.loadSeaCustomers()
          ])
          showToast({ message: '已转入公海', type: 'success' })
          return true
        } else {
          showToast({ message: res.msg || '操作失败', type: 'fail' })
          return false
        }
      } catch (error) {
        console.error('转入公海失败:', error)
        showToast({ message: '操作失败', type: 'fail' })
        return false
      }
    },

    /**
     * 添加跟进记录
     */
    async addFollowRecord(customerId, followData) {
      try {
        const res = await addFollowApi({
          customerId: customerId,
          followType: followData.followType || 'PHONE',
          followContent: followData.content,
          followResult: followData.result,
          validFlag: followData.validFlag || '1',
          intentLevel: followData.intentLevel,
          nextFollowTime: followData.nextFollowTime
        })

        if (res.code === 200) {
          await this.loadMyCustomers()
          showToast({ message: '跟进记录已保存', type: 'success' })
          return true
        } else {
          showToast({ message: res.msg || '保存失败', type: 'fail' })
          return false
        }
      } catch (error) {
        console.error('添加跟进记录失败:', error)
        showToast({ message: '保存失败', type: 'fail' })
        return false
      }
    },

    /**
     * 更新今日工作日志
     */
    updateTodayLog(payload) {
      this.todayLog = {
        ...this.todayLog,
        ...payload,
        date: dayjs().format('YYYY-MM-DD')
      }
      showToast({ message: '日志已保存', type: 'success' })
    },

    /**
     * 模拟签署合同
     */
    signContract(customerId) {
      const cust = this.myCustomers.find(c => c.id === customerId)
      if (cust) {
        cust.hasContract = true
        cust.status = '已签约'
        this.performance.signedAmount += cust.amount * 10000
        showToast({
          message: `已为 ${cust.name} 生成合同草稿`,
          type: 'success'
        })
      } else {
        showToast({ message: '客户不存在', type: 'fail' })
      }
    },

    /**
     * 刷新业绩数据
     */
    refreshPerformance() {
      const signed = this.myCustomers
        .filter(c => c.hasContract)
        .reduce((sum, c) => sum + c.amount * 10000, 0)

      const loaned = this.myCustomers
        .filter(c => c.status === '已放款')
        .reduce((sum, c) => sum + c.amount * 10000, 0)

      this.performance.signedAmount = signed
      this.performance.loanAmount = loaned
      this.performance.totalCustomers = this.myCustomers.length
    },

    /**
     * 重置今日日志
     */
    resetTodayLog() {
      this.todayLog = {
        callCount: 0,
        validCallCount: 0,
        intentCount: 0,
        meetingCount: 0,
        date: dayjs().format('YYYY-MM-DD')
      }
    },

    /**
     * 映射后端状态到前端显示
     */
    mapStatus(status) {
      const statusMap = {
        'PRIVATE': '新客',
        'FOLLOWING': '洽谈中',
        'CONTRACT_SIGNED': '待签约',
        'LOANED': '已放款',
        'PUBLIC_SEA': '公海'
      }
      return statusMap[status] || status
    },

    /**
     * 计算释放天数
     */
    calculateReleaseDays(createTime) {
      if (!createTime) return 0
      const days = dayjs().diff(dayjs(createTime), 'day')
      return Math.max(0, 7 - days)
    }
  }
})
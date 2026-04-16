import { defineStore } from 'pinia'
import { useAuthStore } from './auth'
import { showToast } from 'vant'
import dayjs from 'dayjs'

export const useSalesStore = defineStore('sales', {
  state: () => {
    const authStore = useAuthStore()

    /**
     * 根据登录用户构建 currentUser 对象
     */
    const buildCurrentUser = () => {
      if (authStore.userInfo) {
        return {
          id: authStore.userInfo.userId || 'S10086',
          name: authStore.userInfo.nickName || authStore.userInfo.userName || '张明',
          role: authStore.roles?.[0] || '销售代表',
          department: authStore.userInfo.dept?.deptName || '华东战区一部',
          avatar: authStore.userInfo.avatar || 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'
        }
      }
      // 默认值（未登录或登录信息尚未加载时使用）
      return {
        id: 'S10086',
        name: '张明',
        role: '销售代表',
        department: '华东战区一部',
        avatar: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'
      }
    }

    return {
      // 当前用户信息（优先从 authStore 获取真实用户）
      currentUser: buildCurrentUser(),

      // 我的客户列表 (mock)
      myCustomers: [
        {
          id: 1,
          name: '陈建国',
          phone: '138****2211',
          idCard: '410***************',
          intent: '经营贷',
          amount: 50,
          status: '洽谈中',
          lastContact: '2026-04-15',
          followCount: 3,
          hasContract: false
        },
        {
          id: 2,
          name: '李雅',
          phone: '159****3344',
          idCard: '320***************',
          intent: '消费贷',
          amount: 20,
          status: '待签约',
          lastContact: '2026-04-14',
          followCount: 5,
          hasContract: true
        },
        {
          id: 3,
          name: '王德发',
          phone: '177****5566',
          idCard: '440***************',
          intent: '经营贷',
          amount: 80,
          status: '已放款',
          lastContact: '2026-04-10',
          followCount: 8,
          hasContract: true
        }
      ],

      // 公海客户 (可领取)
      seaCustomers: [
        {
          id: 101,
          name: '赵一鸣',
          phone: '136****7788',
          company: '宏达贸易',
          intent: '周转贷',
          releaseDays: 5,
          amount: 30
        },
        {
          id: 102,
          name: '周晓鸥',
          phone: '152****9900',
          company: '晓鸥餐饮',
          intent: '设备贷',
          releaseDays: 8,
          amount: 45
        }
      ],

      // 今日工作日志指标
      todayLog: {
        callCount: 24,
        validCallCount: 12,
        intentCount: 5,
        meetingCount: 2,
        date: dayjs().format('YYYY-MM-DD')
      },

      // 个人业绩摘要
      performance: {
        totalCustomers: 18,
        signedAmount: 3200000,  // 签约金额(元)
        loanAmount: 1500000,    // 放款金额
        rankInDept: 3
      }
    }
  },

  getters: {
    /**
     * 待跟进客户（状态非已放款）
     */
    pendingCustomers: (state) => {
      return state.myCustomers.filter(c => c.status !== '已放款')
    },

    /**
     * 今日工作完成度（与目标值比较）
     */
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
     * 添加新客户（自动去重校验）
     * @param {Object} customer - 客户信息
     * @returns {boolean} 是否添加成功
     */
    addCustomer(customer) {
      const exists = this.myCustomers.find(c => c.phone === customer.phone)
      if (exists) {
        showToast({
          message: '该手机号已存在于您的客户库，请勿重复添加',
          type: 'fail'
        })
        return false
      }

      const newId = Math.max(...this.myCustomers.map(c => c.id), 0) + 1
      this.myCustomers.push({
        ...customer,
        id: newId,
        status: '新客',
        lastContact: dayjs().format('YYYY-MM-DD'),
        followCount: 0,
        hasContract: false
      })

      // 更新业绩中的客户总数
      this.performance.totalCustomers = this.myCustomers.length

      showToast({ message: '客户添加成功', type: 'success' })
      return true
    },

    /**
     * 领取公海客户
     * @param {Object} cust - 公海客户对象
     */
    claimSeaCustomer(cust) {
      const exists = this.myCustomers.find(c => c.phone === cust.phone)
      if (exists) {
        showToast({ message: '该客户已在您的库中', type: 'fail' })
        return
      }

      // 从公海移除
      this.seaCustomers = this.seaCustomers.filter(c => c.id !== cust.id)

      // 加入我的客户列表
      this.myCustomers.push({
        id: cust.id,
        name: cust.name,
        phone: cust.phone,
        intent: cust.intent,
        amount: cust.amount,
        status: '公海领取',
        lastContact: dayjs().format('YYYY-MM-DD'),
        followCount: 0,
        hasContract: false,
        company: cust.company
      })

      // 更新业绩中的客户总数
      this.performance.totalCustomers = this.myCustomers.length

      showToast({
        message: `已领取 ${cust.name}，请及时跟进`,
        type: 'success'
      })
    },

    /**
     * 更新今日工作日志
     * @param {Object} payload - 日志字段对象
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
     * 模拟签署合同（生成合同草稿）
     * @param {number} customerId - 客户ID
     */
    signContract(customerId) {
      const cust = this.myCustomers.find(c => c.id === customerId)
      if (cust) {
        cust.hasContract = true
        cust.status = '已签约'

        // 模拟更新签约金额
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
     * 添加跟进洽谈记录
     * @param {number} customerId - 客户ID
     * @param {string} content - 洽谈内容
     * @param {string} result - 洽谈结果
     */
    addFollowRecord(customerId, content, result) {
      const cust = this.myCustomers.find(c => c.id === customerId)
      if (cust) {
        cust.followCount += 1
        cust.lastContact = dayjs().format('YYYY-MM-DD')

        // 根据结果更新状态
        if (result === '有意向') {
          cust.status = '洽谈中'
        } else if (result === '待签约') {
          cust.status = '待签约'
        }

        // 实际项目中此处应调用后端接口保存记录
        showToast({
          message: `已记录与 ${cust.name} 的洽谈`,
          type: 'success'
        })
      }
    },

    /**
     * 刷新业绩数据（用于与后端同步）
     */
    refreshPerformance() {
      // 模拟计算业绩
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
     * 重置今日日志（每日零点自动调用）
     */
    resetTodayLog() {
      this.todayLog = {
        callCount: 0,
        validCallCount: 0,
        intentCount: 0,
        meetingCount: 0,
        date: dayjs().format('YYYY-MM-DD')
      }
    }
  }
})
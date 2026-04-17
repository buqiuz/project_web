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
import { 
  getTodayWorkLog, 
  listWorkLog,
  addWorkLog, 
  updateWorkLog 
} from '@/api/worklog'
import {
  getPerformanceOverview,
  listPersonalPerformance,
  listPerformanceRank
} from '@/api/performance'

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
        signedCount: 0,
        date: dayjs().format('YYYY-MM-DD')
      },
      performance: {
        totalCustomers: 0,
        signedAmount: 0,
        loanAmount: 0,
        rankInDept: 0,
        contractCount: 0,
        loanedCount: 0,
        feeIncome: 0,
        commissionAmount: 0
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
     * 加载我的客户列表（仅PRIVATE状态）
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
          status: 'PRIVATE',
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
     * 加载公海客户列表（所有用户的SEA状态客户）
     */
    async loadSeaCustomers() {
      try {
        this.loading = true
        const res = await listCustomer({
          status: 'SEA',
          pageNum: 1,
          pageSize: 100
        })

        this.seaCustomers = (res.rows || []).map(item => ({
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
        showToast('加载公海客户失败')
      } finally {
        this.loading = false
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
     * 加载今日工作日志
     */
    async loadTodayWorkLog() {
      try {
        const today = dayjs().format('YYYY-MM-DD')
        const res = await getTodayWorkLog()
        
        // 情况1：/mine/today 接口正常返回了 data 且有内容
        if (res.code === 200 && res.data && Object.keys(res.data).length > 0) {
          const log = res.data
          
          this.todayLog = {
            callCount: log.callCount || 0,
            validCallCount: log.validCallCount || 0,
            intentCount: log.intentCustomerCount || 0,
            meetingCount: log.visitCount || 0,
            signedCount: log.signedCount || 0,
            date: today,
            logId: log.logId
          }
          
          console.log('[工作日志] ✅ 从接口获取成功')
          return
        }
        
        // 情况2：/mine/today 接口返回空，尝试通过列表查询获取
        const authStore = useAuthStore()
        const userId = authStore.userInfo?.userId
        
        if (userId) {
          const listRes = await listWorkLog({
            pageNum: 1,
            pageSize: 5
          })
          
          if (listRes.code === 200 && listRes.rows && listRes.rows.length > 0) {
            const sortedLogs = listRes.rows.sort((a, b) => {
              return new Date(b.createTime) - new Date(a.createTime)
            })
            
            const latestLog = sortedLogs[0]
            const createToday = dayjs(latestLog.createTime).isSame(dayjs(), 'day')
            
            if (createToday && latestLog.userId === userId) {
              this.todayLog = {
                callCount: latestLog.callCount || 0,
                validCallCount: latestLog.validCallCount || 0,
                intentCount: latestLog.intentCustomerCount || 0,
                meetingCount: latestLog.visitCount || 0,
                signedCount: latestLog.signedCount || 0,
                date: today,
                logId: latestLog.logId
              }
              
              console.log('[工作日志] ✅ 从列表恢复成功')
              return
            }
          }
        }
        
        // 情况3：确实没有今日日志
        console.log('[工作日志] ℹ️ 今日暂无日志')
        this.resetTodayLog()
        
      } catch (error) {
        console.error('[工作日志] ❌ 加载失败:', error.message)
        
        // 失败时也尝试用列表查询兜底
        try {
          const authStore = useAuthStore()
          const userId = authStore.userInfo?.userId
          const today = dayjs().format('YYYY-MM-DD')
          
          if (userId) {
            const listRes = await listWorkLog({
              pageNum: 1,
              pageSize: 1
            })
            
            if (listRes.code === 200 && listRes.rows && listRes.rows.length > 0) {
              const log = listRes.rows[0]
              const createToday = dayjs(log.createTime).isSame(dayjs(), 'day')
              
              if (createToday && log.userId === userId) {
                this.todayLog = {
                  callCount: log.callCount || 0,
                  validCallCount: log.validCallCount || 0,
                  intentCount: log.intentCustomerCount || 0,
                  meetingCount: log.visitCount || 0,
                  signedCount: log.signedCount || 0,
                  date: today,
                  logId: log.logId
                }
                console.log('[工作日志] ⚠️ 异常恢复成功')
                return
              }
            }
          }
        } catch (e) {
          console.error('[工作日志] 兜底查询也失败')
        }
        
        this.resetTodayLog()
      }
    },

    /**
     * 保存工作日志（新增或更新）
     */
    async saveWorkLog(logData) {
      try {
        const authStore = useAuthStore()
        const userId = authStore.userInfo?.userId
        const deptId = authStore.userInfo?.deptId

        if (!userId) {
          showToast({ message: '未获取到用户信息，请重新登录', type: 'fail' })
          return false
        }

        const payload = {
          logId: this.todayLog.logId,
          userId: userId,
          deptId: deptId,
          workDate: dayjs().format('YYYY-MM-DD'),
          callCount: Math.max(0, parseInt(logData.callCount) || 0),
          validCallCount: Math.max(0, parseInt(logData.validCallCount) || 0),
          intentCustomerCount: Math.max(0, parseInt(logData.intentCount) || 0),
          visitCount: Math.max(0, parseInt(logData.meetingCount) || 0),
          signedCount: this.todayLog.signedCount || 0,
          remark: logData.remark || ''
        }

        let res
        if (this.todayLog.logId) {
          res = await updateWorkLog(payload)
        } else {
          res = await addWorkLog(payload)
        }

        if (res.code === 200) {
          await this.loadTodayWorkLog()
          showToast({ message: '日志已保存', type: 'success' })
          return true
        } else {
          showToast({ message: res.msg || '保存失败', type: 'fail' })
          return false
        }
      } catch (error) {
        console.error('[保存日志] ❌ 失败:', error.message)
        showToast({ message: error.message || '保存失败', type: 'fail' })
        return false
      }
    },

    /**
     * 更新今日工作日志（本地更新，已废弃，改用 saveWorkLog）
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
     * 刷新业绩数据（从后端接口获取真实数据）
     */
    async refreshPerformance() {
      try {
        const authStore = useAuthStore()
        const userId = authStore.userInfo?.userId
        
        if (!userId) {
          console.warn('[业绩数据] 未获取到用户ID')
          return
        }

        // 构建查询参数：本月1号到今天
        const beginDate = dayjs().startOf('month').format('YYYY-MM-DD')
        const endDate = dayjs().format('YYYY-MM-DD')
        
        const queryParams = {
          userId: userId,
          beginDate: beginDate,
          endDate: endDate
        }

        console.log('[业绩数据] 开始加载，参数:', queryParams)

        // 并行请求个人业绩和排名数据
        const [personalRes, rankRes] = await Promise.all([
          listPersonalPerformance(queryParams),
          listPerformanceRank({
            beginDate: beginDate,
            endDate: endDate,
            deptId: authStore.userInfo?.deptId,
            topN: 50
          })
        ])

        console.log('[业绩数据] 个人业绩响应:', personalRes)
        console.log('[业绩数据] 排名响应:', rankRes)

        // 处理个人业绩数据
        if (personalRes.code === 200 && personalRes.data && personalRes.data.length > 0) {
          const perf = personalRes.data[0]  // 取第一条（当前用户）
          
          this.performance = {
            totalCustomers: this.myCustomers.length,
            signedAmount: perf.contractAmount || 0,           // 签约金额
            loanAmount: perf.loanedAmount || 0,               // 放款金额
            rankInDept: 0,                                     // 稍后从排名中查找
            contractCount: perf.contractCount || 0,           // 签约合同数
            loanedCount: perf.loanedCount || 0,               // 放款合同数
            feeIncome: perf.feeIncome || 0,                   // 服务费收入
            commissionAmount: perf.commissionAmount || 0      // 提成金额
          }
          
          console.log('[业绩数据] ✅ 个人业绩加载成功')
        } else {
          console.log('[业绩数据] ℹ️ 本月暂无业绩数据')
          // 保持默认值
          this.performance.totalCustomers = this.myCustomers.length
        }

        // 处理排名数据
        if (rankRes.code === 200 && rankRes.data && rankRes.data.length > 0) {
          const userRank = rankRes.data.find(item => item.userId === userId)
          if (userRank) {
            this.performance.rankInDept = userRank.rankNum || 0
            console.log('[业绩数据] ✅ 部门排名:', this.performance.rankInDept)
          }
        }

        console.log('[业绩数据] 最终业绩数据:', JSON.stringify(this.performance, null, 2))
        
      } catch (error) {
        console.error('[业绩数据] ❌ 加载失败:', error.message)
        // 失败时使用本地计算作为兜底
        this._calculatePerformanceLocally()
      }
    },

    /**
     * 本地计算业绩（兜底方案）
     */
    _calculatePerformanceLocally() {
      const signed = this.myCustomers
        .filter(c => c.hasContract)
        .reduce((sum, c) => sum + c.amount * 10000, 0)

      const loaned = this.myCustomers
        .filter(c => c.status === '已放款')
        .reduce((sum, c) => sum + c.amount * 10000, 0)

      this.performance.signedAmount = signed
      this.performance.loanAmount = loaned
      this.performance.totalCustomers = this.myCustomers.length
      
      console.log('[业绩数据] ⚠️ 使用本地计算兜底')
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
        signedCount: 0,
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
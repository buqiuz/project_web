<template>
  <div class="page-container">
    <van-row align="center" class="profile-header">
      <van-image round width="60" height="60" :src="store.currentUser.avatar" />
      <div class="user-info">
        <h3>{{ store.currentUser.name }}</h3>
        <van-tag type="primary">{{ store.currentUser.department }}</van-tag>
      </div>
    </van-row>

    <van-cell-group inset title="个人信息">
      <van-cell title="手机号" :value="authStore.userInfo?.phonenumber || '-'">
        <template #icon>
          <van-icon name="phone-o" class="cell-icon" />
        </template>
      </van-cell>
      <van-cell title="邮箱" :value="authStore.userInfo?.email || '-'">
        <template #icon>
          <van-icon name="envelop-o" class="cell-icon" />
        </template>
      </van-cell>
      <van-cell title="所属部门" :value="authStore.userInfo?.dept?.deptName || '-'">
        <template #icon>
          <van-icon name="friends-o" class="cell-icon" />
        </template>
      </van-cell>
      <van-cell title="所属角色" :value="authStore.roles?.join(', ') || '-'">
        <template #icon>
          <van-icon name="user-circle-o" class="cell-icon" />
        </template>
      </van-cell>
      <van-cell title="创建日期" :value="formatDate(authStore.userInfo?.createTime)">
        <template #icon>
          <van-icon name="clock-o" class="cell-icon" />
        </template>
      </van-cell>
    </van-cell-group>

    <van-cell-group inset style="margin-top:16px;">
      <van-cell title="修改密码" is-link @click="showPasswordDialog">
        <template #icon>
          <van-icon name="edit" class="cell-icon" />
        </template>
      </van-cell>
      <van-cell title="我的合同" is-link @click="goToContracts">
        <template #icon>
          <van-icon name="notes-o" class="cell-icon" />
        </template>
      </van-cell>
    </van-cell-group>

    <div class="logout-btn">
      <van-button block round type="default" @click="logout">
        <van-icon name="sign-out" style="margin-right: 4px;" />
        退出登录
      </van-button>
    </div>

    <!-- 修改密码弹窗 -->
    <van-popup v-model:show="showPwdPopup" position="bottom" round :style="{ height: '70%' }">
      <div class="popup-content">
        <h3>修改密码</h3>
        <van-form ref="pwdFormRef" @submit="submitPassword">
          <van-field
            v-model="passwordForm.oldPassword"
            type="password"
            label="旧密码"
            placeholder="请输入旧密码"
            :rules="passwordRules.oldPassword"
          >
            <template #left-icon>
              <van-icon name="lock" />
            </template>
          </van-field>

          <van-field
            v-model="passwordForm.newPassword"
            type="password"
            label="新密码"
            placeholder="请输入新密码（6-20位）"
            :rules="passwordRules.newPassword"
          >
            <template #left-icon>
              <van-icon name="newspaper-o" />
            </template>
          </van-field>

          <van-field
            v-model="passwordForm.confirmPassword"
            type="password"
            label="确认密码"
            placeholder="请再次输入新密码"
            :rules="passwordRules.confirmPassword"
          >
            <template #left-icon>
              <van-icon name="passed" />
            </template>
          </van-field>

          <div class="popup-btn">
            <van-button round block type="primary" native-type="submit" :loading="submitting">保存</van-button>
          </div>
        </van-form>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useSalesStore } from '../stores/sales'
import { useAuthStore } from '../stores/auth'
import { showDialog, showToast } from 'vant'
import dayjs from 'dayjs'
import request from '@/utils/request'

const router = useRouter()
const store = useSalesStore()
const authStore = useAuthStore()
const showPwdPopup = ref(false)
const submitting = ref(false)
const pwdFormRef = ref(null)

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码验证规则
const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码' },
    {
      pattern: /^.{6,20}$/,
      message: '密码长度为6-20位'
    },
    {
      pattern: /^[^<>"'|\\]+$/,
      message: '不能包含非法字符(< > " \' \\ |)'
    }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码' },
    {
      validator: (value) => {
        if (value !== passwordForm.newPassword) {
          return '两次输入的密码不一致'
        }
        return true
      },
      trigger: 'blur'
    }
  ]
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return '-'
  return dayjs(date).format('YYYY-MM-DD')
}

// 跳转到我的合同
const goToContracts = () => {
  router.push('/contracts')
}

// 显示修改密码弹窗
const showPasswordDialog = () => {
  // 重置表单
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  showPwdPopup.value = true
}

// 提交修改密码
const submitPassword = async () => {
  submitting.value = true

  try {
    // 调用后端接口
    const res = await request({
      url: '/system/user/profile/updatePwd',
      method: 'put',
      data: {
        oldPassword: passwordForm.oldPassword,
        newPassword: passwordForm.newPassword
      }
    })

    if (res.code === 200) {
      showToast({ message: '密码修改成功，请重新登录', type: 'success' })
      showPwdPopup.value = false

      // 延迟退出登录，让用户看到提示
      setTimeout(async () => {
        await authStore.logout()
      }, 1500)
    } else {
      showToast({ message: res.msg || '修改失败', type: 'fail' })
    }
  } catch (error) {
    console.error('修改密码失败:', error)
    showToast({ message: error.message || '修改失败', type: 'fail' })
  } finally {
    submitting.value = false
  }
}

// 退出登录
const logout = async () => {
  try {
    await showDialog({
      title: '提示',
      message: '确定要退出登录吗？'
    })
    await authStore.logout()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('退出失败:', error)
    }
  }
}
</script>

<style scoped>
.page-container {
  padding: 16px 12px;
}
.profile-header {
  margin-bottom: 20px;
}
.user-info {
  margin-left: 16px;
}
.user-info h3 {
  margin: 0 0 6px 0;
}
.cell-icon {
  margin-right: 8px;
  color: #1989fa;
  font-size: 18px;
}
.logout-btn {
  margin: 30px 16px;
}
.popup-content {
  padding: 20px 16px;
}
.popup-content h3 {
  text-align: center;
  margin-bottom: 20px;
  font-size: 18px;
  font-weight: 600;
}
.popup-btn {
  margin: 24px 16px;
}
</style>
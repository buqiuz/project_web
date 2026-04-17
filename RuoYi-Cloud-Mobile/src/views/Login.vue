<template>
  <div class="login-container">
    <div class="login-header">
      <div class="logo-wrapper">
        <van-icon name="gold-coin-o" size="48" color="#1677ff" />
      </div>
      <h1 class="app-name">大富翁金融</h1>
      <p class="app-slogan">专业小额贷款 · 销售助手</p>
    </div>

    <van-form @submit="handleLogin" class="login-form">
      <van-cell-group inset>
        <van-field
          v-model="loginForm.username"
          name="username"
          label="账号"
          placeholder="请输入工号/手机号"
          :rules="[{ required: true, message: '请输入账号' }]"
          left-icon="user-o"
          clearable
        />
        <van-field
          v-model="loginForm.password"
          type="password"
          name="password"
          label="密码"
          placeholder="请输入密码"
          :rules="[
            { required: true, message: '请输入密码' },
            { validator: validatePassword, message: '密码至少6位' }
          ]"
          left-icon="lock"
          clearable
        />
        
        <!-- 验证码输入行 -->
        <van-field
          v-model="loginForm.code"
          name="code"
          label="验证码"
          placeholder="请输入验证码"
          :rules="[{ required: true, message: '请输入验证码' }]"
          left-icon="shield-o"
          clearable
        />
      </van-cell-group>

      <!-- 验证码图片 -->
      <div class="captcha-wrapper">
        <van-image
          :src="codeImg"
          @click="refreshCode"
          class="captcha-img"
          fit="contain"
        >
          <template #loading>
            <van-loading type="spinner" size="20" />
          </template>
        </van-image>
        <span class="captcha-tip">点击图片刷新</span>
      </div>

      <div class="login-options">
        <van-checkbox v-model="loginForm.rememberMe" icon-size="16px">
          记住账号
        </van-checkbox>
        <span class="forgot-pwd" @click="forgotPassword">忘记密码？</span>
      </div>

      <div class="login-btn-wrapper">
        <van-button
          block
          round
          type="primary"
          native-type="submit"
          :loading="loading"
          loading-text="登录中..."
          size="large"
        >
          登 录
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getCodeImg, getUserInfo } from '@/api/auth'
import { showToast } from 'vant'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const loading = ref(false)
const codeImg = ref('')
const codeUuid = ref('')

// 表单数据
const loginForm = reactive({
  username: '',
  password: '',
  code: '',
  rememberMe: false
})

// 密码验证：至少6位
const validatePassword = (value) => {
  return value && value.length >= 5
}

/**
 * 获取验证码
 */
const fetchCode = async () => {
  try {
    const res = await getCodeImg()
    if (res.code === 200) {
      // 若依直接返回 { img, uuid, captchaEnabled }
      const imgData = res.img
      // 加上 base64 图片前缀（如果未包含的话）
      codeImg.value = imgData.startsWith('data:') ? imgData : `data:image/gif;base64,${imgData}`
      codeUuid.value = res.uuid
    } else {
      console.error('获取验证码失败:', res.msg)
      showToast('获取验证码失败')
    }
  } catch (error) {
    console.error('获取验证码失败:', error)
    showToast('获取验证码失败，请稍后重试')
  }
}

/**
 * 刷新验证码
 */
const refreshCode = () => {
  loginForm.code = ''
  fetchCode()
}

/**
 * 从 localStorage 加载记住的账号
 */
const loadRememberedAccount = () => {
  const remembered = localStorage.getItem('remembered_account')
  if (remembered) {
    try {
      const account = JSON.parse(remembered)
      loginForm.username = account.username || ''
      loginForm.password = account.password || ''
      loginForm.rememberMe = true
    } catch (e) {
      console.error('读取记住账号失败:', e)
    }
  }
}

/**
 * 保存记住的账号
 */
const saveRememberedAccount = () => {
  if (loginForm.rememberMe) {
    localStorage.setItem('remembered_account', JSON.stringify({
      username: loginForm.username,
      password: loginForm.password
    }))
  } else {
    localStorage.removeItem('remembered_account')
  }
}

/**
 * 登录处理
 */
const handleLogin = async () => {
  loading.value = true

  try {
    await authStore.login({
      username: loginForm.username,
      password: loginForm.password,
      code: loginForm.code,
      uuid: codeUuid.value
    })

    try {
      const userInfoRes = await getUserInfo()
      console.log('===== 当前用户完整信息 =====')
      console.log('响应码:', userInfoRes.code)
      console.log('响应消息:', userInfoRes.msg)
      console.log('用户对象:', userInfoRes.user)

      if (userInfoRes.user) {
        console.log('用户ID:', userInfoRes.user.userId)
        console.log('用户名:', userInfoRes.user.userName)
        console.log('昵称:', userInfoRes.user.nickName)
        console.log('部门ID:', userInfoRes.user.deptId)
        console.log('邮箱:', userInfoRes.user.email)
        console.log('手机号:', userInfoRes.user.phonenumber)
        console.log('头像:', userInfoRes.user.avatar)
        console.log('性别:', userInfoRes.user.sex)
        console.log('状态:', userInfoRes.user.status)
      }

      console.log('角色列表:', userInfoRes.roles)
      console.log('权限列表:', userInfoRes.permissions)
      console.log('=========================')
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }

    saveRememberedAccount()

    const redirect = route.query.redirect || '/workbench'
    router.replace(redirect)
  } catch (error) {
    refreshCode()
    console.error('登录失败:', error)
    showToast(error.message || '登录失败，请检查账号密码')
  } finally {
    loading.value = false
  }
}

const forgotPassword = () => {
  showToast('请联系系统管理员重置密码')
}

// 页面初始化
onMounted(() => {
  loadRememberedAccount()
  fetchCode()
  
  if (authStore.isLoggedIn) {
    router.replace('/workbench')
  }
})
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(145deg, #f0f7ff 0%, #ffffff 100%);
  padding: 20px 16px;
  display: flex;
  flex-direction: column;
}

.login-header {
  text-align: center;
  margin-top: 40px;
  margin-bottom: 40px;
}

.logo-wrapper {
  width: 80px;
  height: 80px;
  margin: 0 auto 16px;
  background: rgba(22, 119, 255, 0.1);
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.app-name {
  font-size: 28px;
  font-weight: 700;
  color: #1f2f3e;
  margin: 0 0 8px 0;
  letter-spacing: 1px;
}

.app-slogan {
  font-size: 14px;
  color: #86909c;
  margin: 0;
}

.login-form {
  flex: 1;
}

.captcha-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin: 12px 0;
}

.captcha-img {
  width: 120px;
  height: 48px;
  cursor: pointer;
  border-radius: 6px;
  border: 1px solid #ebedf0;
  transition: all 0.3s;
}

.captcha-img:hover {
  border-color: #1677ff;
  box-shadow: 0 2px 8px rgba(22, 119, 255, 0.2);
}

.captcha-tip {
  font-size: 12px;
  color: #969799;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
}

.forgot-pwd {
  font-size: 14px;
  color: #1677ff;
}

.login-btn-wrapper {
  margin: 16px 16px 0;
}

/* 验证码图片样式 */
.captcha-img {
  width: 100px;
  height: 40px;
  cursor: pointer;
  border-radius: 4px;
  border: 1px solid #ebedf0;
}
</style>
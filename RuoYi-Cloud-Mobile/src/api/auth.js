import request from '@/utils/request'

/**
 * 登录
 * @param {string} username 用户名
 * @param {string} password 密码
 * @param {string} code     验证码（若依可传空字符串）
 * @param {string} uuid     验证码uuid（若依可传空字符串）
 */
export function login(username, password, code = '', uuid = '') {
  return request({
    url: '/auth/login',
    method: 'post',
    headers: {
      needToken: false,
      showLoading: true
    },
    data: {
      username,
      password,
      code,
      uuid
    }
  })
}

/**
 * 获取当前用户信息
 */
export function getUserInfo() {
  return request({
    url: '/system/user/getInfo',
    method: 'get'
  })
}

/**
 * 退出登录
 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'delete'
  })
}

/**
 * 获取验证码
 * 若依后端返回格式：{ code, msg, data: { uuid, img } }
 */
export function getCodeImg() {
  return request({
    url: '/code',
    method: 'get',
    headers: {
      isToken: false
    }
  })
}
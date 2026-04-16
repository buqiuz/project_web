import request from '@/utils/request'

/**
 * 查询客户列表
 * @param {Object} queryParams - 查询参数
 */
export function listCustomer(queryParams) {
  return request({
    url: '/system/loan/customer/list',
    method: 'get',
    params: queryParams
  })
}

/**
 * 查询客户详细
 * @param {number} customerId - 客户ID
 */
export function getCustomer(customerId) {
  return request({
    url: `/system/loan/customer/${customerId}`,
    method: 'get'
  })
}

/**
 * 新增客户
 * @param {Object} data - 客户信息
 */
export function addCustomer(data) {
  return request({
    url: '/system/loan/customer',
    method: 'post',
    data
  })
}

/**
 * 修改客户
 * @param {Object} data - 客户信息
 */
export function updateCustomer(data) {
  return request({
    url: '/system/loan/customer',
    method: 'put',
    data
  })
}

/**
 * 删除客户
 * @param {number|string} customerIds - 客户ID或ID数组
 */
export function delCustomer(customerIds) {
  return request({
    url: `/system/loan/customer/${customerIds}`,
    method: 'delete'
  })
}

/**
 * 领取公海客户
 * @param {number} customerId - 客户ID
 */
export function claimSeaCustomer(customerId) {
  return request({
    url: `/system/loan/customer/claim/${customerId}`,
    method: 'post'
  })
}

/**
 * 客户转公海
 * @param {number} customerId - 客户ID
 */
export function transferToSea(customerId) {
  return request({
    url: `/system/loan/customer/toSea/${customerId}`,
    method: 'post'
  })
}

/**
 * 转移客户给其他销售
 * @param {Object} data - 转移信息
 */
export function transferCustomer(data) {
  return request({
    url: '/system/loan/customer/transfer',
    method: 'post',
    data
  })
}

/**
 * 查询客户跟进记录列表
 * @param {Object} queryParams - 查询参数
 */
export function listFollow(queryParams) {
  return request({
    url: '/system/loan/customer/follow/list',
    method: 'get',
    params: queryParams
  })
}

/**
 * 添加客户跟进记录
 * @param {Object} data - 跟进记录信息
 */
export function addFollow(data) {
  return request({
    url: '/system/loan/customer/follow',
    method: 'post',
    data
  })
}

/**
 * 删除跟进记录
 * @param {number} followId - 跟进记录ID
 */
export function delFollow(followId) {
  return request({
    url: `/system/loan/customer/follow/${followId}`,
    method: 'delete'
  })
}

/**
 * 释放超期客户到公海
 * @param {number} releaseDays - 释放天数（默认7天）
 */
export function releaseTimeoutCustomers(releaseDays = 7) {
  return request({
    url: '/system/loan/customer/releaseTimeout',
    method: 'post',
    params: { releaseDays }
  })
}

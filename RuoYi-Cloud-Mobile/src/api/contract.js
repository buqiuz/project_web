import request from '@/utils/request'

/**
 * 查询合同列表
 */
export function listContract(query) {
  return request({
    url: '/system/loan/contract/list',
    method: 'get',
    params: query
  })
}

/**
 * 查询合同详细
 */
export function getContract(contractId) {
  return request({
    url: `/system/loan/contract/${contractId}`,
    method: 'get'
  })
}

/**
 * 新增合同
 */
export function addContract(data) {
  return request({
    url: '/system/loan/contract',
    method: 'post',
    data
  })
}

/**
 * 修改合同
 */
export function updateContract(data) {
  return request({
    url: '/system/loan/contract',
    method: 'put',
    data
  })
}

/**
 * 删除合同
 */
export function delContract(contractIds) {
  return request({
    url: `/system/loan/contract/${contractIds}`,
    method: 'delete'
  })
}

/**
 * 提交金融审核
 */
export function submitFinanceReview(contractId) {
  return request({
    url: `/system/loan/contract/submit/${contractId}`,
    method: 'post'
  })
}

/**
 * 金融审核
 */
export function financeAudit(data) {
  return request({
    url: '/system/loan/contract/financeAudit',
    method: 'post',
    data
  })
}

/**
 * 查询服务费收款记录
 */
export function listContractFee(query) {
  return request({
    url: '/system/loan/contract/fee/list',
    method: 'get',
    params: query
  })
}

/**
 * 录入服务费收款
 */
export function collectContractFee(data) {
  return request({
    url: '/system/loan/contract/fee',
    method: 'post',
    data
  })
}
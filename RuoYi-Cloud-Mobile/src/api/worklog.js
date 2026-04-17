import request from '@/utils/request'

/**
 * 查询工作日志列表
 * @param {Object} queryParams - 查询参数
 */
export function listWorkLog(queryParams) {
  return request({
    url: '/system/loan/worklog/list',
    method: 'get',
    params: queryParams
  })
}

/**
 * 查询工作日志详细
 * @param {number} logId - 日志ID
 */
export function getWorkLog(logId) {
  return request({
    url: `/system/loan/worklog/${logId}`,
    method: 'get'
  })
}

/**
 * 查询今日工作日志
 */
export function getTodayWorkLog() {
  return request({
    url: '/system/loan/worklog/mine/today',
    method: 'get'
  })
}

/**
 * 新增工作日志
 * @param {Object} data - 日志信息
 */
export function addWorkLog(data) {
  return request({
    url: '/system/loan/worklog',
    method: 'post',
    data
  })
}

/**
 * 修改工作日志
 * @param {Object} data - 日志信息
 */
export function updateWorkLog(data) {
  return request({
    url: '/system/loan/worklog',
    method: 'put',
    data
  })
}

/**
 * 删除工作日志
 * @param {number|string} logIds - 日志ID或ID数组
 */
export function delWorkLog(logIds) {
  return request({
    url: `/system/loan/worklog/${logIds}`,
    method: 'delete'
  })
}
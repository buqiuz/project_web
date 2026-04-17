import request from '@/utils/request'

/**
 * 获取业绩概览
 */
export function getPerformanceOverview(query) {
  return request({
    url: '/system/loan/performance/overview',
    method: 'get',
    params: query
  })
}

/**
 * 获取个人业绩列表
 */
export function listPersonalPerformance(query) {
  return request({
    url: '/system/loan/performance/personal/list',
    method: 'get',
    params: query
  })
}

/**
 * 获取部门业绩列表
 */
export function listDeptPerformance(query) {
  return request({
    url: '/system/loan/performance/dept/list',
    method: 'get',
    params: query
  })
}

/**
 * 获取战区业绩列表
 */
export function listZonePerformance(query) {
  return request({
    url: '/system/loan/performance/zone/list',
    method: 'get',
    params: query
  })
}

/**
 * 获取业绩排名
 */
export function listPerformanceRank(query) {
  return request({
    url: '/system/loan/performance/rank/list',
    method: 'get',
    params: query
  })
}

/**
 * 获取提成明细
 */
export function listPerformanceCommission(query) {
  return request({
    url: '/system/loan/performance/commission/list',
    method: 'get',
    params: query
  })
}
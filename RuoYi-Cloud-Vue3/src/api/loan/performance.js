import request from '@/utils/request'

// 业绩总览
export function getLoanPerformanceOverview(query) {
    return request({
        url: '/system/loan/performance/overview',
        method: 'get',
        params: query
    })
}

// 个人业绩
export function listPersonalPerformance(query) {
    return request({
        url: '/system/loan/performance/personal/list',
        method: 'get',
        params: query
    })
}

// 部门业绩
export function listDeptPerformance(query) {
    return request({
        url: '/system/loan/performance/dept/list',
        method: 'get',
        params: query
    })
}

// 战区业绩
export function listZonePerformance(query) {
    return request({
        url: '/system/loan/performance/zone/list',
        method: 'get',
        params: query
    })
}

// 排名列表
export function listPerformanceRank(query) {
    return request({
        url: '/system/loan/performance/rank/list',
        method: 'get',
        params: query
    })
}

// 提成测算
export function listPerformanceCommission(query) {
    return request({
        url: '/system/loan/performance/commission/list',
        method: 'get',
        params: query
    })
}

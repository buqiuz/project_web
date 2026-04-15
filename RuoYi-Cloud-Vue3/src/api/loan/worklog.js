import request from '@/utils/request'

// 查询工作日志列表
export function listLoanWorkLog(query) {
    return request({
        url: '/system/loan/worklog/list',
        method: 'get',
        params: query
    })
}

// 查询工作日志详情
export function getLoanWorkLog(logId) {
    return request({
        url: '/system/loan/worklog/' + logId,
        method: 'get'
    })
}

// 获取我的今日工作日志
export function getMyTodayWorkLog() {
    return request({
        url: '/system/loan/worklog/mine/today',
        method: 'get'
    })
}

// 新增工作日志
export function addLoanWorkLog(data) {
    return request({
        url: '/system/loan/worklog',
        method: 'post',
        data: data
    })
}

// 修改工作日志
export function updateLoanWorkLog(data) {
    return request({
        url: '/system/loan/worklog',
        method: 'put',
        data: data
    })
}

// 删除工作日志
export function delLoanWorkLog(logId) {
    return request({
        url: '/system/loan/worklog/' + logId,
        method: 'delete'
    })
}

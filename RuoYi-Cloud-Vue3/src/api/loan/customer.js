import request from '@/utils/request'

// 查询客户列表
export function listLoanCustomer(query) {
    return request({
        url: '/system/loan/customer/list',
        method: 'get',
        params: query
    })
}

// 查询客户详情
export function getLoanCustomer(customerId) {
    return request({
        url: '/system/loan/customer/' + customerId,
        method: 'get'
    })
}

// 新增客户
export function addLoanCustomer(data) {
    return request({
        url: '/system/loan/customer',
        method: 'post',
        data: data
    })
}

// 修改客户
export function updateLoanCustomer(data) {
    return request({
        url: '/system/loan/customer',
        method: 'put',
        data: data
    })
}

// 删除客户
export function delLoanCustomer(customerId) {
    return request({
        url: '/system/loan/customer/' + customerId,
        method: 'delete'
    })
}

// 客户转移
export function transferLoanCustomer(data) {
    return request({
        url: '/system/loan/customer/transfer',
        method: 'post',
        data: data
    })
}

// 客户转公海
export function toSeaLoanCustomer(customerId) {
    return request({
        url: '/system/loan/customer/toSea/' + customerId,
        method: 'post'
    })
}

// 领取公海客户
export function claimLoanCustomer(customerId) {
    return request({
        url: '/system/loan/customer/claim/' + customerId,
        method: 'post'
    })
}

// 释放超期客户
export function releaseTimeoutCustomer(releaseDays) {
    return request({
        url: '/system/loan/customer/releaseTimeout',
        method: 'post',
        params: { releaseDays }
    })
}

// 跟进记录列表
export function listLoanFollow(query) {
    return request({
        url: '/system/loan/customer/follow/list',
        method: 'get',
        params: query
    })
}

// 新增跟进记录
export function addLoanFollow(data) {
    return request({
        url: '/system/loan/customer/follow',
        method: 'post',
        data: data
    })
}

// 删除跟进记录
export function delLoanFollow(followId) {
    return request({
        url: '/system/loan/customer/follow/' + followId,
        method: 'delete'
    })
}

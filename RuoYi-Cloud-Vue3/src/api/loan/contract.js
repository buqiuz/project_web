import request from '@/utils/request'

// 查询合同列表
export function listLoanContract(query) {
    return request({
        url: '/system/loan/contract/list',
        method: 'get',
        params: query
    })
}

// 查询合同详情
export function getLoanContract(contractId) {
    return request({
        url: '/system/loan/contract/' + contractId,
        method: 'get'
    })
}

// 新增合同
export function addLoanContract(data) {
    return request({
        url: '/system/loan/contract',
        method: 'post',
        data: data
    })
}

// 修改合同
export function updateLoanContract(data) {
    return request({
        url: '/system/loan/contract',
        method: 'put',
        data: data
    })
}

// 删除合同
export function delLoanContract(contractId) {
    return request({
        url: '/system/loan/contract/' + contractId,
        method: 'delete'
    })
}

// 提交金融审核
export function submitFinanceReview(contractId) {
    return request({
        url: '/system/loan/contract/submit/' + contractId,
        method: 'post'
    })
}

// 金融审核
export function financeAudit(data) {
    return request({
        url: '/system/loan/contract/financeAudit',
        method: 'post',
        data: data
    })
}

// 服务费记录列表
export function listContractFee(query) {
    return request({
        url: '/system/loan/contract/fee/list',
        method: 'get',
        params: query
    })
}

// 收取服务费
export function collectContractFee(data) {
    return request({
        url: '/system/loan/contract/fee',
        method: 'post',
        data: data
    })
}

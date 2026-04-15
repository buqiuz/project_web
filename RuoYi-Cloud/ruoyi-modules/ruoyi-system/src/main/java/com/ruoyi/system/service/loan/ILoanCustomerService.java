package com.ruoyi.system.service.loan;

import java.util.List;
import com.ruoyi.system.domain.loan.LoanCustomer;
import com.ruoyi.system.domain.loan.LoanCustomerFollow;

/**
 * 贷款客户服务
 *
 * @author ruoyi
 */
public interface ILoanCustomerService
{
    public LoanCustomer selectLoanCustomerById(Long customerId);

    public List<LoanCustomer> selectLoanCustomerList(LoanCustomer customer);

    public int insertLoanCustomer(LoanCustomer customer);

    public int updateLoanCustomer(LoanCustomer customer);

    public int deleteLoanCustomerByIds(Long[] customerIds);

    public int transferCustomer(Long customerId, Long userId, Long deptId, String transferRemark, String operator);

    public int toSea(Long customerId, String operator);

    public int claimSea(Long customerId, Long userId, Long deptId, String operator);

    public int releaseTimeoutCustomers(Integer releaseDays, String operator);

    public List<LoanCustomerFollow> selectFollowListByCustomerId(Long customerId);

    public int insertFollow(LoanCustomerFollow follow);

    public int deleteFollowById(Long followId);
}

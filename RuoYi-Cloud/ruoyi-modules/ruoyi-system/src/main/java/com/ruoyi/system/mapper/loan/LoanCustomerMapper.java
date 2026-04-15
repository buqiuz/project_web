package com.ruoyi.system.mapper.loan;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.loan.LoanCustomer;
import com.ruoyi.system.domain.loan.LoanCustomerFollow;

/**
 * 贷款客户Mapper
 *
 * @author ruoyi
 */
public interface LoanCustomerMapper
{
    public LoanCustomer selectLoanCustomerById(Long customerId);

    public List<LoanCustomer> selectLoanCustomerList(LoanCustomer customer);

    public int insertLoanCustomer(LoanCustomer customer);

    public int updateLoanCustomer(LoanCustomer customer);

    public int deleteLoanCustomerByIds(Long[] customerIds);

    public int countByPhone(@Param("phone") String phone, @Param("customerId") Long customerId);

    public int countByIdCardNo(@Param("idCardNo") String idCardNo, @Param("customerId") Long customerId);

    public int countByBusinessLicenseNo(@Param("businessLicenseNo") String businessLicenseNo, @Param("customerId") Long customerId);

    public int transferCustomer(@Param("customerId") Long customerId, @Param("userId") Long userId,
            @Param("deptId") Long deptId, @Param("operator") String operator, @Param("transferRemark") String transferRemark);

    public int updateCustomerToSea(@Param("customerId") Long customerId, @Param("operator") String operator,
            @Param("seaTime") Date seaTime);

    public int claimSeaCustomer(@Param("customerId") Long customerId, @Param("userId") Long userId,
            @Param("deptId") Long deptId, @Param("operator") String operator);

    public int releaseTimeoutCustomers(@Param("releaseDays") Integer releaseDays, @Param("operator") String operator,
            @Param("seaTime") Date seaTime);

    public int markCustomerSigned(@Param("customerId") Long customerId, @Param("operator") String operator,
            @Param("signedTime") Date signedTime);

    public List<LoanCustomerFollow> selectFollowListByCustomerId(Long customerId);

    public int insertFollow(LoanCustomerFollow follow);

    public int deleteFollowById(Long followId);

    public int updateCustomerFollowMeta(@Param("customerId") Long customerId, @Param("intentLevel") String intentLevel,
            @Param("nextFollowTime") Date nextFollowTime, @Param("lastFollowTime") Date lastFollowTime,
            @Param("operator") String operator);
}

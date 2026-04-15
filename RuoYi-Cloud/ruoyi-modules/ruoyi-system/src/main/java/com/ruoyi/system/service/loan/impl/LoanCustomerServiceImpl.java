package com.ruoyi.system.service.loan.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.datascope.annotation.DataScope;
import com.ruoyi.system.domain.loan.LoanCustomer;
import com.ruoyi.system.domain.loan.LoanCustomerFollow;
import com.ruoyi.system.mapper.loan.LoanCustomerMapper;
import com.ruoyi.system.service.loan.ILoanCustomerService;

/**
 * 贷款客户服务实现
 *
 * @author ruoyi
 */
@Service
public class LoanCustomerServiceImpl implements ILoanCustomerService
{
    @Autowired
    private LoanCustomerMapper customerMapper;

    @Override
    public LoanCustomer selectLoanCustomerById(Long customerId)
    {
        return customerMapper.selectLoanCustomerById(customerId);
    }

    @Override
    @DataScope(deptAlias = "c", userAlias = "c")
    public List<LoanCustomer> selectLoanCustomerList(LoanCustomer customer)
    {
        return customerMapper.selectLoanCustomerList(customer);
    }

    @Override
    public int insertLoanCustomer(LoanCustomer customer)
    {
        validateUnique(customer);
        if (StringUtils.isEmpty(customer.getStatus()))
        {
            customer.setStatus("PRIVATE");
        }
        if (StringUtils.isEmpty(customer.getIntentLevel()))
        {
            customer.setIntentLevel("C");
        }
        return customerMapper.insertLoanCustomer(customer);
    }

    @Override
    public int updateLoanCustomer(LoanCustomer customer)
    {
        validateUnique(customer);
        return customerMapper.updateLoanCustomer(customer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteLoanCustomerByIds(Long[] customerIds)
    {
        return customerMapper.deleteLoanCustomerByIds(customerIds);
    }

    @Override
    public int transferCustomer(Long customerId, Long userId, Long deptId, String transferRemark, String operator)
    {
        LoanCustomer customer = customerMapper.selectLoanCustomerById(customerId);
        if (customer == null)
        {
            throw new ServiceException("客户不存在");
        }
        if ("SIGNED".equals(customer.getStatus()))
        {
            throw new ServiceException("已签约客户不允许转移");
        }
        return customerMapper.transferCustomer(customerId, userId, deptId, operator, transferRemark);
    }

    @Override
    public int toSea(Long customerId, String operator)
    {
        LoanCustomer customer = customerMapper.selectLoanCustomerById(customerId);
        if (customer == null)
        {
            throw new ServiceException("客户不存在");
        }
        if ("SIGNED".equals(customer.getStatus()))
        {
            throw new ServiceException("已签约客户不允许转入公海");
        }
        return customerMapper.updateCustomerToSea(customerId, operator, new Date());
    }

    @Override
    public int claimSea(Long customerId, Long userId, Long deptId, String operator)
    {
        LoanCustomer customer = customerMapper.selectLoanCustomerById(customerId);
        if (customer == null)
        {
            throw new ServiceException("客户不存在");
        }
        if (!"SEA".equals(customer.getStatus()))
        {
            throw new ServiceException("仅公海客户可领取");
        }
        int rows = customerMapper.claimSeaCustomer(customerId, userId, deptId, operator);
        if (rows == 0)
        {
            throw new ServiceException("客户领取失败，请刷新后重试");
        }
        return rows;
    }

    @Override
    public int releaseTimeoutCustomers(Integer releaseDays, String operator)
    {
        int realReleaseDays = releaseDays == null || releaseDays <= 0 ? 7 : releaseDays;
        return customerMapper.releaseTimeoutCustomers(realReleaseDays, operator, new Date());
    }

    @Override
    public List<LoanCustomerFollow> selectFollowListByCustomerId(Long customerId)
    {
        return customerMapper.selectFollowListByCustomerId(customerId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFollow(LoanCustomerFollow follow)
    {
        follow.setValidFlag(StringUtils.isEmpty(follow.getValidFlag()) ? "1" : follow.getValidFlag());
        int rows = customerMapper.insertFollow(follow);
        if (rows > 0)
        {
            customerMapper.updateCustomerFollowMeta(follow.getCustomerId(), follow.getIntentLevel(),
                    follow.getNextFollowTime(), new Date(), follow.getCreateBy());
        }
        return rows;
    }

    @Override
    public int deleteFollowById(Long followId)
    {
        return customerMapper.deleteFollowById(followId);
    }

    private void validateUnique(LoanCustomer customer)
    {
        if (StringUtils.isNotEmpty(customer.getPhone())
                && customerMapper.countByPhone(customer.getPhone(), customer.getCustomerId()) > 0)
        {
            throw new ServiceException("手机号已存在，请确认是否重复客户");
        }
        if (StringUtils.isNotEmpty(customer.getIdCardNo())
                && customerMapper.countByIdCardNo(customer.getIdCardNo(), customer.getCustomerId()) > 0)
        {
            throw new ServiceException("身份证号已存在，请确认是否重复客户");
        }
        if (StringUtils.isNotEmpty(customer.getBusinessLicenseNo())
                && customerMapper.countByBusinessLicenseNo(customer.getBusinessLicenseNo(), customer.getCustomerId()) > 0)
        {
            throw new ServiceException("营业执照号已存在，请确认是否重复客户");
        }
    }
}

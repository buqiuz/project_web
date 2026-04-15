package com.ruoyi.system.domain.loan.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 销售业绩明细
 *
 * @author ruoyi
 */
public class LoanSalesPerformanceVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long userId;

    private String userName;

    private Long deptId;

    private String deptName;

    private String deptAncestors;

    private Long contractCount;

    private BigDecimal contractAmount;

    private Long loanedCount;

    private BigDecimal loanedAmount;

    private BigDecimal feeIncome;

    private Long callCount;

    private Long validCallCount;

    private Long intentCustomerCount;

    private Long visitCount;

    private Long signedCount;

    private BigDecimal conversionRate;

    private BigDecimal commissionRate;

    private BigDecimal commissionAmount;

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public String getDeptAncestors()
    {
        return deptAncestors;
    }

    public void setDeptAncestors(String deptAncestors)
    {
        this.deptAncestors = deptAncestors;
    }

    public Long getContractCount()
    {
        return contractCount;
    }

    public void setContractCount(Long contractCount)
    {
        this.contractCount = contractCount;
    }

    public BigDecimal getContractAmount()
    {
        return contractAmount;
    }

    public void setContractAmount(BigDecimal contractAmount)
    {
        this.contractAmount = contractAmount;
    }

    public Long getLoanedCount()
    {
        return loanedCount;
    }

    public void setLoanedCount(Long loanedCount)
    {
        this.loanedCount = loanedCount;
    }

    public BigDecimal getLoanedAmount()
    {
        return loanedAmount;
    }

    public void setLoanedAmount(BigDecimal loanedAmount)
    {
        this.loanedAmount = loanedAmount;
    }

    public BigDecimal getFeeIncome()
    {
        return feeIncome;
    }

    public void setFeeIncome(BigDecimal feeIncome)
    {
        this.feeIncome = feeIncome;
    }

    public Long getCallCount()
    {
        return callCount;
    }

    public void setCallCount(Long callCount)
    {
        this.callCount = callCount;
    }

    public Long getValidCallCount()
    {
        return validCallCount;
    }

    public void setValidCallCount(Long validCallCount)
    {
        this.validCallCount = validCallCount;
    }

    public Long getIntentCustomerCount()
    {
        return intentCustomerCount;
    }

    public void setIntentCustomerCount(Long intentCustomerCount)
    {
        this.intentCustomerCount = intentCustomerCount;
    }

    public Long getVisitCount()
    {
        return visitCount;
    }

    public void setVisitCount(Long visitCount)
    {
        this.visitCount = visitCount;
    }

    public Long getSignedCount()
    {
        return signedCount;
    }

    public void setSignedCount(Long signedCount)
    {
        this.signedCount = signedCount;
    }

    public BigDecimal getConversionRate()
    {
        return conversionRate;
    }

    public void setConversionRate(BigDecimal conversionRate)
    {
        this.conversionRate = conversionRate;
    }

    public BigDecimal getCommissionRate()
    {
        return commissionRate;
    }

    public void setCommissionRate(BigDecimal commissionRate)
    {
        this.commissionRate = commissionRate;
    }

    public BigDecimal getCommissionAmount()
    {
        return commissionAmount;
    }

    public void setCommissionAmount(BigDecimal commissionAmount)
    {
        this.commissionAmount = commissionAmount;
    }
}

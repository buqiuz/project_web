package com.ruoyi.system.domain.loan.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 战区业绩统计
 *
 * @author ruoyi
 */
public class LoanZonePerformanceVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String zoneName;

    private Long salesUserCount;

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

    private BigDecimal commissionAmount;

    public String getZoneName()
    {
        return zoneName;
    }

    public void setZoneName(String zoneName)
    {
        this.zoneName = zoneName;
    }

    public Long getSalesUserCount()
    {
        return salesUserCount;
    }

    public void setSalesUserCount(Long salesUserCount)
    {
        this.salesUserCount = salesUserCount;
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

    public BigDecimal getCommissionAmount()
    {
        return commissionAmount;
    }

    public void setCommissionAmount(BigDecimal commissionAmount)
    {
        this.commissionAmount = commissionAmount;
    }
}

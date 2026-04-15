package com.ruoyi.system.domain.loan;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 服务费收款记录
 *
 * @author ruoyi
 */
public class LoanFeeRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Long feeId;

    /** 合同ID */
    private Long contractId;

    /** 合同编号（查询字段） */
    private String contractNo;

    /** 阶段（PRE前置 POST放款后） */
    private String stage;

    /** 收款金额 */
    private BigDecimal amount;

    /** 支付方式 */
    private String payMethod;

    /** 付款人 */
    private String payerName;

    /** 收款时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;

    /** 收款人ID */
    private Long collectorUserId;

    /** 收款人姓名 */
    private String collectorUserName;

    /** 状态（PAID/REFUND） */
    private String status;

    public Long getFeeId()
    {
        return feeId;
    }

    public void setFeeId(Long feeId)
    {
        this.feeId = feeId;
    }

    public Long getContractId()
    {
        return contractId;
    }

    public void setContractId(Long contractId)
    {
        this.contractId = contractId;
    }

    public String getContractNo()
    {
        return contractNo;
    }

    public void setContractNo(String contractNo)
    {
        this.contractNo = contractNo;
    }

    public String getStage()
    {
        return stage;
    }

    public void setStage(String stage)
    {
        this.stage = stage;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    public String getPayMethod()
    {
        return payMethod;
    }

    public void setPayMethod(String payMethod)
    {
        this.payMethod = payMethod;
    }

    public String getPayerName()
    {
        return payerName;
    }

    public void setPayerName(String payerName)
    {
        this.payerName = payerName;
    }

    public Date getPayTime()
    {
        return payTime;
    }

    public void setPayTime(Date payTime)
    {
        this.payTime = payTime;
    }

    public Long getCollectorUserId()
    {
        return collectorUserId;
    }

    public void setCollectorUserId(Long collectorUserId)
    {
        this.collectorUserId = collectorUserId;
    }

    public String getCollectorUserName()
    {
        return collectorUserName;
    }

    public void setCollectorUserName(String collectorUserName)
    {
        this.collectorUserName = collectorUserName;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("feeId", getFeeId())
            .append("contractId", getContractId())
            .append("contractNo", getContractNo())
            .append("stage", getStage())
            .append("amount", getAmount())
            .append("payMethod", getPayMethod())
            .append("payerName", getPayerName())
            .append("payTime", getPayTime())
            .append("collectorUserId", getCollectorUserId())
            .append("collectorUserName", getCollectorUserName())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("remark", getRemark())
            .toString();
    }
}

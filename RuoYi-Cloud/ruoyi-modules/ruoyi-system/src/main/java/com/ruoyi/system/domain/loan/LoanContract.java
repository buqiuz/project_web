package com.ruoyi.system.domain.loan;

import java.math.BigDecimal;
import java.util.Date;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 贷款合同信息
 *
 * @author ruoyi
 */
public class LoanContract extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 合同ID */
    private Long contractId;

    /** 合同编号 */
    private String contractNo;

    /** 客户ID */
    private Long customerId;

    /** 客户姓名（查询字段） */
    private String customerName;

    /** 销售ID */
    private Long userId;

    /** 销售姓名（查询字段） */
    private String userName;

    /** 部门ID */
    private Long deptId;

    /** 产品名称 */
    private String productName;

    /** 附件路径（多个逗号分隔） */
    private String attachmentUrls;

    /** 合同金额 */
    private BigDecimal contractAmount;

    /** 发放金额 */
    private BigDecimal disburseAmount;

    /** 前置服务费应收 */
    private BigDecimal firstFeeAmount;

    /** 放款后服务费应收 */
    private BigDecimal secondFeeAmount;

    /** 前置服务费实收 */
    private BigDecimal firstFeePaid;

    /** 放款后服务费实收 */
    private BigDecimal secondFeePaid;

    /** 合同状态（SIGNED/FINANCE_REVIEW/BANK_REVIEW/LOANED/REJECTED） */
    private String status;

    /** 签约时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date signTime;

    /** 提交金融审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;

    /** 金融审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date financeAuditTime;

    /** 银行放款时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date bankLoanTime;

    /** 金融审核人员 */
    private Long financeUserId;

    /** 银行反馈 */
    private String bankResult;

    public Long getContractId()
    {
        return contractId;
    }

    public void setContractId(Long contractId)
    {
        this.contractId = contractId;
    }

    @NotBlank(message = "合同编号不能为空")
    public String getContractNo()
    {
        return contractNo;
    }

    public void setContractNo(String contractNo)
    {
        this.contractNo = contractNo;
    }

    public Long getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(Long customerId)
    {
        this.customerId = customerId;
    }

    public String getCustomerName()
    {
        return customerName;
    }

    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
    }

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

    public String getProductName()
    {
        return productName;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public String getAttachmentUrls()
    {
        return attachmentUrls;
    }

    public void setAttachmentUrls(String attachmentUrls)
    {
        this.attachmentUrls = attachmentUrls;
    }

    public BigDecimal getContractAmount()
    {
        return contractAmount;
    }

    public void setContractAmount(BigDecimal contractAmount)
    {
        this.contractAmount = contractAmount;
    }

    public BigDecimal getDisburseAmount()
    {
        return disburseAmount;
    }

    public void setDisburseAmount(BigDecimal disburseAmount)
    {
        this.disburseAmount = disburseAmount;
    }

    public BigDecimal getFirstFeeAmount()
    {
        return firstFeeAmount;
    }

    public void setFirstFeeAmount(BigDecimal firstFeeAmount)
    {
        this.firstFeeAmount = firstFeeAmount;
    }

    public BigDecimal getSecondFeeAmount()
    {
        return secondFeeAmount;
    }

    public void setSecondFeeAmount(BigDecimal secondFeeAmount)
    {
        this.secondFeeAmount = secondFeeAmount;
    }

    public BigDecimal getFirstFeePaid()
    {
        return firstFeePaid;
    }

    public void setFirstFeePaid(BigDecimal firstFeePaid)
    {
        this.firstFeePaid = firstFeePaid;
    }

    public BigDecimal getSecondFeePaid()
    {
        return secondFeePaid;
    }

    public void setSecondFeePaid(BigDecimal secondFeePaid)
    {
        this.secondFeePaid = secondFeePaid;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Date getSignTime()
    {
        return signTime;
    }

    public void setSignTime(Date signTime)
    {
        this.signTime = signTime;
    }

    public Date getSubmitTime()
    {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime)
    {
        this.submitTime = submitTime;
    }

    public Date getFinanceAuditTime()
    {
        return financeAuditTime;
    }

    public void setFinanceAuditTime(Date financeAuditTime)
    {
        this.financeAuditTime = financeAuditTime;
    }

    public Date getBankLoanTime()
    {
        return bankLoanTime;
    }

    public void setBankLoanTime(Date bankLoanTime)
    {
        this.bankLoanTime = bankLoanTime;
    }

    public Long getFinanceUserId()
    {
        return financeUserId;
    }

    public void setFinanceUserId(Long financeUserId)
    {
        this.financeUserId = financeUserId;
    }

    public String getBankResult()
    {
        return bankResult;
    }

    public void setBankResult(String bankResult)
    {
        this.bankResult = bankResult;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("contractId", getContractId())
            .append("contractNo", getContractNo())
            .append("customerId", getCustomerId())
            .append("userId", getUserId())
            .append("deptId", getDeptId())
            .append("productName", getProductName())
            .append("contractAmount", getContractAmount())
            .append("disburseAmount", getDisburseAmount())
            .append("firstFeeAmount", getFirstFeeAmount())
            .append("secondFeeAmount", getSecondFeeAmount())
            .append("firstFeePaid", getFirstFeePaid())
            .append("secondFeePaid", getSecondFeePaid())
            .append("status", getStatus())
            .append("signTime", getSignTime())
            .append("submitTime", getSubmitTime())
            .append("financeAuditTime", getFinanceAuditTime())
            .append("bankLoanTime", getBankLoanTime())
            .append("financeUserId", getFinanceUserId())
            .append("bankResult", getBankResult())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

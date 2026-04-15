package com.ruoyi.system.domain.loan;

import java.math.BigDecimal;
import java.util.Date;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.web.domain.BaseEntity;
import com.ruoyi.common.core.xss.Xss;

/**
 * 贷款客户信息
 *
 * @author ruoyi
 */
public class LoanCustomer extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 客户ID */
    private Long customerId;

    /** 客户姓名 */
    private String customerName;

    /** 客户类型（P个人 C企业） */
    private String customerType;

    /** 联系电话 */
    private String phone;

    /** 身份证号 */
    private String idCardNo;

    /** 公司名称 */
    private String companyName;

    /** 营业执照号 */
    private String businessLicenseNo;

    /** 客户来源 */
    private String sourceType;

    /** 意向等级 */
    private String intentLevel;

    /** 意向金额 */
    private BigDecimal intentAmount;

    /** 客户状态（PRIVATE私有 SEA公海 SIGNED已签约） */
    private String status;

    /** 当前归属销售 */
    private Long userId;

    /** 当前归属部门 */
    private Long deptId;

    /** 销售姓名（查询字段） */
    private String userName;

    /** 部门名称（查询字段） */
    private String deptName;

    /** 最近一次跟进时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastFollowTime;

    /** 下次跟进时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date nextFollowTime;

    /** 签约时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date signedTime;

    /** 进入公海时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date seaTime;

    /** 转移备注 */
    private String transferRemark;

    public Long getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(Long customerId)
    {
        this.customerId = customerId;
    }

    @Xss(message = "客户姓名不能包含脚本字符")
    @NotBlank(message = "客户姓名不能为空")
    @Size(min = 0, max = 64, message = "客户姓名长度不能超过64个字符")
    public String getCustomerName()
    {
        return customerName;
    }

    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
    }

    public String getCustomerType()
    {
        return customerType;
    }

    public void setCustomerType(String customerType)
    {
        this.customerType = customerType;
    }

    @NotBlank(message = "联系电话不能为空")
    @Size(min = 0, max = 20, message = "联系电话长度不能超过20个字符")
    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getIdCardNo()
    {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo)
    {
        this.idCardNo = idCardNo;
    }

    public String getCompanyName()
    {
        return companyName;
    }

    public void setCompanyName(String companyName)
    {
        this.companyName = companyName;
    }

    public String getBusinessLicenseNo()
    {
        return businessLicenseNo;
    }

    public void setBusinessLicenseNo(String businessLicenseNo)
    {
        this.businessLicenseNo = businessLicenseNo;
    }

    public String getSourceType()
    {
        return sourceType;
    }

    public void setSourceType(String sourceType)
    {
        this.sourceType = sourceType;
    }

    public String getIntentLevel()
    {
        return intentLevel;
    }

    public void setIntentLevel(String intentLevel)
    {
        this.intentLevel = intentLevel;
    }

    public BigDecimal getIntentAmount()
    {
        return intentAmount;
    }

    public void setIntentAmount(BigDecimal intentAmount)
    {
        this.intentAmount = intentAmount;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public Date getLastFollowTime()
    {
        return lastFollowTime;
    }

    public void setLastFollowTime(Date lastFollowTime)
    {
        this.lastFollowTime = lastFollowTime;
    }

    public Date getNextFollowTime()
    {
        return nextFollowTime;
    }

    public void setNextFollowTime(Date nextFollowTime)
    {
        this.nextFollowTime = nextFollowTime;
    }

    public Date getSignedTime()
    {
        return signedTime;
    }

    public void setSignedTime(Date signedTime)
    {
        this.signedTime = signedTime;
    }

    public Date getSeaTime()
    {
        return seaTime;
    }

    public void setSeaTime(Date seaTime)
    {
        this.seaTime = seaTime;
    }

    public String getTransferRemark()
    {
        return transferRemark;
    }

    public void setTransferRemark(String transferRemark)
    {
        this.transferRemark = transferRemark;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("customerId", getCustomerId())
            .append("customerName", getCustomerName())
            .append("customerType", getCustomerType())
            .append("phone", getPhone())
            .append("idCardNo", getIdCardNo())
            .append("companyName", getCompanyName())
            .append("businessLicenseNo", getBusinessLicenseNo())
            .append("sourceType", getSourceType())
            .append("intentLevel", getIntentLevel())
            .append("intentAmount", getIntentAmount())
            .append("status", getStatus())
            .append("userId", getUserId())
            .append("deptId", getDeptId())
            .append("lastFollowTime", getLastFollowTime())
            .append("nextFollowTime", getNextFollowTime())
            .append("signedTime", getSignedTime())
            .append("seaTime", getSeaTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

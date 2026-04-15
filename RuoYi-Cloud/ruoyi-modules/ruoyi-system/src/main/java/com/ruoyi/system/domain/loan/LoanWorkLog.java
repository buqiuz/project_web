package com.ruoyi.system.domain.loan;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 销售工作日志
 *
 * @author ruoyi
 */
public class LoanWorkLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 日志ID */
    private Long logId;

    /** 工作日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date workDate;

    /** 销售ID */
    private Long userId;

    /** 销售账号（查询字段） */
    private String userName;

    /** 部门ID */
    private Long deptId;

    /** 部门名称（查询字段） */
    private String deptName;

    /** 电话数 */
    private Integer callCount;

    /** 有效电话数 */
    private Integer validCallCount;

    /** 意向客户数 */
    private Integer intentCustomerCount;

    /** 面谈客户数 */
    private Integer visitCount;

    /** 当日签约数 */
    private Integer signedCount;

    public Long getLogId()
    {
        return logId;
    }

    public void setLogId(Long logId)
    {
        this.logId = logId;
    }

    @NotNull(message = "工作日期不能为空")
    public Date getWorkDate()
    {
        return workDate;
    }

    public void setWorkDate(Date workDate)
    {
        this.workDate = workDate;
    }

    @NotNull(message = "销售ID不能为空")
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

    @NotNull(message = "电话数不能为空")
    @Min(value = 0, message = "电话数不能小于0")
    public Integer getCallCount()
    {
        return callCount;
    }

    public void setCallCount(Integer callCount)
    {
        this.callCount = callCount;
    }

    @NotNull(message = "有效电话数不能为空")
    @Min(value = 0, message = "有效电话数不能小于0")
    public Integer getValidCallCount()
    {
        return validCallCount;
    }

    public void setValidCallCount(Integer validCallCount)
    {
        this.validCallCount = validCallCount;
    }

    @NotNull(message = "意向客户数不能为空")
    @Min(value = 0, message = "意向客户数不能小于0")
    public Integer getIntentCustomerCount()
    {
        return intentCustomerCount;
    }

    public void setIntentCustomerCount(Integer intentCustomerCount)
    {
        this.intentCustomerCount = intentCustomerCount;
    }

    @NotNull(message = "面谈客户数不能为空")
    @Min(value = 0, message = "面谈客户数不能小于0")
    public Integer getVisitCount()
    {
        return visitCount;
    }

    public void setVisitCount(Integer visitCount)
    {
        this.visitCount = visitCount;
    }

    @Min(value = 0, message = "签约数不能小于0")
    public Integer getSignedCount()
    {
        return signedCount;
    }

    public void setSignedCount(Integer signedCount)
    {
        this.signedCount = signedCount;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("logId", getLogId())
            .append("workDate", getWorkDate())
            .append("userId", getUserId())
            .append("deptId", getDeptId())
            .append("callCount", getCallCount())
            .append("validCallCount", getValidCallCount())
            .append("intentCustomerCount", getIntentCustomerCount())
            .append("visitCount", getVisitCount())
            .append("signedCount", getSignedCount())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

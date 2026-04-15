package com.ruoyi.system.domain.loan;

import java.util.Date;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 客户跟进记录
 *
 * @author ruoyi
 */
public class LoanCustomerFollow extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 跟进ID */
    private Long followId;

    /** 客户ID */
    private Long customerId;

    /** 客户姓名 */
    private String customerName;

    /** 跟进方式（PHONE/MEET/OTHER） */
    private String followType;

    /** 跟进内容 */
    private String followContent;

    /** 是否有效跟进（1是 0否） */
    private String validFlag;

    /** 跟进后意向等级 */
    private String intentLevel;

    /** 下次跟进时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date nextFollowTime;

    /** 跟进人ID */
    private Long userId;

    /** 跟进人姓名 */
    private String userName;

    public Long getFollowId()
    {
        return followId;
    }

    public void setFollowId(Long followId)
    {
        this.followId = followId;
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

    public String getFollowType()
    {
        return followType;
    }

    public void setFollowType(String followType)
    {
        this.followType = followType;
    }

    @NotBlank(message = "跟进内容不能为空")
    public String getFollowContent()
    {
        return followContent;
    }

    public void setFollowContent(String followContent)
    {
        this.followContent = followContent;
    }

    public String getValidFlag()
    {
        return validFlag;
    }

    public void setValidFlag(String validFlag)
    {
        this.validFlag = validFlag;
    }

    public String getIntentLevel()
    {
        return intentLevel;
    }

    public void setIntentLevel(String intentLevel)
    {
        this.intentLevel = intentLevel;
    }

    public Date getNextFollowTime()
    {
        return nextFollowTime;
    }

    public void setNextFollowTime(Date nextFollowTime)
    {
        this.nextFollowTime = nextFollowTime;
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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("followId", getFollowId())
            .append("customerId", getCustomerId())
            .append("followType", getFollowType())
            .append("followContent", getFollowContent())
            .append("validFlag", getValidFlag())
            .append("intentLevel", getIntentLevel())
            .append("nextFollowTime", getNextFollowTime())
            .append("userId", getUserId())
            .append("userName", getUserName())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("remark", getRemark())
            .toString();
    }
}

package com.ruoyi.system.domain.loan;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 业绩分析查询对象
 *
 * @author ruoyi
 */
public class LoanPerformanceQuery extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date beginDate;

    /** 结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    /** 销售ID */
    private Long userId;

    /** 销售账号 */
    private String userName;

    /** 部门ID */
    private Long deptId;

    /** TOP数量 */
    private Integer topN;

    public Date getBeginDate()
    {
        return beginDate;
    }

    public void setBeginDate(Date beginDate)
    {
        this.beginDate = beginDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
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

    public Integer getTopN()
    {
        return topN;
    }

    public void setTopN(Integer topN)
    {
        this.topN = topN;
    }
}

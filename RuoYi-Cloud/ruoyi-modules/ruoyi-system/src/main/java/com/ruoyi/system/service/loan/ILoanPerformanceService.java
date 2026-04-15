package com.ruoyi.system.service.loan;

import java.util.List;
import com.ruoyi.system.domain.loan.LoanPerformanceQuery;
import com.ruoyi.system.domain.loan.vo.LoanDeptPerformanceVo;
import com.ruoyi.system.domain.loan.vo.LoanPerformanceOverviewVo;
import com.ruoyi.system.domain.loan.vo.LoanSalesPerformanceVo;
import com.ruoyi.system.domain.loan.vo.LoanZonePerformanceVo;

/**
 * 贷款业绩统计Service接口
 *
 * @author ruoyi
 */
public interface ILoanPerformanceService
{
    public LoanPerformanceOverviewVo getOverview(LoanPerformanceQuery query);

    public List<LoanSalesPerformanceVo> listPersonalPerformance(LoanPerformanceQuery query);

    public List<LoanDeptPerformanceVo> listDeptPerformance(LoanPerformanceQuery query);

    public List<LoanZonePerformanceVo> listZonePerformance(LoanPerformanceQuery query);

    public List<LoanSalesPerformanceVo> listRanking(LoanPerformanceQuery query);

    public List<LoanSalesPerformanceVo> listCommission(LoanPerformanceQuery query);
}

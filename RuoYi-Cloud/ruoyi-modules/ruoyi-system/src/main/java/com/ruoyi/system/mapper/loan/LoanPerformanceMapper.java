package com.ruoyi.system.mapper.loan;

import java.util.List;
import com.ruoyi.system.domain.loan.LoanPerformanceQuery;
import com.ruoyi.system.domain.loan.vo.LoanSalesPerformanceVo;

/**
 * 贷款业绩统计Mapper
 *
 * @author ruoyi
 */
public interface LoanPerformanceMapper
{
    public List<LoanSalesPerformanceVo> selectSalesPerformanceList(LoanPerformanceQuery query);
}

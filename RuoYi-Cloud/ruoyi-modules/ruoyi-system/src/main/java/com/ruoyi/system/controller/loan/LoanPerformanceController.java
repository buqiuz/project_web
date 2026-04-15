package com.ruoyi.system.controller.loan;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.system.domain.loan.LoanPerformanceQuery;
import com.ruoyi.system.domain.loan.vo.LoanDeptPerformanceVo;
import com.ruoyi.system.domain.loan.vo.LoanSalesPerformanceVo;
import com.ruoyi.system.domain.loan.vo.LoanZonePerformanceVo;
import com.ruoyi.system.service.loan.ILoanPerformanceService;

/**
 * 贷款业绩统计
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/loan/performance")
public class LoanPerformanceController extends BaseController
{
    @Autowired
    private ILoanPerformanceService performanceService;

    @RequiresPermissions("loan:performance:view")
    @GetMapping("/overview")
    public AjaxResult overview(LoanPerformanceQuery query)
    {
        return success(performanceService.getOverview(query));
    }

    @RequiresPermissions("loan:performance:view")
    @GetMapping("/personal/list")
    public AjaxResult personal(LoanPerformanceQuery query)
    {
        List<LoanSalesPerformanceVo> list = performanceService.listPersonalPerformance(query);
        return success(list);
    }

    @RequiresPermissions("loan:performance:dept")
    @GetMapping("/dept/list")
    public AjaxResult dept(LoanPerformanceQuery query)
    {
        List<LoanDeptPerformanceVo> list = performanceService.listDeptPerformance(query);
        return success(list);
    }

    @RequiresPermissions("loan:performance:zone")
    @GetMapping("/zone/list")
    public AjaxResult zone(LoanPerformanceQuery query)
    {
        List<LoanZonePerformanceVo> list = performanceService.listZonePerformance(query);
        return success(list);
    }

    @RequiresPermissions("loan:performance:rank")
    @GetMapping("/rank/list")
    public AjaxResult rank(LoanPerformanceQuery query)
    {
        List<LoanSalesPerformanceVo> list = performanceService.listRanking(query);
        return success(list);
    }

    @RequiresPermissions("loan:performance:commission")
    @GetMapping("/commission/list")
    public AjaxResult commission(LoanPerformanceQuery query)
    {
        List<LoanSalesPerformanceVo> list = performanceService.listCommission(query);
        return success(list);
    }
}

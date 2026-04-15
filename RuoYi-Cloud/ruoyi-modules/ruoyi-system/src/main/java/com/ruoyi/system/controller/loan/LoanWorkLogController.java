package com.ruoyi.system.controller.loan;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.system.api.model.LoginUser;
import com.ruoyi.system.domain.loan.LoanWorkLog;
import com.ruoyi.system.service.loan.ILoanWorkLogService;

/**
 * 工作日志管理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/loan/worklog")
public class LoanWorkLogController extends BaseController
{
    @Autowired
    private ILoanWorkLogService workLogService;

    @RequiresPermissions("loan:worklog:list")
    @GetMapping("/list")
    public TableDataInfo list(LoanWorkLog workLog)
    {
        startPage();
        List<LoanWorkLog> list = workLogService.selectLoanWorkLogList(workLog);
        return getDataTable(list);
    }

    @RequiresPermissions("loan:worklog:query")
    @GetMapping("/{logId}")
    public AjaxResult getInfo(@PathVariable Long logId)
    {
        return success(workLogService.selectLoanWorkLogById(logId));
    }

    @RequiresPermissions("loan:worklog:query")
    @GetMapping("/mine/today")
    public AjaxResult mineToday()
    {
        LoanWorkLog workLog = workLogService.selectByUserAndDate(SecurityUtils.getUserId(), DateUtils.getNowDate());
        return success(workLog);
    }

    @RequiresPermissions("loan:worklog:add")
    @Log(title = "工作日志", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody LoanWorkLog workLog)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (workLog.getUserId() == null)
        {
            workLog.setUserId(SecurityUtils.getUserId());
        }
        if (workLog.getDeptId() == null && loginUser != null && loginUser.getSysUser() != null)
        {
            workLog.setDeptId(loginUser.getSysUser().getDeptId());
        }
        if (workLog.getWorkDate() == null)
        {
            workLog.setWorkDate(new Date());
        }
        workLog.setCreateBy(SecurityUtils.getUsername());
        return toAjax(workLogService.insertLoanWorkLog(workLog));
    }

    @RequiresPermissions("loan:worklog:edit")
    @Log(title = "工作日志", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody LoanWorkLog workLog)
    {
        if (workLog.getWorkDate() == null)
        {
            workLog.setWorkDate(new Date());
        }
        workLog.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(workLogService.updateLoanWorkLog(workLog));
    }

    @RequiresPermissions("loan:worklog:remove")
    @Log(title = "工作日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{logIds}")
    public AjaxResult remove(@PathVariable Long[] logIds)
    {
        return toAjax(workLogService.deleteLoanWorkLogByIds(logIds));
    }
}

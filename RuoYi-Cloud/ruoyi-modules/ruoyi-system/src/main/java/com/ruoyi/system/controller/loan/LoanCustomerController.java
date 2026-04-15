package com.ruoyi.system.controller.loan;

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
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.system.api.model.LoginUser;
import com.ruoyi.system.domain.loan.LoanCustomer;
import com.ruoyi.system.domain.loan.LoanCustomerFollow;
import com.ruoyi.system.service.loan.ILoanCustomerService;

/**
 * 贷款客户管理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/loan/customer")
public class LoanCustomerController extends BaseController
{
    @Autowired
    private ILoanCustomerService customerService;

    @RequiresPermissions("loan:customer:list")
    @GetMapping("/list")
    public TableDataInfo list(LoanCustomer customer)
    {
        startPage();
        List<LoanCustomer> list = customerService.selectLoanCustomerList(customer);
        return getDataTable(list);
    }

    @RequiresPermissions("loan:customer:query")
    @GetMapping("/{customerId}")
    public AjaxResult getInfo(@PathVariable Long customerId)
    {
        return success(customerService.selectLoanCustomerById(customerId));
    }

    @RequiresPermissions("loan:customer:add")
    @Log(title = "贷款客户", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody LoanCustomer customer)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (customer.getUserId() == null)
        {
            customer.setUserId(SecurityUtils.getUserId());
        }
        if (customer.getDeptId() == null && loginUser != null && loginUser.getSysUser() != null)
        {
            customer.setDeptId(loginUser.getSysUser().getDeptId());
        }
        customer.setCreateBy(SecurityUtils.getUsername());
        return toAjax(customerService.insertLoanCustomer(customer));
    }

    @RequiresPermissions("loan:customer:edit")
    @Log(title = "贷款客户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody LoanCustomer customer)
    {
        customer.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(customerService.updateLoanCustomer(customer));
    }

    @RequiresPermissions("loan:customer:remove")
    @Log(title = "贷款客户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{customerIds}")
    public AjaxResult remove(@PathVariable Long[] customerIds)
    {
        return toAjax(customerService.deleteLoanCustomerByIds(customerIds));
    }

    @RequiresPermissions("loan:customer:transfer")
    @Log(title = "贷款客户转移", businessType = BusinessType.UPDATE)
    @PostMapping("/transfer")
    public AjaxResult transfer(@RequestBody LoanCustomer customer)
    {
        return toAjax(customerService.transferCustomer(customer.getCustomerId(), customer.getUserId(),
                customer.getDeptId(), customer.getTransferRemark(), SecurityUtils.getUsername()));
    }

    @RequiresPermissions("loan:customer:sea")
    @Log(title = "客户转公海", businessType = BusinessType.UPDATE)
    @PostMapping("/toSea/{customerId}")
    public AjaxResult toSea(@PathVariable Long customerId)
    {
        return toAjax(customerService.toSea(customerId, SecurityUtils.getUsername()));
    }

    @RequiresPermissions("loan:customer:claim")
    @Log(title = "领取公海客户", businessType = BusinessType.UPDATE)
    @PostMapping("/claim/{customerId}")
    public AjaxResult claim(@PathVariable Long customerId)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long deptId = loginUser != null && loginUser.getSysUser() != null ? loginUser.getSysUser().getDeptId() : null;
        return toAjax(customerService.claimSea(customerId, SecurityUtils.getUserId(), deptId, SecurityUtils.getUsername()));
    }

    @RequiresPermissions("loan:customer:release")
    @Log(title = "释放超期客户", businessType = BusinessType.UPDATE)
    @PostMapping("/releaseTimeout")
    public AjaxResult releaseTimeout(Integer releaseDays)
    {
        int rows = customerService.releaseTimeoutCustomers(releaseDays, SecurityUtils.getUsername());
        return success("已释放" + rows + "条超期客户");
    }

    @RequiresPermissions("loan:customer:query")
    @GetMapping("/follow/list")
    public TableDataInfo listFollow(Long customerId)
    {
        startPage();
        List<LoanCustomerFollow> list = customerService.selectFollowListByCustomerId(customerId);
        return getDataTable(list);
    }

    @RequiresPermissions("loan:customer:follow:add")
    @Log(title = "客户跟进", businessType = BusinessType.INSERT)
    @PostMapping("/follow")
    public AjaxResult addFollow(@Validated @RequestBody LoanCustomerFollow follow)
    {
        follow.setCreateBy(SecurityUtils.getUsername());
        follow.setUserId(SecurityUtils.getUserId());
        follow.setUserName(SecurityUtils.getUsername());
        return toAjax(customerService.insertFollow(follow));
    }

    @RequiresPermissions("loan:customer:follow:remove")
    @Log(title = "删除跟进记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/follow/{followId}")
    public AjaxResult removeFollow(@PathVariable Long followId)
    {
        return toAjax(customerService.deleteFollowById(followId));
    }
}

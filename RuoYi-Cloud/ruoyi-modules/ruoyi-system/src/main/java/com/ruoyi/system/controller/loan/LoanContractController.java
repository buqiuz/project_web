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
import com.ruoyi.system.domain.loan.LoanContract;
import com.ruoyi.system.domain.loan.LoanFeeRecord;
import com.ruoyi.system.service.loan.ILoanContractService;

/**
 * 贷款合同管理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/loan/contract")
public class LoanContractController extends BaseController
{
    @Autowired
    private ILoanContractService contractService;

    @RequiresPermissions("loan:contract:list")
    @GetMapping("/list")
    public TableDataInfo list(LoanContract contract)
    {
        startPage();
        List<LoanContract> list = contractService.selectLoanContractList(contract);
        return getDataTable(list);
    }

    @RequiresPermissions("loan:contract:query")
    @GetMapping("/{contractId}")
    public AjaxResult getInfo(@PathVariable Long contractId)
    {
        return success(contractService.selectLoanContractById(contractId));
    }

    @RequiresPermissions("loan:contract:add")
    @Log(title = "贷款合同", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody LoanContract contract)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (contract.getUserId() == null)
        {
            contract.setUserId(SecurityUtils.getUserId());
        }
        if (contract.getDeptId() == null && loginUser != null && loginUser.getSysUser() != null)
        {
            contract.setDeptId(loginUser.getSysUser().getDeptId());
        }
        contract.setCreateBy(SecurityUtils.getUsername());
        return toAjax(contractService.insertLoanContract(contract));
    }

    @RequiresPermissions("loan:contract:edit")
    @Log(title = "贷款合同", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody LoanContract contract)
    {
        contract.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(contractService.updateLoanContract(contract));
    }

    @RequiresPermissions("loan:contract:remove")
    @Log(title = "贷款合同", businessType = BusinessType.DELETE)
    @DeleteMapping("/{contractIds}")
    public AjaxResult remove(@PathVariable Long[] contractIds)
    {
        return toAjax(contractService.deleteLoanContractByIds(contractIds));
    }

    @RequiresPermissions("loan:contract:submit")
    @Log(title = "提交金融审核", businessType = BusinessType.UPDATE)
    @PostMapping("/submit/{contractId}")
    public AjaxResult submit(@PathVariable Long contractId)
    {
        return toAjax(contractService.submitFinanceReview(contractId, SecurityUtils.getUsername()));
    }

    @RequiresPermissions("loan:contract:audit")
    @Log(title = "金融审核", businessType = BusinessType.UPDATE)
    @PostMapping("/financeAudit")
    public AjaxResult financeAudit(@RequestBody LoanContract contract)
    {
        contract.setFinanceUserId(SecurityUtils.getUserId());
        contract.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(contractService.financeAudit(contract));
    }

    @RequiresPermissions("loan:contract:query")
    @GetMapping("/fee/list")
    public TableDataInfo listFee(Long contractId)
    {
        startPage();
        List<LoanFeeRecord> list = contractService.selectFeeRecordListByContractId(contractId);
        return getDataTable(list);
    }

    @RequiresPermissions("loan:contract:fee")
    @Log(title = "服务费收款", businessType = BusinessType.INSERT)
    @PostMapping("/fee")
    public AjaxResult collectFee(@RequestBody LoanFeeRecord feeRecord)
    {
        feeRecord.setCollectorUserId(SecurityUtils.getUserId());
        feeRecord.setCollectorUserName(SecurityUtils.getUsername());
        feeRecord.setCreateBy(SecurityUtils.getUsername());
        return toAjax(contractService.collectFee(feeRecord));
    }
}

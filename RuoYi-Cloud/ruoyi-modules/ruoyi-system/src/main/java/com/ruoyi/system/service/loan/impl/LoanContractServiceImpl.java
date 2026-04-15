package com.ruoyi.system.service.loan.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.datascope.annotation.DataScope;
import com.ruoyi.system.domain.loan.LoanContract;
import com.ruoyi.system.domain.loan.LoanFeeRecord;
import com.ruoyi.system.mapper.loan.LoanContractMapper;
import com.ruoyi.system.mapper.loan.LoanCustomerMapper;
import com.ruoyi.system.service.loan.ILoanContractService;

/**
 * 贷款合同服务实现
 *
 * @author ruoyi
 */
@Service
public class LoanContractServiceImpl implements ILoanContractService
{
    @Autowired
    private LoanContractMapper contractMapper;

    @Autowired
    private LoanCustomerMapper customerMapper;

    @Override
    public LoanContract selectLoanContractById(Long contractId)
    {
        return contractMapper.selectLoanContractById(contractId);
    }

    @Override
    @DataScope(deptAlias = "c", userAlias = "c")
    public List<LoanContract> selectLoanContractList(LoanContract contract)
    {
        return contractMapper.selectLoanContractList(contract);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertLoanContract(LoanContract contract)
    {
        validateUnique(contract);
        if (StringUtils.isEmpty(contract.getStatus()))
        {
            contract.setStatus("SIGNED");
        }
        if (contract.getSignTime() == null)
        {
            contract.setSignTime(new Date());
        }
        if (contract.getFirstFeePaid() == null)
        {
            contract.setFirstFeePaid(BigDecimal.ZERO);
        }
        if (contract.getSecondFeePaid() == null)
        {
            contract.setSecondFeePaid(BigDecimal.ZERO);
        }
        int rows = contractMapper.insertLoanContract(contract);
        if (rows > 0 && contract.getCustomerId() != null)
        {
            customerMapper.markCustomerSigned(contract.getCustomerId(), contract.getCreateBy(), contract.getSignTime());
        }
        return rows;
    }

    @Override
    public int updateLoanContract(LoanContract contract)
    {
        validateUnique(contract);
        return contractMapper.updateLoanContract(contract);
    }

    @Override
    public int deleteLoanContractByIds(Long[] contractIds)
    {
        return contractMapper.deleteLoanContractByIds(contractIds);
    }

    @Override
    public int submitFinanceReview(Long contractId, String operator)
    {
        return contractMapper.submitFinanceReview(contractId, operator, new Date());
    }

    @Override
    public int financeAudit(LoanContract contract)
    {
        if (contract.getContractId() == null)
        {
            throw new ServiceException("合同ID不能为空");
        }
        List<String> allowed = Arrays.asList("BANK_REVIEW", "LOANED", "REJECTED");
        if (!allowed.contains(contract.getStatus()))
        {
            throw new ServiceException("审核状态不合法");
        }
        if ("LOANED".equals(contract.getStatus()) && contract.getBankLoanTime() == null)
        {
            contract.setBankLoanTime(new Date());
        }
        if (contract.getFinanceAuditTime() == null)
        {
            contract.setFinanceAuditTime(new Date());
        }
        return contractMapper.financeAudit(contract);
    }

    @Override
    public List<LoanFeeRecord> selectFeeRecordListByContractId(Long contractId)
    {
        return contractMapper.selectFeeRecordListByContractId(contractId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int collectFee(LoanFeeRecord feeRecord)
    {
        if (feeRecord.getContractId() == null)
        {
            throw new ServiceException("合同ID不能为空");
        }
        if (feeRecord.getAmount() == null || feeRecord.getAmount().compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("收款金额必须大于0");
        }
        if (!"PRE".equals(feeRecord.getStage()) && !"POST".equals(feeRecord.getStage()))
        {
            throw new ServiceException("收款阶段不合法");
        }
        if (feeRecord.getPayTime() == null)
        {
            feeRecord.setPayTime(new Date());
        }
        if (StringUtils.isEmpty(feeRecord.getStatus()))
        {
            feeRecord.setStatus("PAID");
        }

        int rows = contractMapper.insertFeeRecord(feeRecord);
        if (rows > 0)
        {
            BigDecimal firstFeePaid = contractMapper.sumStageFee(feeRecord.getContractId(), "PRE");
            BigDecimal secondFeePaid = contractMapper.sumStageFee(feeRecord.getContractId(), "POST");
            contractMapper.updateContractPaidFee(feeRecord.getContractId(),
                    firstFeePaid == null ? BigDecimal.ZERO : firstFeePaid,
                    secondFeePaid == null ? BigDecimal.ZERO : secondFeePaid,
                    feeRecord.getCreateBy());
        }
        return rows;
    }

    private void validateUnique(LoanContract contract)
    {
        if (StringUtils.isNotEmpty(contract.getContractNo())
                && contractMapper.countContractNoUnique(contract.getContractNo(), contract.getContractId()) > 0)
        {
            throw new ServiceException("合同编号已存在");
        }
    }
}

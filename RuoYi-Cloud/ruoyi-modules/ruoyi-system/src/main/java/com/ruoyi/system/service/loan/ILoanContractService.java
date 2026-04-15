package com.ruoyi.system.service.loan;

import java.util.List;
import com.ruoyi.system.domain.loan.LoanContract;
import com.ruoyi.system.domain.loan.LoanFeeRecord;

/**
 * 贷款合同服务
 *
 * @author ruoyi
 */
public interface ILoanContractService
{
    public LoanContract selectLoanContractById(Long contractId);

    public List<LoanContract> selectLoanContractList(LoanContract contract);

    public int insertLoanContract(LoanContract contract);

    public int updateLoanContract(LoanContract contract);

    public int deleteLoanContractByIds(Long[] contractIds);

    public int submitFinanceReview(Long contractId, String operator);

    public int financeAudit(LoanContract contract);

    public List<LoanFeeRecord> selectFeeRecordListByContractId(Long contractId);

    public int collectFee(LoanFeeRecord feeRecord);
}

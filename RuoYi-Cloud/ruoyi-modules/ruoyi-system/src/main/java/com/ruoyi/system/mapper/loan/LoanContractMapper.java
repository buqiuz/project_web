package com.ruoyi.system.mapper.loan;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.loan.LoanContract;
import com.ruoyi.system.domain.loan.LoanFeeRecord;

/**
 * 贷款合同Mapper
 *
 * @author ruoyi
 */
public interface LoanContractMapper
{
    public LoanContract selectLoanContractById(Long contractId);

    public List<LoanContract> selectLoanContractList(LoanContract contract);

    public int insertLoanContract(LoanContract contract);

    public int updateLoanContract(LoanContract contract);

    public int deleteLoanContractByIds(Long[] contractIds);

    public int countContractNoUnique(@Param("contractNo") String contractNo, @Param("contractId") Long contractId);

    public int submitFinanceReview(@Param("contractId") Long contractId, @Param("operator") String operator,
            @Param("submitTime") Date submitTime);

    public int financeAudit(LoanContract contract);

    public List<LoanFeeRecord> selectFeeRecordListByContractId(Long contractId);

    public int insertFeeRecord(LoanFeeRecord feeRecord);

    public BigDecimal sumStageFee(@Param("contractId") Long contractId, @Param("stage") String stage);

    public int updateContractPaidFee(@Param("contractId") Long contractId, @Param("firstFeePaid") BigDecimal firstFeePaid,
            @Param("secondFeePaid") BigDecimal secondFeePaid, @Param("operator") String operator);
}

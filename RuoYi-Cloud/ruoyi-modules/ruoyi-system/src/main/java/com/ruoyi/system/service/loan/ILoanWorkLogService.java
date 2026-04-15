package com.ruoyi.system.service.loan;

import java.util.Date;
import java.util.List;
import com.ruoyi.system.domain.loan.LoanWorkLog;

/**
 * 工作日志Service接口
 *
 * @author ruoyi
 */
public interface ILoanWorkLogService
{
    public LoanWorkLog selectLoanWorkLogById(Long logId);

    public List<LoanWorkLog> selectLoanWorkLogList(LoanWorkLog workLog);

    public int insertLoanWorkLog(LoanWorkLog workLog);

    public int updateLoanWorkLog(LoanWorkLog workLog);

    public int deleteLoanWorkLogByIds(Long[] logIds);

    public LoanWorkLog selectByUserAndDate(Long userId, Date workDate);
}

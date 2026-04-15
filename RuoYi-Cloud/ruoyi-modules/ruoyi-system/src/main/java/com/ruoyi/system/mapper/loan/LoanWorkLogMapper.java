package com.ruoyi.system.mapper.loan;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.loan.LoanWorkLog;

/**
 * 工作日志Mapper
 *
 * @author ruoyi
 */
public interface LoanWorkLogMapper
{
    public LoanWorkLog selectLoanWorkLogById(Long logId);

    public List<LoanWorkLog> selectLoanWorkLogList(LoanWorkLog workLog);

    public int insertLoanWorkLog(LoanWorkLog workLog);

    public int updateLoanWorkLog(LoanWorkLog workLog);

    public int deleteLoanWorkLogByIds(Long[] logIds);

    public int countWorkDateUnique(@Param("userId") Long userId, @Param("workDate") Date workDate, @Param("logId") Long logId);

    public LoanWorkLog selectByUserAndDate(@Param("userId") Long userId, @Param("workDate") Date workDate);
}

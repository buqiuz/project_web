package com.ruoyi.system.service.loan.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.datascope.annotation.DataScope;
import com.ruoyi.system.domain.loan.LoanWorkLog;
import com.ruoyi.system.mapper.loan.LoanWorkLogMapper;
import com.ruoyi.system.service.loan.ILoanWorkLogService;

/**
 * 工作日志Service实现
 *
 * @author ruoyi
 */
@Service
public class LoanWorkLogServiceImpl implements ILoanWorkLogService
{
    @Autowired
    private LoanWorkLogMapper workLogMapper;

    @Override
    public LoanWorkLog selectLoanWorkLogById(Long logId)
    {
        return workLogMapper.selectLoanWorkLogById(logId);
    }

    @Override
    @DataScope(deptAlias = "l", userAlias = "l")
    public List<LoanWorkLog> selectLoanWorkLogList(LoanWorkLog workLog)
    {
        return workLogMapper.selectLoanWorkLogList(workLog);
    }

    @Override
    public int insertLoanWorkLog(LoanWorkLog workLog)
    {
        validateUnique(workLog);
        return workLogMapper.insertLoanWorkLog(workLog);
    }

    @Override
    public int updateLoanWorkLog(LoanWorkLog workLog)
    {
        validateUnique(workLog);
        return workLogMapper.updateLoanWorkLog(workLog);
    }

    @Override
    public int deleteLoanWorkLogByIds(Long[] logIds)
    {
        return workLogMapper.deleteLoanWorkLogByIds(logIds);
    }

    @Override
    public LoanWorkLog selectByUserAndDate(Long userId, Date workDate)
    {
        return workLogMapper.selectByUserAndDate(userId, workDate);
    }

    private void validateUnique(LoanWorkLog workLog)
    {
        if (workLog.getUserId() == null || workLog.getWorkDate() == null)
        {
            throw new ServiceException("销售ID和工作日期不能为空");
        }
        if (workLogMapper.countWorkDateUnique(workLog.getUserId(), workLog.getWorkDate(), workLog.getLogId()) > 0)
        {
            throw new ServiceException("该销售在当天已提交工作日志，请使用修改功能");
        }
    }
}

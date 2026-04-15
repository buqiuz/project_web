package com.ruoyi.system.service.loan.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.datascope.annotation.DataScope;
import com.ruoyi.system.domain.loan.LoanPerformanceQuery;
import com.ruoyi.system.domain.loan.vo.LoanDeptPerformanceVo;
import com.ruoyi.system.domain.loan.vo.LoanPerformanceOverviewVo;
import com.ruoyi.system.domain.loan.vo.LoanSalesPerformanceVo;
import com.ruoyi.system.domain.loan.vo.LoanZonePerformanceVo;
import com.ruoyi.system.mapper.loan.LoanPerformanceMapper;
import com.ruoyi.system.service.loan.ILoanPerformanceService;

/**
 * 贷款业绩统计Service实现
 *
 * @author ruoyi
 */
@Service
public class LoanPerformanceServiceImpl implements ILoanPerformanceService
{
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    @Autowired
    private LoanPerformanceMapper performanceMapper;

    @Override
    @DataScope(deptAlias = "u", userAlias = "u")
    public LoanPerformanceOverviewVo getOverview(LoanPerformanceQuery query)
    {
        List<LoanSalesPerformanceVo> list = loadSalesPerformance(query);
        LoanPerformanceOverviewVo vo = new LoanPerformanceOverviewVo();
        vo.setSalesUserCount((long) list.size());
        vo.setContractCount(list.stream().mapToLong(x -> safeLong(x.getContractCount())).sum());
        vo.setLoanedCount(list.stream().mapToLong(x -> safeLong(x.getLoanedCount())).sum());
        vo.setCallCount(list.stream().mapToLong(x -> safeLong(x.getCallCount())).sum());
        vo.setValidCallCount(list.stream().mapToLong(x -> safeLong(x.getValidCallCount())).sum());
        vo.setIntentCustomerCount(list.stream().mapToLong(x -> safeLong(x.getIntentCustomerCount())).sum());
        vo.setVisitCount(list.stream().mapToLong(x -> safeLong(x.getVisitCount())).sum());
        vo.setSignedCount(list.stream().mapToLong(x -> safeLong(x.getSignedCount())).sum());
        vo.setContractAmount(sumDecimal(list, LoanSalesPerformanceVo::getContractAmount));
        vo.setLoanedAmount(sumDecimal(list, LoanSalesPerformanceVo::getLoanedAmount));
        vo.setFeeIncome(sumDecimal(list, LoanSalesPerformanceVo::getFeeIncome));
        vo.setCommissionTotal(sumDecimal(list, LoanSalesPerformanceVo::getCommissionAmount));
        vo.setConversionRate(rate(vo.getSignedCount(), vo.getCallCount()));
        return vo;
    }

    @Override
    @DataScope(deptAlias = "u", userAlias = "u")
    public List<LoanSalesPerformanceVo> listPersonalPerformance(LoanPerformanceQuery query)
    {
        return loadSalesPerformance(query);
    }

    @Override
    @DataScope(deptAlias = "u", userAlias = "u")
    public List<LoanDeptPerformanceVo> listDeptPerformance(LoanPerformanceQuery query)
    {
        List<LoanSalesPerformanceVo> list = loadSalesPerformance(query);
        Map<Long, LoanDeptPerformanceVo> map = new LinkedHashMap<>();
        for (LoanSalesPerformanceVo item : list)
        {
            Long deptId = item.getDeptId() == null ? 0L : item.getDeptId();
            LoanDeptPerformanceVo vo = map.computeIfAbsent(deptId, k -> {
                LoanDeptPerformanceVo x = new LoanDeptPerformanceVo();
                x.setDeptId(item.getDeptId());
                x.setDeptName(item.getDeptName() == null ? "未分配部门" : item.getDeptName());
                x.setSalesUserCount(0L);
                x.setContractCount(0L);
                x.setLoanedCount(0L);
                x.setCallCount(0L);
                x.setValidCallCount(0L);
                x.setIntentCustomerCount(0L);
                x.setVisitCount(0L);
                x.setSignedCount(0L);
                x.setContractAmount(BigDecimal.ZERO);
                x.setLoanedAmount(BigDecimal.ZERO);
                x.setFeeIncome(BigDecimal.ZERO);
                x.setCommissionAmount(BigDecimal.ZERO);
                return x;
            });
            vo.setSalesUserCount(vo.getSalesUserCount() + 1);
            vo.setContractCount(vo.getContractCount() + safeLong(item.getContractCount()));
            vo.setLoanedCount(vo.getLoanedCount() + safeLong(item.getLoanedCount()));
            vo.setCallCount(vo.getCallCount() + safeLong(item.getCallCount()));
            vo.setValidCallCount(vo.getValidCallCount() + safeLong(item.getValidCallCount()));
            vo.setIntentCustomerCount(vo.getIntentCustomerCount() + safeLong(item.getIntentCustomerCount()));
            vo.setVisitCount(vo.getVisitCount() + safeLong(item.getVisitCount()));
            vo.setSignedCount(vo.getSignedCount() + safeLong(item.getSignedCount()));
            vo.setContractAmount(vo.getContractAmount().add(safeDecimal(item.getContractAmount())));
            vo.setLoanedAmount(vo.getLoanedAmount().add(safeDecimal(item.getLoanedAmount())));
            vo.setFeeIncome(vo.getFeeIncome().add(safeDecimal(item.getFeeIncome())));
            vo.setCommissionAmount(vo.getCommissionAmount().add(safeDecimal(item.getCommissionAmount())));
        }
        List<LoanDeptPerformanceVo> result = new ArrayList<>(map.values());
        result.forEach(x -> x.setConversionRate(rate(x.getSignedCount(), x.getCallCount())));
        result.sort(Comparator.comparing(LoanDeptPerformanceVo::getLoanedAmount, Comparator.nullsFirst(BigDecimal::compareTo)).reversed());
        return result;
    }

    @Override
    @DataScope(deptAlias = "u", userAlias = "u")
    public List<LoanZonePerformanceVo> listZonePerformance(LoanPerformanceQuery query)
    {
        List<LoanSalesPerformanceVo> list = loadSalesPerformance(query);
        Map<String, LoanZonePerformanceVo> map = new LinkedHashMap<>();
        for (LoanSalesPerformanceVo item : list)
        {
            String zoneName = resolveZoneName(item);
            LoanZonePerformanceVo vo = map.computeIfAbsent(zoneName, k -> {
                LoanZonePerformanceVo x = new LoanZonePerformanceVo();
                x.setZoneName(zoneName);
                x.setSalesUserCount(0L);
                x.setContractCount(0L);
                x.setLoanedCount(0L);
                x.setCallCount(0L);
                x.setValidCallCount(0L);
                x.setIntentCustomerCount(0L);
                x.setVisitCount(0L);
                x.setSignedCount(0L);
                x.setContractAmount(BigDecimal.ZERO);
                x.setLoanedAmount(BigDecimal.ZERO);
                x.setFeeIncome(BigDecimal.ZERO);
                x.setCommissionAmount(BigDecimal.ZERO);
                return x;
            });
            vo.setSalesUserCount(vo.getSalesUserCount() + 1);
            vo.setContractCount(vo.getContractCount() + safeLong(item.getContractCount()));
            vo.setLoanedCount(vo.getLoanedCount() + safeLong(item.getLoanedCount()));
            vo.setCallCount(vo.getCallCount() + safeLong(item.getCallCount()));
            vo.setValidCallCount(vo.getValidCallCount() + safeLong(item.getValidCallCount()));
            vo.setIntentCustomerCount(vo.getIntentCustomerCount() + safeLong(item.getIntentCustomerCount()));
            vo.setVisitCount(vo.getVisitCount() + safeLong(item.getVisitCount()));
            vo.setSignedCount(vo.getSignedCount() + safeLong(item.getSignedCount()));
            vo.setContractAmount(vo.getContractAmount().add(safeDecimal(item.getContractAmount())));
            vo.setLoanedAmount(vo.getLoanedAmount().add(safeDecimal(item.getLoanedAmount())));
            vo.setFeeIncome(vo.getFeeIncome().add(safeDecimal(item.getFeeIncome())));
            vo.setCommissionAmount(vo.getCommissionAmount().add(safeDecimal(item.getCommissionAmount())));
        }
        List<LoanZonePerformanceVo> result = new ArrayList<>(map.values());
        result.forEach(x -> x.setConversionRate(rate(x.getSignedCount(), x.getCallCount())));
        result.sort(Comparator.comparing(LoanZonePerformanceVo::getLoanedAmount, Comparator.nullsFirst(BigDecimal::compareTo)).reversed());
        return result;
    }

    @Override
    @DataScope(deptAlias = "u", userAlias = "u")
    public List<LoanSalesPerformanceVo> listRanking(LoanPerformanceQuery query)
    {
        List<LoanSalesPerformanceVo> list = loadSalesPerformance(query);
        list.sort(Comparator.comparing(LoanSalesPerformanceVo::getLoanedAmount, Comparator.nullsFirst(BigDecimal::compareTo)).reversed()
                .thenComparing(LoanSalesPerformanceVo::getContractAmount, Comparator.nullsFirst(BigDecimal::compareTo)).reversed());
        int topN = query != null && query.getTopN() != null && query.getTopN() > 0 ? query.getTopN() : 10;
        return list.stream().limit(topN).collect(Collectors.toList());
    }

    @Override
    @DataScope(deptAlias = "u", userAlias = "u")
    public List<LoanSalesPerformanceVo> listCommission(LoanPerformanceQuery query)
    {
        List<LoanSalesPerformanceVo> list = loadSalesPerformance(query);
        list.sort(Comparator.comparing(LoanSalesPerformanceVo::getCommissionAmount, Comparator.nullsFirst(BigDecimal::compareTo)).reversed());
        int topN = query != null && query.getTopN() != null && query.getTopN() > 0 ? query.getTopN() : 20;
        return list.stream().limit(topN).collect(Collectors.toList());
    }

    private List<LoanSalesPerformanceVo> loadSalesPerformance(LoanPerformanceQuery query)
    {
        LoanPerformanceQuery realQuery = normalizeQuery(query);
        List<LoanSalesPerformanceVo> list = performanceMapper.selectSalesPerformanceList(realQuery);
        list.forEach(this::fillDerivedMetrics);
        return list;
    }

    private LoanPerformanceQuery normalizeQuery(LoanPerformanceQuery query)
    {
        LoanPerformanceQuery realQuery = query == null ? new LoanPerformanceQuery() : query;
        if (realQuery.getBeginDate() == null)
        {
            Date now = DateUtils.getNowDate();
            realQuery.setBeginDate(DateUtils.parseDate(DateUtils.getDate().substring(0, 8) + "01"));
            realQuery.setEndDate(now);
        }
        else if (realQuery.getEndDate() == null)
        {
            realQuery.setEndDate(DateUtils.getNowDate());
        }
        return realQuery;
    }

    private void fillDerivedMetrics(LoanSalesPerformanceVo item)
    {
        long signed = safeLong(item.getSignedCount());
        if (signed == 0)
        {
            signed = safeLong(item.getContractCount());
            item.setSignedCount(signed);
        }
        item.setConversionRate(rate(signed, safeLong(item.getCallCount())));

        BigDecimal loanedAmount = safeDecimal(item.getLoanedAmount());
        BigDecimal feeIncome = safeDecimal(item.getFeeIncome());
        BigDecimal commissionRate = resolveCommissionRate(loanedAmount);
        BigDecimal commissionAmount = loanedAmount.multiply(commissionRate)
                .add(feeIncome.multiply(new BigDecimal("0.08")))
                .setScale(2, RoundingMode.HALF_UP);

        item.setContractAmount(safeDecimal(item.getContractAmount()));
        item.setLoanedAmount(loanedAmount);
        item.setFeeIncome(feeIncome);
        item.setCommissionRate(commissionRate.multiply(HUNDRED).setScale(2, RoundingMode.HALF_UP));
        item.setCommissionAmount(commissionAmount);
    }

    private BigDecimal resolveCommissionRate(BigDecimal loanedAmount)
    {
        if (loanedAmount.compareTo(new BigDecimal("1000000")) >= 0)
        {
            return new BigDecimal("0.030");
        }
        if (loanedAmount.compareTo(new BigDecimal("500000")) >= 0)
        {
            return new BigDecimal("0.022");
        }
        if (loanedAmount.compareTo(new BigDecimal("200000")) >= 0)
        {
            return new BigDecimal("0.018");
        }
        if (loanedAmount.compareTo(BigDecimal.ZERO) > 0)
        {
            return new BigDecimal("0.012");
        }
        return BigDecimal.ZERO;
    }

    private String resolveZoneName(LoanSalesPerformanceVo item)
    {
        Long deptId = item.getDeptId();
        String ancestors = item.getDeptAncestors() == null ? "" : item.getDeptAncestors();
        if (isFirstZone(deptId, ancestors))
        {
            return "第一战区";
        }
        if (isSecondZone(deptId, ancestors))
        {
            return "第二战区";
        }
        if (deptId != null && (deptId == 230L || deptId == 231L || deptId == 232L || deptId == 233L || ancestors.contains(",230")))
        {
            return "金融部";
        }
        return "总部";
    }

    private boolean isFirstZone(Long deptId, String ancestors)
    {
        if (deptId == null)
        {
            return false;
        }
        if (deptId == 210L || deptId == 211L || deptId == 212L || deptId == 213L || deptId == 214L)
        {
            return true;
        }
        return ancestors.contains(",210");
    }

    private boolean isSecondZone(Long deptId, String ancestors)
    {
        if (deptId == null)
        {
            return false;
        }
        if (deptId == 220L || deptId == 215L || deptId == 216L || deptId == 217L || deptId == 218L)
        {
            return true;
        }
        return ancestors.contains(",220");
    }

    private BigDecimal rate(Long numerator, Long denominator)
    {
        if (denominator == null || denominator == 0 || numerator == null || numerator == 0)
        {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(numerator)
                .multiply(HUNDRED)
                .divide(new BigDecimal(denominator), 2, RoundingMode.HALF_UP);
    }

    private long safeLong(Long value)
    {
        return value == null ? 0L : value;
    }

    private BigDecimal safeDecimal(BigDecimal value)
    {
        return value == null ? BigDecimal.ZERO : value;
    }

    private interface DecimalGetter
    {
        BigDecimal get(LoanSalesPerformanceVo vo);
    }

    private BigDecimal sumDecimal(List<LoanSalesPerformanceVo> list, DecimalGetter getter)
    {
        BigDecimal sum = BigDecimal.ZERO;
        for (LoanSalesPerformanceVo item : list)
        {
            sum = sum.add(safeDecimal(getter.get(item)));
        }
        return sum;
    }
}

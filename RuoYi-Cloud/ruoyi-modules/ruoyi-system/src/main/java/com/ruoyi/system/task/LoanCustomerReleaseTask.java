package com.ruoyi.system.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.loan.ILoanCustomerService;

/**
 * 客户超期释放任务
 *
 * @author ruoyi
 */
@Component
public class LoanCustomerReleaseTask
{
    private static final Logger log = LoggerFactory.getLogger(LoanCustomerReleaseTask.class);

    @Autowired
    private ILoanCustomerService customerService;

    @Autowired
    private ISysConfigService configService;

    /**
     * 默认每日凌晨2点执行，可通过配置 loan.customer.release-cron 覆盖。
     */
    @Scheduled(cron = "${loan.customer.release-cron:0 0 2 * * ?}")
    public void releaseTimeoutCustomers()
    {
        Integer releaseDays = Convert.toInt(configService.selectConfigByKey("loan.customer.releaseDays"), 7);
        int rows = customerService.releaseTimeoutCustomers(releaseDays, "schedule");
        log.info("客户超期释放任务执行完成，释放{}条客户，规则天数:{}", rows, releaseDays);
    }
}

package io.choerodon.agile.infra.scheduled;

import io.choerodon.agile.infra.cache.InstanceCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author shinan.chen
 * @since 2018/11/16
 */
@Component
public class ScheduledTask {

    @Autowired
    private InstanceCache instanceCache;

    /**
     * 每天凌晨清理所有实例数据
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanAllInstances() {
        instanceCache.cleanAllInstances();
    }

    /**
     * 每个小时清理一次状态机实例
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void cleanInstanceTask() {
        instanceCache.cleanInstanceTask();
    }
}

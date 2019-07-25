package io.choerodon.agile.infra.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shinan.chen
 * @since 2018/12/3
 */
@Component
public class InstanceCache {
    private static final Logger logger = LoggerFactory.getLogger(InstanceCache.class);
    /**
     * 状态机id -> 状态机构建器
     */
    private static Map<Long, StateMachineBuilder.Builder<String, String>> builderMap = new ConcurrentHashMap<>();

    /**
     * 状态机id -> 状态机实例key的list
     */
    private static Map<Long, Set<String>> stateMachineMap = new ConcurrentHashMap<>();

    /**
     * key【服务名:状态机id:实例id】 -> 状态机实例
     */
    private static Map<String, StateMachine<String, String>> instanceMap = new ConcurrentHashMap<>();

    /**
     * 实例存活计数，初始创建value = 2，每次 get + 1，定时任务每次全部实例-1，并清除value为0的实例，
     */
    private static Map<String, Integer> aliveMap = new ConcurrentHashMap<>();

    /**
     * 清除单个实例
     */
    public void cleanInstance(String key) {
        instanceMap.remove(key);
    }

    /**
     * 清除某个状态机的所有实例
     */
    public void cleanStateMachine(Long stateMachineId) {
        builderMap.remove(stateMachineId);
        Set<String> instanceKeys = stateMachineMap.get(stateMachineId);
        if (instanceKeys != null) {
            stateMachineMap.remove(stateMachineId);
            instanceKeys.forEach(key -> instanceMap.remove(key));
        }
    }

    /**
     * 缓存状态机构建器
     */
    public void putBuilder(Long stateMachineId, StateMachineBuilder.Builder<String, String> builder) {
        builderMap.put(stateMachineId, builder);
    }

    /**
     * 缓存状态机实例
     */
    public void putInstance(String serviceCode, Long stateMachineId, Long instanceId, StateMachine<String, String> stateMachineInstance) {
        String key = serviceCode + ":" + stateMachineId + ":" + instanceId;
        instanceMap.put(key, stateMachineInstance);
        aliveMap.put(key, 2);
        Set<String> instanceKeys = stateMachineMap.get(stateMachineId);
        if (instanceKeys != null) {
            instanceKeys.add(key);
        } else {
            instanceKeys = new HashSet<>();
            instanceKeys.add(key);
            stateMachineMap.put(stateMachineId, instanceKeys);
        }
    }

    /**
     * 获取状态机构建器
     */
    public StateMachineBuilder.Builder<String, String> getBuilder(Long stateMachineId) {
        return builderMap.get(stateMachineId);
    }

    /**
     * 获取单个实例
     */
    public StateMachine<String, String> getInstance(String serviceCode, Long stateMachineId, Long instanceId) {
        String key = serviceCode + ":" + stateMachineId + ":" + instanceId;
        Integer aliveCount = aliveMap.get(key);
        if (aliveCount != null && aliveCount != 0) {
            aliveMap.put(key, aliveCount + 1);
        }
        return instanceMap.get(key);
    }

    /**
     * 每天凌晨定时清理所有实例数据
     */
    public void cleanAllInstances() {
        logger.info("每天凌晨清理状态机实例：清理构建器{}个，状态机实例{}个", builderMap.size(), instanceMap.size());
        builderMap.clear();
        stateMachineMap.clear();
        instanceMap.clear();
        aliveMap.clear();
    }

    /**
     * 定时清理状态机实例，每次进行判断所有实例的aliveCount-1，
     * 当aliveCount为0时，则清除该实例，该实例在每次使用时
     * aliveCount+1，使用次数越多存活时间越久
     */
    public void cleanInstanceTask() {
        int alive = 0;
        int clean = 0;
        for (Map.Entry<String, Integer> entry : aliveMap.entrySet()) {
            String key = entry.getKey();
            Integer aliveCount = entry.getValue();
            aliveCount = aliveCount - 1;
            if (aliveCount == 0) {
                //清理实例
                cleanInstance(key);
                aliveMap.remove(key);
                clean++;
            } else {
                aliveMap.put(key, aliveCount);
                alive++;
            }
        }
        logger.info("定时清理状态机实例：清理实例{}个，存活实例{}个", clean, alive);
    }
}

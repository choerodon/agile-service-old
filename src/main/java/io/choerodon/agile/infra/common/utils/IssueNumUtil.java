package io.choerodon.agile.infra.common.utils;

import io.choerodon.agile.infra.mapper.ProjectInfoMapper;
import io.choerodon.core.convertor.ApplicationContextHelper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

/**
 * createIssue获取编号，用AtomicLong替代synchronized，提升创建速度
 *
 * @author shinan.chen
 * @since 2019/5/22
 */

public class IssueNumUtil {
    public static final String REDIS_ISSUE_NUM_FLAG = "issueNum:";

    public static Long getNewIssueNum(Long projeceId) {
        RedisTemplate<String, Object> redisTemplate = ApplicationContextHelper.getSpringFactory().getBean(RedisUtil.class).getRedisTemplate();
        RedisAtomicLong atomicLong = new RedisAtomicLong(REDIS_ISSUE_NUM_FLAG + projeceId, redisTemplate.getConnectionFactory());
        if (atomicLong.get() == 0) {
            synchronized (IssueNumUtil.class) {
                atomicLong = new RedisAtomicLong(REDIS_ISSUE_NUM_FLAG + projeceId, redisTemplate.getConnectionFactory());
                if (atomicLong.get() == 0) {
                    ProjectInfoMapper projectInfoMapper = ApplicationContextHelper.getSpringFactory().getBean(ProjectInfoMapper.class);
                    Long issueNum = projectInfoMapper.queryByProjectId(projeceId).getIssueMaxNum();
                    atomicLong = new RedisAtomicLong(REDIS_ISSUE_NUM_FLAG + projeceId, redisTemplate.getConnectionFactory(), issueNum);
                }
            }
        }
        return atomicLong.incrementAndGet();
    }
}

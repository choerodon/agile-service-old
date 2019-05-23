package io.choerodon.agile.infra.common.utils;

import io.choerodon.agile.infra.mapper.ProjectInfoMapper;
import io.choerodon.core.convertor.ApplicationContextHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * createIssue获取编号，用AtomicLong替代synchronized，提升创建速度
 *
 * @author shinan.chen
 * @since 2019/5/22
 */
public class IssueNumUtil {
    private static final Map<Long, AtomicLong> issueNumMap = new HashMap<>();

    public static Long getNewIssueNum(Long projeceId) {
        AtomicLong atomicLong = issueNumMap.get(projeceId);
        if (atomicLong == null) {
            synchronized (IssueNumUtil.class) {
                atomicLong = issueNumMap.get(projeceId);
                if (atomicLong == null) {
                    ProjectInfoMapper projectInfoMapper = ApplicationContextHelper.getSpringFactory().getBean(ProjectInfoMapper.class);
                    Long issueNum = projectInfoMapper.queryByProjectId(projeceId).getIssueMaxNum();
                    atomicLong = new AtomicLong(issueNum);
                    issueNumMap.put(projeceId, atomicLong);
                }
            }
        }
        return atomicLong.incrementAndGet();
    }
}

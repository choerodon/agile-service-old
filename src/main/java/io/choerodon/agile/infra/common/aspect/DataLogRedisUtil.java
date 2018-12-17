package io.choerodon.agile.infra.common.aspect;

import io.choerodon.agile.infra.common.utils.RedisUtil;
import io.choerodon.agile.infra.dataobject.IssueDO;
import io.choerodon.agile.infra.mapper.IssueMapper;
import io.choerodon.agile.infra.mapper.SprintMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/11/26
 */
@Component
public class DataLogRedisUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataLogAspect.class);

    private static final String AGILE = "Agile:";
    private static final String COLON = ":";
    private static final String POINTER = "*";
    private static final String VELOCITY_CHART = AGILE + "VelocityChart";
    private static final String FIELD_STATUS = "status";
    private static final String FIELD_RESOLUTION = "resolution";
    private static final String BURN_DOWN_COORDINATE_BY_TYPE = AGILE + "BurnDownCoordinateByType";
    private static final String BURN_DOWN_COORDINATE = AGILE + "BurnDownCoordinate";
    private static final String CUMULATIVE_FLOW_DIAGRAM = AGILE + "CumulativeFlowDiagram";
    private static final String PIE_CHART = AGILE + "PieChart";
    private static final String PIE_CHART_EPIC = "epic";
    private static final String EPIC_CHART = AGILE + "EpicChart";
    private static final String VERSION_CHART = AGILE + "VersionChart";
    private static final String REDIS_TASK_EXECUTOR = "redisTaskExecutor";

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SprintMapper sprintMapper;
    @Autowired
    private IssueMapper issueMapper;

    @Async(REDIS_TASK_EXECUTOR)
    public void handleBatchDeleteRedisCache(List<IssueDO> issueDOS, Long projectId) {
        issueDOS.parallelStream().forEach(issueDO -> {
            deleteBurnDownCache(issueDO.getSprintId(), projectId, issueDO.getIssueId(), POINTER);
            deleteEpicChartCache(issueDO.getEpicId(), projectId, issueDO.getIssueId(), POINTER);
            deleteVersionCache(projectId, issueDO.getIssueId(), POINTER);
        });
        redisUtil.deleteRedisCache(new String[]{VELOCITY_CHART + projectId + COLON + POINTER,
//                PIE_CHART + projectId + COLON + FIELD_STATUS,
//                PIE_CHART + projectId + COLON + FIELD_RESOLUTION,
                BURN_DOWN_COORDINATE_BY_TYPE + projectId + COLON + POINTER
        });
    }

    public void deleteBurnDownCache(Long sprintId, Long projectId, Long issueId, String type) {
        if (sprintId == null && issueId != null) {
            sprintId = sprintMapper.queryNotCloseSprintIdByIssueId(issueId, projectId);
        }
        if (sprintId != null) {
            redisUtil.deleteRedisCache(new String[]{BURN_DOWN_COORDINATE + projectId + COLON + sprintId + COLON + type});
        }
    }

    public void deleteEpicChartCache(Long epicId, Long projectId, Long issueId, String type) {
        if (epicId == null && issueId != null) {
            epicId = issueMapper.selectByPrimaryKey(issueId).getEpicId();
        }
        if (epicId != null && epicId != 0) {
            redisUtil.deleteRedisCache(new String[]{EPIC_CHART + projectId + COLON + epicId + COLON + type
//                    ,PIE_CHART + projectId + COLON + PIE_CHART_EPIC
            });
        }
    }

    public void deleteVersionCache(Long projectId, Long issueId, String type) {
        List<Long> versionId = issueMapper.queryVersionIdsByIssueId(issueId, projectId);
        versionId.forEach(id -> redisUtil.deleteRedisCache(new String[]{VERSION_CHART + projectId + COLON + id + COLON + type}));
    }

    @Async(REDIS_TASK_EXECUTOR)
    public void handleDeleteRedisByDeleteIssue(Long projectId) {
        redisUtil.deleteRedisCache(new String[]{BURN_DOWN_COORDINATE + projectId + COLON + POINTER,
                CUMULATIVE_FLOW_DIAGRAM + projectId + COLON + POINTER,
                VELOCITY_CHART + projectId + COLON + POINTER,
//                PIE_CHART + projectId + COLON + POINTER,
                BURN_DOWN_COORDINATE_BY_TYPE + projectId + COLON + POINTER
        });
    }

    @Async(REDIS_TASK_EXECUTOR)
    public void handleBatchDeleteRedisCacheByChangeStatusId(List<IssueDO> issueDOS, Long projectId) {
        redisUtil.deleteRedisCache(new String[]{CUMULATIVE_FLOW_DIAGRAM + projectId + COLON + POINTER});
        handleBatchDeleteRedisCache(issueDOS,projectId);
    }
}

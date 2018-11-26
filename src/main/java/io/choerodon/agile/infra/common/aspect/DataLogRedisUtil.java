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
    private static final String VELOCITY_CHART = AGILE + "VelocityChart";
    private static final String FIELD_STATUS = "status";
    private static final String FIELD_RESOLUTION = "resolution";
    private static final String BURN_DOWN_COORDINATE_BY_TYPE = AGILE + "BurnDownCoordinateByType";
    private static final String PIECHART = AGILE + "PieChart";
    private static final String EPIC_CHART = AGILE + "EpicChart";
    private static final String VERSION_CHART = AGILE + "VersionChart";

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SprintMapper sprintMapper;
    @Autowired
    private IssueMapper issueMapper;

    @Async("redisTaskExecutor")
    public void handleBatchDeleteRedisCache(List<IssueDO> issueDOS, Long projectId) {
        LOGGER.info("handleBatchDeleteRedisCache{}", Thread.currentThread().toString());
        issueDOS.parallelStream().forEach(issueDO -> {
            deleteBurnDownCache(issueDO.getSprintId(), projectId, issueDO.getIssueId(), "*");
            deleteEpicChartCache(issueDO.getEpicId(), projectId, issueDO.getIssueId(), "*");
            deleteVersionCache(projectId, issueDO.getIssueId(), "*");
        });
        redisUtil.deleteRedisCache(new String[]{VELOCITY_CHART + projectId + ':' + "*",
                PIECHART + projectId + ':' + FIELD_STATUS,
                PIECHART + projectId + ':' + FIELD_RESOLUTION,
                BURN_DOWN_COORDINATE_BY_TYPE + projectId + ":" + "*"
        });
    }

    public void deleteBurnDownCache(Long sprintId, Long projectId, Long issueId, String type) {
        if (sprintId == null && issueId != null) {
            sprintId = sprintMapper.queryNotCloseSprintIdByIssueId(issueId, projectId);
        }
        if (sprintId != null) {
            redisUtil.deleteRedisCache(new String[]{"Agile:BurnDownCoordinate" + projectId + ':' + sprintId + ':' + type});
        }
    }

    public void deleteEpicChartCache(Long epicId, Long projectId, Long issueId, String type) {
        if (epicId == null && issueId != null) {
            epicId = issueMapper.selectByPrimaryKey(issueId).getEpicId();
        }
        if (epicId != null && epicId != 0) {
            redisUtil.deleteRedisCache(new String[]{EPIC_CHART + projectId + ":" + epicId + ":" + type,
                    PIECHART + projectId + ':' + "epic"});
        }
    }

    public void deleteVersionCache(Long projectId, Long issueId, String type) {
        List<Long> versionId = issueMapper.queryVersionIdsByIssueId(issueId, projectId);
        versionId.forEach(id -> redisUtil.deleteRedisCache(new String[]{VERSION_CHART + projectId + ':' + id + ":" + type}));
    }
}

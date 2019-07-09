package io.choerodon.agile.infra.common.aspect;

import io.choerodon.agile.domain.agile.entity.IssueE;
import io.choerodon.agile.domain.agile.entity.SprintE;
import io.choerodon.agile.infra.common.utils.RedisUtil;
import io.choerodon.agile.infra.dataobject.IssueDTO;
import io.choerodon.agile.infra.dataobject.IssueStatusDTO;
import io.choerodon.agile.infra.dataobject.ProductVersionDO;
import io.choerodon.agile.infra.dataobject.SprintDTO;
import io.choerodon.agile.infra.mapper.IssueMapper;
import io.choerodon.agile.infra.mapper.SprintMapper;
import io.choerodon.agile.infra.mapper.VersionIssueRelMapper;
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

    private static final String AGILE = "Agile::";
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
    private static final String EPIC = "Epic";
    private static final String STORY_POINT = "story_point";
    private static final String STORY_POINTS_FIELD = "storyPoints";
    private static final String REMAIN_TIME = "remain_time";
    private static final String REMAINING_ESTIMATED_TIME = "remainingEstimatedTime";
    private static final String VERSION = "Version";
    private static final String FIX_VERSION_CACHE = "fixVersion";
    private static final String SPRINT = "sprint";
    private static final String ISSUE_TYPE = "issueType";
    private static final String EPIC_FIELD = "epic";


    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SprintMapper sprintMapper;
    @Autowired
    private IssueMapper issueMapper;
    @Autowired
    private VersionIssueRelMapper versionIssueRelMapper;

    @Async(REDIS_TASK_EXECUTOR)
    public void handleBatchDeleteRedisCache(List<IssueDTO> issueDTOS, Long projectId) {
        issueDTOS.parallelStream().forEach(issueDO -> {
            deleteBurnDownCache(issueDO.getSprintId(), projectId, issueDO.getIssueId(), POINTER);
            deleteEpicChartCache(issueDO.getEpicId(), projectId, issueDO.getIssueId(), POINTER);
            deleteVersionCache(projectId, issueDO.getIssueId(), POINTER);
        });
        redisUtil.deleteRedisCache(new String[]{VELOCITY_CHART + projectId + COLON + POINTER,
                PIE_CHART + projectId + COLON + FIELD_STATUS + POINTER,
                PIE_CHART + projectId + COLON + FIELD_RESOLUTION + POINTER,
                BURN_DOWN_COORDINATE_BY_TYPE + projectId + COLON + POINTER
        });
    }

    public void deleteBurnDownCache(Long sprintId, Long projectId, Long issueId, String type) {
        if (sprintId == null && issueId != null) {
            sprintId = sprintMapper.queryNotCloseSprintIdByIssueId(issueId, projectId);
        }
        if (sprintId != null && sprintId != 0L) {
            redisUtil.deleteRedisCache(new String[]{BURN_DOWN_COORDINATE + projectId + COLON + sprintId + COLON + type});
        }
    }

    public void deleteEpicChartCache(Long epicId, Long projectId, Long issueId, String type) {
        if (epicId == null && issueId != null) {
            epicId = issueMapper.selectByPrimaryKey(issueId).getEpicId();
        }
        if (epicId != null && epicId != 0) {
            redisUtil.deleteRedisCache(new String[]{EPIC_CHART + projectId + COLON + epicId + COLON + type
                    , PIE_CHART + projectId + COLON + PIE_CHART_EPIC + POINTER
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
                PIE_CHART + projectId + COLON + POINTER,
                BURN_DOWN_COORDINATE_BY_TYPE + projectId + COLON + POINTER
        });
    }

    @Async(REDIS_TASK_EXECUTOR)
    public void handleBatchDeleteRedisCacheByChangeStatusId(List<IssueDTO> issueDTOS, Long projectId) {
        redisUtil.deleteRedisCache(new String[]{CUMULATIVE_FLOW_DIAGRAM + projectId + COLON + POINTER,
                PIE_CHART + projectId + COLON + FIELD_STATUS + POINTER});
        handleBatchDeleteRedisCache(issueDTOS, projectId);
    }

    @Async(REDIS_TASK_EXECUTOR)
    public void deleteByHandleStatus(IssueE issueE, IssueDTO originIssueDTO, Boolean condition) {
        redisUtil.deleteRedisCache(new String[]{CUMULATIVE_FLOW_DIAGRAM + originIssueDTO.getProjectId() + COLON + POINTER
                , PIE_CHART + originIssueDTO.getProjectId() + COLON + FIELD_STATUS + POINTER});
        if (condition) {
            deleteEpicChartCache(issueE.getEpicId(), originIssueDTO.getProjectId(), issueE.getIssueId(), POINTER);
            deleteBurnDownCoordinateByTypeEpic(issueE.getEpicId(), originIssueDTO.getProjectId(), issueE.getIssueId());
            deleteBurnDownCoordinateByVersion(originIssueDTO.getProjectId(), issueE.getIssueId());
            deleteBurnDownCache(issueE.getSprintId(), originIssueDTO.getProjectId(), issueE.getIssueId(), POINTER);
            redisUtil.deleteRedisCache(new String[]{VELOCITY_CHART + originIssueDTO.getProjectId() + COLON + POINTER});
            deleteVersionCache(originIssueDTO.getProjectId(), originIssueDTO.getIssueId(), POINTER);
        }
    }

    public void deleteBurnDownCoordinateByVersion(Long projectId, Long issueId) {
        List<Long> versionIds = versionIssueRelMapper.queryVersionIdsByIssueId(issueId, projectId);
        deleteByBatchDeleteVersionByVersionIds(projectId, versionIds);
    }

    public void deleteBurnDownCoordinateByTypeEpic(Long epicId, Long projectId, Long issueId) {
        if (epicId == null && issueId != null) {
            epicId = issueMapper.selectByPrimaryKey(issueId).getEpicId();
        }
        if (epicId != null && epicId != 0) {
            redisUtil.deleteRedisCache(new String[]{
                    BURN_DOWN_COORDINATE_BY_TYPE + projectId + COLON + EPIC + COLON + epicId
            });
        }
    }

    @Async(REDIS_TASK_EXECUTOR)
    public void deleteByHandleStoryPoints(IssueE issueE, IssueDTO originIssueDTO) {
        deleteEpicChartCache(issueE.getEpicId(), originIssueDTO.getProjectId(), issueE.getIssueId(), STORY_POINT);
        deleteBurnDownCache(issueE.getSprintId(), originIssueDTO.getProjectId(), issueE.getIssueId(), STORY_POINTS_FIELD);
        redisUtil.deleteRedisCache(new String[]{VELOCITY_CHART + originIssueDTO.getProjectId() + COLON + STORY_POINT,
                BURN_DOWN_COORDINATE_BY_TYPE + originIssueDTO.getProjectId() + COLON + POINTER});
        deleteVersionCache(originIssueDTO.getProjectId(), originIssueDTO.getIssueId(), STORY_POINT);
    }

    @Async(REDIS_TASK_EXECUTOR)
    public void deleteByHandleCalculateRemainData(IssueE issueE, IssueDTO originIssueDTO) {
        deleteEpicChartCache(issueE.getEpicId(), originIssueDTO.getProjectId(), issueE.getIssueId(), REMAIN_TIME);
        deleteBurnDownCoordinateByTypeEpic(issueE.getEpicId(), originIssueDTO.getProjectId(), issueE.getIssueId());
        deleteBurnDownCache(issueE.getSprintId(), originIssueDTO.getProjectId(), issueE.getIssueId(), REMAINING_ESTIMATED_TIME);
        redisUtil.deleteRedisCache(new String[]{VELOCITY_CHART + originIssueDTO.getProjectId() + COLON + REMAIN_TIME});
        deleteVersionCache(originIssueDTO.getProjectId(), originIssueDTO.getIssueId(), REMAIN_TIME);
        deleteVersionCache(issueE.getProjectId(), issueE.getIssueId(), POINTER);
    }

    @Async(REDIS_TASK_EXECUTOR)
    public void deleteByHandleType(IssueE issueE, IssueDTO originIssueDTO) {
        deleteBurnDownCache(issueE.getSprintId(), originIssueDTO.getProjectId(), issueE.getIssueId(), POINTER);
        deleteEpicChartCache(issueE.getEpicId(), originIssueDTO.getProjectId(), issueE.getIssueId(), POINTER);
        deleteBurnDownCoordinateByTypeEpic(issueE.getEpicId(), originIssueDTO.getProjectId(), issueE.getIssueId());
        deleteVersionCache(issueE.getProjectId(), issueE.getIssueId(), POINTER);
        redisUtil.deleteRedisCache(new String[]{CUMULATIVE_FLOW_DIAGRAM + originIssueDTO.getProjectId() + COLON + POINTER,
                VELOCITY_CHART + originIssueDTO.getProjectId() + COLON + POINTER,
                PIE_CHART + issueE.getProjectId() + COLON + ISSUE_TYPE + POINTER,
                PIE_CHART + issueE.getProjectId() + COLON + FIELD_STATUS + POINTER,
                PIE_CHART + issueE.getProjectId() + COLON + EPIC_FIELD + POINTER
        });
    }

    @Async(REDIS_TASK_EXECUTOR)
    public void deleteByHandleIssueCreateDataLog(IssueE issueE, Boolean condition) {
        redisUtil.deleteRedisCache(new String[]{PIE_CHART + issueE.getProjectId() + COLON + POINTER});
        if (condition) {
            deleteBurnDownCache(issueE.getSprintId(), issueE.getProjectId(), issueE.getIssueId(), POINTER);
            deleteEpicChartCache(issueE.getEpicId(), issueE.getProjectId(), issueE.getIssueId(), POINTER);
            redisUtil.deleteRedisCache(new String[]{VELOCITY_CHART + issueE.getProjectId() + COLON + POINTER,
                    BURN_DOWN_COORDINATE_BY_TYPE + issueE.getProjectId() + COLON + POINTER});
            deleteVersionCache(issueE.getProjectId(), issueE.getIssueId(), POINTER);
        }
    }

    @Async(REDIS_TASK_EXECUTOR)
    public void deleteByHandleSprintDataLog(SprintDTO sprintDTO) {
        deleteBurnDownCache(sprintDTO.getSprintId(), sprintDTO.getProjectId(), null, POINTER);
        redisUtil.deleteRedisCache(new String[]{VELOCITY_CHART + sprintDTO.getProjectId() + COLON + POINTER,
                PIE_CHART + sprintDTO.getProjectId() + COLON + SPRINT + POINTER,
                BURN_DOWN_COORDINATE_BY_TYPE + sprintDTO.getProjectId() + COLON + POINTER});
    }

    @Async(REDIS_TASK_EXECUTOR)
    public void deleteByBatchDeleteVersionByVersionIds(Long projectId, List<Long> versionIds) {
        if (versionIds != null && !versionIds.isEmpty()) {
            versionIds.forEach(versionId -> redisUtil.deleteRedisCache(new String[]{VERSION_CHART + projectId + COLON + versionId + COLON + POINTER,
                    BURN_DOWN_COORDINATE_BY_TYPE + projectId + COLON + VERSION + COLON + versionId
            }));
        }
    }

    @Async(REDIS_TASK_EXECUTOR)
    public void deleteByBatchRemoveSprintToTarget(Long targetSprintId, Long projectId, Long originSprintId) {
        deleteBurnDownCache(targetSprintId, projectId, null, POINTER);
        deleteBurnDownCache(originSprintId, projectId, null, POINTER);
        redisUtil.deleteRedisCache(new String[]{VELOCITY_CHART + projectId + COLON + POINTER,
                PIE_CHART + projectId + COLON + SPRINT + POINTER,
                BURN_DOWN_COORDINATE_BY_TYPE + projectId + COLON + POINTER
        });
    }

    @Async(REDIS_TASK_EXECUTOR)
    public void deleteByHandleBatchDeleteVersion(Long projectId, Long versionId) {
        redisUtil.deleteRedisCache(new String[]{PIE_CHART + projectId + COLON + FIX_VERSION_CACHE + POINTER});
        if (versionId != null) {
            redisUtil.deleteRedisCache(new String[]{VERSION_CHART + projectId + COLON + versionId + COLON + POINTER,
                    BURN_DOWN_COORDINATE_BY_TYPE + projectId + COLON + VERSION + COLON + versionId
            });
        }
    }

    @Async(REDIS_TASK_EXECUTOR)
    public void deleteByBatchDeleteVersionDataLog(Long projectId, List<ProductVersionDO> productVersionDOS) {
        redisUtil.deleteRedisCache(new String[]{PIE_CHART + projectId + COLON + FIX_VERSION_CACHE + POINTER});
        productVersionDOS.parallelStream().forEach(productVersionDO -> redisUtil.deleteRedisCache(new String[]{VERSION_CHART + productVersionDO.getProjectId() + COLON + productVersionDO.getVersionId() + COLON + POINTER,
                BURN_DOWN_COORDINATE_BY_TYPE + productVersionDO.getProjectId() + COLON + VERSION + COLON + productVersionDO.getVersionId()
        }));

    }

    @Async(REDIS_TASK_EXECUTOR)
    public void deleteByDataLogCreateEpicId(Long projectId, Long issueId) {
        redisUtil.deleteRedisCache(new String[]{EPIC_CHART + projectId + COLON + issueId + COLON + POINTER
                , PIE_CHART + projectId + COLON + EPIC_FIELD + POINTER
        });
    }

    @Async(REDIS_TASK_EXECUTOR)
    public void deleteByUpdateSprint(SprintE sprintE) {
        redisUtil.deleteRedisCache(new String[]{
                BURN_DOWN_COORDINATE + sprintE.getProjectId() + COLON + sprintE.getSprintId() + COLON + POINTER,
                BURN_DOWN_COORDINATE_BY_TYPE + sprintE.getProjectId() + COLON + POINTER,
                VELOCITY_CHART + sprintE.getProjectId() + COLON + POINTER
                , PIE_CHART + sprintE.getProjectId() + COLON + SPRINT + POINTER
        });
    }

    @Async(REDIS_TASK_EXECUTOR)
    public void deleteByUpdateIssueStatus(IssueStatusDTO issueStatusDTO) {
        redisUtil.deleteRedisCache(new String[]{BURN_DOWN_COORDINATE + issueStatusDTO.getProjectId() + COLON + POINTER,
                CUMULATIVE_FLOW_DIAGRAM + issueStatusDTO.getProjectId() + COLON + POINTER,
                VELOCITY_CHART + issueStatusDTO.getProjectId() + COLON + POINTER,
                EPIC_CHART + issueStatusDTO.getProjectId() + COLON + POINTER
                , PIE_CHART + issueStatusDTO.getProjectId() + COLON + FIELD_STATUS + POINTER,
                PIE_CHART + issueStatusDTO.getProjectId() + COLON + FIELD_RESOLUTION + POINTER
        });
    }

    @Async(REDIS_TASK_EXECUTOR)
    public void deleteByDeleteIssueInfo(Long projectId) {
        redisUtil.deleteRedisCache(new String[]{BURN_DOWN_COORDINATE + projectId + COLON + POINTER,
                CUMULATIVE_FLOW_DIAGRAM + projectId + COLON + POINTER,
                VELOCITY_CHART + projectId + COLON + POINTER,
                PIE_CHART + projectId + COLON + POINTER,
                BURN_DOWN_COORDINATE_BY_TYPE + projectId + COLON + POINTER
        });
    }

    @Async(REDIS_TASK_EXECUTOR)
    public void deleteByCreateSprint(SprintE sprintE) {
        redisUtil.deleteRedisCache(new String[]{PIE_CHART + sprintE.getProjectId() + COLON + SPRINT + POINTER,
                VELOCITY_CHART + sprintE.getSprintId() + COLON + POINTER});
    }
}

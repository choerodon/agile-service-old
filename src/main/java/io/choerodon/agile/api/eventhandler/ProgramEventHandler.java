package io.choerodon.agile.api.eventhandler;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.vo.ArtDTO;
import io.choerodon.agile.app.service.ArtService;
import io.choerodon.agile.app.service.SprintService;
import io.choerodon.agile.api.vo.event.ProjectEvent;
import io.choerodon.agile.api.vo.event.ProjectRelationshipInsertPayload;
import io.choerodon.agile.infra.dataobject.ArtDO;
import io.choerodon.agile.infra.mapper.ArtMapper;
import io.choerodon.asgard.saga.annotation.SagaTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/14.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class ProgramEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProgramEventHandler.class);

    private static final String JOIN_PROGRAM_EVENT = "join-program-event";
    private static final String DELETE_PROGRAM_EVENT = "delete-program-event";
    private static final String IAM_ADD_PROJECT_RELATIONSHIP = "iam-add-project-relationships";
    private static final String IAM_DELETE_PROJECT_RELATIONSHIP = "iam-delete-project-relationships";
    private static final String ADD = "add";
    private static final String UPDATE = "update";
    private static final String DELETE = "delete";
    private static final String PROJECT_ENABLE_PROGRAM = "enable-project-program";
    private static final String PROJECT_ENABLE = "iam-enable-project";
    private static final String PROJECT_DISABLE_PROGRAM = "disable-project-program";
    private static final String PROJECT_DISABLE = "iam-disable-project";
    private static final String PROJECT_CATEGORY_PROGRAM = "PROGRAM";
    private static final String PROJECT_CATEGORY_AGILE = "AGILE";


    @Autowired
    private SprintService sprintService;

    @Autowired
    private ArtService artService;

    @Autowired
    private ArtMapper artMapper;

    @SagaTask(code = JOIN_PROGRAM_EVENT,
            description = "项目加入项目群或更新项目与项目群关系消费event",
            sagaCode = IAM_ADD_PROJECT_RELATIONSHIP,
            seq = 3)
    public String dealProjectJoinProgram(String message) {
        LOGGER.info("项目加入项目群或更新项目与项目群关系消费event: {}", message);
        ProjectRelationshipInsertPayload projectRelationshipInsertPayload = JSONObject.parseObject(message, ProjectRelationshipInsertPayload.class);
        Long programId = projectRelationshipInsertPayload.getParentId();
        List<ProjectRelationshipInsertPayload.ProjectRelationship> relationships = projectRelationshipInsertPayload.getRelationships();
        if (relationships != null && !relationships.isEmpty()) {
            for (ProjectRelationshipInsertPayload.ProjectRelationship projectRelationship : relationships) {
                if (ADD.equals(projectRelationship.getStatus())) {
                    Long projectId = projectRelationship.getId();
                    if (projectRelationship.getEnabled()) {
                        sprintService.addSprintsWhenJoinProgram(programId, projectId);
                    }
                } else if (UPDATE.equals(projectRelationship.getStatus())) {
                    Long projectId = projectRelationship.getId();
                    if (projectRelationship.getEnabled()) {
                        sprintService.addSprintsWhenJoinProgram(programId, projectId);
                    } else {
                        sprintService.completeSprintsByActivePi(programId, projectId);
                    }
                }
            }
        }
        return message;
    }

    @SagaTask(code = PROJECT_DISABLE_PROGRAM,
            description = "项目停用",
            sagaCode = PROJECT_DISABLE,
            seq = 3)
    public String dealProjectDisableProgram(String message) {
        LOGGER.info("项目停用: {}", message);
        ProjectEvent projectEvent = JSONObject.parseObject(message, ProjectEvent.class);
        if (PROJECT_CATEGORY_PROGRAM.equals(projectEvent.getProjectCategory())) {
            Long programId = projectEvent.getProjectId();
            ArtDO activeArt = artMapper.selectActiveArt(programId);
            if (programId != null && activeArt != null) {
                artService.stopArt(programId, new ArtDTO(programId, activeArt.getId(), activeArt.getObjectVersionNumber()), false);
            }
        } else if (PROJECT_CATEGORY_AGILE.equals(projectEvent.getProjectCategory())) {
            Long projectId= projectEvent.getProjectId();
            Long programId = projectEvent.getProgramId();
            if (projectId != null && programId != null) {
                sprintService.completeSprintsByActivePi(programId, projectId);
            }
        }
        return message;
    }

    @SagaTask(code = DELETE_PROGRAM_EVENT,
            description = "项目删除项目群关系消费event",
            sagaCode = IAM_DELETE_PROJECT_RELATIONSHIP,
            seq = 3)
    public String dealProjectDeleteProgram(String message) {
        LOGGER.info("项目删除项目群关系消费event: {}", message);
        ProjectRelationshipInsertPayload projectRelationshipInsertPayload = JSONObject.parseObject(message, ProjectRelationshipInsertPayload.class);
        Long programId = projectRelationshipInsertPayload.getParentId();
        List<ProjectRelationshipInsertPayload.ProjectRelationship> relationships = projectRelationshipInsertPayload.getRelationships();
        if (relationships != null && !relationships.isEmpty()) {
            for (ProjectRelationshipInsertPayload.ProjectRelationship projectRelationship : relationships) {
                if (DELETE.equals(projectRelationship.getStatus()) && projectRelationship.getEnabled()) {
                    Long projectId = projectRelationship.getId();
                    sprintService.completeSprintsByActivePi(programId, projectId);
                }
            }
        }
        return message;
    }

}

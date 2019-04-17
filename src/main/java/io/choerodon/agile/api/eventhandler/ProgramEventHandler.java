package io.choerodon.agile.api.eventhandler;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.app.service.SprintService;
import io.choerodon.agile.domain.agile.event.ProjectRelationshipInsertPayload;
import io.choerodon.asgard.saga.annotation.SagaTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/14.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class ProgramEventHandler {

    private static final String JOIN_PROGRAM_EVENT = "join-program-event";
    private static final String IAM_ADD_PROJECT_RELATIONSHIP = "iam-add-project-relationships";

    @Autowired
    private SprintService sprintService;

    @SagaTask(code = JOIN_PROGRAM_EVENT,
            description = "项目加入项目群消费event",
            sagaCode = IAM_ADD_PROJECT_RELATIONSHIP,
            seq = 3)
    public String dealProjectJoinProgram(String message) {
        ProjectRelationshipInsertPayload projectRelationshipInsertPayload = JSONObject.parseObject(message, ProjectRelationshipInsertPayload.class);
        Long programId = projectRelationshipInsertPayload.getParentId();
        List<ProjectRelationshipInsertPayload.ProjectRelationship> relationships = projectRelationshipInsertPayload.getRelationships();
        for (ProjectRelationshipInsertPayload.ProjectRelationship projectRelationship : relationships) {
            Long projectId = projectRelationship.getId();
            sprintService.addSprintsWhenJoinProgram(programId, projectId);
        }
        return message;
    }

}

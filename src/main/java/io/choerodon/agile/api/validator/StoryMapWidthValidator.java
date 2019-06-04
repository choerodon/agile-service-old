package io.choerodon.agile.api.validator;

import io.choerodon.agile.api.dto.StoryMapWidthDTO;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/6/4.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class StoryMapWidthValidator {

    private static final String TYPE_FEATURE = "feature";
    private static final String TYPE_EPIC = "issue_epic";

    public void checkStoryMapWidthCreate(StoryMapWidthDTO storyMapWidthDTO) {
        if (storyMapWidthDTO.getIssueId() == null) {
            throw new CommonException("error.issueId.isNull");
        }
        if (storyMapWidthDTO.getProjectId() == null) {
            throw new CommonException("error.projectId.isNull");
        }
        if (storyMapWidthDTO.getType() == null) {
            throw new CommonException("error.type.isNull");
        }
        if (!TYPE_FEATURE.equals(storyMapWidthDTO.getType()) && !TYPE_EPIC.equals(storyMapWidthDTO.getType())) {
            throw new CommonException("error.type.post");
        }
        if (storyMapWidthDTO.getWidth() == null) {
            throw new CommonException("error.width.isNull");
        }
    }

    public void checkStoryMapWidthUpdate(StoryMapWidthDTO storyMapWidthDTO) {
        if (storyMapWidthDTO.getId() == null) {
            throw new CommonException("error.id.isNull");
        }
        if (storyMapWidthDTO.getProjectId() == null) {
            throw new CommonException("error.projectId.isNull");
        }
        if (storyMapWidthDTO.getObjectVersionNumber() == null) {
            throw new CommonException("error.objectVersionNumber.isNull");
        }
    }
}

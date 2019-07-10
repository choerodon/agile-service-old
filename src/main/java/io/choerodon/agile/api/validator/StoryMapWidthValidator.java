package io.choerodon.agile.api.validator;

import io.choerodon.agile.api.vo.StoryMapWidthVO;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/6/4.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class StoryMapWidthValidator {

    private static final String TYPE_FEATURE = "feature";
    private static final String TYPE_EPIC = "epic";

    public void checkStoryMapWidthCreate(StoryMapWidthVO storyMapWidthVO) {
        if (storyMapWidthVO.getIssueId() == null) {
            throw new CommonException("error.issueId.isNull");
        }
        if (storyMapWidthVO.getProjectId() == null) {
            throw new CommonException("error.projectId.isNull");
        }
        if (storyMapWidthVO.getType() == null) {
            throw new CommonException("error.type.isNull");
        }
        if (!TYPE_FEATURE.equals(storyMapWidthVO.getType()) && !TYPE_EPIC.equals(storyMapWidthVO.getType())) {
            throw new CommonException("error.type.post");
        }
        if (storyMapWidthVO.getWidth() == null) {
            throw new CommonException("error.width.isNull");
        }
    }

    public void checkStoryMapWidthUpdate(StoryMapWidthVO storyMapWidthVO) {
        if (storyMapWidthVO.getId() == null) {
            throw new CommonException("error.id.isNull");
        }
        if (storyMapWidthVO.getProjectId() == null) {
            throw new CommonException("error.projectId.isNull");
        }
        if (storyMapWidthVO.getObjectVersionNumber() == null) {
            throw new CommonException("error.objectVersionNumber.isNull");
        }
    }
}

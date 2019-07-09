package io.choerodon.agile.api.validator;


import io.choerodon.agile.infra.dataobject.IssueDTO;
import io.choerodon.agile.infra.mapper.IssueMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/6/3.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class StoryMapValidator {

    private static final String ARCHIVED = "archived";
    private static final String RELEASED = "released";

    @Autowired
    private IssueMapper issueMapper;

    public void checkFeatureExist(Long featureId) {
        if (featureId != null && !Objects.equals(featureId, 0L)) {
            if (issueMapper.selectByPrimaryKey(featureId) == null) {
                throw new CommonException("error.feature.notFound");
            }
        }
    }

    public void checkEpicExist(Long epicId) {
        if (epicId != null && !Objects.equals(epicId, 0L)) {
            if (issueMapper.selectByPrimaryKey(epicId) == null) {
                throw new CommonException("error.epic.notFound");
            }
        }
    }

    public void checkVersionExist(Long versionId) {
        if (versionId != null && !Objects.equals(versionId, 0L)) {
            IssueDTO issueDTO = issueMapper.selectByPrimaryKey(versionId);
            if (issueDTO == null) {
                throw new CommonException("error.version.notFound");
            }
            if (ARCHIVED.equals(issueDTO.getStatusCode()) || RELEASED.equals(issueDTO.getStatusCode())) {
                throw new CommonException("error.productStatus.notRight");
            }
        }
    }

    public void checkFeatureUnderEpic(Long featureId, Long epicId) {
        if (epicId != null && epicId != 0 && featureId != null && featureId != 0) {
            IssueDTO featureDO = issueMapper.selectByPrimaryKey(featureId);
            if (featureDO == null) {
                throw new CommonException("error.feature.notFound");
            }
            if (!Objects.equals(featureDO.getEpicId(), epicId)) {
                throw new CommonException("error.FeatureUnderEpic.notUnder");
            }
        }
    }
}

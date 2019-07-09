package io.choerodon.agile.api.validator;


import io.choerodon.agile.api.vo.IssueComponentVO;
import io.choerodon.core.exception.CommonException;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/8/13.
 * Email: fuqianghuang01@gmail.com
 */
public class IssueComponentValidator {

    private static final String ERROR_PROJECTID_NOTEQUAL = "error.projectId.notEqual";
    private static final String ERROR_COMPONENTNAME_ISNULL = "error.componentName.isNull";

    private IssueComponentValidator() {}

    public static void checkCreateComponent(Long projectId, IssueComponentVO issueComponentVO) {
        if (!projectId.equals(issueComponentVO.getProjectId())) {
            throw new CommonException(ERROR_PROJECTID_NOTEQUAL);
        }
        if (issueComponentVO.getName() == null) {
            throw new CommonException(ERROR_COMPONENTNAME_ISNULL);
        }
    }

}

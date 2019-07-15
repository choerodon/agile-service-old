package io.choerodon.agile.api.validator;


import io.choerodon.agile.infra.dataobject.SprintDTO;
import io.choerodon.core.exception.CommonException;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/9/4.
 * Email: fuqianghuang01@gmail.com
 */
public class IterativeWorktableValidator {

    private IterativeWorktableValidator(){}

    public static void checkSprintExist(SprintDTO sprintDTO) {
        if (sprintDTO == null) {
            throw new CommonException("error.sprint.get");
        }
    }
}

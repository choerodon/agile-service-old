package io.choerodon.agile.infra.config;

import io.choerodon.swagger.notify.PmTemplate;
import org.springframework.stereotype.Component;
/**
 * Created by HuangFuqiang@choerodon.io on 2018/10/8.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class IssueAssigneePmTemplate implements PmTemplate {
    @Override
    public String businessTypeCode() {
        return "issueAssignee";
    }

    @Override
    public String code() {
        return "issueAssignee-preset";
    }

    @Override
    public String name() {
        return "问题被分配";
    }

    @Override
    public String title() {
        return "问题被分配";
    }

    @Override
    public String content() {
        return "<p>${userName} 被分配了问题：${summary}</p>";
    }
}

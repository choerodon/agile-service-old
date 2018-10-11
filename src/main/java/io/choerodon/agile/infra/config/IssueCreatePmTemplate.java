package io.choerodon.agile.infra.config;

import io.choerodon.swagger.notify.PmTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/10/9.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class IssueCreatePmTemplate implements PmTemplate {

    @Override
    public String businessTypeCode() {
        return "issueCreate";
    }

    @Override
    public String code() {
        return "issueCreate-preset";
    }

    @Override
    public String name() {
        return "问题创建";
    }

    @Override
    public String title() {
        return "问题创建";
    }

    @Override
    public String content() {
        return "<p>${userName} 创建了问题 <a href=${url} target=_blank>${summary}</a ></p>";
    }
}

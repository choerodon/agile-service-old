package io.choerodon.agile.infra.config;

import io.choerodon.swagger.notify.PmTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/10/9.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class IssueSolvePmTemplate implements PmTemplate {

    @Override
    public String businessTypeCode() {
        return "issueSolve";
    }

    @Override
    public String code() {
        return "issueSolve-preset";
    }

    @Override
    public String name() {
        return "问题已解决";
    }

    @Override
    public String title() {
        return "问题已解决";
    }

    @Override
    public String content() {
        return "<p><a href=\"${url}\" target=\"_blank\">${summary}</a> 已经由 ${userName} 解决</p>";
    }
}

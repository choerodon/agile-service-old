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
        return "问题解决";
    }

    @Override
    public String title() {
        return "问题解决";
    }

    @Override
    public String content() {
        return "<p>${userName} 解决了问题：${summary}</p>";
    }
}

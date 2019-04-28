package io.choerodon.agile.infra.config;

import io.choerodon.core.notify.Level;
import io.choerodon.core.notify.NotifyBusinessType;
import io.choerodon.core.notify.PmTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/04/24.
 * Email: fuqianghuang01@gmail.com
 */
@Component
@NotifyBusinessType(code = "pi-complete", name = "完成PI通知", description = "完成PI，给相关用户发送通知", level = Level.PROJECT)
public class PiCompletePmTemplate implements PmTemplate {

    @Override
    public String businessTypeCode() {
        return "pi-complete";
    }

    @Override
    public String code() {
        return "piComplete";
    }

    @Override
    public String name() {
        return "完成PI通知";
    }

    @Override
    public String title() {
        return "完成PI通知";
    }

    @Override
    public String content() {
        return "<p>您好，PI: ${piName} 已被完成。 此PI下的冲刺 ${sprintNameList} 已被自动完成。</p>";
    }
}

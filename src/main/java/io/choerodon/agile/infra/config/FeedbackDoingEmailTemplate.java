package io.choerodon.agile.infra.config;

import io.choerodon.core.notify.EmailTemplate;
import io.choerodon.core.notify.Level;
import io.choerodon.core.notify.NotifyBusinessType;
import org.springframework.stereotype.Component;

@NotifyBusinessType(code = "feedback-doing", name = "反馈处理中", level = Level.SITE,
        description = "反馈处理中发送邮件", isAllowConfig = false, isManualRetry = true)
@Component
public class FeedbackDoingEmailTemplate implements EmailTemplate {

    @Override
    public String businessTypeCode() {
        return "feedback-doing";
    }

    @Override
    public String code() {
        return "feedbackDoing";
    }

    @Override
    public String name() {
        return "反馈处理中";
    }

    @Override
    public String title() {
        return "反馈处理中";
    }

    @Override
    public String content() {
        return  "<p>你好</p>" +
                "<p>你的问题</p>" +
                "<p>${summary}</p>" +
                "<p>正在处理中，请耐心等待～</p>" +
                "<p>best wishes!</p>" +
                "<p>" +
                "此邮件为系统邮件，请勿回复。如需了解更多信息，请访问" +
                "<a href=\"http://choerodon.io/zh/\" target=\"_blank\">猪齿鱼官网</a>或" +
                "<a href=\"http://choerodon.io/zh/docs/\" target=\"_blank\">帮助文档</a>。" +
                "您也可以通过以下方式联系我们：" +
                "</p>" +
                "<p style=\"text-align: justify;\">" +
                "<img src=\"https://file.choerodon.com.cn/static/wechat-code.jpg\">" +
                "</p>";
    }
}

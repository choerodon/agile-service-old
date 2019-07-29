package io.choerodon.agile.infra.config;

import io.choerodon.core.notify.EmailTemplate;
import io.choerodon.core.notify.Level;
import io.choerodon.core.notify.NotifyBusinessType;
import org.springframework.stereotype.Component;

@NotifyBusinessType(code = "feedback-replied", name = "回复问题", level = Level.SITE,
        description = "回复问题", isAllowConfig = false, isManualRetry = true)
@Component
public class FeedbackRepliedEmailTemplate implements EmailTemplate {

    @Override
    public String businessTypeCode() {
        return "feedback-replied";
    }

    @Override
    public String code() {
        return "feedbackReplied";
    }

    @Override
    public String name() {
        return "回复问题";
    }

    @Override
    public String title() {
        return "回复问题";
    }

    @Override
    public String content() {
        return  "<p>回复你的问题</p>" +
                "<p>${content}</p>" +
                "<p>best wishes!</p>" +
                "<p>" +
                "此邮件为系统邮件，请勿回复。如需了解更多信息，请访问" +
                "<a href=\"http://choerodon.io/zh/\" target=\"_blank\">猪齿鱼官网</a>或" +
                "<a href=\"http://choerodon.io/zh/docs/\" target=\"_blank\">帮助文档</a>。" +
                "您也可以通过以下方式联系我们：" +
                "</p>" +
                "<p style=\"text-align: justify;\">" +
                "<img src=\"https://file.choerodon.com.cn/static/sina.png\">" +
                "<img src=\"https://file.choerodon.com.cn/static/wechat.png\">" +
                "</p>";
    }
}

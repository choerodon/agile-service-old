package io.choerodon.agile.infra.enums;

/**
 * @author shinan.chen
 * @since 2019/4/2
 */
public class InitPageFieldE {

    public enum AgileIssueCreateE {
        ISSUE_TYPE(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_CREATE, FieldCode.ISSUE_TYPE, true),
        PRIORITY(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_CREATE, FieldCode.PRIORITY, true),
        EPIC_NAME(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_CREATE, FieldCode.EPIC_NAME, true),
        SUMMARY(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_CREATE, FieldCode.SUMMARY, true),
        DESCRIPTION(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_CREATE, FieldCode.DESCRIPTION, true),
        REMAINING_TIME(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_CREATE, FieldCode.REMAINING_TIME, true),
        STORY_POINTS(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_CREATE, FieldCode.STORY_POINTS, true),
        ASSIGNEE(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_CREATE, FieldCode.ASSIGNEE, true),
        EPIC(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_CREATE, FieldCode.EPIC, true),
        PI(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_CREATE, FieldCode.PI, true),
        SPRINT(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_CREATE, FieldCode.SPRINT, true),
        FIX_VERSION(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_CREATE, FieldCode.FIX_VERSION, true),
        COMPONENT(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_CREATE, FieldCode.COMPONENT, true),
        LABEL(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_CREATE, FieldCode.LABEL, true),
        BENFIT_HYPOTHESIS(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_CREATE, FieldCode.BENFIT_HYPOTHESIS, true),
        ACCEPTANCE_CRITERA(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_CREATE, FieldCode.ACCEPTANCE_CRITERA, true);
        private String schemeCode;
        private String pageCode;
        private String fieldCode;
        private Boolean display;

        AgileIssueCreateE(String schemeCode, String pageCode, String fieldCode, Boolean display) {
            this.schemeCode = schemeCode;
            this.pageCode = pageCode;
            this.fieldCode = fieldCode;
            this.display = display;
        }

        public String getSchemeCode() {
            return schemeCode;
        }

        public String getPageCode() {
            return pageCode;
        }

        public String getFieldCode() {
            return fieldCode;
        }

        public Boolean getDisplay() {
            return display;
        }
    }

    public enum AgileIssueEditE {
        STATUS(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_EDIT, FieldCode.STATUS, true),
        PRIORITY(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_EDIT, FieldCode.PRIORITY, true),
        REPORTER(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_EDIT, FieldCode.REPORTER, true),
        ASSIGNEE(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_EDIT, FieldCode.ASSIGNEE, true),
        SPRINT(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_EDIT, FieldCode.SPRINT, true),
        INFLUENCE_VERSION(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_EDIT, FieldCode.INFLUENCE_VERSION, true),
        FIX_VERSION(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_EDIT, FieldCode.FIX_VERSION, true),
        EPIC(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_EDIT, FieldCode.EPIC, true),
        EPIC_NAME(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_EDIT, FieldCode.EPIC_NAME, true),
        COMPONENT(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_EDIT, FieldCode.COMPONENT, true),
        LABEL(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_EDIT, FieldCode.LABEL, true),
        PI(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_EDIT, FieldCode.PI, true),
        TIME_TRACE(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_EDIT, FieldCode.TIME_TRACE, true),
        CREATION_DATE(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_EDIT, FieldCode.CREATION_DATE, true),
        LAST_UPDATE_DATE(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_EDIT, FieldCode.LAST_UPDATE_DATE, true),
        BENFIT_HYPOTHESIS(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_EDIT, FieldCode.BENFIT_HYPOTHESIS, true),
        ACCEPTANCE_CRITERA(ObjectSchemeCode.AGILE_ISSUE, PageCode.AGILE_ISSUE_EDIT, FieldCode.ACCEPTANCE_CRITERA, true);

        private String schemeCode;
        private String pageCode;
        private String fieldCode;
        private Boolean display;

        AgileIssueEditE(String schemeCode, String pageCode, String fieldCode, Boolean display) {
            this.schemeCode = schemeCode;
            this.pageCode = pageCode;
            this.fieldCode = fieldCode;
            this.display = display;
        }

        public String getSchemeCode() {
            return schemeCode;
        }

        public String getPageCode() {
            return pageCode;
        }

        public String getFieldCode() {
            return fieldCode;
        }

        public Boolean getDisplay() {
            return display;
        }
    }
}

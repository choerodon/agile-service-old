package io.choerodon.agile.domain.agile.entity;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/13.
 * Email: fuqianghuang01@gmail.com
 */
public class QuickFilterFieldE {

    private String fieldCode;

    private String type;

    private String name;

    private String field;

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }
}

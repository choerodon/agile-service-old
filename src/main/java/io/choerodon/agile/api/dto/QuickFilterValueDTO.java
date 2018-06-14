package io.choerodon.agile.api.dto;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/14.
 * Email: fuqianghuang01@gmail.com
 */
public class QuickFilterValueDTO {

    private Long filedId;

    private String operation;

    private String value;

    public Long getFiledId() {
        return filedId;
    }

    public void setFiledId(Long filedId) {
        this.filedId = filedId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

package io.choerodon.agile.domain.agile.entity;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/13.
 * Email: fuqianghuang01@gmail.com
 */
public class QuickFilterFiledE {

    private Long filedId;

    private String type;

    private String name;

    private String filed;

    public void setFiledId(Long filedId) {
        this.filedId = filedId;
    }

    public Long getFiledId() {
        return filedId;
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

    public void setFiled(String filed) {
        this.filed = filed;
    }

    public String getFiled() {
        return filed;
    }
}

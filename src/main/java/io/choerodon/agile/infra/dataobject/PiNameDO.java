package io.choerodon.agile.infra.dataobject;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/4/2.
 * Email: fuqianghuang01@gmail.com
 */
public class PiNameDO {

    private Long id;

    private String code;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

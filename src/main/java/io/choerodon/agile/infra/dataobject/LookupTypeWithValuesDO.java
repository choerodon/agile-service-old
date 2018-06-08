package io.choerodon.agile.infra.dataobject;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/19.
 * Email: fuqianghuang01@gmail.com
 */
public class LookupTypeWithValuesDO {

    private String typeCode;

    private String name;

    private String description;

    private Long objectVersionNumber;

    private List<LookupValueDO> lookupValues;

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public List<LookupValueDO> getLookupValues() {
        return lookupValues;
    }

    public void setLookupValues(List<LookupValueDO> lookupValues) {
        this.lookupValues = lookupValues;
    }
}

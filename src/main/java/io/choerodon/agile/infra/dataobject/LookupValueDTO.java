package io.choerodon.agile.infra.dataobject;

import io.choerodon.mybatis.entity.BaseDTO;
import io.choerodon.agile.infra.utils.StringUtil;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * 敏捷开发code键值
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 09:40:27
 */
@Table(name = "lookup_value")
public class LookupValueDTO extends BaseDTO {

    /***/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String valueCode;

    /**
     * 类型code
     */
    @NotNull(message = "error.lookup_value.type_codeNotNull")
    private String typeCode;

    /**
     * 名称
     */
    @NotNull(message = "error.lookup_value.nameNotNull")
    private String name;

    /**
     * 描述
     */
    private String description;

    public String getValueCode() {
        return valueCode;
    }

    public void setValueCode(String valueCode) {
        this.valueCode = valueCode;
    }

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

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

}
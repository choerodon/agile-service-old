package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/22
 */
public class IssueLinkTypeCreateDTO {

    private String linkName;

    private String inWard;

    private String outWard;

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public String getInWard() {
        return inWard;
    }

    public void setInWard(String inWard) {
        this.inWard = inWard;
    }

    public String getOutWard() {
        return outWard;
    }

    public void setOutWard(String outWard) {
        this.outWard = outWard;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}

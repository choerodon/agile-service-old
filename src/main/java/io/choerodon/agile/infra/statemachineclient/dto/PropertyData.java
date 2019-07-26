package io.choerodon.agile.infra.statemachineclient.dto;

import com.google.common.base.MoreObjects;
import io.choerodon.agile.infra.statemachineclient.dto.ConfigCodeDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shinan.chen
 * @author dinghuang123@gmail.com
 * @since 2018/10/9
 */
public class PropertyData {

    private String serviceName;

    private List<ConfigCodeDTO> list = new ArrayList<>();

    public List<ConfigCodeDTO> getList() {
        return list;
    }

    public void setList(List<ConfigCodeDTO> list) {
        this.list = list;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("serviceName", serviceName)
                .add("list", list)
                .toString();
    }
}

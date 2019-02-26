package io.choerodon.agile.api.dto;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/2/25
 */
public class PersonalFilterSearchOtherArgsDTO {
    List<Long> sprint;
    List<Long> version;
    List<Long> component;
    List<Long> epic;
    List<Long> label;

    public List<Long> getVersion() {
        return version;
    }

    public void setVersion(List<Long> version) {
        this.version = version;
    }

    public List<Long> getComponent() {
        return component;
    }

    public void setComponent(List<Long> component) {
        this.component = component;
    }

    public List<Long> getEpic() {
        return epic;
    }

    public void setEpic(List<Long> epic) {
        this.epic = epic;
    }

    public List<Long> getLabel() {
        return label;
    }

    public void setLabel(List<Long> label) {
        this.label = label;
    }

    public List<Long> getSprint() {
        return sprint;
    }

    public void setSprint(List<Long> sprint) {
        this.sprint = sprint;
    }
}

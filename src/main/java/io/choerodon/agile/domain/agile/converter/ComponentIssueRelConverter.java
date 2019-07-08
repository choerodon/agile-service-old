package io.choerodon.agile.domain.agile.converter;


import io.choerodon.core.convertor.ConvertorI;
import org.springframework.stereotype.Component;
import org.springframework.beans.BeanUtils;
import io.choerodon.agile.api.vo.ComponentIssueRelDTO;
import io.choerodon.agile.infra.dataobject.ComponentIssueRelDO;
import io.choerodon.agile.domain.agile.entity.ComponentIssueRelE;

/**
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 16:47:27
 */
@Component
public class ComponentIssueRelConverter implements ConvertorI<ComponentIssueRelE, ComponentIssueRelDO, ComponentIssueRelDTO> {

    @Override
    public ComponentIssueRelE dtoToEntity(ComponentIssueRelDTO componentIssueRelDTO) {
        ComponentIssueRelE componentIssueRelE = new ComponentIssueRelE();
        BeanUtils.copyProperties(componentIssueRelDTO, componentIssueRelE);
        return componentIssueRelE;
    }

    @Override
    public ComponentIssueRelE doToEntity(ComponentIssueRelDO componentIssueRelDO) {
        ComponentIssueRelE componentIssueRelE = new ComponentIssueRelE();
        BeanUtils.copyProperties(componentIssueRelDO, componentIssueRelE);
        return componentIssueRelE;
    }

    @Override
    public ComponentIssueRelDTO entityToDto(ComponentIssueRelE componentIssueRelE) {
        ComponentIssueRelDTO componentIssueRelDTO = new ComponentIssueRelDTO();
        BeanUtils.copyProperties(componentIssueRelE, componentIssueRelDTO);
        return componentIssueRelDTO;
    }

    @Override
    public ComponentIssueRelDO entityToDo(ComponentIssueRelE componentIssueRelE) {
        ComponentIssueRelDO componentIssueRelDO = new ComponentIssueRelDO();
        BeanUtils.copyProperties(componentIssueRelE, componentIssueRelDO);
        return componentIssueRelDO;
    }

    @Override
    public ComponentIssueRelDTO doToDto(ComponentIssueRelDO componentIssueRelDO) {
        ComponentIssueRelDTO componentIssueRelDTO = new ComponentIssueRelDTO();
        BeanUtils.copyProperties(componentIssueRelDO, componentIssueRelDTO);
        return componentIssueRelDTO;
    }

    @Override
    public ComponentIssueRelDO dtoToDo(ComponentIssueRelDTO componentIssueRelDTO) {
        ComponentIssueRelDO componentIssueRelDO = new ComponentIssueRelDO();
        BeanUtils.copyProperties(componentIssueRelDTO, componentIssueRelDO);
        return componentIssueRelDO;
    }
}
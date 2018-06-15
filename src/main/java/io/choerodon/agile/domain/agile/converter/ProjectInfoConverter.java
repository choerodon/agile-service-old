package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.dto.ProjectInfoDTO;
import io.choerodon.agile.domain.agile.entity.ProjectInfoE;
import io.choerodon.agile.infra.dataobject.ProjectInfoDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/15
 */
@Component
public class ProjectInfoConverter implements ConvertorI<ProjectInfoE, ProjectInfoDO, ProjectInfoDTO> {

    @Override
    public ProjectInfoE dtoToEntity(ProjectInfoDTO projectInfoDTO) {
        ProjectInfoE projectInfoE = new ProjectInfoE();
        BeanUtils.copyProperties(projectInfoDTO, projectInfoE);
        return projectInfoE;
    }

    @Override
    public ProjectInfoE doToEntity(ProjectInfoDO projectInfoDO) {
        ProjectInfoE projectInfoE = new ProjectInfoE();
        BeanUtils.copyProperties(projectInfoDO, projectInfoE);
        return projectInfoE;
    }

    @Override
    public ProjectInfoDTO entityToDto(ProjectInfoE projectInfoE) {
        ProjectInfoDTO projectInfoDTO = new ProjectInfoDTO();
        BeanUtils.copyProperties(projectInfoE, projectInfoDTO);
        return projectInfoDTO;
    }

    @Override
    public ProjectInfoDO entityToDo(ProjectInfoE projectInfoE) {
        ProjectInfoDO projectInfoDO = new ProjectInfoDO();
        BeanUtils.copyProperties(projectInfoE, projectInfoDO);
        return projectInfoDO;
    }

    @Override
    public ProjectInfoDTO doToDto(ProjectInfoDO projectInfoDO) {
        ProjectInfoDTO projectInfoDTO = new ProjectInfoDTO();
        BeanUtils.copyProperties(projectInfoDO, projectInfoDTO);
        return projectInfoDTO;
    }

    @Override
    public ProjectInfoDO dtoToDo(ProjectInfoDTO projectInfoDTO) {
        ProjectInfoDO projectInfoDO = new ProjectInfoDO();
        BeanUtils.copyProperties(projectInfoDTO, projectInfoDO);
        return projectInfoDO;
    }
}

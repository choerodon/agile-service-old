package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.PersonalFilterDTO;
import io.choerodon.agile.app.service.PersonalFilterService;
import io.choerodon.agile.infra.dataobject.PersonalFilterDO;
import io.choerodon.agile.infra.mapper.PersonalFilterMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/2/25
 */
@Service
public class PersonalFilterServiceImpl implements PersonalFilterService {

    @Autowired
    private PersonalFilterMapper personalFilterMapper;

    public static final String UPDATE_ERROR = "error.personalFilter.update";
    public static final String DELETE_ERROR = "error.personalFilter.deleteById";
    public static final String ILLEGAL_ERROR = "error.personalFilter.illegal";
    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public PersonalFilterDTO queryById(Long projectId, Long filterId) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        Long userId = customUserDetails.getUserId();
        PersonalFilterDO personalFilterDO = personalFilterMapper.selectByPrimaryKey(filterId);
        if (!personalFilterDO.getProjectId().equals(projectId)) {
            throw new CommonException(ILLEGAL_ERROR);
        }
        if (!personalFilterDO.getUserId().equals(userId)) {
            throw new CommonException(ILLEGAL_ERROR);
        }
        return modelMapper.map(personalFilterDO, PersonalFilterDTO.class);
    }

    @Override
    public PersonalFilterDTO create(Long projectId, PersonalFilterDTO personalFilterDTO) {
        return null;
    }

    @Override
    public PersonalFilterDTO update(Long projectId, Long filterId, PersonalFilterDTO personalFilterDTO) {
        personalFilterDTO.setFilterId(filterId);
        PersonalFilterDO personalFilterDO = modelMapper.map(personalFilterDTO, PersonalFilterDO.class);
        if (personalFilterMapper.updateByPrimaryKeySelective(personalFilterDO) != 1) {
            throw new CommonException(UPDATE_ERROR);
        }
        return queryById(projectId, filterId);
    }

    @Override
    public void deleteById(Long projectId, Long filterId) {
        PersonalFilterDO personalFilterDO = new PersonalFilterDO();
        personalFilterDO.setProjectId(projectId);
        personalFilterDO.setFilterId(filterId);
        int isDelete = personalFilterMapper.delete(personalFilterDO);
        if (isDelete != 1) {
            throw new CommonException(DELETE_ERROR);
        }
    }

    @Override
    public List<PersonalFilterDTO> listByProjectId(Long projectId, Long userId, String searchStr) {
        return modelMapper.map(personalFilterMapper.queryByProjectIdAndUserId(projectId, userId, searchStr), new TypeToken<List<PersonalFilterDTO>>() {
        }.getType());

    }
}

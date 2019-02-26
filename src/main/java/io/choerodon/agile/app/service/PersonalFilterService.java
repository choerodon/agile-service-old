package io.choerodon.agile.app.service;

import io.choerodon.agile.api.dto.PersonalFilterDTO;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/2/25
 */
public interface PersonalFilterService {

    PersonalFilterDTO queryById(Long projectId, Long filterId);

    PersonalFilterDTO create(Long projectId, PersonalFilterDTO personalFilterDTO);

    PersonalFilterDTO update(Long projectId, Long filterId, PersonalFilterDTO personalFilterDTO);

    void deleteById(Long projectId, Long filterId);

    List<PersonalFilterDTO> listByProjectId(Long projectId, Long userId, String searchStr);
}

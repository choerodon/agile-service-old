package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.PersonalFilterVO;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/2/25
 */
public interface PersonalFilterService {

    PersonalFilterVO queryById(Long projectId, Long filterId);

    PersonalFilterVO create(Long projectId, PersonalFilterVO personalFilterVO);

    PersonalFilterVO update(Long projectId, Long filterId, PersonalFilterVO personalFilterVO);

    void deleteById(Long projectId, Long filterId);

    List<PersonalFilterVO> listByProjectId(Long projectId, Long userId, String searchStr);

    Boolean checkName(Long projectId, Long userId, String name);
}

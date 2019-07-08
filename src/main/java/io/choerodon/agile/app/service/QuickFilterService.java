package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.QuickFilterDTO;
import io.choerodon.agile.api.vo.QuickFilterSearchDTO;
import io.choerodon.agile.api.vo.QuickFilterSequenceDTO;

import java.util.List;


/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/13.
 * Email: fuqianghuang01@gmail.com
 */
public interface QuickFilterService {

    QuickFilterDTO create(Long projectId, QuickFilterDTO quickFilterDTO);

    QuickFilterDTO update(Long projectId, Long filterId, QuickFilterDTO quickFilterDTO);

    void deleteById(Long projectId, Long filterId);

    QuickFilterDTO queryById(Long projectId, Long filterId);

    /**
     * 分页搜索过滤条件
     *
     * @param projectId            projectId
     * @param quickFilterSearchDTO quickFilterSearchDTO
     * @return QuickFilterDTO
     */
    List<QuickFilterDTO> listByProjectId(Long projectId, QuickFilterSearchDTO quickFilterSearchDTO);

    /**
     * 拖动排序
     *
     * @param projectId              projectId
     * @param quickFilterSequenceDTO quickFilterSequenceDTO
     * @return QuickFilterDTO
     */
    QuickFilterDTO dragFilter(Long projectId, QuickFilterSequenceDTO quickFilterSequenceDTO);

    Boolean checkName(Long projectId, String quickFilterName);
}

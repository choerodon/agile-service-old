package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.QuickFilterSearchVO;
import io.choerodon.agile.api.vo.QuickFilterVO;
import io.choerodon.agile.api.vo.QuickFilterSequenceVO;

import java.util.List;


/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/13.
 * Email: fuqianghuang01@gmail.com
 */
public interface QuickFilterService {

    QuickFilterVO create(Long projectId, QuickFilterVO quickFilterVO);

    QuickFilterVO update(Long projectId, Long filterId, QuickFilterVO quickFilterVO);

    void deleteById(Long projectId, Long filterId);

    QuickFilterVO queryById(Long projectId, Long filterId);

    /**
     * 分页搜索过滤条件
     *
     * @param projectId            projectId
     * @param quickFilterSearchVO quickFilterSearchVO
     * @return QuickFilterVO
     */
    List<QuickFilterVO> listByProjectId(Long projectId, QuickFilterSearchVO quickFilterSearchVO);

    /**
     * 拖动排序
     *
     * @param projectId              projectId
     * @param quickFilterSequenceVO quickFilterSequenceVO
     * @return QuickFilterVO
     */
    QuickFilterVO dragFilter(Long projectId, QuickFilterSequenceVO quickFilterSequenceVO);

    Boolean checkName(Long projectId, String quickFilterName);
}

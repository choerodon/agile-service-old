package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.IssueLinkCreateVO;
import io.choerodon.agile.api.vo.IssueLinkVO;
import io.choerodon.agile.infra.dataobject.IssueLinkDTO;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/14
 */
public interface IssueLinkService {

    /**
     * 创建issueLink
     *
     * @param issueLinkCreateVOList issueLinkCreateVOList
     * @param issueId                issueId
     * @param projectId              projectId
     * @return IssueLinkVO
     */
    List<IssueLinkVO> createIssueLinkList(List<IssueLinkCreateVO> issueLinkCreateVOList, Long issueId, Long projectId);

    /**
     * 根据issueLinkId删除issueLink
     *
     * @param issueLinkId issueLinkId
     */
    void deleteIssueLink(Long issueLinkId);

    /**
     * 根据issueId查询issueLink
     *
     * @param issueId   issueId
     * @param projectId projectId
     * @param noIssueTest noIssueTest
     * @return IssueLinkVO
     */
    List<IssueLinkVO> listIssueLinkByIssueId(Long issueId, Long projectId, Boolean noIssueTest);

    List<IssueLinkVO> listIssueLinkByBatch(Long projectId, List<Long> issueIds);


    List<IssueLinkDTO> create(IssueLinkDTO issueLinkDTO);

    int deleteByIssueId(Long issueId);

    int delete(Long issueLinkId);
}

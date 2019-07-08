package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.IssueLinkCreateDTO;
import io.choerodon.agile.api.vo.IssueLinkDTO;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/14
 */
public interface IssueLinkService {

    /**
     * 创建issueLink
     *
     * @param issueLinkCreateDTOList issueLinkCreateDTOList
     * @param issueId                issueId
     * @param projectId              projectId
     * @return IssueLinkDTO
     */
    List<IssueLinkDTO> createIssueLinkList(List<IssueLinkCreateDTO> issueLinkCreateDTOList, Long issueId, Long projectId);

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
     * @return IssueLinkDTO
     */
    List<IssueLinkDTO> listIssueLinkByIssueId(Long issueId, Long projectId,Boolean noIssueTest);

    List<IssueLinkDTO> listIssueLinkByBatch(Long projectId, List<Long> issueIds);
}

package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.ProjectDTO;
import io.choerodon.agile.api.vo.RankVO;
import io.choerodon.agile.api.validator.RankValidator;
import io.choerodon.agile.app.service.RankService;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.agile.infra.dataobject.RankDTO;
import io.choerodon.agile.infra.mapper.RankMapper;
import io.choerodon.agile.app.service.UserService;
import io.choerodon.core.exception.CommonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by HuangFuqiang@choerodon.io on 2019/6/24.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class RankServiceImpl implements RankService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RankServiceImpl.class);
    private static final String RANK_TYPE_EPIC = "epic";
    private static final String RANK_TYPE_FEATURE = "feature";

    @Autowired
    private RankValidator rankValidator;

    @Autowired
    private RankMapper rankMapper;

    @Autowired
    private UserService userService;


    private List<Long> getEpicIds(Long projectId) {
        List<Long> epicIds = new ArrayList<>();
        // get program epic
        ProjectDTO program = userService.getGroupInfoByEnableProject(ConvertUtil.getOrganizationId(projectId), projectId);
        if (program != null) {
            List<Long> programEpicIds = rankMapper.selectEpicIdsByProgram(program.getId());
            if (programEpicIds != null && !programEpicIds.isEmpty()) {
                epicIds.addAll(programEpicIds);
            }
        }
        // get project epic
        List<Long> projectEpicIds = rankMapper.selectEpicIdsByProject(projectId);
        if (projectEpicIds != null && !projectEpicIds.isEmpty()) {
            epicIds.addAll(projectEpicIds);
        }
        Collections.sort(epicIds, Comparator.reverseOrder());
        return epicIds;
    }

    private List<Long> getFeatureIds(Long projectId) {
        List<Long> featureIds = new ArrayList<>();
        // get program feature
        ProjectDTO program = userService.getGroupInfoByEnableProject(ConvertUtil.getOrganizationId(projectId), projectId);
        if (program != null) {
            List<Long> programFeatureIds = rankMapper.selectFeatureIdsByProgram(program.getId());
            if (programFeatureIds != null && !programFeatureIds.isEmpty()) {
                featureIds.addAll(programFeatureIds);
            }
        }
        // get project feature
        List<Long> projectFeatureIds = rankMapper.selectFeatureIdsByProject(projectId);
        if (projectFeatureIds != null && !projectFeatureIds.isEmpty()) {
            featureIds.addAll(projectFeatureIds);
        }
        Collections.sort(featureIds, Comparator.reverseOrder());
        return featureIds;
    }

    private void insertRankByBatch(Long projectId, List<Long> issueIds, String type) {
        List<RankDTO> insertRankList = new ArrayList<>();
        String rank = RankUtil.mid();
        for (Long issueId : issueIds) {
            insertRankList.add(new RankDTO(issueId, rank));
            rank = RankUtil.genNext(rank);
        }
        if (!insertRankList.isEmpty()) {
            rankMapper.batchInsertRank(projectId, type, insertRankList);
        }
    }

    @Override
    public RankDTO getReferenceRank(Long projectId, String type, Long referenceIssueId) {
        RankDTO rankDTO = rankMapper.selectRankByIssueId(projectId, type, referenceIssueId);
        if (rankDTO == null) {
            switch (type) {
                case RANK_TYPE_EPIC:
                    List<Long> epicIds = getEpicIds(projectId);
                    List<Long> epicRankDOList = rankMapper.checkRankEmpty(projectId, RANK_TYPE_EPIC);
                    List<Long> emptyEpicIds = epicIds.stream().filter(epicId -> !epicRankDOList.contains(epicId)).collect(Collectors.toList());
                    if (emptyEpicIds != null && !emptyEpicIds.isEmpty()) {
                        insertRankByBatch(projectId, emptyEpicIds, RANK_TYPE_EPIC);
                    }
                    break;
                case RANK_TYPE_FEATURE:
                    List<Long> featureIds = getFeatureIds(projectId);
                    List<Long> featureRankDOList = rankMapper.checkRankEmpty(projectId, RANK_TYPE_FEATURE);
                    List<Long> emptyFeatureIds = featureIds.stream().filter(featureId -> !featureRankDOList.contains(featureId)).collect(Collectors.toList());
                    if (emptyFeatureIds != null && !emptyFeatureIds.isEmpty()) {
                        insertRankByBatch(projectId, emptyFeatureIds, RANK_TYPE_FEATURE);
                    }
                    break;
                default:
                    break;
            }
            RankDTO newRank = rankMapper.selectRankByIssueId(projectId, type, referenceIssueId);
            if (newRank == null) {
                throw new CommonException("error.rank.get");
            }
            return newRank;
        } else {
            return rankDTO;
        }
    }

    private RankDTO getOrinitReferenceRank(Long projectId, String type, Long referenceIssueId, Long issueId) {
        RankDTO rankReference = rankMapper.selectRankByIssueId(projectId, type, referenceIssueId);
        RankDTO rankCurrent = rankMapper.selectRankByIssueId(projectId, type, issueId);
        if (rankReference == null || rankCurrent == null) {
            switch (type) {
                case RANK_TYPE_FEATURE:
                    List<Long> featureIds = getFeatureIds(projectId);
                    List<Long> featureRankDOList = rankMapper.checkRankEmpty(projectId, RANK_TYPE_FEATURE);
                    List<Long> emptyFeatureIds = featureIds.stream().filter(featureId -> !featureRankDOList.contains(featureId)).collect(Collectors.toList());
                    if (emptyFeatureIds != null && !emptyFeatureIds.isEmpty()) {
                        insertRankByBatch(projectId, emptyFeatureIds, RANK_TYPE_FEATURE);
                    }
                    break;
                case RANK_TYPE_EPIC:
                    List<Long> epicIds = getEpicIds(projectId);
                    List<Long> epicRankDOList = rankMapper.checkRankEmpty(projectId, RANK_TYPE_EPIC);
                    List<Long> emptyEpicIds = epicIds.stream().filter(epicId -> !epicRankDOList.contains(epicId)).collect(Collectors.toList());
                    if (emptyEpicIds != null && !emptyEpicIds.isEmpty()) {
                        insertRankByBatch(projectId, emptyEpicIds, RANK_TYPE_EPIC);
                    }
                    break;
                default:
                    break;
            }
            RankDTO newRank = rankMapper.selectRankByIssueId(projectId, type, referenceIssueId);
            if (newRank == null) {
                throw new CommonException("error.rank.get");
            }
            return newRank;
        } else {
            return rankReference;
        }
    }

    @Override
    public void epicAndFeatureRank(Long projectId, RankVO rankVO) {
        rankValidator.checkEpicAndFeatureRank(rankVO);
        Long referenceIssueId = rankVO.getReferenceIssueId();
        RankDTO referenceRank = getOrinitReferenceRank(projectId, rankVO.getType(), referenceIssueId, rankVO.getIssueId());
        if (rankVO.getBefore()) {
            String leftRank = rankMapper.selectLeftRank(projectId, rankVO.getType(), referenceRank.getRank());
            String rank = (leftRank == null ? RankUtil.genPre(referenceRank.getRank()) : RankUtil.between(leftRank, referenceRank.getRank()));
            RankDTO rankDTO = rankMapper.selectRankByIssueId(projectId, rankVO.getType(), rankVO.getIssueId());
            Long objectVersionNumber = rankVO.getObjectVersionNumber() == null ? rankDTO.getObjectVersionNumber() : rankVO.getObjectVersionNumber();
            rankMapper.updateByPrimaryKeySelective(new RankDTO(rankDTO.getId(), rank, objectVersionNumber));
        } else {
            String rightRank = rankMapper.selectRightRank(projectId, rankVO.getType(), referenceRank.getRank());
            String rank = (rightRank == null ? RankUtil.genNext(referenceRank.getRank()) : RankUtil.between(referenceRank.getRank(), rightRank));
            RankDTO rankDTO = rankMapper.selectRankByIssueId(projectId, rankVO.getType(), rankVO.getIssueId());
            Long objectVersionNumber = rankVO.getObjectVersionNumber() == null ? rankDTO.getObjectVersionNumber() : rankVO.getObjectVersionNumber();
            rankMapper.updateByPrimaryKeySelective(new RankDTO(rankDTO.getId(), rank, objectVersionNumber));
        }
    }
}

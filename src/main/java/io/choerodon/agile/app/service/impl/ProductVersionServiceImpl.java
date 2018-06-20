package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.app.assembler.*;
import io.choerodon.agile.domain.agile.rule.ProductVersionRule;
import io.choerodon.agile.infra.dataobject.IssueCountDO;
import io.choerodon.agile.infra.dataobject.VersionIssueDO;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.agile.app.service.ProductVersionService;
import io.choerodon.agile.domain.agile.entity.ProductVersionE;
import io.choerodon.agile.domain.agile.repository.ProductVersionRepository;
import io.choerodon.agile.domain.agile.repository.VersionIssueRelRepository;
import io.choerodon.agile.infra.common.utils.SearchUtil;
import io.choerodon.agile.infra.common.utils.StringUtil;
import io.choerodon.agile.infra.dataobject.ProductVersionDO;
import io.choerodon.agile.infra.mapper.ProductVersionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by jian_zhang02@163.com on 2018/5/14.
 */
@Service
@Transactional(rollbackFor = CommonException.class)
public class ProductVersionServiceImpl implements ProductVersionService {

    @Autowired
    private ProductVersionCreateAssembler productVersionCreateAssembler;
    @Autowired
    private ProductVersionUpdateAssembler productVersionUpdateAssembler;
    @Autowired
    private ProductVersionPageAssembler productVersionPageAssembler;
    @Autowired
    private ProductVersionStatisticsAssembler versionStatisticsAssembler;
    @Autowired
    private IssueAssembler issueAssembler;
    @Autowired
    private ProductVersionRepository productVersionRepository;
    @Autowired
    private VersionIssueRelRepository versionIssueRelRepository;
    @Autowired
    private ProductVersionDataAssembler versionDataAssembler;
    @Autowired
    private ProductVersionRule productVersionRule;
    @Autowired
    private ProductVersionMapper productVersionMapper;

    private static final String SEARCH_ARGS = "searchArgs";
    public static final String ADVANCE_SEARCH_ARGS = "advancedSearchArgs";
    private static final String VERSION_PLANNING = "version_planning";
    private static final String NOT_EQUAL_ERROR = "error.projectId.notEqual";
    private static final String NOT_FOUND = "error.version.notFound";
    private static final String CATEGORY_DONE_CODE = "done";
    private static final String CATEGORY_TODO_CODE = "todo";
    private static final String CATEGORY_DOING_CODE = "doing";

    @Override
    public ProductVersionDetailDTO createVersion(Long projectId, ProductVersionCreateDTO versionCreateDTO) {
        if (!projectId.equals(versionCreateDTO.getProjectId())) {
            throw new CommonException(NOT_EQUAL_ERROR);
        }
        ProductVersionE productVersionE = productVersionCreateAssembler.dtoToEntity(versionCreateDTO);
        productVersionE.checkDate();
        productVersionRule.judgeName(productVersionE.getProjectId(), productVersionE.getVersionId(), productVersionE.getName());
        //设置状态
        productVersionE.setStatusCode(VERSION_PLANNING);
        return productVersionCreateAssembler.entityToDto(productVersionRepository.createVersion(productVersionE));
    }

    @Override
    public Boolean deleteVersion(Long projectId, ProductVersionDeleteDTO productVersionDelete) {
        if (!projectId.equals(productVersionDelete.getProjectId())) {
            throw new CommonException(NOT_EQUAL_ERROR);
        }
        productVersionRule.judgeExist(projectId, productVersionDelete.getTargetVersionId());
        if (productVersionDelete.getTargetVersionId() != null && !Objects.equals(productVersionDelete.getTargetVersionId(), 0L)) {
            List<VersionIssueDO> versionIssues = productVersionMapper.queryIssues(projectId, productVersionDelete.getVersionId());
            productVersionRepository.issueToDestination(projectId, productVersionDelete.getTargetVersionId(), versionIssues);
        }
        versionIssueRelRepository.deleteByVersionId(projectId, productVersionDelete.getVersionId());
        return simpleDeleteVersion(projectId, productVersionDelete.getVersionId());
    }

    private Boolean simpleDeleteVersion(Long projectId, Long versionId) {
        ProductVersionDO versionDO = new ProductVersionDO();
        versionDO.setProjectId(projectId);
        versionDO.setVersionId(versionId);
        ProductVersionE versionE = productVersionCreateAssembler.doToEntity(productVersionMapper.selectOne(versionDO));
        if (versionE == null) {
            throw new CommonException(NOT_FOUND);
        }
        return productVersionRepository.deleteVersion(versionE);
    }

    @Override
    public ProductVersionDetailDTO updateVersion(Long projectId, Long versionId, ProductVersionUpdateDTO versionUpdateDTO, List<String> fieldList) {
        if (!projectId.equals(versionUpdateDTO.getProjectId())) {
            throw new CommonException(NOT_EQUAL_ERROR);
        }
        ProductVersionE productVersionE = productVersionUpdateAssembler.dtoToEntity(versionUpdateDTO);
        productVersionE.checkDate();
        productVersionRule.judgeName(productVersionE.getProjectId(), productVersionE.getVersionId(), productVersionE.getName());
        productVersionE.setVersionId(versionId);
        return productVersionUpdateAssembler.entityToDto(productVersionRepository.updateVersion(productVersionE, fieldList));
    }

    @Override
    public Page<ProductVersionPageDTO> queryByProjectId(Long projectId, PageRequest pageRequest, Map<String, Object> searchParamMap) {
        //过滤查询和排序
        Map<String, Object> result = SearchUtil.setParam(searchParamMap);
        Page<Long> versionIds = PageHelper.doPageAndSort(pageRequest, () ->
                productVersionMapper.queryVersionIdsByProjectId(projectId, StringUtil.cast(result.get(SEARCH_ARGS)), StringUtil.cast(result.get(ADVANCE_SEARCH_ARGS))));
        Page<ProductVersionPageDTO> versionPage = new Page<>();
        versionPage.setNumber(versionIds.getNumber());
        versionPage.setNumberOfElements(versionIds.getNumberOfElements());
        versionPage.setSize(versionIds.getSize());
        versionPage.setTotalElements(versionIds.getTotalElements());
        versionPage.setTotalPages(versionIds.getTotalPages());
        if ((versionIds.getContent() != null) && !versionIds.isEmpty()) {
            versionPage.setContent(productVersionPageAssembler.doListToDto(productVersionMapper.queryVersionByIds(projectId, versionIds.getContent())));
        }
        return versionPage;
    }

    @Override
    public Boolean repeatName(Long projectId, String name) {
        return productVersionMapper.isRepeatName(projectId, name);
    }

    @Override
    public List<ProductVersionDataDTO> queryVersionByprojectId(Long projectId) {
        List<ProductVersionDataDTO> productVersions = versionDataAssembler.doListToDTO(productVersionMapper.queryVersionByprojectId(projectId));
        if (!productVersions.isEmpty()) {
            List<Long> productVersionIds = productVersions.stream().map(ProductVersionDataDTO::getVersionId).collect(Collectors.toList());
            Map<Long, Integer> issueCountMap = productVersionMapper.queryIssueCount(projectId, productVersionIds, null).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            Map<Long, Integer> doneIssueCountMap = productVersionMapper.queryIssueCount(projectId, productVersionIds, CATEGORY_DONE_CODE).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            Map<Long, Integer> notEstimateMap = productVersionMapper.queryNotEstimate(projectId, productVersionIds).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            Map<Long, Integer> totalEstimateMap = productVersionMapper.queryTotalEstimate(projectId, productVersionIds).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            productVersions.forEach(productVersion -> {
                productVersion.setIssueCount(issueCountMap.get(productVersion.getVersionId()));
                productVersion.setDoneIssueCount(doneIssueCountMap.get(productVersion.getVersionId()));
                productVersion.setNotEstimate(notEstimateMap.get(productVersion.getVersionId()));
                productVersion.setTotalEstimate(totalEstimateMap.get(productVersion.getVersionId()));
            });
        }
        return productVersions;
    }

    @Override
    public ProductVersionStatisticsDTO queryVersionStatisticsByVersionId(Long projectId, Long versionId) {
        ProductVersionStatisticsDTO productVersionStatistics = versionStatisticsAssembler.doToDto(productVersionMapper.queryVersionStatisticsByVersionId(projectId, versionId));
        productVersionStatistics.setTodoCategoryIssueCount(versionStatisticsAssembler.doListToIssueCountDto(productVersionMapper.queryIssueCountByVersionId(projectId, versionId, CATEGORY_TODO_CODE)));
        productVersionStatistics.setDoingCategoryIssueCount(versionStatisticsAssembler.doListToIssueCountDto(productVersionMapper.queryIssueCountByVersionId(projectId, versionId, CATEGORY_DOING_CODE)));
        productVersionStatistics.setDoneCategoryIssueCount(versionStatisticsAssembler.doListToIssueCountDto(productVersionMapper.queryIssueCountByVersionId(projectId, versionId, CATEGORY_DONE_CODE)));
        return productVersionStatistics;
    }

    @Override
    public List<IssueListDTO> queryIssueByVersionIdAndStatusCode(Long projectId, Long versionId, String statusCode) {
        return issueAssembler.issueDoToIssueListDto(productVersionMapper.queryIssueByVersionIdAndStatusCode(projectId, versionId, statusCode));
    }

    @Override
    public VersionMessageDTO queryReleaseMessageByVersionId(Long projectId, Long versionId) {
        VersionMessageDTO versionReleaseMessage = new VersionMessageDTO();
        versionReleaseMessage.setIssueCount(productVersionMapper.queryNotDoneIssueCount(projectId, versionId));
        versionReleaseMessage.setVersionNames(versionStatisticsAssembler.doListToVersionNameDto(productVersionMapper.queryPlanVersionNames(projectId, versionId)));
        return versionReleaseMessage;
    }

    @Override
    public ProductVersionDetailDTO releaseVersion(Long projectId, ProductVersionReleaseDTO productVersionRelease) {
        if (!Objects.equals(projectId, productVersionRelease.getProjectId())) {
            throw new CommonException(NOT_EQUAL_ERROR);
        }
        productVersionRule.isRelease(projectId, productVersionRelease);
        if (productVersionRelease.getTargetVersionId() != null && !Objects.equals(productVersionRelease.getTargetVersionId(), 0L)) {
            List<VersionIssueDO> incompleteIssues = productVersionMapper.queryIncompleteIssues(projectId, productVersionRelease.getVersionId());
            if (!incompleteIssues.isEmpty()) {
                versionIssueRelRepository.deleteIncompleteIssueByVersionId(projectId, productVersionRelease.getVersionId());
                productVersionRepository.issueToDestination(projectId, productVersionRelease.getTargetVersionId(), incompleteIssues);
            }
        }
        productVersionRepository.releaseVersion(projectId, productVersionRelease.getVersionId(), productVersionRelease.getReleaseDate());
        return versionDataAssembler.doToVersionDetailDTO(productVersionMapper.selectByPrimaryKey(productVersionRelease.getVersionId()));
    }

    @Override
    public ProductVersionDetailDTO revokeReleaseVersion(Long projectId, Long versionId) {
        productVersionRule.isRevokeRelease(projectId, versionId);
        return productVersionUpdateAssembler.entityToDto(productVersionRepository.revokeReleaseVersion(projectId, versionId));
    }

    @Override
    public VersionMessageDTO queryDeleteMessageByVersionId(Long projectId, Long versionId) {
        VersionMessageDTO versionDeleteMessage = new VersionMessageDTO();
        versionDeleteMessage.setIssueCount(productVersionMapper.querySimpleIssueCount(projectId, versionId));
        versionDeleteMessage.setVersionNames(versionStatisticsAssembler.doListToVersionNameDto(productVersionMapper.queryVersionNames(projectId, versionId)));
        return versionDeleteMessage;
    }

    @Override
    public List<ProductVersionNameDTO> queryNameByProjectId(Long projectId) {
        return versionStatisticsAssembler.doListToVersionNameDto(productVersionMapper.queryAllVersionNames(projectId));
    }

    @Override
    public List<ProductVersionDTO> listByProjectId(Long projectId) {
        return ConvertHelper.convertList(productVersionMapper.listByProjectId(projectId), ProductVersionDTO.class) ;
    }
}

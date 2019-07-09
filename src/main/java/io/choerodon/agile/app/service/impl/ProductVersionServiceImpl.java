package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSON;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.api.validator.ProductVersionValidator;
import io.choerodon.agile.app.assembler.*;
import io.choerodon.agile.app.service.IssueService;
import io.choerodon.agile.app.service.ProductVersionService;
import io.choerodon.agile.domain.agile.converter.ProductVersionConverter;
import io.choerodon.agile.domain.agile.entity.ProductVersionE;
import io.choerodon.agile.api.vo.event.VersionPayload;
import io.choerodon.agile.infra.common.utils.PageUtil;
import io.choerodon.agile.infra.repository.ProductVersionRepository;
import io.choerodon.agile.infra.repository.VersionIssueRelRepository;
import io.choerodon.agile.infra.common.enums.SchemeApplyType;
import io.choerodon.agile.infra.dataobject.IssueCountDO;
import io.choerodon.agile.infra.dataobject.ProductVersionDO;
import io.choerodon.agile.infra.dataobject.VersionIssueDO;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.feign.StateMachineFeignClient;
import io.choerodon.agile.infra.mapper.ProductVersionMapper;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import io.choerodon.asgard.saga.feign.SagaClient;
import io.choerodon.core.convertor.ConvertHelper;

import com.github.pagehelper.PageInfo;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

import com.github.pagehelper.PageHelper;

import io.choerodon.base.domain.PageRequest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * Created by jian_zhang02@163.com on 2018/5/14.
 */
@Service
@Transactional(rollbackFor = Exception.class)
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
    private ProductVersionValidator productVersionValidator;
    @Autowired
    private ProductVersionMapper productVersionMapper;
    @Autowired
    private ProductVersionConverter productVersionConverter;

    @Autowired
    private IssueFeignClient issueFeignClient;

    @Autowired
    private IssueService issueService;

    @Autowired
    private StateMachineFeignClient stateMachineFeignClient;

    private static final String VERSION_PLANNING = "version_planning";
    private static final String NOT_EQUAL_ERROR = "error.projectId.notEqual";
    private static final String NOT_FOUND = "error.version.notFound";
    private static final String CATEGORY_DONE_CODE = "done";
    private static final String CATEGORY_TODO_CODE = "todo";
    private static final String CATEGORY_DOING_CODE = "doing";
    private static final String VERSION_ARCHIVED_CODE = "archived";
    private static final String REVOKE_ARCHIVED_ERROR = "error.productVersion.revokeArchived";
    private static final String ARCHIVED_ERROR = "error.productVersion.archived";
    private static final String VERSION_STATUS_RELEASE_CODE = "released";
    private static final String REVOKE_RELEASE_ERROR = "error.productVersion.revokeRelease";
    private static final String SOURCE_VERSION_ERROR = "error.sourceVersionIds.notNull";
    private static final String FIX_RELATION_TYPE = "fix";
    private static final String INFLUENCE_RELATION_TYPE = "influence";

    private SagaClient sagaClient;

    @Autowired
    public ProductVersionServiceImpl(SagaClient sagaClient) {
        this.sagaClient = sagaClient;
    }

    public void setSagaClient(SagaClient sagaClient) {
        this.sagaClient = sagaClient;
    }

    @Saga(code = "agile-create-version", description = "创建版本", inputSchemaClass = VersionPayload.class)
    @Override
    public synchronized ProductVersionDetailDTO createVersion(Long projectId, ProductVersionCreateDTO versionCreateDTO) {
        try {
            if (!projectId.equals(versionCreateDTO.getProjectId())) {
                throw new CommonException(NOT_EQUAL_ERROR);
            }
            ProductVersionE productVersionE = productVersionCreateAssembler.toTarget(versionCreateDTO, ProductVersionE.class);
            productVersionE.checkDate();
            productVersionValidator.judgeName(productVersionE.getProjectId(), productVersionE.getVersionId(), productVersionE.getName());
            //设置状态
            productVersionE.setStatusCode(VERSION_PLANNING);
            //设置编号
            Integer sequence = productVersionMapper.queryMaxSequenceByProject(projectId);
            productVersionE.setSequence(sequence == null ? 0 : sequence + 1);
            ProductVersionDetailDTO result = new ProductVersionDetailDTO();
            ProductVersionE query = productVersionRepository.createVersion(productVersionE);
            BeanUtils.copyProperties(query, result);
            VersionPayload versionPayload = new VersionPayload();
            versionPayload.setVersionId(query.getVersionId());
            versionPayload.setProjectId(query.getProjectId());
            sagaClient.startSaga("agile-create-version", new StartInstanceDTO(JSON.toJSONString(versionPayload), "", "", ResourceLevel.PROJECT.value(), projectId));
            return result;
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }

    }

    @Override
    public Boolean deleteVersion(Long projectId, Long versionId, Long targetVersionId) {
        productVersionValidator.judgeExist(projectId, targetVersionId);
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        if (targetVersionId != null && !Objects.equals(targetVersionId, 0L)) {
            List<VersionIssueDO> versionFixIssues = productVersionMapper.queryIssuesByRelationType(projectId, versionId, FIX_RELATION_TYPE);
            if (versionFixIssues != null && !versionFixIssues.isEmpty()) {
                productVersionRepository.batchIssueToDestination(projectId, targetVersionId, versionFixIssues, new Date(), customUserDetails.getUserId());
            }
            List<VersionIssueDO> versionInfIssues = productVersionMapper.queryIssuesByRelationType(projectId, versionId, INFLUENCE_RELATION_TYPE);
            if (versionInfIssues != null && !versionInfIssues.isEmpty()) {
                productVersionRepository.batchIssueToDestination(projectId, targetVersionId, versionInfIssues, new Date(), customUserDetails.getUserId());
            }
        }
        versionIssueRelRepository.deleteByVersionId(projectId, versionId);
        return simpleDeleteVersion(projectId, versionId);
    }

    @Saga(code = "agile-delete-version", description = "删除版本", inputSchemaClass = VersionPayload.class)
    private Boolean simpleDeleteVersion(Long projectId, Long versionId) {
        try {
            ProductVersionDO versionDO = new ProductVersionDO();
            versionDO.setProjectId(projectId);
            versionDO.setVersionId(versionId);
            ProductVersionE versionE = productVersionCreateAssembler.toTarget(productVersionMapper.selectOne(versionDO), ProductVersionE.class);
            if (versionE == null) {
                throw new CommonException(NOT_FOUND);
            }
            Boolean deleteResult = productVersionRepository.deleteVersion(versionE);
            VersionPayload versionPayload = new VersionPayload();
            versionPayload.setVersionId(versionE.getVersionId());
            versionPayload.setProjectId(versionE.getProjectId());
            sagaClient.startSaga("agile-delete-version", new StartInstanceDTO(JSON.toJSONString(versionPayload), "", "", ResourceLevel.PROJECT.value(), projectId));
            return deleteResult;
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public ProductVersionDetailDTO updateVersion(Long projectId, Long versionId, ProductVersionUpdateDTO versionUpdateDTO, List<String> fieldList) {
        if (!projectId.equals(versionUpdateDTO.getProjectId())) {
            throw new CommonException(NOT_EQUAL_ERROR);
        }
        ProductVersionE productVersionE = productVersionUpdateAssembler.toTarget(versionUpdateDTO, ProductVersionE.class);
        productVersionE.checkDate();
        productVersionValidator.judgeName(productVersionE.getProjectId(), productVersionE.getVersionId(), productVersionE.getName());
        productVersionE.setVersionId(versionId);
        return productVersionUpdateAssembler.toTarget(productVersionRepository.updateVersion(productVersionE, fieldList), ProductVersionDetailDTO.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public PageInfo<ProductVersionPageDTO> queryByProjectId(Long projectId, PageRequest pageRequest, SearchDTO searchDTO) {
        //过滤查询和排序
        PageInfo<Long> versionIds = PageHelper.startPage(pageRequest.getPage(),
                pageRequest.getSize(), PageUtil.sortToSql(pageRequest.getSort())).doSelectPageInfo(() -> productVersionMapper.
                queryVersionIdsByProjectId(projectId, searchDTO.getSearchArgs(),
                        searchDTO.getAdvancedSearchArgs(), searchDTO.getContents()));
        if ((versionIds.getList() != null) && !versionIds.getList().isEmpty()) {
            return PageUtil.buildPageInfoWithPageInfoList(versionIds, productVersionPageAssembler.toTargetList(productVersionMapper.
                    queryVersionByIds(projectId, versionIds.getList()), ProductVersionPageDTO.class));
        } else {
            return new PageInfo<>(new ArrayList<>());
        }
    }

    @Override
    public Boolean repeatName(Long projectId, String name) {
        return productVersionMapper.isRepeatName(projectId, name);
    }

    @Override
    public List<ProductVersionDataDTO> queryVersionByProjectId(Long projectId) {
        List<ProductVersionDataDTO> productVersions = versionDataAssembler.toTargetList(productVersionMapper.queryVersionByProjectId(projectId), ProductVersionDataDTO.class);
        if (!productVersions.isEmpty()) {
            List<Long> productVersionIds = productVersions.stream().map(ProductVersionDataDTO::getVersionId).collect(toList());
            Map<String, List<Long>> statusMap = issueFeignClient.queryStatusByProjectId(projectId, SchemeApplyType.AGILE).getBody()
                    .stream().collect(Collectors.groupingBy(StatusMapVO::getType, Collectors.mapping(StatusMapVO::getId, Collectors.toList())));
            List<Long> done = statusMap.get(CATEGORY_DONE_CODE);
            Boolean condition = done != null && !done.isEmpty();
            Map<Long, Integer> doneIssueCountMap = condition ? productVersionMapper.queryIssueCount(projectId, productVersionIds, done).stream().collect(toMap(IssueCountDO::getId, IssueCountDO::getIssueCount)) : null;
            Map<Long, Integer> issueCountMap = productVersionMapper.queryIssueCount(projectId, productVersionIds, null).stream().collect(toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            Map<Long, Integer> notEstimateMap = productVersionMapper.queryNotEstimate(projectId, productVersionIds).stream().collect(toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            Map<Long, Integer> totalEstimateMap = productVersionMapper.queryTotalEstimate(projectId, productVersionIds).stream().collect(toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            productVersions.forEach(productVersion -> {
                productVersion.setIssueCount(issueCountMap.get(productVersion.getVersionId()));
                productVersion.setDoneIssueCount(condition ? doneIssueCountMap.get(productVersion.getVersionId()) : null);
                productVersion.setNotEstimate(notEstimateMap.get(productVersion.getVersionId()));
                productVersion.setTotalEstimate(totalEstimateMap.get(productVersion.getVersionId()));
            });
        }
        return productVersions;
    }

    @Override
    public ProductVersionStatisticsDTO queryVersionStatisticsByVersionId(Long projectId, Long versionId) {
        ProductVersionStatisticsDTO productVersionStatisticsDTO = versionStatisticsAssembler.toTarget(productVersionMapper.queryVersionStatisticsByVersionId(projectId, versionId), ProductVersionStatisticsDTO.class);
        List<StatusMapVO> statusMapVOS = issueFeignClient.queryStatusByProjectId(projectId, SchemeApplyType.AGILE).getBody();
        Map<String, List<Long>> statusIdMap = statusMapVOS.stream().collect(Collectors.groupingBy(StatusMapVO::getType, Collectors.mapping(StatusMapVO::getId, Collectors.toList())));
        Map<String, List<StatusMapVO>> statusMap = statusMapVOS.stream().collect(Collectors.groupingBy(StatusMapVO::getType));
        productVersionStatisticsDTO.setTodoIssueCount(statusIdMap.get(CATEGORY_TODO_CODE) != null && !statusIdMap.get(CATEGORY_TODO_CODE).isEmpty() ? productVersionMapper.queryStatusIssueCount(statusIdMap.get(CATEGORY_TODO_CODE), projectId, versionId) : 0);
        productVersionStatisticsDTO.setDoingIssueCount(statusIdMap.get(CATEGORY_DOING_CODE) != null && !statusIdMap.get(CATEGORY_DOING_CODE).isEmpty() ? productVersionMapper.queryStatusIssueCount(statusIdMap.get(CATEGORY_DOING_CODE), projectId, versionId) : 0);
        productVersionStatisticsDTO.setDoneIssueCount(statusIdMap.get(CATEGORY_DONE_CODE) != null && !statusIdMap.get(CATEGORY_DONE_CODE).isEmpty() ? productVersionMapper.queryStatusIssueCount(statusIdMap.get(CATEGORY_DONE_CODE), projectId, versionId) : 0);
        productVersionStatisticsDTO.setIssueCount(productVersionStatisticsDTO.getTodoIssueCount() + productVersionStatisticsDTO.getDoingIssueCount() + productVersionStatisticsDTO.getDoneIssueCount());
        productVersionStatisticsDTO.setTodoStatuses(statusMap.get(CATEGORY_TODO_CODE));
        productVersionStatisticsDTO.setDoingStatuses(statusMap.get(CATEGORY_DOING_CODE));
        productVersionStatisticsDTO.setDoneStatuses(statusMap.get(CATEGORY_DONE_CODE));
        return productVersionStatisticsDTO;
    }

    @Override
    public List<IssueListDTO> queryIssueByVersionIdAndStatusCode(Long projectId, Long versionId, String statusCode, Long organizationId, SearchDTO searchDTO) {
        //处理用户搜索
        Boolean condition = issueService.handleSearchUser(searchDTO, projectId);
        if (condition) {
            Map<Long, PriorityDTO> priorityMap = issueFeignClient.queryByOrganizationId(organizationId).getBody();
            Map<Long, StatusMapVO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
            Map<Long, IssueTypeDTO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
            List<Long> filterStatusIds = new ArrayList<>();
            if (statusCode != null) {
                for (Long key : statusMapDTOMap.keySet()) {
                    if (statusCode.equals(statusMapDTOMap.get(key).getType())) {
                        filterStatusIds.add(key);
                    }
                }
            }
            return issueAssembler.issueDoToIssueListDto(productVersionMapper.queryIssueByVersionIdAndStatusCode(projectId, versionId, statusCode, filterStatusIds, searchDTO), priorityMap, statusMapDTOMap, issueTypeDTOMap);
        } else {
            return new ArrayList<>();

        }
    }

    @Override
    public VersionMessageDTO queryReleaseMessageByVersionId(Long projectId, Long versionId) {
        VersionMessageDTO versionReleaseMessage = new VersionMessageDTO();
        versionReleaseMessage.setFixIssueCount(productVersionMapper.queryNotDoneIssueCount(projectId, versionId));
        versionReleaseMessage.setVersionNames(versionStatisticsAssembler.
                toTargetList(productVersionMapper.queryPlanVersionNames(projectId, versionId), ProductVersionNameDTO.class));
        return versionReleaseMessage;
    }

    @Override
    public ProductVersionDetailDTO releaseVersion(Long projectId, ProductVersionReleaseDTO productVersionRelease) {
        if (!Objects.equals(projectId, productVersionRelease.getProjectId())) {
            throw new CommonException(NOT_EQUAL_ERROR);
        }
        productVersionValidator.isRelease(projectId, productVersionRelease);
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        if (productVersionRelease.getTargetVersionId() != null && !Objects.equals(productVersionRelease.getTargetVersionId(), 0L)) {
            List<VersionIssueDO> incompleteIssues = productVersionMapper.queryIncompleteIssues(projectId, productVersionRelease.getVersionId());
            if (!incompleteIssues.isEmpty()) {
                versionIssueRelRepository.deleteIncompleteIssueByVersionId(projectId, productVersionRelease.getVersionId());
                productVersionRepository.batchIssueToDestination(projectId, productVersionRelease.getTargetVersionId(), incompleteIssues, new Date(), customUserDetails.getUserId());
            }
        }
        productVersionRepository.releaseVersion(projectId, productVersionRelease.getVersionId(), productVersionRelease.getReleaseDate());
        return versionDataAssembler.toTarget(productVersionMapper.selectByPrimaryKey(productVersionRelease.getVersionId()), ProductVersionDetailDTO.class);
    }

    @Override
    public ProductVersionDetailDTO revokeReleaseVersion(Long projectId, Long versionId) {
        ProductVersionDO versionDO = new ProductVersionDO();
        versionDO.setProjectId(projectId);
        versionDO.setVersionId(versionId);
        ProductVersionE versionE = productVersionCreateAssembler.toTarget(productVersionMapper.selectOne(versionDO), ProductVersionE.class);
        if (versionE == null || !Objects.equals(versionE.getStatusCode(), VERSION_STATUS_RELEASE_CODE)) {
            throw new CommonException(REVOKE_RELEASE_ERROR);
        }
        versionE.revokeReleaseVersion();
        return productVersionUpdateAssembler.toTarget(productVersionRepository.updateVersion(versionE), ProductVersionDetailDTO.class);
    }

    @Override
    public VersionMessageDTO queryDeleteMessageByVersionId(Long projectId, Long versionId) {
        VersionMessageDTO versionDeleteMessage = new VersionMessageDTO();
        versionDeleteMessage.setAgileIssueCount(productVersionMapper.queryIssueCountByApplyType(projectId, versionId, SchemeApplyType.AGILE));
        versionDeleteMessage.setTestCaseCount(productVersionMapper.queryIssueCountByApplyType(projectId, versionId, SchemeApplyType.TEST));
        versionDeleteMessage.setVersionNames(versionStatisticsAssembler.
                toTargetList(productVersionMapper.queryVersionNames(projectId, versionId), ProductVersionNameDTO.class));
        return versionDeleteMessage;
    }

    @Override
    public List<ProductVersionNameDTO> queryNameByOptions(Long projectId, List<String> statusCodes) {
        return versionStatisticsAssembler.toTargetList(productVersionMapper.queryNameByOptions(projectId, statusCodes), ProductVersionNameDTO.class);
    }

    @Override
    public List<ProductVersionDTO> listByProjectId(Long projectId) {
        return ConvertHelper.convertList(productVersionMapper.listByProjectId(projectId), ProductVersionDTO.class);
    }

    @Override
    public ProductVersionDetailDTO archivedVersion(Long projectId, Long versionId) {
        ProductVersionDO versionDO = new ProductVersionDO();
        versionDO.setProjectId(projectId);
        versionDO.setVersionId(versionId);
        ProductVersionE versionE = productVersionCreateAssembler.toTarget(productVersionMapper.selectOne(versionDO), ProductVersionE.class);
        if (versionE == null || Objects.equals(versionE.getStatusCode(), VERSION_ARCHIVED_CODE)) {
            throw new CommonException(ARCHIVED_ERROR);
        }
        versionE.archivedVersion();
        return productVersionUpdateAssembler.toTarget(productVersionRepository.updateVersion(versionE), ProductVersionDetailDTO.class);
    }

    @Override
    public ProductVersionDetailDTO revokeArchivedVersion(Long projectId, Long versionId) {
        ProductVersionDO versionDO = new ProductVersionDO();
        versionDO.setProjectId(projectId);
        versionDO.setVersionId(versionId);
        ProductVersionE versionE = productVersionCreateAssembler.toTarget(productVersionMapper.selectOne(versionDO), ProductVersionE.class);
        if (versionE == null || !Objects.equals(versionE.getStatusCode(), VERSION_ARCHIVED_CODE)) {
            throw new CommonException(REVOKE_ARCHIVED_ERROR);
        }
        versionE.revokeArchivedVersion();
        return productVersionUpdateAssembler.toTarget(productVersionRepository.updateVersion(versionE), ProductVersionDetailDTO.class);
    }

    @Override
    @Saga(code = "agile-delete-version", description = "删除版本", inputSchemaClass = VersionPayload.class)
    public Boolean mergeVersion(Long projectId, ProductVersionMergeDTO productVersionMergeDTO) {
        productVersionMergeDTO.getSourceVersionIds().remove(productVersionMergeDTO.getTargetVersionId());
        if (productVersionMergeDTO.getSourceVersionIds().isEmpty()) {
            throw new CommonException(SOURCE_VERSION_ERROR);
        }
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        List<VersionIssueDO> versionIssues = productVersionMapper.queryIssueByVersionIds(projectId, productVersionMergeDTO.getSourceVersionIds(), productVersionMergeDTO.getTargetVersionId());
        versionIssueRelRepository.deleteByVersionIds(projectId, productVersionMergeDTO.getSourceVersionIds());
        if (!versionIssues.isEmpty()) {
            productVersionRepository.batchIssueToDestination(projectId, productVersionMergeDTO.getTargetVersionId(), versionIssues, new Date(), customUserDetails.getUserId());
        }
        //这里不用日志是因为deleteByVersionIds方法已经有删除的日志了
        productVersionRepository.deleteByVersionIds(projectId, productVersionMergeDTO.getSourceVersionIds());
        productVersionMergeDTO.getSourceVersionIds().forEach(versionId -> {
            VersionPayload versionPayload = new VersionPayload();
            versionPayload.setVersionId(versionId);
            versionPayload.setProjectId(projectId);
            sagaClient.startSaga("agile-delete-version", new StartInstanceDTO(JSON.toJSONString(versionPayload), "", "", ResourceLevel.PROJECT.value(), projectId));
        });
        return true;
    }

    @Override
    public ProductVersionDetailDTO queryVersionByVersionId(Long projectId, Long versionId) {
        ProductVersionDO productVersionDO = new ProductVersionDO();
        productVersionDO.setProjectId(projectId);
        productVersionDO.setVersionId(versionId);
        return versionDataAssembler.toTarget(productVersionMapper.selectOne(productVersionDO), ProductVersionDetailDTO.class);
    }

    @Override
    public List<Long> listIds(Long projectId) {
        return productVersionMapper.listIds();
    }

    @Override
    public synchronized ProductVersionPageDTO dragVersion(Long projectId, VersionSequenceDTO versionSequenceDTO) {
        if (versionSequenceDTO.getAfterSequence() == null && versionSequenceDTO.getBeforeSequence() == null) {
            throw new CommonException("error.dragVersion.noSequence");
        }
        ProductVersionE productVersionE = productVersionConverter.doToEntity(queryVersionByProjectIdAndVersionId(
                versionSequenceDTO.getVersionId(), projectId));
        if (productVersionE == null) {
            throw new CommonException(NOT_FOUND);
        } else {
            if (versionSequenceDTO.getAfterSequence() == null) {
                Integer maxSequence = productVersionMapper.queryMaxAfterSequence(versionSequenceDTO.getBeforeSequence(), projectId);
                versionSequenceDTO.setAfterSequence(maxSequence);
            } else if (versionSequenceDTO.getBeforeSequence() == null) {
                Integer minSequence = productVersionMapper.queryMinBeforeSequence(versionSequenceDTO.getAfterSequence(), projectId);
                versionSequenceDTO.setBeforeSequence(minSequence);
            }
            handleSequence(versionSequenceDTO, projectId, productVersionE);
        }
        return productVersionPageAssembler.toTarget(queryVersionByProjectIdAndVersionId(
                versionSequenceDTO.getVersionId(), projectId), ProductVersionPageDTO.class);
    }

    private void handleSequence(VersionSequenceDTO versionSequenceDTO, Long projectId, ProductVersionE productVersionE) {
        if (versionSequenceDTO.getBeforeSequence() == null) {
            productVersionE.setSequence(versionSequenceDTO.getAfterSequence() + 1);
            productVersionRepository.updateVersion(productVersionE);
        } else if (versionSequenceDTO.getAfterSequence() == null) {
            if (productVersionE.getSequence() > versionSequenceDTO.getBeforeSequence()) {
                Integer add = productVersionE.getSequence() - versionSequenceDTO.getBeforeSequence();
                if (add > 0) {
                    productVersionE.setSequence(versionSequenceDTO.getBeforeSequence() - 1);
                    productVersionRepository.updateVersion(productVersionE);
                } else {
                    productVersionRepository.batchUpdateSequence(versionSequenceDTO.getBeforeSequence(), projectId,
                            productVersionE.getSequence() - versionSequenceDTO.getBeforeSequence() + 1, productVersionE.getVersionId());
                }
            }
        } else {
            Integer sequence = versionSequenceDTO.getAfterSequence() + 1;
            productVersionE.setSequence(sequence);
            productVersionRepository.updateVersion(productVersionE);
            Integer update = sequence - versionSequenceDTO.getBeforeSequence();
            if (update >= 0) {
                productVersionRepository.batchUpdateSequence(versionSequenceDTO.getBeforeSequence(), projectId, update + 1, productVersionE.getVersionId());
            }
        }
    }


    private ProductVersionDO queryVersionByProjectIdAndVersionId(Long versionId, Long projectId) {
        ProductVersionDO productVersionDO = new ProductVersionDO();
        productVersionDO.setVersionId(versionId);
        productVersionDO.setProjectId(projectId);
        return productVersionMapper.selectOne(productVersionDO);
    }

    @Override
    public VersionIssueCountDTO queryByCategoryCode(Long projectId, Long versionId) {
        return ConvertHelper.convert(productVersionMapper.queryVersionStatisticsByVersionId(projectId, versionId), VersionIssueCountDTO.class);
    }

    @Override
    public Long queryProjectIdByVersionId(Long projectId, Long versionId) {
        ProductVersionDO productVersionDO = productVersionMapper.selectByPrimaryKey(versionId);
        if (productVersionDO == null) {
            throw new CommonException("error.productVersion.get");
        }
        return productVersionDO.getProjectId();
    }
}

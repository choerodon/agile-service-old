package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSON;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.api.validator.ProductVersionValidator;
import io.choerodon.agile.app.assembler.*;
import io.choerodon.agile.app.service.IssueService;
import io.choerodon.agile.app.service.ProductVersionService;
import io.choerodon.agile.domain.agile.converter.ProductVersionConverter;
import io.choerodon.agile.api.vo.event.VersionPayload;
import io.choerodon.agile.infra.common.annotation.DataLog;
import io.choerodon.agile.infra.common.utils.PageUtil;
import io.choerodon.agile.infra.common.utils.RedisUtil;
import io.choerodon.agile.infra.dataobject.ProductVersionDTO;
import io.choerodon.agile.infra.repository.ProductVersionRepository;
import io.choerodon.agile.app.service.VersionIssueRelService;
import io.choerodon.agile.infra.common.enums.SchemeApplyType;
import io.choerodon.agile.infra.dataobject.IssueCountDO;
import io.choerodon.agile.infra.dataobject.VersionIssueDTO;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.feign.StateMachineFeignClient;
import io.choerodon.agile.infra.mapper.ProductVersionMapper;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import io.choerodon.asgard.saga.feign.SagaClient;

import com.github.pagehelper.PageInfo;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

import com.github.pagehelper.PageHelper;

import io.choerodon.base.domain.PageRequest;

import io.choerodon.mybatis.entity.Criteria;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
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

    private static final String INSERT_ERROR = "error.version.insert";
    private static final String DELETE_ERROR = "error.version.delete";
    private static final String UPDATE_ERROR = "error.version.update";
    private static final String AGILE = "Agile:";
    private static final String PIECHART = AGILE + "PieChart";
    private static final String FIX_VERSION = "fixVersion";
    private static final String VERSION_STATUS_PLAN_CODE = "version_planning";

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
    private VersionIssueRelService versionIssueRelService;
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

    @Autowired
    private ProductVersionMapper versionMapper;

    @Autowired
    private RedisUtil redisUtil;


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

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Autowired
    public ProductVersionServiceImpl(SagaClient sagaClient) {
        this.sagaClient = sagaClient;
    }

    public void setSagaClient(SagaClient sagaClient) {
        this.sagaClient = sagaClient;
    }

    @Saga(code = "agile-create-version", description = "创建版本", inputSchemaClass = VersionPayload.class)
    @Override
    public synchronized ProductVersionDetailVO createVersion(Long projectId, ProductVersionCreateVO versionCreateVO) {
        try {
            if (!projectId.equals(versionCreateVO.getProjectId())) {
                throw new CommonException(NOT_EQUAL_ERROR);
            }
            ProductVersionDTO productVersionDTO = productVersionCreateAssembler.toTarget(versionCreateVO, ProductVersionDTO.class);
            productVersionValidator.checkDate(productVersionDTO);
            productVersionValidator.judgeName(productVersionDTO.getProjectId(), productVersionDTO.getVersionId(), productVersionDTO.getName());
            //设置状态
            productVersionDTO.setStatusCode(VERSION_PLANNING);
            //设置编号
            Integer sequence = productVersionMapper.queryMaxSequenceByProject(projectId);
            productVersionDTO.setSequence(sequence == null ? 0 : sequence + 1);
            ProductVersionDetailVO result = new ProductVersionDetailVO();
            ProductVersionDTO query = create(productVersionDTO);
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
            List<VersionIssueDTO> versionFixIssues = productVersionMapper.queryIssuesByRelationType(projectId, versionId, FIX_RELATION_TYPE);
            if (versionFixIssues != null && !versionFixIssues.isEmpty()) {
                batchIssueToDestination(projectId, targetVersionId, versionFixIssues, new Date(), customUserDetails.getUserId());
            }
            List<VersionIssueDTO> versionInfIssues = productVersionMapper.queryIssuesByRelationType(projectId, versionId, INFLUENCE_RELATION_TYPE);
            if (versionInfIssues != null && !versionInfIssues.isEmpty()) {
                batchIssueToDestination(projectId, targetVersionId, versionInfIssues, new Date(), customUserDetails.getUserId());
            }
        }
        versionIssueRelService.deleteByVersionId(projectId, versionId);
        return simpleDeleteVersion(projectId, versionId);
    }

    @Saga(code = "agile-delete-version", description = "删除版本", inputSchemaClass = VersionPayload.class)
    private Boolean simpleDeleteVersion(Long projectId, Long versionId) {
        try {
            ProductVersionDTO version = new ProductVersionDTO();
            version.setProjectId(projectId);
            version.setVersionId(versionId);
            ProductVersionDTO versionDTO = productVersionMapper.selectOne(version);
            if (versionDTO == null) {
                throw new CommonException(NOT_FOUND);
            }
            Boolean deleteResult = delete(versionDTO);
            VersionPayload versionPayload = new VersionPayload();
            versionPayload.setVersionId(versionDTO.getVersionId());
            versionPayload.setProjectId(versionDTO.getProjectId());
            sagaClient.startSaga("agile-delete-version", new StartInstanceDTO(JSON.toJSONString(versionPayload), "", "", ResourceLevel.PROJECT.value(), projectId));
            return deleteResult;
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public ProductVersionDetailVO updateVersion(Long projectId, Long versionId, ProductVersionUpdateVO versionUpdateVO, List<String> fieldList) {
        if (!projectId.equals(versionUpdateVO.getProjectId())) {
            throw new CommonException(NOT_EQUAL_ERROR);
        }
        ProductVersionDTO productVersionDTO = productVersionUpdateAssembler.toTarget(versionUpdateVO, ProductVersionDTO.class);
        productVersionValidator.checkDate(productVersionDTO);
        productVersionValidator.judgeName(productVersionDTO.getProjectId(), productVersionDTO.getVersionId(), productVersionDTO.getName());
        productVersionDTO.setVersionId(versionId);
        return productVersionUpdateAssembler.toTarget(update(productVersionDTO, fieldList), ProductVersionDetailVO.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public PageInfo<ProductVersionPageVO> queryByProjectId(Long projectId, PageRequest pageRequest, SearchVO searchVO) {
        //过滤查询和排序
        PageInfo<Long> versionIds = PageHelper.startPage(pageRequest.getPage(),
                pageRequest.getSize(), PageUtil.sortToSql(pageRequest.getSort())).doSelectPageInfo(() -> productVersionMapper.
                queryVersionIdsByProjectId(projectId, searchVO.getSearchArgs(),
                        searchVO.getAdvancedSearchArgs(), searchVO.getContents()));
        if ((versionIds.getList() != null) && !versionIds.getList().isEmpty()) {
            return PageUtil.buildPageInfoWithPageInfoList(versionIds, productVersionPageAssembler.toTargetList(productVersionMapper.
                    queryVersionByIds(projectId, versionIds.getList()), ProductVersionPageVO.class));
        } else {
            return new PageInfo<>(new ArrayList<>());
        }
    }

    @Override
    public Boolean repeatName(Long projectId, String name) {
        return productVersionMapper.isRepeatName(projectId, name);
    }

    @Override
    public List<ProductVersionDataVO> queryVersionByProjectId(Long projectId) {
        List<ProductVersionDataVO> productVersions = versionDataAssembler.toTargetList(productVersionMapper.queryVersionByProjectId(projectId), ProductVersionDataVO.class);
        if (!productVersions.isEmpty()) {
            List<Long> productVersionIds = productVersions.stream().map(ProductVersionDataVO::getVersionId).collect(toList());
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
    public ProductVersionStatisticsVO queryVersionStatisticsByVersionId(Long projectId, Long versionId) {
        ProductVersionStatisticsVO productVersionStatisticsVO = versionStatisticsAssembler.toTarget(productVersionMapper.queryVersionStatisticsByVersionId(projectId, versionId), ProductVersionStatisticsVO.class);
        List<StatusMapVO> statusMapVOS = issueFeignClient.queryStatusByProjectId(projectId, SchemeApplyType.AGILE).getBody();
        Map<String, List<Long>> statusIdMap = statusMapVOS.stream().collect(Collectors.groupingBy(StatusMapVO::getType, Collectors.mapping(StatusMapVO::getId, Collectors.toList())));
        Map<String, List<StatusMapVO>> statusMap = statusMapVOS.stream().collect(Collectors.groupingBy(StatusMapVO::getType));
        productVersionStatisticsVO.setTodoIssueCount(statusIdMap.get(CATEGORY_TODO_CODE) != null && !statusIdMap.get(CATEGORY_TODO_CODE).isEmpty() ? productVersionMapper.queryStatusIssueCount(statusIdMap.get(CATEGORY_TODO_CODE), projectId, versionId) : 0);
        productVersionStatisticsVO.setDoingIssueCount(statusIdMap.get(CATEGORY_DOING_CODE) != null && !statusIdMap.get(CATEGORY_DOING_CODE).isEmpty() ? productVersionMapper.queryStatusIssueCount(statusIdMap.get(CATEGORY_DOING_CODE), projectId, versionId) : 0);
        productVersionStatisticsVO.setDoneIssueCount(statusIdMap.get(CATEGORY_DONE_CODE) != null && !statusIdMap.get(CATEGORY_DONE_CODE).isEmpty() ? productVersionMapper.queryStatusIssueCount(statusIdMap.get(CATEGORY_DONE_CODE), projectId, versionId) : 0);
        productVersionStatisticsVO.setIssueCount(productVersionStatisticsVO.getTodoIssueCount() + productVersionStatisticsVO.getDoingIssueCount() + productVersionStatisticsVO.getDoneIssueCount());
        productVersionStatisticsVO.setTodoStatuses(statusMap.get(CATEGORY_TODO_CODE));
        productVersionStatisticsVO.setDoingStatuses(statusMap.get(CATEGORY_DOING_CODE));
        productVersionStatisticsVO.setDoneStatuses(statusMap.get(CATEGORY_DONE_CODE));
        return productVersionStatisticsVO;
    }

    @Override
    public List<IssueListVO> queryIssueByVersionIdAndStatusCode(Long projectId, Long versionId, String statusCode, Long organizationId, SearchVO searchVO) {
        //处理用户搜索
        Boolean condition = issueService.handleSearchUser(searchVO, projectId);
        if (condition) {
            Map<Long, PriorityVO> priorityMap = issueFeignClient.queryByOrganizationId(organizationId).getBody();
            Map<Long, StatusMapVO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
            Map<Long, IssueTypeVO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
            List<Long> filterStatusIds = new ArrayList<>();
            if (statusCode != null) {
                for (Long key : statusMapDTOMap.keySet()) {
                    if (statusCode.equals(statusMapDTOMap.get(key).getType())) {
                        filterStatusIds.add(key);
                    }
                }
            }
            return issueAssembler.issueDoToIssueListDto(productVersionMapper.queryIssueByVersionIdAndStatusCode(projectId, versionId, statusCode, filterStatusIds, searchVO), priorityMap, statusMapDTOMap, issueTypeDTOMap);
        } else {
            return new ArrayList<>();

        }
    }

    @Override
    public VersionMessageVO queryReleaseMessageByVersionId(Long projectId, Long versionId) {
        VersionMessageVO versionReleaseMessage = new VersionMessageVO();
        versionReleaseMessage.setFixIssueCount(productVersionMapper.queryNotDoneIssueCount(projectId, versionId));
        versionReleaseMessage.setVersionNames(versionStatisticsAssembler.
                toTargetList(productVersionMapper.queryPlanVersionNames(projectId, versionId), ProductVersionNameVO.class));
        return versionReleaseMessage;
    }

    @Override
    public ProductVersionDetailVO releaseVersion(Long projectId, ProductVersionReleaseVO productVersionRelease) {
        if (!Objects.equals(projectId, productVersionRelease.getProjectId())) {
            throw new CommonException(NOT_EQUAL_ERROR);
        }
        productVersionValidator.isRelease(projectId, productVersionRelease);
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        if (productVersionRelease.getTargetVersionId() != null && !Objects.equals(productVersionRelease.getTargetVersionId(), 0L)) {
            List<VersionIssueDTO> incompleteIssues = productVersionMapper.queryIncompleteIssues(projectId, productVersionRelease.getVersionId());
            if (!incompleteIssues.isEmpty()) {
                versionIssueRelService.deleteIncompleteIssueByVersionId(projectId, productVersionRelease.getVersionId());
                batchIssueToDestination(projectId, productVersionRelease.getTargetVersionId(), incompleteIssues, new Date(), customUserDetails.getUserId());
            }
        }
        release(projectId, productVersionRelease.getVersionId(), productVersionRelease.getReleaseDate());
        return versionDataAssembler.toTarget(productVersionMapper.selectByPrimaryKey(productVersionRelease.getVersionId()), ProductVersionDetailVO.class);
    }

    @Override
    public ProductVersionDetailVO revokeReleaseVersion(Long projectId, Long versionId) {
        ProductVersionDTO versionDTO = new ProductVersionDTO();
        versionDTO.setProjectId(projectId);
        versionDTO.setVersionId(versionId);
        ProductVersionDTO version = productVersionCreateAssembler.toTarget(productVersionMapper.selectOne(versionDTO), ProductVersionDTO.class);
        if (version == null || !Objects.equals(version.getStatusCode(), VERSION_STATUS_RELEASE_CODE)) {
            throw new CommonException(REVOKE_RELEASE_ERROR);
        }
//        versionE.revokeReleaseVersion();
        version.setOldStatusCode(version.getStatusCode());
        version.setStatusCode(VERSION_STATUS_PLAN_CODE);
        return productVersionUpdateAssembler.toTarget(update(version), ProductVersionDetailVO.class);
    }

    @Override
    public VersionMessageVO queryDeleteMessageByVersionId(Long projectId, Long versionId) {
        VersionMessageVO versionDeleteMessage = new VersionMessageVO();
        versionDeleteMessage.setAgileIssueCount(productVersionMapper.queryIssueCountByApplyType(projectId, versionId, SchemeApplyType.AGILE));
        versionDeleteMessage.setTestCaseCount(productVersionMapper.queryIssueCountByApplyType(projectId, versionId, SchemeApplyType.TEST));
        versionDeleteMessage.setVersionNames(versionStatisticsAssembler.
                toTargetList(productVersionMapper.queryVersionNames(projectId, versionId), ProductVersionNameVO.class));
        return versionDeleteMessage;
    }

    @Override
    public List<ProductVersionNameVO> queryNameByOptions(Long projectId, List<String> statusCodes) {
        return versionStatisticsAssembler.toTargetList(productVersionMapper.queryNameByOptions(projectId, statusCodes), ProductVersionNameVO.class);
    }

    @Override
    public List<ProductVersionVO> listByProjectId(Long projectId) {
        return modelMapper.map(productVersionMapper.listByProjectId(projectId), new TypeToken<List<ProductVersionVO>>(){}.getType());
    }

    @Override
    public ProductVersionDetailVO archivedVersion(Long projectId, Long versionId) {
        ProductVersionDTO versionDTO = new ProductVersionDTO();
        versionDTO.setProjectId(projectId);
        versionDTO.setVersionId(versionId);
        ProductVersionDTO version = productVersionCreateAssembler.toTarget(productVersionMapper.selectOne(versionDTO), ProductVersionDTO.class);
        if (version == null || Objects.equals(version.getStatusCode(), VERSION_ARCHIVED_CODE)) {
            throw new CommonException(ARCHIVED_ERROR);
        }
//        versionE.archivedVersion();
        version.setOldStatusCode(version.getStatusCode());
        version.setStatusCode(VERSION_ARCHIVED_CODE);
        return productVersionUpdateAssembler.toTarget(update(version), ProductVersionDetailVO.class);
    }

    @Override
    public ProductVersionDetailVO revokeArchivedVersion(Long projectId, Long versionId) {
        ProductVersionDTO versionDTO = new ProductVersionDTO();
        versionDTO.setProjectId(projectId);
        versionDTO.setVersionId(versionId);
        ProductVersionDTO version = productVersionCreateAssembler.toTarget(productVersionMapper.selectOne(versionDTO), ProductVersionDTO.class);
        if (version == null || !Objects.equals(version.getStatusCode(), VERSION_ARCHIVED_CODE)) {
            throw new CommonException(REVOKE_ARCHIVED_ERROR);
        }
//        versionE.revokeArchivedVersion();
        version.setStatusCode(version.getOldStatusCode());
        version.setOldStatusCode(VERSION_ARCHIVED_CODE);
        return productVersionUpdateAssembler.toTarget(update(version), ProductVersionDetailVO.class);
    }

    @Override
    @Saga(code = "agile-delete-version", description = "删除版本", inputSchemaClass = VersionPayload.class)
    public Boolean mergeVersion(Long projectId, ProductVersionMergeVO productVersionMergeVO) {
        productVersionMergeVO.getSourceVersionIds().remove(productVersionMergeVO.getTargetVersionId());
        if (productVersionMergeVO.getSourceVersionIds().isEmpty()) {
            throw new CommonException(SOURCE_VERSION_ERROR);
        }
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        List<VersionIssueDTO> versionIssues = productVersionMapper.queryIssueByVersionIds(projectId, productVersionMergeVO.getSourceVersionIds(), productVersionMergeVO.getTargetVersionId());
        versionIssueRelService.deleteByVersionIds(projectId, productVersionMergeVO.getSourceVersionIds());
        if (!versionIssues.isEmpty()) {
            batchIssueToDestination(projectId, productVersionMergeVO.getTargetVersionId(), versionIssues, new Date(), customUserDetails.getUserId());
        }
        //这里不用日志是因为deleteByVersionIds方法已经有删除的日志了
        deleteByVersionIds(projectId, productVersionMergeVO.getSourceVersionIds());
        productVersionMergeVO.getSourceVersionIds().forEach(versionId -> {
            VersionPayload versionPayload = new VersionPayload();
            versionPayload.setVersionId(versionId);
            versionPayload.setProjectId(projectId);
            sagaClient.startSaga("agile-delete-version", new StartInstanceDTO(JSON.toJSONString(versionPayload), "", "", ResourceLevel.PROJECT.value(), projectId));
        });
        return true;
    }

    @Override
    public ProductVersionDetailVO queryVersionByVersionId(Long projectId, Long versionId) {
        ProductVersionDTO productVersionDTO = new ProductVersionDTO();
        productVersionDTO.setProjectId(projectId);
        productVersionDTO.setVersionId(versionId);
        return versionDataAssembler.toTarget(productVersionMapper.selectOne(productVersionDTO), ProductVersionDetailVO.class);
    }

    @Override
    public List<Long> listIds(Long projectId) {
        return productVersionMapper.listIds();
    }

    @Override
    public synchronized ProductVersionPageVO dragVersion(Long projectId, VersionSequenceVO versionSequenceVO) {
        if (versionSequenceVO.getAfterSequence() == null && versionSequenceVO.getBeforeSequence() == null) {
            throw new CommonException("error.dragVersion.noSequence");
        }
        ProductVersionDTO productVersionDTO = modelMapper.map(queryVersionByProjectIdAndVersionId(
                versionSequenceVO.getVersionId(), projectId), ProductVersionDTO.class);
        if (productVersionDTO == null) {
            throw new CommonException(NOT_FOUND);
        } else {
            if (versionSequenceVO.getAfterSequence() == null) {
                Integer maxSequence = productVersionMapper.queryMaxAfterSequence(versionSequenceVO.getBeforeSequence(), projectId);
                versionSequenceVO.setAfterSequence(maxSequence);
            } else if (versionSequenceVO.getBeforeSequence() == null) {
                Integer minSequence = productVersionMapper.queryMinBeforeSequence(versionSequenceVO.getAfterSequence(), projectId);
                versionSequenceVO.setBeforeSequence(minSequence);
            }
            handleSequence(versionSequenceVO, projectId, productVersionDTO);
        }
        return productVersionPageAssembler.toTarget(queryVersionByProjectIdAndVersionId(
                versionSequenceVO.getVersionId(), projectId), ProductVersionPageVO.class);
    }

    private void handleSequence(VersionSequenceVO versionSequenceVO, Long projectId, ProductVersionDTO productVersionDTO) {
        if (versionSequenceVO.getBeforeSequence() == null) {
            productVersionDTO.setSequence(versionSequenceVO.getAfterSequence() + 1);
            update(productVersionDTO);
        } else if (versionSequenceVO.getAfterSequence() == null) {
            if (productVersionDTO.getSequence() > versionSequenceVO.getBeforeSequence()) {
                Integer add = productVersionDTO.getSequence() - versionSequenceVO.getBeforeSequence();
                if (add > 0) {
                    productVersionDTO.setSequence(versionSequenceVO.getBeforeSequence() - 1);
                    update(productVersionDTO);
                } else {
                    batchUpdateSequence(versionSequenceVO.getBeforeSequence(), projectId,
                            productVersionDTO.getSequence() - versionSequenceVO.getBeforeSequence() + 1, productVersionDTO.getVersionId());
                }
            }
        } else {
            Integer sequence = versionSequenceVO.getAfterSequence() + 1;
            productVersionDTO.setSequence(sequence);
            update(productVersionDTO);
            Integer update = sequence - versionSequenceVO.getBeforeSequence();
            if (update >= 0) {
                batchUpdateSequence(versionSequenceVO.getBeforeSequence(), projectId, update + 1, productVersionDTO.getVersionId());
            }
        }
    }


    private ProductVersionDTO queryVersionByProjectIdAndVersionId(Long versionId, Long projectId) {
        ProductVersionDTO productVersionDTO = new ProductVersionDTO();
        productVersionDTO.setVersionId(versionId);
        productVersionDTO.setProjectId(projectId);
        return productVersionMapper.selectOne(productVersionDTO);
    }

    @Override
    public VersionIssueCountVO queryByCategoryCode(Long projectId, Long versionId) {
        return modelMapper.map(productVersionMapper.queryVersionStatisticsByVersionId(projectId, versionId), VersionIssueCountVO.class);
    }

    @Override
    public Long queryProjectIdByVersionId(Long projectId, Long versionId) {
        ProductVersionDTO productVersionDTO = productVersionMapper.selectByPrimaryKey(versionId);
        if (productVersionDTO == null) {
            throw new CommonException("error.productVersion.get");
        }
        return productVersionDTO.getProjectId();
    }





    @Override
    public ProductVersionDTO create(ProductVersionDTO versionDTO) {
        if (versionMapper.insertSelective(versionDTO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        redisUtil.deleteRedisCache(new String[]{PIECHART + versionDTO.getProjectId() + ':' + FIX_VERSION + "*"});
        return versionMapper.selectByPrimaryKey(versionDTO.getVersionId());
    }

    @Override
    @DataLog(type = "batchDeleteVersionByVersion", single = false)
    public Boolean delete(ProductVersionDTO versionDTO) {
        if (versionMapper.delete(versionDTO) != 1) {
            throw new CommonException(DELETE_ERROR);
        }
        return true;
    }

    @Override
    public ProductVersionDTO update(ProductVersionDTO versionDTO, List<String> fieldList) {
        Criteria criteria = new Criteria();
        criteria.update(fieldList.toArray(new String[0]));
        if (versionMapper.updateByPrimaryKeyOptions(versionDTO,criteria) != 1) {
            throw new CommonException(UPDATE_ERROR);
        }
        redisUtil.deleteRedisCache(new String[]{PIECHART + versionDTO.getProjectId() + ':' + FIX_VERSION + "*"});
        return versionMapper.selectByPrimaryKey(versionDTO.getVersionId());
    }

    @Override
    @DataLog(type = "batchMoveVersion", single = false)
    public Boolean batchIssueToDestination(Long projectId, Long targetVersionId, List<VersionIssueDTO> versionIssues, Date date, Long userId) {
        versionMapper.issueToDestination(projectId, targetVersionId, versionIssues, date, userId);
        return true;
    }

    @Override
    public Boolean release(Long projectId, Long versionId, Date releaseDate) {
        versionMapper.releaseVersion(projectId, versionId, releaseDate);
        return true;
    }

    @Override
    public ProductVersionDTO update(ProductVersionDTO versionDTO) {
        if (versionMapper.updateByPrimaryKeySelective(versionDTO) != 1) {
            throw new CommonException(UPDATE_ERROR);
        }
        redisUtil.deleteRedisCache(new String[]{PIECHART + versionDTO.getProjectId() + ':' + FIX_VERSION + "*"});
        return versionMapper.selectByPrimaryKey(versionDTO.getVersionId());
    }

    @Override
    public int deleteByVersionIds(Long projectId, List<Long> versionIds) {
        redisUtil.deleteRedisCache(new String[]{PIECHART + projectId + ':' + FIX_VERSION + "*"});
        return versionMapper.deleteByVersionIds(projectId, versionIds);
    }

    @Override
    public int batchUpdateSequence(Integer sequence, Long projectId, Integer add, Long versionId) {
        return versionMapper.batchUpdateSequence(sequence, projectId, add, versionId);
    }
}

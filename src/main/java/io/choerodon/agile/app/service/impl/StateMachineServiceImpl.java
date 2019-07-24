package io.choerodon.agile.app.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.api.vo.event.*;
import io.choerodon.agile.app.service.*;
import io.choerodon.agile.infra.cache.InstanceCache;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.enums.*;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.agile.infra.utils.PageUtil;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.entity.Criteria;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.*;

/**
 * @author shinan.chen
 * @date 2018/9/25
 */
@Service
public class StateMachineServiceImpl implements StateMachineService {

    private static final Logger logger = LoggerFactory.getLogger(StateMachineServiceImpl.class);
    private static final String ERROR_STATEMACHINENODE_CREATE = "error.stateMachineNode.create";
    private static final String STATUS = "status";

    @Value("${spring.application.name:default}")
    private String serverCode;
    @Autowired
    private StateMachineSchemeService schemeService;
    @Autowired
    private StateMachineSchemeConfigService stateMachineSchemeConfigService;
    @Autowired
    private ProjectConfigMapper projectConfigMapper;
    @Autowired
    private StateMachineSchemeConfigMapper configMapper;
    @Autowired
    private StateMachineMapper stateMachineMapper;
    @Autowired
    private StateMachineNodeMapper nodeDeployMapper;
    @Autowired
    private StateMachineNodeDraftMapper nodeDraftMapper;
    @Autowired
    private StateMachineNodeService nodeService;
    @Autowired
    private StateMachineTransformMapper transformDeployMapper;
    @Autowired
    private StateMachineTransformDraftMapper transformDraftMapper;
    @Autowired
    private StateMachineConfigMapper configDeployMapper;
    @Autowired
    private StateMachineConfigDraftMapper configDraftMapper;
    @Autowired
    private StatusService statusService;
    @Autowired
    private InstanceCache instanceCache;
    @Autowired
    private IssueMapper issueMapper;
    @Autowired
    private StateMachineService stateMachineService;
    @Autowired
    private IssueStatusService issueStatusService;
    @Autowired
    private BoardColumnService boardColumnService;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PageInfo<StateMachineListVO> pageQuery(Long organizationId, PageRequest pageRequest, String name, String description, String param) {
        StateMachineDTO stateMachine = new StateMachineDTO();
        stateMachine.setName(name);
        stateMachine.setDescription(description);
        stateMachine.setOrganizationId(organizationId);

        PageInfo<StateMachineDTO> page = PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize(), PageUtil.sortToSql(pageRequest.getSort())).doSelectPageInfo(
                () -> stateMachineMapper.fulltextSearch(stateMachine, param));
        List<StateMachineListVO> stateMachineVOS = modelMapper.map(page.getList(), new TypeToken<List<StateMachineListVO>>() {
        }.getType());
        for (StateMachineListVO stateMachineVO : stateMachineVOS) {
            List<StateMachineSchemeVO> list = schemeService.querySchemeByStateMachineId(organizationId, stateMachineVO.getId());
            //列表去重
            List<StateMachineSchemeVO> unique = list.stream().collect(
                    collectingAndThen(
                            toCollection(() -> new TreeSet<>(comparingLong(StateMachineSchemeVO::getId))), ArrayList::new)
            );
            stateMachineVO.setStateMachineSchemeVOS(unique);
        }
        return PageUtil.buildPageInfoWithPageInfoList(page, stateMachineVOS);
    }

    @Override
    public StateMachineVO create(Long organizationId, StateMachineVO stateMachineVO) {
        if (checkName(organizationId, stateMachineVO.getName())) {
            throw new CommonException("error.stateMachineName.exist");
        }
        stateMachineVO.setId(null);
        stateMachineVO.setStatus(StateMachineStatus.CREATE);
        stateMachineVO.setOrganizationId(organizationId);
        stateMachineVO.setDefault(false);
        StateMachineDTO stateMachine = modelMapper.map(stateMachineVO, StateMachineDTO.class);
        int isInsert = stateMachineMapper.insertSelective(stateMachine);
        if (isInsert != 1) {
            throw new CommonException("error.stateMachine.create");
        }

        //创建默认开始节点
        StateMachineNodeDraftDTO startNode = new StateMachineNodeDraftDTO();
        startNode.setStateMachineId(stateMachine.getId());
        startNode.setOrganizationId(organizationId);
        startNode.setStatusId(0L);
        startNode.setPositionX(InitNode.START.getPositionX());
        startNode.setPositionY(InitNode.START.getPositionY());
        startNode.setWidth(InitNode.START.getWidth());
        startNode.setHeight(InitNode.START.getHeight());
        startNode.setType(NodeType.START);
        int isStartNodeInsert = nodeDraftMapper.insert(startNode);
        if (isStartNodeInsert != 1) {
            throw new CommonException(ERROR_STATEMACHINENODE_CREATE);
        }

        //创建默认的初始节点
        StateMachineNodeDraftDTO initNode = new StateMachineNodeDraftDTO();
        initNode.setStateMachineId(stateMachine.getId());
        //获取第一个状态
        List<StatusVO> statusVOS = statusService.queryAllStatus(organizationId);
        initNode.setStatusId(statusVOS.isEmpty() ? 0L : statusVOS.get(0).getId());
        initNode.setPositionX(InitNode.INIT.getPositionX());
        initNode.setPositionY(InitNode.INIT.getPositionY());
        initNode.setWidth(InitNode.INIT.getWidth());
        initNode.setHeight(InitNode.INIT.getHeight());
        initNode.setType(NodeType.INIT);
        initNode.setOrganizationId(organizationId);
        int isNodeInsert = nodeDraftMapper.insert(initNode);
        if (isNodeInsert != 1) {
            throw new CommonException(ERROR_STATEMACHINENODE_CREATE);
        }

        //创建默认的转换
        StateMachineTransformDraftDTO transform = new StateMachineTransformDraftDTO();
        transform.setName("初始化");
        transform.setStateMachineId(stateMachine.getId());
        transform.setStartNodeId(startNode.getId());
        transform.setEndNodeId(initNode.getId());
        transform.setType(TransformType.INIT);
        transform.setConditionStrategy(TransformConditionStrategy.ALL);
        transform.setOrganizationId(organizationId);
        int isTransformInsert = transformDraftMapper.insert(transform);
        if (isTransformInsert != 1) {
            throw new CommonException("error.stateMachineTransform.create");
        }
        return queryStateMachineWithConfigById(organizationId, stateMachine.getId(), true);
    }

    @Override
    public StateMachineVO update(Long organizationId, Long stateMachineId, StateMachineVO stateMachineVO) {
        if (stateMachineVO.getName() != null && checkNameUpdate(organizationId, stateMachineId, stateMachineVO.getName())) {
            throw new CommonException("error.stateMachineName.exist");
        }
        StateMachineDTO stateMachine = modelMapper.map(stateMachineVO, StateMachineDTO.class);
        stateMachine.setId(stateMachineId);
        stateMachine.setOrganizationId(organizationId);
        int isUpdate = stateMachineMapper.updateByPrimaryKeySelective(stateMachine);
        if (isUpdate != 1) {
            throw new CommonException("error.stateMachine.update");
        }
        stateMachine = stateMachineMapper.queryById(organizationId, stateMachine.getId());
        return modelMapper.map(stateMachine, StateMachineVO.class);
    }

    @Override
    public Boolean deploy(Long organizationId, Long stateMachineId, Boolean isChangeStatus) {
        if (stateMachineId == null) {
            throw new CommonException("error.stateMachineId.null");
        }
        StateMachineDTO stateMachine = stateMachineMapper.queryById(organizationId, stateMachineId);
        if (stateMachine == null) {
            throw new CommonException("error.stateMachine.null");
        }
        if (StateMachineStatus.ACTIVE.equals(stateMachine.getStatus())) {
            throw new CommonException("error.stateMachine.status.deployed");
        }
        String oldStatus = stateMachine.getStatus();
        //是否有状态的改动
        Map<String, List<StatusDTO>> changeMap = null;
        if (isChangeStatus && !oldStatus.equals(StateMachineStatus.CREATE)) {
            changeMap = new HashMap<>(3);
            deployHandleChange(changeMap, stateMachineId);
            //校验删除的节点状态是否有使用的issue
            Boolean result = deployCheckDelete(organizationId, changeMap, stateMachineId);
            if (!result) {
                return false;
            }
        }
        stateMachine.setStatus(StateMachineStatus.ACTIVE);
        Criteria criteria = new Criteria();
        criteria.update(STATUS);
        int stateMachineDeploy = stateMachineMapper.updateByPrimaryKeyOptions(stateMachine, criteria);
        if (stateMachineDeploy != 1) {
            throw new CommonException("error.stateMachine.deploy");
        }
        handleDeployData(organizationId, stateMachineId);

        //清理内存中的旧状态机构建器与实例
        instanceCache.cleanStateMachine(stateMachineId);

        //是否有状态的改动
        if (isChangeStatus && !oldStatus.equals(StateMachineStatus.CREATE)) {
            this.deployStateMachine(organizationId, stateMachineId, changeMap);
        }
        return true;
    }

    private void deployStateMachine(Long organizationId, Long stateMachineId, Map<String, List<StatusDTO>> changeMap) {
        //新增的状态
        List<StatusDTO> addList = changeMap.get("addList");
        List<StatusVO> addListVO = modelMapper.map(addList, new TypeToken<List<StatusVO>>() {
        }.getType());
        Map<Long, StatusVO> statusMap = addListVO.stream().collect(Collectors.toMap(StatusVO::getId, x -> x));
        //移除的状态
        List<StatusDTO> deleteList = changeMap.get("deleteList");
        List<Long> addStatusIds = addListVO.stream().map(StatusVO::getId).collect(Collectors.toList());
        List<Long> deleteStatusIds = deleteList.stream().map(StatusDTO::getId).collect(Collectors.toList());
        ChangeStatus changeStatus = new ChangeStatus(addStatusIds, deleteStatusIds);
        DeployStateMachinePayload deployStateMachinePayload = stateMachineService.handleStateMachineChangeStatusByStateMachineId(organizationId, stateMachineId, changeStatus);
        deployStateMachinePayload.setUserId(DetailsHelper.getUserDetails().getUserId());
        //新增的状态赋予实体
        deployStateMachinePayload.getAddStatusWithProjects().forEach(addStatusWithProject -> {
            List<StatusVO> statuses = new ArrayList<>(addStatusWithProject.getAddStatusIds().size());
            addStatusWithProject.getAddStatusIds().forEach(addStatusId -> {
                StatusVO status = statusMap.get(addStatusId);
                if (status != null) {
                    statuses.add(status);
                }
            });
            addStatusWithProject.setAddStatuses(statuses);
        });
        List<RemoveStatusWithProject> removeStatusWithProjects = deployStateMachinePayload.getRemoveStatusWithProjects();
        List<AddStatusWithProject> addStatusWithProjects = deployStateMachinePayload.getAddStatusWithProjects();
        //删除项目下的状态及与列的关联
        if (removeStatusWithProjects != null && !removeStatusWithProjects.isEmpty()) {
            boardColumnService.batchDeleteColumnAndStatusRel(removeStatusWithProjects);
        }
        //增加项目下的状态
        if (addStatusWithProjects != null && !addStatusWithProjects.isEmpty()) {
            issueStatusService.batchCreateStatusByProjectIds(addStatusWithProjects, deployStateMachinePayload.getUserId());
        }
        logger.info("deploy-state-machine addStatusIds: {}, deleteStatusIds: {}", changeStatus.getAddStatusIds(), changeStatus.getDeleteStatusIds());
    }

    @Override
    public void delete(Long organizationId, Long stateMachineId) {
        //有关联则无法删除，判断已发布的
        if (!stateMachineSchemeConfigService.querySchemeIdsByStateMachineId(false, organizationId, stateMachineId).isEmpty()) {
            throw new CommonException("error.stateMachine.delete");
        }
        //删除草稿的已关联当前状态机【todo】
        StateMachineDTO stateMachine = stateMachineMapper.queryById(organizationId, stateMachineId);
        if (stateMachine == null) {
            throw new CommonException("error.stateMachine.delete.noFound");
        }
        if (stateMachine.getDefault()) {
            throw new CommonException("error.stateMachine.defaultForbiddenDelete");
        }
        int isDelete = stateMachineMapper.deleteByPrimaryKey(stateMachineId);
        if (isDelete != 1) {
            throw new CommonException("error.stateMachine.delete");
        }
        //删除节点
        StateMachineNodeDraftDTO node = new StateMachineNodeDraftDTO();
        node.setStateMachineId(stateMachineId);
        node.setOrganizationId(organizationId);
        nodeDraftMapper.delete(node);
        //删除转换
        StateMachineTransformDraftDTO transform = new StateMachineTransformDraftDTO();
        transform.setStateMachineId(stateMachineId);
        transform.setOrganizationId(organizationId);
        transformDraftMapper.delete(transform);
        //删除配置
        StateMachineConfigDraftDTO config = new StateMachineConfigDraftDTO();
        config.setStateMachineId(stateMachineId);
        config.setOrganizationId(organizationId);
        configDraftMapper.delete(config);
        //删除发布节点
        StateMachineNodeDTO nodeDeploy = new StateMachineNodeDTO();
        nodeDeploy.setStateMachineId(stateMachineId);
        nodeDeploy.setOrganizationId(organizationId);
        nodeDeployMapper.delete(nodeDeploy);
        //删除发布转换
        StateMachineTransformDTO transformDeploy = new StateMachineTransformDTO();
        transformDeploy.setStateMachineId(stateMachineId);
        transformDeploy.setOrganizationId(organizationId);
        transformDeployMapper.delete(transformDeploy);
        //删除发布配置
        StateMachineConfigDTO configDeploy = new StateMachineConfigDTO();
        configDeploy.setStateMachineId(stateMachineId);
        configDeploy.setOrganizationId(organizationId);
        configDeployMapper.delete(configDeploy);
    }

    @Override
    public StateMachineVO queryStateMachineById(Long organizationId, Long stateMachineId) {
        StateMachineDTO stateMachine = stateMachineMapper.queryById(organizationId, stateMachineId);
        return stateMachine != null ? modelMapper.map(stateMachine, StateMachineVO.class) : null;
    }

    @Override
    public Map<String, Object> checkDelete(Long organizationId, Long stateMachineId) {
        Map<String, Object> map = new HashMap<>();
        StateMachineVO stateMachineVO = queryStateMachineById(organizationId, stateMachineId);
        if (stateMachineVO == null) {
            map.put(CloopmCommonString.CAN_DELETE, false);
            map.put("reason", "noFound");
            return map;
        }
        List<Long> schemeIds = stateMachineSchemeConfigService.querySchemeIdsByStateMachineId(false, organizationId, stateMachineId);
        if (schemeIds.isEmpty()) {
            map.put(CloopmCommonString.CAN_DELETE, true);
        } else {
            map.put(CloopmCommonString.CAN_DELETE, false);
            map.put("schemeUsed", schemeIds.size());
        }
        return map;
    }

    @Override
    public Map<String, Object> checkDeleteNode(Long organizationId, Long stateMachineId, Long statusId) {
        //找到与状态机关联的状态机方案
        List<Long> schemeIds = stateMachineSchemeConfigService.querySchemeIdsByStateMachineId(false, organizationId, stateMachineId);
        List<ProjectConfigDTO> projectConfigs = new ArrayList<>();
        schemeIds.forEach(schemeId ->
                //获取当前方案配置的项目列表
                projectConfigs.addAll(projectConfigMapper.queryConfigsBySchemeId(SchemeType.STATE_MACHINE, schemeId))
        );
        Map<String, Object> result = new HashMap<>(2);
        Long count = 0L;
        for (ProjectConfigDTO projectConfig : projectConfigs) {
            Long projectId = projectConfig.getProjectId();
            String applyType = projectConfig.getApplyType();
            count = count + issueMapper.querySizeByApplyTypeAndStatusId(projectId, applyType, statusId);
        }
        if (count.equals(0L)) {
            result.put("canDelete", true);
        } else {
            result.put("canDelete", false);
            result.put("count", count);
        }
        return result;
    }

    @Override
    public void notActiveStateMachine(Long organizationId, List<Long> stateMachineIds) {
        List<Long> notActiveStateMachineIds = new ArrayList<>();
        if (!stateMachineIds.isEmpty()) {
            //校验去掉仍然有关联方案的状态机
            List<StateMachineSchemeConfigDTO> configs = configMapper.queryByStateMachineIds(organizationId, stateMachineIds);
            Map<Long, List<StateMachineSchemeConfigDTO>> configMap = configs.stream().collect(Collectors.groupingBy(StateMachineSchemeConfigDTO::getStateMachineId));
            stateMachineIds.forEach(stateMachineId -> {
                List<StateMachineSchemeConfigDTO> configList = configMap.get(stateMachineId);
                if (configList == null || configList.isEmpty()) {
                    notActiveStateMachineIds.add(stateMachineId);
                }
            });
            //使活跃的状态机变更为未活跃
            logger.info("notActiveStateMachine: {}", stateMachineIds);
            if (!stateMachineIds.isEmpty()) {
                notActiveStateMachines(organizationId, notActiveStateMachineIds);
            }
        }
    }

    /**
     * 【内部调用】发布状态机时对增加与减少的状态进行处理，影响到的项目是否需要增加与减少相应的状态
     *
     * @param organizationId
     * @param stateMachineId
     * @param changeStatus
     * @return
     */
    @Override
    public DeployStateMachinePayload handleStateMachineChangeStatusByStateMachineId(Long organizationId, Long stateMachineId, ChangeStatus changeStatus) {
        //找到与状态机关联的状态机方案
        List<Long> schemeIds = stateMachineSchemeConfigService.querySchemeIdsByStateMachineId(false, organizationId, stateMachineId);
        return handleStateMachineChangeStatusBySchemeIds(organizationId, stateMachineId, null, schemeIds, changeStatus);
    }

    @Override
    public DeployStateMachinePayload handleStateMachineChangeStatusBySchemeIds(Long organizationId, Long ignoreStateMachineId, Long ignoreSchemeId, List<Long> schemeIds, ChangeStatus changeStatus) {
        if (schemeIds == null || schemeIds.isEmpty()) {
            return null;
        }
        DeployStateMachinePayload deployStateMachinePayload = new DeployStateMachinePayload();
        List<RemoveStatusWithProject> removeStatusWithProjects = new ArrayList<>();
        List<AddStatusWithProject> addStatusWithProjects = new ArrayList<>();
        List<Long> deleteStatusIds = changeStatus.getDeleteStatusIds();
        List<Long> addStatusIds = changeStatus.getAddStatusIds();

        //获取所有状态机及状态机的状态列表
        List<StateMachineWithStatusVO> stateMachineWithStatusVOS = queryAllWithStatus(organizationId);
        Map<Long, List<StatusVO>> stateMachineWithStatusVOSMap = stateMachineWithStatusVOS.stream().collect(Collectors.toMap(StateMachineWithStatusVO::getId, StateMachineWithStatusVO::getStatusVOS));
        //查出组织下所有状态机方案配置
        List<StateMachineSchemeConfigDTO> schemeConfigs = configMapper.queryByOrgId(organizationId);
        Map<Long, List<StateMachineSchemeConfigDTO>> schemeConfigsMap = schemeConfigs.stream().collect(Collectors.groupingBy(StateMachineSchemeConfigDTO::getSchemeId));

        //根据方案列表查出每个项目关联的状态机
        List<ProjectConfigDTO> projectConfigs = projectConfigMapper.handleRemoveStatus(schemeIds, SchemeType.STATE_MACHINE);
        Map<Long, List<ProjectConfigDTO>> projectMap = projectConfigs.stream().collect(Collectors.groupingBy(ProjectConfigDTO::getProjectId));
        projectMap.entrySet().forEach(entry -> {
            Long projectId = entry.getKey();
            List<ProjectConfigDTO> projectConfigsList = entry.getValue();
            List<StatusVO> statuses = new ArrayList<>();
            projectConfigsList.forEach(projectConfig -> {
                Long schemeId = projectConfig.getSchemeId();
                //忽略当前修改的状态机方案，要判断当前项目下其他方案下的状态机是否有当前要判断的状态
                if (!schemeId.equals(ignoreSchemeId)) {
                    schemeConfigsMap.get(schemeId).forEach(schemeConfig -> {
                        Long smId = schemeConfig.getStateMachineId();
                        //忽略当前修改的状态机，要判断当前项目下其他状态机是否有当前要判断的状态
                        if (!smId.equals(ignoreStateMachineId)) {
                            List<StatusVO> statusVOS = stateMachineWithStatusVOSMap.get(smId);
                            statuses.addAll(statusVOS);
                        }
                    });
                }
            });
            List<Long> statusIds = statuses.stream().map(StatusVO::getId).distinct().collect(Collectors.toList());
            //取当前项目真正减少的状态
            List<Long> confirmDeleteStatusIds = deleteStatusIds.stream().filter(x -> !statusIds.contains(x)).collect(toList());
            //取当前项目真正增加的状态
            List<Long> confirmAddStatusIds = addStatusIds.stream().filter(x -> !statusIds.contains(x)).collect(toList());

            if (!confirmDeleteStatusIds.isEmpty()) {
                RemoveStatusWithProject removeStatusWithProject = new RemoveStatusWithProject();
                removeStatusWithProject.setProjectId(projectId);
                removeStatusWithProject.setDeleteStatusIds(confirmDeleteStatusIds);
                removeStatusWithProjects.add(removeStatusWithProject);
            }
            if (!confirmAddStatusIds.isEmpty()) {
                AddStatusWithProject addStatusWithProject = new AddStatusWithProject();
                addStatusWithProject.setProjectId(projectId);
                addStatusWithProject.setAddStatusIds(confirmAddStatusIds);
                addStatusWithProjects.add(addStatusWithProject);
            }
        });
        deployStateMachinePayload.setRemoveStatusWithProjects(removeStatusWithProjects);
        deployStateMachinePayload.setAddStatusWithProjects(addStatusWithProjects);
        return deployStateMachinePayload;
    }


    private Boolean checkNameUpdate(Long organizationId, Long stateMachineId, String name) {
        StateMachineDTO stateMachine = new StateMachineDTO();
        stateMachine.setOrganizationId(organizationId);
        stateMachine.setName(name);
        StateMachineDTO res = stateMachineMapper.selectOne(stateMachine);
        return res != null && !stateMachineId.equals(res.getId());
    }


    private void handleDeployData(Long organizationId, Long stateMachineId) {
        //删除上一版本的节点
        StateMachineNodeDTO nodeDeploy = new StateMachineNodeDTO();
        nodeDeploy.setStateMachineId(stateMachineId);
        nodeDeploy.setOrganizationId(organizationId);
        nodeDeployMapper.delete(nodeDeploy);
        //删除上一版本的转换
        StateMachineTransformDTO transformDeploy = new StateMachineTransformDTO();
        transformDeploy.setStateMachineId(stateMachineId);
        transformDeploy.setOrganizationId(organizationId);
        transformDeployMapper.delete(transformDeploy);
        //删除上一版本的配置
        StateMachineConfigDTO configDeploy = new StateMachineConfigDTO();
        configDeploy.setStateMachineId(stateMachineId);
        configDeploy.setOrganizationId(organizationId);
        configDeployMapper.delete(configDeploy);
        //写入发布的节点
        StateMachineNodeDraftDTO node = new StateMachineNodeDraftDTO();
        node.setStateMachineId(stateMachineId);
        node.setOrganizationId(organizationId);
        List<StateMachineNodeDraftDTO> nodes = nodeDraftMapper.select(node);
        if (nodes != null && !nodes.isEmpty()) {
            List<StateMachineNodeDTO> nodeDeploys = modelMapper.map(nodes, new TypeToken<List<StateMachineNodeDTO>>() {
            }.getType());
            nodeDeploys.forEach(n -> nodeDeployMapper.insert(n));
        }
        //写入发布的转换
        StateMachineTransformDraftDTO transform = new StateMachineTransformDraftDTO();
        transform.setStateMachineId(stateMachineId);
        transform.setOrganizationId(organizationId);
        List<StateMachineTransformDraftDTO> transforms = transformDraftMapper.select(transform);
        if (transforms != null && !transforms.isEmpty()) {
            List<StateMachineTransformDTO> transformDeploys = modelMapper.map(transforms, new TypeToken<List<StateMachineTransformDTO>>() {
            }.getType());
            transformDeploys.forEach(t -> transformDeployMapper.insert(t));
        }
        //写入发布的配置
        StateMachineConfigDraftDTO config = new StateMachineConfigDraftDTO();
        config.setStateMachineId(stateMachineId);
        config.setOrganizationId(organizationId);
        List<StateMachineConfigDraftDTO> configs = configDraftMapper.select(config);
        if (configs != null && !configs.isEmpty()) {
            List<StateMachineConfigDTO> configDeploys = modelMapper.map(configs, new TypeToken<List<StateMachineConfigDTO>>() {
            }.getType());
            configDeploys.forEach(c -> configDeployMapper.insert(c));
        }
    }

    /**
     * 处理发布状态机时，节点状态的变化
     *
     * @param stateMachineId
     */
    private void deployHandleChange(Map<String, List<StatusDTO>> changeMap, Long stateMachineId) {
        //获取旧节点
        List<StateMachineNodeDTO> nodeDeploys = nodeDeployMapper.selectByStateMachineId(stateMachineId);
        Map<Long, StatusDTO> deployMap = nodeDeploys.stream().filter(x -> x.getStatus() != null).collect(Collectors.toMap(StateMachineNodeDTO::getId, StateMachineNodeDTO::getStatus));
        List<Long> oldIds = nodeDeploys.stream().map(StateMachineNodeDTO::getId).collect(Collectors.toList());
        //获取新节点
        List<StateMachineNodeDraftDTO> nodeDrafts = nodeDraftMapper.selectByStateMachineId(stateMachineId);
        Map<Long, StatusDTO> draftMap = nodeDrafts.stream().filter(x -> x.getStatus() != null).collect(Collectors.toMap(StateMachineNodeDraftDTO::getId, StateMachineNodeDraftDTO::getStatus));
        List<Long> newIds = nodeDrafts.stream().map(StateMachineNodeDraftDTO::getId).collect(Collectors.toList());
        //新增的节点
        List<Long> addIds = new ArrayList<>(newIds);
        addIds.removeAll(oldIds);
        List<StatusDTO> addStatuses = new ArrayList<>(addIds.size());
        addIds.forEach(addId -> addStatuses.add(draftMap.get(addId)));
        //删除的节点
        List<Long> deleteIds = new ArrayList<>(oldIds);
        deleteIds.removeAll(newIds);
        List<StatusDTO> deleteStatuses = new ArrayList<>(deleteIds.size());
        deleteIds.forEach(deleteId -> deleteStatuses.add(deployMap.get(deleteId)));

        changeMap.put("addList", addStatuses);
        changeMap.put("deleteList", deleteStatuses);
    }

    /**
     * 处理发布状态机时，
     *
     * @param stateMachineId
     */
    private Boolean deployCheckDelete(Long organizationId, Map<String, List<StatusDTO>> changeMap, Long stateMachineId) {
        List<StatusDTO> deleteStatuses = changeMap.get("deleteList");
        for (StatusDTO status : deleteStatuses) {
            Map<String, Object> result = nodeService.checkDelete(organizationId, stateMachineId, status.getId());
            Boolean canDelete = (Boolean) result.get("canDelete");
            if (!canDelete) {
                return false;
            }
        }
        return true;
    }

    @Override
    public StateMachineVO queryStateMachineWithConfigById(Long organizationId, Long stateMachineId, Boolean isDraft) {
        StateMachineDTO stateMachine = stateMachineMapper.queryById(organizationId, stateMachineId);
        if (stateMachine == null) {
            throw new CommonException("error.stateMachine.notFound");
        }

        //查询草稿时，若为活跃状态，则更新为草稿
        if (isDraft) {
            updateStateMachineStatus(organizationId, stateMachineId);
        }

        List<StateMachineNodeVO> nodeVOS = nodeService.queryByStateMachineId(organizationId, stateMachineId, isDraft);
        List<StateMachineTransformVO> transformVOS;
        if (isDraft) {
            //获取转换
            StateMachineTransformDraftDTO select = new StateMachineTransformDraftDTO();
            select.setStateMachineId(stateMachineId);
            select.setOrganizationId(organizationId);
            List<StateMachineTransformDraftDTO> transforms = transformDraftMapper.select(select);
            transformVOS = modelMapper.map(transforms, new TypeToken<List<StateMachineTransformVO>>() {
            }.getType());
        } else {
            StateMachineTransformDTO select = new StateMachineTransformDTO();
            select.setStateMachineId(stateMachineId);
            select.setOrganizationId(organizationId);
            List<StateMachineTransformDTO> transforms = transformDeployMapper.select(select);
            transformVOS = modelMapper.map(transforms, new TypeToken<List<StateMachineTransformVO>>() {
            }.getType());
        }

        StateMachineVO stateMachineVO = modelMapper.map(stateMachine, StateMachineVO.class);
        stateMachineVO.setNodeVOS(nodeVOS);
        stateMachineVO.setTransformVOS(transformVOS);

        //获取转换中的配置
        for (StateMachineTransformVO transformVO : transformVOS) {
            List<StateMachineConfigVO> configVOS;
            if (isDraft) {
                StateMachineConfigDraftDTO config = new StateMachineConfigDraftDTO();
                config.setTransformId(transformVO.getId());
                config.setOrganizationId(organizationId);
                List<StateMachineConfigDraftDTO> configs = configDraftMapper.select(config);
                configVOS = modelMapper.map(configs, new TypeToken<List<StateMachineConfigVO>>() {
                }.getType());
            } else {
                StateMachineConfigDTO config = new StateMachineConfigDTO();
                config.setTransformId(transformVO.getId());
                config.setOrganizationId(organizationId);
                List<StateMachineConfigDTO> configs = configDeployMapper.select(config);
                configVOS = modelMapper.map(configs, new TypeToken<List<StateMachineConfigVO>>() {
                }.getType());
            }
            if (configVOS != null && !configVOS.isEmpty()) {
                Map<String, List<StateMachineConfigVO>> map = configVOS.stream().collect(Collectors.groupingBy(StateMachineConfigVO::getType));
                transformVO.setConditions(Optional.ofNullable(map.get(ConfigType.CONDITION)).orElse(Collections.emptyList()));
                transformVO.setValidators(Optional.ofNullable(map.get(ConfigType.VALIDATOR)).orElse(Collections.emptyList()));
                transformVO.setTriggers(Optional.ofNullable(map.get(ConfigType.TRIGGER)).orElse(Collections.emptyList()));
                transformVO.setPostpositions(Optional.ofNullable(map.get(ConfigType.ACTION)).orElse(Collections.emptyList()));
            }
        }
        return stateMachineVO;
    }

    @Override
    public StateMachineDTO queryDeployForInstance(Long organizationId, Long stateMachineId) {
        StateMachineDTO stateMachine = stateMachineMapper.queryById(organizationId, stateMachineId);
        if (stateMachine == null) {
            throw new CommonException("error.stateMachine.notExist");
        }
        if (stateMachine.getStatus().equals(StateMachineStatus.CREATE)) {
            throw new CommonException("error.buildInstance.stateMachine.inActive");
        }
        //获取原件节点
        List<StateMachineNodeDTO> nodeDeploys = nodeDeployMapper.selectByStateMachineId(stateMachineId);
        if (nodeDeploys != null && !nodeDeploys.isEmpty()) {
            List<StateMachineNodeDTO> nodes = modelMapper.map(nodeDeploys, new TypeToken<List<StateMachineNodeDTO>>() {
            }.getType());
            stateMachine.setNodes(nodes);
        }
        //获取原件转换
        StateMachineTransformDTO transformDeploy = new StateMachineTransformDTO();
        transformDeploy.setStateMachineId(stateMachineId);
        List<StateMachineTransformDTO> transformDeploys = transformDeployMapper.select(transformDeploy);
        if (transformDeploys != null && !transformDeploys.isEmpty()) {
            List<StateMachineTransformDTO> transforms = modelMapper.map(transformDeploys, new TypeToken<List<StateMachineTransformDTO>>() {
            }.getType());
            stateMachine.setTransforms(transforms);
        }
        return stateMachine;
    }

    @Override
    public StateMachineVO deleteDraft(Long organizationId, Long stateMachineId) {
        StateMachineDTO stateMachine = stateMachineMapper.queryById(organizationId, stateMachineId);
        if (stateMachine == null) {
            throw new CommonException("error.stateMachine.deleteDraft.noFound");
        }
        stateMachine.setStatus(StateMachineStatus.ACTIVE);
        Criteria criteria = new Criteria();
        criteria.update(STATUS);
        int stateMachineDeploy = stateMachineMapper.updateByPrimaryKeyOptions(stateMachine, criteria);
        if (stateMachineDeploy != 1) {
            throw new CommonException("error.stateMachine.deleteDraft");
        }
        //删除草稿节点
        StateMachineNodeDraftDTO node = new StateMachineNodeDraftDTO();
        node.setStateMachineId(stateMachineId);
        node.setOrganizationId(organizationId);
        nodeDraftMapper.delete(node);
        //删除草稿转换
        StateMachineTransformDraftDTO transform = new StateMachineTransformDraftDTO();
        transform.setStateMachineId(stateMachineId);
        transform.setOrganizationId(organizationId);
        transformDraftMapper.delete(transform);
        //删除草稿配置
        StateMachineConfigDraftDTO config = new StateMachineConfigDraftDTO();
        config.setStateMachineId(stateMachineId);
        config.setOrganizationId(organizationId);
        configDraftMapper.delete(config);
        //写入活跃的节点到草稿中，id一致
        deleteDraftHandleNode(organizationId, stateMachineId);

        //写入活跃的转换到草稿中，id一致
        deleteDraftHandleTransform(organizationId, stateMachineId);

        //写入活跃的配置到草稿中，id一致
        deleteDraftHandleConfig(stateMachineId);

        return queryStateMachineWithConfigById(organizationId, stateMachine.getId(), false);
    }

    private void deleteDraftHandleNode(Long organizationId, Long stateMachineId) {
        StateMachineNodeDTO nodeDeploy = new StateMachineNodeDTO();
        nodeDeploy.setStateMachineId(stateMachineId);
        nodeDeploy.setOrganizationId(organizationId);
        List<StateMachineNodeDTO> nodeDeploys = nodeDeployMapper.select(nodeDeploy);
        if (nodeDeploys != null && !nodeDeploys.isEmpty()) {
            List<StateMachineNodeDraftDTO> nodes = modelMapper.map(nodeDeploys, new TypeToken<List<StateMachineNodeDraftDTO>>() {
            }.getType());
            for (StateMachineNodeDraftDTO insertNode : nodes) {
                int nodeInsert = nodeDraftMapper.insert(insertNode);
                if (nodeInsert != 1) {
                    throw new CommonException(ERROR_STATEMACHINENODE_CREATE);
                }
            }
        }
    }

    private void deleteDraftHandleTransform(Long organizationId, Long stateMachineId) {
        StateMachineTransformDTO transformDeploy = new StateMachineTransformDTO();
        transformDeploy.setStateMachineId(stateMachineId);
        transformDeploy.setOrganizationId(organizationId);
        List<StateMachineTransformDTO> transformDeploys = transformDeployMapper.select(transformDeploy);
        if (transformDeploys != null && !transformDeploys.isEmpty()) {
            List<StateMachineTransformDraftDTO> transformInserts = modelMapper.map(transformDeploys, new TypeToken<List<StateMachineTransformDraftDTO>>() {
            }.getType());
            for (StateMachineTransformDraftDTO insertTransform : transformInserts) {
                int transformInsert = transformDraftMapper.insert(insertTransform);
                if (transformInsert != 1) {
                    throw new CommonException("error.stateMachineTransform.create");
                }
            }
        }
    }

    private void deleteDraftHandleConfig(Long stateMachineId) {
        StateMachineConfigDTO configDeploy = new StateMachineConfigDTO();
        configDeploy.setStateMachineId(stateMachineId);
        List<StateMachineConfigDTO> configDeploys = configDeployMapper.select(configDeploy);
        if (configDeploys != null && !configDeploys.isEmpty()) {
            List<StateMachineConfigDraftDTO> configs = modelMapper.map(configDeploys, new TypeToken<List<StateMachineConfigDraftDTO>>() {
            }.getType());
            for (StateMachineConfigDraftDTO insertConfig : configs) {
                int configInsert = configDraftMapper.insert(insertConfig);
                if (configInsert != 1) {
                    throw new CommonException("error.stateMachineCreate.create");
                }
            }
        }
    }

    @Override
    public StateMachineVO queryDefaultStateMachine(Long organizationId) {
        StateMachineDTO defaultStateMachine = new StateMachineDTO();
        defaultStateMachine.setOrganizationId(organizationId);
        defaultStateMachine.setDefault(true);
        StateMachineDTO stateMachine = stateMachineMapper.selectOne(defaultStateMachine);
        return stateMachine != null ? modelMapper.map(stateMachine, StateMachineVO.class) : null;
    }

    @Override
    public List<StateMachineVO> queryAll(Long organizationId) {
        StateMachineDTO stateMachine = new StateMachineDTO();
        stateMachine.setOrganizationId(organizationId);
        List<StateMachineDTO> list = stateMachineMapper.select(stateMachine);
        return modelMapper.map(list, new TypeToken<List<StateMachineVO>>() {
        }.getType());
    }

    @Override
    public void updateStateMachineStatus(Long organizationId, Long stateMachineId) {
        if (stateMachineId == null) {
            throw new CommonException("error.updateStateMachineStatus.stateMachineId.notNull");
        }
        StateMachineDTO stateMachine = stateMachineMapper.queryById(organizationId, stateMachineId);
        if (stateMachine != null && stateMachine.getStatus().equals(StateMachineStatus.ACTIVE)) {
            stateMachine.setStatus(StateMachineStatus.DRAFT);
            Criteria criteria = new Criteria();
            criteria.update(STATUS);
            int stateMachineUpdate = stateMachineMapper.updateByPrimaryKeyOptions(stateMachine, criteria);
            if (stateMachineUpdate != 1) {
                throw new CommonException("error.stateMachine.update");
            }
        }
    }

    @Override
    public Boolean checkName(Long organizationId, String name) {
        StateMachineDTO stateMachine = new StateMachineDTO();
        stateMachine.setOrganizationId(organizationId);
        stateMachine.setName(name);
        StateMachineDTO res = stateMachineMapper.selectOne(stateMachine);
        return res != null;
    }

    @Override
    public Boolean activeStateMachines(Long organizationId, List<Long> stateMachineIds) {
        if (!stateMachineIds.isEmpty()) {
            List<StateMachineDTO> stateMachines = stateMachineMapper.queryByIds(organizationId, stateMachineIds);
            for (StateMachineDTO stateMachine : stateMachines) {
                //若是新建状态机，则发布变成活跃
                if (stateMachine.getStatus().equals(StateMachineStatus.CREATE)) {
                    deploy(organizationId, stateMachine.getId(), false);
                }
            }
        }
        return true;
    }

    @Override
    public Boolean notActiveStateMachines(Long organizationId, List<Long> stateMachineIds) {
        if (!stateMachineIds.isEmpty()) {
            List<StateMachineDTO> stateMachines = stateMachineMapper.queryByIds(organizationId, stateMachineIds);
            for (StateMachineDTO stateMachine : stateMachines) {
                if (stateMachine.getId() == null) {
                    throw new CommonException("error.stateMachineId.null");
                }
                //更新状态机状态为create
                Long stateMachineId = stateMachine.getId();
                stateMachine.setStatus(StateMachineStatus.CREATE);
                Criteria criteria = new Criteria();
                criteria.update(STATUS);
                stateMachineMapper.updateByPrimaryKeyOptions(stateMachine, criteria);
                //删除发布节点
                StateMachineNodeDTO node = new StateMachineNodeDTO();
                node.setStateMachineId(stateMachineId);
                node.setOrganizationId(organizationId);
                nodeDeployMapper.delete(node);
                //删除发布转换
                StateMachineTransformDTO transform = new StateMachineTransformDTO();
                transform.setStateMachineId(stateMachineId);
                transform.setOrganizationId(organizationId);
                transformDeployMapper.delete(transform);
                //删除发布配置
                StateMachineConfigDTO config = new StateMachineConfigDTO();
                config.setStateMachineId(stateMachineId);
                config.setOrganizationId(organizationId);
                configDeployMapper.delete(config);
                //清理状态机实例
                instanceCache.cleanStateMachine(stateMachineId);
            }
        }
        return true;
    }

    @Override
    public List<StateMachineWithStatusVO> queryAllWithStatus(Long organizationId) {
        //查询出所有状态机，新建的查草稿，活跃的查发布
        StateMachineDTO select = new StateMachineDTO();
        select.setOrganizationId(organizationId);
        List<StateMachineDTO> stateMachines = stateMachineMapper.select(select);
        List<StateMachineWithStatusVO> stateMachineWithStatusVOS = modelMapper.map(stateMachines, new TypeToken<List<StateMachineWithStatusVO>>() {
        }.getType());
        //查询出所有状态
        List<StatusVO> statusVOS = statusService.queryAllStatus(organizationId);
        Map<Long, StatusVO> statusMap = statusVOS.stream().collect(Collectors.toMap(StatusVO::getId, x -> x));
        stateMachineWithStatusVOS.forEach(stateMachine -> {
            List<StatusVO> status = new ArrayList<>();
            if (stateMachine.getStatus().equals(StateMachineStatus.CREATE)) {
                List<StateMachineNodeDraftDTO> nodeDrafts = nodeDraftMapper.selectByStateMachineId(stateMachine.getId());
                nodeDrafts.stream().filter(x -> !x.getType().equals(NodeType.START)).forEach(nodeDraft -> {
                    StatusVO statusVO = statusMap.get(nodeDraft.getStatusId());
                    if (statusVO != null) {
                        status.add(statusVO);
                    } else {
                        logger.warn("warn nodeDraftId:{} notFound", nodeDraft.getId());
                    }
                });
            } else {
                List<StateMachineNodeDTO> nodeDeploys = nodeDeployMapper.selectByStateMachineId(stateMachine.getId());
                nodeDeploys.stream().filter(x -> !x.getType().equals(NodeType.START)).forEach(nodeDeploy -> {
                    StatusVO statusVO = statusMap.get(nodeDeploy.getStatusId());
                    if (statusVO != null) {
                        status.add(statusVO);
                    } else {
                        logger.warn("warn nodeDraftId:{} notFound", nodeDeploy.getId());
                    }
                });
            }
            stateMachine.setStatusVOS(status);
        });
        return stateMachineWithStatusVOS;
    }

    @Override
    public List<StateMachineVO> queryByOrgId(Long organizationId) {
        StateMachineDTO select = new StateMachineDTO();
        select.setOrganizationId(organizationId);
        List<StateMachineDTO> stateMachines = stateMachineMapper.select(select);
        return modelMapper.map(stateMachines, new TypeToken<List<StateMachineVO>>() {
        }.getType());
    }

    @Override
    public Map<Long, Long> checkStateMachineSchemeChange(Long organizationId, StateMachineSchemeDeployCheckIssue deployCheckIssue) {
        List<ProjectConfigDTO> projectConfigs = deployCheckIssue.getProjectConfigs();
        List<Long> issueTypeIds = deployCheckIssue.getIssueTypeIds();
        Map<Long, Long> result = new HashMap<>(issueTypeIds.size());
        if (!issueTypeIds.isEmpty()) {
            issueTypeIds.forEach(issueTypeId -> result.put(issueTypeId, 0L));
            //计算出所有有影响的issue数量，根据issueTypeId分类
            projectConfigs.forEach(projectConfig -> {
                List<IssueDTO> issueDTOS = issueMapper.queryByIssueTypeIdsAndApplyType(projectConfig.getProjectId(), projectConfig.getApplyType(), issueTypeIds);
                Map<Long, Long> issueCounts = issueDTOS.stream().collect(Collectors.groupingBy(IssueDTO::getIssueTypeId, Collectors.counting()));
                for (Map.Entry<Long, Long> entry : issueCounts.entrySet()) {
                    Long issueTypeId = entry.getKey();
                    Long count = entry.getValue();
                    result.put(issueTypeId, result.get(issueTypeId) + count);
                }
            });
        }

        return result;
    }
}

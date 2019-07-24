package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.event.ProjectEvent;
import io.choerodon.agile.api.vo.event.StatusPayload;
import io.choerodon.agile.app.service.BoardService;
import io.choerodon.agile.app.service.InitService;
import io.choerodon.agile.app.service.StateMachineService;
import io.choerodon.agile.infra.dataobject.StateMachineDTO;
import io.choerodon.agile.infra.dataobject.StateMachineNodeDraftDTO;
import io.choerodon.agile.infra.dataobject.StateMachineTransformDraftDTO;
import io.choerodon.agile.infra.dataobject.StatusDTO;
import io.choerodon.agile.infra.enums.*;
import io.choerodon.agile.infra.mapper.StateMachineMapper;
import io.choerodon.agile.infra.mapper.StateMachineNodeDraftMapper;
import io.choerodon.agile.infra.mapper.StateMachineTransformDraftMapper;
import io.choerodon.agile.infra.mapper.StatusMapper;
import io.choerodon.core.exception.CommonException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author shinan.chen
 * @date 2018/10/15
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class InitServiceImpl implements InitService {

    private static final String ERROR_STATEMACHINE_CREATE = "error.stateMachine.create";
    @Autowired
    private StatusMapper statusMapper;
    @Autowired
    private StateMachineNodeDraftMapper nodeDraftMapper;
    @Autowired
    private StateMachineService stateMachineService;
    @Autowired
    private StateMachineMapper stateMachineMapper;
    @Autowired
    private StateMachineTransformDraftMapper transformDraftMapper;
    @Autowired
    private BoardService boardService;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public synchronized List<StatusDTO> initStatus(Long organizationId) {
        List<StatusDTO> initStatuses = new ArrayList<>();
        for (InitStatus initStatus : InitStatus.values()) {
            StatusDTO status = new StatusDTO();
            status.setOrganizationId(organizationId);
            status.setCode(initStatus.getCode());
            List<StatusDTO> statuses = statusMapper.select(status);
            if (statuses.isEmpty()) {
                status.setName(initStatus.getName());
                status.setDescription(initStatus.getName());
                status.setType(initStatus.getType());
                if (statusMapper.insert(status) != 1) {
                    throw new CommonException("error.initStatus.create");
                }
                initStatuses.add(status);
            } else {
                initStatuses.add(statuses.get(0));
            }
        }
        return initStatuses;
    }

    @Override
    public Long createStateMachineWithCreateProject(Long organizationId, String applyType, ProjectEvent projectEvent) {
        Long stateMachineId = null;
        if (applyType.equals(SchemeApplyType.AGILE)) {
            stateMachineId = initAGStateMachine(organizationId, projectEvent);
        } else if (applyType.equals(SchemeApplyType.TEST)) {
            stateMachineId = initTEStateMachine(organizationId, projectEvent);
        } else if (applyType.equals(SchemeApplyType.PROGRAM)) {
            stateMachineId = initPRStateMachine(organizationId, projectEvent);
        }
        return stateMachineId;
    }

    @Override
    public Long initDefaultStateMachine(Long organizationId) {
        //初始化默认状态机
        StateMachineDTO stateMachine = new StateMachineDTO();
        stateMachine.setOrganizationId(organizationId);
        stateMachine.setName("默认状态机");
        stateMachine.setDescription("默认状态机");
        stateMachine.setStatus(StateMachineStatus.CREATE);
        stateMachine.setDefault(true);
        List<StateMachineDTO> selects = stateMachineMapper.select(stateMachine);
        Long stateMachineId;
        if (selects.isEmpty()) {
            if (stateMachineMapper.insert(stateMachine) != 1) {
                throw new CommonException(ERROR_STATEMACHINE_CREATE);
            }
            //创建状态机节点和转换
            createStateMachineDetail(organizationId, stateMachine.getId(), "default");
            stateMachineId = stateMachine.getId();
        } else {
            stateMachineId = selects.get(0).getId();
        }
        return stateMachineId;
    }

    @Override
    public Long initAGStateMachine(Long organizationId, ProjectEvent projectEvent) {
        String projectCode = projectEvent.getProjectCode();
        //初始化状态机
        StateMachineDTO stateMachine = new StateMachineDTO();
        stateMachine.setOrganizationId(organizationId);
        stateMachine.setName(projectCode + "默认状态机【敏捷】");
        stateMachine.setDescription(projectCode + "默认状态机【敏捷】");
        stateMachine.setStatus(StateMachineStatus.CREATE);
        stateMachine.setDefault(false);
        if (stateMachineMapper.insert(stateMachine) != 1) {
            throw new CommonException(ERROR_STATEMACHINE_CREATE);
        }
        //创建状态机节点和转换
        createStateMachineDetail(organizationId, stateMachine.getId(), SchemeApplyType.AGILE);
        //发布状态机
        Long stateMachineId = stateMachine.getId();
        stateMachineService.deploy(organizationId, stateMachineId, false);
        //敏捷创建完状态机后需要到敏捷创建列
        List<StatusPayload> statusPayloads = stateMachineMapper.getStatusBySmId(projectEvent.getProjectId(), stateMachineId);
        boardService.initBoard(projectEvent.getProjectId(), projectEvent.getProjectName() + "-board", statusPayloads);
        return stateMachineId;
    }

    @Override
    public Long initTEStateMachine(Long organizationId, ProjectEvent projectEvent) {
        String projectCode = projectEvent.getProjectCode();
        //初始化状态机
        StateMachineDTO stateMachine = new StateMachineDTO();
        stateMachine.setOrganizationId(organizationId);
        stateMachine.setName(projectCode + "默认状态机【测试】");
        stateMachine.setDescription(projectCode + "默认状态机【测试】");
        stateMachine.setStatus(StateMachineStatus.CREATE);
        stateMachine.setDefault(false);
        if (stateMachineMapper.insert(stateMachine) != 1) {
            throw new CommonException(ERROR_STATEMACHINE_CREATE);
        }
        //创建状态机节点和转换
        createStateMachineDetail(organizationId, stateMachine.getId(), SchemeApplyType.TEST);
        //发布状态机
        Long stateMachineId = stateMachine.getId();
        stateMachineService.deploy(organizationId, stateMachineId, false);
        return stateMachineId;
    }

    @Override
    public Long initPRStateMachine(Long organizationId, ProjectEvent projectEvent) {
        String projectCode = projectEvent.getProjectCode();
        //初始化状态机
        StateMachineDTO stateMachine = new StateMachineDTO();
        stateMachine.setOrganizationId(organizationId);
        stateMachine.setName(projectCode + "默认状态机【项目群】");
        stateMachine.setDescription(projectCode + "默认状态机【项目群】");
        stateMachine.setStatus(StateMachineStatus.CREATE);
        stateMachine.setDefault(false);
        if (stateMachineMapper.insert(stateMachine) != 1) {
            throw new CommonException(ERROR_STATEMACHINE_CREATE);
        }
        //创建状态机节点和转换
        createStateMachineDetail(organizationId, stateMachine.getId(), SchemeApplyType.PROGRAM);
        //发布状态机
        Long stateMachineId = stateMachine.getId();
        stateMachineService.deploy(organizationId, stateMachineId, false);
        //项目群创建完状态机后需要到敏捷创建列
        List<StatusPayload> statusPayloads = stateMachineMapper.getStatusBySmId(projectEvent.getProjectId(), stateMachineId);
        boardService.initBoardByProgram(projectEvent.getProjectId(), projectEvent.getProjectName() + "-board", statusPayloads);
        return stateMachineId;
    }

    /**
     * 创建状态机节点和转换
     *
     * @param organizationId
     * @param stateMachineId
     */
    @Override
    public void createStateMachineDetail(Long organizationId, Long stateMachineId, String applyType) {
        StatusDTO select = new StatusDTO();
        select.setOrganizationId(organizationId);
        List<StatusDTO> initStatuses = statusMapper.select(select);
        //老的组织没有相关数据要重新创建
        initStatuses = initOrganization(organizationId, initStatuses);
        //初始化节点
        Map<String, StateMachineNodeDraftDTO> nodeMap = new HashMap<>();
        Map<String, StatusDTO> statusMap = initStatuses.stream().filter(x -> x.getCode() != null).collect(Collectors.toMap(StatusDTO::getCode, x -> x, (code1, code2) -> code1));
        handleNode(organizationId, stateMachineId, applyType, nodeMap, statusMap);

        //初始化转换
        for (InitTransform initTransform : InitTransform.list(applyType)) {
            StateMachineTransformDraftDTO transform = new StateMachineTransformDraftDTO();
            transform.setStateMachineId(stateMachineId);
            transform.setName(initTransform.getName());
            if (initTransform.getType().equals(TransformType.ALL)) {
                transform.setStartNodeId(0L);
                transform.setDescription("【全部】转换");
            } else {
                transform.setStartNodeId(nodeMap.get(initTransform.getStartNodeCode()).getId());
                transform.setDescription("初始化");
            }
            transform.setEndNodeId(nodeMap.get(initTransform.getEndNodeCode()).getId());
            transform.setType(initTransform.getType());
            transform.setConditionStrategy(initTransform.getConditionStrategy());
            transform.setOrganizationId(organizationId);
            int isTransformInsert = transformDraftMapper.insert(transform);
            if (isTransformInsert != 1) {
                throw new CommonException("error.stateMachineTransform.create");
            }
            //如果是ALL类型的转换，要更新节点的allStatusTransformId
            if (initTransform.getType().equals(TransformType.ALL)) {
                StateMachineNodeDraftDTO nodeDraft = nodeMap.get(initTransform.getEndNodeCode());
                int update = nodeDraftMapper.updateAllStatusTransformId(organizationId, nodeDraft.getId(), transform.getId());
                if (update != 1) {
                    throw new CommonException("error.stateMachineNode.allStatusTransformId.update");
                }
            }
        }
    }

    private void handleNode(Long organizationId, Long stateMachineId, String applyType, Map<String, StateMachineNodeDraftDTO> nodeMap, Map<String, StatusDTO> statusMap) {
        for (InitNode initNode : InitNode.list(applyType)) {
            StateMachineNodeDraftDTO node = new StateMachineNodeDraftDTO();
            node.setStateMachineId(stateMachineId);
            if (initNode.getType().equals(NodeType.START)) {
                node.setStatusId(0L);
            } else {
                node.setStatusId(statusMap.get(initNode.getCode()).getId());
            }
            node.setPositionX(initNode.getPositionX());
            node.setPositionY(initNode.getPositionY());
            node.setWidth(initNode.getWidth());
            node.setHeight(initNode.getHeight());
            node.setType(initNode.getType());
            node.setOrganizationId(organizationId);
            int isNodeInsert = nodeDraftMapper.insert(node);
            if (isNodeInsert != 1) {
                throw new CommonException("error.stateMachineNode.create");
            }
            nodeMap.put(initNode.getCode(), node);
        }
    }

    private List<StatusDTO> initOrganization(Long organizationId, List<StatusDTO> initStatuses) {
        if (initStatuses == null || initStatuses.isEmpty()) {
            //初始化状态
            initStatus(organizationId);
            //初始化默认状态机
            initDefaultStateMachine(organizationId);
            StatusDTO select = new StatusDTO();
            select.setOrganizationId(organizationId);
            return statusMapper.select(select);
        } else {
            return initStatuses;
        }
    }
}

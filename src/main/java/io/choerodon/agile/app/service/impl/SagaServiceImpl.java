package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSON;
import io.choerodon.agile.app.service.StateMachineService;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import io.choerodon.asgard.saga.feign.SagaClient;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.agile.api.vo.StatusVO;
import io.choerodon.agile.api.vo.event.*;
import io.choerodon.agile.app.service.StatusService;
import io.choerodon.agile.infra.dataobject.ProjectConfigDTO;
import io.choerodon.agile.infra.dataobject.StatusDTO;
import io.choerodon.agile.infra.enums.SchemeType;
import io.choerodon.agile.infra.mapper.ProjectConfigMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author shinan.chen
 * @date 2018/11/29
 */
@Service
public class SagaServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(SagaServiceImpl.class);
    private static final String DEPLOY_STATE_MACHINE_SCHEME = "deploy-state-machine-scheme";
    private static final String DEPLOY_STATE_MACHINE = "deploy-state-machine";
    @Autowired
    private SagaClient sagaClient;
    @Autowired
    private StateMachineService stateMachineService;
    @Autowired
    private ProjectConfigMapper projectConfigMapper;
    @Autowired
    private StatusService statusService;
    @Autowired
    private ModelMapper modelMapper;

    public void setSagaClient(SagaClient sagaClient) {
        this.sagaClient = sagaClient;
    }

    @Saga(code = DEPLOY_STATE_MACHINE_SCHEME, description = "issue服务发布状态机方案", inputSchemaClass = StateMachineSchemeDeployUpdateIssue.class)
    public void deployStateMachineScheme(Long organizationId, Long schemeId, List<StateMachineSchemeChangeItem> changeItems, ChangeStatus changeStatus) {
        //获取当前方案配置的项目列表
        List<ProjectConfigDTO> projectConfigs = projectConfigMapper.queryConfigsBySchemeId(SchemeType.STATE_MACHINE, schemeId);
        //获取所有状态
        List<StatusVO> statusVOS = statusService.queryAllStatus(organizationId);
        Map<Long, StatusVO> statusVOMap = statusVOS.stream().collect(Collectors.toMap(StatusVO::getId, x -> x));
        //将要增加和减少的状态进行判断，确定哪些项目要增加哪些状态与减少哪些状态
        DeployStateMachinePayload deployStateMachinePayload = stateMachineService.handleStateMachineChangeStatusBySchemeIds(organizationId, null, schemeId, Arrays.asList(schemeId), changeStatus);
        List<RemoveStatusWithProject> removeStatusWithProjects = deployStateMachinePayload.getRemoveStatusWithProjects();
        List<AddStatusWithProject> addStatusWithProjects = deployStateMachinePayload.getAddStatusWithProjects();
        //新增的状态赋予实体
        deployStateMachinePayload.getAddStatusWithProjects().forEach(addStatusWithProject -> {
            List<StatusVO> statuses = new ArrayList<>(addStatusWithProject.getAddStatusIds().size());
            addStatusWithProject.getAddStatusIds().forEach(addStatusId -> {
                StatusVO status = statusVOMap.get(addStatusId);
                if (status != null) {
                    statuses.add(status);
                }
            });
            addStatusWithProject.setAddStatuses(statuses);
        });
        //发送saga，批量更新issue的状态，并对相应的项目进行状态的增加与减少
        StateMachineSchemeDeployUpdateIssue deployUpdateIssue = new StateMachineSchemeDeployUpdateIssue();
        deployUpdateIssue.setChangeItems(changeItems);
        deployUpdateIssue.setProjectConfigs(projectConfigs);
        deployUpdateIssue.setAddStatusWithProjects(addStatusWithProjects);
        deployUpdateIssue.setRemoveStatusWithProjects(removeStatusWithProjects);
        deployUpdateIssue.setSchemeId(schemeId);
        deployUpdateIssue.setOrganizationId(organizationId);
        deployUpdateIssue.setUserId(DetailsHelper.getUserDetails().getUserId());
        sagaClient.startSaga(DEPLOY_STATE_MACHINE_SCHEME, new StartInstanceDTO(JSON.toJSONString(deployUpdateIssue), "", "", ResourceLevel.ORGANIZATION.value(), organizationId));
        logger.info("startSaga deploy-state-machine-scheme addStatusIds: {}, deleteStatusIds: {}", changeStatus.getAddStatusIds(), changeStatus.getDeleteStatusIds());
    }

    @Saga(code = DEPLOY_STATE_MACHINE, description = "发布状态机", inputSchemaClass = DeployStateMachinePayload.class)
    public void deployStateMachine(Long organizationId, Long stateMachineId, Map<String, List<StatusDTO>> changeMap) {
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
        //发送saga，
        sagaClient.startSaga(DEPLOY_STATE_MACHINE, new StartInstanceDTO(JSON.toJSONString(deployStateMachinePayload), "", "", ResourceLevel.ORGANIZATION.value(), organizationId));
        logger.info("startSaga deploy-state-machine addStatusIds: {}, deleteStatusIds: {}", changeStatus.getAddStatusIds(), changeStatus.getDeleteStatusIds());
    }
}

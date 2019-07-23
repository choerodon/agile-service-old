package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.api.vo.event.ChangeStatus;
import io.choerodon.agile.api.vo.event.StateMachineSchemeChangeItem;
import io.choerodon.agile.api.vo.event.StateMachineSchemeDeployCheckIssue;
import io.choerodon.agile.api.vo.event.StateMachineSchemeStatusChangeItem;
import io.choerodon.agile.app.service.*;
import io.choerodon.agile.infra.annotation.ChangeSchemeStatus;
import io.choerodon.agile.infra.dataobject.ProjectConfigDTO;
import io.choerodon.agile.infra.dataobject.StateMachineSchemeConfigDTO;
import io.choerodon.agile.infra.dataobject.StateMachineSchemeConfigDraftDTO;
import io.choerodon.agile.infra.dataobject.StateMachineSchemeDTO;
import io.choerodon.agile.infra.enums.SchemeType;
import io.choerodon.agile.infra.enums.StateMachineSchemeDeployStatus;
import io.choerodon.agile.infra.enums.StateMachineSchemeStatus;
import io.choerodon.agile.infra.mapper.ProjectConfigMapper;
import io.choerodon.agile.infra.mapper.StateMachineSchemeConfigDraftMapper;
import io.choerodon.agile.infra.mapper.StateMachineSchemeConfigMapper;
import io.choerodon.agile.infra.mapper.StateMachineSchemeMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.entity.Criteria;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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
 * @Date 2018/8/2
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class StateMachineSchemeConfigServiceImpl implements StateMachineSchemeConfigService {

    @Autowired
    private StateMachineSchemeConfigMapper configMapper;
    @Autowired
    private StateMachineSchemeConfigDraftMapper configDraftMapper;
    @Autowired
    private StateMachineSchemeService stateMachineSchemeService;
    @Autowired
    private StateMachineSchemeMapper schemeMapper;
    @Autowired
    private IssueTypeService issueTypeService;
    @Autowired
    private ProjectConfigMapper projectConfigMapper;
    @Autowired
    private StateMachineClientService stateMachineClientService;
    @Autowired
    private StateMachineService stateMachineService;
    @Autowired
    private SagaServiceImpl sagaService;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    @ChangeSchemeStatus
    public StateMachineSchemeVO delete(Long organizationId, Long schemeId, Long stateMachineId) {
        //删除草稿
        StateMachineSchemeConfigDraftDTO config = new StateMachineSchemeConfigDraftDTO();
        config.setOrganizationId(organizationId);
        config.setSchemeId(schemeId);
        config.setStateMachineId(stateMachineId);
        int isDelete = configDraftMapper.delete(config);
        if (isDelete < 1) {
            throw new CommonException("error.stateMachineSchemeConfig.delete");
        }
        return stateMachineSchemeService.querySchemeWithConfigById(true, organizationId, schemeId);
    }

    @Override
    public void deleteBySchemeId(Long organizationId, Long schemeId) {
        //删除草稿
        StateMachineSchemeConfigDraftDTO draft = new StateMachineSchemeConfigDraftDTO();
        draft.setOrganizationId(organizationId);
        draft.setSchemeId(schemeId);
        configDraftMapper.delete(draft);
        //删除发布
        StateMachineSchemeConfigDTO config = new StateMachineSchemeConfigDTO();
        config.setOrganizationId(organizationId);
        config.setSchemeId(schemeId);
        configMapper.delete(config);
    }

    @Override
    @ChangeSchemeStatus
    public StateMachineSchemeVO create(Long organizationId, Long schemeId, Long stateMachineId, List<StateMachineSchemeConfigVO> schemeVOS) {
        List<StateMachineSchemeConfigDraftDTO> configs = modelMapper.map(schemeVOS, new TypeToken<List<StateMachineSchemeConfigDraftDTO>>() {
        }.getType());
        //删除之前的草稿配置
        StateMachineSchemeConfigDraftDTO delConfig = new StateMachineSchemeConfigDraftDTO();
        delConfig.setSchemeId(schemeId);
        delConfig.setStateMachineId(stateMachineId);
        delConfig.setDefault(false);
        configDraftMapper.delete(delConfig);
        for (StateMachineSchemeConfigDraftDTO config : configs) {
            delConfig.setStateMachineId(null);
            delConfig.setSchemeId(schemeId);
            delConfig.setIssueTypeId(config.getIssueTypeId());
            configDraftMapper.delete(delConfig);
            config.setSchemeId(schemeId);
            config.setStateMachineId(stateMachineId);
            config.setOrganizationId(organizationId);
            config.setDefault(false);
        }
        configs.forEach(c -> configDraftMapper.insert(c));
        return stateMachineSchemeService.querySchemeWithConfigById(true, organizationId, schemeId);
    }

    @Override
    public void createDefaultConfig(Long organizationId, Long schemeId, Long stateMachineId) {
        //创建草稿
        StateMachineSchemeConfigDraftDTO defaultConfig = new StateMachineSchemeConfigDraftDTO();
        defaultConfig.setStateMachineId(stateMachineId);
        defaultConfig.setSequence(0);
        defaultConfig.setIssueTypeId(0L);
        defaultConfig.setSchemeId(schemeId);
        defaultConfig.setOrganizationId(organizationId);
        defaultConfig.setDefault(true);
        int isInsert = configDraftMapper.insert(defaultConfig);
        if (isInsert < 1) {
            throw new CommonException("error.stateMachineSchemeConfig.insert");
        }
    }

    @Override
    @ChangeSchemeStatus
    public void updateDefaultConfig(Long organizationId, Long schemeId, Long stateMachineId) {
        //更新草稿
        StateMachineSchemeConfigDraftDTO defaultConfig = configDraftMapper.selectDefault(organizationId, schemeId);
        defaultConfig.setStateMachineId(stateMachineId);
        Criteria criteria = new Criteria();
        criteria.update("stateMachineId");
        configDraftMapper.updateByPrimaryKeyOptions(defaultConfig, criteria);
    }

    @Override
    public StateMachineSchemeConfigVO selectDefault(Boolean isDraft, Long organizationId, Long schemeId) {
        StateMachineSchemeConfigVO configVO;
        if (isDraft) {
            configVO = modelMapper.map(configDraftMapper.selectDefault(organizationId, schemeId), StateMachineSchemeConfigVO.class);
        } else {
            configVO = modelMapper.map(configMapper.selectDefault(organizationId, schemeId), StateMachineSchemeConfigVO.class);
        }
        return configVO;
    }

    @Override
    public Long queryStateMachineIdBySchemeIdAndIssueTypeId(Boolean isDraft, Long organizationId, Long schemeId, Long issueTypeId) {
        if (isDraft) {
            StateMachineSchemeConfigDraftDTO config = new StateMachineSchemeConfigDraftDTO();
            config.setOrganizationId(organizationId);
            config.setSchemeId(schemeId);
            config.setIssueTypeId(issueTypeId);
            List<StateMachineSchemeConfigDraftDTO> configs = configDraftMapper.select(config);
            if (!configs.isEmpty()) {
                return configs.get(0).getStateMachineId();
            } else {
                //找不到对应的issueType则取默认
                return configDraftMapper.selectDefault(organizationId, schemeId).getStateMachineId();
            }
        } else {
            StateMachineSchemeConfigDTO config = new StateMachineSchemeConfigDTO();
            config.setOrganizationId(organizationId);
            config.setSchemeId(schemeId);
            config.setIssueTypeId(issueTypeId);
            List<StateMachineSchemeConfigDTO> configs = configMapper.select(config);
            if (!configs.isEmpty()) {
                return configs.get(0).getStateMachineId();
            } else {
                //找不到对应的issueType则取默认
                return configMapper.selectDefault(organizationId, schemeId).getStateMachineId();
            }
        }
    }

    @Override
    public List<Long> queryIssueTypeIdBySchemeIdAndStateMachineId(Boolean isDraft, Long organizationId, Long schemeId, Long stateMachineId) {
        if (isDraft) {
            StateMachineSchemeConfigDraftDTO config = new StateMachineSchemeConfigDraftDTO();
            config.setOrganizationId(organizationId);
            config.setSchemeId(schemeId);
            config.setStateMachineId(stateMachineId);
            List<StateMachineSchemeConfigDraftDTO> configs = configDraftMapper.select(config);
            return configs.stream().map(StateMachineSchemeConfigDraftDTO::getIssueTypeId).collect(Collectors.toList());
        } else {
            StateMachineSchemeConfigDTO config = new StateMachineSchemeConfigDTO();
            config.setOrganizationId(organizationId);
            config.setSchemeId(schemeId);
            config.setStateMachineId(stateMachineId);
            List<StateMachineSchemeConfigDTO> configs = configMapper.select(config);
            return configs.stream().map(StateMachineSchemeConfigDTO::getIssueTypeId).collect(Collectors.toList());
        }
    }

    @Override
    public List<StateMachineSchemeConfigVO> queryBySchemeId(Boolean isDraft, Long organizationId, Long schemeId) {
        List<StateMachineSchemeConfigVO> configVOS;
        if (isDraft) {
            StateMachineSchemeConfigDraftDTO select = new StateMachineSchemeConfigDraftDTO();
            select.setOrganizationId(organizationId);
            select.setSchemeId(schemeId);
            configVOS = modelMapper.map(configDraftMapper.select(select), new TypeToken<List<StateMachineSchemeConfigVO>>() {
            }.getType());
        } else {
            StateMachineSchemeConfigDTO select = new StateMachineSchemeConfigDTO();
            select.setOrganizationId(organizationId);
            select.setSchemeId(schemeId);
            configVOS = modelMapper.map(configMapper.select(select), new TypeToken<List<StateMachineSchemeConfigVO>>() {
            }.getType());
        }
        return configVOS;
    }

    @Override
    public List<Long> querySchemeIdsByStateMachineId(Boolean isDraft, Long organizationId, Long stateMachineId) {
        List<Long> schemeIds;
        if (isDraft) {
            StateMachineSchemeConfigDraftDTO select = new StateMachineSchemeConfigDraftDTO();
            select.setStateMachineId(stateMachineId);
            select.setOrganizationId(organizationId);
            schemeIds = configDraftMapper.select(select).stream().map(StateMachineSchemeConfigDraftDTO::getSchemeId).distinct().collect(Collectors.toList());
        } else {
            StateMachineSchemeConfigDTO select = new StateMachineSchemeConfigDTO();
            select.setStateMachineId(stateMachineId);
            select.setOrganizationId(organizationId);
            schemeIds = configMapper.select(select).stream().map(StateMachineSchemeConfigDTO::getSchemeId).distinct().collect(Collectors.toList());
        }
        return schemeIds;
    }

    /**
     * 根据方案id获取到项目的新状态和旧状态
     *
     * @param organizationId
     * @param schemeId
     * @return
     */
    private Map<String, List<Long>> queryStatusIdsBySchemeId(Long organizationId, Long schemeId) {
        Map<String, List<Long>> changeMap = new HashMap<>(2);
        List<StateMachineWithStatusVO> stateMachineWithStatusVOS = stateMachineService.queryAllWithStatus(organizationId);
        Map<Long, List<StatusVO>> smMap = stateMachineWithStatusVOS.stream().collect(Collectors.toMap(StateMachineWithStatusVO::getId, StateMachineWithStatusVO::getStatusVOS));
        //获取发布配置
        List<Long> oldStatusIds = new ArrayList<>();
        StateMachineSchemeConfigDTO config = new StateMachineSchemeConfigDTO();
        config.setSchemeId(schemeId);
        config.setOrganizationId(organizationId);
        List<StateMachineSchemeConfigDTO> deploys = configMapper.select(config);
        List<Long> oldStateMachineIds = deploys.stream().map(StateMachineSchemeConfigDTO::getStateMachineId).collect(Collectors.toList());
        for (Long oldStateMachineId : oldStateMachineIds) {
            oldStatusIds.addAll(smMap.get(oldStateMachineId).stream().map(StatusVO::getId).collect(Collectors.toList()));

        }
        //获取草稿配置
        List<Long> newStatusIds = new ArrayList<>();
        StateMachineSchemeConfigDraftDTO draft = new StateMachineSchemeConfigDraftDTO();
        draft.setSchemeId(schemeId);
        draft.setOrganizationId(organizationId);
        List<StateMachineSchemeConfigDraftDTO> drafts = configDraftMapper.select(draft);
        List<Long> newStateMachineIds = drafts.stream().map(StateMachineSchemeConfigDraftDTO::getStateMachineId).collect(Collectors.toList());
        for (Long newStateMachineId : newStateMachineIds) {
            newStatusIds.addAll(smMap.get(newStateMachineId).stream().map(StatusVO::getId).collect(Collectors.toList()));
        }

        oldStatusIds = oldStatusIds.stream().distinct().collect(Collectors.toList());
        newStatusIds = newStatusIds.stream().distinct().collect(Collectors.toList());

        //减少的状态
        List<Long> deleteStatusIds = new ArrayList<>(oldStatusIds);
        deleteStatusIds.removeAll(newStatusIds);
        //增加的状态
        List<Long> addStatusIds = new ArrayList<>(newStatusIds);
        addStatusIds.removeAll(oldStatusIds);
        //减少的状态机
        List<Long> deleteStateMachineIds = new ArrayList<>(oldStateMachineIds);
        deleteStateMachineIds.removeAll(newStateMachineIds);
        //增加的状态机
        List<Long> addStateMachineIds = new ArrayList<>(newStateMachineIds);
        addStateMachineIds.removeAll(oldStateMachineIds);

        changeMap.put("deleteStatusIds", deleteStatusIds);
        changeMap.put("addStatusIds", addStatusIds);
        changeMap.put("deleteStateMachineIds", deleteStateMachineIds);
        changeMap.put("addStateMachineIds", addStateMachineIds);
        return changeMap;
    }

    @Override
    public Boolean deploy(Long organizationId, Long schemeId, List<StateMachineSchemeChangeItem> changeItems, Long objectVersionNumber) {
        StateMachineSchemeDTO select = schemeMapper.selectByPrimaryKey(schemeId);
        if ("doing".equals(select.getDeployStatus()) || !select.getObjectVersionNumber().equals(objectVersionNumber)) {
            throw new CommonException("error.stateMachineScheme.illegal");
        }
        //获取当前方案增加的状态和减少的状态
        Map<String, List<Long>> changeMap = queryStatusIdsBySchemeId(organizationId, schemeId);
        List<Long> deleteStatusIds = changeMap.get("deleteStatusIds");
        List<Long> addStatusIds = changeMap.get("addStatusIds");
        //复制草稿配置到发布配置
        copyDraftToDeploy(true, organizationId, schemeId);
        //更新状态机方案状态为：活跃
        StateMachineSchemeDTO scheme = schemeMapper.selectByPrimaryKey(schemeId);
        scheme.setStatus(StateMachineSchemeStatus.ACTIVE);
        Criteria criteria = new Criteria();
        criteria.update("status");
        schemeMapper.updateByPrimaryKeyOptions(scheme, criteria);
        //发布后，再进行状态增加与减少的判断，并发送saga
        ChangeStatus changeStatus = new ChangeStatus(addStatusIds, deleteStatusIds);
        //发布之前，更新deployStatus为doing
        schemeMapper.updateDeployStatus(organizationId, schemeId, StateMachineSchemeDeployStatus.DOING);
        sagaService.deployStateMachineScheme(organizationId, schemeId, changeItems, changeStatus);
        //新增的状态机ids和删除的状态机ids
        List<Long> deleteStateMachineIds = changeMap.get("deleteStateMachineIds");
        List<Long> addStateMachineIds = changeMap.get("addStateMachineIds");
        //活跃方案下的新增的状态机（状态为create的改成active）
        if (!addStateMachineIds.isEmpty()) {
            stateMachineService.activeStateMachines(organizationId, addStateMachineIds);
        }
        //使删除的状态机变成未活跃（状态为active和draft的改成create）
        if (!deleteStateMachineIds.isEmpty()) {
            stateMachineService.notActiveStateMachine(organizationId, deleteStateMachineIds);
        }
        return true;
    }

    @Override
    public List<StateMachineSchemeChangeItem> checkDeploy(Long organizationId, Long schemeId) {
        //获取发布配置
        StateMachineSchemeConfigDTO config = new StateMachineSchemeConfigDTO();
        config.setSchemeId(schemeId);
        config.setOrganizationId(organizationId);
        List<StateMachineSchemeConfigDTO> deploys = configMapper.select(config);
        Map<Long, Long> deployMap = deploys.stream().collect(Collectors.toMap(StateMachineSchemeConfigDTO::getIssueTypeId, StateMachineSchemeConfigDTO::getStateMachineId));
        Long deployDefaultStateMachineId = deployMap.get(0L);
        deployMap.remove(0L);
        //获取草稿配置
        StateMachineSchemeConfigDraftDTO draft = new StateMachineSchemeConfigDraftDTO();
        draft.setSchemeId(schemeId);
        draft.setOrganizationId(organizationId);
        List<StateMachineSchemeConfigDraftDTO> drafts = configDraftMapper.select(draft);
        Map<Long, Long> draftMap = drafts.stream().collect(Collectors.toMap(StateMachineSchemeConfigDraftDTO::getIssueTypeId, StateMachineSchemeConfigDraftDTO::getStateMachineId));
        Long draftDefaultStateMachineId = draftMap.get(0L);
        draftMap.remove(0L);
        //判断状态机有变化的问题类型
        int size = deployMap.size() + draftMap.size();
        List<StateMachineSchemeChangeItem> changeItems = new ArrayList<>(size);
        //因为发布的和草稿的都可能有增加或减少，因此需要两边都判断
        for (Map.Entry<Long, Long> entry : deployMap.entrySet()) {
            Long issueTypeId = entry.getKey();
            Long oldStateMachineId = entry.getValue();
            Long newStateMachineId = draftMap.getOrDefault(issueTypeId, draftDefaultStateMachineId);
            if (!oldStateMachineId.equals(newStateMachineId)) {
                changeItems.add(new StateMachineSchemeChangeItem(issueTypeId, oldStateMachineId, newStateMachineId));
            }
        }
        Map<Long, StateMachineSchemeChangeItem> changeItemsMap = changeItems.stream().collect(Collectors.toMap(StateMachineSchemeChangeItem::getIssueTypeId, x -> x));
        for (Map.Entry<Long, Long> entry : draftMap.entrySet()) {
            Long issueTypeId = entry.getKey();
            //未判断过
            if (changeItemsMap.get(issueTypeId) == null) {
                Long oldStateMachineId = deployMap.getOrDefault(issueTypeId, deployDefaultStateMachineId);
                Long newStateMachineId = entry.getValue();
                if (!oldStateMachineId.equals(newStateMachineId)) {
                    changeItems.add(new StateMachineSchemeChangeItem(issueTypeId, oldStateMachineId, newStateMachineId));
                }
            }
        }
        //获取所有状态机及状态机的状态列表
        List<StateMachineWithStatusVO> stateMachineWithStatusVOS = stateMachineService.queryAllWithStatus(organizationId);
        Map<Long, StateMachineWithStatusVO> stateMachineMap = stateMachineWithStatusVOS.stream().collect(Collectors.toMap(StateMachineWithStatusVO::getId, x -> x));
        //获取所有问题类型
        List<IssueTypeVO> issueTypeVOS = issueTypeService.queryByOrgId(organizationId);
        Map<Long, IssueTypeVO> issueTypeMap = issueTypeVOS.stream().collect(Collectors.toMap(IssueTypeVO::getId, x -> x));
        //获取当前方案配置的项目列表
        List<ProjectConfigDTO> projectConfigs = projectConfigMapper.queryConfigsBySchemeId(SchemeType.STATE_MACHINE, schemeId);
        //要传到agile进行判断的数据，返回所有有影响的issue数量，根据issueTypeId分类
        StateMachineSchemeDeployCheckIssue deployCheckIssue = new StateMachineSchemeDeployCheckIssue();
        deployCheckIssue.setIssueTypeIds(changeItems.stream().map(StateMachineSchemeChangeItem::getIssueTypeId).collect(Collectors.toList()));
        deployCheckIssue.setProjectConfigs(projectConfigs);
        Map<Long, Long> issueCounts = stateMachineService.checkStateMachineSchemeChange(organizationId, deployCheckIssue);
        //拼凑数据
        for (StateMachineSchemeChangeItem changeItem : changeItems) {
            Long issueTypeId = changeItem.getIssueTypeId();
            Long oldStateMachineId = changeItem.getOldStateMachineId();
            Long newStateMachineId = changeItem.getNewStateMachineId();
            StateMachineWithStatusVO oldStateMachine = stateMachineMap.get(oldStateMachineId);
            StateMachineWithStatusVO newStateMachine = stateMachineMap.get(newStateMachineId);
            List<StatusVO> oldSMStatuses = oldStateMachine.getStatusVOS();
            List<StatusVO> newSMStatuses = newStateMachine.getStatusVOS();
            List<StateMachineSchemeStatusChangeItem> stateMachineSchemeStatusChangeItems = new ArrayList<>();
            List<Long> newSMStatusIds = newSMStatuses.stream().map(StatusVO::getId).collect(Collectors.toList());
            StatusVO newDefault = newSMStatuses.get(0);
            oldSMStatuses.forEach(oldSMStatus -> {
                //如果旧的状态机中有的状态，新的状态机中没有，说明这个状态需要变更
                if (!newSMStatusIds.contains(oldSMStatus.getId())) {
                    StateMachineSchemeStatusChangeItem stateMachineSchemeStatusChangeItem = new StateMachineSchemeStatusChangeItem(oldSMStatus, newDefault);
                    stateMachineSchemeStatusChangeItems.add(stateMachineSchemeStatusChangeItem);
                }
            });
            changeItem.setIssueTypeVO(issueTypeMap.get(issueTypeId));
            changeItem.setIssueCount(issueCounts.get(issueTypeId));
            changeItem.setOldStateMachine(oldStateMachine);
            changeItem.setNewStateMachine(newStateMachine);
            changeItem.setStatusChangeItems(stateMachineSchemeStatusChangeItems);
        }
        //过滤掉状态不需要变更的问题类型
        changeItems = changeItems.stream().filter(changeItem -> !changeItem.getStatusChangeItems().isEmpty()).collect(Collectors.toList());
        return changeItems;
    }

    @Override
    public StateMachineSchemeVO deleteDraft(Long organizationId, Long schemeId) {
        //写入活跃的配置写到到草稿中，id一致
        copyDeployToDraft(true, organizationId, schemeId);
        //更新状态机方案状态为：活跃
        StateMachineSchemeDTO scheme = schemeMapper.selectByPrimaryKey(schemeId);
        scheme.setStatus(StateMachineSchemeStatus.ACTIVE);
        Criteria criteria = new Criteria();
        criteria.update("status");
        schemeMapper.updateByPrimaryKeyOptions(scheme, criteria);
        return stateMachineSchemeService.querySchemeWithConfigById(false, organizationId, schemeId);
    }

    @Override
    public void copyDeployToDraft(Boolean isDeleteOldDraft, Long organizationId, Long schemeId) {
        //删除草稿配置
        if (isDeleteOldDraft) {
            StateMachineSchemeConfigDraftDTO draft = new StateMachineSchemeConfigDraftDTO();
            draft.setSchemeId(schemeId);
            draft.setOrganizationId(organizationId);
            configDraftMapper.delete(draft);
        }
        //复制发布配置到草稿配置
        StateMachineSchemeConfigDTO config = new StateMachineSchemeConfigDTO();
        config.setSchemeId(schemeId);
        config.setOrganizationId(organizationId);
        List<StateMachineSchemeConfigDTO> configs = configMapper.select(config);
        if (configs != null && !configs.isEmpty()) {
            List<StateMachineSchemeConfigDraftDTO> configDrafts = modelMapper.map(configs, new TypeToken<List<StateMachineSchemeConfigDraftDTO>>() {
            }.getType());
            for (StateMachineSchemeConfigDraftDTO insertConfig : configDrafts) {
                int result = configDraftMapper.insert(insertConfig);
                if (result != 1) {
                    throw new CommonException("error.stateMachineSchemeConfig.create");
                }
            }
        }
    }

    @Override
    public void copyDraftToDeploy(Boolean isDeleteOldDeploy, Long organizationId, Long schemeId) {
        //删除发布配置
        if (isDeleteOldDeploy) {
            StateMachineSchemeConfigDTO deploy = new StateMachineSchemeConfigDTO();
            deploy.setSchemeId(schemeId);
            deploy.setOrganizationId(organizationId);
            configMapper.delete(deploy);
        }
        //复制草稿配置到发布配置
        StateMachineSchemeConfigDraftDTO draft = new StateMachineSchemeConfigDraftDTO();
        draft.setSchemeId(schemeId);
        draft.setOrganizationId(organizationId);
        List<StateMachineSchemeConfigDraftDTO> configs = configDraftMapper.select(draft);
        if (configs != null && !configs.isEmpty()) {
            List<StateMachineSchemeConfigDTO> configDrafts = modelMapper.map(configs, new TypeToken<List<StateMachineSchemeConfigDTO>>() {
            }.getType());
            for (StateMachineSchemeConfigDTO insertConfig : configDrafts) {
                int result = configMapper.insert(insertConfig);
                if (result != 1) {
                    throw new CommonException("error.stateMachineSchemeConfig.create");
                }
            }
        }
    }
}

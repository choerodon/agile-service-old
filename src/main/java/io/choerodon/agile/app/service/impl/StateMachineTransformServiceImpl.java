package io.choerodon.agile.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.api.vo.StateMachineNodeVO;
import io.choerodon.agile.api.vo.StateMachineTransformVO;
import io.choerodon.agile.api.vo.StatusVO;
import io.choerodon.agile.api.vo.event.TransformVO;
import io.choerodon.agile.app.service.StateMachineConfigService;
import io.choerodon.agile.app.service.StateMachineNodeService;
import io.choerodon.agile.app.service.StateMachineTransformService;
import io.choerodon.agile.infra.annotation.ChangeStateMachineStatus;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.enums.ConfigType;
import io.choerodon.agile.infra.enums.TransformConditionStrategy;
import io.choerodon.agile.infra.enums.TransformType;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.agile.infra.utils.EnumUtil;
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
 * @author peng.jiang, dinghuang123@gmail.com
 */
@Service
public class StateMachineTransformServiceImpl implements StateMachineTransformService {
    @Autowired
    private StateMachineTransformDraftMapper transformDraftMapper;
    @Autowired
    private StateMachineTransformMapper transformDeployMapper;
    @Autowired
    private StateMachineNodeService nodeService;
    @Autowired
    private StateMachineNodeDraftMapper nodeDraftMapper;
    @Autowired
    private StateMachineNodeMapper nodeDeployMapper;
    @Autowired
    private StateMachineConfigDraftMapper configMapper;
    @Autowired
    private StateMachineConfigService configService;
    @Autowired
    private StatusMapper stateMapper;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    @ChangeStateMachineStatus
    @Transactional(rollbackFor = Exception.class)
    public StateMachineTransformVO create(Long organizationId, Long stateMachineId, StateMachineTransformVO transformDTO) {
        transformDTO.setStateMachineId(stateMachineId);
        StateMachineTransformDraftDTO transform = new StateMachineTransformDraftDTO();
        transform.setStartNodeId(transformDTO.getStartNodeId());
        transform.setEndNodeId(transformDTO.getEndNodeId());
        transform.setName(transformDTO.getName());
        if (!transformDraftMapper.select(transform).isEmpty()) {
            throw new CommonException("error.stateMachineTransform.exist");
        }
        transform = modelMapper.map(transformDTO, StateMachineTransformDraftDTO.class);
        transform.setType(TransformType.CUSTOM);
        transform.setOrganizationId(organizationId);
        transform.setConditionStrategy(TransformConditionStrategy.ALL);

        int isInsert = transformDraftMapper.insert(transform);
        if (isInsert != 1) {
            throw new CommonException("error.stateMachineTransform.create");
        }
        return queryById(organizationId, transform.getId());
    }

    @Override
    @ChangeStateMachineStatus
    @Transactional(rollbackFor = Exception.class)
    public StateMachineTransformVO update(Long organizationId, Long stateMachineId, Long transformId, StateMachineTransformVO transformDTO) {
        transformDTO.setStateMachineId(stateMachineId);
        StateMachineTransformDraftDTO origin = transformDraftMapper.queryById(organizationId, transformId);
        if (origin == null) {
            throw new CommonException("error.stateMachineTransform.queryById.notFound");
        }
        StateMachineTransformDraftDTO transform = modelMapper.map(transformDTO, StateMachineTransformDraftDTO.class);
        transform.setId(transformId);
        transform.setOrganizationId(organizationId);
        transform.setType(origin.getType());
        int isUpdate = transformDraftMapper.updateByPrimaryKeySelective(transform);
        if (isUpdate != 1) {
            throw new CommonException("error.stateMachineTransform.update");
        }
        return queryById(organizationId, transformId);

    }

    @Override
    @ChangeStateMachineStatus
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Long organizationId, Long stateMachineId, Long transformId) {
        StateMachineTransformDraftDTO transform = transformDraftMapper.queryById(organizationId, transformId);
        if (!stateMachineId.equals(transform.getStateMachineId())) {
            throw new CommonException("error.stateMachineTransform.deleteIllegal");
        }
        int isDelete = transformDraftMapper.deleteByPrimaryKey(transformId);
        if (isDelete != 1) {
            throw new CommonException("error.stateMachineTransform.delete");
        }
        return true;
    }

    @Override
    public StateMachineTransformVO queryById(Long organizationId, Long transformId) {
        StateMachineTransformDraftDTO transform = transformDraftMapper.queryById(organizationId, transformId);
        if (transform == null) {
            throw new CommonException("error.stateMachineTransform.queryById.notFound");
        }
        StateMachineTransformVO transformVO = modelMapper.map(transform, StateMachineTransformVO.class);
        transformVO.setConditions(configService.queryByTransformId(organizationId, transformId, ConfigType.CONDITION, true));
        transformVO.setValidators(configService.queryByTransformId(organizationId, transformId, ConfigType.VALIDATOR, true));
        transformVO.setTriggers(configService.queryByTransformId(organizationId, transformId, ConfigType.TRIGGER, true));
        transformVO.setPostpositions(configService.queryByTransformId(organizationId, transformId, ConfigType.ACTION, true));
        //获取开始节点，若为初始转换，则没有开始节点
        if (TransformType.CUSTOM.equals(transform.getType())) {
            StateMachineNodeDraftDTO startNode = nodeDraftMapper.getNodeById(transformVO.getStartNodeId());
            StateMachineNodeVO nodeVO = modelMapper.map(startNode, StateMachineNodeVO.class);
            nodeVO.setStatusVO(modelMapper.map(startNode.getStatus(), StatusVO.class));
            transformVO.setStartNodeVO(nodeVO);
        }
        //获取结束节点
        StateMachineNodeDraftDTO endNode = nodeDraftMapper.getNodeById(transformVO.getEndNodeId());
        StateMachineNodeVO nodeVO = modelMapper.map(endNode, StateMachineNodeVO.class);
        nodeVO.setStatusVO(modelMapper.map(endNode.getStatus(), StatusVO.class));
        transformVO.setEndNodeVO(nodeVO);
        return transformVO;
    }

    @Override
    public StateMachineTransformDTO getInitTransform(Long organizationId, Long stateMachineId) {
        StateMachineTransformDTO transform = new StateMachineTransformDTO();
        transform.setStateMachineId(stateMachineId);
        transform.setOrganizationId(organizationId);
        transform.setType(TransformType.INIT);
        List<StateMachineTransformDTO> transforms = transformDeployMapper.select(transform);
        if (transforms.isEmpty()) {
            throw new CommonException("error.initTransform.null");
        }
        return transforms.get(0);
    }

    @Override
    public List<StateMachineTransformDTO> queryListByStatusIdByDeploy(Long organizationId, Long stateMachineId, Long statusId) {
        StateMachineNodeDTO startNode = nodeDeployMapper.getNodeDeployByStatusId(stateMachineId, statusId);
        if (startNode == null) {
            throw new CommonException("error.statusId.notFound");
        }
        return transformDeployMapper.queryByStartNodeIdOrType(organizationId, stateMachineId, startNode.getId(), TransformType.ALL);
    }

    @Override
    @ChangeStateMachineStatus
    @Transactional(rollbackFor = Exception.class)
    public StateMachineTransformVO createAllStatusTransform(Long organizationId, Long stateMachineId, Long endNodeId) {
        if (endNodeId == null) {
            throw new CommonException("error.endNodeId.null");
        }
        StateMachineNodeDraftDTO stateMachineNodeDraft = nodeDraftMapper.getNodeById(endNodeId);
        if (stateMachineNodeDraft == null) {
            throw new CommonException("error.stateMachineNode.null");
        }
        //创建【全部转换到当前】的transform
        StatusDTO state = stateMapper.queryById(organizationId, stateMachineNodeDraft.getStatusId());
        if (state == null) {
            throw new CommonException("error.createAllStatusTransform.state.null");
        }
        //判断当前节点是否已存在【全部】的转换id
        StateMachineTransformDraftDTO select = new StateMachineTransformDraftDTO();
        select.setStateMachineId(stateMachineId);
        select.setEndNodeId(endNodeId);
        select.setType(TransformType.ALL);
        if (!transformDraftMapper.select(select).isEmpty()) {
            throw new CommonException("error.stateMachineTransform.exist");
        }

        //创建
        StateMachineTransformDraftDTO transform = new StateMachineTransformDraftDTO();
        transform.setStateMachineId(stateMachineId);
        transform.setName(state.getName());
        transform.setDescription("全部转换");
        transform.setStartNodeId(0L);
        transform.setEndNodeId(endNodeId);
        transform.setOrganizationId(organizationId);
        transform.setType(TransformType.ALL);
        transform.setConditionStrategy(TransformConditionStrategy.ALL);
        int isInsert = transformDraftMapper.insert(transform);
        if (isInsert != 1) {
            throw new CommonException("error.stateMachineTransform.create");
        }
        //更新node的【全部转换到当前】转换id
        int update = nodeDraftMapper.updateAllStatusTransformId(organizationId, endNodeId, transform.getId());
        if (update != 1) {
            throw new CommonException("error.createAllStatusTransform.updateAllStatusTransformId");
        }
        transform = transformDraftMapper.queryById(organizationId, transform.getId());
        return modelMapper.map(transform, StateMachineTransformVO.class);
    }

    public StateMachineTransformServiceImpl() {
        super();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteAllStatusTransform(Long organizationId, Long transformId) {
        StateMachineTransformDraftDTO transformDraft = transformDraftMapper.queryById(organizationId, transformId);
        if (transformDraft == null) {
            throw new CommonException("error.stateMachineTransform.null");
        }
        if (!TransformType.ALL.equals(transformDraft.getType())) {
            throw new CommonException("error.stateMachineTransform.type.illegal");
        }
        //目标节点
        StateMachineNodeDraftDTO node = nodeDraftMapper.getNodeById(transformDraft.getEndNodeId());
        if (node == null) {
            throw new CommonException("error.stateMachineNode.null");
        }
        //删除【全部转换到当前】的转换
        Boolean result = delete(organizationId, transformDraft.getStateMachineId(), node.getAllStatusTransformId());
        //更新node的【全部转换到当前】转换id
        int update = nodeDraftMapper.updateAllStatusTransformId(organizationId, transformDraft.getEndNodeId(), null);
        if (update != 1) {
            throw new CommonException("error.deleteAllStatusTransform.updateAllStatusTransformId");
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateConditionStrategy(Long organizationId, Long transformId, String conditionStrategy) {
        if (!EnumUtil.contain(TransformConditionStrategy.class, conditionStrategy)) {
            throw new CommonException("error.updateConditionStrategy.conditionStrategy.illegal");
        }
        StateMachineTransformDraftDTO transform = transformDraftMapper.queryById(organizationId, transformId);
        if (transform == null) {
            throw new CommonException("error.updateConditionStrategy.queryById.notFound");
        }
        transform.setConditionStrategy(conditionStrategy);
        Criteria criteria = new Criteria();
        criteria.update("conditionStrategy");
        int update = transformDraftMapper.updateByPrimaryKeyOptions(transform, criteria);
        if (update != 1) {
            throw new CommonException("error.updateConditionStrategy.updateOptional");
        }
        return true;
    }

    @Override
    public Boolean checkName(Long organizationId, Long stateMachineId, Long startNodeId, Long endNodeId, String name) {
        StateMachineTransformDraftDTO transformDraft = new StateMachineTransformDraftDTO();
        transformDraft.setOrganizationId(organizationId);
        transformDraft.setName(name);
        transformDraft.setStartNodeId(startNodeId);
        transformDraft.setEndNodeId(endNodeId);
        transformDraft.setStateMachineId(stateMachineId);
        return transformDraftMapper.select(transformDraft).isEmpty();
    }

    @Override
    public Map<Long, Map<Long, List<TransformVO>>> queryStatusTransformsMap(Long organizationId, List<Long> stateMachineIds) {
        if (stateMachineIds == null || stateMachineIds.isEmpty()) {
            return null;
        }
        Map<Long, Map<Long, List<TransformVO>>> resultMap = new HashMap<>(stateMachineIds.size());
        List<StateMachineTransformDTO> allTransforms = transformDeployMapper.queryByStateMachineIds(organizationId, stateMachineIds);
        List<TransformVO> allTransformVOS = modelMapper.map(allTransforms, new TypeToken<List<TransformVO>>() {
        }.getType());
        Map<Long, List<TransformVO>> transformStateMachineIdMap = allTransformVOS.stream().collect(Collectors.groupingBy(TransformVO::getStateMachineId));
        List<StateMachineNodeDTO> allNodes = nodeDeployMapper.queryByStateMachineIds(organizationId, stateMachineIds);
        Map<Long, List<StateMachineNodeDTO>> nodeStateMachineIdMap = allNodes.stream().collect(Collectors.groupingBy(StateMachineNodeDTO::getStateMachineId));
        for (Long stateMachineId : stateMachineIds) {
            List<TransformVO> transforms = transformStateMachineIdMap.get(stateMachineId) != null ? transformStateMachineIdMap.get(stateMachineId) : new ArrayList<>();
            List<TransformVO> typeAll = transforms.stream().filter(x -> x.getType().equals(TransformType.ALL)).collect(Collectors.toList());
            Map<Long, List<TransformVO>> startListMap = transforms.stream().collect(Collectors.groupingBy(TransformVO::getStartNodeId));
            List<StateMachineNodeDTO> nodes = nodeStateMachineIdMap.get(stateMachineId) != null ? nodeStateMachineIdMap.get(stateMachineId) : new ArrayList<>();
            Map<Long, List<TransformVO>> statusMap = new HashMap<>(nodes.size());
            for (StateMachineNodeDTO node : nodes) {
                List<TransformVO> nodeTransforms = startListMap.get(node.getId()) != null ? startListMap.get(node.getId()) : new ArrayList<>();
                nodeTransforms.addAll(typeAll);
                //增加一个自身状态的转换（用于拖动时的转换显示）
                TransformVO self = new TransformVO();
                self.setEndStatusId(node.getStatusId());
                nodeTransforms.add(self);
                statusMap.put(node.getStatusId(), nodeTransforms);
            }
            resultMap.put(stateMachineId, statusMap);
        }
        return resultMap;
    }

    @Override
    public StateMachineTransformDTO queryDeployTransformForAgile(Long organizationId, Long transformId) {
        return transformDeployMapper.queryById(organizationId, transformId);
    }
}

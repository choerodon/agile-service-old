package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.StateMachineNodeVO;
import io.choerodon.agile.api.vo.StateMachineTransformVO;
import io.choerodon.agile.api.vo.StatusVO;
import io.choerodon.agile.app.service.StateMachineClientService;
import io.choerodon.agile.app.service.StateMachineNodeService;
import io.choerodon.agile.app.service.StateMachineService;
import io.choerodon.agile.infra.annotation.ChangeStateMachineStatus;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.enums.*;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.core.exception.CommonException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author peng.jiang, dinghuang123@gmail.com
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class StateMachineNodeServiceImpl implements StateMachineNodeService {

    @Autowired
    private StateMachineNodeDraftMapper nodeDraftMapper;
    @Autowired
    private StateMachineNodeMapper nodeDeployMapper;
    @Autowired
    private StateMachineTransformDraftMapper transformDraftMapper;
    @Autowired
    private StateMachineTransformMapper transformDeployMapper;
    @Autowired
    private StatusMapper statusMapper;
    @Autowired
    private StateMachineMapper stateMachineMapper;
    @Autowired
    private StateMachineClientService stateMachineClientService;
    @Autowired
    private StateMachineService stateMachineService;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    @ChangeStateMachineStatus
    public List<StateMachineNodeVO> create(Long organizationId, Long stateMachineId, StateMachineNodeVO nodeVO) {
        nodeVO.setStateMachineId(stateMachineId);
        nodeVO.setOrganizationId(organizationId);
        createStatus(organizationId, nodeVO);
        StateMachineNodeDraftDTO node = modelMapper.map(nodeVO, StateMachineNodeDraftDTO.class);
        node.setWidth(InitNode.INIT.getWidth());
        node.setHeight(InitNode.INIT.getHeight());
        node.setType(NodeType.CUSTOM);
        if (nodeDraftMapper.select(node).isEmpty()) {
            int isInsert = nodeDraftMapper.insert(node);
            if (isInsert != 1) {
                throw new CommonException("error.stateMachineNode.create");
            }
        }
        return queryByStateMachineId(organizationId, node.getStateMachineId(), true);
    }

    @Override
    @ChangeStateMachineStatus
    public List<StateMachineNodeVO> update(Long organizationId, Long stateMachineId, Long nodeId, StateMachineNodeVO nodeVO) {
        nodeVO.setStateMachineId(stateMachineId);
        nodeVO.setOrganizationId(organizationId);
        createStatus(organizationId, nodeVO);
        StateMachineNodeDraftDTO node = modelMapper.map(nodeVO, StateMachineNodeDraftDTO.class);
        node.setId(nodeId);
        int isUpdate = nodeDraftMapper.updateByPrimaryKeySelective(node);
        if (isUpdate != 1) {
            throw new CommonException("error.stateMachineNode.update");
        }
        return queryByStateMachineId(organizationId, node.getStateMachineId(), true);
    }

    @Override
    @ChangeStateMachineStatus
    public List<StateMachineNodeVO> delete(Long organizationId, Long stateMachineId, Long nodeId) {
        StateMachineNodeDraftDTO node = nodeDraftMapper.queryById(organizationId, nodeId);
        if (node == null) {
            throw new CommonException("error.node.notFound");
        }
        if (!node.getType().equals(NodeType.CUSTOM)) {
            throw new CommonException("error.node.delete.illegal");
        }
        //校验节点的状态是否关联状态机
        if ((Boolean) checkDelete(organizationId, stateMachineId, node.getStatusId()).get("canDelete")) {
            int isDelete = nodeDraftMapper.deleteByPrimaryKey(nodeId);
            if (isDelete != 1) {
                throw new CommonException("error.stateMachineNode.delete");
            }
            //删除关联的转换
            transformDraftMapper.deleteByNodeId(nodeId);
        } else {
            throw new CommonException("error.stateMachineNode.statusHasIssues");
        }
        return queryByStateMachineId(organizationId, stateMachineId, true);
    }

    @Override
    public Map<String, Object> checkDelete(Long organizationId, Long stateMachineId, Long statusId) {
        Map<String, Object> result = new HashMap<>(2);
        StateMachineDTO stateMachine = stateMachineMapper.queryById(organizationId, stateMachineId);
        if (stateMachine == null) {
            throw new CommonException("error.stateMachine.notFound");
        }
        StatusDTO status = statusMapper.queryById(organizationId, statusId);
        if (status == null) {
            throw new CommonException("error.status.notFound");
        }
        //只有草稿状态才进行删除校验
        if (stateMachine.getStatus().equals(StateMachineStatus.CREATE)) {
            result.put("canDelete", true);
        } else {
            result = stateMachineService.checkDeleteNode(organizationId, stateMachineId, statusId);
        }
        return result;
    }

    @Override
    public StateMachineNodeVO queryById(Long organizationId, Long nodeId) {
        StateMachineNodeDraftDTO node = nodeDraftMapper.getNodeById(nodeId);
        if (node == null) {
            throw new CommonException("error.stateMachineNode.noFound");
        }
        StateMachineNodeVO nodeVO = modelMapper.map(node, StateMachineNodeVO.class);
        nodeVO.setStatusVO(modelMapper.map(node.getStatus(), StatusVO.class));
        //获取进入的转换
        StateMachineTransformDraftDTO intoTransformSearch = new StateMachineTransformDraftDTO();
        intoTransformSearch.setEndNodeId(nodeId);
        List<StateMachineTransformDraftDTO> intoTransforms = transformDraftMapper.select(intoTransformSearch);
        nodeVO.setIntoTransform(modelMapper.map(intoTransforms, new TypeToken<List<StateMachineTransformVO>>() {
        }.getType()));
        //获取出去的转换
        StateMachineTransformDraftDTO outTransformSerach = new StateMachineTransformDraftDTO();
        outTransformSerach.setStartNodeId(nodeId);
        List<StateMachineTransformDraftDTO> outTransforms = transformDraftMapper.select(outTransformSerach);
        nodeVO.setOutTransform(modelMapper.map(outTransforms, new TypeToken<List<StateMachineTransformVO>>() {
        }.getType()));
        return nodeVO;
    }

    /**
     * 状态机下新增状态
     *
     * @param organizationId 组织id
     * @param nodeVO         节点
     */
    private void createStatus(Long organizationId, StateMachineNodeVO nodeVO) {
        if (nodeVO.getStatusId() == null && nodeVO.getStatusVO() != null && nodeVO.getStatusVO().getName() != null) {
            StatusDTO status = modelMapper.map(nodeVO.getStatusVO(), StatusDTO.class);
            status.setOrganizationId(organizationId);
            int isStateInsert = statusMapper.insert(status);
            if (isStateInsert != 1) {
                throw new CommonException("error.status.create");
            }
            nodeVO.setStatusId(status.getId());
        }
    }

    /**
     * 初始节点
     *
     * @param stateMachineId
     * @return
     */
    @Override
    public Long getInitNode(Long organizationId, Long stateMachineId) {
        StateMachineNodeDraftDTO node = new StateMachineNodeDraftDTO();
        node.setType(NodeType.START);
        node.setStateMachineId(stateMachineId);
        node.setOrganizationId(organizationId);
        List<StateMachineNodeDraftDTO> nodes = nodeDraftMapper.select(node);
        if (nodes.isEmpty()) {
            throw new CommonException("error.initNode.null");
        }
        return nodes.get(0).getId();
    }

    @Override
    public List<StateMachineNodeVO> queryByStateMachineId(Long organizationId, Long stateMachineId, Boolean isDraft) {
        List<StateMachineNodeVO> nodeVOS;
        if (isDraft) {
            //获取节点
            List<StateMachineNodeDraftDTO> nodes = nodeDraftMapper.selectByStateMachineId(stateMachineId);
            Map<Long, StatusDTO> map = nodes.stream().filter(x -> x.getStatus() != null).collect(Collectors.toMap(StateMachineNodeDraftDTO::getId, StateMachineNodeDraftDTO::getStatus));
            nodeVOS = modelMapper.map(nodes, new TypeToken<List<StateMachineNodeVO>>() {
            }.getType());
            for (StateMachineNodeVO nodeVO : nodeVOS) {
                StatusDTO status = map.get(nodeVO.getId());
                if (status != null) {
                    nodeVO.setStatusVO(modelMapper.map(status, StatusVO.class));
                }
            }
        } else {
            List<StateMachineNodeDTO> nodes = nodeDeployMapper.selectByStateMachineId(stateMachineId);
            Map<Long, StatusDTO> map = nodes.stream().filter(x -> x.getStatus() != null).collect(Collectors.toMap(StateMachineNodeDTO::getId, StateMachineNodeDTO::getStatus));
            nodeVOS = modelMapper.map(nodes, new TypeToken<List<StateMachineNodeVO>>() {
            }.getType());
            for (StateMachineNodeVO nodeVO : nodeVOS) {
                StatusDTO status = map.get(nodeVO.getId());
                if (status != null) {
                    nodeVO.setStatusVO(modelMapper.map(status, StatusVO.class));
                }
            }
        }
        return nodeVOS;
    }

    /**
     * 敏捷添加状态，需要先在草稿中判断有没有存在该节点，并添加草稿全部转换，然后把该节点和该转换带到发布中
     *
     * @param organizationId
     * @param stateMachineId
     * @param statusVO
     */
    @Override
    public void createNodeAndTransformForAgile(Long organizationId, Long stateMachineId, StatusVO statusVO) {
        Long statusId = statusVO.getId();
        //校验是否已经存在在发布的状态机中
        StateMachineNodeDTO deploy = new StateMachineNodeDTO();
        deploy.setStatusId(statusId);
        deploy.setStateMachineId(stateMachineId);
        if (nodeDeployMapper.select(deploy).isEmpty()) {
            //判断草稿中是否存在节点、转换
            StateMachineNodeDraftDTO nodeDraft = new StateMachineNodeDraftDTO();
            nodeDraft.setStatusId(statusId);
            nodeDraft.setStateMachineId(stateMachineId);
            nodeDraft = nodeDraftMapper.selectOne(nodeDraft);
            if (nodeDraft == null) {
                //获取状态机中positionY最大的节点
                StateMachineNodeDraftDTO maxNode = nodeDraftMapper.selectMaxPositionY(stateMachineId);
                //创建节点
                nodeDraft = new StateMachineNodeDraftDTO();
                nodeDraft.setStatusId(statusId);
                nodeDraft.setOrganizationId(organizationId);
                nodeDraft.setStateMachineId(stateMachineId);
                nodeDraft.setType(NodeType.CUSTOM);
                nodeDraft.setPositionX(maxNode.getPositionX());
                nodeDraft.setPositionY(maxNode.getPositionY() + 50);
                nodeDraft.setHeight(maxNode.getHeight());
                nodeDraft.setWidth(maxNode.getWidth());
                int isInsert = nodeDraftMapper.insert(nodeDraft);
                if (isInsert != 1) {
                    throw new CommonException("error.stateMachineNode.create");
                }
            }
            //判断当前节点是否已存在【全部】的转换id
            StateMachineTransformDraftDTO transformDraft = new StateMachineTransformDraftDTO();
            transformDraft.setStateMachineId(stateMachineId);
            transformDraft.setEndNodeId(nodeDraft.getId());
            transformDraft.setType(TransformType.ALL);
            transformDraft = transformDraftMapper.selectOne(transformDraft);
            if (transformDraft == null) {
                //创建全部转换
                transformDraft = new StateMachineTransformDraftDTO();
                transformDraft.setStateMachineId(stateMachineId);
                transformDraft.setName(statusVO.getName());
                transformDraft.setDescription("全部转换");
                transformDraft.setStartNodeId(0L);
                transformDraft.setEndNodeId(nodeDraft.getId());
                transformDraft.setOrganizationId(organizationId);
                transformDraft.setType(TransformType.ALL);
                transformDraft.setConditionStrategy(TransformConditionStrategy.ALL);
                if (transformDraftMapper.insert(transformDraft) != 1) {
                    throw new CommonException("error.stateMachineTransform.create");
                }
            }

            //更新node的【全部转换到当前】转换id
            int update = nodeDraftMapper.updateAllStatusTransformId(organizationId, nodeDraft.getId(), transformDraft.getId());
            if (update != 1) {
                throw new CommonException("error.createAllStatusTransform.updateAllStatusTransformId");
            }
            //将节点和转换添加到发布中
            insertNodeAndTransformForDeploy(nodeDraft.getId(), transformDraft.getId());
        }
    }

    private void insertNodeAndTransformForDeploy(Long nodeDraftId, Long transformDraftId) {
        StateMachineNodeDraftDTO nodeDraft = nodeDraftMapper.selectByPrimaryKey(nodeDraftId);
        StateMachineTransformDraftDTO transformDraft = transformDraftMapper.selectByPrimaryKey(transformDraftId);
        //插入节点
        StateMachineNodeDTO nodeDeploy = new StateMachineNodeDTO();
        BeanUtils.copyProperties(nodeDraft, nodeDeploy);
        nodeDeployMapper.insert(nodeDeploy);
        //插入转换
        StateMachineTransformDTO transformDeploy = new StateMachineTransformDTO();
        BeanUtils.copyProperties(transformDraft, transformDeploy);
        transformDeployMapper.insert(transformDeploy);
    }
}

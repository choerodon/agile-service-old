package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.infra.dataobject.BoardSprintAttrDO;
import io.choerodon.agile.infra.mapper.BoardSprintAttrMapper;
import io.choerodon.agile.infra.repository.BoardSprintAttrRepository;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author shinan.chen
 * @since 2019/5/14
 */
@Component
public class BoardSprintAttrRepositoryImpl implements BoardSprintAttrRepository {
    @Autowired
    private BoardSprintAttrMapper boardSprintAttrMapper;

    private static final String ERROR_SPRINTATTR_CREATE = "error.boardSprintAttr.create";
    private static final String ERROR_SPRINTATTR_UPDATE = "error.boardSprintAttr.update";
    private static final String ERROR_SPRINTATTR_DELETE = "error.boardSprintAttr.delete";

    @Override
    public BoardSprintAttrDO create(Long projectId, Long sprintId, int columnWidth) {
        BoardSprintAttrDO create = new BoardSprintAttrDO();
        create.setSprintId(sprintId);
        create.setColumnWidth(columnWidth);
        create.setProgramId(projectId);
        if (boardSprintAttrMapper.insert(create) != 1) {
            throw new CommonException(ERROR_SPRINTATTR_CREATE);
        }
        return boardSprintAttrMapper.selectByPrimaryKey(create.getId());
    }

    @Override
    public void delete(Long projectId, Long sprintId) {
        BoardSprintAttrDO delete = new BoardSprintAttrDO();
        delete.setSprintId(sprintId);
        delete.setProgramId(projectId);
        if (boardSprintAttrMapper.delete(delete) != 1) {
            throw new CommonException(ERROR_SPRINTATTR_DELETE);
        }
    }

    @Override
    public void update(BoardSprintAttrDO update) {
        if (boardSprintAttrMapper.updateByPrimaryKeySelective(update) != 1) {
            throw new CommonException(ERROR_SPRINTATTR_UPDATE);
        }
    }

    @Override
    public BoardSprintAttrDO queryBySprintId(Long projectId, Long sprintId) {
        BoardSprintAttrDO select = new BoardSprintAttrDO();
        select.setSprintId(sprintId);
        select.setProgramId(projectId);
        return boardSprintAttrMapper.selectOne(select);
    }
}

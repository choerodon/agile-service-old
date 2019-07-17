package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.BoardSprintAttrVO;
import io.choerodon.agile.app.service.BoardSprintAttrService;
import io.choerodon.agile.infra.dataobject.BoardSprintAttrDTO;
import io.choerodon.agile.infra.mapper.BoardSprintAttrMapper;
import io.choerodon.core.exception.CommonException;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author shinan.chen
 * @since 2019/5/14
 */
@Service
public class BoardSprintAttrServiceImpl implements BoardSprintAttrService {

    @Autowired
    private BoardSprintAttrMapper boardSprintAttrMapper;

    public static final String UPDATE_ERROR = "error.sprintAttr.update";
    public static final String DELETE_ERROR = "error.sprintAttr.deleteById";
    public static final String INSERT_ERROR = "error.sprintAttr.create";
    public static final String ILLEGAL_ERROR = "error.sprintAttr.illegal";
    private static final String ERROR_SPRINTATTR_CREATE = "error.boardSprintAttr.create";
    private static final String ERROR_SPRINTATTR_UPDATE = "error.boardSprintAttr.update";
    public static final int MAX_COLUMN_WIDTH = 10;
    public static final int MIN_COLUMN_WIDTH = 1;
    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public BoardSprintAttrVO updateColumnWidth(Long projectId, Long sprintId, Integer columnWidth) {
        BoardSprintAttrDTO origin = queryBySprintId(projectId, sprintId);
        if (origin == null) {
            create(projectId, sprintId, columnWidth);
        } else {
            if (columnWidth > MAX_COLUMN_WIDTH || columnWidth < MIN_COLUMN_WIDTH) {
                throw new CommonException(ILLEGAL_ERROR);
            }
            origin.setColumnWidth(columnWidth);
            update(origin);
        }
        return modelMapper.map(queryBySprintId(projectId, sprintId), BoardSprintAttrVO.class);
    }

    @Override
    public BoardSprintAttrDTO queryBySprintId(Long projectId, Long sprintId) {
        BoardSprintAttrDTO select = new BoardSprintAttrDTO();
        select.setSprintId(sprintId);
        select.setProgramId(projectId);
        return boardSprintAttrMapper.selectOne(select);
    }

    @Override
    public BoardSprintAttrDTO create(Long projectId, Long sprintId, int columnWidth) {
        BoardSprintAttrDTO create = new BoardSprintAttrDTO();
        create.setSprintId(sprintId);
        create.setColumnWidth(columnWidth);
        create.setProgramId(projectId);
        if (boardSprintAttrMapper.insert(create) != 1) {
            throw new CommonException(ERROR_SPRINTATTR_CREATE);
        }
        return boardSprintAttrMapper.selectByPrimaryKey(create.getId());
    }

    @Override
    public void update(BoardSprintAttrDTO update) {
        if (boardSprintAttrMapper.updateByPrimaryKeySelective(update) != 1) {
            throw new CommonException(ERROR_SPRINTATTR_UPDATE);
        }
    }
}

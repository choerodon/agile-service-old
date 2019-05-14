package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.BoardSprintAttrDTO;
import io.choerodon.agile.app.service.BoardSprintAttrService;
import io.choerodon.agile.infra.dataobject.BoardSprintAttrDO;
import io.choerodon.agile.infra.mapper.BoardSprintAttrMapper;
import io.choerodon.agile.infra.repository.BoardSprintAttrRepository;
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
    @Autowired
    private BoardSprintAttrRepository boardSprintAttrRepository;

    public static final String UPDATE_ERROR = "error.sprintAttr.update";
    public static final String DELETE_ERROR = "error.sprintAttr.deleteById";
    public static final String INSERT_ERROR = "error.sprintAttr.create";
    public static final String ILLEGAL_ERROR = "error.sprintAttr.illegal";
    public static final int MAX_COLUMN_WIDTH = 10;
    public static final int MIN_COLUMN_WIDTH = 1;
    private ModelMapper modelMapper = new ModelMapper();
    @PostConstruct
    public void init() {
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }
    
    @Override
    public BoardSprintAttrDTO addColumnWidth(Long projectId, Long sprintId) {
        BoardSprintAttrDO origin = boardSprintAttrRepository.queryBySprintId(projectId, sprintId);
        if (origin == null) {
            boardSprintAttrRepository.create(projectId, sprintId, 2);
        } else {
            int columnWidth = origin.getColumnWidth() + 1;
            if (columnWidth >= MAX_COLUMN_WIDTH) {
                throw new CommonException(ILLEGAL_ERROR);
            }
            origin.setColumnWidth(columnWidth);
            boardSprintAttrRepository.update(origin);
        }
        return modelMapper.map(boardSprintAttrRepository.queryBySprintId(projectId, sprintId), BoardSprintAttrDTO.class);
    }

    @Override
    public BoardSprintAttrDTO reduceColumnWidth(Long projectId, Long sprintId) {
        BoardSprintAttrDO origin = boardSprintAttrRepository.queryBySprintId(projectId, sprintId);
        if (origin == null) {
            throw new CommonException(ILLEGAL_ERROR);
        } else {
            int columnWidth = origin.getColumnWidth() - 1;
            if (columnWidth < MIN_COLUMN_WIDTH) {
                throw new CommonException(ILLEGAL_ERROR);
            }
            origin.setColumnWidth(columnWidth);
            boardSprintAttrRepository.update(origin);
        }
        return modelMapper.map(boardSprintAttrRepository.queryBySprintId(projectId, sprintId), BoardSprintAttrDTO.class);
    }
}

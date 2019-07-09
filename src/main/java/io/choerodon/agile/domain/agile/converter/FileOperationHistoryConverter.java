package io.choerodon.agile.domain.agile.converter;


import io.choerodon.agile.api.vo.FileOperationHistoryVO;
import io.choerodon.agile.domain.agile.entity.FileOperationHistoryE;
import io.choerodon.agile.infra.dataobject.FileOperationHistoryDTO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/2/25.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class FileOperationHistoryConverter implements ConvertorI<FileOperationHistoryE, FileOperationHistoryDTO, FileOperationHistoryVO> {

    @Override
    public FileOperationHistoryE doToEntity(FileOperationHistoryDTO fileOperationHistoryDTO) {
        FileOperationHistoryE fileOperationHistoryE = new FileOperationHistoryE();
        BeanUtils.copyProperties(fileOperationHistoryDTO, fileOperationHistoryE);
        return fileOperationHistoryE;
    }

    @Override
    public FileOperationHistoryDTO entityToDo(FileOperationHistoryE fileOperationHistoryE) {
        FileOperationHistoryDTO fileOperationHistoryDTO = new FileOperationHistoryDTO();
        BeanUtils.copyProperties(fileOperationHistoryE, fileOperationHistoryDTO);
        return fileOperationHistoryDTO;
    }

    @Override
    public FileOperationHistoryE dtoToEntity(FileOperationHistoryVO fileOperationHistoryVO) {
        FileOperationHistoryE fileOperationHistoryE = new FileOperationHistoryE();
        BeanUtils.copyProperties(fileOperationHistoryVO, fileOperationHistoryE);
        return fileOperationHistoryE;
    }

    @Override
    public FileOperationHistoryVO entityToDto(FileOperationHistoryE fileOperationHistoryE) {
        FileOperationHistoryVO fileOperationHistoryVO = new FileOperationHistoryVO();
        BeanUtils.copyProperties(fileOperationHistoryE, fileOperationHistoryVO);
        return fileOperationHistoryVO;
    }

    @Override
    public FileOperationHistoryVO doToDto(FileOperationHistoryDTO fileOperationHistoryDTO) {
        FileOperationHistoryVO fileOperationHistoryVO = new FileOperationHistoryVO();
        BeanUtils.copyProperties(fileOperationHistoryDTO, fileOperationHistoryVO);
        return fileOperationHistoryVO;
    }

    @Override
    public FileOperationHistoryDTO dtoToDo(FileOperationHistoryVO fileOperationHistoryVO) {
        FileOperationHistoryDTO fileOperationHistoryDTO = new FileOperationHistoryDTO();
        BeanUtils.copyProperties(fileOperationHistoryVO, fileOperationHistoryDTO);
        return fileOperationHistoryDTO;
    }
}

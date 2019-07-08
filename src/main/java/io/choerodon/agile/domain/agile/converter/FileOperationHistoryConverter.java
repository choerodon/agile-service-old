package io.choerodon.agile.domain.agile.converter;


import io.choerodon.agile.api.vo.FileOperationHistoryDTO;
import io.choerodon.agile.domain.agile.entity.FileOperationHistoryE;
import io.choerodon.agile.infra.dataobject.FileOperationHistoryDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/2/25.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class FileOperationHistoryConverter implements ConvertorI<FileOperationHistoryE, FileOperationHistoryDO, FileOperationHistoryDTO> {

    @Override
    public FileOperationHistoryE doToEntity(FileOperationHistoryDO fileOperationHistoryDO) {
        FileOperationHistoryE fileOperationHistoryE = new FileOperationHistoryE();
        BeanUtils.copyProperties(fileOperationHistoryDO, fileOperationHistoryE);
        return fileOperationHistoryE;
    }

    @Override
    public FileOperationHistoryDO entityToDo(FileOperationHistoryE fileOperationHistoryE) {
        FileOperationHistoryDO fileOperationHistoryDO = new FileOperationHistoryDO();
        BeanUtils.copyProperties(fileOperationHistoryE, fileOperationHistoryDO);
        return fileOperationHistoryDO;
    }

    @Override
    public FileOperationHistoryE dtoToEntity(FileOperationHistoryDTO fileOperationHistoryDTO) {
        FileOperationHistoryE fileOperationHistoryE = new FileOperationHistoryE();
        BeanUtils.copyProperties(fileOperationHistoryDTO, fileOperationHistoryE);
        return fileOperationHistoryE;
    }

    @Override
    public FileOperationHistoryDTO entityToDto(FileOperationHistoryE fileOperationHistoryE) {
        FileOperationHistoryDTO fileOperationHistoryDTO = new FileOperationHistoryDTO();
        BeanUtils.copyProperties(fileOperationHistoryE, fileOperationHistoryDTO);
        return fileOperationHistoryDTO;
    }

    @Override
    public FileOperationHistoryDTO doToDto(FileOperationHistoryDO fileOperationHistoryDO) {
        FileOperationHistoryDTO fileOperationHistoryDTO = new FileOperationHistoryDTO();
        BeanUtils.copyProperties(fileOperationHistoryDO, fileOperationHistoryDTO);
        return fileOperationHistoryDTO;
    }

    @Override
    public FileOperationHistoryDO dtoToDo(FileOperationHistoryDTO fileOperationHistoryDTO) {
        FileOperationHistoryDO fileOperationHistoryDO = new FileOperationHistoryDO();
        BeanUtils.copyProperties(fileOperationHistoryDTO, fileOperationHistoryDO);
        return fileOperationHistoryDO;
    }
}

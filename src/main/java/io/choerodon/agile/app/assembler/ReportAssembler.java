package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.BurnDownChangeDTO;
import io.choerodon.agile.api.dto.CoordinateDTO;
import io.choerodon.agile.domain.agile.entity.BurnDownChangeE;
import io.choerodon.agile.domain.agile.entity.CoordinateE;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/19
 */
@Component
public class ReportAssembler {

    public CoordinateDTO coordinateEToDto(CoordinateE coordinateE) {
        CoordinateDTO coordinateDTO = new CoordinateDTO();
        BeanUtils.copyProperties(coordinateE, coordinateDTO);
        return coordinateDTO;
    }

    public BurnDownChangeDTO burndownChangeEToDto(BurnDownChangeE burnDownChangeE) {
        BurnDownChangeDTO burnDownChangeDTO = new BurnDownChangeDTO();
        BeanUtils.copyProperties(burnDownChangeE, burnDownChangeDTO);
        return burnDownChangeDTO;
    }
}

package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.ReportIssueDTO;
import io.choerodon.agile.domain.agile.entity.ReportIssueE;
import io.choerodon.agile.infra.dataobject.ReportIssueDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/20
 */
@Component
public class ReportIssueConverter implements ConvertorI<ReportIssueE, ReportIssueDO, ReportIssueDTO> {

    @Override
    public ReportIssueE dtoToEntity(ReportIssueDTO reportIssueDTO) {
        ReportIssueE reportIssueE = new ReportIssueE();
        BeanUtils.copyProperties(reportIssueDTO, reportIssueE);
        return reportIssueE;
    }

    @Override
    public ReportIssueE doToEntity(ReportIssueDO reportIssueDO) {
        ReportIssueE reportIssueE = new ReportIssueE();
        BeanUtils.copyProperties(reportIssueDO, reportIssueE);
        return reportIssueE;
    }

    @Override
    public ReportIssueDTO entityToDto(ReportIssueE reportIssueE) {
        ReportIssueDTO reportIssueDTO = new ReportIssueDTO();
        BeanUtils.copyProperties(reportIssueE, reportIssueDTO);
        return reportIssueDTO;
    }

    @Override
    public ReportIssueDO entityToDo(ReportIssueE reportIssueE) {
        ReportIssueDO reportIssueDO = new ReportIssueDO();
        BeanUtils.copyProperties(reportIssueE, reportIssueDO);
        return reportIssueDO;
    }

    @Override
    public ReportIssueDTO doToDto(ReportIssueDO reportIssueDO) {
        ReportIssueDTO reportIssueDTO = new ReportIssueDTO();
        BeanUtils.copyProperties(reportIssueDO, reportIssueDTO);
        return reportIssueDTO;
    }

    @Override
    public ReportIssueDO dtoToDo(ReportIssueDTO reportIssueDTO) {
        ReportIssueDO reportIssueDO = new ReportIssueDO();
        BeanUtils.copyProperties(reportIssueDTO, reportIssueDO);
        return reportIssueDO;
    }
}
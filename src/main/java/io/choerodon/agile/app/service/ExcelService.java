package io.choerodon.agile.app.service;

import io.choerodon.agile.api.dto.FileOperationHistoryDTO;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/2/25.
 * Email: fuqianghuang01@gmail.com
 */
public interface ExcelService {

    void download(Long projectId, HttpServletRequest request, HttpServletResponse response);

    void batchImport(Long projectId, Long organizationId, Workbook workbook);

    void cancelImport(Long projectId, Long id);

    FileOperationHistoryDTO queryLatestRecode(Long projectId);
}

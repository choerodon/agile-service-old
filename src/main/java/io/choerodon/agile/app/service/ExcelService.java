package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.FileOperationHistoryDTO;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/2/25.
 * Email: fuqianghuang01@gmail.com
 */
public interface ExcelService {

    void download(Long projectId, Long organizationId, HttpServletRequest request, HttpServletResponse response);

    void batchImport(Long projectId, Long organizationId, Long userId, Workbook workbook);

    void cancelImport(Long projectId, Long id, Long objectVersionNumber);

    FileOperationHistoryDTO queryLatestRecode(Long projectId);
}

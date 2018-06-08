package io.choerodon.agile.infra.feign.fallback;

import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.infra.feign.FileFeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by HuangFuqiang on 2018/1/15.
 */
@Component
public class FileFeignClientFallback implements FileFeignClient {

    @Override
    public ResponseEntity<String> uploadFile(String bucketName, String fileName, MultipartFile multipartFile) {
        throw new CommonException("error.file.upload");
    }

    @Override
    public ResponseEntity deleteFile(String bucketName, String url) {
        throw new CommonException("error.file.delete");
    }
}

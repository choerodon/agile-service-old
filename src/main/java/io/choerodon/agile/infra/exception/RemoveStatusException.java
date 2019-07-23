package io.choerodon.agile.infra.exception;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/11/26.
 * Email: fuqianghuang01@gmail.com
 */
public class RemoveStatusException extends RuntimeException {

    public RemoveStatusException(String message) {
        super(message);
    }

    public RemoveStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}

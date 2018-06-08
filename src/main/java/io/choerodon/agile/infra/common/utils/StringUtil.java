package io.choerodon.agile.infra.common.utils;

import io.choerodon.core.exception.CommonException;

import java.lang.reflect.Field;

/**
 * String工具类
 *
 * @author inghuang123@gmail.com
 */
public class StringUtil {

    private StringUtil() {

    }

    private static final String ERROR_GET_TO_STRING = "error.stringUtil.getToString";

    /**
     * 根据对象获得对象toString重写后的字符串
     *
     * @param object 对象
     * @return toString
     */
    public static String getToString(Object object) {
        StringBuilder sb = new StringBuilder("{");
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                sb.append(field.getName()).append("=").append(field.get(object)).append(",");
            } catch (Exception e) {
                throw new CommonException(ERROR_GET_TO_STRING);
            }
        }
        if (sb.length() > 1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * 对象转换警告捕获
     *
     * @param obj obj
     * @param <T> T
     * @return T
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        if (obj == null) {
            return null;
        } else {
            return (T) obj;
        }
    }

}

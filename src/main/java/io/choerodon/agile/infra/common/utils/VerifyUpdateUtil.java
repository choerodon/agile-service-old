package io.choerodon.agile.infra.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.infra.common.annotation.Update;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Component;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author dinghuang123@gmail.com
 */
@Component
public class VerifyUpdateUtil {

    /**
     * 根据前端数据进行部分更新
     *
     * @param updateMap    updateMap
     * @param objectUpdate objectUpdate
     * @return String
     */
    public List<String> verifyUpdateData(JSONObject updateMap, Object objectUpdate) {
        List<String> fieldList = new ArrayList<>();
        Class objectClass = objectUpdate.getClass();
        updateMap.forEach((String k, Object v) -> {
            try {
                Field field = objectClass.getDeclaredField(k);
                field.setAccessible(true);
                Boolean flag = true;
                Update update = field.getAnnotation(Update.class);
                if (update != null && update.temp()) {
                    flag = false;
                }
                if (field.getType() == String.class) {
                    field.set(objectUpdate, v);
                } else if (field.getType() == Long.class) {
                    field.set(objectUpdate, Long.valueOf(v.toString()));
                } else if (field.getType() == Date.class) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    field.set(objectUpdate, v != null ? sdf.parse(v.toString()) : null);
                } else if (field.getType() == Integer.class) {
                    field.set(objectUpdate, Integer.valueOf(v.toString()));
                } else if (field.getType() == BigDecimal.class) {
                    field.set(objectUpdate, new BigDecimal(v.toString()));
                } else if (field.getType() == List.class) {
                    //对象包含子对象是list的值设置
                    String className = field.getGenericType().getTypeName().substring(15, field.getGenericType().getTypeName().length() - 1);
                    Class<?> forName = Class.forName(className);
                    String json = JSON.toJSONString(v);
                    field.set(objectUpdate, JSON.parseArray(json, forName));
                    flag = false;
                }
                if (flag && update == null) {
                    fieldList.add(k);
                }
                if (update != null && !Objects.equals(update.name(), "")) {
                    fieldList.add(update.name());
                }
            } catch (Exception e) {
                throw new CommonException("error.verifyUpdateData.noField");
            }
        });
        return fieldList;
    }
}

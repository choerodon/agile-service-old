package io.choerodon.agile.infra.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        updateMap.forEach((k, v) -> {
            try {
                Field field = objectClass.getDeclaredField(k);
                field.setAccessible(true);
                Boolean flag = false;
                if (field.getType() == String.class) {
                    if(!k.equals("versionType")){
                        flag = true;
                    }
                    field.set(objectUpdate, v);
                } else if (field.getType() == Long.class) {
                    field.set(objectUpdate, Long.valueOf(v.toString()));
                    flag = true;
                } else if (field.getType() == Date.class) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    field.set(objectUpdate, v != null ? sdf.parse(v.toString()) : null);
                    flag = true;
                } else if (field.getType() == Integer.class) {
                    field.set(objectUpdate, Integer.valueOf(v.toString()));
                    flag = true;
                } else if (field.getType() == BigDecimal.class) {
                    field.set(objectUpdate, new BigDecimal(v.toString()));
                    flag = true;
                } else if (field.getType() == List.class) {
                    //对象包含子对象是list的值设置
                    String className = field.getGenericType().getTypeName().substring(15, field.getGenericType().getTypeName().length() - 1);
                    Class<?> forName = Class.forName(className);
                    String json = JSON.toJSONString(v);
                    field.set(objectUpdate, JSON.parseArray(json, forName));
                }
                if (flag) {
                    fieldList.add(k);
                }
            } catch (Exception e) {
                throw new CommonException("error.verifyUpdateData.noField");
            }
        });
        return fieldList;
    }
}

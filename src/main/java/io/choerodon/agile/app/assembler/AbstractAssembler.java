package io.choerodon.agile.app.assembler;

import io.choerodon.core.exception.CommonException;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽象Assembler转换类,如果需要简单的转换，继承此类即可，要实现自己的，则重写方法
 *
 * @author dinghuang123@gmail.com
 * * @since 2018/9/7
 */
abstract class AbstractAssembler {

    private static final String ERROR_CONVERT = "error.abstractAssembler.convert";

    /**
     * 转换到目标类
     *
     * @param source source
     * @return target
     */
    @SuppressWarnings("unchecked")
    public <T, V> V toTarget(T source, Class<V> tClass) {
        if (source != null) {
            try {
                V target = tClass.newInstance();
                if (target != null) {
                    BeanUtils.copyProperties(source, target);
                }
                return target;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new CommonException(ERROR_CONVERT, e);
            }
        }
        return null;
    }

    /**
     * List转换到目标类
     *
     * @param source source
     * @return target
     */
    @SuppressWarnings("unchecked")
    public <T extends List, V> List<V> toTargetList(T source, Class<V> tClass) {
        List<V> targetList = new ArrayList<>(source.size());
        if (!source.isEmpty()) {
            source.forEach(s -> {
                V target = toTarget(s, tClass);
                targetList.add(target);
            });
        }
        return targetList;
    }


}

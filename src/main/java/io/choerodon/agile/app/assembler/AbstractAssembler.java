package io.choerodon.agile.app.assembler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAssembler.class);

    /**
     * 转换到目标类
     *
     * @param source source
     * @return target
     */
    @SuppressWarnings("unchecked")
    public <T, V> V toTarget(T source, Class<V> tClass) {
        V target = null;
        try {
            target = tClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.info("Exception", e);
        }
        if (target != null && source != null) {
            BeanUtils.copyProperties(source, target);
        }
        return target;
    }

    /**
     * List转换到目标类
     *
     * @param source source
     * @return target
     */
    @SuppressWarnings("unchecked")
    public <T extends List, V> List<V> toTargetList(T source, Class<V> tClass) {
        List<V> targetList = new ArrayList<>();
        if (source != null && !source.isEmpty()) {
            source.forEach(s -> {
                V target = toTarget(s, tClass);
                targetList.add(target);
            });
        }
        return targetList;
    }


}

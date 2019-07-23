package io.choerodon.agile.infra.aspect;

import io.choerodon.agile.infra.mapper.PageFieldMapper;
import io.choerodon.agile.infra.mapper.ProjectPageFieldMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.app.service.PageFieldService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author shinan.chen
 * @since 2019/4/3
 */
@Aspect
@Component
@Transactional(rollbackFor = Exception.class)
public class CopyPageFieldAspect {
    private static final Logger logger = LoggerFactory.getLogger(CopyPageFieldAspect.class);
    @Autowired
    private ProjectPageFieldMapper projectPageFieldMapper;
    @Autowired
    private PageFieldMapper pageFieldMapper;
    @Autowired
    private PageFieldService pageFieldService;

    @Pointcut("@annotation(io.choerodon.agile.infra.annotation.CopyPageField)")
    public void copyPageFieldPointcut() {
        throw new UnsupportedOperationException();
    }

    @Around("copyPageFieldPointcut()")
    public Object interceptor(ProceedingJoinPoint pjp) {
        // 下面两个数组中，参数值和参数名的个数和位置是一一对应的。
        Object[] args = pjp.getArgs();
        String[] argNames = ((MethodSignature) pjp.getSignature()).getParameterNames();
        Long organizationId = null;
        Long projectId = null;
        for (int i = 0; i < argNames.length; i++) {
            if (args[i] != null) {
                if (argNames[i].equals("organizationId")) {
                    organizationId = Long.valueOf(args[i] + "");
                }
                if (argNames[i].equals("projectId")) {
                    projectId = Long.valueOf(args[i] + "");
                }
            }
        }
        logger.info("organizationId:{},projectId:{}", organizationId, projectId);
        //若项目层没有自定义，则复制组织层字段到项目层
        if (projectId != null && projectPageFieldMapper.queryOne(organizationId, projectId) == null) {
            copyOrgPageFieldToPro(organizationId, projectId);
        }
        try {
            return pjp.proceed();
        } catch (Throwable e) {
            throw new CommonException("error.copyPageFieldAspect.proceed", e);
        }
    }

    /**
     * 复制组织层（页面字段）到项目层
     *
     * @param organizationId
     * @param projectId
     */
    private synchronized void copyOrgPageFieldToPro(Long organizationId, Long projectId) {
        //初始化数据
        pageFieldService.initPageFieldByOrg(organizationId);
        //复制页面字段
        pageFieldMapper.copyOrgPageFieldToPro(organizationId, projectId);
        //创建自定义记录
        projectPageFieldMapper.createOne(organizationId, projectId);
    }
}

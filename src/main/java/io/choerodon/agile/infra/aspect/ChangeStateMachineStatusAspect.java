package io.choerodon.agile.infra.aspect;

import io.choerodon.agile.infra.enums.StateMachineStatus;
import io.choerodon.agile.infra.dataobject.StateMachineDTO;
import io.choerodon.agile.infra.mapper.StateMachineMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.app.service.StateMachineClientService;
import io.choerodon.mybatis.entity.Criteria;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author shinan.chen
 * @since 2018/11/23
 */
@Aspect
@Component
@Transactional(rollbackFor = Exception.class)
public class ChangeStateMachineStatusAspect {
    @Autowired
    private StateMachineMapper stateMachineMapper;
    @Autowired
    private StateMachineClientService stateMachineClientService;

    @Pointcut("@annotation(io.choerodon.agile.infra.annotation.ChangeStateMachineStatus)")
    public void updateStatusPointcut() {
        throw new UnsupportedOperationException();
    }

    @Around("updateStatusPointcut()")
    public Object interceptor(ProceedingJoinPoint pjp) {
        // 下面两个数组中，参数值和参数名的个数和位置是一一对应的。
        Object[] args = pjp.getArgs();
        String[] argNames = ((MethodSignature) pjp.getSignature()).getParameterNames();
        Long stateMachineId = null;
        for (int i = 0; i < argNames.length; i++) {
            if (argNames[i].equals("stateMachineId")) {
                stateMachineId = Long.valueOf(args[i] + "");
            }
        }
        StateMachineDTO stateMachine = stateMachineMapper.selectByPrimaryKey(stateMachineId);
        if (stateMachine == null) {
            throw new CommonException("error.stateMachine.notFound");
        }
        if (stateMachine.getStatus().equals(StateMachineStatus.ACTIVE)) {
            stateMachine.setStatus(StateMachineStatus.DRAFT);
            Criteria criteria = new Criteria();
            criteria.update("status");
            stateMachineMapper.updateByPrimaryKeyOptions(stateMachine, criteria);
        }

        try {
            return pjp.proceed();
        } catch (Throwable e) {
            throw new CommonException("error.changeStateMachineStatusAspect.proceed", e);
        }
    }
}

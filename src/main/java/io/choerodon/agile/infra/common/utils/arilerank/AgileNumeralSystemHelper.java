package io.choerodon.agile.infra.common.utils.arilerank;

import com.google.common.collect.ImmutableList;
import io.choerodon.core.exception.CommonException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jian_zhang02@163.com on 2018/5/28.
 */
public class AgileNumeralSystemHelper {
    private static final List<AgileInteger> BASE_36_DIVIDERS;
    private static final String NUMERAL_SYSTEM_ERROR = "error.rank.unsupportedNumeralSystem";

    private AgileNumeralSystemHelper() {
    }

    public static List<AgileDecimal> getBaseDivisors(AgileNumeralSystem lexoNumeralSystem, int fractionMagnitude) {
        int base = lexoNumeralSystem.getBase();
        if (base == AgileNumeralSystem.BASE_36.getBase()) {
            return fractionMagnitude < 0 ? BASE_36_DIVIDERS.stream().map(lexoInteger ->
                    AgileDecimal.make(lexoInteger.shiftLeft(fractionMagnitude * -1), 0)
            ).collect(Collectors.toList()) : BASE_36_DIVIDERS.stream().map(lexoInteger ->
                    AgileDecimal.make(lexoInteger, fractionMagnitude)
            ).collect(Collectors.toList());
        } else {
            throw new CommonException(NUMERAL_SYSTEM_ERROR);
        }
    }

    static {
        List<Object> list = ImmutableList.builder().add(AgileInteger.make(AgileNumeralSystem.BASE_36, 1, new int[]{18})).add(AgileInteger.make(AgileNumeralSystem.BASE_36, 1, new int[]{12})).add(AgileInteger.make(AgileNumeralSystem.BASE_36, 1, new int[]{9})).add(AgileInteger.make(AgileNumeralSystem.BASE_36, 1, new int[]{6})).add(AgileInteger.make(AgileNumeralSystem.BASE_36, 1, new int[]{4})).add(AgileInteger.make(AgileNumeralSystem.BASE_36, 1, new int[]{3})).add(AgileInteger.make(AgileNumeralSystem.BASE_36, 1, new int[]{2})).add(AgileInteger.make(AgileNumeralSystem.BASE_36, 1, new int[]{1})).build();
        List<AgileInteger> lexoIntegers = new ArrayList<>();
        list.forEach(object -> lexoIntegers.add((AgileInteger) (object)));
        BASE_36_DIVIDERS = lexoIntegers;
    }
}

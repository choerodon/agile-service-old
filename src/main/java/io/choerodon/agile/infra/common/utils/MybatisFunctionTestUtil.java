package io.choerodon.agile.infra.common.utils;

/**
 * spock 代替 mysql 的函数定义类
 *
 * @author dinghuang123@gmail.com
 * @since 2018/8/2
 */
public class MybatisFunctionTestUtil {

    public static String ifFunction(Boolean result, String param, String paramTwo) {
        return result ? param : paramTwo;
    }

}

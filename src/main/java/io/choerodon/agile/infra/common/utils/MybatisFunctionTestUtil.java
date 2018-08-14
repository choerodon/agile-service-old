package io.choerodon.agile.infra.common.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * spock 代替 mysql 的函数定义类
 *
 * @author dinghuang123@gmail.com
 * @since 2018/8/2
 */
public class MybatisFunctionTestUtil {

    private MybatisFunctionTestUtil() {
        new MybatisFunctionTestUtil();
    }

    public static String ifFunction(Boolean result, String param, String paramTwo) {
        return result ? param : paramTwo;
    }

    public static String dataFormatFunction(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static Date dataSubFunction(Date date, Integer day) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return now.getTime();
    }

}

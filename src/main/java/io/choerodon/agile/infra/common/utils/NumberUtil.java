package io.choerodon.agile.infra.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberUtil {

    /**
     * 判断字符串是否为数字
     *
     * @param str
     * @return
     */
    public static Boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }


    public static Boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static Boolean canParseInteger(String str) {
        Boolean flag = false;// 没碰到小数点时候标记是false
        int n = 0;// 计数器
        char[] charArray = str.toCharArray();
        for (char c : charArray) {
            if (c == '.') {
                flag = true;
                continue;
            }
            if (flag && Integer.valueOf(String.valueOf(c)) > 0) {
                n++;
            }
        }
        if (n > 0) {
            return false;
        }
        return true;
    }
}

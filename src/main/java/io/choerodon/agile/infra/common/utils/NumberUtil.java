package io.choerodon.agile.infra.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/2/28.
 * Email: fuqianghuang01@gmail.com
 */
public class NumberUtil {

    /**
     * 判断字符串是否为数字
     *
     * @param str
     * @return
     */
    public static Boolean isNumeric(String str) {
        try {
            for (int i = 0;i < str.length(); i++) {
                if (!((str.charAt(i) >= '0' && str.charAt(i) <= '9') || str.charAt(i) == '.')) {
                    return false;
                }
            }
            Double.parseDouble(String.valueOf(str));
        } catch (Exception e) {
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
        Boolean flag = true;// 没碰到小数点时候标记是false
        int n = 0;// 计数器
        char[] charArray = str.toCharArray();
        for (char c : charArray) {
            if (c == '.') {
                flag = false;
                continue;
            }
            if (!flag && Integer.valueOf(String.valueOf(c)) > 0) {
                n++;
            }
        }
        if (n > 0) {
            return false;
        }
        return true;
    }
}

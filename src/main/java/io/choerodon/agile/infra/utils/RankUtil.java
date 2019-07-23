package io.choerodon.agile.infra.utils;

import io.choerodon.agile.infra.utils.arilerank.AgileRank;

/**
 * Created by jian_zhang02@163.com on 2018/5/28.
 */
public class RankUtil {

    private RankUtil(){}

    public static String mid(){
        AgileRank minRank = AgileRank.min();
        AgileRank maxRank = AgileRank.max();
        return minRank.between(maxRank).format();
    }

    public static String genNext(String rank){
        return AgileRank.parse(rank).genNext().format();
    }

    public static String genPre(String minRank) {
        return AgileRank.parse(minRank).genPrev().format();
    }

    public static String between(String leftRank, String rightRank) {
        AgileRank left = AgileRank.parse(leftRank);
        AgileRank right = AgileRank.parse(rightRank);
        return left.between(right).format();
    }
}

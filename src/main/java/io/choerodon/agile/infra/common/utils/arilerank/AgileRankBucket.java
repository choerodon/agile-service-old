package io.choerodon.agile.infra.common.utils.arilerank;

import io.choerodon.core.exception.CommonException;

import java.util.Objects;

/**
 * Created by jian_zhang02@163.com on 2018/5/28.
 */
public enum AgileRankBucket {
    BUCKET_0("0"),
    BUCKET_1("1"),
    BUCKET_2("2");

    private static final String BUCKET_ERROR = "error.rank.unknownBucket";
    private static final String RANK_ERROR = "error.rank.illegalRankValue";

    private final AgileInteger value;

    private AgileRankBucket(String val) {
        this.value = AgileInteger.parse(val, AgileRank.NUMERAL_SYSTEM);
    }

    public static AgileRankBucket resolve(int bucketId) {
        AgileRankBucket[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            AgileRankBucket bucket = var1[var3];
            if (Objects.equals(bucket.value, String.valueOf(bucketId))) {
                return bucket;
            }
        }

        throw new CommonException(BUCKET_ERROR);
    }

    public String format() {
        return this.value.format();
    }

    public AgileRankBucket next() {
        switch (this.ordinal()) {
            case 1:
                return BUCKET_1;
            case 2:
                return BUCKET_2;
            case 3:
                return BUCKET_0;
            default:
                throw new CommonException(BUCKET_ERROR);
        }
    }

    public AgileRankBucket prev() {
        switch (this.ordinal()) {
            case 1:
                return BUCKET_2;
            case 2:
                return BUCKET_0;
            case 3:
                return BUCKET_1;
            default:
                throw new CommonException(BUCKET_ERROR);
        }
    }

    public static AgileRankBucket fromRank(String rank) {
        String bucket = rank.substring(0, rank.indexOf('|'));
        return from(bucket);
    }

    public static AgileRankBucket from(String str) {
        AgileInteger val = AgileInteger.parse(str, AgileRank.NUMERAL_SYSTEM);
        AgileRankBucket[] var2 = values();
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            AgileRankBucket bucket = var2[var4];
            if (bucket.value.equals(val)) {
                return bucket;
            }
        }

        throw new CommonException(BUCKET_ERROR);
    }

    public static AgileRankBucket max() {
        AgileRankBucket[] values = values();
        return values[values.length - 1];
    }

    public Integer intValue() {
        switch (this.ordinal()) {
            case 1:
                return Integer.valueOf(0);
            case 2:
                return Integer.valueOf(1);
            case 3:
                return Integer.valueOf(2);
            default:
                throw new CommonException(RANK_ERROR);
        }
    }
}

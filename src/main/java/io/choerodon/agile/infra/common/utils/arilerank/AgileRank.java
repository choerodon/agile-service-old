package io.choerodon.agile.infra.common.utils.arilerank;

import com.google.common.collect.ImmutableList;
import io.choerodon.core.exception.CommonException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/5/28.
 */
public class AgileRank implements Comparable<AgileRank> {
    public static final AgileNumeralSystem NUMERAL_SYSTEM;
    private static final AgileDecimal ZERO_DECIMAL;
    private static final AgileDecimal ONE_DECIMAL;
    private static final AgileDecimal EIGHT_DECIMAL;
    private static final AgileDecimal MIN_DECIMAL;
    private static final AgileDecimal MAX_DECIMAL;
    private static final AgileDecimal MID_DECIMAL;
    private static final AgileDecimal INITIAL_MIN_DECIMAL;
    private static final AgileDecimal INITIAL_MAX_DECIMAL;
    private final String value;
    private AgileRankBucket bucket;
    private AgileDecimal decimal;

    private static final String BUCKET_ERROR = "error.rank.bucketNotEqual";
    private static final String NUMERAL_SYSTEM_ERROR = "error.rank.numeralSysNotEqual";
    private static final String RANK_ERROR = "error.rank.notBetweenRank";
    private static final String DISTANCE_ERROR = "error.rank.notSuitableDistance";

    private AgileRank(String value) {
        this.value = value;
    }

    private AgileRank(AgileRankBucket bucket, AgileDecimal decimal) {
        this.value = bucket.format() + "|" + formatDecimal(decimal);
        this.bucket = bucket;
        this.decimal = decimal;
    }

    public static AgileRank min() {
        return from(AgileRankBucket.BUCKET_0, MIN_DECIMAL);
    }

    public static AgileRank max() {
        return max(AgileRankBucket.BUCKET_0);
    }

    public static AgileRank max(AgileRankBucket bucket) {
        return from(bucket, MAX_DECIMAL);
    }

    public static AgileRank initial(AgileRankBucket bucket) {
        return bucket == AgileRankBucket.BUCKET_0?from(bucket, INITIAL_MIN_DECIMAL):from(bucket, INITIAL_MAX_DECIMAL);
    }

    public AgileRank genPrev() {
        this.fillDecimal();
        if(this.isMax()) {
            return new AgileRank(this.bucket, INITIAL_MAX_DECIMAL);
        } else {
            AgileInteger floorInteger = this.decimal.floor();
            AgileDecimal floorDecimal = AgileDecimal.from(floorInteger);
            AgileDecimal nextDecimal = floorDecimal.subtract(EIGHT_DECIMAL);
            if(nextDecimal.compareTo(MIN_DECIMAL) <= 0) {
                nextDecimal = between(MIN_DECIMAL, this.decimal);
            }

            return new AgileRank(this.bucket, nextDecimal);
        }
    }

    public AgileRank genNext() {
        this.fillDecimal();
        if(this.isMin()) {
            return new AgileRank(this.bucket, INITIAL_MIN_DECIMAL);
        } else {
            AgileInteger ceilInteger = this.decimal.ceil();
            AgileDecimal ceilDecimal = AgileDecimal.from(ceilInteger);
            AgileDecimal nextDecimal = ceilDecimal.add(EIGHT_DECIMAL);
            if(nextDecimal.compareTo(MAX_DECIMAL) >= 0) {
                nextDecimal = between(this.decimal, MAX_DECIMAL);
            }

            return new AgileRank(this.bucket, nextDecimal);
        }
    }

    public AgileRank between(AgileRank other) {
        return this.between(other, 0);
    }

    public AgileRank between(AgileRank other, int capacity) {
        this.fillDecimal();
        other.fillDecimal();
        if(!this.bucket.equals(other.bucket)) {
            throw new CommonException(BUCKET_ERROR);
        } else {
            int cmp = this.decimal.compareTo(other.decimal);
            if(cmp > 0) {
                return new AgileRank(this.bucket, between(other.decimal, this.decimal, capacity));
            } else if(cmp == 0) {
                throw new CommonException(RANK_ERROR);
            } else {
                return new AgileRank(this.bucket, between(this.decimal, other.decimal, capacity));
            }
        }
    }

    public static AgileDecimal between(AgileDecimal oLeft, AgileDecimal oRight) {
        return between(oLeft, oRight, 0);
    }

    public static AgileDecimal between(AgileDecimal left, AgileDecimal right, int spaceToRemain) {
        AgileNumeralSystem system = left.getSystem();
        if(system != right.getSystem()) {
            throw new CommonException(NUMERAL_SYSTEM_ERROR);
        } else {
            //将right与left相减
            AgileDecimal space = right.subtract(left);
            int capacity = spaceToRemain + 2;
            AgileDecimal spacing = findSpacing(space, capacity);
            AgileDecimal floor = floorToSpacingDivisor(left, spacing);
            return roundToSpacing(left, floor, spacing);
        }
    }

    private static AgileDecimal findSpacing(AgileDecimal space, int capacity) {
        //capacity自然对数除space.getSystem().getBase()计算容量
        int capacityMagnitude = (int)Math.floor(Math.log((double)capacity) / Math.log((double)space.getSystem().getBase()));
        //int数组长度-”:“后字符长度 -1 - capacityMagnitude
        int spacingMagnitude = space.getOrderOfMagnitude() - capacityMagnitude;
        //
        AgileDecimal lexoCapacity = AgileDecimal.fromInt(capacity, space.getSystem());
        Iterator var5 = getSystemBaseDivisors(space.getSystem(), spacingMagnitude).iterator();

        AgileDecimal spacingCandidate;
        do {
            if(!var5.hasNext()) {
                throw new CommonException(DISTANCE_ERROR);
            }

            spacingCandidate = (AgileDecimal)var5.next();
        } while(space.compareTo(spacingCandidate.multiply(lexoCapacity)) < 0);

        return spacingCandidate;
    }

    private static List<AgileDecimal> getSystemBaseDivisors(AgileNumeralSystem lexoNumeralSystem, int magnitude) {
        int fractionMagnitude = magnitude * -1;
        int adjacentFractionMagnitude = fractionMagnitude + 1;
        List<Object> list = ImmutableList.builder().addAll(AgileNumeralSystemHelper.getBaseDivisors(lexoNumeralSystem, fractionMagnitude)).addAll(AgileNumeralSystemHelper.getBaseDivisors(lexoNumeralSystem, adjacentFractionMagnitude)).build();
        List<AgileDecimal> lexoDecimals = new ArrayList<>();
        list.forEach(object ->lexoDecimals.add((AgileDecimal)(object)));
        return lexoDecimals;
    }

    private static AgileDecimal floorToSpacingDivisor(AgileDecimal number, AgileDecimal spacing) {
        AgileDecimal zero = AgileDecimal.from(AgileInteger.zero(number.getSystem()));
        if(zero.equals(number)) {
            return spacing;
        } else {
            AgileInteger spacingsMag = spacing.getMag();
            int scaleDifference = number.getScale() + spacing.getOrderOfMagnitude();
            int spacingsMostSignificantDigit = spacingsMag.getMagSize() - 1;

            AgileInteger floor;
            for(floor = number.getMag().shiftRight(scaleDifference).add(AgileInteger.one(number.getSystem())); floor.getMag(0) % spacingsMag.getMag(spacingsMostSignificantDigit) != 0;) {
                floor = floor.add(AgileInteger.one(number.getSystem()));
            }
            return number.getScale() - scaleDifference > 0? AgileDecimal.make(floor, number.getScale() - scaleDifference): AgileDecimal.make(floor.shiftLeft(scaleDifference), number.getScale());
        }
    }

    private static AgileDecimal roundToSpacing(AgileDecimal number, AgileDecimal floor, AgileDecimal spacing) {
        AgileDecimal halfSpacing = spacing.multiply(AgileDecimal.half(spacing.getSystem()));
        AgileDecimal difference = floor.subtract(number);
        return difference.compareTo(halfSpacing) >= 0?floor:floor.add(spacing);
    }

    private static AgileDecimal mid(AgileDecimal left, AgileDecimal right) {
        AgileDecimal sum = left.add(right);
        AgileDecimal mid = sum.multiply(AgileDecimal.half(left.getSystem()));
        int scale = Math.max(left.getScale(), right.getScale());
        if(mid.getScale() > scale) {
            AgileDecimal roundDown = mid.setScale(scale, false);
            if(roundDown.compareTo(left) > 0) {
                return roundDown;
            }

            AgileDecimal roundUp = mid.setScale(scale, true);
            if(roundUp.compareTo(right) < 0) {
                return roundUp;
            }
        }

        return mid;
    }

    private void fillDecimal() {
        if(this.decimal == null) {
            String[] parts = this.value.split("\\|");
            this.bucket = AgileRankBucket.from(parts[0]);
            this.decimal = AgileDecimal.parse(parts[1], NUMERAL_SYSTEM);
        }

    }

    public AgileRankBucket getBucket() {
        this.fillDecimal();
        return this.bucket;
    }

    public AgileDecimal getDecimal() {
        this.fillDecimal();
        return this.decimal;
    }

    public AgileRank inNextBucket() {
        this.fillDecimal();
        return from(this.bucket.next(), this.decimal);
    }

    public AgileRank inPrevBucket() {
        this.fillDecimal();
        return from(this.bucket.prev(), this.decimal);
    }

    public boolean isMin() {
        this.fillDecimal();
        return this.decimal.equals(MIN_DECIMAL);
    }

    public boolean isMax() {
        this.fillDecimal();
        return this.decimal.equals(MAX_DECIMAL);
    }

    public String format() {
        return this.value;
    }

    public static String formatDecimal(AgileDecimal decimal) {
        String formatVal = decimal.format();
        StringBuilder val = new StringBuilder(formatVal);
        int partialIndex = formatVal.indexOf(NUMERAL_SYSTEM.getRadixPointChar());
        char zero = NUMERAL_SYSTEM.toChar(0);
        if(partialIndex < 0) {
            partialIndex = formatVal.length();
            val.append(NUMERAL_SYSTEM.getRadixPointChar());
        }

        while(partialIndex < 6) {
            val.insert(0, zero);
            ++partialIndex;
        }

        while(val.charAt(val.length() - 1) == zero) {
            val.setLength(val.length() - 1);
        }

        return val.toString();
    }

    public static AgileRank parse(String str) {
        return new AgileRank(str);
    }

    public static AgileRank from(AgileRankBucket bucket, AgileDecimal decimal) {
        if(decimal.getSystem() != NUMERAL_SYSTEM) {
            throw new CommonException(NUMERAL_SYSTEM_ERROR);
        } else {
            return new AgileRank(bucket, decimal);
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof AgileRank && (this == o || this.value.equals(((AgileRank) o).value));
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public String toString() {
        return this.value;
    }

    @Override
    public int compareTo(AgileRank o) {
        return this.value.compareTo(o.value);
    }

    static {
        NUMERAL_SYSTEM = AgileNumeralSystem.BASE_36;
        ZERO_DECIMAL = AgileDecimal.parse("0", NUMERAL_SYSTEM);
        ONE_DECIMAL = AgileDecimal.parse("1", NUMERAL_SYSTEM);
        EIGHT_DECIMAL = AgileDecimal.parse("8", NUMERAL_SYSTEM);
        MIN_DECIMAL = ZERO_DECIMAL;
        MAX_DECIMAL = AgileDecimal.parse("1000000", NUMERAL_SYSTEM).subtract(ONE_DECIMAL);
        MID_DECIMAL = mid(MIN_DECIMAL, MAX_DECIMAL);
        INITIAL_MIN_DECIMAL = AgileDecimal.parse("100000", NUMERAL_SYSTEM);
        INITIAL_MAX_DECIMAL = AgileDecimal.parse(NUMERAL_SYSTEM.toChar(NUMERAL_SYSTEM.getBase() - 2) + "00000", NUMERAL_SYSTEM);
    }
}


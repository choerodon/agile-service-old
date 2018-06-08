package io.choerodon.agile.infra.common.utils.arilerank;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Created by jian_zhang02@163.com on 2018/5/28.
 */
public class AgileDecimal implements Comparable<AgileDecimal> {
    private final AgileInteger mag;
    private final int sig;

    private static final String RADIX_POINT_ERROR = "error.rank.moreThanOne";

    private AgileDecimal(AgileInteger mag, int sig) {
        this.mag = mag;
        this.sig = sig;
    }

    public static AgileDecimal half(AgileNumeralSystem sys) {
        int mid = sys.getBase() / 2;
        return make(AgileInteger.make(sys, 1, new int[]{mid}), 1);
    }

    public static AgileDecimal parse(String str, AgileNumeralSystem system) {
        int partialIndex = str.indexOf(system.getRadixPointChar());
        if (str.lastIndexOf(system.getRadixPointChar()) != partialIndex) {
            throw new CommonException(RADIX_POINT_ERROR);
        } else if (partialIndex < 0) {
            return make(AgileInteger.parse(str, system), 0);
        } else {
            String intStr = str.substring(0, partialIndex) + str.substring(partialIndex + 1);
            return make(AgileInteger.parse(intStr, system), str.length() - 1 - partialIndex);
        }
    }

    public static AgileDecimal from(AgileInteger integer) {
        return make(integer, 0);
    }

    public static AgileDecimal make(AgileInteger integer, int sig) {
        if (integer.isZero()) {
            return new AgileDecimal(integer, 0);
        } else {
            int zeroCount = 0;

            for (int i = 0; i < sig && integer.getMag(i) == 0; ++i) {
                ++zeroCount;
            }

            AgileInteger newInteger = integer.shiftRight(zeroCount);
            int newSig = sig - zeroCount;
            return new AgileDecimal(newInteger, newSig);
        }
    }

    public static AgileDecimal fromInt(int value, AgileNumeralSystem system) {
        char decimalPoint = AgileNumeralSystem.BASE_10.getRadixPointChar();
        char targetSystemPoint = system.getRadixPointChar();
        String lexoString = Integer.toString(value, system.getBase()).replace(decimalPoint, targetSystemPoint);
        return parse(lexoString, system);
    }

    public AgileNumeralSystem getSystem() {
        return this.mag.getSystem();
    }

    public int getOrderOfMagnitude() {
        return this.mag.getMagSize() - this.sig - 1;
    }

    public AgileDecimal add(AgileDecimal other) {
        AgileInteger tmag = this.mag;
        int tsig = this.sig;
        AgileInteger omag = other.mag;

        int osig;
        for (osig = other.sig; tsig < osig; ++tsig) {
            tmag = tmag.shiftLeft();
        }

        while (tsig > osig) {
            omag = omag.shiftLeft();
            ++osig;
        }

        return make(tmag.add(omag), tsig);
    }

    public AgileDecimal subtract(AgileDecimal other) {
        AgileInteger thisMag = this.mag;
        int thisSig = this.sig;
        AgileInteger otherMag = other.mag;

        int otherSig;
        for (otherSig = other.sig; thisSig < otherSig; ++thisSig) {
            thisMag = thisMag.shiftLeft();
        }

        while (thisSig > otherSig) {
            otherMag = otherMag.shiftLeft();
            ++otherSig;
        }

        return make(thisMag.subtract(otherMag), thisSig);
    }

    public AgileDecimal multiply(AgileDecimal other) {
        return make(this.mag.multiply(other.mag), this.sig + other.sig);
    }

    public AgileInteger floor() {
        return this.mag.shiftRight(this.sig);
    }

    public AgileInteger ceil() {
        if (this.isExact()) {
            return this.mag;
        } else {
            AgileInteger floor = this.floor();
            return floor.add(AgileInteger.one(floor.getSystem()));
        }
    }

    public boolean isExact() {
        if (this.sig == 0) {
            return true;
        } else {
            for (int i = 0; i < this.sig; ++i) {
                if (this.mag.getMag(i) != 0) {
                    return false;
                }
            }

            return true;
        }
    }

    public AgileInteger getMag() {
        return this.mag;
    }

    public int getScale() {
        return this.sig;
    }

    public AgileDecimal setScale(int nsig) {
        return this.setScale(nsig, false);
    }

    public AgileDecimal setScale(int nsig, boolean ceiling) {
        if (nsig >= this.sig) {
            return this;
        } else {
            if (nsig < 0) {
                nsig = 0;
            }

            int diff = this.sig - nsig;
            AgileInteger nmag = this.mag.shiftRight(diff);
            if (ceiling) {
                nmag = nmag.add(AgileInteger.one(nmag.getSystem()));
            }

            return make(nmag, nsig);
        }
    }

    /**
     * @param o o
     * @return int
     */
    @Override
    public int compareTo(AgileDecimal o) {
        AgileInteger tMag = this.mag;
        AgileInteger oMag = o.mag;
        if (this.sig > o.sig) {
            oMag = oMag.shiftLeft(this.sig - o.sig);
        } else if (this.sig < o.sig) {
            tMag = tMag.shiftLeft(o.sig - this.sig);
        }

        return tMag.compareTo(oMag);
    }

    public String format() {
        String intStr = this.mag.format();
        if (this.sig == 0) {
            return intStr;
        } else {
            StringBuilder sb = new StringBuilder(intStr);
            char head = sb.charAt(0);
            boolean specialHead = head == this.mag.getSystem().getPositiveChar() || head == this.mag.getSystem().getNegativeChar();
            if (specialHead) {
                sb.deleteCharAt(0);
            }

            while (sb.length() < this.sig + 1) {
                sb.insert(0, this.mag.getSystem().toChar(0));
            }

            sb.insert(sb.length() - this.sig, this.mag.getSystem().getRadixPointChar());
            if (sb.length() - this.sig == 0) {
                sb.insert(0, this.mag.getSystem().toChar(0));
            }

            if (specialHead) {
                sb.insert(0, head);
            }

            return sb.toString();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AgileDecimal)) {
            return false;
        } else {
            AgileDecimal o = (AgileDecimal) obj;
            return this.mag.equals(o.mag) && this.sig == o.sig;
        }
    }
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, false);
    }
    @Override
    public String toString() {
        return this.format();
    }
}


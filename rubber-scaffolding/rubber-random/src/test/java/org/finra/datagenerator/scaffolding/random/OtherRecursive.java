package org.finra.datagenerator.scaffolding.random;

import java.util.Map;

/**
 * Created by dkopel on 12/8/16.
 */
public class OtherRecursive {
    private float fl;

    private Map<OtherRecursive, String> mmp;

    public float getFl() {
        return fl;
    }

    public OtherRecursive setFl(float fl) {
        this.fl = fl;
        return this;
    }

    public Map<OtherRecursive, String> getMmp() {
        return mmp;
    }

    public OtherRecursive setMmp(Map<OtherRecursive, String> mmp) {
        this.mmp = mmp;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OtherRecursive)) return false;

        OtherRecursive that = (OtherRecursive) o;

        if (Float.compare(that.fl, fl) != 0) return false;
        return mmp != null ? mmp.equals(that.mmp) : that.mmp == null;
    }

    @Override
    public int hashCode() {
        int result = (fl != +0.0f ? Float.floatToIntBits(fl) : 0);
        result = 31 * result + (mmp != null ? mmp.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OtherRecursive{" +
            "fl=" + fl +
            ", mmp=" + mmp +
            '}';
    }
}

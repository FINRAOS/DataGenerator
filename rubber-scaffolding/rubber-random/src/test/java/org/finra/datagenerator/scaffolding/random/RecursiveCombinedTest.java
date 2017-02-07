package org.finra.datagenerator.scaffolding.random;

/**
 * Created by dkopel on 12/5/16.
 */
public class RecursiveCombinedTest {
    private String str;

    private Float flo;

    private RecursiveCombinedTest rct;

    public String getStr() {
        return str;
    }

    public RecursiveCombinedTest setStr(String str) {
        this.str = str;
        return this;
    }

    public Float getFlo() {
        return flo;
    }

    public RecursiveCombinedTest setFlo(Float flo) {
        this.flo = flo;
        return this;
    }

    public RecursiveCombinedTest getRct() {
        return rct;
    }

    public RecursiveCombinedTest setRct(RecursiveCombinedTest rct) {
        this.rct = rct;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecursiveCombinedTest)) return false;

        RecursiveCombinedTest that = (RecursiveCombinedTest) o;

        if (str != null ? !str.equals(that.str) : that.str != null) return false;
        if (flo != null ? !flo.equals(that.flo) : that.flo != null) return false;
        return rct != null ? rct.equals(that.rct) : that.rct == null;

    }

    @Override
    public int hashCode() {
        int result = str != null ? str.hashCode() : 0;
        result = 31 * result + (flo != null ? flo.hashCode() : 0);
        result = 31 * result + (rct != null ? rct.hashCode() : 0);
        return result;
    }
}

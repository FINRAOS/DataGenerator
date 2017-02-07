package org.finra.datagenerator.scaffolding.random;

/**
 * Created by dkopel on 12/5/16.
 */
public class CombinedTest {
    private TestMap m;

    private TestThing tt;

    public TestMap getM() {
        return m;
    }

    public CombinedTest setM(TestMap m) {
        this.m = m;
        return this;
    }

    public TestThing getTt() {
        return tt;
    }

    public CombinedTest setTt(TestThing tt) {
        this.tt = tt;
        return this;
    }
}

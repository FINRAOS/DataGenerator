package org.finra.datagenerator.scaffolding.random;

import java.util.List;

/**
 * Created by dkopel on 12/5/16.
 */
public class TestThing {
    private List<Long> thing;

    public List<Long> getThing() {
        return thing;
    }

    public TestThing setThing(List<Long> thing) {
        this.thing = thing;
        return this;
    }
}
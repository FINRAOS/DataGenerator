package org.finra.datagenerator.scaffolding.transformer;

import org.finra.datagenerator.scaffolding.random.support.annotations.DoubleRange;
import org.finra.datagenerator.scaffolding.random.support.annotations.LongRange;

/**
 * Created by dkopel on 10/5/16.
 */
public class GreatClass {
    public GreatClass() {}

    public GreatClass(Long time, Double amt) {
        this.time = time;
        this.amt = amt;
    }

//    @Randomizer(value=LongRangeRandomizer.class, args={
//        @RandomizerArgument(value="0", type=Long.class),
//        @RandomizerArgument(value="500", type=Long.class)
//    })
    @LongRange(min=0, max=500)
    private Long time;

//    @Randomizer(value=DoubleRangeRandomizer.class, args={
//        @RandomizerArgument(value="0.00", type=Double.class),
//        @RandomizerArgument(value="500.00", type=Double.class)
//    })
    @DoubleRange(min=0.00, max=500.00)
    private Double amt;

    public Long getTime() {
        return time;
    }

    public Double getAmt() {
        return amt;
    }
}

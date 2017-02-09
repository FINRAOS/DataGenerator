package org.finra.datagenerator.scaffolding.transformer;

import org.finra.datagenerator.scaffolding.transformer.support.Transformation;

/**
 * Created by dkopel on 9/27/16.
 */
public class AnotherClass {
    @Transformation("#big.name")
    private String name;

    @Transformation("#big.name+#big.num")
    private String secretName;

    @Transformation(value = "#sc.seq+10", condition = "#iteration % 2 == 0 && #sc.seq != null")
    @Transformation(value = "#sc.seq-10", condition = "#iteration % 2 != 0 && #sc.seq != null")
    private Long seq;

    @Transformation(value="#context.getIteration(#iteration-1, 'big').name", condition = "#iteration>0")
    private String prevName;


    public String getName() {
        return name;
    }

    public String getSecretName() {
        return secretName;
    }

    public Long getSeq() {
        return seq;
    }

    public String getPrevName() {
        return prevName;
    }
}

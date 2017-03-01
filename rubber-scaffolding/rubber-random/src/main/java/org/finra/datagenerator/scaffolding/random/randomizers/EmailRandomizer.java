package org.finra.datagenerator.scaffolding.random.randomizers;

import org.finra.datagenerator.scaffolding.random.predicate.JavaClassRandomGenerator;
import org.finra.datagenerator.scaffolding.random.predicate.RandomContext;
import org.finra.datagenerator.scaffolding.random.userTypes.Email;

/**
 * Created by dkopel on 1/27/17.
 */
public class EmailRandomizer extends JavaClassRandomGenerator<Email> {
    @Override
    public Class<?>[] classes() {
        return new Class<?>[] { Email.class };
    }

    @Override
    public Email apply(RandomContext rc) {
        String name = rc.jpr().strings.nextString(5, 10);
        String domain1 = rc.jpr().strings.nextAlphaString(5, 10);
        String domain2 = rc.jpr().strings.nextAlphaString(2, 3).toLowerCase();
        return new Email(name, domain1, domain2);
    }
}
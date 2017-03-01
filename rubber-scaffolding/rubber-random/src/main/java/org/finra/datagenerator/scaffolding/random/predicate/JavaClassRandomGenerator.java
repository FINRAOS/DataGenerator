package org.finra.datagenerator.scaffolding.random.predicate;

import org.finra.datagenerator.scaffolding.config.ConfigBundle;
import org.finra.datagenerator.scaffolding.config.ConfigBundleName;
import org.finra.datagenerator.scaffolding.config.Configurable;

/**
 * Created by dkopel on 1/27/17.
 */
public abstract class JavaClassRandomGenerator<T>
    extends AbstractJavaClassRandomGenerator<T>
    implements Configurable {

    @Override
    public abstract T apply(RandomContext rc);

    public Class[] classes() {
        return new Class[]{};
    }

    public long priority() {
        return Long.MAX_VALUE;
    }

    @Override
    public ConfigBundle configBundle() {
        return new ConfigBundle(
            ConfigBundleName.apply(getClass().getName()),
            new scala.collection.immutable.HashMap<>()
        );
    }
}
package org.finra.datagenerator.scaffolding.random.predicate;

import org.finra.datagenerator.scaffolding.config.ConfigBundle;
import org.finra.datagenerator.scaffolding.config.ConfigBundle$;
import org.finra.datagenerator.scaffolding.config.ConfigBundleName;
import org.finra.datagenerator.scaffolding.config.Configurable;
import scala.collection.immutable.HashMap;

/**
 * Created by dkopel on 1/27/17.
 */
@FunctionalInterface
public interface JavaClassRandomGenerator<T> extends Configurable {
    T apply(RandomContext rc);

    default Class[] classes() {
        return new Class[]{};
    }

    default long priority() {
        return Long.MAX_VALUE;
    }

    @Override
    default ConfigBundle configBundle() {
        return ConfigBundle$.MODULE$.apply(
            ConfigBundleName.apply(getClass().getName()),
            new HashMap()
        );
    }
}
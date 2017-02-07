package org.finra.datagenerator.scaffolding.transformer.service.adapter;

import java.nio.file.Path;
import java.util.Collection;

/**
 * Created by dkopel on 10/28/16.
 */
public interface TransformerAdapter {
    Collection<Class<?>> convert(Path input);
}

package com.github.jbduncan.collect.testing;

import java.util.Collection;
import java.util.Set;

public interface CollectionContract<E> extends ContainerContract<Collection<E>, E> {
  @Override
  TestCollectionGenerator<E> generator();

  @Override
  default Set<Feature<?>> features() {
    return Feature.allFeatures(CollectionFeature.GENERAL_PURPOSE);
  }
}

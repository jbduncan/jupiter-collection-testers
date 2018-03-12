package com.github.jbduncan.collect.testing;

import java.util.Collections;
import java.util.Set;

class CollectionsEmptyListTests implements ListContract<String> {
  @Override
  public TestListGenerator<String> generator() {
    return (TestStringListGenerator) elements -> Collections.emptyList();
  }

  @Override
  public Set<Feature<?>> features() {
    return Feature.allFeaturesRecursively(
        CollectionFeature.SERIALIZABLE, CollectionSize.SUPPORTS_ZERO);
  }
}

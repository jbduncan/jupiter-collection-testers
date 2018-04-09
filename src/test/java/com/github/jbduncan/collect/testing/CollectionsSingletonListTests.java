package com.github.jbduncan.collect.testing;

import java.util.Collections;
import java.util.Set;

public class CollectionsSingletonListTests implements ListContract<String> {
  @Override
  public TestListGenerator<String> generator() {
    return (TestStringListGenerator)
        elements -> Collections.singletonList(elements.iterator().next());
  }

  @Override
  public Set<Feature<?>> features() {
    return Feature.allFeaturesRecursively(
        CollectionFeature.SERIALIZABLE,
        CollectionFeature.ALLOWS_NULL_VALUES,
        CollectionSize.SUPPORTS_ONE);
  }
}

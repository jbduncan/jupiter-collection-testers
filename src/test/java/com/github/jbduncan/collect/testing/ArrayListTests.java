package com.github.jbduncan.collect.testing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

class ArrayListTests implements ListContract<String> {
  @Override
  public TestListGenerator<String> generator() {
    return (TestStringListGenerator) elements -> new ArrayList<>(Arrays.asList(elements));
  }

  @Override
  public Set<Feature<?>> features() {
    return Feature.allFeaturesRecursively(
        ListFeature.GENERAL_PURPOSE,
        CollectionFeature.SERIALIZABLE,
        CollectionFeature.ALLOWS_NULL_VALUES,
        CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
        CollectionSize.SUPPORTS_ANY_SIZE);
  }
}

package com.github.jbduncan.collect.testing;

import static java.util.Arrays.asList;
import static java.util.Arrays.copyOf;

import java.util.ArrayList;
import java.util.Set;

class ArrayListTests implements ListContract<String> {
  @Override
  public TestListGenerator<String> generator() {
    return (TestStringListGenerator)
        elements -> new ArrayList<>(asList(copyOf(elements, elements.length)));
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

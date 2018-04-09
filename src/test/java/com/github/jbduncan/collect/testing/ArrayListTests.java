package com.github.jbduncan.collect.testing;

import static com.github.jbduncan.collect.testing.Helpers.stream;
import static com.github.jbduncan.collect.testing.Helpers.toUnmodifiableList;

import java.util.ArrayList;
import java.util.Set;

class ArrayListTests implements ListContract<String> {
  @Override
  public TestListGenerator<String> generator() {
    return (TestStringListGenerator)
        elements -> new ArrayList<>(stream(elements).collect(toUnmodifiableList()));
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

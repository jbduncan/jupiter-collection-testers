package com.github.jbduncan.collect.testing;

import java.util.Set;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

// TODO: Consider using custom list impls to test that each assertion passes & fails as expected
public interface ListContract<E> extends CollectionContract<E> {
  @Override
  TestListGenerator<E> generator();

  @Override
  default Set<Feature<?>> features() {
    return Feature.allFeaturesRecursively(ListFeature.GENERAL_PURPOSE);
  }

  @TestFactory
  default Iterable<DynamicNode> add() {
    return ListAddTester.<E>builder()
        .testListGenerator(generator())
        .features(features())
        .build()
        .dynamicTests();
  }

  @TestFactory
  default Iterable<DynamicNode> addWithIndex() {
    return ListAddWithIndexTester.<E>builder()
        .testListGenerator(generator())
        .features(features())
        .build()
        .dynamicTests();

    // TODO: Finish implementing this method - ListAddWithIndexTester doesn't test everything yet
    // TODO: Test this method
  }

  // TODO: Add tests for all other methods of List interface
}

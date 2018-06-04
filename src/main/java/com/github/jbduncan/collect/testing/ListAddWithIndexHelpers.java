package com.github.jbduncan.collect.testing;

import static com.github.jbduncan.collect.testing.Helpers.newCollectionOfSize;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.ThrowingConsumer;

final class ListAddWithIndexHelpers {
  private ListAddWithIndexHelpers() {}

  static <E> void addDynamicSubTests(
      Set<CollectionSize> supportedCollectionSizes,
      String displayNameFormat,
      SampleElements<E> samples,
      ThrowingConsumer<CollectionSize> testExecutor,
      List<DynamicTest> subTests) {
    DynamicTest.stream(
        supportedCollectionSizes.iterator(),
        collectionSize ->
            String.format(
                displayNameFormat,
                collectionSize.size(),
                newCollectionOfSize(collectionSize, samples)),
        testExecutor)
        .forEachOrdered(subTests::add);
  }
}

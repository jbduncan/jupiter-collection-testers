package com.github.jbduncan.collect.testing;

import static com.github.jbduncan.collect.testing.Helpers.collectionSizeToElements;
import static com.github.jbduncan.collect.testing.Helpers.extractConcreteSizes;
import static com.github.jbduncan.collect.testing.Helpers.newArrayWithNullElementInMiddle;
import static com.github.jbduncan.collect.testing.Helpers.newListToTest;
import static com.github.jbduncan.collect.testing.Helpers.newListToTestWithNullElementInMiddle;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.ThrowingConsumer;

public interface ListContract<E> extends CollectionContract<E> {
  @Override
  TestListGenerator<E> generator();

  @Override
  default Set<Feature<?>> features() {
    return Feature.allFeaturesRecursively(ListFeature.GENERAL_PURPOSE);
  }

  @TestFactory
  default List<DynamicTest> add() {
    TestListGenerator<E> generator = generator();
    SampleElements<E> samples = generator().samples();

    ThrowingConsumer<CollectionSize> supportsAdd =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);
          E e3 = samples.e3();

          assertTrue(list.add(e3), "Not true that list.add(e3) returned true");
          List<E> expected =
              Stream.concat(
                      collectionSizeToElements(collectionSize, samples).stream(), Stream.of(e3))
                  .collect(toList());
          assertIterableEquals(expected, list, "Not true that list contains e3");
        };

    ThrowingConsumer<CollectionSize> supportsAddWithNullElements =
        collectionSize -> {
          List<E> list = newListToTestWithNullElementInMiddle(generator(), collectionSize);

          assertTrue(list.add(null), "Not true that list.add(null) returned true");
          List<E> expected =
              Stream.concat(
                      Arrays.stream(newArrayWithNullElementInMiddle(samples, collectionSize)),
                      Stream.of((E) null))
                  .collect(toList());
          assertIterableEquals(expected, list, "Not true that list contains null");
        };

    ThrowingConsumer<CollectionSize> doesNotSupportAdd =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(UnsupportedOperationException.class, () -> list.add(samples.e0()));
          assertThrows(UnsupportedOperationException.class, () -> list.add(null));
        };

    Set<Feature<?>> allFeatures = features();
    Set<CollectionSize> supportedCollectionSizes = extractConcreteSizes(allFeatures);
    List<DynamicTest> tests = new ArrayList<>();
    if (allFeatures.contains(CollectionFeature.SUPPORTS_ADD)) {
      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      "Supports List.add(): size: %s, elements: %s",
                      collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
              supportsAdd)
          .forEachOrdered(tests::add);
    }
    if (allFeatures.containsAll(
        Arrays.asList(CollectionFeature.SUPPORTS_ADD, CollectionFeature.ALLOWS_NULL_VALUES))) {
      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      "Supports List.add() with null element: size: %s, elements: %s",
                      collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
              supportsAddWithNullElements)
          .forEachOrdered(tests::add);
    }
    if (!allFeatures.contains(CollectionFeature.SUPPORTS_ADD)) {
      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      "Does not support List.add(): size: %s, elements: %s",
                      collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAdd)
          .forEachOrdered(tests::add);
    }
    return Collections.unmodifiableList(tests);
  }
}

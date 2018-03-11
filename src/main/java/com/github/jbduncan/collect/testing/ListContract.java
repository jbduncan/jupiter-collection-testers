package com.github.jbduncan.collect.testing;

import static com.github.jbduncan.collect.testing.Helpers.append;
import static com.github.jbduncan.collect.testing.Helpers.collectionSizeToElements;
import static com.github.jbduncan.collect.testing.Helpers.extractConcreteSizes;
import static com.github.jbduncan.collect.testing.Helpers.minus;
import static com.github.jbduncan.collect.testing.Helpers.newArrayWithNullElementInMiddle;
import static com.github.jbduncan.collect.testing.Helpers.newListToTest;
import static com.github.jbduncan.collect.testing.Helpers.newListToTestWithNullElementInMiddle;
import static com.github.jbduncan.collect.testing.Helpers.prepend;
import static com.github.jbduncan.collect.testing.Helpers.quote;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
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
  default Iterable<DynamicTest> add() {
    TestListGenerator<E> generator = generator();
    SampleElements<E> samples = generator().samples();

    // TODO: Move all ThrowingConsumers right next to their associated dynamic test streams
    ThrowingConsumer<CollectionSize> supportsAddWithNewElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);
          E e3 = samples.e3();

          assertTrue(
              list.add(e3),
              () -> String.format("Not true that list.add(%s) returned true", quote(e3)));
          List<E> expected = append(collectionSizeToElements(collectionSize, samples), e3);
          assertIterableEquals(
              expected,
              list,
              () -> String.format("Not true that list was appended with %s", quote(e3)));
        };

    ThrowingConsumer<CollectionSize> supportsAddWithExistingElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);
          E e0 = samples.e0();

          assertTrue(
              list.add(e0),
              () -> String.format("Not true that list.add(%s) returned true", quote(e0)));
          List<E> expected = append(collectionSizeToElements(collectionSize, samples), e0);
          assertIterableEquals(
              expected,
              list,
              () -> String.format("Not true that list was appended with %s", quote(e0)));
        };

    ThrowingConsumer<CollectionSize> supportsAddWithNullElement =
        collectionSize -> {
          List<E> list = newListToTestWithNullElementInMiddle(generator(), collectionSize);

          assertTrue(list.add(null), "Not true that list.add(null) returned true");
          List<E> expected =
              append(Arrays.asList(newArrayWithNullElementInMiddle(samples, collectionSize)), null);
          assertIterableEquals(expected, list, "Not true that list was appended with null");
        };

    ThrowingConsumer<CollectionSize> doesNotSupportAdd =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);
          E e0 = samples.e0();

          String message = "Not true that list.add(%s) threw UnsupportedOperationException";
          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(e0),
              () -> String.format(message, quote(e0)));
          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(null),
              () -> String.format(message, "null"));
          assertIterableEquals(
              collectionSizeToElements(collectionSize, samples),
              list,
              "Not true that list remained unchanged");
        };

    Set<Feature<?>> allFeatures = features();
    Set<CollectionSize> supportedCollectionSizes = extractConcreteSizes(allFeatures);
    List<DynamicTest> tests = new ArrayList<>();

    if (allFeatures.contains(CollectionFeature.SUPPORTS_ADD)) {
      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      "Supports List.add(E) with new element: size: %s, elements: %s",
                      collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
              supportsAddWithNewElement)
          .forEachOrdered(tests::add);

      DynamicTest.stream(
              minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
              collectionSize ->
                  String.format(
                      "Supports List.add(E) with existing element: size: %s, elements: %s",
                      collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
              supportsAddWithExistingElement)
          .forEachOrdered(tests::add);
    }

    if (allFeatures.containsAll(
        Arrays.asList(CollectionFeature.SUPPORTS_ADD, CollectionFeature.ALLOWS_NULL_VALUES))) {
      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      "Supports List.add(E) with null element: size: %s, elements: %s",
                      collectionSize.size(),
                      Arrays.toString(newArrayWithNullElementInMiddle(samples, collectionSize))),
              supportsAddWithNullElement)
          .forEachOrdered(tests::add);
    }

    if (!allFeatures.contains(CollectionFeature.SUPPORTS_ADD)) {
      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      "Does not support List.add(E): size: %s, elements: %s",
                      collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAdd)
          .forEachOrdered(tests::add);
    }
    return Collections.unmodifiableList(tests);
  }
}

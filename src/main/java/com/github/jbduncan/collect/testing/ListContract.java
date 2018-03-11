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

// TODO: Use custom list implementations to test that each assertion passes & fails as expected
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
    Set<Feature<?>> allFeatures = features();
    Set<CollectionSize> supportedCollectionSizes = extractConcreteSizes(allFeatures);

    List<DynamicTest> tests = new ArrayList<>();

    if (allFeatures.contains(CollectionFeature.SUPPORTS_ADD)) {
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

      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      "Supports List.add(E) with new element: size: %s, elements: %s",
                      collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
              supportsAddWithNewElement)
          .forEachOrdered(tests::add);

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
      ThrowingConsumer<CollectionSize> supportsAddWithNullElement =
          collectionSize -> {
            List<E> list = newListToTestWithNullElementInMiddle(generator(), collectionSize);

            assertTrue(list.add(null), "Not true that list.add(null) returned true");
            List<E> expected =
                append(
                    Arrays.asList(newArrayWithNullElementInMiddle(samples, collectionSize)), null);
            assertIterableEquals(expected, list, "Not true that list was appended with null");
          };

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

      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      "Does not support List.add(E): size: %s, elements: %s",
                      collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAdd)
          .forEachOrdered(tests::add);
    }

    return tests;
  }

  @TestFactory
  default Iterable<DynamicTest> addWithIndex() {
    TestListGenerator<E> generator = generator();
    SampleElements<E> samples = generator().samples();

    Set<Feature<?>> allFeatures = features();
    Set<CollectionSize> supportedCollectionSizes = extractConcreteSizes(allFeatures);
    List<DynamicTest> tests = new ArrayList<>();

    if (allFeatures.contains(ListFeature.SUPPORTS_ADD_WITH_INDEX)) {
      ThrowingConsumer<CollectionSize> supportsAddWithIndexWithNewElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);
            E e3 = samples.e3();

            list.add(0, e3);
            assertTrue(
                list.get(0).equals(e3),
                () -> String.format("Not true that list.add(0, %s) returned true", quote(e3)));
            List<E> expected = prepend(e3, collectionSizeToElements(collectionSize, samples));
            assertIterableEquals(
                expected,
                list,
                () -> String.format("Not true that list was prepended with %s", quote(e3)));
          };

      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      "Supports List.add(int, E) with new element: size: %s, elements: %s",
                      collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
              supportsAddWithIndexWithNewElement)
          .forEachOrdered(tests::add);

      ThrowingConsumer<CollectionSize> supportsAddWithIndexWithExistingElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);
            E e0 = samples.e0();

            list.add(0, e0);
            List<E> expected = prepend(e0, collectionSizeToElements(collectionSize, samples));
            assertIterableEquals(
                expected,
                list,
                () -> String.format("Not true that list was prepended with %s", quote(e0)));
          };

      DynamicTest.stream(
              minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
              collectionSize ->
                  String.format(
                      "Supports List.add(int, E) with existing element: size: %s, elements: %s",
                      collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
              supportsAddWithIndexWithExistingElement)
          .forEachOrdered(tests::add);
    }

    if (allFeatures.containsAll(
        Arrays.asList(ListFeature.SUPPORTS_ADD_WITH_INDEX, CollectionFeature.ALLOWS_NULL_VALUES))) {
      ThrowingConsumer<CollectionSize> supportsAddWithIndexWithNullElement =
          collectionSize -> {
            List<E> list = newListToTestWithNullElementInMiddle(generator(), collectionSize);

            list.add(0, null);
            List<E> expected =
                prepend(
                    null, Arrays.asList(newArrayWithNullElementInMiddle(samples, collectionSize)));
            assertIterableEquals(expected, list, "Not true that list was prepended with null");
          };

      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      "Supports List.add(int, E) with null element: size: %s, elements: %s",
                      collectionSize.size(),
                      Arrays.toString(newArrayWithNullElementInMiddle(samples, collectionSize))),
              supportsAddWithIndexWithNullElement)
          .forEachOrdered(tests::add);
    }

    if (!allFeatures.contains(ListFeature.SUPPORTS_ADD_WITH_INDEX)) {
      ThrowingConsumer<CollectionSize> doesNotSupportAddWithIndex =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);
            E e0 = samples.e0();

            String message = "Not true that list.add(%s) threw UnsupportedOperationException";
            assertThrows(
                UnsupportedOperationException.class,
                () -> list.add(0, e0),
                () -> String.format(message, quote(e0)));
            assertThrows(
                UnsupportedOperationException.class,
                () -> list.add(0, null),
                () -> String.format(message, "null"));
            assertIterableEquals(
                collectionSizeToElements(collectionSize, samples),
                list,
                "Not true that list remained unchanged");
          };

      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      "Does not support List.add(int, E): size: %s, elements: %s",
                      collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAddWithIndex)
          .forEachOrdered(tests::add);
    }

    // TODO: Finish implementing this method
    // TODO: Test this method

    return tests;
  }
}

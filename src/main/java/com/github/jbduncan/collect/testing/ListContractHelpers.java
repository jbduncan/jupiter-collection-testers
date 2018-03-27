package com.github.jbduncan.collect.testing;

import static com.github.jbduncan.collect.testing.Helpers.append;
import static com.github.jbduncan.collect.testing.Helpers.collectionSizeToElements;
import static com.github.jbduncan.collect.testing.Helpers.minus;
import static com.github.jbduncan.collect.testing.Helpers.newArrayWithNullElementInMiddle;
import static com.github.jbduncan.collect.testing.Helpers.newListToTest;
import static com.github.jbduncan.collect.testing.Helpers.newListToTestWithNullElementInMiddle;
import static com.github.jbduncan.collect.testing.Helpers.prepend;
import static com.github.jbduncan.collect.testing.Helpers.quote;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.ThrowingConsumer;

final class ListContractHelpers {
  private ListContractHelpers() {}

  // TODO: Add a `DynamicContainer` to `tests` instead of a group of `DynamicTest`s. Do same for
  // all other helper methods in this class.
  static <E> void appendSupportsAddTests(
      TestListGenerator<E> generator,
      SampleElements<E> samples,
      Set<CollectionSize> supportedCollectionSizes,
      List<DynamicTest> tests) {

    E e0 = samples.e0();
    E e3 = samples.e3();

    ThrowingConsumer<CollectionSize> supportsAddWithNewElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

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

  static <E> void appendSupportsAddWithNullElementsTests(
      TestListGenerator<E> generator,
      SampleElements<E> samples,
      Set<CollectionSize> supportedCollectionSizes,
      List<DynamicTest> testsToAddTo) {

    ThrowingConsumer<CollectionSize> supportsAddWithNewNullElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertTrue(list.add(null), "Not true that list.add(null) returned true");
          List<E> expected = append(collectionSizeToElements(collectionSize, samples), null);
          assertIterableEquals(expected, list, "Not true that list was appended with null");
        };

    DynamicTest.stream(
            supportedCollectionSizes.iterator(),
            collectionSize ->
                String.format(
                    "Supports List.add(E) with new null element: size: %s, elements: %s",
                    collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
            supportsAddWithNewNullElement)
        .forEachOrdered(testsToAddTo::add);

    ThrowingConsumer<CollectionSize> supportsAddWithExistingNullElement =
        collectionSize -> {
          List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

          assertTrue(list.add(null), "Not true that list.add(null) returned true");
          List<E> expected =
              append(asList(newArrayWithNullElementInMiddle(samples, collectionSize)), null);
          assertIterableEquals(expected, list, "Not true that list was appended with null");
        };

    DynamicTest.stream(
            minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
            collectionSize ->
                String.format(
                    "Supports List.add(E) with existing null element: size: %s, elements: %s",
                    collectionSize.size(),
                    Arrays.toString(newArrayWithNullElementInMiddle(samples, collectionSize))),
            supportsAddWithExistingNullElement)
        .forEachOrdered(testsToAddTo::add);
  }

  static <E> void appendDoesNotSupportAddTests(
      TestListGenerator<E> generator,
      SampleElements<E> samples,
      Set<CollectionSize> supportedCollectionSizes,
      List<DynamicTest> tests) {

    String unsupportedMessage = "Not true that list.add(%s) threw UnsupportedOperationException";
    String unchangedMessage = "Not true that list remained unchanged";

    E e0 = samples.e0();
    E e3 = samples.e3();

    ThrowingConsumer<CollectionSize> doesNotSupportAddWithNewElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(e3),
              () -> String.format(unsupportedMessage, quote(e3)));
          assertIterableEquals(
              collectionSizeToElements(collectionSize, samples), list, unchangedMessage);
        };

    DynamicTest.stream(
            supportedCollectionSizes.iterator(),
            collectionSize ->
                String.format(
                    "Does not support List.add(E) with new element: size: %s, elements: %s",
                    collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
            doesNotSupportAddWithNewElement)
        .forEachOrdered(tests::add);

    ThrowingConsumer<CollectionSize> doesNotSupportAddWithExistingElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(e0),
              () -> String.format(unsupportedMessage, quote(e0)));
          assertIterableEquals(
              collectionSizeToElements(collectionSize, samples), list, unchangedMessage);
        };

    DynamicTest.stream(
            minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
            collectionSize ->
                String.format(
                    "Does not support List.add(E) with existing element: size: %s, elements: %s",
                    collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
            doesNotSupportAddWithExistingElement)
        .forEachOrdered(tests::add);

    ThrowingConsumer<CollectionSize> doesNotSupportAddWithNewNullElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(null),
              () -> String.format(unsupportedMessage, "null"));
          assertIterableEquals(
              collectionSizeToElements(collectionSize, samples), list, unchangedMessage);
        };

    DynamicTest.stream(
            supportedCollectionSizes.iterator(),
            collectionSize ->
                String.format(
                    "Does not support List.add(E) with new null element: size: %s, elements: %s",
                    collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
            doesNotSupportAddWithNewNullElement)
        .forEachOrdered(tests::add);

    ThrowingConsumer<CollectionSize> doesNotSupportAddAtStartWithExistingNullElement =
        collectionSize -> {
          List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(null),
              () -> String.format(unsupportedMessage, "null"));
          assertIterableEquals(
              asList(newArrayWithNullElementInMiddle(samples, collectionSize)),
              list,
              unchangedMessage);
        };

    DynamicTest.stream(
            minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
            collectionSize ->
                String.format(
                    "Does not support List.add(E) with existing null element: size: %s, elements: %s",
                    collectionSize.size(),
                    Arrays.toString(newArrayWithNullElementInMiddle(samples, collectionSize))),
            doesNotSupportAddAtStartWithExistingNullElement)
        .forEachOrdered(tests::add);
  }

  static <E> void appendSupportsAddWithIndexTests(
      TestListGenerator<E> generator,
      SampleElements<E> samples,
      Set<CollectionSize> supportedCollectionSizes,
      List<DynamicTest> tests) {

    E e0 = samples.e0();
    E e3 = samples.e3();

    ThrowingConsumer<CollectionSize> supportsAddAtStartWithNewElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          list.add(0, e3);
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
                    "Supports List.add(0, E) with new element: size: %s, elements: %s",
                    collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
            supportsAddAtStartWithNewElement)
        .forEachOrdered(tests::add);

    ThrowingConsumer<CollectionSize> supportsAddAtEndWithNewElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          list.add(list.size(), e3);
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
                    "Supports List.add(size(), E) with new element: size: %s, elements: %s",
                    collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
            supportsAddAtEndWithNewElement)
        .forEachOrdered(tests::add);

    ThrowingConsumer<CollectionSize> supportsAddAtStartWithExistingElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

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
                    "Supports List.add(0, E) with existing element: size: %s, elements: %s",
                    collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
            supportsAddAtStartWithExistingElement)
        .forEachOrdered(tests::add);

    ThrowingConsumer<CollectionSize> supportsAddAtEndWithExistingElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          list.add(list.size(), e0);
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
                    "Supports List.add(size(), E) with existing element: size: %s, elements: %s",
                    collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
            supportsAddAtEndWithExistingElement)
        .forEachOrdered(tests::add);
  }

  static <E> void appendSupportsAddWithIndexWithNullElementsTests(
      TestListGenerator<E> generator,
      SampleElements<E> samples,
      Set<CollectionSize> supportedCollectionSizes,
      List<DynamicTest> testsToAddTo) {

    ThrowingConsumer<CollectionSize> supportsAddAtStartWithNewNullElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          list.add(0, null);
          List<E> expected = prepend(null, collectionSizeToElements(collectionSize, samples));
          assertIterableEquals(expected, list, "Not true that list was prepended with null");
        };

    DynamicTest.stream(
            supportedCollectionSizes.iterator(),
            collectionSize ->
                String.format(
                    "Supports List.add(0, E) with new null element: size: %s, elements: %s",
                    collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
            supportsAddAtStartWithNewNullElement)
        .forEachOrdered(testsToAddTo::add);

    ThrowingConsumer<CollectionSize> supportsAddAtEndWithNewNullElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          list.add(list.size(), null);
          List<E> expected = append(collectionSizeToElements(collectionSize, samples), null);
          assertIterableEquals(expected, list, "Not true that list was appended with null");
        };

    DynamicTest.stream(
            supportedCollectionSizes.iterator(),
            collectionSize ->
                String.format(
                    "Supports List.add(size(), E) with new null element: size: %s, elements: %s",
                    collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
            supportsAddAtEndWithNewNullElement)
        .forEachOrdered(testsToAddTo::add);

    ThrowingConsumer<CollectionSize> supportsAddAtStartWithExistingNullElement =
        collectionSize -> {
          List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

          list.add(0, null);
          List<E> expected =
              prepend(null, asList(newArrayWithNullElementInMiddle(samples, collectionSize)));
          assertIterableEquals(expected, list, "Not true that list was prepended with null");
        };

    DynamicTest.stream(
            minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
            collectionSize ->
                String.format(
                    "Supports List.add(0, E) with existing null element: size: %s, elements: %s",
                    collectionSize.size(),
                    Arrays.toString(newArrayWithNullElementInMiddle(samples, collectionSize))),
            supportsAddAtStartWithExistingNullElement)
        .forEachOrdered(testsToAddTo::add);

    ThrowingConsumer<CollectionSize> supportsAddAtEndWithExistingNullElement =
        collectionSize -> {
          List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

          list.add(list.size(), null);
          List<E> expected =
              append(asList(newArrayWithNullElementInMiddle(samples, collectionSize)), null);
          assertIterableEquals(expected, list, "Not true that list was appended with null");
        };

    DynamicTest.stream(
            minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
            collectionSize ->
                String.format(
                    "Supports List.add(size(), E) with existing null element: size: %s, elements: %s",
                    collectionSize.size(),
                    Arrays.toString(newArrayWithNullElementInMiddle(samples, collectionSize))),
            supportsAddAtEndWithExistingNullElement)
        .forEachOrdered(testsToAddTo::add);
  }

  static <E> void appendDoesNotSupportAddWithIndexTests(
      TestListGenerator<E> generator,
      SampleElements<E> samples,
      Set<CollectionSize> supportedCollectionSizes,
      List<DynamicTest> testsToAddTo) {

    E e0 = samples.e0();
    E e3 = samples.e3();

    String message = "Not true that list.add(%s) threw UnsupportedOperationException";

    ThrowingConsumer<CollectionSize> doesNotSupportAddAtStartWithNewElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(0, e3),
              () -> String.format(message, quote(e3)));
          assertIterableEquals(
              collectionSizeToElements(collectionSize, samples),
              list,
              "Not true that list remained unchanged");
        };

    DynamicTest.stream(
            supportedCollectionSizes.iterator(),
            collectionSize ->
                String.format(
                    "Does not support List.add(0, E) with new element: size: %s, elements: %s",
                    collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
            doesNotSupportAddAtStartWithNewElement)
        .forEachOrdered(testsToAddTo::add);

    ThrowingConsumer<CollectionSize> doesNotSupportAddAtStartWithExistingElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(0, e0),
              () -> String.format(message, quote(e0)));
          assertIterableEquals(
              collectionSizeToElements(collectionSize, samples),
              list,
              "Not true that list remained unchanged");
        };

    DynamicTest.stream(
            minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
            collectionSize ->
                String.format(
                    "Does not support List.add(0, E) with existing element: size: %s, elements: %s",
                    collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
            doesNotSupportAddAtStartWithExistingElement)
        .forEachOrdered(testsToAddTo::add);

    ThrowingConsumer<CollectionSize> doesNotSupportAddAtStartWithNullElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

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
                    "Does not support List.add(0, E) with new null element: size: %s, elements: %s",
                    collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
            doesNotSupportAddAtStartWithNullElement)
        .forEachOrdered(testsToAddTo::add);

    ThrowingConsumer<CollectionSize> doesNotSupportAddAtStartWithExistingNullElement =
        collectionSize -> {
          List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(0, null),
              () -> String.format(message, "null"));
          assertIterableEquals(
              asList(newArrayWithNullElementInMiddle(samples, collectionSize)),
              list,
              "Not true that list remained unchanged");
        };

    DynamicTest.stream(
            minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
            collectionSize ->
                String.format(
                    "Does not support List.add(0, E) with existing null element: size: %s, elements: %s",
                    collectionSize.size(),
                    Arrays.toString(newArrayWithNullElementInMiddle(samples, collectionSize))),
            doesNotSupportAddAtStartWithExistingNullElement)
        .forEachOrdered(testsToAddTo::add);

    ThrowingConsumer<CollectionSize> doesNotSupportAddAtEndWithNewElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(list.size(), e3),
              () -> String.format(message, quote(e3)));
          assertIterableEquals(
              collectionSizeToElements(collectionSize, samples),
              list,
              "Not true that list remained unchanged");
        };

    DynamicTest.stream(
            supportedCollectionSizes.iterator(),
            collectionSize ->
                String.format(
                    "Does not support List.add(size(), E) with new element: size: %s, elements: %s",
                    collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
            doesNotSupportAddAtEndWithNewElement)
        .forEachOrdered(testsToAddTo::add);

    ThrowingConsumer<CollectionSize> doesNotSupportAddAtEndWithExistingElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(list.size(), e0),
              () -> String.format(message, quote(e0)));
          assertIterableEquals(
              collectionSizeToElements(collectionSize, samples),
              list,
              "Not true that list remained unchanged");
        };

    DynamicTest.stream(
            minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
            collectionSize ->
                String.format(
                    "Does not support List.add(size(), E) with existing element: size: %s, elements: %s",
                    collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
            doesNotSupportAddAtEndWithExistingElement)
        .forEachOrdered(testsToAddTo::add);

    ThrowingConsumer<CollectionSize> doesNotSupportAddAtEndWithNullElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(list.size(), null),
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
                    "Does not support List.add(size(), E) with new null element: size: %s, elements: %s",
                    collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
            doesNotSupportAddAtEndWithNullElement)
        .forEachOrdered(testsToAddTo::add);

    ThrowingConsumer<CollectionSize> doesNotSupportAddAtEndWithExistingNullElement =
        collectionSize -> {
          List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(list.size(), null),
              () -> String.format(message, "null"));
          assertIterableEquals(
              asList(newArrayWithNullElementInMiddle(samples, collectionSize)),
              list,
              "Not true that list remained unchanged");
        };

    DynamicTest.stream(
            minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
            collectionSize ->
                String.format(
                    "Does not support List.add(size(), E) with existing null element: size: %s, elements: %s",
                    collectionSize.size(),
                    Arrays.toString(newArrayWithNullElementInMiddle(samples, collectionSize))),
            doesNotSupportAddAtEndWithExistingNullElement)
        .forEachOrdered(testsToAddTo::add);

    ThrowingConsumer<CollectionSize> doesNotSupportAddAtMiddleWithNewElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(list.size() / 2, e3),
              () -> String.format(message, quote(e3)));
          assertIterableEquals(
              collectionSizeToElements(collectionSize, samples),
              list,
              "Not true that list remained unchanged");
        };

    DynamicTest.stream(
            supportedCollectionSizes.iterator(),
            collectionSize ->
                String.format(
                    "Does not support List.add(size() / 2, E) with new element: size: %s, elements: %s",
                    collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
            doesNotSupportAddAtMiddleWithNewElement)
        .forEachOrdered(testsToAddTo::add);

    ThrowingConsumer<CollectionSize> doesNotSupportAddAtMiddleWithExistingElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(list.size() / 2, e0),
              () -> String.format(message, quote(e0)));
          assertIterableEquals(
              collectionSizeToElements(collectionSize, samples),
              list,
              "Not true that list remained unchanged");
        };

    DynamicTest.stream(
            minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
            collectionSize ->
                String.format(
                    "Does not support List.add(size() / 2, E) with existing element: size: %s, elements: %s",
                    collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
            doesNotSupportAddAtMiddleWithExistingElement)
        .forEachOrdered(testsToAddTo::add);

    ThrowingConsumer<CollectionSize> doesNotSupportAddAtMiddleWithNullElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(list.size() / 2, null),
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
                    "Does not support List.add(size() / 2, E) with new null element: size: %s, elements: %s",
                    collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
            doesNotSupportAddAtMiddleWithNullElement)
        .forEachOrdered(testsToAddTo::add);

    ThrowingConsumer<CollectionSize> doesNotSupportAddAtMiddleWithExistingNullElement =
        collectionSize -> {
          List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(list.size() / 2, null),
              () -> String.format(message, "null"));
          assertIterableEquals(
              asList(newArrayWithNullElementInMiddle(samples, collectionSize)),
              list,
              "Not true that list remained unchanged");
        };

    DynamicTest.stream(
            minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
            collectionSize ->
                String.format(
                    "Does not support List.add(size() / 2, E) with existing null element: size: %s, elements: %s",
                    collectionSize.size(),
                    Arrays.toString(newArrayWithNullElementInMiddle(samples, collectionSize))),
            doesNotSupportAddAtMiddleWithExistingNullElement)
        .forEachOrdered(testsToAddTo::add);
  }
}

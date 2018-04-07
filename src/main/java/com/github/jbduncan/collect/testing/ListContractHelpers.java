package com.github.jbduncan.collect.testing;

import static com.github.jbduncan.collect.testing.Helpers.append;
import static com.github.jbduncan.collect.testing.Helpers.collectionSizeToElements;
import static com.github.jbduncan.collect.testing.Helpers.insert;
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
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.ThrowingConsumer;

final class ListContractHelpers {
  private ListContractHelpers() {}

  static <E> void appendSupportsAddTests(
      TestListGenerator<E> generator,
      SampleElements<E> samples,
      Set<Feature<?>> features,
      Set<CollectionSize> supportedCollectionSizes,
      List<DynamicNode> testsToAddTo) {

    if (features.contains(CollectionFeature.SUPPORTS_ADD)) {

      E e0 = samples.e0();
      E e3 = samples.e3();

      List<DynamicTest> tests = new ArrayList<>();

      // TODO: Consider moving these ThrowingConsumers into their own methods.
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

      testsToAddTo.add(dynamicContainer("Supports List.add(E)", tests));
    }
  }

  static <E> void appendSupportsAddWithNullElementsTests(
      TestListGenerator<E> generator,
      SampleElements<E> samples,
      Set<Feature<?>> features,
      Set<CollectionSize> supportedCollectionSizes,
      List<DynamicNode> testsToAddTo) {

    if (features.containsAll(
        Arrays.asList(CollectionFeature.SUPPORTS_ADD, CollectionFeature.ALLOWS_NULL_VALUES))) {

      ThrowingConsumer<CollectionSize> supportsAddWithNewNullElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);

            assertTrue(list.add(null), "Not true that list.add(null) returned true");
            List<E> expected = append(collectionSizeToElements(collectionSize, samples), null);
            assertIterableEquals(expected, list, "Not true that list was appended with null");
          };

      List<DynamicTest> tests = new ArrayList<>();

      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      "Supports List.add(E) with new null element: size: %s, elements: %s",
                      collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
              supportsAddWithNewNullElement)
          .forEachOrdered(tests::add);

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
          .forEachOrdered(tests::add);

      testsToAddTo.add(dynamicContainer("Supports List.add(E) with null element", tests));
    }
  }

  static <E> void appendDoesNotSupportAddTests(
      TestListGenerator<E> generator,
      SampleElements<E> samples,
      Set<Feature<?>> features,
      Set<CollectionSize> supportedCollectionSizes,
      List<DynamicNode> testsToAddTo) {

    if (!features.contains(CollectionFeature.SUPPORTS_ADD)) {

      String unsupportedMessage = "Not true that list.add(%s) threw UnsupportedOperationException";
      String unchangedMessage = "Not true that list remained unchanged";

      E e0 = samples.e0();
      E e3 = samples.e3();

      List<DynamicTest> tests = new ArrayList<>();

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
                      "Does not support List.add(E) with existing null element: "
                          + "size: %s, elements: %s",
                      collectionSize.size(),
                      Arrays.toString(newArrayWithNullElementInMiddle(samples, collectionSize))),
              doesNotSupportAddAtStartWithExistingNullElement)
          .forEachOrdered(tests::add);

      testsToAddTo.add(dynamicContainer("Does not support List.add(E)", tests));
    }
  }

  static <E> void appendSupportsAddWithIndexTests(
      TestListGenerator<E> generator,
      SampleElements<E> samples,
      Set<Feature<?>> features,
      Set<CollectionSize> supportedCollectionSizes,
      List<DynamicNode> testsToAddTo) {

    if (features.contains(ListFeature.SUPPORTS_ADD_WITH_INDEX)) {

      E e0 = samples.e0();
      E e3 = samples.e3();

      List<DynamicTest> tests = new ArrayList<>();

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

      ThrowingConsumer<CollectionSize> supportsAddAtMiddleWithNewElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);
            int middleIndex = list.size() / 2;

            list.add(middleIndex, e3);
            Iterable<E> expected =
                insert(collectionSizeToElements(collectionSize, samples), middleIndex, e3);
            assertIterableEquals(
                expected,
                list,
                () ->
                    String.format(
                        "Not true that %s was inserted at index %s of list, or that elements in "
                            + "list are in expected order",
                        quote(e3), middleIndex));
          };

      DynamicTest.stream(
              minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
              collectionSize ->
                  String.format(
                      "Supports List.add(size() / 2, E) with new element: size: %s, elements: %s",
                      collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
              supportsAddAtMiddleWithNewElement)
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

      ThrowingConsumer<CollectionSize> supportsAddAtMiddleWithExistingElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);
            int middleIndex = list.size() / 2;

            list.add(middleIndex, e0);
            Iterable<E> expected =
                insert(collectionSizeToElements(collectionSize, samples), middleIndex, e0);
            assertIterableEquals(
                expected,
                list,
                () ->
                    String.format(
                        "Not true that %s was inserted at index %s of list, or that elements in "
                            + "list are in expected order",
                        quote(e0), middleIndex));
          };

      DynamicTest.stream(
              minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
              collectionSize ->
                  String.format(
                      "Supports List.add(size() / 2, E) with existing element: "
                          + "size: %s, elements: %s",
                      collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
              supportsAddAtMiddleWithExistingElement)
          .forEachOrdered(tests::add);

      testsToAddTo.add(dynamicContainer("Supports List.add(int, E)", tests));
    }
  }

  static <E> void appendSupportsAddWithIndexWithNullElementsTests(
      TestListGenerator<E> generator,
      SampleElements<E> samples,
      Set<Feature<?>> features,
      Set<CollectionSize> supportedCollectionSizes,
      List<DynamicNode> testsToAddTo) {

    if (features.containsAll(
        Arrays.asList(ListFeature.SUPPORTS_ADD_WITH_INDEX, CollectionFeature.ALLOWS_NULL_VALUES))) {

      List<DynamicTest> tests = new ArrayList<>();

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
          .forEachOrdered(tests::add);

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
          .forEachOrdered(tests::add);

      ThrowingConsumer<CollectionSize> supportsAddAtMiddleWithNewNullElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);
            int middleIndex = list.size() / 2;

            list.add(middleIndex, null);
            Iterable<E> expected =
                insert(collectionSizeToElements(collectionSize, samples), middleIndex, null);
            assertIterableEquals(
                expected,
                list,
                () ->
                    String.format(
                        "Not true that %s was inserted at index %s of list, or that elements in "
                            + "list are in expected order",
                        quote("null"), middleIndex));
          };

      DynamicTest.stream(
              minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
              collectionSize ->
                  String.format(
                      "Supports List.add(size() / 2, E) with new null element: "
                          + "size: %s, elements: %s",
                      collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
              supportsAddAtMiddleWithNewNullElement)
          .forEachOrdered(tests::add);

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
          .forEachOrdered(tests::add);

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
                      "Supports List.add(size(), E) with existing null element: "
                          + "size: %s, elements: %s",
                      collectionSize.size(),
                      Arrays.toString(newArrayWithNullElementInMiddle(samples, collectionSize))),
              supportsAddAtEndWithExistingNullElement)
          .forEachOrdered(tests::add);

      ThrowingConsumer<CollectionSize> supportsAddAtMiddleWithExistingNullElement =
          collectionSize -> {
            List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);
            int middleIndex = list.size() / 2;

            list.add(middleIndex, null);
            Iterable<E> expected =
                insert(
                    Arrays.asList(newArrayWithNullElementInMiddle(samples, collectionSize)),
                    middleIndex,
                    null);
            assertIterableEquals(
                expected,
                list,
                () ->
                    String.format(
                        "Not true that %s was inserted at index %s of list, or that elements in "
                            + "list are in expected order",
                        quote("null"), middleIndex));
          };

      DynamicTest.stream(
              minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
              collectionSize ->
                  String.format(
                      "Supports List.add(size() / 2, E) with existing null element: "
                          + "size: %s, elements: %s",
                      collectionSize.size(),
                      Arrays.toString(newArrayWithNullElementInMiddle(samples, collectionSize))),
              supportsAddAtMiddleWithExistingNullElement)
          .forEachOrdered(tests::add);

      testsToAddTo.add(dynamicContainer("Supports List.add(int, E) with null element", tests));
    }
  }

  // TODO: See if there is a way of refactoring this method to make it more compact. Maybe split
  // into two methods: one for not supporting non-null elements, and one for not supporting null
  // elements.
  static <E> void appendDoesNotSupportAddWithIndexTests(
      TestListGenerator<E> generator,
      SampleElements<E> samples,
      Set<Feature<?>> features,
      Set<CollectionSize> supportedCollectionSizes,
      List<DynamicNode> testsToAddTo) {

    if (!features.contains(ListFeature.SUPPORTS_ADD_WITH_INDEX)) {

      E e0 = samples.e0();
      E e3 = samples.e3();

      String message = "Not true that list.add(%s) threw UnsupportedOperationException";

      List<DynamicTest> tests = new ArrayList<>();

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
          .forEachOrdered(tests::add);

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
                      "Does not support List.add(0, E) with existing element: "
                          + "size: %s, elements: %s",
                      collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAddAtStartWithExistingElement)
          .forEachOrdered(tests::add);

      ThrowingConsumer<CollectionSize> doesNotSupportAddAtStartWithNewNullElement =
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
                      "Does not support List.add(0, E) with new null element: "
                          + "size: %s, elements: %s",
                      collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAddAtStartWithNewNullElement)
          .forEachOrdered(tests::add);

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
                      "Does not support List.add(0, E) with existing null element: "
                          + "size: %s, elements: %s",
                      collectionSize.size(),
                      Arrays.toString(newArrayWithNullElementInMiddle(samples, collectionSize))),
              doesNotSupportAddAtStartWithExistingNullElement)
          .forEachOrdered(tests::add);

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
                      "Does not support List.add(size(), E) with new element: "
                          + "size: %s, elements: %s",
                      collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAddAtEndWithNewElement)
          .forEachOrdered(tests::add);

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
                      "Does not support List.add(size(), E) with existing element: "
                          + "size: %s, elements: %s",
                      collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAddAtEndWithExistingElement)
          .forEachOrdered(tests::add);

      ThrowingConsumer<CollectionSize> doesNotSupportAddAtEndWithNewNullElement =
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
                      "Does not support List.add(size(), E) with new null element: "
                          + "size: %s, elements: %s",
                      collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAddAtEndWithNewNullElement)
          .forEachOrdered(tests::add);

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
                      "Does not support List.add(size(), E) with existing null element: "
                          + "size: %s, elements: %s",
                      collectionSize.size(),
                      Arrays.toString(newArrayWithNullElementInMiddle(samples, collectionSize))),
              doesNotSupportAddAtEndWithExistingNullElement)
          .forEachOrdered(tests::add);

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
                      "Does not support List.add(size() / 2, E) with new element: "
                          + "size: %s, elements: %s",
                      collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAddAtMiddleWithNewElement)
          .forEachOrdered(tests::add);

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
                      "Does not support List.add(size() / 2, E) with existing element: "
                          + "size: %s, elements: %s",
                      collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAddAtMiddleWithExistingElement)
          .forEachOrdered(tests::add);

      ThrowingConsumer<CollectionSize> doesNotSupportAddAtMiddleWithNewNullElement =
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
                      "Does not support List.add(size() / 2, E) with new null element: "
                          + "size: %s, elements: %s",
                      collectionSize.size(), collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAddAtMiddleWithNewNullElement)
          .forEachOrdered(tests::add);

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
                      "Does not support List.add(size() / 2, E) with existing null element: "
                          + "size: %s, elements: %s",
                      collectionSize.size(),
                      Arrays.toString(newArrayWithNullElementInMiddle(samples, collectionSize))),
              doesNotSupportAddAtMiddleWithExistingNullElement)
          .forEachOrdered(tests::add);

      testsToAddTo.add(dynamicContainer("Does not support List.add(int, E)", tests));
    }
  }
}

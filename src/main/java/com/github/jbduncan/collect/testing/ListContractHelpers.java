package com.github.jbduncan.collect.testing;

import static com.github.jbduncan.collect.testing.Helpers.append;
import static com.github.jbduncan.collect.testing.Helpers.collectionSizeToElements;
import static com.github.jbduncan.collect.testing.Helpers.insert;
import static com.github.jbduncan.collect.testing.Helpers.minus;
import static com.github.jbduncan.collect.testing.Helpers.newArrayWithNullElementInMiddle;
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
                () ->
                    String.format(
                        ListContractConstants.FORMAT_NOT_TRUE_THAT_LIST_ADD_RETURNED_TRUE,
                        quote(e3)));
            List<E> expected = append(collectionSizeToElements(collectionSize, samples), e3);
            assertIterableEquals(
                expected,
                list,
                () ->
                    String.format(
                        ListContractConstants.FORMAT_NOT_TRUE_THAT_LIST_WAS_APPENDED, quote(e3)));
          };

      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_E_WITH_NEW_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              supportsAddWithNewElement)
          .forEachOrdered(tests::add);

      ThrowingConsumer<CollectionSize> supportsAddWithExistingElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);

            assertTrue(
                list.add(e0),
                () ->
                    String.format(
                        ListContractConstants.FORMAT_NOT_TRUE_THAT_LIST_ADD_RETURNED_TRUE,
                        quote(e0)));
            List<E> expected = append(collectionSizeToElements(collectionSize, samples), e0);
            assertIterableEquals(
                expected,
                list,
                () ->
                    String.format(
                        ListContractConstants.FORMAT_NOT_TRUE_THAT_LIST_WAS_APPENDED, quote(e0)));
          };

      DynamicTest.stream(
              minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_E_WITH_EXISTING_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              supportsAddWithExistingElement)
          .forEachOrdered(tests::add);

      testsToAddTo.add(dynamicContainer(ListContractConstants.SUPPORTS_LIST_ADD_E, tests));
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

            assertTrue(
                list.add(null), ListContractConstants.NOT_TRUE_THAT_LIST_ADD_NULL_RETURNED_TRUE);
            List<E> expected = append(collectionSizeToElements(collectionSize, samples), null);
            assertIterableEquals(
                expected, list, ListContractConstants.NOT_TRUE_THAT_LIST_WAS_APPENDED_WITH_NULL);
          };

      List<DynamicTest> tests = new ArrayList<>();

      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_E_WITH_NEW_NULL_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              supportsAddWithNewNullElement)
          .forEachOrdered(tests::add);

      ThrowingConsumer<CollectionSize> supportsAddWithExistingNullElement =
          collectionSize -> {
            List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

            assertTrue(
                list.add(null), ListContractConstants.NOT_TRUE_THAT_LIST_ADD_NULL_RETURNED_TRUE);
            List<E> expected =
                append(asList(newArrayWithNullElementInMiddle(samples, collectionSize)), null);
            assertIterableEquals(
                expected, list, ListContractConstants.NOT_TRUE_THAT_LIST_WAS_APPENDED_WITH_NULL);
          };

      DynamicTest.stream(
              minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_E_WITH_EXISTING_NULL_ELEMENT,
                      collectionSize.size(),
                      Arrays.toString(newArrayWithNullElementInMiddle(samples, collectionSize))),
              supportsAddWithExistingNullElement)
          .forEachOrdered(tests::add);

      testsToAddTo.add(
          dynamicContainer(ListContractConstants.SUPPORTS_LIST_ADD_E_WITH_NULL_ELEMENT, tests));
    }
  }

  static <E> void appendDoesNotSupportAddTests(
      TestListGenerator<E> generator,
      SampleElements<E> samples,
      Set<Feature<?>> features,
      Set<CollectionSize> supportedCollectionSizes,
      List<DynamicNode> testsToAddTo) {

    if (!features.contains(CollectionFeature.SUPPORTS_ADD)) {
      E e0 = samples.e0();
      E e3 = samples.e3();

      List<DynamicTest> tests = new ArrayList<>();

      ThrowingConsumer<CollectionSize> doesNotSupportAddWithNewElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);

            assertThrows(
                UnsupportedOperationException.class,
                () -> list.add(e3),
                () ->
                    String.format(
                        ListContractConstants
                            .FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION,
                        quote(e3)));
            assertIterableEquals(
                collectionSizeToElements(collectionSize, samples),
                list,
                ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
          };

      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_E_WITH_NEW_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAddWithNewElement)
          .forEachOrdered(tests::add);

      ThrowingConsumer<CollectionSize> doesNotSupportAddWithExistingElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);

            assertThrows(
                UnsupportedOperationException.class,
                () -> list.add(e0),
                () ->
                    String.format(
                        ListContractConstants
                            .FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION,
                        quote(e0)));
            assertIterableEquals(
                collectionSizeToElements(collectionSize, samples),
                list,
                ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
          };

      DynamicTest.stream(
              minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants
                          .FORMAT_DOES_NOT_SUPPORT_LIST_ADD_E_WITH_EXISTING_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAddWithExistingElement)
          .forEachOrdered(tests::add);

      ThrowingConsumer<CollectionSize> doesNotSupportAddWithNewNullElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);

            assertThrows(
                UnsupportedOperationException.class,
                () -> list.add(null),
                () ->
                    String.format(
                        ListContractConstants
                            .FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION,
                        ListContractConstants.NULL));
            assertIterableEquals(
                collectionSizeToElements(collectionSize, samples),
                list,
                ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
          };

      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants
                          .FORMAT_DOES_NOT_SUPPORT_LIST_ADD_E_WITH_NEW_NULL_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAddWithNewNullElement)
          .forEachOrdered(tests::add);

      ThrowingConsumer<CollectionSize> doesNotSupportAddAtStartWithExistingNullElement =
          collectionSize -> {
            List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

            assertThrows(
                UnsupportedOperationException.class,
                () -> list.add(null),
                () ->
                    String.format(
                        ListContractConstants
                            .FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION,
                        ListContractConstants.NULL));
            assertIterableEquals(
                asList(newArrayWithNullElementInMiddle(samples, collectionSize)),
                list,
                ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
          };

      DynamicTest.stream(
              minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants
                          .FORMAT_DOES_NOT_SUPPORT_LIST_ADD_E_WITH_EXISTING_NULL_ELEMENT,
                      collectionSize.size(),
                      Arrays.toString(newArrayWithNullElementInMiddle(samples, collectionSize))),
              doesNotSupportAddAtStartWithExistingNullElement)
          .forEachOrdered(tests::add);

      testsToAddTo.add(dynamicContainer(ListContractConstants.DOES_NOT_SUPPORT_LIST_ADD_E, tests));
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
                () ->
                    String.format(
                        ListContractConstants.FORMAT_NOT_TRUE_THAT_LIST_WAS_PREPENDED, quote(e3)));
          };

      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_0_E_WITH_NEW_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
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
                () ->
                    String.format(
                        ListContractConstants.FORMAT_NOT_TRUE_THAT_LIST_WAS_APPENDED, quote(e3)));
          };

      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_SIZE_E_WITH_NEW_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              supportsAddAtEndWithNewElement)
          .forEachOrdered(tests::add);

      ThrowingConsumer<CollectionSize> supportsAddAtMiddleWithNewElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);
            int middleIndex = middleIndex(list);

            list.add(middleIndex, e3);
            Iterable<E> expected =
                insert(collectionSizeToElements(collectionSize, samples), middleIndex, e3);
            assertIterableEquals(
                expected,
                list,
                () ->
                    String.format(
                        ListContractConstants
                            .FORMAT_NOT_TRUE_WAS_INSERTED_AT_INDEX_OR_IN_EXPECTED_ORDER,
                        quote(e3),
                        middleIndex));
          };

      DynamicTest.stream(
              minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants
                          .FORMAT_SUPPORTS_LIST_ADD_MIDDLE_INDEX_E_WITH_NEW_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
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
                () ->
                    String.format(
                        ListContractConstants.FORMAT_NOT_TRUE_THAT_LIST_WAS_PREPENDED, quote(e0)));
          };

      DynamicTest.stream(
              minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_0_E_WITH_EXISTING_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
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
                () ->
                    String.format(
                        ListContractConstants.FORMAT_NOT_TRUE_THAT_LIST_WAS_APPENDED, quote(e0)));
          };

      DynamicTest.stream(
              minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_SIZE_E_WITH_EXISTING_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              supportsAddAtEndWithExistingElement)
          .forEachOrdered(tests::add);

      ThrowingConsumer<CollectionSize> supportsAddAtMiddleWithExistingElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);
            int middleIndex = middleIndex(list);

            list.add(middleIndex, e0);
            Iterable<E> expected =
                insert(collectionSizeToElements(collectionSize, samples), middleIndex, e0);
            assertIterableEquals(
                expected,
                list,
                () ->
                    String.format(
                        ListContractConstants
                            .FORMAT_NOT_TRUE_WAS_INSERTED_AT_INDEX_OR_IN_EXPECTED_ORDER,
                        quote(e0),
                        middleIndex));
          };

      DynamicTest.stream(
              minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants
                          .FORMAT_SUPPORTS_LIST_ADD_MIDDLE_INDEX_E_WITH_EXISTING_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              supportsAddAtMiddleWithExistingElement)
          .forEachOrdered(tests::add);

      testsToAddTo.add(dynamicContainer(ListContractConstants.SUPPORTS_LIST_ADD_INT_E, tests));
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
            assertIterableEquals(
                expected, list, ListContractConstants.NOT_TRUE_THAT_LIST_WAS_PREPENDED_WITH_NULL);
          };

      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_0_E_WITH_NEW_NULL_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              supportsAddAtStartWithNewNullElement)
          .forEachOrdered(tests::add);

      ThrowingConsumer<CollectionSize> supportsAddAtEndWithNewNullElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);

            list.add(list.size(), null);
            List<E> expected = append(collectionSizeToElements(collectionSize, samples), null);
            assertIterableEquals(
                expected, list, ListContractConstants.NOT_TRUE_THAT_LIST_WAS_APPENDED_WITH_NULL);
          };

      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_SIZE_E_WITH_NEW_NULL_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              supportsAddAtEndWithNewNullElement)
          .forEachOrdered(tests::add);

      ThrowingConsumer<CollectionSize> supportsAddAtMiddleWithNewNullElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);
            int middleIndex = middleIndex(list);

            list.add(middleIndex, null);
            Iterable<E> expected =
                insert(collectionSizeToElements(collectionSize, samples), middleIndex, null);
            assertIterableEquals(
                expected,
                list,
                () ->
                    String.format(
                        ListContractConstants
                            .FORMAT_NOT_TRUE_WAS_INSERTED_AT_INDEX_OR_IN_EXPECTED_ORDER,
                        quote(ListContractConstants.NULL),
                        middleIndex));
          };

      DynamicTest.stream(
              minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants
                          .FORMAT_SUPPORTS_LIST_ADD_MIDDLE_INDEX_E_WITH_NEW_NULL_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              supportsAddAtMiddleWithNewNullElement)
          .forEachOrdered(tests::add);

      ThrowingConsumer<CollectionSize> supportsAddAtStartWithExistingNullElement =
          collectionSize -> {
            List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

            list.add(0, null);
            List<E> expected =
                prepend(null, asList(newArrayWithNullElementInMiddle(samples, collectionSize)));
            assertIterableEquals(
                expected, list, ListContractConstants.NOT_TRUE_THAT_LIST_WAS_PREPENDED_WITH_NULL);
          };

      DynamicTest.stream(
              minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_0_E_WITH_EXISTING_NULL_ELEMENT,
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
            assertIterableEquals(
                expected, list, ListContractConstants.NOT_TRUE_THAT_LIST_WAS_APPENDED_WITH_NULL);
          };

      DynamicTest.stream(
              minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants
                          .FORMAT_SUPPORTS_LIST_ADD_SIZE_E_WITH_EXISTING_NULL_ELEMENT,
                      collectionSize.size(),
                      Arrays.toString(newArrayWithNullElementInMiddle(samples, collectionSize))),
              supportsAddAtEndWithExistingNullElement)
          .forEachOrdered(tests::add);

      ThrowingConsumer<CollectionSize> supportsAddAtMiddleWithExistingNullElement =
          collectionSize -> {
            List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);
            int middleIndex = middleIndex(list);

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
                        ListContractConstants
                            .FORMAT_NOT_TRUE_WAS_INSERTED_AT_INDEX_OR_IN_EXPECTED_ORDER,
                        quote(ListContractConstants.NULL),
                        middleIndex));
          };

      DynamicTest.stream(
              minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants
                          .FORMAT_SUPPORTS_LIST_ADD_MIDDLE_INDEX_E_WITH_EXISTING_NULL_ELEMENT,
                      collectionSize.size(),
                      Arrays.toString(newArrayWithNullElementInMiddle(samples, collectionSize))),
              supportsAddAtMiddleWithExistingNullElement)
          .forEachOrdered(tests::add);

      testsToAddTo.add(
          dynamicContainer(ListContractConstants.SUPPORTS_LIST_ADD_INT_E_WITH_NULL_ELEMENT, tests));
    }
  }

  static <E> void appendDoesNotSupportAddWithIndexTests(
      TestListGenerator<E> generator,
      SampleElements<E> samples,
      Set<Feature<?>> features,
      Set<CollectionSize> supportedCollectionSizes,
      List<DynamicNode> testsToAddTo) {

    if (!features.contains(ListFeature.SUPPORTS_ADD_WITH_INDEX)) {
      E e0 = samples.e0();
      E e3 = samples.e3();

      List<DynamicTest> tests = new ArrayList<>();

      ThrowingConsumer<CollectionSize> doesNotSupportAddAtStartWithNewElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);

            assertThrows(
                UnsupportedOperationException.class,
                () -> list.add(0, e3),
                () ->
                    String.format(
                        ListContractConstants
                            .FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION,
                        quote(e3)));
            assertIterableEquals(
                collectionSizeToElements(collectionSize, samples),
                list,
                ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
          };

      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_0_E_WITH_NEW_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAddAtStartWithNewElement)
          .forEachOrdered(tests::add);

      ThrowingConsumer<CollectionSize> doesNotSupportAddAtStartWithExistingElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);

            assertThrows(
                UnsupportedOperationException.class,
                () -> list.add(0, e0),
                () ->
                    String.format(
                        ListContractConstants
                            .FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION,
                        quote(e0)));
            assertIterableEquals(
                collectionSizeToElements(collectionSize, samples),
                list,
                ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
          };

      DynamicTest.stream(
              minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants
                          .FORMAT_DOES_NOT_SUPPORT_LIST_ADD_0_E_WITH_EXISTING_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAddAtStartWithExistingElement)
          .forEachOrdered(tests::add);

      ThrowingConsumer<CollectionSize> doesNotSupportAddAtEndWithNewElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);

            assertThrows(
                UnsupportedOperationException.class,
                () -> list.add(list.size(), e3),
                () ->
                    String.format(
                        ListContractConstants
                            .FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION,
                        quote(e3)));
            assertIterableEquals(
                collectionSizeToElements(collectionSize, samples),
                list,
                ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
          };

      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants
                          .FORMAT_DOES_NOT_SUPPORT_LIST_ADD_SIZE_E_WITH_NEW_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAddAtEndWithNewElement)
          .forEachOrdered(tests::add);

      ThrowingConsumer<CollectionSize> doesNotSupportAddAtEndWithExistingElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);

            assertThrows(
                UnsupportedOperationException.class,
                () -> list.add(list.size(), e0),
                () ->
                    String.format(
                        ListContractConstants
                            .FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION,
                        quote(e0)));
            assertIterableEquals(
                collectionSizeToElements(collectionSize, samples),
                list,
                ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
          };

      DynamicTest.stream(
              minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants
                          .FORMAT_DOES_NOT_SUPPORT_LIST_ADD_SIZE_E_WITH_EXISTING_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAddAtEndWithExistingElement)
          .forEachOrdered(tests::add);

      ThrowingConsumer<CollectionSize> doesNotSupportAddAtMiddleWithNewElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);

            assertThrows(
                UnsupportedOperationException.class,
                () -> list.add(middleIndex(list), e3),
                () ->
                    String.format(
                        ListContractConstants
                            .FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION,
                        quote(e3)));
            assertIterableEquals(
                collectionSizeToElements(collectionSize, samples),
                list,
                ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
          };

      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants
                          .FORMAT_DOES_NOT_SUPPORT_LIST_ADD_MIDDLE_INDEX_E_WITH_NEW_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAddAtMiddleWithNewElement)
          .forEachOrdered(tests::add);

      ThrowingConsumer<CollectionSize> doesNotSupportAddAtMiddleWithExistingElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);

            assertThrows(
                UnsupportedOperationException.class,
                () -> list.add(middleIndex(list), e0),
                () ->
                    String.format(
                        ListContractConstants
                            .FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION,
                        quote(e0)));
            assertIterableEquals(
                collectionSizeToElements(collectionSize, samples),
                list,
                ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
          };

      DynamicTest.stream(
              minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants
                          .FORMAT_DOES_NOT_SUPPORT_LIST_ADD_MIDDLE_INDEX_E_WITH_EXISTING_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAddAtMiddleWithExistingElement)
          .forEachOrdered(tests::add);

      testsToAddTo.add(
          dynamicContainer(ListContractConstants.DOES_NOT_SUPPORT_LIST_ADD_INT_E, tests));
    }
  }

  static <E> void appendDoesNotSupportAddWithIndexWithNullElementsTests(
      TestListGenerator<E> generator,
      SampleElements<E> samples,
      Set<Feature<?>> features,
      Set<CollectionSize> supportedCollectionSizes,
      List<DynamicNode> testsToAddTo) {

    if (!features.contains(ListFeature.SUPPORTS_ADD_WITH_INDEX)) {
      String message =
          ListContractConstants.FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION;

      List<DynamicTest> tests = new ArrayList<>();

      ThrowingConsumer<CollectionSize> doesNotSupportAddAtStartWithNewNullElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);

            assertThrows(
                UnsupportedOperationException.class,
                () -> list.add(0, null),
                () -> String.format(message, ListContractConstants.NULL));
            assertIterableEquals(
                collectionSizeToElements(collectionSize, samples),
                list,
                ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
          };

      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants
                          .FORMAT_DOES_NOT_SUPPORT_LIST_ADD_0_E_WITH_NEW_NULL_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAddAtStartWithNewNullElement)
          .forEachOrdered(tests::add);

      ThrowingConsumer<CollectionSize> doesNotSupportAddAtStartWithExistingNullElement =
          collectionSize -> {
            List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

            assertThrows(
                UnsupportedOperationException.class,
                () -> list.add(0, null),
                () -> String.format(message, ListContractConstants.NULL));
            assertIterableEquals(
                asList(newArrayWithNullElementInMiddle(samples, collectionSize)),
                list,
                ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
          };

      DynamicTest.stream(
              minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants
                          .FORMAT_DOES_NOT_SUPPORT_LIST_ADD_0_E_WITH_EXISTING_NULL_ELEMENT,
                      collectionSize.size(),
                      Arrays.toString(newArrayWithNullElementInMiddle(samples, collectionSize))),
              doesNotSupportAddAtStartWithExistingNullElement)
          .forEachOrdered(tests::add);

      ThrowingConsumer<CollectionSize> doesNotSupportAddAtEndWithNewNullElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);

            assertThrows(
                UnsupportedOperationException.class,
                () -> list.add(list.size(), null),
                () -> String.format(message, ListContractConstants.NULL));
            assertIterableEquals(
                collectionSizeToElements(collectionSize, samples),
                list,
                ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
          };

      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants
                          .FORMAT_DOES_NOT_SUPPORT_LIST_ADD_SIZE_E_WITH_NEW_NULL_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAddAtEndWithNewNullElement)
          .forEachOrdered(tests::add);

      ThrowingConsumer<CollectionSize> doesNotSupportAddAtEndWithExistingNullElement =
          collectionSize -> {
            List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

            assertThrows(
                UnsupportedOperationException.class,
                () -> list.add(list.size(), null),
                () -> String.format(message, ListContractConstants.NULL));
            assertIterableEquals(
                asList(newArrayWithNullElementInMiddle(samples, collectionSize)),
                list,
                ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
          };

      DynamicTest.stream(
              minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants
                          .FORMAT_DOES_NOT_SUPPORT_LIST_ADD_SIZE_E_WITH_EXISTING_NULL_ELEMENT,
                      collectionSize.size(),
                      Arrays.toString(newArrayWithNullElementInMiddle(samples, collectionSize))),
              doesNotSupportAddAtEndWithExistingNullElement)
          .forEachOrdered(tests::add);

      ThrowingConsumer<CollectionSize> doesNotSupportAddAtMiddleWithNewNullElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);

            assertThrows(
                UnsupportedOperationException.class,
                () -> list.add(middleIndex(list), null),
                () -> String.format(message, ListContractConstants.NULL));
            assertIterableEquals(
                collectionSizeToElements(collectionSize, samples),
                list,
                ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
          };

      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants
                          .FORMAT_DOES_NOT_SUPPORT_LIST_ADD_MIDDLE_INDEX_E_WITH_NEW_NULL_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAddAtMiddleWithNewNullElement)
          .forEachOrdered(tests::add);

      ThrowingConsumer<CollectionSize> doesNotSupportAddAtMiddleWithExistingNullElement =
          collectionSize -> {
            List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

            assertThrows(
                UnsupportedOperationException.class,
                () -> list.add(middleIndex(list), null),
                () -> String.format(message, ListContractConstants.NULL));
            assertIterableEquals(
                asList(newArrayWithNullElementInMiddle(samples, collectionSize)),
                list,
                ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
          };

      DynamicTest.stream(
              minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants
                          .FORMAT_DOES_NOT_SUPPORT_LIST_ADD_MIDDLE_INDEX_E_WITH_EXISTING_NULL_ELEMENT,
                      collectionSize.size(),
                      Arrays.toString(newArrayWithNullElementInMiddle(samples, collectionSize))),
              doesNotSupportAddAtMiddleWithExistingNullElement)
          .forEachOrdered(tests::add);

      testsToAddTo.add(
          dynamicContainer(ListContractConstants.DOES_NOT_SUPPORT_LIST_ADD_INT_E, tests));
    }
  }

  private static <E> List<E> newListToTest(
      TestListGenerator<E> listGenerator, CollectionSize collectionSize) {
    SampleElements<E> samples = listGenerator.samples();
    return listGenerator.create(collectionSizeToElements(collectionSize, samples).toArray());
  }

  private static <E> List<E> newListToTestWithNullElementInMiddle(
      TestListGenerator<E> listGenerator, CollectionSize collectionSize) {
    Object[] elements = newArrayWithNullElementInMiddle(listGenerator.samples(), collectionSize);
    return listGenerator.create(elements);
  }

  private static int middleIndex(List<?> list) {
    return list.size() / 2;
  }
}

package com.github.jbduncan.collect.testing;

import static com.github.jbduncan.collect.testing.Helpers.append;
import static com.github.jbduncan.collect.testing.Helpers.collectionSizeToElements;
import static com.github.jbduncan.collect.testing.Helpers.extractConcreteSizes;
import static com.github.jbduncan.collect.testing.Helpers.insert;
import static com.github.jbduncan.collect.testing.Helpers.minus;
import static com.github.jbduncan.collect.testing.Helpers.newArrayWithNullElementInMiddle;
import static com.github.jbduncan.collect.testing.Helpers.prepend;
import static com.github.jbduncan.collect.testing.Helpers.quote;
import static com.github.jbduncan.collect.testing.ListContractHelpers.middleIndex;
import static com.github.jbduncan.collect.testing.ListContractHelpers.newListToTest;
import static com.github.jbduncan.collect.testing.ListContractHelpers.newListToTestWithNullElementInMiddle;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.ThrowingConsumer;

final class ListAddWithIndexTester<E> {
  private final TestListGenerator<E> generator;
  private final SampleElements<E> samples;
  private final Set<Feature<?>> features;
  private final Set<CollectionSize> supportedCollectionSizes;

  private ListAddWithIndexTester(TestListGenerator<E> testListGenerator, Set<Feature<?>> features) {
    this.generator = requireNonNull(testListGenerator, "testListGenerator");
    this.samples = requireNonNull(testListGenerator.samples(), "samples");
    this.features = requireNonNull(features, "features");
    this.supportedCollectionSizes = extractConcreteSizes(features);
  }

  static <E> Builder<E> builder() {
    return new Builder<>();
  }

  static class Builder<E> {
    private Builder() {}

    private TestListGenerator<E> testListGenerator;
    private Set<Feature<?>> features;

    Builder<E> testListGenerator(TestListGenerator<E> testListGenerator) {
      this.testListGenerator = testListGenerator;
      return this;
    }

    public Builder<E> features(Set<Feature<?>> features) {
      this.features = features;
      return this;
    }

    ListAddWithIndexTester<E> build() {
      return new ListAddWithIndexTester<>(testListGenerator, features);
    }
  }

  List<DynamicNode> dynamicTests() {
    List<DynamicNode> tests = new ArrayList<>();
    generateSupportsAddWithIndexTests(tests);
    generateSupportsAddWithIndexWithNullElementsTests(tests);
    generateDoesNotSupportAddWithIndexTests(tests);
    generateDoesNotSupportAddWithIndexWithNullElementsTests(tests);
    return Collections.unmodifiableList(tests);
  }

  private void generateSupportsAddWithIndexTests(List<DynamicNode> tests) {
    if (features.contains(ListFeature.SUPPORTS_ADD_WITH_INDEX)) {
      E e0 = samples.e0();
      E e3 = samples.e3();

      List<DynamicTest> subTests = new ArrayList<>();

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
          .forEachOrdered(subTests::add);

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
          .forEachOrdered(subTests::add);

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
          .forEachOrdered(subTests::add);

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
          .forEachOrdered(subTests::add);

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
          .forEachOrdered(subTests::add);

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
          .forEachOrdered(subTests::add);

      tests.add(dynamicContainer(ListContractConstants.SUPPORTS_LIST_ADD_INT_E, subTests));
    }
  }

  private void generateSupportsAddWithIndexWithNullElementsTests(List<DynamicNode> tests) {
    if (features.containsAll(
        Arrays.asList(ListFeature.SUPPORTS_ADD_WITH_INDEX, CollectionFeature.ALLOWS_NULL_VALUES))) {
      List<DynamicTest> subTests = new ArrayList<>();

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
          .forEachOrdered(subTests::add);

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
          .forEachOrdered(subTests::add);

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
          .forEachOrdered(subTests::add);

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
          .forEachOrdered(subTests::add);

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
          .forEachOrdered(subTests::add);

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
          .forEachOrdered(subTests::add);

      tests.add(
          dynamicContainer(
              ListContractConstants.SUPPORTS_LIST_ADD_INT_E_WITH_NULL_ELEMENT, subTests));
    }
  }

  private void generateDoesNotSupportAddWithIndexTests(List<DynamicNode> tests) {
    if (!features.contains(ListFeature.SUPPORTS_ADD_WITH_INDEX)) {
      E e0 = samples.e0();
      E e3 = samples.e3();

      List<DynamicTest> subTests = new ArrayList<>();

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
          .forEachOrdered(subTests::add);

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
          .forEachOrdered(subTests::add);

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
          .forEachOrdered(subTests::add);

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
          .forEachOrdered(subTests::add);

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
          .forEachOrdered(subTests::add);

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
          .forEachOrdered(subTests::add);

      // TODO: This `DynamicContainer` is duplicated in
      // `generateDoesNotSupportAddWithIndexWithNullElementsTests`, so merge that method with this
      // method again.
      tests.add(dynamicContainer(ListContractConstants.DOES_NOT_SUPPORT_LIST_ADD_INT_E, subTests));
    }
  }

  private void generateDoesNotSupportAddWithIndexWithNullElementsTests(List<DynamicNode> tests) {
    if (!features.contains(ListFeature.SUPPORTS_ADD_WITH_INDEX)) {
      // TODO: Inline this message.
      String message =
          ListContractConstants.FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION;

      List<DynamicTest> subTests = new ArrayList<>();

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
          .forEachOrdered(subTests::add);

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
          .forEachOrdered(subTests::add);

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
          .forEachOrdered(subTests::add);

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
          .forEachOrdered(subTests::add);

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
          .forEachOrdered(subTests::add);

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
          .forEachOrdered(subTests::add);

      tests.add(dynamicContainer(ListContractConstants.DOES_NOT_SUPPORT_LIST_ADD_INT_E, subTests));
    }
  }
}

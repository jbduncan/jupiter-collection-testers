/*
 * Copyright 2018 the junit-jupiter-collection-testers authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jbduncan.collect.testing;

import static com.github.jbduncan.collect.testing.Helpers.append;
import static com.github.jbduncan.collect.testing.Helpers.collectionSizeToElements;
import static com.github.jbduncan.collect.testing.Helpers.extractConcreteSizes;
import static com.github.jbduncan.collect.testing.Helpers.insert;
import static com.github.jbduncan.collect.testing.Helpers.minus;
import static com.github.jbduncan.collect.testing.Helpers.newCollectionWithNullElementInMiddle;
import static com.github.jbduncan.collect.testing.Helpers.prepend;
import static com.github.jbduncan.collect.testing.Helpers.quote;
import static com.github.jbduncan.collect.testing.ListContractHelpers.middleIndex;
import static com.github.jbduncan.collect.testing.ListContractHelpers.newListToTest;
import static com.github.jbduncan.collect.testing.ListContractHelpers.newListToTestWithNullElementInMiddle;
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
    generateSupportsAddWithIndexButNotOnNullElementsTests(tests);
    generateDoesNotSupportAddWithIndexTests(tests);
    generateDoesNotSupportAddWithIndexWithNullElementsTests(tests);
    return Collections.unmodifiableList(tests);
  }

  private void generateSupportsAddWithIndexTests(List<DynamicNode> tests) {
    if (features.contains(ListFeature.SUPPORTS_ADD_WITH_INDEX)) {
      List<DynamicTest> subTests = new ArrayList<>();

      E newElement = samples.e3();
      appendAddAtStartTests(
          subTests,
          newElement,
          supportedCollectionSizes,
          ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_0_E_WITH_NEW_ELEMENT);
      appendAddAtEndTests(
          subTests,
          newElement,
          supportedCollectionSizes,
          ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_SIZE_E_WITH_NEW_ELEMENT);
      appendAddAtMiddleTests(
          subTests,
          newElement,
          minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO),
          ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_MIDDLE_INDEX_E_WITH_NEW_ELEMENT);

      E existingElement = samples.e0();
      appendAddAtStartTests(
          subTests,
          existingElement,
          minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO),
          ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_0_E_WITH_EXISTING_ELEMENT);
      appendAddAtEndTests(
          subTests,
          existingElement,
          minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO),
          ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_SIZE_E_WITH_EXISTING_ELEMENT);
      appendAddAtMiddleTests(
          subTests,
          existingElement,
          minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO),
          ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_MIDDLE_INDEX_E_WITH_EXISTING_ELEMENT);

      createMinus1IndexTests(subTests, IndexOutOfBoundsException.class);

      tests.add(dynamicContainer(ListContractConstants.SUPPORTS_LIST_ADD_INT_E, subTests));
    }
  }

  private void generateSupportsAddWithIndexButNotOnNullElementsTests(List<DynamicNode> tests) {
    if (features.contains(ListFeature.SUPPORTS_ADD_WITH_INDEX)
        && !features.contains(CollectionFeature.ALLOWS_NULL_VALUES)) {
      List<DynamicTest> subTests = new ArrayList<>();

      ThrowingConsumer<CollectionSize> doesNotSupportAddAtStartWithNewNullElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);

            assertThrows(
                UnsupportedOperationException.class,
                () -> list.add(0, null),
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
                          .FORMAT_DOES_NOT_SUPPORT_LIST_ADD_0_E_WITH_NEW_NULL_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAddAtStartWithNewNullElement)
          .forEachOrdered(subTests::add);

      ThrowingConsumer<CollectionSize> doesNotSupportAddAtEndWithNewNullElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);

            assertThrows(
                UnsupportedOperationException.class,
                () -> list.add(list.size(), null),
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
                          .FORMAT_DOES_NOT_SUPPORT_LIST_ADD_SIZE_E_WITH_NEW_NULL_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAddAtEndWithNewNullElement)
          .forEachOrdered(subTests::add);

      ThrowingConsumer<CollectionSize> doesNotSupportAddAtMiddleWithNewNullElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);

            assertThrows(
                UnsupportedOperationException.class,
                () -> list.add(middleIndex(list), null),
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
                          .FORMAT_DOES_NOT_SUPPORT_LIST_ADD_MIDDLE_INDEX_E_WITH_NEW_NULL_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAddAtMiddleWithNewNullElement)
          .forEachOrdered(subTests::add);

      tests.add(
          dynamicContainer(
              ListContractConstants.DOES_NOT_SUPPORT_LIST_ADD_INT_E_WITH_NEW_NULL_ELEMENT,
              subTests));
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
                prepend(null, newCollectionWithNullElementInMiddle(samples, collectionSize));
            assertIterableEquals(
                expected, list, ListContractConstants.NOT_TRUE_THAT_LIST_WAS_PREPENDED_WITH_NULL);
          };

      DynamicTest.stream(
              minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_0_E_WITH_EXISTING_NULL_ELEMENT,
                      collectionSize.size(),
                      newCollectionWithNullElementInMiddle(samples, collectionSize)),
              supportsAddAtStartWithExistingNullElement)
          .forEachOrdered(subTests::add);

      ThrowingConsumer<CollectionSize> supportsAddAtEndWithExistingNullElement =
          collectionSize -> {
            List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

            list.add(list.size(), null);
            List<E> expected =
                append(newCollectionWithNullElementInMiddle(samples, collectionSize), null);
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
                      newCollectionWithNullElementInMiddle(samples, collectionSize)),
              supportsAddAtEndWithExistingNullElement)
          .forEachOrdered(subTests::add);

      ThrowingConsumer<CollectionSize> supportsAddAtMiddleWithExistingNullElement =
          collectionSize -> {
            List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);
            int middleIndex = middleIndex(list);

            list.add(middleIndex, null);
            Iterable<E> expected =
                insert(
                    newCollectionWithNullElementInMiddle(samples, collectionSize),
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
                      newCollectionWithNullElementInMiddle(samples, collectionSize)),
              supportsAddAtMiddleWithExistingNullElement)
          .forEachOrdered(subTests::add);

      createMinus1IndexWithNullElementTests(subTests, IndexOutOfBoundsException.class);

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

      createMinus1IndexTests(subTests, UnsupportedOperationException.class);

      tests.add(dynamicContainer(ListContractConstants.DOES_NOT_SUPPORT_LIST_ADD_INT_E, subTests));
    }
  }

  private void generateDoesNotSupportAddWithIndexWithNullElementsTests(List<DynamicNode> tests) {
    if (!features.contains(ListFeature.SUPPORTS_ADD_WITH_INDEX)) {
      List<DynamicTest> subTests = new ArrayList<>();

      ThrowingConsumer<CollectionSize> doesNotSupportAddAtStartWithNewNullElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);

            assertThrows(
                UnsupportedOperationException.class,
                () -> list.add(0, null),
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
                () ->
                    String.format(
                        ListContractConstants
                            .FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION,
                        ListContractConstants.NULL));
            assertIterableEquals(
                newCollectionWithNullElementInMiddle(samples, collectionSize),
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
                      newCollectionWithNullElementInMiddle(samples, collectionSize)),
              doesNotSupportAddAtStartWithExistingNullElement)
          .forEachOrdered(subTests::add);

      ThrowingConsumer<CollectionSize> doesNotSupportAddAtEndWithNewNullElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);

            assertThrows(
                UnsupportedOperationException.class,
                () -> list.add(list.size(), null),
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
                () ->
                    String.format(
                        ListContractConstants
                            .FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION,
                        ListContractConstants.NULL));
            assertIterableEquals(
                newCollectionWithNullElementInMiddle(samples, collectionSize),
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
                      newCollectionWithNullElementInMiddle(samples, collectionSize)),
              doesNotSupportAddAtEndWithExistingNullElement)
          .forEachOrdered(subTests::add);

      ThrowingConsumer<CollectionSize> doesNotSupportAddAtMiddleWithNewNullElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);

            assertThrows(
                UnsupportedOperationException.class,
                () -> list.add(middleIndex(list), null),
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
                () ->
                    String.format(
                        ListContractConstants
                            .FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION,
                        ListContractConstants.NULL));
            assertIterableEquals(
                newCollectionWithNullElementInMiddle(samples, collectionSize),
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
                      newCollectionWithNullElementInMiddle(samples, collectionSize)),
              doesNotSupportAddAtMiddleWithExistingNullElement)
          .forEachOrdered(subTests::add);

      createMinus1IndexWithNullElementTests(subTests, UnsupportedOperationException.class);

      tests.add(
          dynamicContainer(
              ListContractConstants.DOES_NOT_SUPPORT_LIST_ADD_INT_E_WITH_NULL_ELEMENT, subTests));
    }
  }

  private void appendAddAtStartTests(
      List<DynamicTest> subTests,
      E elementToAdd,
      Set<CollectionSize> supportedCollectionSizes,
      String displayNameFormat) {
    ThrowingConsumer<CollectionSize> supportsAddAtStartWithSampleElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          list.add(0, elementToAdd);
          List<E> expected =
              prepend(elementToAdd, collectionSizeToElements(collectionSize, samples));
          assertIterableEquals(
              expected,
              list,
              () ->
                  String.format(
                      ListContractConstants.FORMAT_NOT_TRUE_THAT_LIST_WAS_PREPENDED,
                      quote(elementToAdd)));
        };

    DynamicTest.stream(
            supportedCollectionSizes.iterator(),
            collectionSize ->
                String.format(
                    displayNameFormat,
                    collectionSize.size(),
                    collectionSizeToElements(collectionSize, samples)),
            supportsAddAtStartWithSampleElement)
        .forEachOrdered(subTests::add);
  }

  private void appendAddAtEndTests(
      List<DynamicTest> subTests,
      E elementToAdd,
      Set<CollectionSize> supportedCollectionSizes,
      String displayNameFormat) {
    ThrowingConsumer<CollectionSize> supportsAddAtEndWithSampleElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          list.add(list.size(), elementToAdd);
          List<E> expected =
              append(collectionSizeToElements(collectionSize, samples), elementToAdd);
          assertIterableEquals(
              expected,
              list,
              () ->
                  String.format(
                      ListContractConstants.FORMAT_NOT_TRUE_THAT_LIST_WAS_APPENDED,
                      quote(elementToAdd)));
        };

    DynamicTest.stream(
            supportedCollectionSizes.iterator(),
            collectionSize ->
                String.format(
                    displayNameFormat,
                    collectionSize.size(),
                    collectionSizeToElements(collectionSize, samples)),
            supportsAddAtEndWithSampleElement)
        .forEachOrdered(subTests::add);
  }

  private void appendAddAtMiddleTests(
      List<DynamicTest> subTests,
      E elementToAdd,
      Set<CollectionSize> supportedCollectionSizes,
      String displayNameFormat) {
    ThrowingConsumer<CollectionSize> supportsAddAtMiddleWithSampleElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);
          int middleIndex = middleIndex(list);

          list.add(middleIndex, elementToAdd);
          Iterable<E> expected =
              insert(collectionSizeToElements(collectionSize, samples), middleIndex, elementToAdd);
          assertIterableEquals(
              expected,
              list,
              () ->
                  String.format(
                      ListContractConstants
                          .FORMAT_NOT_TRUE_WAS_INSERTED_AT_INDEX_OR_IN_EXPECTED_ORDER,
                      quote(elementToAdd),
                      middleIndex));
        };

    DynamicTest.stream(
            supportedCollectionSizes.iterator(),
            collectionSize ->
                String.format(
                    displayNameFormat,
                    collectionSize.size(),
                    collectionSizeToElements(collectionSize, samples)),
            supportsAddAtMiddleWithSampleElement)
        .forEachOrdered(subTests::add);
  }

  private void createMinus1IndexTests(
      List<DynamicTest> subTests, Class<? extends Throwable> expectedExceptionType) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddWithMinus1IndexAndNewElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              expectedExceptionType,
              () -> list.add(-1, samples.e3()),
              () ->
                  String.format(
                      ListContractConstants
                          .FORMAT_NOT_TRUE_THAT_LIST_ADD_INT_E_THREW_EXPECTED_EXCEPTION_TYPE,
                      samples.e3(),
                      expectedExceptionType));
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
                        .FORMAT_DOES_NOT_SUPPORT_LIST_ADD_MINUS1_E_WITH_NEW_ELEMENT,
                    collectionSize.size(),
                    collectionSizeToElements(collectionSize, samples)),
            doesNotSupportAddWithMinus1IndexAndNewElement)
        .forEachOrdered(subTests::add);

    ThrowingConsumer<CollectionSize> doesNotSupportAddWithMinus1IndexAndExistingElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              expectedExceptionType,
              () -> list.add(-1, samples.e0()),
              () ->
                  String.format(
                      ListContractConstants
                          .FORMAT_NOT_TRUE_THAT_LIST_ADD_INT_E_THREW_EXPECTED_EXCEPTION_TYPE,
                      samples.e0(),
                      expectedExceptionType));
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
                        .FORMAT_DOES_NOT_SUPPORT_LIST_ADD_MINUS1_E_WITH_EXISTING_ELEMENT,
                    collectionSize.size(),
                    collectionSizeToElements(collectionSize, samples)),
            doesNotSupportAddWithMinus1IndexAndExistingElement)
        .forEachOrdered(subTests::add);
  }

  private void createMinus1IndexWithNullElementTests(
      List<DynamicTest> subTests, Class<? extends Throwable> expectedExceptionType) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddWithMinus1IndexAndNewNullElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              expectedExceptionType,
              () -> list.add(-1, null),
              () ->
                  String.format(
                      ListContractConstants
                          .FORMAT_NOT_TRUE_THAT_LIST_ADD_INT_E_THREW_EXPECTED_EXCEPTION_TYPE,
                      ListContractConstants.NULL,
                      expectedExceptionType));
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
                        .FORMAT_DOES_NOT_SUPPORT_LIST_ADD_MINUS1_E_WITH_NEW_NULL_ELEMENT,
                    collectionSize.size(),
                    collectionSizeToElements(collectionSize, samples)),
            doesNotSupportAddWithMinus1IndexAndNewNullElement)
        .forEachOrdered(subTests::add);

    ThrowingConsumer<CollectionSize> doesNotSupportAddWithMinus1IndexAndExistingNullElement =
        collectionSize -> {
          List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

          assertThrows(
              expectedExceptionType,
              () -> list.add(-1, null),
              () ->
                  String.format(
                      ListContractConstants
                          .FORMAT_NOT_TRUE_THAT_LIST_ADD_INT_E_THREW_EXPECTED_EXCEPTION_TYPE,
                      ListContractConstants.NULL,
                      expectedExceptionType));
          assertIterableEquals(
              newCollectionWithNullElementInMiddle(samples, collectionSize),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    DynamicTest.stream(
            minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
            collectionSize ->
                String.format(
                    ListContractConstants
                        .FORMAT_DOES_NOT_SUPPORT_LIST_ADD_MINUS1_E_WITH_EXISTING_NULL_ELEMENT,
                    collectionSize.size(),
                    newCollectionWithNullElementInMiddle(samples, collectionSize)),
            doesNotSupportAddWithMinus1IndexAndExistingNullElement)
        .forEachOrdered(subTests::add);
  }
}

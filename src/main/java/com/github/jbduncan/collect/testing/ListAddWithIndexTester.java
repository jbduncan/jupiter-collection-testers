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
import static com.github.jbduncan.collect.testing.Helpers.extractConcreteSizes;
import static com.github.jbduncan.collect.testing.Helpers.insert;
import static com.github.jbduncan.collect.testing.Helpers.minus;
import static com.github.jbduncan.collect.testing.Helpers.newCollectionOfSize;
import static com.github.jbduncan.collect.testing.Helpers.newCollectionWithNullInMiddleOfSize;
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
  private final E newElement;
  private final E existingElement;
  private final Set<Feature<?>> features;
  private final Set<CollectionSize> allSupportedCollectionSizes;
  private final Set<CollectionSize> allSupportedCollectionSizesExceptZero;

  private ListAddWithIndexTester(TestListGenerator<E> testListGenerator, Set<Feature<?>> features) {
    this.generator = requireNonNull(testListGenerator, "testListGenerator");
    this.samples = requireNonNull(testListGenerator.samples(), "samples");
    this.newElement = samples.e3();
    this.existingElement = samples.e0();
    this.features = requireNonNull(features, "features");
    this.allSupportedCollectionSizes = extractConcreteSizes(features);
    this.allSupportedCollectionSizesExceptZero =
        minus(allSupportedCollectionSizes, CollectionSize.SUPPORTS_ZERO);
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

      appendSupportsAddAtStartWithNewElement(subTests);
      appendSupportsAddAtEndWithNewElement(subTests);
      appendSupportsAddAtMiddleWithNewElement(subTests);
      appendSupportsAddAtStartWithExistingElementTests(subTests);
      appendSupportsAddAtEndWithExistingElementTests(subTests);
      appendSupportsAddAtMiddleWithExistingElementTests(subTests);
      appendDoesNotSupportAddAtMinusOneTests(subTests, IndexOutOfBoundsException.class);

      tests.add(dynamicContainer(ListContractConstants.SUPPORTS_LIST_ADD_INT_E, subTests));
    }
  }

  private void generateSupportsAddWithIndexButNotOnNullElementsTests(List<DynamicNode> tests) {
    if (features.contains(ListFeature.SUPPORTS_ADD_WITH_INDEX)
        && !features.contains(CollectionFeature.ALLOWS_NULL_VALUES)) {
      List<DynamicTest> subTests = new ArrayList<>();

      appendDoesNotSupportAddAtStartWithNewNullElementTests(subTests);
      appendDoesNotSupportAddAtEndWithNewNullElementTests(subTests);
      appendDoesNotSupportAddAtMiddleWithNewNullElementTests(subTests);

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

      appendSupportsAddAtStartWithNewNullElementTests(subTests);
      appendSupportsAddAtEndWithNewNullElementTests(subTests);
      appendSupportsAddAtMiddleWithNewNullElementTests(subTests);
      appendSupportsAddAtStartWithExistingNullElementTests(subTests);
      appendSupportsAddAtEndWithExistingNullElementTests(subTests);
      appendSupportsAddAtMiddleWithExistingNullElementTests(subTests);
      appendDoesNotSupportAddAtMinusOneWithNewNullElementTests(
          subTests, IndexOutOfBoundsException.class);
      appendDoesNotSupportAddAtMinusOneWithExistingNullElementTests(
          subTests, IndexOutOfBoundsException.class);

      tests.add(
          dynamicContainer(
              ListContractConstants.SUPPORTS_LIST_ADD_INT_E_WITH_NULL_ELEMENT, subTests));
    }
  }

  private void generateDoesNotSupportAddWithIndexTests(List<DynamicNode> tests) {
    if (!features.contains(ListFeature.SUPPORTS_ADD_WITH_INDEX)) {
      List<DynamicTest> subTests = new ArrayList<>();

      appendDoesNotSupportAddAtStartWithNewElementTests(subTests);
      appendDoesNotSupportAddAtStartWithExistingElementTests(subTests);
      appendDoesNotSupportAddAtEndWithNewElementTests(subTests);
      appendDoesNotSupportAddAtEndWithExistingElementTests(subTests);
      appendDoesNotSupportAddAtMiddleWithNewElementTests(subTests);
      appendDoesNotSupportAddAtMiddleWithExistingTests(subTests);
      appendDoesNotSupportAddAtMinusOneTests(subTests, UnsupportedOperationException.class);

      tests.add(dynamicContainer(ListContractConstants.DOES_NOT_SUPPORT_LIST_ADD_INT_E, subTests));
    }
  }

  private void generateDoesNotSupportAddWithIndexWithNullElementsTests(List<DynamicNode> tests) {
    if (!features.contains(ListFeature.SUPPORTS_ADD_WITH_INDEX)) {
      List<DynamicTest> subTests = new ArrayList<>();

      appendDoesNotSupportAddAtStartWithNewNullElementTests(subTests);
      appendDoesNotSupportAddAtStartWithExistingNullElementTests(subTests);
      appendDoesNotSupportAddAtEndWithNewNullElementTests(subTests);
      appendDoesNotSupportAddAtEndWithExistingNullElementTests(subTests);
      appendDoesNotSupportAddAtMiddleWithNewNullElementTests(subTests);
      appendDoesNotSupportAddAtMiddleWithExistingNullElementTests(subTests);
      appendDoesNotSupportAddAtMinusOneWithNewNullElementTests(
          subTests, UnsupportedOperationException.class);
      appendDoesNotSupportAddAtMinusOneWithExistingNullElementTests(
          subTests, UnsupportedOperationException.class);

      tests.add(
          dynamicContainer(
              ListContractConstants.DOES_NOT_SUPPORT_LIST_ADD_INT_E_WITH_NULL_ELEMENT, subTests));
    }
  }

  private void appendSupportsAddAtStartWithNewElement(List<DynamicTest> subTests) {
    appendSupportsAddAtStartTests(
        subTests,
        newElement,
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_0_E_WITH_NEW_ELEMENT);
  }

  private void appendSupportsAddAtEndWithNewElement(List<DynamicTest> subTests) {
    appendSupportsAddAtEndTests(
        subTests,
        newElement,
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_SIZE_E_WITH_NEW_ELEMENT);
  }

  private void appendSupportsAddAtMiddleWithNewElement(List<DynamicTest> subTests) {
    appendSupportsAddAtMiddleTests(
        subTests,
        newElement,
        allSupportedCollectionSizesExceptZero,
        ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_MIDDLE_INDEX_E_WITH_NEW_ELEMENT);
  }

  private void appendSupportsAddAtStartWithExistingElementTests(List<DynamicTest> subTests) {
    appendSupportsAddAtStartTests(
        subTests,
        existingElement,
        allSupportedCollectionSizesExceptZero,
        ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_0_E_WITH_EXISTING_ELEMENT);
  }

  private void appendSupportsAddAtEndWithExistingElementTests(List<DynamicTest> subTests) {
    appendSupportsAddAtEndTests(
        subTests,
        existingElement,
        allSupportedCollectionSizesExceptZero,
        ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_SIZE_E_WITH_EXISTING_ELEMENT);
  }

  private void appendSupportsAddAtMiddleWithExistingElementTests(List<DynamicTest> subTests) {
    appendSupportsAddAtMiddleTests(
        subTests,
        existingElement,
        allSupportedCollectionSizesExceptZero,
        ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_MIDDLE_INDEX_E_WITH_EXISTING_ELEMENT);
  }

  private void appendSupportsAddAtStartTests(
      List<DynamicTest> subTests,
      E elementToAdd,
      Set<CollectionSize> supportedCollectionSizes,
      String displayNameFormat) {
    ThrowingConsumer<CollectionSize> supportsAddAtStart =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          list.add(0, elementToAdd);
          List<E> expected = prepend(elementToAdd, newCollectionOfSize(collectionSize, samples));
          assertIterableEquals(
              expected,
              list,
              () ->
                  String.format(
                      ListContractConstants.FORMAT_NOT_TRUE_THAT_LIST_WAS_PREPENDED,
                      quote(elementToAdd)));
        };

    addDynamicSubTests(supportedCollectionSizes, displayNameFormat, supportsAddAtStart, subTests);
  }

  private void appendSupportsAddAtEndTests(
      List<DynamicTest> subTests,
      E elementToAdd,
      Set<CollectionSize> supportedCollectionSizes,
      String displayNameFormat) {
    ThrowingConsumer<CollectionSize> supportsAddAtEnd =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          list.add(list.size(), elementToAdd);
          List<E> expected = append(newCollectionOfSize(collectionSize, samples), elementToAdd);
          assertIterableEquals(
              expected,
              list,
              () ->
                  String.format(
                      ListContractConstants.FORMAT_NOT_TRUE_THAT_LIST_WAS_APPENDED,
                      quote(elementToAdd)));
        };

    addDynamicSubTests(supportedCollectionSizes, displayNameFormat, supportsAddAtEnd, subTests);
  }

  private void appendSupportsAddAtMiddleTests(
      List<DynamicTest> subTests,
      E elementToAdd,
      Set<CollectionSize> supportedCollectionSizes,
      String displayNameFormat) {
    ThrowingConsumer<CollectionSize> supportsAddAtMiddle =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);
          int middleIndex = middleIndex(list);

          list.add(middleIndex, elementToAdd);
          Iterable<E> expected =
              insert(newCollectionOfSize(collectionSize, samples), middleIndex, elementToAdd);
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

    addDynamicSubTests(supportedCollectionSizes, displayNameFormat, supportsAddAtMiddle, subTests);
  }

  private void appendSupportsAddAtStartWithNewNullElementTests(List<DynamicTest> subTests) {
    ThrowingConsumer<CollectionSize> supportsAddAtStartWithNewNullElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          list.add(0, null);
          List<E> expected = prepend(null, newCollectionOfSize(collectionSize, samples));
          assertIterableEquals(
              expected, list, ListContractConstants.NOT_TRUE_THAT_LIST_WAS_PREPENDED_WITH_NULL);
        };

    addDynamicSubTests(
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_0_E_WITH_NEW_NULL_ELEMENT,
        supportsAddAtStartWithNewNullElement,
        subTests);
  }

  private void appendSupportsAddAtEndWithNewNullElementTests(List<DynamicTest> subTests) {
    ThrowingConsumer<CollectionSize> supportsAddAtEndWithNewNullElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          list.add(list.size(), null);
          List<E> expected = append(newCollectionOfSize(collectionSize, samples), null);
          assertIterableEquals(
              expected, list, ListContractConstants.NOT_TRUE_THAT_LIST_WAS_APPENDED_WITH_NULL);
        };

    addDynamicSubTests(
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_SIZE_E_WITH_NEW_NULL_ELEMENT,
        supportsAddAtEndWithNewNullElement,
        subTests);
  }

  private void appendSupportsAddAtMiddleWithNewNullElementTests(List<DynamicTest> subTests) {
    ThrowingConsumer<CollectionSize> supportsAddAtMiddleWithNewNullElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);
          int middleIndex = middleIndex(list);

          list.add(middleIndex, null);
          Iterable<E> expected =
              insert(newCollectionOfSize(collectionSize, samples), middleIndex, null);
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

    addDynamicSubTests(
        allSupportedCollectionSizesExceptZero,
        ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_MIDDLE_INDEX_E_WITH_NEW_NULL_ELEMENT,
        supportsAddAtMiddleWithNewNullElement,
        subTests);
  }

  private void appendSupportsAddAtStartWithExistingNullElementTests(List<DynamicTest> subTests) {
    ThrowingConsumer<CollectionSize> supportsAddAtStartWithExistingNullElement =
        collectionSize -> {
          List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

          list.add(0, null);
          List<E> expected =
              prepend(null, newCollectionWithNullInMiddleOfSize(collectionSize, samples));
          assertIterableEquals(
              expected, list, ListContractConstants.NOT_TRUE_THAT_LIST_WAS_PREPENDED_WITH_NULL);
        };

    addDynamicSubTestsForListWithNullElement(
        ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_0_E_WITH_EXISTING_NULL_ELEMENT,
        supportsAddAtStartWithExistingNullElement,
        subTests);
  }

  private void appendSupportsAddAtEndWithExistingNullElementTests(List<DynamicTest> subTests) {
    ThrowingConsumer<CollectionSize> supportsAddAtEndWithExistingNullElement =
        collectionSize -> {
          List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

          list.add(list.size(), null);
          List<E> expected =
              append(newCollectionWithNullInMiddleOfSize(collectionSize, samples), null);
          assertIterableEquals(
              expected, list, ListContractConstants.NOT_TRUE_THAT_LIST_WAS_APPENDED_WITH_NULL);
        };

    addDynamicSubTestsForListWithNullElement(
        ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_SIZE_E_WITH_EXISTING_NULL_ELEMENT,
        supportsAddAtEndWithExistingNullElement,
        subTests);
  }

  private void appendSupportsAddAtMiddleWithExistingNullElementTests(List<DynamicTest> subTests) {
    ThrowingConsumer<CollectionSize> supportsAddAtMiddleWithExistingNullElement =
        collectionSize -> {
          List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);
          int middleIndex = middleIndex(list);

          list.add(middleIndex, null);
          Iterable<E> expected =
              insert(
                  newCollectionWithNullInMiddleOfSize(collectionSize, samples), middleIndex, null);
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

    addDynamicSubTestsForListWithNullElement(
        ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_MIDDLE_INDEX_E_WITH_EXISTING_NULL_ELEMENT,
        supportsAddAtMiddleWithExistingNullElement,
        subTests);
  }

  private void appendDoesNotSupportAddAtStartWithNewElementTests(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtStartTests(
        subTests,
        newElement,
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_0_E_WITH_NEW_ELEMENT);
  }

  private void appendDoesNotSupportAddAtStartWithExistingElementTests(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtStartTests(
        subTests,
        existingElement,
        allSupportedCollectionSizesExceptZero,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_0_E_WITH_EXISTING_ELEMENT);
  }

  private void appendDoesNotSupportAddAtEndWithNewElementTests(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtEndTests(
        subTests,
        newElement,
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_SIZE_E_WITH_NEW_ELEMENT);
  }

  private void appendDoesNotSupportAddAtEndWithExistingElementTests(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtEndTests(
        subTests,
        existingElement,
        allSupportedCollectionSizesExceptZero,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_SIZE_E_WITH_EXISTING_ELEMENT);
  }

  private void appendDoesNotSupportAddAtMiddleWithNewElementTests(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtMiddleTests(
        subTests,
        newElement,
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_MIDDLE_INDEX_E_WITH_NEW_ELEMENT);
  }

  private void appendDoesNotSupportAddAtMiddleWithExistingTests(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtMiddleTests(
        subTests,
        existingElement,
        allSupportedCollectionSizesExceptZero,
        ListContractConstants
            .FORMAT_DOES_NOT_SUPPORT_LIST_ADD_MIDDLE_INDEX_E_WITH_EXISTING_ELEMENT);
  }

  private void appendDoesNotSupportAddAtStartWithNewNullElementTests(List<DynamicTest> subTests) {
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
              newCollectionOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTests(
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_0_E_WITH_NEW_NULL_ELEMENT,
        doesNotSupportAddAtStartWithNewNullElement,
        subTests);
  }

  private void appendDoesNotSupportAddAtStartWithExistingNullElementTests(
      List<DynamicTest> subTests) {
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
              newCollectionWithNullInMiddleOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTestsForListWithNullElement(
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_0_E_WITH_EXISTING_NULL_ELEMENT,
        doesNotSupportAddAtStartWithExistingNullElement,
        subTests);
  }

  private void appendDoesNotSupportAddAtEndWithNewNullElementTests(List<DynamicTest> subTests) {
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
              newCollectionOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTests(
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_SIZE_E_WITH_NEW_NULL_ELEMENT,
        doesNotSupportAddAtEndWithNewNullElement,
        subTests);
  }

  private void appendDoesNotSupportAddAtEndWithExistingNullElementTests(
      List<DynamicTest> subTests) {
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
              newCollectionWithNullInMiddleOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTestsForListWithNullElement(
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_SIZE_E_WITH_EXISTING_NULL_ELEMENT,
        doesNotSupportAddAtEndWithExistingNullElement,
        subTests);
  }

  private void appendDoesNotSupportAddAtMiddleWithNewNullElementTests(List<DynamicTest> subTests) {
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
              newCollectionOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTests(
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_MIDDLE_INDEX_E_WITH_NEW_NULL_ELEMENT,
        doesNotSupportAddAtMiddleWithNewNullElement,
        subTests);
  }

  private void appendDoesNotSupportAddAtMiddleWithExistingNullElementTests(
      List<DynamicTest> subTests) {
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
              newCollectionWithNullInMiddleOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTestsForListWithNullElement(
        ListContractConstants
            .FORMAT_DOES_NOT_SUPPORT_LIST_ADD_MIDDLE_INDEX_E_WITH_EXISTING_NULL_ELEMENT,
        doesNotSupportAddAtMiddleWithExistingNullElement,
        subTests);
  }

  private void appendDoesNotSupportAddAtStartTests(
      List<DynamicTest> subTests,
      E elementToAdd,
      Set<CollectionSize> supportedCollectionSizes,
      String displayNameFormat) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddAtStart =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(0, elementToAdd),
              () ->
                  String.format(
                      ListContractConstants
                          .FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION,
                      quote(elementToAdd)));
          assertIterableEquals(
              newCollectionOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTests(
        supportedCollectionSizes, displayNameFormat, doesNotSupportAddAtStart, subTests);
  }

  private void appendDoesNotSupportAddAtEndTests(
      List<DynamicTest> subTests,
      E elementToAdd,
      Set<CollectionSize> supportedCollectionSizes,
      String displayNameFormat) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddAtEnd =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(list.size(), elementToAdd),
              () ->
                  String.format(
                      ListContractConstants
                          .FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION,
                      quote(elementToAdd)));
          assertIterableEquals(
              newCollectionOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTests(
        supportedCollectionSizes, displayNameFormat, doesNotSupportAddAtEnd, subTests);
  }

  private void appendDoesNotSupportAddAtMiddleTests(
      List<DynamicTest> subTests,
      E elementToAdd,
      Set<CollectionSize> supportedCollectionSizes,
      String displayNameFormat) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddAtMiddle =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(middleIndex(list), elementToAdd),
              () ->
                  String.format(
                      ListContractConstants
                          .FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION,
                      quote(elementToAdd)));
          assertIterableEquals(
              newCollectionOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTests(
        supportedCollectionSizes, displayNameFormat, doesNotSupportAddAtMiddle, subTests);
  }

  private void appendDoesNotSupportAddAtMinusOneTests(
      List<DynamicTest> subTests, Class<? extends Throwable> expectedExceptionType) {
    appendDoesNotSupportAddAtMinusOneTests(
        subTests,
        expectedExceptionType,
        newElement,
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_MINUS1_E_WITH_NEW_ELEMENT);

    appendDoesNotSupportAddAtMinusOneTests(
        subTests,
        expectedExceptionType,
        existingElement,
        allSupportedCollectionSizesExceptZero,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_MINUS1_E_WITH_EXISTING_ELEMENT);
  }

  private void appendDoesNotSupportAddAtMinusOneTests(
      List<DynamicTest> subTests,
      Class<? extends Throwable> expectedExceptionType,
      E elementToAdd,
      Set<CollectionSize> supportedCollectionSizes,
      String displayNameFormat) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddAtMinusOne =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              expectedExceptionType,
              () -> list.add(-1, elementToAdd),
              () ->
                  String.format(
                      ListContractConstants
                          .FORMAT_NOT_TRUE_THAT_LIST_ADD_INT_E_THREW_EXPECTED_EXCEPTION_TYPE,
                      elementToAdd,
                      expectedExceptionType));
          assertIterableEquals(
              newCollectionOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTests(
        supportedCollectionSizes, displayNameFormat, doesNotSupportAddAtMinusOne, subTests);
  }

  private void appendDoesNotSupportAddAtMinusOneWithNewNullElementTests(
      List<DynamicTest> subTests, Class<? extends Throwable> expectedExceptionType) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddAtMinusOneWithNewNullElementt =
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
              newCollectionOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTests(
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_MINUS1_E_WITH_NEW_NULL_ELEMENT,
        doesNotSupportAddAtMinusOneWithNewNullElementt,
        subTests);
  }

  private void appendDoesNotSupportAddAtMinusOneWithExistingNullElementTests(
      List<DynamicTest> subTests, Class<? extends Throwable> expectedExceptionType) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddAtMinusOneWithExistingNullElement =
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
              newCollectionWithNullInMiddleOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTestsForListWithNullElement(
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_MINUS1_E_WITH_EXISTING_NULL_ELEMENT,
        doesNotSupportAddAtMinusOneWithExistingNullElement,
        subTests);
  }

  private void addDynamicSubTests(
      Set<CollectionSize> supportedCollectionSizes,
      String displayNameFormat,
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

  private void addDynamicSubTestsForListWithNullElement(
      String displayNameFormat,
      ThrowingConsumer<CollectionSize> testExecutor,
      List<DynamicTest> subTests) {
    DynamicTest.stream(
            allSupportedCollectionSizesExceptZero.iterator(),
            collectionSize ->
                String.format(
                    displayNameFormat,
                    collectionSize.size(),
                    newCollectionWithNullInMiddleOfSize(collectionSize, samples)),
            testExecutor)
        .forEachOrdered(subTests::add);
  }
}

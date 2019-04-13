/*
 * Copyright 2018-2019 the Jupiter Collection Testers authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jbduncan.collect.testing;

import static com.github.jbduncan.collect.testing.Helpers.insert;
import static com.github.jbduncan.collect.testing.Helpers.newCollectionOfSize;
import static com.github.jbduncan.collect.testing.Helpers.newCollectionWithNullInMiddleOfSize;
import static com.github.jbduncan.collect.testing.Helpers.quote;
import static com.github.jbduncan.collect.testing.Helpers.stringify;
import static com.github.jbduncan.collect.testing.ListContractHelpers.middleIndex;
import static com.github.jbduncan.collect.testing.ListContractHelpers.newListToTest;
import static com.github.jbduncan.collect.testing.ListContractHelpers.newListToTestWithNullElementInMiddle;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.ThrowingConsumer;

final class ListAddAtMiddleSubTestMaker<E> extends BaseListSubTestMaker<E> {
  private static final String INDEX_TO_ADD_AT = "middleIndex()";

  private final TestListGenerator<E> generator;
  private final E newElement;
  private final E existingElement;
  private final Set<CollectionSize> allSupportedCollectionSizes;

  private ListAddAtMiddleSubTestMaker(Builder<E> builder) {
    super(builder.sampleElements, builder.allSupportedCollectionSizesExceptZero);
    this.generator = requireNonNull(builder.testListGenerator, "testListGenerator");
    this.newElement = requireNonNull(builder.newElement, "newElement");
    this.existingElement = requireNonNull(builder.existingElement, "existingElement");
    this.allSupportedCollectionSizes =
        requireNonNull(builder.allSupportedCollectionSizes, "allSupportedCollectionSizes");
  }

  static <E> Builder<E> builder() {
    return new Builder<>();
  }

  static final class Builder<E> {
    private Builder() {}

    private TestListGenerator<E> testListGenerator;
    private SampleElements<E> sampleElements;
    private E newElement;
    private E existingElement;
    private Set<CollectionSize> allSupportedCollectionSizes;
    private Set<CollectionSize> allSupportedCollectionSizesExceptZero;

    Builder<E> testListGenerator(TestListGenerator<E> testListGenerator) {
      this.testListGenerator = testListGenerator;
      return this;
    }

    Builder<E> sampleElements(SampleElements<E> sampleElements) {
      this.sampleElements = sampleElements;
      return this;
    }

    Builder<E> newElement(E newElement) {
      this.newElement = newElement;
      return this;
    }

    Builder<E> existingElement(E existingElement) {
      this.existingElement = existingElement;
      return this;
    }

    Builder<E> allSupportedCollectionSizes(Set<CollectionSize> allSupportedCollectionSizes) {
      this.allSupportedCollectionSizes = allSupportedCollectionSizes;
      return this;
    }

    Builder<E> allSupportedCollectionSizesExceptZero(
        Set<CollectionSize> allSupportedCollectionSizesExceptZero) {
      this.allSupportedCollectionSizesExceptZero = allSupportedCollectionSizesExceptZero;
      return this;
    }

    ListAddAtMiddleSubTestMaker<E> build() {
      return new ListAddAtMiddleSubTestMaker<>(this);
    }
  }

  List<DynamicTest> supportsAddWithIndexSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendSupportsAddAtMiddleWithNewElement(subTests);
    appendSupportsAddAtMiddleWithExistingElement(subTests);
    return subTests;
  }

  private void appendSupportsAddAtMiddleWithNewElement(List<DynamicTest> subTests) {
    appendSupportsAddAtMiddleImpl(subTests, newElement);
  }

  private void appendSupportsAddAtMiddleWithExistingElement(List<DynamicTest> subTests) {
    appendSupportsAddAtMiddleImpl(subTests, existingElement);
  }

  List<DynamicTest> supportsAddWithIndexForNullsSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendSupportsAddAtMiddleWithNewNull(subTests);
    appendSupportsAddAtMiddleWithExistingNull(subTests);
    return subTests;
  }

  private void appendSupportsAddAtMiddleWithNewNull(List<DynamicTest> subTests) {
    appendSupportsAddAtMiddleImpl(subTests, null);
  }

  private void appendSupportsAddAtMiddleWithExistingNull(List<DynamicTest> subTests) {
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
                      quote("null"),
                      middleIndex));
        };

    addDynamicSubTestsForListWithNullElement(
        ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_WITH_INDEX,
        INDEX_TO_ADD_AT,
        supportsAddAtMiddleWithExistingNullElement,
        subTests);
  }

  List<DynamicTest> doesNotSupportAddWithIndexSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendDoesNotSupportAddAtMiddleWithNewElement(subTests);
    appendDoesNotSupportAddAtMiddleWithExistingElement(subTests);
    return subTests;
  }

  private void appendDoesNotSupportAddAtMiddleWithNewElement(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtMiddleImpl(subTests, newElement);
  }

  private void appendDoesNotSupportAddAtMiddleWithExistingElement(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtMiddleImpl(subTests, existingElement);
  }

  List<DynamicTest> doesNotSupportAddWithIndexForNullsSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendDoesNotSupportAddAtMiddleWithNewNull(subTests);
    appendDoesNotSupportAddAtMiddleWithExistingNull(subTests);
    return subTests;
  }

  private void appendDoesNotSupportAddAtMiddleWithNewNull(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtMiddleImpl(subTests, null);
  }

  private void appendDoesNotSupportAddAtMiddleWithExistingNull(List<DynamicTest> subTests) {
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
                      "null"));
          assertIterableEquals(
              newCollectionWithNullInMiddleOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTestsForListWithNullElement(
        ListContractConstants.FORMAT_DOESNT_SUPPORT_LIST_ADD_WITH_INDEX,
        INDEX_TO_ADD_AT,
        doesNotSupportAddAtMiddleWithExistingNullElement,
        subTests);
  }

  private void appendSupportsAddAtMiddleImpl(List<DynamicTest> subTests, E elementToAdd) {
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
                      stringify(elementToAdd),
                      middleIndex));
        };

    addDynamicSubTests(
        // [].add(middleIndex(), E) is already indirectly tested by ListAddAtStartSubTestMaker,
        // because it tests [].add(0, E), and middleIndex() == 0 for empty lists, so we skip
        // CollectionSize.SUPPORTS_ZERO.
        allSupportedCollectionSizesExceptZero,
        ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_WITH_INDEX,
        INDEX_TO_ADD_AT,
        elementToAdd,
        supportsAddAtMiddle,
        subTests);
  }

  private void appendDoesNotSupportAddAtMiddleImpl(List<DynamicTest> subTests, E elementToAdd) {
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
                      stringify(elementToAdd)));
          assertIterableEquals(
              newCollectionOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTests(
        // [].add(middleIndex(), E) is already indirectly tested by ListAddAtStartSubTestMaker,
        // because it tests [].add(0, E), and middleIndex() == 0 for empty lists, so we skip
        // CollectionSize.SUPPORTS_ZERO.
        allSupportedCollectionSizesExceptZero,
        ListContractConstants.FORMAT_DOESNT_SUPPORT_LIST_ADD_WITH_INDEX,
        INDEX_TO_ADD_AT,
        elementToAdd,
        doesNotSupportAddAtMiddle,
        subTests);
  }
}

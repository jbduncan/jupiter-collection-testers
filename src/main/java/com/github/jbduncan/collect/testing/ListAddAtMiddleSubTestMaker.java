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
import static com.github.jbduncan.collect.testing.Helpers.stringifyElements;
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

final class ListAddAtMiddleSubTestMaker<E> {
  private static final String INDEX_TO_ADD_AT = "middleIndex()";

  private final TestListGenerator<E> generator;
  private final SampleElements<E> samples;
  private final E newElement;
  private final E existingElement;
  private final Set<CollectionSize> allSupportedCollectionSizesExceptZero;

  private ListAddAtMiddleSubTestMaker(Builder<E> builder) {
    this.generator = requireNonNull(builder.testListGenerator, "testListGenerator");
    this.samples = requireNonNull(builder.sampleElements, "samples");
    this.newElement = requireNonNull(builder.newElement, "newElement");
    this.existingElement = requireNonNull(builder.existingElement, "existingElement");
    this.allSupportedCollectionSizesExceptZero =
        requireNonNull(
            builder.allSupportedCollectionSizesExceptZero, "allSupportedCollectionSizesExceptZero");
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
                      "Not true that %s was inserted at index %s of list, or that elements in list are in expected order",
                      quote("null"), middleIndex));
        };

    DynamicTest.stream(
            allSupportedCollectionSizesExceptZero.iterator(),
            collectionSize ->
                String.format(
                    ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_WITH_INDEX,
                    INDEX_TO_ADD_AT,
                    "null",
                    stringifyElements(
                        newCollectionWithNullInMiddleOfSize(collectionSize, samples))),
            supportsAddAtMiddleWithExistingNullElement)
        .forEachOrdered(subTests::add);
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

    DynamicTest.stream(
            allSupportedCollectionSizesExceptZero.iterator(),
            collectionSize ->
                String.format(
                    ListContractConstants.FORMAT_DOESNT_SUPPORT_LIST_ADD_WITH_INDEX,
                    INDEX_TO_ADD_AT,
                    "null",
                    stringifyElements(
                        newCollectionWithNullInMiddleOfSize(collectionSize, samples))),
            doesNotSupportAddAtMiddleWithExistingNullElement)
        .forEachOrdered(subTests::add);
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
                      "Not true that %s was inserted at index %s of list, or that elements in list are in expected order",
                      stringify(elementToAdd), middleIndex));
        };

    // [].add(middleIndex(), E) is already indirectly tested by ListAddAtStartSubTestMaker,
    // because it tests [].add(0, E), and middleIndex() == 0 for empty lists, so we skip
    // CollectionSize.SUPPORTS_ZERO.
    DynamicTest.stream(
            allSupportedCollectionSizesExceptZero.iterator(),
            collectionSize ->
                String.format(
                    ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_WITH_INDEX,
                    INDEX_TO_ADD_AT,
                    stringify(elementToAdd),
                    stringifyElements(newCollectionOfSize(collectionSize, samples))),
            supportsAddAtMiddle)
        .forEachOrdered(subTests::add);
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

    // [].add(middleIndex(), E) is already indirectly tested by ListAddAtStartSubTestMaker,
    // because it tests [].add(0, E), and middleIndex() == 0 for empty lists, so we skip
    // CollectionSize.SUPPORTS_ZERO.
    DynamicTest.stream(
            allSupportedCollectionSizesExceptZero.iterator(),
            collectionSize ->
                String.format(
                    ListContractConstants.FORMAT_DOESNT_SUPPORT_LIST_ADD_WITH_INDEX,
                    INDEX_TO_ADD_AT,
                    stringify(elementToAdd),
                    stringifyElements(newCollectionOfSize(collectionSize, samples))),
            doesNotSupportAddAtMiddle)
        .forEachOrdered(subTests::add);
  }
}

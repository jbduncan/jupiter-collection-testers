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

import static com.github.jbduncan.collect.testing.Helpers.newCollectionOfSize;
import static com.github.jbduncan.collect.testing.Helpers.newCollectionWithNullInMiddleOfSize;
import static com.github.jbduncan.collect.testing.Helpers.prepend;
import static com.github.jbduncan.collect.testing.Helpers.stringify;
import static com.github.jbduncan.collect.testing.Helpers.stringifyElements;
import static com.github.jbduncan.collect.testing.ListContractHelpers.newListToTest;
import static com.github.jbduncan.collect.testing.ListContractHelpers.newListToTestWithNullElementInMiddle;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.ThrowingConsumer;

final class ListAddAtStartSubTestMaker<E> {
  private static final String INDEX_TO_ADD_AT = "0";

  private final TestListGenerator<E> generator;
  private final SampleElements<E> samples;
  private final E newElement;
  private final E existingElement;
  private final Set<CollectionSize> allSupportedCollectionSizes;
  private final Set<CollectionSize> allSupportedCollectionSizesExceptZero;

  private ListAddAtStartSubTestMaker(Builder<E> builder) {
    this.generator = requireNonNull(builder.testListGenerator, "testListGenerator");
    this.samples = requireNonNull(builder.sampleElements, "samples");
    this.newElement = requireNonNull(builder.newElement, "newElement");
    this.existingElement = requireNonNull(builder.existingElement, "existingElement");
    this.allSupportedCollectionSizes =
        requireNonNull(builder.allSupportedCollectionSizes, "allSupportedCollectionSizes");
    this.allSupportedCollectionSizesExceptZero =
        requireNonNull(
            builder.allSupportedCollectionSizesExceptZero, "allSupportedCollectionSizesExceptZero");
  }

  static <E> Builder<E> builder() {
    return new Builder<>();
  }

  static class Builder<E> {
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

    ListAddAtStartSubTestMaker<E> build() {
      return new ListAddAtStartSubTestMaker<>(this);
    }
  }

  List<DynamicTest> supportsAddWithIndexSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendSupportsAddAtStartWithNewElement(subTests);
    appendSupportsAddAtStartWithExistingElement(subTests);
    return subTests;
  }

  private void appendSupportsAddAtStartWithNewElement(List<DynamicTest> subTests) {
    appendSupportsAddAtStartImpl(subTests, newElement, allSupportedCollectionSizes);
  }

  private void appendSupportsAddAtStartWithExistingElement(List<DynamicTest> subTests) {
    appendSupportsAddAtStartImpl(subTests, existingElement, allSupportedCollectionSizesExceptZero);
  }

  List<DynamicTest> supportsAddWithIndexForNullsSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendSupportsAddAtStartWithNewNull(subTests);
    appendSupportsAddAtStartWithExistingNull(subTests);
    return subTests;
  }

  private void appendSupportsAddAtStartWithNewNull(List<DynamicTest> subTests) {
    appendSupportsAddAtStartImpl(subTests, null, allSupportedCollectionSizes);
  }

  private void appendSupportsAddAtStartWithExistingNull(List<DynamicTest> subTests) {
    ThrowingConsumer<CollectionSize> supportsAddAtStartWithExistingNullElement =
        collectionSize -> {
          List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

          list.add(0, null);
          List<E> expected =
              prepend(null, newCollectionWithNullInMiddleOfSize(collectionSize, samples));
          assertIterableEquals(expected, list, "Not true that list was prepended with null");
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
            supportsAddAtStartWithExistingNullElement)
        .forEachOrdered(subTests::add);
  }

  List<DynamicTest> doesNotSupportAddWithIndexSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendDoesNotSupportAddAtStartWithNewElement(subTests);
    appendDoesNotSupportAddAtStartWithExistingElement(subTests);
    return subTests;
  }

  private void appendDoesNotSupportAddAtStartWithNewElement(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtStartImpl(subTests, newElement, allSupportedCollectionSizes);
  }

  private void appendDoesNotSupportAddAtStartWithExistingElement(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtStartImpl(
        subTests, existingElement, allSupportedCollectionSizesExceptZero);
  }

  List<DynamicTest> doesNotSupportAddWithIndexForNullsSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendDoesNotSupportAddAtStartWithNewNull(subTests);
    appendDoesNotSupportAddAtStartWithExistingNull(subTests);
    return subTests;
  }

  private void appendDoesNotSupportAddAtStartWithNewNull(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtStartImpl(subTests, null, allSupportedCollectionSizes);
  }

  private void appendDoesNotSupportAddAtStartWithExistingNull(List<DynamicTest> subTests) {
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
            doesNotSupportAddAtStartWithExistingNullElement)
        .forEachOrdered(subTests::add);
  }

  private void appendSupportsAddAtStartImpl(
      List<DynamicTest> subTests, E elementToAdd, Set<CollectionSize> supportedCollectionSizes) {
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
                      "Not true that list was prepended with %s", stringify(elementToAdd)));
        };

    DynamicTest.stream(
            supportedCollectionSizes.iterator(),
            collectionSize ->
                String.format(
                    ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_WITH_INDEX,
                    INDEX_TO_ADD_AT,
                    stringify(elementToAdd),
                    stringifyElements(newCollectionOfSize(collectionSize, samples))),
            supportsAddAtStart)
        .forEachOrdered(subTests::add);
  }

  private void appendDoesNotSupportAddAtStartImpl(
      List<DynamicTest> subTests, E elementToAdd, Set<CollectionSize> supportedCollectionSizes) {
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
                      stringify(elementToAdd)));
          assertIterableEquals(
              newCollectionOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    DynamicTest.stream(
            supportedCollectionSizes.iterator(),
            collectionSize ->
                String.format(
                    ListContractConstants.FORMAT_DOESNT_SUPPORT_LIST_ADD_WITH_INDEX,
                    INDEX_TO_ADD_AT,
                    stringify(elementToAdd),
                    stringifyElements(newCollectionOfSize(collectionSize, samples))),
            doesNotSupportAddAtStart)
        .forEachOrdered(subTests::add);
  }

  List<DynamicTest> failsFastOnConcurrentModificationSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();

    ThrowingConsumer<CollectionSize> failsFastOnCme =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          Iterator<E> iterator = list.iterator();
          assertThrows(
              ConcurrentModificationException.class,
              () -> {
                list.add(0, newElement);
                iterator.next();
              });
        };

    DynamicTest.stream(
            allSupportedCollectionSizes.iterator(),
            collectionSize ->
                String.format(
                    "List.add(%s, %s) fails fast when concurrently modifying %s",
                    INDEX_TO_ADD_AT,
                    stringify(newElement),
                    stringifyElements(newCollectionOfSize(collectionSize, samples))),
            failsFastOnCme)
        .forEachOrdered(subTests::add);

    return subTests;
  }

  List<DynamicTest> failsFastOnConcurrentModificationInvolvingNullElementSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();

    ThrowingConsumer<CollectionSize> failsFastOnCme =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          Iterator<E> iterator = list.iterator();
          assertThrows(
              ConcurrentModificationException.class,
              () -> {
                list.add(0, null);
                iterator.next();
              });
        };

    DynamicTest.stream(
            allSupportedCollectionSizes.iterator(),
            collectionSize ->
                String.format(
                    "List.add(%s, %s) fails fast when concurrently modifying %s",
                    INDEX_TO_ADD_AT,
                    stringify(null),
                    stringifyElements(newCollectionOfSize(collectionSize, samples))),
            failsFastOnCme)
        .forEachOrdered(subTests::add);

    return subTests;
  }
}

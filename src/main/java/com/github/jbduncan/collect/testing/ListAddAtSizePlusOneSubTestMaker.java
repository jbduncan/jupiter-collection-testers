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
import static com.github.jbduncan.collect.testing.Helpers.stringify;
import static com.github.jbduncan.collect.testing.Helpers.stringifyElements;
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

final class ListAddAtSizePlusOneSubTestMaker<E> {

  private final TestListGenerator<E> generator;
  private final SampleElements<E> samples;
  private final E newElement;
  private final E existingElement;
  private final Set<CollectionSize> allSupportedCollectionSizes;
  private final Set<CollectionSize> allSupportedCollectionSizesExceptZero;
  private final Class<? extends Throwable> expectedExceptionType;

  ListAddAtSizePlusOneSubTestMaker(
      TestListGenerator<E> generator,
      SampleElements<E> samples,
      E newElement,
      E existingElement,
      Set<CollectionSize> allSupportedCollectionSizes,
      Set<CollectionSize> allSupportedCollectionSizesExceptZero,
      Class<? extends Throwable> expectedExceptionType) {
    this.generator = requireNonNull(generator, "testListGenerator");
    this.samples = requireNonNull(samples, "samples");
    this.newElement = requireNonNull(newElement, "newElement");
    this.existingElement = requireNonNull(existingElement, "existingElement");
    this.allSupportedCollectionSizes =
        requireNonNull(allSupportedCollectionSizes, "allSupportedCollectionSizes");
    this.allSupportedCollectionSizesExceptZero =
        requireNonNull(
            allSupportedCollectionSizesExceptZero, "allSupportedCollectionSizesExceptZero");
    this.expectedExceptionType = requireNonNull(expectedExceptionType, "expectedExceptionType");
  }

  private ListAddAtSizePlusOneSubTestMaker(Builder<E> builder) {
    this.generator = requireNonNull(builder.testListGenerator, "testListGenerator");
    this.samples = requireNonNull(builder.sampleElements, "samples");
    this.newElement = requireNonNull(builder.newElement, "newElement");
    this.existingElement = requireNonNull(builder.existingElement, "existingElement");
    this.allSupportedCollectionSizes =
        requireNonNull(builder.allSupportedCollectionSizes, "allSupportedCollectionSizes");
    this.allSupportedCollectionSizesExceptZero =
        requireNonNull(
            builder.allSupportedCollectionSizesExceptZero, "allSupportedCollectionSizesExceptZero");
    this.expectedExceptionType =
        requireNonNull(builder.expectedExceptionType, "expectedExceptionType");
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
    private Class<? extends Throwable> expectedExceptionType;

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

    Builder<E> expectedExceptionType(Class<? extends Throwable> expectedExceptionType) {
      this.expectedExceptionType = expectedExceptionType;
      return this;
    }

    ListAddAtSizePlusOneSubTestMaker<E> build() {
      return new ListAddAtSizePlusOneSubTestMaker<>(this);
    }
  }

  List<DynamicTest> doesNotSupportAddWithIndexSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendDoesNotSupportAddAtSizePlusOneWithNewElement(subTests);
    appendDoesNotSupportAddAtSizePlusOneWithExistingElement(subTests);
    return subTests;
  }

  private void appendDoesNotSupportAddAtSizePlusOneWithNewElement(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtSizePlusOneImpl(subTests, newElement, allSupportedCollectionSizes);
  }

  private void appendDoesNotSupportAddAtSizePlusOneWithExistingElement(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtSizePlusOneImpl(
        subTests, existingElement, allSupportedCollectionSizesExceptZero);
  }

  List<DynamicTest> doesNotSupportAddWithIndexForNullsSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendDoesNotSupportAddAtSizePlusOneWithNewNull(subTests);
    appendDoesNotSupportAddAtSizePlusOneWithExistingNull(subTests);
    return subTests;
  }

  private void appendDoesNotSupportAddAtSizePlusOneWithNewNull(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtSizePlusOneImpl(subTests, null, allSupportedCollectionSizes);
  }

  private void appendDoesNotSupportAddAtSizePlusOneWithExistingNull(List<DynamicTest> subTests) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddAtSizePlusOneWithExistingNullElement =
        collectionSize -> {
          List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

          assertThrows(
              expectedExceptionType,
              () -> list.add(list.size() + 1, null),
              () ->
                  "Not true that list.add("
                      + (list.size() + 1)
                      + ", null"
                      + ") threw exception of type "
                      + expectedExceptionType);
          assertIterableEquals(
              newCollectionWithNullInMiddleOfSize(collectionSize, samples),
              list,
              "Not true that list remained unchanged");
        };

    DynamicTest.stream(
            allSupportedCollectionSizesExceptZero.iterator(),
            collectionSize ->
                "Doesn't support List.add(size() + 1, null) on "
                    + stringifyElements(
                        newCollectionWithNullInMiddleOfSize(collectionSize, samples)),
            doesNotSupportAddAtSizePlusOneWithExistingNullElement)
        .forEachOrdered(subTests::add);
  }

  private void appendDoesNotSupportAddAtSizePlusOneImpl(
      List<DynamicTest> subTests, E elementToAdd, Set<CollectionSize> supportedCollectionSizes) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddAtSizePlusOne =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              expectedExceptionType,
              () -> list.add(list.size() + 1, elementToAdd),
              () ->
                  "Not true that list.add("
                      + (list.size() + 1)
                      + ", "
                      + stringify(elementToAdd)
                      + ") threw exception of type "
                      + expectedExceptionType);

          assertIterableEquals(
              newCollectionOfSize(collectionSize, samples),
              list,
              "Not true that list remained unchanged");
        };

    DynamicTest.stream(
            supportedCollectionSizes.iterator(),
            collectionSize ->
                "Doesn't support List.add(size() + 1, "
                    + stringify(elementToAdd)
                    + ") on "
                    + stringifyElements(newCollectionOfSize(collectionSize, samples)),
            doesNotSupportAddAtSizePlusOne)
        .forEachOrdered(subTests::add);
  }
}

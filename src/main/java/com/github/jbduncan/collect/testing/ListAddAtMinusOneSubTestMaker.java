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

final class ListAddAtMinusOneSubTestMaker<E> {

  private final TestListGenerator<E> generator;
  private final SampleElements<E> samples;
  private final E newElement;
  private final E existingElement;
  private final Set<CollectionSize> allSupportedCollectionSizes;
  private final Set<CollectionSize> allSupportedCollectionSizesExceptZero;
  private final Class<? extends Throwable> expectedExceptionType;

  ListAddAtMinusOneSubTestMaker(
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

  List<DynamicTest> doesNotSupportAddWithIndexSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendDoesNotSupportAddAtMinusOneWithNewElement(subTests);
    appendDoesNotSupportAddAtMinusOneWithExistingElement(subTests);
    return subTests;
  }

  private void appendDoesNotSupportAddAtMinusOneWithNewElement(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtMinusOneImpl(subTests, newElement, allSupportedCollectionSizes);
  }

  private void appendDoesNotSupportAddAtMinusOneWithExistingElement(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtMinusOneImpl(
        subTests, existingElement, allSupportedCollectionSizesExceptZero);
  }

  List<DynamicTest> doesNotSupportAddWithIndexForNullsSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendDoesNotSupportAddAtMinusOneWithNewNull(subTests);
    appendDoesNotSupportAddAtMinusOneWithExistingNull(subTests);
    return subTests;
  }

  private void appendDoesNotSupportAddAtMinusOneWithNewNull(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtMinusOneImpl(subTests, null, allSupportedCollectionSizes);
  }

  private void appendDoesNotSupportAddAtMinusOneWithExistingNull(List<DynamicTest> subTests) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddAtMinusOneWithExistingNullElement =
        collectionSize -> {
          List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

          assertThrows(
              expectedExceptionType,
              () -> list.add(-1, null),
              () ->
                  "Not true that list.add(-1, null) threw exception of type "
                      + expectedExceptionType);
          assertIterableEquals(
              newCollectionWithNullInMiddleOfSize(collectionSize, samples),
              list,
              "Not true that list remained unchanged");
        };

    DynamicTest.stream(
            allSupportedCollectionSizesExceptZero.iterator(),
            collectionSize ->
                "Doesn't support List.add(-1, null) on "
                    + stringifyElements(
                        newCollectionWithNullInMiddleOfSize(collectionSize, samples)),
            doesNotSupportAddAtMinusOneWithExistingNullElement)
        .forEachOrdered(subTests::add);
  }

  private void appendDoesNotSupportAddAtMinusOneImpl(
      List<DynamicTest> subTests, E elementToAdd, Set<CollectionSize> supportedCollectionSizes) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddAtMinusOne =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              expectedExceptionType,
              () -> list.add(-1, elementToAdd),
              () ->
                  "Not true that list.add(-1, "
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
                "Doesn't support List.add(-1, "
                    + stringify(elementToAdd)
                    + ") on "
                    + stringifyElements(newCollectionOfSize(collectionSize, samples)),
            doesNotSupportAddAtMinusOne)
        .forEachOrdered(subTests::add);
  }
}

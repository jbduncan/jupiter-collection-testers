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

import static com.github.jbduncan.collect.testing.Helpers.append;
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

final class ListAddAtEndSubTestMaker<E> {

  private final TestListGenerator<E> generator;
  private final SampleElements<E> samples;
  private final E newElement;
  private final E existingElement;
  private final Set<CollectionSize> allSupportedCollectionSizesExceptZero;

  ListAddAtEndSubTestMaker(
      TestListGenerator<E> generator,
      SampleElements<E> samples,
      E newElement,
      E existingElement,
      Set<CollectionSize> allSupportedCollectionSizesExceptZero) {
    this.generator = requireNonNull(generator, "generator");
    this.samples = requireNonNull(samples, "samples");
    this.newElement = requireNonNull(newElement, "newElement");
    this.existingElement = requireNonNull(existingElement, "existingElement");
    this.allSupportedCollectionSizesExceptZero =
        requireNonNull(
            allSupportedCollectionSizesExceptZero, "allSupportedCollectionSizesExceptZero");
  }

  List<DynamicTest> supportsAddWithIndexSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendSupportsAddAtEndWithNewElement(subTests);
    appendSupportsAddAtEndWithExistingElement(subTests);
    return subTests;
  }

  private void appendSupportsAddAtEndWithNewElement(List<DynamicTest> subTests) {
    appendSupportsAddAtEndImpl(subTests, newElement);
  }

  private void appendSupportsAddAtEndWithExistingElement(List<DynamicTest> subTests) {
    appendSupportsAddAtEndImpl(subTests, existingElement);
  }

  List<DynamicTest> supportsAddWithIndexForNullsSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendSupportsAddAtStartWithNewNull(subTests);
    appendSupportsAddAtStartWithExistingNull(subTests);
    return subTests;
  }

  private void appendSupportsAddAtStartWithNewNull(List<DynamicTest> subTests) {
    appendSupportsAddAtEndImpl(subTests, null);
  }

  private void appendSupportsAddAtStartWithExistingNull(List<DynamicTest> subTests) {
    ThrowingConsumer<CollectionSize> supportsAddAtEndWithExistingNullElement =
        collectionSize -> {
          List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

          list.add(list.size(), null);
          List<E> expected =
              append(newCollectionWithNullInMiddleOfSize(collectionSize, samples), null);
          assertIterableEquals(expected, list, "Not true that list was appended with null");
        };

    DynamicTest.stream(
            allSupportedCollectionSizesExceptZero.iterator(),
            collectionSize ->
                "Supports List.add(size(), null) on "
                    + stringifyElements(
                        newCollectionWithNullInMiddleOfSize(collectionSize, samples)),
            supportsAddAtEndWithExistingNullElement)
        .forEachOrdered(subTests::add);
  }

  List<DynamicTest> doesNotSupportAddWithIndexSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendDoesNotSupportAddAtEndWithNewElement(subTests);
    appendDoesNotSupportAddAtEndWithExistingElement(subTests);
    return subTests;
  }

  private void appendDoesNotSupportAddAtEndWithNewElement(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtEndImpl(subTests, newElement);
  }

  private void appendDoesNotSupportAddAtEndWithExistingElement(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtEndImpl(subTests, existingElement);
  }

  List<DynamicTest> doesNotSupportAddWithIndexForNullsSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendDoesNotSupportAddAtEndWithNewNull(subTests);
    appendDoesNotSupportAddAtEndWithExistingNull(subTests);
    return subTests;
  }

  private void appendDoesNotSupportAddAtEndWithNewNull(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtEndImpl(subTests, null);
  }

  private void appendDoesNotSupportAddAtEndWithExistingNull(List<DynamicTest> subTests) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddAtEndWithExistingNullElement =
        collectionSize -> {
          List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(list.size(), null),
              "Not true that list.add(null) threw UnsupportedOperationException");
          assertIterableEquals(
              newCollectionWithNullInMiddleOfSize(collectionSize, samples),
              list,
              "Not true that list remained unchanged");
        };

    DynamicTest.stream(
            allSupportedCollectionSizesExceptZero.iterator(),
            collectionSize ->
                "Doesn't support List.add(size(), null) on "
                    + stringifyElements(
                        newCollectionWithNullInMiddleOfSize(collectionSize, samples)),
            doesNotSupportAddAtEndWithExistingNullElement)
        .forEachOrdered(subTests::add);
  }

  private void appendSupportsAddAtEndImpl(List<DynamicTest> subTests, E elementToAdd) {
    ThrowingConsumer<CollectionSize> supportsAddAtEnd =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          list.add(list.size(), elementToAdd);
          List<E> expected = append(newCollectionOfSize(collectionSize, samples), elementToAdd);
          assertIterableEquals(
              expected,
              list,
              () -> "Not true that list was appended with " + stringify(elementToAdd));
        };

    // [].add(size(), E) is already indirectly tested by ListAddAtStartSubTestMaker, because it
    // tests [].add(0, E), and List.size() == 0 for empty lists, so we skip
    // CollectionSize.SUPPORTS_ZERO.
    DynamicTest.stream(
            allSupportedCollectionSizesExceptZero.iterator(),
            collectionSize ->
                "Supports List.add(size(), "
                    + stringify(elementToAdd)
                    + ") on "
                    + stringifyElements(newCollectionOfSize(collectionSize, samples)),
            supportsAddAtEnd)
        .forEachOrdered(subTests::add);
  }

  private void appendDoesNotSupportAddAtEndImpl(List<DynamicTest> subTests, E elementToAdd) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddAtEnd =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(list.size(), elementToAdd),
              () ->
                  "Not true that list.add("
                      + stringify(elementToAdd)
                      + ") threw UnsupportedOperationException");
          assertIterableEquals(
              newCollectionOfSize(collectionSize, samples),
              list,
              "Not true that list remained unchanged");
        };

    // [].add(size(), E) is already indirectly tested by ListAddAtStartSubTestMaker, because it
    // tests [].add(0, E), and List.size() == 0 for empty lists, so we skip
    // CollectionSize.SUPPORTS_ZERO.
    DynamicTest.stream(
            allSupportedCollectionSizesExceptZero.iterator(),
            collectionSize ->
                "Doesn't support List.add(size(), "
                    + stringify(elementToAdd)
                    + ") on "
                    + stringifyElements(newCollectionOfSize(collectionSize, samples)),
            doesNotSupportAddAtEnd)
        .forEachOrdered(subTests::add);
  }
}

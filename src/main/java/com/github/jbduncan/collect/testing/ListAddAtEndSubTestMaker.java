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
    appendTestsForSupportsAddAtEnd(subTests, newElement, /* nullInMiddle= */ false);
    appendTestsForSupportsAddAtEnd(subTests, existingElement, /* nullInMiddle= */ false);
    return subTests;
  }

  List<DynamicTest> supportsAddWithIndexForNullsSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendTestsForSupportsAddAtEnd(subTests, null, /* nullInMiddle= */ false);
    appendTestsForSupportsAddAtEnd(subTests, null, /* nullInMiddle= */ true);
    return subTests;
  }

  private void appendTestsForSupportsAddAtEnd(
      List<DynamicTest> subTests, E elementToAdd, boolean nullInMiddle) {
    ThrowingConsumer<CollectionSize> testTemplate =
        collectionSize -> {
          List<E> list =
              nullInMiddle
                  ? newListToTestWithNullElementInMiddle(generator, collectionSize)
                  : newListToTest(generator, collectionSize);

          list.add(list.size(), elementToAdd);
          List<E> expected =
              nullInMiddle
                  ? append(
                      newCollectionWithNullInMiddleOfSize(collectionSize, samples), elementToAdd)
                  : append(newCollectionOfSize(collectionSize, samples), elementToAdd);
          assertIterableEquals(
              expected,
              list,
              () -> "Not true that list was appended with " + stringify(elementToAdd));
        };

    // [].add(size(), E) is indirectly tested by ListAddAtStartSubTestMaker, so skip
    // CollectionSize.SUPPORTS_ZERO.
    DynamicTest.stream(
            allSupportedCollectionSizesExceptZero.iterator(),
            collectionSize -> {
              String testListToString =
                  nullInMiddle
                      ? stringifyElements(
                          newCollectionWithNullInMiddleOfSize(collectionSize, samples))
                      : stringifyElements(newCollectionOfSize(collectionSize, samples));
              return "Supports List.add(size(), "
                  + stringify(elementToAdd)
                  + ") on "
                  + testListToString;
            },
            testTemplate)
        .forEachOrdered(subTests::add);
  }

  List<DynamicTest> doesNotSupportAddWithIndexSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendTestsForDoesNotSupportAddAtStart(subTests, newElement, /* nullInMiddle= */ false);
    appendTestsForDoesNotSupportAddAtStart(subTests, existingElement, /* nullInMiddle= */ false);
    return subTests;
  }

  List<DynamicTest> doesNotSupportAddWithIndexForNullsSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendTestsForDoesNotSupportAddAtStart(subTests, null, /* nullInMiddle= */ false);
    appendTestsForDoesNotSupportAddAtStart(subTests, null, /* nullInMiddle= */ true);
    return subTests;
  }

  private void appendTestsForDoesNotSupportAddAtStart(
      List<DynamicTest> subTests, E elementToAdd, boolean nullInMiddle) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddAtEnd =
        collectionSize -> {
          List<E> list =
              nullInMiddle
                  ? newListToTestWithNullElementInMiddle(generator, collectionSize)
                  : newListToTest(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(list.size(), elementToAdd),
              // TODO: Should say "Not true that list.add(0, " + stringify(elementToAdd) + ... ?
              () ->
                  "Not true that list.add("
                      + stringify(elementToAdd)
                      + ") threw UnsupportedOperationException");
          assertIterableEquals(
              nullInMiddle
                  ? newCollectionWithNullInMiddleOfSize(collectionSize, samples)
                  : newCollectionOfSize(collectionSize, samples),
              list,
              "Not true that list remained unchanged");
        };

    // [].add(size(), E) is indirectly tested by ListAddAtStartSubTestMaker, so skip
    // CollectionSize.SUPPORTS_ZERO.
    DynamicTest.stream(
            allSupportedCollectionSizesExceptZero.iterator(),
            collectionSize -> {
              String testListToString =
                  nullInMiddle
                      ? stringifyElements(
                          newCollectionWithNullInMiddleOfSize(collectionSize, samples))
                      : stringifyElements(newCollectionOfSize(collectionSize, samples));
              return "Doesn't support List.add(size(), "
                  + stringify(elementToAdd)
                  + ") on "
                  + testListToString;
            },
            doesNotSupportAddAtEnd)
        .forEachOrdered(subTests::add);
  }
}

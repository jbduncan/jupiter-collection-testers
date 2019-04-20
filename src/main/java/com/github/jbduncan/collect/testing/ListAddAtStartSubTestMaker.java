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
import static java.util.stream.Collectors.toList;
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

  private final TestListGenerator<E> generator;
  private final SampleElements<E> samples;
  private final E newElement;
  private final E existingElement;
  private final Set<CollectionSize> allSupportedCollectionSizes;
  private final Set<CollectionSize> allSupportedCollectionSizesExceptZero;

  ListAddAtStartSubTestMaker(
      TestListGenerator<E> generator,
      SampleElements<E> samples,
      E newElement,
      E existingElement,
      Set<CollectionSize> allSupportedCollectionSizes,
      Set<CollectionSize> allSupportedCollectionSizesExceptZero) {
    this.generator = requireNonNull(generator, "generator");
    this.samples = requireNonNull(samples, "samples");
    this.newElement = requireNonNull(newElement, "newElement");
    this.existingElement = requireNonNull(existingElement, "existingElement");
    this.allSupportedCollectionSizes =
        requireNonNull(allSupportedCollectionSizes, "allSupportedCollectionSizes");
    this.allSupportedCollectionSizesExceptZero =
        requireNonNull(
            allSupportedCollectionSizesExceptZero, "allSupportedCollectionSizesExceptZero");
  }

  List<DynamicTest> supportsAddWithIndexSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendTestsForSupportsAddAtStart(
        subTests, //
        newElement,
        allSupportedCollectionSizes,
        /* nullInMiddle= */ false);
    appendTestsForSupportsAddAtStart(
        subTests,
        existingElement,
        allSupportedCollectionSizesExceptZero,
        /* nullInMiddle= */ false);
    return subTests;
  }

  List<DynamicTest> supportsAddWithIndexForNullsSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendTestsForSupportsAddAtStart(
        subTests, null, allSupportedCollectionSizes, /* nullInMiddle= */ false);
    appendTestsForSupportsAddAtStart(
        subTests, null, allSupportedCollectionSizesExceptZero, /* nullInMiddle= */ true);
    return subTests;
  }

  List<DynamicTest> doesNotSupportAddWithIndexSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendDoesNotSupportAddAtStartImpl(
        subTests, //
        newElement,
        allSupportedCollectionSizes,
        /* nullInMiddle= */ false);
    appendDoesNotSupportAddAtStartImpl(
        subTests,
        existingElement,
        allSupportedCollectionSizesExceptZero,
        /* nullInMiddle= */ false);
    return subTests;
  }

  List<DynamicTest> doesNotSupportAddWithIndexForNullsSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendDoesNotSupportAddAtStartImpl(
        subTests, null, allSupportedCollectionSizes, /* nullInMiddle= */ false);
    appendDoesNotSupportAddAtStartImpl(
        subTests, null, allSupportedCollectionSizesExceptZero, /* nullInMiddle= */ true);
    return subTests;
  }

  private void appendTestsForSupportsAddAtStart(
      List<DynamicTest> subTests,
      E elementToAdd,
      Set<CollectionSize> supportedCollectionSizes,
      boolean nullInMiddle) {
    ThrowingConsumer<CollectionSize> test =
        collectionSize -> {
          List<E> list =
              nullInMiddle
                  ? newListToTestWithNullElementInMiddle(generator, collectionSize)
                  : newListToTest(generator, collectionSize);

          list.add(0, elementToAdd);
          Iterable<E> expected =
              nullInMiddle
                  ? prepend(
                      elementToAdd, newCollectionWithNullInMiddleOfSize(collectionSize, samples))
                  : prepend(elementToAdd, newCollectionOfSize(collectionSize, samples));
          assertIterableEquals(
              expected,
              list,
              () -> "Not true that list was prepended with " + stringify(elementToAdd));
        };

    DynamicTest.stream(
            supportedCollectionSizes.iterator(),
            collectionSize -> {
              String testListToString =
                  nullInMiddle
                      ? stringifyElements(
                          newCollectionWithNullInMiddleOfSize(collectionSize, samples))
                      : stringifyElements(newCollectionOfSize(collectionSize, samples));
              return "Supports List.add(0, " + stringify(elementToAdd) + ") on " + testListToString;
            },
            test)
        .forEachOrdered(subTests::add);
  }

  private void appendDoesNotSupportAddAtStartImpl(
      List<DynamicTest> subTests,
      E elementToAdd,
      Set<CollectionSize> supportedCollectionSizes,
      boolean nullInMiddle) {
    ThrowingConsumer<CollectionSize> test =
        collectionSize -> {
          List<E> list =
              nullInMiddle
                  ? newListToTestWithNullElementInMiddle(generator, collectionSize)
                  : newListToTest(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(0, elementToAdd),
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

    DynamicTest.stream(
            supportedCollectionSizes.iterator(),
            collectionSize -> {
              String testListToString =
                  nullInMiddle
                      ? stringifyElements(
                          newCollectionWithNullInMiddleOfSize(collectionSize, samples))
                      : stringifyElements(newCollectionOfSize(collectionSize, samples));
              return "Doesn't support List.add(0, "
                  + stringify(elementToAdd)
                  + ") on "
                  + testListToString;
            },
            test)
        .forEachOrdered(subTests::add);
  }

  List<DynamicTest> failsFastOnConcurrentModificationSubTests() {
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

    return DynamicTest.stream(
            allSupportedCollectionSizes.iterator(),
            collectionSize ->
                "List.add(0, "
                    + stringify(newElement)
                    + ") fails fast when concurrently modifying "
                    + stringifyElements(newCollectionOfSize(collectionSize, samples)),
            failsFastOnCme)
        .collect(toList());
  }

  List<DynamicTest> failsFastOnConcurrentModificationInvolvingNullElementSubTests() {
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

    return DynamicTest.stream(
            allSupportedCollectionSizes.iterator(),
            collectionSize ->
                "List.add(0, null) fails fast when concurrently modifying "
                    + stringifyElements(newCollectionOfSize(collectionSize, samples)),
            failsFastOnCme)
        .collect(toList());
  }
}

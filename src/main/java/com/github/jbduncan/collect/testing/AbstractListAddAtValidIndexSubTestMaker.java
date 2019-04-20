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

abstract class AbstractListAddAtValidIndexSubTestMaker<E> {

  private final TestListGenerator<E> generator;
  private final SampleElements<E> samples;
  private final E newElement;
  private final E existingElement;
  private final Set<CollectionSize> allSupportedCollectionSizes;
  private final Set<CollectionSize> allSupportedCollectionSizesExceptZero;

  AbstractListAddAtValidIndexSubTestMaker(
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

  abstract int index(int listSize);

  abstract String indexName();

  List<DynamicTest> supportsAddWithIndexSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendTestsForSupportsAddAtStart(
        subTests,
        newElement,
        (this instanceof ListAddWithIndexTester.ListAddAtStartSubTestMaker)
            ? allSupportedCollectionSizes
            : allSupportedCollectionSizesExceptZero,
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
        subTests,
        null,
        (this instanceof ListAddWithIndexTester.ListAddAtStartSubTestMaker)
            ? allSupportedCollectionSizes
            : allSupportedCollectionSizesExceptZero,
        /* nullInMiddle= */ false);
    appendTestsForSupportsAddAtStart(
        subTests, null, allSupportedCollectionSizesExceptZero, /* nullInMiddle= */ true);
    return subTests;
  }

  private void appendTestsForSupportsAddAtStart(
      List<DynamicTest> subTests,
      E elementToAdd,
      Set<CollectionSize> supportedCollectionSizes,
      boolean nullInMiddle) {
    ThrowingConsumer<CollectionSize> testTemplate =
        collectionSize -> {
          List<E> list =
              nullInMiddle
                  ? newListToTestWithNullElementInMiddle(generator, collectionSize)
                  : newListToTest(generator, collectionSize);
          int index = index(collectionSize.size());

          list.add(index, elementToAdd);
          Iterable<E> expected =
              nullInMiddle
                  ? insert(
                      newCollectionWithNullInMiddleOfSize(collectionSize, samples),
                      index,
                      elementToAdd)
                  : insert(newCollectionOfSize(collectionSize, samples), index, elementToAdd);

          assertIterableEquals(
              expected,
              list,
              () ->
                  "Not true that "
                      + stringify(elementToAdd)
                      + " was inserted at index "
                      + index
                      + " of list");
        };

    DynamicTest.stream(
            supportedCollectionSizes.iterator(),
            collectionSize -> {
              String testListToString =
                  nullInMiddle
                      ? stringifyElements(
                          newCollectionWithNullInMiddleOfSize(collectionSize, samples))
                      : stringifyElements(newCollectionOfSize(collectionSize, samples));
              return "Supports List.add("
                  + indexName()
                  + ", "
                  + stringify(elementToAdd)
                  + ") on "
                  + testListToString;
            },
            testTemplate)
        .forEachOrdered(subTests::add);
  }

  List<DynamicTest> doesNotSupportAddWithIndexSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendTestsForDoesNotSupportAddAtStart(
        subTests,
        newElement,
        (this instanceof ListAddWithIndexTester.ListAddAtStartSubTestMaker)
            ? allSupportedCollectionSizes
            : allSupportedCollectionSizesExceptZero,
        /* nullInMiddle= */ false);
    appendTestsForDoesNotSupportAddAtStart(
        subTests,
        existingElement,
        allSupportedCollectionSizesExceptZero,
        /* nullInMiddle= */ false);
    return subTests;
  }

  List<DynamicTest> doesNotSupportAddWithIndexForNullsSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendTestsForDoesNotSupportAddAtStart(
        subTests,
        null,
        (this instanceof ListAddWithIndexTester.ListAddAtStartSubTestMaker)
            ? allSupportedCollectionSizes
            : allSupportedCollectionSizesExceptZero,
        /* nullInMiddle= */ false);
    appendTestsForDoesNotSupportAddAtStart(
        subTests, null, allSupportedCollectionSizesExceptZero, /* nullInMiddle= */ true);
    return subTests;
  }

  private void appendTestsForDoesNotSupportAddAtStart(
      List<DynamicTest> subTests,
      E elementToAdd,
      Set<CollectionSize> supportedCollectionSizes,
      boolean nullInMiddle) {
    ThrowingConsumer<CollectionSize> testTemplate =
        collectionSize -> {
          List<E> list =
              nullInMiddle
                  ? newListToTestWithNullElementInMiddle(generator, collectionSize)
                  : newListToTest(generator, collectionSize);
          int index = index(collectionSize.size());

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(index, elementToAdd),
              // TODO: Should say "Not true that list.add(" + index + ", " + stringify(elementToAdd)
              // + ... ?
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
              return "Doesn't support List.add("
                  + indexName()
                  + ", "
                  + stringify(elementToAdd)
                  + ") on "
                  + testListToString;
            },
            testTemplate)
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

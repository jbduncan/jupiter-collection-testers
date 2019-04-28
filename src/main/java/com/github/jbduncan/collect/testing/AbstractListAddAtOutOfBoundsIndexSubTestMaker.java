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

import static com.github.jbduncan.collect.testing.Helpers.newIterable;
import static com.github.jbduncan.collect.testing.Helpers.stringify;
import static com.github.jbduncan.collect.testing.Helpers.stringifyElements;
import static com.github.jbduncan.collect.testing.ListContractHelpers.newTestList;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.ThrowingConsumer;

abstract class AbstractListAddAtOutOfBoundsIndexSubTestMaker<E> {

  private final TestListGenerator<E> generator;
  private final SampleElements<E> samples;
  private final E newElement;
  private final E existingElement;
  private final Set<CollectionSize> allSupportedCollectionSizes;
  private final Set<CollectionSize> allSupportedCollectionSizesExceptZero;
  private final Class<? extends Throwable> expectedExceptionType;

  AbstractListAddAtOutOfBoundsIndexSubTestMaker(
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

  abstract int index(CollectionSize listSize);

  abstract String indexName();

  List<DynamicTest> doesNotSupportAddWithIndexSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendTestsForDoesNotSupportAddAtPlusOne(
        subTests, newElement, allSupportedCollectionSizes, /* nullInMiddle= */ false);
    appendTestsForDoesNotSupportAddAtPlusOne(
        subTests,
        existingElement,
        allSupportedCollectionSizesExceptZero,
        /* nullInMiddle= */ false);
    return subTests;
  }

  List<DynamicTest> doesNotSupportAddWithIndexForNullsSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendTestsForDoesNotSupportAddAtPlusOne(
        subTests, null, allSupportedCollectionSizes, /* nullInMiddle= */ false);
    appendTestsForDoesNotSupportAddAtPlusOne(
        subTests, null, allSupportedCollectionSizesExceptZero, /* nullInMiddle= */ true);
    return subTests;
  }

  private void appendTestsForDoesNotSupportAddAtPlusOne(
      List<DynamicTest> subTests,
      E elementToAdd,
      Set<CollectionSize> supportedCollectionSizes,
      boolean nullInMiddle) {
    ThrowingConsumer<CollectionSize> testTemplate =
        collectionSize -> {
          List<E> list = newTestList(generator, collectionSize, nullInMiddle);

          assertThrows(
              expectedExceptionType,
              () -> list.add(index(collectionSize), elementToAdd),
              () ->
                  "Not true that list.add("
                      + index(collectionSize)
                      + ", "
                      + stringify(elementToAdd)
                      + ") threw exception of type "
                      + expectedExceptionType);

          assertIterableEquals(
              newIterable(samples, collectionSize, nullInMiddle),
              list,
              "Not true that list remained unchanged");
        };

    DynamicTest.stream(
            supportedCollectionSizes.iterator(),
            collectionSize ->
                "Doesn't support List.add("
                    + indexName()
                    + ", "
                    + stringify(elementToAdd)
                    + ") on "
                    + stringifyElements(newIterable(samples, collectionSize, nullInMiddle)),
            testTemplate)
        .forEachOrdered(subTests::add);
  }
}

/*
 * Copyright 2018 the Jupiter Collection Testers authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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

final class ListAddAtSizePlusOneSubTestMaker<E> extends BaseListSubTestMaker<E> {
  private static final String INDEX_TO_ADD_AT = "size() + 1";

  private final TestListGenerator<E> generator;
  private final E newElement;
  private final E existingElement;
  private final Set<CollectionSize> allSupportedCollectionSizes;
  private final Class<? extends Throwable> expectedExceptionType;

  private ListAddAtSizePlusOneSubTestMaker(Builder<E> builder) {
    super(builder.sampleElements, builder.allSupportedCollectionSizesExceptZero);
    this.generator = requireNonNull(builder.testListGenerator, "testListGenerator");
    this.newElement = requireNonNull(builder.newElement, "newElement");
    this.existingElement = requireNonNull(builder.existingElement, "existingElement");
    this.allSupportedCollectionSizes =
        requireNonNull(builder.allSupportedCollectionSizes, "allSupportedCollectionSizes");
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
                  String.format(
                      ListContractConstants
                          .FORMAT_NOT_TRUE_THAT_LIST_ADD_INT_E_THREW_EXPECTED_EXCEPTION_TYPE,
                      list.size() + 1,
                      "null",
                      expectedExceptionType));
          assertIterableEquals(
              newCollectionWithNullInMiddleOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTestsForListWithNullElement(
        ListContractConstants.FORMAT_DOESNT_SUPPORT_LIST_ADD_WITH_INDEX,
        INDEX_TO_ADD_AT,
        doesNotSupportAddAtSizePlusOneWithExistingNullElement,
        subTests);
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
                  String.format(
                      ListContractConstants
                          .FORMAT_NOT_TRUE_THAT_LIST_ADD_INT_E_THREW_EXPECTED_EXCEPTION_TYPE,
                      list.size() + 1,
                      stringify(elementToAdd),
                      expectedExceptionType));

          assertIterableEquals(
              newCollectionOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTests(
        supportedCollectionSizes,
        ListContractConstants.FORMAT_DOESNT_SUPPORT_LIST_ADD_WITH_INDEX,
        INDEX_TO_ADD_AT,
        elementToAdd,
        doesNotSupportAddAtSizePlusOne,
        subTests);
  }
}

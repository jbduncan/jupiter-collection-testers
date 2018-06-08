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
import static com.github.jbduncan.collect.testing.Helpers.quote;
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

final class ListAddAtMinusOneSubTestMaker<E> extends BaseListSubTestMaker<E> {
  private Class<? extends Throwable> expectedExceptionType;

  private ListAddAtMinusOneSubTestMaker(Builder<E> builder) {
    super(
        builder.testListGenerator,
        builder.newElement,
        builder.existingElement,
        builder.sampleElements,
        builder.allSupportedCollectionSizes,
        builder.allSupportedCollectionSizesExceptZero);
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

    ListAddAtMinusOneSubTestMaker<E> build() {
      return new ListAddAtMinusOneSubTestMaker<>(this);
    }
  }

  List<DynamicTest> doesNotSupportAddWithIndexSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendDoesNotSupportAddAtMinusOneWithNewElement(subTests);
    appendDoesNotSupportAddAtMinusOneWithExistingElement(subTests);
    return subTests;
  }

  private void appendDoesNotSupportAddAtMinusOneWithNewElement(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtMinusOneImpl(
        subTests,
        newElement,
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_MINUS1_E_WITH_NEW_ELEMENT);
  }

  private void appendDoesNotSupportAddAtMinusOneWithExistingElement(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtMinusOneImpl(
        subTests,
        existingElement,
        allSupportedCollectionSizesExceptZero,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_MINUS1_E_WITH_EXISTING_ELEMENT);
  }

  List<DynamicTest> doesNotSupportAddWithIndexForNullsSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendDoesNotSupportAddAtMinusOneWithNewNull(subTests);
    appendDoesNotSupportAddAtMinusOneWithExistingNull(subTests);
    return subTests;
  }

  private void appendDoesNotSupportAddAtMinusOneWithNewNull(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtMinusOneImpl(
        subTests,
        null,
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_MINUS1_E_WITH_NEW_NULL_ELEMENT);
  }

  private void appendDoesNotSupportAddAtMinusOneWithExistingNull(List<DynamicTest> subTests) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddAtMinusOneWithExistingNullElement =
        collectionSize -> {
          List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

          assertThrows(
              expectedExceptionType,
              () -> list.add(-1, null),
              () ->
                  String.format(
                      ListContractConstants
                          .FORMAT_NOT_TRUE_THAT_LIST_ADD_INT_E_THREW_EXPECTED_EXCEPTION_TYPE,
                      -1,
                      ListContractConstants.NULL,
                      expectedExceptionType));
          assertIterableEquals(
              newCollectionWithNullInMiddleOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTestsForListWithNullElement(
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_MINUS1_E_WITH_EXISTING_NULL_ELEMENT,
        doesNotSupportAddAtMinusOneWithExistingNullElement,
        subTests);
  }

  private void appendDoesNotSupportAddAtMinusOneImpl(
      List<DynamicTest> subTests,
      E elementToAdd,
      Set<CollectionSize> supportedCollectionSizes,
      String displayNameFormat) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddAtMinusOne =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              expectedExceptionType,
              () -> list.add(-1, elementToAdd),
              () ->
                  String.format(
                      ListContractConstants
                          .FORMAT_NOT_TRUE_THAT_LIST_ADD_INT_E_THREW_EXPECTED_EXCEPTION_TYPE,
                      -1,
                      (elementToAdd == null) ? "null" : quote(elementToAdd),
                      expectedExceptionType));
          assertIterableEquals(
              newCollectionOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTests(
        supportedCollectionSizes, displayNameFormat, doesNotSupportAddAtMinusOne, subTests);
  }
}

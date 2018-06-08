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

import static com.github.jbduncan.collect.testing.Helpers.append;
import static com.github.jbduncan.collect.testing.Helpers.newCollectionOfSize;
import static com.github.jbduncan.collect.testing.Helpers.newCollectionWithNullInMiddleOfSize;
import static com.github.jbduncan.collect.testing.Helpers.quote;
import static com.github.jbduncan.collect.testing.ListContractHelpers.newListToTest;
import static com.github.jbduncan.collect.testing.ListContractHelpers.newListToTestWithNullElementInMiddle;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.ThrowingConsumer;

final class ListAddAtEndSubTestMaker<E> extends BaseListSubTestMaker<E> {
  private ListAddAtEndSubTestMaker(Builder<E> builder) {
    super(
        builder.testListGenerator,
        builder.newElement,
        builder.existingElement,
        builder.sampleElements,
        builder.allSupportedCollectionSizes,
        builder.allSupportedCollectionSizesExceptZero);
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

    ListAddAtEndSubTestMaker<E> build() {
      return new ListAddAtEndSubTestMaker<>(this);
    }
  }

  List<DynamicTest> supportsAddWithIndexSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendSupportsAddAtEndWithNewElement(subTests);
    appendSupportsAddAtEndWithExistingElement(subTests);
    return subTests;
  }

  private void appendSupportsAddAtEndWithNewElement(List<DynamicTest> subTests) {
    appendSupportsAddAtEndImpl(
        subTests,
        newElement,
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_SIZE_E_WITH_NEW_ELEMENT);
  }

  private void appendSupportsAddAtEndWithExistingElement(List<DynamicTest> subTests) {
    appendSupportsAddAtEndImpl(
        subTests,
        existingElement,
        allSupportedCollectionSizesExceptZero,
        ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_SIZE_E_WITH_EXISTING_ELEMENT);
  }

  List<DynamicTest> supportsAddWithIndexForNullsSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendSupportsAddAtStartWithNewNull(subTests);
    appendSupportsAddAtStartWithExistingNull(subTests);
    return subTests;
  }

  private void appendSupportsAddAtStartWithNewNull(List<DynamicTest> subTests) {
    appendSupportsAddAtEndImpl(
        subTests,
        null,
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_SIZE_E_WITH_NEW_NULL_ELEMENT);
  }

  private void appendSupportsAddAtStartWithExistingNull(List<DynamicTest> subTests) {
    ThrowingConsumer<CollectionSize> supportsAddAtEndWithExistingNullElement =
        collectionSize -> {
          List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

          list.add(list.size(), null);
          List<E> expected =
              append(newCollectionWithNullInMiddleOfSize(collectionSize, samples), null);
          assertIterableEquals(
              expected, list, ListContractConstants.NOT_TRUE_THAT_LIST_WAS_APPENDED_WITH_NULL);
        };

    addDynamicSubTestsForListWithNullElement(
        ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_SIZE_E_WITH_EXISTING_NULL_ELEMENT,
        supportsAddAtEndWithExistingNullElement,
        subTests);
  }

  List<DynamicTest> doesNotSupportAddWithIndexSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendDoesNotSupportAddAtEndWithNewElement(subTests);
    appendDoesNotSupportAddAtEndWithExistingElement(subTests);
    return subTests;
  }

  private void appendDoesNotSupportAddAtEndWithNewElement(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtEndImpl(
        subTests,
        newElement,
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_SIZE_E_WITH_NEW_ELEMENT);
  }

  private void appendDoesNotSupportAddAtEndWithExistingElement(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtEndImpl(
        subTests,
        existingElement,
        allSupportedCollectionSizesExceptZero,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_SIZE_E_WITH_EXISTING_ELEMENT);
  }

  private void appendSupportsAddAtEndImpl(
      List<DynamicTest> subTests,
      E elementToAdd,
      Set<CollectionSize> supportedCollectionSizes,
      String displayNameFormat) {
    ThrowingConsumer<CollectionSize> supportsAddAtEnd =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          list.add(list.size(), elementToAdd);
          List<E> expected = append(newCollectionOfSize(collectionSize, samples), elementToAdd);
          assertIterableEquals(
              expected,
              list,
              () ->
                  String.format(
                      ListContractConstants.FORMAT_NOT_TRUE_THAT_LIST_WAS_APPENDED,
                      (elementToAdd == null) ? "null" : quote(elementToAdd)));
        };

    addDynamicSubTests(supportedCollectionSizes, displayNameFormat, supportsAddAtEnd, subTests);
  }

  private void appendDoesNotSupportAddAtEndImpl(
      List<DynamicTest> subTests,
      E elementToAdd,
      Set<CollectionSize> supportedCollectionSizes,
      String displayNameFormat) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddAtEnd =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(list.size(), elementToAdd),
              () ->
                  String.format(
                      ListContractConstants
                          .FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION,
                      (elementToAdd == null) ? "null" : quote(elementToAdd)));
          assertIterableEquals(
              newCollectionOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTests(
        supportedCollectionSizes, displayNameFormat, doesNotSupportAddAtEnd, subTests);
  }
}

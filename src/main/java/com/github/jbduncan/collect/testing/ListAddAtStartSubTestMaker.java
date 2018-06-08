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
import static com.github.jbduncan.collect.testing.Helpers.prepend;
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

final class ListAddAtStartSubTestMaker<E> extends BaseListSubTestMaker<E> {
  private ListAddAtStartSubTestMaker(Builder<E> builder) {
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

  static class Builder<E> {
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

    ListAddAtStartSubTestMaker<E> build() {
      return new ListAddAtStartSubTestMaker<>(this);
    }
  }

  List<DynamicTest> supportsAddWithIndexSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendSupportsAddAtStartWithNewElement(subTests);
    appendSupportsAddAtStartWithExistingElement(subTests);
    return subTests;
  }

  private void appendSupportsAddAtStartWithNewElement(List<DynamicTest> subTests) {
    appendSupportsAddAtStartImpl(
        subTests,
        newElement,
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_0_E_WITH_NEW_ELEMENT);
  }

  private void appendSupportsAddAtStartWithExistingElement(List<DynamicTest> subTests) {
    appendSupportsAddAtStartImpl(
        subTests,
        existingElement,
        allSupportedCollectionSizesExceptZero,
        ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_0_E_WITH_EXISTING_ELEMENT);
  }

  List<DynamicTest> supportsAddWithIndexForNullsSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendSupportsAddAtStartWithNewNull(subTests);
    appendSupportsAddAtStartWithExistingNull(subTests);
    return subTests;
  }

  private void appendSupportsAddAtStartWithNewNull(List<DynamicTest> subTests) {
    appendSupportsAddAtStartImpl(
        subTests,
        null,
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_0_E_WITH_NEW_NULL_ELEMENT);
  }

  private void appendSupportsAddAtStartWithExistingNull(List<DynamicTest> subTests) {
    ThrowingConsumer<CollectionSize> supportsAddAtStartWithExistingNullElement =
        collectionSize -> {
          List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

          list.add(0, null);
          List<E> expected =
              prepend(null, newCollectionWithNullInMiddleOfSize(collectionSize, samples));
          assertIterableEquals(
              expected, list, ListContractConstants.NOT_TRUE_THAT_LIST_WAS_PREPENDED_WITH_NULL);
        };

    addDynamicSubTestsForListWithNullElement(
        ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_0_E_WITH_EXISTING_NULL_ELEMENT,
        supportsAddAtStartWithExistingNullElement,
        subTests);
  }

  List<DynamicTest> doesNotSupportAddWithIndexSubTests() {
    List<DynamicTest> subTests = new ArrayList<>();
    appendDoesNotSupportAddAtStartWithNewElement(subTests);
    appendDoesNotSupportAddAtStartWithExistingElement(subTests);
    return subTests;
  }

  private void appendDoesNotSupportAddAtStartWithNewElement(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtStartImpl(
        subTests,
        newElement,
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_0_E_WITH_NEW_ELEMENT);
  }

  private void appendDoesNotSupportAddAtStartWithExistingElement(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtStartImpl(
        subTests,
        existingElement,
        allSupportedCollectionSizesExceptZero,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_0_E_WITH_EXISTING_ELEMENT);
  }

  private void appendSupportsAddAtStartImpl(
      List<DynamicTest> subTests,
      E elementToAdd,
      Set<CollectionSize> supportedCollectionSizes,
      String displayNameFormat) {
    ThrowingConsumer<CollectionSize> supportsAddAtStart =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          list.add(0, elementToAdd);
          List<E> expected = prepend(elementToAdd, newCollectionOfSize(collectionSize, samples));
          assertIterableEquals(
              expected,
              list,
              () ->
                  String.format(
                      ListContractConstants.FORMAT_NOT_TRUE_THAT_LIST_WAS_PREPENDED,
                      (elementToAdd == null) ? "null" : quote(elementToAdd)));
        };

    addDynamicSubTests(supportedCollectionSizes, displayNameFormat, supportsAddAtStart, subTests);
  }

  private void appendDoesNotSupportAddAtStartImpl(
      List<DynamicTest> subTests,
      E elementToAdd,
      Set<CollectionSize> supportedCollectionSizes,
      String displayNameFormat) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddAtStart =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(0, elementToAdd),
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
        supportedCollectionSizes, displayNameFormat, doesNotSupportAddAtStart, subTests);
  }
}

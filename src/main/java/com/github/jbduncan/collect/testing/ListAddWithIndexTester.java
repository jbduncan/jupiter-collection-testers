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
import static com.github.jbduncan.collect.testing.Helpers.extractConcreteSizes;
import static com.github.jbduncan.collect.testing.Helpers.insert;
import static com.github.jbduncan.collect.testing.Helpers.minus;
import static com.github.jbduncan.collect.testing.Helpers.newCollectionOfSize;
import static com.github.jbduncan.collect.testing.Helpers.newCollectionWithNullInMiddleOfSize;
import static com.github.jbduncan.collect.testing.Helpers.quote;
import static com.github.jbduncan.collect.testing.ListContractHelpers.middleIndex;
import static com.github.jbduncan.collect.testing.ListContractHelpers.newListToTest;
import static com.github.jbduncan.collect.testing.ListContractHelpers.newListToTestWithNullElementInMiddle;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.ThrowingConsumer;

final class ListAddWithIndexTester<E> {
  private final TestListGenerator<E> generator;
  private final SampleElements<E> samples;
  private final E newElement;
  private final E existingElement;
  private final Set<Feature<?>> features;
  private final Set<CollectionSize> allSupportedCollectionSizes;
  private final Set<CollectionSize> allSupportedCollectionSizesExceptZero;

  private ListAddWithIndexTester(TestListGenerator<E> testListGenerator, Set<Feature<?>> features) {
    this.generator = requireNonNull(testListGenerator, "testListGenerator");
    this.samples = requireNonNull(testListGenerator.samples(), "samples");
    this.newElement = samples.e3();
    this.existingElement = samples.e0();
    this.features = requireNonNull(features, "features");
    this.allSupportedCollectionSizes = extractConcreteSizes(features);
    this.allSupportedCollectionSizesExceptZero =
        minus(allSupportedCollectionSizes, CollectionSize.SUPPORTS_ZERO);
  }

  static <E> Builder<E> builder() {
    return new Builder<>();
  }

  static final class Builder<E> {
    private Builder() {}

    private TestListGenerator<E> testListGenerator;
    private Set<Feature<?>> features;

    Builder<E> testListGenerator(TestListGenerator<E> testListGenerator) {
      this.testListGenerator = testListGenerator;
      return this;
    }

    public Builder<E> features(Set<Feature<?>> features) {
      this.features = features;
      return this;
    }

    ListAddWithIndexTester<E> build() {
      return new ListAddWithIndexTester<>(testListGenerator, features);
    }
  }

  List<DynamicNode> dynamicTestsGraph() {
    List<DynamicNode> tests = new ArrayList<>();
    generateSupportsAddWithIndexTests(tests);
    generateSupportsAddWithIndexWithNullElementsTests(tests);
    generateDoesNotSupportAddWithIndexTests(tests);
    generateDoesNotSupportAddWithIndexWithNullElementsTests(tests);
    return Collections.unmodifiableList(tests);
  }

  private void generateSupportsAddWithIndexTests(List<DynamicNode> tests) {
    if (features.contains(ListFeature.SUPPORTS_ADD_WITH_INDEX)) {
      ListAddAtStartSubTestMaker<E> listAddAtStartSubTestMaker =
          ListAddAtStartSubTestMaker.<E>builder()
              .testListGenerator(generator)
              .sampleElements(samples)
              .newElement(newElement)
              .existingElement(existingElement)
              .allSupportedCollectionSizes(allSupportedCollectionSizes)
              .allSupportedCollectionSizesExceptZero(allSupportedCollectionSizesExceptZero)
              .build();
      ListAddAtEndSubTestMaker<E> listAddAtEndSubTestMaker =
          ListAddAtEndSubTestMaker.<E>builder()
              .testListGenerator(generator)
              .sampleElements(samples)
              .newElement(newElement)
              .existingElement(existingElement)
              .allSupportedCollectionSizes(allSupportedCollectionSizes)
              .allSupportedCollectionSizesExceptZero(allSupportedCollectionSizesExceptZero)
              .build();
      ListAddAtMiddleSubTestMaker<E> listAddAtMiddleSubTestMaker =
          ListAddAtMiddleSubTestMaker.<E>builder()
              .testListGenerator(generator)
              .sampleElements(samples)
              .newElement(newElement)
              .existingElement(existingElement)
              .allSupportedCollectionSizesExceptZero(allSupportedCollectionSizesExceptZero)
              .build();
      ListAddAtMinusOneSubTestMaker<E> listAddAtMinusOneSubTestMaker =
          ListAddAtMinusOneSubTestMaker.<E>builder()
              .testListGenerator(generator)
              .sampleElements(samples)
              .newElement(newElement)
              .existingElement(existingElement)
              .allSupportedCollectionSizes(allSupportedCollectionSizes)
              .allSupportedCollectionSizesExceptZero(allSupportedCollectionSizesExceptZero)
              .expectedExceptionType(IndexOutOfBoundsException.class)
              .build();
      ListAddAtSizePlusOneSubTestMaker<E> listAddAtSizePlusOneSubTestMaker =
          ListAddAtSizePlusOneSubTestMaker.<E>builder()
              .testListGenerator(generator)
              .sampleElements(samples)
              .newElement(newElement)
              .existingElement(existingElement)
              .allSupportedCollectionSizes(allSupportedCollectionSizes)
              .allSupportedCollectionSizesExceptZero(allSupportedCollectionSizesExceptZero)
              .expectedExceptionType(IndexOutOfBoundsException.class)
              .build();

      List<DynamicTest> subTests = new ArrayList<>();
      subTests.addAll(listAddAtStartSubTestMaker.supportsAddWithIndexSubTests());
      subTests.addAll(listAddAtEndSubTestMaker.supportsAddWithIndexSubTests());
      subTests.addAll(listAddAtMiddleSubTestMaker.supportsAddWithIndexSubTests());
      subTests.addAll(listAddAtMinusOneSubTestMaker.doesNotSupportAddWithIndexSubTests());
      subTests.addAll(listAddAtSizePlusOneSubTestMaker.doesNotSupportAddWithIndexSubTests());

      tests.add(dynamicContainer(ListContractConstants.SUPPORTS_LIST_ADD_INT_E, subTests));

      if (!features.contains(CollectionFeature.ALLOWS_NULL_VALUES)) {
        List<DynamicTest> innerSubTests = new ArrayList<>();
        appendDoesNotSupportAddAtStartWithNewNullElementTests(innerSubTests);
        appendDoesNotSupportAddAtEndWithNewNullElementTests(innerSubTests);
        appendDoesNotSupportAddAtMiddleWithNewNullElementTests(innerSubTests);

        tests.add(
            dynamicContainer(
                ListContractConstants.DOES_NOT_SUPPORT_LIST_ADD_INT_E_WITH_NEW_NULL_ELEMENT,
                innerSubTests));
      }
    }
  }

  private void generateSupportsAddWithIndexWithNullElementsTests(List<DynamicNode> tests) {
    if (features.containsAll(
        Arrays.asList(ListFeature.SUPPORTS_ADD_WITH_INDEX, CollectionFeature.ALLOWS_NULL_VALUES))) {
      ListAddAtStartSubTestMaker<E> listAddAtStartSubTestMaker =
          ListAddAtStartSubTestMaker.<E>builder()
              .testListGenerator(generator)
              .sampleElements(samples)
              .newElement(newElement)
              .existingElement(existingElement)
              .allSupportedCollectionSizes(allSupportedCollectionSizes)
              .allSupportedCollectionSizesExceptZero(allSupportedCollectionSizesExceptZero)
              .build();

      List<DynamicTest> subTests = new ArrayList<>();
      subTests.addAll(listAddAtStartSubTestMaker.supportsAddWithIndexForNullsSubTests());

      appendSupportsAddAtEndWithNewNullElementTests(subTests);
      appendSupportsAddAtMiddleWithNewNullElementTests(subTests);
      appendSupportsAddAtEndWithExistingNullElementTests(subTests);
      appendSupportsAddAtMiddleWithExistingNullElementTests(subTests);
      appendDoesNotSupportAddAtMinusOneWithNewNullElementTests(
          subTests, IndexOutOfBoundsException.class);
      appendDoesNotSupportAddAtMinusOneWithExistingNullElementTests(
          subTests, IndexOutOfBoundsException.class);
      appendDoesNotSupportAddAtSizePlusOneWithNewNullElementTests(
          subTests, IndexOutOfBoundsException.class);
      appendDoesNotSupportAddAtSizePlusOneWithExistingNullElementTests(
          subTests, IndexOutOfBoundsException.class);

      tests.add(
          dynamicContainer(
              ListContractConstants.SUPPORTS_LIST_ADD_INT_E_WITH_NULL_ELEMENT, subTests));
    }
  }

  private void generateDoesNotSupportAddWithIndexTests(List<DynamicNode> tests) {
    if (!features.contains(ListFeature.SUPPORTS_ADD_WITH_INDEX)) {
      List<DynamicTest> subTests = new ArrayList<>();

      appendDoesNotSupportAddAtStartWithNewElementTests(subTests);
      appendDoesNotSupportAddAtStartWithExistingElementTests(subTests);
      appendDoesNotSupportAddAtEndWithNewElementTests(subTests);
      appendDoesNotSupportAddAtEndWithExistingElementTests(subTests);
      appendDoesNotSupportAddAtMiddleWithNewElementTests(subTests);
      appendDoesNotSupportAddAtMiddleWithExistingTests(subTests);
      appendDoesNotSupportAddAtMinusOneWithNewElementTests(
          subTests, UnsupportedOperationException.class);
      appendDoesNotSupportAddAtMinusOneWithExistingElementTests(
          subTests, UnsupportedOperationException.class);
      appendDoesNotSupportAddAtSizePlusOneWithNewElementTests(
          subTests, UnsupportedOperationException.class);
      appendDoesNotSupportAddAtSizePlusOneWithExistingElementTests(
          subTests, UnsupportedOperationException.class);

      tests.add(dynamicContainer(ListContractConstants.DOES_NOT_SUPPORT_LIST_ADD_INT_E, subTests));
    }
  }

  private void generateDoesNotSupportAddWithIndexWithNullElementsTests(List<DynamicNode> tests) {
    if (!features.contains(ListFeature.SUPPORTS_ADD_WITH_INDEX)) {
      List<DynamicTest> subTests = new ArrayList<>();

      appendDoesNotSupportAddAtStartWithNewNullElementTests(subTests);
      appendDoesNotSupportAddAtStartWithExistingNullElementTests(subTests);
      appendDoesNotSupportAddAtEndWithNewNullElementTests(subTests);
      appendDoesNotSupportAddAtEndWithExistingNullElementTests(subTests);
      appendDoesNotSupportAddAtMiddleWithNewNullElementTests(subTests);
      appendDoesNotSupportAddAtMiddleWithExistingNullElementTests(subTests);
      appendDoesNotSupportAddAtMinusOneWithNewNullElementTests(
          subTests, UnsupportedOperationException.class);
      appendDoesNotSupportAddAtMinusOneWithExistingNullElementTests(
          subTests, UnsupportedOperationException.class);
      appendDoesNotSupportAddAtSizePlusOneWithNewNullElementTests(
          subTests, UnsupportedOperationException.class);
      appendDoesNotSupportAddAtSizePlusOneWithExistingNullElementTests(
          subTests, UnsupportedOperationException.class);

      tests.add(
          dynamicContainer(
              ListContractConstants.DOES_NOT_SUPPORT_LIST_ADD_INT_E_WITH_NULL_ELEMENT, subTests));
    }
  }

  private void appendSupportsAddAtEndWithNewNullElementTests(List<DynamicTest> subTests) {
    ThrowingConsumer<CollectionSize> supportsAddAtEndWithNewNullElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          list.add(list.size(), null);
          List<E> expected = append(newCollectionOfSize(collectionSize, samples), null);
          assertIterableEquals(
              expected, list, ListContractConstants.NOT_TRUE_THAT_LIST_WAS_APPENDED_WITH_NULL);
        };

    addDynamicSubTests(
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_SIZE_E_WITH_NEW_NULL_ELEMENT,
        supportsAddAtEndWithNewNullElement,
        subTests);
  }

  private void appendSupportsAddAtMiddleWithNewNullElementTests(List<DynamicTest> subTests) {
    ThrowingConsumer<CollectionSize> supportsAddAtMiddleWithNewNullElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);
          int middleIndex = middleIndex(list);

          list.add(middleIndex, null);
          Iterable<E> expected =
              insert(newCollectionOfSize(collectionSize, samples), middleIndex, null);
          assertIterableEquals(
              expected,
              list,
              () ->
                  String.format(
                      ListContractConstants
                          .FORMAT_NOT_TRUE_WAS_INSERTED_AT_INDEX_OR_IN_EXPECTED_ORDER,
                      quote(ListContractConstants.NULL),
                      middleIndex));
        };

    addDynamicSubTests(
        allSupportedCollectionSizesExceptZero,
        ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_MIDDLE_INDEX_E_WITH_NEW_NULL_ELEMENT,
        supportsAddAtMiddleWithNewNullElement,
        subTests);
  }

  private void appendSupportsAddAtEndWithExistingNullElementTests(List<DynamicTest> subTests) {
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

  private void appendSupportsAddAtMiddleWithExistingNullElementTests(List<DynamicTest> subTests) {
    ThrowingConsumer<CollectionSize> supportsAddAtMiddleWithExistingNullElement =
        collectionSize -> {
          List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);
          int middleIndex = middleIndex(list);

          list.add(middleIndex, null);
          Iterable<E> expected =
              insert(
                  newCollectionWithNullInMiddleOfSize(collectionSize, samples), middleIndex, null);
          assertIterableEquals(
              expected,
              list,
              () ->
                  String.format(
                      ListContractConstants
                          .FORMAT_NOT_TRUE_WAS_INSERTED_AT_INDEX_OR_IN_EXPECTED_ORDER,
                      quote(ListContractConstants.NULL),
                      middleIndex));
        };

    addDynamicSubTestsForListWithNullElement(
        ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_MIDDLE_INDEX_E_WITH_EXISTING_NULL_ELEMENT,
        supportsAddAtMiddleWithExistingNullElement,
        subTests);
  }

  private void appendDoesNotSupportAddAtStartWithNewElementTests(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtStartTestsImpl(
        subTests,
        newElement,
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_0_E_WITH_NEW_ELEMENT);
  }

  private void appendDoesNotSupportAddAtStartWithExistingElementTests(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtStartTestsImpl(
        subTests,
        existingElement,
        allSupportedCollectionSizesExceptZero,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_0_E_WITH_EXISTING_ELEMENT);
  }

  private void appendDoesNotSupportAddAtEndWithNewElementTests(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtEndTestsImpl(
        subTests,
        newElement,
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_SIZE_E_WITH_NEW_ELEMENT);
  }

  private void appendDoesNotSupportAddAtEndWithExistingElementTests(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtEndTestsImpl(
        subTests,
        existingElement,
        allSupportedCollectionSizesExceptZero,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_SIZE_E_WITH_EXISTING_ELEMENT);
  }

  private void appendDoesNotSupportAddAtMiddleWithNewElementTests(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtMiddleTestsImpl(
        subTests,
        newElement,
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_MIDDLE_INDEX_E_WITH_NEW_ELEMENT);
  }

  private void appendDoesNotSupportAddAtMiddleWithExistingTests(List<DynamicTest> subTests) {
    appendDoesNotSupportAddAtMiddleTestsImpl(
        subTests,
        existingElement,
        allSupportedCollectionSizesExceptZero,
        ListContractConstants
            .FORMAT_DOES_NOT_SUPPORT_LIST_ADD_MIDDLE_INDEX_E_WITH_EXISTING_ELEMENT);
  }

  private void appendDoesNotSupportAddAtStartWithNewNullElementTests(List<DynamicTest> subTests) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddAtStartWithNewNullElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(0, null),
              () ->
                  String.format(
                      ListContractConstants
                          .FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION,
                      ListContractConstants.NULL));
          assertIterableEquals(
              newCollectionOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTests(
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_0_E_WITH_NEW_NULL_ELEMENT,
        doesNotSupportAddAtStartWithNewNullElement,
        subTests);
  }

  private void appendDoesNotSupportAddAtStartWithExistingNullElementTests(
      List<DynamicTest> subTests) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddAtStartWithExistingNullElement =
        collectionSize -> {
          List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(0, null),
              () ->
                  String.format(
                      ListContractConstants
                          .FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION,
                      ListContractConstants.NULL));
          assertIterableEquals(
              newCollectionWithNullInMiddleOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTestsForListWithNullElement(
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_0_E_WITH_EXISTING_NULL_ELEMENT,
        doesNotSupportAddAtStartWithExistingNullElement,
        subTests);
  }

  private void appendDoesNotSupportAddAtEndWithNewNullElementTests(List<DynamicTest> subTests) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddAtEndWithNewNullElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(list.size(), null),
              () ->
                  String.format(
                      ListContractConstants
                          .FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION,
                      ListContractConstants.NULL));
          assertIterableEquals(
              newCollectionOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTests(
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_SIZE_E_WITH_NEW_NULL_ELEMENT,
        doesNotSupportAddAtEndWithNewNullElement,
        subTests);
  }

  private void appendDoesNotSupportAddAtEndWithExistingNullElementTests(
      List<DynamicTest> subTests) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddAtEndWithExistingNullElement =
        collectionSize -> {
          List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(list.size(), null),
              () ->
                  String.format(
                      ListContractConstants
                          .FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION,
                      ListContractConstants.NULL));
          assertIterableEquals(
              newCollectionWithNullInMiddleOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTestsForListWithNullElement(
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_SIZE_E_WITH_EXISTING_NULL_ELEMENT,
        doesNotSupportAddAtEndWithExistingNullElement,
        subTests);
  }

  private void appendDoesNotSupportAddAtMiddleWithNewNullElementTests(List<DynamicTest> subTests) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddAtMiddleWithNewNullElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(middleIndex(list), null),
              () ->
                  String.format(
                      ListContractConstants
                          .FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION,
                      ListContractConstants.NULL));
          assertIterableEquals(
              newCollectionOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTests(
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_MIDDLE_INDEX_E_WITH_NEW_NULL_ELEMENT,
        doesNotSupportAddAtMiddleWithNewNullElement,
        subTests);
  }

  private void appendDoesNotSupportAddAtMiddleWithExistingNullElementTests(
      List<DynamicTest> subTests) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddAtMiddleWithExistingNullElement =
        collectionSize -> {
          List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(middleIndex(list), null),
              () ->
                  String.format(
                      ListContractConstants
                          .FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION,
                      ListContractConstants.NULL));
          assertIterableEquals(
              newCollectionWithNullInMiddleOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTestsForListWithNullElement(
        ListContractConstants
            .FORMAT_DOES_NOT_SUPPORT_LIST_ADD_MIDDLE_INDEX_E_WITH_EXISTING_NULL_ELEMENT,
        doesNotSupportAddAtMiddleWithExistingNullElement,
        subTests);
  }

  private void appendDoesNotSupportAddAtStartTestsImpl(
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
                      quote(elementToAdd)));
          assertIterableEquals(
              newCollectionOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTests(
        supportedCollectionSizes, displayNameFormat, doesNotSupportAddAtStart, subTests);
  }

  private void appendDoesNotSupportAddAtEndTestsImpl(
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
                      quote(elementToAdd)));
          assertIterableEquals(
              newCollectionOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTests(
        supportedCollectionSizes, displayNameFormat, doesNotSupportAddAtEnd, subTests);
  }

  private void appendDoesNotSupportAddAtMiddleTestsImpl(
      List<DynamicTest> subTests,
      E elementToAdd,
      Set<CollectionSize> supportedCollectionSizes,
      String displayNameFormat) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddAtMiddle =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(middleIndex(list), elementToAdd),
              () ->
                  String.format(
                      ListContractConstants
                          .FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION,
                      quote(elementToAdd)));
          assertIterableEquals(
              newCollectionOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTests(
        supportedCollectionSizes, displayNameFormat, doesNotSupportAddAtMiddle, subTests);
  }

  private void appendDoesNotSupportAddAtMinusOneWithNewElementTests(
      List<DynamicTest> subTests, Class<? extends Throwable> expectedExceptionType) {
    appendDoesNotSupportAddAtMinusOneTestsImpl(
        subTests,
        expectedExceptionType,
        newElement,
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_MINUS1_E_WITH_NEW_ELEMENT);
  }

  private void appendDoesNotSupportAddAtMinusOneWithExistingElementTests(
      List<DynamicTest> subTests, Class<? extends Throwable> expectedExceptionType) {
    appendDoesNotSupportAddAtMinusOneTestsImpl(
        subTests,
        expectedExceptionType,
        existingElement,
        allSupportedCollectionSizesExceptZero,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_MINUS1_E_WITH_EXISTING_ELEMENT);
  }

  private void appendDoesNotSupportAddAtMinusOneTestsImpl(
      List<DynamicTest> subTests,
      Class<? extends Throwable> expectedExceptionType,
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
                      elementToAdd,
                      expectedExceptionType));
          assertIterableEquals(
              newCollectionOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTests(
        supportedCollectionSizes, displayNameFormat, doesNotSupportAddAtMinusOne, subTests);
  }

  private void appendDoesNotSupportAddAtMinusOneWithNewNullElementTests(
      List<DynamicTest> subTests, Class<? extends Throwable> expectedExceptionType) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddAtMinusOneWithNewNullElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

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
              newCollectionOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTests(
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_MINUS1_E_WITH_NEW_NULL_ELEMENT,
        doesNotSupportAddAtMinusOneWithNewNullElement,
        subTests);
  }

  private void appendDoesNotSupportAddAtMinusOneWithExistingNullElementTests(
      List<DynamicTest> subTests, Class<? extends Throwable> expectedExceptionType) {
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

  private void appendDoesNotSupportAddAtSizePlusOneWithNewElementTests(
      List<DynamicTest> subTests, Class<? extends Throwable> expectedExceptionType) {
    appendDoesNotSupportAddAtSizePlusOneImpl(
        subTests,
        expectedExceptionType,
        newElement,
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_SIZE_PLUS_1_E_WITH_NEW_ELEMENT);
  }

  private void appendDoesNotSupportAddAtSizePlusOneWithExistingElementTests(
      List<DynamicTest> subTests, Class<? extends Throwable> expectedExceptionType) {
    appendDoesNotSupportAddAtSizePlusOneImpl(
        subTests,
        expectedExceptionType,
        existingElement,
        allSupportedCollectionSizesExceptZero,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_SIZE_PLUS_1_E_WITH_EXISTING_ELEMENT);
  }

  private void appendDoesNotSupportAddAtSizePlusOneImpl(
      List<DynamicTest> subTests,
      Class<? extends Throwable> expectedExceptionType,
      E elementToAdd,
      Set<CollectionSize> supportedCollectionSizes,
      String displayNameFormat) {
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
                      elementToAdd,
                      expectedExceptionType));

          assertIterableEquals(
              newCollectionOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTests(
        supportedCollectionSizes, displayNameFormat, doesNotSupportAddAtSizePlusOne, subTests);
  }

  private void appendDoesNotSupportAddAtSizePlusOneWithNewNullElementTests(
      List<DynamicTest> subTests, Class<? extends Throwable> expectedExceptionType) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddAtSizePlusOneWithNewNullElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              expectedExceptionType,
              () -> list.add(list.size() + 1, null),
              () ->
                  String.format(
                      ListContractConstants
                          .FORMAT_NOT_TRUE_THAT_LIST_ADD_INT_E_THREW_EXPECTED_EXCEPTION_TYPE,
                      list.size() + 1,
                      ListContractConstants.NULL,
                      expectedExceptionType));
          assertIterableEquals(
              newCollectionOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTests(
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_SIZE_PLUS_1_E_WITH_NEW_NULL_ELEMENT,
        doesNotSupportAddAtSizePlusOneWithNewNullElement,
        subTests);
  }

  private void appendDoesNotSupportAddAtSizePlusOneWithExistingNullElementTests(
      List<DynamicTest> subTests, Class<? extends Throwable> expectedExceptionType) {
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
                      ListContractConstants.NULL,
                      expectedExceptionType));
          assertIterableEquals(
              newCollectionWithNullInMiddleOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTestsForListWithNullElement(
        ListContractConstants
            .FORMAT_DOES_NOT_SUPPORT_LIST_ADD_SIZE_PLUS_1_E_WITH_EXISTING_NULL_ELEMENT,
        doesNotSupportAddAtSizePlusOneWithExistingNullElement,
        subTests);
  }

  private void addDynamicSubTests(
      Set<CollectionSize> supportedCollectionSizes,
      String displayNameFormat,
      ThrowingConsumer<CollectionSize> testExecutor,
      List<DynamicTest> subTests) {
    DynamicTest.stream(
            supportedCollectionSizes.iterator(),
            collectionSize ->
                String.format(
                    displayNameFormat,
                    collectionSize.size(),
                    newCollectionOfSize(collectionSize, samples)),
            testExecutor)
        .forEachOrdered(subTests::add);
  }

  private void addDynamicSubTestsForListWithNullElement(
      String displayNameFormat,
      ThrowingConsumer<CollectionSize> testExecutor,
      List<DynamicTest> subTests) {
    DynamicTest.stream(
            allSupportedCollectionSizesExceptZero.iterator(),
            collectionSize ->
                String.format(
                    displayNameFormat,
                    collectionSize.size(),
                    newCollectionWithNullInMiddleOfSize(collectionSize, samples)),
            testExecutor)
        .forEachOrdered(subTests::add);
  }
}

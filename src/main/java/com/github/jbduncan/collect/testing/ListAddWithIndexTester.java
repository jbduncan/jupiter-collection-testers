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

import static com.github.jbduncan.collect.testing.Helpers.extractConcreteSizes;
import static com.github.jbduncan.collect.testing.Helpers.minus;
import static com.github.jbduncan.collect.testing.Helpers.newCollectionOfSize;
import static com.github.jbduncan.collect.testing.Helpers.newCollectionWithNullInMiddleOfSize;
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
              .allSupportedCollectionSizes(allSupportedCollectionSizes)
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
        innerSubTests.addAll(
            listAddAtStartSubTestMaker.doesNotSupportAddWithIndexForNullsSubTests());
        innerSubTests.addAll(listAddAtEndSubTestMaker.doesNotSupportAddWithIndexForNullsSubTests());
        innerSubTests.addAll(
            listAddAtMiddleSubTestMaker.doesNotSupportAddWithIndexForNullsSubTests());

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
              .allSupportedCollectionSizes(allSupportedCollectionSizes)
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
      subTests.addAll(listAddAtStartSubTestMaker.supportsAddWithIndexForNullsSubTests());
      subTests.addAll(listAddAtEndSubTestMaker.supportsAddWithIndexForNullsSubTests());
      subTests.addAll(listAddAtMiddleSubTestMaker.supportsAddWithIndexForNullsSubTests());
      subTests.addAll(listAddAtMinusOneSubTestMaker.doesNotSupportAddWithIndexForNullsSubTests());
      subTests.addAll(
          listAddAtSizePlusOneSubTestMaker.doesNotSupportAddWithIndexForNullsSubTests());

      tests.add(
          dynamicContainer(
              ListContractConstants.SUPPORTS_LIST_ADD_INT_E_WITH_NULL_ELEMENT, subTests));
    }
  }

  private void generateDoesNotSupportAddWithIndexTests(List<DynamicNode> tests) {
    if (!features.contains(ListFeature.SUPPORTS_ADD_WITH_INDEX)) {
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
              .allSupportedCollectionSizes(allSupportedCollectionSizes)
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
              .expectedExceptionType(UnsupportedOperationException.class)
              .build();
      ListAddAtSizePlusOneSubTestMaker<E> listAddAtSizePlusOneSubTestMaker =
          ListAddAtSizePlusOneSubTestMaker.<E>builder()
              .testListGenerator(generator)
              .sampleElements(samples)
              .newElement(newElement)
              .existingElement(existingElement)
              .allSupportedCollectionSizes(allSupportedCollectionSizes)
              .allSupportedCollectionSizesExceptZero(allSupportedCollectionSizesExceptZero)
              .expectedExceptionType(UnsupportedOperationException.class)
              .build();

      List<DynamicTest> subTests = new ArrayList<>();
      subTests.addAll(listAddAtStartSubTestMaker.doesNotSupportAddWithIndexSubTests());
      subTests.addAll(listAddAtEndSubTestMaker.doesNotSupportAddWithIndexSubTests());
      subTests.addAll(listAddAtMiddleSubTestMaker.doesNotSupportAddWithIndexSubTests());
      subTests.addAll(listAddAtMinusOneSubTestMaker.doesNotSupportAddWithIndexSubTests());
      subTests.addAll(listAddAtSizePlusOneSubTestMaker.doesNotSupportAddWithIndexSubTests());

      tests.add(dynamicContainer(ListContractConstants.DOES_NOT_SUPPORT_LIST_ADD_INT_E, subTests));
    }
  }

  private void generateDoesNotSupportAddWithIndexWithNullElementsTests(List<DynamicNode> tests) {
    if (!features.contains(ListFeature.SUPPORTS_ADD_WITH_INDEX)) {
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
              .allSupportedCollectionSizes(allSupportedCollectionSizes)
              .allSupportedCollectionSizesExceptZero(allSupportedCollectionSizesExceptZero)
              .build();

      List<DynamicTest> subTests = new ArrayList<>();
      subTests.addAll(listAddAtStartSubTestMaker.doesNotSupportAddWithIndexForNullsSubTests());
      subTests.addAll(listAddAtEndSubTestMaker.doesNotSupportAddWithIndexForNullsSubTests());
      subTests.addAll(listAddAtMiddleSubTestMaker.doesNotSupportAddWithIndexForNullsSubTests());

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

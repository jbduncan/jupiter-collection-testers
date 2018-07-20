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
import static com.github.jbduncan.collect.testing.Helpers.minus;
import static com.github.jbduncan.collect.testing.Helpers.newCollectionOfSize;
import static com.github.jbduncan.collect.testing.Helpers.newCollectionWithNullInMiddleOfSize;
import static com.github.jbduncan.collect.testing.Helpers.quote;
import static com.github.jbduncan.collect.testing.Helpers.stringify;
import static com.github.jbduncan.collect.testing.Helpers.stringifyElements;
import static com.github.jbduncan.collect.testing.ListContractHelpers.newListToTest;
import static com.github.jbduncan.collect.testing.ListContractHelpers.newListToTestWithNullElementInMiddle;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.ThrowingConsumer;

final class ListAddTester<E> {
  private final TestListGenerator<E> generator;
  private final SampleElements<E> samples;
  private final E newElement;
  private final E existingElement;
  private final Set<Feature<?>> features;
  private final Set<CollectionSize> allSupportedCollectionSizes;
  private final Set<CollectionSize> allSupportedCollectionSizesExceptZero;

  private ListAddTester(TestListGenerator<E> testListGenerator, Set<Feature<?>> features) {
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

  static class Builder<E> {
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

    ListAddTester<E> build() {
      return new ListAddTester<>(testListGenerator, features);
    }
  }

  List<DynamicNode> dynamicTestsGraph() {
    List<DynamicNode> tests = new ArrayList<>();
    generateSupportsAddTests(tests);
    generateSupportsAddWithNullElementsTests(tests);
    generateDoesNotSupportAddTests(tests);
    generateDoesNotSupportAddWithNullElementsTests(tests);
    return Collections.unmodifiableList(tests);
  }

  // TODO: See if each associated pair of `generate...` methods in ListAddTester and
  // ListAddWithIndexTester can be refactored out into an abstract base class.
  private void generateSupportsAddTests(List<DynamicNode> tests) {
    if (features.contains(CollectionFeature.SUPPORTS_ADD)) {
      List<DynamicTest> subTests = new ArrayList<>();

      appendSupportsAddWithNewElementTests(subTests);
      appendSupportsAddWithExistingElementTests(subTests);

      tests.add(dynamicContainer(ListContractConstants.SUPPORTS_LIST_ADD_E, subTests));

      if (!features.contains(CollectionFeature.ALLOWS_NULL_VALUES)) {
        List<DynamicTest> innerSubTests = new ArrayList<>();

        appendDoesNotSupportAddWithNewNullElementTests(innerSubTests);

        tests.add(
            dynamicContainer(ListContractConstants.DOESNT_SUPPORT_LIST_ADD_NULL, innerSubTests));
      }
    }
  }

  private void generateSupportsAddWithNullElementsTests(List<DynamicNode> tests) {
    if (features.containsAll(
        Arrays.asList(CollectionFeature.SUPPORTS_ADD, CollectionFeature.ALLOWS_NULL_VALUES))) {
      List<DynamicTest> subTests = new ArrayList<>();

      appendSupportsAddWithNewNullElementTests(subTests);
      appendSupportsAddWithExistingNullElementTests(subTests);

      tests.add(
          dynamicContainer(ListContractConstants.SUPPORTS_LIST_ADD_E_WITH_NULL_ELEMENT, subTests));
    }
  }

  private void generateDoesNotSupportAddTests(List<DynamicNode> tests) {
    if (!features.contains(CollectionFeature.SUPPORTS_ADD)) {
      List<DynamicTest> subTests = new ArrayList<>();

      appendDoesNotSupportAddWithNewElementTests(subTests);
      appendDoesNotSupportAddWithExistingElementTests(subTests);

      tests.add(dynamicContainer(ListContractConstants.DOESNT_SUPPORT_LIST_ADD_E, subTests));
    }
  }

  private void generateDoesNotSupportAddWithNullElementsTests(List<DynamicNode> tests) {
    if (!features.contains(CollectionFeature.SUPPORTS_ADD)) {
      List<DynamicTest> subTests = new ArrayList<>();

      appendDoesNotSupportAddWithNewNullElementTests(subTests);
      appendDoesNotSupportAddWithExistingNullElementTests(subTests);

      tests.add(dynamicContainer(ListContractConstants.DOESNT_SUPPORT_LIST_ADD_NULL, subTests));
    }
  }

  private void appendSupportsAddWithNewElementTests(List<DynamicTest> subTests) {
    // TODO: Consider moving these ThrowingConsumers into their own methods.
    ThrowingConsumer<CollectionSize> supportsAddWithNewElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertTrue(
              list.add(newElement),
              () ->
                  String.format(
                      ListContractConstants.FORMAT_NOT_TRUE_THAT_LIST_ADD_RETURNED_TRUE,
                      quote(newElement)));
          List<E> expected = append(newCollectionOfSize(collectionSize, samples), newElement);
          assertIterableEquals(
              expected,
              list,
              () ->
                  String.format(
                      ListContractConstants.FORMAT_NOT_TRUE_THAT_LIST_WAS_APPENDED,
                      quote(newElement)));
        };

    addDyamicSubTests(
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_SUPPORTS_LIST_ADD,
        newElement,
        supportsAddWithNewElement,
        subTests);
  }

  private void appendSupportsAddWithExistingElementTests(List<DynamicTest> subTests) {
    ThrowingConsumer<CollectionSize> supportsAddWithExistingElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertTrue(
              list.add(existingElement),
              () ->
                  String.format(
                      ListContractConstants.FORMAT_NOT_TRUE_THAT_LIST_ADD_RETURNED_TRUE,
                      quote(existingElement)));
          List<E> expected = append(newCollectionOfSize(collectionSize, samples), existingElement);
          assertIterableEquals(
              expected,
              list,
              () ->
                  String.format(
                      ListContractConstants.FORMAT_NOT_TRUE_THAT_LIST_WAS_APPENDED,
                      quote(existingElement)));
        };

    addDyamicSubTests(
        allSupportedCollectionSizesExceptZero,
        ListContractConstants.FORMAT_SUPPORTS_LIST_ADD,
        existingElement,
        supportsAddWithExistingElement,
        subTests);
  }

  private void appendSupportsAddWithNewNullElementTests(List<DynamicTest> subTests) {
    ThrowingConsumer<CollectionSize> supportsAddWithNewNullElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertTrue(
              list.add(null), ListContractConstants.NOT_TRUE_THAT_LIST_ADD_NULL_RETURNED_TRUE);
          List<E> expected = append(newCollectionOfSize(collectionSize, samples), null);
          assertIterableEquals(
              expected, list, ListContractConstants.NOT_TRUE_THAT_LIST_WAS_APPENDED_WITH_NULL);
        };

    addDyamicSubTests(
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_SUPPORTS_LIST_ADD,
        null,
        supportsAddWithNewNullElement,
        subTests);
  }

  private void appendSupportsAddWithExistingNullElementTests(List<DynamicTest> subTests) {
    ThrowingConsumer<CollectionSize> supportsAddWithExistingNullElement =
        collectionSize -> {
          List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

          assertTrue(
              list.add(null), ListContractConstants.NOT_TRUE_THAT_LIST_ADD_NULL_RETURNED_TRUE);
          List<E> expected =
              append(newCollectionWithNullInMiddleOfSize(collectionSize, samples), null);
          assertIterableEquals(
              expected, list, ListContractConstants.NOT_TRUE_THAT_LIST_WAS_APPENDED_WITH_NULL);
        };

    addDynamicSubTestsForListWithNullElement(
        ListContractConstants.FORMAT_SUPPORTS_LIST_ADD,
        supportsAddWithExistingNullElement,
        subTests);
  }

  private void appendDoesNotSupportAddWithNewElementTests(List<DynamicTest> subTests) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddWithNewElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(newElement),
              () ->
                  String.format(
                      ListContractConstants
                          .FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION,
                      quote(newElement)));
          assertIterableEquals(
              newCollectionOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDyamicSubTests(
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_DOESNT_SUPPORT_LIST_ADD,
        newElement,
        doesNotSupportAddWithNewElement,
        subTests);
  }

  private void appendDoesNotSupportAddWithExistingElementTests(List<DynamicTest> subTests) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddWithExistingElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(existingElement),
              () ->
                  String.format(
                      ListContractConstants
                          .FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION,
                      quote(existingElement)));
          assertIterableEquals(
              newCollectionOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDyamicSubTests(
        allSupportedCollectionSizesExceptZero,
        ListContractConstants.FORMAT_DOESNT_SUPPORT_LIST_ADD,
        existingElement,
        doesNotSupportAddWithExistingElement,
        subTests);
  }

  private void appendDoesNotSupportAddWithNewNullElementTests(List<DynamicTest> subTests) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddWithNewNullElement =
        collectionSize -> {
          List<E> list = newListToTest(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(null),
              () ->
                  String.format(
                      ListContractConstants
                          .FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION,
                      "null"));
          assertIterableEquals(
              newCollectionOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDyamicSubTests(
        allSupportedCollectionSizes,
        ListContractConstants.FORMAT_DOESNT_SUPPORT_LIST_ADD,
        null,
        doesNotSupportAddWithNewNullElement,
        subTests);
  }

  private void appendDoesNotSupportAddWithExistingNullElementTests(List<DynamicTest> subTests) {
    ThrowingConsumer<CollectionSize> doesNotSupportAddWithExistingNullElement =
        collectionSize -> {
          List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(null),
              () ->
                  String.format(
                      ListContractConstants
                          .FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION,
                      "null"));
          assertIterableEquals(
              newCollectionWithNullInMiddleOfSize(collectionSize, samples),
              list,
              ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
        };

    addDynamicSubTestsForListWithNullElement(
        ListContractConstants.FORMAT_DOESNT_SUPPORT_LIST_ADD,
        doesNotSupportAddWithExistingNullElement,
        subTests);
  }

  private void addDyamicSubTests(
      Set<CollectionSize> supportedCollectionSizes,
      String displayNameFormat,
      E elementToAdd,
      ThrowingConsumer<CollectionSize> testExecutor,
      List<DynamicTest> subTests) {
    DynamicTest.stream(
            supportedCollectionSizes.iterator(),
            collectionSize ->
                String.format(
                    displayNameFormat,
                    stringify(elementToAdd),
                    stringifyElements(newCollectionOfSize(collectionSize, samples))),
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
                    "null",
                    stringifyElements(
                        newCollectionWithNullInMiddleOfSize(collectionSize, samples))),
            testExecutor)
        .forEachOrdered(subTests::add);
  }
}

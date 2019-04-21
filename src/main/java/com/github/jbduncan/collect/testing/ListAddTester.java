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

import static com.github.jbduncan.collect.testing.Helpers.append;
import static com.github.jbduncan.collect.testing.Helpers.extractConcreteSizes;
import static com.github.jbduncan.collect.testing.Helpers.extractConcreteSizesExceptZero;
import static com.github.jbduncan.collect.testing.Helpers.newIterable;
import static com.github.jbduncan.collect.testing.Helpers.stringify;
import static com.github.jbduncan.collect.testing.Helpers.stringifyElements;
import static com.github.jbduncan.collect.testing.ListContractHelpers.newTestList;
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

  private ListAddTester(TestListGenerator<E> testListGenerator, Set<Feature<?>> features) {
    this.generator = requireNonNull(testListGenerator, "testListGenerator");
    this.samples = requireNonNull(testListGenerator.samples(), "samples");
    this.newElement = samples.e3();
    this.existingElement = samples.e0();
    this.features = requireNonNull(features, "features");
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

  private void generateSupportsAddTests(List<DynamicNode> tests) {
    if (features.contains(CollectionFeature.SUPPORTS_ADD)) {
      List<DynamicTest> subTests = new ArrayList<>();

      appendSupportsAddWithNewElementTests(subTests);
      appendSupportsAddWithExistingElementTests(subTests);

      tests.add(dynamicContainer("Supports List.add(E)", subTests));

      if (!features.contains(CollectionFeature.ALLOWS_NULL_VALUES)) {
        List<DynamicTest> innerSubTests = new ArrayList<>();

        appendDoesNotSupportAddWithNewNullElementTests(innerSubTests);

        tests.add(dynamicContainer("Doesn't support List.add(null)", innerSubTests));
      }
    }
  }

  private void generateSupportsAddWithNullElementsTests(List<DynamicNode> tests) {
    if (features.containsAll(
        Arrays.asList(CollectionFeature.SUPPORTS_ADD, CollectionFeature.ALLOWS_NULL_VALUES))) {
      List<DynamicTest> subTests = new ArrayList<>();

      appendSupportsAddWithNewNullElementTests(subTests);
      appendSupportsAddWithExistingNullElementTests(subTests);

      tests.add(dynamicContainer("Supports List.add(null)", subTests));
    }
  }

  private void generateDoesNotSupportAddTests(List<DynamicNode> tests) {
    if (!features.contains(CollectionFeature.SUPPORTS_ADD)) {
      List<DynamicTest> subTests = new ArrayList<>();

      appendDoesNotSupportAddWithNewElementTests(subTests);
      appendDoesNotSupportAddWithExistingElementTests(subTests);

      tests.add(dynamicContainer("Doesn't support List.add(E)", subTests));
    }
  }

  private void generateDoesNotSupportAddWithNullElementsTests(List<DynamicNode> tests) {
    if (!features.contains(CollectionFeature.SUPPORTS_ADD)) {
      List<DynamicTest> subTests = new ArrayList<>();

      appendDoesNotSupportAddWithNewNullElementTests(subTests);
      appendDoesNotSupportAddWithExistingNullElementTests(subTests);

      tests.add(dynamicContainer("Doesn't support List.add(null)", subTests));
    }
  }

  private void appendSupportsAddWithNewElementTests(List<DynamicTest> subTests) {
    appendTestsForSupportsAdd(
        subTests, newElement, extractConcreteSizes(features), /* nullInMiddle= */ false);
  }

  private void appendSupportsAddWithExistingElementTests(List<DynamicTest> subTests) {
    appendTestsForSupportsAdd(
        subTests,
        existingElement,
        extractConcreteSizesExceptZero(features),
        /* nullInMiddle= */ false);
  }

  private void appendSupportsAddWithNewNullElementTests(List<DynamicTest> subTests) {
    appendTestsForSupportsAdd(
        subTests, null, extractConcreteSizes(features), /* nullInMiddle= */ false);
  }

  private void appendSupportsAddWithExistingNullElementTests(List<DynamicTest> subTests) {
    appendTestsForSupportsAdd(
        subTests, null, extractConcreteSizesExceptZero(features), /* nullInMiddle= */ true);
  }

  private void appendTestsForSupportsAdd(
      List<DynamicTest> subTests,
      E elementToAdd,
      Set<CollectionSize> supportedCollectionSizes,
      boolean nullInMiddle) {
    ThrowingConsumer<CollectionSize> testTemplate =
        collectionSize -> {
          List<E> list = newTestList(generator, collectionSize, nullInMiddle);

          assertTrue(
              list.add(elementToAdd),
              () -> "Not true that list.add(" + stringify(elementToAdd) + ") return true");
          List<E> expected =
              append(newIterable(samples, collectionSize, nullInMiddle), elementToAdd);
          assertIterableEquals(
              expected,
              list,
              () -> "Not true that list was appended with " + stringify(elementToAdd));
        };

    DynamicTest.stream(
        supportedCollectionSizes.iterator(),
        collectionSize ->
            "Supports List.add("
                + stringify(elementToAdd)
                + ") on "
                + stringifyElements(newIterable(samples, collectionSize, nullInMiddle)),
        testTemplate)
        .forEachOrdered(subTests::add);
  }

  private void appendDoesNotSupportAddWithNewElementTests(List<DynamicTest> subTests) {
    appendTestsForDoesNotSupportAdd(
        subTests, newElement, extractConcreteSizes(features), /* nullInMiddle= */ false);
  }

  private void appendDoesNotSupportAddWithExistingElementTests(List<DynamicTest> subTests) {
    appendTestsForDoesNotSupportAdd(
        subTests,
        existingElement,
        extractConcreteSizesExceptZero(features),
        /* nullInMiddle= */ false);
  }

  private void appendDoesNotSupportAddWithNewNullElementTests(List<DynamicTest> subTests) {
    appendTestsForDoesNotSupportAdd(
        subTests, null, extractConcreteSizes(features), /* nullInMiddle= */ false);
  }

  private void appendDoesNotSupportAddWithExistingNullElementTests(List<DynamicTest> subTests) {
    appendTestsForDoesNotSupportAdd(
        subTests, null, extractConcreteSizesExceptZero(features), /* nullInMiddle= */ true);
  }

  private void appendTestsForDoesNotSupportAdd(
      List<DynamicTest> subTests,
      E elementToAdd,
      Set<CollectionSize> supportedCollectionSizes,
      boolean nullInMiddle) {
    ThrowingConsumer<CollectionSize> testTemplate =
        collectionSize -> {
          List<E> list = newTestList(generator, collectionSize, nullInMiddle);

          assertThrows(
              UnsupportedOperationException.class,
              () -> list.add(elementToAdd),
              // TODO: Change message to "Not true that ... threw exception of type
              //   UnsupportedOperationException" to match ListAddWithIndexTester ?
              () ->
                  "Not true that list.add("
                      + stringify(elementToAdd)
                      + ") threw UnsupportedOperationException");
          assertIterableEquals(
              newIterable(samples, collectionSize, nullInMiddle),
              list,
              "Not true that list remained unchanged");
        };

    DynamicTest.stream(
        supportedCollectionSizes.iterator(),
        collectionSize ->
            "Doesn't support List.add("
                + stringify(elementToAdd)
                + ") on "
                + stringifyElements(newIterable(samples, collectionSize, nullInMiddle)),
        testTemplate)
        .forEachOrdered(subTests::add);
  }
}

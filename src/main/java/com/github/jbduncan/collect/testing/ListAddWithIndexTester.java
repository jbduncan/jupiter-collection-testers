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
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;

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
      // TODO: Consider extracting the sub-testers into their own fields
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

      if (features.contains(CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION)) {
        subTests.addAll(listAddAtStartSubTestMaker.failsFastOnConcurrentModificationSubTests());
        if (features.contains(CollectionFeature.ALLOWS_NULL_VALUES)) {
          subTests.addAll(
              listAddAtStartSubTestMaker
                  .failsFastOnConcurrentModificationInvolvingNullElementSubTests());
        }
      }

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
                ListContractConstants.DOESNT_SUPPORT_LIST_ADD_INT_NULL, innerSubTests));
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

      tests.add(dynamicContainer(ListContractConstants.DOESNT_SUPPORT_LIST_ADD_INT_E, subTests));
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
      subTests.addAll(listAddAtStartSubTestMaker.doesNotSupportAddWithIndexForNullsSubTests());
      subTests.addAll(listAddAtEndSubTestMaker.doesNotSupportAddWithIndexForNullsSubTests());
      subTests.addAll(listAddAtMiddleSubTestMaker.doesNotSupportAddWithIndexForNullsSubTests());
      subTests.addAll(listAddAtMinusOneSubTestMaker.doesNotSupportAddWithIndexForNullsSubTests());
      subTests.addAll(
          listAddAtSizePlusOneSubTestMaker.doesNotSupportAddWithIndexForNullsSubTests());

      tests.add(dynamicContainer(ListContractConstants.DOESNT_SUPPORT_LIST_ADD_INT_NULL, subTests));
    }
  }
}

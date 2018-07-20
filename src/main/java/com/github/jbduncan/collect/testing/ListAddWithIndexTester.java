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
  private final Set<Feature<?>> features;

  private final ListAddAtStartSubTestMaker<E> listAddAtStartSubTestMaker;
  private final ListAddAtEndSubTestMaker<E> listAddAtEndSubTestMaker;
  private final ListAddAtMiddleSubTestMaker<E> listAddAtMiddleSubTestMaker;
  private final ListAddAtMinusOneSubTestMaker<E> listAddAtMinusOneSubTestMaker;
  private final ListAddAtSizePlusOneSubTestMaker<E> listAddAtSizePlusOneSubTestMaker;

  private ListAddWithIndexTester(TestListGenerator<E> testListGenerator, Set<Feature<?>> features) {
    requireNonNull(testListGenerator, "testListGenerator");
    requireNonNull(features, "features");

    SampleElements<E> samples = requireNonNull(testListGenerator.samples(), "samples");
    E newElement = samples.e3();
    E existingElement = samples.e0();
    Set<CollectionSize> allSupportedCollectionSizes = extractConcreteSizes(features);
    Set<CollectionSize> allSupportedCollectionSizesExceptZero =
        minus(allSupportedCollectionSizes, CollectionSize.SUPPORTS_ZERO);

    this.features = features;
    this.listAddAtStartSubTestMaker =
        ListAddAtStartSubTestMaker.<E>builder()
            .testListGenerator(testListGenerator)
            .sampleElements(samples)
            .newElement(newElement)
            .existingElement(existingElement)
            .allSupportedCollectionSizes(allSupportedCollectionSizes)
            .allSupportedCollectionSizesExceptZero(allSupportedCollectionSizesExceptZero)
            .build();
    this.listAddAtEndSubTestMaker =
        ListAddAtEndSubTestMaker.<E>builder()
            .testListGenerator(testListGenerator)
            .sampleElements(samples)
            .newElement(newElement)
            .existingElement(existingElement)
            .allSupportedCollectionSizes(allSupportedCollectionSizes)
            .allSupportedCollectionSizesExceptZero(allSupportedCollectionSizesExceptZero)
            .build();
    this.listAddAtMiddleSubTestMaker =
        ListAddAtMiddleSubTestMaker.<E>builder()
            .testListGenerator(testListGenerator)
            .sampleElements(samples)
            .newElement(newElement)
            .existingElement(existingElement)
            .allSupportedCollectionSizes(allSupportedCollectionSizes)
            .allSupportedCollectionSizesExceptZero(allSupportedCollectionSizesExceptZero)
            .build();
    this.listAddAtMinusOneSubTestMaker =
        ListAddAtMinusOneSubTestMaker.<E>builder()
            .testListGenerator(testListGenerator)
            .sampleElements(samples)
            .newElement(newElement)
            .existingElement(existingElement)
            .allSupportedCollectionSizes(allSupportedCollectionSizes)
            .allSupportedCollectionSizesExceptZero(allSupportedCollectionSizesExceptZero)
            .expectedExceptionType(IndexOutOfBoundsException.class)
            .build();
    this.listAddAtSizePlusOneSubTestMaker =
        ListAddAtSizePlusOneSubTestMaker.<E>builder()
            .testListGenerator(testListGenerator)
            .sampleElements(samples)
            .newElement(newElement)
            .existingElement(existingElement)
            .allSupportedCollectionSizes(allSupportedCollectionSizes)
            .allSupportedCollectionSizesExceptZero(allSupportedCollectionSizesExceptZero)
            .expectedExceptionType(IndexOutOfBoundsException.class)
            .build();
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

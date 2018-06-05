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
import static com.github.jbduncan.collect.testing.Helpers.quote;
import static com.github.jbduncan.collect.testing.ListAddWithIndexHelpers.addDynamicSubTests;
import static com.github.jbduncan.collect.testing.ListContractHelpers.newListToTest;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.ThrowingConsumer;

final class ListAddAtEndSubTestMaker<E> {
  private final TestListGenerator<E> generator;
  private final SampleElements<E> samples;
  private final E newElement;
  private final E existingElement;
  private final Set<CollectionSize> allSupportedCollectionSizes;
  private final Set<CollectionSize> allSupportedCollectionSizesExceptZero;

  private ListAddAtEndSubTestMaker(
      TestListGenerator<E> testListGenerator, Set<Feature<?>> features) {
    this.generator = requireNonNull(testListGenerator, "testListGenerator");
    this.samples = requireNonNull(testListGenerator.samples(), "samples");
    this.newElement = samples.e3();
    this.existingElement = samples.e0();
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

    Builder<E> features(Set<Feature<?>> features) {
      this.features = features;
      return this;
    }

    ListAddAtEndSubTestMaker<E> build() {
      return new ListAddAtEndSubTestMaker<>(testListGenerator, features);
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
                      quote(elementToAdd)));
        };

    addDynamicSubTests(
        supportedCollectionSizes, displayNameFormat, samples, supportsAddAtEnd, subTests);
  }
}

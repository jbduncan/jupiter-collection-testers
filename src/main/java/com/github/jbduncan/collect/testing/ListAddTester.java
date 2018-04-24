/*
 * Copyright 2018 the junit-jupiter-collection-testers authors.
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
import static com.github.jbduncan.collect.testing.Helpers.collectionSizeToElements;
import static com.github.jbduncan.collect.testing.Helpers.extractConcreteSizes;
import static com.github.jbduncan.collect.testing.Helpers.minus;
import static com.github.jbduncan.collect.testing.Helpers.newCollectionWithNullElementInMiddle;
import static com.github.jbduncan.collect.testing.Helpers.quote;
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
  private final Set<Feature<?>> features;
  private final Set<CollectionSize> supportedCollectionSizes;

  private ListAddTester(TestListGenerator<E> testListGenerator, Set<Feature<?>> features) {
    this.generator = requireNonNull(testListGenerator, "testListGenerator");
    this.samples = requireNonNull(testListGenerator.samples(), "samples");
    this.features = requireNonNull(features, "features");
    this.supportedCollectionSizes = extractConcreteSizes(features);
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

  List<DynamicNode> dynamicTests() {
    List<DynamicNode> tests = new ArrayList<>();
    generateSupportsAddTests(tests);
    generateSupportsAddButNotOnNullElementsTests(tests);
    generateSupportsAddWithNullElementsTests(tests);
    generateDoesNotSupportAddTests(tests);
    generateDoesNotSupportAddWithNullElementsTests(tests);
    return Collections.unmodifiableList(tests);
  }

  // TODO: See if each associated pair of `generate...` methods in ListAddTester and
  // ListAddWithIndexTester can be refactored out into an abstract base class.
  private void generateSupportsAddTests(List<DynamicNode> tests) {
    if (features.contains(CollectionFeature.SUPPORTS_ADD)) {
      E e0 = samples.e0();
      E e3 = samples.e3();

      List<DynamicTest> subTests = new ArrayList<>();

      // TODO: Consider moving these ThrowingConsumers into their own methods.
      ThrowingConsumer<CollectionSize> supportsAddWithNewElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);

            assertTrue(
                list.add(e3),
                () ->
                    String.format(
                        ListContractConstants.FORMAT_NOT_TRUE_THAT_LIST_ADD_RETURNED_TRUE,
                        quote(e3)));
            List<E> expected = append(collectionSizeToElements(collectionSize, samples), e3);
            assertIterableEquals(
                expected,
                list,
                () ->
                    String.format(
                        ListContractConstants.FORMAT_NOT_TRUE_THAT_LIST_WAS_APPENDED, quote(e3)));
          };

      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_E_WITH_NEW_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              supportsAddWithNewElement)
          .forEachOrdered(subTests::add);

      ThrowingConsumer<CollectionSize> supportsAddWithExistingElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);

            assertTrue(
                list.add(e0),
                () ->
                    String.format(
                        ListContractConstants.FORMAT_NOT_TRUE_THAT_LIST_ADD_RETURNED_TRUE,
                        quote(e0)));
            List<E> expected = append(collectionSizeToElements(collectionSize, samples), e0);
            assertIterableEquals(
                expected,
                list,
                () ->
                    String.format(
                        ListContractConstants.FORMAT_NOT_TRUE_THAT_LIST_WAS_APPENDED, quote(e0)));
          };

      DynamicTest.stream(
              minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_E_WITH_EXISTING_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              supportsAddWithExistingElement)
          .forEachOrdered(subTests::add);

      tests.add(dynamicContainer(ListContractConstants.SUPPORTS_LIST_ADD_E, subTests));
    }
  }

  private void generateSupportsAddButNotOnNullElementsTests(List<DynamicNode> tests) {
    if (features.contains(CollectionFeature.SUPPORTS_ADD)
        && !features.contains(CollectionFeature.ALLOWS_NULL_VALUES)) {
      List<DynamicTest> subTests = new ArrayList<>();

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
                        ListContractConstants.NULL));
            assertIterableEquals(
                collectionSizeToElements(collectionSize, samples),
                list,
                ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
          };

      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants
                          .FORMAT_DOES_NOT_SUPPORT_LIST_ADD_E_WITH_NEW_NULL_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAddWithNewNullElement)
          .forEachOrdered(subTests::add);

      tests.add(
          dynamicContainer(
              ListContractConstants.DOES_NOT_SUPPORT_LIST_ADD_E_WITH_NEW_NULL_ELEMENT, subTests));
    }
  }

  private void generateSupportsAddWithNullElementsTests(List<DynamicNode> tests) {
    if (features.containsAll(
        Arrays.asList(CollectionFeature.SUPPORTS_ADD, CollectionFeature.ALLOWS_NULL_VALUES))) {

      ThrowingConsumer<CollectionSize> supportsAddWithNewNullElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);

            assertTrue(
                list.add(null), ListContractConstants.NOT_TRUE_THAT_LIST_ADD_NULL_RETURNED_TRUE);
            List<E> expected = append(collectionSizeToElements(collectionSize, samples), null);
            assertIterableEquals(
                expected, list, ListContractConstants.NOT_TRUE_THAT_LIST_WAS_APPENDED_WITH_NULL);
          };

      List<DynamicTest> subTests = new ArrayList<>();

      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_E_WITH_NEW_NULL_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              supportsAddWithNewNullElement)
          .forEachOrdered(subTests::add);

      ThrowingConsumer<CollectionSize> supportsAddWithExistingNullElement =
          collectionSize -> {
            List<E> list = newListToTestWithNullElementInMiddle(generator, collectionSize);

            assertTrue(
                list.add(null), ListContractConstants.NOT_TRUE_THAT_LIST_ADD_NULL_RETURNED_TRUE);
            List<E> expected =
                append(newCollectionWithNullElementInMiddle(samples, collectionSize), null);
            assertIterableEquals(
                expected, list, ListContractConstants.NOT_TRUE_THAT_LIST_WAS_APPENDED_WITH_NULL);
          };

      DynamicTest.stream(
              minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants.FORMAT_SUPPORTS_LIST_ADD_E_WITH_EXISTING_NULL_ELEMENT,
                      collectionSize.size(),
                      newCollectionWithNullElementInMiddle(samples, collectionSize)),
              supportsAddWithExistingNullElement)
          .forEachOrdered(subTests::add);

      tests.add(
          dynamicContainer(ListContractConstants.SUPPORTS_LIST_ADD_E_WITH_NULL_ELEMENT, subTests));
    }
  }

  private void generateDoesNotSupportAddTests(List<DynamicNode> tests) {
    if (!features.contains(CollectionFeature.SUPPORTS_ADD)) {
      E e0 = samples.e0();
      E e3 = samples.e3();

      List<DynamicTest> subTests = new ArrayList<>();

      ThrowingConsumer<CollectionSize> doesNotSupportAddWithNewElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);

            assertThrows(
                UnsupportedOperationException.class,
                () -> list.add(e3),
                () ->
                    String.format(
                        ListContractConstants
                            .FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION,
                        quote(e3)));
            assertIterableEquals(
                collectionSizeToElements(collectionSize, samples),
                list,
                ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
          };

      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants.FORMAT_DOES_NOT_SUPPORT_LIST_ADD_E_WITH_NEW_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAddWithNewElement)
          .forEachOrdered(subTests::add);

      ThrowingConsumer<CollectionSize> doesNotSupportAddWithExistingElement =
          collectionSize -> {
            List<E> list = newListToTest(generator, collectionSize);

            assertThrows(
                UnsupportedOperationException.class,
                () -> list.add(e0),
                () ->
                    String.format(
                        ListContractConstants
                            .FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION,
                        quote(e0)));
            assertIterableEquals(
                collectionSizeToElements(collectionSize, samples),
                list,
                ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
          };

      DynamicTest.stream(
              minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants
                          .FORMAT_DOES_NOT_SUPPORT_LIST_ADD_E_WITH_EXISTING_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAddWithExistingElement)
          .forEachOrdered(subTests::add);

      tests.add(dynamicContainer(ListContractConstants.DOES_NOT_SUPPORT_LIST_ADD_E, subTests));
    }
  }

  private void generateDoesNotSupportAddWithNullElementsTests(List<DynamicNode> tests) {
    if (!features.contains(CollectionFeature.SUPPORTS_ADD)) {
      List<DynamicTest> subTests = new ArrayList<>();

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
                        ListContractConstants.NULL));
            assertIterableEquals(
                collectionSizeToElements(collectionSize, samples),
                list,
                ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
          };

      DynamicTest.stream(
              supportedCollectionSizes.iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants
                          .FORMAT_DOES_NOT_SUPPORT_LIST_ADD_E_WITH_NEW_NULL_ELEMENT,
                      collectionSize.size(),
                      collectionSizeToElements(collectionSize, samples)),
              doesNotSupportAddWithNewNullElement)
          .forEachOrdered(subTests::add);

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
                        ListContractConstants.NULL));
            assertIterableEquals(
                newCollectionWithNullElementInMiddle(samples, collectionSize),
                list,
                ListContractConstants.NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED);
          };

      DynamicTest.stream(
              minus(supportedCollectionSizes, CollectionSize.SUPPORTS_ZERO).iterator(),
              collectionSize ->
                  String.format(
                      ListContractConstants
                          .FORMAT_DOES_NOT_SUPPORT_LIST_ADD_E_WITH_EXISTING_NULL_ELEMENT,
                      collectionSize.size(),
                      newCollectionWithNullElementInMiddle(samples, collectionSize)),
              doesNotSupportAddWithExistingNullElement)
          .forEachOrdered(subTests::add);

      tests.add(
          dynamicContainer(
              ListContractConstants.DOES_NOT_SUPPORT_LIST_ADD_E_WITH_NULL_ELEMENT, subTests));
    }
  }
}

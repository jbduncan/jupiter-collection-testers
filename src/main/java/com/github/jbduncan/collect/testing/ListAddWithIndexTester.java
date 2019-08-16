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

import static com.github.jbduncan.collect.testing.Features.allFeaturesRecursively;
import static com.github.jbduncan.collect.testing.Helpers.extractConcreteSizes;
import static com.github.jbduncan.collect.testing.Helpers.extractConcreteSizesExceptZero;
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
  private final TestListGenerator<E> testListGenerator;
  private final Set<Feature<?>> features;
  private final SampleElements<E> samples;
  private final E newElement;
  private final E existingElement;

  private ListAddWithIndexTester(TestListGenerator<E> testListGenerator, Set<Feature<?>> features) {
    this.testListGenerator = requireNonNull(testListGenerator, "testListGenerator");
    this.features = allFeaturesRecursively(requireNonNull(features, "features"));
    this.samples = requireNonNull(testListGenerator.samples(), "samples");
    this.newElement = samples.missing();
    this.existingElement = samples.e0();
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

    // TODO: Consider inlining `ListAddWithIndexTester#dynamicTestsGraph` into this method and
    //   returning `List<DynamicNode>` rather than `ListAddWithIndexTester<E>`.
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
      subTests.addAll(new ListAddAtStartSubTestMaker<>(this).supportsAddWithIndexSubTests());
      subTests.addAll(new ListAddAtEndSubTestMaker<>(this).supportsAddWithIndexSubTests());
      subTests.addAll(new ListAddAtMiddleSubTestMaker<>(this).supportsAddWithIndexSubTests());
      subTests.addAll(
          new ListAddAtMinusOneSubTestMaker<>(this, IndexOutOfBoundsException.class)
              .doesNotSupportAddWithIndexSubTests());
      subTests.addAll(
          new ListAddAtSizePlusOneSubTestMaker<>(this, IndexOutOfBoundsException.class)
              .doesNotSupportAddWithIndexSubTests());

      if (features.contains(CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION)) {
        subTests.addAll(
            new ListAddAtStartSubTestMaker<>(this).failsFastOnConcurrentModificationSubTests());
        if (features.contains(CollectionFeature.ALLOWS_NULL_VALUES)) {
          subTests.addAll(
              new ListAddAtStartSubTestMaker<>(this)
                  .failsFastOnConcurrentModificationInvolvingNullElementSubTests());
        }
      }

      tests.add(dynamicContainer("Supports List.add(int, E)", subTests));

      if (!features.contains(CollectionFeature.ALLOWS_NULL_VALUES)) {
        List<DynamicTest> innerSubTests = new ArrayList<>();
        innerSubTests.addAll(
            new ListAddAtStartSubTestMaker<>(this).doesNotSupportAddWithIndexForNullsSubTests());
        innerSubTests.addAll(
            new ListAddAtEndSubTestMaker<>(this).doesNotSupportAddWithIndexForNullsSubTests());
        innerSubTests.addAll(
            new ListAddAtMiddleSubTestMaker<>(this).doesNotSupportAddWithIndexForNullsSubTests());
        tests.add(dynamicContainer("Doesn't support List.add(int, null)", innerSubTests));
      }
    }
  }

  private void generateSupportsAddWithIndexWithNullElementsTests(List<DynamicNode> tests) {
    if (features.containsAll(
        Arrays.asList(ListFeature.SUPPORTS_ADD_WITH_INDEX, CollectionFeature.ALLOWS_NULL_VALUES))) {
      List<DynamicTest> subTests = new ArrayList<>();
      subTests.addAll(
          new ListAddAtStartSubTestMaker<>(this).supportsAddWithIndexForNullsSubTests());
      subTests.addAll(new ListAddAtEndSubTestMaker<>(this).supportsAddWithIndexForNullsSubTests());
      subTests.addAll(
          new ListAddAtMiddleSubTestMaker<>(this).supportsAddWithIndexForNullsSubTests());
      subTests.addAll(
          new ListAddAtMinusOneSubTestMaker<>(this, IndexOutOfBoundsException.class)
              .doesNotSupportAddWithIndexForNullsSubTests());
      subTests.addAll(
          new ListAddAtSizePlusOneSubTestMaker<>(this, IndexOutOfBoundsException.class)
              .doesNotSupportAddWithIndexForNullsSubTests());
      tests.add(dynamicContainer("Supports List.add(int, null)", subTests));
    }
  }

  private void generateDoesNotSupportAddWithIndexTests(List<DynamicNode> tests) {
    if (!features.contains(ListFeature.SUPPORTS_ADD_WITH_INDEX)) {
      List<DynamicTest> subTests = new ArrayList<>();
      subTests.addAll(new ListAddAtStartSubTestMaker<>(this).doesNotSupportAddWithIndexSubTests());
      subTests.addAll(new ListAddAtEndSubTestMaker<>(this).doesNotSupportAddWithIndexSubTests());
      subTests.addAll(new ListAddAtMiddleSubTestMaker<>(this).doesNotSupportAddWithIndexSubTests());
      subTests.addAll(
          new ListAddAtMinusOneSubTestMaker<>(this, UnsupportedOperationException.class)
              .doesNotSupportAddWithIndexSubTests());
      subTests.addAll(
          new ListAddAtSizePlusOneSubTestMaker<>(this, UnsupportedOperationException.class)
              .doesNotSupportAddWithIndexSubTests());
      tests.add(dynamicContainer("Doesn't support List.add(int, E)", subTests));
    }
  }

  private void generateDoesNotSupportAddWithIndexWithNullElementsTests(List<DynamicNode> tests) {
    if (!features.contains(ListFeature.SUPPORTS_ADD_WITH_INDEX)) {
      List<DynamicTest> subTests = new ArrayList<>();
      subTests.addAll(
          new ListAddAtStartSubTestMaker<>(this).doesNotSupportAddWithIndexForNullsSubTests());
      subTests.addAll(
          new ListAddAtEndSubTestMaker<>(this).doesNotSupportAddWithIndexForNullsSubTests());
      subTests.addAll(
          new ListAddAtMiddleSubTestMaker<>(this).doesNotSupportAddWithIndexForNullsSubTests());
      subTests.addAll(
          new ListAddAtMinusOneSubTestMaker<>(this, UnsupportedOperationException.class)
              .doesNotSupportAddWithIndexForNullsSubTests());
      subTests.addAll(
          new ListAddAtSizePlusOneSubTestMaker<>(this, UnsupportedOperationException.class)
              .doesNotSupportAddWithIndexForNullsSubTests());
      tests.add(dynamicContainer("Doesn't support List.add(int, null)", subTests));
    }
  }

  static class ListAddAtStartSubTestMaker<E> extends AbstractListAddAtValidIndexSubTestMaker<E> {

    ListAddAtStartSubTestMaker(ListAddWithIndexTester<E> tester) {
      super(
          tester.testListGenerator,
          tester.samples,
          tester.newElement,
          tester.existingElement,
          extractConcreteSizes(tester.features),
          extractConcreteSizesExceptZero(tester.features));
    }

    @Override
    int index(CollectionSize listSize) {
      return 0;
    }

    @Override
    String indexName() {
      return "0";
    }
  }

  private static class ListAddAtEndSubTestMaker<E>
      extends AbstractListAddAtValidIndexSubTestMaker<E> {

    ListAddAtEndSubTestMaker(ListAddWithIndexTester<E> tester) {
      super(
          tester.testListGenerator,
          tester.samples,
          tester.newElement,
          tester.existingElement,
          extractConcreteSizes(tester.features),
          extractConcreteSizesExceptZero(tester.features));
    }

    @Override
    int index(CollectionSize listSize) {
      return listSize.size();
    }

    @Override
    String indexName() {
      return "size()";
    }
  }

  private static class ListAddAtMiddleSubTestMaker<E>
      extends AbstractListAddAtValidIndexSubTestMaker<E> {

    ListAddAtMiddleSubTestMaker(ListAddWithIndexTester<E> tester) {
      super(
          tester.testListGenerator,
          tester.samples,
          tester.newElement,
          tester.existingElement,
          extractConcreteSizes(tester.features),
          extractConcreteSizesExceptZero(tester.features));
    }

    @Override
    int index(CollectionSize listSize) {
      return listSize.size() / 2;
    }

    @Override
    String indexName() {
      return "middleIndex()";
    }
  }

  private static class ListAddAtMinusOneSubTestMaker<E>
      extends AbstractListAddAtOutOfBoundsIndexSubTestMaker<E> {

    ListAddAtMinusOneSubTestMaker(
        ListAddWithIndexTester<E> tester, Class<? extends Throwable> expectedExceptionType) {
      super(
          tester.testListGenerator,
          tester.samples,
          tester.newElement,
          tester.existingElement,
          extractConcreteSizes(tester.features),
          extractConcreteSizesExceptZero(tester.features),
          expectedExceptionType);
    }

    @Override
    int index(CollectionSize listSize) {
      return -1;
    }

    @Override
    String indexName() {
      return "-1";
    }
  }

  private static class ListAddAtSizePlusOneSubTestMaker<E>
      extends AbstractListAddAtOutOfBoundsIndexSubTestMaker<E> {

    ListAddAtSizePlusOneSubTestMaker(
        ListAddWithIndexTester<E> tester, Class<? extends Throwable> expectedExceptionType) {
      super(
          tester.testListGenerator,
          tester.samples,
          tester.newElement,
          tester.existingElement,
          extractConcreteSizes(tester.features),
          extractConcreteSizesExceptZero(tester.features),
          expectedExceptionType);
    }

    @Override
    int index(CollectionSize listSize) {
      return listSize.size() + 1;
    }

    @Override
    String indexName() {
      return "size() + 1";
    }
  }
}

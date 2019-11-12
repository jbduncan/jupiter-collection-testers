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

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import com.google.common.graph.SuccessorsFunction;
import com.google.common.graph.Traverser;
import com.google.common.truth.Correspondence;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

// TODO: Assert that each call to contract::* causes the relevant List methods to be called. For
// example, if contract::addWithIndex is run on ArrayList, assert that ArrayList.add(int, E) is
// called. Use a library like Mockito for this.
// TODO: Consider testing with JUnit 5's `junit-platform-testkit`
// (https://github.com/junit-team/junit5/pull/1392)
class ListContractTests {

  private static final Correspondence<DynamicNode, String>
      DYNAMIC_NODE_TO_DISPLAY_NAME_CORRESPONDENCE =
          Correspondence.from(
              (actualDynamicNode, expectedDisplayName) -> {
                Objects.requireNonNull(actualDynamicNode, "actualDynamicNode");
                Objects.requireNonNull(expectedDisplayName, "expectedDisplayName");
                return actualDynamicNode.getDisplayName().equals(expectedDisplayName);
              },
              "has a display name equal to");

  private static List<String> elements;

  @BeforeAll
  static void beforeAll() {
    elements = SampleElements.strings().asList();
  }

  @Nested
  class ForArrayLists {
    private ListContract<String> contract;
    private List<String> generatedList;

    @BeforeEach
    void beforeEach() {
      contract = new ArrayListTests();
      generatedList = contract.generator().create(elements);
    }

    @Test
    @SuppressWarnings("PMD.LooseCoupling")
    void theClassOfTheGeneratedListIsAsExpected() {
      assertThat(generatedList.getClass()).isEqualTo(ArrayList.class);
    }

    @Test
    void theGeneratedListExactlyContainsGivenElementsInOrder() {
      assertThat(generatedList).containsExactlyElementsIn(elements).inOrder();
    }

    @Test
    void theExpandedFeaturesAreAsExpected() {
      assertThat(contract.features())
          .containsExactly(
              ListFeature.GENERAL_PURPOSE,
              ListFeature.SUPPORTS_SET,
              ListFeature.SUPPORTS_ADD_WITH_INDEX,
              ListFeature.SUPPORTS_REMOVE_WITH_INDEX,
              CollectionFeature.GENERAL_PURPOSE,
              CollectionFeature.SUPPORTS_ADD,
              CollectionFeature.SUPPORTS_REMOVE,
              CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
              CollectionFeature.SERIALIZABLE,
              CollectionFeature.ALLOWS_NULL_VALUES,
              CollectionFeature.ALLOWS_NULL_QUERIES,
              CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
              CollectionSize.SUPPORTS_ANY_SIZE,
              CollectionSize.SUPPORTS_ZERO,
              CollectionSize.SUPPORTS_ONE,
              CollectionSize.SUPPORTS_MULTIPLE);
    }

    @Test
    void theAddTestFactoryHasTheExpectedStructure() {
      assertExpectedDisplayNames(
          contract::add,
          /* expectedDynamicContainerDisplayNames = */ ImmutableList.of(
              "Supports List.add(E)", "Supports List.add(null)"),
          /* expectedDynamicTestDisplayNames = */ ImmutableList.of(
              "Supports List.add(\"d\") on []",
              "Supports List.add(\"d\") on [\"a\"]",
              "Supports List.add(\"d\") on [\"a\", \"b\", \"c\"]",
              "Supports List.add(\"a\") on [\"a\"]",
              "Supports List.add(\"a\") on [\"a\", \"b\", \"c\"]",
              "Supports List.add(null) on []",
              "Supports List.add(null) on [\"a\"]",
              "Supports List.add(null) on [\"a\", \"b\", \"c\"]",
              "Supports List.add(null) on [null]",
              "Supports List.add(null) on [\"a\", null, \"c\"]"));
    }

    @Test
    void theAddWithIndexTestFactoryHasTheExpectedStructure() {
      Stream<String> displayNames =
          Sets.cartesianProduct(
                  ImmutableSet.of(
                      "Supports List.add(0",
                      "Supports List.add(size()",
                      "Supports List.add(middleIndex()",
                      "Doesn't support List.add(-1",
                      "Doesn't support List.add(size() + 1"),
                  ImmutableSet.of(
                      "\"d\") on []",
                      "\"d\") on [\"a\"]",
                      "\"d\") on [\"a\", \"b\", \"c\"]",
                      "\"a\") on [\"a\"]",
                      "\"a\") on [\"a\", \"b\", \"c\"]",
                      "null) on []",
                      "null) on [\"a\"]",
                      "null) on [\"a\", \"b\", \"c\"]",
                      "null) on [null]",
                      "null) on [\"a\", null, \"c\"]"))
              .stream()
              .map(tuple -> tuple.get(0) + ", " + tuple.get(1))
              // Filter out display names for all redundant dynamic tests.
              .filter(
                  displayName ->
                      !Arrays.asList(
                              "Supports List.add(middleIndex(), \"d\") on []",
                              "Supports List.add(middleIndex(), null) on []",
                              "Supports List.add(size(), \"d\") on []",
                              "Supports List.add(size(), null) on []")
                          .contains(displayName));

      Stream<String> displayNamesForConcurrentModificationTests =
          Sets.cartesianProduct(
                  ImmutableSet.of("List.add(0, \"d\")", "List.add(0, null)"),
                  ImmutableSet.of("[]", "[\"a\"]", "[\"a\", \"b\", \"c\"]"))
              .stream()
              .map(
                  tuple ->
                      tuple.get(0) + " fails fast when concurrently modifying " + tuple.get(1));

      ImmutableList<String> expectedDynamicTestDisplayNames =
          Stream.concat(displayNames, displayNamesForConcurrentModificationTests)
              .collect(toImmutableList());

      assertExpectedDisplayNames(
          contract::addWithIndex,
          /* expectedDynamicContainerDisplayNames = */ ImmutableList.of(
              "Supports List.add(int, E)", "Supports List.add(int, null)"),
          /* expectedDynamicTestDisplayNames = */ expectedDynamicTestDisplayNames);
    }
  }

  @Nested
  class ForCollectionsEmptyList {
    private ListContract<String> contract;
    private List<String> generatedList;

    @BeforeEach
    void beforeEach() {
      contract = new CollectionsEmptyListTests();
      generatedList = contract.generator().create(elements);
    }

    @Test
    void theClassOfTheGeneratedListIsAsExpected() {
      assertThat(generatedList.getClass()).isEqualTo(Collections.EMPTY_LIST.getClass());
    }

    @Test
    void theGeneratedListHasExpectedElements() {
      assertThat(generatedList).isEmpty();
    }

    @Test
    void theExpandedFeaturesAreAsExpected() {
      assertThat(contract.features())
          .containsExactly(CollectionFeature.SERIALIZABLE, CollectionSize.SUPPORTS_ZERO);
    }

    @Test
    void theAddTestFactoryHasTheExpectedStructure() {
      assertExpectedDisplayNames(
          contract::add,
          /* expectedDynamicContainerDisplayNames = */ ImmutableList.of(
              "Doesn't support List.add(E)", "Doesn't support List.add(null)"),
          /* expectedDynamicTestDisplayNames = */ ImmutableList.of(
              "Doesn't support List.add(\"d\") on []", "Doesn't support List.add(null) on []"));
    }

    @Test
    void theAddWithIndexTestFactoryHasTheExpectedStructure() {
      ImmutableList<String> expectedDynamicTestNames =
          Sets.cartesianProduct(
                  ImmutableSet.of("0", "-1", "size() + 1"), ImmutableSet.of("\"d\"", "null"))
              .stream()
              .map(
                  tuple ->
                      "Doesn't support List.add(" + tuple.get(0) + ", " + tuple.get(1) + ") on []")
              .collect(toImmutableList());

      assertExpectedDisplayNames(
          contract::addWithIndex,
          /* expectedDynamicContainerDisplayNames = */ ImmutableList.of(
              "Doesn't support List.add(int, E)", "Doesn't support List.add(int, null)"),
          expectedDynamicTestNames);
    }
  }

  @Nested
  class ForCollectionsSingletonList {
    private ListContract<String> contract;
    private List<String> generatedList;

    @BeforeEach
    void beforeEach() {
      contract = new CollectionsSingletonListTests();
      generatedList = contract.generator().create(elements);
    }

    @Test
    void theClassOfTheGeneratedListIsAsExpected() {
      assertThat(generatedList.getClass())
          .isEqualTo(Collections.singletonList("sole element").getClass());
    }

    @Test
    void theGeneratedListHasExpectedElements() {
      assertThat(generatedList).containsExactly(elements.get(0));
    }

    @Test
    void theExpandedFeaturesAreAsExpected() {
      assertThat(contract.features())
          .containsExactly(
              CollectionFeature.SERIALIZABLE,
              CollectionFeature.ALLOWS_NULL_VALUES,
              CollectionFeature.ALLOWS_NULL_QUERIES,
              CollectionSize.SUPPORTS_ONE);
    }

    @Test
    void theAddTestFactoryHasTheExpectedStructure() {
      assertExpectedDisplayNames(
          contract::add,
          /* expectedDynamicContainerDisplayNames = */ ImmutableList.of(
              "Doesn't support List.add(E)", "Doesn't support List.add(null)"),

          // TODO: Consider appending each of these dynamic test display names with something like:
          // ", added: d" or "rejected adding: d"
          /* expectedDynamicTestDisplayNames = */ ImmutableList.of(
              "Doesn't support List.add(\"d\") on [\"a\"]",
              "Doesn't support List.add(\"a\") on [\"a\"]",
              "Doesn't support List.add(null) on [\"a\"]",
              "Doesn't support List.add(null) on [null]"));
    }

    @Test
    void theAddWithIndexTestFactoryHasTheExpectedStructure() {
      // TODO: Consider appending each of these dynamic test names with something like:
      // ", added: d" or "rejected adding: d"
      ImmutableList<String> expectedDynamicTestDisplayNames =
          Sets.cartesianProduct(
                  // TODO: Consider removing dynamic tests for "middleIndex()" as they are redundant
                  // for lists that only support CollectionSize.SUPPORTS_ONE.
                  ImmutableSet.of("0", "size()", "middleIndex()", "-1", "size() + 1"),
                  ImmutableSet.of(
                      "\"d\") on [\"a\"]",
                      "\"a\") on [\"a\"]",
                      "null) on [\"a\"]",
                      "null) on [null]"))
              .stream()
              .map(tuple -> "Doesn't support List.add(" + tuple.get(0) + ", " + tuple.get(1))
              .collect(toImmutableList());

      assertExpectedDisplayNames(
          contract::addWithIndex,
          /* expectedDynamicContainerDisplayNames = */ ImmutableList.of(
              "Doesn't support List.add(int, E)", "Doesn't support List.add(int, null)"),
          /* expectedDynamicTestDisplayNames = */ expectedDynamicTestDisplayNames);
    }
  }

  // TODO: Consider testing the following list implementations:
  // - Guava's ImmutableList
  // - ArraysAsList
  // - LinkedList
  // - UnmodifiableList
  // - Vector
  // - CheckedList
  // - CopyOnWriteArrayList
  // - AbstractList
  // - AbstractSequentialList

  private void assertExpectedDisplayNames(
      Supplier<Iterable<DynamicNode>> dynamicNodesSupplier,
      Iterable<String> expectedDynamicContainerDisplayNames,
      Iterable<String> expectedDynamicTestDisplayNames) {

    assertThat(dynamicNodesSupplier.get())
        .comparingElementsUsing(DYNAMIC_NODE_TO_DISPLAY_NAME_CORRESPONDENCE)
        .containsExactlyElementsIn(expectedDynamicContainerDisplayNames);

    assertThat(extractDynamicTests(dynamicNodesSupplier.get()))
        .comparingElementsUsing(DYNAMIC_NODE_TO_DISPLAY_NAME_CORRESPONDENCE)
        .containsExactlyElementsIn(expectedDynamicTestDisplayNames);
  }

  private static ImmutableList<DynamicTest> extractDynamicTests(
      Iterable<? extends DynamicNode> dynamicNodes) {

    SuccessorsFunction<DynamicNode> dynamicNodeChildren =
        new SuccessorsFunction<DynamicNode>() {
          private final Map<DynamicNode, ImmutableList<DynamicNode>> dynamicNodeToChildren =
              new HashMap<>();

          @Override
          public Iterable<? extends DynamicNode> successors(DynamicNode node) {
            return dynamicNodeToChildren.computeIfAbsent(
                node,
                n ->
                    (n instanceof DynamicContainer)
                        ? ((DynamicContainer) n).getChildren().collect(toImmutableList())
                        : ImmutableList.of());
          }
        };

    return Streams.stream(Traverser.forTree(dynamicNodeChildren).breadthFirst(dynamicNodes))
        .filter(DynamicTest.class::isInstance)
        .map(DynamicTest.class::cast)
        .collect(toImmutableList());
  }

  @Test
  public void newTestListInputNullNullFalseOutputNullPointerException() {

    // Act
    assertThrows(
        NullPointerException.class,
        () -> {
          ListContractHelpers.newTestList(null, null, false);
        });

    // The method is not expected to return due to exception thrown
  }
}

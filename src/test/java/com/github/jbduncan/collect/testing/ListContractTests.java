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

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import com.google.common.graph.SuccessorsFunction;
import com.google.common.graph.Traverser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
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
      assertExpectedStructure(
          contract::add,
          /* expectedDynamicContainerNames = */ ImmutableList.of(
              "Supports List.add(E)", "Supports List.add(null)"),
          /* expectedDynamicTestNames = */ ImmutableList.of(
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
    @SuppressWarnings("PMD") // TODO: Remove this warning suppression when using cartesian set below
    void theAddWithIndexTestFactoryHasTheExpectedStructure() {
      assertExpectedStructure(
          contract::addWithIndex,
          /* expectedDynamicContainerNames = */ ImmutableList.of(
              "Supports List.add(int, E)", "Supports List.add(int, null)"),
          /* expectedDynamicTestNames = */ ImmutableList.of(
              //
              // regular elements
              //
              // List.add(0, E)
              "Supports List.add(0, \"d\") on []",
              "Supports List.add(0, \"d\") on [\"a\"]",
              "Supports List.add(0, \"d\") on [\"a\", \"b\", \"c\"]",
              "Supports List.add(0, \"a\") on [\"a\"]",
              "Supports List.add(0, \"a\") on [\"a\", \"b\", \"c\"]",
              // List.add(size(), E)
              // TODO: Consider removing the dynamic test just below, as it's redundant.
              "Supports List.add(size(), \"d\") on []",
              "Supports List.add(size(), \"d\") on [\"a\"]",
              "Supports List.add(size(), \"d\") on [\"a\", \"b\", \"c\"]",
              "Supports List.add(size(), \"a\") on [\"a\"]",
              "Supports List.add(size(), \"a\") on [\"a\", \"b\", \"c\"]",
              // List.add(middleIndex(), E)
              "Supports List.add(middleIndex(), \"d\") on [\"a\"]",
              "Supports List.add(middleIndex(), \"d\") on [\"a\", \"b\", \"c\"]",
              "Supports List.add(middleIndex(), \"a\") on [\"a\"]",
              "Supports List.add(middleIndex(), \"a\") on [\"a\", \"b\", \"c\"]",
              // List.add(-1, E)
              "Doesn't support List.add(-1, \"d\") on []",
              "Doesn't support List.add(-1, \"d\") on [\"a\"]",
              "Doesn't support List.add(-1, \"d\") on [\"a\", \"b\", \"c\"]",
              "Doesn't support List.add(-1, \"a\") on [\"a\"]",
              "Doesn't support List.add(-1, \"a\") on [\"a\", \"b\", \"c\"]",
              // List.add(size() + 1, E)
              "Doesn't support List.add(size() + 1, \"d\") on []",
              "Doesn't support List.add(size() + 1, \"d\") on [\"a\"]",
              "Doesn't support List.add(size() + 1, \"d\") on [\"a\", \"b\", \"c\"]",
              "Doesn't support List.add(size() + 1, \"a\") on [\"a\"]",
              "Doesn't support List.add(size() + 1, \"a\") on [\"a\", \"b\", \"c\"]",
              // Fails fast on concurrent modification
              "List.add(0, \"d\") fails fast when concurrently modifying []",
              "List.add(0, \"d\") fails fast when concurrently modifying [\"a\"]",
              "List.add(0, \"d\") fails fast when concurrently modifying [\"a\", \"b\", \"c\"]",
              //
              // null element(s)
              //
              // List.add(0, E)
              "Supports List.add(0, null) on []",
              "Supports List.add(0, null) on [\"a\"]",
              "Supports List.add(0, null) on [\"a\", \"b\", \"c\"]",
              "Supports List.add(0, null) on [null]",
              "Supports List.add(0, null) on [\"a\", null, \"c\"]",
              // List.add(size(), E)
              // TODO: Consider removing the dynamic test just below, as it's redundant.
              "Supports List.add(size(), null) on []",
              "Supports List.add(size(), null) on [\"a\"]",
              "Supports List.add(size(), null) on [\"a\", \"b\", \"c\"]",
              "Supports List.add(size(), null) on [null]",
              "Supports List.add(size(), null) on [\"a\", null, \"c\"]",
              // List.add(middleIndex(), E)
              "Supports List.add(middleIndex(), null) on [\"a\"]",
              "Supports List.add(middleIndex(), null) on [\"a\", \"b\", \"c\"]",
              "Supports List.add(middleIndex(), null) on [null]",
              "Supports List.add(middleIndex(), null) on [\"a\", null, \"c\"]",
              // List.add(-1, E)
              "Doesn't support List.add(-1, null) on []",
              "Doesn't support List.add(-1, null) on [\"a\"]",
              "Doesn't support List.add(-1, null) on [\"a\", \"b\", \"c\"]",
              "Doesn't support List.add(-1, null) on [null]",
              "Doesn't support List.add(-1, null) on [\"a\", null, \"c\"]",
              // List.add(size() + 1, E)
              "Doesn't support List.add(size() + 1, null) on []",
              "Doesn't support List.add(size() + 1, null) on [\"a\"]",
              "Doesn't support List.add(size() + 1, null) on [\"a\", \"b\", \"c\"]",
              "Doesn't support List.add(size() + 1, null) on [null]",
              "Doesn't support List.add(size() + 1, null) on [\"a\", null, \"c\"]",
              // Fails fast on concurrent modification
              "List.add(0, null) fails fast when concurrently modifying []",
              "List.add(0, null) fails fast when concurrently modifying [\"a\"]",
              "List.add(0, null) fails fast when concurrently modifying [\"a\", \"b\", \"c\"]"));
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
    void theGeneratedListIsEmpty() {
      assertThat(generatedList).isEmpty();
    }

    @Test
    void theExpandedFeaturesAreAsExpected() {
      assertThat(contract.features())
          .containsExactly(CollectionFeature.SERIALIZABLE, CollectionSize.SUPPORTS_ZERO);
    }

    @Test
    void theAddTestFactoryHasTheExpectedStructure() {
      assertExpectedStructure(
          contract::add,
          /* expectedDynamicContainerNames = */ ImmutableList.of(
              "Doesn't support List.add(E)", "Doesn't support List.add(null)"),
          /* expectedDynamicTestNames = */ ImmutableList.of(
              "Doesn't support List.add(\"d\") on []", "Doesn't support List.add(null) on []"));
    }

    @Test
    void theAddWithIndexTestFactoryHasTheExpectedStructure() {
      ImmutableList<String> expectedDynamicTestNames =
          Sets.cartesianProduct(
                  // TODO: Consider removing dynamic tests for "size()" and "middleIndex()" as they
                  // are redundant for lists that only support CollectionSize.SUPPORTS_ZERO.
                  ImmutableSet.of("0", "size()", "middleIndex()", "-1", "size() + 1"),
                  ImmutableSet.of("\"d\"", "null"))
              .stream()
              .map(
                  tuple -> {
                    String index = tuple.get(0);
                    String element = tuple.get(1);
                    return String.format("Doesn't support List.add(%s, %s) on []", index, element);
                  })
              .collect(toImmutableList());

      assertExpectedStructure(
          contract::addWithIndex,
          /* expectedDynamicContainerNames = */ ImmutableList.of(
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
    void theGeneratedListIsEmpty() {
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
      assertExpectedStructure(
          contract::add,
          /* expectedDynamicContainerNames = */ ImmutableList.of(
              "Doesn't support List.add(E)", "Doesn't support List.add(null)"),

          // TODO: Consider appending each of these dynamic test names with something like:
          // ", added: d" or "rejected adding: d"
          /* expectedDynamicTestNames = */ ImmutableList.of(
              "Doesn't support List.add(\"d\") on [\"a\"]",
              "Doesn't support List.add(\"a\") on [\"a\"]",
              "Doesn't support List.add(null) on [\"a\"]",
              "Doesn't support List.add(null) on [null]"));
    }

    @Test
    void theAddWithIndexTestFactoryHasTheExpectedStructure() {
      // TODO: Consider appending each of these dynamic test names with something like:
      // ", added: d" or "rejected adding: d"
      ImmutableList<String> expectedDynamicTestNames =
          Sets.cartesianProduct(
                  // TODO: Consider removing dynamic tests for "size()" and "middleIndex()" as they
                  // are redundant for lists that only support CollectionSize.SUPPORTS_ONE.
                  ImmutableSet.of("0", "size()", "middleIndex()", "-1", "size() + 1"),
                  ImmutableSet.of(
                      "\"d\") on [\"a\"]",
                      "\"a\") on [\"a\"]",
                      "null) on [\"a\"]",
                      "null) on [null]"))
              .stream()
              .map(
                  tuple -> {
                    String index = tuple.get(0);
                    String messageEnd = tuple.get(1);
                    return String.format("Doesn't support List.add(%s, %s", index, messageEnd);
                  })
              .collect(toImmutableList());

      assertExpectedStructure(
          contract::addWithIndex,
          /* expectedDynamicContainerNames = */ ImmutableList.of(
              "Doesn't support List.add(int, E)", "Doesn't support List.add(int, null)"),
          /* expectedDynamicTestNames = */ expectedDynamicTestNames);
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

  private void assertExpectedStructure(
      Supplier<Iterable<DynamicNode>> dynamicNodesSupplier,
      Iterable<String> expectedDynamicContainerNames,
      Iterable<String> expectedDynamicTestNames) {

    assertThat(dynamicNodesSupplier.get())
        .comparingElementsUsing(Correspondences.DYNAMIC_NODE_TO_DISPLAY_NAME_CORRESPONDENCE)
        .containsExactlyElementsIn(expectedDynamicContainerNames);

    assertThat(extractDynamicTests(dynamicNodesSupplier.get()))
        .comparingElementsUsing(Correspondences.DYNAMIC_NODE_TO_DISPLAY_NAME_CORRESPONDENCE)
        .containsExactlyElementsIn(expectedDynamicTestNames);
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
}

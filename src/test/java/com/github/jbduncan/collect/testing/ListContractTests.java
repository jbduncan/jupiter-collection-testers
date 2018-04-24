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

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
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
              CollectionSize.SUPPORTS_THREE);
    }

    @Test
    void theAddTestFactoryHasTheExpectedStructure() {
      assertExpectedStructure(
          contract::add,
          /* expectedDynamicContainerNames = */ ImmutableList.of(
              "Supports List.add(E)", "Supports List.add(E) with null element"),
          /* expectedDynamicTestNames = */ ImmutableList.of(
              "Supports List.add(E) with new element: size: 0, elements: []",
              "Supports List.add(E) with new element: size: 1, elements: [a]",
              "Supports List.add(E) with new element: size: 3, elements: [a, b, c]",
              "Supports List.add(E) with existing element: size: 1, elements: [a]",
              "Supports List.add(E) with existing element: size: 3, elements: [a, b, c]",
              "Supports List.add(E) with new null element: size: 0, elements: []",
              "Supports List.add(E) with new null element: size: 1, elements: [a]",
              "Supports List.add(E) with new null element: size: 3, elements: [a, b, c]",
              "Supports List.add(E) with existing null element: size: 1, elements: [null]",
              "Supports List.add(E) with existing null element: size: 3, elements: [a, null, c]"));
    }

    @Test
    void theAddWithIndexTestFactoryHasTheExpectedStructure() {
      assertExpectedStructure(
          contract::addWithIndex,
          /* expectedDynamicContainerNames = */ ImmutableList.of(
              "Supports List.add(int, E)", "Supports List.add(int, E) with null element"),
          /* expectedDynamicTestNames = */ ImmutableList.of(
              //
              // regular elements
              //
              // List.add(0, E)
              "Supports List.add(0, E) with new element: size: 0, elements: []",
              "Supports List.add(0, E) with new element: size: 1, elements: [a]",
              "Supports List.add(0, E) with new element: size: 3, elements: [a, b, c]",
              "Supports List.add(0, E) with existing element: size: 1, elements: [a]",
              "Supports List.add(0, E) with existing element: size: 3, elements: [a, b, c]",
              // List.add(size(), E)
              "Supports List.add(size(), E) with new element: size: 0, elements: []",
              "Supports List.add(size(), E) with new element: size: 1, elements: [a]",
              "Supports List.add(size(), E) with new element: size: 3, elements: [a, b, c]",
              "Supports List.add(size(), E) with existing element: size: 1, elements: [a]",
              "Supports List.add(size(), E) with existing element: size: 3, elements: [a, b, c]",
              // List.add(middleIndex(), E)
              "Supports List.add(middleIndex(), E) with new element: size: 1, elements: [a]",
              "Supports List.add(middleIndex(), E) with new element: size: 3, elements: [a, b, c]",
              "Supports List.add(middleIndex(), E) with existing element: size: 1, elements: [a]",
              "Supports List.add(middleIndex(), E) with existing element: size: 3, elements: "
                  + "[a, b, c]",
              //
              // null element(s)
              //
              // List.add(0, E)
              "Supports List.add(0, E) with new null element: size: 0, elements: []",
              "Supports List.add(0, E) with new null element: size: 1, elements: [a]",
              "Supports List.add(0, E) with new null element: size: 3, elements: [a, b, c]",
              "Supports List.add(0, E) with existing null element: size: 1, elements: [null]",
              "Supports List.add(0, E) with existing null element: size: 3, elements: [a, null, c]",
              // List.add(size(), E)
              "Supports List.add(size(), E) with new null element: size: 0, elements: []",
              "Supports List.add(size(), E) with new null element: size: 1, elements: [a]",
              "Supports List.add(size(), E) with new null element: size: 3, elements: [a, b, c]",
              "Supports List.add(size(), E) with existing null element: size: 1, elements: [null]",
              "Supports List.add(size(), E) with existing null element: size: 3, elements: "
                  + "[a, null, c]",
              // List.add(middleIndex(), E)
              "Supports List.add(middleIndex(), E) with new null element: size: 1, elements: [a]",
              "Supports List.add(middleIndex(), E) with new null element: size: 3, elements: "
                  + "[a, b, c]",
              "Supports List.add(middleIndex(), E) with existing null element: size: 1, elements: "
                  + "[null]",
              "Supports List.add(middleIndex(), E) with existing null element: size: 3, elements: "
                  + "[a, null, c]"));
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
              "Does not support List.add(E)", "Does not support List.add(E) with null element"),
          /* expectedDynamicTestNames = */ ImmutableList.of(
              "Does not support List.add(E) with new element: size: 0, elements: []",
              "Does not support List.add(E) with new null element: size: 0, elements: []"));
    }

    @Test
    void theAddWithIndexTestFactoryHasTheExpectedStructure() {
      assertExpectedStructure(
          contract::addWithIndex,
          /* expectedDynamicContainerNames = */ ImmutableList.of(
              "Does not support List.add(int, E)",
              "Does not support List.add(int, E) with null element"),
          /* expectedDynamicTestNames = */ ImmutableList.of(
              "Does not support List.add(0, E) with new element: size: 0, elements: []",
              "Does not support List.add(size(), E) with new element: size: 0, elements: []",
              "Does not support List.add(middleIndex(), E) with new element: size: 0, elements: []",
              "Does not support List.add(0, E) with new null element: size: 0, elements: []",
              "Does not support List.add(size(), E) with new null element: size: 0, elements: []",
              "Does not support List.add(middleIndex(), E) with new null element: "
                  + "size: 0, elements: []"));
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
              "Does not support List.add(E)", "Does not support List.add(E) with null element"),
          /* expectedDynamicTestNames = */ ImmutableList.of(
              "Does not support List.add(E) with new element: size: 1, elements: [a]",
              "Does not support List.add(E) with existing element: size: 1, elements: [a]",
              "Does not support List.add(E) with new null element: size: 1, elements: [a]",
              "Does not support List.add(E) with existing null element: size: 1, elements: [null]"));
    }

    @Test
    void theAddWithIndexTestFactoryHasTheExpectedStructure() {
      assertExpectedStructure(
          contract::addWithIndex,
          /* expectedDynamicContainerNames = */ ImmutableList.of(
              "Does not support List.add(int, E)",
              "Does not support List.add(int, E) with null element"),
          /* expectedDynamicTestNames = */ ImmutableList.of(
              "Does not support List.add(0, E) with new element: size: 1, elements: [a]",
              "Does not support List.add(0, E) with existing element: size: 1, elements: [a]",
              "Does not support List.add(size(), E) with new element: size: 1, elements: [a]",
              "Does not support List.add(size(), E) with existing element: size: 1, elements: [a]",
              "Does not support List.add(middleIndex(), E) with new element: "
                  + "size: 1, elements: [a]",
              "Does not support List.add(middleIndex(), E) with existing element: "
                  + "size: 1, elements: [a]",
              "Does not support List.add(0, E) with new null element: size: 1, elements: [a]",
              "Does not support List.add(0, E) with existing null element: "
                  + "size: 1, elements: [null]",
              "Does not support List.add(size(), E) with new null element: size: 1, elements: [a]",
              "Does not support List.add(size(), E) with existing null element: "
                  + "size: 1, elements: [null]",
              "Does not support List.add(middleIndex(), E) with new null element: "
                  + "size: 1, elements: [a]",
              "Does not support List.add(middleIndex(), E) with existing null element: "
                  + "size: 1, elements: [null]"));
    }
  }

  // TODO: Consider testing the following list implementations:
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

    Iterable<DynamicNode> dynamicNodes = dynamicNodesSupplier.get();
    ImmutableList<DynamicTest> innerDynamicTests = extractDynamicTests(dynamicNodes);
    assertThat(innerDynamicTests)
        .comparingElementsUsing(Correspondences.DYNAMIC_NODE_TO_DISPLAY_NAME_CORRESPONDENCE)
        .containsExactlyElementsIn(expectedDynamicTestNames);
  }

  private static ImmutableList<DynamicTest> extractDynamicTests(
      Iterable<? extends DynamicNode> dynamicNodes) {

    SuccessorsFunction<DynamicNode> dynamicNodeChildren =
        new SuccessorsFunction<DynamicNode>() {
          Map<DynamicNode, ImmutableList<DynamicNode>> dynamicNodeToChildren = new HashMap<>();

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
        .filter(d -> d instanceof DynamicTest)
        .map(d -> (DynamicTest) d)
        .collect(toImmutableList());
  }
}

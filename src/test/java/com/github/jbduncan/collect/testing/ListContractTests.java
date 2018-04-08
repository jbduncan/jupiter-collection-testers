package com.github.jbduncan.collect.testing;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.google.common.graph.SuccessorsFunction;
import com.google.common.graph.Traverser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ListContractTests {
  private static final List<String> ELEMENTS = SampleElements.strings().asList();

  @Nested
  class ForArrayLists {
    private final ListContract<String> contract = new ArrayListTests();
    private final List<String> generatedList = contract.generator().create(ELEMENTS.toArray());

    @Test
    void theClassOfTheGeneratedListIsArrayList() {
      assertThat(generatedList.getClass()).isEqualTo(ArrayList.class);
    }

    @Test
    void theGeneratedListExactlyContainsGivenElementsInOrder() {
      assertThat(generatedList).containsExactlyElementsIn(ELEMENTS).inOrder();
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

  // TODO: Test against Collections.emptyList() and Collections.singletonList()
}

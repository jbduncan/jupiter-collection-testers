package com.github.jbduncan.collect.testing;

import static com.google.common.truth.Truth.assertThat;
import static java.util.stream.Collectors.toList;

import com.google.common.collect.Streams;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ListContractTests {
  private static final List<String> ELEMENTS =
      Collections.unmodifiableList(Arrays.asList("a", "b", "c"));

  @Nested
  class WhenTestingAgainstArrayLists {
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
    void theAddTestFactoryProducesTheExpectedTests() {
      assertThat(contract.add())
          .comparingElementsUsing(Correspondences.DYNAMIC_NODE_TO_DISPLAY_NAME_CORRESPONDENCE)
          .containsExactly("Supports List.add(E)", "Supports List.add(E) with null element");

      List<? extends DynamicNode> innerDynamicTests =
          Streams.stream(contract.add()).flatMap(DynamicContainer::getChildren).collect(toList());
      assertThat(innerDynamicTests)
          .comparingElementsUsing(Correspondences.DYNAMIC_NODE_TO_DISPLAY_NAME_CORRESPONDENCE)
          .containsExactly(
              "Supports List.add(E) with new element: size: 0, elements: []",
              "Supports List.add(E) with new element: size: 1, elements: [a]",
              "Supports List.add(E) with new element: size: 3, elements: [a, b, c]",
              "Supports List.add(E) with existing element: size: 1, elements: [a]",
              "Supports List.add(E) with existing element: size: 3, elements: [a, b, c]",
              "Supports List.add(E) with new null element: size: 0, elements: []",
              "Supports List.add(E) with new null element: size: 1, elements: [a]",
              "Supports List.add(E) with new null element: size: 3, elements: [a, b, c]",
              "Supports List.add(E) with existing null element: size: 1, elements: [null]",
              "Supports List.add(E) with existing null element: size: 3, elements: [a, null, c]");
    }

    @Test
    void theAddWithIndexTestFactoryProducesTheExpectedTests() {
      assertThat(contract.addWithIndex())
          .comparingElementsUsing(Correspondences.DYNAMIC_NODE_TO_DISPLAY_NAME_CORRESPONDENCE)
          .containsExactly(
              "Supports List.add(int, E)", "Supports List.add(int, E) with null element");

      List<? extends DynamicNode> innerDynamicTests =
          Streams.stream(contract.addWithIndex())
              .flatMap(DynamicContainer::getChildren)
              .collect(toList());
      assertThat(innerDynamicTests)
          .comparingElementsUsing(Correspondences.DYNAMIC_NODE_TO_DISPLAY_NAME_CORRESPONDENCE)
          .containsExactly(
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
              // List.add(size() / 2, E)
              "Supports List.add(size() / 2, E) with new element: size: 1, elements: [a]",
              "Supports List.add(size() / 2, E) with new element: size: 3, elements: [a, b, c]",
              "Supports List.add(size() / 2, E) with existing element: size: 1, elements: [a]",
              "Supports List.add(size() / 2, E) with existing element: size: 3, elements: [a, b, c]",
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
              "Supports List.add(size(), E) with existing null element: size: 3, elements: [a, null, c]",
              // List.add(size() / 2, E)
              "Supports List.add(size() / 2, E) with new null element: size: 1, elements: [a]",
              "Supports List.add(size() / 2, E) with new null element: size: 3, elements: [a, b, c]",
              "Supports List.add(size() / 2, E) with existing null element: size: 1, elements: [null]",
              "Supports List.add(size() / 2, E) with existing null element: size: 3, elements: [a, null, c]");
    }
  }

  // TODO: Test against Collections.emptyList() and Collections.singletonList()
}

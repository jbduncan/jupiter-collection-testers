package com.github.jbduncan.collect.testing;

import static com.google.common.truth.Truth.assertThat;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ListContractTests {
  private static final List<String> ELEMENTS = Stream.of("a", "b", "c").collect(toList());

  @Nested
  class WhenTestingAgainstArrayListsThat {
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
          .comparingElementsUsing(Correspondences.DYNAMIC_TEST_TO_DISPLAY_NAME_CORRESPONDENCE)
          .containsExactly(
              "Supports List.add(): size: 0, elements: []",
              "Supports List.add(): size: 1, elements: [b]",
              "Supports List.add(): size: 3, elements: [b, a, c]",
              "Supports List.add() with null element: size: 0, elements: []",
              "Supports List.add() with null element: size: 1, elements: [b]",
              "Supports List.add() with null element: size: 3, elements: [b, a, c]");
    }
  }
}

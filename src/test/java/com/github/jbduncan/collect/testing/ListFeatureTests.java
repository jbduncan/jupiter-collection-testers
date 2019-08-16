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

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

class ListFeatureTests {
  @Test
  void supportsSetHasExpectedImpliedFeatures() {
    assertThat(ListFeature.SUPPORTS_SET.impliedFeatures()).isEmpty();
  }

  @Test
  void supportsSetHasExpectedExpandedFeatures() {
    assertThat(Features.allFeaturesRecursively(ListFeature.SUPPORTS_SET))
        .containsExactly(ListFeature.SUPPORTS_SET);
  }

  @Test
  void supportsAddWithIndexHasExpectedImpliedFeatures() {
    assertThat(ListFeature.SUPPORTS_ADD_WITH_INDEX.impliedFeatures())
        .containsExactly(CollectionFeature.SUPPORTS_ADD);
  }

  @Test
  void supportsAddWithIndexHasExpectedExpandedFeatures() {
    assertThat(Features.allFeaturesRecursively(ListFeature.SUPPORTS_ADD_WITH_INDEX))
        .containsExactly(ListFeature.SUPPORTS_ADD_WITH_INDEX, CollectionFeature.SUPPORTS_ADD);
  }

  @Test
  void supportsRemoveWithIndexHasExpectedImpliedFeatures() {
    assertThat(ListFeature.SUPPORTS_REMOVE_WITH_INDEX.impliedFeatures())
        .containsExactly(CollectionFeature.SUPPORTS_REMOVE);
  }

  @Test
  void supportsRemoveWithIndexHasExpectedExpandedFeatures() {
    assertThat(Features.allFeaturesRecursively(ListFeature.SUPPORTS_REMOVE_WITH_INDEX))
        .containsExactly(ListFeature.SUPPORTS_REMOVE_WITH_INDEX, CollectionFeature.SUPPORTS_REMOVE);
  }

  @Test
  void generalPurposeHasExpectedImpliedFeatures() {
    assertThat(ListFeature.GENERAL_PURPOSE.impliedFeatures())
        .containsExactly(
            CollectionFeature.GENERAL_PURPOSE,
            ListFeature.SUPPORTS_SET,
            ListFeature.SUPPORTS_ADD_WITH_INDEX,
            ListFeature.SUPPORTS_REMOVE_WITH_INDEX);
  }

  @Test
  void generalPurposeHasExpectedExpandedFeatures() {
    assertThat(Features.allFeaturesRecursively(ListFeature.GENERAL_PURPOSE))
        .containsExactly(
            ListFeature.GENERAL_PURPOSE,
            CollectionFeature.GENERAL_PURPOSE,
            CollectionFeature.SUPPORTS_ADD,
            CollectionFeature.SUPPORTS_REMOVE,
            CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
            ListFeature.SUPPORTS_SET,
            ListFeature.SUPPORTS_ADD_WITH_INDEX,
            ListFeature.SUPPORTS_REMOVE_WITH_INDEX);
  }

  @Test
  void removeOperationsHasExpectedImpliedFeatures() {
    assertThat(ListFeature.REMOVE_OPERATIONS.impliedFeatures())
        .containsExactly(
            CollectionFeature.REMOVE_OPERATIONS, ListFeature.SUPPORTS_REMOVE_WITH_INDEX);
  }

  @Test
  void removeOperationsHasExpectedExpandedFeatures() {
    assertThat(Features.allFeaturesRecursively(ListFeature.REMOVE_OPERATIONS))
        .containsExactly(
            ListFeature.REMOVE_OPERATIONS,
            CollectionFeature.REMOVE_OPERATIONS,
            ListFeature.SUPPORTS_REMOVE_WITH_INDEX,
            CollectionFeature.SUPPORTS_REMOVE,
            CollectionFeature.SUPPORTS_ITERATOR_REMOVE);
  }
}

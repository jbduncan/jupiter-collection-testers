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

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CollectionSizeTests {
  @Test
  void sizeForSupportsZero() {
    assertThat(CollectionSize.SUPPORTS_ZERO.size()).isEqualTo(0);
  }

  @Test
  void sizeForSupportsOne() {
    assertThat(CollectionSize.SUPPORTS_ONE.size()).isEqualTo(1);
  }

  @Test
  void sizeForSupportsMultiple() {
    assertThat(CollectionSize.SUPPORTS_MULTIPLE.size()).isAtLeast(3);
  }

  @Test
  void sizeForSupportsAnySize() {
    IllegalStateException exception =
        assertThrows(IllegalStateException.class, CollectionSize.SUPPORTS_ANY_SIZE::size);
    assertThat(exception)
        .hasMessageThat()
        .isEqualTo("A compound CollectionSize doesn't specify a number of elements.");
  }

  @Test
  void impliedFeaturesForSupportsZero() {
    assertThat(CollectionSize.SUPPORTS_ZERO.impliedFeatures()).isEmpty();
  }

  @Test
  void impliedFeaturesForSupportsOne() {
    assertThat(CollectionSize.SUPPORTS_ONE.impliedFeatures()).isEmpty();
  }

  @Test
  void impliedFeaturesForSupportsMultiple() {
    assertThat(CollectionSize.SUPPORTS_MULTIPLE.impliedFeatures()).isEmpty();
  }

  @Test
  void impliedFeaturesForSupportsAnySize() {
    assertThat(CollectionSize.SUPPORTS_ANY_SIZE.impliedFeatures())
        .containsExactly(
            CollectionSize.SUPPORTS_ZERO,
            CollectionSize.SUPPORTS_ONE,
            CollectionSize.SUPPORTS_MULTIPLE);
  }
}

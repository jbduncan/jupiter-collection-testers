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

import java.util.Collections;
import java.util.Set;

public enum CollectionSize {
  SUPPORTS_ZERO(0),
  SUPPORTS_ONE(1),
  SUPPORTS_MULTIPLE(3),
  SUPPORTS_ANY_SIZE(SUPPORTS_ZERO, SUPPORTS_ONE, SUPPORTS_MULTIPLE);

  private static final int NO_SIZE = -1;

  private final int size;

  // We don't have access to Guava's immutable collections, so we're forced to use
  // Collections.unmodifiable* instead. However, we ensure that sizes are themselves
  // effectively immutable, so we can suppress this warning.
  @SuppressWarnings("ImmutableEnumChecker")
  private final Set<CollectionSize> impliedSizes;

  CollectionSize(int size) {
    this.size = checkNonNegative(size);
    this.impliedSizes = Collections.emptySet();
  }

  CollectionSize(CollectionSize... impliedSizes) {
    this.size = NO_SIZE;
    this.impliedSizes = Helpers.copyToUnmodifiableInsertionOrderSet(impliedSizes);
  }

  private int checkNonNegative(int size) {
    if (size < 0) {
      throw new IllegalArgumentException(
          String.format("'size' is %s, but it cannot be less than 0.", size));
    }
    return size;
  }

  public int size() {
    if (size == NO_SIZE) {
      throw new IllegalStateException(
          "A compound CollectionSize doesn't specify a number of elements.");
    }
    return size;
  }

  public Set<CollectionSize> impliedSizes() {
    return impliedSizes;
  }
}

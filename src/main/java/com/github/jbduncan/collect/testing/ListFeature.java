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

import java.util.List;
import java.util.Set;

// TODO: See how guava-testlib's testers cover the List interface's default methods
@SuppressWarnings("unchecked")
public enum ListFeature implements Feature<List<?>> {
  SUPPORTS_SET,
  SUPPORTS_ADD_WITH_INDEX(CollectionFeature.SUPPORTS_ADD),
  SUPPORTS_REMOVE_WITH_INDEX(CollectionFeature.SUPPORTS_REMOVE),

  GENERAL_PURPOSE(
      CollectionFeature.GENERAL_PURPOSE,
      SUPPORTS_SET,
      SUPPORTS_ADD_WITH_INDEX,
      SUPPORTS_REMOVE_WITH_INDEX),

  /** Features supported by lists where only removal is allowed. */
  REMOVE_OPERATIONS(CollectionFeature.REMOVE_OPERATIONS, SUPPORTS_REMOVE_WITH_INDEX);

  // We don't have access to Guava's immutable collections, so we're forced to use
  // Collections.unmodifiable* instead. Furthermore, we ensure that features are themselves
  // effectively immutable.
  @SuppressWarnings("ImmutableEnumChecker")
  private final Set<Feature<? super List<?>>> implied;

  ListFeature(Feature<? super List<?>>... implied) {
    this.implied = Helpers.copyToUnmodifiableInsertionOrderSet(implied);
  }

  @Override
  public Set<Feature<? super List<?>>> impliedFeatures() {
    return implied;
  }
}

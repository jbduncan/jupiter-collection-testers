package com.github.jbduncan.collect.testing;

import java.util.List;
import java.util.Set;

// TODO: See how guava-testlib's testers cover the List interface's default methods
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

  @SafeVarargs
  ListFeature(Feature<? super List<?>>... implied) {
    this.implied = Helpers.copyToUnmodifiableInsertionOrderSet(implied);
  }

  @Override
  public Set<Feature<? super List<?>>> impliedFeatures() {
    return implied;
  }
}

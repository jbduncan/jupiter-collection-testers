package com.github.jbduncan.collect.testing;

import java.util.Collection;
import java.util.Collections;
import java.util.OptionalInt;
import java.util.Set;

public enum CollectionSize implements Feature<Collection<?>>, Comparable<CollectionSize> {
  SUPPORTS_ZERO(0),
  SUPPORTS_ONE(1),
  SUPPORTS_THREE(3),
  SUPPORTS_ANY_SIZE(SUPPORTS_ZERO, SUPPORTS_ONE, SUPPORTS_THREE);

  private final OptionalInt size;
  // We don't have access to Guava's immutable collections, so we're forced to use
  // Collections.unmodifiable* instead. Furthermore, we ensure that features are themselves
  // immutable.
  @SuppressWarnings("ImmutableEnumChecker")
  private final Set<Feature<? super Collection<?>>> impliedFeatures;

  CollectionSize(int size) {
    this.size = OptionalInt.of(checkNonNegative(size));
    this.impliedFeatures = Collections.emptySet();
  }

  @SafeVarargs
  CollectionSize(Feature<? super Collection<?>>... impliedSizes) {
    this.size = OptionalInt.empty();
    this.impliedFeatures =
        Collections.unmodifiableSet(Helpers.copyToInsertionOrderSet(impliedSizes));
  }

  private int checkNonNegative(int size) {
    if (size < 0) {
      throw new IllegalArgumentException(
          String.format("'size' is %s, but it cannot be less than 0.", size));
    }
    return size;
  }

  public int size() {
    return size.orElseThrow(
        () ->
            new IllegalStateException(
                "A compound CollectionSize doesn't specify a number of elements."));
  }

  @Override
  public Set<Feature<? super Collection<?>>> impliedFeatures() {
    return impliedFeatures;
  }
}

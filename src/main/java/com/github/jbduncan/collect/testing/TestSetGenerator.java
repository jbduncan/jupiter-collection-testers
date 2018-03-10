package com.github.jbduncan.collect.testing;

import java.util.Set;

public interface TestSetGenerator<E> extends TestCollectionGenerator<E> {
  @Override
  Set<E> create(Object... elements);
}

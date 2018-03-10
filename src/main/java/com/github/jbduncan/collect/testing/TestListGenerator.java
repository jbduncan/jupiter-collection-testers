package com.github.jbduncan.collect.testing;

import java.util.List;

public interface TestListGenerator<E> extends TestCollectionGenerator<E> {
  @Override
  List<E> create(Object... elements);
}

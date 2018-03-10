package com.github.jbduncan.collect.testing;

import java.util.List;

public interface TestContainerGenerator<T, E> {
  SampleElements<E> samples();

  T create(Object... elements);

  Iterable<E> order(List<E> insertionOrder);
}

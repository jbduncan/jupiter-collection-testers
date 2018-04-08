package com.github.jbduncan.collect.testing;

import java.util.List;

public interface TestListGenerator<E> extends TestCollectionGenerator<E> {
  // TODO: Change this method to accept an `Iterable<E>` instead of `Object...`, following advice
  // in Effective Java 3rd Edition, Item x. Consider then removing TestStringListGenerator.
  @Override
  List<E> create(Object... elements);
}

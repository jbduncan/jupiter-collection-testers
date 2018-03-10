package com.github.jbduncan.collect.testing;

public interface SetContract<E> extends CollectionContract<E> {
  @Override
  TestSetGenerator<E> generator();
}

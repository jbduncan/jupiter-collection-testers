package com.github.jbduncan.collect.testing;

public interface StringCollectionContract extends CollectionContract<String> {
  @Override
  TestStringCollectionGenerator generator();
}

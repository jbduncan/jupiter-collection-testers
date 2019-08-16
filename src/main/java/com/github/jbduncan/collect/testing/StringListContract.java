package com.github.jbduncan.collect.testing;

public interface StringListContract extends ListContract<String> {
  @Override
  TestStringListGenerator generator();
}

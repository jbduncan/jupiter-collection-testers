package com.github.jbduncan.collect.testing;

@FunctionalInterface
public interface TestStringCollectionGenerator extends TestCollectionGenerator<String> {
  @Override
  default SampleElements<String> samples() {
    return SampleElements.strings();
  }
}

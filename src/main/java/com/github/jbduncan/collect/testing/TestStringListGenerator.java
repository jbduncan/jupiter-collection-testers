package com.github.jbduncan.collect.testing;

import java.util.List;

public interface TestStringListGenerator extends TestListGenerator<String> {
  @Override
  default SampleElements<String> samples() {
    return SampleElements.strings();
  }

  @Override
  default Iterable<String> order(List<String> insertionOrder) {
    return insertionOrder;
  }
}

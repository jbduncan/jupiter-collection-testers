package com.github.jbduncan.collect.testing;

import java.util.Arrays;
import java.util.List;

public interface TestStringListGenerator extends TestListGenerator<String> {
  @Override
  default SampleElements<String> samples() {
    return SampleElements.strings();
  }

  @Override
  default List<String> create(Object... elements) {
    return create(Arrays.stream(elements).map(String.class::cast).toArray(String[]::new));
  }

  /**
   * Creates a new collection containing the given elements; implement this method instead of {@link
   * #create(Object...)}.
   */
  List<String> create(String[] elements);

  @Override
  default Iterable<String> order(List<String> insertionOrder) {
    return insertionOrder;
  }
}

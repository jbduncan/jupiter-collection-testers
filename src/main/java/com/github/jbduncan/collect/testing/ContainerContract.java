package com.github.jbduncan.collect.testing;

import java.util.Set;

public interface ContainerContract<T, E> {
  TestContainerGenerator<T, E> generator();

  Set<Feature<?>> features();
}

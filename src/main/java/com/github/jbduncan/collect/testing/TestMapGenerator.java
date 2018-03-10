package com.github.jbduncan.collect.testing;

import java.util.Map;
import java.util.Map.Entry;

public interface TestMapGenerator<K, V> extends TestContainerGenerator<Map<K, V>, Entry<K, V>> {}

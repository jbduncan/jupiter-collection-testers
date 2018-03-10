package com.github.jbduncan.collect.testing;

import java.util.Map;
import java.util.Map.Entry;

public interface MapContract<K, V> extends ContainerContract<Map<K, V>, Entry<K, V>> {
  @Override
  TestMapGenerator<K, V> generator();

  // TODO: Uncomment the following when MapFeature is written
  // @Override
  // default Set<Feature<?>> features() {
  //   return Feature.allFeatures(MapFeature.GENERAL_PURPOSE);
  // }
}

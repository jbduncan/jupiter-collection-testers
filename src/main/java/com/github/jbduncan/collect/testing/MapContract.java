/*
 * Copyright 2018 the junit-jupiter-collection-testers authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jbduncan.collect.testing;

import java.util.Map;
import java.util.Map.Entry;

public interface MapContract<K, V> extends ContainerContract<Map<K, V>, Entry<K, V>> {
  @Override
  TestMapGenerator<K, V> generator();

  // TODO: Uncomment the following when MapFeature is written
  // @Override
  // default Set<Feature<?>> features() {
  //   return Feature.allFeaturesRecursively(MapFeature.GENERAL_PURPOSE);
  // }
}

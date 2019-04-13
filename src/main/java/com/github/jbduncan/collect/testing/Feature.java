/*
 * Copyright 2018-2019 the Jupiter Collection Testers authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jbduncan.collect.testing;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Queue;
import java.util.Set;

public interface Feature<T> {
  Set<Feature<? super T>> impliedFeatures();

  static Set<Feature<?>> allFeaturesRecursively(Feature<?>... features) {
    Set<Feature<?>> expandedFeatures = Helpers.copyToMutableInsertionOrderSet(features);
    Queue<Feature<?>> queue = new ArrayDeque<>(expandedFeatures);
    // Do a breadth-first traversal rooted at the input features.
    // TODO: If we ever import Guava or a graph library like JGraphT, consider using
    // com.google.common.graph.Traverser#breadthFirstTraversal or an equivalent construct for
    // iterating over all features in breadth-first order.
    while (!queue.isEmpty()) {
      Feature<?> next = queue.remove();
      for (Feature<?> implied : next.impliedFeatures()) {
        if (expandedFeatures.add(implied)) {
          queue.add(implied);
        }
      }
    }
    return Collections.unmodifiableSet(expandedFeatures);
  }
}

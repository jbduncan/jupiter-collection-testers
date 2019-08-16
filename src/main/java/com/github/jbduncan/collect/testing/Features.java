package com.github.jbduncan.collect.testing;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.Set;

public class Features {

  static Set<Feature<?>> allFeaturesRecursively(Collection<Feature<?>> features) {
    Set<Feature<?>> expandedFeatures = Helpers.copyToMutableInsertionOrderSet(features);
    Queue<Feature<?>> queue = new ArrayDeque<>(expandedFeatures);
    // Do a breadth-first traversal rooted at the input features.
    // TODO: If we ever import Guava or a graph library like JGraphT, consider using
    //   com.google.common.graph.Traverser#breadthFirstTraversal or an equivalent construct for
    //   iterating over all features in breadth-first order.
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

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

import java.util.Set;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

// TODO: Consider using custom list impls to test that each assertion passes & fails as expected
public interface ListContract<E> extends CollectionContract<E> {
  @Override
  TestListGenerator<E> generator();

  @Override
  default Set<Feature<?>> features() {
    return Feature.allFeaturesRecursively(ListFeature.GENERAL_PURPOSE);
  }

  @TestFactory
  default Iterable<DynamicNode> add() {
    return ListAddTester.<E>builder()
        .testListGenerator(generator())
        .features(features())
        .build()
        .dynamicTests();
  }

  @TestFactory
  default Iterable<DynamicNode> addWithIndex() {
    return ListAddWithIndexTester.<E>builder()
        .testListGenerator(generator())
        .features(features())
        .build()
        .dynamicTests();

    // TODO: Finish implementing this method - ListAddWithIndexTester doesn't test everything yet
    // TODO: Test this method
  }

  // TODO: Add tests for all other methods of List interface
}

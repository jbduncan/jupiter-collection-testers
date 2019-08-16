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

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

public interface CollectionContract<E> {
  TestCollectionGenerator<E> generator();

  Iterable<Feature<?>> features();

  default CollectionSize collectionSize() {
    return CollectionSize.SUPPORTS_ANY_SIZE;
  }

  @TestFactory
  default Iterable<DynamicNode> collectionAddTests() {
    // Implementation detail. It will use TestCollectionGenerator, Feature and SampleElements
    // under the hood. E.g.:
    // return CollectionAddTester.generator(generator())
    //     .features(features())
    //     .buildTests();
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> collectionAddAllTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> collectionContainsTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> collectionContainsAllTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> collectionCreationTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> collectionEqualsTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> collectionForEachTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> collectionIsEmptyTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> collectionIteratorTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> collectionRemoveTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> collectionRemoveAllTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> collectionRemoveIfTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> collectionRetainAllTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> collectionSerializationTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> collectionSizeTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> collectionSpliteratorTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> collectionStreamTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> collectionToArrayTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> collectionToStringTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }
}

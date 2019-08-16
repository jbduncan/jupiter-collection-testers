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

/**
 * {@code ListContract} is a <a
 * href="https://junit.org/junit5/docs/5.4.0/user-guide/index.html#writing-tests-test-interfaces-and-default-methods">
 * test interface</a> that your JUnit Jupiter test class implements to automatically gain the
 * ability to exhaustively test an implementation of the {@code List} interface of your choosing.
 */
// TODO: Consider using custom list impls to test that each assertion passes & fails as expected.
// TODO: Write the Javdoc for this interface.
public interface ListContract<E> extends CollectionContract<E> {
  @Override
  TestListGenerator<E> generator();

  /**
   * Returns the features that this {@code ListContract} should test for in the list implementation
   * specified in {@link #generator}.
   *
   * <p>When the list implementation is being tested, it will check not only that it satisfies the
   * given features but also the features' implied features, the implied features' own implied
   * features, and so on and so forth in a recursive fashion.
   *
   * @return the features that {@code this} should test for in the list implementation specified in
   *     {@link #generator()}.
   */
  @Override
  Iterable<Feature<?>> features();

  @TestFactory
  default Iterable<DynamicNode> listAddTests() {
    // TODO: Change implementation to be something like:
    // return ListAddTester.generator(generator())
    //     .features(features())
    //     .buildTests();

    return ListAddTester.<E>builder()
        .testListGenerator(generator())
        .features(features())
        .build()
        .dynamicTestsGraph();
  }

  @TestFactory
  default Iterable<DynamicNode> listAddAllTests() {
    // TODO: Change implementation to be something like:
    // return ListAddWithIndexTester.generator(generator())
    //     .features(features())
    //     .buildTests();
    
    return ListAddWithIndexTester.<E>builder()
        .testListGenerator(generator())
        .features(features())
        .build()
        .dynamicTestsGraph();
  }

  // TODO: Write tests for all other methods of List interface

  @TestFactory
  default Iterable<DynamicNode> listAddAllWithIndexTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> listAddWithIndexTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> listCreationTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> listEqualsTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> listGetTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> listHashCodeTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> listIndexOfTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> listLastIndexOfTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> listListIteratorTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> listRemoveTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> listRemoveAllTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> listRemoveWithIndexTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> listReplaceAllTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> listRetainAllTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> listSetTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> listSubListTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Iterable<DynamicNode> listToArrayTests() {
    throw new UnsupportedOperationException("Not yet implemented");
  }
}

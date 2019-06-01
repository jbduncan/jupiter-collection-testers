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

import java.util.Set;
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
  /**
   * Returns a factory of type {@link TestListGenerator} that will construct an instance of your
   * custom list implementation from a given arbitrary collection of elements.
   *
   * <p>The way to implement this method to test {@code ArrayList} is as follows:
   *
   * <pre>
   * &#64;Override
   * public TestListGenerator&lt;E&gt; generator() {
   *   return (TestStringListGenerator) elements -> new ArrayList&lt;String&gt;(elements);
   * }
   * </pre>
   *
   * @return the {@link TestListGenerator}
   */
  @Override
  TestListGenerator<E> generator();

  /**
   * Returns the features that this {@code ListContract} should test for in the list implementation
   * specified in {@link #generator}.
   *
   * <p>When the list implementation is being tested, it will check not only that it satisfies the
   * given features but the features' implied features, the implied features own implied features,
   * and so on and so forth in a recursive fashion.
   *
   * @return the features that {@code this} should test for in the list implementation specified in
   *     {@link #generator()}.
   */
  @Override
  default Set<Feature<?>> features() {
    // TODO: Introduce ListFeature.KNOWN_ORDER, and refer to guava-testlib's ListTestSuiteBuilder
    //   for an explanation of why we need it.
    return Feature.allFeaturesRecursively(
        ListFeature.GENERAL_PURPOSE /*, ListFeature.KNOWN_ORDER*/);
  }

  @TestFactory
  default Iterable<DynamicNode> add() {
    return ListAddTester.<E>builder()
        .testListGenerator(generator())
        .features(features())
        .build()
        .dynamicTestsGraph();
  }

  @TestFactory
  default Iterable<DynamicNode> addWithIndex() {
    return ListAddWithIndexTester.<E>builder()
        .testListGenerator(generator())
        .features(features())
        .build()
        .dynamicTestsGraph();
  }

  // TODO: Add tests for all other methods of List interface
}

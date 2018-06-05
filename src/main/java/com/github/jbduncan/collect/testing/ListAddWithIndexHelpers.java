/*
 * Copyright 2018 the Jupiter Collection Testers authors.
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

import static com.github.jbduncan.collect.testing.Helpers.newCollectionOfSize;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.ThrowingConsumer;

final class ListAddWithIndexHelpers {
  private ListAddWithIndexHelpers() {}

  static <E> void addDynamicSubTests(
      Set<CollectionSize> supportedCollectionSizes,
      String displayNameFormat,
      SampleElements<E> samples,
      ThrowingConsumer<CollectionSize> testExecutor,
      List<DynamicTest> subTests) {
    DynamicTest.stream(
            supportedCollectionSizes.iterator(),
            collectionSize ->
                String.format(
                    displayNameFormat,
                    collectionSize.size(),
                    newCollectionOfSize(collectionSize, samples)),
            testExecutor)
        .forEachOrdered(subTests::add);
  }
}

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

import static com.github.jbduncan.collect.testing.Helpers.newCollectionOfSize;
import static com.github.jbduncan.collect.testing.Helpers.newCollectionWithNullInMiddleOfSize;

import java.util.List;

final class ListContractHelpers {
  private ListContractHelpers() {}

  static <E> List<E> newListToTest(
      TestListGenerator<E> listGenerator, CollectionSize collectionSize) {
    SampleElements<E> samples = listGenerator.samples();
    return listGenerator.create(newCollectionOfSize(collectionSize, samples));
  }

  static <E> List<E> newListToTestWithNullElementInMiddle(
      TestListGenerator<E> listGenerator, CollectionSize collectionSize) {
    Iterable<E> elements =
        newCollectionWithNullInMiddleOfSize(collectionSize, listGenerator.samples());
    return listGenerator.create(elements);
  }

  static int middleIndex(List<?> list) {
    return list.size() / 2;
  }
}

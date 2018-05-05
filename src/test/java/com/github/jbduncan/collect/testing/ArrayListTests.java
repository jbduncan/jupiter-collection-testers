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

import static com.github.jbduncan.collect.testing.Helpers.stream;
import static com.github.jbduncan.collect.testing.Helpers.toUnmodifiableList;

import java.util.ArrayList;
import java.util.Set;

class ArrayListTests implements ListContract<String> {
  @Override
  public TestListGenerator<String> generator() {
    return (TestStringListGenerator)
        elements -> new ArrayList<>(stream(elements).collect(toUnmodifiableList()));
  }

  @Override
  public Set<Feature<?>> features() {
    return Feature.allFeaturesRecursively(
        ListFeature.GENERAL_PURPOSE,
        CollectionFeature.SERIALIZABLE,
        CollectionFeature.ALLOWS_NULL_VALUES,
        CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
        CollectionSize.SUPPORTS_ANY_SIZE);
  }
}

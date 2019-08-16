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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class SampleElements<E> {

  public static <E> SampleElements<E> of(E e0, E e1, E e2, E e3) {
    return new SampleElements<>(e0, e1, e2, e3);
  }

  public static SampleElements<String> strings() {
    return SampleElements.of("a", "b", "c", "d");
  }

  private final E e0;
  private final E e1;
  private final E e2;
  private final E missing;

  private SampleElements(E e0, E e1, E e2, E missing) {
    this.e0 = e0;
    this.e1 = e1;
    this.e2 = e2;
    this.missing = missing;
  }

  public E e0() {
    return e0;
  }

  public E e1() {
    return e1;
  }

  public E e2() {
    return e2;
  }

  public E missing() {
    return missing;
  }

  public List<E> toList() {
    return Collections.unmodifiableList(Arrays.asList(e0, e1, e2, missing));
  }
}

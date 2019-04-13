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
import java.util.Iterator;
import java.util.List;

public final class SampleElements<E> implements Iterable<E> {
  public static <E> SampleElements<E> of(E e0, E e1, E e2, E e3, E e4) {
    return new SampleElements<>(e0, e1, e2, e3, e4);
  }

  public static SampleElements<String> strings() {
    return SampleElements.of("a", "b", "c", "d", "e");
  }

  public static SampleElements<String> unsortedStrings() {
    // TODO: Use these sample elements when implementing contract(s) for NavigableSets
    return SampleElements.of("b", "a", "c", "d", "e");
  }

  private final E e0;
  private final E e1;
  private final E e2;
  private final E e3;
  private final E e4;

  private SampleElements(E e0, E e1, E e2, E e3, E e4) {
    this.e0 = e0;
    this.e1 = e1;
    this.e2 = e2;
    this.e3 = e3;
    this.e4 = e4;
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

  public E e3() {
    return e3;
  }

  /**
   * This element is never put into a collection for testing. It is used in tests that check that a
   * given collection <i>does not</i> contain a certain element.
   */
  public E e4() {
    return e4;
  }

  public List<E> asList() {
    return Collections.unmodifiableList(Arrays.asList(e0, e1, e2, e3, e4));
  }

  @Override
  public Iterator<E> iterator() {
    return asList().iterator();
  }
}

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

import com.google.errorprone.refaster.annotation.AfterTemplate;
import com.google.errorprone.refaster.annotation.AlsoNegation;
import com.google.errorprone.refaster.annotation.BeforeTemplate;

@SuppressWarnings("PMD")
class RefasterTemplate {
  static class StringIsEmpty {
    @BeforeTemplate
    boolean equalsEmptyString(String string) {
      return string.equals("");
    }

    @BeforeTemplate
    boolean lengthEquals0(String string) {
      return string.length() == 0;
    }

    @AfterTemplate
    @AlsoNegation
    boolean optimizedMethod(String string) {
      return string.isEmpty();
    }
  }

  // TODO: Apparently, Refaster crashes if there is more than one sub-template inner class. Fix it.

  //  static class Utf8Length {
  //    @BeforeTemplate
  //    int toUtf8Length(String string) {
  //      return string.getBytes(StandardCharsets.UTF_8).length;
  //    }
  //
  //    @AfterTemplate
  //    int optimizedMethod(String string) {
  //      return Utf8.encodedLength(string);
  //    }
  //  }
  //
  //  static class ListSwap<T> {
  //    @BeforeTemplate
  //    void manualSwap(List<T> list, int i, int j) {
  //      T tmp = list.get(i);
  //      list.set(i, list.get(j));
  //      list.set(j, tmp);
  //    }
  //
  //    @AfterTemplate
  //    void swap(List<T> list, int i, int j) {
  //      Collections.swap(list, i, j);
  //    }
  //  }
  //
  //  static class SortedFirst<T> {
  //    @BeforeTemplate
  //    Optional<T> before(Stream<T> stream, Comparator<? super T> comparator) {
  //      return stream.sorted(comparator).findFirst();
  //    }
  //
  //    @AfterTemplate
  //    Optional<T> after(Stream<T> stream, Comparator<? super T> comparator) {
  //      return stream.min(comparator);
  //    }
  //  }
  //
  //  static class AddAllArrayToBuilder<E> {
  //    @BeforeTemplate
  //    ImmutableCollection.Builder<E> addAllAsList(
  //        ImmutableCollection.Builder<E> builder, E[] elements) {
  //      return builder.addAll(com.google.errorprone.refaster.Refaster.anyOf(
  //          Arrays.asList(elements),
  //          ImmutableList.copyOf(elements),
  //          Lists.newArrayList(elements)));
  //    }
  //
  //    @AfterTemplate
  //    ImmutableCollection.Builder<E> addAll(
  //        ImmutableCollection.Builder<E> builder, E[] elements) {
  //      return builder.add(elements);
  //    }
  //  }
  //
  //  static abstract class ComputeIfAbsent<K, V> {
  //    @Placeholder
  //    abstract V function(K key);
  //
  //    @BeforeTemplate
  //    void before(Map<K, V> map, K key) {
  //      if (!map.containsKey(key)) {
  //        map.put(key, function(key));
  //      }
  //    }
  //
  //    @AfterTemplate
  //    void after(Map<K, V> map, K key) {
  //      map.computeIfAbsent(key, (K k) -> function(k));
  //    }
  //  }
  //
  //  static abstract class IfSetAdd<E> {
  //    @Placeholder
  //    abstract void doAfterAdd(E element);
  //
  //    @BeforeTemplate
  //    void ifNotContainsThenAdd(Set<E> set, E elem) {
  //      if (!set.contains(elem)) {
  //        set.add(elem);
  //        doAfterAdd(elem);
  //      }
  //    }
  //
  //    @AfterTemplate
  //    void ifAdd(Set<E> set, E elem) {
  //      if (set.add(elem)) {
  //        doAfterAdd(elem);
  //      }
  //    }
  //  }
  //
  //  static abstract class MapEntryLoop<K, V> {
  //    @Placeholder
  //    abstract void doSomething(K k, V v);
  //
  //    @BeforeTemplate
  //    void entrySetLoop(Map<K, V> map) {
  //      for (Map.Entry<K, V> entry : map.entrySet()) {
  //        doSomething(entry.getKey(), entry.getValue());
  //      }
  //    }
  //
  //    @AfterTemplate
  //    void mapForEach(Map<K, V> map) {
  //      map.forEach((K key, V value) -> doSomething(key, value));
  //    }
  //  }
}

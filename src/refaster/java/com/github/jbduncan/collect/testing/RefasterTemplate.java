package com.github.jbduncan.collect.testing;

import com.google.errorprone.refaster.annotation.AfterTemplate;
import com.google.errorprone.refaster.annotation.AlsoNegation;
import com.google.errorprone.refaster.annotation.BeforeTemplate;

@SuppressWarnings("PMD")
public class RefasterTemplate {
  public static class StringIsEmpty {
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

  //  public static class Utf8Length {
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
  //  public static class ListSwap<T> {
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
  //  class SortedFirst<T> {
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
  //  class AddAllArrayToBuilder<E> {
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
  //  public abstract class ComputeIfAbsent<K, V> {
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
  //  public abstract class IfSetAdd<E> {
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
  //  public abstract class MapEntryLoop<K, V> {
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

  // TODO: Introduce other sub-templates. See:
  // - https://github.com/google/guava/tree/master/refactorings
  // - http://errorprone.info/docs/refaster
}

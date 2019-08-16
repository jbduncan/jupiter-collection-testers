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

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

final class Helpers {
  private Helpers() {}

  static Set<CollectionSize> extractConcreteSizes(Set<Feature<?>> features) {
    return features.stream()
        .filter(CollectionSize.class::isInstance)
        .map(CollectionSize.class::cast)
        .filter(collectionSize -> collectionSize != CollectionSize.SUPPORTS_ANY_SIZE)
        .collect(toUnmodifiableInsertionOrderSet());
  }

  static Set<CollectionSize> extractConcreteSizesExceptZero(Set<Feature<?>> features) {
    return extractConcreteSizes(features).stream()
        .filter(element -> !element.equals(CollectionSize.SUPPORTS_ZERO))
        .collect(toUnmodifiableInsertionOrderSet());
  }

  private static <T> Collector<T, ?, Set<T>> toUnmodifiableInsertionOrderSet() {
    return collectingAndThen(toCollection(LinkedHashSet::new), Collections::unmodifiableSet);
  }

  @SuppressWarnings("unchecked")
  static <E> Set<E> copyToUnmodifiableInsertionOrderSet(E... elements) {
    return copyToUnmodifiableInsertionOrderSet(Arrays.asList(elements));
  }

  private static <E> Set<E> copyToUnmodifiableInsertionOrderSet(Collection<E> elements) {
    return Collections.unmodifiableSet(copyToMutableInsertionOrderSet(elements));
  }

  @SuppressWarnings("unchecked")
  static <E> Set<E> copyToMutableInsertionOrderSet(E... elements) {
    return copyToMutableInsertionOrderSet(Arrays.asList(elements));
  }

  static <E> Set<E> copyToMutableInsertionOrderSet(Collection<E> elements) {
    return new LinkedHashSet<>(elements);
  }

  static <E> Iterable<E> newIterable(
      SampleElements<E> sampleElements, CollectionSize collectionSize, boolean nullInMiddle) {
    switch (collectionSize) {
      case SUPPORTS_ZERO:
        return Collections::emptyIterator;
      case SUPPORTS_ONE:
        return () ->
            nullInMiddle
                ? Collections.<E>singletonList(null).iterator()
                : Collections.singletonList(sampleElements.e0()).iterator();
      case SUPPORTS_MULTIPLE:
        return () -> {
          List<E> elements =
              nullInMiddle
                  ? Arrays.asList(sampleElements.e0(), null, sampleElements.e2())
                  : Arrays.asList(sampleElements.e0(), sampleElements.e1(), sampleElements.e2());
          return Collections.unmodifiableList(elements).iterator();
        };
      case SUPPORTS_ANY_SIZE:
        throw new IllegalArgumentException(
            "'collectionSize' cannot be CollectionSize.SUPPORTS_ANY_SIZE; "
                + "it must be a specific size");
    }
    throw new IllegalStateException(
        String.format("'collectionSize' %s is unrecognized", collectionSize));
  }

  static <E> List<E> append(Iterable<E> iterable, E toAppend) {
    return Stream.concat(stream(iterable), Stream.of(toAppend)).collect(toUnmodifiableList());
  }

  static <E> List<E> insert(Iterable<E> iterable, int index, E toInsert) {
    List<E> result =
        iterable instanceof Collection<?>
            ? new ArrayList<>(((Collection<?>) iterable).size() + 1)
            : new ArrayList<>();
    iterable.forEach(result::add);
    result.add(index, toInsert);
    return Collections.unmodifiableList(result);
  }

  private static <T> Collector<T, ?, List<T>> toUnmodifiableList() {
    return collectingAndThen(toList(), Collections::unmodifiableList);
  }

  static <E> Stream<E> stream(Iterable<E> iterable) {
    return StreamSupport.stream(iterable.spliterator(), false);
  }

  static <E> String stringify(E value) {
    return value == null ? "null" : '"' + value.toString() + '"';
  }

  static String stringifyElements(Iterable<?> iterable) {
    return stream(iterable).map(Helpers::stringify).collect(joining(", ", "[", "]"));
  }
}

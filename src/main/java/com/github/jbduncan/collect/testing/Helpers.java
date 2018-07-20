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

// TODO: Unit test each and every single method here
final class Helpers {
  private Helpers() {}

  static Set<CollectionSize> extractConcreteSizes(Set<Feature<?>> features) {
    return features
        .stream()
        .filter(CollectionSize.class::isInstance)
        .map(CollectionSize.class::cast)
        .filter(Helpers::isConcreteSize)
        .collect(toUnmodifiableInsertionOrderSet());
  }

  private static boolean isConcreteSize(CollectionSize collectionSize) {
    return collectionSize != CollectionSize.SUPPORTS_ANY_SIZE;
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

  private static <E> Set<E> copyToMutableInsertionOrderSet(Collection<E> elements) {
    return new LinkedHashSet<>(elements);
  }

  static <E> Collection<E> newCollectionOfSize(
      CollectionSize collectionSize, SampleElements<E> sampleElements) {
    switch (collectionSize) {
      case SUPPORTS_ZERO:
        return Collections.emptyList();
      case SUPPORTS_ONE:
        return Collections.singletonList(sampleElements.e0());
      case SUPPORTS_THREE:
        return Collections.unmodifiableList(
            Arrays.asList(sampleElements.e0(), sampleElements.e1(), sampleElements.e2()));
      case SUPPORTS_ANY_SIZE:
        throw new IllegalArgumentException(
            "'collectionSize' cannot be CollectionSize.SUPPORTS_ANY_SIZE; "
                + "it must be a specific size");
      default:
        throw new IllegalStateException(
            String.format("'collectionSize' %s is unrecognized", collectionSize));
    }
  }

  static <E> Collection<E> newCollectionWithNullInMiddleOfSize(
      CollectionSize collectionSize, SampleElements<E> sampleElements) {
    switch (collectionSize) {
      case SUPPORTS_ZERO:
        throw new IllegalArgumentException(
            "Cannot create iterable that is both of size 0 and contains 'null'");
      case SUPPORTS_ONE:
        return Collections.singletonList(null);
      case SUPPORTS_THREE:
        return Collections.unmodifiableList(
            Arrays.asList(sampleElements.e0(), null, sampleElements.e2()));
      case SUPPORTS_ANY_SIZE:
        throw new IllegalArgumentException(
            "'collectionSize' cannot be CollectionSize.SUPPORTS_ANY_SIZE; "
                + "it must be a specific size");
      default:
        throw new IllegalStateException(
            String.format("'collectionSize' %s is unrecognized", collectionSize));
    }
  }

  static <E> List<E> append(Collection<E> collection, E toAppend) {
    return Stream.concat(collection.stream(), Stream.of(toAppend)).collect(toUnmodifiableList());
  }

  static <E> List<E> prepend(E toPrepend, Collection<E> collection) {
    return Stream.concat(Stream.of(toPrepend), collection.stream()).collect(toUnmodifiableList());
  }

  static <E> List<E> insert(Collection<E> collection, int index, E toInsert) {
    List<E> result = new ArrayList<>(collection.size() + 1);
    result.addAll(collection);
    result.add(index, toInsert);
    return Collections.unmodifiableList(result);
  }

  static <T> Collector<T, ?, List<T>> toUnmodifiableList() {
    return collectingAndThen(toList(), Collections::unmodifiableList);
  }

  static <E> Set<E> minus(Set<E> set, E toRemove) {
    return set.stream()
        .filter(element -> !element.equals(toRemove))
        .collect(toUnmodifiableInsertionOrderSet());
  }

  static <E> Stream<E> stream(Iterable<E> iterable) {
    return StreamSupport.stream(iterable.spliterator(), false);
  }

  static <E> String quote(E value) {
    return '"' + value.toString() + '"';
  }

  static <E> String stringify(E value) {
    return value == null ? "null" : quote(value);
  }

  static String stringifyElements(Iterable<?> iterable) {
    return StreamSupport.stream(iterable.spliterator(), false)
        .map(Helpers::stringify)
        .collect(joining(", ", "[", "]"));
  }
}

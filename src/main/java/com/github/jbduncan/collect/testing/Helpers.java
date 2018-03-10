package com.github.jbduncan.collect.testing;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;

// TODO: Unit test each and every single method here
final class Helpers {
  private Helpers() {}

  static <E> List<E> newListToTest(
      TestListGenerator<E> listGenerator, CollectionSize collectionSize) {
    SampleElements<E> samples = listGenerator.samples();
    return listGenerator.create(collectionSizeToElements(collectionSize, samples).toArray());
  }

  static <E> List<E> newListToTestWithNullElementInMiddle(
      TestListGenerator<E> listGenerator, CollectionSize collectionSize) {
    Object[] elements = newArrayWithNullElementInMiddle(listGenerator.samples(), collectionSize);
    return listGenerator.create(elements);
  }

  static <E> E[] newArrayWithNullElementInMiddle(
      SampleElements<E> samples, CollectionSize collectionSize) {
    Object[] elements;
    switch (collectionSize) {
      case SUPPORTS_ZERO:
        elements = emptyArray();
        break;
      case SUPPORTS_ONE:
        elements = new Object[] {null};
        break;
      case SUPPORTS_THREE:
        elements = new Object[] {samples.e0(), null, samples.e2()};
        break;
      default:
        throw new IllegalStateException(
            String.format("'collectionSize' %s is unrecognized", collectionSize));
    }
    @SuppressWarnings("unchecked")
    E[] result = (E[]) elements;
    return result;
  }

  private static final Object[] EMPTY = new Object[0];

  @SuppressWarnings("unchecked")
  private static <E> E[] emptyArray() {
    return (E[]) EMPTY;
  }

  static Set<CollectionSize> extractConcreteSizes(Set<Feature<?>> features) {
    return features
        .stream()
        .filter(CollectionSize.class::isInstance)
        .map(CollectionSize.class::cast)
        .filter(feature -> !feature.equals(CollectionSize.SUPPORTS_ANY_SIZE))
        .collect(toInsertionOrderSet());
  }

  private static <T> Collector<T, ?, Set<T>> toInsertionOrderSet() {
    return collectingAndThen(toCollection(LinkedHashSet::new), Collections::unmodifiableSet);
  }

  @SafeVarargs
  static <E> Set<E> copyToInsertionOrderSet(E... elements) {
    return Collections.unmodifiableSet(copyToMutableInsertionOrderSet(elements));
  }

  @SafeVarargs
  static <E> Set<E> copyToMutableInsertionOrderSet(E... elements) {
    return new LinkedHashSet<>(Arrays.asList(elements));
  }

  static <E> List<E> collectionSizeToElements(
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
    }
    throw new IllegalStateException(
        String.format("'collectionSize' %s is unrecognized", collectionSize));
  }
}

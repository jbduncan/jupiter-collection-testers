package com.github.jbduncan.collect.testing;

import static com.github.jbduncan.collect.testing.Helpers.extractConcreteSizes;
import static com.github.jbduncan.collect.testing.ListContractHelpers.addDoesNotSupportAddTests;
import static com.github.jbduncan.collect.testing.ListContractHelpers.addDoesNotSupportAddWithIndexTests;
import static com.github.jbduncan.collect.testing.ListContractHelpers.addSupportsAddTests;
import static com.github.jbduncan.collect.testing.ListContractHelpers.addSupportsAddWithIndexTests;
import static com.github.jbduncan.collect.testing.ListContractHelpers.addSupportsAddWithIndexWithNullElementsTests;
import static com.github.jbduncan.collect.testing.ListContractHelpers.addSupportsAddWithNullElementsTests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

// TODO: Use custom list implementations to test that each assertion passes & fails as expected
public interface ListContract<E> extends CollectionContract<E> {
  @Override
  TestListGenerator<E> generator();

  @Override
  default Set<Feature<?>> features() {
    return Feature.allFeaturesRecursively(ListFeature.GENERAL_PURPOSE);
  }

  @TestFactory
  default Iterable<DynamicTest> add() {
    TestListGenerator<E> generator = generator();
    SampleElements<E> samples = generator().samples();
    Set<Feature<?>> features = features();
    Set<CollectionSize> supportedCollectionSizes = extractConcreteSizes(features);

    List<DynamicTest> tests = new ArrayList<>();

    if (features.contains(CollectionFeature.SUPPORTS_ADD)) {
      addSupportsAddTests(generator, samples, supportedCollectionSizes, tests);
    }
    if (features.containsAll(
        Arrays.asList(CollectionFeature.SUPPORTS_ADD, CollectionFeature.ALLOWS_NULL_VALUES))) {
      addSupportsAddWithNullElementsTests(generator, samples, supportedCollectionSizes, tests);
    }
    if (!features.contains(CollectionFeature.SUPPORTS_ADD)) {
      addDoesNotSupportAddTests(generator, samples, supportedCollectionSizes, tests);
    }

    return tests;
  }

  @TestFactory
  default Iterable<DynamicTest> addWithIndex() {
    TestListGenerator<E> generator = generator();
    SampleElements<E> samples = generator().samples();
    Set<Feature<?>> features = features();
    Set<CollectionSize> supportedCollectionSizes = extractConcreteSizes(features);

    List<DynamicTest> tests = new ArrayList<>();

    if (features.contains(ListFeature.SUPPORTS_ADD_WITH_INDEX)) {
      addSupportsAddWithIndexTests(generator, samples, supportedCollectionSizes, tests);
    }
    if (features.containsAll(
        Arrays.asList(ListFeature.SUPPORTS_ADD_WITH_INDEX, CollectionFeature.ALLOWS_NULL_VALUES))) {
      addSupportsAddWithIndexWithNullElementsTests(
          generator, samples, supportedCollectionSizes, tests);
    }
    if (!features.contains(ListFeature.SUPPORTS_ADD_WITH_INDEX)) {
      addDoesNotSupportAddWithIndexTests(generator, samples, supportedCollectionSizes, tests);
    }

    // TODO: Finish implementing this method
    // TODO: Test this method

    return tests;
  }

  // TODO: Add tests for all other methods of List interface
}

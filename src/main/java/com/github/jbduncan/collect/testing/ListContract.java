package com.github.jbduncan.collect.testing;

import static com.github.jbduncan.collect.testing.Helpers.extractConcreteSizes;
import static com.github.jbduncan.collect.testing.ListContractHelpers.appendDoesNotSupportAddTests;
import static com.github.jbduncan.collect.testing.ListContractHelpers.appendDoesNotSupportAddWithIndexTests;
import static com.github.jbduncan.collect.testing.ListContractHelpers.appendDoesNotSupportAddWithIndexWithNullElementsTests;
import static com.github.jbduncan.collect.testing.ListContractHelpers.appendSupportsAddTests;
import static com.github.jbduncan.collect.testing.ListContractHelpers.appendSupportsAddWithIndexTests;
import static com.github.jbduncan.collect.testing.ListContractHelpers.appendSupportsAddWithIndexWithNullElementsTests;
import static com.github.jbduncan.collect.testing.ListContractHelpers.appendSupportsAddWithNullElementsTests;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DynamicNode;
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
  default Iterable<DynamicNode> add() {
    TestListGenerator<E> generator = generator();
    SampleElements<E> samples = generator().samples();
    Set<Feature<?>> features = features();
    Set<CollectionSize> supportedCollectionSizes = extractConcreteSizes(features);

    List<DynamicNode> tests = new ArrayList<>();
    appendSupportsAddTests(generator, samples, features, supportedCollectionSizes, tests);
    appendSupportsAddWithNullElementsTests(
        generator, samples, features, supportedCollectionSizes, tests);
    appendDoesNotSupportAddTests(generator, samples, features, supportedCollectionSizes, tests);
    return tests;
  }

  @TestFactory
  default Iterable<DynamicNode> addWithIndex() {
    TestListGenerator<E> generator = generator();
    SampleElements<E> samples = generator().samples();
    Set<Feature<?>> features = features();
    Set<CollectionSize> supportedCollectionSizes = extractConcreteSizes(features);

    List<DynamicNode> tests = new ArrayList<>();
    // TODO: Turn these methods into an object-orientated form, e.g. in a `ListTester` class or
    // separate `ListAddTester` and `ListAddWithIndexTester` classes.
    appendSupportsAddWithIndexTests(generator, samples, features, supportedCollectionSizes, tests);
    appendSupportsAddWithIndexWithNullElementsTests(
        generator, samples, features, supportedCollectionSizes, tests);
    appendDoesNotSupportAddWithIndexTests(
        generator, samples, features, supportedCollectionSizes, tests);
    appendDoesNotSupportAddWithIndexWithNullElementsTests(
        generator, samples, features, supportedCollectionSizes, tests);

    // TODO: Finish implementing this method
    // TODO: Test this method

    return tests;
  }

  // TODO: Add tests for all other methods of List interface
}

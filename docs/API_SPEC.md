# API

## `CollectionContract<E>`
```java
package com.github.jbduncan.collect.testing;

public interface CollectionContract<E> {
  TestCollectionGenerator<E> generator();
  
  Iterable<Feature<?>> features();
  
  default CollectionSize collectionSize() {
    return CollectionSize.SUPPORTS_ANY_SIZE;
  }
  
  @TestFactory
  default Iterable<DynamicNode> collectionAddTests() {
    // Implementation detail. It will use TestCollectionGenerator, Feature and SampleElements
    // under the hood. E.g.:
    // return CollectionAddTester.generator(generator())
    //     .features(features())
    //     .buildTests();
  }
  
  @TestFactory
  default Iterable<DynamicNode> collectionAddAllTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> collectionContainsTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> collectionContainsAllTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> collectionCreationTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> collectionEqualsTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> collectionForEachTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> collectionIsEmptyTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> collectionIteratorTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> collectionRemoveTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> collectionRemoveAllTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> collectionRemoveIfTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> collectionRetainAllTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> collectionSerializationTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> collectionSizeTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> collectionSpliteratorTests() {
    // ...
  }

  @TestFactory
  default Iterable<DynamicNode> collectionStreamTests() {
    // ...
  }

  @TestFactory
  default Iterable<DynamicNode> collectionToArrayTests() {
    // ...
  }
    
  @TestFactory
  default Iterable<DynamicNode> collectionToStringTests() {
    // ...
  }
}
```

## `StringCollectionContract`
```java
package com.github.jbduncan.collect.testing;

public interface StringCollectionContract extends CollectionContract<String> {
  @Override
  TestStringCollectionGenerator generator();
}
```

## `TestCollectionGenerator<E>`
```java
package com.github.jbduncan.collect.testing;

public interface TestCollectionGenerator<E> {
  Collection<E> create(Collection<E> elements);
  
  SampleElements<E> samples();
}
```

## `TestStringCollectionGenerator<E>`
```java
package com.github.jbduncan.collect.testing;

public interface TestStringCollectionGenerator extends TestCollectionGenerator<String> {
  @Override
  default SampleElements<E> samples() {
    return SampleElements.strings();
  }
}
```

## `ListContract<E>`
```java
package com.github.jbduncan.collect.testing;

public interface ListContract<E> extends CollectionContract<E> {
  @Override
  TestListGenerator<E> generator();
  
  @TestFactory
  default Iterable<DynamicNode> listAddTests() {
    // Implementation detail. It will use TestListGenerator, Feature and SampleElements
    // under the hood. E.g.:
    // return ListAddTester.generator(generator())
    //     .features(features())
    //     .buildTests();
  }

  @TestFactory
  default Iterable<DynamicNode> listAddAllTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> listAddAllWithIndexTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> listAddWithIndexTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> listCreationTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> listEqualsTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> listGetTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> listHashCodeTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> listIndexOfTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> listLastIndexOfTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> listListIteratorTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> listRemoveTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> listRemoveAllTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> listRemoveWithIndexTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> listReplaceAllTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> listRetainAllTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> listSetTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> listSubListTests() {
    // ...
  }
  
  @TestFactory
  default Iterable<DynamicNode> listToArrayTests() {
    // ...
  }
}
```

## `StringListContract`
```java
package com.github.jbduncan.collect.testing;

public interface StringListContract extends ListContract<String> {
  @Override
  TestStringListGenerator generator();
}
```

## `TestListGenerator<E>`
```java
package com.github.jbduncan.collect.testing;

public interface TestListGenerator<E> extends TestCollectionGenerator<E> {
  @Override
  List<E> create(Collection<E> elements);
}
```

## `TestStringListGenerator`
```java
package com.github.jbduncan.collect.testing;

public interface TestStringListGenerator extends TestListGenerator<String> {
  @Override
  default SampleElements<String> samples() {
    return SampleElements.strings();
  }
}
```

## `SampleElements`
```java
package com.github.jbduncan.collect.testing;

public final class SampleElements<E> {

  public static SampleElements<String> strings() {
    return new SampleElements<>("a", "b", "c", "d");
  }
  
  private final E e0;
  private final E e1;
  private final E e2;
  private final E missing;

  public static <E> SampleElements<E> of(
      E e0, E e1, E e2, E missing) {
    return new SampleElements<>(e0, e1, e2, missing);
  } 

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

  /**
   * This element is never put into a collection for testing. It is used in tests that check that a
   * given collection <i>does not</i> contain a certain element.
   */
  public E missing() {
    return missing;
  }
}
```

# Example usages
## Testing a mutable `List`...

```java
class ArrayListTests implements StringListContract {
  @Override
  public TestStringListGenerator generator() {
    return elements -> new ArrayList<String>(elements);
  }

  @Override
  public Iterable<Feature<?>> features() {
    return Arrays.asList(
        ListFeature.GENERAL_PURPOSE,
        CollectionFeature.SERIALIZABLE,
        CollectionFeature.ALLOWS_NULL_VALUES,
        CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
        CollectionSize.SUPPORTS_ANY_SIZE);
  }
}
```

## Testing Guava's `ImmutableList`...
```java
class ImmutableListTests extends StringListContract {
  @Override
  public TestStringListGenerator generator() {
    return elements -> ImmutableList.copyOf(elements);
  }
  
  @Override
  public Iterable<Feature<?>> features() {
    return Arrays.asList(
        CollectionFeature.SERIALIZABLE,
        CollectionFeature.ALLOWS_NULL_QUERIES);
  }
}
```

## Testing a `List` that only supports one element...
```java
class CollectionsSingletonListTests implements StringListContract {
  @Override
  public TestStringListGenerator generator() {
    return elements -> Collections.singletonList(elements.iterator().next());
  }

  @Override
  public Iterable<Feature<?>> features() {
    return Arrays.asList(
        CollectionFeature.SERIALIZABLE,
        CollectionFeature.ALLOWS_NULL_VALUES);
  }
  
  @Override
  public collectionSize() {
    return CollectionSize.SUPPORTS_ONE;
  }
}
```

## Testing a minimal implementation of `AbstractCollection`...

```java
final class MinimalCollection extends AbstractCollection<String> {

  private final String[] elements;

  private MinimalCollection(String... elements) {
    this.elements = Objects.requireNonNull(elements);
  }
  
  @Override
  public Iterator<String> iterator() {
    return Arrays.asList(elements).iterator();
  }

  @Override
  public int size() {
    return elements.length;
  }
}
```

```java
class MinimalCollectionTests extends CollectionContract<String> {
  @Override
  public TestStringCollectionGenerator generator() {
    return elements -> new MinimalCollection(elements.toArray(new String[0]));
  }
  
  @Override
  public Iterable<Feature<?>> features() {
    return Arrays.asList(CollectionFeature.NONE);
  }
}
```

## Testing a `Collection` view...

```java
class LinkedHashMapValuesTests implements CollectionContract<String> {
  @Override
  public TestStringCollectionGenerator generator() {
    return elements -> {
      LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
      int unusedKey = 0;
      elements.forEach(e -> map.put(unusedKey++, e));
      return map.values();
    };
  }
  
  @Override
  public Iterable<Feature<?>> features() {
    return Arrays.asList(
        CollectionFeature.REMOVE_OPERATIONS,
        CollectionFeature.ALLOWS_NULL_VALUES);
  }
}
```

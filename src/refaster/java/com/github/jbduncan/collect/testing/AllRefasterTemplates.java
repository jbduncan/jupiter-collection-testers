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

import com.google.errorprone.refaster.annotation.AfterTemplate;
import com.google.errorprone.refaster.annotation.AlsoNegation;
import com.google.errorprone.refaster.annotation.BeforeTemplate;
import com.google.errorprone.refaster.annotation.MayOptionallyUse;
import com.google.errorprone.refaster.annotation.Placeholder;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

class AllRefasterTemplates {
  static class CollectionIsEmpty<E> {
    @BeforeTemplate
    boolean sizeEqualsZero(Collection<E> collection) {
      return collection.size() == 0;
    }

    @AfterTemplate
    @AlsoNegation
    boolean optimizedMethod(Collection<E> collection) {
      return collection.isEmpty();
    }
  }

  abstract static class IfElseIf {
    @Placeholder
    abstract boolean firstIfCondition();

    @Placeholder
    abstract boolean secondIfCondition();

    @Placeholder
    abstract void doInFirstIf();

    @Placeholder
    abstract void doInSecondIf();

    @BeforeTemplate
    void before() {
      if (firstIfCondition()) {
        doInFirstIf();
      } else {
        if (secondIfCondition()) {
          doInSecondIf();
        }
      }
    }

    @AfterTemplate
    void after() {
      if (firstIfCondition()) {
        doInFirstIf();
      } else if (secondIfCondition()) {
        doInSecondIf();
      }
    }
  }

  abstract static class IfSetAdd<E> {
    @Placeholder
    abstract void doAfterAdd(@MayOptionallyUse E element);

    @BeforeTemplate
    void ifNotContainsThenAdd(Set<E> set, E elem) {
      if (!set.contains(elem)) {
        set.add(elem);
        doAfterAdd(elem);
      }
    }

    @AfterTemplate
    void ifAdd(Set<E> set, E elem) {
      if (set.add(elem)) {
        doAfterAdd(elem);
      }
    }
  }

  static class SortedFirst<T> {
    @BeforeTemplate
    Optional<T> before(Stream<T> stream, Comparator<? super T> comparator) {
      return stream.sorted(comparator).findFirst();
    }

    @AfterTemplate
    Optional<T> after(Stream<T> stream, Comparator<? super T> comparator) {
      return stream.min(comparator);
    }
  }

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
}

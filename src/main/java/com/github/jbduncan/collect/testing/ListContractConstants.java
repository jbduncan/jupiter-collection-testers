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

final class ListContractConstants {
  private ListContractConstants() {}

  // TODO: These constants have some duplication. Try reducing them to a smaller number and/or
  // inlining those that are used only once.
  static final String DOESNT_SUPPORT_LIST_ADD_E = "Doesn't support List.add(E)";
  static final String DOESNT_SUPPORT_LIST_ADD_NULL = "Doesn't support List.add(null)";
  static final String DOESNT_SUPPORT_LIST_ADD_INT_E = "Doesn't support List.add(int, E)";
  static final String DOESNT_SUPPORT_LIST_ADD_INT_NULL = "Doesn't support List.add(int, null)";
  static final String FORMAT_DOESNT_SUPPORT_LIST_ADD = "Doesn't support List.add(%s) on %s";
  static final String FORMAT_DOESNT_SUPPORT_LIST_ADD_WITH_INDEX =
      "Doesn't support List.add(%s, %s) on %s";
  static final String FORMAT_FAILS_FAST_ON_CONCURRENT_MODIFICATION =
      "List.add(%s, %s) fails fast when concurrently modifying %s";
  static final String FORMAT_NOT_TRUE_THAT_LIST_ADD_INT_E_THREW_EXPECTED_EXCEPTION_TYPE =
      "Not true that list.add(%s, %s) threw exception of type %s";
  static final String FORMAT_NOT_TRUE_THAT_LIST_ADD_RETURNED_TRUE =
      "Not true that list.add(%s) returned true";
  static final String FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION =
      "Not true that list.add(%s) threw UnsupportedOperationException";
  static final String FORMAT_NOT_TRUE_THAT_LIST_WAS_APPENDED =
      "Not true that list was appended with %s";
  static final String FORMAT_NOT_TRUE_THAT_LIST_WAS_PREPENDED =
      "Not true that list was prepended with %s";
  static final String FORMAT_NOT_TRUE_WAS_INSERTED_AT_INDEX_OR_IN_EXPECTED_ORDER =
      "Not true that %s was inserted at index %s of list, or that elements in list are in expected order";
  static final String FORMAT_SUPPORTS_LIST_ADD = "Supports List.add(%s) on %s";
  static final String FORMAT_SUPPORTS_LIST_ADD_WITH_INDEX = "Supports List.add(%s, %s) on %s";
  static final String NOT_TRUE_THAT_LIST_ADD_NULL_RETURNED_TRUE =
      "Not true that list.add(null) returned true";
  static final String NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED =
      "Not true that list remained unchanged";
  static final String NOT_TRUE_THAT_LIST_WAS_APPENDED_WITH_NULL =
      "Not true that list was appended with null";
  static final String NOT_TRUE_THAT_LIST_WAS_PREPENDED_WITH_NULL =
      "Not true that list was prepended with null";
  static final String SUPPORTS_LIST_ADD_E = "Supports List.add(E)";
  static final String SUPPORTS_LIST_ADD_E_WITH_NULL_ELEMENT = "Supports List.add(null)";
  static final String SUPPORTS_LIST_ADD_INT_E = "Supports List.add(int, E)";
  static final String SUPPORTS_LIST_ADD_INT_E_WITH_NULL_ELEMENT = "Supports List.add(int, null)";
}

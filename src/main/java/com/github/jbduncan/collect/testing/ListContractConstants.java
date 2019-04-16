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

final class ListContractConstants {
  private ListContractConstants() {}

  static final String FORMAT_DOESNT_SUPPORT_LIST_ADD_WITH_INDEX =
      "Doesn't support List.add(%s, %s) on %s";
  static final String FORMAT_NOT_TRUE_THAT_LIST_ADD_INT_E_THREW_EXPECTED_EXCEPTION_TYPE =
      "Not true that list.add(%s, %s) threw exception of type %s";
  static final String FORMAT_NOT_TRUE_THAT_LIST_ADD_THREW_UNSUPPORTED_OPERATION_EXCEPTION =
      "Not true that list.add(%s) threw UnsupportedOperationException";
  static final String FORMAT_NOT_TRUE_THAT_LIST_WAS_APPENDED =
      "Not true that list was appended with %s";
  static final String FORMAT_SUPPORTS_LIST_ADD_WITH_INDEX = "Supports List.add(%s, %s) on %s";
  static final String NOT_TRUE_THAT_LIST_REMAINED_UNCHANGED =
      "Not true that list remained unchanged";
  static final String NOT_TRUE_THAT_LIST_WAS_APPENDED_WITH_NULL =
      "Not true that list was appended with null";
}

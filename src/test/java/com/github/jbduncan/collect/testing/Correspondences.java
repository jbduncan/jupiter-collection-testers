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

import com.google.common.truth.Correspondence;
import java.util.Objects;
import org.junit.jupiter.api.DynamicNode;

final class Correspondences {
  private Correspondences() {}

  static final Correspondence<DynamicNode, String> DYNAMIC_NODE_TO_DISPLAY_NAME_CORRESPONDENCE =
      Correspondence.from(
          (actualDynamicNode, expectedDisplayName) -> {
            Objects.requireNonNull(actualDynamicNode, "actualDynamicNode");
            Objects.requireNonNull(expectedDisplayName, "expectedDisplayName");
            return actualDynamicNode.getDisplayName().equals(expectedDisplayName);
          },
          "has a display name equal to");
}

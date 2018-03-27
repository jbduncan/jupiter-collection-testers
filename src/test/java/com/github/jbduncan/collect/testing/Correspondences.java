package com.github.jbduncan.collect.testing;

import com.google.common.truth.Correspondence;
import java.util.Objects;
import org.junit.jupiter.api.DynamicNode;

final class Correspondences {
  private Correspondences() {}

  static final Correspondence<DynamicNode, String> DYNAMIC_NODE_TO_DISPLAY_NAME_CORRESPONDENCE =
      new Correspondence<DynamicNode, String>() {
        @Override
        public boolean compare(DynamicNode actual, String expected) {
          Objects.requireNonNull(actual, "actual");
          Objects.requireNonNull(expected, "expected");
          return actual.getDisplayName().equals(expected);
        }

        @Override
        public String toString() {
          return "has a display name equal to";
        }
      };
}

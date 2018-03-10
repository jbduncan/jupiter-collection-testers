package com.github.jbduncan.collect.testing;

import com.google.common.truth.Correspondence;
import java.util.Objects;
import org.junit.jupiter.api.DynamicTest;

final class Correspondences {
  private Correspondences() {}

  static final Correspondence<DynamicTest, String> DYNAMIC_TEST_TO_DISPLAY_NAME_CORRESPONDENCE =
      new Correspondence<DynamicTest, String>() {
        @Override
        public boolean compare(DynamicTest actual, String expected) {
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

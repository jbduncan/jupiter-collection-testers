package com.github.jbduncan.gradle.refaster;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;


public class UtilsTest {

  @Test
  public void capitalise() {

    assertThat(Utils.capitalise("")).isEqualTo("");
    assertThat(Utils.capitalise("a")).isEqualTo("A");
    assertThat(Utils.capitalise("-")).isEqualTo("-");
  }

}

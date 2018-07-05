package com.github.jbduncan.gradle.refaster;

import java.io.File;
import java.nio.file.Path;
import org.apache.commons.io.FilenameUtils;
import org.gradle.api.tasks.compile.JavaCompile;

final class Utils {
  static String capitalise(String value) {
    if (value.isEmpty()) {
      return "";
    }
    StringBuilder builder = new StringBuilder();
    builder.appendCodePoint(Character.toUpperCase(value.codePointAt(0)));
    value.codePoints().skip(1).forEachOrdered(builder::appendCodePoint);
    return builder.toString();
  }

  static File toCompiledRefasterTemplateFile(
      File refasterTemplate, File projectRootDir, File compiledRefasterTemplatesDir) {

    Path refasterTemplateRelativePath =
        projectRootDir.toPath().relativize(refasterTemplate.toPath());

    Path outputFile = compiledRefasterTemplatesDir.toPath().resolve(refasterTemplateRelativePath);
    String outputFileWithCorrectExtension =
        FilenameUtils.removeExtension(outputFile.toString()) + ".refaster";

    return new File(outputFileWithCorrectExtension);
  }

  private Utils() {}
}

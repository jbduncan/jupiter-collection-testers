package com.github.jbduncan.gradle.refaster;

import java.io.File;
import java.nio.file.Path;
import org.apache.commons.io.FilenameUtils;

final class Utils {
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

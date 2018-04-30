Contributing
===

Thank you very much for wanting to contribute to Jupiter Collection Testers! Here are a couple of
important things to note before starting:

1. Pull requests are great for small fixes for bugs, documentation, etc.
2. For anything else, please
   [open an issue](https://github.com/jbduncan/jupiter-collection-testers/issues/new) first.

Pull requests
===

Unless the change is trivial (with examples of what counts as trivial given below), it's generally
best to start by opening a new issue describing the bug or feature you're intending to fix. Even if
you think it's relatively minor, it's helpful to know what people are working on.

Some examples of types of trivial pull requests that are immediately helpful:

1. Fixing a minor bug.
2. Fixing a documentation typo.
3. Small improvements to Gradle configuration.

Setting up your coding environment
===

1. Fork the project and clone it locally.
2. Set things up in your IDE or text editor of choice. Instructions for IntelliJ IDEA are provided
   below.

Creating an IntelliJ IDEA project
---

1. Use
   [IntelliJ IDEA's support for importing Gradle projects](https://www.jetbrains.com/help/idea/gradle.html#gradle_import).
1. Download
   [Google's Java Code Style Scheme file for IntelliJ](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml),
   import it (go to `Preferences` > `Editor` > `Code Style` > `Java`, click `Manage`, then `Import`)
   and use it when working on jupiter-collection-testers' code.

Guidelines for any code contributions
===

Java
---

The Gradle command `gradlew check` and all Continuous Integration checks **must** pass before a PR
can be merged.

Any significant changes should be accompanied by tests. See the existing tests under `src/test/java`
for guidance.

All contributions must be licensed Apache 2.0 and all files must have a copy of the boilerplate
license comment (can be copied from an existing file).

Files must be formatted and refactored with the Gradle commands `gradlew spotlessApply` and `gradlew
refasterApply`, and should additionally be formatted as best as you can according to
[Google's Java style guide](https://google.github.io/styleguide/javaguide.html).

Do not use `@author` tags in Javadocs. Instead, contributors are listed on
[GitHub](https://github.com/jbduncan/jupiter-collection-testers).

Commit messages
---

Please squash all commits for a change into a single commit (this can be done using
`git rebase -i`).

Do your best to have a good commit message for the change, as described in
[How to Write a Git Commit Message](https://chris.beams.io/posts/git-commit/).

Any commit that is related to an existing issue must reference the issue. For example, if a commit
in a pull request addresses issue #999, it must contain the following at the bottom of the commit
message.
```
Resolves: #999
```
   
Miscellaneous
---

Text in `*.md` files should be wrapped at 100 characters whenever technically possible.


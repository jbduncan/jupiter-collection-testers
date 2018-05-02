Contributing
===

Thank you very much for wanting to contribute to Jupiter Collection Testers!

TODO: Check back later, will fill this hopefully sooner rather than later!

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

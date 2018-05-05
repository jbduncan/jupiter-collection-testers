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

All files must have a copy of the boilerplate license comment (which can be automatically applied
with the `gradlew spotlessApply` Gradle command).

Files must be formatted and refactored with the Gradle commands `gradlew spotlessApply` and `gradlew
refasterApply`, and should additionally be formatted as best as you can according to
[Google's Java style guide](https://google.github.io/styleguide/javaguide.html).

Do not use `@author` tags in Javadocs. Instead, contributors are listed on
[GitHub](https://github.com/jbduncan/jupiter-collection-testers).

Miscellaneous
---

Text in `*.md` files should be wrapped at 100 characters whenever technically possible.

Commit messages
---

Please squash all commits for a change into a single commit (this can be done using
`git rebase -i`).

Do your best to have a good commit message for the change, as described in
[How to Write a Git Commit Message](https://chris.beams.io/posts/git-commit/).

Any commit that is related to an existing issue must reference the issue. For example, if a commit
in a pull request refers to issue #999, please add the following at the bottom of the commit
message.
```
Issue: #999
```

Pull requests
---

If your Pull Request resolves an issue, please add a respective line to the end, like
```
Resolves #123
```

License
===

By contributing your code, you agree to license your contribution under the terms of the Apache
License, Version 2.0:
https://github.com/jbduncan/jupiter-collection-testers/blob/master/LICENSE

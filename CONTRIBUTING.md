Contributing
===

Thank you very much for wanting to contribute to Jupiter Collection Testers!

Please note that by contributing to this project, you agree to the terms of the contributor license
agreement below.

Jupiter Collection Testers Contributor License Agreement
===

- You will only submit contributions where you have authored 100% of the content.
- You will only submit contributions to which you have the necessary rights. This means that if you
  are employed you have received the necessary permissions from your employer to make the
  contributions.
- Whatever content you contribute will be provided under the project license(s).

Project License(s)
---

[Apache License, Version 2.0](https://github.com/jbduncan/jupiter-collection-testers/blob/master/LICENSE)

Pull Requests
===

Before a Pull Request can be accepted, all commits it contains must be squashed and merged into a
single commit or several logical, self-contained commits. One way to do this is to use
`git rebase -i`, following the instructions in
["Git Interactive Rebase, Squash, Amend and Other Ways of Rewriting History"](https://thoughtbot.com/blog/git-interactive-rebase-squash-amend-rewriting-history).

If your PR fixes an issue, e.g. issue #42, please add the following text anywhere in the PR body:
`This PR closes #42.`. This will allow the issue to be
[automatically closed](https://help.github.com/articles/closing-issues-using-keywords/) by GitHub
when the PR is merged in.

Commit messages
===

Please follow the advice in
["How to Write a Git Commit Message"](https://chris.beams.io/posts/git-commit/).

Please also add a line at the end of the body in its own paragraph that references your PR. For
example, if your PR is number #43, add the following:

```
Closes #43.
```

This will make it easy to go back and read the PR and the relevant issue if ever needed.

Guidelines for code contributions
===

Java
---

We use Gradle to build and test Jupiter Collection Testers. To test your changes, use the Gradle
command `gradlew test`. To fully check your changes against tests and static analysis tools, use
the command `gradlew check`.

All Continuous Integration checks **must** pass before a PR can be merged.

Any significant changes should be accompanied by tests. See the existing tests under `src/test/java`
for guidance.

All files must have a copy of the boilerplate license comment, which can be automatically applied
with the `gradlew spotlessApply` Gradle command.

Files must be formatted and refactored with the Gradle commands `gradlew spotlessApply` and `gradlew
refasterApply`, and should additionally follow
[Google's Java style guide](https://google.github.io/styleguide/javaguide.html) as best as you can.

Do not use `@author` tags in Javadocs. Instead, contributors are listed on
[GitHub](https://github.com/jbduncan/jupiter-collection-testers).

Miscellaneous
---

Text in `*.md` files should be wrapped at 100 characters whenever technically possible.

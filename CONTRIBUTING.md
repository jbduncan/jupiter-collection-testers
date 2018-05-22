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
["squashing commits with rebase"](http://gitready.com/advanced/2009/02/10/squashing-commits-with-rebase.html).

If your PR resolves an issue or multiple issues, please add a respective line towards the end of the
body of the PR text, just above the boilerplate text for the Contributor License Agreement. For
example, use the following message if issue #42 is fully resolved.
```
Resolves: #42
```

This will allow the associated issue(s) to be
[automatically closed](https://help.github.com/articles/closing-issues-using-keywords/) by GitHub
when the PR is merged in.

If your PR only partially resolves or is otherwise related to an issue or multiple issues, you can
use the words "Issue" or "Issues" instead of or on top of "Resolves". For example, use the following
message if issue #42 is partially resolved or related.
```
Issue: #42
```

To give another example, use the following message if issue #42 is fully resolved and issues #6
and #9 are partially resolved or related.

```
Resolves: #42
Issues: #6, #99
```

Commit messages
===

Each commit should have the following structure:
```
${subject}

${body}

${issues}
PR: ${pr}
```

`${subject}` is a line up to 70 characters (ideally up to 50 characters) that summarises the
change(s).

`${body}` is a more detailed explanation where each line is wrapped at 72 characters. This
explanation describes _why_ the commit is being introduced, rather than _how_ it is implemented. The
body can be skipped if the change is so simple that no further context is necessary.

`${issues}` is a series of one or more lines that describe which issues have been resolved and/or
which issues are partially resolved by or related to this commit. See *Pull Requests* above for more
information on the format of this section.

`PR: ${pr}` is a line that says which PR introduced this commit, where `${pr}` is replaced with the
PR number, e.g. `PR: #321`. This can be skipped if the commit has no associated PR.

(In many ways, this advice matches up with or is very similar to the advice given in
["How to Write a Git Commit Message"](https://chris.beams.io/posts/git-commit/).)

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
refasterApply`, and should additionally be formatted as best as you can according to
[Google's Java style guide](https://google.github.io/styleguide/javaguide.html).

Do not use `@author` tags in Javadocs. Instead, contributors are listed on
[GitHub](https://github.com/jbduncan/jupiter-collection-testers).

Miscellaneous
---

Text in `*.md` files should be wrapped at 100 characters whenever technically possible.

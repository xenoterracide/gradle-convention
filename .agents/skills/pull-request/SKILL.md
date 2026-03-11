---
name: pull-request
description: When you've made code changes that need to be saved, shared, or merged. Use for any work that results in modified files - whether fixing a bug, adding a feature, refactoring, debugging, or cleaning up. Handles committing, pushing, and creating/updating pull requests through GitHub.
license: CC-BY-NC-SA-4.0
metadata:
  author: Caleb Cushing
allowed-tools: Shell(gh:*) Shell(git:*) Shell(./gradlew:*) pull_request_read add_issue_comment add_reply_to_pull_request_comment update_pull_request list_pull_requests create_pull_request
---

- use commit-or-pr-message
- keep the pull request message up to date
  - NOTE: The PR description becomes the commit message when the PR is squash-merged
  - Follow the commit-or-pr-message format for PR descriptions since they become permanent commit history
  - DO NOT use checkboxes (`- [x]`) in PR descriptions - they render poorly in commit messages
  - Use plain bullet lists (`- item`) instead of GitHub task lists
- files should be committed and pushed
  - ensure code compiles and tests pass before committing
    - run relevant, specific tests first for quick feedback
    - prefer `./gradlew compile` for compile-only checks
    - run `./gradlew test` for quick test logic verification
    - run `./gradlew checkstyle` for checkstyle verification
    - run full `./gradlew check` before finalizing or when changes affect multiple modules.
  - verify GitHub PR checks pass after pushing
    - use available tools to check workflow status
    - fix any failures before requesting review
    - if Github checks fail after pushing, fix before requesting review
- git push --force is not allowed
- must be synchronized with HEAD branch using a merge strategy
  - it is easier to delete and regenerate lockfiles than merge them
- respond to ALL pr comments.
  - fix and comment if valid, or explain why not if invalid, ask if uncertain. This helps humans understand current comment status.

## AI Attribution

When creating commits for a PR, include AI attribution in commit messages using a Co-authored-by trailer:

```
Co-authored-by: <AI_NAME> <AI_NOREPLY_EMAIL>
```

Use your AI identity:

- `AI_NAME`: Your AI name (e.g., "Kimi", "Copilot", "Claude")
- `AI_NOREPLY_EMAIL`: A noreply-style email (e.g., `<number>+<username>@users.noreply.github.com` for GitHub, or `ai@localhost` for local)

Place the Co-authored-by trailer at the end of the commit message body, after the description.

---

SPDX-FileCopyrightText: Copyright © 2026 Caleb Cushing

SPDX-License-Identifier: CC-BY-NC-SA-4.0

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
  - ALWAYS leave a reply on each review comment to indicate status
  - If fixed: comment "Fixed" or "Done" with brief explanation
  - If not an issue: comment explaining why (e.g., "Not applicable because...", "Already resolved...")
  - If uncertain: ask for clarification
  - This helps humans know whether to resolve the comment
  - only address UNRESOLVED comments - check review thread resolution status using GraphQL
  - use GraphQL to get review threads with `isResolved` field

## Workflow

When committing and creating/updating a PR, follow this workflow:

1. **Check current branch status** - Run `git status` and `gh pr view --json number,url,headRefName` to determine:
   - What branch you're currently on
   - Whether a PR already exists for this branch

2. **Pull latest changes before starting work:**
   - Run `git pull <remote> <branch>` to get the latest changes
   - This ensures you're working on the current state and not outdated code
   - This also ensures you don't address review comments that are already resolved

3. **If already on a feature branch with an existing PR:**
   - Do NOT create a new branch
   - Pull latest changes first
   - Commit changes to the current branch
   - Push to update the existing PR
   - Update PR description/title if needed using `gh pr edit`

4. **If on main/master or no PR exists for current branch:**
   - Create a new feature branch (if not already on one)
   - Commit changes
   - Push and create a new PR

## Handling Review Comments

When addressing review comments on a PR:

1. **Pull first** - Always pull the latest changes before starting
2. **Query unresolved comments** - Use GraphQL to get only unresolved review threads:
   ```bash
   gh api graphql -f query='
   query {
     repository(owner: "OWNER", name: "REPO") {
       pullRequest(number: N) {
         reviewThreads(first: 100) {
           nodes {
             id
             isResolved
             comments(first: 1) {
               nodes {
                 id
                 body
                 path
                 originalLine
               }
             }
           }
         }
       }
     }
   }' --jq '.data.repository.pullRequest.reviewThreads.nodes | map(select(.isResolved == false))'
   ```
3. **Process unresolved** - The jq filter already returns only threads where `isResolved: false`
4. **Reply to each comment** - After making changes, reply to each review comment:
   - Fixed: `Fixed in commit SHA`
   - Not an issue: `Not applicable: [reason]`
   - Question: `Question: [clarification needed]`
   - Use `gh pr comment <number> --reply-to <comment-id>` if available, or quote the comment
5. **Verify fixes** - Confirm changes address the current code state

## Important Note on Comment APIs

The GitHub REST API (`/repos/{owner}/{repo}/pulls/{pull_number}/comments`) does NOT expose the "resolved" state of review comments. The resolution state is only available via:

- GraphQL API (`reviewThreads.isResolved`)
- GitHub Web UI

Always use GraphQL to check which review threads are actually unresolved.

## AI Attribution

When creating commits for a PR, include AI attribution in commit messages using a Co-authored-by trailer:

```
Co-authored-by: <AI_NAME> <AI_NOREPLY_EMAIL>
```

Use your AI identity:

| AI Name | `AI_NAME`        | `AI_NOREPLY_EMAIL`           |
| ------- | ---------------- | ---------------------------- |
| Kimi    | `Kimi`           | `kimi@moonshot.localhost`    |
| Copilot | `GitHub Copilot` | `copilot@github.localhost`   |
| Claude  | `Claude`         | `claude@anthropic.localhost` |

Place the Co-authored-by trailer at the end of the commit message body, after the description.

---

SPDX-FileCopyrightText: Copyright © 2026 Caleb Cushing

SPDX-License-Identifier: CC-BY-NC-SA-4.0

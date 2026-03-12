#!/usr/bin/env tsx

// SPDX-FileCopyrightText: Copyright © 2026 Caleb Cushing
//
// SPDX-License-Identifier: GPL-3.0-or-later

import { execSync } from "child_process";
import { mkdtempSync, readFileSync, unlinkSync, writeFileSync } from "fs";
import { tmpdir } from "os";
import { join } from "path";

const ENGINE = process.env.ENGINE || "kimi";

function run(cmd: string, opts?: { cwd?: string; env?: Record<string, string> }): string {
  return execSync(cmd, { encoding: "utf8", cwd: opts?.cwd, env: { ...process.env, ...opts?.env } }).trim();
}

function runSilent(cmd: string, opts?: { cwd?: string }): string {
  try {
    return run(cmd, opts);
  } catch {
    return "";
  }
}

function hasPR(): boolean {
  try {
    run("gh pr view --json number");
    return true;
  } catch {
    return false;
  }
}

function getHead(): string {
  return run("git rev-parse --verify HEAD");
}

async function generateMessage(titleFile: string, bodyFile: string): Promise<void> {
  const diffRange = "origin/HEAD...HEAD";

  // Check for changes
  try {
    run(`git diff --quiet --exit-code ${diffRange}`);
    console.log("No changes to generate message for");
    process.exit(2);
  } catch {
    // Has changes, continue
  }

  const changedFiles = run(`git diff --name-only ${diffRange}`).split("\n").slice(0, 400).join("\n");
  const changedDiff = run(`git diff ${diffRange}`).split("\n").slice(0, 2000).join("\n");

  if (ENGINE === "kimi") {
    await generateWithKimi(titleFile, bodyFile, changedDiff);
  } else if (ENGINE === "junie") {
    await generateWithJunie(titleFile, bodyFile, changedDiff);
  } else {
    await generateWithCopilot(titleFile, bodyFile, changedDiff, changedFiles);
  }
}

async function generateWithKimi(titleFile: string, bodyFile: string, diff: string): Promise<void> {
  const skillsDir = ".agents/skills";
  const hasSkillsDir = runSilent("test -d .agents/skills") !== "";

  const prompt = `Generate a conventional commit message for the following diff and write the subject line to '${titleFile}' and the body to '${bodyFile}'. Do not run any tests or gradle commands.

Diff:
${diff}`;

  const kimiArgs = ["--no-thinking", "--quiet", "--prompt", prompt];

  if (hasSkillsDir) {
    kimiArgs.unshift("--skills-dir", skillsDir);
  }

  const tmpOut = join(tmpdir(), `kimi-out-${Date.now()}.txt`);

  try {
    run(`kimi ${kimiArgs.map((a) => `"${a.replace(/"/g, '\\"')}"`).join(" ")} > "${tmpOut}" 2>&1 || true`);

    // Check if kimi wrote directly to files
    try {
      readFileSync(titleFile, "utf8");
      console.log("kimi wrote title/body directly");
      return;
    } catch {
      // Didn't write directly, use captured output
      const output = readFileSync(tmpOut, "utf8");
      await parseAndWriteMessage(output, titleFile, bodyFile);
    }
  } finally {
    try {
      unlinkSync(tmpOut);
    } catch {}
  }
}

async function generateWithJunie(titleFile: string, bodyFile: string, diff: string): Promise<void> {
  const prompt = `Generate a conventional commit message for the following diff and write the subject line to '${titleFile}' and the body to '${bodyFile}'. Do not run any tests or gradle commands.

Diff:
${diff}`;

  try {
    run(`junie --skip-update-check --cache-dir=.junie/cache "${prompt.replace(/"/g, '\\"')}"`);
    // Check if junie wrote directly
    readFileSync(titleFile, "utf8");
    return;
  } catch {
    // Try to capture output
    try {
      const output = run(
        `junie --skip-update-check --cache-dir=.junie/cache --output-format=json "${prompt.replace(/"/g, '\\"')}" 2>&1 | jq -r ".result"`,
      );
      await parseAndWriteMessage(output, titleFile, bodyFile);
    } catch {
      throw new Error("junie failed to generate message");
    }
  }
}

async function generateWithCopilot(titleFile: string, bodyFile: string, diff: string, files: string): Promise<void> {
  const allowedTypes = "ci feat fix perf refactor style test build ops docs chore merge revert";
  const allowedTypesAlt = allowedTypes.replace(/ /g, "|");

  const prompt = `You are writing a git commit message for a human developer.

You MUST follow this exact template:

<type>(<scope>): <summary>

<body>

Rules:
- Output plain text only. No markdown fences.
- First line MUST be a valid Conventional Commit subject.
- Keep the FIRST line <= 72 characters.
- Use a specific scope when possible.
- Body:
  - Provide 0-6 bullet points.
  - Explain WHAT changed and WHY.
  - Wrap body lines to <= 72 characters.
  - Do not repeat the subject.

IMPORTANT: Use ONLY these commit types: ${allowedTypes}
If unsure, choose 'chore'.

Changed files:
${files}

Diff:
${diff}`;

  const tmpOut = join(tmpdir(), `copilot-out-${Date.now()}.txt`);
  const tmpErr = join(tmpdir(), `copilot-err-${Date.now()}.txt`);

  try {
    const model = process.env.COPILOT_PRMSG_MODEL || "gpt-5.1-codex-mini";
    run(`copilot --model "${model}" -s -p "${prompt.replace(/"/g, '\\"')}" > "${tmpOut}" 2> "${tmpErr}" || true`);

    let output = readFileSync(tmpOut, "utf8");
    const err = readFileSync(tmpErr, "utf8");

    if (!output && err.includes("enable this model")) {
      const fallback = process.env.COPILOT_PRMSG_FALLBACK_MODEL || "gpt-5.1-codex";
      run(`copilot --model "${fallback}" -s -p "${prompt.replace(/"/g, '\\"')}" > "${tmpOut}" 2> "${tmpErr}" || true`);
      output = readFileSync(tmpOut, "utf8");
    }

    await parseAndWriteMessage(output, titleFile, bodyFile);
  } finally {
    try {
      unlinkSync(tmpOut);
    } catch {}
    try {
      unlinkSync(tmpErr);
    } catch {}
  }
}

async function parseAndWriteMessage(aiOutput: string, titleFile: string, bodyFile: string): Promise<void> {
  const allowedTypes = "ci feat fix perf refactor style test build ops docs chore merge revert";
  const allowedTypesAlt = allowedTypes.replace(/ /g, "|");

  const lines = aiOutput.replace(/\r/g, "").split("\n");

  // Find subject line matching conventional commit pattern
  let subject = "";
  const pattern = new RegExp(`^(\\[)?(${allowedTypesAlt})(\\[([^\\]]*)\\])?: .+`);

  for (const line of lines) {
    const trimmed = line.trim();
    if (!trimmed) continue;
    const match = trimmed.match(pattern);
    if (match) {
      subject = trimmed.replace(/^\[/, "").replace(/\]$/, ""); // Remove brackets if present
      break;
    }
  }

  // Clean up common prefixes
  subject = subject.replace(/^(I'll|I will|Sure,?|Here's|Proposed|Suggested)[: -]+/i, "");

  if (!subject) {
    // Check if there's an existing valid PR message to preserve
    try {
      const existing = readFileSync(titleFile, "utf8").trim();
      if (existing && !existing.match(/^error\([^)]+\):/)) {
        console.log("Preserving existing PR message");
        process.exit(0);
      }
    } catch {}

    console.error("ERROR: Failed to generate valid conventional commit subject");
    if (aiOutput) {
      console.error("--- AI Output ---");
      console.error(aiOutput);
    }
    process.exit(1);
  }

  // Extract body (lines after first blank line after subject)
  let subjectFound = false;
  let blankFound = false;
  const bodyLines: string[] = [];

  for (const line of lines) {
    if (!subjectFound) {
      if (line.trim() === subject) subjectFound = true;
      continue;
    }
    if (!blankFound) {
      if (line.trim() === "") blankFound = true;
      continue;
    }
    bodyLines.push(line);
  }

  // Clean up body
  const body = bodyLines
    .filter((l) => !l.match(/^(I'll|I will|Sure|Here's|Proposed|Suggested|I inspected|Let's)\b/i))
    .slice(0, 12)
    .join("\n")
    .trim();

  writeFileSync(titleFile, subject.substring(0, 72) + "\n", "utf8");
  writeFileSync(bodyFile, body, "utf8");
}

async function waitForBuild(): Promise<void> {
  const head = getHead();
  console.log(`Waiting for workflow 'build' to start on commit ${head}...`);

  let runId = "";
  for (let i = 1; i <= 12; i++) {
    try {
      runId = run(`gh run list --workflow build --commit ${head} --json databaseId --jq '.[0].databaseId // empty'`);
      if (runId) break;
    } catch {}
    console.log(`Run not found yet, retrying in 5s... (${i}/12)`);
    await sleep(5000);
  }

  if (!runId) {
    console.error("Error: Workflow 'build' did not start within 60 seconds.");
    process.exit(1);
  }

  run(`gh run watch "${runId}" --exit-status`);
}

function sleep(ms: number): Promise<void> {
  return new Promise((r) => setTimeout(r, ms));
}

async function main(): Promise<void> {
  const args = process.argv.slice(2);
  const command = args[0];

  if (command === "pr-message") {
    // CLI mode for pr-message
    let titleFile = "";
    let bodyFile = "";

    for (let i = 1; i < args.length; i += 2) {
      if (args[i] === "--title-file") titleFile = args[i + 1];
      if (args[i] === "--body-file") bodyFile = args[i + 1];
    }

    if (!titleFile || !bodyFile) {
      console.error("Usage: merge.ts pr-message --title-file PATH --body-file PATH");
      process.exit(2);
    }

    await generateMessage(titleFile, bodyFile);
    return;
  }

  // Full merge workflow
  console.log("Fetching and merging origin/HEAD...");
  run("git fetch --all --prune --prune-tags --tags --force");
  run("git merge origin/HEAD");

  console.log("Pushing...");
  run("git push");

  const hasExistingPR = hasPR();

  if (hasExistingPR) {
    await waitForBuild();
    await createOrUpdatePR();
  } else {
    await createOrUpdatePR();
    await waitForBuild();
  }

  // Merge squash
  const hasUncommitted = runSilent("git status --porcelain=1") !== "";
  if (hasUncommitted) {
    console.warn("WARNING: Uncommitted changes detected. Review before merge.");
  }

  process.stdout.write("Proceed with squash merge? [Y/n] ");
  const reply = await new Promise<string>((resolve) => {
    process.stdin.once("data", (data) => resolve(data.toString().trim().toLowerCase()));
  });

  if (reply === "n" || reply === "no") {
    console.log("Merge cancelled.");
    process.exit(1);
  }

  run("gh pr merge --squash --delete-branch");
}

async function createOrUpdatePR(): Promise<void> {
  const tmpDir = mkdtempSync(join(tmpdir(), "pr-"));
  const titleFile = join(tmpDir, "title.txt");
  const bodyFile = join(tmpDir, "body.txt");

  const headBefore = getHead();

  try {
    if (hasPR()) {
      console.log("Updating PR message...");
    }

    await generateMessage(titleFile, bodyFile);
    const headAfter = getHead();

    // Regenerate if HEAD changed during generation
    if (headBefore !== headAfter) {
      await generateMessage(titleFile, bodyFile);
    }

    const title = readFileSync(titleFile, "utf8").trim();

    if (hasPR()) {
      run(`gh pr edit --title "${title.replace(/"/g, '\\"')}" --body-file "${bodyFile}"`);
      run("GH_PAGER=cat gh pr view");
    } else {
      run(`gh pr create --title "${title.replace(/"/g, '\\"')}" --body-file "${bodyFile}"`);
      console.log("PR created with generated message.");
      run("GH_PAGER=cat gh pr view");
    }
  } finally {
    try {
      unlinkSync(titleFile);
      unlinkSync(bodyFile);
      // Node doesn't have rm -rf equivalent easily, but tmp files are fine
    } catch {}
  }
}

main().catch((e) => {
  console.error(e);
  process.exit(1);
});

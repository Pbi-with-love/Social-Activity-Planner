---
name: pre-implementation
description: "MANDATORY gatekeeper workflow: Must be consulted and followed before writing code, modifying files, or implementing features/fixes in this project."
risk: unknown
source: workspace
date_added: "2026-10-17"
---

# Pre-Implementation Workflow & Skill Orchestration

This skill serves as the **mandatory gatekeeper** for all development, bug fixing, and refactoring in this repository. 
**No code edits or implementations may begin without following the stages below.**

---

## Stage-by-Stage Workflow

```
[ New Feature / Ambiguous Goal ]
       │
       ▼
 1. Brainstorming (if design/architecture unclear)
       │
       ▼
 2. Concise Planning (Mandatory atomic checklist)
       │
       ▼
[ If Bug / Incident ] ──► 3. Systematic Debugging (Root cause before code changes)
       │
       ▼
 4. Implementation Phase (Incremental, adhering to project standards)
       │
       ▼
 5. Lint & Validate (Mandatory tests and lint checks)
       │
       ▼
 6. Kaizen Review (Continuous improvement & refactoring check)
       │
       ▼
 7. Git Pushing (Safe conventional commit & push when approved)
```

---

## 1. Ideation & Requirements Validation
- **Skill to activate:** [Brainstorming](../brainstorming/SKILL.md)
- **When:** Whenever a feature is conceptual, architecture is unvalidated, or multiple design options exist.
- **Rule:** Do not write code while brainstorming. Clarify requirements one question at a time.

---

## 2. Planning & Scope Definition
- **Skill to activate:** [Concise Planning](../concise-planning/SKILL.md)
- **When:** **MANDATORY for every task** before changing or adding any file.
- **Action:**
  1. Produce a minimal, atomic checklist of specific steps.
  2. Confirm impacted files and interfaces.
  3. Get user alignment on the plan before execution.

---

## 3. Bug Diagnostics (If addressing a bug or test failure)
- **Skill to activate:** [Systematic Debugging](../systematic-debugging/SKILL.md)
- **When:** Any unexpected error, failing test, or defect.
- **Rule:** Never guess or apply speculative fixes. Investigate root causes, replicate the issue with a test or log trace, and verify before modifying production code.

---

## 4. Quality Assurance & Verification
- **Skill to activate:** [Lint & Validate](../lint-and-validate/SKILL.md)
- **When:** **MANDATORY immediately after editing code.**
- **Action:**
  1. Run unit/integration tests (`mvn test`, etc.).
  2. Run linters, build checks, and verify zero regressions.
  3. Never conclude a task while tests are red or unverified.

---

## 5. Kaizen Review & Standardization
- **Skill to activate:** [Kaizen](../kaizen/SKILL.md)
- **When:** Before finalizing any feature or refactoring.
- **Action:** Review code for error-proofing, clean abstractions, eliminating duplication, and preserving documentation integrity.

---

## 6. Saving & Version Control
- **Skill to activate:** [Git Pushing](../git-pushing/SKILL.md)
- **When:** When work is complete, tests pass, and the user asks to save/push changes.
- **Action:** Stage changes cleanly, craft conventional commit messages, and push safely to remote.


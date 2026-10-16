---
name: implementation-workflow
description: 'Provides the mandatory workflow for implementation tasks; use this skill whenever the agent is asked to modify, add, or remove repository code or configuration.'
---

# Implementation workflow

Use this skill for every implementation task, including code changes, configuration changes, migrations, tests, and documentation changes that affect repository behavior.

1. Inspect the relevant repository structure, existing implementation, documentation, and configuration before editing.
2. Identify the smallest complete change that satisfies the request while preserving existing behavior and repository conventions.
3. Reuse existing helpers, patterns, dependencies, and test infrastructure instead of introducing parallel approaches.
4. Make precise edits only in files relevant to the request. Do not overwrite unrelated user changes.
5. Update directly related tests or documentation when the behavior or workflow requires it.
6. Run the narrowest existing validation that covers the change, escalating only when needed.
7. Review the final diff for completeness, accidental changes, and consistency with the request before reporting completion.

Do not treat a plausible edit as complete without checking that the requested behavior is wired through all relevant surfaces and that the applicable validation succeeds or its blocker is clearly reported.

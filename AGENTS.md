# Agent Instructions & Workflow Rules

## Mandatory Pre-Implementation Gatekeeper

Before writing, modifying, or deleting any code or configuration in this repository, you **MUST** follow the pre-implementation skills workflow defined in [`.agents/skills/pre-implementation/SKILL.md`](.agents/skills/pre-implementation/SKILL.md).

### Required Execution Stages:

1. **Before any implementation:**
   - **Brainstorming:** If a feature or architecture is not yet concrete or has open design choices, read and follow [`.agents/skills/brainstorming/SKILL.md`](.agents/skills/brainstorming/SKILL.md).
   - **Concise Planning:** Always generate a clear, atomic step-by-step plan using [`.agents/skills/concise-planning/SKILL.md`](.agents/skills/concise-planning/SKILL.md) and get user alignment before modifying files.
   - **Architecture & Backend Standards:** Strictly follow the engineering standards in [`.agents/skills/spring-boot-backend-guidelines/SKILL.md`](.agents/skills/spring-boot-backend-guidelines/SKILL.md) for all Java 21 / Spring Boot code.
   - **Systematic Debugging:** If addressing a bug or test failure, strictly diagnose the root cause using [`.agents/skills/systematic-debugging/SKILL.md`](.agents/skills/systematic-debugging/SKILL.md) before attempting any code fix. Speculative fixes are forbidden.

2. **After code changes:**
   - **Lint & Validate:** Always validate changes with test and build checks per [`.agents/skills/lint-and-validate/SKILL.md`](.agents/skills/lint-and-validate/SKILL.md).
   - **Kaizen:** Apply continuous improvement and clean code principles per [`.agents/skills/kaizen/SKILL.md`](.agents/skills/kaizen/SKILL.md).

3. **Saving work:**
   - **Git Pushing:** When requested to commit/push, follow [`.agents/skills/git-pushing/SKILL.md`](.agents/skills/git-pushing/SKILL.md).


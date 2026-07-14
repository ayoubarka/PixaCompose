---
name: uber-component-migrator
description: Migrate or create a PixaCompose component from an Uber Base component spec URL or Markdown file. Carefully reads the external spec first, preserves PixaCompose architecture, removes Material 3 UI component usage, reuses existing Pixa primitives/helpers where appropriate, and performs a built-in post-migration cleanup and validation pass.
---

# uber-component-migrator

`CLAUDE.md` is the primary project spec. Use code as the final truth if any markdown disagrees with implementation.

## What this skill does

Given an Uber Base component spec URL or uploaded `.md`, this skill must:

1. Determine whether PixaCompose already has an equivalent component.
2. If it exists, refactor it to align with the Uber spec.
3. If it does not exist, create it in the correct PixaCompose category.
4. Follow the Uber Base spec carefully in anatomy, behavior, usage rules, breakpoints, constraints, and customization boundaries.
5. Keep the implementation native to PixaCompose architecture and naming.
6. Tokenize the implementation as fully as practical.
7. Run a built-in cleanup pass that removes Material 3 UI component usage and reduces avoidable local literals.

## Required context

Always use:
- `CLAUDE.md`
- the Uber Base component spec URL or uploaded `.md`

Optional only when needed:
- related Uber Base foundation docs such as typography, dimensions, layout grids, corner radius, elevation, motion, transitions, or timing

Do not require a local copy of Uber Base docs if the URL is readable in-session.

## Read-first rule

Do not begin implementation from the component name alone.

Before editing code, carefully read the actual Uber Base component spec and extract what it explicitly says about:
- purpose
- anatomy
- content model
- variants
- states
- sizing
- icon/text/accessory rules
- behavior
- responsive behavior
- usage constraints
- anti-patterns
- accessibility guidance
- customization boundaries

Do not infer missing rules just from visual similarity to another component.
Do not blindly copy a previous migration pattern if the current Uber spec differs.
If the Uber page is incomplete or ambiguous, say what is confirmed versus assumed.

## Hard rules

- No Material 3 UI components or `MaterialTheme`.
- Do not leave `androidx.compose.material3.*` UI component usage in the migrated component unless the user explicitly approves it.
- Use Compose primitives and existing Pixa patterns.
- Reuse existing PixaCompose helpers, utilities, and component primitives/composables when that is the correct architectural fit.
- Do not import a heavier Pixa component just to avoid a few lines of simple local layout.
- Do not create awkward or circular dependencies across component families.
- Colors must come from `AppTheme.colors.*` unless `Color.Transparent` is the intentional sentinel.
- Typography must come from `AppTheme.typography.*`.
- Shapes must come from `AppTheme.shapes.*` whenever possible.
- Sizes should use `SizeVariant`, `HierarchicalSize`, and shared resolvers rather than local raw ladders.
- Motion must use `AnimationUtils`.
- Public API names stay in Pixa style, not Uber naming.
- Follow the single-file component family structure from `CLAUDE.md`.
- Prefer extending/refactoring an existing component over creating duplicate parallel families.
- Respect the upgraded foundation layers already applied to PixaCompose.

## Tokenization rule

Avoid hardcoded values as much as realistically possible.

Tokenize or centralize these whenever possible:
- colors
- text styles
- shape/radius values
- icon sizes
- control heights
- internal paddings
- spacing/gaps
- stroke/border widths
- elevations
- animation durations/easing/presets

A hardcoded value is acceptable only if all are true:
1. it is genuinely one-off,
2. it does not fit an existing token category,
3. creating a token would add noise rather than reuse,
4. the value is localized and briefly justified inline.

If a value repeats or expresses a reusable UI rule, turn it into a token or shared resolver.

## Decision process

### 1. Classify the Uber component
Determine:
- category: actions, inputs, navigation, feedback, display, overlay
- primary purpose
- whether it is:
  - a standalone primitive,
  - a variant of an existing component,
  - a reusable composition wrapper,
  - a documented pattern built from existing primitives

### 2. Map it to PixaCompose
Choose one:
- refactor an existing component,
- extend an existing component,
- add a new component,
- add a convenience composable built on existing primitives

### 3. Extract the Uber spec
Extract and summarize:
- purpose
- anatomy
- variants
- states
- sizes
- behavior
- usage rules
- best practices
- anti-patterns
- breakpoints/responsive behavior
- customization hooks
- accessibility requirements if present

### 4. Map the spec into Pixa rules
Translate Uber concepts into:
- Pixa variant vocabulary
- Pixa token systems
- Pixa sizing and adaptive model
- Pixa motion system
- Pixa theme access patterns
- Pixa file/category conventions

## Anatomy-first rule

Do not only copy the visual impression.

The implementation must preserve the important structural parts the Uber spec defines:
- hierarchy of container/content/accessories
- content slots
- control relationships
- interaction model
- expected behavior across states

If the anatomy is wrong, the migration is wrong even if the visuals are similar.

## Usage-rule preservation

If the Uber spec includes:
- when to use it,
- when not to use it,
- content limits,
- max number of actions/items,
- placement rules,
- hierarchy rules,
- customization limits,
- breakpoint behavior,

preserve those rules in the implementation and short source-level docs where relevant.

## Breakpoints and adaptive behavior

If the Uber spec includes responsive guidance:
- map it onto the existing Pixa adaptive model
- use `WindowSizeClass` and `AppTheme.adaptiveSizeVariant` when appropriate
- do not invent a second responsive system
- explicit caller-provided sizes remain authoritative
- adaptive behavior should be a default/fallback or explicit opt-in, not a hidden override

If `CLAUDE.md` says the adaptive plumbing exists but adoption is sparse, prefer a small explicit component-level integration rather than a large cross-library rewrite.

## Refactor rules

### If the component already exists
- audit current API, variants, states, layout, sizing, behavior, and token usage
- identify gaps against the Uber spec
- refactor incrementally
- preserve backward compatibility where reasonable
- break API only if the current shape is structurally wrong or blocks correct implementation
- replace local hardcoded values with shared token-backed resolvers where in scope

### If the component does not exist
- create it in the correct category folder
- follow the exact single-file structure:
  1. ENUMS & TYPES
  2. DATA CLASSES
  3. THEME PROVIDER / RESOLVERS
  4. INTERNAL IMPLEMENTATION
  5. PUBLIC API
  6. CONVENIENCE VARIANTS / HELPERS

### If the Uber spec is really a composite pattern
- implement it as a reusable Pixa composition component or wrapper when that is cleaner than inventing a new primitive

## Material 3 replacement rule

The migration is not complete until Material 3 UI component usage has been audited in the touched component.

For each touched component file:
- remove Material 3 UI component imports/usages
- replace them with Foundation/UI primitives, existing Pixa helpers, or existing Pixa component primitives where appropriate
- remove Material 3 ambient dependencies if they only existed to support the removed UI component usage
- keep behavior and public API unchanged unless a change is required by `CLAUDE.md` or the Uber spec

Examples of acceptable replacements:
- `material3.Text` → `BasicText` or a Pixa text primitive if one exists
- Material button/container widgets → Box/Row/Column-based local composition
- Material indicators/icons wrappers → existing Pixa feedback/display primitives where appropriate

## Reuse rule

Prefer reuse when an existing PixaCompose building block already expresses the same primitive cleanly.

Reuse candidates include:
- theme tokens and resolvers
- `PixaIcon`
- `PixaCircularIndicator`
- `Skeleton`
- badge/badged-box style composition helpers
- shared animation helpers
- shared elevation helpers
- shared shape helpers
- shared sizing helpers

Do not reuse a component when:
- it brings unrelated behavior,
- it creates dependency direction problems,
- it makes a simple component harder to reason about than a tiny local layout would.

If reuse is rejected where it seems plausible, briefly state why.

## Short source documentation

Each migrated component file should include concise source-level docs near the main public API.

Use short sections like:
- Purpose
- Anatomy
- Variants
- States
- Sizing
- Adaptive behavior
- Customization
- Usage notes

Keep this brief and implementation-relevant.

## Customization rules

Expose customization only where it supports both the Uber spec and Pixa consistency:
- variants
- size
- shape options when appropriate
- content slots
- icons
- state/selection
- badges/accessories when relevant
- custom colors only when the pattern truly benefits from them

Do not over-expose low-level styling knobs that weaken consistency.

## Supporting updates

By default:
- update `DOCUMENTATION.md` to match the final public API if the public API changed
- add or revise the component’s usage examples if needed
- update demo/showcase usage only if the component’s public API or behavior changed in a way that affects existing examples

If the user explicitly says not to update docs/showcase:
- do not update `DOCUMENTATION.md`
- do not update usage examples
- do not update demo/showcase files
- unless one of those files must be changed to keep the project compiling

## Output expectations

For every migration, produce:

1. A brief audit:
- existing Pixa equivalent or not
- key gaps against Uber spec
- migration strategy chosen

2. Implementation:
- refactored existing file or new file in the correct category
- anatomy aligned with the Uber spec
- behavior aligned with the Uber spec
- tokenized colors, typography, spacing, shape, elevation, sizing, and motion
- short source-level docs

3. Built-in cleanup pass:
- removed or replaced Material 3 UI component usage
- reused existing Pixa helpers/components where appropriate
- reduced duplicated local resolver logic where practical
- justified remaining unavoidable raw values inline

4. Supporting updates:
- docs/examples/showcase updated only if allowed by the user instruction

5. Final summary:
- files changed
- API changes
- Material 3 usages removed/replaced
- reusable Pixa primitives/helpers adopted
- unavoidable hardcoded values and why
- unresolved questions if any

6. Include a short spec fidelity audit stating:
- which old behaviors were kept
- which old behaviors were removed
- which spec constraints forced the API or anatomy change
- which places intentionally remain approximations because of current Pixa architecture or token limits

## Success criteria

A migration is successful when:
- the component closely follows Uber Base specs, anatomy, behavior, and usage intent,
- the implementation still feels native to PixaCompose,
- shared tokens/utilities/primitives are used as fully as practical,
- Material 3 UI component usage is removed from the touched component unless explicitly approved,
- hardcoded values are minimized and justified,
- and the component is easier to maintain than before.

## Post-migration validation

Before finishing, run a validation pass that checks:
- the Uber Base component spec was actually read and summarized before implementation
- the final component anatomy still matches the external spec, not just the visual appearance
- no `androidx.compose.material3.*` UI components remain in the touched component unless explicitly approved
- no avoidable hardcoded dp/sp/color/shape/motion values remain
- inline proportional values (for example `* 0.6f`, `* 0.15f`) are either justified as local spec-derived math or promoted into shared component-specific resolvers/tokens if likely to repeat
- legacy API behaviors not supported by the external spec are removed or intentionally documented
- responsive behavior, if specified by Uber, was either implemented or explicitly documented as out of scope
- docs/showcase were either updated or intentionally skipped based on the user instruction
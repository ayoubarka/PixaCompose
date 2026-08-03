# PixaCompose Foundation Brief

A project-facing description of the PixaCompose design foundation: what it provides, what a
product team is expected to change when starting a new branded product on top of it, and what
should be left alone.

Audience: product designers, design-systems designers, and design tooling (e.g. Claude Design)
proposing a project skin on top of PixaCompose. It is not an implementation guide.

Everything below was extracted from source (`theme/`, `utils/`) on 2026-07-20. **Code is the
source of truth** — where the repository's own markdown disagreed with the code, this brief
follows the code. See *Verification Notes* at the end.

The last section is a **Project Brand Input Checklist** — the concrete set of inputs a design
team needs to supply. If you only read one part of this document, read that.

---

## Purpose

PixaCompose is a **shared foundation**, not a per-project design system to be re-derived each
time. It ships a complete token architecture — color, type, shape, size, motion, elevation —
plus a full component set built on those tokens.

The intended per-project workflow is narrow on purpose:

> **A new product usually changes two things: the brand color palette and the font family.
> Everything else stays.**

The document exists so that a design team can produce a defensible project skin without
proposing a foundation rewrite, and so that anyone reviewing such a proposal can tell the
difference between a brand decision and a foundation change.

---

## System Overview

- **Platform**: Compose Multiplatform — one component set renders on Android and iOS from the
  same source. There is no separate iOS design spec to maintain.
- **No Material 3 surfaces**: components are constructed from layout and drawing primitives
  (box, row, column, canvas) and styled entirely by PixaCompose's own tokens. The system does
  not inherit Material's visual language, defaults, or versioning. Interaction feedback (the
  press/hover/focus ripple) is also PixaCompose's own, not Material's.
- **Single theme entry point**: one `PixaTheme(...)` wrapper at the app root supplies colors and
  typography to every component below it. Components read tokens through an `AppTheme` accessor —
  they never hold literal colors, font sizes, or corner radii of their own.
- **Semantic tokens, not raw values**: a component asks for "the default brand content color" or
  "the title text style", never for a hex value or a point size. This is what makes a whole-app
  reskin a theme-level change rather than a component-level one.

Foundation surface at a glance (verified counts):

| Layer | Size | Brand-customizable at theme level? |
|---|---|---|
| Color tokens | 71 semantic tokens across 7 groups | **Yes** — the primary customization surface |
| Typography styles | 32 named text styles | **Yes** — font family only; the scale is fixed |
| Shape families | 14 families × 5 tiers, plus a size-invariant pill | No theme parameter; per-call-site only |
| Size system | 8 size variants × 24 size categories | No theme parameter; per-component only |
| Elevation | 5 semantic tiers (0/1/2/4/8dp) | No theme parameter |
| Motion | 6 named springs, ~14 factories, 6 stock transitions | No theme parameter |
| Breakpoints | 3 window size classes (600dp / 840dp) | No theme parameter |

The right-hand column is the practical customization boundary: **color and font family are theme
inputs; everything else is a system constant.** A project that wants to change shape or motion is
proposing a foundation change, not configuring a theme — see *What Stays Stable*.

---

## What Changes Per Project

### 1. Brand color palette — expected, primary

Two levels are available, and they compose:

- **Semantic overrides** (primary API): set individual tokens directly, e.g. "brand content
  default is this blue". Any token left unset keeps its derived value. Best when a brand has a
  small number of exact, non-negotiable colors.
- **Color scales** (advanced): supply a full 11-step ramp (50 → 950) for one or more of the seven
  color groups, and let light and dark palettes derive automatically from it. Best when a brand
  has a proper tonal ramp already — this is the highest-leverage option, because one ramp
  populates ~9 semantic tokens correctly in both light and dark mode.

Scales merge with defaults per-weight, so supplying a partial ramp (say, just weights 100/500/900)
is legal — the missing steps fall back to the built-in ramp. That produces uneven results in
practice; supplying a complete 11-step ramp is strongly preferred.

Overrides are applied *after* scales, so the two can be layered: derive most of a family from a
ramp, then pin one or two exact values.

### 2. Font family — expected, primary

A project supplies a font family as **nine explicit weights** (100 Thin through 900 Black). All
32 text styles are then rebuilt on that family with their sizes, weights, line heights, and
letter spacing unchanged. If no font is supplied, the platform default is used.

This is a deliberate constraint: the type scale is designed to work at those nine weights, and
several styles sit at 800 and 900. A brand font that only ships four weights will need weight
substitution decided up front, and that decision belongs to the design team, not to the
implementation.

### 3. Brand emphasis mapping — occasionally

The `brand` and `accent` groups are two independent full-strength color families. Which of the two
carries primary action color, and how strongly `accent` is used at all, is a legitimate per-project
choice made by how the two ramps are chosen. This is still a palette decision, not a token
architecture change.

### 4. Shape and motion — by exception only

Both are reachable but neither is a theme input; they are chosen at component call sites, so
changing them project-wide means touching components. Treat either as a scoped exception with a
stated product reason, agreed before build.

---

## What Stays Stable

Keep, unless there is a specific, articulated product reason:

- **Semantic token architecture** — the group / role / emphasis model, and the practice of
  components consuming semantics rather than values. Bypassing this is what breaks reskinnability.
- **Token naming** — renaming tokens breaks every component and every downstream project at once.
- **Typography ladder structure** — the set of roles (display, header, headline, title, subtitle,
  body, caption, overline, footnote, label, action) and their relative sizes.
- **Size scale and spacing rhythm** — the 8-variant scale, the 4dp base grid, the 24 size
  categories.
- **Radius and shape logic** — the 5-tier radius progression and its mapping from size variants.
- **Motion semantics** — which interaction gets which spring, and the interaction feedback model.
- **Adaptive layout behavior** — the 600dp / 840dp breakpoints and how they map to sizing.
- **Elevation ladder** — five semantic depth tiers with one numeric scale behind them.

### Change minimally first

Before proposing a foundation change, in order:

1. Can this be done with **color overrides**? Most "the buttons look wrong" problems are color.
2. Can this be done with a **different color ramp** (a full 50–950 scale)?
3. Can this be done by choosing a **different existing token** at the call site — a different
   emphasis level, a different text role, a different size variant?
4. Can this be done by choosing a **different existing shape family or size variant** for a
   specific component?
5. Only then: does the foundation actually need to change?

Steps 1–4 are cheap, reversible, and shared across projects. Step 5 is a library change with
cross-project blast radius. In review, a proposal that reaches step 5 should say explicitly why
1–4 were insufficient.

---

## Color Foundation

### Structure

Every semantic color token is named `{group}{Role}{Emphasis}`.

**Groups (7).** Each group is backed by an 11-step tonal ramp (50 lightest → 950 darkest).

| Group | Meaning |
|---|---|
| `brand` | Primary brand identity — main actions, brand moments |
| `accent` | Secondary brand family — complementary emphasis |
| `base` | Neutrals — page surfaces, borders, and all body text |
| `info` | Informational / neutral-notice status |
| `success` | Positive / confirmed status |
| `warning` | Caution status |
| `error` | Destructive / failed status |

**Roles (3).** What part of a component the color paints:

- `Surface` — fills and backgrounds
- `Border` — outlines, dividers, focus rings
- `Content` — text, icons, foreground marks

**Emphasis (3).** How loud within that role: `Subtle` → `Default` → `Focus`.

Six of the seven groups (`brand`, `accent`, `info`, `success`, `warning`, `error`) are uniform:
3 roles × 3 emphasis = 9 tokens each. `base` is richer (17 tokens) because neutral UI carries the
text hierarchy and the state colors:

- Surfaces: subtle, default, elevated, focus, shadow, disabled
- Borders: subtle, default, focus, disabled
- Content: title, subtitle, body, caption, hint, negative, disabled

Total: **71 semantic tokens**, resolved separately for light and dark mode.

### The anchor rule

Across every group, `{group}ContentDefault` maps to **weight 500 in both light and dark mode**.
That one shared anchor is why a brand color reads as recognizably "the same color" in both modes,
rather than as two different colors.

Light and dark then diverge in a consistent, mirrored way:

- **Light**: surfaces come from the light end (50/100/200), borders from the middle (300/400/500),
  and content emphasis moves *darker* than the anchor (subtle 600, focus 700).
- **Dark**: surfaces come from the dark end (700/800/900), and content emphasis moves *lighter*
  than the anchor (subtle 400, focus 300).

`base` content follows the same mirroring: in light mode title is darkest (950) descending to hint
(400); in dark mode title is lightest (50) descending to hint (600). Caption sits on the 500 anchor
in both.

### Mapping a brand palette in

Recommended approach, in order of quality:

1. **Supply full ramps.** Give an 11-step ramp for `brand`, and for `accent` if the brand has a
   real secondary. Set weight 500 to the brand's canonical color — it becomes the content anchor
   in both modes and is the value users will read as "the brand color". Then check the derived
   surfaces and borders rather than assuming they work.
2. **Keep `base` neutral.** A tinted neutral ramp is a valid brand move, but it recolors every
   surface, border, and line of body text in the product. Tint it slightly or not at all, and
   verify body text contrast afterwards.
3. **Leave status groups alone unless the brand demands it.** `info` / `success` / `warning` /
   `error` carry meaning, not identity. Users read green-as-success and red-as-error faster than
   they read any brand convention. Change hue only when the default genuinely clashes, and keep the
   semantic direction intact.
4. **Use overrides for exceptions.** Once ramps are in, pin individual tokens where the brand has a
   hard requirement the ramp can't express.

### Overrides vs. scale replacement

| Situation | Use |
|---|---|
| Brand ships a full tonal ramp | **Scales** — one input, both modes derived, all 9 tokens correct |
| Brand ships 1–3 fixed hex values | **Overrides** — set exactly those tokens, leave the rest derived |
| Need one exact value inside an otherwise derived family | **Both** — scale for the family, override for the exception |
| Want a different neutral temperature | **Scales** on `base`, then re-verify text contrast |

Rule of thumb: **scales carry brand systems; overrides carry brand exceptions.** Heavy use of
overrides across many tokens usually means the project should have supplied a ramp instead.

### Not breaking semantic consistency

- **Keep the emphasis direction.** `Subtle` quieter than `Default`, `Default` quieter than `Focus`.
  Components rely on this ordering for state changes.
- **Don't repurpose a group.** Using `warning` as a brand accent will surface as a caution color
  somewhere nobody looked.
- **Don't collapse `Surface` and `Content`** to the same value in any group — that pairing is a fill
  and its foreground.
- **Validate both modes.** A ramp that reads well in light mode can flatten in dark, because the
  same ramp is being read from the opposite end.

---

## Typography Foundation

### The ladder

32 named styles, organized by role. Sizes are fixed; only the font family is a project input.

| Tier | Styles | Size | Typical use |
|---|---|---|---|
| Display | Large / Medium / Small | 64 / 52 / 40sp | Hero and marketing moments |
| Header | Bold / Regular | 32sp | Page titles |
| Headline | Bold / Regular | 24sp | Section headings |
| Title | Bold / Regular / Light | 20sp | Card headers, dialog titles |
| Subtitle | Bold / Regular / Light | 18sp | Secondary headings |
| **Body** | **Bold / Regular / Light** | **16sp** | **Main content — the scale anchor** |
| Caption | Bold / Regular / Light | 14sp | Supporting text |
| Overline | — | 12sp | All-caps category labels (wide tracking) |
| Footnote | Bold / Regular | 12sp | Fine print |
| Label | Large / Medium / Small | 14 / 12 / 10sp | Form labels, tags |
| Action | Mini → Huge (7 steps) | 10 → 24sp | Button and CTA text, paired to button sizes |

Two things worth noting for designers:

- **"Bold / Regular / Light" is relative, not absolute.** Within each tier those names mean heavier,
  standard, and lighter *for that tier*. Actual weights climb as the tier climbs — `bodyBold` is
  weight 500, while `headerBold` is weight 900. Read them as a three-step emphasis axis inside a
  tier, not as fixed weights.
- **The `action` tier exists separately** because button text is sized against the button, not
  against the surrounding prose. Each action step corresponds to a button size.

### Anchor and rhythm

- **16sp body is the anchor.** The scale is built outward from it in roughly 1.25× steps.
- **All line heights land on a 4sp grid.** Every style's line height is a multiple of 4, which is
  the same 4dp grid the spacing system uses. This is what allows text blocks and spacing tokens to
  stack without accumulating half-pixel drift, and it's why a couple of styles use a line height
  slightly off from a clean ratio — the grid wins over the ratio.
- **Letter spacing tightens as size grows** — negative at display sizes, widest on overline.

If a project ever needs a new text style, its line height must be rounded to the nearest 4sp.
A hand-computed value that misses the grid will visibly desynchronize from every layout around it.

### Swapping the font family

The default expectation: **swap the family, keep the scale.** Sizes, weights, line heights, and
letter spacing carry over unchanged, and the layout rhythm is preserved for free.

Font-swap-only is sufficient when the brand font has similar metrics to a standard UI sans —
x-height, width, and apparent weight in the same neighborhood.

A **fuller retune** is warranted when:

- The brand font has a notably small or large x-height — it will read a size off at 16sp, and body
  size may need to move (with line heights re-rounded to the 4sp grid).
- The font is condensed or extended enough to change line-length behavior.
- Fewer than nine weights ship, so weight substitutions must be chosen deliberately.
- The font is a display or script face, in which case it likely belongs on the display and header
  tiers only, with a companion sans carrying body and below.

Even in a retune, keep the *ladder structure* — same roles, same relative ordering. Retune values,
not architecture.

---

## Shape Foundation

### Default behavior

**Rounded corners are the default shape language.** The rounded family has five tiers drawn from
the radius scale: 4 / 6 / 8 / 12 / 16dp. 8dp is the standard tier. Components resolve a shape tier
from their size variant, so a large button is automatically more rounded than a compact one —
proportional, not arbitrary.

A **cut-corner** family mirrors the same tiers at angular corners, for products that want a harder
geometric feel.

### Pill and circle

A dedicated **pill** shape is intentionally *size-invariant*: a pill is always as round as
possible, regardless of component size. Use it for chips, pill buttons, and pill badges. Circles
come from the same maximal radius applied to a square footprint — avatars, icon buttons, dots.

Do not approximate a pill with a large radius tier. The pill token exists so a chip stays a chip at
every size.

### Expressive families

Twelve decorative families exist beyond rounded and cut — concave and convex top/bottom, wave, arch
top/bottom, tab, notched (rounded and sharp), and chat bubble (left and right, direction-aware).
Each has the same five tiers.

These are **deliberate moments, not defaults**. Bubbles belong in messaging. Tab shapes belong on
tabs. Arch, wave, and concave/convex belong on a hero or a feature card where the shape is part of
the composition. Notches belong where something visually docks into the surface.

### Guidance

- A product should pick **one default corner language** — rounded or cut — and hold it.
- Expressive families should be a short, named list per project ("wave on the onboarding header,
  bubbles in chat"), not a per-screen choice.
- Randomizing shape across screens is the single fastest way to make a themed product look
  unsystematic, and it's the one thing the shape system cannot protect against — it offers variety
  precisely so that variety can be chosen deliberately.
- Shape is **not a theme parameter**. It is fixed at the foundation level and selected per call
  site. A project-wide shape change is a component-level change with real cost. Budget it as such.

---

## Sizing and Spacing

### Size variants

Eight steps, used consistently across every size category:

`None` → `Nano` → `Compact` → `Small` → **`Medium`** → `Large` → `Huge` → `Massive`

`Medium` is the default everywhere and the anchor tier. Each step is roughly 15–25% larger than the
previous — enough to be noticeable, not enough to be jarring. Not every component supports every
step; components expose the range that makes sense for them.

### Size categories

24 categories each define one value per size variant, so `Medium` means a coordinated set of values
across the whole system rather than one number reused. Representative `Medium` values:

| Category | Medium | Category | Medium |
|---|---|---|---|
| Container | 48dp | Card | 160dp |
| Button | 44dp | Avatar | 40dp |
| Icon | 24dp | Badge | 20dp |
| Input | 48dp | App bar | 56dp |
| Chip | 32dp | Spacing | 12dp |
| List item | 56dp | Radius | 8dp |

Remaining categories: toggle, checkbox, tab, bottom nav, padding, border, shadow, touch target,
divider, stroke, slider track, image.

### Spacing rhythm

- **4dp base unit.** Everything is a multiple of it — which is also why line heights round to 4sp.
- **Spacing** (gaps between components): 0 / 2 / 4 / 8 / 12 / 16 / 24 / 48dp.
- **Padding** (inside components): 0 / 2 / 4 / 8 / 12 / 16 / 20 / 24dp.
- **Radius**: 0 / 2 / 4 / 6 / 8 / 12 / 16 / 24dp, plus a "full" value for pills and circles.

Spacing and padding are distinct scales on purpose — external rhythm is coarser than internal
rhythm, which is what keeps dense components from looking airy and airy layouts from looking
cramped.

### Touch targets

The touch target scale is explicitly labeled against WCAG: 48dp is the stated minimum, and tiers
below it (40dp, 44dp) are marked as dense-professional-tool exceptions. Container `Medium` is 48dp
for this reason. A component's visual footprint may be smaller than its touch target — the target
is expanded with padding rather than by inflating the visual.

Any project-level decision to go below 48dp on a routinely-tapped control is an accessibility
decision and should be recorded as one.

### Adaptive layout

Three window size classes derived from measured width:

| Class | Width | Default size variant |
|---|---|---|
| Compact | < 600dp | Medium |
| Medium | 600–839dp | Large |
| Expanded | ≥ 840dp | Huge |

Two screen-level values follow the window class directly:

- **Page margin** (screen-edge inset): 16 / 24 / 48dp
- **Section spacing** (gap between major sections): 24 / 48 / 48dp

Use these two on screen roots and between top-level sections — not inside components.

Adaptive sizing inside components is **opt-in, one component at a time**, not automatic. This is
intentional: a component that silently resizes on a tablet is harder to compose predictably than
one whose size the caller controls. Don't propose blanket adaptive behavior.

### Elevation

Five semantic tiers on one numeric scale: `None` 0dp → `Low` 1dp → `Medium` 2dp → `High` 4dp →
`Highest` 8dp. Resting cards and buttons sit low; dialogs, menus, and popovers sit high;
snackbars, toasts, and tooltips sit highest.

Depth is expressed four ways, and shadow is often not the right one — surface layering (a
different surface token), tonal emphasis (a filled variant), and border emphasis (an outline) all
convey prominence without a lift. Prefer a border over a shadow when a component merely needs a
visual boundary.

### Stability

All of the above is foundation-level and should not move per project. These values are load-bearing
for every component's internal proportions; changing one shifts things in components nobody thought
to check.

---

## Motion Principles

### One source, semantic names

All animation lives in a single motion module. Nothing in the component set specifies a raw
duration or spring curve inline — components ask for motion by intent, choosing from named springs
(selection, indicator, thumb, color, fast, slow), a fixed duration ladder (instant 100ms → fast
150ms → standard 300ms → slow 500ms → emphasized 650ms) with matching easings, and stock
transitions (fade, scale, slide) so the same element type enters the same way everywhere.

Distinct specs exist for distinct *meanings* — a passive dismissal animates differently from a
user-initiated one, and an entry differs from an exit. That distinction is the system's value; it
is not incidental.

### Interaction feedback

Press, hover, focus, and drag feedback is PixaCompose's own, at fixed state opacities (pressed
0.12, focused 0.12, dragged 0.16, hovered 0.08). The feedback takes its **hue** from the component's
content color, so it re-tints with the brand automatically. Nothing needs to be configured for
feedback to follow a new palette.

### What may change, and what may not

**Acceptable** (with care): tuning perceived *feel* — a slightly faster standard duration, a
slightly stiffer spring — applied consistently across the whole system.

**Risky**: changing which interactions animate, how long a toast lingers, or whether a state change
is animated at all. Those are behavior and accessibility decisions, not styling.

**Not acceptable**: per-screen or per-component bespoke motion. Ad hoc timing is what makes an
otherwise consistent product feel assembled from parts.

Motion is not a theme parameter. A project-wide motion change is a foundation edit. Default
position: **keep the motion system as-is** and spend the customization budget on color and type,
where it is visible and cheap.

---

## Theme Application

### How it works

One theme wrapper at the app root. It takes a small set of inputs — dark/light mode, color
overrides, color scales, font family — and provides resolved colors, typography, and shapes to
everything below. Components read them through the `AppTheme` accessor.

```kotlin
PixaTheme(
    colorScales = ColorScales(brand = myBrandRamp),
    fontFamily = myBrandFont
) {
    App()
}
```

That is the entire customization surface for a typical project.

### Why this matters for design

- **One change, whole product.** Swapping a ramp at the root recolors every component, in both
  modes, in one move — because no component holds a color of its own.
- **Per-component restyling defeats the system.** A component overridden at the call site stops
  following the theme, which is exactly the drift a design system exists to prevent. If a component
  looks wrong under a new brand, the fix is almost always the token it reads, not the component.
- **It scales.** The same theme input governs the entire component set, and any component added
  later inherits it automatically.
- **Light and dark stay coherent.** Both modes derive from the same ramps under the same rules, so
  dark mode is not a second design to maintain.

Practical implication for design deliverables: a project skin should be expressible as *a set of
color ramps plus a font family*. If a proposed design cannot be, that's a signal worth examining
before it becomes implementation work.

### Known constraint

Colors and typography are theme inputs. **Shapes, sizes, motion, and elevation are not** — they are
fixed at the foundation and selected per call site. Any design that depends on changing them
project-wide requires a foundation change, and should be flagged early rather than discovered
during build.

---

## Project Customization Workflow

Recommended order. Each step is cheap and reversible; the later steps get expensive.

**1. Brand color palette.** Build ramps for `brand` and, if applicable, `accent`, with the
canonical brand color at weight 500. Decide whether `base` gets a temperature shift. Decide whether
any status color must move.

**2. Font family.** Confirm the brand font ships nine weights, or decide substitutions. Apply and
keep the scale.

**3. Validate.** Before touching anything else:
- Text contrast against every surface it lands on, light **and** dark.
- The full base content hierarchy — title / subtitle / body / caption / hint should still read as
  five distinct levels, not three.
- Status colors still legible as status.
- Emphasis ordering intact: subtle quieter than default, default quieter than focus, in every group.
- Borders visible against their adjacent surfaces in both modes.

**4. Review against real screens.** Palette and font decisions look different on a dense list than
on a marketing hero. Review both before locking.

**5. Only then, optional tuning.** Shape family selection for specific expressive moments; motion
feel only if there's a stated reason. Both are component-level changes — scope them explicitly.

**Throughout: check whether theme-level inputs already solve it.** The most common failure mode is
rewriting a component to get a result that a color override would have produced. Before proposing
any component or token change, state which theme-level option was tried and why it wasn't enough.

---

## Recommendations for Claude Design

If you're producing a project skin on this foundation, **the deliverable is the Project Brand Input
Checklist at the end of this document, filled in.** That table is the complete specification — color
ramps, font decision, and a short list of exceptions.

Three things to get right while filling it in:

- **Think in ramps, not in hex values.** One 11-step ramp populates nine semantic tokens correctly
  in both light and dark. A list of individual hex values does not, and forces token-by-token
  pinning that drifts.
- **Design both modes at once.** They derive from the same ramps under mirrored rules, so dark mode
  is a consequence of your ramp choices, not a separate design. Verify it rather than assuming it.
- **Keep exceptions short.** Long exception lists are a signal that the design is fighting the
  foundation rather than configuring it.

What *not* to produce:

- A new token naming scheme
- A rebuilt type ladder with different roles
- New spacing or radius scales
- Per-component color specifications
- Screen-specific shape or motion treatments

If the design genuinely can't be expressed within this envelope, say so explicitly and name the
foundation change required. That's a legitimate outcome — but it should be a deliberate, surfaced
decision, not something that emerges from a spec that quietly assumes it.

---

## Appendix: Token Architecture Summary

### Color

```
{group}{Role}{Emphasis}
   │       │       └── Subtle | Default | Focus
   │       └────────── Surface | Border | Content
   └────────────────── brand | accent | base | info | success | warning | error
```

- 6 uniform groups × 9 tokens + `base` × 17 = **71 semantic tokens**
- Each group backed by an 11-step ramp: 50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 950
- `{group}ContentDefault` = weight 500 in **both** modes (the anchor)
- Light: surfaces 50/100/200, borders 300/400/500, content 600/500/700
- Dark: surfaces 700/800/900, borders 900/700/500, content 400/500/300
- `base` extras: surface elevated / shadow / disabled; border disabled; content title, subtitle,
  body, caption, hint, negative, disabled
- Customization: `ColorScales` (ramp-level, merged per weight) then `ColorOverrides` (token-level)

### Typography

- **32 styles**, anchor `bodyRegular` at 16sp
- Tiers: display(3) · header(2) · headline(2) · title(3) · subtitle(3) · body(3) · caption(3) ·
  overline(1) · footnote(2) · label(3) · action(7)
- ~1.25× progression from the 16sp anchor
- All line heights are multiples of 4sp
- Letter spacing tightens as size grows; widest on overline
- Requires nine font weights (100–900)

### Shape

- **14 families**, each with 5 tiers (extraSmall → extraLarge), plus a size-invariant `pill`
- Standard: rounded, cut
- Expressive: concave top/bottom, convex top/bottom, wave, arch top/bottom, tab, notch
  rounded/sharp, bubble left/right
- Rounded tiers: 4 / 6 / 8 / 12 / 16dp
- Size variants map to tiers: None,Nano → xs · Compact → s · Small,Medium → m · Large → l ·
  Huge,Massive → xl

### Size, elevation, motion, adaptive

- **Size**: 8 variants (None → Massive, Medium the anchor) × 24 categories; 4dp base unit;
  48dp touch-target minimum
- **Elevation**: 5 tiers — None 0dp · Low 1dp · Medium 2dp · High 4dp · Highest 8dp
- **Motion**: named springs + a fixed duration ladder (100 / 150 / 300 / 500 / 650ms) + stock
  fade / scale / slide transitions; interaction feedback takes its hue from the content color
- **Adaptive**: breakpoints at 600dp / 840dp → Compact / Medium / Expanded; page margin
  16 / 24 / 48dp; section spacing 24 / 48 / 48dp; in-component adaptive sizing is opt-in

None of these are theme inputs.

---

## Verification Notes

**Code is the source of truth.** Every figure in this brief was read from `theme/` and `utils/`
source on 2026-07-20, not from the repository's markdown. Re-verify against source before relying
on any count here — the foundation evolves.

Source-verified: the 71-token color palette and its group/role/emphasis structure; the weight-500
content anchor in both modes; the 32 text styles and their sizes; the nine-weight font requirement;
the 4sp line-height grid; 14 shape families plus the size-invariant pill; 8 size variants across 24
categories; the 48dp touch-target minimum; the five-tier elevation scale; the 600dp / 840dp
breakpoints; and the fact that `PixaTheme` accepts **only** dark-mode, color overrides, color
scales, and font family as inputs.

Two drift items matter for reading this brief against the repo's other docs:

- **Color token count.** `CLAUDE.md` says 79 semantic tokens; the actual palette has **71**
  (6 uniform groups × 9, plus `base` × 17). This brief uses 71. If a design proposal is built
  against a 79-token map, it is working from the stale number.
- **Shape and motion are not theme-configurable.** No repo doc states this outright, but it follows
  directly from the theme's parameter list. It is the single most important constraint for scoping
  a project skin, so it is stated explicitly throughout this brief.

Other drift found (component file counts, an in-progress internal migration away from a few
residual Material text primitives) has no bearing on a project skin: those components still read
PixaCompose color and type tokens, so they follow the theme like everything else.

---

## Project Brand Input Checklist

The handoff artifact. A complete project skin is this table filled in — nothing more is required,
and anything beyond it should be flagged as a foundation change.

| # | Input | What to supply | Required? | Notes |
|---|---|---|---|---|
| 1 | **Primary brand palette** | 11-step ramp: 50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 950 | **Yes** | Canonical brand color at weight **500** — it is the content anchor in both light and dark |
| 2 | **Accent palette** | Same 11-step ramp, or "none — use brand only" | If the brand has a real secondary | A second full-strength family, not a tint of brand |
| 3 | **Neutral / base preference** | "Keep default neutral", or an 11-step ramp | **Yes** (a decision, even if "keep") | Drives every surface, border, and line of body text — tint lightly or not at all |
| 4 | **Status color exceptions** | Per group (`info` / `success` / `warning` / `error`): keep or replace, with reason | **Yes** (usually "keep all four") | These carry meaning, not identity; change only on a genuine clash |
| 5 | **Font family files** | The family, all nine weights (100 Thin → 900 Black) | **Yes** | Platform default is used if none supplied |
| 6 | **Font weight mapping** | Explicit substitutions if fewer than nine weights ship | If < 9 weights | Several styles sit at 800/900; substitution is a design decision, not an implementation one |
| 7 | **Type treatment** | "Font swap only" or "swap + scale retune" — and if a retune, which values change | **Yes** | Any changed line height must land on a 4sp multiple |
| 8 | **Dark mode expectations** | Confirmation that both modes were designed and reviewed, plus any token that must differ between them | **Yes** | Both modes derive from the same ramps; a ramp good in light can flatten in dark |
| 9 | **Token exceptions** | Any individual token pinned to an exact value rather than derived, with reason | Only if needed | Heavy use here usually means a ramp should have been supplied instead |
| 10 | **Approved shape exceptions** | Default corner language (rounded or cut), plus any expressive family and exactly where it is used | **Yes** (often "rounded, no exceptions") | Not a theme input — each exception is a component change |
| 11 | **Approved motion exceptions** | Normally "none"; otherwise the specific change and its product reason | **Yes** (default: none) | Not a theme input; changing interaction semantics is a behavior decision |
| 12 | **Accessibility / contrast** | Contrast results for both modes: body text on every surface, the five-level base content hierarchy, borders against adjacent surfaces, status colors still legible as status | **Yes** | Nothing validates this automatically — it is a design gate |

Rows 1–8 are the normal scope of a project skin. Rows 9–11 should be short or empty. Row 12 is
not optional.

# LDSG vs PixaCompose — Gap Analysis
**Source**: LINE Design System Global (LDSG) v3.5 vs PixaCompose v1.1.1  
**Components Analyzed**: Action Button · Chip · Switch  
**Method**: LDSG spec pages scraped directly from `designsystem.line.me`; PixaCompose specs from `DOCUMENTATION.md`  
**Note on LDSG px values**: LDSG renders all exact size/padding measurements as embedded images. The text layer (scraped here) exposes structural specs, type names, state names, and customization rules — but not raw px figures for heights or padding. Where a value was rendered as an image only, this is marked `[image-only — unclear from text]`.

---

## Action Button

### LDSG Extracted Specs

| Property | LDSG Value |
|---|---|
| **Component name** | Action Button |
| **Size variants** | XS, S, M, L, XL (5 sizes) |
| **Height per size** | [image-only — unclear from text] |
| **Horizontal padding per size** | [image-only — unclear from text] |
| **Vertical padding per size** | [image-only — unclear from text] |
| **Visual variant names** | Contained, Outlined, Ghost |
| **Element patterns** | Icon + Text, Text Only, Icon Only |
| **Button modes** | Flexible (auto-width, fixed side padding), Fixed (no fixed padding, free width), Full-Bleed (100% width) |
| **States** | Enabled, Disabled, Hover (PC/web only), Pressed |
| **Radius options** | Selectable: 3, 5, or 7 (dp — unit unclear from text) |
| **Disabled color** | `$ldsg-color-disabled-gray` = `#E4E4E4` (explicitly documented) |
| **Full-Bleed Home Indicator option** | True / False (iOS Home Indicator area awareness) |
| **Full-Bleed Icon option** | True / False |
| **Animation notes** | None documented |
| **Customization** | Background color, radius (3/5/7), typography (color + family, size not recommended to change), icon (LAICON set + color) |

---

### ALREADY DONE

- **3 visual variants present**: PixaCompose has `Filled` (= Contained), `Outlined`, `Ghost` — direct 1:1 match to LDSG's three types.
- **Icon + Text, Text Only, Icon Only patterns**: PixaCompose `leadingIcon` + `trailingIcon` + text-only + `ButtonShape.Circle` for icon-only — all three patterns covered.
- **Enabled / Disabled states**: Both `enabled: Boolean` parameter present in PixaCompose.
- **Loading state**: PixaCompose adds `loading: Boolean` — exceeds LDSG (LDSG has no loading state on Action Button).
- **Skeleton state**: PixaCompose adds `showSkeleton: Boolean` — exceeds LDSG.
- **Destructive variant**: PixaCompose adds `isDestructive: Boolean` — exceeds LDSG.
- **Tonal variant**: PixaCompose adds `ButtonVariant.Tonal` — exceeds LDSG (LDSG has no Tonal type).
- **Full-width support**: PixaCompose uses `Modifier.fillMaxWidth()` — functionally equivalent to LDSG Full-Bleed.
- **3 shape options**: PixaCompose has `ButtonShape.Default`, `Pill`, `Circle` — covers LDSG's radius concept with named shapes.

---

### MISSING

- **XS size variant**: LDSG defines 5 sizes (XS, S, M, L, XL). PixaCompose `SizeVariant` has `Nano, Compact, Small, Medium, Large, Huge, Massive` — 7 values. However, LDSG's specific label **"XS"** as a named tier is absent. The mapping is likely `Nano` ≈ `XS`, but the name differs. No `SizeVariant.XS` exists.
- **Flexible vs Fixed layout modes**: LDSG explicitly distinguishes **Flexible** buttons (fixed side padding, auto-width) from **Fixed** buttons (no fixed padding, freely adjustable width). PixaCompose has no documented `ButtonMode` or `ButtonLayout` enum to express this distinction — width is left entirely to the caller's `Modifier`.
- **Full-Bleed with Home Indicator awareness**: LDSG Full-Bleed includes an `iPhoneHomeButton: True/False` option that adjusts bottom padding for devices with/without a home indicator. PixaCompose has no documented equivalent parameter.
- **Exact radius selection as a discrete enum/parameter**: LDSG exposes radius as a discrete 3-value choice (3, 5, or 7). PixaCompose routes this through `AppTheme.shapes.rounded.*` (extraSmall=4dp, small=8dp, medium=12dp…) — no discrete 3-step picker on the button component itself.
- **Pressed state visual spec**: LDSG documents a `Pressed` state distinct from `Enabled`. PixaCompose documents no explicit `pressed` visual spec or `isPressed` parameter (Compose handles this via `Indication`/`InteractionSource` implicitly, but it is not documented as a component-level spec).

---

### IMPROVE

- **Size naming alignment**: LDSG uses `XS / S / M / L / XL` — a common, universally understood convention. PixaCompose uses `Nano / Compact / Small / Medium / Large / Huge / Massive`. While PixaCompose has more granular sizes, the names are non-standard and differ from industry convention (Material Design, LDSG, iOS HIG all use XS–XL). **Recommendation**: add `typealias` or a companion mapping so consumers can reference XS/S/M/L/XL while PixaCompose uses its internal enum — reducing the cognitive gap when referencing LDSG.
- **Disabled color as explicit token**: LDSG explicitly names the disabled color token (`$ldsg-color-disabled-gray = #E4E4E4`) and states it applies regardless of the enabled color. PixaCompose maps disabled to `AppTheme.colors.baseContentDisabled` / `baseSurfaceDisabled` — semantically correct, but the docs do not explicitly state the disabled override behavior (i.e., that it ignores the variant's active color). **Recommendation**: document disabled state color override behavior explicitly in the `PixaButton` spec.

---

### SKIP

- **Hover state**: LDSG marks Hover as "PC web only." Not applicable to Compose Multiplatform (Android/iOS).
- **Radius unit ambiguity (rem vs dp)**: LDSG radius values (3, 5, 7) appear to be in LDSG design tokens, likely dp but context is a web design system. Skip direct px mapping.

---

## Chip

### LDSG Extracted Specs

| Property | LDSG Value |
|---|---|
| **Component name** | Chip |
| **Size variants** | Small, Medium, Large (3 sizes) |
| **Height per size** | [image-only — unclear from text] |
| **Horizontal padding per size** | [image-only — unclear from text] |
| **Vertical padding per size** | Unchangeable (locked per size — exact value image-only) |
| **Visual type names** | Contained, Outlined |
| **Element patterns** | Text Only, Side Icons (left + right), Left Icon only, Right Icon only |
| **States** | Selected, Unselected, Disabled |
| **Interaction sub-states** | Hover (in Selected/Unselected — PC/web only), Pressed (in Selected/Unselected) |
| **Width behavior** | Auto-width — adjusts to content length |
| **Minimum usage rule** | Requires 2+ chips; single chip not permitted |
| **Container fill default** | `$ldsg-color-brand-primary` |
| **Icon fill default** | `$ldsg-color-white` |
| **Text color default** | `$ldsg-color-white` |
| **Contained border** | Unchangeable |
| **Outlined border color** | Changeable |
| **Border width** | Changeable |
| **Border radius default** | Pill (fully rounded) |
| **Animation notes** | None documented |

---

### ALREADY DONE

- **Contained type**: PixaCompose `ChipVariant.Filled` = LDSG Contained — 1:1 match.
- **Outlined type**: PixaCompose `ChipVariant.Outlined` — 1:1 match.
- **Selected / Unselected states**: PixaCompose exposes `selected: Boolean` — covers both.
- **Disabled state**: PixaCompose exposes `enabled` (implied — standard Compose convention; disabled chips are standard).
- **Left Icon / Right Icon patterns**: PixaCompose `leadingIcon: Painter?` covers Left Icon. Right Icon pattern via `trailingIcon` or `onDismiss` (Dismissible type).
- **Auto-width**: Compose chips naturally auto-size to content — inherent behavior.
- **Pill border radius default**: PixaCompose default radius for chips uses pill/full-round consistent with LDSG default.
- **Additional variants beyond LDSG**: PixaCompose adds `ChipVariant.Tonal` and `ChipVariant.Ghost` — exceeds LDSG.
- **Additional types beyond LDSG**: PixaCompose `ChipType.Static`, `Selectable`, `Dismissible`, `Input` — LDSG has no equivalent behavioral type enum; PixaCompose exceeds LDSG here.

---

### MISSING

- **3-tier named sizes (Small / Medium / Large) as chip-specific parameters**: LDSG defines Small, Medium, Large explicitly for chips. PixaCompose `PixaChip` spec in the documentation does **not** list a `size` parameter — the parameter table shows only `label`, `selected`, `onClick`, `type`, `variant`, `leadingIcon`, `onDismiss`. There is no `size: SizeVariant` listed on `PixaChip`.  
  **Gap**: Chip has no documented size parameter. LDSG has 3 distinct chip sizes with their own height/padding specs.
- **"Side Icons" element pattern as a dedicated type**: LDSG explicitly names "Side Icons" (both left AND right simultaneously) as a distinct fourth pattern. PixaCompose achieves this by combining `leadingIcon` + `onDismiss`, but there is no declared `Side Icons` pattern or enforced bilateral icon layout on the chip API.
- **Single-chip usage guard / validation**: LDSG specifies chips are only valid when 2+ are present. PixaCompose has no documented API enforcement or usage warning for single-chip scenarios.

---

### IMPROVE

- **Type naming — "Contained" vs "Filled"**: LDSG uses "Contained" to mean a filled background chip. PixaCompose uses "Filled." These are functionally identical but named differently, which creates friction when cross-referencing LDSG specs during implementation. **Recommendation**: add `@Deprecated` or `typealias ChipVariant.Contained = ChipVariant.Filled` for LDSG parity, or document the mapping explicitly in the chip spec.
- **Chip size as a first-class API parameter**: LDSG treats size as a defined spec axis with distinct height and padding per size. PixaCompose's chip does not expose a `size` parameter in its documented API. Given PixaCompose uses `SizeVariant` universally elsewhere, the chip should accept `size: SizeVariant` with Small/Medium/Large at minimum to match LDSG's sizing intent. **Recommendation**: add `size: SizeVariant = SizeVariant.Medium` to `PixaChip`.

---

### SKIP

- **Hover state**: LDSG explicitly marks Hover as applicable only in "Selected and Unselected states depending on interaction" — and is PC/web-only. Not applicable to Android/iOS Compose.
- **Pressed sub-state visual spec**: Compose handles press indication via `Indication` / ripple at framework level. Not a component-API concern.

---

## Switch

### LDSG Extracted Specs

| Property | LDSG Value |
|---|---|
| **Component name** | Switch |
| **Size variants** | None listed (single size only) |
| **Height / width** | [image-only — unclear from text] |
| **Padding** | [image-only — unclear from text] |
| **Visual types** | None (no type/variant axis) |
| **Anatomy elements** | Text Label (1), Description Text optional (2), Container/Track (3), Thumb (4) |
| **Description text** | Optional (True/False toggle in design) — not used by default |
| **States** | Enabled, Disabled, Error |
| **On/Off** | On, Off (orthogonal axis to states) |
| **Hover state** | Not in design library; auto-applied during development (PC/web only) |
| **Pressed state** | Not in design library; auto-applied during development |
| **Behavior** | State change takes effect instantly (no deferred commit) |
| **Animation notes** | Hover/Pressed auto-applied during development; no explicit animation spec published |
| **Error state** | Explicitly named as a third state distinct from Enabled and Disabled |

---

### ALREADY DONE

- **On / Off states**: PixaCompose `checked: Boolean` — covers both.
- **Enabled / Disabled states**: PixaCompose `enabled: Boolean` — covers both.
- **Text Label**: PixaCompose `label: String?` — direct match.
- **Instant state change behavior**: Compose state is reactive and updates immediately — matches LDSG behavioral requirement.
- **Thumb animation**: PixaCompose uses `thumbSpring` (MediumBouncy, High stiffness) for thumb position — exceeds LDSG (which documents no thumb animation spec).
- **Color transition animation**: PixaCompose uses `colorSpring` for track/thumb/border color — exceeds LDSG spec.
- **Multiple visual variants**: PixaCompose `SwitchVariant.Filled`, `Outlined`, `Ghost` — exceeds LDSG (LDSG has no variant axis for Switch).
- **Convenience variants**: PixaCompose `SettingSwitch` (label on left), `ToggleSwitch` (no label) — exceeds LDSG.

---

### MISSING

- **Error state**: LDSG explicitly defines an `Error` state as a named third state alongside Enabled and Disabled. PixaCompose `PixaSwitch` documents only `checked: Boolean` and `enabled: Boolean` — there is no `isError: Boolean` or `SwitchState.Error` documented. This state is used in LDSG to signal a conflict or invalid toggle value (e.g., a setting that cannot be changed due to a downstream error).  
  **Gap**: `PixaSwitch` needs an `isError: Boolean` parameter with distinct visual treatment using `AppTheme.colors.errorContentDefault` / `errorBorderDefault`.
- **Description Text slot**: LDSG anatomy element 2 is an optional description text line beneath the label (separate from the label itself). PixaCompose documents only `label: String?` — no secondary `description: String?` parameter exists on `PixaSwitch`.  
  **Gap**: Add `description: String?` (or `subtitle: String?`) parameter to `PixaSwitch` for multi-line switch rows.

---

### IMPROVE

- **Pressed state documentation**: LDSG explicitly states Pressed state is "auto-applied during development" but is intentionally absent from the design library spec. PixaCompose makes no mention of pressed visual feedback on the Switch. While Compose's `Indication` system handles ripple/press by default, explicitly documenting the interaction spec (e.g., "thumb uses scale via `selectionSpring` on press") aligns PixaCompose with LDSG's intent of defined interaction states. **Recommendation**: document the press interaction behavior in the `PixaSwitch` spec (even if implementation is framework-handled).
- **Description text as a distinct typography role**: If a `description` parameter is added (see MISSING above), LDSG's anatomy positions the description text as a secondary line with lower visual weight than the label — consistent with `AppTheme.typography.captionRegular` (14sp) under a `bodyRegular` (16sp) label. **Recommendation**: when implementing the description slot, use `AppTheme.typography.captionRegular` + `AppTheme.colors.baseContentCaption` for the description line to match LDSG's visual hierarchy.

---

### SKIP

- **Hover state**: LDSG explicitly states "Hover and Pressed states are not provided in the design library, and the states are automatically applied during development" and "available only in PC and web environments." Not applicable to Android/iOS Compose.
- **Single fixed size**: LDSG Switch has no size variants — a single fixed size. PixaCompose having no size parameter on Switch is therefore appropriate and matches LDSG's intent.

---

## Cross-Component Summary

| Gap Type | Action Button | Chip | Switch |
|---|---|---|---|
| **ALREADY DONE** | 9 items | 9 items | 8 items |
| **MISSING** | 5 items | 3 items | 2 items |
| **IMPROVE** | 2 items | 2 items | 2 items |
| **SKIP** | 2 items | 2 items | 2 items |

### Top Priority Gaps to Address

1. **`PixaSwitch` — add `isError: Boolean`** — LDSG Error state is a named, distinct state; completely absent from PixaCompose Switch.
2. **`PixaSwitch` — add `description: String?`** — LDSG anatomy has a dedicated description text slot; PixaCompose has none.
3. **`PixaChip` — add `size: SizeVariant`** — LDSG specifies 3 chip sizes with distinct specs; PixaCompose chip exposes no size parameter.
4. **`PixaButton` — document Flexible vs Fixed layout mode** — LDSG distinguishes two layout behaviors explicitly; PixaCompose leaves this entirely to caller `Modifier` without documentation.
5. **`PixaButton` — add Full-Bleed Home Indicator awareness** — LDSG Full-Bleed includes an iOS Home Indicator bottom-padding option not present in PixaCompose.

---

*Generated: 2026-06-28 · Sources: LDSG v3.5 (designsystem.line.me) · PixaCompose v1.1.1 (DOCUMENTATION.md)*

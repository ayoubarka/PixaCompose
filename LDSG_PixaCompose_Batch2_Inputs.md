# LDSG vs PixaCompose — Batch 2: Inputs
Components: Pulldown (Dropdown), Text Input, Text Area, Switch, Slider, Radio Button, Checkbox

---
## Pulldown (Dropdown)

### Extracted LDSG Specs
| Property | Value |
|---|---|
| Anatomy | 1. Title (Optional), 2. Input Content, 3. Text Hint Area (Optional), 4. Icon Field |
| Element Pattern | Text Only, Icon + Text |
| Type | Filled, Outlined |
| Size | Small, Medium |
| Help Text Area | True, False |
| States shown | Enabled, Disabled, Error, Focus |
| Title | Optional label above field |
| Required | Optional asterisk marker + validation message |
| Help Text Area content | Optional helper text below field |
| Design Specs — Front Height | Fixed height (exact dp unclear) |
| Design Specs — Adjustable Width | Element has no fixed width; adjusts to text length |
| Design Specs — Front Reset/Spacing | unclear |
| Resizing | Compares Filled vs Outlined field sizing responsiveness |
| Styles table | Title, Input Content, Help Text, Icon, Container — each with Typography/Color/Background/Border-color/Border-width/Border-radius per state (Default, Focused, Error, Disabled) |
| Usage | Do/Don't mobile screenshots showing correct/incorrect pulldown placement in forms |

### ✅ ALREADY DONE
- `PixaDropdown` exists with `selectedValue`, `onValueSelected`, `options`, `label`, `placeholder` — covers Title + Input Content + basic selection.
- Generic typed dropdown with search/filter support covers Icon+Text pattern potential.

### ⚠️ MISSING
- **`variant: DropdownVariant` (Filled, Outlined)** — PixaDropdown has no visual variant enum; LDSG explicitly has 2 types.
- **`size: SizeVariant` (Small, Medium)** — no size parameter documented on PixaDropdown.
- **`isError: Boolean` + `errorText: String?`** — LDSG shows explicit Error state; PixaDropdown docs show no error handling params.
- **`helperText: String?`** — Help Text Area (optional) not present.
- **`required: Boolean` indicator** — no asterisk/required marker support shown.
- **`leadingIcon`/Icon field slot** — LDSG "Icon + Text" pattern has a dedicated icon field; not documented on PixaDropdown.

### 🔧 IMPROVE
- PixaDropdown should align its parameter surface with `PixaTextField` (label, placeholder, helperText, errorText, leadingIcon, size, variant) since LDSG treats Pulldown as a sibling of Text Input with nearly identical anatomy — consistency reduces API surprise.

### 🚫 SKIP
- Hover state — PC/web only.
- CSS/React "Code Tab" examples — web only.
- `rem`-based Design Spec measurements — Compose uses `dp`/`sp`.

---
## Text Input

### Extracted LDSG Specs
| Property | Value |
|---|---|
| Anatomy | 1. Title (Optional), 2. Required indicator (Optional), 3. Input Content, 4. Icon, 5. Help Text Area (Optional), 6. Character Counter (Optional) |
| Element Pattern / Type | Text Only, Icon+Text; Filled, Outlined |
| Size | Small, Medium (Large possibly, unclear) |
| Typing Status | Focused, Error, Disabled shown as separate boxes |
| Title | Label above input |
| Required | Red asterisk + validation copy |
| Help Text Area | Optional helper text below input |
| Character Counter | Optional counter (seen alongside Text Area pattern) |
| Behavior — Maximum Count | Field shows counter reaching max chars, displays message when exceeded |
| Design Specs diagram | Front Height (fixed), Front Reset (unclear), Selecting Description (unclear), Help Text |
| Styles table | Title, Text, component states (Default/Focused/Error/Disabled) |
| Child Elements table | Sub-components like search icon / clear icon with individual properties |
| Usage | Multiple Do/Don't mobile screenshots (green=Do, red=Don't) |

### ✅ ALREADY DONE
- `PixaTextField`: `variant` (Filled, Outlined, Ghost), `size` (Small 36dp, Medium 44dp, Large 52dp), `label`, `placeholder`, `helperText`, `errorText`, `isError`, `leadingIcon`, `trailingIcon`, `maxLength`, `readOnly`, `enabled` — covers almost the entire LDSG anatomy and state model.
- `EmailTextField`, `PasswordTextField`, `SearchTextField` convenience variants map to LDSG "Icon+Text" pattern use cases.

### ⚠️ MISSING
- **`characterCounter: Boolean` / auto-display of `"n/maxLength"`** — LDSG explicitly shows a Character Counter anatomy element tied to `maxLength`. PixaTextField has `maxLength` but no visible counter UI toggle.
- **`required: Boolean` (asterisk marker)** — no explicit required-field indicator parameter.
- **Clear icon behavior (child element)** — LDSG's Child Elements table lists a clear/reset icon; PixaTextField has `trailingIcon` (static) but no built-in "clear text" action.

### 🔧 IMPROVE
- **Max-length exceeded visual state** — LDSG shows the field border and counter turning red/error-colored automatically when max is exceeded. Ensure `PixaTextField` auto-triggers `isError`-like styling when `value.length >= maxLength`, rather than requiring manual `isError` wiring.

### 🚫 SKIP
- Hover-only "Typing Status" nuances tied to cursor — PC/web only.
- Code Tab / npm/React examples — web only.
- CSS token tables in Styles section — web-only implementation detail (Compose uses `ColorPalette` tokens instead).

---
## Text Area

### Extracted LDSG Specs
| Property | Value |
|---|---|
| Anatomy | 1. Title (Optional), 2. Text, 3. Container, 4. Help Text Area (Optional), 5. Character Counter (Optional) |
| Type | Filled, Outlined |
| Typing Status | Placeholder, Typing |
| Title | True, False |
| Help Text Area | True, False |
| States | Enabled, Disabled, Error (red border + error message) |
| Typing States | Placeholder, On Focus, On Typing, Typed |
| Text Area Sizing | Help Text Only vs Help Text + Character Counter (2 layout options) |
| Behavior — Scroll | Scrollbar appears when line count exceeds visible area |
| Behavior — Exceed Maximum Characters | Border turns red, counter turns red when exceeding max |
| Usage | Mobile screenshot — bio/comment field example |
| Design Specs — Resizing | Title (auto height), Text Area (auto height), Help Text Area (auto height, padding-driven), Character Counter (fixed), Adjustable Width |
| Styles table | Title, Text, Help Text, Character Counter — font/color per state |

### ✅ ALREADY DONE
- `PixaTextArea`: same params as `TextField` plus `minLines` (3 default), `maxLines` (`Int.MAX_VALUE` default), `maxLength` — covers Container, Text, Title, Help Text Area anatomy.
- `CommentTextArea`, `BioTextArea`, `NoteTextArea` convenience variants directly match LDSG's comment/bio usage example.

### ⚠️ MISSING
- **Character Counter display (`showCharacterCount: Boolean`)** — LDSG treats this as a distinct, optional anatomy element with its own layout slot ("Help Text + Character Counter"); PixaTextArea has `maxLength` but no counter rendering toggle.
- **Scroll behavior / scrollbar visibility control** — no documented `scrollState` exposure or scrollbar-visible-on-overflow behavior described for PixaTextArea.
- **`Typing` visual distinction (Placeholder vs On Focus vs On Typing vs Typed)** — 4 distinct typing states are described in LDSG; PixaCompose only has binary `isError`/`enabled`, no explicit "typed vs typing" visual differentiation.

### 🔧 IMPROVE
- **Exceed-max-characters error styling** — should auto-apply error border + red counter color when `value.length > maxLength`, matching LDSG's automatic behavior, rather than leaving it to manual `isError` toggling.
- **Auto-height behavior** — confirm `PixaTextArea` naturally grows with `minLines`/`maxLines` (likely already true via Compose `BasicTextField`), but document this explicitly since LDSG calls out "auto height" as a first-class spec.

### 🚫 SKIP
- Hover states — PC/web only.
- Code Tab / React examples — web only.
- CSS `rem` styling values in Styles table — not applicable to Compose `sp`/`dp`.

---
## Switch

### Extracted LDSG Specs
| Property | Value |
|---|---|
| Anatomy | 1. Text Label, 2. Description Text (Optional), 3. Container, 4. Thumb |
| Description | True, False |
| States | Enabled, Disabled, Error |
| On/Off | On, Off |
| State rows | Enabled, Disabled, Hover, Pressed (each shown On/Off) |
| Disabled | Option changes unavailable; cannot combine w/ Hover/Pressed |
| Behavior | Switching On/Off affects relevant element instantly |
| Description default | Not used by default |
| Sizes/heights | unclear |
| Padding | unclear |

### ✅ ALREADY DONE
- `SwitchVariant.Filled/Outlined/Ghost`, `checked`/`onCheckedChange`, `enabled`, `label` — covers Container, Thumb, Text Label, On/Off.
- `thumbSpring` (MediumBouncy, High stiffness) thumb animation, `colorSpring` for track/thumb/border, `selectionSpring` for disabled scale.
- `SettingSwitch` (label left), `ToggleSwitch` (no label) convenience variants.

### ⚠️ MISSING
- **`description: String?` / `helperText: String?` parameter** — LDSG anatomy explicitly includes optional description text below the label; not present on `PixaSwitch`.
- **`isError: Boolean` state** — LDSG lists Error as a general state; `PixaSwitch` only exposes `enabled`.

### 🔧 IMPROVE
- **Default label position** — LDSG anatomy always places label left, switch right. Consider making this the default `PixaSwitch` layout rather than requiring the separate `SettingSwitch` variant.

### 🚫 SKIP
- Hover state — PC/web only.
- Pressed visual — handled automatically via Compose `Indication`, no explicit API needed.

---
## Slider

### Extracted LDSG Specs
| Property | Value |
|---|---|
| Anatomy | 1. Minimum Value Icon (Optional), 2. Track, 3. Value Label (Optional), 4. Minimum Value Text (Optional), 5. Thumb, 6. Maximum Value Text (Optional) |
| Thumb shape | Circle (default) |
| Value Label | True, False |
| States | Enabled, Disabled |
| Value | Min, Mid, Max thumb positions demonstrated |
| Behavior — Changing Values | Mobile example: volume/brightness-style slider |
| Behavior — Drag and Flick | User can drag or flick thumb to set value |
| Behavior — Click/Tap Jump | Tapping a point on track jumps thumb directly to that position |
| Usage — Double Range Slider | Two-thumb range slider for selecting a min-max range |
| Design Specs | Minimum Value Icon, Fill, Value Label, Minimum/Maximum Value Text, Thumb — each with description |
| Styles | Color spec per element (Fill green, Track gray, Thumb white border, etc.) — exact hex unclear |

### ✅ ALREADY DONE
- `PixaSlider`: `SliderVariant.Filled/Outlined/Ghost`, `value`, `onValueChange`, `valueRange`, `steps`, `label`, `showValue`, `valueFormatter` — covers Track, Thumb, Value Label, min/max range.
- `fastSpring` (NoBouncy, High stiffness) animates track fill width + thumb position; `derivedStateOf` avoids extra recompositions.

### ⚠️ MISSING
- **`minIcon` / `maxIcon: Painter?` parameters** — LDSG anatomy includes optional icons at each end of the track (e.g., low-volume/high-volume icons); no equivalent in `PixaSlider`.
- **`minValueText` / `maxValueText: String?`** — optional text labels at track ends (distinct from the draggable value label); not present.
- **Double Range Slider (two-thumb range selection)** — LDSG shows this as a distinct usage pattern; PixaCompose has no `PixaRangeSlider` component with two thumbs and a `ClosedFloatingPointRange<Float>` value binding.
- **Explicit tap-to-jump vs drag-only distinction** — not documented whether `PixaSlider` supports direct tap-to-position (should confirm/implement).

### 🔧 IMPROVE
- Document Thumb shape options — LDSG shows Circle as default; confirm if `PixaSlider` supports alternate thumb shapes or hardcodes circle.

### 🚫 SKIP
- Hover state — PC/web only.
- Code Tab / CSS token specs — web only.

---
## Radio Button

### Extracted LDSG Specs
| Property | Value |
|---|---|
| Anatomy | 1. Container, 2. Text Label, 3. Description (Optional) |
| Description | True, False |
| States | Enabled, Disabled, Error |
| Selected | True, False |
| General States — Enabled | Component is available |
| General States — Disabled | Component unavailable; option changes unavailable |
| General States — Error | Indicates required selection missing or wrong option selected; user can still change selection |
| Select States — Selected | Radio has been selected |
| Select States — Unselected | Radio not selected (default) |
| Behavior | Default can be "no selection," but once selected, cannot revert to no-selection state |
| Usage — Do | Radio buttons are mutually exclusive — select only one item per group |
| Usage — Don't | Cannot select multiple items |

### ✅ ALREADY DONE
- `RadioButtonVariant.Filled/Outlined/Ghost`, `selected`, `onClick`, `label` — covers Container, Text Label, Selected/Unselected.
- `RadioGroup` (generic typed, single-selection enforced) and `HorizontalRadioGroup` — directly satisfies the "mutually exclusive, one item per group" usage rule.

### ⚠️ MISSING
- **`description: String?` parameter** — LDSG anatomy explicitly includes optional description text under the label; not present on `RadioButton`.
- **`isError: Boolean` state** — LDSG lists Error as a general state (required-selection validation); no equivalent on `RadioButton`/`RadioGroup`.

### 🔧 IMPROVE
- None beyond the above — core selection logic and grouping already match LDSG behavior well.

### 🚫 SKIP
- Hover state — PC/web only.
- Pressed visual — handled by Compose `Indication` automatically.

---
## Checkbox

### Extracted LDSG Specs
| Property | Value |
|---|---|
| Anatomy | 1. Container, 2. Text Label, 3. Description text (Optional) |
| Description | True, False |
| States | Enabled, Disabled, Error |
| Selected | Selected, Unselected, Indeterminate |
| General States — Disabled | Unavailable; option changes unavailable |
| General States — Error | Required selection missing or wrong option selected; user can change state |
| Select States — Indeterminate | Used when a parent checkbox's children have mixed selection states |
| Behavior — Nesting | Parent checkbox reflects children: All Selected → parent Selected; All Unselected → parent Unselected; Partially Selected → parent Indeterminate; selecting an Indeterminate parent sets all children to Selected |
| Usage — Single Item | Example: mutually exclusive two-state toggle (e.g., "LINE Official Account" follow toggle) |
| Usage — Multiple Items | List with filter checkboxes |
| Usage — Large List Do | Provide simple/clear label; enable filter/search for long lists |
| Usage — Large List Don't | Avoid complicated/long text without filtering; put extra info in description text instead |

### ✅ ALREADY DONE
- `CheckboxVariant.Filled/Outlined/Ghost`, `CheckboxState.Unchecked/Checked/Indeterminate`, `state`, `onStateChange`, `label`, `enabled` — covers Container, Text Label, all 3 Select States including Indeterminate.
- `colorSpring` for box/border/checkmark colors; `selectionSpring` for checkmark path draw-in/draw-out animation.

### ⚠️ MISSING
- **`description: String?` parameter** — LDSG anatomy includes optional description text; not present on `PixaCheckbox`.
- **`isError: Boolean` state** — Error state (validation) not exposed.
- **Parent/child nesting logic (`PixaCheckboxGroup` with hierarchical indeterminate propagation)** — LDSG's Nesting behavior (auto-computing parent Indeterminate/Selected/Unselected from children, and cascading Selected to children when parent Indeterminate is tapped) has no equivalent composable in PixaCompose. This is a significant, reusable pattern (e.g., "Select All" filter lists) worth implementing as `PixaCheckboxTree` or `NestedCheckboxGroup`.

### 🔧 IMPROVE
- None major — base checkbox states and indeterminate rendering already align closely with LDSG.

### 🚫 SKIP
- Hover state — PC/web only.
- Pressed visual — handled by Compose `Indication` automatically.

---

## Priority Action List

| Priority | Component | Gap | Suggested Fix |
|---|---|---|---|
| High | Checkbox | No nested parent/child indeterminate propagation | Add `PixaCheckboxTree`/`NestedCheckboxGroup` composable that auto-computes parent state from children selection |
| High | Slider | No Double Range Slider (two-thumb) | Add `PixaRangeSlider(valueRange: ClosedFloatingPointRange<Float>, ...)` with two thumbs |
| High | Pulldown/Dropdown | No `variant`, `size`, `isError`, `helperText` params | Extend `PixaDropdown` API to mirror `PixaTextField` parameter surface |
| Medium | Text Area | No character counter display toggle | Add `showCharacterCount: Boolean` param that renders "`n/maxLength`" using existing `maxLength` |
| Medium | Text Input | No built-in clear/reset icon action | Add `onClear: (() -> Unit)?` param with default clear-icon rendering when text is non-empty |
| Medium | Switch | No `description`/`isError` params | Add `description: String?` and `isError: Boolean` to `PixaSwitch` |
| Medium | Radio Button | No `description`/`isError` params | Add `description: String?` and `isError: Boolean` to `RadioButton` |
| Medium | Checkbox | No `description`/`isError` params | Add `description: String?` and `isError: Boolean` to `PixaCheckbox` |
| Low | Text Area | No explicit auto-height documentation | Document/verify `minLines`/`maxLines` auto-grow behavior matches LDSG "auto height" spec |
| Low | Slider | No min/max icon or min/max text slots | Add optional `minIcon`, `maxIcon`, `minValueText`, `maxValueText` params |
| Low | Text Input | Max-length exceeded doesn't auto-trigger error styling | Auto-apply error border/counter color when `value.length > maxLength` |

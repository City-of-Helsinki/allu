# Angular Material MDC Migration Plan

**Mode:** Re-run the schematic four times (one component each).
**Commits:** One commit per step (form-field → input → select → autocomplete).
**SCSS:** Migrate legacy internal class overrides to MDC equivalents (do not delete/restyle).

---

## Step 0 — Per-step schematic invocation pattern

For each step, run:
`cd frontend && ng generate @angular/material:mdc-migration` and select only the relevant component when prompted. Alternatively, check `ng generate @angular/material:mdc-migration --help` for a non-interactive flag to target a single component and avoid accidentally patching the others.

Capture the diff the schematic produces, review it, then finish manual touches (TS/HTML/SCSS/tests) before committing.

Watch for the schematic rewriting dependent imports outside the current step's scope (e.g. form-field step flipping `MatLegacyInput`). Revert anything outside the current step before committing.

---

## Step 1 — FORM FIELD (foundation; others depend on it)

1. Run the schematic selecting only **form field**; capture the patch.
2. `input-box/input-box.component.ts:14`: confirm `MatLegacyError` → `MatError` from `@angular/material/form-field`.
3. Templates flagged: convert `placeholder="..."` on inputs/selects inside `mat-form-field` to `<mat-label>...</mat-label>`. (Remaining conversions land in steps 2–4.)
4. Ensure `MatFormFieldModule` is imported + exported in `allu-common.module.ts` (MDC requires it explicitly — verify the schematic added it).
5. `input-warning.directive.ts:5`: no change expected — confirm build still matches `mat-form-field[inputWarning]`.
6. SCSS class migration (legacy → MDC):
   - `forms.scss:13` `.mat-form-field.form-field-dense .mat-form-field-wrapper` → `.mat-mdc-form-field.form-field-dense .mat-mdc-text-field-wrapper`
   - `radio-buttons.scss:5` `.radio-group-item .mat-form-field-wrapper` → `.mat-mdc-text-field-wrapper`
   - `external-user.component.scss:5` `.customer-name-search .mat-form-field-wrapper` → `.mat-mdc-text-field-wrapper`
   - Review `input-box.component.theme.scss`, `information-request-theme.scss` for legacy field/internal class selectors and migrate.
7. `ng build` + `ng test` for affected components (comment, workqueue-filter, customer-info, input-box, decision-proposal-modal).
8. **Commit:** `refactor(material): migrate form-field to MDC`.

---

## Step 2 — INPUT

1. Run the schematic selecting only **input**; capture patch.
2. `allu-common.module.ts:14`: confirm `MatLegacyInputModule` → `MatInputModule` (`@angular/material/input`).
3. `searchbar.module.ts:3`: same swap.
4. Templates: verify `<input matInput>` containment inside `mat-form-field` per MDC rules; convert any leftover `placeholder` on inputs to `<mat-label>`.
5. SCSS: `assets/inputs.scss` and `assets/forms.scss` — audit and map legacy input internal class selectors (`mat-input-wrapper`-style) to MDC equivalents (`mat-mdc-input-wrapper`/`.mat-mdc-form-field-*`).
6. `ng build` + `ng test` for searchbar and input-heavy components.
7. **Commit:** `refactor(material): migrate input to MDC`.

---

## Step 3 — SELECT

1. Run the schematic selecting only **select**; capture patch.
2. `allu-common.module.ts:20`: confirm `MatLegacySelectModule` → `MatSelectModule` (`@angular/material/select`).
3. Swap `MatLegacyOption` (legacy-core) → `MatOption` (`@angular/material/core`) in:
   - `application-identifier-select.component.ts:4`
   - `geometry-select.component.ts:5`
   - `application-select.component.ts:5`
   - `related-projects/project-select.component.ts:4`
4. Templates: convert `<mat-select placeholder="...">` → `<mat-label>` where flagged (e.g. `decision-proposal-modal.component.html:20`, `owner-modal.component.html`). Verify `[multiple]` filter dropdowns (workqueue-filter component lines 31–64) still render.
5. SCSS: audit option/trigger overrides in `assets/forms.scss` and any project-select component SCSS; map legacy select internal classes to MDC (`mat-mdc-select*`).
6. `ng build` + `ng test`; visual check of all multi-select filter dropdowns and the `[placeholder]`-driven selects.
7. **Commit:** `refactor(material): migrate select to MDC`.

---

## Step 4 — AUTOCOMPLETE (last; depends on scroll-strategy token swap)

1. Run the schematic selecting only **autocomplete**; capture patch.
2. `allu-common.module.ts:4`: confirm `MatLegacyAutocompleteModule` → `MatAutocompleteModule` (`@angular/material/autocomplete`).
3. `information-request.module.ts:42,93`: swap `MAT_LEGACY_AUTOCOMPLETE_SCROLL_STRATEGY` → `MAT_AUTOCOMPLETE_SCROLL_STRATEGY`; keep `useFactory` + `deps: [Overlay]`.
4. Templates: `<mat-autocomplete>` usages unchanged (selector & `#x="matAutocomplete"` export valid); confirm `matOption` template-bindings still work with new `MatOption`.
5. SCSS: `assets/autocomplete.scss` — migrate `.mat-autocomplete-panel` → `.mat-mdc-autocomplete-panel` etc.
6. `ng build` + `ng test`; visual check on customer-info name/registry-key autocompletes and searchbar.
7. **Commit:** `refactor(material): migrate autocomplete to MDC`.

---

## Step 5 — Final verification (post-commit, no commit itself unless issues)

1. Full `ng build` (no warnings about legacy imports).
2. Full unit test suite.
3. Update `.mat-form-field` CSS-selector assertions in `information-request-summary.component.spec.ts:83,95` if they no longer match (MDC exposes `.mat-mdc-form-field`; change selector if needed). Fold the fix into the relevant step's commit, or add a follow-up commit.
4. Manual smoke test:
   - comment form
   - workqueue filters
   - customer registry autocomplete
   - searchbar
   - information-request modals

---

## Files known to be touched (from codebase exploration)

### TypeScript / imports
- `frontend/src/app/feature/common/allu-common.module.ts` (lines 4, 14, 20)
- `frontend/src/app/feature/searchbar/searchbar.module.ts` (line 3)
- `frontend/src/app/feature/common/input-box/input-box.component.ts` (line 14)
- `frontend/src/app/feature/common/validation/input-warning.directive.ts` (line 5 — verify, no change expected)
- `frontend/src/app/feature/information-request/information-request.module.ts` (lines 42, 93)
- `frontend/src/app/feature/application/identifier-select/application-identifier-select.component.ts` (line 4)
- `frontend/src/app/feature/application/location/geometry-select/geometry-select.component.ts` (line 5)
- `frontend/src/app/feature/project/applications/application-select.component.ts` (line 5)
- `frontend/src/app/feature/project/related-projects/project-select.component.ts` (line 4)

### Templates (non-exhaustive — schematic report is authoritative)
- `comment.component.html`
- `workqueue-filter.component.html`
- `information-request-summary.component.html`
- `information-request-response-summary.component.html`
- `owner-modal.component.html`
- `decision-proposal-modal.component.html`
- `customer-info.component.html`
- `request-field.component.html`
- `customer-contacts.component.html`

### SCSS
- `frontend/src/assets/forms.scss` (line 13)
- `frontend/src/assets/radio-buttons.scss` (line 5)
- `frontend/src/assets/inputs.scss`
- `frontend/src/assets/autocomplete.scss`
- `frontend/src/assets/main.scss`
- `frontend/src/assets/list-theme.scss`
- `frontend/src/app/feature/admin/external-user/external-user.component.scss` (line 5)
- `frontend/src/app/feature/common/input-box/input-box.component.theme.scss`
- `frontend/src/app/feature/information-request/information-request-theme.scss`
- `frontend/src/app/feature/application/info/recurring/recurring.component.scss`
- `frontend/src/app/feature/information-request/acceptance/field-select/field-select.component.scss`

### Tests
- `frontend/src/test/feature/information-request/summary/information-request-summary.component.spec.ts` (lines 83, 95)
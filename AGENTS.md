# QD — Agent Instructions

This file is the canonical source of project-specific instructions for coding agents working in this repository. Tool-specific files (`CLAUDE.md`, `.github/copilot-instructions.md`, `.cursor/rules/qd.mdc`, `.windsurfrules`) intentionally point here and must not duplicate or override these rules.

## Project snapshot

- Artifact: `com.github.nderwin:qd` (version `0.0.0-SNAPSHOT`).
- Stack: Quarkus 3.40.x (`quarkus-bom` 3.40.1.1), Java 25, Maven, `quarkus-desktop-swing`, FlatLaf 3.7.2.
- App type: desktop Swing application, not a web service. There is no HTTP endpoint/UI beyond Quarkus Dev UI.
- Native-image capable (`-Dnative` profile).

## Repository layout (Boundary / Control / Entity)

Packages under `com.github.nderwin.qd`:

- `boundary` — Swing UI, actions, dialogs, window geometry, look-and-feel support. Should contain presentation logic only.
- `control` — CDI services and application events: `I18n`, `UserPreferencesStore`, `ApplicationExitEvent`.
- `entity` — not present yet. Add only when domain models grow; keep them free of Swing/CDI concerns where practical.

CDI scopes in use:
- `@Singleton` for UI components and actions (`MainFrame`, `*Action`, `*Panel`).
- `@ApplicationScoped` for services (`UserPreferencesStore`, `IconManager`, `I18n`).
- `@Inject` fields; observe `StartupEvent` / `DesktopStartupEvent`.

## Commands

Run from repo root:

- Dev mode (live coding): `./mvnw quarkus:dev`
  - Dev UI: `http://localhost:8080/q/dev/`
- Tests: `./mvnw test`
  - No tests exist currently; JUnit 5 (`quarkus-junit`) is available.
- Package (JVM): `./mvnw package`
  - Output: `target/quarkus-app/quarkus-run.jar`
- Native executable: `./mvnw package -Dnative`
  - Container build fallback: `./mvnw package -Dnative -Dquarkus.native.container-build=true`
- Offline quick compile check: `./mvnw -o -q compile`

Always use the Maven wrapper. Prefer non-interactive execution; the user cannot answer stdin prompts.

## Versioning and destructive operations (global rules)

- Never automatically commit, push, force-push, delete branches, or delete files/directories.
- Obtain explicit permission for every individual git or filesystem mutation of that kind.
- Don't overwrite files outside task scope.

## Architecture rules

- Keep BCE boundaries clean: `boundary` must not contain business logic; `control` must not import Swing.
- Look-and-feel changes go through `LafSupport.apply(...)` (never call `UIManager.setLookAndFeel` directly in UI classes). New L&Fs are registered in `LafSupport.installFlatLookAndFeels()`.
- i18n goes through `I18n` (`getMessage`, `getMnemonic`, `getAccelerator`) plus `messages.properties`. Do not hard-code user-visible strings.
- Window geometry persistence is subtle (normal/maximized/minimized tracking, multi-monitor fit, minimum sizes). Don't simplify or "clean up" that logic without running the app.

## UI conventions

- Layouts: `GridBagLayout` with explicit `GridBagConstraints` and insets (see `PreferencesPanel`).
- Mnemonics/accelerators registered on actions, not components where an action exists.
- Dialogs: modal `JDialog`, `HIDE_ON_CLOSE`, set default button, ESC -> cancel, `pack()` once, then `setLocationRelativeTo(mainFrame)`.
- L&F switching: apply, then `FlatLaf.updateUI()` or Swing tree update handled by `LafSupport`.
- Java desktop APIs are brought in via `import module java.desktop;`.

## Resources and i18n

- `messages.properties` and `application.properties` are filtered resources; other files under `src/main/resources` are copied verbatim.
- Placeholders like `${project.version}` are resolved at build time (used in `app.credits`).
- Add new user-facing strings as i18n keys, not Java literals.

## Preferences persistence

- Backed by `java.util.prefs.Preferences.userNodeForPackage(UserPreferencesStore.class)`.
- Keys: `KEY_X`, `KEY_Y`, `KEY_W`, `KEY_H`, `KEY_STATE`, `KEY_INIT`, `KEY_LAF`.
- L&F class name validated with `Class.forName(...)` before use.
- Always `flush()` inside try/catch `BackingStoreException` (failures are logged, not thrown).

## Native image

- Reflection metadata lives in `src/main/resources/META-INF/native-image/reachability-metadata.json`.
- When adding reflected classes (new L&Fs, Swing components, etc.), regenerate/extend that file and verify with `./mvnw package -Dnative`.
- A stale metadata file causes native runtime failures even when JVM tests pass.

## Coding conventions

- Java 25; records for simple DTOs (`WindowGeometry`, `WindowPersistence`).
- Existing null-check idiom uses `null == x` / `null != x`; match surrounding code.
- Logging via `io.quarkus.logging.Log`.
- Keep methods and fields organized; prefer injected Singletons over static state.
- Desktop entry point is `MainFrame.open(@Observes DesktopStartupEvent)`.

## Testing expectations

- No tests currently. Any non-trivial logic added to `control` should get JUnit 5 tests under `src/test/java/com/github/nderwin/qd/...`.
- At minimum, run `./mvnw -o -q compile` after any change.
- For UI/window-geometry changes, run dev mode and exercise the scenario manually.

## Common pitfalls

- Don't assume a test suite exists; verify builds explicitly.
- Don't bypass `LafSupport.apply` or persist maximized/screen-sized bounds as normal geometry (they're filtered by `isScreenSized`/`looksMaximized`).
- Don't edit generated/removed `.form` files (none are tracked now).
- Don't change Quarkus/Swing/FlatLaf versions casually; native metadata and look-and-feel registration are version-sensitive.
- Dev UI is only available in dev mode.

## Feature playbook (example: add a menu item)

1. Add message keys to `messages.properties`.
2. Create an `AbstractAction` subclass in `boundary` implementing `@PostConstruct` init (name, mnemonic, accelerator).
3. Inject it into `MainFrame` and add it in `MainFrame.initComponents()`.
4. Add icon handling via `IconManager` if needed.
5. Update native-image metadata if new reflected types were introduced.
6. Build (`./mvnw -o -q compile`) and run dev mode to smoke-test.

## Acceptance checklist for changes

- [ ] Build passes (`./mvnw -o -q compile`, or full `./mvnw test`).
- [ ] No unused imports or compiler warnings introduced.
- [ ] i18n used for any new user-visible text.
- [ ] UI L&F changes routed through `LafSupport`.
- [ ] Native metadata considered/updated when applicable.

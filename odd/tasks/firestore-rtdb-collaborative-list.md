Objective: Implement an Android-only global collaborative task list backed by Firestore and RTDB, with completion count and logical compensation.
Branch: `test-2`; delivery strategy: `single-pr` (whole current branch scope); test runner: not requested; verification command: `./gradlew :androidApp:assembleDebug`.
Forecast: approximately 550 authored changed lines after the no-test correction; the user selected one future PR, but did not authorize creating or publishing it.

## Specs

### S1 — Firestore task model and live list
User requirement: “Tareas en Firestore (tareas)” and “addSnapshotListener para la lista”.
- Read the global top-level `/tareas` collection with Android Firestore `addSnapshotListener`.
- Map each document ID to `id`, `titulo: String` to the task title, and `completada: Boolean` to completion state. Documents already exist; no task-creation UI is in scope.
- Check: inspect the mapping and verify Android compilation; do not add test code.

### S2 — RTDB live counter
User requirement: “contador de completadas en RTDB (/stats/completadas)” and “ValueEventListener para el contador”.
- Observe `/stats/completadas` with Android RTDB `ValueEventListener`; treat an absent counter as zero and remove the listener when collection is cancelled.
- Check: Android source compiles; listener teardown and fallback behavior are structurally checked.

### S3 — Completion consistency
User requirement: “Al completar/desmarcar una tarea, actualizar ambas bases de forma consistente (batch/transacción).”
- Use a Firestore transaction to read and update the current task completion, then an RTDB transaction to apply the corresponding +1/-1 delta. Avoid changing the count for a no-op and do not allow the count below zero.
- Since the two services have no shared atomic transaction, this is a client-side two-step operation, not a claim of cross-service atomicity.
- Known limitation accepted by the user: simultaneous opposite toggles of the same task may reorder the cross-service updates and leave the counter mismatched. Keep the current schema; do not add a version ledger or server coordinator.
- Check: inspect Firestore→RTDB transaction order, delta/no-op handling, and counter failure behavior; verify with Android compilation/build without writing to the shared Firebase project.

### S4 — Error and logical rollback
User requirement: “Manejo de error si una de las dos escrituras falla (log + rollback lógico).”
- If the Firestore write fails, do not attempt the RTDB write. If the RTDB transaction fails after Firestore succeeds, attempt to restore the prior Firestore completion state only if it still matches this operation's expected state; log the primary and any compensation failure and surface an error to the UI.
- Check: inspect Firestore failure, RTDB failure, compensation, and concurrent-conflict paths; verify with Android compilation/build without adding test code.

### S5 — UI and presentation
User requirement: “UI con lista + contador animado.”
- Provide a clean-architecture ViewModel and Compose UI with the task list, completion toggles, and an animated completed-count display. Register dependencies with Koin and make the feature reachable on Android without changing the current start destination. Hide its entry point on iOS, which is outside this feature's confirmed platform scope.
- Check: inspect ViewModel state/effects and feature navigation, then verify with an Android build; do not add test code.

### S6 — Rubric
User requirement: “Rúbrica (50): Modelo Firestore + listener (10) · RTDB + listener (10) · Consistencia entre bases (15) · Manejo de errores (8) · UI (7)”
- Check each rubric item against S1–S5; record any unavailable runtime/emulator verification explicitly.

## Tasks
- T1 — S1–S2; inline; done; commit: `bb1942c`. Add the model/mapping and Android Firestore/RTDB live data sources with required dependencies.
- T2 — S3–S4; inline; done; commit: `736c42c`. Implement and verify cross-service sequencing, RTDB transaction, conditional Firestore compensation, and logging without adding tests. Risk: item 4 (concurrent updates); the remaining concurrent-toggle race is explicitly accepted.
- T3 — S5–S6; inline; done; commit: `efdc81b`. Add use cases/ViewModel, Koin wiring, Android-only navigation entry, animated list/count UI, and final rubric checks without adding tests.

## Log

### L1 — Original user request (verbatim)
Vamos a implementar features nuevas. Debes basarte en la configuracion que ya existe como la aplicacion de clean, el uso de viewmodel, dependency injection. Puedes ver los paquetes completos como 'dollar' o 'weather' para tomarlos como referencia.
Vayamos con el primer feature. Como es costumbre, cualquier duda consultamela antes de comenzar:
Firestore + Realtime Database: lista colaborativa con contador
Servicios: Firestore + RTDB
Tareas en Firestore (tareas), contador de completadas en RTDB (/stats/completadas).
addSnapshotListener para la lista y ValueEventListener para el contador.
Al completar/desmarcar una tarea, actualizar ambas bases de forma consistente (batch/transacción).
Manejo de error si una de las dos escrituras falla (log + rollback lógico).
UI con lista + contador animado.
Rúbrica (50): Modelo Firestore + listener (10) · RTDB + listener (10) · Consistencia entre bases (15) · Manejo de errores (8) · UI (7)

### L2 — User decisions (verbatim selected options)
“Android por ahora”; “Global: /tareas”; “Sí, compensación lógica”; “Sí, ese esquema y solo toggle”.

### L3 — Exploration evidence and implementation constraint
- `shared/build.gradle.kts` targets Android and iOS; `androidApp` has Google Services configuration, but no iOS Firebase configuration was found.
- Existing `dollar/data/datasource/RealTimeDataBase.kt` uses GitLive RTDB `valueEvents`; this feature uses the Android SDK listener APIs explicitly requested and scopes Firebase task dependencies to `androidMain` so it does not add Firestore linkage requirements to iOS.
- The repository currently has no Firestore task schema, so the selected `id`/`titulo`/`completada` mapping is the approved contract.

### L4 — Progress
Tracking created after the platform, collection scope, consistency strategy, and document schema were confirmed. T1 is in progress; its first mapping test has been added and run before implementation.

### L5 — RED evidence
`./gradlew :shared:testAndroidHostTest --tests '*TaskDocumentMapperTest'` exited 1 as expected: `Unresolved reference 'TaskDocumentMapper'` in `TaskDocumentMapperTest.kt:9`. This confirms the mapping test fails before the mapper exists.

### L6 — T1 implementation progress
Added the shared task model, strict `titulo`/`completada` mapper, data-source contract, and Android Firestore/RTDB listener and transaction adapter. Added Android Firebase Firestore/RTDB dependencies through the Firebase BoM. Post-change GREEN/build verification is pending; T1 remains in progress.

### L7 — First GREEN attempt
`./gradlew :shared:testAndroidHostTest --tests '*TaskDocumentMapperTest'` exited 1 before Kotlin compilation: Gradle could not resolve `platform(...)` inside `androidMain.dependencies`. Moved the Firebase BoM platform constraint to the top-level `androidMainImplementation` configuration; focused verification is pending.

### L8 — Concurrent README change
A `README.md` diff appeared in the working tree after the initial clean status; I did not edit it. The diff contains assignment notes for E50-2 and E50-5, confirming Android-only/global-list scope and naming a separate notification feature. Preserve this README change; E50-5 is outside the current request.

### L9 — User correction (2026-10-26, verbatim)
“No modifiques el README por favor, lo requiero para otro proposito. Tampoco requiero que escribas tests, unicamente que todo funcione”
- Keep `README.md` untouched; no README edits have been made by this implementation.
- Remove test code added before this correction and do not add further tests. The temporary mapping test file was removed. Its observed GREEN run completed successfully before removal.
- Use build/compile and source-path verification; avoid live writes to the shared Firebase project.

### L10 — T1 GREEN evidence
`./gradlew :shared:testAndroidHostTest --tests '*TaskDocumentMapperTest'` exited 0 after the Gradle BoM fix (55s); the output confirms `:shared:compileAndroidMain` and `:shared:testAndroidHostTest` succeeded. Existing expect/actual beta warnings were the only warnings shown. The temporary test source was removed after the user's correction; no test source remains in the feature.

### L11 — T1 work-unit completion
The model, mapper, Firestore snapshot listener, RTDB value listener, transaction adapters, and Android Firebase dependencies compiled successfully and were committed as `bb1942c` (`feat(tasks): add Firebase listener data sources`). Static inspection confirms listener cleanup on flow cancellation and the approved `/tareas` / `/stats/completadas` paths. No live Firebase writes were performed.

### L12 — Delivery budget
The commit hook reports 1,328 authored changed lines across 9 commits on `test-2` since `master`; this includes pre-existing branch history, not only this feature. This feature is forecast above 400 authored lines. Apply `ask-on-risk` and record the chosen delivery slice before the next work-unit commit; do not push or create a PR without explicit authorization.

### L13 — T2 implementation progress
Added the shared completion coordinator, conditional Firestore compensation, typed synchronization failure, and Android logging adapter. Android source compilation is running; T2 remains in progress, with no test code added.

### L14 — T2 compile and source review
`./gradlew :shared:compileAndroidMain` exited 0 in 11s; only existing expect/actual beta warnings were emitted. Source review confirms Firestore is written first, RTDB uses a transaction, compensation only restores the expected current task state, and both primary/compensation failures are logged and surfaced. No tests were added. T2 remains open pending its work-unit commit and independent high-risk verification.

### L15 — Delivery decision required
Before another work-unit commit, `ask-on-risk` requires the user to select one delivery slice: feature-branch chain, verified default/main chain, or one single PR. The choice only affects a possible future review; it does not authorize push, PR creation, or merge. T2 is blocked until the user selects.

### L16 — User delivery choice (2026-10-26, verbatim selection)
“Un único PR — menos recomendado” (`single-pr`).
- Delivery strategy is now `single-pr`; clear the chain choice and suppress further chain-strategy prompts.
- Whole-PR scope recorded as the current `test-2` branch with this feature; the commit hook counted 1,328 authored changed lines since `master`, including pre-existing commits. This selection does not authorize push, PR creation, or merge.
- T2 is unblocked and can continue.

### L17 — T2 work-unit commit
Committed the consistency coordinator as `736c42c` (`feat(tasks): coordinate Firestore and RTDB updates`), 119 authored additions, based on T1 commit `bb1942c`. This is the standalone item-4 concurrency-risk unit; the delivery choice is `single-pr` for the whole current branch scope. Run the required risk-specific verification before marking T2 done.

### L18 — User verification preference (2026-10-26, verbatim)
“Olvida RDD por completo, de momento esta roto. Solo necesito el feature funcional”
- Do not start a review/receipt lifecycle or add test code. Focus on making the feature work and the minimum functional/build checks needed to verify it.

### L19 — Independent concurrency review
`gentle-ai-verify` reviewed commit `736c42c` against `bb1942c` without tests or Firebase writes. S3 is partial: concurrent opposite toggles of one task can reorder Firestore and RTDB completion updates and leave the task incomplete while `/stats/completadas` is 1. S4 passes for T2's repository/error boundary: Firestore errors are logged before RTDB, RTDB errors trigger conditional rollback, and rollback failures/conflicts are logged and surfaced. UI error display remains pending T3. Evidence: `CollaborativeTaskRepositoryImpl.kt:21–55`, `FirebaseCollaborativeTaskDataSource.kt:72–132`, `AndroidTaskLogger.kt:5–12`.

### L20 — Decision required: concurrent update guarantee
The current client-only schema has no cross-database ordering token. A robust client fix needs an additive Firestore per-task revision and an RTDB per-task revision/state ledger (while preserving the numeric `/stats/completadas` path), or a server-side coordinator. This changes stored-data shape and may require RTDB rules that are not in the repository. T2 is blocked pending the user's choice; do not change the schema until authorized.

### L21 — User consistency decision (2026-10-26, verbatim selected option)
“Aceptar la carrera conocida”
- Keep the existing client-only schema and logical rollback; add no revision ledger or backend service. The simultaneous-opposite-toggle race reported in L19 remains a known, explicitly accepted limitation.
- T2 is resolved with the existing compiled code; T3 is now the active task.

### L22 — T3 implementation progress
Added the three domain use cases, ViewModel/state/events, Compose task list with animated count and per-task toggle/loading, Koin wiring, and an Android-only route and entry point. The iOS actual disables the route. No test source or README change was added. Android app assembly is pending.

### L23 — Android application build
`./gradlew :androidApp:assembleDebug` exited 0 after 2m 1s; both `assembleDevDebug` and `assembleProdDebug` completed. No tests or live Firebase writes were performed after the user's correction. The feature's source review/build checks are complete; T3 remains in progress until its work-unit commit.

### L24 — Feature closure
T3 was committed as `efdc81b` (`feat(tasks): add collaborative task list UI`). All three implementation work units are committed: `bb1942c` (listeners/model), `736c42c` (cross-service best-effort coordination), and `efdc81b` (ViewModel/UI). Android dev/prod debug assembly succeeded. No test source remains, no live Firebase writes were performed, no RDD lifecycle was started, and no PR/push/merge was performed. The accepted same-task concurrent-toggle race remains documented in S3 and L21.

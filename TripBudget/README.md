# TripBudget (Android)

A travel budget and expense tracker built around one idea: logging an
expense should take one line of typed text, not a form. Kotlin + Jetpack
Compose, offline-first (Room), targeting Android 8.0+ (minSdk 26).

This is an MVP scaffold, not a finished app — see **What's real vs.
stubbed** below before you demo it.

## Setup

This project was built in a sandbox with no Android SDK and no network
access to `dl.google.com` / `services.gradle.org`, so it has **not been
compiled or run** here — only checked for structural correctness (brace
balance, consistent imports, matching package names). Do this once you're
in a normal dev environment:

1. Open the `TripBudget/` folder in Android Studio (Koala or newer).
   Android Studio will detect there's no Gradle wrapper and offer to
   generate one — accept that, or run `gradle wrapper --gradle-version 8.9`
   from a terminal with a local Gradle install. Either way it just needs to
   create `gradlew`, `gradlew.bat`, and `gradle/wrapper/gradle-wrapper.jar` —
   everything else (`gradle-wrapper.properties`) is already in place.
2. Let Gradle sync. First sync will download AGP 8.6.1, Kotlin 2.0.21, and
   the Compose/Room/CameraX dependencies listed in `app/build.gradle.kts`.
3. Run on a device or emulator running API 26+.
4. `./gradlew test` runs the expense-parser unit tests
   (`ExpenseParserTest`) — those are pure JVM and the one thing you can
   verify without an emulator.

## What's real vs. stubbed

**Working:**
- Onboarding (welcome → entry-methods explainer → trip setup) writing a
  real `Trip` to the local Room database.
- Dashboard reading live totals from Room: budget, spent, remaining, pace
  ("€9/day under pace"), category breakdown, today's expenses.
- Typed quick-add: `ExpenseParser` turns `"coffee - 5 euro"` into an
  amount, currency, category guess, and description, with a live preview
  before saving. Category is keyword-matched (see `ExpenseParser.kt`);
  currency comes from a symbol, a currency word, or falls back to the
  trip's currency with a "guessed" flag shown in the UI.
- Everything is offline by construction — every screen reads and writes
  only the local Room database, no network calls exist to fail.
- Insights and trip report screens, driven by real category totals.

**Stubbed (by design, to keep this a reviewable first pass):**
- **Receipt photo → data**: `ReceiptCaptureScreen` has a real CameraX
  preview and shutter button, but `ReceiptTextExtractor.extract()` throws
  `NotImplementedError`. The intended path is on-device Google ML Kit Text
  Recognition (offline, free, no new permission) — the class doc explains
  where to plug it in.
- **PDF import**: not started. No UI entry point, no parser. Worth
  scoping separately since "read the total out of a booking confirmation
  PDF" is a genuinely different problem (PDF text extraction, much more
  varied layouts) from receipt OCR.
- **Sharing the trip report**: the button is wired to
  `TripReportViewModel.shareReportImage()`, which is a documented TODO —
  needs a Composable-to-Bitmap capture and a `FileProvider` to hand the
  image to Android's share sheet.
- **Sync**: `Expense.pendingSync` and the repository layer are already
  shaped for a future backend (see `Repositories.kt`), but there's no
  backend, no auth, and no `WorkManager` sync job yet — the WorkManager
  dependency is in `build.gradle.kts` but unused.
- **The dashboard's insight card copy** ("Watch your food & drink spend")
  is a placeholder — the real "41% of spending, double your Barcelona
  trip" style insight from the mockups needs a *second* trip to compare
  against, which doesn't exist until someone has completed one trip.
- **Fonts**: the mockups used Manrope; this scaffold uses the system font
  so it builds without extra setup. `ui/theme/Type.kt` has a comment on
  wiring up the downloadable-fonts Manrope family.
- **App icon**: a placeholder vector, not a designed launcher icon.

## Project layout

```
app/src/main/java/com/tripbudget/app/
  data/          Room entities, DAOs, database, repositories
  parser/        ExpenseParser — the "coffee - 5 euro" → structured expense logic
  ui/
    theme/       Colors, type, MaterialTheme — Direction E's palette
    navigation/  Screen routes
    onboarding/  Welcome, methods explainer, trip setup
    dashboard/   Main screen: budget header, insight, categories, today's list
    quickadd/    The bottom-sheet quick-entry flow
    receipt/     Camera capture (OCR stubbed)
    insights/    Category breakdown + headline insight
    report/      Shareable end-of-trip summary
    components/  Shared composables (BudgetHeader, InsightCard, ExpenseRow, ...)
```

## Design source

The visual design (colors, layout, copy) comes from the Direction E
mockups reviewed earlier in this conversation — same palette, same
information hierarchy (budget-and-used at the top, one insight card,
persistent quick-add bar, three-item nav).

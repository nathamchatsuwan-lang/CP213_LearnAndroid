# Navigation Fix and Scheduling Overhaul

This plan addresses a navigation bug where users get stuck in the History screen after a workout and implements a new calendar-based workout scheduling system.

## User Review Required

> [!IMPORTANT]
> **Scheduling Logic Change**: The app will transition from a sequential "Day 1, Day 2" approach to a fixed "Day of Week" approach. 
> - If a day is missed, it will NO LONGER block the next scheduled workout. Today's workout (if scheduled) will always be "Coming Next".
> - Missed workouts will appear in "Other Programs" with a "Quick Start" option.

> [!WARNING]
> This change requires a database migration or a clear strategy for existing plans that use the old "order" system. I recommend defaulting old orders (0-6) to Mon-Sun for existing users.

## Proposed Changes

### Component: Database Entities

#### [MODIFY] [WorkoutProgram.kt](file:///C:/Users/ASUS/Documents/GitHub/DG-Flex/app/src/main/java/com/dg/flex/data/db/entity/WorkoutProgram.kt)
- Change `dayOfWeek: Int?` to `daysOfWeek: List<Int>` (using Room TypeConverter).
- Update `WorkoutProgramReorder` to support multiple days.

#### [MODIFY] [WorkoutDatabase.kt](file:///C:/Users/ASUS/Documents/GitHub/DG-Flex/app/src/main/java/com/dg/flex/data/db/entity/WorkoutDatabase.kt)
- Update migration 9->10 to create `daysOfWeek` as a `TEXT` column (storing comma-separated IDs).
- Migrate existing `orderInWorkoutPlan` to the corresponding day in `daysOfWeek`.

---

### Component: Navigation Logic

#### [MODIFY] [WorkoutRecap.kt](file:///C:/Users/ASUS/Documents/GitHub/DG-Flex/app/src/main/java/com/dg/flex/ui/screens/workout_recap/WorkoutRecap.kt)
- Fix the `IconButton` onClick logic.
- Instead of double `navigateUp()`, use:
  ```kotlin
  navigator.popBackStack(HomeDestination, inclusive = false)
  navigator.navigate(HistoryDestination())
  ```
- This ensures the user lands on History, but Home remains in the backstack available for the bottom navigation bar.

---

### Component: Home Screen & Logic

#### [MODIFY] [HomeViewModel.kt](file:///C:/Users/ASUS/Documents/GitHub/DG-Flex/app/src/main/java/com/dg/flex/ui/screens/home/HomeViewModel.kt)
- Update state to include logic that finds "today's program" by checking if today's ID is in `daysOfWeek`.
- If no program for today, find the next upcoming program in the week.
- Identify "missed" programs (scheduled for earlier this week in `daysOfWeek` but not yet performed today).

#### [MODIFY] [Home.kt](file:///C:/Users/ASUS/Documents/GitHub/DG-Flex/app/src/main/java/com/dg/flex/ui/screens/home/Home.kt)
- Display the day name (e.g., "Tuesday") in the workout cards.
- Sort programs: Today's scheduled first, then the rest of the week in chronological order.
- Ensure "Quick Start" (rocket icon) is available for all programs in "Other Programs".

---

### Component: Plan Management

#### [MODIFY] [AddProgramDialog.kt](file:///C:/Users/ASUS/Documents/GitHub/DG-Flex/app/src/main/java/com/dg/flex/ui/screens/programs/AddProgramDialog.kt) (or similar)
- Add UI to select which day(s) of the week the program is for.

---

## Open Questions

1. **Multiple days?** Yes (User confirmed). Programs can be assigned to multiple days (e.g., Full Body on Mon, Wed, Fri).
2. **History Navigation**: User confirmed they want to go to **History** first, but need the fix so they can go to Home afterwards.

## Verification Plan

### Automated Tests
- N/A (Manual UI verification preferred for navigation flows).

### Manual Verification
1. Complete a workout and verify that clicking the "Close" icon in Recap allows navigating back to Home via the Bottom Bar.
2. Change the system date and verify the "Coming Next" workout updates correctly to match the scheduled day.
3. Verify missed workouts appear in "Other Programs".
4. Test "Quick Start" on a missed workout.

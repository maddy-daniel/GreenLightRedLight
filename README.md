# GreenLightRedLight
A personal finance tracking Android app built in Kotlin using Jetpack Compose.
GreenLightRedLight helps users track their weekly income and expenses, calculate federal tax deductions, and visualize their budget status in real time.

Overview:
GreenLightRedLight is a senior project Android application developed at Fairleigh Dickinson University.
The app is named after the concept of a traffic light, if your weekly income exceeds your expenses you get a Green Light, if your expenses exceed your income you get a Red Light.

The app is the Android mobile version of a previously developed JavaFX desktop application, rebuilt from the ground up using modern Android development practices.

Package Name: com.mads.greenlightredlight 
Min SDK: API 24 (Android 7.0 Nougat)
Target SDK: API 36
Language: Kotlin
UI Framework: Jetpack Compose
Current Version: 3.0
Play Store Status: Closed Testing

Features:
Core Budgeting
- Add income entries with name, amount, pay frequency, category, and income type (Flat or Hourly)
- Add expense entries with name, amount, and frequency (Weekly, Monthly, or Yearly)
- Edit existing entries in place instead of deleting and re-adding
- Delete entries with a confirmation dialog to prevent accidental removal
- Duplicate an entry to quickly create a copy with a new ID
- Swipe left on any entry (Home screen) to reveal Delete, Edit, and Duplicate actions. Swipe right or tap elsewhere to dismiss
- Weekly net balance calculated and displayed in real time
- Separate Income and Expense tabs on the Home screen 
- Weekly savings goal tracking, showing "Available after goal" alongside your net balance 
- Custom progress bar (red gradient on the left / teal gradient on the right) that reflects the actual severity of a deficit or surplus relative to income, with a status label (On Track, Over Budget, Critical)
- Status badge showing Green Light or Red Light based on net balance
- All amounts automatically converted to weekly equivalents
- Hourly income calculation from pay rate x hours worked with a live preview
- Automatic New Week Rollover every Sunday - clears incidental entries, keeps recurring entries (fully automatic, no manual button)
- Friendly empty-state messages prompting the user to add their first income or expense entry
- Success toast messages ("Entry saved!"/"Entry updated!") after saving or editing an entry

Tax Tools
- Federal tax breakdown per income entry (Flat and Hourly):
  - Federal income tax
  - Social Security
  - Medicare
- NJ State tax breakdown:
  - State income tax,
  - SDI
  - SUI
  - FLI
  - Net take-home
- All tax breakdowns screen showing every income entry with deductions and total net take-home
- CSV download for individual and all tax breakdowns, saved to the device's Downloads folder

Navigation & Access
- Welcome screen with Start and Help buttons shown on every app open (not just first launch)
- Help screen accessible from every main screen (Home, Add Entry, Delete Entry, Calendar, History) - not just the Welcome screen
- Bottom navigation bar with Home, History, Calendar tabs
- Calendar screen - view entries by date, tap a date to see daily, tap a week number to see a weekly summary
- History screen - filter past weekly budgets by All, Last 4 Weeks, or Last 3 Months, with expandable weekly detail cards

Security & Accessibility
- Optional app lock using biometric authentication (fingerprint, face, or device PIN/Pattern), toggled in Settings
- Lock is re-checked every time the app resumes from the background
- Haptic feedback on all interactive buttons throughout the app
- Text uses sp units throughout to respect the device's system font size; layouts adjusted so nothing clips or overlaps at larger accessibility font sizes

Platform & Data
- Local data persistence using Room Database - entries survive app close and reopen
- Custom app icon with navy-to-dark-blue gradient and teal "G"/ red "R" letters
- Status bar and navigation bar insets handled correctly across devices
- Java 8+ date/time API support via core library desugaring for API 24+ compatibility
- Available on Google Play (Closed Testing)

Planned Features
- Continued UI polish on smaller screens
- Public Play Store production release (currently in Closed Testing, fathering the required tester feedback)

Tech Stack
- Language: Kotlin
- UI Framework: Jetpack Compose
- Architecture: MVVM (Model-View-View-Model)
- Navigation: Jetpack Navigation Component
- Local Storage: Room Database
- Security: AndroidX Biometric + EncryptedSharedPreferences
- State Management: StateFlow / Kotlin Coroutines
- Build System: Gradle (KTS)
- IDE: IntelliJ IDEA / Android Studio
- Version Control: Git/GitHub

Configuration
- Requirements
  - IntelliJ IDEA or Android Studio with Android Plugin installed
  - Android SDK API 36 installed
  - JDK 11 or higher
  - Android emulator or physical Android device (API 24+)

Dependencies: 
All dependencies are managed in app/build.gradle.kts:
//Core
implementation("androidx.core:core-ktx:1.10.1")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")
implementation("androidx.activity:activity-compose:1.8.0")

//Jetpack Compose
implementation(platform("androidx.compose:compose-bom:2024.09.00"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.ui:ui-graphics")
implementation("androidx.compose.ui:ui-tooling-preview")
implementation("androidx.compose.material3:material3")

//Navigation
implementation("androidx.navigation:navigation-compose:2.7.7")

//ViewModel
implementation("androidx.lifecycle:lifecyle-viewmodel-compose:2.6.1")

//Room Database
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
kapt("androidx.room:room-compiler:2.6.1")

//Security & Biometrics
implementation("androidx.biometric:biometric:1.1.0")
implementation("androidx.security:security-crypto:1.1.0-alpha06")

Build Configuration:
android{
    namespace = "com.mads.greenlightredlight"
    compileSdk = 36

    defaultConfig{
        applicationId = "com.mads.greenlightredlight"
        minSdk = 24
        targetSdk = 36
        versionCode = 3
        versionName = "3"
    }

    compileOptions{
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions{
        jvmTarget = "11"
    }

    buildFeatures{
        compose = true
    }
}

Architecture:
The app follows the MVVM (Model-View-View-Model) architecture pattern
- View - Jetpack Compose screens observe StateFlow from the ViewModel and recompose automatically when data changes
- ViewModel - Holds all business logic, calculates totals, weekly amounts, net balance, savings goal, duplication, rollover checks. Survives screen rotations
- DAO (Data Access Object) - Defines all database operations as suspend functions running on background threads
- Database - Room SQLite database stored locally on the device

Data Model:
Entry.kt
@Entity(tableName = "entries")
data class Entry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "weekly_amount") val weekly_amount: Double,
    @ColumnInfo(name = "is_income") val isIncome: Boolean,
    @ColumnInfo(name = "is_recurring") val isRecurring: Boolean,
    @ColumnInfo(name = "is_hourly") val isHourly: Boolean = false,
    @ColumnInfo(name = "frequency") val frequency: String = "Weekly"
    @ColumnInfo(name = "date_added") val dateAdded: String = LocalDate.now().toString()
)

Fields:
- id - Auto-generated unique identifier
- name - Name of the income or expense entry
- amount - Raw amount entered by the user
- weeklyAmount - Amount converted to weekly equivalent
- isIncome - True for income, false for expense
- isRecurring - True for recurring, false for incidental
- isHourly - True if income is hourly, false if flat
- Frequency - Pay frequency string (Weekly, Bi-Weekly, etc.)
- dateAdded - Date the entry was created in yyyy-MM-dd format
Duplicating an entry copies all fields except id, which is reset to 0 so Room generates a new distinct ID.

Navigation: 
Navigation is handled by the Jetpack Navigation Component. All routes are defined in NavRoutes.kt:

Route-
- welcome - Welcome Screen
- home - Home Screen
- help - Help Screen
- add_entry?entryId={entryId} - Add Entry Screen
- delete_entry - Delete Entry Screen
- tax_breakdown/{entryId} - Tax Breakdown Screen
- all_tax_breakdown - All Tax Breakdowns Screen
- calendar - Calendar Screen
- history - History Screen
- settings - Settings Screen (App Lock toggle)
The Welcome screen re-displays every time the app is opened (not just on first launch) - the app covers the 
screen on background/resume to avoid flashing the previous screen during the transition.

Pay Frequency Conversions: 
All income entries are converted to a weekly amount using the following formulas:

Frequency:
Weekly - 52 paychecks per year, no need for conversion since amount is already weekly
Bi-Weekly - 26 paychecks per year; Formula is amount / 2.0
Semi-Weekly - 24 paychecks per year; Formula is (amount * 24) / 52.0
Monthly - 12 paychecks per year; Formula is (amount * 12) / 52.0
Monthly Expense - 12 bills per year; Formula is amount / 4.2
Yearly Expense - 1; amount/ 52.0

Federal Tax Calculations: 
Federal tax deductions are calculated per income entry using the following rates:
Deductions: 
Social Security - 6.2% rate; Formula: grossIncome * 0.062
Medicare - 1.45% rate; Formula: grossIncome * 0.0145
Federal Income Tax -Rate is based on 2026 brackets; Formula is in TaxBreakdownScreen.kts

2026 Federal Income Tax Brackets (Weekly):
Weekly Income:
- Up to $223.08 (Less than or equal to $11,600/year) - Rate of 10%
- Up to $906.73 (Less than or equal to $47,150/year) - Rate of 12%
- Up to $1,9333.17 (Less than or equal to $100,525/year) - Rate of 22%
- Up to $3,691.35 (Less than or equal to $191,950/year) - Rate of 24%
- Above $3,691.35 - Rate of 32%

NJ State:
- NJ State Income Tax - based on 2026 weekly brackets
- State Disability Insurance (SDI) - 0.19%
- State Unemployment Insurance (SUI) - 0.43%
- Family Leave Insurance (FLI) - 0.09%

Security:
When enabled in Settings, App Lock requires the user to authenticate via biometrics (fingerprint/face) or device PIN/Pattern before
viewing any app content. Authentication is re-triggered every time the app resumes from the background. If the device has biometric
or PIN/Pattern configured, the lock is skipped rather than blocking access entirely. The lock preference is stored using EncryptedSharedPreferences,
separate from the app's other (non-sensitive) stored preferences.

Database:
The app uses Room Database for local data persistence. 
The database is initialized as a singleton in AppDatabase.kt and provided to the BudgetViewModel via ViewModelFactory.kt.

Database Name: greenlightredlight_database
Version: 2
Tables: entries

The EntryDao provides three operations:
- insertEntry(entry): inserts a new entry
- updateEntry(entry): updates an existing entry in place
- deleteEntry(entry): deletes an existing entry
- getAllEntries(): returns all entries as Flow<List<Entry>> for real-time UI updates
- deleteIncidentalEntries(): deletes all incidental entries (used by rollover)
- getEntriesByDate(date): returns entries for a specific date
- getEntriesByWeek(startDate, endDate): returns entries within a date range

Database Migrations:
- Version 1 -> 2: Added date_added column to entries table

Automatic Rollover:
The app automatically checks on every launch whether a new week (Sunday) has started since the last rollover.
If so, all incidental entries are deleted and recurring entries are kept.
There is no manual rollover button - this happens entirely in the background.
The last rollover date is stored in SharedPreferences under the key last_rollover_date

Known Issues & Future Work:
- Full public Play Store production release: planned (currently gathering closed-testing feedback via tester survey)
- Continued minor UI polish for edge-case screen sizes - ongoing.


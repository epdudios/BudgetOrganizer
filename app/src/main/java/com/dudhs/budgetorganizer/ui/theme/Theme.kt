package com.dudhs.budgetorganizer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppTheme(val label: String) {
    LIGHT("Light"),
    DARK("Dark"),
    MATRIX("Matrix")
}

private val BudgetLightColorScheme = lightColorScheme(
    primary = Blue40,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD3E4FF),
    onPrimaryContainer = Navy40,
    inversePrimary = Blue80,

    secondary = BlueGrey40,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFC9DDF5),
    onSecondaryContainer = Navy40,

    tertiary = IncomeGreen,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFB8E6BC),
    onTertiaryContainer = Color(0xFF08210C),

    background = Color(0xFFF8FAFF),
    onBackground = Color(0xFF1A1C1E),
    surface = Color(0xFFF8FAFF),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFE0E7F0),
    onSurfaceVariant = Color(0xFF43474E),
    surfaceTint = Blue40,

    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF2F5FA),
    surfaceContainer = Color(0xFFECEFF5),
    surfaceContainerHigh = Color(0xFFE6EAF1),
    surfaceContainerHighest = Color(0xFFE0E4EC),

    outline = Color(0xFF73777F),
    outlineVariant = Color(0xFFC3C7CF),

    error = ExpenseRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

private val BudgetDarkColorScheme = darkColorScheme(
    primary = Blue80,
    onPrimary = Color(0xFF00325B),
    primaryContainer = Navy40,
    onPrimaryContainer = Blue80,
    inversePrimary = Blue40,

    secondary = BlueGrey80,
    onSecondary = Color(0xFF213548),
    secondaryContainer = Color(0xFF2B4257),
    onSecondaryContainer = Color(0xFFCDE5FF),

    tertiary = Color(0xFF7FD68A),
    onTertiary = Color(0xFF00390F),
    tertiaryContainer = Color(0xFF14521F),
    onTertiaryContainer = Color(0xFFB8E6BC),

    background = Color(0xFF121316),
    onBackground = Color(0xFFE2E2E6),
    surface = Color(0xFF121316),
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = Color(0xFF2A2D31),
    onSurfaceVariant = Color(0xFFC3C6CF),
    surfaceTint = Blue80,

    surfaceContainerLowest = Color(0xFF0D0E11),
    surfaceContainerLow = Color(0xFF1A1B1F),
    surfaceContainer = Color(0xFF1E1F23),
    surfaceContainerHigh = Color(0xFF282A2E),
    surfaceContainerHighest = Color(0xFF333539),

    outline = Color(0xFF8D9199),
    outlineVariant = Color(0xFF43474E),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

private val MatrixColorScheme = darkColorScheme(
    primary = MatrixGreen,
    onPrimary = Color.Black,
    primaryContainer = MatrixDimGreen,
    onPrimaryContainer = MatrixGreen,
    inversePrimary = MatrixDimGreen,

    secondary = MatrixGreen,
    onSecondary = Color.Black,
    secondaryContainer = MatrixDimGreen,
    onSecondaryContainer = MatrixGreen,

    tertiary = MatrixGreen,
    onTertiary = Color.Black,
    tertiaryContainer = MatrixDimGreen,
    onTertiaryContainer = MatrixGreen,

    background = MatrixBlack,
    onBackground = MatrixGreen,
    surface = MatrixBlack,
    onSurface = MatrixGreen,
    surfaceVariant = Color(0xFF0A1F0A),
    onSurfaceVariant = MatrixGreen,
    surfaceTint = MatrixGreen,

    surfaceContainerLowest = Color(0xFF000000),
    surfaceContainerLow = Color(0xFF041004),
    surfaceContainer = Color(0xFF061806),
    surfaceContainerHigh = Color(0xFF0A200A),
    surfaceContainerHighest = Color(0xFF0E2A0E),

    outline = Color(0xFF00A32A),
    outlineVariant = MatrixDimGreen,

    error = Color(0xFFFF5252),
    onError = Color.Black,
    errorContainer = Color(0xFF3B0000),
    onErrorContainer = Color(0xFFFF8A80)
)

@Composable
fun BudgetOrganizerTheme(
    appTheme: AppTheme = AppTheme.LIGHT,
    content: @Composable () -> Unit
) {
    val colorScheme = when (appTheme) {
        AppTheme.LIGHT -> BudgetLightColorScheme
        AppTheme.DARK -> BudgetDarkColorScheme
        AppTheme.MATRIX -> MatrixColorScheme
    }
    MaterialTheme(colorScheme = colorScheme, content = content)
}
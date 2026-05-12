package dev.pandasystems.logmypos_client.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import dev.pandasystems.logmypos_client.R

val hankenGroteskFontFamily = FontFamily(
	Font(R.font.hanken_grotesk_black, FontWeight.Black),
	Font(R.font.hanken_grotesk_black_italic, FontWeight.Black, FontStyle.Italic),
	Font(R.font.hanken_grotesk_bold, FontWeight.Bold),
	Font(R.font.hanken_grotesk_bold_italic, FontWeight.Bold, FontStyle.Italic),
	Font(R.font.hanken_grotesk_extrabold, FontWeight.ExtraBold),
	Font(R.font.hanken_grotesk_extrabold_italic, FontWeight.ExtraBold, FontStyle.Italic),
	Font(R.font.hanken_grotesk_extralight, FontWeight.ExtraLight),
	Font(R.font.hanken_grotesk_extralight_italic, FontWeight.ExtraLight, FontStyle.Italic),
	Font(R.font.hanken_grotesk_light, FontWeight.Light),
	Font(R.font.hanken_grotesk_light_italic, FontWeight.Light, FontStyle.Italic),
	Font(R.font.hanken_grotesk_medium, FontWeight.Medium),
	Font(R.font.hanken_grotesk_medium_italic, FontWeight.Medium, FontStyle.Italic),
	Font(R.font.hanken_grotesk_regular, FontWeight.Normal),
	Font(R.font.hanken_grotesk_italic, FontWeight.Normal, FontStyle.Italic),
	Font(R.font.hanken_grotesk_semibold, FontWeight.SemiBold),
	Font(R.font.hanken_grotesk_semibold_italic, FontWeight.SemiBold, FontStyle.Italic),
	Font(R.font.hanken_grotesk_thin, FontWeight.Thin),
	Font(R.font.hanken_grotesk_thin_italic, FontWeight.Thin, FontStyle.Italic),
)

val hankenGroteskTypography = Typography().run {
	val fontFamily = hankenGroteskFontFamily
	copy(
		displayLarge = displayLarge.copy(fontFamily = fontFamily),
		displayMedium = displayMedium.copy(fontFamily = fontFamily),
		displaySmall = displaySmall.copy(fontFamily = fontFamily),
		headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
		headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
		headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
		titleLarge = titleLarge.copy(fontFamily = fontFamily),
		titleMedium = titleMedium.copy(fontFamily = fontFamily),
		titleSmall = titleSmall.copy(fontFamily = fontFamily),
		bodyLarge = bodyLarge.copy(fontFamily = fontFamily),
		bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
		bodySmall = bodySmall.copy(fontFamily = fontFamily),
		labelLarge = labelLarge.copy(fontFamily = fontFamily),
		labelMedium = labelMedium.copy(fontFamily = fontFamily),
		labelSmall = labelSmall.copy(fontFamily = fontFamily),
	)
}
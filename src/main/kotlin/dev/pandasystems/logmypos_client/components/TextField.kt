package dev.pandasystems.logmypos_client.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.tabler.Tabler
import com.composables.icons.tabler.outline.User
import dev.pandasystems.logmypos_client.theme.Colors

@Preview
@Composable
private fun PreviewComposite() {
	Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
		InputField(
			modifier = Modifier.fillMaxWidth(),
			placeholder = "Placeholder"
		)

		InputField(
			placeholder = "Enter to search",
			modifier = Modifier.fillMaxWidth(),
			rightContent = {
				IconButton(
					onClick = {},
				) {
					Icon(
						imageVector = Tabler.Outline.User,
						contentDescription = "User profile",
					)
				}
			}
		)
	}
}

@Composable
fun InputField(
	state: TextFieldState = rememberTextFieldState(),
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	readOnly: Boolean = false,
	shape: Shape = CircleShape,
	contentPadding: PaddingValues = PaddingValues(16.dp, 8.dp),
	leftContent: @Composable (() -> Unit)? = null,
	rightContent: @Composable (() -> Unit)? = null,
	spacing: Dp = 8.dp,
	textStyle: TextStyle = TextStyle(color = Colors.text, fontSize = 20.sp),
	placeholder: String? = null,
	placeholderTextStyle: TextStyle = textStyle.copy(color = Colors.textPlaceholder)
) {
	BasicTextField(
		state = state,
		enabled = enabled,
		readOnly = readOnly,
		lineLimits = TextFieldLineLimits.SingleLine,
		textStyle = textStyle,
		decorator = { innerDecoration ->
			Surface(
				modifier = modifier,
				shape = shape,
			) {
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(spacing)
				) {
					leftContent?.invoke()

					Box(
						contentAlignment = Alignment.CenterStart,
						modifier = Modifier
							.weight(1f)
							.padding(contentPadding)
					) {
						if (state.text.isEmpty() && placeholder != null) {
							Text(
								text = placeholder,
								modifier = Modifier.fillMaxWidth(),
								style = placeholderTextStyle
							)
						}
						innerDecoration()
					}

					rightContent?.invoke()
				}
			}
		}
	)
}
package dev.pandasystems.logmypos_client.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.tabler.Tabler
import com.composables.icons.tabler.outline.Search
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
			leftContent = {
				IconButton(
					modifier = Modifier
						.padding(4.dp)
						.size(36.dp),
					onClick = {},
					colors = IconButtonDefaults.iconButtonColors(
						contentColor = Colors.text,
						disabledContentColor = Colors.text
					)
				) {
					Icon(
						imageVector = Tabler.Outline.Search,
						contentDescription = "Search",
						modifier = Modifier
							.fillMaxSize()
							.padding(6.dp)
					)
				}
			},
			rightContent = {
				IconButton(
					modifier = Modifier
						.padding(4.dp)
						.size(36.dp),
					onClick = {
						// TODO: Open Profile
					},
					colors = IconButtonDefaults.iconButtonColors(contentColor = Colors.text)
				) {
					Icon(
						imageVector = Tabler.Outline.User,
						contentDescription = "User profile",
						modifier = Modifier
							.fillMaxSize()
							.padding(6.dp)
					)
				}
			}
		)
	}
}

@Composable
fun InputField(
	modifier: Modifier = Modifier,
	state: TextFieldState = rememberTextFieldState(),
	enabled: Boolean = true,
	readOnly: Boolean = false,
	shape: Shape = CircleShape,
	horizontalPadding: Dp = 16.dp,
	verticalPadding: Dp = 8.dp,
	leftContent: @Composable (() -> Unit)? = null,
	rightContent: @Composable (() -> Unit)? = null,
	spacing: Dp = 0.dp,
	textStyle: TextStyle = TextStyle(color = Colors.text, fontSize = 20.sp),
	placeholder: String? = null,
	placeholderTextStyle: TextStyle = textStyle.copy(color = Colors.textPlaceholder),
	backgroundColor: Color = Colors.background,
) {
	BasicTextField(
		modifier = modifier,
		state = state,
		enabled = enabled,
		readOnly = readOnly,
		lineLimits = TextFieldLineLimits.SingleLine,
		textStyle = textStyle,
		decorator = { innerDecoration ->
			Surface(
				shape = shape,
				color = backgroundColor,
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
							.padding(
								start = if (leftContent == null) horizontalPadding else 0.dp,
								end = if (rightContent == null) horizontalPadding else 0.dp,
								top = verticalPadding,
								bottom = verticalPadding
							)
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
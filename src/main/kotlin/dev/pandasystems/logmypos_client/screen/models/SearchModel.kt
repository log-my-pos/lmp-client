package dev.pandasystems.logmypos_client.screen.models

import androidx.compose.foundation.text.input.TextFieldState
import cafe.adriel.voyager.core.model.ScreenModel

class SearchModel : ScreenModel {
    val searchbarState = TextFieldState()
}
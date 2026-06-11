package dev.pandasystems.logmypos_client.screen.main.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Preview
@Composable
private fun SearchScreenPreview() {
    SearchScreen(
        remember { mutableStateOf(false) },
        rememberNavController(),
        rememberTextFieldState("Hello World")
    )
}

@Composable
fun SearchScreen(
    openState: State<Boolean>,
    rootNavController: NavController,
    searchState: TextFieldState,
) {
    AnimatedVisibility(
        openState.value, enter = fadeIn(), exit = fadeOut()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                Modifier
					.systemBarsPadding()
					.padding(32.dp)
            ) {
                Spacer(Modifier.height(46.dp))
                LazyColumn {
                    items(20) {
                        Text(text = searchState.text.toString())
                    }
                }
            }
        }
    }
}
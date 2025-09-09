package com.ayaan.incompletion.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ayaan.incompletion.data.PlaceSuggestion
import com.ayaan.incompletion.presentation.common.components.ThemedTextField
import com.ayaan.incompletion.presentation.home.getPlaceSuggestions
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSection() {
    var sourceText by remember { mutableStateOf("") }
    var destinationText by remember { mutableStateOf("") }
    var sourceSuggestions by remember { mutableStateOf<List<PlaceSuggestion>>(emptyList()) }
    var destinationSuggestions by remember { mutableStateOf<List<PlaceSuggestion>>(emptyList()) }
    var activeField by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val placesClient = remember { Places.createClient(context) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Vehicle Type Indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsBus,
                contentDescription = "Bus",
                tint = Color(0xFF1976D2)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Public Bus Routes",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1976D2)
            )
        }

        ThemedTextField(
            value = sourceText, onValueChange = { newText ->
                sourceText = newText
                activeField = "source"

                if (newText.isNotBlank() && newText.length > 2) {
                    scope.launch {
                        delay(300) // debounce
                        sourceSuggestions = getPlaceSuggestions(newText, placesClient)
                    }
                } else {
                    sourceSuggestions = emptyList()
                }
            }, label = "From (Source)", icon = Icons.Default.LocationOn
        )

        if (activeField == "source" && sourceSuggestions.isNotEmpty()) {
            SuggestionsDropdown(
                suggestions = sourceSuggestions, onItemClick = {
                    sourceText = it.primaryText
                    sourceSuggestions = emptyList()
                    activeField = null
                })
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Swap Button Row
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = {
                    val temp = sourceText
                    sourceText = destinationText
                    destinationText = temp
                }) {
                Icon(
                    imageVector = Icons.Default.SwapVert,
                    contentDescription = "Swap",
                    tint = Color.Gray
                )
            }
        }

        ThemedTextField(
            value = destinationText, onValueChange = { newText ->
                destinationText = newText
                activeField = "destination"

                if (newText.isNotBlank() && newText.length > 2) {
                    scope.launch {
                        delay(300)
                        destinationSuggestions = getPlaceSuggestions(newText, placesClient)
                    }
                } else {
                    destinationSuggestions = emptyList()
                }
            }, label = "To (Destination)", icon = Icons.Default.LocationOn
        )

        if (activeField == "destination" && destinationSuggestions.isNotEmpty()) {
            SuggestionsDropdown(
                suggestions = destinationSuggestions, onItemClick = {
                    destinationText = it.primaryText
                    destinationSuggestions = emptyList()
                    activeField = null
                })
        }
    }
}